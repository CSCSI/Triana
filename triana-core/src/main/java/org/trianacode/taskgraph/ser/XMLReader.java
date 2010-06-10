/*
 * Copyright 2004 - 2009 University of Cardiff.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.trianacode.taskgraph.ser;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.trianacode.taskgraph.CableException;
import org.trianacode.taskgraph.InstanceIDManager;
import org.trianacode.taskgraph.Node;
import org.trianacode.taskgraph.NodeException;
import org.trianacode.taskgraph.ParameterNode;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskException;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.TaskGraphException;
import org.trianacode.taskgraph.TaskGraphManager;
import org.trianacode.taskgraph.TaskGraphUtils;
import org.trianacode.taskgraph.imp.RenderingHintImp;
import org.trianacode.taskgraph.imp.SerializedObject;
import org.trianacode.taskgraph.imp.ToolImp;
import org.trianacode.taskgraph.proxy.Proxy;
import org.trianacode.taskgraph.proxy.ProxyFactory;
import org.trianacode.taskgraph.proxy.ProxyInstantiationException;
import org.trianacode.taskgraph.proxy.java.JavaProxy;
import org.trianacode.taskgraph.tool.Tool;

/**
 * Class Description Here...
 *
 * @author Andrew Harrison
 * @version $Revision:$
 */

public class XMLReader implements XMLConstants {

    static Logger log = Logger.getLogger("org.trianacode.taskgraph.ser.XMLReader");

    /**
     * Instance of a SAXBuilder from JDOM
     */
    private Reader in = null;

    private DocumentHandler handler;

    /**
     * a flag indicating whether the serialized task instance is preserved in the deserialized tasks
     */
    private boolean preserveinst = false;


    public XMLReader(Reader reader) throws IOException {
        this.in = reader;
        this.handler = new DocumentHandler(reader);
    }


    /**
     * @return true if the serialized task instance is preserved in the deserialized task. If true then the instance id
     *         and transient variables are deserialized, if false they are discarded.
     */
    public boolean isPreserveInstance() {
        return preserveinst;
    }

    /**
     * Sets whether serialized task instance is preserved in the deserialized task. If true then the instance id and
     * transient variables are deserialized, if false they are discarded.
     */
    public void setPreserveInstance(boolean state) {
        preserveinst = state;
    }

    /**
     * Read method.
     *
     * @return a TrianaIOComponent containing the abstract representation of either a ToolImp, TaskGraphImp or
     *         TaskGraphLayout.
     */
    public Tool readComponent() throws TaskGraphException, IOException {

        return parseTaskGraph(handler.document());
    }


    /**
     * Close the stream.  Once a stream has been closed, further read(), ready(), mark(), or reset() invocations will
     * throw an IOException. Closing a previously-closed stream, however, has no effect.
     *
     * @throws IOException If an I/O error occurs
     */
    public void close() throws IOException {
        try {
            in.close();
        } catch (IOException e) {
            throw e;
        }
    }

    private Tool parseTaskGraph(Document xmlDoc) throws TaskGraphException {
        Element root = xmlDoc.getDocumentElement();

        if (isTool(root)) {
            Tool tool = getTool(root);

            if ((preserveinst) && (!root.getLocalName().equals(TOOL_TAG))) {
                return TaskGraphManager.createTask(tool, TaskGraphManager.NON_RUNNABLE_FACTORY_TYPE, preserveinst);
            } else {
                return tool;
            }
        } else {
            return null;
        }
    }

    private boolean isTool(Element elem) {
        return (elem.getLocalName().equals(TOOL_TAG) ||
                elem.getLocalName().equals(TASK_TAG) ||
                elem.getLocalName().equals(TASKGRAPH_TAG));
    }


    private Tool getTool(Element parent) throws TaskGraphException {
        Element tasklist = handler.getChild(parent, TASK_LIST_TAG);

        if (tasklist != null) {
            List<Element> taskElems = handler.getChildren(tasklist);
            TaskGraph taskgraph = TaskGraphManager.createTaskGraph();

            initTool((ToolImp) taskgraph, parent);

            for (Iterator iter = taskElems.iterator(); iter.hasNext();) {
                Element taskelem = (Element) iter.next();

                if (isTool(taskelem)) {
                    taskgraph.createTask(getTool(taskelem), preserveinst);
                }
            }

            addConnections(taskgraph, tasklist);
            addExternalMap(taskgraph, tasklist);
            addControlTask(taskgraph, tasklist);
            return taskgraph;
        } else {
            ToolImp tool = new ToolImp();
            initTool(tool, parent);
            return tool;
        }
    }

