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
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.TaskGraphException;
import org.trianacode.taskgraph.tool.Tool;

import java.util.ArrayList;

/**
 * A class for executing a Triana taskgraph. TrianaExec assumes that each
 * taskgraph invocation will correspond with a single set of output data, and
 * thereby synchronizes multiple invocations.
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 * @created
 * @date $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */

public class TrianaExec {

    /**
     * the time in milliseconds that the manager threads sleep when waiting for the
     * previous data to be sent/the data to arrive.
     */
    public static long DATA_SLEEP_TIME = 500;


    private TrianaRun run;

    private ExecInputManager inmanager;
    private ExecOutputManager outmanager;


    /**
     * Constructs a TrianaExec to execute a clone of the specified taskgraph.
     * Uses a default tool table
     */
    public TrianaExec(TaskGraph taskgraph) throws TaskGraphException {
        run = new TrianaRun(taskgraph);
        initExecManagers();
    }

    /**
     * Constructs a TrianaExec to execute a clone of the specified tool.
     * Uses a default tool table.
     */
    public TrianaExec(Tool tool) throws TaskGraphException {
        run = new TrianaRun(tool);
        initExecManagers();
    }

    /**
     * Start the exec manager threads
     */
    private void initExecManagers() {
        outmanager = new ExecOutputManager();
        inmanager = new ExecInputManager(outmanager);

        outmanager.start();
        inmanager.start();
    }


    /**
     * @return the taskgraph that is being executed. If a single task was executed
     *         then this returns that task wrapped in a simple taskgraph.
     */
    public TaskGraph getTaskGraph() {
        return run.getTaskGraph();
    }

    /**
     * @return the task that is being executed.
     */
    public Task getTask() {
        return run.getTask();
    }


    /**
     * @return the dummy tool name
     */
    public String getDummyToolName() {
        return run.getDummyToolName();
    }

    /**
     * Sets the dummy tool name
     */
    public void setDummyToolName(String dummyname) {
        run.setDummyToolName(dummyname);
    }


    /**
     * @return true if the taskgraph has finished executing
     */
    public boolean isFinished() {
        return run.isFinished();
    }


    /**
     * Executes the taskgraph using the specified input data array. Automatically
     * unpacks any result data contained in data messages.
     */
    public Object[] run(Object[] data) throws TaskGraphException, SchedulerException {
        return run(data, true);
    }

    /**
     * Executes the taskgraph using the specified input data array.
     *
     * @param unpack sets whether data within data messages is automatically
     *               unpacked in the results array
     */
    public Object[] run(Object[] data, boolean unpack) throws TaskGraphException, SchedulerException {
        ExecRun exec = new ExecRun(data, Thread.currentThread(), unpack);
        inmanager.addExecRun(exec);

        while (!exec.isOutputData()) {
            try {
                Thread.sleep(Long.MAX_VALUE);
            } catch (InterruptedException except) {
            }
        }

        return exec.getOutputData();
    }


    /**
     * Dispose of the TrianaExec and clean up the threads
     */
    public void dispose() {
        inmanager.stopThread();
        outmanager.stopThread();
        run.dispose();
    }


    protected class ExecRun {

        private Object[] indata;
        private Object[] outdata = null;
        private Thread thread;

        private boolean unpack;


        public ExecRun(Object[] indata, Thread thread, boolean unpack) {
            this.indata = indata;
            this.thread = thread;
            this.unpack = unpack;
        }


        /**
         * @return true if the data should be automatically unpacked
         */
        public boolean unpackData() {
            return unpack;
        }


        /**
         * @return the input data
         */
        public Object[] getInputData() {
            return indata;
        }

        /**
         * @return true if the output data is available
         */
        public boolean isOutputData() {
            return outdata != null;
        }

        /**
         * Sets the output data and interupts the waiting thread
         */
        public void setOutputData(Object[] outdata) {
            this.outdata = outdata;
            thread.interrupt();
        }

