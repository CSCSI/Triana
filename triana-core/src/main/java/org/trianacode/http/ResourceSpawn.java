package org.trianacode.http;

import java.util.concurrent.atomic.AtomicInteger;

import org.thinginitself.http.RequestContext;
import org.thinginitself.http.RequestProcessException;
import org.thinginitself.http.Resource;
import org.thinginitself.http.target.MemoryTarget;
import org.trianacode.taskgraph.Task;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 23, 2010
 */

public class ResourceSpawn extends MemoryTarget {

    private Task task;
    private AtomicInteger count = new AtomicInteger(0);

    public ResourceSpawn(Task task) {
        super(task.getToolName());
        this.task = task;
        ToolRenderer r = RendererRegistry.getToolRenderer(ToolRenderer.TOOL_DESCRIPTION_TEMPLATE);
        r.init(task, task.getToolName());
        Resource res = new Resource(task.getToolName(), r.render());
        store.put(res);
    }

    public void onPost(RequestContext context) throws RequestProcessException {
        String path = task.getToolName() + "/" + count.incrementAndGet();
        TaskResource res = new TaskResource(task, path);
        store.put(res);
        ToolRenderer r = RendererRegistry.getToolRenderer(ToolRenderer.TOOL_INSTANCE_TEMPLATE);
        r.init(task, path);
        context.setResponseEntity(r.render());
    }
}
