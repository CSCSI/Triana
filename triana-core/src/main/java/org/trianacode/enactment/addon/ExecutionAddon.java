package org.trianacode.enactment.addon;

import org.trianacode.TrianaInstance;
import org.trianacode.enactment.Exec;
import org.trianacode.taskgraph.tool.Tool;

import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 17/08/2011
 * Time: 18:03
 * To change this template use File | Settings | File Templates.
 */

//Discoverable from Exec, and can step in to run the workflow if extra processing is needed before execution.
//it is up to the ExecutionAddon to do execEngine.execute(tool, data) if required.
public interface ExecutionAddon extends CLIaddon {

    public void execute(Exec execEngine, TrianaInstance engine, String workflow, Object workflowObject, Object inputData, String[] TrianaArgs) throws Exception;

    public Tool getTool(TrianaInstance instance, String workflowFilePath) throws Exception;

    public File getConfigFile() throws IOException;
}
