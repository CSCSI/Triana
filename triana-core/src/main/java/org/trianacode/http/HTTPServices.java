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
    static TrianaHttpServer workflowServer;
    static HttpPeer httpEngine;
    static DiscoverTools discoverTools;

    public HTTPServices() {
    }

    public void startServices(ToolResolver resolver) throws Exception {

        // start a http server first

        workflowServer = new TrianaHttpServer();
        workflowServer.start();

        httpEngine = workflowServer.getHTTPPeerInstance();

        discoverTools = new DiscoverTools(httpEngine, resolver);
    }

    public static TrianaHttpServer getWorkflowServer() {
        return workflowServer;
    }

    public static HttpPeer getHttpEngine() {
        return httpEngine;
    }

}
