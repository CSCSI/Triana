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
import org.thinginitself.streamable.StreamableString;
import org.trianacode.discovery.protocols.tdp.TDPRequest;
import org.trianacode.discovery.protocols.tdp.TDPResponse;
import org.trianacode.discovery.protocols.tdp.TDPServer;
import org.trianacode.discovery.protocols.tdp.imp.trianatools.LocalTrawler;
import org.trianacode.discovery.protocols.thirdparty.ServiceTypesAndProtocols;
import org.trianacode.discovery.toolinfo.ToolMetadata;
import sun.misc.Timeable;
import sun.misc.Timer;

import java.io.*;
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
        startServices();

        //  scan every 10 seconds....

        timer = new Timer(this, 10000);
        timer.cont();

        ServiceTypes st = new ServiceTypes();

        st.registerServiceType("http._tcp", "HTTP is cool....");

        WebDefines webDefines = new WebDefines(null,null,null,null,null);

        discoveredServices = new DiscoveredTools(this);

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


            try {
                TDPResponse data = sendMessageToServer(request, endpoint);

                List<ToolMetadata> tools = data.getTools();

                for (ToolMetadata toolmd: tools) {
                    System.out.println(toolmd.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (ClassNotFoundException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    private TDPResponse sendMessageToServer(TDPRequest request, String endpoint) throws IOException, ClassNotFoundException  {
        TDPResponse data;
        ObjectInputStream r = null;

        RequestContext c = new RequestContext(endpoint);

        Response response = sendRequest(c,request);

        Streamable stream = response.getContext().getResponseEntity();

        System.out.println("Response is " + response.getOutcome());

        r = new ObjectInputStream(stream.getInputStream());
        data = (TDPResponse)r.readObject();
        return data;
    }

    private Response sendRequestNotWorking(RequestContext c, TDPRequest request) throws IOException {
        c.setResource(new Resource(new StreamableObject(request)));
        return httpEngine.post(c);

    }

    private Response sendRequest(RequestContext c, TDPRequest request) throws IOException {


        c.setResource(new Resource(new StreamableString(objectToString(request))));
        return httpEngine.post(c);
    }

    public static String objectToString(Serializable object) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
         ObjectOutputStream ou = new ObjectOutputStream(out);
         ou.writeObject(object);
         ou.flush();
        String output = out.toString();
        out.close();
        return output;
    }
    public static WebBootstrap getBonjourServer() {
        return bonjourServer;
    }
}
