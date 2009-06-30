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

import org.trianacode.taskgraph.ParameterNode;
import org.trianacode.taskgraph.Task;

/**
 * An implementation of the parameter node interface, that extends NodeImp
 *
 * @author      Ian Wang
 * @created     2nd July 2003
 * @version     $Revision: 4048 $
 * @date        $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 *
 */

public class ParameterNodeImp extends NodeImp implements ParameterNode {

    /**
     * The name of the parameter this node inputs/outputs
     */
    private String paramname;

    /**
     * A flag indicating whether this parameter node requires data for the task
     * to execute.
     */
    private boolean trigger = false;

    /**
     * A flag indicating whether errors are output from this node
     */
    private boolean error = false;


    public ParameterNodeImp(String paramname, Task task, boolean input) {
        super(task, input);
        this.paramname = paramname;
    }



    /**
     * @return the name of the parameter that this node is inputting/outputting
     */
    public String getParameterName() {
        if (isTopLevelNode())
            return paramname;
        else
            return ((ParameterNode) getTopLevelNode()).getParameterName();
    }

    /**
     * Sets the parameter that this node is inputting/outputting
     */
    public void setParameterName(String name) {
        if (!isTopLevelNode())
            throw (new RuntimeException("Cannot set parameter name on group node (top-level nodes only)"));

        paramname = name;
        notifyParameterNameSet();
    }


    /**
     * @return true if this node is a data node
     */
    public boolean isDataNode() {
        return false;
    }

    /**
     * @return true if this node is a parameter node
     */
    public boolean isParameterNode() {
        return true;
    }


    /**
     * @return true if this parameter node is a trigger node
     */
    public boolean isTriggerNode() {
        return trigger;
    }

    /**
     * Sets this parameter node as a trigger node, If true then the node is
     * essential for the task to run, otherwise it is optional.
     */
    public void setTriggerNode(boolean state) {
        trigger = state;
    }

    /**
     * @return true if this parameter node is a error node
     */
    public boolean isErrorNode() {
        return error;
    }

    /**
     * Sets this parameter node as a error node, If true then the node is
     * essential for the task to run, otherwise it is optional.
     */
    public void setErrorNode(boolean state) {
        error = state;
    }


    /**
     * @return true if data is essential at this node for the task to run.
     * Note that data nodes are essential by default.
     */
    public boolean isEssential() {
        return trigger;
    }

    /**
     * @return true if data is essential at this node only if the node is
     * connected
     */
    public boolean isEssentialIfConnected() {
        return false;
    }

    /**
     * @return true if data is not required at this node for the task to run.
     * Note that parameter nodes are optional by default.
     */
    public boolean isOptional() {
        return !trigger;
    }

}
