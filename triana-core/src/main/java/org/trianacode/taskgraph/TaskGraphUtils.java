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
package org.trianacode.taskgraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Vector;

import org.trianacode.taskgraph.imp.TaskFactoryImp;
import org.trianacode.taskgraph.imp.TaskGraphImp;
import org.trianacode.taskgraph.imp.TaskImp;
import org.trianacode.taskgraph.imp.ToolImp;
import org.trianacode.taskgraph.proxy.ProxyFactory;
import org.trianacode.taskgraph.proxy.ProxyInstantiationException;
import org.trianacode.taskgraph.tool.Tool;


/**
 * Utility functions useful in handling task graphs
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 */
public class TaskGraphUtils {

    /**
     * @return true if the specified task is a control task
     */
    public static boolean isControlTask(Task task) {
        if (task.getParent() == null) {
            return false;
        } else {
            return (task.getParent().getControlTask() == task);
        }
    }


    /**
     * Creates a non-runnable clone of the specfied tool.
     */
    public static Tool cloneTool(Tool tool) throws TaskException {
        if (tool instanceof TaskGraph) {
            try {
                return cloneTaskGraph((TaskGraph) tool, TaskGraphManager.NON_RUNNABLE_FACTORY_TYPE, false, false, true);
            } catch (TaskGraphException except) {
                throw (new TaskException(except));
            }
        } else {
            return toolClone(tool);
        }
    }

    /**
     * Creates a non-runnable clone of the specfied task, optionally preserving the instance id.
     */
    public static Task cloneTask(Task task, boolean preserveinst) throws TaskException {
        if (task instanceof TaskGraph) {
            try {
                return (Task) cloneTaskGraph((TaskGraph) task, TaskGraphManager.NON_RUNNABLE_FACTORY_TYPE, preserveinst,
                        false, true);
            } catch (TaskGraphException except) {
                throw (new TaskException(except));
            }
        } else {
            return taskClone(task, preserveinst);
        }
    }

    /**
     * Creates a clone of the tool. If the tool is a taskgraph then a dummy single tool is returned
     */
    public static Tool dummyCloneTool(Tool tool) throws TaskException {
        return toolClone(tool);
    }

    /**
     * Creates a clone of the task. If the task is a taskgraph then a dummy single task is returned
     */
    public static Task dummyCloneTask(Task task, boolean preserveinst) throws TaskException {
        return taskClone(task, preserveinst);
    }


    /**
     * Creates a place holder tool, which is a copy of only the nodes and tool classes of the specified tool.
     */
    public static Tool createPlaceHolderTool(Tool tool) throws TaskException {
        try {
            ToolImp placeholder = new ToolImp();
            placeholder.setToolName(tool.getToolName());
            placeholder.setDataInputNodeCount(tool.getDataInputNodeCount());
            placeholder.setDataOutputNodeCount(tool.getDataOutputNodeCount());
            placeholder.setProxy(ProxyFactory.cloneProxy(tool.getProxy()));

            RenderingHint[] hints = tool.getRenderingHints();
            for (int count = 0; count < hints.length; count++) {
                placeholder.addRenderingHint(hints[count]);
            }

            String[] names = tool.getExtensionNames();
            for (int count = 0; count < names.length; count++) {
                placeholder.addExtension(names[count], tool.getExtension(names[count]));
            }

            int paramin = tool.getParameterInputNodeCount();
            String[] paramnames = new String[paramin];
            boolean[] trigger = new boolean[paramin];

            for (int count = 0; count < paramin; count++) {
                paramnames[count] = tool.getParameterInputName(count);
                trigger[count] = tool.isParameterTriggerNode(count);
            }

            placeholder.setParameterInputs(paramnames, trigger);

            int paramout = tool.getParameterOutputNodeCount();
            paramnames = new String[paramout];

            for (int count = 0; count < paramout; count++) {
                paramnames[count] = tool.getParameterOutputName(count);
            }

            placeholder.setParameterOutputs(paramnames);

            if (tool instanceof TaskGraph) {
                return new TaskGraphImp(placeholder, new TaskFactoryImp(), false);
            } else {
                return placeholder;
            }
        } catch (NodeException except) {
            throw (new TaskException(except));
        } catch (ProxyInstantiationException except) {
            throw (new TaskException(except));
        }
    }


