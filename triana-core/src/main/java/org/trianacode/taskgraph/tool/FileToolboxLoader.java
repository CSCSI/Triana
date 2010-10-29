package org.trianacode.taskgraph.tool;

import org.trianacode.config.TrianaProperties;

import java.io.File;

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
    public Toolbox loadToolbox(String location, String name, TrianaProperties properties) {
        File f = new File(location);
        if (!f.exists()) {
            return null;
        }
        if (name == null) {
            return new FileToolbox(location, properties);
        } else {
            return new FileToolbox(location, name, properties);
        }
    }
}
