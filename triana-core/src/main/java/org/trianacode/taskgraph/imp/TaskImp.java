/*
 * The University of Wales, Cardiff Triana Project Software License (Based
 * on the Apache Software License Version 1.1)
 *
 * Copyright (c) 2007 University of Wales, Cardiff. All rights reserved.
 *
 * Redistribution and use of the software in source and binary forms, with
 * or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1.  Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 * 2.  Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 * 3. The end-user documentation included with the redistribution, if any,
 *    must include the following acknowledgment: "This product includes
 *    software developed by the University of Wales, Cardiff for the Triana
 *    Project (http://www.trianacode.org)." Alternately, this
 *    acknowledgment may appear in the software itself, if and wherever
 *    such third-party acknowledgments normally appear.
 *
 * 4. The names "Triana" and "University of Wales, Cardiff" must not be
 *    used to endorse or promote products derived from this software
 *    without prior written permission. For written permission, please
 *    contact triana@trianacode.org.
 *
 * 5. Products derived from this software may not be called "Triana," nor
 *    may Triana appear in their name, without prior written permission of
 *    the University of Wales, Cardiff.
 *
 * 6. This software may not be sold, used or incorporated into any product
 *    for sale to third parties.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN
 * NO EVENT SHALL UNIVERSITY OF WALES, CARDIFF OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 *
 * ------------------------------------------------------------------------
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Triana Project. For more information on the
 * Triana Project, please see. http://www.trianacode.org.
 *
 * This license is based on the BSD license as adopted by the Apache
 * Foundation and is governed by the laws of England and Wales.
 *
 */
package org.trianacode.taskgraph.imp;

import java.util.ArrayList;

import org.trianacode.taskgraph.CableException;
import org.trianacode.taskgraph.ExecutionState;
import org.trianacode.taskgraph.InstanceIDManager;
import org.trianacode.taskgraph.Node;
import org.trianacode.taskgraph.NodeException;
import org.trianacode.taskgraph.ParameterNode;
import org.trianacode.taskgraph.RenderingHint;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskException;
import org.trianacode.taskgraph.TaskFactory;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.TaskGraphContext;
import org.trianacode.taskgraph.event.ParameterUpdateEvent;
import org.trianacode.taskgraph.event.TaskDisposedEvent;
import org.trianacode.taskgraph.event.TaskListener;
import org.trianacode.taskgraph.event.TaskNodeEvent;
import org.trianacode.taskgraph.event.TaskPropertyEvent;
import org.trianacode.taskgraph.proxy.Proxy;
import org.trianacode.taskgraph.proxy.ProxyFactory;
import org.trianacode.taskgraph.proxy.ProxyInstantiationException;
import org.trianacode.taskgraph.tool.Tool;

/**
 * A task within a taskgraph.
 */
public class TaskImp extends ToolImp implements Task {

    /**
     * the instance id of this task, all copies of a task within a parameter space (sharing the same parameters) have
     * the same instance id
     */
    private String instanceid;


    /**
     * the taskgraph in which this task is contained
     */
    private TaskGraph taskgraph;

    /**
     * the task factory used to create nodes etc.
     */
    private TaskFactory factory;


    /**
     * an array list of the task's listeners
     */
    private ArrayList listeners = new ArrayList();

    /**
     * an array list of the task's listeners
     */
    private TaskListener[] listenerarray = new TaskListener[0];

    /**
     * a list of the input data nodes
     */
    private ArrayList innodes = new ArrayList();

    /**
     * a list of the input data nodes
     */
    private ArrayList outnodes = new ArrayList();

    /**
     * a list of the input data nodes
     */
    private ArrayList inparams = new ArrayList();

    /**
     * a list of the input data nodes
     */
    private ArrayList outparams = new ArrayList();

    /**
     * a flag to whether this task runs continuously
     */
    private boolean runcontin = false;

    private String subtext = "";

    private TaskGraphContext context = new TaskGraphContext();


