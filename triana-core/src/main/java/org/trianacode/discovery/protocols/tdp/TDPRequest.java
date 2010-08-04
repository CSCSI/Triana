package org.trianacode.discovery.protocols.tdp;

import java.io.Serializable;

/**
 * Objects that get send to Triana Discovery Protocol (TDP) servers to request information.
 * The following Request's are available.  The aim here is to keep things as simple as
 * possible so this list should only be increased for very good reasons....
 *
 *
 * <ol>
 * <li> GET_TOOLS_LIST - gets the list of tools that this TDP service is hosting.  The reply
 * is wrapped in a TDPResponse object and contains a list of ToolMetadata objects containing
 * the list of tools that it serves.
 *
 * <li> GET_UPDATED_TOOL_LIST - This requests an update between the last request and this one
 * A list of updated tools is returns for inclusion into the tree.
 *
 * <li> RESOLVE_TOOL_LIST - this requests that each tool is resolved and a collection of
 * ToolData objects is returned to the server. ToolData objects typically require the actual
 * source code to be introspected or instantiated to find out this infomration, so this should
 * be requested in a background thread since it will involved trawling all of the toolset and
 * return more complete information.
 * 
 * </ol>
 *
 * User: Ian Taylor
 * Date: Jul 30, 2010
 * Time: 2:51:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class TDPRequest implements Serializable {
    public static enum Request {GET_TOOLS_LIST, GET_UPDATED_TOOL_LIST, RESOLVE_TOOL_LIST}


    Request request;

    /**
     * Creates a request for sending to a Triana Discovery Protocol service.
     *
     * @param request
     */
    public TDPRequest(Request request) {
        this.request = request;
    }

    public Request getRequest() {
        return request;
    }
}
