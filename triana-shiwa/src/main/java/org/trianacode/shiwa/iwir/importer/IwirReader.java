package org.trianacode.shiwa.iwir.importer;

import org.shiwa.fgi.iwir.IWIR;
import org.trianacode.config.TrianaProperties;
import org.trianacode.gui.extensions.AbstractFormatFilter;
import org.trianacode.gui.extensions.TaskGraphImporterInterface;
import org.trianacode.shiwa.iwir.importer.utils.ImportIwir;
import org.trianacode.shiwa.iwir.importer.utils.TaskTypeToTool;
import org.trianacode.shiwa.test.InOut;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.TaskGraphException;

import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 07/04/2011
 * Time: 12:25
 * To change this template use File | Settings | File Templates.
 */
public class IwirReader extends AbstractFormatFilter implements TaskGraphImporterInterface {


    public IwirReader() {

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
        return "IwirReader";
    }


    @Override
    public TaskGraph importWorkflow(File file, TrianaProperties properties) throws TaskGraphException, IOException {
        try {
//            return new IwirToTaskGraph().importIWIR(file, properties);
            TaskTypeToTool.addTaskType("InOut", InOut.class);

            return new ImportIwir().taskFromIwir(new IWIR(file));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


}
