package org.trianacode.http;

import java.io.OutputStream;

import org.trianacode.taskgraph.tool.Tool;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 20, 2010
 */

public class ToolRenderer implements Renderer {

    private Tool tool;
    private String templatePath;

    public ToolRenderer(Tool tool, String templatePath) {
        this.tool = tool;
        this.templatePath = templatePath;
    }

    @Override
    public void render(OutputStream out) {
    }
}
