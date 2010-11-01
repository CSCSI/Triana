package org.trianacode.http;

import org.thinginitself.streamable.Streamable;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.tool.Tool;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 20, 2010
 */

public class ToolParameterRenderer implements Renderer {


    private Task parent;
    private Tool tool;
    private String path;


    public void init(Tool tool, String path) {
        if (tool instanceof Task) {
            Task t = (Task) tool;
            this.parent = t.getUltimateParent();
        }
        this.tool = tool;
        this.path = path;

    }


    public Streamable render(String type, String mime) {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("path", path);
        properties.put("toolname", parent.getToolName());
        properties.put("toolpackage", parent.getToolPackage());
        properties.put("subtoolname", tool.getToolName());
        properties.put("subtoolpackage", tool.getToolPackage());
        return Output.output(properties, type, mime);
    }
}
