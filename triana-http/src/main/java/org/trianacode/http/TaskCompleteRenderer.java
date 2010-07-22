package org.trianacode.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.thinginitself.streamable.Streamable;
import org.trianacode.taskgraph.tool.Tool;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 21, 2010
 */

public class TaskCompleteRenderer implements Renderer {

    private Tool tool;

    public TaskCompleteRenderer(Tool tool, String templatePath) {
        this.tool = tool;
        try {
            Output.registerTemplate(Renderer.TOOL_COMPLETED_TEMPLATE, templatePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public Streamable render() {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("toolname", tool.getToolName());
        properties.put("toolpackage", tool.getToolPackage());
        return Output.output(properties, Renderer.TOOL_COMPLETED_TEMPLATE);

    }
}
