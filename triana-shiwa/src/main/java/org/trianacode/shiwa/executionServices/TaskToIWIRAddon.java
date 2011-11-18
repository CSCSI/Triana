package org.trianacode.shiwa.executionServices;

import org.trianacode.enactment.addon.ConversionAddon;
import org.trianacode.shiwa.iwir.importer.utils.ExportIwir;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.tool.Tool;

import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 17/11/2011
 * Time: 16:49
 * To change this template use File | Settings | File Templates.
 */
public class TaskToIWIRAddon implements ConversionAddon {

    @Override
    public Object toolToWorkflow(Tool tool) throws IOException {
        ExportIwir exportIwir = new ExportIwir();
        return exportIwir.taskGraphToBlockScope((TaskGraph) tool);
    }

    @Override
    public Tool workflowToTool(Object workflowObject) {
        return null;
    }

    @Override
    public Tool processWorkflow(Tool workflow) {
        return null;
    }

    @Override
    public File toolToWorkflowFile(Tool tool, File configFile, String filePath) throws Exception {
        ExportIwir exportIwir = new ExportIwir();
        File outputFile = new File(filePath);
        exportIwir.taskGraphToIWIRFile((TaskGraph) tool, outputFile);
        return outputFile;
    }

    @Override
    public String getServiceName() {
        return "TaskGraphToIWIR";
    }

    @Override
    public String getLongOption() {
        return "taskgraph-to-iwir";
    }

    @Override
    public String getShortOption() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Converts TaskGraph objects to IWIR blockscopes, or IWIR files.";
    }
}
