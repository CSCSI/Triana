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
public class DaxFileHolder {

    /** The tool. */
    private Tool tool = null;
    
    /** The filename. */
    private String filename = "";
    
    /** The triana tool name. */
    private String trianaToolName = "";
    
    /** The num input nodes. */
    private int numInputNodes = 0;
    
    /** The num output nodes. */
    private int numOutputNodes = 0;
    
    /** The jobs in. */
    private HashMap jobsIn = new HashMap();
    
    /** The jobs out. */
    private HashMap jobsOut = new HashMap();
    
    /** The connected in nodes. */
    private int connectedInNodes = 0;
    
    /** The connected out nodes. */
    private int connectedOutNodes = 0;

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
     * Gets the filename.
     *
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Sets the filename.
     *
     * @param filename the new filename
     */
    public void setFilename(String filename) {
        this.filename = filename;
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
     * Adds the job in.
     *
     * @param node the node
     * @param id the id
     */
    public void addJobIn(int node, String id) {
        jobsIn.put(node, id);
        numInputNodes++;
    }

    /**
     * Adds the job out.
     *
     * @param node the node
     * @param id the id
     */
    public void addJobOut(int node, String id) {
        jobsOut.put(node, id);
        numOutputNodes++;
    }

    /**
     * Gets the job at in node.
     *
     * @param node the node
     * @return the job at in node
     */
    public String getJobAtInNode(int node) {
        return (String) jobsIn.get(node);
    }

    /**
     * Gets the job at out node.
     *
     * @param node the node
     * @return the job at out node
     */
    public String getJobAtOutNode(int node) {
        return (String) jobsOut.get(node);
    }

    /**
     * Gets the free in node.
     *
     * @return the free in node
     */
    public int getFreeInNode() {
        return jobsIn.size();
    }

    /**
     * Gets the free out node.
     *
     * @return the free out node
     */
    public int getFreeOutNode() {
        return jobsOut.size();
    }

    /**
     * Gets the unconnected in node.
     *
     * @return the unconnected in node
     */
    public int getUnconnectedInNode() {
        connectedInNodes++;
        return (connectedInNodes - 1);
    }

    /**
     * Gets the unconnected out node.
     *
     * @return the unconnected out node
     */
    public int getUnconnectedOutNode() {
        connectedOutNodes++;
        return (connectedOutNodes - 1);
    }
}
