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

import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.tool.ToolTable;


/**
 * The interface used by Unit to access RunnableTask. RunnableTasks implement
 * the data handling capability of a Task.
 *
 * @author      Ian Taylor
 * @created     29th April 2002
 * @version     $Revision: 4048 $
 * @date        $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public interface RunnableInterface extends ClipableTaskInterface {

    /**
     * @return an interface to the task
     */
    public Task getTask();

    /**
     * @return an control interface to the scheduler (returns null if the
     * task is not a control task)
     */
    public ControlInterface getControlInterface();


    /**
     * @return true if there is data waiting on the specified node
     */
    public boolean isInput(int nodeNumber);

    /**
     * Returns the data at input node <i>nodeNumber</i>. If data is not
     * ready, NOT_READY triana type is returned. If there is no cable
     * connected to the input node the NOT_CONNECTED triana type is returned.
     *
     * @param nodeNumber the node you want to get the data from.
     * @return the data input at node nodeNumber
     */
    public Object getInput(int nodeNumber)
            throws OutOfRangeException, EmptyingException, NotCompatibleException;

    /**
     * Outputs the data across all nodes. This passses the given data set
     * to the first output node and then makes copies for any other output
     * nodes.
     *
     * @param data the data to be sent
     */
    public void output(Object data);

    /**
     * Outputs the data to the given node <i>outputNode</i>. If specified
     * this method blocks until the data is successfully sent, otherwise,
     * if non-blocking, isOutputSent() can be used to poll whether the
     * data has been successfully sent. This method is used to set the
     * data at each particular output node if this is necessary, otherwise
     * use output(Object data) to copy the data across all nodes.
     *
     * @param outputNode the output node you wish to set
     * @param data the data to be sent
     */
    public void output(int outputNode, Object data, boolean blocking);

    /**
     * @return true if the data sent with the send call has
     * reached its destination
     */
    public boolean isOutputSent(int outputNode);


    /**
     * called by a Unit when an error occurs
     */
    public void notifyError(String message);


    /**
     * @return a table of the currently loaded tools
     */
    public ToolTable getToolTable();

}