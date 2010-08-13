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
    private BlockingQueue<RenderInfo> nextTask = new SynchronousQueue<RenderInfo>();
    private boolean started = false;


    public TaskResource(Task task, String path) {
        super(new Path(path),
                new Http.Method[]{Http.Method.POST});
        this.task = task;
        controller = new ExecutionController(task, this);

    }

    public Task getTask() {
        return task;
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
            RenderInfo info = nextTask.take();
            requestContext.setResponseEntity(info.getRenderer().render(info.getTemplate()));
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
                ToolRenderer r = RendererRegistry.getToolRenderer(ToolRenderer.TOOL_PARAMETER_TEMPLATE);
                r.init(task, task.getToolName());
                nextTask.put(new RenderInfo(r, ToolRenderer.TOOL_PARAMETER_TEMPLATE));
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
            ToolRenderer r = RendererRegistry.getToolRenderer(ToolRenderer.TOOL_COMPLETED_TEMPLATE);
            r.init(task, task.getToolName());
            nextTask.put(new RenderInfo(r, ToolRenderer.TOOL_COMPLETED_TEMPLATE));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static class RenderInfo {
        private Renderer renderer;
        private String template;

        private RenderInfo(Renderer renderer, String template) {
            this.renderer = renderer;
            this.template = template;
        }

        public Renderer getRenderer() {
            return renderer;
        }

        public String getTemplate() {
            return template;
        }
    }
}
