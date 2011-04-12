package org.trianacode.shiwa;

import org.trianacode.config.TrianaProperties;
import org.trianacode.gui.action.files.TaskGraphFileHandler;
import org.trianacode.gui.extensions.AbstractFormatFilter;
import org.trianacode.gui.extensions.TaskGraphImporterInterface;
import org.trianacode.gui.main.organize.DaxOrganize;
import org.trianacode.shiwa.xslt.xsltTransformer;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.TaskGraphException;

import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 07/04/2011
 * Time: 12:25
 * To change this template use File | Settings | File Templates.
 */
public class IwirReader extends AbstractFormatFilter implements TaskGraphImporterInterface {

    public IwirReader() {

    }

    @Override
    public String getFilterDescription() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public FileFilter[] getChoosableFileFilters() {
        return new FileFilter[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public FileFilter getDefaultFileFilter() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean hasOptions() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int showOptionsDialog(Component parent) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String toString() {
        return "IwirReader";
    }

    @Override
    public TaskGraph importWorkflow(File file, TrianaProperties properties) throws TaskGraphException, IOException {

        if (file.exists() && file.canRead()) {
            String root = "triana-shiwa/src/main/java/org/trianacode/shiwa/xslt/";
            String filePath = file.getAbsolutePath();
            String taskgraphFileName = "trianaTaskgraph_" + file.getName() + ".xml";


            new xsltTransformer(filePath, root + "iwir/outputTemp.xml", root + "iwir/removeNamespace.xsl");
            System.out.println("Stripped namespace");
            new xsltTransformer(root + "iwir/outputTemp.xml", taskgraphFileName, root + "iwir/iwir.xsl");
            System.out.println("Created taskgraph file " + taskgraphFileName);
            TaskGraph tg = TaskGraphFileHandler.openTaskgraph(new File(taskgraphFileName), true);
            DaxOrganize daxOrganize = new DaxOrganize(tg);
            return tg;
        }
        return null;
    }
}
