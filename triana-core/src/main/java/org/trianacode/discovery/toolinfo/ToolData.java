package org.trianacode.discovery.toolinfo;

import java.net.URL;

/**
 * This is a cut down version of tool containing the information about a tool that provides a fuller description of a
 * tool for a tool browser.  This information is transferred to fill in whatever it can within the Tool class for
 * fitting in with the existing implementation of Triana.
 * <p/>
 * This is a fraction of the size of Tool and encompasses everything one might want to know about a tool before
 * instantiating it.
 * <p/>
 * User: Ian Taylor Date: Aug 1, 2010 Time: 10:19:47 AM To change this template use File | Settings | File Templates.
 */
public class ToolData extends ToolMetadata {

    URL helpfile;
    Object[] inputTypes;
    Object[] outputTypes;
    String popUpDescription;

    public ToolData(String toolName, String displayName, URL url, String unitWrapper, boolean taskgraph) {
        super(toolName, displayName, url, unitWrapper, taskgraph);
    }

    public URL getHelpfile() {
        return helpfile;
    }

    public void setHelpfile(URL helpfile) {
        this.helpfile = helpfile;
    }

    public Object[] getInputTypes() {
        return inputTypes;
    }

    public void setInputTypes(Object[] inputTypes) {
        this.inputTypes = inputTypes;
    }

    public Object[] getOutputTypes() {
        return outputTypes;
    }

    public void setOutputTypes(Object[] outputTypes) {
        this.outputTypes = outputTypes;
    }

    public String getPopUpDescription() {
        return popUpDescription;
    }

    public void setPopUpDescription(String popUpDescription) {
        this.popUpDescription = popUpDescription;
    }
}
