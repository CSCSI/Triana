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

import java.util.ArrayList;

import org.trianacode.taskgraph.CableException;
import org.trianacode.taskgraph.Node;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.event.NodeEvent;
import org.trianacode.taskgraph.event.NodeListener;
import org.trianacode.taskgraph.imp.CableImp;
import org.trianacode.taskgraph.interceptor.InterceptorChain;

/**
 * A Local input/output implementation (i.e. implements IOCable) of the GAPCable class for local delivery of data
 * between the nodes of the connected units of this cable.
 *
 * @author Ian Taylor
 * @version $Revision: 4048 $
 * @see Scheduler
 */
public abstract class LocalCable extends CableImp implements IOCable, java.io.Serializable, NodeListener {

    private Monitor monitor;
    private RunnableInstance sendTask;
    private RunnableInstance recTask;

    /**
     * A buffer of items sent before the cable is connected
     */
    private ArrayList buffer = new ArrayList();

    /**
     * a flag indicating whether the cable is suspended
     */
    private boolean suspended = false;


    /**
     * A local cable must be created by giving a reference to a RunnableInstance object so that wehen we use
     * non-blocking sending of data we can notify the RunnableInstance object when each packet has finished
     */
    public void connect(Node sendnode, Node recnode) throws CableException {
        super.connect(sendnode, recnode);

        getSendingNode().addNodeListener(this);
        getReceivingNode().addNodeListener(this);

        updateConnection();
    }

    /**
     * Reconnects the sender to this cable
     */
    public void reconnect(Node node) throws CableException {
        getSendingNode().removeNodeListener(this);
        getReceivingNode().removeNodeListener(this);

        super.reconnect(node);

        getSendingNode().addNodeListener(this);
        getReceivingNode().addNodeListener(this);

        updateConnection();
    }

    /**
     * Updates the sending and receiving tasks
     */
    private void updateConnection() throws CableException {
        if (!(getSendingNode().getTopLevelTask() instanceof RunnableInstance)) {
            throw (new CableException("Inconsistent taskgraph; cannot attach a runnable cable to a non-runnable task"));
        }

        if (!(getReceivingNode().getTopLevelTask() instanceof RunnableInstance)) {
            throw (new CableException("Inconsistent taskgraph; cannot attach a runnable cable to a non-runnable task"));
        }

        sendTask = (RunnableInstance) getSendingNode().getTopLevelTask();
        recTask = (RunnableInstance) getReceivingNode().getTopLevelTask();

        createMonitor();
    }


    /**
     * Disconnect this cable from its nodes.
     */
    public void disconnect() throws CableException {
        if (isConnected()) {
            destroyMonitor();

            getSendingNode().removeNodeListener(this);
            getReceivingNode().removeNodeListener(this);

            super.disconnect();
        }
    }

    /**
     * A method that can be over-ridden to provide additional send functionality.
     *
     * @param data     the data being sent
     * @param blocking true if it is a blocking send
     * @return the actual data to be sent
     */
    protected Object preSend(Object data, boolean blocking) {
        return InterceptorChain.interceptSend(getSendingNode(), getReceivingNode(), data);
    }

    /**
     * A method that can be over-ridden to provide additional receive functionality.
     *
     * @param data the data being received
     * @return the actual data to receive
     */
    protected Object preReceive(Object data) {
        return InterceptorChain.interceptReceive(getSendingNode(), getReceivingNode(), data);
    }


    /**
     * Returns the data from this connection if there is data there.
     */
    public Object recv() {
        if (suspended) {
            throw (new EmptyingException("Suspended cable: " + toString()));
        }

        Object data = null;

        do {
            data = monitor.get();
            data = preReceive(data);
            //data = AspectManager.applyAfter(this, data);
        } while (data == null);

        return data;

    }

    /**
     * @return true if the data is ready to be collected
     */
    public boolean isDataSent() {
        return monitor.isReady();
    }

    /**
     * Blocking call to send the data from the sending task to the receiving task.
     *
     * @param data the data to be sent
     */
    public synchronized void send(Object data) {
        if (suspended) {
            return;
        }

        data = preSend(data, false);

        if (data instanceof DataMessage) {
            setOutputType(((DataMessage) data).getData().getClass().getName());
        }

        if (monitor == null) {
            buffer.add(data);
        } else {
            monitor.put(data);
        }
    }

    /**
     * Non-blocking call to send the data from the sending task to the receiving task.
     *
     * @param data the data to be sent
     */
    public synchronized void sendNonBlocking(Object data) {
        if (suspended) {
            return;
        }

        data = preSend(data, false);

        if (data instanceof DataMessage) {
            setOutputType(((DataMessage) data).getData().getClass().getName());
        }

        if (monitor == null) {
            buffer.add(data);
        } else {
            monitor.putButDontBlock(data);
        }
    }


    /**
     * Sets the output type for the sending task
     */
    private void setOutputType(String type) {
        Node node = getSendingNode();

        while (node != null) {
            if (type != null) {
                node.getTask().setParameter(Task.OUTPUT_TYPE + node.getAbsoluteNodeIndex(), type);
            } else {
                node.getTask().removeParameter(Task.OUTPUT_TYPE + node.getAbsoluteNodeIndex());
            }

            node = node.getChildNode();
        }
    }

    /**
     * Create a new monitor to provide the synchronisation between the OldUnit thread's. See the Producer/Consumer
     * example in the Java Language Tutorial for an explanation of how this works.
     */
    public void createMonitor() {
        destroyMonitor();
        // notice the monitor is given a reference to the runnableTask so it
        // can notify it when it has finished.
        monitor = new Monitor(sendTask, recTask, getReceivingNode());

        while (buffer.size() > 0) {
            send(buffer.get(0));
            buffer.remove(0);
        }
    }

    public void destroyMonitor() {
// todo FIX EMPTY
//        if (monitor != null) {
//            try {
//                monitor.empty();
//            } catch (Exception e) {
//            }
//        }
        if (monitor != null) {
            monitor.empty();
            monitor = null;
        }
    }

    /**
     * When the garbage collector de-allocates space from this object we reset all references to other object we have
     * used
     */
    protected void finalize() throws Throwable {
        super.finalize();
    }

    /**
     * @return true if the data is ready to be collected
     */
    public boolean isDataReady() {
        return monitor.isReady();
    }


    /**
     * Suspends the pipe. Any new data sent to the pipe should be ignored.
     */
    public void suspend() {
        suspended = true;
    }

    /**
     * Flushes the data on the pipe
     */
    public void flush() {
        buffer.clear();
        if (monitor != null) {
            monitor.empty();
        }
    }

    /**
     * Unsuspends a pipe
     */
    public void resume() {
        suspended = false;
    }


    /**
     * Called when one of a group node's parents changes
     */
    public void nodeParentChanged(NodeEvent event) {
        try {
            updateConnection();
        } catch (CableException except) {
            throw (new RuntimeException("Invalid node parent change"));
        }
    }

    /**
     * Called when one of a group node's child changes
     */
    public void nodeChildChanged(NodeEvent event) {
    }

    /**
     * Called when a node is connected to a cable.
     */
    public void nodeConnected(NodeEvent event) {
    }

    /**
     * Called before a node is diconnected from a cable.
     */
    public void nodeDisconnected(NodeEvent event) {
    }

    /**
     * Called when the name of the parameter the node is inputting/outputting is set.
     */
    public void parameterNameSet(NodeEvent event) {
    }

}

