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

import org.trianacode.taskgraph.*;
import org.trianacode.taskgraph.event.NodeEvent;
import org.trianacode.taskgraph.event.NodeListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * An input/output NodeCable associated with a Task.
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 */
public class NodeImp implements NodeListener, Node {

    /**
     * The task this node attached to.
     */
    private Task task;

    /**
     * The parent and child nodes for this node
     */
    private Node parent;
    private Node child;

    /**
     * The cable this node is connected to.
     */
    private Cable cable;

    /**
     * An array list of the node's listeners
     */
    private ArrayList listeners = new ArrayList();

    /**
     * A flag indicating whether this is an input node
     */
    private boolean input;

    private Map<String, String> properties = new HashMap<String, String>();


    /**
     * Creates a node attached to the specified Task.
     */
    public NodeImp(Task task, boolean input) {
        this.task = task;
        this.input = input;
    }


    /**
     * @return the task this node is attached to
     */
    public Task getTask() {
        return task;
    }


    /**
     * Adds a node listener to this node.
     */
    public void addNodeListener(final NodeListener listener) {
        TaskGraphEventDispatch.invokeLater(new Runnable() {
            public void run() {
                if ((listeners != null) && (!listeners.contains(listener))) {
                    listeners.add(listener);
                }
            }
        });
    }

    /**
     * Removes a node listener from this node.
     */
    public void removeNodeListener(final NodeListener listener) {
        TaskGraphEventDispatch.invokeLater(new Runnable() {
            public void run() {
                listeners.remove(listener);
            }
        });
    }


    /**
     * @return the index of this node within its associated task
     */
    public int getNodeIndex() {
        if (task != null) {
            return task.getNodeIndex(this);
        } else {
            return -1;
        }
    }

    /**
     * This is a convience method to provide backward compatibility with TrianaGUI, in which parameter nodes where
     * indexed after data nodes.
     * <p/>
     * The absolute index of a data node is the same as its standard index. The absolute index of a parameter node is
     * its standard index + the total number of data input node.
     *
     * @return the absolute index of this node within its associated Task.
     */
    public int getAbsoluteNodeIndex() {
        if (task != null) {
            return task.getAbsoluteNodeIndex(this);
        } else {
            return -1;
        }
    }


    /**
     * Connect a cable to this node. Should only be called from within cable.
     */
    public void connect(Cable cable) {
        if (getChildNode() != null) {
            child.setParentNode(null);
            setChildNode(null);
        }

        this.cable = cable;
        notifyNodeConnected();
    }

    /**
     * Disconnect the cable from this node. Should only be called from within cable.
     */
    public void disconnect() {
        if (isConnected()) {
            notifyNodeDisconnected();
            cable = null;
        }
    }

    /**
     * @return true if this node is connected to a cable
     */
    public boolean isConnected() {
        if (isBottomLevelNode()) {
            return cable != null;
        } else {
            return getBottomLevelNode().isConnected();
        }
    }

    /**
     * @return the cable this node is connected to
     */
    public Cable getCable() {
        if (isBottomLevelNode()) {
            return cable;
        } else {
            return getBottomLevelNode().getCable();
        }
    }


    /**
     * @return true if this node is an input node
     */
    public boolean isInputNode() {
        return input;
    }

    /**
     * @return true if this node is an output node
     */
    public boolean isOutputNode() {
        return !input;
    }

    /**
     * @return true if this node is a data node
     */
    public boolean isDataNode() {
        return true;
    }

    /**
     * @return true if this node is a parameter node
     */
    public boolean isParameterNode() {
        return false;
    }


    /**
     * @return true if data is not required at this node for the task to run. Note that parameter nodes are optional by
     *         default.
     */
    public boolean isOptional() {
        if (task == null) {
            return true;
        }

        if (isOutputNode()) {
            return true;
        }

        if (isTopLevelNode()) {
            return task.getNodeRequirement(getNodeIndex()).equals(Task.OPTIONAL);
        } else {
            return getTopLevelNode().isOptional();
        }
    }

