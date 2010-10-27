package org.trianacode.taskgraph.tool;

import org.trianacode.config.TrianaProperties;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Oct 26, 2010
 */
public interface ToolboxLoader {

    public String getType();

    public Toolbox loadToolbox(String location, TrianaProperties properties);
}
