package org.trianacode.pegasus.gui.importer;

import org.trianacode.config.TrianaProperties;
import org.trianacode.gui.extensions.AbstractFormatFilter;
import org.trianacode.gui.extensions.TaskGraphImporterInterface;
import org.trianacode.gui.main.organize.TaskGraphOrganize;
import org.trianacode.pegasus.dax.DaxReader;
import org.trianacode.pegasus.gui.guiUnits.DaxFile;
import org.trianacode.pegasus.gui.guiUnits.DaxJob;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.TaskGraphException;

import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 19/05/2011
 * Time: 15:15
 * To change this template use File | Settings | File Templates.
 */
public class DaxReaderGUIInterface extends AbstractFormatFilter implements TaskGraphImporterInterface {
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
        return "DaxReader";
    }

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
