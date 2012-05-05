package org.trianacode.enactment.logging.stampede;

import org.trianacode.config.TrianaProperties;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.enactment.logging.LoggingUtils;
import org.trianacode.enactment.logging.runtimeLogging.RuntimeFileLog;
import org.trianacode.taskgraph.*;
import org.trianacode.taskgraph.service.ExecutionEvent;
import org.trianacode.taskgraph.service.ExecutionListener;
import org.trianacode.taskgraph.service.RunnableTask;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 14/03/2012
 * Time: 13:19
 * To change this template use File | Settings | File Templates.
 */
public class StampedeLog {

    public static final String RUN_UUID_STRING = "runUUID";
    public static final String PARENT_UUID_STRING = "parentUUID";
    public static final String JOB_ID = "jobID";
    public static final String JOB_INST_ID = "jobInstID";

    private HashMap<Task, Integer> stateChanges;

    private RuntimeFileLog runtimeFileLog;
    private TaskGraph tgraph;

    private UUID runUUID = null;
    private UUID parentUUID = null;
    private String jobID = null;
    private String jobInstID = null;

    private TrianaProperties properties;
    private int sched_id_count = 0;
    private ExecutionListener executionLogger;
    long startTime;


    public StampedeLog(TaskGraph tgraph, UUID runUUID) {
        this.tgraph = tgraph;
        properties = tgraph.getProperties();
        reset(runUUID);

//        this.runUUID = runUUID;
//        runtimeFileLog = new RuntimeFileLog(
//                tgraph.getProperties(), runUUID);
//        taskMap = new HashMap<Task, Integer>();
//        stateChanges = new HashMap<Task, Integer>();
//        startTime = new Date().getTime() / 1000;

        executionLogger = new ExecutionLogger(this);
    }

    public void reset(UUID uuid) {
        startTime = new Date().getTime() / 1000;
        setRunUUID(uuid);

        String log_location = properties.getProperty(TrianaProperties.LOG_LOCATION);
        runtimeFileLog = new RuntimeFileLog(log_location, runUUID);
        taskMap = new HashMap<Task, Integer>();
        stateChanges = new HashMap<Task, Integer>();
        sched_id_count = 0;
    }


    public void logStampedeEvent(StampedeEvent stampedeEvent) {
        if (stampedeEvent != null) {
            if (LoggingUtils.loggingToRabbitMQ(properties)) {
                Loggers.STAMPEDE_LOGGER.info(stampedeEvent);
            }
            runtimeFileLog.info(stampedeEvent);
        }
    }

    public void logPlanEvent(TaskGraph tgraph) {
        String parent;
        boolean subWorkflow;
        if (parentUUID == null) {
            parent = runUUID.toString();
            subWorkflow = false;
        } else {
            parent = parentUUID.toString();
            subWorkflow = true;
        }

        StampedeEvent planEvent = new StampedeEvent(LogDetail.PLAN)
                .add(LogDetail.ROOT_WF, parent)
                .add(LogDetail.PLANNER_VERSION, (String) tgraph.getParameter(TrianaProperties.VERSION))
                .add(LogDetail.SUBMIT_DIR, System.getProperty("user.dir"))
                .add(LogDetail.USERNAME, System.getProperty("user.name"))
                .add(LogDetail.EXECUTION_FILE_NAME, "exec:" + tgraph.getToolName())
                .add(LogDetail.SUBMIT_HOSTNAME, getHostname())
                .add(LogDetail.WF_VERSION, "1")
                .add(LogDetail.DESCRIPTION_FILE, tgraph.getToolName())
                .add(LogDetail.ARGS, "\"\"");
        addBaseEventDetails(planEvent);
        logStampedeEvent(planEvent);

        logGraph(tgraph);

        if (subWorkflow) {
            logSubWf();
        }
    }

