package org.trianacode.discovery.protocols.thirdparty;

import org.trianacode.discovery.protocols.tdp.TDPRequest;
import org.trianacode.discovery.protocols.tdp.TDPResponse;

/**
 * Interface for third party service - whatever is hooked up must be capable of
 * sending and receiving TDP requests and responses (locally) and then translate those
 * into protocol specific interactions.
 *
 * User: scmijt
 * Date: Jul 30, 2010
 * Time: 2:45:01 PM
 * To change this template use File | Settings | File Templates.
 */
public interface BonjourService {

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
