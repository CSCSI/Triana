package org.trianacode.shiwaall.iwir.factory;

import org.shiwa.fgi.iwir.*;
import org.trianacode.shiwaall.iwir.holders.*;
import org.trianacode.shiwaall.iwir.importer.utils.ToolUtils;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskException;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.Unit;
import org.trianacode.taskgraph.proxy.java.JavaProxy;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
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

    public Task addTaskHolder(AbstractTask iwirTask, TaskGraph taskGraph) throws TaskException {
        TaskHolder holder = getTaskHolder(iwirTask);
        Task task = taskGraph.createTask(ToolUtils.initTool(holder, taskGraph.getProperties()));

        if (iwirTask instanceof org.shiwa.fgi.iwir.Task) {
            String taskType = ((org.shiwa.fgi.iwir.Task) iwirTask).getTasktype();
            Unit unit = ((JavaProxy)task.getProxy()).getUnit();
            if(unit instanceof AtomicTaskHolder){
//                ((AtomicTaskHolder) unit).setExecutable(new Executable(taskType));
            }
        }
        return task;
    }

    private TaskHolder getTaskHolder(AbstractTask iwirTask){
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
//            if (iwirTask instanceof Task) {
//                String taskType = ((Task) iwirTask).getTasktype();
//                taskHolder.setExecutable(new Executable(taskType));
//            }
        }
        taskHolder.setIWIRTask(iwirTask);
        taskHolder.registerIWIRTask(iwirTask);

        //TODO
        return taskHolder;
    }


}
