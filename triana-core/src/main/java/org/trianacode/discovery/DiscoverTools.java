package org.trianacode.discovery;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

import org.thinginitself.http.HttpPeer;
import org.thinginitself.http.RequestContext;
import org.thinginitself.http.Resource;
import org.thinginitself.http.Response;
import org.thinginitself.streamable.Streamable;
import org.thinginitself.streamable.StreamableObject;
import org.trianacode.config.TrianaProperties;
import org.trianacode.discovery.protocols.tdp.TDPRequest;
import org.trianacode.discovery.protocols.tdp.TDPResponse;
import org.trianacode.discovery.protocols.tdp.TDPServer;
import org.trianacode.discovery.protocols.tdp.imp.trianatools.LocalTrawler;
import org.trianacode.discovery.protocols.thirdparty.ServiceTypesAndProtocols;
import org.trianacode.discovery.toolinfo.ToolMetadata;
import org.trianacode.discovery.protocols.tdp.imp.trianatools.ToolResolver;
import mil.navy.nrl.discovery.WebBootstrap;
import mil.navy.nrl.discovery.api.ServiceInfoEndpoint;
import mil.navy.nrl.discovery.types.ServiceTypes;
import mil.navy.nrl.discovery.web.template.WebDefines;
import sun.misc.Timeable;
import sun.misc.Timer;


/**
 * Created by IntelliJ IDEA. User: scmijt Date: Jul 30, 2010 Time: 2:37:49 PM To change this template use File |
 * Settings | File Templates.
 */
public class DiscoverTools extends Thread implements Timeable {
    private static WebBootstrap bonjourServer;
    private ServiceTypesAndProtocols tdpProtocols;
    private DiscoveredTools discoveredServices;
    ToolResolver toolResolver;

    private Timer timer;
    private HttpPeer httpEngine;
    TrianaProperties properties;

    public DiscoverTools(ToolResolver resolver, HttpPeer httpEngine, TrianaProperties properties) {
        this.tdpProtocols = new ServiceTypesAndProtocols();
        this.httpEngine = httpEngine;
        this.properties=properties;
        this.toolResolver=resolver;
        Thread discoverThread = new Thread(this);
        discoverThread.setPriority(Thread.MIN_PRIORITY);
        discoverThread.start();
    }

    public void startServices(ToolResolver resolver) {
        new LocalTrawler(httpEngine, resolver);
    }

    public ToolResolver getToolResolver() {
        return toolResolver;
    }

    public void run() {
        startServices(toolResolver);
        ServiceTypes st = new ServiceTypes();

        // you would use this to provide custom icons for Triana etc
        WebDefines webDefines = new WebDefines(null, null, null, null, null);

        discoveredServices = new DiscoveredTools(this);

        try {
            bonjourServer = new WebBootstrap(discoveredServices, httpEngine,
                    "TrianaServer", "triana-web", "Triana Bonjour Service!",
                    "Published Services", webDefines, st);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        //tick(null);
        //  scan every 60 seconds....
      //   timer = new Timer(this, 10000);
        // timer.cont();

         tick(null);

    }

    public void tick(sun.misc.Timer timer) {
        System.out.println("Looking for bonjour services !!");
        Object[] protocols = discoveredServices.getProtocols().toArray();

        for (Object obj : protocols) {
            ServiceInfoEndpoint protocol = (ServiceInfoEndpoint) obj;
            String endpoint = "http://" + protocol.getServiceAddress() + ":" + protocol.getPort() + "/"
                    + TDPServer.command;

            TDPRequest request = new TDPRequest(TDPRequest.Request.GET_TOOLS_LIST);

            try {
                TDPResponse data = sendMessageToServer(request, endpoint);

                List<ToolMetadata> tools = data.getTools();

                System.out.println("Here's the list of tools found from the bonjour service  !!");

                for (ToolMetadata toolmd : tools) {
                    // System.out.println(toolmd.toString());
                    discoveredServices.addTool(toolmd,protocol);                    
                }
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (ClassNotFoundException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    private TDPResponse sendMessageToServer(TDPRequest request, String endpoint)
            throws IOException, ClassNotFoundException {
        TDPResponse data;
        ObjectInputStream r = null;

        RequestContext c = new RequestContext(endpoint);

        Response response = sendRequest(c, request);

        RequestContext rc = response.getContext();
        Streamable stream = rc.getResource().getStreamable();


        r = new ObjectInputStream(stream.getInputStream());
        data = (TDPResponse) r.readObject();
        return data;
    }

    private Response sendRequest(RequestContext c, TDPRequest request) throws IOException {
        c.setResource(new Resource(new StreamableObject(request)));
        return httpEngine.post(c);

    }

    public static WebBootstrap getBonjourServer() {
        return bonjourServer;
    }

    public void shutdown() {
        if(bonjourServer != null) {
            bonjourServer.getDiscovery().shutdown();
        }
    }
}
