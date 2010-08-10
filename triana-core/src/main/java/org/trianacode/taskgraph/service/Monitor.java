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

import org.trianacode.taskgraph.Node;


// for debug output

/**
 * This class is used by the LocalCable class to ensure that all the information that is being sent out of a sending
 * unit is being received by a receiving unit and visa-verse. It does this by using Java monitors to provide the
 * necessary synchronization needed between the sending and receiving units within the the Triana system. There is one
 * monitor set up for each connection (i.e. by using a GAPCable) made.
 *
 * @author Ian Taylor
 * @version $Revision: 4048 $
 * @see LocalCable
 */
public class Monitor implements Runnable, java.io.Serializable {

    /**
     * This make the monitor a thread.
     */
    private Thread threadAlive;

    private RunnableInstance sender = null;
    private RunnableInstance receiver = null;

    private Node node;

    /**
     * the data received by the get method
     */
    private Object data;

    /**
     * the data that is put into the monitor
     */
    private Object tempdata;

    private boolean available = false;
    private boolean waitingForData = false;
    private boolean blockingput = false;

    /**
     * Creates a new monitor for a particular connection name and a RunnableInstance object (i.e. a LocalCable).
     */
    public Monitor(RunnableInstance sn, RunnableInstance rn, Node innode) {
        this.sender = sn;
        this.receiver = rn;
        this.node = innode;
    }

    public Monitor(RunnableInstance sn) {
        this.sender = sn;
    }


    /**
     * The Sending unit calls this method when it has data which is ready to be sent to the receiving unit. The monitor
     * then waits until the unit is ready to receive the data before releasing the data
     */
    public synchronized void put(Object data) throws EmptyingException {
        if (blockingput) {
            throw (new RuntimeException("Monitor Error: Cannot put data on blocked monitior"));
        }

        blockingput = true;
        threadAlive = null;

        tempdata = data;

        block(true);
        release();

        blockingput = false;
    }


    /**
     * This puts the data on this connection but does not block the unit. Instead it puts it in the background and waits
     * in a separate thread
     */
    public void putButDontBlock(Object data) {
        if (blockingput) {
            throw (new RuntimeException("Monitor Error: Cannot put data on blocked monitor"));
        }

        this.tempdata = data;

        if (threadAlive == null) {
            threadAlive = new Thread(this, getClass().getName());
            threadAlive.setPriority(Thread.NORM_PRIORITY);
            threadAlive.start();
        }
    }

    /**
     * Blocks until the unit is ready to receivd the data/the data is sent
     */
    public synchronized void block(boolean put) throws EmptyingException {
        // monitor has been acquired by the Sender
        while (available == put) {
            try {     // wait until the data has been received before continuing
                wait();

                if (data instanceof EmptyingType) {
                    if (put) {
                        throw (new EmptyingException("Empty at monitor (put)"));
                    } else {
                        throw (new EmptyingException("Empty at monitor (get)"));
                    }
                }
            } catch (InterruptedException e) {
            }
        }
    }

    /**
     * Makes the tempdata available by releasing the monitor and waking up the receiving unit.
     */
    public synchronized void release() {
        data = tempdata;
        tempdata = null;

        available = true;
        myNotify();
        // notify the get method that data has been received

        if ((receiver != null) && (data != null)) {
            receiver.wakeUp(node.getTopLevelNode());
        }
    }

    /**
     * The Receiving unit calls this method when it wants to receive data. It then waits until it is <i>notified</i>
     * that the data is ready to be received.
     */
    public synchronized Object get() throws EmptyingException {
        waitingForData = true;

        block(false);

        waitingForData = false;
        available = false;
        myNotify(); // let's put method know that the data has been received

        if (sender != null) {
            sender.finished();
        } // lets the Cable know we've finished

        return data;
        // monitor is released by the Receiver
    }


    /**
     * Replaces the basic notifyAll fuction to keep a note of whether this monitor is being notified or released
     */
    public void myNotify() {
        notifyAll();
    }


    /**
     * Release the data to the receving task.
     */
    public void run() {
        block(true);

        if (threadAlive != null) {
            release();
        }

        threadAlive = null;
    }


    /**
     * Flushes the data out of the pipes
     */
    public synchronized void empty() {
        try {
            if (waitingForData) {
                put(new EmptyingType());
            } else if (blockingput) {
                data = new EmptyingType();
                get();
            }

            available = false;
            waitingForData = false;
            blockingput = false;
        } catch (EmptyingException except) {
            new EmptyingException("Error: Emptying excetpion received while emptying monitor").printStackTrace();
        }
    }


    /**
     * The Receiving unit calls this method when it wants to receive data which may or may not be there.  If it is
     * available then the unit returns true and then can go ahead and call the get() method to retrieve the date
     * otherwise it return false.  This method is used to implement the idea of an optional() connection within Triana
     * e.g. if an input node is marked optional then the unit can quite happily process its information without
     * contribution from this input i.e. the input does not block the unit.  However if data is ready at the input then
     * the unit can retrieve it and use it.
     * <p/>
     * see RunnableUnit#optional
     */
    public boolean isReady() {
        return available;
    }


    /**
     * When the garbage collector de-allocates space from this object we reset all references to other object we have
     * used
     */
    public void finalize() throws Throwable {
        super.finalize();
        empty();
        data = null;
        threadAlive = null;
    }

    private class EmptyingType {

    }
}














