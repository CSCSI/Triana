package org.trianacode.velocity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.thinginitself.streamable.Streamable;
import org.trianacode.http.ToolboxRenderer;
import org.trianacode.taskgraph.tool.Toolbox;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 20, 2010
 */

public class ToolboxDescriptionRenderer implements ToolboxRenderer {

    private Toolbox toolbox;
    private String path;
    private String templatePath = "/templates/toolbox-description.tpl";


    @Override
    public String[] getRenderTypes() {
        return new String[]{TOOLBOX_DESCRIPTION_TEMPLATE};
    }

    @Override
    public Streamable render(String type) {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("toolboxpath", toolbox.getPath());
        return Output.output(properties, type);
    }

    @Override
    public void init(Toolbox toolbox, String path) {
        this.toolbox = toolbox;
        this.path = path;
        try {
            Output.registerTemplate(TOOLBOX_DESCRIPTION_TEMPLATE, templatePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
