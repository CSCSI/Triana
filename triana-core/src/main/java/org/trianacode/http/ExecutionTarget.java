package org.trianacode.http;

import org.thinginitself.http.*;
import org.thinginitself.http.util.PathTree;
import org.thinginitself.streamable.Streamable;
import org.thinginitself.streamable.StreamableString;
import org.trianacode.TrianaInstance;
import org.trianacode.discovery.protocols.tdp.imp.trianatools.ToolResolver;
import org.trianacode.enactment.Exec;
import org.trianacode.enactment.io.IoConfiguration;
import org.trianacode.enactment.io.IoHandler;
import org.trianacode.taskgraph.tool.Tool;

import java.io.IOException;


/**
 * @author Andrew Harrison
 * @version 1.0.0 Oct 4, 2010
 */
public class ExecutionTarget implements Target {

    private ToolResolver toolResolver;
    private PathTree pathTree;

    public ExecutionTarget(ToolResolver toolResolver) {
        this.toolResolver = toolResolver;
        this.pathTree = new PathTree(getPath().toString());
    }

    @Override
    public Path getPath() {
        return new Path("run");
    }

    @Override
    public Resource getResource(RequestContext requestContext) {
        return pathTree.getResource(requestContext.getRequestPath());
    }

    @Override
    public void onGet(RequestContext requestContext) throws RequestProcessException {
        Resource r = pathTree.getResource(requestContext.getRequestPath());
        String pid = r.getPath().getLast();
        if (pid != null) {
            try {
                String status = Exec.readUuidFile(pid);
                requestContext.setResponseEntity(new StreamableString(status));
                requestContext.setResponseCode(201);
            } catch (IOException e) {
                throw new RequestProcessException("Error getting workflow status for UUID:" + pid, 400);
            }
        }

    }

    @Override
    public void onPut(RequestContext requestContext) throws RequestProcessException {
    }

    @Override
    public void onPost(RequestContext requestContext) throws RequestProcessException {
        try {
            IoHandler handler = new IoHandler();
            Streamable s = requestContext.getRequestEntity();
            final IoConfiguration config = handler.deserialize(s.getInputStream());

            String tool = config.getToolName();
            if (tool == null) {
                throw new RequestProcessException("No tool specified.", 400);
            }
            final Tool task = toolResolver.getTool(tool);
            if (task == null) {
                throw new RequestProcessException("No tool found with name:" + tool, 400);
            }
            TrianaInstance inst = toolResolver.getProperties().getEngine();
            final Exec exec = new Exec(null);
            String pid = exec.getPid();
            inst.execute(new Runnable() {
                public void run() {
                    try {
                        exec.execute(task, config);
                    } catch (Exception e) {
                        e.printStackTrace();   // TODO
                    }
                }
            });
            requestContext.setResponseEntity(new StreamableString(pid));
            requestContext.setResponseCode(201);
            pathTree.addResource(new Resource(new Path(task.getQualifiedToolName() + "/" + pid)));
        } catch (Exception e) {
            throw new RequestProcessException(e, 400);
        }


    }

    @Override
    public void onDelete(RequestContext requestContext) throws RequestProcessException {
    }

    @Override
    public void onOptions(RequestContext requestContext) throws RequestProcessException {
    }

}
