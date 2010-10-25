package org.trianacode.taskgraph.tool;

import org.trianacode.config.TrianaProperties;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Oct 25, 2010
 */
public interface Toolbox {

    public String getName();

    public String getPath();

    public String getType();

    public TrianaProperties getProperties();

    public void setProperties(TrianaProperties properties);

    public void loadTools() throws Exception;

    public void refresh(URL url) throws Exception;

    public List<Tool> getTools();


}
