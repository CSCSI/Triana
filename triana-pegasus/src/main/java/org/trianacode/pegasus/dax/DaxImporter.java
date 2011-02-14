package org.trianacode.pegasus.dax;

import org.trianacode.config.TrianaProperties;
import org.trianacode.gui.extensions.TaskGraphImporterInterface;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.TaskGraphException;

import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Aug 31, 2010
 * Time: 11:04:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class DaxImporter implements TaskGraphImporterInterface {

    @Override
    public TaskGraph importWorkflow(File file, TrianaProperties properties) throws TaskGraphException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
