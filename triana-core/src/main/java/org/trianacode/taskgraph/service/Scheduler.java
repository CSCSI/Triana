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

import org.trianacode.enactment.logging.ExecutionStateLogger;
import org.trianacode.taskgraph.*;
import org.trianacode.taskgraph.clipin.HistoryClipIn;

/**
 * The scheduler is responsible for waking-up all the input tasks in a taskgraph when the taskgraph is run.
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 */
public class Scheduler implements SchedulerInterface {

    private ExecutionState tgState = ExecutionState.RESET;


    /**
     * The taskgraph which this scheduler runs.
     */
    private TaskGraph taskgraph;

    /**
     * The history clipin
     */
    private HistoryClipIn history;

    private ExecutionStateLogger logger = new ExecutionStateLogger();


    /**
     * Construct a scheduler for the given taskgraph and registers it with all the tasks in the taskgraph
     */
    public Scheduler(TaskGraph taskgraph) {
        this.taskgraph = taskgraph;
    }


    /**
     * @return the taskgraph state
     */
    public ExecutionState getExecutionState() {
        return tgState;
    }


    /**
     * Runs the main taskgraph
     */
    public void runTaskGraph() throws SchedulerException {
        runTaskGraph((HistoryClipIn) null);
    }

    /**
     * Runs the taskgraph by waking up all the input tasks. Attaches the specified object as a history clip-in
     */
    public void runTaskGraph(HistoryClipIn history) throws SchedulerException {
        this.history = history;

        if (tgState == ExecutionState.ERROR) {
            throw (new SchedulerException("Run Error: Taskgraph is in an error state (try reseting)"));
        } else if (isSuspendedTasks(taskgraph)) {
            throw (new SchedulerException("Run Error: Taskgraph contains suspended tasks"));
        } else if (tgState == ExecutionState.PAUSED) {
            resumeTaskGraph(taskgraph);
        } else {
            runTaskGraph(taskgraph);
        }
    }


    /**
     * Pauses the taskgraph
     */
    public void pauseTaskGraph() {
        pauseTaskGraph(taskgraph);
    }

    /**
     * Continue after a pause.
     */
    public void resumeTaskGraph() {
        resumeTaskGraph(taskgraph);
    }

    /**
     * Halt the taskgraph and reset all units to their start values.
     */
    public void resetTaskGraph() {
        resetTaskGraph(taskgraph);
    }


    /**
     * Flushes all data from the task graph cables
     */
    public void flushTaskGraph() {
        pauseTaskGraph();

        Cable[] cables = TaskGraphUtils.getConnectedCables(TaskGraphUtils.getAllTasksRecursive(taskgraph, true));

        for (int i = 0; i < cables.length; i++) {
            if (cables[i] instanceof InputCable) {
                ((InputCable) cables[i]).suspend();
            } else if (cables[i] instanceof OutputCable) {
                ((OutputCable) cables[i]).suspend();
            }
        }

        // todo flush the units

        for (int i = 0; i < cables.length; i++) {
            if (cables[i] instanceof InputCable) {
                ((InputCable) cables[i]).resume();
            } else if (cables[i] instanceof OutputCable) {
                ((OutputCable) cables[i]).resume();
            }
        }
    }


    /**
     * Runs the specified task (within a running taskgraph)
     */
    public void runTask(Task task) throws SchedulerException {
        if (tgState == ExecutionState.RUNNING) {
            wakeTask(task);
        } else {
            throw (new SchedulerException("Cannot run task " + task.getToolName() + ": Execution state = " + tgState));
        }
    }


    /**
     * @return true if any tasks within the taskgraph are suspended
     */
    private boolean isSuspendedTasks(TaskGraph taskgraph) {
        Task[] tasks = TaskGraphUtils.getAllTasksRecursive(taskgraph, true);
        boolean suspended = false;

        for (int count = 0; (count < tasks.length) && (!suspended); count++) {
            if ((tasks[count] instanceof RunnableInstance)) {
                suspended = suspended || (((RunnableInstance) tasks[count]).getExecutionState()
                        == ExecutionState.SUSPENDED);
            }
        }

        return suspended;
    }


    /**
     * Called to notify an error has occured in the taskgraph
     *
     * @param cause the cause of the error
     */
    public void notifyError(RunnableInstance cause) {
        stopTaskGraph(taskgraph);
        //taskgraph.removeExecutionListener(logger);
    }

    @Override
    public void addExecutionListener(ExecutionListener listener) {
        taskgraph.addExecutionListener(listener);
    }

    @Override
    public void removeExecutionListener(ExecutionListener listener) {
        taskgraph.removeExecutionListener(listener);
    }


    /**
     * Wakes up the specfied task within a running taskgraph
     */
    public void wakeTask(Task task) {
        if (tgState == ExecutionState.RUNNING) {
            if (task instanceof RunnableInstance) {
                attachHistoryClipIn(task);
                ((RunnableInstance) task).wakeUp();
            } else if (task instanceof TaskGraph) {
                TaskGraph tgraph = ((TaskGraph) task);

                if (tgraph.isControlTask() && tgraph.isControlTaskConnected()) {
                    wakeTask(tgraph.getControlTask());
                } else {
                    Task[] tasks = tgraph.getTasks(false);

                    for (int count = 0; count < tasks.length; count++) {
                        wakeTask(tasks[count]);
                    }
                }
            }
        }
    }

