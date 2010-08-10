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

import org.trianacode.taskgraph.Node;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.TaskGraphChecker;
import org.trianacode.taskgraph.TaskGraphUtils;
import org.trianacode.taskgraph.event.ControlTaskStateEvent;
import org.trianacode.taskgraph.event.ParameterUpdateEvent;
import org.trianacode.taskgraph.event.TaskDisposedEvent;
import org.trianacode.taskgraph.event.TaskGraphCableEvent;
import org.trianacode.taskgraph.event.TaskGraphTaskEvent;
import org.trianacode.taskgraph.event.TaskNodeEvent;
import org.trianacode.taskgraph.event.TaskPropertyEvent;

/**
 * A class responsible for maintaining the consitency of taskgraphs with regards to the input/output nodes on their
 * control tasks. For example, if a group input node is deleted, then the corresponding loop out node on the control
 * task is also deleted.
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 */

public class TaskGraphCheckerImp implements TaskGraphChecker {

    /**
     * Sets this taskgraph checker to monitor the specified taskgraph, and any sub-taskgraphs
     */
    public void monitorTaskGraph(TaskGraph taskgraph) {
        taskgraph.addTaskGraphListener(this);

        if (taskgraph.getControlTask() != null) {
            taskgraph.getControlTask().addTaskListener(this);
        }

        Task[] tasks = taskgraph.getTasks(true);

        for (int count = 0; count < tasks.length; count++) {
            if (tasks[count] instanceof TaskGraph) {
                monitorTaskGraph((TaskGraph) tasks[count]);
            }
        }
    }

    /**
     * Removes this taskgraph checker from monitoring the specified taskgraph, and any sub-taskgraphs
     */
    public void unmonitorTaskGraph(TaskGraph taskgraph) {
        taskgraph.removeTaskGraphListener(this);

        if (taskgraph.getControlTask() != null) {
            taskgraph.getControlTask().removeTaskListener(this);
        }

        Task[] tasks = taskgraph.getTasks(true);

        for (int count = 0; count < tasks.length; count++) {
            if (tasks[count] instanceof TaskGraph) {
                unmonitorTaskGraph((TaskGraph) tasks[count]);
            }
        }
    }


    /**
     * Called when a new task is created in a taskgraph.
     */
    public void taskCreated(TaskGraphTaskEvent event) {
        if (event.getTask().getParent() != null) {
            TaskGraph taskgraph = event.getTask().getParent();

            if (taskgraph.getControlTask() == event.getTask()) {
                event.getTask().addTaskListener(this);
            }
        }

        if (event.getTask() instanceof TaskGraph) {
            monitorTaskGraph((TaskGraph) event.getTask());
        }
    }

    /**
     * Called when a task is removed from a taskgraph. Note that this method is called when tasks are removed from a
     * taskgraph due to being grouped (they are place in the group's taskgraph).
     */
    public void taskRemoved(TaskGraphTaskEvent event) {
        event.getTask().removeTaskListener(this);

        if (event.getTask() instanceof TaskGraph) {
            unmonitorTaskGraph((TaskGraph) event.getTask());
        }
    }

    /**
     * Called when a new connection is made between two tasks.
     */
    public void cableConnected(TaskGraphCableEvent event) {
    }

    /**
     * Called when a connection is reconnected to a different task.
     */
    public void cableReconnected(TaskGraphCableEvent event) {
    }

    /**
     * Called before a connection between two tasks is removed.
     */
    public void cableDisconnected(TaskGraphCableEvent event) {
        if (event.getCable() != null) {
            nodeDisconnected(event.getCable().getSendingNode());
        }

        if (event.getCable() != null) {
            nodeDisconnected(event.getCable().getReceivingNode());
        }
    }

    /**
     * Called when the control task is connected/disconnected or unstable
     */
    public void controlTaskStateChanged(ControlTaskStateEvent event) {
    }


