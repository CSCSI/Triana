package org.trianacode.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.thinginitself.streamable.Streamable;
import org.trianacode.taskgraph.tool.Toolbox;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 20, 2010
 */

public class ToolboxRenderer implements Renderer {

    private Toolbox toolbox;

    public ToolboxRenderer(Toolbox toolbox, String templatePath) {
        this.toolbox = toolbox;
        try {
            Output.registerTemplate(Renderer.TOOLBOX_TEMPLATE, templatePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Streamable render() {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("toolboxpath", toolbox.getPath());
        return Output.output(properties, Renderer.TOOLBOX_TEMPLATE);
    }
}
