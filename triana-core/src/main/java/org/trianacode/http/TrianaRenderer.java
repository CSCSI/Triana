package org.trianacode.http;

import org.thinginitself.streamable.Streamable;
import org.trianacode.TrianaInstance;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 20, 2010
 */

public class TrianaRenderer implements Renderer {

    private String path;

    public void init(TrianaInstance instance, String path) {
        this.path = path;

    }

    public Streamable render(String type, String mime) {
        Map<String, Object> properties = new HashMap<String, Object>();
        return Output.output(properties, type, mime);
    }
}