    public TaskImp(Tool tool, TaskFactory factory, boolean preserveinst) throws TaskException {
        try {
            this.factory = factory;

            this.setToolName(tool.getToolName());
            this.setToolPackage(tool.getToolPackage());
            this.setDefinitionType(tool.getDefinitionType());
            if (tool.getProxy() != null)

            {
                this.setProxy(ProxyFactory.cloneProxy(tool.getProxy()));
            }

            this.instanceid = InstanceIDManager.registerID(this, tool, preserveinst);

            this.setDefinitionPath(tool.getDefinitionPath());
            this.setToolBox(tool.getToolBox());
            this.setPopUpDescription(tool.getPopUpDescription());
            this.setHelpFile(tool.getHelpFile());

            this.setDataInputTypes(tool.getDataInputTypes());
            this.setDataOutputTypes(tool.getDataOutputTypes());

            int count = 0;
            while (tool.getDataInputTypes(count) != null) {
                this.setDataInputTypes(count, tool.getDataInputTypes(count));
                count++;
            }

            count = 0;
            while (tool.getDataOutputTypes(count) != null) {
                this.setDataOutputTypes(count, tool.getDataOutputTypes(count));
                count++;
            }

            RenderingHint[] hints = tool.getRenderingHints();
            for (count = 0; count < hints.length; count++) {
                this.addRenderingHint(hints[count]);
            }

            String[] names = tool.getExtensionNames();
            for (count = 0; count < names.length; count++) {
                addExtension(names[count], tool.getExtension(names[count]));
            }

            this.setDataInputNodeCount(tool.getDataInputNodeCount());
            this.setDataOutputNodeCount(tool.getDataOutputNodeCount());

            int paramin = tool.getParameterInputNodeCount();
            ParameterNode paramnode;
            for (count = 0; count < paramin; count++) {
                paramnode = addParameterInputNode(tool.getParameterInputName(count));
                paramnode.setTriggerNode(tool.isParameterTriggerNode(count));
            }

            int paramout = tool.getParameterOutputNodeCount();
            for (count = 0; count < paramout; count++) {
                addParameterOutputNode(tool.getParameterOutputName(count));
            }

            String[] params = tool.getParameterNames();
            Object paramval;
            String paramtype;

            for (count = 0; count < params.length; count++) {
                if (tool instanceof ToolImp) {
                    paramval = ((ToolImp) tool).getParameter(params[count], false);
                } else {
                    paramval = tool.getParameter(params[count]);
                }

                paramtype = tool.getParameterType(params[count]);

                if (paramval != null) {
                    this.setParameter(params[count], paramval);
                }

                if (paramtype != null) {
                    this.setParameterType(params[count], paramtype);
                }
            }

        } catch (NodeException except) {
            throw (new TaskException(except));
        } catch (ProxyInstantiationException except) {
            throw (new TaskException(except));
        }
    }


    /**
     * Initialisation method is called immediately after the parent is set
     */
    public void init() throws TaskException {
    }


    /**
     * Sets the name associated with this Task.
     */
    public void setToolName(String toolname) {
        String oldname = getToolName();

        if (!toolname.equals(oldname)) {
            super.setToolName(toolname);
            notifyPropertyUpdate(TaskPropertyEvent.TASK_NAME_UPDATE, oldname, toolname);
        }
    }

    public String getQualifiedTaskName() {
        String taskname = getToolName();
        TaskGraph parent = getParent();

        while (parent != null) {
            if (parent.getParent() != null) {
                taskname = parent.getToolName() + "." + taskname;
            }

            parent = parent.getParent();
        }

        return taskname;
    }

    /**
     * @return the taskgraph that this task is located within
     */
    public TaskGraph getParent() {
        return taskgraph;
    }

    public TaskGraph getUltimateParent() {
        if (taskgraph != null) {
            TaskGraph ult = taskgraph.getParent();
            if (ult != null) {
                return ult;
            }
            return taskgraph;
        }
        if (this instanceof TaskGraph) {
            return (TaskGraph) this;
        }
        return null;
    }

    /**
     * Sets the taskgraph that this task is located within
     */
    public void setParent(TaskGraph taskgraph) {
        this.taskgraph = taskgraph;


    }

    /**
     * All copies of a task within a parameter space (sharing the same parameters) have the same intance id.
     *
     * @return the instance id of this task
     */
    public String getInstanceID() {
        return instanceid;
    }


