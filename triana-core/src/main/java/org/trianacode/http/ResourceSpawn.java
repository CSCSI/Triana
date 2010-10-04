package org.trianacode.http;

import org.apache.commons.logging.Log;
import org.thinginitself.http.HttpPeer;
import org.thinginitself.http.RequestContext;
import org.thinginitself.http.RequestProcessException;
import org.thinginitself.http.Resource;
import org.thinginitself.http.target.MemoryTarget;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.taskgraph.Task;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 23, 2010
 */

public class ResourceSpawn extends MemoryTarget {

    private static Log log = Loggers.TOOL_LOGGER;


    private Task task;
    private AtomicInteger count = new AtomicInteger(0);
    private HttpPeer peer;

    public ResourceSpawn(String path, Task task, HttpPeer peer) {
        super(path);
        this.task = task;
        ToolRenderer r = RendererRegistry.getToolRenderer(ToolRenderer.TOOL_CREATE_INSTANCE_TEMPLATE);
        r.init(task, task.getToolName());
        Resource res = new Resource(getPath().append(task.getToolName()),
                r.render(ToolRenderer.TOOL_CREATE_INSTANCE_TEMPLATE));
        store.put(res);
        this.peer = peer;
    }


    public void onPost(RequestContext context) throws RequestProcessException {
        log.debug("ResourceSpawn.onPost ENTER " + context.getRequestTarget());
        String path = task.getToolName() + "/" + count.incrementAndGet() + "/";
        TaskResource res = new TaskResource(task, getPath().append(path).toString(), peer);
        peer.addTarget(res);
        ToolRenderer r = RendererRegistry.getToolRenderer(ToolRenderer.TOOL_INSTANCE_TEMPLATE);
        r.init(task, path);
        context.setResponseEntity(r.render(ToolRenderer.TOOL_INSTANCE_TEMPLATE));
    }

    public org.thinginitself.http.Resource getResource(
            org.thinginitself.http.RequestContext context) {

        log.debug("ResourceSpawn.getResource path:" + context.getRequestPath());
        log.debug("ResourceSpawn.getResource target:" + context.getRequestTarget());
        return super.getResource(context);
    }

}
