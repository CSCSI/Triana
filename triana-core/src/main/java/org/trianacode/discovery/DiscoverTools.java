package org.trianacode.discovery;

import mil.navy.nrl.discovery.WebBootstrap;
import mil.navy.nrl.discovery.api.DiscoveredServicesInterface;
import mil.navy.nrl.discovery.api.ServiceInfoEndpoint;
import mil.navy.nrl.discovery.types.ServiceTypes;
import mil.navy.nrl.discovery.web.template.WebDefines;
import org.thinginitself.http.HttpPeer;
import org.thinginitself.http.RequestContext;
import org.thinginitself.http.Resource;
import org.thinginitself.http.Response;
import org.thinginitself.streamable.Streamable;
import org.thinginitself.streamable.StreamableObject;
import org.trianacode.discovery.protocols.tdp.TDPRequest;
import org.trianacode.discovery.protocols.tdp.TDPResponse;
import org.trianacode.discovery.protocols.tdp.TDPServer;
import org.trianacode.discovery.protocols.tdp.imp.trianatools.LocalTrawler;
import org.trianacode.discovery.protocols.thirdparty.ServiceTypesAndProtocols;
import org.trianacode.discovery.toolinfo.ToolMetadata;
import sun.misc.Timeable;
import sun.misc.Timer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: scmijt
 * Date: Jul 30, 2010
 * Time: 2:37:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class DiscoverTools implements Timeable {
    static WebBootstrap bonjourServer;
    ServiceTypesAndProtocols tdpProtocols;
    DiscoveredTools discoveredServices;

    Timer timer;

    HttpPeer httpEngine;

    public DiscoverTools(HttpPeer httpEngine) {
        tdpProtocols= new ServiceTypesAndProtocols();

        this.httpEngine=httpEngine;

        //  scan every 10 seconds....

        timer = new Timer(this, 10000);
        timer.cont();

        ServiceTypes st = new ServiceTypes();

        st.registerServiceType("http._tcp", "HTTP is cool....");

        WebDefines webDefines = new WebDefines(null,null,null,null,null);

        discoveredServices = new DiscoveredTools(this);
        startServices();

        try {
            bonjourServer = new WebBootstrap((DiscoveredServicesInterface)discoveredServices, httpEngine,
                    "TrianaServer", "triana", "Triana Bonjour Service!",
                    "Published Services", webDefines ,st);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void startServices() {
        new LocalTrawler(httpEngine);
    }

    public void tick(sun.misc.Timer timer) {
        Object[] protocols = discoveredServices.getProtocols().toArray();

        for (Object obj: protocols) {
            ServiceInfoEndpoint protocol = (ServiceInfoEndpoint)obj;
            String endpoint = "http://" + protocol.getServiceAddress() + ":" + protocol.getPort() + "/" + TDPServer.command;

            TDPRequest request = new TDPRequest(TDPRequest.Request.GET_TOOLS_LIST);
            
            RequestContext c = new RequestContext(endpoint);
            c.setResource(new Resource(new StreamableObject(request)));

            TDPResponse data;
            ObjectInputStream r = null;
            
            try {
                Response response = httpEngine.post(c);

                Streamable stream = response.getContext().getResponseEntity();

                System.out.println("Response is " + response.getOutcome());

                r = new ObjectInputStream(stream.getInputStream());
                data = (TDPResponse)r.readObject();

                List<ToolMetadata> tools = data.getTools();

                for (ToolMetadata toolmd: tools) {
                    System.out.println(toolmd.toString());
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException ee) {
                ee.printStackTrace();
            }
        }
    }


    public static WebBootstrap getBonjourServer() {
        return bonjourServer;
    }
}
