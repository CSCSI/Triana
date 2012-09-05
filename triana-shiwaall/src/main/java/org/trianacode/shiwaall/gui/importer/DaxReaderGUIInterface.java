package org.trianacode.shiwaall.gui.importer;

import org.trianacode.config.TrianaProperties;
import org.trianacode.gui.extensions.AbstractFormatFilter;
import org.trianacode.gui.extensions.TaskGraphImporterInterface;
import org.trianacode.gui.main.organize.TaskGraphOrganize;
import org.trianacode.shiwaall.dax.DaxReader;
import org.trianacode.shiwaall.gui.guiUnits.DaxFile;
import org.trianacode.shiwaall.gui.guiUnits.DaxJob;
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
 * Date: 19/05/2011
 * Time: 15:15
 * To change this template use File | Settings | File Templates.
 */
public class DaxReaderGUIInterface extends AbstractFormatFilter implements TaskGraphImporterInterface {
    
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
        return "DaxReader";
    }

    /* (non-Javadoc)
     * @see org.trianacode.gui.extensions.TaskGraphImporterInterface#importWorkflow(java.io.File, org.trianacode.config.TrianaProperties)
     */
    @Override
    public TaskGraph importWorkflow(File file, TrianaProperties properties) throws TaskGraphException, IOException {
        DaxReader daxReader = new DaxReader(
                DaxFile.class.getPackage().getName(),
                DaxFile.class.getSimpleName(),
                DaxJob.class.getSimpleName()
        );
        TaskGraph tg = daxReader.importWorkflow(file, properties);
        try {
            TaskGraphOrganize.organizeTaskGraph(TaskGraphOrganize.DAX_ORGANIZE, tg);
        } catch (Exception e) {
            System.out.println("Organise failed.");
        }

        return tg;
    }
}
