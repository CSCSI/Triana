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


import org.trianacode.taskgraph.*;
import org.trianacode.taskgraph.clipin.ClipInBucket;
import org.trianacode.taskgraph.tool.Tool;

import java.util.ArrayList;

/**
 * An abstract service task is responsible for handling the wake-ups and output from a task. Sub classes for this class
 * are only responsible for implementing the invoke and flush methods.
 *
 * @author Ian Wang
 */


public abstract class AbstractServiceTask extends AbstractRunnableTask {

    /**
     * the default maximum number of concurrent invocations of the service
     */
    private static int DEFAULT_MAX_CONCURRENT_INVOCATIONS = 10;


    /**
     * an list of the nodes that wakeups have been received from
     */
    private ArrayList wakeups = new ArrayList();

    /**
     * a flag indicating whether multi-valued output is returned
     */
    private boolean multival = false;

    /**
     * the max concurrent invocations
     */
    private int maxconcurrent = DEFAULT_MAX_CONCURRENT_INVOCATIONS;

    /**
     * the current number of invocations
     */
    private int curinvocations;

    /**
     * a hashtable of threads waiting to output, keyed on sequence number (String)
     */
    private ArrayList outthreads = new ArrayList();


    /**
     * Constructs an abstract service task for the specified tool. Optionally returns multi-valued results (default is
     * one result copied to all nodes)
     *
     * @param multival true if return multi-valued results
     */
    public AbstractServiceTask(boolean multival, Tool tool, TaskFactory factory, boolean preserveinst)
            throws TaskException {
        super(tool, factory, preserveinst);
        this.multival = multival;
    }

    /**
     * Constructs an abstract service task for the specified tool.
     */
    public AbstractServiceTask(Tool tool, TaskFactory factory, boolean preserveinst) throws TaskException {
        super(tool, factory, preserveinst);
    }

    /**
     * Initialises the web service runnable task
     */
    public void init() {
        reset();
    }

    /**
     * @return true if input is required at every data input node, false if input only required at connected input
     *         nodes.
     */
    protected abstract boolean isInputEssential();

    /**
     * This method invokes the service with the specified input data. The return from this method is the output data
     * from the task.
     *
     * @param input  an array of the input data (indexed by node)
     * @param bucket clip-ins for this invoke
     * @return an array of the output data (indexed by node)
     */
    protected abstract Object[] invoke(Object[] input, ClipInBucket bucket) throws Exception;

    /**
     * Method called before data is received, should be over-ridden with pre receive fucntionality
     */
    protected void preReceive() {
    }

    /**
     * Method called after data is sent, should be over-ridden with post send fucntionality.
     */
    protected void postSend() {
    }


    /**
     * This method flushes the currently invoking services.
     */
    protected abstract void flush();


    /**
     * @return the maximum concurrent invocations for this service
     */
    public int getMaxConcurrentInvocations() {
        return maxconcurrent;
    }

    /**
     * Sets the maximum concurrent invocations for this service
     */
    protected void setMaxConcurrentInvocations(int maxconcurrent) {
        this.maxconcurrent = maxconcurrent;
    }

    /**
     * @return true if multi-value output are expected. If multi-value outputs are expected then each output value will
     *         be put on a seperate node, if not then the single value will be copied to every output node.
     */
    public boolean isMultiValuedOutput() {
        return multival;
    }


    /**
     * Tells the runnable instance (e.g. runnable task) to wake up if data has been received on all of its essential
     * nodes. The parameter indicates the node that received data to cause this wake-up.
     */
    public synchronized void wakeUp(Node node) {
        if (node.isDataNode() || ((node instanceof ParameterNode) && node.isEssential())) {
            if (!wakeups.contains(node)) {
                wakeups.add(node);
            }
        }

        wakeUp();
    }

    /**
     * Tells a runnable instance (e.g. runnable task) to wake-up if data has been received on all of its essential
     * nodes.
     */
    public synchronized void wakeUp() {
        if (wakeups.size() == getRequiredInputNodeCount()) {
            wakeups.clear();

            executionRequested();

            while ((curinvocations >= maxconcurrent)) {
                synchronized (this) {
                    try {
                        this.wait();
                    } catch (InterruptedException except) {
                    }
                }
            }

            System.out.println("INVOKING " + getToolName());

            curinvocations++;

            preReceive();
            receiveFromTriggers();

            final Task task = this;
            final ClipInBucket bucket = new ClipInBucket(task);
            final Object[] indata = getData(bucket);

            getProperties().getEngine().execute(new Runnable() {
                public void run() {
                    try {
                        try {
                            outthreads.add(Thread.currentThread());

                            executionStarting();

                            Object[] retvals = invoke(indata, bucket);

                            // after aspect

                            if ((retvals != null) && (retvals.length > 0)) {
                                output(retvals, bucket);
                            }

                            postSend();

                            System.out.println("FINISHED INVOKING " + getToolName());
                        } catch (EmptyingException except) {
                        } catch (NotifyErrorException except) {
                        } catch (Exception except) {
                            except.printStackTrace();
                            handleError(except, bucket);
                        }
                    } catch (NotifyErrorException except2) {
                    }

                    executionFinished();

                    synchronized (this) {
                        curinvocations--;
                        this.notifyAll();
                    }
                }
            });
        }
    }

