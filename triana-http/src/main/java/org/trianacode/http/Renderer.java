package org.trianacode.http;

import java.io.OutputStream;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 20, 2010
 */

public interface Renderer {

    public void render(OutputStream out);
}
