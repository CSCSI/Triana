package org.trianacode.http;

import org.thinginitself.streamable.Streamable;
import org.trianacode.taskgraph.tool.Toolbox;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 20, 2010
 */

public class ToolboxRenderer implements Renderer {

    private Toolbox toolbox;
    private String templatePath;

    public ToolboxRenderer(Toolbox toolbox, String templatePath) {
        this.toolbox = toolbox;
        this.templatePath = templatePath;
    }

    @Override
    public Streamable render() {

        return null;
    }
}
