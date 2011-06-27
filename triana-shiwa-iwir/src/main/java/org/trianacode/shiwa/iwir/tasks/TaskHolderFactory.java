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
        if(iwirTask instanceof ForEachTask){
            return new ForEachTaskHolder();
        }
        if(iwirTask instanceof ForTask){
            return new ForEachTaskHolder();
        }
        if(iwirTask instanceof IfTask){
            return new ForEachTaskHolder();
        }
        if(iwirTask instanceof ParallelForEachTask){
            return new ForEachTaskHolder();
        }
        if(iwirTask instanceof ParallelForTask){
            return new ForEachTaskHolder();
        }
        if(iwirTask instanceof WhileTask){
            return new ForEachTaskHolder();
        }
        if(iwirTask instanceof Task){
            return new AtomicTaskHolder();
        }
        return null;
    }


}
