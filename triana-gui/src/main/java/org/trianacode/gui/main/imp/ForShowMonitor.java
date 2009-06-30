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

package org.trianacode.gui.main.imp;

import org.trianacode.gui.main.NodeComponent;
import org.trianacode.gui.main.ShowToolPanel;
import org.trianacode.gui.main.TaskComponent;
import org.trianacode.taskgraph.Node;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.event.*;
import org.trianacode.taskgraph.tool.Tool;

import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;

/**
 * A class to monitor the for show tool for a node and update the main triana
 * when applicable.
 *
 * @author Ian Wang
<<<<<<< ForShowMonitor.java
 * @version $Revision: 4048 $
=======
 * @version $Revision: 4048 $
>>>>>>> 1.5.2.1
 * @created 21st April 2004
<<<<<<< ForShowMonitor.java
 * @date $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
=======
 * @date $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
>>>>>>> 1.5.2.1
 */

public class ForShowMonitor
        implements NodeListener, TaskGraphListener, ContainerListener {

    private Node node;
    private ShowToolPanel panel;

    private Node level;

    private ForShowTool showtool;
    private DrawCable showcable;

    /**
     * the task the monitor is awaiting the creation of so as to attach a cable
     */
    private Task leveltool;

    /**
     * a flag indicating that the control task is unstable
     */
    private boolean unstable = false;


    public ForShowMonitor(Node node, ShowToolPanel cont) {
        this.node = node;
        this.panel = cont;

        node.addNodeListener(this);
        cont.getContainer().addContainerListener(this);
        cont.getTaskGraph().addTaskGraphListener(this);

        initForShowTool();
    }


    /**
     * Initializes a for show tool
     */
    private void initForShowTool() {
        if (!unstable) {
            boolean updategui = disposeForShowTool();

            if (node.getParentNode() != null) {
                level = getLevelNode(node);

                if (level != null)
                    updategui = initForShowTool(level);
            }

            if (updategui) {
                panel.getContainer().invalidate();
                panel.getContainer().validate();
                panel.getContainer().repaint();
            }
        }
    }

    /**
     * Initializes a for show tool for the specified level node
     */
    private boolean initForShowTool(Node level) {
        boolean updategui = false;

        level.addNodeListener(this);
        leveltool = level.getTask();

        TaskComponent taskcomp = panel.getTaskComponent(leveltool);
        NodeComponent nodecomp;

        if (taskcomp != null) {
            nodecomp = taskcomp.getNodeComponent(level);

            if (nodecomp == null)
                return false;

            showtool = new ForShowTool(level, node);

            if (node.isInputNode())
                showcable = CableFactory.createDrawCable(showtool.getNodeComponent().getComponent(), nodecomp.getComponent(), panel.getContainer());
            else
                showcable = CableFactory.createDrawCable(nodecomp.getComponent(), showtool.getNodeComponent().getComponent(), panel.getContainer());

            panel.addShowTool(showtool, showcable);
            updategui = true;
        }

        return updategui;
    }

    /**
     * Disposes the for show tool for the specified node
     */
    private boolean disposeForShowTool() {
        boolean updategui = false;

        if (level != null)
            level.removeNodeListener(this);

        if (showtool != null) {
            showtool.dispose();
            panel.removeShowTool(showtool);
            updategui = true;
        }

        level = null;
        showcable = null;
        showtool = null;

        return updategui;
    }


    /**
     * @return what would have been the parent node of the specified group node
     *         if there was no control task
     */
    private Node getLevelNode(Node node) {
        Node parent = node.getParentNode();

        if (parent != null) {
            TaskGraph taskgraph = parent.getTask().getParent();

            if ((parent != null) && (taskgraph.getControlTask() == parent.getTask())) {
                try {
                    if (parent.isInputNode())
                        parent = parent.getTask().getDataOutputNode(taskgraph.getDataOutputNodeCount() + parent.getNodeIndex()).getCable().getReceivingNode();
                    else
                        parent = parent.getTask().getDataInputNode(taskgraph.getDataInputNodeCount() + parent.getNodeIndex()).getCable().getSendingNode();
                } catch (IndexOutOfBoundsException except) {
                    return null;
                }
            }
        }

        return parent;
    }


    /**
     * Called when a node is connected to a cable.
     */
    public void nodeConnected(NodeEvent event) {
    }

    /**
     * Called before a node is diconnected from a cable.
     */
    public void nodeDisconnected(NodeEvent event) {
    }

    /**
     * Called when one of a group node's parents changes
     */
    public void nodeParentChanged(NodeEvent event) {
        initForShowTool();
    }

    /**
     * Called when one of a group node's child changes
     */
    public void nodeChildChanged(NodeEvent event) {
        if (event.getNode() == level)
            initForShowTool();
    }

    /**
     * Called when the name of the parameter the node is inputting/outputting is set.
     */
    public void parameterNameSet(NodeEvent event) {
    }


    /**
     * Called when the control task is connected/disconnected or unstable
     */
    public void controlTaskStateChanged(ControlTaskStateEvent event) {
        if (event.getID() == ControlTaskStateEvent.CONTROL_TASK_UNSTABLE) {
            unstable = true;
        } else {
            unstable = false;
            initForShowTool();
        }
    }

    /**
     * Called when a new task is created in a taskgraph.
     */
    public void taskCreated(TaskGraphTaskEvent event) {
    }

    /**
     * Called when a task is removed from a taskgraph. Note that this method
     * is called when tasks are removed from a taskgraph due to being grouped
     * (they are place in the groups taskgraph).
     */
    public void taskRemoved(TaskGraphTaskEvent event) {
    }

    /**
     * Called when a new connection is made between two tasks.
     */
    public void cableConnected(TaskGraphCableEvent event) {
    }

    /**
     * Called before a connection between two tasks is removed.
     */
    public void cableDisconnected(TaskGraphCableEvent event) {
    }

    /**
     * Called when a connection is reconnected to a different task.
     */
    public void cableReconnected(TaskGraphCableEvent event) {
    }


    /**
     * Invoked when a component has been added to the container.
     */
    public void componentAdded(ContainerEvent event) {
        if (event.getChild() instanceof TaskComponent) {
            Tool tool = ((TaskComponent) event.getChild()).getTaskInterface();

            if (tool == leveltool)
                initForShowTool();
        }
    }

    /**
     * Invoked when a component has been removed from the container.
     */
    public void componentRemoved(ContainerEvent event) {
        if (event.getChild() instanceof TaskComponent) {
            Tool tool = ((TaskComponent) event.getChild()).getTaskInterface();

            if (tool == leveltool)
                initForShowTool();
        }
    }


    /**
     * Cleans up the for show monitor when the node is deleted
     */
    public void dispose() {
        disposeForShowTool();
        leveltool = null;

        panel.getTaskGraph().removeTaskGraphListener(this);
        node.removeNodeListener(this);
        panel.getContainer().removeContainerListener(this);

        panel = null;
        node = null;
    }

}
