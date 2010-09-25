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

package org.trianacode.enactment;

import org.trianacode.TrianaInstance;
import org.trianacode.taskgraph.CableException;
import org.trianacode.taskgraph.ExecutionState;
import org.trianacode.taskgraph.Node;
import org.trianacode.taskgraph.NodeException;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskException;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.TaskGraphException;
import org.trianacode.taskgraph.TaskGraphManager;
import org.trianacode.taskgraph.TaskGraphUtils;
import org.trianacode.taskgraph.TaskLayoutUtils;
import org.trianacode.taskgraph.imp.CableImp;
import org.trianacode.taskgraph.imp.TaskFactoryImp;
import org.trianacode.taskgraph.imp.TaskImp;
import org.trianacode.taskgraph.imp.ToolImp;
import org.trianacode.taskgraph.interceptor.InterceptorChain;
import org.trianacode.taskgraph.service.*;
import org.trianacode.taskgraph.tool.Tool;
import org.trianacode.taskgraph.tool.ToolTable;


/**
 * A class for running a taskgraph. Unlike TrianaExec, TrianaRun does not handle synchronuizing between multiple
 * invocations.
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 */

public class TrianaRun {

    protected Task task;
    protected TaskGraph taskgraph;

    protected ExecServer server;
    protected Scheduler scheduler;

    protected ExecCable[] incables;
    protected ExecCable[] outcables;

    private Task dummy;


    /**
     * Constructs a TrianaExec to execute a clone of the specified taskgraph. Uses a default tool table
     */
    public TrianaRun(TaskGraph taskgraph) throws TaskGraphException {
        initToolTable();
        init(taskgraph);
    }

    /**
     * Constructs a TrianaExec to execute a clone of the specified tool. Uses a default tool table.
     */
    public TrianaRun(Tool tool) throws TaskGraphException {
        initToolTable();
        init(tool);
    }


    /**
     * Create and initialise a new tool table
     *
     * Ian T - this is as diodgy as hell.... what is this class for?
     */
    public static void initToolTable() {
//        TrianaInstance engine;
//        try {
//            engine = new TrianaInstance(null, null);
//        } catch (Exception e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }

    }

    public SchedulerInterface getScheduler() {
        return scheduler;
    }

    /**
     * Initializes the triana exec
     */
    protected void init(Tool tool) throws TaskGraphException {
        initDummyTask();

        if (tool instanceof TaskGraph) {
            TaskGraph group = (TaskGraph) tool;
            this.task = TaskGraphUtils.cloneTaskGraph(group, TaskGraphManager.DEFAULT_FACTORY_TYPE);
            this.taskgraph = (TaskGraph) task;
        } else {
            this.taskgraph = TaskGraphManager.createTaskGraph(TaskGraphManager.DEFAULT_FACTORY_TYPE);
            this.task = taskgraph.createTask(tool);
            initGroupNodes();

            TaskLayoutUtils.translateToOrigin(taskgraph.getTasks(false));
            taskgraph.setToolName(tool.getToolName());
            taskgraph.setToolPackage(tool.getToolPackage());
        }

        this.scheduler = new Scheduler(taskgraph);
        TaskGraphManager.setTrianaServer(taskgraph, new ExecServer(TaskGraphManager.getToolTable()));

        initCables();
    }

    /**
     * Initialises a dummy task with the specified tool name. This task acts as a place holder at the unconnected end of
     * the exec cables.
     */
    private void initDummyTask() throws TaskException, NodeException {
        dummy = new TaskImp(new ToolImp(), new TaskFactoryImp(), false);

        dummy.setToolName("Dummy");
        dummy.addDataInputNode();
        dummy.addDataOutputNode();
    }

    /**
     * If an individual task was created then set its nodes as the taskgraphs parent nodes.
     */
    private void initGroupNodes() throws NodeException {
        Node[] innodes = task.getInputNodes();
        Node[] outnodes = task.getOutputNodes();

        for (int count = 0; count < innodes.length; count++) {
            taskgraph.addDataInputNode(innodes[count]);
        }

        for (int count = 0; count < outnodes.length; count++) {
            taskgraph.addDataOutputNode(outnodes[count]);
        }
    }

    /**
     * Initializes the cables connected to the taskgraph
     */
    private void initCables() throws CableException {
        Node[] innodes = taskgraph.getInputNodes();
        Node[] outnodes = taskgraph.getOutputNodes();

        incables = new ExecCable[innodes.length];
        outcables = new ExecCable[outnodes.length];

        for (int count = 0; count < innodes.length; count++) {
            incables[count] = new ExecCable();
            incables[count].connectInput(innodes[count]);
        }

        for (int count = 0; count < outnodes.length; count++) {
            outcables[count] = new ExecCable();
            outcables[count].connectOutput(outnodes[count]);
        }
    }

    /**
     * @return the taskgraph that is being executed. If a single task was executed then this returns that task wrapped
     *         in a simple taskgraph.
     */
    public TaskGraph getTaskGraph() {
        return taskgraph;
    }

    /**
     * @return the task that is being executed.
     */
    public Task getTask() {
        return task;
    }

