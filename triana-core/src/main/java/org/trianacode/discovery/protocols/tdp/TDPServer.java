package org.trianacode.discovery.protocols.tdp;

import mil.navy.nrl.discovery.ProtoSD;
import org.thinginitself.http.Http;
import org.thinginitself.http.HttpPeer;
import org.thinginitself.http.RequestContext;
import org.thinginitself.http.Resource;
import org.thinginitself.streamable.Streamable;
import org.thinginitself.streamable.StreamableObject;
import org.thinginitself.streamable.StreamableString;
import org.trianacode.discovery.DiscoverTools;
import org.trianacode.http.HTTPServices;

import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * A base implementation of a Triana Discovery Protocol (BonjourService) server, which
 * handles requests and delegates the handling of those requests to the
 * specific protocol.
 *
 * User: Ian Taylor
 * Date: Jul 30, 2010
 * Time: 11:54:53 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class TDPServer extends Resource {

    public static String command = "tdp";

    public TDPServer(HttpPeer httpPeer) {
        super(command, Http.Method.POST);
        System.out.println("TDPServer: Added following target to the http container - " + command);
        httpPeer.addTarget(this);
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

            System.out.println("Got a " + request.toString());
        } catch (IOException e) {
            output="Received object is not a TDPRequest object!!!!!  Permission denied";
        } catch (ClassNotFoundException e) {
        }

        TDPResponse response = handleRequest(request);

        System.out.println("Returning " + response.toString());

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
