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

import org.trianacode.taskgraph.*;
import org.trianacode.taskgraph.proxy.Proxy;
import org.trianacode.taskgraph.tool.Tool;
import org.w3c.dom.Element;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

/**
 * Class Description Here...
 *
 * @author Andrew Harrison
 * @version $Revision:$
 * @created Jun 25, 2009: 4:15:53 PM
 * @date $Date:$ modified by $Author:$
 */

public class XMLWriter implements XMLConstants {

    private Writer writer;
    private DocumentHandler handler;


    /**
     * a flag indicating whether the task instance should be preserved in the xml
     */
    private boolean preserveinst = false;

    public XMLWriter(Writer writer) {
        this.writer = writer;
        handler = new DocumentHandler();
    }


    /**
     * Write a tool component
     */
    public void writeComponent(Tool component) throws IOException {
        try {
            generateDocFromTool(component);
            handler.output(writer, true);
        } catch (TaskGraphException e) {
            throw new IOException(e.toString());
        }
    }


    /**
     * @return true if the task instance is preserved in the serialized task.
     *         If true then the instance id and transient variables are serialized, if
     *         false they are discarded.
     */
    public boolean isPreserveInstance() {
        return preserveinst;
    }

    /**
     * Sets whether the task instance is preserved in the serialized task.
     * If true then the instance id and transient variables are serialized, if
     * false they are discarded.
     */
    public void setPreserveInstance(boolean state) {
        this.preserveinst = state;
    }


    private void generateDocFromTool(Tool tool) throws TaskGraphException {
        Element root = getComponent(tool, false, preserveinst);
        handler.setRoot(root);
    }

    public void close() throws IOException {
        writer.close();
    }


    private Element getComponent(Tool tool, boolean insidetaskgraph, boolean preserveinst) throws TaskGraphException {
        Element elem;

        if ((tool instanceof TaskGraph) && (preserveinst || insidetaskgraph))
            elem = handler.element(TASKGRAPH_TAG);
        else if ((tool instanceof Task) && (preserveinst || insidetaskgraph))
            elem = handler.element(TASK_TAG);
        else
            elem = handler.element(TOOL_TAG);

        if ((tool instanceof Task) && preserveinst)
            addInstanceID((Task) tool, elem);

        addToolName(tool, elem);
        addPackage(tool, elem);
        addVersion(tool, elem);
        addProxy(tool, elem);
        addRenderingHints(tool, elem);
        addExtensions(tool, elem);

        addInportNum(tool, elem);
        addOutportNum(tool, elem);
        addInParamNames(tool, elem);
        addOutParamNames(tool, elem);
        addInputTypes(tool, elem);
        addOutputTypes(tool, elem);
        addParams(tool, elem, preserveinst);
        addSubTasks(tool, elem, preserveinst);

        return elem;
    }


    private void addPackage(Tool tool, Element parent) {
        Element current = handler.element(PACKAGE_TAG);
        handler.add(tool.getToolPackage(), current);
        handler.add(current, parent);
    }

    private void addInstanceID(Task task, Element parent) {
        Element current = handler.element(INSTANCE_ID_TAG);
        handler.add(task.getInstanceID(), current);
        handler.add(current, parent);
    }

    /**
     * recursive method with addTask() to add all sub tasks and task graphs
     */
    private void addSubTasks(Tool tool, Element parent, boolean preserveinst) throws TaskGraphException {
        if (tool instanceof TaskGraph) {
            TaskGraph taskgraph = TaskGraphUtils.cloneTaskGraph((TaskGraph) tool, TaskGraphManager.NON_RUNNABLE_FACTORY_TYPE);
            TaskGraphUtils.disconnectControlTask(taskgraph);

            Task[] tasks = taskgraph.getTasks(false);

            if (tasks.length > 0) {
                Element taskListElem = handler.element(TASK_LIST_TAG);
                handler.add(taskListElem, parent);

                // add tasks
                for (int i = 0; i < tasks.length; i++) {
                    addTask(tasks[i], taskListElem, preserveinst);
                }

                // add control task
                if (((TaskGraph) tool).isControlTask()) {
                    Element controlTaskElem = handler.element(CONTROL_TASK_TAG);
                    handler.add(controlTaskElem, taskListElem);
                    addTask(((TaskGraph) tool).getControlTask(), controlTaskElem, preserveinst);
                }

                // add connections
                Cable[] connections =
                        TaskGraphUtils.getInternalCables(tasks);
                if (connections.length > 0) {
                    Element connectionListElem = handler.element(CONNECTION_LIST_TAG);
                    handler.add(connectionListElem, taskListElem);
                    for (int i = 0; i < connections.length; i++) {
                        handler.add(getConnection(connections[i], taskgraph), connectionListElem);
                    }
                }

                // add external group node mappings
                boolean hasMapping = false;
                TaskGraph group = taskgraph;
                Element groupNodeElem = handler.element(GROUP_MAPING_TAG);

                Element current = handler.element(INPUT_TAG);
                handler.add(current, groupNodeElem);

                Node[] nodes = group.getDataInputNodes();
                if (nodes.length > 0) {
                    addExternalNodes(nodes, current);
                    hasMapping = true;
                }
                nodes = group.getParameterInputNodes();
                if (nodes.length > 0) {
                    addExternalNodes(nodes, current);
                    hasMapping = true;
                }

                current = handler.element(OUTPUT_TAG);
                handler.add(current, groupNodeElem);

                nodes = group.getDataOutputNodes();
                if (nodes.length > 0) {
                    addExternalNodes(nodes, current);
                    hasMapping = true;
                }
                nodes = group.getParameterOutputNodes();
                if (nodes.length > 0) {
                    addExternalNodes(nodes, current);
                    hasMapping = true;
                }

                if (hasMapping) {
                    handler.add(groupNodeElem, taskListElem);
                }
            }
        }
    }

