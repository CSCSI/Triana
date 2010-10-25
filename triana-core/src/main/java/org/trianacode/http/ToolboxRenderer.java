package org.trianacode.http;

import org.trianacode.taskgraph.tool.Toolbox;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 20, 2010
 */

public interface ToolboxRenderer extends Renderer {

    String TOOLBOX_DESCRIPTION_TEMPLATE = "toolbox.description.template";
    String TOOLBOX_CLASSPATH_TEMPLATE = "toolbox.classpath.template";

    public void init(Toolbox toolbox, String path);

}
