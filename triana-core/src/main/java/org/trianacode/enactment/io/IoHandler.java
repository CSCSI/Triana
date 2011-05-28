package org.trianacode.enactment.io;

import org.trianacode.TrianaInstance;
import org.trianacode.enactment.io.handlers.*;
import org.trianacode.taskgraph.Node;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskGraphException;
import org.trianacode.taskgraph.ser.DocumentHandler;
import org.trianacode.taskgraph.ser.XMLReader;
import org.trianacode.taskgraph.service.TypeChecking;
import org.w3c.dom.Element;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Very much work in progress...
 *
 * @author Andrew Harrison
 * @version 1.0.0 Oct 2, 2010
 */
public class IoHandler {

    private static Map<String, IoTypeHandler> handlers = new HashMap<String, IoTypeHandler>();

    static {
        registerHandler(new DoubleHandler());
        registerHandler(new IntegerHandler());
        registerHandler(new SerializableHandler());
        registerHandler(new StringHandler());
        registerHandler(new StreamHandler());
        registerHandler(new BytesHandler());
    }

    public static void registerHandler(IoTypeHandler handler) {
        String[] types = handler.getKnownTypes();
        for (String type : types) {
            handlers.put(type, handler);
        }
    }

    public static IoTypeHandler getHandler(String type) {
        return handlers.get(type);
    }

    private StreamResolver streamResolver = new StreamResolver();

    public NodeMappings map(IoConfiguration config, Task task) throws TaskGraphException {
        String toolname = config.getToolName();
        String ver = config.getToolVersion();
        if (toolname != null) {
            if (!toolname.equals(task.getQualifiedToolName())) {
                throw new TaskGraphException("config tool name " + toolname + " does not match tool name " + task.getQualifiedToolName());
            }
        }
        if (ver != null) {
            if (!ver.equals(task.getVersion())) {
                throw new TaskGraphException("config tool version " + ver + " does not match tool version " + task.getVersion());
            }
        }
        List<IoMapping> maps = config.getInputs();
        Node[] nodes = task.getDataInputNodes();
        NodeMappings ret = new NodeMappings();
        for (Node node : nodes) {
            IoMapping curr = null;
            for (IoMapping map : maps) {
                String name = map.getNodeName();
                if (name.equals(node.getNodeIndex() + "")) {
                    curr = map;
                    break;
                }
            }
            if (curr == null && node.isEssential()) {
                throw new TaskGraphException("No IOMapping defined for essential node:" + node.getNodeIndex());
            }
            if (curr != null) {
                IoType iotype = curr.getIoType();
                String type = iotype.getType();
                IoTypeHandler handler = getHandler(type);
                if (handler == null) {
                    throw new TaskGraphException("Unsupported type:" + type);
                }
                String val = iotype.getValue();
                if (val != null) {
                    InputStream in = null;
                    if (iotype.isReference()) {
                        in = streamResolver.handle(val);
                        if (in == null) {
                            throw new TaskGraphException("Could not resolve io type reference:" + val);
                        }
                    } else {
                        in = new ByteArrayInputStream(val.getBytes());
                    }
                    Object o = handler.read(type, in);
                    String[] intypes = task.getDataInputTypes(node.getNodeIndex());
                    if (intypes == null) {
                        intypes = task.getDataInputTypes();
                    }
                    String[] clss = new String[]{o.getClass().getName()};
                    boolean compatible = TypeChecking.isCompatibility(TypeChecking.classForTrianaType(clss),
                            TypeChecking.classForTrianaType(intypes));

                    if (!compatible) {
                        throw new TaskGraphException("input types are not compatible:" + type + " is not compatible with " + Arrays.asList(intypes));
                    }
                    ret.addMapping(node.getNodeIndex(), o);
                }
            }
        }
        return ret;
    }

    public void serialize(DocumentHandler handler, IoConfiguration config) throws IOException {
        Element root = handler.element("configuration");
        handler.setRoot(root);
        if (config.getToolName() != null) {
            handler.addAttribute(root, "toolName", config.getToolName());
        }
        if (config.getToolVersion() != null) {
            handler.addAttribute(root, "toolVersion", config.getToolVersion());
        }
        List<IoMapping> mappings = config.getInputs();
        Element mps = handler.element("inputPorts");
        root.appendChild(mps);
        for (IoMapping mapping : mappings) {
            Element map = handler.element("inputPort");
            mps.appendChild(serializeMapping(handler, mapping, map));
        }
        mps = handler.element("outputPorts");
        mappings = config.getOutputs();
        root.appendChild(mps);
        for (IoMapping mapping : mappings) {
            Element map = handler.element("outputPort");
            mps.appendChild(serializeMapping(handler, mapping, map));
        }
    }

