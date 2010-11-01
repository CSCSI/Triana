package org.trianacode.http;

import org.thinginitself.http.target.DirectoryTarget;
import org.thinginitself.http.target.TargetResource;
import org.trianacode.config.TrianaProperties;
import org.trianacode.taskgraph.tool.FileToolbox;
import org.trianacode.taskgraph.tool.Tool;

import java.io.File;
import java.net.URL;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Aug 11, 2010
 */

public class ToolResource extends TargetResource {

    public ToolResource(String path, Tool tool) {
        super(path);
        if (!(tool.getToolBox() instanceof FileToolbox)) {
            throw new IllegalArgumentException("Can only be a resource for local file tools");
        }
        ToolFileRenderer rend = new ToolFileRenderer();
        rend.init(tool);
        String helpFile = tool.getToolName() + ".html";
        URL url = ((FileToolbox) tool.getToolBox()).getClassLoader().getResource(helpFile);
        if (url != null) {
            File help = ((FileToolbox) tool.getToolBox()).getFile("help");
            if (help != null) {
                getPathTree().addLocatable(new DirectoryTarget(PathController.getInstance().getToolPath(tool) + "help/",
                        getRealHelpPath(help, tool)));
            } else {
                getPathTree().addLocatable(new RendererResource(PathController.getInstance().getToolPath(tool) + "help/" + tool.getToolName() + ".html",
                        rend,
                        TrianaProperties.NOHELP_TEMPLATE_PROPERTY));
            }
        } else {
            getPathTree().addLocatable(new RendererResource(PathController.getInstance().getToolPath(tool) + "help/" + tool.getToolName() + ".html",
                    rend,
                    TrianaProperties.NOHELP_TEMPLATE_PROPERTY));
        }
        getPathTree().addLocatable(new RendererResource(PathController.getInstance().getToolPath(tool),
                rend,
                TrianaProperties.TOOL_DESCRIPTION_TEMPLATE_PROPERTY));
        getPathTree().addLocatable(new ToolWriterResource(PathController.getInstance().getToolPath(tool) + "definition.xml", tool));
    }

    //todo

    private File getRealHelpPath(File help, Tool tool) {
        String pkg = tool.getToolPackage();
        if (pkg.indexOf(".") > -1) {
            pkg = pkg.substring(pkg.indexOf(".") + 1);
        } else {
            return help;
        }
        pkg = pkg.replace('.', File.separatorChar);
        String ret = help.getAbsolutePath();
        if (!ret.endsWith(File.separator)) {
            ret += File.separator;
        }
        ret += pkg;
        return new File(ret);
    }


}
