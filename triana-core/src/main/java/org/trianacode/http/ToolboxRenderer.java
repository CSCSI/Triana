package org.trianacode.http;

import org.thinginitself.streamable.Streamable;
import org.trianacode.taskgraph.tool.Toolbox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 20, 2010
 */

public class ToolboxRenderer implements Renderer {

    private Toolbox toolbox;
    private List<String> libs;

    public void init(Toolbox toolbox, List<String> libs) {
        this.toolbox = toolbox;
        this.libs = libs;

    }

    public Streamable render(String type, String mime) {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("paths", libs);
        properties.put("toolbox", toolbox.getName());
        properties.put("toolboxpath", PathController.getInstance().getToolboxPath(toolbox));

        return Output.output(properties, type, mime);
    }

}
