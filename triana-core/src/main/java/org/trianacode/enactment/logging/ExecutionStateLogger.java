package org.trianacode.enactment.logging;

import org.trianacode.taskgraph.service.ExecutionEvent;
import org.trianacode.taskgraph.service.ExecutionListener;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Oct 1, 2010
 */
public class ExecutionStateLogger implements ExecutionListener {

    @Override
    public void executionStateChanged(ExecutionEvent event) {
        Loggers.EXECUTION_STATE_LOGGER.debug(event.getTask().getQualifiedTaskName() + ": " + event.getState());
    }

    @Override
    public void executionRequested(ExecutionEvent event) {
        Loggers.EXECUTION_STATE_LOGGER.debug(event.getTask().getQualifiedTaskName() + ": " + event.getState());
    }

    @Override
    public void executionStarting(ExecutionEvent event) {
        Loggers.EXECUTION_STATE_LOGGER.debug(event.getTask().getQualifiedTaskName() + ": " + event.getState());
    }

    @Override
    public void executionFinished(ExecutionEvent event) {
        Loggers.EXECUTION_STATE_LOGGER.debug(event.getTask().getQualifiedTaskName() + ": " + event.getState());
    }

    @Override
    public void executionReset(ExecutionEvent event) {
        Loggers.EXECUTION_STATE_LOGGER.debug(event.getTask().getQualifiedTaskName() + ": " + event.getState());
    }
}