    /**
     * Adds a proxy this tool
     */

    public void setProxy(Proxy proxy) throws TaskException {
        if (getParent() != null) {
            throw (new TaskException("Cannot set proxy in " + getToolName() + ": Task already instantiated"));
        }

        super.setProxy(proxy);
    }

    /**
     * Refreshes the proxy
     */
    public void updateProxy() {
        notifyPropertyUpdate(TaskPropertyEvent.PROXY_UPDATE, null, null);
    }

    /**
     * Removes the proxy for this tool
     */
    public void removeProxy() throws TaskException {
        if (getParent() != null) {
            throw (new TaskException("Cannot remove proxy in " + getToolName() + ": Task already instantiated"));
        }

        super.removeProxy();
    }


    /**
     * Adds a task listener to this task.
     */
    public void addTaskListener(TaskListener listener) {
        final TaskListener list = listener;

        TaskGraphEventDispatch.invokeLater(new Runnable() {
            public void run() {
                if (!listeners.contains(list)) {
                    listeners.add(list);
                    listenerarray = (TaskListener[]) listeners.toArray(new TaskListener[listeners.size()]);
                }
            }
        });
    }

    /**
     * Removes a task listener from this task.
     */
    public void removeTaskListener(TaskListener listener) {
        final TaskListener list = listener;

        TaskGraphEventDispatch.invokeLater(new Runnable() {
            public void run() {
                listeners.remove(list);
                listenerarray = (TaskListener[]) listeners.toArray(new TaskListener[listeners.size()]);
            }
        });
    }


    /**
     * Sets the value for the specified parameter name.
     */
    public void setParameter(String name, Object value) {

        Object oldvalue = getParameter(name);

        super.setParameter(name, value);
        if (value != null) {
            if (!value.equals(oldvalue)) {
                notifyParameterSet(name, getParameterType(name), oldvalue, value);
            }
        }
    }

    /**
     * Removes the value associated with the specified parameter name.
     */
    public void removeParameter(String name) {
        Object oldvalue = getParameter(name);

        super.removeParameter(name);

        if (oldvalue != null) {
            notifyParameterSet(name, getParameterType(name), oldvalue, null);
        }
    }


    /**
     * Adds a data input node.
     */
    public Node addDataInputNode() throws NodeException {
        Node node = factory.createNode(this, true);
        innodes.add(node);

        notifyNodeAdded(node);

        return node;
    }

    /**
     * Removes a data input node.
     */
    public void removeDataInputNode(Node node) {
        if (innodes.contains(node)) {
            disposeNode((Node) node, innodes);
        }
    }

    /**
     * @return the data input node at the specified index
     */
    public Node getDataInputNode(int index) {
        return (Node) innodes.get(index);
    }

    /**
     * @return an array of data input nodes
     */
    public Node[] getDataInputNodes() {
        return (Node[]) innodes.toArray(new Node[innodes.size()]);
    }

    /**
     * @return the number of data input nodes
     */
    public int getDataInputNodeCount() {
        return innodes.size();
    }

    /**
     * Used by ToolFactory to set the number of input nodes.
     */
    public void setDataInputNodeCount(int nodeCount) throws NodeException {
        while (getDataInputNodeCount() < nodeCount) {
            addDataInputNode();
        }

        while (getDataInputNodeCount() > nodeCount) {
            removeDataInputNode(getDataInputNode(getDataInputNodeCount() - 1));
        }
    }


    /**
     * Adds a data output node.
     */
    public Node addDataOutputNode() throws NodeException {
        Node node = factory.createNode(this, false);
        outnodes.add(node);

        notifyNodeAdded(node);

        return node;
    }

    /**
     * Removes a data output node.
     */
    public void removeDataOutputNode(Node node) {
        if (outnodes.contains(node)) {
            disposeNode((Node) node, outnodes);
        }
    }

    /**
     * @return the data output node at the specified index
     */
    public Node getDataOutputNode(int index) {
        return (Node) outnodes.get(index);
    }

    /**
     * @return an array of data output nodes
     */
    public Node[] getDataOutputNodes() {
        return (Node[]) outnodes.toArray(new Node[outnodes.size()]);
    }

