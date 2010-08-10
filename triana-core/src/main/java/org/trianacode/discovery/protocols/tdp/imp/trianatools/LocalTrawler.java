package org.trianacode.discovery.protocols.tdp.imp.trianatools;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.thinginitself.http.HttpPeer;
import org.trianacode.discovery.protocols.tdp.TDPRequest;
import org.trianacode.discovery.protocols.tdp.TDPResponse;
import org.trianacode.discovery.protocols.tdp.TDPServer;
import org.trianacode.discovery.toolinfo.ToolMetadata;
import org.trianacode.taskgraph.tool.Tool;
import org.trianacode.taskgraph.tool.ToolResolver;

/**
 * Searches for local Triana tools on this instance of Triana and exposes them to the network.
 * <p/>
 * User: scmijt Date: Jul 30, 2010 Time: 2:57:41 PM To change this template use File | Settings | File Templates.
 */
public class LocalTrawler extends TDPServer {

    static Logger log = Logger.getLogger("org.trianacode.discovery.protocols.tdp.imp.trianatools.LocalTrawler");

    private ToolResolver resolver;

    public LocalTrawler(HttpPeer httpPeer, ToolResolver resolver) {
        super(httpPeer);
        this.resolver = resolver;
    }

    @Override
    public TDPResponse handleRequest(TDPRequest request) {
        if (request.getRequest() == TDPRequest.Request.GET_TOOLS_LIST) {
            List<Tool> tools = resolver.getLocalTools();
            return new TDPResponse(getMetadata(tools));
        }
        // ??
        return new TDPResponse(new ArrayList<ToolMetadata>());
    }

    private List<ToolMetadata> getMetadata(List<Tool> tools) {
        List<ToolMetadata> ret = new ArrayList<ToolMetadata>();
        for (Tool tool : tools) {
            ret.add(new ToolMetadata(tool.getToolName(), tool.getToolName(), tool.getDefinitionPath(), null));
        }
        return ret;
    }

    public String getServiceName() {
        return "TrianaService";
    }


}
    
