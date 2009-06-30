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

package org.trianacode.gui.hci;

import org.trianacode.gui.main.IndicationCableInterface;
import org.trianacode.gui.main.NodeComponent;
import org.trianacode.gui.main.TaskGraphPanel;
import org.trianacode.taskgraph.Node;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskGraphException;
import org.trianacode.taskgraph.TaskGraphUtils;
import org.trianacode.taskgraph.tool.Tool;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * A class for handling mouse functions on a node. This functionality was
 * originally in ToolMouseHandler
 *
 * @author Ian Wang
 * @versbion $Revision: 4048 $
 * @created
 * @date $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $

 */

public class NodeMouseHandler implements MouseListener, MouseMotionListener {

    /**
     * a reference to the taskgraph panel
     */
    private TaskGraphPanel panel;

    /**
     * the node component a cable is being dragged from
     */
    private NodeComponent output;


    public NodeMouseHandler(TaskGraphPanel panel) {
        this.panel = panel;
    }


    /**
     * @return the node component an event is associated with (or null if
     *         unknown)
     */
    private NodeComponent getNodeComponent(MouseEvent event) {
        if (!(event.getSource() instanceof NodeComponent))
            return null;

        return (NodeComponent) event.getSource();
    }


    /**
     * Invoked when the mouse button has been clicked (pressed
     * and released) on a component.
     */
    public void mouseClicked(MouseEvent event) {
        NodeComponent comp = getNodeComponent(event);

        if (comp == null)
            return;

        if (SwingUtilities.isRightMouseButton(event)) {
            if (comp.isOutputNode() && comp.isConnectable()) {
                Task task = comp.getNode().getTask();

                if (!task.isParameterName(Tool.OUTPUT_POLICY))
                    task.setParameter(Tool.OUTPUT_POLICY, Tool.COPY_OUTPUT);
                else if (task.getParameter(Tool.OUTPUT_POLICY).equals(Tool.COPY_OUTPUT))
                    task.setParameter(Tool.OUTPUT_POLICY, Tool.CLONE_MULTIPLE_OUTPUT);
                else
                    task.setParameter(Tool.OUTPUT_POLICY, Tool.COPY_OUTPUT);
            }

            comp.getComponent().invalidate();
            comp.getComponent().validate();
            comp.getComponent().repaint();
        } if (event.getClickCount() == 2)
            try {
                Node node = comp.getNode();

                if (node.getTask().getParent() != null) {
                    TaskGraphUtils.disconnectControlTask(node.getTask().getParent());

                    if (node.getChildNode() != null)
                        node.getChildNode().getTask().removeNode(node.getChildNode());
                    else if (node.isConnected())
                        node.getTask().getParent().disconnect(node.getCable());

                    TaskGraphUtils.connectControlTask(node.getTask().getParent());
                }
            } catch (TaskGraphException except) {
                except.printStackTrace();
            }
    }

    /**
     * Invoked when the mouse enters a component.
     */
    public void mouseEntered(MouseEvent e) {
    }

    /**
     * Invoked when the mouse exits a component.
     */
    public void mouseExited(MouseEvent e) {
    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     */
    public void mousePressed(MouseEvent event) {
        if (panel instanceof IndicationCableInterface) {
            NodeComponent comp = getNodeComponent(event);

            if (comp == null)
                return;

            if (comp.isOutputNode() && comp.isConnectable())
                output = comp;
            else
                output = null;
        }
    }

    /**
     * Invoked when a mouse button has been released on a component.
     */
    public void mouseReleased(MouseEvent event) {
        if (panel instanceof IndicationCableInterface) {
            if (output != null) {
                NodeComponent comp = ((IndicationCableInterface) panel).getIndicationNode();

                if ((comp != null) && comp.isInputNode() && comp.isConnectable())
                    TaskGraphHandler.connect(panel, output.getNode(), comp.getNode());
            }

            ((IndicationCableInterface) panel).clearIndicationCableInterface();
            output = null;
        }
    }


    /**
     * Invoked when a mouse button is pressed on a component and then
     * dragged.  <code>MOUSE_DRAGGED</code> events will continue to be
     * delivered to the component where the drag originated until the
     * mouse button is released (regardless of whether the mouse position
     * is within the bounds of the component).
     * <p/>
     * Due to platform-dependent Drag&Drop implementations,
     * <code>MOUSE_DRAGGED</code> events may not be delivered during a native
     * Drag&Drop operation.
     */
    public void mouseDragged(MouseEvent event) {
        if ((output != null) && (panel instanceof IndicationCableInterface))
            ((IndicationCableInterface) panel).drawIndicationCableInterface(output.getComponent(), event.getPoint());
    }

    /**
     * Invoked when the mouse cursor has been moved onto a component
     * but no buttons have been pushed.
     */
    public void mouseMoved(MouseEvent e) {
    }

}
