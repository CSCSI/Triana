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
public class DaxJobHolder {

    private Tool tool = null;
    private String toolname = "";
    private String jobID = "";
    private String trianaToolName = "";
    private int numInputNodes = 0;
    private int numOutputNodes = 0;
    private HashMap filesIn = new HashMap();
    private HashMap filesOut = new HashMap();
    private boolean isCollection = false;

    public Tool getTool() {
        return tool;
    }

    public void setTool(Tool tool) {
        this.tool = tool;
    }

    public String getToolname() {
        return toolname;
    }
    
    public void setToolname(String toolname) {
        this.toolname = toolname;
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

    public String getJobID() {
        return jobID;
    }

    public void setJobID(String jobID) {
        this.jobID = jobID;
    }

    public void addFileIn(int node, String link){
        filesIn.put(node, link);
    }

    public void addFileOut(int node, String link){
        filesOut.put(node, link);
    }

    public String getLinkAtInNode(int node){
        return (String)filesIn.get(node);
    }

    public String getLinkAtOutNode(int node){
        return (String)filesOut.get(node);
    }

    public boolean isCollection() {
        return isCollection;
    }

    public void setCollection(boolean collection) {
        isCollection = collection;
    }
}
