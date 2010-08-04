package org.trianacode.http;

import mil.navy.nrl.discovery.WebBootstrap;
import mil.navy.nrl.discovery.api.DiscoveredServicesInterface;
import mil.navy.nrl.discovery.types.ServiceTypes;
import mil.navy.nrl.discovery.web.template.WebDefines;
import org.thinginitself.http.HttpPeer;
import org.trianacode.discovery.DiscoverTools;
import org.trianacode.discovery.DiscoveredTools;

/**
 * Starts off the HTTP and discovery services...
 *
 * User: scmijt
 * Date: Jul 30, 2010
 * Time: 12:06:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class HTTPServices {
    static TrianaHttpServer workflowServer;
    static HttpPeer httpEngine;
    static DiscoverTools discoverTools;

    public HTTPServices() {}

    public void startServices() throws Exception {

        // start a http server first
        
        workflowServer = new TrianaHttpServer();
        workflowServer.start();

        httpEngine =  workflowServer.getHTTPPeerInstance();

        discoverTools=new DiscoverTools(httpEngine);
    }

    public static TrianaHttpServer getWorkflowServer() {
        return workflowServer;
    }

    public static HttpPeer getHttpEngine() {
        return httpEngine;
    }

}