    private void initTool(ToolImp tool, Element parent) throws TaskGraphException {
        tool.setToolName(getToolName(parent));
        tool.setToolPackage(getToolPackage(parent));
        tool.setVersion(getVersion(parent));
        tool.setDefinitionType(Tool.DEFINITION_TRIANA_XML);
        try {
            tool.setDataInputNodeCount(getInportNum(parent));
            tool.setDataOutputNodeCount(getOutputNum(parent));

            // Convert old style tools (where everything was a unit) to proxy format
            if (isPreProxyTool(parent)) {
                parseToolClasses(tool, parent);
            } else {
                setProxy(tool, parent);
                setRenderingHints(tool, parent);
            }

            setInParams(tool, parent);
            setOutParams(tool, parent);

            // Handles legacy DESCRIPTION tag - pop-up description is now a parameter
            setDescription(tool, parent);

            Element current = handler.getChild(parent, INPUT_TAG);
            if (current != null) {
                Element[] nodes = getIONodes(current);
                for (int count = 0; count < nodes.length; count++) {
                    tool.setDataInputTypes(count, getIODataTypes(nodes[count]));
                }

                tool.setDataInputTypes(getIODataTypes(current));
            }
            current = handler.getChild(parent, OUTPUT_TAG);
            if (current != null) {
                Element[] nodes = getIONodes(current);
                for (int count = 0; count < nodes.length; count++) {
                    tool.setDataOutputTypes(count, getIODataTypes(nodes[count]));
                }

                tool.setDataOutputTypes(getIODataTypes(current));
            }

            addParameters(parent, tool);
            addExtensions(parent, tool);

            if (preserveinst) {
                initInstanceID(tool, parent);
            }
        } catch (TaskGraphException except) {
            throw (new TaskGraphException("Error Reading " + tool.getQualifiedToolName() + ": " + except.getMessage()));
        }
    }


    private void initInstanceID(Tool tool, Element parent) {
        Element current = handler.getChild(parent, INSTANCE_ID_TAG);

        if (current != null) {
            InstanceIDManager.registerID(tool, current.getTextContent().trim());
        }
    }


    private void addConnections(TaskGraph taskgraph, Element parent) throws CableException {
        Element connectListElem = handler.getChild(parent, CONNECTION_LIST_TAG);
        if (connectListElem != null) {
            List<Element> connections = handler.getChildren(connectListElem, CONNECTION_TAG);
            for (Iterator it = connections.iterator(); it.hasNext();) {
                Element connection = (Element) it.next();
                Element source = handler.getChild(connection, SOURCE_TAG);
                Element target = handler.getChild(connection, TARGET_TAG);
                Task sourceTask = taskgraph.getTask(source.getAttribute(TASK_NAME_TAG));
                Task targetTask = taskgraph.getTask(target.getAttribute(TASK_NAME_TAG));
                Node sourceNode = sourceTask.getOutputNode(Integer.parseInt(source.getAttribute(NODE_TAG)));
                Node targetNode = targetTask.getInputNode(Integer.parseInt(target.getAttribute(NODE_TAG)));

                taskgraph.connect(sourceNode, targetNode);
            }
        }
    }

    private void addExternalMap(TaskGraph taskgraph, Element parent) {
        Element groupMapListElem = handler.getChild(parent, GROUP_MAPING_TAG);
        if (groupMapListElem != null) {
            Element mapping = handler.getChild(groupMapListElem, INPUT_TAG);
            if (mapping != null) {
                List maps = handler.getChildren(mapping);
                TaskGraph group = taskgraph;
                for (Iterator it = maps.iterator(); it.hasNext();) {
                    Element map = (Element) it.next();
                    int extNodeNum = Integer.parseInt(map.getAttribute(EXTERNAL_NODE_TAG));
                    String taskName = map.getAttribute(TASK_NAME_TAG);
                    int nodeNum = Integer.parseInt(map.getAttribute(NODE_TAG));
                    Task task = taskgraph.getTask(taskName);

                    group.setGroupNodeParent(group.getInputNode(extNodeNum), task.getInputNode(nodeNum));
                }
            }
            mapping = handler.getChild(groupMapListElem, OUTPUT_TAG);
            if (mapping != null) {
                List maps = handler.getChildren(mapping);
                TaskGraph group = taskgraph;
                for (Iterator it = maps.iterator(); it.hasNext();) {
                    Element map = (Element) it.next();
                    int extNodeNum = Integer.parseInt(map.getAttribute(EXTERNAL_NODE_TAG));
                    String taskName = map.getAttribute(TASK_NAME_TAG);
                    int nodeNum = Integer.parseInt(map.getAttribute(NODE_TAG));
                    Task task = taskgraph.getTask(taskName);

                    group.setGroupNodeParent(group.getOutputNode(extNodeNum), task.getOutputNode(nodeNum));
                }
            }
        }
    }

