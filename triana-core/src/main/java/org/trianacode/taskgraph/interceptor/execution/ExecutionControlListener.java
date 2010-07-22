package org.trianacode.taskgraph.interceptor.execution;

import org.trianacode.taskgraph.Task;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 22, 2010
 */

public interface ExecutionControlListener {

    public void executionSuspended(Task task);

    public void executionComplete(Task task);
}
