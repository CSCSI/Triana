package org.trianacode.shiwaall.dax;

import org.trianacode.taskgraph.tool.Tool;

import java.util.HashMap;

// TODO: Auto-generated Javadoc
/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: Aug 20, 2010
 * Time: 12:49:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class DaxJobHolder {

    /** The tool. */
    private Tool tool = null;
    
    /** The toolname. */
    private String toolname = "";
    
    /** The job id. */
    private String jobID = "";
    
    /** The triana tool name. */
    private String trianaToolName = "";
    
    /** The num input nodes. */
    private int numInputNodes = 0;
    
    /** The num output nodes. */
    private int numOutputNodes = 0;
    
    /** The files in. */
    private HashMap filesIn = new HashMap();
    
    /** The files out. */
    private HashMap filesOut = new HashMap();
    
    /** The is collection. */
    private boolean isCollection = false;

    /**
     * Gets the tool.
     *
     * @return the tool
     */
    public Tool getTool() {
        return tool;
    }

    /**
     * Sets the tool.
     *
     * @param tool the new tool
     */
    public void setTool(Tool tool) {
        this.tool = tool;
    }

    /**
     * Gets the toolname.
     *
     * @return the toolname
     */
    public String getToolname() {
        return toolname;
    }

    /**
     * Sets the toolname.
     *
     * @param toolname the new toolname
     */
    public void setToolname(String toolname) {
        this.toolname = toolname;
    }

    /**
     * Gets the files in.
     *
     * @return the files in
     */
    public HashMap getFilesIn() {
        return filesIn;
    }

    /**
     * Gets the files out.
     *
     * @return the files out
     */
    public HashMap getFilesOut() {
        return filesOut;
    }


    /**
     * Gets the num input nodes.
     *
     * @return the num input nodes
     */
    public int getNumInputNodes() {
        return numInputNodes;
    }

    /**
     * Sets the num input nodes.
     *
     * @param numInputNodes the new num input nodes
     */
    public void setNumInputNodes(int numInputNodes) {
        this.numInputNodes = numInputNodes;
    }

    /**
     * Gets the num output nodes.
     *
     * @return the num output nodes
     */
    public int getNumOutputNodes() {
        return numOutputNodes;
    }

    /**
     * Sets the num output nodes.
     *
     * @param numOutputNodes the new num output nodes
     */
    public void setNumOutputNodes(int numOutputNodes) {
        this.numOutputNodes = numOutputNodes;
    }

    /**
     * Gets the triana tool name.
     *
     * @return the triana tool name
     */
    public String getTrianaToolName() {
        return trianaToolName;
    }

    /**
     * Sets the triana tool name.
     *
     * @param trianaToolName the new triana tool name
     */
    public void setTrianaToolName(String trianaToolName) {
        this.trianaToolName = trianaToolName;
    }

    /**
     * Gets the job id.
     *
     * @return the job id
     */
    public String getJobID() {
        return jobID;
    }

    /**
     * Sets the job id.
     *
     * @param jobID the new job id
     */
    public void setJobID(String jobID) {
        this.jobID = jobID;
    }

    /**
     * Adds the file in.
     *
     * @param node the node
     * @param link the link
     */
    public void addFileIn(int node, String link) {
        filesIn.put(node, link);
    }

    /**
     * Adds the file out.
     *
     * @param node the node
     * @param link the link
     */
    public void addFileOut(int node, String link) {
        filesOut.put(node, link);
    }

    /**
     * Gets the link at in node.
     *
     * @param node the node
     * @return the link at in node
     */
    public String getLinkAtInNode(int node) {
        return (String) filesIn.get(node);
    }

    /**
     * Gets the link at out node.
     *
     * @param node the node
     * @return the link at out node
     */
    public String getLinkAtOutNode(int node) {
        return (String) filesOut.get(node);
    }

    /**
     * Checks if is collection.
     *
     * @return true, if is collection
     */
    public boolean isCollection() {
        return isCollection;
    }

    /**
     * Sets the collection.
     *
     * @param collection the new collection
     */
    public void setCollection(boolean collection) {
        isCollection = collection;
    }
}
