package org.trianacode.http;

import org.thinginitself.http.Resource;
import org.thinginitself.http.target.DirectoryTarget;
import org.thinginitself.http.target.TargetResource;
import org.thinginitself.streamable.StreamableData;
import org.trianacode.config.TrianaProperties;
import org.trianacode.taskgraph.ser.XMLWriter;
import org.trianacode.taskgraph.tool.FileToolbox;
import org.trianacode.taskgraph.tool.Tool;

import java.io.*;
import java.net.URL;
import java.util.List;

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
        List<String> libs = ((FileToolbox) tool.getToolBox()).getLibPaths();
        rend.init(tool, libs);
        String helpFile = tool.getToolName() + ".html";
        URL url = ((FileToolbox) tool.getToolBox()).getClassLoader().getResource(helpFile);
        if (url != null) {
            File help = ((FileToolbox) tool.getToolBox()).getFile("help");
            if (help != null) {
                getPathTree().addLocatable(new DirectoryTarget(PathController.getInstance().getToolPath(tool) + "help/", getRealHelpPath(help, tool)));
            } else {
                getPathTree().addLocatable(new Resource(PathController.getInstance().getToolPath(tool) + "help/" + tool.getToolName() + ".html", rend.render(TrianaProperties.NOHELP_TEMPLATE_PROPERTY)));
            }
        } else {
            getPathTree().addLocatable(new Resource(PathController.getInstance().getToolPath(tool) + "help/" + tool.getToolName() + ".html", rend.render(TrianaProperties.NOHELP_TEMPLATE_PROPERTY)));
        }
        getPathTree().addLocatable(new Resource(PathController.getInstance().getToolPath(tool) + "classpath.html", rend.render(TrianaProperties.TOOL_CP_HTML_TEMPLATE_PROPERTY)));
        getPathTree().addLocatable(new Resource(PathController.getInstance().getToolPath(tool), rend.render(TrianaProperties.TOOL_DESCRIPTION_TEMPLATE_PROPERTY)));
        File classes = ((FileToolbox) tool.getToolBox()).getFile("classes");

        if (classes != null) {
            getPathTree().addLocatable(new DirectoryTarget(PathController.getInstance().getToolPath(tool) + "classes/", classes));
        }
        File lib = ((FileToolbox) tool.getToolBox()).getFile("lib");
        if (lib != null) {
            getPathTree().addLocatable(new DirectoryTarget(PathController.getInstance().getToolPath(tool) + "lib/", lib));
        }


        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            XMLWriter writer = new XMLWriter(new BufferedWriter(new OutputStreamWriter(bout)));
            writer.writeComponent(tool);
            writer.close();
            getPathTree().addLocatable(new Resource(PathController.getInstance().getToolPath(tool) + "definition.xml", new StreamableData(bout.toByteArray(), "text/xml")));
        } catch (IOException e) {
            e.printStackTrace();
            // todo
        }

    }

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
