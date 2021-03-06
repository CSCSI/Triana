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

package org.trianacode.gui.components.hidden;

import org.trianacode.gui.hci.tools.DoNothingAction;
import org.trianacode.gui.hci.tools.ToolComponentModel;
import org.trianacode.gui.main.NodeComponent;
import org.trianacode.gui.main.TaskComponent;
import org.trianacode.taskgraph.Node;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.tool.Tool;

import javax.swing.*;
import java.awt.*;
import java.util.Hashtable;

/**
 * A tool model that renders a tool invisible on the gui.
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 */


public class HiddenComponentModel implements ToolComponentModel {

    /**
     * @return the icon for the specified tool (if null is returned then the default leaf icon is used)
     */
    public Icon getTreeIcon(Tool tool) {
        return null;
    }

    /**
     * @return the tool tip for the tool when in the tree (if null is returned then the default tool tip is used, return
     *         a empty string for no tip)
     */
    public String getTreeToolTip(Tool tool, boolean extended) {
        return null;
    }

    /**
     * @return the popup menu the tool when in the tree (if null is returned then the default popup menu is used, return
     *         a empty popup menu for no popup)
     */
    public JPopupMenu getTreePopup(Tool tool) {
        return null;
    }

    /**
     * @return the tool tip for the tool when on the workspace (if null is returned then the default tool tip is used,
     *         return a empty string for no tip)
     */
    public String getWorkspaceToolTip(Task task, boolean extended) {
        return "";
    }

    /**
     * @return the popup menu the tool when in the workspace (if null is returned then the default popup menu is used,
     *         return a empty popup menu for no popup)
     */
    public JPopupMenu getWorkspacePopup(Task task) {
        return new JPopupMenu("Hidden Menu");
    }

    /**
     * @return the task component used to represent the specified task (if null is returned then the default component
     *         is used)
     */
    public TaskComponent getTaskComponent(Task task) {
        return new HiddenTaskComponent(task);
    }

    /**
     * @return the action that is invoked when the task is activated (e.g. double-clicked). If null is returned the
     *         default tool action is used.
     */
    public Action getTaskAction(Task task) {
        return new DoNothingAction();
    }


    /**
     * Return whether the icon is shown for the specified update action (e.g. INCREASE_INPUT_NODES_ACTION as defined in
     * UpdateActionConstants)
     *
     * @return either DISPLAY_ICON, HIDE_ICON or UNKNOWN_ACTION
     */
    public int isUpdateIcon(Task task, String action) {
        return UNKNOWN_ACTION;
    }

    /**
     * Called to determine the action that is invoked when an update action is choosen.
     *
     * @param action the update action (e.g. ADD_INPUT_NODE_ACTION)
     * @return either the action or null if unknown.
     */
    public Action getUpdateAction(Task task, String action) {
        return null;
    }


    private class HiddenTaskComponent extends AbstractButton implements TaskComponent {

        private Task task;
        private Hashtable nodes = new Hashtable();

        private boolean selected;


        public HiddenTaskComponent(Task task) {
            this.task = task;
        }

        /**
         * @return this main task component
         */
        public Component getComponent() {
            return this;
        }

        /**
         * @return the task for this component
         */
        public Task getTaskInterface() {
            return task;
        }

        /**
         * @return the node component for ths specified node
         */
        public NodeComponent getNodeComponent(Node node) {
            if (!nodes.containsKey(node)) {
                nodes.put(node, new HiddenNodeComponent(node));
            }

            return (NodeComponent) nodes.get(node);
        }


        /**
         * Sets the component as selected
         */
        public void setSelected(boolean state) {
            if (selected != state) {
                this.selected = state;
            }
        }

        /**
         * Returns true if the particular component is selected
         */
        public boolean isSelected() {
            return selected;
        }


        /**
         * Dispose of this task component
         */
        public void dispose() {
        }

    }

    private class HiddenNodeComponent extends AbstractButton implements NodeComponent {

        private Node node;

        public HiddenNodeComponent(Node node) {
            this.node = node;
        }

        /**
         * @return this component
         */
        public Component getComponent() {
            return this;
        }

        /**
         * @return the interface to the node this component represents
         */
        public Node getNode() {
            return node;
        }

        /**
         * @return true if the node represented by this component can be connected to/connected from
         */
        public boolean isConnectable() {
            return true;
        }

        /**
         * @return true if the node represented by this component is an input node
         */
        public boolean isInputNode() {
            return node.isInputNode();
        }

        /**
         * @return true if the node represented by this component is an output node
         */
        public boolean isOutputNode() {
            return node.isOutputNode();
        }

    }


}