    public void logSubWf() {
        if (parentUUID != null && jobID != null && jobInstID != null) {
            StampedeEvent subWfMap = new StampedeEvent(LogDetail.MAP_SUB_WORKFLOW)
                    .add(LogDetail.SUB_WF_ID, runUUID.toString())
                    .add(LogDetail.UNIT_ID, jobID)
                    .add(LogDetail.UNIT_INST_ID, jobInstID);

            //base event, but replacing runUUID with the parentUUID
            subWfMap.add(LogDetail.LEVEL, LogDetail.LEVELS.INFO)
                    .add(LogDetail.UUID, parentUUID.toString());

            logStampedeEvent(subWfMap);
        }
    }


    public void logTaskGraphTask(TaskGraph taskgraph) {
        logStampedeEvent(
                addBaseEventDetails(new StampedeEvent(LogDetail.TASK_INFO)
                        .add(LogDetail.TYPE_DESC, "compute")
                        .add(LogDetail.TASK_ID, "task:" + taskgraph.getQualifiedTaskName())
                        .add(LogDetail.ARGS, "\"\""))
                        .add(LogDetail.TYPE, "1")
                        .add(LogDetail.TRANSFORMATION, taskgraph.getQualifiedToolName())
        );
    }


    public void logAllTasks(TaskGraph taskgraph) {
        for (Task task : TaskGraphUtils.getAllTasksRecursive(taskgraph, false)) {

            logStampedeEvent(addBaseEventDetails(new StampedeEvent(LogDetail.TASK_INFO)
                    .add(LogDetail.TYPE_DESC, "compute")
                    .add(LogDetail.TASK_ID, "task:" + task.getQualifiedTaskName())
                    .add(LogDetail.ARGS, getTaskArgs(task))
                    .add(LogDetail.TYPE, "1")
                    .add(LogDetail.TRANSFORMATION, task.getQualifiedToolName()))
            );
        }
    }

    public void logTaskgraphCables(TaskGraph taskgraph) {
        for (Cable cable : TaskGraphUtils.getConnectedCables(TaskGraphUtils.getAllTasksRecursive(taskgraph, false))) {
            logStampedeEvent(addBaseEventDetails(new StampedeEvent(LogDetail.TASK_CABLES)
                    .add(LogDetail.CHILD_TASK, cable.getReceivingTask().getQualifiedTaskName())
                    .add(LogDetail.PARENT_TASK, cable.getSendingTask().getQualifiedTaskName()))
            );
        }
    }

    public void logTaskgraphJob(TaskGraph taskgraph) {
        logStampedeEvent(addBaseEventDetails(
                new StampedeEvent(LogDetail.UNIT_INFO)
                        .add(LogDetail.UNIT_ID, "unit:" + taskgraph.getQualifiedToolName())
                        .add(LogDetail.SUBMIT_FILE, "null")
                        .add(LogDetail.TYPE, "1")
                        .add(LogDetail.TYPE_DESC, "compute")
                        .add(LogDetail.CLUSTERED, "0")
                        .add(LogDetail.MAX_RETRIES, "1")
                        .add(LogDetail.TASK_COUNT, "1")
                        .add(LogDetail.ARGS, "\"\""))
                .add(LogDetail.EXECUTABLE, taskgraph.getQualifiedToolName())
        );
    }

    public void logTaskgraphJobs(TaskGraph taskgraph) {
        for (Task task : TaskGraphUtils.getAllTasksRecursive(taskgraph, false)) {

            logStampedeEvent(addBaseEventDetails(
                    new StampedeEvent(LogDetail.UNIT_INFO)
                            .add(LogDetail.UNIT_ID, "unit:" + task.getQualifiedToolName())
                            .add(LogDetail.SUBMIT_FILE, "null")
                            .add(LogDetail.TYPE, "1")
                            .add(LogDetail.TYPE_DESC, "compute")
                            .add(LogDetail.CLUSTERED, "0")
                            .add(LogDetail.MAX_RETRIES, "1")
                            .add(LogDetail.TASK_COUNT, "1")
                            .add(LogDetail.ARGS, getTaskArgs(task))
                            .add(LogDetail.EXECUTABLE, task.getQualifiedToolName()))
            );
        }
    }

