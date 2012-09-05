package org.trianacode.shiwaall.executionServices;

import org.shiwa.fgi.iwir.IWIR;
import org.trianacode.enactment.addon.ConversionAddon;
import org.trianacode.shiwaall.iwir.importer.utils.ImportIwir;
import org.trianacode.taskgraph.tool.Tool;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

// TODO: Auto-generated Javadoc
/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 05/07/2012
 * Time: 11:22
 * To change this template use File | Settings | File Templates.
 */
public class IwirToTaskAddon implements ConversionAddon {
    
    /* (non-Javadoc)
     * @see org.trianacode.enactment.addon.ConversionAddon#toolToWorkflow(org.trianacode.taskgraph.tool.Tool)
     */
    @Override
    public Object toolToWorkflow(Tool tool) throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /* (non-Javadoc)
     * @see org.trianacode.enactment.addon.ConversionAddon#workflowToTool(java.lang.Object)
     */
    @Override
    public Tool workflowToTool(Object workflowObject) {
        if(workflowObject instanceof File){
            ImportIwir importIwir = new ImportIwir();
            try {
                return importIwir.taskFromIwir(new IWIR((File) workflowObject), null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.trianacode.enactment.addon.ConversionAddon#processWorkflow(org.trianacode.taskgraph.tool.Tool)
     */
    @Override
    public Tool processWorkflow(Tool workflow) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /* (non-Javadoc)
     * @see org.trianacode.enactment.addon.ConversionAddon#toolToWorkflowFile(org.trianacode.taskgraph.tool.Tool, java.io.File, java.lang.String)
     */
    @Override
    public File toolToWorkflowFile(Tool tool, File configFile, String filePath) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /* (non-Javadoc)
     * @see org.trianacode.enactment.addon.ConversionAddon#toolToWorkflowFileInputStream(org.trianacode.taskgraph.tool.Tool)
     */
    @Override
    public InputStream toolToWorkflowFileInputStream(Tool tool) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /* (non-Javadoc)
     * @see org.trianacode.enactment.addon.CLIaddon#getServiceName()
     */
    @Override
    public String getServiceName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /* (non-Javadoc)
     * @see org.trianacode.enactment.addon.CLIaddon#getLongOption()
     */
    @Override
    public String getLongOption() {
        return "IWIR-To-Task";
    }

    /* (non-Javadoc)
     * @see org.trianacode.enactment.addon.CLIaddon#getShortOption()
     */
    @Override
    public String getShortOption() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /* (non-Javadoc)
     * @see org.trianacode.enactment.addon.CLIaddon#getDescription()
     */
    @Override
    public String getDescription() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /* (non-Javadoc)
     * @see org.trianacode.enactment.addon.CLIaddon#getUsageString()
     */
    @Override
    public String getUsageString() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
