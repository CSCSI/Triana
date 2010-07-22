package org.trianacode.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.thinginitself.streamable.Streamable;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.tool.Tool;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 20, 2010
 */

public class ToolParameterRenderer implements Renderer {


    private Task parent;
    private Tool tool;
    private String templatePath;

    public ToolParameterRenderer(Task parent, Tool tool, String templatePath) {
        this.parent = parent;
        this.tool = tool;
        try {
            Output.registerTemplate(Renderer.TOOL_PARAMETER_TEMPLATE, templatePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Streamable render() {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("toolname", parent.getToolName());
        properties.put("toolpackage", parent.getToolPackage());
        properties.put("subtoolname", tool.getToolName());
        properties.put("subtoolpackage", tool.getToolPackage());
        return Output.output(properties, Renderer.TOOL_PARAMETER_TEMPLATE);
    }
}
