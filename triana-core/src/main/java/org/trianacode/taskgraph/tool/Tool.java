/*
 * The University of Wales, Cardiff Triana Project Software License (Based
 * on the Apache Software License Version 1.1)
 *
 * Copyright (c) 2007 University of Wales, Cardiff. All rights reserved.
 *
 * Redistribution and use of the software in source and binary forms, with
 * or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1.  Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 * 2.  Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 * 3. The end-user documentation included with the redistribution, if any,
 *    must include the following acknowledgment: "This product includes
 *    software developed by the University of Wales, Cardiff for the Triana
 *    Project (http://www.trianacode.org)." Alternately, this
 *    acknowledgment may appear in the software itself, if and wherever
 *    such third-party acknowledgments normally appear.
 *
 * 4. The names "Triana" and "University of Wales, Cardiff" must not be
 *    used to endorse or promote products derived from this software
 *    without prior written permission. For written permission, please
 *    contact triana@trianacode.org.
 *
 * 5. Products derived from this software may not be called "Triana," nor
 *    may Triana appear in their name, without prior written permission of
 *    the University of Wales, Cardiff.
 *
 * 6. This software may not be sold, used or incorporated into any product
 *    for sale to third parties.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN
 * NO EVENT SHALL UNIVERSITY OF WALES, CARDIFF OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 *
 * ------------------------------------------------------------------------
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Triana Project. For more information on the
 * Triana Project, please see. http://www.trianacode.org.
 *
 * This license is based on the BSD license as adopted by the Apache
 * Foundation and is governed by the laws of England and Wales.
 *
 */
package org.trianacode.taskgraph.tool;

import org.trianacode.config.TrianaProperties;
import org.trianacode.taskgraph.NodeException;
import org.trianacode.taskgraph.RenderingHint;
import org.trianacode.taskgraph.TaskException;
import org.trianacode.taskgraph.proxy.Proxy;

import java.net.URL;


/**
 * @author Matthew Shields
 * @version $Revision: 4048 $
 */
public interface Tool {

    /**
     * node defaults
     */
    public static int DEFAULT_MIN_NODES = 0;
    public static int DEFAULT_IN_NODES = 1;
    public static int DEFAULT_OUT_NODES = 1;
    public static int DEFAULT_MAX_NODES = Integer.MAX_VALUE;

    /**
     * pop_up description default
     */
    public static final String DEFAULT_POP_UP_DESCRIPTION = "No description for tool";

    /**
     * tool parameter names
     */
    public static final String TOOL_VERSION = "toolVersion";
    public static final String HELP_FILE_PARAM = "helpFile";
    public static final String POP_UP_DESCRIPTION = "popUpDescription";

    public static final String DEFAULT_INPUT_NODES = "defaultIn";
    public static final String MIN_INPUT_NODES = "minIn";
    public static final String MAX_INPUT_NODES = "maxIn";

    public static final String DEFAULT_OUTPUT_NODES = "defaultOut";
    public static final String MIN_OUTPUT_NODES = "minOut";
    public static final String MAX_OUTPUT_NODES = "maxOut";

    public static final String OUTPUT_POLICY = "outputPolicy";
    public static final String PARAM_UPDATE_POLICY = "paramUpdatePolicy";

    public static final String PARAM_PANEL_CLASS = "paramPanelClass";
    public static final String PARAM_PANEL_INSTANTIATE = "paramPanelInstantiate";
    public static final String GUI_BUILDER = "guiBuilder";
    public static final String OLD_GUI_BUILDER = "guiBuilderV2";


    /**
     * parameter types
     */

    // Unknown type
    public static final String UNKNOWN_TYPE = "unknown";

    // Internal parameter not exposed to user
    public static final String INTERNAL = "internal";
    // Internal parameter not exposed to user or serialized in tool XML
    public static final String TRANSIENT = "transient";
    // Internal parameter only initialized on first getParameter call
    public static final String LATE_INITIALIZE = "lateInitialize";

