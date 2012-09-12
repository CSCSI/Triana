package org.trianacode.shiwaall.iwir.importer;

import org.shiwa.fgi.iwir.IWIR;
import org.trianacode.config.TrianaProperties;
import org.trianacode.gui.extensions.AbstractFormatFilter;
import org.trianacode.gui.extensions.TaskGraphImporterInterface;
import org.trianacode.shiwaall.executionServices.TaskTypeToolDescriptor;
import org.trianacode.shiwaall.iwir.importer.utils.ImportIwir;
import org.trianacode.shiwaall.iwir.importer.utils.TaskTypeRepo;
import org.trianacode.shiwaall.test.InOut;
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
 * Date: 07/04/2011
 * Time: 12:25
 * To change this template use File | Settings | File Templates.
 */
public class IwirReader extends AbstractFormatFilter implements TaskGraphImporterInterface {


    /**
     * Instantiates a new iwir reader.
     */
    public IwirReader() {

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
        return "IwirReader";
    }


    /* (non-Javadoc)
     * @see org.trianacode.gui.extensions.TaskGraphImporterInterface#importWorkflow(java.io.File, org.trianacode.config.TrianaProperties)
     */
    @Override
    public TaskGraph importWorkflow(File file, TrianaProperties properties) throws TaskGraphException, IOException {
        try {
//            return new IwirToTaskGraph().importIWIR(file, properties);
            TaskTypeRepo.addTaskType(new TaskTypeToolDescriptor("InOut", InOut.class, properties));

            return new ImportIwir().taskFromIwir(new IWIR(file), null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


}