    public void mapTasksTojobs(TaskGraph taskgraph) {
        logStampedeEvent(addBaseEventDetails(new StampedeEvent(LogDetail.MAP_TASK_UNIT)
                .add(LogDetail.TASK_ID, "task:" + taskgraph.getQualifiedTaskName())
                .add(LogDetail.UNIT_ID, "unit:" + taskgraph.getQualifiedToolName())
        ));
        for (Task task : TaskGraphUtils.getAllTasksRecursive(taskgraph, false)) {
            logStampedeEvent(addBaseEventDetails(new StampedeEvent(LogDetail.MAP_TASK_UNIT)
                    .add(LogDetail.TASK_ID, "task:" + task.getQualifiedTaskName())
                    .add(LogDetail.UNIT_ID, "unit:" + task.getQualifiedToolName())
            ));
        }
    }

    public void mapTaskCablesToJobCables(TaskGraph taskgraph) {
        for (Cable cable : TaskGraphUtils.getConnectedCables(TaskGraphUtils.getAllTasksRecursive(taskgraph, false))) {
            logStampedeEvent(addBaseEventDetails(new StampedeEvent(LogDetail.UNIT_CABLES)
                    .add(LogDetail.CHILD_UNIT, cable.getReceivingTask().getQualifiedTaskName())
                    .add(LogDetail.PARENT_UNIT, cable.getSendingTask().getQualifiedTaskName())
            ));
        }
    }


    public void logGraph(TaskGraph taskgraph) {
        logTaskGraphTask(taskgraph);
        logAllTasks(taskgraph);
        logTaskgraphCables(taskgraph);

        logTaskgraphJob(taskgraph);
        logTaskgraphJobs(taskgraph);

        mapTasksTojobs(taskgraph);
        mapTaskCablesToJobCables(taskgraph);


        //Task INFO

//        logStampedeEvent(addBaseEventDetails(new StampedeEvent(LogDetail.TASK_INFO)
//                .add(LogDetail.TYPE_DESC, "compute")
//                .add(LogDetail.TASK_ID, "task:" + taskgraph.getQualifiedTaskName())
//                .add(LogDetail.ARGS, "\"\""))
//                .add(LogDetail.TYPE, "1")
//                .add(LogDetail.TRANSFORMATION, taskgraph.getQualifiedToolName())
//        );

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

//        for (Cable cable : TaskGraphUtils.getConnectedCables(TaskGraphUtils.getAllTasksRecursive(taskgraph, false))) {
//            logStampedeEvent(addBaseEventDetails(new StampedeEvent(LogDetail.TASK_CABLES)
//                    .add(LogDetail.CHILD_TASK, cable.getReceivingTask().getQualifiedTaskName())
//                    .add(LogDetail.PARENT_TASK, cable.getSendingTask().getQualifiedTaskName()))
//            );
//        }

        //"Job" (Unit) Info

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

        //Map tasks to jobs 1->1

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

//        for (Cable cable : TaskGraphUtils.getConnectedCables(TaskGraphUtils.getAllTasksRecursive(taskgraph, false))) {
//            logStampedeEvent(addBaseEventDetails(new StampedeEvent(LogDetail.UNIT_CABLES)
//                    .add(LogDetail.CHILD_UNIT, cable.getReceivingTask().getQualifiedTaskName())
//                    .add(LogDetail.PARENT_UNIT, cable.getSendingTask().getQualifiedTaskName())
//            ));
//        }
    }

    public HashMap<Task, Integer> taskMap; // = new HashMap<Task, Integer>();

    public Integer getTaskNumber(Task task) {
        return taskMap.get(task);
    }

    public StampedeEvent addBaseJobInstDetails(StampedeEvent stampedeEvent, Task task) {
        Integer changes = stateChanges.get(task);
        if (changes == null) {
            changes = 0;
        } else {
            changes++;
        }
        stateChanges.put(task, changes);
        addBaseEventDetails(stampedeEvent)
                .add(LogDetail.UNIT_INST_ID, String.valueOf(getTaskNumber(task)))
                .add(LogDetail.UNIT_STATE_ID, String.valueOf(changes))
                .add(LogDetail.UNIT_ID, "unit:" + task.getQualifiedToolName());

        return stampedeEvent;
    }

