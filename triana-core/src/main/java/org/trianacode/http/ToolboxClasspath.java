package org.trianacode.http;

import java.util.List;

import org.thinginitself.streamable.Streamable;
import org.thinginitself.streamable.StreamableString;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Aug 11, 2010
 */

public class ToolboxClasspath {

    private List<String> ss;

    public ToolboxClasspath(Streamable s) {

    }

    public ToolboxClasspath(List<String> paths) {
        this.ss = paths;
    }

    public Streamable getStreamable(String mime) {
        if (mime.equals("text/html")) {
            StringBuilder sb = new StringBuilder("");
            sb.append("<html><head><title>Classpath</title></head><body><ul>");
            for (String path : ss) {
                sb.append("<li><a href=\"").append(path).append("\">").append(path).append("</a></li>");
            }
            sb.append("</ul></body></html>");
            return new StreamableString(sb.toString(), "text/html");
        } else {
            StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
            sb.append("<classpath>");
            for (String path : ss) {
                sb.append("<path>").append(path).append("</path>");
            }
            sb.append("</classpath>");
            return new StreamableString(sb.toString(), "text/xml");
        }
    }


}
