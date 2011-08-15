package org.trianacode.shiwa.iwir.factory;

import org.shiwa.fgi.iwir.*;
import org.trianacode.shiwa.iwir.execute.Executable;
import org.trianacode.shiwa.iwir.holders.*;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 24/06/2011
 * Time: 15:09
 * To change this template use File | Settings | File Templates.
 */
public class TaskHolderFactory {

    private static TaskHolderFactory taskHolderFactory = new TaskHolderFactory();

    private TaskHolderFactory() {
    }

    public static TaskHolderFactory getTaskHolderFactory() {
        return taskHolderFactory;
    }

    public TaskHolder getTaskHolder(AbstractTask iwirTask) {
        TaskHolder taskHolder = null;
        if (iwirTask instanceof ForEachTask) {
            taskHolder = new ForEachTaskHolder();
        }
        if (iwirTask instanceof ForTask) {
            taskHolder = new ForTaskHolder();
        }
        if (iwirTask instanceof IfTask) {
            taskHolder = new IfTaskHolder();
        }
        if (iwirTask instanceof ParallelForEachTask) {
            taskHolder = new ParallelForEachTaskHolder();
        }
        if (iwirTask instanceof ParallelForTask) {
            taskHolder = new ParallelForTaskHolder();
        }
        if (iwirTask instanceof WhileTask) {
            taskHolder = new WhileTaskHolder();
        }
        if (taskHolder == null) {
            taskHolder = new AtomicTaskHolder();
        }
        taskHolder.setIWIRTask(iwirTask);
        taskHolder.registerIWIRTask(iwirTask);

        //TODO
        taskHolder.setExecutable(new Executable());
        return taskHolder;
    }


}
