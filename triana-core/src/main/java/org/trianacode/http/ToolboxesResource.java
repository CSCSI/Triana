package org.trianacode.http;

import org.thinginitself.http.MimeType;
import org.thinginitself.http.target.TargetResource;
import org.thinginitself.streamable.Streamable;
import org.trianacode.taskgraph.tool.ToolResolver;

import java.util.List;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Oct 29, 2010
 */
public class ToolboxesResource extends TargetResource {

    private ToolResolver resolver;

    public ToolboxesResource(ToolResolver resolver) {
        super(PathController.getInstance().getToolboxesRoot());
        this.resolver = resolver;
    }

    public Streamable getStreamable(List<MimeType> mimes) {
        System.out.println("ToolboxesResource$ListToolboxesResource.getStreamable CALLED");
        ToolboxesRenderer r = new ToolboxesRenderer();
        r.init(resolver.getToolboxes(), getPath().toString());
        Streamable s = r.render(ToolboxesRenderer.TOOLBOXES_DESCRIPTION_TEMPLATE);
        System.out.println("ToolboxesResource$ListToolboxesResource.getStreamable:" + s);
        return s;
    }
}