    private void addExternalNodes(Node[] nodes, Element parent) {
        for (int i = 0; i < nodes.length; i++) {
            Element node = handler.element(NODE_TAG);
            node.setAttribute(EXTERNAL_NODE_TAG, Integer.toString(i));
            node.setAttribute(TASK_NAME_TAG, nodes[i].getParentNode().getTask().getToolName());
            node.setAttribute(NODE_TAG,
                    Integer.toString(nodes[i].getParentNode().getAbsoluteNodeIndex()));
            handler.add(node, parent);
        }
    }

    private void addTask(Task task, Element parent, boolean preserveinst) throws TaskGraphException {
        Element tasktag = getComponent(task, true, preserveinst);
        handler.add(tasktag, parent);
    }


    private void addParams(Tool tool, Element parent, boolean preserveinst) {
        String[] paramNames = tool.getParameterNames();
        if (paramNames.length > 0) {
            Element param = handler.element(PARAM_LIST_TAG);
            handler.add(param, parent);

            for (int i = 0; i < paramNames.length; i++) {
                if (preserveinst || (!tool.getParameterType(paramNames[i]).startsWith(Tool.TRANSIENT))) {
                    Element current = handler.element(PARAM_TAG);
                    current.setAttribute(NAME_TAG, paramNames[i]);

                    Element val = handler.element(VALUE_TAG);
                    handler.add(ObjectMarshaller.marshallJavaToElement(val, tool.getParameter(paramNames[i])), current);
                    current.setAttribute(PARAM_TYPE_TAG,
                            tool.getParameterType(paramNames[i]));
                    handler.add(current, param);
                }
            }
        }
    }

    private void addOutputTypes(Tool tool, Element parent) {
        Element output = handler.element(OUTPUT_TAG);

        String[] outputs;
        int count = 0;

        do {
            outputs = tool.getDataOutputTypes(count);

            if (outputs != null) {
                Element node = handler.element(NODE_TAG);
                node.setAttribute(INDEX_TAG, String.valueOf(count));

                for (int i = 0; i < outputs.length; i++) {
                    Element current = handler.element(TYPE_TAG);
                    handler.add(outputs[i], current);
                    handler.add(current, node);
                }

                handler.add(node, output);
                count++;
            }
        } while (outputs != null);

        outputs = tool.getDataOutputTypes();

        for (int i = 0; i < outputs.length; i++) {
            Element current = handler.element(TYPE_TAG);
            handler.add(outputs[i], current);
            handler.add(current, output);
        }

        if (handler.hasChildren(output))
            handler.add(output, parent);
    }

    private void addInputTypes(Tool tool, Element parent) {
        Element input = handler.element(INPUT_TAG);

        String[] inputs;
        int count = 0;

        do {
            inputs = tool.getDataInputTypes(count);

            if (inputs != null) {
                Element node = handler.element(NODE_TAG);
                node.setAttribute(INDEX_TAG, String.valueOf(count));

                for (int i = 0; i < inputs.length; i++) {
                    Element current = handler.element(TYPE_TAG);
                    handler.add(inputs[i], current);
                    handler.add(current, node);
                }

                handler.add(node, input);
                count++;
            }
        } while (inputs != null);

        inputs = tool.getDataInputTypes();

        for (int i = 0; i < inputs.length; i++) {
            Element current = handler.element(TYPE_TAG);
            handler.add(inputs[i], current);
            handler.add(current, input);
        }

        if (handler.hasChildren(input))
            handler.add(input, parent);
    }

    private void addOutportNum(Tool tool, Element parent) {
        Element current = handler.element(OUTPORT_NUM_TAG);
        handler.add(Integer.toString(tool.getDataOutputNodeCount()), current);
        handler.add(current, parent);
    }

    private void addInportNum(Tool tool, Element parent) {
        Element current = handler.element(INPORT_NUM_TAG);
        handler.add(Integer.toString(tool.getDataInputNodeCount()), current);
        handler.add(current, parent);
    }

