package org.trianacode.http;

import org.thinginitself.http.HttpPeer;
import org.thinginitself.http.MimeType;
import org.thinginitself.http.Resource;
import org.thinginitself.http.target.TargetResourceAdapter;
import org.thinginitself.http.target.UrlTarget;
import org.thinginitself.streamable.Streamable;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.tool.ToolResolver;

import java.io.IOException;
import java.util.List;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 20, 2010
 */

public class TrianaHttpServer extends TargetResourceAdapter {

    private HttpPeer peer;
    private ToolResolver toolResolver;

    public TrianaHttpServer() {
        super(PathController.getRoot());
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
        getPathTree().addLocatable(new ToolboxesResource(toolResolver));
        peer.addTarget(this);
        peer.addTarget(new UrlTarget(PathController.getResourcesRoot(), getClass().getResource("/me.txt")));
        peer.open();
    }

    public void stop() throws IOException {
        peer.close();
    }

    public HttpPeer getHTTPPeerInstance() {
        return peer;
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
