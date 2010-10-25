package org.trianacode.taskgraph.tool;

import org.trianacode.config.TrianaProperties;

import java.net.URL;
import java.util.List;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Oct 25, 2010
 */
public interface Toolbox {

    /**
     * a name for the toolbox
     *
     * @return
     */
    public String getName();

    /**
     * a path for the toolbox. This should be unique among all loaded toolboxes
     *
     * @return
     */
    public String getPath();

    /**
     * a type for the toolbox. There is no strict taxonomy for this at the moment, but local, remote, virtual
     * are typical values
     *
     * @return
     */
    public String getType();

    public TrianaProperties getProperties();

    public void setProperties(TrianaProperties properties);

    /**
     * load all tools
     *
     * @throws Exception
     */
    public void loadTools() throws Exception;

    /**
     * relaod tools at a particular URL (this is the tool.getDefinitionPath())
     *
     * @param url
     * @throws Exception
     */
    public void refresh(URL url) throws Exception;

    /**
     * get tools that share the same definition path
     *
     * @param url
     * @return
     * @throws Exception
     */
    public List<Tool> getTools(URL url);

    /**
     * get all tools
     *
     * @return
     */
    public List<Tool> getTools();

    /**
     * get a tool using tool.getQualifiedToolName()
     *
     * @param name
     * @return
     */
    public Tool getTool(String name);

    /**
     * could be called registerTool
     *
     * @param name
     * @return
     */
    public Tool removeTool(String name);

    /**
     * not always supported. Has the concept of 'deletion' that may not be appropriate
     * Keeping for now...
     *
     * @param name
     * @return
     */
    public Tool deleteTool(String name);


}
