package org.trianacode.http;

import org.thinginitself.streamable.Streamable;
import org.trianacode.taskgraph.tool.Tool;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 20, 2010
 */

public class ToolParameterRenderer implements Renderer {


    private Tool tool;
    private String templatePath;

    public ToolParameterRenderer(Tool tool, String templatePath) {
        this.tool = tool;
        this.templatePath = templatePath;
    }

    @Override
    public Streamable render() {
        return null;
    }
}