    private void handleError(Exception except, ClipInBucket clipins) {
        ParameterNode[] outnodes = getParameterOutputNodes();
        Node errornode = null;

        for (int count = 0; count < outnodes.length; count++) {
            if (outnodes[count].isErrorNode()) {
                errornode = outnodes[count];
            }
        }

        if (errornode != null) {
            if (errornode.isConnected() && (errornode.getCable() instanceof OutputCable)) {
                if (clipins != null) {
                    ((OutputCable) errornode.getCable()).send(new DataMessage(except, clipins.extract(errornode)));
                } else {
                    ((OutputCable) errornode.getCable()).send(except);
                }
            }
        } else {
            notifyError(except.getMessage());
        }
    }


    /**
     * @return the number of connected data input nodes
     */
    private int getRequiredInputNodeCount() {
        int count = 0;

        if (isInputEssential()) {
            count = getDataInputNodeCount();
        } else {
            Node[] nodes = getDataInputNodes();

            for (int ptr = 0; ptr < nodes.length; ptr++) {
                if (nodes[ptr].isConnected()) {
                    count++;
                }
            }
        }

        ParameterNode[] paramnodes = getParameterInputNodes();

        for (int ptr = 0; ptr < paramnodes.length; ptr++) {
            if (paramnodes[ptr].isEssential()) {
                count++;
            }
        }

        return count;
    }

    /**
     * Receive data from trigger nodes
     */
    private void receiveFromTriggers() {
        Node node;

        for (int count = 0; count < getParameterInputNodeCount(); count++) {
            node = getParameterInputNode(count);

            if ((node.isConnected()) && (node.getCable() instanceof InputCable)) {
                ((InputCable) node.getCable()).recv();
            }
        }
    }

    /**
     * @return an array of the input data
     */
    private Object[] getData(ClipInBucket bucket) {
        Object[] data = new Object[getDataInputNodeCount()];
        Object recdata;
        Node node;

        for (int count = 0; (count < data.length) && (count < getDataInputNodeCount()); count++) {
            node = getDataInputNode(count);

            if ((node.isConnected()) && (node.getCable() instanceof InputCable)) {
                recdata = ((InputCable) node.getCable()).recv();

                if (recdata instanceof DataMessage) {
                    if (((DataMessage) recdata).hasClipIns()) {
                        ClipInBucket messclips = ((DataMessage) recdata).getClipIns();
                        bucket.insert(messclips, node);
                    }

                    data[count] = ((DataMessage) recdata).getData();
                } else {
                    data[count] = recdata;
                }
            }
        }

        return data;
    }

    /**
     * Outputs the return data to the cables
     */
    private void output(Object[] data, ClipInBucket clipins) {
        waitSequence();

        try {
            if (multival) {
                outputMultiVal(data, clipins);
            } else if (data.length > 0) {
                outputSingle(data[0], clipins);
            }
        } catch (RuntimeException except) {
            nextSequence();
            throw (except);
        }

        nextSequence();
    }

    /**
     * Waits until this thread is the next to output
     */
    private void waitSequence() {
        waitPause();

        while (outthreads.get(0) != Thread.currentThread()) {
            try {
                Thread.sleep(Long.MAX_VALUE);
            } catch (InterruptedException except) {
            }
        }
    }

    /**
     * Interrupts the next thread in the sequence
     */
    private void nextSequence() {
        outthreads.remove(0);

        if (!outthreads.isEmpty()) {
            ((Thread) outthreads.get(0)).interrupt();
        }
    }


    private void outputSingle(Object data, ClipInBucket clipins) {
        for (int count = 0; count < getDataOutputNodeCount(); count++) {
            Node node = getDataOutputNode(count);

            if (node.isConnected() && (node.getCable() instanceof OutputCable)) {
                if (clipins != null) {
                    ((OutputCable) node.getCable()).send(new DataMessage(data, clipins.extract(node)));
                } else {
                    ((OutputCable) node.getCable()).send(data);
                }
            }
        }
    }

    private void outputMultiVal(Object[] data, ClipInBucket clipins) {
        for (int count = 0; (count < getDataOutputNodeCount()) && (count < data.length); count++) {
            Node node = getDataOutputNode(count);

            if (node.isConnected() && (node.getCable() instanceof OutputCable)) {
                if (clipins != null) {
                    ((OutputCable) node.getCable()).send(new DataMessage(data[count], clipins.extract(node)));
                } else {
                    ((OutputCable) node.getCable()).send(data[count]);
                }
            }
        }
    }

    /**
     * Tell the data monitor that this thread monitor has completed outputting the data i.e. the data has been received
     * by the receiving process.
     */
    public void finished() {
    }


    /**
     * Order a runnable instance to stop and reset it to its pre-run state
     */
    public synchronized void reset() {
        super.reset();

        wakeups.clear();

        Thread thread = new Thread() {
            public void run() {
                flush();

                while (curinvocations > 0) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException except) {
                    }
                }

                executionReset();
            }
        };

        thread.setName("ServiceFlushThread");
        thread.start();
    }

}
