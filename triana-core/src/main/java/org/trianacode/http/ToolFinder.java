package org.trianacode.http;

import org.thinginitself.http.RequestContext;
import org.thinginitself.http.RequestProcessException;
import org.thinginitself.http.Resource;
import org.thinginitself.http.target.TargetResource;
import org.trianacode.taskgraph.tool.Tool;
import org.trianacode.taskgraph.tool.Toolbox;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Oct 29, 2010
 */
public class ToolFinder extends TargetResource {

    private Toolbox toolbox;

    public ToolFinder(Toolbox toolbox) {
        super(PathController.getInstance().getToolboxPath(toolbox));
        this.toolbox = toolbox;
    }

    public Resource getResource(RequestContext context) throws RequestProcessException {
        Resource r = super.getResource(context);
        if (r != null && r != this) {
            return r;
        }
        String toolPart = PathController.getInstance().getToolPart(context.getRequestTarget().toString());
        if (toolPart != null && toolPart.length() > 0) {
            String[] comps = toolPart.split("/");
            List<String> l = new ArrayList<String>();
            for (String comp : comps) {
                if (comp.indexOf(".") == -1) {
                    l.add(comp);
                }
            }
            if (l.size() == 0) {

                return null;
            }
            List<Tool> tools = toolbox.getTools();
            Tool t = null;
            while (l.size() > 0) {
                StringBuilder sb = new StringBuilder(l.get(0));
                for (int i = 1; i < l.size(); i++) {
                    String s = l.get(i);
                    sb.append(".").append(s);
                }
                for (Tool tool : tools) {
                    if (tool.getQualifiedToolName().equals(sb.toString())) {
                        t = tool;
                        break;
                    }
                }
                if (t != null) {
                    break;
                }
                l.remove(l.size() - 1);
            }

            if (t != null) {
                ToolResource res = new ToolResource(PathController.getInstance().getToolPath(t), t);
                getPathTree().addLocatable(res);
                return res.getResource(context);
            }
        }
        return null;
    }
}