    /**
     * Creates a clone of the specified taskgraph within the parent, optionally preserving the instance ids of the
     * cloned task.
     */
    public static TaskGraph cloneTaskGraph(TaskGraph taskgraph, TaskGraph parent, boolean preserveinst)
            throws TaskGraphException {
        TaskGraph clone;
        String factorytype;

        if (parent != null) {
            factorytype = TaskGraphManager.getTaskGraphFactoryType(parent);
        } else {
            throw (new TaskException("Parent taskgraphs must be created using TaskGraphManager"));
        }

        if (preserveinst) {
            clone = TaskGraphUtils.cloneTaskGraph(taskgraph, factorytype);
        } else {
            clone = TaskGraphUtils.copyTaskGraph(taskgraph, factorytype);
        }

        ((Task) clone).setParent(parent);
        ((Task) clone).init();

        return clone;
    }


    /**
     * Creates a full clone of the taskgraph, preserving the instances of the clone and all the tasks within the clone.
     * Note that setParent() and init() are not called on the clone.
     */
    public static TaskGraph cloneTaskGraph(TaskGraph taskgraph, String factorytype) throws TaskGraphException {
        return cloneTaskGraph(taskgraph, factorytype, true, true, true);
    }

    /**
     * Creates a semi-clone of the taskgraph, preserving the instances of the tasks within the the clone, but not the
     * clone itself (i.e. the clone is a new taskgraph instance with the same contents). Note that the control task is
     * not cloned. Also note that setParent() and init() are not called on the clone.
     */
    public static TaskGraph semiCloneTaskGraph(TaskGraph taskgraph, String factorytype) throws TaskGraphException {
        return cloneTaskGraph(taskgraph, factorytype, false, true, false);
    }

    /**
     * Creates a copy of the taskgraph, not preserving the instance id of the clone or the tasks within the clone. Note
     * that setParent() and init() are not called on the clone.
     */
    public static TaskGraph copyTaskGraph(TaskGraph taskgraph, String factorytype) throws TaskGraphException {
        return cloneTaskGraph(taskgraph, factorytype, false, false, true);
    }


    /**
     * Creates a clone of the specified tool
     */
    private static Tool toolClone(Tool tool) throws TaskException {
        return new ToolImp(tool, tool.getProperties());
    }

    /**
     * Creates a clone of the specified tool
     */
    private static Task taskClone(Task task, boolean preserveinst) throws TaskException {
        return new TaskImp(task, new TaskFactoryImp(), preserveinst);
    }


