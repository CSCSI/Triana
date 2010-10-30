package org.trianacode.http;

import org.thinginitself.streamable.Streamable;
import org.trianacode.config.TrianaProperties;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.tool.Tool;
import org.trianacode.velocity.Output;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 20, 2010
 */

public class ToolParameterRenderer implements Renderer {

    public static String TOOL_PARAMETER_TEMPLATE = "tool.parameter.template";


    private Task parent;
    private Tool tool;
    private String path;
    private String templatePath;


    public void init(Tool tool, String path) {
        if (tool instanceof Task) {
            Task t = (Task) tool;
            this.parent = t.getUltimateParent();
        }
        this.tool = tool;
        this.path = path;
        templatePath = tool.getProperties().getProperty(TrianaProperties.TOOL_PARAMETER_WINDOW_TEMPLATE_PROPERTY);

        try {
            Output.registerTemplate(TOOL_PARAMETER_TEMPLATE, templatePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] getRenderTypes() {
        return new String[]{TOOL_PARAMETER_TEMPLATE};
    }

    public Streamable render(String type) {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("path", path);
        properties.put("toolname", parent.getToolName());
        properties.put("toolpackage", parent.getToolPackage());
        properties.put("subtoolname", tool.getToolName());
        properties.put("subtoolpackage", tool.getToolPackage());
        return Output.output(properties, type);
    }
}
