package org.trianacode.velocity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.thinginitself.streamable.Streamable;
import org.trianacode.http.ToolRenderer;
import org.trianacode.taskgraph.tool.Tool;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 20, 2010
 */

public class ToolInstanceRenderer implements ToolRenderer {

    private Tool tool;
    private String path;
    private String templatePath = "/templates/tool-instance.tpl";


    @Override
    public void init(Tool tool, String path) {
        this.tool = tool;
        this.path = path;
        try {
            Output.registerTemplate(TOOL_INSTANCE_TEMPLATE, templatePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String[] getRenderTypes() {
        return new String[]{TOOL_INSTANCE_TEMPLATE};
    }

    @Override
    public Streamable render() {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("path", path);
        properties.put("toolname", tool.getToolName());
        properties.put("toolpackage", tool.getToolPackage());
        return Output.output(properties, TOOL_INSTANCE_TEMPLATE);
    }
}