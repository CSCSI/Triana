package org.trianacode.http;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.thinginitself.http.HttpPeer;
import org.thinginitself.http.Path;
import org.thinginitself.http.RequestContext;
import org.thinginitself.http.RequestProcessException;
import org.thinginitself.http.Resource;
import org.thinginitself.http.Target;
import org.thinginitself.http.util.PathTree;
import org.thinginitself.streamable.Streamable;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskGraphException;
import org.trianacode.taskgraph.ser.XMLReader;
import org.trianacode.taskgraph.tool.Tool;
import org.trianacode.taskgraph.tool.ToolListener;
import org.trianacode.taskgraph.tool.Toolbox;
import org.trianacode.taskgraph.util.UrlUtils;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 20, 2010
 */

public class TrianaHttpServer implements Target, ToolListener {

    private HttpPeer peer;
    private PathTree pathTree;
    private String path = "triana";

    public TrianaHttpServer() {
        peer = new HttpPeer();
        peer.addTarget(this);
        pathTree = new PathTree(path);
    }

    public void addTask(ResourceSpawn task) {
        peer.addTarget(task);
    }


    /**
     * Adds a blob of data (Java object) and makes it available via restless as a resource
     *
     * @param deploymentURL
     * @param workflowObject
     */
    public void addDataResource(String deploymentURL, Serializable workflowObject) {
        pathTree.addResource(new DataResource(getPath().append(deploymentURL).toString(), workflowObject));
    }

    public void start() throws IOException {
        peer.open();
    }

    public void stop() throws IOException {
        peer.close();
    }

    public void addToolbox(Toolbox toolbox) {
        Path toolboxPath = getPath().append(UrlUtils.createToolboxPath(toolbox.getName()));
        pathTree.addResource(new ToolboxResource(toolboxPath, toolbox));
    }

    public void addTool(Tool tool) {
        Path path = getPath().append(UrlUtils.createToolboxPath(tool.getToolBox().getName()))
                .append((UrlUtils.createToolPath(tool.getQualifiedToolName())));
        System.out.println("TrianaHttpServer.addTool " + path);
        pathTree.addResource(new ToolResource(path, tool));

    }

    public void removeTool(Tool tool) {
        Path path = getPath()//.append(UrlUtils.createToolboxPath(tool.getToolBox()))
                .append((UrlUtils.createToolPath(tool.getQualifiedToolName())));
        pathTree.removeResource(path.toString());

    }

    /**
     * Adds a workflow to the resource tree of this Http server
     *
     * @param workflowFile
     * @throws IOException
     * @throws TaskGraphException
     */
    public void addWorkflow(String workflowFile) throws IOException, TaskGraphException {
        Reader r = null;
        try {
            URL url = new URL(workflowFile);
            // r = new UrlReader(url);
        } catch (MalformedURLException e) {
            r = new FileReader(workflowFile);
        }
        XMLReader reader = new XMLReader(r);
        ResourceSpawn res = new ResourceSpawn((Task) reader.readComponent());
        addTask(res);
    }

    public HttpPeer getHTTPPeerInstance() {
        return peer;
    }

    @Override
    public Path getPath() {
        return new Path(path);
    }

    private void process(RequestContext requestContext) {
        Resource r = requestContext.getResource();
        if (r.methodIsAllowed(requestContext.getMethod())) {
            Streamable s = r.getStreamable();
            if (s != null) {
                requestContext.setResponseEntity(s);
            }
        }
    }

    @Override
    public Resource getResource(RequestContext requestContext) throws RequestProcessException {
        System.out.println("TrianaHttpServer.getResource CALLED with " + requestContext.getRequestPath());
        Resource r = pathTree.getResource(requestContext.getRequestPath());
        System.out.println("TrianaHttpServer.getResource RESOURCE IS " + r);
        return r;
    }

    @Override
    public void onGet(RequestContext requestContext) throws RequestProcessException {
        process(requestContext);
    }

    @Override
    public void onPut(RequestContext requestContext) throws RequestProcessException {
        process(requestContext);
    }

    @Override
    public void onPost(RequestContext requestContext) throws RequestProcessException {
        process(requestContext);
    }

    @Override
    public void onDelete(RequestContext requestContext) throws RequestProcessException {
        process(requestContext);
    }

    @Override
    public void onOptions(RequestContext requestContext) throws RequestProcessException {
    }

    @Override
    public void toolsAdded(List<Tool> tools) {
        for (Tool tool : tools) {
            addTool(tool);
        }
    }

    @Override
    public void toolsRemoved(List<Tool> tools) {
        for (Tool tool : tools) {
            removeTool(tool);
        }
    }

    @Override
    public void toolAdded(Tool tool) {
        addTool(tool);
    }

    @Override
    public void toolRemoved(Tool tool) {
        removeTool(tool);
    }

    @Override
    public void toolBoxAdded(Toolbox toolbox) {
    }

    @Override
    public void toolBoxRemoved(Toolbox toolbox) {
    }
}
