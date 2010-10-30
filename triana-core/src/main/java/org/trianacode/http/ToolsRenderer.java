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

public class ToolsRenderer implements Renderer {

    public static String TOOLS_DESCRIPTION_TEMPLATE = "tools.description.template";


    private List<Tool> tools;
    private String path;

    public void init(List<Tool> tools, String path) {
        this.tools = tools;
        this.path = path;
        if (tools.size() > 0) {
            TrianaProperties props = tools.get(0).getProperties();
            try {
                Output.registerDefaults(props);
                Output.registerTemplate(TOOLS_DESCRIPTION_TEMPLATE, props.getProperty(TrianaProperties.TOOLS_DESCRIPTION_TEMPLATE_PROPERTY));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String[] getRenderTypes() {
        return new String[]{
                TOOLS_DESCRIPTION_TEMPLATE};
    }

    public Streamable render(String type) {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("path", path);
        properties.put("tools", tools);
        return Output.output(properties, type);
    }


}
