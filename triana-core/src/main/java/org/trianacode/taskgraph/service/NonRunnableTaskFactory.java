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

package org.trianacode.taskgraph.service;

import org.trianacode.taskgraph.Cable;
import org.trianacode.taskgraph.CableException;
import org.trianacode.taskgraph.Node;
import org.trianacode.taskgraph.NodeException;
import org.trianacode.taskgraph.ParameterNode;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskException;
import org.trianacode.taskgraph.TaskFactory;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.imp.CableImp;
import org.trianacode.taskgraph.imp.NodeImp;
import org.trianacode.taskgraph.imp.ParameterNodeImp;
import org.trianacode.taskgraph.tool.Tool;


/**
 * A taskgraph factory for creating transient tasks. Transient tasks are tasks which are in the process of being
 * distributed and there should not be executed.
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 */

public class NonRunnableTaskFactory implements TaskFactory {

    public final static String NON_RUNNABLE_FACTORY_NAME = "Non-Runnable";
    public final static String NON_RUNNABLE_FACTORY_DESCRIPTION
            = "Non-runnable tasks that generate an error if execution attempted";

    private String factoryname = NON_RUNNABLE_FACTORY_NAME;
    private String description = NON_RUNNABLE_FACTORY_DESCRIPTION;


    public NonRunnableTaskFactory() {
    }

    public NonRunnableTaskFactory(String factoryname, String description) {
        this.factoryname = factoryname;
        this.description = description;
    }


    /**
     * @return the name of the taskgraph factory
     */
    public String getFactoryName() {
        return factoryname;
    }

    /**
     * @return a description of the task factory
     */
    public String getFactoryDescription() {
        return description;
    }


    /**
     * @return a new task of type tool, optionally preserving the original instance id in the new task.
     */
    public Task createTask(Tool tool, TaskGraph parent, boolean preserveinst) throws TaskException {
        Task task = new NonRunnableTask(tool, this, preserveinst);
        task.setParent(parent);
        task.init();
        return task;
    }

    /**
     * @return a new node connected to the specified task
     */
    public Node createNode(Task task, boolean input) throws NodeException {
        return new NodeImp(task, input);
    }

    /**
     * @return a new parameter node inputting/outputting the specified parameter and connected to the specified task
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