        /**
         * @return the output data
         */
        public Object[] getOutputData() {
            return outdata;
        }

    }

    private class ExecInputManager extends Thread {

        private ArrayList inexec = new ArrayList();
        private ExecOutputManager outmanager;

        private boolean stopped = false;


        public ExecInputManager(ExecOutputManager outmanager) {
            setName("TrianaExecInputManager");

            this.outmanager = outmanager;
        }


        public void addExecRun(ExecRun run) {
            synchronized (this) {
                inexec.add(run);
                notifyAll();
            }
        }


        public boolean isStopped() {
            return stopped;
        }

        public void stopThread() {
            synchronized (this) {
                this.stopped = true;
                this.notifyAll();
            }
        }

        public void run() {
            while (!stopped) {
                try {
                    if (!inexec.isEmpty()) {
                        waitReady();
                        handleRun();
                    } else {
                        synchronized (this) {
                            if (!stopped) {
                                try {
                                    this.wait();
                                } catch (InterruptedException except) {
                                }
                            }
                        }
                    }
                } catch (SchedulerException except) {
                    except.printStackTrace();
                }
            }

            inexec.clear();
            outmanager = null;
        }

        private void waitReady() {
            boolean ready = false;

            do {
                ready = true;

                for (int count = 0; (count < run.getInputNodeCount()) && (ready); count++)
                    ready = ready && run.isDataSent(count);

                if (!ready)
                    sleepThread(DATA_SLEEP_TIME);
            } while ((!ready) && (!stopped));
        }

        private void handleRun() throws SchedulerException {
            if (!stopped) {
                ExecRun exec = (ExecRun) inexec.remove(0);
                Object[] indata = exec.getInputData();

                outmanager.addExecRun(exec);
                run.runTaskGraph();

                for (int count = 0; (count < run.getOutputNodeCount()) && (count < indata.length); count++)
                    run.sendInputData(count, indata[count]);
            }
        }

        private void sleepThread(long length) {
            synchronized (this) {
                try {
                    if (!stopped)
                        this.wait(length);
                } catch (InterruptedException e) {
                }
            }
        }

    }

    private class ExecOutputManager extends Thread {

        private ArrayList outexec = new ArrayList();

        private boolean stopped = false;


        public ExecOutputManager() {
            setName("TrianaExecOutputManager");
        }


        public void addExecRun(ExecRun run) {
            synchronized (this) {
                outexec.add(run);
                notifyAll();
            }
        }

        public boolean isStopped() {
            return stopped;
        }

        public synchronized void stopThread() {
            synchronized (this) {
                this.stopped = true;
                this.notifyAll();
            }
        }


        public void run() {
            while (!stopped) {
                if (!outexec.isEmpty()) {
                    waitReady();
                    handleOutput();
                } else {
                    synchronized (this) {
                        if (!stopped) {
                            try {
                                this.wait();
                            } catch (InterruptedException except) {
                            }
                        }
                    }
                }
            }

            outexec.clear();
        }

        private void waitReady() {
            boolean ready = false;

            do {
                ready = true;

                for (int count = 0; (count < run.getOutputNodeCount()) && (ready); count++)
                    ready = ready && run.isOutputReady(count);

                if (!ready)
                    sleepThread(DATA_SLEEP_TIME);
            } while ((!ready) && (!stopped));
        }


        private void handleOutput() {
            if (!stopped) {
                ExecRun exec = (ExecRun) outexec.remove(0);
                Object[] outdata = new Object[run.getOutputNodeCount()];

                for (int count = 0; count < outdata.length; count++)
                    outdata[count] = run.receiveOutputData(count, exec.unpackData());

                exec.setOutputData(outdata);
            }
        }

        private void sleepThread(long length) {
            synchronized (this) {
                try {
                    if (!stopped)
                        this.wait(length);
                } catch (InterruptedException e) {
                }
            }
        }

    }

}
