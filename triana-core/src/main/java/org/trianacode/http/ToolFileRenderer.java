package org.trianacode.http;

import org.thinginitself.streamable.Streamable;
import org.trianacode.taskgraph.tool.Tool;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 20, 2010
 */

public class ToolFileRenderer implements Renderer {

    private Tool tool;

    public void init(Tool tool) {
        this.tool = tool;
    }

    public Streamable render(String type, String mime) {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("toolpath", PathController.getInstance().getToolPath(tool));
        properties.put("tool", tool.getQualifiedToolName());
        properties.put("toolname", tool.getToolName());
        properties.put("toolbox", tool.getToolBox().getName());

        return Output.output(properties, type, mime);
    }


}
