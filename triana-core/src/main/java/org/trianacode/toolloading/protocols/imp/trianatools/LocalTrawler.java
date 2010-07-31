package org.trianacode.toolloading.protocols.imp.trianatools;

import org.thinginitself.http.HttpPeer;
import org.trianacode.toolloading.protocols.tdp.TDPRequest;
import org.trianacode.toolloading.protocols.tdp.TDPResponse;
import org.trianacode.toolloading.protocols.tdp.TDPServer;

/**
 * Searches for local Triana tools on this instance of Triana and exposes them to
 * the network.
 * 
 * User: scmijt
 * Date: Jul 30, 2010
 * Time: 2:57:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class LocalTrawler extends TDPServer {

    public LocalTrawler(HttpPeer httpPeer) {
        super(httpPeer);
    }

    @Override
    public TDPResponse handleRequest(TDPRequest request) {
        return null; 
    }

    public String getServiceName() {
        return "TrianaService";
    }
}