    /**
     * @return the number of data output nodes
     */
    public int getDataOutputNodeCount() {
        return outnodes.size();
    }

    /**
     * Used by ToolFactory to set the number of output nodes.
     */
    public void setDataOutputNodeCount(int nodeCount) throws NodeException {
        while (getDataOutputNodeCount() < nodeCount) {
            addDataOutputNode();
        }

        while (getDataOutputNodeCount() > nodeCount) {
            removeDataOutputNode(getDataOutputNode(getDataOutputNodeCount() - 1));
        }
    }


    /**
     * Adds a parameter input node for the specified parameter name.
     */
    public ParameterNode addParameterInputNode(String paramname) throws NodeException {
        ParameterNode node = factory.createParameterNode(paramname, this, true);
        inparams.add(node);

        notifyNodeAdded(node);

        return node;
    }

    /**
     * Removes a parameter input node.
     */
    public void removeParameterInputNode(ParameterNode node) {
        if (inparams.contains(node)) {
            disposeNode((Node) node, inparams);
        }
    }

    /**
     * Used to set the names/number of input parameter nodes
     */
    public void setParameterInputs(String[] names, boolean[] trigger) throws NodeException {
        ParameterNode[] innodes = getParameterInputNodes();
        ParameterNode node;

        for (int count = 0; (count < names.length) && (count < innodes.length); count++) {
            innodes[count].setParameterName(names[count]);
            innodes[count].setTriggerNode(trigger[count]);
        }

        while (getParameterInputNodeCount() > names.length) {
            removeParameterInputNode(getParameterInputNode(getParameterInputNodeCount() - 1));
        }

        while (names.length > getParameterInputNodeCount()) {
            node = addParameterInputNode(names[getParameterInputNodeCount()]);
            node.setTriggerNode(trigger[getParameterInputNodeCount() - 1]);
        }
    }

    /**
     * @return the parameter input node at the specified index
     */
    public ParameterNode getParameterInputNode(int index) {
        return (ParameterNode) inparams.get(index);
    }

    /**
     * @return the parameter name input on the specified node index
     */
    public String getParameterInputName(int index) {
        return getParameterInputNode(index).getParameterName();
    }

    /**
     * @return true if the specified parameter input node is a trigger node
     */
    public boolean isParameterTriggerNode(int index) {
        return getParameterInputNode(index).isTriggerNode();
    }


    /**
     * @return an array of parameter input nodes
     */
    public ParameterNode[] getParameterInputNodes() {
        return (ParameterNode[]) inparams.toArray(new ParameterNode[inparams.size()]);
    }

    /**
     * @return the number of parameter input nodes
     */
    public int getParameterInputNodeCount() {
        return inparams.size();
    }


    /**
     * Adds a parameter output node for the specified paramter name.
     */
    public ParameterNode addParameterOutputNode(String paramname) throws NodeException {
        ParameterNode node = factory.createParameterNode(paramname, this, false);
        outparams.add(node);

        notifyNodeAdded(node);

        return node;
    }

    /**
     * Removes a parameter output node.
     */
    public void removeParameterOutputNode(ParameterNode node) {
        if (outparams.contains(node)) {
            disposeNode((Node) node, outparams);
        }
    }

    /**
     * Used to set the names/number of output parameter nodes
     */
    public void setParameterOutputNames(String[] names) throws NodeException {
        ParameterNode[] outnodes = getParameterOutputNodes();

        for (int count = 0; (count < names.length) && (count < outnodes.length); count++) {
            outnodes[count].setParameterName(names[count]);
        }

        while (getParameterOutputNodeCount() > names.length) {
            removeParameterOutputNode(getParameterOutputNode(getParameterOutputNodeCount() - 1));
        }

        while (names.length > getParameterOutputNodeCount()) {
            addParameterOutputNode(names[getParameterOutputNodeCount()]);
        }
    }

    /**
     * @return the parameter name input on the specified node index
     */
    public String getParameterOutputName(int index) {
        return getParameterOutputNode(index).getParameterName();
    }

