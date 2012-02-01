package org.trianacode.shiwa.iwir.exporter;

import org.trianacode.gui.extensions.AbstractFormatFilter;
import org.trianacode.gui.extensions.TaskGraphExporterInterface;
import org.trianacode.shiwa.iwir.importer.utils.ExportIwir;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.TaskGraphException;

import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 07/07/2011
 * Time: 14:56
 * To change this template use File | Settings | File Templates.
 */
public class IwirExporter extends AbstractFormatFilter implements TaskGraphExporterInterface {

    public IwirExporter() {
    }

    @Override
    public void exportWorkflow(TaskGraph taskgraph, File file, boolean appendSuffix) throws IOException, TaskGraphException {
//        IwirCreator.iwirFromTaskGraph(taskgraph, file.getName());
        ExportIwir exportIwir = new ExportIwir();
        exportIwir.taskGraphToIWIRFile(taskgraph, file);
    }

    @Override
    public String getFilterDescription() {
        return null;
    }

    @Override
    public FileFilter[] getChoosableFileFilters() {
        return new FileFilter[0];
    }

    @Override
    public FileFilter getDefaultFileFilter() {
        return null;
    }

    @Override
    public boolean hasOptions() {
        return false;
    }

    @Override
    public int showOptionsDialog(Component parent) {
        return 0;
    }

    public String toString() {
        return "IwirExporter";
    }
}
