package org.trianacode.discovery.toolinfo;

import java.io.Serializable;
import java.net.URL;

/**
 * Basic information required for displaying tools in the tooltree
 * <p/>
 * User: Ian Taylor Date: Jul 30, 2010 Time: 11:43:13 AM To change this template use File | Settings | File Templates.
 */
public class ToolMetadata implements Serializable {

    private String toolName;
    private String displayName;
    // url for the unit.
    private URL url;

    // Triana unit class that will wrap this unit - if null then it is assumed
    // that this unit is a Triana unit and a wrapper is not needed
    private String unitWrapper;
    private boolean taskgraph = false;

    public ToolMetadata(String toolName, String displayName, URL url, String unitWrapper, boolean taskgraph) {
        this.toolName = toolName;
        this.displayName = displayName;
        this.url = url;
        this.unitWrapper = unitWrapper;
        this.taskgraph = taskgraph;
    }

    public String getToolName() {
        return toolName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public URL getUrl() {
        return url;
    }

    public String getUnitWrapper() {
        return unitWrapper;
    }

    public boolean isTaskgraph() {
        return taskgraph;
    }

    public String toString() {
        StringBuffer toolinfo = new StringBuffer();
        toolinfo.append("Tool Name: " + toolName + "\n");
        toolinfo.append("Display Name: " + displayName + "\n");
        toolinfo.append("Tool URL: " + url + "\n");
        toolinfo.append("Tool wrapper: " + unitWrapper + "\n");
        toolinfo.append("Is Taskgraph? " + taskgraph + "\n");

        return toolinfo.toString();
    }
}