    private void addControlTask(TaskGraph taskgraph, Element parent) throws TaskGraphException {
        Element controlElem = handler.getChild(parent, CONTROL_TASK_TAG);

        if (controlElem != null) {
            Element taskElem = handler.getChild(controlElem, TASK_TAG);

            if (taskElem != null) {
                taskgraph.createControlTask(getTool(taskElem), true);
                TaskGraphUtils.connectControlTask(taskgraph);
            }
        }
    }

    private Element[] getIONodes(Element component) {
        List nodes = handler.getChildren(component, NODE_TAG);
        Element[] result = new Element[nodes.size()];
        int index;

        for (int i = 0; i < nodes.size(); i++) {
            index = Integer.parseInt(((Element) nodes.get(i)).getAttribute(INDEX_TAG));
            result[index] = (Element) nodes.get(i);
        }
        return result;
    }

    private String[] getIODataTypes(Element component) {
        List types = handler.getChildren(component, TYPE_TAG);
        String[] result = new String[types.size()];
        for (int i = 0; i < types.size(); i++) {
            result[i] = ((Element) types.get(i)).getTextContent();
        }
        return result;
    }

    private void addParameters(Element xmlItem, Tool tool) {
        Element current = handler.getChild(xmlItem, PARAM_LIST_TAG);
        Element childelem;
        Element valueelem;
        Object paramvalue;
        String paramtype;
        String serializer;
        String name;

        if (current != null) {
            List<Element> l = handler.getChildren(current);
            for (Iterator it = l.iterator(); it.hasNext();) {
                childelem = (Element) it.next();
                valueelem = handler.getChild(childelem, VALUE_TAG);
                paramtype = childelem.getAttribute(PARAM_TYPE_TAG);

                if (valueelem == null) {
                    paramvalue = null;
                    serializer = null;
                } else {
                    paramvalue = valueelem.getTextContent();
                    serializer = valueelem.getAttribute(SERIALIZER_TAG);
                }

                if (paramvalue != null) {
                    /*if (serializer == null) {
                        // todo
                        // Standard string or legacy serialized object
                        if (JSXObjectDeserializer.isJSXSerialized((String) paramvalue))
                            paramvalue = new SerializedObject((String) paramvalue, JSXObjectDeserializer.deserializer);
                    } else {
                        // Serialized object
                        ObjectDeserializer deserializer = ObjectDeserializationManager.getObjectDeserializer(serializer);

                        if (deserializer == null)
                            logger.error("Invalid Object Deserializer in " + tool.getQualifiedToolName() + ": " + serializer);
                        else
                            paramvalue = new SerializedObject((String) paramvalue, deserializer);
                    }*/
                    ObjectDeserializer deserializer = ObjectDeserializationManager.getObjectDeserializer(serializer);
                    if (serializer != null) {
                        if (deserializer == null) {
                            log.warning("Invalid Object Deserializer in " + tool.getQualifiedToolName() + ": "
                                    + serializer);
                        } else {
                            paramvalue = new SerializedObject((String) paramvalue, deserializer);
                        }
                    }
                }

                if (preserveinst || (paramtype == null) || (!paramtype.startsWith(Tool.TRANSIENT))) {
                    name = childelem.getAttribute(NAME_TAG);

                    if (name.indexOf('.') >= 0) {
                        System.err.println(
                                "WARNING: " + tool.getQualifiedToolName() + " contains an invalid parameter name ("
                                        + name + ")");
                    }

                    if (paramtype != null) {
                        tool.setParameterType(name, paramtype);
                    }

                    if (paramvalue != null) {
                        tool.setParameter(name, paramvalue);
                    } else {
                        tool.setParameter(name, childelem.getAttribute(VALUE_TAG));
                    }
                }
            }
        }
    }

    /**
     * Adds the extensions to the tool (without parsing text)
     */
    private void addExtensions(Element parent, Tool tool) {
        Element extelem = handler.getChild(parent, EXTENSIONS_TAG);

        if (extelem != null) {
            /*List children = extelem.getChildren();
            XMLOutputter outputter = new XMLOutputter(Format.getCompactFormat());

            for (Iterator it = children.iterator(); it.hasNext();) {
                Element elem = (Element) it.next();

                try {
                    StringWriter writer = new StringWriter();
                    outputter.output(elem.getContent(), writer);

                    tool.addExtension(elem.getName(), writer.toString());
                } catch (IOException e) {
                    log.severe("Error reading extension from " + tool.getQualifiedToolName() + ": " + elem.getName());
                }
            }*/
        }
    }


