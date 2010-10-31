package org.trianacode.http;

import org.thinginitself.http.HttpPeer;
import org.thinginitself.http.MimeType;
import org.thinginitself.http.Resource;
import org.thinginitself.http.target.TargetResource;
import org.thinginitself.http.target.UrlTarget;
import org.thinginitself.streamable.Streamable;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.tool.Tool;
import org.trianacode.taskgraph.tool.ToolListener;
import org.trianacode.taskgraph.tool.ToolResolver;
import org.trianacode.taskgraph.tool.Toolbox;

import java.io.IOException;
import java.util.List;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 20, 2010
 */

public class TrianaHttpServer extends TargetResource implements ToolListener {

    private HttpPeer peer;
    private ToolboxesResource toolboxesResource = new ToolboxesResource();

    public TrianaHttpServer() {
        super(PathController.getInstance().getRoot());
        this.peer = new HttpPeer();

    }

    /**
     * adds a task that can be invoked via HTML forms.
     * Currently disabled
     *
     * @param path
     * @param task
     */
    public void addWebViewTask(String path, Task task) {
        //peer.addTarget(new ResourceSpawn(path, task, peer));
    }

    public void start(ToolResolver toolResolver) throws IOException {
        //getPathTree().addLocatable(new ExecutionTarget(toolResolver));
        getPathTree().addLocatable(new Home(getPath().toString(), toolResolver));
        getPathTree().addLocatable(toolboxesResource);
        getPathTree().addLocatable(new UrlTarget(PathController.getInstance().getResourcesRoot(), getClass().getResource("/me.txt")));
        peer.addTarget(this);
        peer.open();
    }

    public void stop() throws IOException {
        peer.close();
    }

    public HttpPeer getHTTPPeerInstance() {
        return peer;
    }

    @Override
    public void toolsAdded(List<Tool> tools) {
    }

    @Override
    public void toolsRemoved(List<Tool> tools) {
    }

    @Override
    public void toolAdded(Tool tool) {
    }

    @Override
    public void toolRemoved(Tool tool) {
    }

    @Override
    public void toolBoxAdded(Toolbox toolbox) {
        toolboxesResource.toolboxAdded(toolbox);
    }

    @Override
    public void toolBoxRemoved(Toolbox toolbox) {
        toolboxesResource.toolboxRemoved(toolbox);
    }

    @Override
    public void toolboxNameChanging(Toolbox toolbox, String newName) {
    }

    @Override
    public void toolboxNameChanged(Toolbox toolbox, String newName) {
    }

    private static class Home extends Resource {

        private ToolResolver resolver;

        public Home(String path, ToolResolver resolver) {
            super(path);
            this.resolver = resolver;
        }

        public Streamable getStreamable(List<MimeType> mimes) {
            TrianaRenderer r = new TrianaRenderer();
            r.init(resolver.getProperties().getEngine(), getPath().toString());
            return r.render(TrianaRenderer.TRIANA_TEMPLATE);
        }
    }


}
