package org.trianacode.http;

import java.util.UUID;

import org.thinginitself.http.Path;
import org.thinginitself.http.RequestContext;
import org.thinginitself.http.RequestProcessException;
import org.thinginitself.http.Resource;
import org.thinginitself.http.Target;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskGraphContext;
import org.trianacode.taskgraph.TaskGraphException;
import org.trianacode.taskgraph.interceptor.execution.ExecutionBus;
import org.trianacode.taskgraph.interceptor.execution.ExecutionQueue;
import org.trianacode.taskgraph.service.SchedulerException;
import org.trianacode.taskgraph.service.TrianaExec;
import org.trianacode.taskgraph.tool.Tool;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 20, 2010
 */

public class TaskTarget implements Target {

    private Task task;
    private Task currentTask = null;
    private ExecutionQueue queue;

    public TaskTarget(Task task) {
        this.task = task;

    }

    @Override
    public Path getPath() {
        return new Path(task.getToolName());
    }

    @Override
    public Resource getResource(RequestContext requestContext) throws RequestProcessException {

        return null;
    }

    @Override
    public void onGet(RequestContext requestContext) throws RequestProcessException {
        requestContext.setResponseEntity(new ToolRenderer(task, "templates/tool.vm").render());
    }


    @Override
    public void onPut(RequestContext requestContext) throws RequestProcessException {

    }

    /**
     * start the task and then block to wait for a running task which may need its parameters exposed to the client.
     * <p/>
     * TODO - break out of blocking at end of run.
     *
     * @param requestContext
     * @throws RequestProcessException
     */
    @Override
    public void onPost(RequestContext requestContext) throws RequestProcessException {
        TaskGraphContext context = task.getContext();
        String executionId = (String) context.getProperty(ExecutionBus.RECEIVE_ID);
        if (executionId == null) {
            executionId = UUID.randomUUID().toString();
            context.setProperty(ExecutionBus.RECEIVE_ID, executionId);
        }
        queue = new ExecutionQueue(executionId);
        ExecutionBus.addQueue(queue);
        try {
            TrianaExec exec = new TrianaExec(task);
            exec.run(new Object[0]);
            currentTask = getDisplayTask();
            ToolParameterRenderer renderer = new ToolParameterRenderer(currentTask, "templates/tool-params.vm");
            requestContext.setResponseEntity(renderer.render());
            requestContext.setSendBody(true);
        } catch (TaskGraphException e) {
            e.printStackTrace();
        } catch (SchedulerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDelete(RequestContext requestContext) throws RequestProcessException {
    }

    @Override
    public void onOptions(RequestContext requestContext) throws RequestProcessException {
    }

    private Task getDisplayTask() throws InterruptedException {
        Task t = queue.getBlockingTask();
        String guiDesc = (String) t.getParameter(Tool.GUI_BUILDER);
        boolean show = showParameters(t);
        while (guiDesc == null || !show) {
            queue.putReadyTask(t);
            t = queue.getBlockingTask();
            guiDesc = (String) t.getParameter(Tool.GUI_BUILDER);
            show = showParameters(t);
        }
        return t;
    }

    private boolean showParameters(Task task) {
        return !task.isParameterName(Tool.PARAM_PANEL_INSTANTIATE)
                || task.getParameter(Tool.PARAM_PANEL_INSTANTIATE).equals(Tool.ON_TASK_INSTANTIATION);
    }
}
