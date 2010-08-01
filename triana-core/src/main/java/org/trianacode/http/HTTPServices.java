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
    static WebBootstrap bonjourServer;
    static DiscoveredServicesInterface discoveredServices;
    static DiscoverTools discoverTools;

    public HTTPServices() {}

    public void startServices() throws Exception {

        // start a http server first
        
        workflowServer = new TrianaHttpServer();
        workflowServer.start();

        httpEngine =  workflowServer.getHTTPPeerInstance();

        ServiceTypes st = new ServiceTypes();

        st.registerServiceType("http._tcp", "HTTP is cool....");

        WebDefines webDefines = new WebDefines(null,null,null,null,null);

        discoveredServices = new DiscoveredTools();
                
        try {
            bonjourServer = new WebBootstrap(discoveredServices, httpEngine,
                    "TrianaServer", "triana", "Triana Bonjour Service!",
                    "Published Services", webDefines ,st);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        discoverTools=new DiscoverTools(bonjourServer, discoveredServices);

    }

    public static TrianaHttpServer getWorkflowServer() {
        return workflowServer;
    }

    public static HttpPeer getHttpEngine() {
        return httpEngine;
    }

    public static WebBootstrap getBonjourServer() {
        return bonjourServer;
    }

    public static DiscoveredServicesInterface getDiscoveredServices() {
        return discoveredServices;
    }
}