    /**
     * @return the dummy tool name
     */
    public String getDummyToolName() {
        return dummy.getToolName();
    }

    /**
     * Sets the dummy tool name
     */
    public void setDummyToolName(String dummyname) {
        dummy.setToolName(dummyname);
    }


    /**
     * Runs the unconnected tasks with the taskgraph
     */
    public void runTaskGraph() throws SchedulerException {
        scheduler.runTaskGraph();
    }

    /**
     * @return true if the taskgraph has finished executing
     */
    public boolean isFinished() {
        return isFinished(taskgraph);
    }

    /**
     * @return true if the taskgraph has finished executing
     */
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


    /**
     * @return the number of input nodes
     */
    public int getInputNodeCount() {
        return incables.length;
    }

    /**
     * @return true if the input data on the specified node is successfully sent
     */
    public boolean isDataSent(int count) {
        return incables[count].isDataSent();
    }

    /**
     * Sends output data using the specified input node
     */
    public void sendInputData(int count, Object obj) {
        incables[count].send(obj);
    }


    /**
     * @return the number of output nodes
     */
    public int getOutputNodeCount() {
        return outcables.length;
    }

    /**
     * @return true if data is ready at the specified output node
     */
    public boolean isOutputReady(int count) {
        return outcables[count].isDataReady();
    }

    /**
     * @return the data at the specified output node, automatically unpacking it from any data message.
     */
    public Object receiveOutputData(int count) {
        return receiveOutputData(count, true);
    }

    /**
     * @return the data at the specified output node, optionally unpacking it from any data message.
     */
    public Object receiveOutputData(int count, boolean unpack) {
        Object data = outcables[count].recv();

        if (unpack) {
            return unpackResult(data);
        } else {
            return data;
        }
    }


    /**
     * @return true if data is ready at every output node
     */
    public boolean isOutputReady() {
        boolean ready = true;

        for (int count = 0; (count < outcables.length) && ready; count++) {
            ready = ready && outcables[count].isDataReady();
        }

        return ready;
    }


    protected Object unpackResult(Object result) {
        Object unpacked;

        if (result instanceof DataMessage) {
            unpacked = ((DataMessage) result).getData();
        } else {
            unpacked = result;
        }

        return unpacked;
    }


    /**
     * Clean up after a run execution.
     */
    public void dispose() {
        scheduler.resetTaskGraph();
        taskgraph.dispose();
    }


    private class ExecServer implements TrianaServer {

        private ToolTable tools;

        public ExecServer(ToolTable tools) {
            this.tools = tools;
        }

        public ToolTable getToolTable() {
            return tools;
        }


        public void notifyError(RunnableInstance runnable, String message) {
            System.err.println("Error: " + message);
        }

        /**
         * Called by a control task to run the specified task within a running taskgraph
         */
        public void runTask(Task task) throws SchedulerException {
            scheduler.runTask(task);
        }

    }

    private class ExecCable extends CableImp implements IOCable {

        private Node node;
        private Object data;

        private boolean output = false;
        private boolean suspended = false;

        public void connectOutput(Node sendnode) throws CableException {
            this.node = sendnode;
            this.output = true;
            sendnode.connect(this);
        }

        public void connectInput(Node recnode) throws CableException {
            this.node = recnode;
            this.output = false;
            recnode.connect(this);
        }


        /**
         * @return the node which receives data along this cable
         */
        public Node getReceivingNode() {
            if (!output) {
                return node;
            } else {
                return dummy.getInputNode(0);
            }
        }

        /**
         * @return the node which receives data along this cable
         */
        public Task getReceivingTask() {
            if (!output) {
                return node.getTask();
            } else {
                return dummy;
            }
        }

        /**
         * @return the node which sends data along this cable
         */
        public Node getSendingNode() {
            if (output) {
                return node;
            } else {
                return dummy.getOutputNode(0);
            }
        }

        /**
         * @return the task which sends data (via a node) along this cable
         */
        public Task getSendingTask() {
            if (output) {
                return node.getTask();
            } else {
                return dummy;
            }
        }


        public String getType() {
            return "Exec";
        }

        /**
         * @return true if this cable is connected.
         */
        public boolean isConnected() {
            return (node != null);
        }

        public boolean isDataReady() {
            return (data != null);
        }


        public Object recv() {
            Object tempdata = data;
            data = null;
            return InterceptorChain.interceptSend(getSendingNode(), getReceivingNode(), tempdata);
        }


        public synchronized void suspend() {
            suspended = true;
        }

        /**
         * Unsuspends a pipe
         */
        public void resume() {
            suspended = false;
        }

        /**
         * Flushes all data out of communication pipes.
         */
        public void flush() {
            data = null;
        }


        public synchronized void send(Object data) {
            if (suspended) {
                return;
            }

            this.data = InterceptorChain.interceptSend(getSendingNode(), getReceivingNode(), data);

            if (!output) {
                ((RunnableInstance) node.getTopLevelTask()).wakeUp(node.getTopLevelNode());
            }
        }

        public synchronized void sendNonBlocking(Object data) {
            if (suspended) {
                return;
            }

            send(data);
        }

        public boolean isDataSent() {
            return (data == null);
        }

    }
}
