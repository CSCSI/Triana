package org.trianacode.toolloading;

import mil.navy.nrl.discovery.WebBootstrap;
import mil.navy.nrl.discovery.api.DiscoveredServicesInterface;
import org.trianacode.toolloading.protocols.ServiceTypesAndProtocols;
import org.trianacode.toolloading.protocols.imp.trianatools.LocalTrawler;

/**
 * Created by IntelliJ IDEA.
 * User: scmijt
 * Date: Jul 30, 2010
 * Time: 2:37:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class DiscoverTools {
    WebBootstrap bonjourServer;
    ServiceTypesAndProtocols tdpProtocols;
    DiscoveredServicesInterface discoveredServices;


    public DiscoverTools(WebBootstrap bonjourServer, DiscoveredServicesInterface discoveredServices) {
        this.bonjourServer = bonjourServer;
        tdpProtocols= new ServiceTypesAndProtocols();
        this.discoveredServices=discoveredServices;
        
        tdpProtocols.registerServiceType("_triana._tcp", "Local Tool Loader");
    }

    // To invoke.
//RequestContext c = new RequestContext("http://bla");
//c.setResource(new Resource(new StreamableObject(myObj)));
//new HttpPeer().post(c);
//
//
//
}
