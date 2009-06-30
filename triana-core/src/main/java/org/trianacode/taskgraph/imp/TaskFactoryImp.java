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
import org.trianacode.taskgraph.tool.Tool;

/**
 * A basic implementation of TaskFactory that creates TaskImps, NodeImps
 * and CableImps.
 *
 * @author Matthew Shields
 * @version $Revision: 4048 $
 * @created May 1, 2002
 * @date $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public class TaskFactoryImp implements TaskFactory {

    private String name = DEFAULT_FACTORY_NAME;
    private String description = "Default Triana Tool (non-runnable)";


    public TaskFactoryImp() {
    }

    public TaskFactoryImp(String factoryname) {
        this.name = factoryname;
    }

    public TaskFactoryImp(TaskFactory factory) {
        this.name = factory.getFactoryName();
        this.description = factory.getFactoryDescription();
    }


    /**
     * @return the name of the taskgraph factory
     */
    public String getFactoryName() {
        return name;
    }

    public String getFactoryDescription() {
        return description;
    }


    /**
     * @return a new task of type tool
     */
    public Task createTask(Tool tool, TaskGraph parent, boolean preserveinst) throws TaskException {
        try {
            Task task = new TaskImp(tool, this, preserveinst);
            task.setParent(parent);
            task.init();

            return task;
        } catch (TaskGraphException except) {
            throw (new TaskException(except.getMessage(), except));
        }
    }

    /**
     * @return a new node connected to the specified task at the specified index
     */
    public Node createNode(Task task, boolean input) throws NodeException {
        return new NodeImp(task, input);
    }

    /**
     * @return a new parameter node inputting/outputting the specified parameter
     *         and connected to the specified task
     */
    public ParameterNode createParameterNode(String paramname, Task task, boolean input) throws NodeException {
        return new ParameterNodeImp(paramname, task, input);
    }


    /**
     * @return a new cable connecting the specified nodes
     */
    public Cable createCable(Node sendnode, Node recnode) throws CableException {
        Cable cable = new CableImp();
        cable.connect(sendnode, recnode);

        return cable;
    }

}
