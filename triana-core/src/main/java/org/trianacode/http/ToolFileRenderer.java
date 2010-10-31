package org.trianacode.http;

import org.thinginitself.streamable.Streamable;
import org.trianacode.config.TrianaProperties;
import org.trianacode.taskgraph.tool.Tool;
import org.trianacode.velocity.Output;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 20, 2010
 */

public class ToolFileRenderer implements Renderer {

    private Tool tool;
    private List<String> paths;

    public void init(Tool tool, List<String> paths) {
        this.tool = tool;
        this.paths = paths;

        TrianaProperties props = tool.getProperties();
        try {
            Output.registerDefaults(props);
            Output.registerTemplate(TrianaProperties.TOOL_CP_HTML_TEMPLATE_PROPERTY, props.getProperty(TrianaProperties.TOOL_CP_HTML_TEMPLATE_PROPERTY));
            Output.registerTemplate(TrianaProperties.TOOL_CP_XML_TEMPLATE_PROPERTY, props.getProperty(TrianaProperties.TOOL_CP_XML_TEMPLATE_PROPERTY));
            Output.registerTemplate(TrianaProperties.NOHELP_TEMPLATE_PROPERTY, props.getProperty(TrianaProperties.NOHELP_TEMPLATE_PROPERTY));
            Output.registerTemplate(TrianaProperties.TOOL_DESCRIPTION_TEMPLATE_PROPERTY, props.getProperty(TrianaProperties.TOOL_DESCRIPTION_TEMPLATE_PROPERTY));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] getRenderTypes() {
        return new String[]{
                TrianaProperties.TOOL_CP_HTML_TEMPLATE_PROPERTY,
                TrianaProperties.TOOL_CP_XML_TEMPLATE_PROPERTY,
                TrianaProperties.TOOL_DESCRIPTION_TEMPLATE_PROPERTY,
                TrianaProperties.NOHELP_TEMPLATE_PROPERTY
        };
    }

    public Streamable render(String type) {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("toolpath", PathController.getInstance().getToolPath(tool));
        properties.put("paths", paths);
        properties.put("tool", tool.getQualifiedToolName());
        properties.put("toolname", tool.getToolName());
        properties.put("toolbox", tool.getToolBox().getName());

        return Output.output(properties, type);
    }


}
