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

import org.trianacode.config.TrianaProperties;
import org.trianacode.taskgraph.*;
import org.trianacode.taskgraph.event.*;
import org.trianacode.taskgraph.service.ExecutionEvent;
import org.trianacode.taskgraph.service.ExecutionListener;
import org.trianacode.taskgraph.service.RunnableInstance;
import org.trianacode.taskgraph.tool.Tool;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * An TaskGraphImp containing a collection of Tasks linked by Cables
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 */
public class TaskGraphImp extends TaskImp
        implements TaskGraph, TaskListener, Serializable, Cloneable {

    /**
     * a hashtable containing all the tasks with this taskgraph keyed by taskid.
     */
    private Map<String, Task> tasks = new ConcurrentHashMap<String, Task>();

    /**
     * an array list of the task's listeners
     */
    private ArrayList listeners = new ArrayList();

    /**
     * the task responsible for looping over the group
     */
    private Task controltask;

    /**
     * a flag indicating whether the control task is connected
     */
    private int controlstate = CONTROL_TASK_DISCONNECTED;

    /**
     * a flag indicating whether group nodes need to be added in the init method
     */
    private boolean initgroupnodes = false;


    /**
     * a list of the execution listeners for this task
     */
    protected ArrayList<ExecutionListener> execlisteners = new ArrayList<ExecutionListener>();

    private AtomicInteger currentState = new AtomicInteger(-1);


    /**
     * Create a taskgraph that uses the default task factory.
     */
    public TaskGraphImp(TrianaProperties properties) throws TaskException {

        this(new ToolImp(properties), new TaskFactoryImp(), false);

        if (getProperties() == null) {
            new Exception().printStackTrace();
        }

    }

    /**
     * Create a taskgraph that takes its details from a specified tool and uses a specific factory. Should only be used
     * by TaskGraphUtils and internally!
     */
    public TaskGraphImp(Tool tool, TaskFactory factory, boolean preserveinst) throws TaskException {
        super(tool, factory, preserveinst);
        setDefinitionType(Tool.DEFINITION_TRIANA_XML);
        addTaskListener(this);
    }

    /**
     * Create a taskgraph containig the specified tasks, and using the specified taskgraph factory. A default mapping
     * between the groups nodes and the tasks nodes is made.
     */
    public TaskGraphImp(Task[] tasks, TaskFactory factory, boolean preserveinst, TrianaProperties properties) throws TaskException {
        this(new ToolImp(properties), factory, preserveinst);

        if (getProperties() == null) {
            new Exception().printStackTrace();
        }
        for (int count = 0; count < tasks.length; count++) {
            this.tasks.put(tasks[count].getToolName() + "_TEMP_KEY" + Math.random(),
                    tasks[count]);

            taskNameUpdate(tasks[count]);

            tasks[count].setParent(this);
            tasks[count].addTaskListener(this);
        }

        initgroupnodes = true;
    }


    /**
     * Adds a execution listener to this runnable instance
     */
    public void addExecutionListener(ExecutionListener listener) {
        if (!execlisteners.contains(listener)) {
            execlisteners.add(listener);
        }
    }

    /**
     * Removes a execution listener from this runnable instance
     */
    public void removeExecutionListener(ExecutionListener listener) {
        execlisteners.remove(listener);
    }

    /**
     * Initialistion method should be called immediately after parent is set
     */
    public void init() throws TaskException {
        try {
            if (initgroupnodes) {
                Cable[] cables = TaskGraphUtils.getExternalCables(getTasks(false));
                ArrayList nodes = new ArrayList();

                for (int count = 0; count < cables.length; count++) {
                    if (containsTask(cables[count].getSendingTask())) {
                        addNodeToList(cables[count].getSendingNode(), nodes);
                    } else {
                        addNodeToList(cables[count].getReceivingNode(), nodes);
                    }
                }

                for (Iterator iter = nodes.iterator(); iter.hasNext();) {
                    Node node = (Node) iter.next();

                    if (node.isInputNode()) {
                        addDataInputNode(node);
                    } else {
                        addDataOutputNode(node);
                    }
                }
            }
        } catch (NodeException except) {
            throw (new TaskException(except.getMessage(), except));
        }
    }

    /**
     * Adds a node to the list, maintaining the nodes in Yposition order
     */
    private static void addNodeToList(Node newnode, ArrayList nodelist) {
        boolean insert = false;
        Node curnode;
        Double[] newpoint = getPositionAsDoubles(newnode.getTask());
        Double[] curpoint;

        for (int count = 0; (count < nodelist.size()) && (!insert); count++) {
            curnode = (Node) nodelist.get(count);
            curpoint = getPositionAsDoubles(curnode.getTask());

            if (newpoint[1] < curpoint[1]) {
                nodelist.add(count, newnode);
                insert = true;
            } else if ((newpoint[1] == curpoint[1]) && (newnode.getAbsoluteNodeIndex() < curnode
                    .getAbsoluteNodeIndex())) {
                nodelist.add(count, newnode);
                insert = true;
            }
        }

        if (!insert) {
            nodelist.add(newnode);
        }
    }

    private static void updateDeprecatedPos(Tool task) {
        if (task.isParameterName(Task.DEPRECATED_GUI_XPOS) && task.isParameterName(Task.DEPRECATED_GUI_YPOS)) {
            int xpos = Integer.parseInt((String) task.getParameter(Task.DEPRECATED_GUI_XPOS));
            int ypos = Integer.parseInt((String) task.getParameter(Task.DEPRECATED_GUI_YPOS));
            setPositionFromDoubles(task, new Double[]{((double) xpos) / 80, ((double) ypos) / 34});
        }
    }

    /**
     * Sets the position of the specified task using Triana's co-ordinate system.
     */
    public static void setPositionFromDoubles(Tool tool, Double[] pos) {
        tool.setParameterType(Task.GUI_X, Tool.GUI);
        tool.setParameterType(Task.GUI_Y, Tool.GUI);
        tool.setParameter(Task.GUI_X, String.valueOf(pos[0]));
    }

    private static Double[] getPositionAsDoubles(Tool task) {
        if (task.isParameterName(Task.GUI_X) && task.isParameterName(Task.GUI_Y)) {
            return new Double[]{Double.parseDouble((String) task.getParameter(Task.GUI_X)),
                    Double.parseDouble((String) task.getParameter(Task.GUI_Y))};
        } else if (task.isParameterName(Task.DEPRECATED_GUI_XPOS) && task.isParameterName(Task.DEPRECATED_GUI_YPOS)) {
            updateDeprecatedPos(task);
            return getPositionAsDoubles(task);
        } else {
            return new Double[]{0.0, 0.0};
        }
    }

    /**
     * @return the data types accepted on the specified node index
     */
    public String[] getDataInputTypes(int index) {
        if (index >= getDataInputNodeCount()) {
            return null;
        }

        Node top = NodeUtils.getTopLevelNode(getDataInputNode(index));
        String[] types = top.getTask().getDataInputTypes(top.getNodeIndex());

        if (types == null) {
            return top.getTask().getDataInputTypes();
        } else {
            return types;
        }
    }

    /**
     * @return the data types accepted on the specified node index
     */
    public String[] getDataOutputTypes(int index) {
        if (index >= getDataOutputNodeCount()) {
            return null;
        }

        Node top = NodeUtils.getTopLevelNode(getDataOutputNode(index));
        String[] types = top.getTask().getDataOutputTypes(top.getNodeIndex());

        if (types == null) {
            return top.getTask().getDataOutputTypes();
        } else {
            return types;
        }
    }


    /**
     * Constructs a new task for looping over the group, optionally preserving the original instance id in the new task.
     * This method does not connect the control task, which should be done using TaskGraphUtils.connectControlTask()
     */
    public Task createControlTask(Tool tool, boolean preserveinst) throws TaskException {
        if (controltask != null) {
            removeControlTask();
        }

        controltask = (Task) createTaskNoNotify(tool, preserveinst);
        new ControlLoopChecker(controltask);

        notifyTaskCreated(controltask, false, false);

        return controltask;
    }

    /**
     * Removes the control task from the group
     */
    public void removeControlTask() {
        if (isControlTaskConnected()) {
            try {
                TaskGraphUtils.disconnectControlTask(this);
            } catch (TaskGraphException except) {
                except.printStackTrace();
            }
        }

        if (controltask != null) {
            removeTaskImp(controltask);
            controltask = null;
        }
    }

    /**
     * @return the task responsible for looping over the group
     */
    public Task getControlTask() {
        return controltask;
    }

    /**
     * @return true if a loop task exists for this group
     */
    public boolean isControlTask() {
        return (controltask != null) && (tasks.containsValue(controltask));
    }

    /**
     * @return true if the control task is connected
     */
    public boolean isControlTaskConnected() {
        return (isControlTask()) && (controlstate == CONTROL_TASK_CONNECTED);
    }

    /**
     * @return true if the control task is unstable
     */
    public boolean isControlTaskUnstable() {
        return (controlstate == CONTROL_TASK_UNSTABLE);
    }

    /**
     * @return the state of the control task (CONTROL_TASK_CONNECTED, CONTROL_TASK_DISCONNECTED or
     *         CONTROL_TASK_UNSTABLE)
     */
    public int getControlTaskState() {
        return controlstate;
    }

    /**
     * Sets the state of the control task (CONTROL_TASK_CONNECTED, CONTROL_TASK_DISCONNECTED or CONTROL_TASK_UNSTABLE)
     */
    public void setControlTaskState(int state) {
        if (state != controlstate) {
            controlstate = state;
            notifyControlTaskStateChanged(state);
        }
    }


    /**
     * Adds a taskgraph listener to this taskgraph.
     */
    public void addTaskGraphListener(final TaskGraphListener listener) {
        TaskGraphEventDispatch.invokeLater(new Runnable() {
            public void run() {
                if (!listeners.contains(listener)) {
                    listeners.add(listener);
                }
            }
        });
    }

    /**
     * Removes a taskgraph listener from this taskgraph.
     */
    public void removeTaskGraphListener(final TaskGraphListener listener) {
        TaskGraphEventDispatch.invokeLater(new Runnable() {
            public void run() {
                listeners.remove(listener);
            }
        });
    }

    /**
     * Create a new task in this taskgraph.
     *
     * @return the id of the new task
     */
    public Task createTask(Tool tool) throws TaskException {
        return createTask(tool, false);
    }

    /**
     * Create a new task in this taskgraph, optionally preserving the instance id from the original task in the new
     * task.
     *
     * @return an interface to the new task
     */
    public Task createTask(Tool tool, boolean preserveinst) throws TaskException {
        Task task = createTaskNoNotify(tool, preserveinst);
        notifyTaskCreated(task, false, false);
        return task;
    }

    /**
     * Creates the specified task without notifying the task listeners
     */
    protected Task createTaskNoNotify(Tool tool, boolean preserveinst) throws TaskException {
        Task task = TaskGraphManager.getTaskGraphFactory(this).createTask(tool, this, preserveinst);
        tasks.put(task.getToolName() + "_TEMP_KEY" + Math.random(), task);

        taskNameUpdate(task);

        task.addTaskListener(this);

        return task;
    }


    /**
     * Remove the specified task from the taskgraph.
     */
    public void removeTask(Task task) {
        if (containsTask(task) && (task != controltask)) {
            removeTaskImp(task);
        }
    }

    private void removeTaskImp(Task task) {
        Node[] nodes = task.getDataInputNodes();
        for (int count = 0; count < nodes.length; count++) {
            task.removeDataInputNode(nodes[count]);
        }

        nodes = task.getDataOutputNodes();
        for (int count = 0; count < nodes.length; count++) {
            task.removeDataOutputNode(nodes[count]);
        }

        nodes = task.getParameterInputNodes();
        for (int count = 0; count < nodes.length; count++) {
            task.removeParameterInputNode((ParameterNode) nodes[count]);
        }

        nodes = task.getParameterOutputNodes();
        for (int count = 0; count < nodes.length; count++) {
            task.removeParameterOutputNode((ParameterNode) nodes[count]);
        }

        notifyTaskRemoved(task, false, false);

        task.removeTaskListener(this);

        ((Task) task).dispose();
        tasks.remove(task.getToolName());
    }


    /**
     * @return an array of all the tasks contained with this taskgraph, optionally including the control task
     */
    public Task[] getTasks(boolean includecontrol) {
        Task[] copy;

        if ((!isControlTask()) || includecontrol) {
            copy = new Task[tasks.size()];
        } else {
            copy = new Task[tasks.size() - 1];
        }

        Set<String> enumeration = tasks.keySet();
        Iterator it = enumeration.iterator();
        Task task;
        int count = 0;

        while (it.hasNext()) {
            task = tasks.get(it.next());

            if (includecontrol || (task != controltask)) {
                if (task == controltask) {
                    copy[copy.length - 1] = task;
                } else {
                    copy[count++] = task;
                }
            }
        }

        return copy;
    }

    /**
     * @return the task with the specified id
     */
    public Task getTask(String taskname) {
        if (tasks.containsKey(taskname)) {
            return (Task) tasks.get(taskname);
        } else {
            return null;
        }
    }

    /**
     * @return the task with the specified instance id
     */
    public Task getTaskInstance(String instanceid) {
        Task[] tasks = getTasks(true);

        for (int count = 0; count < tasks.length; count++) {
            if (tasks[count].getInstanceID().equals(instanceid)) {
                return tasks[count];
            }
        }

        return null;
    }

    /**
     * @return the task within this taskgraph linked to the specified node
     */
    public Task getTask(Node node) {
        if (containsTask(node.getTask())) {
            return node.getTask();
        } else {
            return null;
        }
    }


    /**
     * @return true if the taskgraph contains the specified task
     */
    public boolean containsTask(String identifier) {
        return tasks.containsKey(identifier);
    }

    /**
     * @return true if the taskgraph contains the specified task instance
     */
    public boolean containsTaskInstance(String instanceid) {
        Task[] tasks = getTasks(false);

        for (int count = 0; count < tasks.length; count++) {
            if (tasks[count].getInstanceID().equals(instanceid)) {
                return true;
            }
        }

        return false;
    }

    /**
     * @return true if the taskgraph contains the specified task
     */
    public boolean containsTask(Task task) {
        return tasks.values().contains(task) || (controltask == task);
    }

    /**
     * @return true if a task represented within this taskgraph is linked to the specified node
     */
    public boolean containsNode(Node node) {
        return (containsTask(node.getTask()));
    }


    /**
     * Groups the specified tasks, returning the taskgraph created. A default mapping between group nodes and task nodes
     * is created. This can be changed later through setDataInputNode etc. in TaskGraph.
     */
    public TaskGraph groupTasks(String[] tasknames, String groupname) throws TaskException {
        Task[] tasks = new Task[tasknames.length];

        for (int count = 0; count < tasks.length; count++) {
            tasks[count] = getTask(tasknames[count]);
        }

        TRectangle bounds = TaskLayoutUtils.getBoundingBox(tasks);

        Cable[] cables = TaskGraphUtils.getInternalCables(tasks);

        for (int count = 0; count < cables.length; count++) {
            notifyCableDisconnected(cables[count], true, false);
        }

        for (int count = 0; count < tasks.length; count++) {
            tasks[count].removeTaskListener(this);
            tasks[count].setParent(null);

            this.tasks.remove(tasks[count].getToolName());
            notifyTaskRemoved(tasks[count], true, false);
        }

        TaskGraph group = TaskGraphManager.getTaskGraphFactory(this).createGroupTask(tasks, this, true);
        group.setToolName(groupname);

        TaskLayoutUtils.setPosition(group, new TPoint(bounds.getX() + (bounds.getWidth() / 2) - 0.5,
                bounds.getY() + (bounds.getHeight() / 2) - 0.5));

        this.tasks.put(group.getToolName() + "_TEMP_KEY" + Math.random(),
                group);

        taskNameUpdate(group);

        group.setParent(this);
        group.addTaskListener(this);

        notifyTaskCreated(group, true, false);

        groupNodes(group.getDataInputNodes(), true);
        groupNodes(group.getDataOutputNodes(), false);

        return group;
    }

    /**
     * Reconnect group nodes to nodes in the parent taskgraph when ungrouping tasks
     */
    private void groupNodes(Node[] nodes, boolean inputrecon) {
        for (int count = 0; count < nodes.length; count++) {
            if (nodes[count].isConnected() && nodes[count].isBottomLevelNode()) {
                notifyCableReconnected(nodes[count].getCable(), inputrecon);
            }
        }
    }

    /**
     * Ungroups the specified group task.
     */
    public void unGroupTask(String groupname) throws TaskGraphException {
        if (containsTask(groupname) && (getTask(groupname) instanceof TaskGraph)) {
            TaskGraph group = (TaskGraph) getTask(groupname);
            Task[] grouptasks = group.getTasks(false);

            TRectangle bounds = TaskLayoutUtils.getBoundingBox(grouptasks);
            TPoint grouppos = TaskLayoutUtils.getPosition(group);
            TaskLayoutUtils.translateTo(grouptasks, new TPoint(Math.max(0, grouppos.getX() - (bounds.getWidth() / 2) + 0.5),
                    Math.max(0, grouppos.getY() - (bounds.getHeight() / 2) + 0.5)));

            TaskGraphUtils.disconnectControlTask(group);

            group.ungroup();
            group.removeTaskListener(this);
            group.setParent(null);

            tasks.remove(group.getToolName());
            notifyTaskRemoved(group, false, true);

            for (int tcount = 0; tcount < grouptasks.length; tcount++) {
                tasks.put(grouptasks[tcount].getToolName() + "_TEMP_KEY" + Math.random(),
                        grouptasks[tcount]);
                taskNameUpdate(grouptasks[tcount]);

                grouptasks[tcount].setParent(this);
                grouptasks[tcount].addTaskListener(this);

                notifyTaskCreated(grouptasks[tcount], false, true);

                unGroupNodes(grouptasks[tcount].getInputNodes());
                unGroupNodes(grouptasks[tcount].getOutputNodes());
            }

            Cable[] cables = TaskGraphUtils.getInternalCables(grouptasks);

            for (int count = 0; count < cables.length; count++) {
                notifyCableConnected(cables[count], false, true);
            }

            group.dispose();
        }
    }

    /**
     * Reconnect group nodes to nodes in the parent taskgraph when ungrouping tasks
     */
    private void unGroupNodes(Node[] nodes) throws CableException {
        for (int ncount = 0; ncount < nodes.length; ncount++) {
            if (!nodes[ncount].isBottomLevelNode()) {
                if (nodes[ncount].getChildNode().getChildNode() != null) {
                    nodes[ncount].setChildNode(nodes[ncount].getChildNode().getChildNode());
                } else if (nodes[ncount].isConnected()) {
                    nodes[ncount].getCable().reconnect(nodes[ncount]);
                    notifyCableReconnected(nodes[ncount].getCable(), nodes[ncount].isInputNode());
                } else {
                    nodes[ncount].setChildNode(null);
                }
            }
        }
    }


    /**
     * removes tasks and notifies listeners when the taskgraph is ungrouped, but does not dispose of the tasks
     */
    public void ungroup() {
        Task[] taskarray = (Task[]) tasks.values().toArray(new Task[tasks.size()]);
        Cable[] cables = TaskGraphUtils.getInternalCables(taskarray);

        for (int count = 0; count < cables.length; count++) {
            notifyCableDisconnected(cables[count], false, true);
        }

        for (int count = 0; count < taskarray.length; count++) {
            notifyTaskRemoved(taskarray[count], false, true);

            taskarray[count].removeTaskListener(this);
            tasks.remove(taskarray[count].getToolName());
        }
    }


    /**
     * Adds a data input node, with the specified node as a parent
     */
    public Node addDataInputNode(Node parent) throws NodeException {
        Node node = addDataInputNode();

        setGroupNodeParent(node, parent);

        return node;
    }

    /**
     * Adds a data output node, with the specified node as a parent
     */
    public Node addDataOutputNode(Node parent) throws NodeException {
        Node node = addDataOutputNode();

        setGroupNodeParent(node, parent);

        return node;
    }

    /**
     * Sets the parent of a group input/output node
     */
    public void setGroupNodeParent(Node groupnode, Node parentnode) {
        if (!containsNode(parentnode)) {
            throw (new RuntimeException("Cannot set group node parent to node not in the taskgraph"));
        }

        if (groupnode.getTask() != this) {
            throw (new RuntimeException("Cannot set parent for another grouptask's node"));
        }

        if (groupnode.isInputNode() != parentnode.isInputNode()) {
            throw (new RuntimeException("Non-matched parent node and group node (input/output) "));
        }

        Node group = groupnode;
        Node parent = parentnode;

        if (group.getParentNode() != null) {
            group.getParentNode().setChildNode(null);
        }

        if (parent.getChildNode() != null) {
            parent.getChildNode().setParentNode(null);
        }

        group.setParentNode(parent);

        try {
            if (parent.isConnected()) {
                if (groupnode.isConnected()) {
                    disconnect(parent.getCable());
                } else {
                    Cable cable = parent.getCable();
                    cable.reconnect(group);
                    notifyCableReconnected(cable, group.isInputNode());
                }
            }

            parent.setChildNode(group);
        } catch (CableException except) {
            throw (new RuntimeException("Error setting group node parent: " + except.getMessage()));
        }
    }

    /**
     * @return the parent of a group input/output node
     */
    public Node getGroupNodeParent(Node groupnode) {
        return groupnode.getParentNode();
    }

    /**
     * Swaps the parents of two group nodes (without disconnecting either). Useful for reordering input/output nodes.
     */
    public void swapGroupNodeParents(Node groupnode1, Node groupnode2) {
        if ((groupnode1.getTask() != this) || (groupnode2.getTask() != this)) {
            throw (new RuntimeException("Cannot set parent for another grouptask's node"));
        }

        if (groupnode1.isInputNode() != groupnode2.isInputNode()) {
            throw (new RuntimeException("Non-matched parent node and group node (input/output) "));
        }

        Node node1 = groupnode1;
        Node node2 = groupnode2;

        Node parent1 = node1.getParentNode();
        Node parent2 = node2.getParentNode();

        node1.setParentNode(parent2);
        node2.setParentNode(parent1);

        parent2.setChildNode(node1);
        parent1.setChildNode(node2);
    }

    /**
     * Removes a data input node.
     */
    public void removeDataInputNode(Node node) {
        if (node.getTask() == this) {
            Node group = node;

            if (group.getParentNode() != null) {
                group.getParentNode().setChildNode(null);
            }

            if (group.getChildNode() != null) {
                group.getChildNode().setParentNode(null);
            }
        }

        super.removeDataInputNode(node);
    }

    /**
     * Removes a data output node.
     */
    public void removeDataOutputNode(Node node) {
        if (node.getTask() == this) {
            Node group = node;

            if (group.getParentNode() != null) {
                group.getParentNode().setChildNode(null);
            }

            if (group.getChildNode() != null) {
                group.getChildNode().setParentNode(null);
            }
        }

        super.removeDataOutputNode(node);
    }


    /**
     * Create a cable connecting sendnode on sendtask to recnode on rectask.
     */
    public Cable connect(Node sendnode, Node recnode) throws CableException {
        if ((!containsNode(sendnode)) || (!containsNode(recnode))) {
            throw (new RuntimeException("Cannot connect to a node outside taskgraph"));
        }

        disconnect(sendnode.getCable());
        disconnect(recnode.getCable());

        Cable cable = TaskGraphManager.getTaskGraphFactory(this).createCable((Node) sendnode, (Node) recnode);
        notifyCableConnected(cable, false, false);

        return cable;
    }

    /**
     * Disconnects the specified cable from its nodes.
     */
    public void disconnect(Cable cable) throws CableException {
        if (cable != null && cable.isConnected()) {
            if (!(containsNode(cable.getSendingNode()) && containsNode(cable.getReceivingNode()))) {
                throw (new CableException("Cannot disconnect a cable not contained in the taskgraph"));
            }

            notifyCableDisconnected(cable, false, false);
            cable.disconnect();
        }
    }


    /**
     * This method is called when a tasks within this taskgraphs name is changed. It enforces the unique task name
     * policy
     */
    public void taskPropertyUpdate(TaskPropertyEvent event) {
        if (event.getUpdatedProperty() == TaskPropertyEvent.TASK_NAME_UPDATE) {
            taskNameUpdate(event.getTask());
        }
    }

    /**
     * This method is called when a tasks within this taskgraphs name is changed. It enforces the unique task name
     * policy
     */
    public void taskNameUpdate(Task task) {
        if (containsTask(task)) {
            Set<String> enumeration = tasks.keySet();
            boolean found = false;
            Object key;
            Iterator it = enumeration.iterator();
            while ((!found) && it.hasNext()) {
                key = it.next();

                if (tasks.get(key) == task) {
                    tasks.remove(key);
                    found = true;
                }
            }

            // Task with same name exists, so find a name unique within the taskgraph.
            if (tasks.containsKey(task.getToolName())) {
                String base = getTaskNameBase(task.getToolName());
                int idcount = getTaskNameNumber(task.getToolName());

                while (tasks.containsKey(base + idcount)) {
                    idcount++;
                }

                task.setToolName(base + idcount);
                tasks.put(task.getToolName(), task);
            } else {
                tasks.put(task.getToolName(), task);
            }
        }
    }

    /**
     * @return the non-numerical base of a task name
     */
    private String getTaskNameBase(String taskname) {
        int count = taskname.length() - 1;

        while (Character.isDigit(taskname.charAt(count)) && (count >= 0)) {
            count--;
        }

        return taskname.substring(0, count + 1);
    }

    /**
     * @return the non-numerical base of a task name
     */
    private int getTaskNameNumber(String taskname) {
        int count = taskname.length() - 1;

        while (Character.isDigit(taskname.charAt(count)) && (count >= 0)) {
            count--;
        }

        if (count == taskname.length() - 1) {
            return 1;
        } else {
            return Integer.parseInt(taskname.substring(count + 1, taskname.length()));
        }
    }

    /**
     * Called when a data input node is added.
     */
    public void nodeAdded(TaskNodeEvent event) {
    }

    /**
     * If the node being removed is one of this group's nodes the this removes the group node also.
     */
    public void nodeRemoved(TaskNodeEvent event) {
    }

    /**
     * Called before the task is disposed
     */
    public void taskDisposed(TaskDisposedEvent event) {
    }


    /**
     * Called when the value of a parameter is changed, including when a parameter is removed.
     */
    public void parameterUpdated(ParameterUpdateEvent event) {
    }


    /**
     * Notifies all the taskgraph listeners that a task has been created.
     */
    protected void notifyTaskCreated(final Task task, final boolean groupevent, final boolean ungroupevent) {
        final TaskGraph taskgraph = this;
        final TaskGraphTaskEvent event = new TaskGraphTaskEvent(TaskGraphTaskEvent.TASK_CREATED, taskgraph, task,
                getTaskGroupEventId(groupevent, ungroupevent));

        TaskGraphEventDispatch.invokeLater(new Runnable() {
            public void run() {
                TaskGraphListener[] copy = (TaskGraphListener[]) listeners
                        .toArray(new TaskGraphListener[listeners.size()]);

                for (int count = 0; count < copy.length; count++) {
                    copy[count].taskCreated(event);
                }
            }
        });
    }

    /**
     * Notifies all the taskgraph listeners that a task has been removed.
     */
    protected void notifyTaskRemoved(final Task task, final boolean groupevent, final boolean ungroupevent) {
        final TaskGraph taskgraph = this;
        final TaskGraphTaskEvent event = new TaskGraphTaskEvent(TaskGraphTaskEvent.TASK_REMOVED, taskgraph, task,
                getTaskGroupEventId(groupevent, ungroupevent));


        TaskGraphEventDispatch.invokeLater(new Runnable() {
            public void run() {
                TaskGraphListener[] copy = (TaskGraphListener[]) listeners
                        .toArray(new TaskGraphListener[listeners.size()]);

                for (int count = 0; count < copy.length; count++) {
                    copy[count].taskRemoved(event);
                }
            }
        });
    }


    /**
     * Notifies all the taskgraph listeners that a cable has been connected.
     */
    protected void notifyCableConnected(final Cable cable, final boolean groupevent, final boolean ungroupevent) {
        final TaskGraph taskgraph = this;
        final TaskGraphCableEvent event = new TaskGraphCableEvent(TaskGraphCableEvent.CABLE_CONNECTED, taskgraph, cable,
                getCableGroupEventId(groupevent, ungroupevent));

        TaskGraphEventDispatch.invokeLater(new Runnable() {
            public void run() {
                TaskGraphListener[] copy = (TaskGraphListener[]) listeners
                        .toArray(new TaskGraphListener[listeners.size()]);

                for (int count = 0; count < copy.length; count++) {
                    copy[count].cableConnected(event);
                }
            }
        });
    }

    /**
     * Notifies all the taskgraph listeners that a task has been removed.
     */
    protected void notifyCableDisconnected(final Cable cable, final boolean groupevent, final boolean ungroupevent) {
        final TaskGraph taskgraph = this;
        final TaskGraphCableEvent event = new TaskGraphCableEvent(TaskGraphCableEvent.CABLE_DISCONNECTED, taskgraph,
                cable, getCableGroupEventId(groupevent, ungroupevent));

        TaskGraphEventDispatch.invokeLater(new Runnable() {
            public void run() {
                TaskGraphListener[] copy = (TaskGraphListener[]) listeners
                        .toArray(new TaskGraphListener[listeners.size()]);

                for (int count = 0; count < copy.length; count++) {
                    copy[count].cableDisconnected(event);
                }
            }
        });
    }

    /**
     * Notifies all the taskgraph listeners that a task has been removed.
     */
    protected void notifyCableReconnected(final Cable cable, final boolean inputrecon) {
        int id;

        if (inputrecon) {
            id = TaskGraphCableEvent.CABLE_RECONNECTED_INPUT;
        } else {
            id = TaskGraphCableEvent.CABLE_RECONNECTED_OUTPUT;
        }

        final TaskGraph taskgraph = this;
        final TaskGraphCableEvent event = new TaskGraphCableEvent(id, taskgraph, cable,
                TaskGraphCableEvent.NON_GROUP_EVENT);

        TaskGraphEventDispatch.invokeLater(new Runnable() {
            public void run() {
                TaskGraphListener[] copy = (TaskGraphListener[]) listeners
                        .toArray(new TaskGraphListener[listeners.size()]);

                for (int count = 0; count < copy.length; count++) {
                    copy[count].cableReconnected(event);
                }
            }
        });
    }


    /**
     * Notifies all the taskgraph listeners that the state of the control task has changed
     */
    private void notifyControlTaskStateChanged(int state) {
        int eventstate;

        if (state == CONTROL_TASK_CONNECTED) {
            eventstate = ControlTaskStateEvent.CONTROL_TASK_CONNECTED;
        } else if (state == CONTROL_TASK_DISCONNECTED) {
            eventstate = ControlTaskStateEvent.CONTROL_TASK_DISCONNECTED;
        } else {
            eventstate = ControlTaskStateEvent.CONTROL_TASK_UNSTABLE;
        }

        final TaskGraph taskgraph = this;
        final ControlTaskStateEvent event = new ControlTaskStateEvent(eventstate, taskgraph, state);

        TaskGraphEventDispatch.invokeLater(new Runnable() {
            public void run() {
                TaskGraphListener[] copy = (TaskGraphListener[]) listeners
                        .toArray(new TaskGraphListener[listeners.size()]);

                for (int count = 0; count < copy.length; count++) {
                    copy[count].controlTaskStateChanged(event);
                }
            }
        });
    }


    /**
     * @return the task graph event id for grouping/ungrouping
     */
    private int getTaskGroupEventId(boolean groupevent, boolean ungroupevent) {
        if (groupevent) {
            return TaskGraphTaskEvent.GROUP_EVENT;
        } else if (ungroupevent) {
            return TaskGraphTaskEvent.UNGROUP_EVENT;
        } else {
            return TaskGraphTaskEvent.NON_GROUP_EVENT;
        }
    }

    /**
     * @return the task graph event id for grouping/ungrouping
     */
    private int getCableGroupEventId(boolean groupevent, boolean ungroupevent) {
        if (groupevent) {
            return TaskGraphCableEvent.GROUP_EVENT;
        } else if (ungroupevent) {
            return TaskGraphCableEvent.UNGROUP_EVENT;
        } else {
            return TaskGraphCableEvent.NON_GROUP_EVENT;
        }
    }


    /**
     * cleans up any operations associated with this taskgraph and the tasks within it
     */
    public void dispose() {
        removeTaskListener(this);

        Set<String> enumeration = tasks.keySet();
        Iterator it = enumeration.iterator();
        while (it.hasNext()) {
            removeTask(tasks.get(it.next()));
        }
        execlisteners.clear();
        super.dispose();
    }

    @Override
    public void executionStateChanged(ExecutionEvent event) {
        for (ExecutionListener execlistener : execlisteners) {
            execlistener.executionStateChanged(event);
        }
    }

    @Override
    public void executionRequested(ExecutionEvent event) {
        if (currentState.get() < 0) {
            currentState.set(0);
            ExecutionEvent evt = new ExecutionEvent(event.getState(), this);
            for (ExecutionListener execlistener : execlisteners) {
                execlistener.executionRequested(evt);
            }
        }
    }

    @Override
    public void executionStarting(ExecutionEvent event) {
        if (currentState.get() < 1) {
            currentState.set(1);
            ExecutionEvent evt = new ExecutionEvent(event.getState(), this);
            for (ExecutionListener execlistener : execlisteners) {
                execlistener.executionStarting(evt);
            }
        }
    }

    @Override
    public void executionFinished(ExecutionEvent event) {
        if (isFinished(this)) {
            ExecutionEvent evt = new ExecutionEvent(event.getState(), this);
            for (ExecutionListener execlistener : execlisteners) {
                execlistener.executionFinished(evt);
            }
        }

    }

    @Override
    public void executionReset(ExecutionEvent event) {
    }

    private boolean isFinished(TaskGraph taskgraph) {
        Task[] tasks = taskgraph.getTasks(true);
        boolean finished = true;

        for (int count = 0; (count < tasks.length) && (finished); count++) {
            if (tasks[count] instanceof RunnableInstance) {
                finished = finished && ((tasks[count]).getExecutionState()
                        == ExecutionState.COMPLETE);
            }

            if (tasks[count] instanceof TaskGraph) {
                finished = finished && isFinished((TaskGraph) tasks[count]);
            }
        }

        return finished;
    }
}

