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

import org.trianacode.gui.hci.color.ColorManager;
import org.trianacode.gui.main.ForShowComponent;
import org.trianacode.gui.main.NodeComponent;
import org.trianacode.taskgraph.*;
import org.trianacode.taskgraph.event.*;
import org.trianacode.taskgraph.imp.TaskFactoryImp;
import org.trianacode.taskgraph.imp.TaskImp;
import org.trianacode.taskgraph.imp.ToolImp;

import java.awt.*;

/**
 * ForShowTool is a class for showing what tools within group
 * are connected to in upper groups.  They provide an icon
 * which is smaller that normal units and just indicates
 * the connection
 * <p/>
 *
 * @author Ian Taylor
 * @version $Revision: 4048 $
 * @created 24 March 1997
 * @date $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public class ForShowTool extends TrianaTool implements NodeListener, TaskListener, ForShowComponent {

    /**
     * The internal node whose external connection is being shown
     */
    private Node node;

    /**
     * The group node whose external connection is being shown
     */
    private Node groupnode;

    /**
     * the bottom level node for the group that contains the internal node
     */
    private Node bottomintnode;

    /**
     * the bottom level node for the group that is connected to
     */
    private Node bottomconnnode;

    /**
     * The top level task that is connected to
     */
    private Task showtask;

    /**
     * The component that represents this for show tools node
     */
    private NodeComponent nodecomp;

    /**
     * a flag indicating whether the node is connected
     */
    private boolean connectflag = false;


    /**
     * @param node      internal node whose external connection is shown by this for show tool
     * @param groupnode the external (group) node whose connection is shown by this for show tool
     */
    public ForShowTool(Node node, Node groupnode) {
        super(node.getTask(), new TrianaToolLayout(2));

        this.node = node;
        this.groupnode = groupnode;

        Component comp = new TextIcon(node.getTask().getToolName());
        comp.setFont(comp.getFont().deriveFont((float) (comp.getFont().getSize2D() * 0.75)));
        setMainComponent(comp);

        initNodes();
        updateTool(false);
    }


    protected void initNodes() {
        try {
            DummyTask dummytask = new DummyTask();
            Node dummynode;

            if (node.isInputNode())
                dummynode = dummytask.addDataOutputNode();
            else
                dummynode = dummytask.addDataInputNode();

            nodecomp = new TrianaNode(dummynode, false);
            setNodeComponent(dummynode, nodecomp);
        } catch (TaskGraphException except) {
            except.printStackTrace();
        }
    }


    /**
     * Called when the tool is repainted and the current shownode and showtask are invalid
     */
    private void updateTool(boolean disconnect) {
        if (bottomintnode != null)
            bottomintnode.removeNodeListener(this);

        if (bottomconnnode != null)
            bottomconnnode.removeNodeListener(this);

        if (showtask != null)
            showtask.removeTaskListener(this);

        bottomintnode = NodeUtils.getBottomLevelNode(node);
        bottomintnode.addNodeListener(this);

        if ((bottomintnode.isConnected()) && (!disconnect)) {
            if (bottomintnode.isInputNode())
                bottomconnnode = bottomintnode.getCable().getSendingNode();
            else
                bottomconnnode = bottomintnode.getCable().getReceivingNode();

            showtask = bottomconnnode.getTask();
            connectflag = true;

            bottomconnnode.addNodeListener(this);
            showtask.addTaskListener(this);
        } else {
            showtask = null;
            bottomconnnode = null;
            connectflag = false;
        }

        updateToolName();
    }


    /**
     * @return this component
     */
    public Component getComponent() {
        return this;
    }

    /**
     * The component used to represent this for show tools node
     */
    public NodeComponent getNodeComponent() {
        return nodecomp;
    }


    /**
     * @return the actual node whose external connection is being shown.
     */
    public Node getInternalNode() {
        return node;
    }

    /**
     * @return the group node whose external connection is being shown
     */
    public Node getGroupNode() {
        return groupnode;
    }


    /**
     * Gets the real name for the task this tool is representing
     */
    public String getToolName() {
        if (node == null)
            return "";
        else if (!connectflag)
            return "NULL";
        else
            return showtask.getToolName();
    }


    /**
     * @return FORSHOW_CONNECTED_COLOR
     */
    public Color getToolColor() {
        if ((bottomintnode == null) || (!bottomintnode.isConnected()))
            return ColorManager.getColor(SHOW_TOOL_UNCONNECTED_ELEMENT);
        else
            return ColorManager.getColor(SHOW_TOOL_CONNECTED_ELEMENT);
    }

    /**
     * @return the color of the stripe on the tool, or the standard tool color
     *         if no stipes
     */
    public Color getStripeColor() {
        return getToolColor();
    }


    /**
     * Updates the name on the text icon
     */
    private void updateToolName() {
        Component comp = getMainComponent();

        if (comp instanceof TextIcon) {
            ((TextIcon) comp).setText(getToolName());

            invalidate();

            if (getParent() != null) {
                invalidate();
                getParent().validate();
                getParent().repaint();
            }
        }
    }


    /**
     * Returns true if the particular unit is selected
     */
    public boolean isSelected() {
        return false;
    }


    /**
     * @return a string representation of this unit.
     */
    public String toString() {
        return "";  // nothing to describe me!
    }


    /**
     * Called when a node is connected to a cable.
     */
    public void nodeConnected(NodeEvent event) {
        updateTool(false);
    }


    /**
     * Called before a node is diconnected from a cable.
     */
    public void nodeDisconnected(NodeEvent event) {
        updateTool(true);
    }

    /**
     * Called when the name of the parameter the node is inputting/outputting is set.
     * No-op.
     */
    public void parameterNameSet(NodeEvent event) {
    }

    /**
     * Called when one of a group node's parents changes
     */
    public void nodeParentChanged(NodeEvent event) {
        updateTool(false);
    }

    /**
     * Called when one of a group node's child changes
     */
    public void nodeChildChanged(NodeEvent event) {
        updateTool(false);
    }


    /**
     * Called when the core properties of a task change i.e. its name, whether it is running continuously etc.
     */
    public void taskPropertyUpdate(TaskPropertyEvent event) {
        if (event.getUpdatedProperty() == TaskPropertyEvent.TASK_NAME_UPDATE)
            updateToolName();
    }


    /**
     * Called when the value of a parameter is changed, including when a parameter is removed.
     * No-op.
     */
    public void parameterUpdated(ParameterUpdateEvent event) {
    }

    /**
     * Called when a data input node is added.
     * No-op.
     */
    public void nodeAdded(TaskNodeEvent event) {
    }

    /**
     * Called before a data input node is removed.
     * No-op.
     */
    public void nodeRemoved(TaskNodeEvent event) {
    }

    /**
     * Called before the task is disposed
     */
    public void taskDisposed(TaskDisposedEvent event) {
    }


    /**
     * Called to clean-up the tool when it is no longer used.
     */
    public void dispose() {
        if (showtask != null) {
            showtask.removeTaskListener(this);
            showtask = null;
        }

        if (bottomintnode != null) {
            bottomintnode.removeNodeListener(this);
            bottomintnode = null;
        }

        if (bottomconnnode != null) {
            bottomconnnode.removeNodeListener(this);
            bottomconnnode = null;
        }

        super.dispose();
    }


    private class DummyTask extends TaskImp {

        public DummyTask() throws TaskException {
            super(new ToolImp(), new TaskFactoryImp(), false);
        }


    }

}