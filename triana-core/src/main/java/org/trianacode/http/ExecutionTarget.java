package org.trianacode.http;

import org.thinginitself.http.Path;
import org.thinginitself.http.RequestContext;
import org.thinginitself.http.RequestProcessException;
import org.thinginitself.http.Resource;
import org.thinginitself.http.target.TargetResource;
import org.thinginitself.http.util.PathTree;
import org.thinginitself.streamable.Streamable;
import org.thinginitself.streamable.StreamableStream;
import org.thinginitself.streamable.StreamableString;
import org.trianacode.TrianaInstance;
import org.trianacode.enactment.Exec;
import org.trianacode.enactment.io.IoConfiguration;
import org.trianacode.enactment.io.IoHandler;
import org.trianacode.taskgraph.tool.Tool;
import org.trianacode.taskgraph.tool.ToolResolver;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


/**
 * @author Andrew Harrison
 * @version 1.0.0 Oct 4, 2010
 */
public class ExecutionTarget extends TargetResource {

    private ToolResolver toolResolver;
    private PathTree pathTree;

    public ExecutionTarget(ToolResolver toolResolver) {
        super("exec");
        this.toolResolver = toolResolver;
    }

    @Override
    public void onGet(RequestContext requestContext) throws RequestProcessException {
        Path path = requestContext.getRequestTarget();
        String[] comps = path.getComponents();
        if (comps.length < 2) {
            requestContext.setResponseEntity(new StreamableString("POST me a workflow to run :-)"));
            return;
        }
        if (comps.length == 2) {
            String target = comps[comps.length - 1];
            Resource r = getPathTree().getLocatable(target);
            if (r.getPath().equals(getPath())) {
                requestContext.setResponseCode(404);
            }
            try {
                Exec exec = new Exec(target);
                String status = exec.readUuidFile();

                requestContext.setResponseEntity(new StreamableString(status));
                requestContext.setResponseCode(201);
            } catch (IOException e) {
                requestContext.setResponseCode(404);
            }
        } else if (comps.length == 3) {
            String target = comps[comps.length - 2];
            String data = comps[comps.length - 1];
            Exec exec = new Exec(target);
            try {
                InputStream in = exec.readData(data);
                if (in != null) {
                    requestContext.setResponseCode(200);
                    requestContext.setResponseEntity(new StreamableStream(in));
                    return;
                }
            } catch (FileNotFoundException e) {

            }
            requestContext.setResponseCode(404);
        }

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
            getPathTree().addLocatable(new Resource(new Path(pid)));

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
        //requestContext.setResponseEntity(StreamableOptions.newOptions(Http.Method.GET, Http.Method.POST));
    }


}