    /**
     * Sets the pop-up description for the tool. NOTE that the description tag is no longer used, pop-up description is
     * now stored as a parameter.
     */
    private void setDescription(Tool tool, Element component) {
        Element current = handler.getChild(component, DESC_TAG);
        if (current != null) {
            tool.setPopUpDescription(current.getTextContent());
        }
    }

    /**
     * For a TaskGraphImp or ToolImp XML Element return the text for the Name element, for a Task return the tool name
     * attribute.
     */
    private String getToolName(Element component) {
        // provides backwards compatibility with old task names
        Element current = handler.getChild(component, TASK_NAME_TAG);
        if (current != null) {
            return current.getTextContent();
        }

        current = handler.getChild(component, TOOL_NAME_TAG);

        if (current != null) {
            return current.getTextContent();
        }

        current = handler.getChild(component, NAME_TAG);

        if (current != null) {
            return current.getTextContent();
        }

        return "Unknown";
    }

    private String getToolPackage(Element component) {
        Element current = handler.getChild(component, PACKAGE_TAG);
        if (current != null) {
            return current.getTextContent();
        } else {
            return "";
        }
    }

    private String getVersion(Element component) {
        Element current = handler.getChild(component, VERSION_TAG);
        if (current != null) {
            return current.getTextContent();
        } else {
            return DEFAULT_VERSION;
        }
    }

    private void setProxy(Tool tool, Element component) throws TaskException {
        Element current = handler.getChild(component, PROXY_TAG);

        if (current != null) {
            String type = current.getAttribute(TYPE_TAG);
            List list = handler.getChildren(current, PARAM_TAG);
            HashMap map = new HashMap();
            Element elem;

            for (Iterator it = list.iterator(); it.hasNext();) {
                elem = (Element) it.next();
                map.put(elem.getAttribute(PARAM_NAME_TAG), ObjectMarshaller.marshallElementToJava(elem));
            }

            try {
                tool.setProxy(ProxyFactory.createProxy(type, map));
            } catch (ProxyInstantiationException except) {
                throw (new TaskException("Cannot instantiate " + type + " proxy: " + except.getMessage(), except));
            }
        }
    }

    private void setRenderingHints(Tool tool, Element component) {
        Element hints = handler.getChild(component, RENDERING_HINTS_TAG);

        if (hints != null) {
            List hintlist = handler.getChildren(hints, RENDERING_HINT_TAG);
            Element hintelem;
            RenderingHintImp hint;

            for (Iterator hintit = hintlist.iterator(); hintit.hasNext();) {
                hintelem = (Element) hintit.next();
                hint = new RenderingHintImp(hintelem.getAttribute(HINT_TAG),
                        Boolean.valueOf(hintelem.getAttribute(PROXY_DEPENDENT_TAG)).booleanValue());

                List paramlist = handler.getChildren(hintelem, PARAM_TAG);
                Element elem;

                for (Iterator paramit = paramlist.iterator(); paramit.hasNext();) {
                    elem = (Element) paramit.next();
                    hint.setRenderingDetail(elem.getAttribute(PARAM_NAME_TAG),
                            ObjectMarshaller.marshallElementToJava(elem));
                }

                tool.addRenderingHint(hint);
            }
        }
    }


    private int getInportNum(Element component) {
        Element current = handler.getChild(component, INPORT_NUM_TAG);
        if (current != null) {
            return Integer.parseInt(current.getTextContent().trim());
        } else {
            return 0;
        }
    }

    private int getOutputNum(Element component) {
        Element current = handler.getChild(component, OUTPORT_NUM_TAG);
        if (current != null) {
            return Integer.parseInt(current.getTextContent());
        } else {
            return 0;
        }
    }

