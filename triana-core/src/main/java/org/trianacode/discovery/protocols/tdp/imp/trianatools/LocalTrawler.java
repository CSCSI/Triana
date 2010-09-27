package org.trianacode.discovery.protocols.tdp.imp.trianatools;

import org.thinginitself.http.HttpPeer;
import org.trianacode.discovery.protocols.tdp.TDPRequest;
import org.trianacode.discovery.protocols.tdp.TDPResponse;
import org.trianacode.discovery.protocols.tdp.TDPServer;
import org.trianacode.discovery.toolinfo.ToolMetadata;

import java.util.ArrayList;
import java.util.List;

/**
 * Searches for local Triana tools on this instance of Triana and exposes them to the network.
 * <p/>
 * User: scmijt Date: Jul 30, 2010 Time: 2:57:41 PM To change this template use File | Settings | File Templates.
 */
public class LocalTrawler extends TDPServer {

    private ToolResolver resolver;

    public LocalTrawler(HttpPeer httpPeer, ToolResolver resolver) {
        super(httpPeer);
        this.resolver = resolver;
    }

    @Override
    public TDPResponse handleRequest(TDPRequest request) {
        if (request.getRequest() == TDPRequest.Request.GET_TOOLS_LIST) {
            List<ToolMetadata> tools = resolver.getLocalToolMetadata();
            return new TDPResponse(tools);
        }
        return new TDPResponse(new ArrayList<ToolMetadata>());
    }


    public String getServiceName() {
        return "LocalTools";
    }


}
    
