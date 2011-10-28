package org.trianacode.enactment.logging.stampede;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 15/08/2011
 * Time: 11:00
 * To change this template use File | Settings | File Templates.
 */
public class LogDetail {
    String name;
    String detail;

    public static final String TASK = "Task";
    public static final String WF = "WORKFLOW.FILE";
    public static final String LEVEL = "Level";
    public static final String RUNNING_WORKFLOW = "WORKFLOW.RUNNING";
    public static final String FINISHED_WORKFLOW = "WORKFLOW.FINISHED";
    public static final String STOPPING_WORKFLOW = "WORKFLOW.STOPPING";
    public static final String RUNNING_TASK = "TASK.RUNNING";
    public static final String FINISHED_TASK = "TASK.FINISHED";
    public static final String ERROR = "ERROR";
    public static final String EXCEPTION = "EXCEPTION";
    public static final String ERRMSG = "ERRMSG";

    public static final String UNIT_OUTPUT = "UNIT.OUTPUT";
    public static final String UNIT_INPUT = "UNIT.INPUT";
    public static final String UUID = "WORKFLOW.UUID";
    public static final String ALLPARAMS = "TASK.ALLPARAMETERS";
    public static final String WAKING_TASK = "TASK.WAKING";
    public static final String LOG_FILE = "WORKFLOW.LOGFILE";

    public LogDetail(String name, String detail) {
        this.name = name;
        this.detail = detail;
    }

    public String getName() {
        return name;
    }

    public String getDetail() {
        return detail;
    }
}
