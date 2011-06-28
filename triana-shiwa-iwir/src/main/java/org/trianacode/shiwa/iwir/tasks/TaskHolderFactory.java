package org.trianacode.shiwa.iwir.tasks;

import org.shiwa.fgi.iwir.*;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 24/06/2011
 * Time: 15:09
 * To change this template use File | Settings | File Templates.
 */
public class TaskHolderFactory {

    private static TaskHolderFactory taskHolderFactory = new TaskHolderFactory();

    private TaskHolderFactory(){}

    public static TaskHolderFactory getTaskHolderFactory(){
        return taskHolderFactory;
    }

    public TaskHolder getTaskHolder(AbstractTask iwirTask){
        TaskHolder taskHolder = null;
        if(iwirTask instanceof ForEachTask){
            taskHolder = new ForEachTaskHolder();
        }
        if(iwirTask instanceof ForTask){
            taskHolder = new ForEachTaskHolder();
        }
        if(iwirTask instanceof IfTask){
            taskHolder = new ForEachTaskHolder();
        }
        if(iwirTask instanceof ParallelForEachTask){
            taskHolder = new ForEachTaskHolder();
        }
        if(iwirTask instanceof ParallelForTask){
            taskHolder = new ForEachTaskHolder();
        }
        if(iwirTask instanceof WhileTask){
            taskHolder = new ForEachTaskHolder();
        }
        if(taskHolder == null){
            taskHolder = new AtomicTaskHolder();
        }
        taskHolder.setIWIRTask(iwirTask);
        return taskHolder;
    }


}
