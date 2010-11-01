package org.trianacode.http;

import org.thinginitself.http.MimeType;
import org.thinginitself.http.target.TargetResource;
import org.thinginitself.streamable.Streamable;
import org.trianacode.config.TrianaProperties;
import org.trianacode.taskgraph.tool.Toolbox;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Oct 29, 2010
 */
public class ToolboxesResource extends TargetResource {

    private List<Toolbox> toolboxes = new ArrayList<Toolbox>();

    public ToolboxesResource() {
        super(PathController.getInstance().getToolboxesRoot());
    }

    public Streamable getStreamable(List<MimeType> mimes) {
        ToolboxesRenderer r = new ToolboxesRenderer();
        r.init(toolboxes, getPath().toString());
        Streamable s = r.render(TrianaProperties.TOOLBOXES_DESCRIPTION_TEMPLATE_PROPERTY, "text/html");
        return s;
    }

    public void toolboxAdded(Toolbox toolbox) {
        toolboxes.add(toolbox);
        getPathTree().addLocatable(new ToolboxResource(toolbox));

    }

    public void toolboxRemoved(Toolbox toolbox) {
        toolboxes.remove(toolbox);
        getPathTree().removeLocatable(PathController.getInstance().getToolboxPath(toolbox));

    }
}
