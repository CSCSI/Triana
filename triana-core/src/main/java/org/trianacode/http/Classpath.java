package org.trianacode.http;

import java.util.List;

import org.thinginitself.streamable.Streamable;
import org.thinginitself.streamable.StreamableString;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Aug 11, 2010
 */

public class Classpath {

    private String list;

    public Classpath(List<String> paths) {
        StringBuilder sb = new StringBuilder("<classpath>");
        for (String path : paths) {
            sb.append("<path>").append(path).append("</path>");
        }
        sb.append("</classpath>");
        list = sb.toString();
    }

    public Streamable getStreamable() {
        return new StreamableString(list, "text/xml");
    }


}
