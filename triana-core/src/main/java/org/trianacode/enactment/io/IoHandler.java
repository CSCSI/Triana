package org.trianacode.enactment.io;

import org.trianacode.TrianaInstance;
import org.trianacode.taskgraph.Node;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskGraphException;
import org.trianacode.taskgraph.ser.DocumentHandler;
import org.trianacode.taskgraph.ser.XMLReader;
import org.w3c.dom.Element;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Very much work in progress...
 *
 * @author Andrew Harrison
 * @version 1.0.0 Oct 2, 2010
 */
public class IoHandler {

    public Object[] map(IoConfiguration config, Task task) throws TaskGraphException {
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
        IoMapping[] maps = config.getInputs();
        Node[] nodes = task.getDataInputNodes();
        Object[] ret = new Object[nodes.length];
        for (IoMapping map : maps) {
            String name = map.getNodeName();
            Node curr = null;
            for (Node node : nodes) {
                if ((node.getNodeIndex() + "").equals(name)) {
                    curr = node;
                    break;
                }
            }
            if (curr != null) {
                boolean compatible = false;
                String[] intypes = task.getDataInputTypes(curr.getNodeIndex());
                if (intypes == null) {
                    intypes = task.getDataInputTypes();
                }
                IoType iotype = map.getIoType();
                String type = iotype.getType();
                for (String intype : intypes) {
                    if (type.equals(intype)) {
                        compatible = true;
                        break;
                    }
                }
                if (!compatible) {
                    throw new TaskGraphException("input types are not compatible:" + type + " is not compatible with " + Arrays.asList(intypes));
                }
                ret[curr.getNodeIndex()] = iotype.getValue();
            } else {
                throw new TaskGraphException("could not find input node matching name:" + name);
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
        IoMapping[] mappings = config.getInputs();
        Element mps = handler.element("inputPorts");
        root.appendChild(mps);
        for (IoMapping mapping : mappings) {
            Element map = handler.element("inputPort");
            handler.addAttribute(map, "name", mapping.getNodeName());
            IoType iot = mapping.getIoType();
            handler.addAttribute(map, "type", iot.getType().toString());
            map.setTextContent(iot.getValue());
            mps.appendChild(map);

        }
    }

    public IoConfiguration deserialize(InputStream in) throws IOException {
        DocumentHandler handler = new DocumentHandler(in);
        Element root = handler.root();
        if (!root.getTagName().equals("configuration")) {
            throw new IOException("unknonw element:" + root.getTagName());
        }
        String toolname = root.getAttribute("toolName");
        String ver = root.getAttribute("toolVersion");

        Element inports = handler.getChild(root, "inputPorts");
        List<IoMapping> mappings = new ArrayList<IoMapping>();
        if (inports != null) {
            List<Element> ins = handler.getChildren(inports, "inputPort");
            for (Element element : ins) {
                String node = element.getAttribute("name");
                String tp = element.getAttribute("type");
                if (tp != null && node != null) {
                    IoMapping iom = new IoMapping(new IoType(element.getTextContent().trim(), tp), node);
                    mappings.add(iom);
                }
            }
        }
        IoConfiguration conf = new IoConfiguration(toolname, ver, mappings.toArray(new IoMapping[mappings.size()]));
        return conf;
    }

    private static void testMappings(IoConfiguration conf, String wf) throws Exception {
        File f = new File(wf);
        if (!f.exists() || f.length() == 0) {
            System.out.println("Cannot find workflow file:" + wf);
            System.exit(1);
        }
        TrianaInstance engine = new TrianaInstance(new String[0], null);
        XMLReader reader = new XMLReader(new FileReader(f));
        Task tool = (Task) reader.readComponent();
        Object[] ret = new IoHandler().map(conf, tool);
        for (Object o : ret) {
            System.out.println("IoHandler.testMappings: " + o);
        }

    }

    public static void main(String[] args) throws Exception {
        IoMapping in0 = new IoMapping(new IoType("hello x", "java.lang.String"), "0");
        IoMapping in1 = new IoMapping(new IoType("hello a", "java.lang.String"), "1");
        IoMapping in2 = new IoMapping(new IoType("hello k", "java.lang.String"), "2");

        IoConfiguration conf = new IoConfiguration("common.regexTG1", "0.1", in0, in1, in2);
        DocumentHandler handler = new DocumentHandler();
        new IoHandler().serialize(handler, conf);
        handler.output(System.out, true);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        handler.output(bout);
        IoConfiguration ioc = new IoHandler().deserialize(new ByteArrayInputStream(bout.toByteArray()));
        System.out.println("config:");
        System.out.println("  toolname:" + ioc.getToolName());
        System.out.println("  tool version:" + ioc.getToolVersion());
        IoMapping[] mappings = ioc.getInputs();
        for (IoMapping mapping : mappings) {
            System.out.println("  mapping:");
            System.out.println("    name:" + mapping.getNodeName());
            System.out.println("    type:" + mapping.getIoType().getType());
            System.out.println("    val:" + mapping.getIoType().getValue());
        }
        testMappings(ioc, "/Users/scmabh/work/projects/triana/code/triana/triana-toolboxes/common/regexTG1.xml");
    }
}
