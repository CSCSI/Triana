package org.trianacode.http;

import org.thinginitself.streamable.Streamable;
import org.trianacode.config.TrianaProperties;
import org.trianacode.taskgraph.tool.Toolbox;
import org.trianacode.velocity.Output;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 20, 2010
 */

public class ToolboxRenderer implements Renderer {

    public static String TOOLBOX_DESCRIPTION_TEMPLATE = "toolbox.description.template";
    public static String TOOLBOX_CLASSPATH_TEMPLATE = "toolbox.classpath.template";


    private Toolbox toolbox;
    private String path;

    public void init(Toolbox toolbox, String path) {
        this.toolbox = toolbox;
        this.path = path;
        try {
            Output.registerDefaults(toolbox.getProperties());
            Output.registerTemplate(TOOLBOX_DESCRIPTION_TEMPLATE, toolbox.getProperties().getProperty(TrianaProperties.TOOLBOX_DESCRIPTION_TEMPLATE_PROPERTY));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] getRenderTypes() {
        return new String[]{TOOLBOX_DESCRIPTION_TEMPLATE};
    }

    public Streamable render(String type) {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("path", path);
        properties.put("toolboxpath", toolbox.getPath());
        return Output.output(properties, type);
    }

}