    /**
     * Attaches history to input tasks
     */
    private void attachHistoryClipIn(Task task) {
        if ((history != null) && (task.getDataInputNodeCount() == 0)) {
            if (task instanceof ClipableTaskInterface) {
                ((ClipableTaskInterface) task).queueClipIn(HistoryClipIn.HISTORY_CLIPIN_NAME, history.clone());
            } else {
                throw (new RuntimeException("Scheduler Error: Cannot attach history clip-in to " + task.getToolName()));
            }
        }
    }


    /**
     * Send all tasks in the task graph a wake up call (except the control task)
     */
    private void runTaskGraph(TaskGraph tgraph) {
        if ((tgState != ExecutionState.ERROR) && (tgState != ExecutionState.RESETTING)) {
            tgState = ExecutionState.RUNNING;

            wakeTask(tgraph);
        }
    }

    /**
     * Pauses all tasks
     */
    private void pauseTaskGraph(TaskGraph tgraph) {
        if (tgState.equals(ExecutionState.RUNNING)) {
            tgState = ExecutionState.PAUSED;
            Task[] tasks = TaskGraphUtils.getAllTasksRecursive(tgraph, true);

            for (int count = 0; count < tasks.length; count++) {
                if ((tasks[count] instanceof RunnableInstance)) {
                    ((RunnableInstance) tasks[count]).pause();
                }
            }
        }
    }

    /**
     * Sends a wake-up call to all tasks except tasks with zero input nodes (and tasks that are running continuously).
     * This resumes paused tasks
     */
    private void resumeTaskGraph(TaskGraph tgraph) {
        if (tgState.equals(ExecutionState.PAUSED)) {
            tgState = ExecutionState.RUNNING;
            Task[] tasks = TaskGraphUtils.getAllTasksRecursive(tgraph, true);

            for (int count = 0; count < tasks.length; count++) {
                if (tasks[count] instanceof RunnableInstance) {
                    ((RunnableInstance) tasks[count]).resume();
                }
            }
        }
    }

    /**
     * Reset all tasks and flushes the taskgraph
     */
    private void resetTaskGraph(final TaskGraph tgraph) {
        if (tgState != ExecutionState.RESETTING) {
            tgState = ExecutionState.RESETTING;

            Runnable runnable = new Runnable() {
                public void run() {
                    Task[] tasks = TaskGraphUtils.getAllTasksRecursive(tgraph, true);
                    Cable[] cables = TaskGraphUtils
                            .getConnectedCables(TaskGraphUtils.getAllTasksRecursive(taskgraph, true));

                    for (int i = 0; i < cables.length; i++) {
                        if (cables[i] instanceof OutputCable) {
                            ((OutputCable) cables[i]).suspend();
                        } else if (cables[i] instanceof InputCable) {
                            ((InputCable) cables[i]).suspend();
                        }
                    }

                    for (int count = 0; count < tasks.length; count++) {
                        if ((tasks[count] instanceof RunnableInstance)) {
                            ((RunnableInstance) tasks[count]).reset();
                        }
                    }

                    for (int i = 0; i < cables.length; i++) {
                        if (cables[i] instanceof OutputCable) {
                            ((OutputCable) cables[i]).flush();
                        } else if (cables[i] instanceof InputCable) {
                            ((InputCable) cables[i]).flush();
                        }
                    }

                    boolean reset;

                    do {
                        reset = true;

                        for (int count = 0; count < tasks.length; count++) {
                            if ((tasks[count].getExecutionState() != ExecutionState.RESET) &&
                                    (tasks[count].getExecutionState() != ExecutionState.NOT_EXECUTABLE) &&
                                    (tasks[count].getExecutionState() != ExecutionState.UNKNOWN)) {
                                reset = false;
                            }
                        }

                        if (!reset) {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException except) {
                            }
                        }
                    } while (!reset);

                    for (int i = 0; i < cables.length; i++) {
                        if (cables[i] instanceof OutputCable) {
                            ((OutputCable) cables[i]).resume();
                        } else if (cables[i] instanceof InputCable) {
                            ((InputCable) cables[i]).resume();
                        }
                    }
                    tgState = ExecutionState.RESET;
                }
            };

            tgraph.getProperties().getEngine().execute(runnable);
        }
    }

    /**
     * Stops all tasks for an error
     */
    private void stopTaskGraph(TaskGraph tgraph) {
        tgState = ExecutionState.ERROR;
        Task[] tasks = TaskGraphUtils.getAllTasksRecursive(tgraph, true);

        for (int count = 0; count < tasks.length; count++) {
            if ((tasks[count] instanceof RunnableInstance)) {
                ((RunnableInstance) tasks[count]).pause();
            }
        }

    }

}