    /**
     * @return the parameter output node at the specified index
     */
    public ParameterNode getParameterOutputNode(int index) {
        return (ParameterNode) outparams.get(index);
    }

    /**
     * @return an array of parameter output nodes
     */
    public ParameterNode[] getParameterOutputNodes() {
        return (ParameterNode[]) outparams.toArray(new ParameterNode[outparams.size()]);
    }

    /**
     * @return the number of parameter output nodes
     */
    public int getParameterOutputNodeCount() {
        return outparams.size();
    }


    /**
     * disconnects and cleans-up when a node is removed
     */
    private void disposeNode(Node node, ArrayList list) {
        if (list.contains(node)) {
            if ((node.getBottomLevelTask().getParent() != null) && (node.isConnected())) {
                try {
                    node.getBottomLevelTask().getParent().disconnect(node.getCable());
                } catch (CableException except) {
                    except.printStackTrace();
                }
            }

            notifyNodeRemoved(node);
            list.remove(node);
            node.dispose();
        }
    }

    /**
     * Returns the index of the specified node within the data input/output and parameter input/output nodes; or -1 if
     * not attached to this task. The index returned is not unique, e.g. the first data input node and the first data
     * output nodes will both return 0.
     */
    public int getNodeIndex(Node node) {
        if (innodes.contains(node)) {
            return innodes.indexOf(node);
        } else if (outnodes.contains(node)) {
            return outnodes.indexOf(node);
        } else if (inparams.contains(node)) {
            return inparams.indexOf(node);
        } else if (outparams.contains(node)) {
            return outparams.indexOf(node);
        } else {
            return -1;
        }
    }

    /**
     * This is a convience method to provide backward compatibility with TrianaGUI, in which parameter nodes where
     * indexed after data nodes.
     * <p/>
     * The absolute index of a data node is the same as its standard index. The absolute index of a parameter node is
     * its standard index + the total number of data input nodes.
     *
     * @return the absolute index of this node within its associated task.
     */
    public int getAbsoluteNodeIndex(Node node) {
        if (getNodeIndex(node) == -1) {
            return -1;
        } else if (isParameterInputNode(node)) {
            return getDataInputNodeCount() + getNodeIndex(node);
        } else if (isParameterOutputNode(node)) {
            return getDataOutputNodeCount() + getNodeIndex(node);
        }

        return getNodeIndex(node);
    }


    /**
     * @return true if the specified node is a data input node for this task
     */
    public boolean isDataInputNode(Node node) {
        return innodes.contains(node);
    }

    /**
     * @return true if the specified node is a data output node for this task
     */
    public boolean isDataOutputNode(Node node) {
        return outnodes.contains(node);
    }

    /**
     * @return true if the specified node is a parameter input node for this task
     */
    public boolean isParameterInputNode(Node node) {
        return inparams.contains(node);
    }

    /**
     * @return true if the specified node is a parameter output node for this task
     */
    public boolean isParameterOutputNode(Node node) {
        return outparams.contains(node);
    }


    /**
     * @return all input nodes (data and parameter)
     */
    public Node[] getInputNodes() {
        Node[] allin = new Node[getDataInputNodeCount() + getParameterInputNodeCount()];
        System.arraycopy(getDataInputNodes(), 0, allin, 0, getDataInputNodeCount());
        System.arraycopy(getParameterInputNodes(), 0, allin, getDataInputNodeCount(), getParameterInputNodeCount());

        return allin;
    }

    /**
     * @return the input node at the specified absolute index (data/parameter)
     */
    public Node getInputNode(int absoluteindex) {
        if (absoluteindex >= getDataInputNodeCount()) {
            return getParameterInputNode(absoluteindex - getDataInputNodeCount());
        } else {
            return getDataInputNode(absoluteindex);
        }
    }


    /**
     * @return all output nodes (data and parameter)
     */
    public Node[] getOutputNodes() {
        Node[] allout = new Node[getDataOutputNodeCount() + getParameterOutputNodeCount()];
        System.arraycopy(getDataOutputNodes(), 0, allout, 0, getDataOutputNodeCount());
        System.arraycopy(getParameterOutputNodes(), 0, allout, getDataOutputNodeCount(), getParameterOutputNodeCount());

        return allout;
    }