    private void setInParams(Tool tool, Element component) throws NodeException {
        Element current = handler.getChild(component, INPARAM_TAG);

        if (current != null) {
            List params = handler.getChildren(current, PARAM_TAG);
            String[] names = new String[params.size()];
            boolean[] trigger = new boolean[params.size()];
            Element elem;
            String trig;
            int index;

            for (int count = 0; count < names.length; count++) {
                elem = ((Element) params.get(count));
                index = Integer.parseInt(elem.getAttribute(INDEX_TAG));
                names[index] = elem.getAttribute(NAME_TAG);

                trig = elem.getAttribute(TRIGGER_TAG);

                if (trig != null) {
                    trigger[index] = Boolean.valueOf(trig).booleanValue();
                } else {
                    trigger[index] = names[index].equals(ParameterNode.TRIGGER_PARAM);
                }
            }

            tool.setParameterInputs(names, trigger);
        } else {
            tool.setParameterInputs(new String[0], new boolean[0]);
        }
    }

    private void setOutParams(Tool tool, Element component) throws NodeException {
        Element current = handler.getChild(component, OUTPARAM_TAG);

        if (current != null) {
            List params = handler.getChildren(current, PARAM_TAG);
            String[] names = new String[params.size()];
            Element elem;
            int index;

            for (int count = 0; count < names.length; count++) {
                elem = ((Element) params.get(count));
                index = Integer.parseInt(elem.getAttribute(INDEX_TAG));
                names[index] = elem.getAttribute(NAME_TAG);
            }

            tool.setParameterOutputs(names);
        } else {
            tool.setParameterOutputs(new String[0]);
        }
    }


    /**
     * @return true if old style XML where everthing is a Java unit rather than a generic unit.
     */
    private boolean isPreProxyTool(Element parent) throws TaskException {
        boolean oldstyle = handler.getChild(parent, UNIT_NAME_TAG) != null;
        oldstyle = oldstyle || (handler.getChild(parent, UNIT_PACKAGE_TAG) != null);
        oldstyle = oldstyle || (handler.getChild(parent, TOOL_CLASSES_TAG) != null);

        boolean newstyle = handler.getChild(parent, PROXY_TAG) != null;
        newstyle = newstyle || (handler.getChild(parent, RENDERING_HINTS_TAG) != null);

        if (oldstyle && newstyle) {
            throw (new TaskException("XML contains both old and new style proxy definitions"));
        }

        return oldstyle;
    }

    private void parseToolClasses(ToolImp tool, Element parent) throws TaskException {
        Element current = handler.getChild(parent, TOOL_CLASSES_TAG);

        if (current != null) {
            List classes = handler.getChildren(current, TOOL_CLASS_TAG);
            Element elem;

            for (int count = 0; count < classes.size(); count++) {
                elem = ((Element) classes.get(count));
                String classname = elem.getAttribute(CLASS_NAME_TAG);

                if (classname != null) {
                    HashMap details = new HashMap();

                    List list = handler.getChildren(elem, PARAM_TAG);
                    Element paramelem;

                    for (Iterator iter = list.iterator(); iter.hasNext();) {
                        paramelem = (Element) iter.next();

                        details.put(paramelem.getAttribute(PARAM_NAME_TAG),
                                ObjectMarshaller.marshallElementToJava(paramelem));
                    }

                    Object root = ObjectMarshaller.marshallElementToJava(elem);
                    if (root != null) {
                        details.put(Proxy.DEFAULT_INSTANCE_DETAIL, root);
                    }

                    if (ProxyFactory.isProxyType(classname)) {
                        if (tool.getProxy() != null) {
                            System.err.println(
                                    "Multiple proxy tool classes found (" + classname + ", " + tool.getProxy().getType()
                                            + ")");
                        }

                        try {
                            tool.setProxy(ProxyFactory.createProxy(classname, details));
                            tool.addRenderingHint(new RenderingHintImp(classname, true));
                        } catch (ProxyInstantiationException except) {
                            throw (new TaskException("Could not instantiate " + classname + " proxy"));
                        }
                    } else {
                        RenderingHintImp hint = new RenderingHintImp(classname, true);
                        String key;

                        for (Iterator it = details.keySet().iterator(); it.hasNext();) {
                            key = (String) it.next();
                            hint.setRenderingDetail(key, details.get(key));
                        }

                        tool.addRenderingHint(hint);
                    }
                }
            }
        }

        if (tool.getProxy() == null) {
            parseJavaUnit(tool, parent);
        }
    }


    private void parseJavaUnit(ToolImp tool, Element component) throws TaskException {

        String unitname;
        String unitpack = "";

        Element elem = handler.getChild(component, UNIT_NAME_TAG);
        if (elem != null) {
            unitname = elem.getTextContent();
        } else {
            return;
        }

        elem = handler.getChild(component, UNIT_PACKAGE_TAG);
        if (elem != null) {
            unitpack = elem.getTextContent();
        }

        tool.setProxy(new JavaProxy(unitname, unitpack));
    }

}
