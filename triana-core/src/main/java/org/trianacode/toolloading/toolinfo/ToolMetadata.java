package org.trianacode.toolloading.toolinfo;

/**
 * Basic information required for displaying tools in the tooltree
 * 
 * User: scmijt
 * Date: Jul 30, 2010
 * Time: 11:43:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class ToolMetadata {
    private String toolName;

    private String displayName;

    // url for the unit.
    
    private String url;

    // Triana unit class that will wrap this unit - if null then it is assumed
    // that this unit is a Triana unit and a wrapper is not needed

    private Class unitWrapper;

    public ToolMetadata(String toolName, String displayName, String url, Class unitWrapper) {
        this.toolName = toolName;
        this.displayName = displayName;
        this.url = url;
        this.unitWrapper = unitWrapper;
    }

    public String getToolName() {
        return toolName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getUrl() {
        return url;
    }

    public Class getUnitWrapper() {
        return unitWrapper;
    }
}
