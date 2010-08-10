package org.trianacode.discovery.protocols.tdp;

import java.io.Serializable;
import java.util.List;

import org.trianacode.discovery.toolinfo.ToolMetadata;

/**
 * A response from a TDP contains the list of tools that the server is hosting.
 * <p/>
 * User: Ian Taylor Date: Jul 30, 2010 Time: 2:51:44 PM To change this template use File | Settings | File Templates.
 */
public class TDPResponse implements Serializable {
    private List<ToolMetadata> tools;

    public TDPResponse(List<ToolMetadata> tools) {
        this.tools = tools;
    }

    public List<ToolMetadata> getTools() {
        return tools;
    }
}
