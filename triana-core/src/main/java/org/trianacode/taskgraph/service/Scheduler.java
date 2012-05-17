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

import org.trianacode.enactment.logging.stampede.StampedeLog;
import org.trianacode.taskgraph.*;
import org.trianacode.taskgraph.clipin.HistoryClipIn;

import java.util.HashMap;
import java.util.UUID;

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

//    private UUID runUUID;
//    private UUID parentUUID = null;

    //    public RuntimeFileLog runtimeFileLog;
//    private HashMap<Task, Integer> taskMap; // = new HashMap<Task, Integer>();
//    private HashMap<Task, Integer> stateChanges;
    public StampedeLog stampedeLog;
    private HashMap<String, String> executionProperties;
//    private int sched_id_count;
//    private ExecutionListener executionLogger = new ExecutionLogger(stampedeLog);
//    private long startTime;


    //    private ExecutionStateLogger logger = new ExecutionStateLogger();


    /**
     * Construct a scheduler for the given taskgraph and registers it with all the tasks in the taskgraph
     *
     * @param taskgraph
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
    public void notifyError(RunnableInstance cause, String message) {
        stopTaskGraph(taskgraph);
        if (stampedeLog.isSubWorkflow()) {
            try {
                Thread.sleep(3000);
                stampedeLog.complete();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void addExecutionListener(ExecutionListener listener) {
        taskgraph.addExecutionListener(listener);
    }

    @Override
    public void removeExecutionListener(ExecutionListener listener) {
        taskgraph.removeExecutionListener(listener);
    }

//    @Override
//    public void setParentUUID(UUID uuid) {
//        this.parentUUID = uuid;
//    }
//
//    public UUID getRunUUID() {
//        return runUUID;
//    }

    @Override
    public void setExecutionProperties(HashMap<String, String> executionProperties) {
        this.executionProperties = executionProperties;
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

//        startTime = new Date().getTime() / 1000;
//        runUUID = UUID.randomUUID();
//        if(parentUUID == null){
//            parentUUID = runUUID;
//        }


        //A new unique identifier for this specific execution
        UUID runUUID = UUID.randomUUID();

        //if there is no stampede logger yet, add one. Otherwise change the current one to this uuid
        if (stampedeLog == null) {
            stampedeLog = new StampedeLog(taskgraph, runUUID);
        } else {
            stampedeLog.reset(runUUID);
//            stampedeLog.setRunUUID(runUUID);
        }

        if (executionProperties != null) {
            stampedeLog.initExecutionProperties(executionProperties);
        }


        stampedeLog.logPlanEvent(taskgraph);
        //if there is no parent ID, then this taskgraph is a root graph, and so should be planned/ mapped.
        //otherwise, assume the parent taskgraph has already been planned/ mapped.
//        if (parentUUID != null) {
//            stampedeLog.logSubWf(parentUUID, runUUID, jobID, jobInstID);
//        }

//        stampedeLog.reset();

//        runtimeFileLog = new RuntimeFileLog(
//                tgraph.getProperties().getProperty(TrianaProperties.LOG_LOCATION), runUUID);
//        taskMap = new HashMap<Task, Integer>();
//        stateChanges = new HashMap<Task, Integer>();
//        sched_id_count = 0;

//        StampedeEvent planEvent = new StampedeEvent(LogDetail.PLAN)
//                .add(LogDetail.ROOT_WF, parentUUID.toString())
//                .add(LogDetail.PLANNER_VERSION, (String) tgraph.getParameter(TrianaProperties.VERSION))
//                .add(LogDetail.SUBMIT_DIR, System.getProperty("user.dir"))
//                .add(LogDetail.USERNAME, System.getProperty("user.name"))
//                .add(LogDetail.EXECUTION_FILE_NAME, "exec:" + tgraph.getToolName())
//                .add(LogDetail.SUBMIT_HOSTNAME, getHostname())
//                .add(LogDetail.WF_VERSION, "1")
//                .add(LogDetail.DESCRIPTION_FILE, tgraph.getToolName())
//                .add(LogDetail.ARGS, "\"\"");
//        addBaseEventDetails(planEvent);
//        logStampedeEvent(planEvent);

//        logGraph(tgraph);

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

    /**
     * Wakes up the specfied task within a running taskgraph
     */
    public void wakeTask(Task task) {

//        sched_id_count++;
//        stampedeLog.taskMap.put(task, sched_id_count);
        stampedeLog.wakeTask(task);
//        StampedeEvent wakeStartEvent = new StampedeEvent(LogDetail.START_WAKING_TASK);
//        addSchedJobInstDetails(wakeStartEvent, task);
//        logStampedeEvent(wakeStartEvent);

        if (tgState == ExecutionState.RUNNING) {
            if (task instanceof RunnableInstance) {
                attachHistoryClipIn(task);
                ((RunnableInstance) task).wakeUp();

            } else if (task instanceof TaskGraph) {
                TaskGraph tgraph = ((TaskGraph) task);
                tgraph.addExecutionListener(stampedeLog.getExecutionLogger());

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

//    private void logGraph(TaskGraph taskgraph) {
//        //Task INFO
//
//        logStampedeEvent(addBaseEventDetails(new StampedeEvent(LogDetail.TASK_INFO)
//                .add(LogDetail.TYPE_DESC, "compute")
//                .add(LogDetail.TASK_ID, "task:" + taskgraph.getQualifiedTaskName())
//                .add(LogDetail.ARGS, "\"\""))
//                .add(LogDetail.TYPE, "1")
//                .add(LogDetail.TRANSFORMATION, taskgraph.getQualifiedToolName())
//        );
//
//        for (Task task : TaskGraphUtils.getAllTasksRecursive(taskgraph, false)) {
//
//            logStampedeEvent(addBaseEventDetails(new StampedeEvent(LogDetail.TASK_INFO)
//                    .add(LogDetail.TYPE_DESC, "compute")
//                    .add(LogDetail.TASK_ID, "task:" + task.getQualifiedTaskName())
//                    .add(LogDetail.ARGS, getTaskArgs(task))
//                    .add(LogDetail.TYPE, "1")
//                    .add(LogDetail.TRANSFORMATION, task.getQualifiedToolName()))
//            );
//        }
//
//        for (Cable cable : TaskGraphUtils.getConnectedCables(TaskGraphUtils.getAllTasksRecursive(taskgraph, false))) {
//            logStampedeEvent(addBaseEventDetails(new StampedeEvent(LogDetail.TASK_CABLES)
//                    .add(LogDetail.CHILD_TASK, cable.getReceivingTask().getQualifiedTaskName())
//                    .add(LogDetail.PARENT_TASK, cable.getSendingTask().getQualifiedTaskName()))
//            );
//        }
//
//        //"Job" (Unit) Info
//
//        logStampedeEvent(addBaseEventDetails(
//                new StampedeEvent(LogDetail.UNIT_INFO)
//                        .add(LogDetail.UNIT_ID, "unit:" + taskgraph.getQualifiedToolName())
//                        .add(LogDetail.SUBMIT_FILE, "null")
//                        .add(LogDetail.TYPE, "1")
//                        .add(LogDetail.TYPE_DESC, "compute")
//                        .add(LogDetail.CLUSTERED, "0")
//                        .add(LogDetail.MAX_RETRIES, "1")
//                        .add(LogDetail.TASK_COUNT, "1")
//                        .add(LogDetail.ARGS, "\"\""))
//                .add(LogDetail.EXECUTABLE, taskgraph.getQualifiedToolName())
//        );
//        for (Task task : TaskGraphUtils.getAllTasksRecursive(taskgraph, false)) {
//
//            logStampedeEvent(addBaseEventDetails(
//                    new StampedeEvent(LogDetail.UNIT_INFO)
//                            .add(LogDetail.UNIT_ID, "unit:" + task.getQualifiedToolName())
//                            .add(LogDetail.SUBMIT_FILE, "null")
//                            .add(LogDetail.TYPE, "1")
//                            .add(LogDetail.TYPE_DESC, "compute")
//                            .add(LogDetail.CLUSTERED, "0")
//                            .add(LogDetail.MAX_RETRIES, "1")
//                            .add(LogDetail.TASK_COUNT, "1")
//                            .add(LogDetail.ARGS, getTaskArgs(task))
//                            .add(LogDetail.EXECUTABLE, task.getQualifiedToolName()))
//            );
//        }
//
//        //Map tasks to jobs 1->1
//
//        logStampedeEvent(addBaseEventDetails(new StampedeEvent(LogDetail.MAP_TASK_UNIT)
//                .add(LogDetail.TASK_ID, "task:" + taskgraph.getQualifiedTaskName())
//                .add(LogDetail.UNIT_ID, "unit:" + taskgraph.getQualifiedToolName())
//        ));
//        for (Task task : TaskGraphUtils.getAllTasksRecursive(taskgraph, false)) {
//            logStampedeEvent(addBaseEventDetails(new StampedeEvent(LogDetail.MAP_TASK_UNIT)
//                    .add(LogDetail.TASK_ID, "task:" + task.getQualifiedTaskName())
//                    .add(LogDetail.UNIT_ID, "unit:" + task.getQualifiedToolName())
//            ));
//        }
//
//        for (Cable cable : TaskGraphUtils.getConnectedCables(TaskGraphUtils.getAllTasksRecursive(taskgraph, false))) {
//            logStampedeEvent(addBaseEventDetails(new StampedeEvent(LogDetail.UNIT_CABLES)
//                    .add(LogDetail.CHILD_UNIT, cable.getReceivingTask().getQualifiedTaskName())
//                    .add(LogDetail.PARENT_UNIT, cable.getSendingTask().getQualifiedTaskName())
//            ));
//        }
//    }


//    public void logStampedeEvent(StampedeEvent stampedeEvent) {
//        if (stampedeEvent != null) {
//            if (LoggingUtils.loggingToRabbitMQ(taskgraph.getProperties())) {
//                Loggers.STAMPEDE_LOGGER.info(stampedeEvent);
//            }
//            runtimeFileLog.info(stampedeEvent);
//        }
//    }

//    public StampedeEvent addBaseJobInstDetails(StampedeEvent stampedeEvent, Task task) {
//        Integer changes = stateChanges.get(task);
//        if (changes == null) {
//            changes = 0;
//        } else {
//            changes++;
//        }
//        stateChanges.put(task, changes);
//        addBaseEventDetails(stampedeEvent)
//                .add(LogDetail.UNIT_INST_ID, String.valueOf(getTaskNumber(task)))
//                .add(LogDetail.UNIT_STATE_ID, String.valueOf(changes))
//                .add(LogDetail.UNIT_ID, "unit:" + task.getQualifiedToolName());
//
//        return stampedeEvent;
//    }
//
//    public StampedeEvent addBaseEventDetails(StampedeEvent stampedeEvent) {
//        stampedeEvent.add(LogDetail.LEVEL, LogDetail.LEVELS.INFO)
//                .add(LogDetail.UUID, runUUID.toString());
//        return stampedeEvent;
//    }
//
//    public StampedeEvent addSchedJobInstDetails(StampedeEvent stampedeEvent, Task task) {
//        addBaseJobInstDetails(stampedeEvent, task);
//        stampedeEvent.add(LogDetail.SCHEDULER_ID, String.valueOf(taskMap.get(task)));
//        return stampedeEvent;
//    }

//    public Integer getTaskNumber(Task task) {
//        return taskMap.get(task);
//    }

//    private StampedeEvent execStateToJobState(ExecutionEvent event) {
//        ExecutionState executionState = event.getState();
//        StampedeEvent stampedeEvent;
//
////        System.out.println("Task " + event.getTask() +
////                " changed from " + event.getOldState() +
////                " to " + event.getState());
//
//        switch (executionState) {
//            //CONDOR JOB STATES
////            case NOT_INITIALIZED:
////                //PRE_SCRIPT_STARTED
////                return 0;
////
////            case NOT_EXECUTABLE:
////                //PRE_SCRIPT_FAILED
////                return 3;
//
//            case SCHEDULED:
//                //SUBMIT
//                stampedeEvent = new StampedeEvent(LogDetail.SCHEDULED);
//                addSchedJobInstDetails(stampedeEvent, event.getTask());
//                stampedeEvent.add(LogDetail.STATUS, "0");
//                return stampedeEvent;
//
//            case RUNNING:
//                //EXECUTE
//                if (event.getOldState() == ExecutionState.PAUSED) {
//                    stampedeEvent = new StampedeEvent(LogDetail.HELD_END);
//                    addSchedJobInstDetails(stampedeEvent, event.getTask());
//                    stampedeEvent.add(LogDetail.STATUS, "0");
//                    return stampedeEvent;
//                } else {
//                    stampedeEvent = new StampedeEvent(LogDetail.JOB_START);
//                    addSchedJobInstDetails(stampedeEvent, event.getTask())
//                            .add(LogDetail.STD_OUT_FILE, runtimeFileLog.getLogFilePath())
//                            .add(LogDetail.STD_ERR_FILE, runtimeFileLog.getLogFilePath());
//                    return stampedeEvent;
//                }
//
//            case PAUSED:
//                //JOB_HELD
//                stampedeEvent = new StampedeEvent(LogDetail.HELD_START);
//                addSchedJobInstDetails(stampedeEvent, event.getTask());
//                return stampedeEvent;
//
//            case COMPLETE:
//                //TERMINATED
//                stampedeEvent = new StampedeEvent(LogDetail.JOB_END);
//                addSchedJobInstDetails(stampedeEvent, event.getTask())
//                        .add(LogDetail.STD_OUT_FILE, runtimeFileLog.getLogFilePath())
//                        .add(LogDetail.STD_ERR_FILE, runtimeFileLog.getLogFilePath())
//                        .add(LogDetail.SITE, getHostname())
//                        .add(LogDetail.MULTIPLIER, "1")
//                        .add(LogDetail.STATUS, "0")
//                        .add(LogDetail.EXIT_CODE, "0");
//                return stampedeEvent;
//
////            case RESETTING:
////                //    UNKNOWN
////                return 9;
////
////            case RESET:
////                //
////                return 9;
////
//            case ERROR:
//                //JOB FAILURE
//                stampedeEvent = new StampedeEvent(LogDetail.JOB_END);
//                addSchedJobInstDetails(stampedeEvent, event.getTask())
//                        .add(LogDetail.STD_OUT_FILE, runtimeFileLog.getLogFilePath())
//                        .add(LogDetail.STD_ERR_FILE, runtimeFileLog.getLogFilePath())
//                        .add(LogDetail.SITE, getHostname())
//                        .add(LogDetail.MULTIPLIER, "1")
//                        .add(LogDetail.STATUS, "-1")
//                        .add(LogDetail.EXIT_CODE, "1");
//                return stampedeEvent;
//
//            case SUSPENDED:
//                //JOB HELD
//                stampedeEvent = new StampedeEvent(LogDetail.HELD_START);
//                addSchedJobInstDetails(stampedeEvent, event.getTask());
//                return stampedeEvent;
//
//            case UNKNOWN:
//                return null;
////
////            case LOCK:
////                return 9;
//        }
//        return null;
//    }
//
//    private String getHostname() {
//        String hostname;
//        try {
//            hostname = Inet4Address.getLocalHost().getHostName();
//        } catch (UnknownHostException e) {
//            hostname = "localhost";
//        }
//        return hostname;
//    }

//    private String getTaskArgs(Task task) {
//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append("\"");
//        for (String param : task.getParameterNames()) {
//            stringBuilder.append(param)
//                    .append(":")
//                    .append(task.getParameter(param))
//                    .append(",");
//        }
//        stringBuilder.append("\"");
//        return stringBuilder.toString().replaceAll("[\n\r]", "");
//    }

//    private class ExecutionLogger implements ExecutionListener {
//
//        @Override
//        public void executionStateChanged(ExecutionEvent event) {
//            StampedeEvent stampedeEvent = execStateToJobState(event);
//            logStampedeEvent(stampedeEvent);
//        }
//
//        @Override
//        public void executionRequested(ExecutionEvent event) {
//            if (event.getTask() instanceof TaskGraph) {
//                StampedeEvent stampedeEvent = new StampedeEvent(LogDetail.SCHEDULED);
//                addSchedJobInstDetails(stampedeEvent, event.getTask());
//                stampedeEvent.add(LogDetail.STATUS, "0");
//                logStampedeEvent(stampedeEvent);
//            }
//        }
//
//        @Override
//        public void executionStarting(ExecutionEvent event) {
//            StampedeEvent runEvent = new StampedeEvent(LogDetail.RUNNING_WORKFLOW)
//                    .add(LogDetail.RESTART_COUNT, "0");
//            addBaseEventDetails(runEvent);
//            logStampedeEvent(runEvent);
//        }
//
//        @Override
//        public void executionFinished(ExecutionEvent event) {
//
////            System.out.println("event : "
////                    + event.getTask().getQualifiedTaskName() + " finished");
//
//            logStampedeEvent(addSchedJobInstDetails(new StampedeEvent(LogDetail.JOB_TERM)
//                    .add(LogDetail.STATUS, "0"),
//                    event.getTask())
//            );
//            StampedeEvent stampedeEvent = new StampedeEvent(LogDetail.JOB_END);
//            addSchedJobInstDetails(stampedeEvent, event.getTask())
//                    .add(LogDetail.STD_OUT_FILE, runtimeFileLog.getLogFilePath())
//                    .add(LogDetail.STD_ERR_FILE, runtimeFileLog.getLogFilePath())
//                    .add(LogDetail.SITE, getHostname())
//                    .add(LogDetail.MULTIPLIER, "1")
//                    .add(LogDetail.STATUS, "0")
//                    .add(LogDetail.EXIT_CODE, "0");
//            logStampedeEvent(stampedeEvent);
//
//            Task task = event.getTask();
//            if (task instanceof TaskGraph) {
//                long duration = (new Date().getTime() / 1000) - startTime;
//                if (duration == 0) {
//                    duration = 1;
//                }
//
//                StampedeEvent invEnd = new StampedeEvent(LogDetail.INVOCATION_END);
//                addBaseEventDetails(invEnd)
//                        .add(LogDetail.UNIT_INST_ID, String.valueOf(getTaskNumber(task)))
//                        .add(LogDetail.INVOCATION_ID, "1")
//                        .add(LogDetail.UNIT_ID, "unit:" + task.getQualifiedToolName())
//                        .add(LogDetail.START_TIME, String.valueOf(startTime))
//                        .add(LogDetail.DURATION, String.valueOf(duration))
//                        .add(LogDetail.TRANSFORMATION, task.getQualifiedTaskName())
//                        .add(LogDetail.EXECUTABLE, "Triana")
//                        .add(LogDetail.ARGS, getTaskArgs(task))
//                        .add(LogDetail.TASK_ID, task.getQualifiedTaskName())
//                        .add(LogDetail.EXIT_CODE, "0")
//                ;
//                logStampedeEvent(invEnd);
//            }
//        }
//
//        @Override
//        public void executionReset(ExecutionEvent event) {
////            System.out.println("Reset event " + event.getTask().getQualifiedTaskName()
////                    + " event " + event.getState());
//        }
//    }
}