    private Element serializeMapping(DocumentHandler handler, IoMapping mapping, Element map) {
        handler.addAttribute(map, "name", mapping.getNodeName());
        IoType iot = mapping.getIoType();
        handler.addAttribute(map, "type", iot.getType());
        if (iot.getValue() != null) {
            if (iot.isReference()) {
                handler.addAttribute(map, "uri", iot.getValue());
            } else {
                map.setTextContent(iot.getValue());
            }
        }
        return map;
    }

    public IoConfiguration deserialize(InputStream in) throws IOException {
        DocumentHandler handler = new DocumentHandler(in);
        Element root = handler.root();
        if (!root.getTagName().equals("configuration")) {
            System.out.println("root tag not recognised.");
            throw new IOException("unknown element:" + root.getTagName());
        }
        String toolname = root.getAttribute("toolName");
        String ver = root.getAttribute("toolVersion");
        if (toolname != null && toolname.length() == 0) {
            toolname = null;
        }
        if (ver != null && ver.length() == 0) {
            ver = null;
        }
        Element inports = handler.getChild(root, "inputPorts");
        IoConfiguration conf = new IoConfiguration(toolname, ver);
        if (inports != null) {
            List<Element> ins = handler.getChildren(inports, "inputPort");
            for (Element element : ins) {
                deserializeMapping(conf, element, true);
            }
        }
        Element outports = handler.getChild(root, "outputPorts");
        if (outports != null) {
            List<Element> outs = handler.getChildren(inports, "outputPort");
            for (Element element : outs) {
                deserializeMapping(conf, element, false);
            }
        }
        return conf;
    }

    private void deserializeMapping(IoConfiguration conf, Element element, boolean in) {
        String node = element.getAttribute("name");
        String tp = element.getAttribute("type");
        if (tp != null && node != null) {
            String uri = element.getAttribute("uri");
            IoMapping iom;
            if (uri != null && uri.length() > 0) {
                iom = new IoMapping(new IoType(uri, tp, true), node);
            } else {
                if (element.getTextContent() != null && element.getTextContent().length() > 0) {
                    iom = new IoMapping(new IoType(element.getTextContent().trim(), tp), node);
                } else {
                    iom = new IoMapping(new IoType(null, tp), node);
                }
            }
            if (in) {
                conf.addInput(iom);
            } else {
                conf.addOutput(iom);
            }
        }
    }

    public static IoTypeHandler getHandler(Object value) {
        if (value instanceof String) {
            return getHandler("string");
        } else if (value instanceof Boolean) {
            return getHandler("boolean");
        } else if (value instanceof Double) {
            return getHandler("double");
        } else if (value instanceof Integer) {
            return getHandler("integer");
        } else if (value instanceof byte[]) {
            return getHandler("bytes");
        } else if (value instanceof InputStream) {
            return getHandler("stream");
        } else if (value instanceof Serializable) {
            return getHandler("java64");
        }
        return null;
    }

    private static void testMappings(IoConfiguration conf, String wf) throws Exception {
        File f = new File(wf);
        if (!f.exists() || f.length() == 0) {
            System.out.println("Cannot find workflow file:" + wf);
            System.exit(1);
        }
        TrianaInstance engine = new TrianaInstance();
        engine.init();
        XMLReader reader = new XMLReader(new FileReader(f));
        Task tool = (Task) reader.readComponent(engine.getProperties());
        NodeMappings ret = new IoHandler().map(conf, tool);
        Map<Integer, Object> map = ret.getMap();
        System.out.println("Node Mappings index => value");
        for (Integer integer : map.keySet()) {
            System.out.println(integer + " => " + map.get(integer));
        }


    }

    public static void main(String[] args) throws Exception {
        IoMapping in0 = new IoMapping(new IoType("hello x", "string"), "0");
        IoMapping in1 = new IoMapping(new IoType("hello a", "string"), "1");
        IoMapping in2 = new IoMapping(new IoType("./hello.txt", "string", true), "2");

        IoMapping out0 = new IoMapping(new IoType("string"), "0");

        IoConfiguration conf = new IoConfiguration("common.regexTG1", "0.1", Arrays.asList(in0, in1, in2), Arrays.asList(out0));
        DocumentHandler handler = new DocumentHandler();
        new IoHandler().serialize(handler, conf);
        handler.output(System.out, true);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        handler.output(bout);
        IoConfiguration ioc = new IoHandler().deserialize(new ByteArrayInputStream(bout.toByteArray()));
        System.out.println("config:");
        System.out.println("  toolname:" + ioc.getToolName());
        System.out.println("  tool version:" + ioc.getToolVersion());
        List<IoMapping> mappings = ioc.getInputs();
        for (IoMapping mapping : mappings) {
            System.out.println("  mapping:");
            System.out.println("    name:" + mapping.getNodeName());
            System.out.println("    type:" + mapping.getIoType().getType());
            System.out.println("    val:" + mapping.getIoType().getValue());
            System.out.println("    ref:" + mapping.getIoType().isReference());
        }
        testMappings(ioc, "/Users/scmabh/work/projects/triana/code/triana/triana-toolboxes/common/regexTG1.xml");
    }
}
