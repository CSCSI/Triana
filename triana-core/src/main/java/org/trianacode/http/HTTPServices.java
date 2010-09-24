package org.trianacode.http;

import org.thinginitself.http.HttpPeer;
import org.trianacode.discovery.protocols.tdp.imp.trianatools.ToolResolver;

/**
 * Starts off the HTTP and discovery services...
 * <p/>
 * User: scmijt Date: Jul 30, 2010 Time: 12:06:44 PM To change this template use File | Settings | File Templates.
 */
public class HTTPServices {
    TrianaHttpServer workflowServer;
    HttpPeer httpEngine;

    public HTTPServices() {
        workflowServer = new TrianaHttpServer();
        httpEngine = workflowServer.getHTTPPeerInstance();
    }

    public void startServices(ToolResolver resolver) throws Exception {

        // start a http server first

        workflowServer.start();

    }

    public TrianaHttpServer getWorkflowServer() {
        return workflowServer;
    }

    public HttpPeer getHttpEngine() {
        return httpEngine;
    }

}
