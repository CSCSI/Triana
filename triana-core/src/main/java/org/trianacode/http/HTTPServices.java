package org.trianacode.http;

import org.thinginitself.http.HttpPeer;
import org.trianacode.taskgraph.tool.ToolResolver;

import java.io.IOException;

/**
 * Starts off the HTTP and discovery services...
 * <p/>
 * User: scmijt Date: Jul 30, 2010 Time: 12:06:44 PM
 */
public class HTTPServices {

    private TrianaHttpServer workflowServer;
    private HttpPeer httpEngine;

    public HTTPServices() {
        workflowServer = new TrianaHttpServer();
        httpEngine = workflowServer.getHTTPPeerInstance();
    }

    public void startServices(ToolResolver resolver) throws IOException {
        workflowServer.start(resolver);

    }

    public TrianaHttpServer getWorkflowServer() {
        return workflowServer;
    }

    public HttpPeer getHttpEngine() {
        return httpEngine;
    }

}
