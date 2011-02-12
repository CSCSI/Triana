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
import org.trianacode.taskgraph.imp.TaskImp;
import org.trianacode.taskgraph.tool.Tool;

import java.util.HashSet;

/**
 * An abstract runnable task. Responsible for providing common functionality to runnable tasks, such as execution state
 * listeners.
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 */

public abstract class AbstractRunnableTask extends TaskImp implements RunnableInstance {

    /**
     * A thread pool for every running unit
     */
    protected static ThreadPool pool = new ThreadPool(Integer.MAX_VALUE);


    /**
     * a list of the execution listeners for this task
     */
    protected HashSet execlisteners = new HashSet();

    /**
     * The current state of the underlying unit.
     */
    private ExecutionState execState = ExecutionState.NOT_INITIALIZED;

    /**
     * a count of the number of execution requests
     */
    private int executionRequest = 0;

    /**
     * a count of the number of completed executions
     */
    private int executionCount = 0;


    public AbstractRunnableTask(Tool tool, TaskFactory factory, boolean preserveinst) throws TaskException {
        super(tool, factory, preserveinst);
    }

    public void setParent(TaskGraph taskgraph) {
        super.setParent(taskgraph);
        if (taskgraph != null) {
            addExecutionListener(taskgraph);
        }
    }


    /**
     * Adds a execution listener to this runnable instance
     */
    public synchronized void addExecutionListener(ExecutionListener listener) {
        boolean added = execlisteners.add(listener);
    }

    /**
     * Removes a execution listener from this runnable instance
     */
    public synchronized void removeExecutionListener(ExecutionListener listener) {
        execlisteners.remove(listener);
    }


    /**
     * Thread safe set method for state.
     */
    private void setExecutionState(ExecutionState newstate) {
        ExecutionState oldstate = this.execState;

        if (!oldstate.equals(newstate)) {
            synchronized (ExecutionState.LOCK) {
                this.execState = newstate;
                setParameterType(EXECUTION_STATE, TRANSIENT);
                setParameter(EXECUTION_STATE, execState);
            }

            notifyExecutionStateChange(newstate, oldstate);

            if (!execState.equals(ExecutionState.ERROR)) {
                removeParameter(ERROR_MESSAGE);
            }
        }
    }

    /**
     * @return the current state of the runnable instance (e.g. runnable task)
     */
    public ExecutionState getExecutionState() {
        return execState;
    }


    /**
     * Called by subclass when execution is requested
     */
    protected void executionRequested() {
        System.out.println("AbstractRunnableTask.executionRequested EXECUTION REQUESTED");
        waitPause();
        System.out.println("AbstractRunnableTask.executionRequested DONE WITH WAIT PAUSE");

        executionRequest++;
        setParameterType(EXECUTION_REQUEST_COUNT, TRANSIENT);
        setParameter(EXECUTION_REQUEST_COUNT, String.valueOf(executionRequest));

        synchronized (ExecutionState.LOCK) {
            if ((getExecutionState() != ExecutionState.RUNNING) && (getExecutionState() != ExecutionState.RESETTING)) {
                setExecutionState(ExecutionState.SCHEDULED);
            }
        }

        notifyExecutionRequest();
    }

    /**
     * Called by subclass when execution is starting
     */
    protected void executionStarting() {
        waitPause();

        synchronized (ExecutionState.LOCK) {
            if (getExecutionState() != ExecutionState.RESETTING) {
                setExecutionState(ExecutionState.RUNNING);
            }
        }

        notifyExecutionStarting();
    }

    /**
     * Called by subclass when execution is finished
     */
    protected void executionFinished() {
        waitPause();

        executionCount++;
        setParameterType(EXECUTION_COUNT, TRANSIENT);
        setParameter(EXECUTION_COUNT, String.valueOf(executionCount));

        synchronized (ExecutionState.LOCK) {
            if ((getExecutionState() != ExecutionState.RESETTING) && (getExecutionState() != ExecutionState.ERROR)) {
                if (getExecutionRequestCount() == getExecutionCount()) {
                    setExecutionState(ExecutionState.COMPLETE);
                } else {
                    setExecutionState(ExecutionState.SCHEDULED);
                }
            }
        }

        notifyExecutionFinished();
    }

    /**
     * Called by subclass when execution is reset
     */
    protected void executionReset() {
        waitPause();

        setParameterType(EXECUTION_REQUEST_COUNT, Tool.TRANSIENT_ACCESSIBLE);
        setParameterType(EXECUTION_COUNT, Tool.TRANSIENT_ACCESSIBLE);

        executionCount = 0;
        executionRequest = 0;

        setParameter(EXECUTION_REQUEST_COUNT, String.valueOf(executionCount));
        setParameter(EXECUTION_COUNT, String.valueOf(executionRequest));

        setExecutionState(ExecutionState.RESET);

        notifyExecutionReset();
    }


