package org.trianacode.toolloading.protocols.tdp;

import mil.navy.nrl.discovery.ProtoSD;
import mil.navy.nrl.discovery.api.ServiceInfo;
import org.thinginitself.http.Http;
import org.thinginitself.http.HttpPeer;
import org.thinginitself.http.RequestContext;
import org.thinginitself.http.Resource;
import org.thinginitself.streamable.Streamable;
import org.thinginitself.streamable.StreamableObject;
import org.thinginitself.streamable.StreamableString;
import org.trianacode.http.HTTPServices;

import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * A base implementation of a Triana Discovery Protocol (TDP) server, which
 * handles requests and delegates the handling of those requests to the
 * specific protocol.
 *
 * User: scmijt
 * Date: Jul 30, 2010
 * Time: 11:54:53 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class TDPServer extends Resource {
    ProtoSD discovery;

    public TDPServer(HttpPeer httpPeer) {
        super("remotecontrol", Http.Method.GET);
        httpPeer.addTarget(this);
        discovery = HTTPServices.getBonjourServer().getDiscovery();
    }

    public void onPost(RequestContext context) {
        System.out.println("Got Request !!!!!!!!!!!");

        Streamable stream = context.getRequestEntity();

        String output=null;
        TDPRequest request=null;

        ObjectInputStream r = null;

        try {
            r = new ObjectInputStream(stream.getInputStream());
            request = (TDPRequest)r.readObject();

            System.out.println(request.toString());
        } catch (IOException e) {
            output="Received object is not a Pegasus workflow object!!!!!  Permission denied";
        } catch (ClassNotFoundException e) {
        }

        TDPResponse response = handleRequest(request);
        
        context.setResponseEntity(new StreamableObject(response));
    }

    public abstract TDPResponse handleRequest(TDPRequest request);

    /**
     * The service name for the type of Triana discovery protocol service. Services are
     * advertised on the network using the type "_tdp._tcp" and using the service name
     * you advertise for your protocol
     * 
     * @return
     */
    public abstract String getServiceName();

}
