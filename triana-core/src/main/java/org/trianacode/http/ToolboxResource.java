package org.trianacode.http;

import org.thinginitself.http.MimeType;
import org.thinginitself.http.Path;
import org.thinginitself.http.target.TargetResource;
import org.thinginitself.streamable.Streamable;
import org.trianacode.taskgraph.tool.Toolbox;

import java.util.List;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Aug 11, 2010
 */

public class ToolboxResource extends TargetResource {

    public ToolboxResource(Toolbox toolbox) {
        super(toolbox.getName());
    }


    @Override
    public Streamable get(Path path, List<MimeType> mimeTypes) {
        return null;
    }

    @Override
    public Streamable put(Path path, List<MimeType> mimeTypes, Streamable streamable) {
        return null;
    }

    @Override
    public Streamable post(Path path, List<MimeType> mimeTypes, Streamable streamable) {
        return null;
    }

    @Override
    public Streamable delete(Path path, List<MimeType> mimeTypes) {
        return null;
    }
}
