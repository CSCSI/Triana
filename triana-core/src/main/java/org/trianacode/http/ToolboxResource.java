package org.trianacode.http;

import org.thinginitself.http.target.TargetResource;
import org.trianacode.taskgraph.tool.Toolbox;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Aug 11, 2010
 */

public class ToolboxResource extends TargetResource {

    public ToolboxResource(Toolbox toolbox) {
        super(toolbox.getName());
    }

}