    // User accessible parameter
    public static final String USER_ACCESSIBLE = "userAccessible";
    // User accessible parameter not serialized in tool XML
    public static final String TRANSIENT_ACCESSIBLE = "transientAccessible";

    // GUI related parameter
    public static final String GUI = "org.trianacode.gui";

    /**
     * parameter update policies
     */
    public static final String IMMEDIATE_UPDATE = "immediateUpdate";
    public static final String PROCESS_UPDATE = "processUpdate";
    public static final String NO_UPDATE = "noUpdate";

    /**
     * parameter panel instantiation policies
     */
    public static final String ON_USER_ACCESS = "onUserAccess";
    public static final String ON_TASK_INSTANTIATION = "onTaskInstantiation";

    /**
     * parameter panel display parameters
     */

    public static final String PARAM_PANEL_SHOW = "showParameterPanel";
    public static final String PARAM_PANEL_HIDE = "hideParameterPanel";

    /**
     * output policies
     */
    public static final String COPY_OUTPUT = "copyOutput";
    public static final String CLONE_MULTIPLE_OUTPUT = "cloneMultipleOutput";
    public static final String CLONE_ALL_OUTPUT = "cloneAllOutput";

    /**
     * special input/output data types
     */
    public static final String UNKNOWN_DATA_TYPE = "Unknown Type";
    public static final String ANY_DATA_TYPE = "Any Type";

    /**
     * Tool types in terms of their definition files.
     */
    public static final String DEFINITION_TRIANA_XML = "definition.triana.xml";
    public static final String DEFINITION_JAVA_CLASS = "definition.java.class";
    public static final String DEFINITION_METADATA = "definition.metadata";
    public static final String DEFINITION_UNKNOWN = "definition.unknown";

    public String getVersion();

    /**
     * @return the  name of this tool.
     */
    public String getToolName();

    public String getDisplayName();

    public String getDisplayPackage();

    public String getDefinitionType();

    public String getSubTitle();

    public void setSubTitle(String title);

    /**
     * @return a Java style package name for this tool in the form [package].[package]. i.e. Common.Input
     */
    public String getToolPackage();


    /**
     * @return the full-qualified (Java style) name of the tool, i.e. toolpackage.toolname
     */
    public String getQualifiedToolName();

    /**
     * @return the location directory that held the definition file this tool was generated from. This could be XML or a
     *         Java .class file. Other things might be supported in the future.
     */
    public URL getDefinitionPath();

    /**
     * @return the path to the toolbox that this tool has been loaded from.
     */
    public Toolbox getToolBox();

    /**
     * Returns the current Triana properties
     *
     * @return
     */
    public TrianaProperties getProperties();


    /**
     * @return the proxies represented by this tool
     */
    public Proxy getProxy();


    /**
     * @return the rendering hints for this tool
     */
    public RenderingHint[] getRenderingHints();

    /**
     * @return the rendering hints for the specified hint
     */
    public RenderingHint getRenderingHint(String hint);

    /**
     * @return true if rendering hints exist for the specified hint
     */
    public boolean isRenderingHint(String hint);


    /**
     * @return the value associated with the specified name
     */
    public Object getParameter(String name);

    /**
     * @return the type of the specified parameter
     */
    public String getParameterType(String name);

    /**
     * @return the names of the parameters for this tool
     */
    public String[] getParameterNames();

    /**
     * @return true if there is a value associated with the specified parameter name
     */
    public boolean isParameterName(String name);


    /**
     * @return a brief description of what this tool does
     */
    public String getPopUpDescription();

    /**
     * @return the location of the help file.
     */
    public String getHelpFile();


    /**
     * @return an array of general input types for nodes not covered by getDataInputTypes(int node)
     */
    public String[] getDataInputTypes();

    /**
     * @return the data types accepted on the specified node index (if null is returned then the general input types
     *         should be assumed)
     */
    public String[] getDataInputTypes(int node);

    /**
     * @return an array of general output types for nodes for nodes not covered by getDataOutputTypes(int node)
     */
    public String[] getDataOutputTypes();

    /**
     * @return an data types output by the specified node index (if null is returned then the general output types
     *         should be assumed)
     */
    public String[] getDataOutputTypes(int node);


