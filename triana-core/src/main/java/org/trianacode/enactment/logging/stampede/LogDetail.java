package org.trianacode.enactment.logging.stampede;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 15/08/2011
 * Time: 11:00
 * To change this template use File | Settings | File Templates.
 */
public class LogDetail {
    String name;
    String detail;

    public static final String TASK_ID = "task.id";
    //    public static final String WF = "WORKFLOW.FILE";
    public static final String LEVEL = "level";
    public static final String RUNNING_WORKFLOW = "stampede.xwf.start";
    public static final String FINISHED_WORKFLOW = "stampede.xwf.end";

//    public static final String FINISHED_WORKFLOW = "WORKFLOW.FINISHED";
//    public static final String STOPPING_WORKFLOW = "WORKFLOW.STOPPING";
//    public static final String RUNNING_TASK = "TASK.RUNNING";
//    public static final String FINISHED_TASK = "TASK.FINISHED";
//    public static final String ERROR = "ERROR";
//    public static final String EXCEPTION = "EXCEPTION";
//    public static final String ERRMSG = "ERRMSG";
    //    public static final String LOG_FILE = "WORKFLOW.LOGFILE";

    //    public static final String UNIT_OUTPUT = "JOB.OUTPUT";
//    public static final String UNIT_INPUT = "JOB.INPUT";
    public static final String UUID = "xwf.id";
    public static final String SUBMIT_HOSTNAME = "submit.hostname";

    public static final String WF_VERSION = "dax.version";
    public static final String DESCRIPTION_FILE = "dax.file";
    public static final String EXECUTION_FILE_NAME = "dag.file.name";
    public static final String PLANNER_VERSION = "planner.version";
    public static final String SUBMIT_DIR = "submit.dir";
    public static final String USERNAME = "user";
    public static final String ROOT_WF = "root.xwf.id";
    public static final String TASK_INFO = "stampede.task.info";
    public static final String TRANSFORMATION = "transformation";
    public static final String TYPE = "type";
    public static final String TYPE_DESC = "type_desc";
    public static final String ARGS = "argv";
    public static final String TASK_CABLES = "stampede.task.edge";
    public static final String CHILD_TASK = "child.task.id";
    public static final String PARENT_TASK = "parent.task.id";
    public static final String UNIT_INFO = "stampede.job.info";
    public static final String SUBMIT_FILE = "submit_file";
    public static final String CLUSTERED = "clustered";
    public static final String MAX_RETRIES = "max_retries";
    public static final String TASK_COUNT = "task_count";
    public static final String UNIT_ID = "job.id";
    public static final String MAP_TASK_UNIT = "stampede.wf.map.task_job";
    public static final String UNIT_CABLES = "stampede.job.edge";
    public static final String PLAN = "stampede.wf.plan";
    public static final String RESTART_COUNT = "restart_count";
    public static final String SCHEDULER_ID = "sched.id";
    public static final String UNIT_STATE_ID = "js.id";
    public static final String UNIT_INST_ID = "job_inst.id";
    public static final String START_WAKING_TASK = "stampede.job_inst.submit.start";
    public static final String SCHEDULED = "stampede.job_inst.submit.end";
    public static final String STATUS = "status";
    public static final String JOB_START = "stampede.job_inst.main.start";
    public static final String STD_ERR_FILE = "stderr.file";
    public static final String STD_OUT_FILE = "stdout.file";
    public static final String JOB_TERM = "stampede.job_inst.main.term";
    public static final String INVOCATION_START = "stampede.inv.start";
    public static final String INVOCATION_ID = "inv.id";
    public static final String INVOCATION_END = "stampede.inv.end";
    public static final String START_TIME = "start_time";
    public static final String DURATION = "dur";
    public static final String EXECUTABLE = "executable";
    public static final String JOB_END = "stampede.job_inst.main.end";
    public static final String SITE = "site";
    public static final String HOST = "stampede.job_inst.host.info";
    public static final String IP_ADDRESS = "ip";
    public static final String HOSTNAME = "hostname";
    public static final String PARENT_UNIT = "parent.job.id";
    public static final String CHILD_UNIT = "child.job.id";
    public static final String EXIT_CODE = "exitcode";
    public static final String MULTIPLIER = "multiplier_factor";
    public static final String HELD_START = "stampede.job_inst.held.start";
    public static final String HELD_END = "stampede.job_inst.held.end";
    public static final String MAP_SUB_WORKFLOW = "stampede.xwf.map.subwf_job";
    public static final String SUB_WF_ID = "subwf.id";
    public static final String LOCAL_DURATION = "local.dur";
    public static final String REMOTE_CPU_TIME = "remote_cpu_time";
    public static final String PARENT_WF = "parent.xwf.id";


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

    public enum LEVELS {
        ;
        public static String INFO = "Info";
        public static String ERROR = "Error";
    }
}
