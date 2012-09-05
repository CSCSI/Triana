package org.trianacode.shiwaall.iwir.exporter;

import org.trianacode.gui.extensions.AbstractFormatFilter;
import org.trianacode.gui.extensions.TaskGraphExporterInterface;
import org.trianacode.shiwaall.iwir.importer.utils.ExportIwir;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.TaskGraphException;

import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;

// TODO: Auto-generated Javadoc
/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 07/07/2011
 * Time: 14:56
 * To change this template use File | Settings | File Templates.
 */
public class IwirExporter extends AbstractFormatFilter implements TaskGraphExporterInterface {

    /**
     * Instantiates a new iwir exporter.
     */
    public IwirExporter() {
    }

    /* (non-Javadoc)
     * @see org.trianacode.gui.extensions.TaskGraphExporterInterface#exportWorkflow(org.trianacode.taskgraph.TaskGraph, java.io.File, boolean)
     */
    @Override
    public void exportWorkflow(TaskGraph taskgraph, File file, boolean appendSuffix) throws IOException, TaskGraphException {
//        IwirCreator.iwirFromTaskGraph(taskgraph, file.getName());
        ExportIwir exportIwir = new ExportIwir();
        exportIwir.taskGraphToIWIRFile(taskgraph, file);
    }

    /* (non-Javadoc)
     * @see org.trianacode.gui.extensions.AbstractFormatFilter#getFilterDescription()
     */
    @Override
    public String getFilterDescription() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.trianacode.gui.extensions.AbstractFormatFilter#getChoosableFileFilters()
     */
    @Override
    public FileFilter[] getChoosableFileFilters() {
        return new FileFilter[0];
    }

    /* (non-Javadoc)
     * @see org.trianacode.gui.extensions.AbstractFormatFilter#getDefaultFileFilter()
     */
    @Override
    public FileFilter getDefaultFileFilter() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.trianacode.gui.extensions.AbstractFormatFilter#hasOptions()
     */
    @Override
    public boolean hasOptions() {
        return false;
    }

    /* (non-Javadoc)
     * @see org.trianacode.gui.extensions.AbstractFormatFilter#showOptionsDialog(java.awt.Component)
     */
    @Override
    public int showOptionsDialog(Component parent) {
        return 0;
    }

    /* (non-Javadoc)
     * @see org.trianacode.gui.extensions.AbstractFormatFilter#toString()
     */
    public String toString() {
        return "IwirExporter";
    }
}
