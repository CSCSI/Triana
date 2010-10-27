package org.trianacode.taskgraph.tool;

import org.trianacode.config.TrianaProperties;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Oct 26, 2010
 */
public class FileToolboxLoader implements ToolboxLoader {

    public static final String LOCAL_TYPE = "local";

    @Override
    public String getType() {
        return LOCAL_TYPE;
    }

    @Override
    public Toolbox loadToolbox(String location, TrianaProperties properties) {
        return new FileToolbox(location, "Local Toolbox", properties);
    }
}
