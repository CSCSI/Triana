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

public class BasicToolRenderer implements ToolRenderer {

    private Tool tool;
    private String path;
    private String create = "/templates/tool-create.tpl";
    private String instance = "/templates/tool-instance.tpl";
    private String description = "/templates/tool-description.tpl";
    private String complete = "/templates/tool-complete.tpl";

    @Override
    public void init(Tool tool, String path) {
        this.tool = tool;
        this.path = path;
        try {
            Output.registerTemplate(TOOL_CREATE_INSTANCE_TEMPLATE, create);
            Output.registerTemplate(TOOL_COMPLETED_TEMPLATE, complete);
            Output.registerTemplate(TOOL_DESCRIPTION_TEMPLATE, description);
            Output.registerTemplate(TOOL_INSTANCE_TEMPLATE, instance);
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