    /**
     * @return the output node at the specified absolute index (data/parameter)
     */
    public Node getOutputNode(int absoluteindex) {
        if (absoluteindex >= getDataOutputNodeCount()) {
            return getParameterOutputNode(absoluteindex - getDataOutputNodeCount());
        } else {
            return getDataOutputNode(absoluteindex);
        }
    }


    /**
     * Removes the specified node
     */
    public void removeNode(Node node) {
        removeDataInputNode(node);
        removeDataOutputNode(node);

        if (node instanceof ParameterNode) {
            removeParameterInputNode((ParameterNode) node);
            removeParameterOutputNode((ParameterNode) node);
        }
    }


    /**
     * @return true if this task is set to run continuously
     */
    public boolean isRunContinuously() {
        return runcontin;
    }

    /**
     * Sets whether this task runs continuously.
     */
    public void setRunContinuously(boolean state) {
        boolean oldstate = runcontin;

        runcontin = state;

        notifyPropertyUpdate(TaskPropertyEvent.RUN_CONTINUOUSLY_UPDATE, new Boolean(oldstate), new Boolean(state));
    }


    /**
     * Sets the default input node requirement for this task (ESSENTIAL, ESSENTIAL_IF_CONNECTED or OPTIONAL)
     */
    public void setDefaultNodeRequirement(String requirement) {
        setParameterType(DEFAULT_NODE_REQUIREMENT, INTERNAL);
        setParameter(DEFAULT_NODE_REQUIREMENT, requirement);
    }

    /**
     * @return the default requirement for this task's input nodes
     */
    public String getDefaultNodeRequirement() {
        if (isParameterName(DEFAULT_NODE_REQUIREMENT)) {
            return (String) getParameter(DEFAULT_NODE_REQUIREMENT);
        } else {
            return ESSENTIAL;
        }
    }

    /**
     * Sets the input node requirement for the specified node index (ESSENTIAL, ESSENTIAL_IF_CONNECTED or OPTIONAL)
     */
    public void setNodeRequirement(int index, String requirement) {
        setParameterType(NODE_REQUIREMENT + index, INTERNAL);
        setParameter(NODE_REQUIREMENT + index, requirement);
    }

    /**
     * @return the requirement for this specified input node index.
     */
    public String getNodeRequirement(int index) {
        if (isParameterName(NODE_REQUIREMENT + index)) {
            return (String) getParameter(NODE_REQUIREMENT + index);
        } else {
            return getDefaultNodeRequirement();
        }
    }


    /**
     * @return the number of times the task has been requested to execut
     */
    public int getExecutionRequestCount() {
        if (isParameterName(EXECUTION_REQUEST_COUNT)) {
            return Integer.parseInt((String) getParameter(EXECUTION_REQUEST_COUNT));
        } else {
            return 0;
        }
    }

    /**
     * @return the number of times the task has been executed
     */
    public int getExecutionCount() {
        if (isParameterName(EXECUTION_COUNT)) {
            return Integer.parseInt((String) getParameter(EXECUTION_COUNT));
        } else {
            return 0;
        }
    }

    /**
     * @return the current execution state of the task
     */
    public ExecutionState getExecutionState() {
        if (isParameterName(EXECUTION_STATE)) {
            return (ExecutionState) getParameter(EXECUTION_STATE);
        } else {
            return ExecutionState.UNKNOWN;
        }
    }

    /**
     * @return the error message associated with the current error (null if the task is not in an error state)
     */
    public String getErrorMessage() {
        if (isParameterName(ERROR_MESSAGE)) {
            return (String) getParameter(ERROR_MESSAGE);
        } else {
            return null;
        }
    }


    /**
     * Notifies all the task listeners that a task property has been updated.
     */
    protected void notifyPropertyUpdate(final int property, final Object oldval, final Object newval) {
        final Task task = this;

        TaskGraphEventDispatch.invokeLater(new Runnable() {
            public void run() {
                TaskPropertyEvent event = new TaskPropertyEvent(task, property, oldval, newval);

                for (int count = 0; count < listenerarray.length; count++) {
                    listenerarray[count].taskPropertyUpdate(event);
                }
            }
        });
    }

