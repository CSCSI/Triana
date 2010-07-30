package org.trianacode.http;

import mil.navy.nrl.discovery.WebBootstrap;
import mil.navy.nrl.discovery.types.ServiceTypes;
import mil.navy.nrl.discovery.web.template.WebDefines;
import org.thinginitself.http.HttpPeer;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: scmijt
 * Date: Jul 30, 2010
 * Time: 12:06:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class HTTPServices {
    static TrianaHttpServer workflowServer;
    static HttpPeer httpEngine;
    static WebBootstrap bonjourServer;

    public HTTPServices() {}

    public void startServices() throws Exception {

        // start a http server first
        
        workflowServer = new TrianaHttpServer();
        workflowServer.start();

        httpEngine =  workflowServer.getHTTPPeerInstance();

        ServiceTypes st = new ServiceTypes();

        st.registerServiceType("http._tcp", "HTTP is cool....");

        WebDefines webDefines = new WebDefines(null,null,null,null,null);

        try {
            bonjourServer = new WebBootstrap(httpEngine, "TrianaServer", "triana", "Triana Bonjour Service!",
                    "Published Services", webDefines ,st);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