    /**
     * @return the number of input nodes (default value in ToolImp)
     */
    public int getDataInputNodeCount();

    /**
     * @return the number of output nodes (default value in ToolImp)
     */
    public int getDataOutputNodeCount();


    /**
     * @return the number of parameter input nodes
     */
    public int getParameterInputNodeCount();

    /**
     * @return the parameter name input on the specified node index
     */
    public String getParameterInputName(int index);

    /**
     * @return true if specified parameter input node is a trigger node
     */
    public boolean isParameterTriggerNode(int index);

    /**
     * @return the number of parameter output nodes
     */
    public int getParameterOutputNodeCount();

    /**
     * @return the parameter name input on the specified node index
     */
    public String getParameterOutputName(int index);


    /**
     * @return the number of input nodes (data + parameter)
     */
    public int getInputNodeCount();

    /**
     * @return the number of output nodes (data + parameter)
     */
    public int getOutputNodeCount();


    /**
     * @return the minimum number of input nodes for the tool.
     */
    public int getMinDataInputNodes();

    /**
     * @return the maximum number of input nodes.
     */
    public int getMaxDataInputNodes();

    /**
     * @return the default number of input nodes
     */
    public int getDefaultDataInputNodes();


    /**
     * @return the minimum number of output nodes
     */
    public int getMinDataOutputNodes();

    /**
     * @return the maximum number of output nodes
     */
    public int getMaxDataOutputNodes();

    /**
     * @return the default number of output nodes
     */
    public int getDefaultDataOutputNodes();


    /**
     * @return the value associated with the specified extension
     */
    public String getExtension(String name);

    /**
     * @return the names of the extensions for this tool
     */
    public String[] getExtensionNames();

    /**
     * @return true if there is a value associated with the specified extension name
     */
    public boolean isExtensionName(String name);

    /**
     * ================from Tool======================
     */

    public void setVersion(String version);

    /**
     * Used to set the tool name of this ToolImp.
     */
    public void setToolName(String toolName);

    /**
     * Set the package name for this tool.
     */
    public void setToolPackage(String pakageName);


    /**
     * Used by ToolTable to set the location of the file this tool was loaded from
     */
    public void setDefinitionPath(URL url);

    /**
     * Used by ToolTable to set the location of the toolbox this tool was loaded from
     */
    public void setToolBox(Toolbox toolbox);

    /**
     * used when creating clones that are not based on the same definition file type
     *
     * @param type
     */
    public void setDefinitionType(String type);


    /**
     * Sets the proxy for this tool
     */
    public void setProxy(Proxy proxy) throws TaskException;

    /**
     * Removes the proxy for this tool
     */
    public void removeProxy() throws TaskException;


    /**
     * Adds a rendering hint
     */
    public void addRenderingHint(RenderingHint hints);

    /**
     * Removes a rendering hint
     */
    public void removeRenderingHint(String hint);


    /**
     * Used to set the parameters.
     */
    public void setParameter(String name, Object value);

    /**
     * Used to set the parameter types.
     */
    public void setParameterType(String name, String type);

    /**
     * Removes the value associated with the specified name.
     */
    public void removeParameter(String name);

    /**
     * Used to set the pop up description.
     */
    public void setPopUpDescription(String name);

    /**
     * Used to set the help file url.
     */
    public void setHelpFile(String url);


    /**
     * Used to set the input types
     */
    public void setDataInputTypes(String[] types);

    /**
     * Used to set the output types.
     */
    public void setDataOutputTypes(String[] types);


    /**
     * Used to set the names/number of input parameter nodes, and whether they are trigger nodes.
     */
    public void setParameterInputs(String[] names, boolean[] trigger) throws NodeException;

    /**
     * Used to set the names/number of output parameter nodes
     */
    public void setParameterOutputs(String[] names) throws NodeException;


    /**
     * Adds an extension
     */
    public void addExtension(String name, String extension);

    /**
     * Removes an extension
     */
    public void removeExtension(String name);

}
