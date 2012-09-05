package org.trianacode.shiwaall.executionServices;

import org.shiwa.fgi.iwir.BlockScope;
import org.shiwa.fgi.iwir.IWIR;
import org.trianacode.enactment.addon.ConversionAddon;
import org.trianacode.shiwaall.iwir.importer.utils.ExportIwir;
import org.trianacode.shiwaall.iwir.importer.utils.ImportIwir;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.tool.Tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

// TODO: Auto-generated Javadoc
/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 17/11/2011
 * Time: 16:49
 * To change this template use File | Settings | File Templates.
 */
public class TaskToIWIRAddon implements ConversionAddon {

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "IWIR";
    }

    /* (non-Javadoc)
     * @see org.trianacode.enactment.addon.CLIaddon#getUsageString()
     */
    @Override
    public String getUsageString() {
        return "";
    }

    /* (non-Javadoc)
     * @see org.trianacode.enactment.addon.ConversionAddon#toolToWorkflow(org.trianacode.taskgraph.tool.Tool)
     */
    @Override
    public Object toolToWorkflow(Tool tool) throws IOException {
        ExportIwir exportIwir = new ExportIwir();
        return exportIwir.taskGraphToBlockScope((TaskGraph) tool);
    }

    /* (non-Javadoc)
     * @see org.trianacode.enactment.addon.ConversionAddon#workflowToTool(java.lang.Object)
     */
    @Override
    public Tool workflowToTool(Object workflowObject) {
        try {
            IWIR iwir = null;
            Tool tool;
            if (workflowObject instanceof String) {
                File file = new File((String) workflowObject);
                if (file.exists()) {
                    iwir = new IWIR(file);
                }
            } else if (workflowObject instanceof File) {
                File file = (File) workflowObject;
                if (file.exists()) {
                    iwir = new IWIR(file);
                }
            }
            if (iwir != null) {
                ImportIwir iwirImporter = new ImportIwir();
                tool = iwirImporter.taskFromIwir(iwir, null);
                return tool;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.trianacode.enactment.addon.ConversionAddon#processWorkflow(org.trianacode.taskgraph.tool.Tool)
     */
    @Override
    public Tool processWorkflow(Tool workflow) {
        return null;
    }

    /* (non-Javadoc)
     * @see org.trianacode.enactment.addon.ConversionAddon#toolToWorkflowFile(org.trianacode.taskgraph.tool.Tool, java.io.File, java.lang.String)
     */
    @Override
    public File toolToWorkflowFile(Tool tool, File configFile, String filePath) throws Exception {
        ExportIwir exportIwir = new ExportIwir();
        File outputFile = new File(filePath);
        exportIwir.taskGraphToIWIRFile((TaskGraph) tool, outputFile);
        return outputFile;
    }

    /* (non-Javadoc)
     * @see org.trianacode.enactment.addon.ConversionAddon#toolToWorkflowFileInputStream(org.trianacode.taskgraph.tool.Tool)
     */
    @Override
    public InputStream toolToWorkflowFileInputStream(Tool tool) {
        try {
            BlockScope blockscope = (BlockScope) toolToWorkflow(tool);
            IWIR iwir = new IWIR(tool.getToolName());
            iwir.setTask(blockscope);
            File file = File.createTempFile("iwir", "tmp");
            file.deleteOnExit();
            iwir.asXMLFile(file);
            return new FileInputStream(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /* (non-Javadoc)
     * @see org.trianacode.enactment.addon.CLIaddon#getServiceName()
     */
    @Override
    public String getServiceName() {
        return "TaskGraphToIWIR";
    }

    /* (non-Javadoc)
     * @see org.trianacode.enactment.addon.CLIaddon#getLongOption()
     */
    @Override
    public String getLongOption() {
        return "iwir-converter";
    }

    /* (non-Javadoc)
     * @see org.trianacode.enactment.addon.CLIaddon#getShortOption()
     */
    @Override
    public String getShortOption() {
        return "iwir";
    }

    /* (non-Javadoc)
     * @see org.trianacode.enactment.addon.CLIaddon#getDescription()
     */
    @Override
    public String getDescription() {
        return "Converts TaskGraph objects to IWIR blockscopes, or IWIR files.";
    }
}
