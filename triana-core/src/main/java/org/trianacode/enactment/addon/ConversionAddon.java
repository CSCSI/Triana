package org.trianacode.enactment.addon;

import org.trianacode.taskgraph.tool.Tool;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 04/11/2011
 * Time: 14:36
 * To change this template use File | Settings | File Templates.
 */
public interface ConversionAddon extends CLIaddon {
    public Object toolToWorkflow(Tool tool);

    public Tool workflowToTool(Object workflowObject);

    public Tool processWorkflow(Tool workflow);

    File toolToWorkflowFile(Tool tool, String filePath) throws Exception;
}
