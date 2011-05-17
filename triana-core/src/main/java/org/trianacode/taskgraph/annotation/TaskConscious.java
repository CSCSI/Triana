package org.trianacode.taskgraph.annotation;

import org.trianacode.taskgraph.Task;

/**
 * Gets an actual handle to the Task itself.
 * One step further than the TaskAware interface.
 *
 * @author Andrew Harrison
 * @version 1.0.0 17/05/2011
 */
public interface TaskConscious {

    public void setTask(Task task);


}
