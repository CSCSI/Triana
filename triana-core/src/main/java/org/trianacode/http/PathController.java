package org.trianacode.http;

import org.thinginitself.http.util.Coder;
import org.trianacode.taskgraph.tool.Tool;
import org.trianacode.taskgraph.tool.Toolbox;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Oct 30, 2010
 */
public class PathController {

    public static String getToolboxes() {
        return "toolboxes/";
    }

    public static String getResources() {
        return "files/";
    }


    public static String getRoot() {
        return "/triana/";
    }

    public static String getToolboxesRoot() {
        return getRoot() + getToolboxes();
    }

    public static String getResourcesRoot() {
        return getRoot() + getResources();
    }


    public static String getPath(Toolbox toolbox) {
        return getToolboxesRoot() + Coder.encodeUTF8(toolbox.getName()) + "/";
    }

    public static String getPath(Tool tool) {
        Toolbox tb = tool.getToolBox();
        StringBuilder sb = new StringBuilder();
        String[] comps = tool.getQualifiedToolName().split("\\.");
        for (String comp : comps) {
            sb.append(Coder.encodeUTF8(comp)).append("/");
        }
        if (tb != null) {
            return getPath(tb) + sb.toString();
        }
        return getRoot() + sb.toString();
    }
}
