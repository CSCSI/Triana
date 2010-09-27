package org.trianacode.discovery.protocols.tdp;

import org.apache.commons.logging.Log;
import org.thinginitself.http.Http;
import org.thinginitself.http.HttpPeer;
import org.thinginitself.http.RequestContext;
import org.thinginitself.http.Resource;
import org.thinginitself.streamable.Streamable;
import org.thinginitself.streamable.StreamableObject;
import org.trianacode.enactment.logging.Loggers;

import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * A base implementation of a Triana Discovery Protocol (BonjourService) server, which handles requests and delegates
 * the handling of those requests to the specific protocol.
 * <p/>
 * User: Ian Taylor Date: Jul 30, 2010 Time: 11:54:53 AM To change this template use File | Settings | File Templates.
 */
public abstract class TDPServer extends Resource {

    private static Log log = Loggers.LOGGER;


    public static String command = "tdp";

    public TDPServer(HttpPeer httpPeer) {
        super(command, Http.Method.POST);
        log.debug("TDPServer: Added following target to the http container - " + command);
        httpPeer.addTarget(this);
    }

    public void onPost(RequestContext context) {
        log.debug("Got Request !!!!!!!!!!!");

        Streamable stream = context.getRequestEntity();

        String output = null;
        TDPRequest request = null;

        ObjectInputStream r = null;

        try {
            r = new ObjectInputStream(stream.getInputStream());
            request = (TDPRequest) r.readObject();

            log.debug("Got a " + request.toString());
        } catch (IOException e) {
            output = "Received object is not a TDPRequest object!!!!!  Permission denied";
        } catch (ClassNotFoundException e) {
        }

        TDPResponse response = handleRequest(request);

        log.debug("Returning " + response.toString());

        context.setResponseEntity(new StreamableObject(response));
    }

    public abstract TDPResponse handleRequest(TDPRequest request);

    /**
     * The service name for the type of Triana discovery protocol service. Services are advertised on the network using
     * the type "_tdp._tcp" and using the service name you advertise for your protocol
     *
     * @return
     */
    public abstract String getServiceName();

}
