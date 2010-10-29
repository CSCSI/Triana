package org.trianacode.http;

import org.thinginitself.http.*;
import org.thinginitself.http.target.TargetResource;
import org.thinginitself.streamable.Streamable;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.tool.Tool;
import org.trianacode.taskgraph.tool.ToolListener;
import org.trianacode.taskgraph.tool.ToolResolver;
import org.trianacode.taskgraph.tool.Toolbox;
import org.trianacode.taskgraph.util.UrlUtils;

import java.io.IOException;
import java.util.List;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 20, 2010
 */

public class TrianaHttpServer extends TargetResource implements ToolListener {

    private HttpPeer peer;
    private String path = "triana";
    private ToolResolver toolResolver;

    public TrianaHttpServer() {
        super("triana");
        this.peer = new HttpPeer();
    }

    /**
     * adds a task that can be invoked via HTML forms.
     *
     * @param path
     * @param task
     */
    public void addWebViewTask(String path, Task task) {
        peer.addTarget(new ResourceSpawn(path, task, peer));
    }

    public void start(ToolResolver toolResolver) throws IOException {
        peer.addTarget(this);
        peer.addTarget(new ExecutionTarget(toolResolver));
        peer.open();
    }

    public void stop() throws IOException {
        peer.close();
    }

    public void addToolbox(Toolbox toolbox) {
        Path toolboxPath = getPath().append(UrlUtils.createPath(toolbox.getName()));
        getPathTree().addLocatable(new ToolboxResource(toolboxPath, toolbox));
    }

    public void addTool(Tool tool) {
        Path path = getPath().append(UrlUtils.createPath(tool.getToolBox().getName()))
                .append((UrlUtils.createPath(tool.getQualifiedToolName())));
        getPathTree().addLocatable(new ToolResource(path, tool));

    }

    public void removeTool(Tool tool) {
        Path path = getPath().append(UrlUtils.createPath(tool.getToolBox().getName()))
                .append((UrlUtils.createPath(tool.getQualifiedToolName())));
        getPathTree().removeLocatable(path.toString());

    }

    public HttpPeer getHTTPPeerInstance() {
        return peer;
    }

    public Path getPath() {
        return new Path(path);
    }

    private void process(RequestContext requestContext) {
        Resource r = requestContext.getResource();
        if (r.methodIsAllowed(requestContext.getMethod())) {
            Streamable s = r.getStreamable();
            if (s != null) {
                requestContext.setResponseEntity(s);
            } else {
                requestContext.setResponseCode(404);
            }
        } else {
            requestContext.setResponseCode(405);
        }
    }

    public Resource getResource(RequestContext requestContext) throws RequestProcessException {
        Resource r = getPathTree().getLocatable(requestContext.getRequestPath());
        return r;
    }

    public void onGet(RequestContext requestContext) throws RequestProcessException {
        process(requestContext);
    }

    public void onPut(RequestContext requestContext) throws RequestProcessException {
        process(requestContext);
    }

    public void onPost(RequestContext requestContext) throws RequestProcessException {
        process(requestContext);
    }

    public void onDelete(RequestContext requestContext) throws RequestProcessException {
        process(requestContext);
    }

    public void onOptions(RequestContext requestContext) throws RequestProcessException {
    }

    @Override
    public Streamable get(Path path, List<MimeType> mimeTypes) {
        return null;
    }

    @Override
    public Streamable put(Path path, List<MimeType> mimeTypes, Streamable streamable) {
        return null;
    }

    @Override
    public Streamable post(Path path, List<MimeType> mimeTypes, Streamable streamable) {
        return null;
    }

    @Override
    public Streamable delete(Path path, List<MimeType> mimeTypes) {
        return null;
    }

    public void toolsAdded(List<Tool> tools) {
        for (Tool tool : tools) {
            addTool(tool);
        }
    }

    public void toolsRemoved(List<Tool> tools) {
        for (Tool tool : tools) {
            removeTool(tool);
        }
    }

    public void toolAdded(Tool tool) {
        addTool(tool);
    }

    public void toolRemoved(Tool tool) {
        removeTool(tool);
    }

    public void toolBoxAdded(Toolbox toolbox) {
    }

    public void toolBoxRemoved(Toolbox toolbox) {
    }

    @Override
    public void toolboxNameChanging(Toolbox toolbox, String newName) {
    }

    @Override
    public void toolboxNameChanged(Toolbox toolbox, String newName) {
    }


}
