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

import org.trianacode.taskgraph.ExecutionState;
import org.trianacode.taskgraph.Node;


/**
 * Communication interface between data monitors and runnable instances. The
 * monitor sends a finished notification when it has finished outputting its
 * data, allowing the runnable instance to continue with its execution (assuming
 * sends are blocking). When data is received by the monitor the runnable
 * instance is sent a wake-up call to allow it to start processing.
 *
 * @see LocalCable
 * @see Monitor
 *
 * @author      Ian Taylor
 * @created     29th April 2002
 * @version     $Revision: 4048 $
 * @date        $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public interface RunnableInstance {

    /**
     * Adds a execution state listener to this runnable instance
     */
    public void addExecutionListener(ExecutionListener listener);

    /**
     * Removes a execution state listener from this runnable instance
     */
    public void removeExecutionListener(ExecutionListener listener);


    /**
     * @return the current state of te runnable instance (e.g. runnable task)
     */
    public ExecutionState getExecutionState();


    /**
     * Indicates to the runnable instance that a wake-up signal has been received
     * from the scheduler. It is up to a task to respond to this wake-up, or to
     * ignore it if wake-ups have not been received from all the nodes a task
     * requires to execute.
     */
    public void wakeUp();

    /**
     * Indicates to the runnable instance that a wake-up signal has been received
     * from the specified node (e.g. data is available on that node). A runnable
     * instance should only run when it has received wake-ups from all the nodes
     * it requires to execute.
     */
    public void wakeUp(Node node);


    /**
     * Request a runnable instance to pause
     */
    public void pause();

    /**
     * Request a runnable instance to resume (unpause)
     */
    public void resume();

    /**
     * Order a runnable instance to stop and reset it to its pre-run state
     */
    public void reset();

    /**
     * Tell the data monitor that this thread monitor has completed outputting
     * the data i.e. the data has been received by the receiving process.
     *
     */
    public void finished();

}












