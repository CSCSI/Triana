package org.trianacode.http;

import org.thinginitself.streamable.Streamable;
import org.trianacode.config.TrianaProperties;
import org.trianacode.taskgraph.tool.Tool;
import org.trianacode.velocity.Output;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 20, 2010
 */

public class ToolRenderer implements Renderer {

    public static String TOOL_DESCRIPTION_TEMPLATE = "tool.description.template";
    public static String TOOL_CREATE_INSTANCE_TEMPLATE = "tool.create.instance.template";
    public static String TOOL_INSTANCE_TEMPLATE = "tool.instance.template";
    public static String TOOL_COMPLETED_TEMPLATE = "tool.completed.template";


    private Tool tool;
    private String path;

    public void init(Tool tool, String path) {
        this.tool = tool;
        this.path = path;
        Properties props = tool.getProperties();
        try {
            Output.registerTemplate(TOOL_CREATE_INSTANCE_TEMPLATE, props.getProperty(TrianaProperties.CREATE_TOOL_INSTANCE_PROPERTY));
            Output.registerTemplate(TOOL_COMPLETED_TEMPLATE, props.getProperty(TrianaProperties.TOOL_COMPLETED_TEMPLATE_PROPERTY));
            Output.registerTemplate(TOOL_DESCRIPTION_TEMPLATE, props.getProperty(TrianaProperties.TOOL_DESCRIPTION_TEMPLATE_PROPERTY));
            Output.registerTemplate(TOOL_INSTANCE_TEMPLATE, props.getProperty(TrianaProperties.TOOL_INSTANCE_PROPERTY));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] getRenderTypes() {
        return new String[]{TOOL_CREATE_INSTANCE_TEMPLATE,
                TOOL_INSTANCE_TEMPLATE,
                TOOL_DESCRIPTION_TEMPLATE,
                TOOL_COMPLETED_TEMPLATE};
    }

    public Streamable render(String type) {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("path", path);
        properties.put("toolname", tool.getToolName());
        properties.put("toolpackage", tool.getToolPackage());
        return Output.output(properties, type);
    }


}
