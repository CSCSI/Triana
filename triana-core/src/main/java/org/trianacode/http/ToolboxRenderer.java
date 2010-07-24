package org.trianacode.http;

import org.trianacode.taskgraph.tool.Toolbox;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 20, 2010
 */

public interface ToolboxRenderer extends Renderer {

    public void init(Toolbox toolbox, String path);

}