package org.trianacode.pegasus.dax;

import org.trianacode.taskgraph.tool.Tool;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: ian
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
    private HashMap jobIn = new HashMap();
    private HashMap jobOut = new HashMap();

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

    public void addJobIn(int node, String id){
        jobIn.put(node, id);
        numInputNodes ++;
    }

    public void addJobOut(int node, String id){
        jobOut.put(node, id);
        numOutputNodes++;
    }

    public String getJobAtInNode(int node){
        return (String)jobIn.get(node);
    }

    public String getJobAtOutNode(int node){
        return (String)jobOut.get(node);
    }
}