    /**
     * @return true if data is essential at this node only if the node is connected
     */
    public boolean isEssentialIfConnected() {
        if (task == null) {
            return false;
        }

        if (isOutputNode()) {
            return false;
        }

        if (isTopLevelNode()) {
            return task.getNodeRequirement(getNodeIndex()).equals(Task.ESSENTIAL_IF_CONNECTED);
        } else {
            return getTopLevelNode().isEssentialIfConnected();
        }
    }

    /**
     * @return true if data is essential at this node for the task to run. Note that data nodes are essential by
     *         default.
     */
    public boolean isEssential() {
        return (!isOptional()) && (!isEssentialIfConnected());
    }


    /**
     * @return the parent group node for this node (null if top level)
     */
    public Node getParentNode() {
        return parent;
    }

    /**
     * Sets the parent group node for this node
     */
    public void setParentNode(Node node) {
        if ((node == null) && (parent != null)) {
            removeThisNode();
        } else if (node != null) {
            if (parent != null) {
                parent.removeNodeListener(this);
            }

            parent = node;

            notifyParentNodeChanged();

            if (parent != null) {
                parent.addNodeListener(this);
            }
        }
    }

    /**
     * @return the child group node for this node (null if bottom level)
     */
    public Node getChildNode() {
        return child;
    }

    /**
     * Sets the child group node for this node
     */
    public void setChildNode(Node node) {
        if (child != null) {
            child.removeNodeListener(this);
        }

        child = node;
        notifyChildNodeChanged();

        if (child != null) {
            child.addNodeListener(this);
        }
    }


    /**
     * @return true if this is a top level group node (i.e. it is attached directly to an actual (non-group) task)
     */
    public boolean isTopLevelNode() {
        return parent == null;
    }

    /**
     * @return the top level parent node in the parent/child group node hierarchy
     */
    public Node getTopLevelNode() {
        Node top = this;

        while (top.getParentNode() != null) {
            top = top.getParentNode();
        }

        return top;
    }

    /**
     * @return the top level parent task in the parent/child group task hierarchy
     */
    public Task getTopLevelTask() {
        return getTopLevelNode().getTask();
    }

    /**
     * @return true if this is a bottom level group node (i.e. it is attached directly to a actual cable task)
     */
    public boolean isBottomLevelNode() {
        return child == null;
    }

    /**
     * @return the bottom level parent node in the parent/child group node hierarchy
     */
    public Node getBottomLevelNode() {
        Node bottom = this;

        while (bottom.getChildNode() != null) {
            bottom = bottom.getChildNode();
        }

        return bottom;
    }

    /**
     * @return the bottom level parent task in the parent/child group task hierarchy
     */
    public Task getBottomLevelTask() {
        return getBottomLevelNode().getTask();
    }


    /**
     * Removes this node from its task
     */
    private void removeThisNode() {
        if (parent != null) {
            parent.removeNodeListener(this);
            parent.setChildNode(null);
        }

        if (child != null) {
            child.removeNodeListener(this);
            child.setParentNode(null);
        }

        parent = null;
        child = null;

        if (task != null) {
            if (isInputNode()) {
                if (isParameterNode()) {
                    task.removeParameterInputNode((ParameterNode) this);
                } else {
                    task.removeDataInputNode(this);
                }
            } else {
                if (isParameterNode()) {
                    task.removeParameterOutputNode((ParameterNode) this);
                } else {
                    task.removeDataOutputNode(this);
                }
            }
        }
    }

    /**
     * Notifies all the node listeners that the node has been connected.
     */
    protected void notifyNodeConnected() {
        final Node node = this;

        //TaskGraphEventDispatch.invokeLater(new Runnable() {
        //    public void run() {
        NodeListener[] copy = (NodeListener[]) listeners.toArray(new NodeListener[listeners.size()]);
        NodeEvent event = new NodeEvent(node);

        for (int count = 0; count < copy.length; count++) {
            copy[count].nodeConnected(event);
        }
        //    }
        //});
    }

    /**
     * Notifies all the node listeners that a the node has been disconnected.
     */
    protected void notifyNodeDisconnected() {
        final Node node = this;

        //TaskGraphEventDispatch.invokeLater(new Runnable() {
        //    public void run() {
        NodeListener[] copy = (NodeListener[]) listeners.toArray(new NodeListener[listeners.size()]);
        NodeEvent event = new NodeEvent(node);

        for (int count = 0; count < copy.length; count++) {
            copy[count].nodeDisconnected(event);
        }
        //    }
        //});
    }

