package org.trianacode.http;

import org.thinginitself.streamable.Streamable;
import org.thinginitself.streamable.StreamableString;
import org.trianacode.taskgraph.tool.Tool;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Aug 11, 2010
 */

public class NoHelp {

    private String nohelp = "";


    public NoHelp(Tool tool) {
        StringBuilder sb = new StringBuilder("<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\">\n" +
                "<html><head>\n" +
                "<title>");
        sb.append(tool.getQualifiedToolName())
                .append(" Help")
                .append("</title><body><h2>No Help Available :-(</h2><p>")
                .append("No Help page is available for ")
                .append(tool.getQualifiedToolName())
                .append(". Sorry about that.<p></body></html>");
        nohelp = sb.toString();
    }

    public Streamable getStreamable() {
        return new StreamableString(nohelp, "text/html");
    }


}