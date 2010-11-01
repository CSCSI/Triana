package org.trianacode.http;

import org.thinginitself.http.RequestContext;
import org.thinginitself.http.RequestProcessException;
import org.thinginitself.http.Resource;
import org.thinginitself.http.target.DirectoryTarget;
import org.thinginitself.http.target.TargetResource;
import org.trianacode.config.TrianaProperties;
import org.trianacode.taskgraph.tool.FileToolbox;
import org.trianacode.taskgraph.tool.Tool;
import org.trianacode.taskgraph.tool.Toolbox;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Aug 11, 2010
 */

public class ToolboxResource extends TargetResource {

    private Toolbox toolbox;

    public ToolboxResource(Toolbox toolbox) {
        super(PathController.getInstance().getToolboxPath(toolbox));
        this.toolbox = toolbox;
        if (!(toolbox instanceof FileToolbox)) {
            throw new IllegalArgumentException("Can only be a resource for local file tools");
        }
        ToolboxRenderer rend = new ToolboxRenderer();
        List<String> libs = ((FileToolbox) toolbox).getLibPaths();
        rend.init(toolbox, libs);
        getPathTree().addLocatable(new RendererResource(PathController.getInstance().getToolboxPath(toolbox),
                rend, TrianaProperties.TOOLBOX_DESCRIPTION_TEMPLATE_PROPERTY));
        getPathTree().addLocatable(new RendererResource(PathController.getInstance().getToolboxPath(toolbox) + "classpath.html",
                rend, TrianaProperties.TOOL_CP_HTML_TEMPLATE_PROPERTY));
        getPathTree().addLocatable(new RendererResource(PathController.getInstance().getToolboxPath(toolbox) + "classpath.xml",
                rend, TrianaProperties.TOOL_CP_XML_TEMPLATE_PROPERTY, "text/xml"));

        File classes = ((FileToolbox) toolbox).getFile("classes");
        if (classes != null) {
            getPathTree().addLocatable(new DirectoryTarget(PathController.getInstance().getToolboxPath(toolbox) + "classes/", classes));
        }
        File lib = ((FileToolbox) toolbox).getFile("lib");
        if (lib != null) {
            getPathTree().addLocatable(new DirectoryTarget(PathController.getInstance().getToolboxPath(toolbox) + "lib/", lib));
        }
    }

    public Resource getResource(RequestContext context) throws RequestProcessException {

        String toolPart = PathController.getInstance().getToolPart(toolbox.getName(), context.getRequestTarget().toString());
        if (toolPart != null && toolPart.length() > 0) {
            String[] comps = toolPart.split("/");
            List<String> l = new ArrayList<String>();
            for (String comp : comps) {
                if (comp.indexOf(".") == -1) {
                    l.add(comp);
                }
            }
            if (l.size() > 0) {
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
        }
        return super.getResource(context);
    }

}
