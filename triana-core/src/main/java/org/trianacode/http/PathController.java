package org.trianacode.http;

import org.thinginitself.http.util.Coder;
import org.trianacode.taskgraph.tool.Tool;
import org.trianacode.taskgraph.tool.Toolbox;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Oct 30, 2010
 */
public class PathController {

    private static PathController pathController = new PathController();

    private PathController() {
    }

    public static PathController getInstance() {
        return pathController;
    }


    public String getToolboxes() {
        return "toolboxes/";
    }

    public String getResources() {
        return "files/";
    }


    public String getRoot() {
        return "/triana/";
    }

    public String getToolboxesRoot() {
        return getRoot() + getToolboxes();
    }

    public String getResourcesRoot() {
        return getRoot() + getResources();
    }


    public String getToolboxPath(Toolbox toolbox) {
        return getToolboxesRoot() + Coder.encodeUTF8(toolbox.getName()) + "/";
    }

    public String getToolPart(String toolbox, String path) {
        if (path.startsWith(getToolboxesRoot())) {
            String root = path.substring(getToolboxesRoot().length());
            if (!root.startsWith(toolbox)) {
                return null;
            }
            String tool = root.substring(toolbox.length(), root.length());
            while (tool.startsWith("/")) {
                tool = tool.substring(1);
            }
            return tool;

        }
        return null;
    }

    public String getToolboxPart(String path) {
        if (path.startsWith(getToolboxesRoot())) {
            String tool = path.substring(getToolboxesRoot().length());
            while (tool.startsWith("/")) {
                tool = tool.substring(1);
            }
            while (tool.endsWith("/")) {
                tool = tool.substring(0, tool.length() - 1);
            }
            return tool;
        }
        return null;
    }


    public String getToolPath(Tool tool) {
        Toolbox tb = tool.getToolBox();
        StringBuilder sb = new StringBuilder();
        String[] comps = tool.getQualifiedToolName().split("\\.");
        for (String comp : comps) {
            sb.append(Coder.encodeUTF8(comp)).append("/");
        }
        if (tb != null) {
            return getToolboxPath(tb) + sb.toString();
        }
        return getRoot() + sb.toString();
    }
}
