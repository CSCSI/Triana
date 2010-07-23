package org.trianacode.http;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

import org.thinginitself.http.Http;
import org.thinginitself.http.Path;
import org.thinginitself.http.RequestContext;
import org.thinginitself.http.RequestProcessException;
import org.thinginitself.http.Resource;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.interceptor.execution.ExecutionControlListener;
import org.trianacode.taskgraph.interceptor.execution.ExecutionController;
import org.trianacode.taskgraph.tool.Tool;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 20, 2010
 */

public class TaskResource extends Resource implements ExecutionControlListener {

    private Task task;
    private ExecutionController controller;
    private BlockingQueue<Renderer> nextTask = new SynchronousQueue<Renderer>();
    private boolean started = false;


    public TaskResource(Task task, String path) {
        super(new Path(path),
                new Http.Method[]{Http.Method.POST});
        this.task = task;
        controller = new ExecutionController(task, this);

    }

    public Resource getResource(RequestContext context) throws RequestProcessException {
        if (context.getRequestTarget().equals(getPath().toString())) {
            return this;
        }
        return null;
    }

    @Override
    public void onPost(RequestContext requestContext) throws RequestProcessException {
        if (!started) {
            started = true;
            controller.begin();
        } else {
            controller.resume();
        }
        try {
            Renderer renderer = nextTask.take();

            requestContext.setResponseEntity(renderer.render());
            requestContext.setSendBody(true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean isDisplayTask(Task t) {
        /*String guiDesc = (String) t.getParameter(Tool.GUI_BUILDER);
        boolean show = showParameters(t);
        if (guiDesc != null && show) {
            return true;
        }
        return false;*/
        return true;
    }

    private boolean showParameters(Task task) {
        return !task.isParameterName(Tool.PARAM_PANEL_INSTANTIATE)
                || task.getParameter(Tool.PARAM_PANEL_INSTANTIATE).equals(Tool.ON_TASK_INSTANTIATION);
    }

    @Override
    public void executionSuspended(Task task) {
        System.out.println("TaskResource.executionSuspended for task " + task.getToolName());
        if (isDisplayTask(task)) {
            try {
                ToolParameterRenderer renderer = new ToolParameterRenderer(this.task, task, getPath().toString(),
                        "/templates/tool-params.tpl");
                nextTask.put(renderer);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            controller.resume();
        }
    }

    @Override
    public void executionComplete(Task task) {
        try {
            TaskCompleteRenderer renderer = new TaskCompleteRenderer(task, "/templates/tool-complete.tpl");
            nextTask.put(renderer);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
