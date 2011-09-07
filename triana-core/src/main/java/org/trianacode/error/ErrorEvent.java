package org.trianacode.error;

import org.trianacode.taskgraph.Task;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 10/07/2011
 * Time: 23:51
 * To change this template use File | Settings | File Templates.
 */
public class ErrorEvent {
    Task task;
    Throwable thrown;
    String errorMsg;
    String interest = ErrorTracker.ALLERRORS;

    public ErrorEvent(Task task, Throwable thrown, String errorMsg) {
        this.task = task;
        this.thrown = thrown;
        this.errorMsg = errorMsg;
    }

    public Task getTask() {
        return task;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public Throwable getThrown() {
        return thrown;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }
}