    /**
     * Notifies all the task listeners that a node has been added.
     */
    protected void notifyNodeAdded(final Node node) {
        final Task task = this;
        final int index = node.getNodeIndex();
        final int absindex = node.getAbsoluteNodeIndex();

        TaskGraphEventDispatch.invokeLater(new Runnable() {
            public void run() {
                TaskNodeEvent event = new TaskNodeEvent(TaskNodeEvent.NODE_ADDED, task, node, index, absindex);

                for (int count = 0; count < listenerarray.length; count++) {
                    listenerarray[count].nodeAdded(event);
                }
            }
        });
    }

    /**
     * Notifies all the task listeners that a node has been removed.
     */
    protected void notifyNodeRemoved(final Node node) {
        final Task task = this;
        final int index = node.getNodeIndex();
        final int absindex = node.getAbsoluteNodeIndex();

        TaskGraphEventDispatch.invokeLater(new Runnable() {
            public void run() {
                TaskNodeEvent event = new TaskNodeEvent(TaskNodeEvent.NODE_REMOVED, task, node, index, absindex);

                for (int count = 0; count < listenerarray.length; count++) {
                    listenerarray[count].nodeRemoved(event);
                }
            }
        });
    }

    /**
     * Notifies all the task listeners that a parameter has been set.
     */
    protected void notifyParameterSet(final String name, final String type, final Object oldval, final Object newval) {
        final Task task = this;

        Runnable event = new Runnable() {
            public void run() {
                ParameterUpdateEvent event = new ParameterUpdateEvent(task, name, type, oldval, newval);

                for (int count = 0; count < listenerarray.length; count++) {
                    listenerarray[count].parameterUpdated(event);
                }
            }
        };

        if (GUI.equals(type)) {
            event.run();
        } else {
            TaskGraphEventDispatch.invokeLater(event);
        }
    }


    /**
     * Notifies all the task listeners that the task is about to be disposed
     */
    protected void notifyTaskDisposed() {
        final Task task = this;

        TaskGraphEventDispatch.invokeLater(new Runnable() {
            public void run() {
                TaskDisposedEvent event = new TaskDisposedEvent(task);

                for (int count = 0; count < listenerarray.length; count++) {
                    listenerarray[count].taskDisposed(event);
                }
            }
        });
    }


    /**
     * cleans up any operations associated with this task
     */
    public void dispose() {
        Node[] nodes = (Node[]) innodes.toArray(new Node[innodes.size()]);
        for (int count = 0; count < nodes.length; count++) {
            disposeNode(nodes[count], innodes);
        }

        nodes = (Node[]) outnodes.toArray(new Node[outnodes.size()]);
        for (int count = 0; count < nodes.length; count++) {
            disposeNode(nodes[count], outnodes);
        }

        nodes = (Node[]) inparams.toArray(new Node[inparams.size()]);
        for (int count = 0; count < nodes.length; count++) {
            disposeNode(nodes[count], inparams);
        }

        nodes = (Node[]) outparams.toArray(new Node[outparams.size()]);
        for (int count = 0; count < nodes.length; count++) {
            disposeNode(nodes[count], outparams);
        }

        notifyTaskDisposed();

        InstanceIDManager.unregisterID(this);

        setParent(null);
        listeners.clear();

        innodes.clear();
        outnodes.clear();
        inparams.clear();
        outparams.clear();
    }

    public TaskGraphContext getContext() {
        return context;
    }

    public Object getContextProperty(String name) {
        Object ret = context.getProperty(name);
        if (ret == null && getParent() != null) {
            ret = getParent().getContextProperty(name);
        }
        return ret;
    }

    @Override
    public void setContextProperty(String name, Object value) {
        context.setProperty(name, value);
    }

    public void setSubTitle(String subtext) {
        String old = this.subtext;
        this.subtext = subtext;
        if (!this.subtext.equals(old)) {
            notifyPropertyUpdate(TaskPropertyEvent.TASK_SUBNAME_UPDATE, old, this.subtext);
        }
    }

    public String getSubTitle() {
        return subtext;
    }

}