    private void addInParamNames(Tool tool, Element parent) {
        Element current = handler.element(INPARAM_TAG);

        for (int i = 0; i < tool.getParameterInputNodeCount(); i++) {
            Element param = handler.element(PARAM_TAG);
            param.setAttribute(INDEX_TAG, String.valueOf(i));
            param.setAttribute(NAME_TAG, tool.getParameterInputName(i));
            param.setAttribute(TRIGGER_TAG, String.valueOf(tool.isParameterTriggerNode(i)));
            handler.add(param, current);
        }

        handler.add(current, parent);
    }

    private void addOutParamNames(Tool tool, Element parent) {
        Element current = handler.element(OUTPARAM_TAG);

        for (int i = 0; i < tool.getParameterOutputNodeCount(); i++) {
            Element param = handler.element(PARAM_TAG);
            param.setAttribute(NAME_TAG, tool.getParameterOutputName(i));
            param.setAttribute(INDEX_TAG, String.valueOf(i));
            handler.add(param, current);
        }

        handler.add(current, parent);
    }


    private void addToolName(Tool tool, Element parent) {
        Element current = handler.element(TOOL_NAME_TAG);

        if (tool.getToolName() != null)
            handler.add(tool.getToolName(), current);
        else if (tool instanceof Task)
            handler.add(((Task) tool).getToolName(), current);

        handler.add(current, parent);
    }

    private void addVersion(Tool tool, Element parent) {
        Element current = handler.element(VERSION_TAG);

        if (tool.getVersion() == null) {
            tool.setVersion(DEFAULT_VERSION);
        }
        handler.add(tool.getVersion(), current);
        handler.add(current, parent);
    }

    private void addProxy(Tool tool, Element parent) {
        Proxy proxy = tool.getProxy();

        if (proxy != null) {
            Element elem = handler.element(PROXY_TAG);
            elem.setAttribute(TYPE_TAG, proxy.getType());

            Map details = proxy.getInstanceDetails();
            Element paramelem;
            String param;

            for (Iterator it = details.keySet().iterator(); it.hasNext();) {
                param = (String) it.next();

                paramelem = handler.element(PARAM_TAG);
                paramelem.setAttribute(PARAM_NAME_TAG, param);
                Element val = handler.element(VALUE_TAG);

                handler.add(ObjectMarshaller.marshallJavaToElement(val, details.get(param)), paramelem);
                handler.add(paramelem, elem);
            }

            handler.add(elem, parent);
        }
    }

    private void addRenderingHints(Tool tool, Element parent) {
        RenderingHint[] hints = tool.getRenderingHints();

        if (hints.length > 0) {
            Element classelem = handler.element(RENDERING_HINTS_TAG);
            Element current;

            for (int count = 0; count < hints.length; count++) {
                current = handler.element(RENDERING_HINT_TAG);
                current.setAttribute(HINT_TAG, hints[count].getRenderingHint());
                current.setAttribute(PROXY_DEPENDENT_TAG, String.valueOf(hints[count].isProxyDependent()));

                String[] paramnames = hints[count].getParameterNames();
                Element paramelem;

                for (int params = 0; params < paramnames.length; params++) {
                    paramelem = handler.element(PARAM_TAG);
                    paramelem.setAttribute(PARAM_NAME_TAG, paramnames[params]);
                    Element val = handler.element(VALUE_TAG);

                    handler.add(ObjectMarshaller.marshallJavaToElement(val, hints[count].getRenderingDetail(paramnames[params])), paramelem);
                    handler.add(paramelem, current);
                }

                handler.add(current, classelem);
            }

            handler.add(classelem, parent);
        }
    }

    private void addExtensions(Tool tool, Element parent) {
        String[] names = tool.getExtensionNames();

        if (names.length > 0) {
            Element extelem = handler.element(EXTENSIONS_TAG);
            Element current;

            for (int count = 0; count < names.length; count++) {
                current = handler.element(names[count]);
                handler.add(tool.getExtension(names[count]), current);
                handler.add(current, extelem);
            }

            handler.add(extelem, parent);
        }
    }

    private Element getConnection(Cable cable, TaskGraph taskgraph) {
        Element connectionElem = handler.element(CONNECTION_TAG);
        connectionElem.setAttribute(TYPE_TAG, cable.getType());

        Element sourceElem = handler.element(SOURCE_TAG);
        Task sourceTask = taskgraph.getTask(cable.getSendingNode());
        int sourceIndex = cable.getSendingNode().getAbsoluteNodeIndex();
        sourceElem.setAttribute(TASK_NAME_TAG, sourceTask.getToolName());
        sourceElem.setAttribute(NODE_TAG, Integer.toString(sourceIndex));

        handler.add(sourceElem, connectionElem);

        Element targetElem = handler.element(TARGET_TAG);
        Task targetTask = taskgraph.getTask(cable.getReceivingNode());
        int targetIndex = cable.getReceivingNode().getAbsoluteNodeIndex();
        targetElem.setAttribute(TASK_NAME_TAG, targetTask.getToolName());
        targetElem.setAttribute(NODE_TAG, Integer.toString(targetIndex));

        handler.add(targetElem, connectionElem);

        return connectionElem;
    }


}
