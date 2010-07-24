package org.trianacode.velocity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.thinginitself.streamable.Streamable;
import org.trianacode.http.ToolRenderer;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.tool.Tool;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 20, 2010
 */

public class ToolParameterRenderer implements ToolRenderer {


    private Task parent;
    private Tool tool;
    private String path;
    private String templatePath = "/templates/tool-params.tpl";


    @Override
    public void init(Tool tool, String path) {
        if (tool instanceof Task) {
            Task t = (Task) tool;
            this.parent = t.getUltimateParent();
        }
        this.tool = tool;
        this.path = path;
        try {
            Output.registerTemplate(TOOL_PARAMETER_TEMPLATE, templatePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String[] getRenderTypes() {
        return new String[]{TOOL_PARAMETER_TEMPLATE};
    }

    @Override
    public Streamable render() {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("path", path);
        properties.put("toolname", parent.getToolName());
        properties.put("toolpackage", parent.getToolPackage());
        properties.put("subtoolname", tool.getToolName());
        properties.put("subtoolpackage", tool.getToolPackage());
        return Output.output(properties, TOOL_PARAMETER_TEMPLATE);
    }
}
