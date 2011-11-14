package org.trianacode.shiwa.executionServices;

import org.trianacode.TrianaInstance;
import org.trianacode.enactment.Exec;
import org.trianacode.enactment.addon.ExecutionAddon;
import org.trianacode.taskgraph.tool.Tool;

import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 04/11/2011
 * Time: 11:53
 * To change this template use File | Settings | File Templates.
 */
public class Bundler implements ExecutionAddon {
    @Override
    public String getServiceName() {
        return null;
    }

    @Override
    public String getLongOption() {
        return "bundle";
    }

    @Override
    public String getShortOption() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public void execute(Exec execEngine, TrianaInstance engine, String workflow, Object workflowObject, Object inputData, String[] TrianaArgs) throws Exception {
    }

    @Override
    public Tool getTool(TrianaInstance instance, String workflowFilePath) throws Exception {
        return null;
    }

    @Override
    public File getConfigFile() throws IOException {
        return null;
    }
}
