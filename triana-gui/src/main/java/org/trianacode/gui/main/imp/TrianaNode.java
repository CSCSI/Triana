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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.AbstractButton;
import org.trianacode.gui.hci.color.ColorManager;
import org.trianacode.gui.main.NodeComponent;
import org.trianacode.gui.main.TrianaLayoutConstants;
import org.trianacode.taskgraph.Node;
import org.trianacode.taskgraph.ParameterNode;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.tool.Tool;

/**
 * <p>TrianaNode is a simple Rectangle Area which represents either an input or output node on a SubTrianaTool.
 *
 * @author Ian Taylor
 * @version $Revision: 4048 $
 */
public class TrianaNode extends AbstractButton implements NodeComponent {

    /**
     * The node this Triana node represents
     */
    private Node node;

    /**
     * A flag indicating whether this node is a group node
     */
    boolean groupNode = false;

    /**
     * A flag indicating whether this node can be connected to/connected from
     */
    private boolean connectable;

    /**
     * A flag indicating whether this node is attached to a task with a copy output policy
     */
    private boolean copyoutput;


    /**
     * The color of the node when it is connected
     */
    private Color connectedcol;

    /**
     * The color of this node when unconnected
     */
    private Color unconnectedcol;

    /**
     * The color of this node if it is a trigger
     */
    private Color triggercolor;

    /**
     * The color of this diamond used if this node is a parameter node
     */
    private Color paramcolor;


    /**
     * Constructs a new TrianaNode
     */
    public TrianaNode(Node node, boolean connectable) {
        this.node = node;
        this.connectable = connectable;
        this.copyoutput = checkCopyOutput();

        setPreferredSize(TrianaLayoutConstants.DEFAULT_NODE_SIZE);

        setConnectedColor(TrianaLayoutConstants.DEFAULT_CONNECTED_NODE_COLOR);
        setUnconnectedColor(TrianaLayoutConstants.DEFAULT_UNCONNECTED_NODE_COLOR);
        setTriggerColor(TrianaLayoutConstants.DEFAULT_TRIGGER_NODE_COLOR);
        setParameterColor(TrianaLayoutConstants.DEFAULT_PARAMETER_NODE_COLOR);
    }

    /**
     * @return true if the node belongs to a task with a copy output policy
     */
    private boolean checkCopyOutput() {
        if ((node.getTask() == null) || (node.isInputNode())) {
            return false;
        }

        Task task = node.getTask();
        return ((task.isParameterName(Tool.OUTPUT_POLICY)) && (task.getParameter(Tool.OUTPUT_POLICY).equals(
                Tool.COPY_OUTPUT)));
    }


    /**
     * @return this component
     */
    public Component getComponent() {
        return this;
    }

    /**
     * @return the node interface for this node
     */
    public Node getNode() {
        return node;
    }

    /**
     * @return true if the node represented by this component is an output node
     */
    public boolean isOutputNode() {
        return node.isOutputNode();
    }

    /**
     * @return true if the node represented by this component is an output node
     */
    public boolean isInputNode() {
        return node.isInputNode();
    }

    /**
     * @return true if the node represented by this component can be connected to/connected from
     */
    public boolean isConnectable() {
        return connectable;
    }


    /**
     * @return true if this node is a parameter node
     */
    public boolean isParameterNode() {
        return node.isParameterNode();
    }

    /**
     * @return true if this node is a trigger node
     */
    public boolean isTriggerNode() {
        return isParameterNode() && ((ParameterNode) node).isTriggerNode();
    }


    /**
     * @return the color of this node
     */
    public Color getConnectedColor() {
        return connectedcol;
    }

    /**
     * Sets the color of this node
     */
    public void setConnectedColor(Color col) {
        this.connectedcol = col;
    }

    /**
     * @return the color of this node when unconnected
     */
    public Color getUnconnectedColor() {
        return unconnectedcol;
    }

    /**
     * Sets the color of this node when unconnected
     */
    public void setUnconnectedColor(Color col) {
        this.unconnectedcol = col;
    }

    /**
     * @return the color of this node if it is a trigger
     */
    public Color getTriggerColor() {
        return triggercolor;
    }

    /**
     * Sets the color of this node if it is a trigger
     */
    public void setTriggerColor(Color col) {
        triggercolor = col;
    }

    /**
     * @return the color of this diamond used to denote parameter nodes
     */
    public Color getParameterColor() {
        return paramcolor;
    }

    /**
     * Sets the color of this diamond used to denote parameter nodes
     */
    public void setParameterColor(Color col) {
        paramcolor = col;
    }


    /**
     * Validates this container and all of its subcomponents.
     * <p/>
     * The <code>validate</code> method is used to cause a container to lay out its subcomponents again. It should be
     * invoked when this container's subcomponents are modified (added to or removed from the container, or
     * layout-related information changed) after the container has been displayed.
     *
     * @see #add(java.awt.Component)
     * @see java.awt.Component#invalidate
     * @see javax.swing.JComponent#revalidate()
     */
    public void validate() {
        copyoutput = checkCopyOutput();
        super.validate();
    }

    /**
     * redraws the TrianaNode at a certain xnew, ynew position
     *
     * @param graphs the graphics context
     */
    public void paintComponent(Graphics graphs) {
        super.paintComponent(graphs);

        Color col = ColorManager.getColor(node);

        if (col.getAlpha() > 0) {
            graphs.setColor(col);

            Dimension size = getSize();

            if (copyoutput) {
                graphs.fillPolygon(new int[]{size.width, size.width, 0, 0},
                        new int[]{0, size.height, (size.height + 1) / 2, size.height / 2}, 4);
            } else {
                graphs.fillRect(0, 0, size.width, size.height);
            }

            if (isParameterNode()) {
                graphs.setColor(getParameterColor());
                graphs.fillOval(size.width / 4, size.height / 4,
                        size.width / 2, size.height / 2);
            }
        } else {
            graphs.setColor(getBackground());
            graphs.drawRect(0, 0, getSize().width, getSize().height);
        }
    }

}













