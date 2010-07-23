package org.trianacode.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.thinginitself.streamable.Streamable;
import org.trianacode.taskgraph.tool.Tool;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 20, 2010
 */

public class ToolInstanceRenderer implements Renderer {

    private Tool tool;
    private String path;

    public ToolInstanceRenderer(Tool tool, String path, String templatePath) {
        this.tool = tool;
        this.path = path;
        try {
            Output.registerTemplate(Renderer.TOOL_INSTANCE_TEMPLATE, templatePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Streamable render() {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("path", path);
        properties.put("toolname", tool.getToolName());
        properties.put("toolpackage", tool.getToolPackage());
        return Output.output(properties, Renderer.TOOL_INSTANCE_TEMPLATE);
    }
}