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

import org.trianacode.taskgraph.event.NodeListener;

/**
 * An interface for accessing the NodeCable associated with a Task.
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 */
public interface Node {


    /**
     * @return the task this node is attached to
     */
    public Task getTask();

    /**
     * Adds a node listener to this node.
     */
    public void addNodeListener(NodeListener listener);

    /**
     * Removes a node listener from this node.
     */
    public void removeNodeListener(NodeListener listener);


    /**
     * @return the index of this node within its associated task (or -1 if disposed/not attached to a task)
     */
    public int getNodeIndex();

    /**
     * This is a convience method to return the absolute positioning of a node.
     * <p/>
     * The absolute index of a data node is the same as its standard index. The absolute index of a parameter node is
     * its standard index + the total number of data input/output nodes.
     *
     * @return the absolute index of this node within its associated task (or -1 if disposed/not attached to a task)
     */
    public int getAbsoluteNodeIndex();


    /**
     * @return true if this node is connected to a cable
     */
    public boolean isConnected();

    /**
     * @return the cable this node is connected to
     */
    public Cable getCable();


    /**
     * @return true if this node is an input node
     */
    public boolean isInputNode();

    /**
     * @return true if this node is an output node
     */
    public boolean isOutputNode();


    /**
     * @return true if this node is a data node
     */
    public boolean isDataNode();

    /**
     * @return true if this node is a parameter node
     */
    public boolean isParameterNode();


    /**
     * @return true if data is not required at this node for the task to run
     */
    public boolean isOptional();

    /**
     * @return true if data is essential at this node for the task to run
     */
    public boolean isEssential();

    /**
     * @return true if data is essential at this node only if the node is connected
     */
    public boolean isEssentialIfConnected();


    /**
     * @return true if this is a top level group node (i.e. it is attached directly to an actual (non-group) task)
     */
    public boolean isTopLevelNode();

    /**
     * @return the top level parent node in the parent/child group node hierarchy
     */
    public Node getTopLevelNode();

    /**
     * @return the top level parent task in the parent/child group task hierarchy
     */
    public Task getTopLevelTask();


    /**
     * @return true if this is a bottom level group node (i.e. it is attached directly to a actual cable)
     */
    public boolean isBottomLevelNode();

    /**
     * @return the bottom level parent node in the parent/child group node hierarchy
     */
    public Node getBottomLevelNode();

    /**
     * @return the bottom level parent task in the parent/child group task hierarchy
     */
    public Task getBottomLevelTask();


    /**
     * @return the parent group node for this node (null if top level)
     */
    public Node getParentNode();

    /**
     * @return the child group node for this node (null if bottom level)
     */
    public Node getChildNode();

    /**
     * from Node
     */


    /**
     * Connect a cable to this node. Should only be called from within cable.
     */
    public void connect(Cable cable);

    /**
     * Disconnect the cable from this node. Should only be called from within cable.
     */
    public void disconnect();


    /**
     * Sets the parent group node for this node
     */
    public void setParentNode(Node node);

    /**
     * Sets the child group node for this node
     */
    public void setChildNode(Node node);

    /**
     * Cleans-up when the node is no longer used
     */
    public void dispose();

}