    /**
     * Called before a node is diconnected from a cable.
     */
    public void nodeDisconnected(Node node) {
        if (node.getTask().getParent() != null) {
            TaskGraph taskgraph = node.getTask().getParent();
            Task controltask = node.getTask().getParent().getControlTask();
            Task parent = node.getTask().getParent();

            if (TaskGraphUtils.isControlTask(controltask) && taskgraph.isControlTaskConnected()
                    && (node.isDataNode())) {
                if (node.isInputNode() && (node.getNodeIndex() >= parent.getDataInputNodeCount()) && (
                        node.getNodeIndex() < parent.getDataInputNodeCount() + parent.getDataOutputNodeCount()))
                // remove opposite output node
                {
                    controltask.removeDataOutputNode(
                            controltask.getDataOutputNode(node.getNodeIndex() - parent.getDataInputNodeCount()));
                } else if (node.isOutputNode() && (node.getNodeIndex() >= parent.getDataOutputNodeCount()) && (
                        node.getNodeIndex() < parent.getDataInputNodeCount() + parent.getDataOutputNodeCount()))
                // remove opposite input node
                {
                    controltask.removeDataInputNode(
                            controltask.getDataInputNode(node.getNodeIndex() - parent.getDataOutputNodeCount()));
                }
            }
        }
    }


    /**
     * Called when the core properties of a task change i.e. its name, whether it is running continuously etc.
     */
    public void taskPropertyUpdate(TaskPropertyEvent event) {
    }

    /**
     * Called when the value of a parameter is changed, including when a parameter is removed.
     */
    public void parameterUpdated(ParameterUpdateEvent event) {
    }

    /**
     * Called when a data input node is added.
     */
    public void nodeAdded(TaskNodeEvent event) {
    }

    /**
     * If the node being removed is a group nodes the this removes the corresponding loop node on the control task
     */
    public void nodeRemoved(TaskNodeEvent event) {
        Node node = event.getNode();

        if (event.getTask().getParent() != null) {
            TaskGraph taskgraph = event.getTask().getParent();
            Task parent = taskgraph;
            Task controltask = event.getTask().getParent().getControlTask();

            if (TaskGraphUtils.isControlTask(event.getTask()) && taskgraph.isControlTaskConnected() && node
                    .isDataNode()) {
                int oldstate = taskgraph.getControlTaskState();
                taskgraph.setControlTaskState(TaskGraph.CONTROL_TASK_UNSTABLE);

                Node opnode = null;

                if (node.isInputNode()) {
                    if (node.getNodeIndex() < parent.getDataInputNodeCount()) {
                        opnode = controltask.getDataOutputNode(parent.getDataOutputNodeCount() + node.getNodeIndex());
                    } else if (node.getNodeIndex() < parent.getDataInputNodeCount() + parent.getDataOutputNodeCount()) {
                        opnode = controltask.getDataOutputNode(node.getNodeIndex() - parent.getDataInputNodeCount());
                    }

                    if (opnode != null) {
                        controltask.removeDataOutputNode(opnode);
                    }
                } else {
                    if (node.getNodeIndex() < parent.getDataOutputNodeCount()) {
                        opnode = controltask.getDataInputNode(parent.getDataInputNodeCount() + node.getNodeIndex());
                    } else if (node.getNodeIndex() < parent.getDataInputNodeCount() + parent.getDataOutputNodeCount()) {
                        opnode = controltask.getDataInputNode(node.getNodeIndex() - parent.getDataOutputNodeCount());
                    }

                    if (opnode != null) {
                        controltask.removeDataInputNode(opnode);
                    }
                }

                taskgraph.setControlTaskState(oldstate);
            }
        }
    }

    /**
     * Called before the task is disposed
     */
    public void taskDisposed(TaskDisposedEvent event) {
        event.getTask().removeTaskListener(this);

        if (event.getTask() instanceof TaskGraph) {
            ((TaskGraph) event.getTask()).removeTaskGraphListener(this);
        }
    }

}