    /**
     * Creates a clone copy of the specified taskgraph that is created using the specified taskgraph factory, optionally
     * preserving the instance id of the original taskgraph in the clone. Note that the control task for the clone is
     * attached using the default connection policy.
     *
     * @param taskgraph    the taskgraph being clones
     * @param factorytype  the taskgraph factory type used to create the clone
     * @param presclone    a flag indicating whether the instance of the taskgraph is preserved
     * @param prestasks    a flag indicating whether the instance of tasks within the taskgraph are preserved
     * @param clonecontrol a flag indicating whether the control task is cloned
     */
    private static TaskGraph cloneTaskGraph(TaskGraph taskgraph, String factorytype, boolean presclone,
                                            boolean prestasks, boolean clonecontrol) throws TaskGraphException {
        try {
            TaskGraph clone = TaskGraphManager.createTaskGraph(taskgraph, factorytype, presclone);
            if (taskgraph.getToolName() != null) {
                clone.setToolName(taskgraph.getToolName());
            }

            Task[] tasks = taskgraph.getTasks(false);

            for (int count = 0; count < tasks.length; count++) {
                clone.createTask(tasks[count], prestasks);
            }

            Cable[] cables = TaskGraphUtils.getInternalCables(tasks);
            Task task;
            Node sendnode;
            Node recnode;

            try {
                for (int count = 0; count < cables.length; count++) {
                    task = clone.getTask(taskgraph.getTask(cables[count].getSendingNode()).getToolName());

                    if (cables[count].getSendingNode().isParameterNode()) {
                        sendnode = task.getParameterOutputNode(cables[count].getSendingNode().getNodeIndex());
                    } else {
                        sendnode = task.getDataOutputNode(cables[count].getSendingNode().getNodeIndex());
                    }

                    task = clone.getTask(taskgraph.getTask(cables[count].getReceivingNode()).getToolName());

                    if (cables[count].getReceivingNode().isParameterNode()) {
                        recnode = task.getParameterInputNode(cables[count].getReceivingNode().getNodeIndex());
                    } else {
                        recnode = task.getDataInputNode(cables[count].getReceivingNode().getNodeIndex());
                    }

                    clone.connect(sendnode, recnode);
                }
            } catch (CableException e) {
                e.printStackTrace();
            }

            TaskGraph group = taskgraph;
            Node nodes[] = group.getDataInputNodes();
            Node node;
            Node clonenode;

            try {
                for (int count = 0; count < nodes.length; count++) {
                    if (taskgraph.isControlTaskConnected()) {
                        node = TaskGraphUtils.getControlNode(nodes[count]).getCable().getReceivingNode();
                    } else {
                        node = nodes[count].getParentNode();
                    }

                    if (node.isParameterNode()) {
                        clonenode = clone.getTask(taskgraph.getTask(node).getToolName())
                                .getParameterInputNode(node.getNodeIndex());
                    } else {
                        clonenode = clone.getTask(taskgraph.getTask(node).getToolName())
                                .getDataInputNode(node.getNodeIndex());
                    }

                    clone.setGroupNodeParent(clone.getDataInputNode(count), clonenode);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            nodes = group.getDataOutputNodes();

            try {
                for (int count = 0; count < nodes.length; count++) {
                    if (taskgraph.isControlTaskConnected()) {
                        node = TaskGraphUtils.getControlNode(nodes[count]).getCable().getSendingNode();
                    } else {
                        node = nodes[count].getParentNode();
                    }

                    if (node.isParameterNode()) {
                        clonenode = clone.getTask(taskgraph.getTask(node).getToolName())
                                .getParameterOutputNode(node.getNodeIndex());
                    } else {
                        clonenode = clone.getTask(taskgraph.getTask(node).getToolName())
                                .getDataOutputNode(node.getNodeIndex());
                    }

                    clone.setGroupNodeParent(clone.getDataOutputNode(count), clonenode);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (taskgraph.isControlTask() && clonecontrol) {
                clone.createControlTask(taskgraph.getControlTask(), prestasks);

                if (taskgraph.isControlTaskConnected()) {
                    connectControlTask(clone);
                }
            }
            TaskGraphContext context = taskgraph.getContext();
            Collection<String> keys = context.getKeys();
            for (String key : keys) {
                clone.setContextProperty(key, context.getProperty(key));
            }


            return clone;
        } catch (ClassCastException except) {
            except.printStackTrace();
            throw (new TaskGraphException("cloningError" + ": " + "NodeError", except));
        } catch (TaskGraphException except) {
            except.printStackTrace();
            throw (new TaskGraphException("cloningError" + ": " + except.getMessage(), except));
        }
    }


    /**
     * Create new tasks in the specified taskgraph taskgraph, optionally preserving the original instance ids in the new
     * tasks. Any connections between the specified tools are also created.
     *
     * @return the interfaces to the new tasks
     */
    public static Task[] createTasks(Tool[] tools, TaskGraph taskgraph, boolean preserveinst)
            throws TaskException, CableException {
        Task[] taskarray = new Task[tools.length];
        Hashtable idmap = new Hashtable();

        for (int count = 0; count < tools.length; count++) {
            taskarray[count] = taskgraph.createTask(tools[count], preserveinst);
            idmap.put(tools[count], taskarray[count]);
        }

        ArrayList tasklist = new ArrayList();
        for (int count = 0; count < tools.length; count++) {
            if (tools[count] instanceof Task) {
                tasklist.add(tools[count]);
            }
        }

        Task[] tasks = (Task[]) tasklist.toArray(new Task[tasklist.size()]);

        Cable[] cables = TaskGraphUtils.getInternalCables(tasks);
        Task sendtask;
        Task rectask;
        Node sendnode;
        Node recnode;

        for (int count = 0; count < cables.length; count++) {
            sendtask = (Task) idmap.get(cables[count].getSendingTask());
            rectask = (Task) idmap.get(cables[count].getReceivingTask());

            if (cables[count].getSendingNode().isDataNode()) {
                sendnode = sendtask.getDataOutputNode(cables[count].getSendingNode().getNodeIndex());
            } else {
                sendnode = sendtask.getParameterOutputNode(cables[count].getSendingNode().getNodeIndex());
            }

            if (cables[count].getReceivingNode().isDataNode()) {
                recnode = rectask.getDataInputNode(cables[count].getReceivingNode().getNodeIndex());
            } else {
                recnode = rectask.getParameterInputNode(cables[count].getReceivingNode().getNodeIndex());
            }

            taskgraph.connect(sendnode, recnode);
        }

        return taskarray;
    }


    /**
     * Replaces a current task with a new tool (instance id is preserved)
     */
    public static Task replaceTask(Task curtask, Tool newtool, boolean rename) throws TaskGraphException {
        return replaceTask(curtask, newtool, rename, true);
    }

    /**
     * Replaces a current task with a new tool.
     * <p/>
     * Note that newtool is a task with no parent it is automatically disposed after the replacement task has been
     * created.
     *
     * @param curtask      the task being replaced
     * @param newtool      the tool used to replace the existing task
     * @param rename       true if the new task should take the same name as the existing task
     * @param preserveinst true if the new task should preseve the instance id of newtool
     */
    public static Task replaceTask(Task curtask, Tool newtool, boolean rename, boolean preserveinst)
            throws TaskGraphException {
        if (curtask.getParent() == null) {
            throw (new RuntimeException("Error replacing task: Task being replaced does not have a parent taskgraph"));
        }

        TaskGraph parent = curtask.getParent();
        boolean reconnectcontrol = false;
        String taskname = curtask.getToolName();

        rename = rename || (newtool.getToolName() == curtask.getToolName());

        if (parent != null) {
            if (parent.isControlTaskConnected()) {
                TaskGraphUtils.disconnectControlTask(parent);
                reconnectcontrol = true;
            }

            Node[] sendnodes = curtask.getInputNodes();
            Node[] childinnodes = new Node[sendnodes.length];

            for (int count = 0; count < sendnodes.length; count++) {
                if (sendnodes[count].getChildNode() != null) {
                    childinnodes[count] = (Node) sendnodes[count].getChildNode();
                    ((Node) sendnodes[count]).setChildNode(null);
                }

                if (sendnodes[count].isConnected()) {
                    sendnodes[count] = sendnodes[count].getCable().getSendingNode();
                } else {
                    sendnodes[count] = null;
                }
            }

            Node[] recnodes = curtask.getOutputNodes();
            Node[] childoutnodes = new Node[recnodes.length];

            for (int count = 0; count < recnodes.length; count++) {
                if (recnodes[count].getChildNode() != null) {
                    childoutnodes[count] = (Node) recnodes[count].getChildNode();
                    ((Node) recnodes[count]).setChildNode(null);
                }

                if (recnodes[count].isConnected()) {
                    recnodes[count] = recnodes[count].getCable().getReceivingNode();
                } else {
                    recnodes[count] = null;
                }
            }

            Task newtask = parent.createTask(newtool, preserveinst);

            if (!(newtask instanceof TaskGraph)) {
                while (newtask.getDataInputNodeCount() < curtask.getDataInputNodeCount()) {
                    newtask.addDataInputNode();
                }

                while (newtask.getDataInputNodeCount() > curtask.getDataInputNodeCount()) {
                    newtask.removeDataInputNode(newtask.getDataInputNode(newtask.getDataInputNodeCount() - 1));
                }

                while (newtask.getDataOutputNodeCount() < curtask.getDataOutputNodeCount()) {
                    newtask.addDataOutputNode();
                }

                while (newtask.getDataOutputNodeCount() > curtask.getDataOutputNodeCount()) {
                    newtask.removeDataOutputNode(newtask.getDataOutputNode(newtask.getDataOutputNodeCount() - 1));
                }

                while (newtask.getParameterInputNodeCount() > 0) {
                    newtask.removeParameterInputNode(newtask.getParameterInputNode(0));
                }

                for (int count = 0; count < curtask.getParameterInputNodeCount(); count++) {
                    newtask.addParameterInputNode(curtask.getParameterInputName(count));
                }

                while (newtask.getParameterOutputNodeCount() > 0) {
                    newtask.removeParameterOutputNode(newtask.getParameterOutputNode(0));
                }

                for (int count = 0; count < curtask.getParameterOutputNodeCount(); count++) {
                    newtask.addParameterOutputNode(curtask.getParameterOutputName(count));
                }
            }

            Node[] innodes = newtask.getInputNodes();

            for (int count = 0; (count < childinnodes.length); count++) {
                if (childinnodes[count] != null) {
                    if (count < innodes.length) {
                        childinnodes[count].setParentNode((Node) innodes[count]);
                        ((Node) innodes[count]).setChildNode(childinnodes[count]);
                    } else {
                        childinnodes[count].setParentNode(null);
                    }
                }
            }

            Node[] outnodes = newtask.getOutputNodes();

            for (int count = 0; (count < childoutnodes.length); count++) {
                if (childoutnodes[count] != null) {
                    if (count < outnodes.length) {
                        childoutnodes[count].setParentNode((Node) outnodes[count]);
                        ((Node) outnodes[count]).setChildNode(childoutnodes[count]);
                    } else {
                        childoutnodes[count].setParentNode(null);
                    }
                }
            }

            for (int count = 0; (count < innodes.length) && (count < sendnodes.length); count++) {
                if (sendnodes[count] != null) {
                    parent.connect(sendnodes[count], innodes[count].getBottomLevelNode());
                }
            }

            for (int count = 0; (count < outnodes.length) && (count < recnodes.length); count++) {
                if (recnodes[count] != null) {
                    parent.connect(outnodes[count].getBottomLevelNode(), recnodes[count]);
                }
            }

            if (reconnectcontrol) {
                TaskGraphUtils.connectControlTask(parent);
            }

            parent.removeTask(curtask);

            if (rename) {
                newtask.setToolName(taskname);
            }

            if ((newtool != curtask) && (newtool instanceof Task) && (((Task) newtool).getParent() == null)) {
                TaskGraphUtils.disposeTool(newtool);
            }

            return newtask;
        } else {
            throw (new RuntimeException("Cannot recreate top-level parent taskgraph"));
        }
    }

    /**
     * Disposes of the specified tool if no longer required
     */
    public static void disposeTool(Tool tool) {
        if (tool instanceof Task) {
            Task disposetask = (Task) tool;

            if (disposetask.getParent() != null) {
                disposetask.getParent().removeTask(disposetask);
            } else {
                disposetask.dispose();
            }
        }
    }

    /**
     * @return an array of the cables connected to the specified task
     */
    public static Cable[] getConnectedCables(Task task) {
        ArrayList list = new ArrayList(100);
        Node[] nodes;

        nodes = task.getDataInputNodes();

        for (int count = 0; count < nodes.length; count++) {
            if ((nodes[count].isConnected()) && (!list.contains(nodes[count].getCable()))) {
                list.add(nodes[count].getCable());
            }
        }

        nodes = task.getDataOutputNodes();

        for (int count = 0; count < nodes.length; count++) {
            if ((nodes[count].isConnected()) && (!list.contains(nodes[count].getCable()))) {
                list.add(nodes[count].getCable());
            }
        }

        nodes = task.getParameterInputNodes();

        for (int count = 0; count < nodes.length; count++) {
            if ((nodes[count].isConnected()) && (!list.contains(nodes[count].getCable()))) {
                list.add(nodes[count].getCable());
            }
        }

        nodes = task.getParameterOutputNodes();

        for (int count = 0; count < nodes.length; count++) {
            if ((nodes[count].isConnected()) && (!list.contains(nodes[count].getCable()))) {
                list.add(nodes[count].getCable());
            }
        }

        return getCableArray(list.toArray());
    }

    /**
     * @return an array of the cables connected to the specified tasks (internal and external)
     */
    public static Cable[] getConnectedCables(Task[] tasklist) {
        ArrayList list = new ArrayList(100);
        Cable[] cables;

        for (int count1 = 0; count1 < tasklist.length; count1++) {
            cables = getConnectedCables(tasklist[count1]);

            for (int count2 = 0; count2 < cables.length; count2++) {
                if (!list.contains(cables[count2])) {
                    list.add(cables[count2]);
                }
            }
        }

        return getCableArray(list.toArray());
    }

    /**
     * @return true if a given cable is connected to a given task
     */
    public static boolean isConnectedCable(Cable cable, Task task) {
        Cable[] cables = getConnectedCables(task);
        boolean flag = false;

        for (int count = 0; (count < cables.length) && (!flag); count++) {
            flag = (cables[count] == cable);
        }

        return true;
    }

    /**
     * Checks if a cable is connected to any task in an array of tasks
     */
    public static boolean isConnectedCable(Cable cable, Task[] tasklist) {
        Cable[] cables = getConnectedCables(tasklist);
        boolean flag = false;

        for (int count = 0; (count < cables.length) && (!flag); count++) {
            flag = (cables[count] == cable);
        }

        return true;
    }


    /**
     * @return the set of cables that are internal to a given set of tasks, i.e. only cables that connect two tasks in
     *         the input set.
     */
    public static Cable[] getInternalCables(Task[] tasklist) {
        ArrayList list = new ArrayList(10);
        Cable[] cables;

        for (int count1 = 0; count1 < tasklist.length; count1++) {
            cables = getConnectedCables(tasklist[count1]);

            for (int count2 = 0; count2 < cables.length; count2++) {
                for (int count3 = 0; count3 < tasklist.length; count3++) {
                    if ((cables[count2].connects(tasklist[count3])) &&
                            (tasklist[count3] != tasklist[count1]) &&
                            (!list.contains(cables[count2]))) {
                        list.add(cables[count2]);
                    }
                }
            }
        }

        return getCableArray(list.toArray());
    }

    /**
     * Checks to see if a given cable is internal to a set of tasks, i.e. it connects two of the given tasks.
     */
    public static boolean isInternalCable(Cable cable, Task[] tasklist) {
        Cable[] cables = getInternalCables(tasklist);
        boolean flag = false;

        for (int count = 0; (count < cables.length) && (!flag); count++) {
            flag = (cables[count] == cable);
        }

        return true;
    }

    /**
     * @return the set of cables that are external to a given set of tasks, i.e. only cables that connect a task in the
     *         input set with one outside.
     */
    public static Cable[] getExternalCables(Task[] tasklist) {
        ArrayList list = new ArrayList(10);
        Cable[] cables;
        boolean flag;

        for (int count1 = 0; count1 < tasklist.length; count1++) {
            cables = getConnectedCables(tasklist[count1]);

            for (int count2 = 0; count2 < cables.length; count2++) {
                flag = true;

                for (int count3 = 0; count3 < tasklist.length; count3++) {
                    if (cables[count2].connects(tasklist[count3]) && (tasklist[count3] != tasklist[count1])) {
                        flag = false;
                    }
                }

                if ((flag) && (!list.contains(cables[count2]))) {
                    list.add(cables[count2]);
                }
            }
        }

        return getCableArray(list.toArray());
    }

    /**
     * Checks to see if a given cable is external to a set of tasks, i.e. it connects a task in the input set with one
     * outside.
     */
    public static boolean isExternalCable(Cable cable, Task[] tasklist) {
        Cable[] cables = getExternalCables(tasklist);
        boolean flag = false;

        for (int count = 0; (count < cables.length) && (!flag); count++) {
            flag = (cables[count] == cable);
        }

        return true;
    }


    private static Cable[] getCableArray(Object[] master) {
        Cable[] copy = new Cable[master.length];

        for (int count = 0; count < master.length; count++) {
            copy[count] = (Cable) master[count];
        }

        return copy;
    }


    /**
     * @return all the tasks that preceed the specified task
     */
    public static Task[] getInputTasks(Task task) {
        Node[] nodes = task.getInputNodes();
        ArrayList tasks = new ArrayList();

        for (int count = 0; count < nodes.length; count++) {
            if (nodes[count].getCable() != null) {
                tasks.add(nodes[count].getCable().getSendingTask());
            }
        }

        return (Task[]) tasks.toArray(new Task[tasks.size()]);
    }

    /**
     * @return all the tasks that follow the specified task
     */
    public static Task[] getOutputTasks(Task task) {
        Node[] nodes = task.getOutputNodes();
        ArrayList tasks = new ArrayList();

        for (int count = 0; count < nodes.length; count++) {
            if (nodes[count].getCable() != null) {
                tasks.add(nodes[count].getCable().getReceivingTask());
            }
        }

        return (Task[]) tasks.toArray(new Task[tasks.size()]);
    }

    /**
     * @return all the tasks that are connected to the specified task
     */
    public static Task[] getConnectedTasks(Task task) {
        Node[] nodes = task.getInputNodes();
        ArrayList tasks = new ArrayList();

        for (int count = 0; count < nodes.length; count++) {
            if (nodes[count].getCable() != null) {
                tasks.add(nodes[count].getCable().getSendingTask());
            }
        }


        nodes = task.getOutputNodes();

        for (int count = 0; count < nodes.length; count++) {
            if (nodes[count].getCable() != null) {
                tasks.add(nodes[count].getCable().getReceivingTask());
            }
        }

        return (Task[]) tasks.toArray(new Task[tasks.size()]);
    }

    /**
     * @return the remaining workflow that follows the specified task, optionally including the original task at index
     *         0.
     */
    public static Task[] getRemainingWorkflow(Task task, boolean include) {
        ArrayList tasks = new ArrayList();
        Task temptask;
        Task[] conntasks;
        boolean done = false;
        int ptr = 0;

        if (include) {
            tasks.add(task);
            ptr++;
        }

        conntasks = getOutputTasks(task);

        for (int count = 0; count < conntasks.length; count++) {
            tasks.add(conntasks[count]);
        }

        while (ptr < tasks.size()) {
            temptask = (Task) tasks.get(ptr++);

            conntasks = getConnectedTasks(temptask);

            for (int count = 0; count < conntasks.length; count++) {
                if (!tasks.contains(conntasks[count])) {
                    tasks.add(conntasks[count]);
                }
            }
        }

        return (Task[]) tasks.toArray(new Task[tasks.size()]);
    }


    /**
     * Sets up the connections for the loop task. Input/output from the group now goes via the loop task.
     */
    public static void connectControlTask(TaskGraph taskgraph) throws TaskGraphException {
        if (taskgraph.getControlTask() == null) {
            return;
        }

        taskgraph.setControlTaskState(TaskGraph.CONTROL_TASK_UNSTABLE);

        Task contask = taskgraph.getControlTask();

        while (contask.getDataInputNodeCount() > 0) {
            contask.removeDataInputNode(contask.getDataInputNode(0));
        }

        while (contask.getDataOutputNodeCount() > 0) {
            contask.removeDataOutputNode(contask.getDataOutputNode(0));
        }

        TaskGraph grouptask = taskgraph;
        Node loopnode;
        Node groupnode;
        Node[] innodes = grouptask.getDataInputNodes();
        Node[] inconnodes = new Node[innodes.length];

        for (int nodecount = 0; nodecount < innodes.length; nodecount++) {
            inconnodes[nodecount] = innodes[nodecount].getParentNode();
            groupnode = (Node) innodes[nodecount];
            loopnode = (Node) contask.addDataInputNode();

            grouptask.setGroupNodeParent(groupnode, loopnode);
        }

        Node[] outnodes = grouptask.getDataOutputNodes();
        Node[] outconnodes = new Node[outnodes.length];

        for (int nodecount = 0; nodecount < outnodes.length; nodecount++) {
            outconnodes[nodecount] = outnodes[nodecount].getParentNode();
            groupnode = (Node) grouptask.getDataOutputNode(nodecount);
            loopnode = (Node) contask.addDataOutputNode();

            grouptask.setGroupNodeParent(groupnode, loopnode);
        }

        for (int nodecount = 0; nodecount < innodes.length; nodecount++) {
            taskgraph.connect(contask.addDataOutputNode(), inconnodes[nodecount]);
        }

        for (int nodecount = 0; nodecount < outnodes.length; nodecount++) {
            taskgraph.connect(outconnodes[nodecount], contask.addDataInputNode());
        }

        taskgraph.setControlTaskState(TaskGraph.CONTROL_TASK_CONNECTED);
    }

    /**
     * Removes the connections for the loop task.
     */
    public static void disconnectControlTask(TaskGraph taskgraph) throws TaskGraphException {
        if (!taskgraph.isControlTaskConnected()) {
            return;
        }

        taskgraph.setControlTaskState(TaskGraph.CONTROL_TASK_UNSTABLE);

        Task contask = taskgraph.getControlTask();
        TaskGraph grouptask = taskgraph;
        Node[] groupnodes;
        Node[] connodes;
        Node groupnode;
        Node recnode;
        Node sendnode;
        Cable cable;

        groupnodes = grouptask.getDataInputNodes();
        connodes = contask.getDataOutputNodes();
        int outcount = grouptask.getDataOutputNodeCount();

        for (int count = 0; count < groupnodes.length; count++) {
            cable = connodes[count + outcount].getCable();

            recnode = (Node) cable.getReceivingNode();
            groupnode = (Node) grouptask.getDataInputNode(count);

            taskgraph.disconnect(cable);

            groupnode.setParentNode(recnode);
            recnode.setChildNode(groupnode);
            ((Node) contask.getDataInputNode(count)).setChildNode(null);
        }

        groupnodes = grouptask.getDataOutputNodes();
        connodes = contask.getDataInputNodes();
        int incount = grouptask.getDataInputNodeCount();

        for (int count = 0; count < groupnodes.length; count++) {
            cable = connodes[count + incount].getCable();

            sendnode = (Node) cable.getSendingNode();
            groupnode = (Node) grouptask.getDataOutputNode(count);

            taskgraph.disconnect(cable);

            groupnode.setParentNode(sendnode);
            sendnode.setChildNode(groupnode);
            ((Node) contask.getDataOutputNode(count)).setChildNode(null);
        }

        while (contask.getDataInputNodeCount() > 0) {
            contask.removeDataInputNode(contask.getDataInputNode(0));
        }

        while (contask.getDataOutputNodeCount() > 0) {
            contask.removeDataOutputNode(contask.getDataOutputNode(0));
        }

        taskgraph.setControlTaskState(TaskGraph.CONTROL_TASK_DISCONNECTED);
    }


    /**
     * Given a group input node returns the opposite node on the control task
     */
    private static Node getControlNode(Node groupnode) {
        Task controltask = groupnode.getTopLevelTask();

        if (groupnode.isInputNode()) {
            return controltask
                    .getDataOutputNode(groupnode.getNodeIndex() + groupnode.getTask().getDataOutputNodeCount());
        } else {
            return controltask.getDataInputNode(groupnode.getNodeIndex() + groupnode.getTask().getDataInputNodeCount());
        }
    }

    /**
     * @return the ultimate source node that sends data to/receives data from the given node (ignoring control tasks).
     *         Null if not connected.
     */
    public static Node getSourceNode(Node node) {
        Task task;
        boolean bottom = false;

        while (!bottom) {
            node = node.getBottomLevelNode();

            if (node.isConnected()) {
                if (node.isInputNode()) {
                    node = node.getCable().getSendingNode();
                } else {
                    node = node.getCable().getReceivingNode();
                }

                task = node.getTask();

                if ((task.getParent() != null) && (task.getParent().getControlTask() == task)) {
                    if (node.isInputNode()) {
                        node = task.getDataOutputNode(node.getNodeIndex() - task.getParent().getDataInputNodeCount());
                    } else {
                        node = task.getDataInputNode(node.getNodeIndex() - task.getParent().getDataOutputNodeCount());
                    }
                } else {
                    bottom = true;
                }
            } else {
                return null;
            }
        }

        return NodeUtils.getTopLevelNode(node);
    }


    /**
     * @return a count of all the tasks within a taskgraph and its sub taskgraphs (optionally including control tasks)
     */
    public static int getAllTasksCount(TaskGraph taskGraph, boolean includecontrol) {
        Task[] tasks = taskGraph.getTasks(includecontrol);
        int taskcount = 0;

        for (int count = 0; count < tasks.length; count++) {
            if (tasks[count] instanceof TaskGraph) {
                taskcount += getAllTasksCount((TaskGraph) tasks[count], includecontrol) + 1;
            } else {
                taskcount++;
            }
        }

        return taskcount;
    }

    /**
     * @return an array of all tasks within a taskgraph and its sub taskgraphs, optionally including control tasks
     */
    public static Task[] getAllTasksRecursive(TaskGraph taskGraph, boolean includecontrol) {
        Vector copy = new Vector();

        getAllTasksRecursive(taskGraph, copy, includecontrol);

        return (Task[]) copy.toArray(new Task[copy.size()]);
    }

    /**
     * Recursively copy all the non-group tasks into the vector
     */
    public static void getAllTasksRecursive(TaskGraph taskGraph, Vector copy, boolean includecontrol) {
        Task[] tasks = taskGraph.getTasks(includecontrol);

        for (int i = 0; i < tasks.length; i++) {
            if (tasks[i] instanceof TaskGraph) {
                getAllTasksRecursive((TaskGraph) tasks[i], copy, includecontrol);
            } else {
                copy.add(tasks[i]);
            }
        }
    }


    /**
     * @return all the data types input by the nodes on the specified tool.
     */
    public static String[] getAllDataInputTypes(Tool tool) {
        ArrayList typelist = new ArrayList();
        String[] types = tool.getDataInputTypes(0);
        int count = 0;

        while (types != null) {
            for (int tcount = 0; tcount < types.length; tcount++) {
                if (!typelist.contains(types[tcount])) {
                    typelist.add(types[tcount]);
                }
            }

            types = tool.getDataInputTypes(++count);
        }

        return (String[]) typelist.toArray(new String[typelist.size()]);
    }

    /**
     * @return all the data types input by the nodes on the specified tool.
     */
    public static String[] getAllDataOutputTypes(Tool tool) {
        ArrayList typelist = new ArrayList();
        String[] types = tool.getDataOutputTypes(0);
        int count = 0;

        while (types != null) {
            for (int tcount = 0; tcount < types.length; tcount++) {
                if (!typelist.contains(types[tcount])) {
                    typelist.add(types[tcount]);
                }
            }

            types = tool.getDataOutputTypes(++count);
        }

        return (String[]) typelist.toArray(new String[typelist.size()]);
    }

}
