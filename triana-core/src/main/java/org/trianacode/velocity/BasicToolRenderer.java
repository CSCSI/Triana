package org.trianacode.velocity;

import org.thinginitself.streamable.Streamable;
import org.trianacode.config.TrianaProperties;
import org.trianacode.http.ToolRenderer;
import org.trianacode.taskgraph.tool.Tool;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 20, 2010
 */

public class BasicToolRenderer implements ToolRenderer {

    private Tool tool;
    private String path;

    @Override
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

    @Override
    public String[] getRenderTypes() {
        return new String[]{TOOL_CREATE_INSTANCE_TEMPLATE,
                TOOL_INSTANCE_TEMPLATE,
                TOOL_DESCRIPTION_TEMPLATE,
                TOOL_COMPLETED_TEMPLATE};
    }

    @Override
    public Streamable render(String type) {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("path", path);
        properties.put("toolname", tool.getToolName());
        properties.put("toolpackage", tool.getToolPackage());
        return Output.output(properties, type);
    }
}
