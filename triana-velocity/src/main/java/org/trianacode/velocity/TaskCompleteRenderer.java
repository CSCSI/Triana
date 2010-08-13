package org.trianacode.velocity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.thinginitself.streamable.Streamable;
import org.trianacode.http.ToolRenderer;
import org.trianacode.taskgraph.tool.Tool;

/**
 * TODO need to get a message to this that the task is complete - to remove it from the resource tree
 *
 * @author Andrew Harrison
 * @version 1.0.0 Jul 21, 2010
 */

public class TaskCompleteRenderer implements ToolRenderer {

    private Tool tool;
    private String path;
    private String templatePath = "/templates/tool-complete.tpl";

    @Override
    public String[] getRenderTypes() {
        return new String[]{BasicToolRenderer.TOOL_COMPLETED_TEMPLATE};
    }

    @Override
    public Streamable render(String type) {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("path", path);
        properties.put("toolname", tool.getToolName());
        properties.put("toolpackage", tool.getToolPackage());
        return Output.output(properties, type);

    }

    @Override
    public void init(Tool tool, String path) {
        this.tool = tool;
        this.path = path;
        try {
            Output.registerTemplate(TOOL_COMPLETED_TEMPLATE, templatePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
