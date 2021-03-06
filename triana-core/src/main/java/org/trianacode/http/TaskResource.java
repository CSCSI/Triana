package org.trianacode.http;

import org.apache.commons.logging.Log;
import org.thinginitself.http.HttpPeer;
import org.thinginitself.http.RequestContext;
import org.thinginitself.http.RequestProcessException;
import org.thinginitself.http.Resource;
import org.thinginitself.http.target.TargetResource;
import org.trianacode.config.TrianaProperties;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.interceptor.execution.ExecutionControlListener;
import org.trianacode.taskgraph.interceptor.execution.ExecutionController;
import org.trianacode.taskgraph.tool.Tool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 20, 2010
 */

public class TaskResource extends TargetResource implements ExecutionControlListener {

    private static Log log = Loggers.TOOL_LOGGER;

    private Task task;
    private ExecutionController controller;
    private BlockingQueue<RenderInfo> nextTask = new SynchronousQueue<RenderInfo>();
    private boolean started = false;
    private HttpPeer peer;
    private Task currentTask = null;


    public TaskResource(Task task, String path, HttpPeer peer) {
        super(path);
        this.task = task;
        this.peer = peer;
        controller = new ExecutionController(task, this);

    }

    public Task getTask() {
        return task;
    }

    public Resource getResource(RequestContext context) throws RequestProcessException {
        log.debug("TaskResource.getResource " + context.getRequestTarget());
        log.debug("TaskResource.getResource me " + getPath().toString());
        if (context.getRequestTarget().toString().startsWith(getPath().toString())) {
            return this;
        }
        return null;
    }

    @Override
    public void onPost(RequestContext requestContext) throws RequestProcessException {
        log.debug("TaskResource.onPost ENTER " + requestContext.getRequestTarget());
        if (currentTask != null) {
            // get the POST data here I think and set the parameters.
        }
        if (!started) {
            started = true;
            controller.begin();
        } else {
            controller.resume();
        }
        try {
            RenderInfo info = nextTask.take();
            requestContext.setResponseEntity(info.getRenderer().render(info.getTemplate(), "text/html"));
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
        log.debug("TaskResource.executionSuspended for task " + task.getToolName());
        if (isDisplayTask(task)) {
            try {
                ToolRenderer r = new ToolRenderer();
                r.init(task, task.getToolName());
                currentTask = task;
                nextTask.put(new RenderInfo(r, TrianaProperties.TOOL_PARAMETER_WINDOW_TEMPLATE_PROPERTY));
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
            ToolRenderer r = new ToolRenderer();
            r.init(task, task.getToolName());
            nextTask.put(new RenderInfo(r, TrianaProperties.TOOL_COMPLETED_TEMPLATE_PROPERTY));
            peer.removeTarget(this);
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