    public StampedeEvent addBaseEventDetails(StampedeEvent stampedeEvent) {
        stampedeEvent.add(LogDetail.LEVEL, LogDetail.LEVELS.INFO)
                .add(LogDetail.UUID, runUUID.toString());
        return stampedeEvent;
    }

    public StampedeEvent addSchedJobInstDetails(StampedeEvent stampedeEvent, Task task) {
        addBaseJobInstDetails(stampedeEvent, task);
        stampedeEvent.add(LogDetail.SCHEDULER_ID, String.valueOf(taskMap.get(task)));
        return stampedeEvent;
    }

    public void wakeTask(Task task) {
        sched_id_count++;
        taskMap.put(task, sched_id_count);
        StampedeEvent wakeStartEvent = new StampedeEvent(LogDetail.START_WAKING_TASK);
        addSchedJobInstDetails(wakeStartEvent, task);
        logStampedeEvent(wakeStartEvent);
    }


    public static String getHostname() {
        String hostname;
        try {
            hostname = Inet4Address.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            hostname = "localhost";
        }
        return hostname;
    }

    static String getTaskArgs(Task task) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\"");
        for (String param : task.getParameterNames()) {
            stringBuilder.append(param)
                    .append(":")
                    .append(task.getParameter(param))
                    .append(",");
        }
        stringBuilder.append("\"");
        return stringBuilder.toString().replaceAll("[\n\r]", "");
    }

    public void scheduleTask(Task task) {
        StampedeEvent stampedeEvent = new StampedeEvent(LogDetail.SCHEDULED);
        addSchedJobInstDetails(stampedeEvent, task);
        stampedeEvent.add(LogDetail.STATUS, "0");
        logStampedeEvent(stampedeEvent);
    }

    public ExecutionListener getExecutionLogger() {
        return executionLogger;
    }

    public void setRunUUID(UUID runUUID) {
        this.runUUID = runUUID;
    }

    public void logInvocationStart(RunnableTask runnableTask) {
        logStampedeEvent(
                addBaseEventDetails(new StampedeEvent(LogDetail.INVOCATION_START)
                        .add(LogDetail.UNIT_INST_ID, String.valueOf(getTaskNumber(runnableTask)))
                        .add(LogDetail.UNIT_ID, runnableTask.getQualifiedToolName())
                        .add(LogDetail.INVOCATION_ID, "1")));
    }

    public void logJobTerminate(RunnableTask runnableTask) {
        logStampedeEvent(
                addSchedJobInstDetails(new StampedeEvent(LogDetail.JOB_TERM)
                        .add(LogDetail.STATUS, "0"),
                        runnableTask)
        );
    }

    public void logInvocationEnd(RunnableTask runnableTask, String args, long startTime, long duration) {
        StampedeEvent invEnd = addBaseEventDetails(new StampedeEvent(LogDetail.INVOCATION_END))
                .add(LogDetail.UNIT_INST_ID, String.valueOf(getTaskNumber(runnableTask)))
                .add(LogDetail.INVOCATION_ID, "1")
                .add(LogDetail.UNIT_ID, "unit:" + runnableTask.getQualifiedToolName())
                .add(LogDetail.START_TIME, String.valueOf(startTime))
                .add(LogDetail.DURATION, String.valueOf(duration))
                .add(LogDetail.TRANSFORMATION, runnableTask.getQualifiedTaskName())
                .add(LogDetail.EXECUTABLE, "Triana")
                .add(LogDetail.ARGS, args)
                .add(LogDetail.TASK_ID, runnableTask.getQualifiedTaskName()
                );

        if (!runnableTask.getExecutionState().equals(ExecutionState.ERROR)) {
//                    endJob.add(LogDetail.STATUS, "-1");
//                    endJob.add(LogDetail.EXIT_CODE, "1");
            invEnd.add(LogDetail.EXIT_CODE, "0");

        } else {
//                    endJob.add(LogDetail.STATUS, "0");
//                    endJob.add(LogDetail.EXIT_CODE, "0");
            invEnd.add(LogDetail.EXIT_CODE, "1");
        }
        logStampedeEvent(invEnd);
    }

    public void logHost(RunnableTask runnableTask) {
        logStampedeEvent(
                addBaseJobInstDetails(new StampedeEvent(LogDetail.HOST), runnableTask)
                        .add(LogDetail.SITE, "localhost")
                        .add(LogDetail.HOSTNAME, getHostname())
                        .add(LogDetail.IP_ADDRESS, "127.0.0.1")
        );
    }

    public void initExecutionProperties(HashMap<String, String> executionProperties) {
        String runprop = executionProperties.get(RUN_UUID_STRING);
        if (runprop != null) {
            runUUID = UUID.fromString(runprop);
        }
        String parentprop = executionProperties.get(PARENT_UUID_STRING);
        if (parentprop != null) {
            parentUUID = UUID.fromString(parentprop);
        }
        jobID = executionProperties.get(JOB_ID);
        jobInstID = executionProperties.get(JOB_INST_ID);
    }

    public UUID getRunUUID() {
        return runUUID;
    }


    class ExecutionLogger implements ExecutionListener {

        private StampedeLog stampedeLog;

        public ExecutionLogger(StampedeLog stampedeLog) {
            this.stampedeLog = stampedeLog;
        }

        @Override
        public void executionStateChanged(ExecutionEvent event) {
            StampedeEvent stampedeEvent = execStateToJobState(event);
            stampedeLog.logStampedeEvent(stampedeEvent);
        }

        @Override
        public void executionRequested(ExecutionEvent event) {
            if (event.getTask() instanceof TaskGraph) {
                stampedeLog.scheduleTask(event.getTask());
//            StampedeEvent stampedeEvent = new StampedeEvent(LogDetail.SCHEDULED);
//            addSchedJobInstDetails(stampedeEvent, event.getTask());
//            stampedeEvent.add(LogDetail.STATUS, "0");
//            logStampedeEvent(stampedeEvent);
            }
        }

        @Override
        public void executionStarting(ExecutionEvent event) {
            StampedeEvent runEvent = new StampedeEvent(LogDetail.RUNNING_WORKFLOW)
                    .add(LogDetail.RESTART_COUNT, "0");
            addBaseEventDetails(runEvent);
            logStampedeEvent(runEvent);
        }

        @Override
        public void executionFinished(ExecutionEvent event) {

//            System.out.println("event : "
//                    + event.getTask().getQualifiedTaskName() + " finished");

            logStampedeEvent(addSchedJobInstDetails(new StampedeEvent(LogDetail.JOB_TERM)
                    .add(LogDetail.STATUS, "0"),
                    event.getTask())
            );
            StampedeEvent stampedeEvent = new StampedeEvent(LogDetail.JOB_END);
            addSchedJobInstDetails(stampedeEvent, event.getTask())
                    .add(LogDetail.STD_OUT_FILE, runtimeFileLog.getLogFilePath())
                    .add(LogDetail.STD_ERR_FILE, runtimeFileLog.getLogFilePath())
                    .add(LogDetail.SITE, getHostname())
                    .add(LogDetail.MULTIPLIER, "1")
                    .add(LogDetail.STATUS, "0")
                    .add(LogDetail.EXIT_CODE, "0");
            logStampedeEvent(stampedeEvent);

            Task task = event.getTask();
            if (task instanceof TaskGraph) {
                long duration = (new Date().getTime() / 1000) - startTime;
                if (duration == 0) {
                    duration = 1;
                }

                StampedeEvent invEnd = new StampedeEvent(LogDetail.INVOCATION_END);
                addBaseEventDetails(invEnd)
                        .add(LogDetail.UNIT_INST_ID, String.valueOf(getTaskNumber(task)))
                        .add(LogDetail.INVOCATION_ID, "1")
                        .add(LogDetail.UNIT_ID, "unit:" + task.getQualifiedToolName())
                        .add(LogDetail.START_TIME, String.valueOf(startTime))
                        .add(LogDetail.DURATION, String.valueOf(duration))
                        .add(LogDetail.TRANSFORMATION, task.getQualifiedTaskName())
                        .add(LogDetail.EXECUTABLE, "Triana")
                        .add(LogDetail.ARGS, getTaskArgs(task))
                        .add(LogDetail.TASK_ID, task.getQualifiedTaskName())
                        .add(LogDetail.EXIT_CODE, "0")
                ;
                logStampedeEvent(invEnd);

                //After execution, stop listening for events.

            }
        }

        @Override
        public void executionReset(ExecutionEvent event) {
//            System.out.println("Reset event " + event.getTask().getQualifiedTaskName()
//                    + " event " + event.getState());
        }


        private StampedeEvent execStateToJobState(ExecutionEvent event) {
            ExecutionState executionState = event.getState();
            StampedeEvent stampedeEvent;

//        System.out.println("Task " + event.getTask() +
//                " changed from " + event.getOldState() +
//                " to " + event.getState());

            switch (executionState) {
                //CONDOR JOB STATES
//            case NOT_INITIALIZED:
//                //PRE_SCRIPT_STARTED
//                return 0;
//
//            case NOT_EXECUTABLE:
//                //PRE_SCRIPT_FAILED
//                return 3;

                case SCHEDULED:
                    //SUBMIT
                    stampedeEvent = new StampedeEvent(LogDetail.SCHEDULED);
                    addSchedJobInstDetails(stampedeEvent, event.getTask());
                    stampedeEvent.add(LogDetail.STATUS, "0");
                    return stampedeEvent;

                case RUNNING:
                    //EXECUTE
                    if (event.getOldState() == ExecutionState.PAUSED) {
                        stampedeEvent = new StampedeEvent(LogDetail.HELD_END);
                        addSchedJobInstDetails(stampedeEvent, event.getTask());
                        stampedeEvent.add(LogDetail.STATUS, "0");
                        return stampedeEvent;
                    } else {
                        stampedeEvent = new StampedeEvent(LogDetail.JOB_START);
                        addSchedJobInstDetails(stampedeEvent, event.getTask())
                                .add(LogDetail.STD_OUT_FILE, runtimeFileLog.getLogFilePath())
                                .add(LogDetail.STD_ERR_FILE, runtimeFileLog.getLogFilePath());
                        return stampedeEvent;
                    }

                case PAUSED:
                    //JOB_HELD
                    stampedeEvent = new StampedeEvent(LogDetail.HELD_START);
                    addSchedJobInstDetails(stampedeEvent, event.getTask());
                    return stampedeEvent;

                case COMPLETE:
                    //TERMINATED
                    stampedeEvent = new StampedeEvent(LogDetail.JOB_END);
                    addSchedJobInstDetails(stampedeEvent, event.getTask())
                            .add(LogDetail.STD_OUT_FILE, runtimeFileLog.getLogFilePath())
                            .add(LogDetail.STD_ERR_FILE, runtimeFileLog.getLogFilePath())
                            .add(LogDetail.SITE, getHostname())
                            .add(LogDetail.MULTIPLIER, "1")
                            .add(LogDetail.STATUS, "0")
                            .add(LogDetail.EXIT_CODE, "0");
                    return stampedeEvent;

//            case RESETTING:
//                //    UNKNOWN
//                return 9;
//
//            case RESET:
//                //
//                return 9;
//
                case ERROR:
                    //JOB FAILURE
                    stampedeEvent = new StampedeEvent(LogDetail.JOB_END);
                    addSchedJobInstDetails(stampedeEvent, event.getTask())
                            .add(LogDetail.STD_OUT_FILE, runtimeFileLog.getLogFilePath())
                            .add(LogDetail.STD_ERR_FILE, runtimeFileLog.getLogFilePath())
                            .add(LogDetail.SITE, getHostname())
                            .add(LogDetail.MULTIPLIER, "1")
                            .add(LogDetail.STATUS, "-1")
                            .add(LogDetail.EXIT_CODE, "1");
                    return stampedeEvent;

                case SUSPENDED:
                    //JOB HELD
                    stampedeEvent = new StampedeEvent(LogDetail.HELD_START);
                    addSchedJobInstDetails(stampedeEvent, event.getTask());
                    return stampedeEvent;

                case UNKNOWN:
                    return null;
//
//            case LOCK:
//                return 9;
            }
            return null;
        }

    }
}