    /**
     * Indicates to the runnable instance that a wake-up signal has been received from the scheduler. It is up to a task
     * to respond to this wake-up, or to ignore it if wake-ups have not been received from all the nodes a task requires
     * to execute.
     */
    public abstract void wakeUp();

    /**
     * Indicates to the runnable instance that a wake-up signal has been received from the specified node (e.g. data is
     * available on that node). A runnable instance should only run when it has received wake-ups from all the nodes it
     * requires to execute.
     */
    public abstract void wakeUp(Node node);


    /**
     * Request a runnable instance to pause
     */
    public void pause() {
        synchronized (ExecutionState.LOCK) {
            if (getExecutionState() == ExecutionState.RUNNING) {
                setExecutionState(ExecutionState.PAUSED);
            }
        }
    }

    /**
     * Request a runnable instance to resume (unpause)
     */
    public void resume() {
        synchronized (ExecutionState.LOCK) {
            if (getExecutionState() == ExecutionState.PAUSED) {
                setExecutionState(ExecutionState.RUNNING);
            }
        }
    }


    /**
     * Order a runnable instance to stop and reset it to its pre-run state
     */
    public void reset() {
        synchronized (ExecutionState.LOCK) {
            if ((getExecutionState() != ExecutionState.RESET) &&
                    (getExecutionState() != ExecutionState.NOT_EXECUTABLE) &&
                    (getExecutionState() != ExecutionState.UNKNOWN)) {
                setExecutionState(ExecutionState.RESETTING);
            }
        }
    }

    /**
     * Tell the data monitor that this thread monitor has completed outputting the data i.e. the data has been received
     * by the receiving process.
     */
    public abstract void finished();

    /**
     * waits until the task is unpaused
     */
    protected void waitPause() {
        while (getExecutionState().equals(ExecutionState.PAUSED)) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException except) {
            }
        }
    }


    /**
     * Stops the scheduler. This maybe needed if your unit produced some exception and you may want the algorithm to
     * stop and ask the user to sort the problem out before resuming.
     */
    public void notifyError(String message) throws NotifyErrorException {
        TaskGraphManager.getTrianaServer(getParent()).notifyError(this, message);
        setExecutionState(ExecutionState.ERROR);

        if (message != null) {
            setParameterType(ERROR_MESSAGE, Tool.TRANSIENT);
            setParameter(ERROR_MESSAGE, message);
        }

        throw (new NotifyErrorException(message));
    }


    private synchronized void notifyExecutionRequest() {
        ExecutionListener[] listeners = (ExecutionListener[]) execlisteners
                .toArray(new ExecutionListener[execlisteners.size()]);
        ExecutionEvent event = new ExecutionEvent(ExecutionState.SCHEDULED, this);

        for (int count = 0; count < listeners.length; count++) {
            listeners[count].executionRequested(event);
        }
    }

    private synchronized void notifyExecutionStarting() {
        ExecutionListener[] listeners = (ExecutionListener[]) execlisteners
                .toArray(new ExecutionListener[execlisteners.size()]);
        ExecutionEvent event = new ExecutionEvent(ExecutionState.RUNNING, this);

        for (int count = 0; count < listeners.length; count++) {
            listeners[count].executionStarting(event);
        }
    }

    private synchronized void notifyExecutionFinished() {
        ExecutionListener[] listeners = (ExecutionListener[]) execlisteners
                .toArray(new ExecutionListener[execlisteners.size()]);
        ExecutionEvent event = new ExecutionEvent(ExecutionState.COMPLETE, this);

        for (int count = 0; count < listeners.length; count++) {
            listeners[count].executionFinished(event);
        }
    }

    private synchronized void notifyExecutionReset() {
        ExecutionListener[] listeners = (ExecutionListener[]) execlisteners
                .toArray(new ExecutionListener[execlisteners.size()]);
        ExecutionEvent event = new ExecutionEvent(ExecutionState.RESET, this);

        for (int count = 0; count < listeners.length; count++) {
            listeners[count].executionReset(event);
        }
    }

    private synchronized void notifyExecutionStateChange(ExecutionState newstate, ExecutionState oldstate) {
        ExecutionListener[] listeners = (ExecutionListener[]) execlisteners
                .toArray(new ExecutionListener[execlisteners.size()]);
        ExecutionEvent event = new ExecutionEvent(newstate, oldstate, this);

        for (int count = 0; count < listeners.length; count++) {
            listeners[count].executionStateChanged(event);
        }
    }


    /**
     * cleans up any operations associated with this task
     */
    public void dispose() {
        setExecutionState(ExecutionState.NOT_EXECUTABLE);

        super.dispose();
    }
}
