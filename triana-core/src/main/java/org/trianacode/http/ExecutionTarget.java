package org.trianacode.http;

import org.thinginitself.http.*;
import org.thinginitself.http.util.PathTree;
import org.thinginitself.http.util.StreamableOptions;
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
        return pathTree.getResource(requestContext.getRequestTarget());
    }

    @Override
    public void onGet(RequestContext requestContext) throws RequestProcessException {
        System.out.println("ExecutionTarget.onGet path:" + requestContext.getRequestTarget());
        String pid = new Path(requestContext.getRequestTarget()).getLast();
        Resource r = pathTree.getResource(pid);
        System.out.println("ExecutionTarget.onGet resource path:" + r.getPath());
        if (r.getPath().equals(getPath())) {
            requestContext.setResponseEntity(new StreamableString("POST to me to run a workflow. :-)"));
            return;
        }

        System.out.println("ExecutionTarget.onGet PID:" + pid);
        try {
            Exec exec = new Exec(pid);
            String status = exec.readUuidFile();
            requestContext.setResponseEntity(new StreamableString(status));
            requestContext.setResponseCode(201);
        } catch (IOException e) {
            throw new RequestProcessException("Error getting workflow status for UUID:" + pid, 400);
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
            exec.createFile();
            String pid = exec.getPid();
            System.out.println("ExecutionTarget.onPost got exec pid:" + pid);
            pathTree.addResource(new Resource(new Path(pid)));

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
        } catch (Exception e) {
            throw new RequestProcessException(e, 400);
        }


    }

    @Override
    public void onDelete(RequestContext requestContext) throws RequestProcessException {
        requestContext.setResponseCode(405);
    }

    @Override
    public void onOptions(RequestContext requestContext) throws RequestProcessException {
        requestContext.setResponseEntity(StreamableOptions.newOptions(Http.Method.GET, Http.Method.POST));
    }

}
