package org.trianacode.pegasus.dax;

import org.trianacode.taskgraph.tool.Tool;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: Aug 20, 2010
 * Time: 12:49:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class DaxFileHolder {

    private Tool tool = null;
    private String filename = "";
    private String trianaToolName = "";
    private int numInputNodes = 0;
    private int numOutputNodes = 0;
    private HashMap jobsIn = new HashMap();
    private HashMap jobsOut = new HashMap();
    private int connectedInNodes = 0;
    private int connectedOutNodes = 0;

    public Tool getTool() {
        return tool;
    }

    public void setTool(Tool tool) {
        this.tool = tool;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getNumInputNodes() {
        return numInputNodes;
    }

    public void setNumInputNodes(int numInputNodes) {
        this.numInputNodes = numInputNodes;
    }

    public int getNumOutputNodes() {
        return numOutputNodes;
    }

    public void setNumOutputNodes(int numOutputNodes) {
        this.numOutputNodes = numOutputNodes;
    }

    public String getTrianaToolName() {
        return trianaToolName;
    }

    public void setTrianaToolName(String trianaToolName) {
        this.trianaToolName = trianaToolName;
    }

    public void addJobIn(int node, String id) {
        jobsIn.put(node, id);
        numInputNodes++;
    }

    public void addJobOut(int node, String id) {
        jobsOut.put(node, id);
        numOutputNodes++;
    }

    public String getJobAtInNode(int node) {
        return (String) jobsIn.get(node);
    }

    public String getJobAtOutNode(int node) {
        return (String) jobsOut.get(node);
    }

    public int getFreeInNode() {
        return jobsIn.size();
    }

    public int getFreeOutNode() {
        return jobsOut.size();
    }

    public int getUnconnectedInNode() {
        connectedInNodes++;
        return (connectedInNodes - 1);
    }

    public int getUnconnectedOutNode() {
        connectedOutNodes++;
        return (connectedOutNodes - 1);
    }
}