    /**
     * Notifies all the node listeners that the name of the parameter this node is inputting/outputting has been set.
     */
    protected void notifyParameterNameSet() {
        final Node node = this;

        //TaskGraphEventDispatch.invokeLater(new Runnable() {
        //    public void run() {
        NodeListener[] copy = (NodeListener[]) listeners.toArray(new NodeListener[listeners.size()]);
        NodeEvent event = new NodeEvent(node);

        for (int count = 0; count < copy.length; count++) {
            copy[count].parameterNameSet(event);
        }
        //    }
        //});
    }

    /**
     * Notifies all the node listeners that a node in the parent hierarchy has changed.
     */
    protected void notifyParentNodeChanged() {
        final Node node = this;

        //TaskGraphEventDispatch.invokeLater(new Runnable() {
        //    public void run() {
        NodeListener[] copy = (NodeListener[]) listeners.toArray(new NodeListener[listeners.size()]);
        NodeEvent event = new NodeEvent(node);

        for (int count = 0; count < copy.length; count++) {
            copy[count].nodeParentChanged(event);
        }
        //    }
        // });
    }

    /**
     * Notifies all the node listeners that a node in the child hierarchy has changed.
     */
    protected void notifyChildNodeChanged() {
        final Node node = this;

        //TaskGraphEventDispatch.invokeLater(new Runnable() {
        //    public void run() {
        NodeListener[] copy = (NodeListener[]) listeners.toArray(new NodeListener[listeners.size()]);
        NodeEvent event = new NodeEvent(node);

        for (int count = 0; count < copy.length; count++) {
            copy[count].nodeChildChanged(event);
        }
        //    }
        //});
    }


    /**
     * Called when a node is connected to a cable.
     */
    public void nodeConnected(NodeEvent event) {
        if (event.getNode() == child) {
            notifyNodeConnected();
        }
    }

    /**
     * Called before a node is diconnected from a cable.
     */
    public void nodeDisconnected(NodeEvent event) {
        if (event.getNode() == child) {
            notifyNodeDisconnected();
        }
    }

    /**
     * Called when one of a group node's parents changes
     */
    public void nodeParentChanged(NodeEvent event) {
        if (event.getNode() == parent) {
            if (getTopLevelNode() == null) {
                removeThisNode();
            }

            notifyParentNodeChanged();
        }
    }

    /**
     * Called when one of a group node's child changes
     */
    public void nodeChildChanged(NodeEvent event) {
        if (event.getNode() == child) {
            notifyChildNodeChanged();
        }
    }

    /**
     * Called when the name of the parameter the node is inputting/outputting is set.
     */
    public void parameterNameSet(NodeEvent event) {
        if (event.getNode() == parent) {
            notifyParameterNameSet();
        }
    }


    public String toString() {
        return task + ":" + getAbsoluteNodeIndex();
    }


    /**
     * Cleans-up when a node is disposed
     */
    public void dispose() {
        if (task != null) {
            if (isConnected() && (getBottomLevelNode().getTask().getParent() != null)) {
                try {
                    getBottomLevelNode().getTask().getParent().disconnect(getCable());
                } catch (CableException except) {
                }
            }

            if (parent != null) {
                parent.removeNodeListener(this);
                parent.setChildNode(null);
                parent = null;
                notifyParentNodeChanged();
            }

            if (child != null) {
                child.removeNodeListener(this);
                child.setParentNode(null);
                child = null;
                notifyChildNodeChanged();
            }

            listeners.clear();
            task = null;
        }
    }

    @Override
    public void setProperty(String key, String value) {
        properties.put(key, value);
    }

    @Override
    public String getProperty(String key) {
        return properties.get(key);
    }

    public String getName() {
        String name = task.getQualifiedTaskName();
        if (this.isInputNode()) {
            name += ".in";
        } else {
            name += ".out";
        }
        name += getNodeIndex();
        return name;
    }

}
