package org.trianacode.http;

import org.thinginitself.streamable.Streamable;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 20, 2010
 */

public interface Renderer {


    public Streamable render(String type, String mime);
}
