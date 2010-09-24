package org.trianacode.http;

import org.thinginitself.http.HttpPeer;
import org.trianacode.discovery.DiscoverTools;
import org.trianacode.taskgraph.tool.ToolResolver;

/**
 * Starts off the HTTP and discovery services...
 * <p/>
 * User: scmijt Date: Jul 30, 2010 Time: 12:06:44 PM To change this template use File | Settings | File Templates.
 */
public class HTTPServices {
    TrianaHttpServer workflowServer;
    HttpPeer httpEngine;

    public HTTPServices() {
    }

    public void startServices(ToolResolver resolver) throws Exception {

        // start a http server first

        workflowServer = new TrianaHttpServer();
        workflowServer.start();

        httpEngine = workflowServer.getHTTPPeerInstance();

    }

    public TrianaHttpServer getWorkflowServer() {
        return workflowServer;
    }

    public HttpPeer getHttpEngine() {
        return httpEngine;
    }

}
