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
package org.trianacode.taskgraph.imp;

import org.apache.commons.logging.Log;
import org.trianacode.config.TrianaProperties;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.taskgraph.NodeException;
import org.trianacode.taskgraph.RenderingHint;
import org.trianacode.taskgraph.TaskException;
import org.trianacode.taskgraph.proxy.Proxy;
import org.trianacode.taskgraph.proxy.ProxyFactory;
import org.trianacode.taskgraph.proxy.ProxyInstantiationException;
import org.trianacode.taskgraph.tool.Tool;
import org.trianacode.taskgraph.tool.Toolbox;

import java.io.IOException;
import java.net.URL;
import java.util.*;


/**
 * A generic definition of a tool in a xmlFilepath which is extended to make a task.
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 */
public class ToolImp implements Tool {

    static Log log = Loggers.TOOL_LOGGER;


    private String version = "0.1";

    private String definitionType = Tool.DEFINITION_UNKNOWN;

    /**
     * ToolImp name
     */
    private String toolname = "";

    private String displayName = "";

    private String displayPackage = "";

    private String subTitle = null;

    /**
     * Number of input nodes
     */
    private int inportNum = 0;

    /**
     * Number of output nodes
     */
    private int outportNum = 0;

    /**
     * A hashtable of the tool class details for this tool
     */
    private Proxy proxy = null;

    /**
     * A hashtable of the rendering hints for this tool
     */
    private Map hinttable = new HashMap();

    /**
     * A hashtable of the extensions for this tool
     */
    private Map extensiontable = new HashMap();


    /**
     * Collection of the node input types
     */
    private Vector nodeInputTypes = new Vector();

    /**
     * Collection of the general input types
     */
    private Vector inputTypes = new Vector();

    /**
     * Collection of the node output types
     */
    private Vector nodeOutputTypes = new Vector();

    /**
     * Collection of the general output types
     */
    private Vector outputTypes = new Vector();

    /**
     * Hashtable of name/value pairs for the tools parameters
     */
    private Hashtable parameters = new Hashtable();

    /**
     * Hashtable of name/type pairs for the parameter typer
     */
    private Hashtable paramtypes = new Hashtable();

    /**
     * a list of input parameter names
     */
    private ArrayList inparamnames = new ArrayList();

    /**
     * a list of output parameter names
     */
    private ArrayList outparamnames = new ArrayList();

    /**
     * Java style package name.
     */
    private String packageName = "";

    /**
     * The name of the unit that contains the executable code for this tool, if it is the empty string it is assumed to
     * be the tool name for backward compatibilty.
     */
    private String unitName = "";

    /**
     * This is the name for the package that contains the executable for this tool. if it is the empty string it is
     * assumed to be the tool package for backward compatibilty.
     */
    private String unitPackage = "";

    /**
     * The location of the file used to generate this tool
     */
    private URL url = null;

    /**
     * The location of the toolbox this tool was loaded from
     */
    private Toolbox toolbox = null;

    /**
     * Current Triana properties configuration
     */
    TrianaProperties properties;

    /**
     * Default constructor
     */
    public ToolImp() {

    }

    /**
     * Creates a clone of the specified tool
     */
    public ToolImp(Tool tool, TrianaProperties properties) throws TaskException {
        this.properties = properties;

        try {
            setDefinitionType(tool.getDefinitionType());
            setToolName(tool.getToolName());
            setToolPackage(tool.getToolPackage());
            setSubTitle(tool.getSubTitle());
            setDisplayName(tool.getDisplayName());
            setDisplayPackage(tool.getDisplayPackage());
            setProxy(ProxyFactory.cloneProxy(tool.getProxy()));

            setDefinitionPath(tool.getDefinitionPath());
            setToolBox(tool.getToolBox());
            setPopUpDescription(tool.getPopUpDescription());
            setHelpFile(tool.getHelpFile());
            setDataInputNodeCount(tool.getDataInputNodeCount());
            setDataOutputNodeCount(tool.getDataOutputNodeCount());

            int count = 0;
            while (tool.getDataInputTypes(count) != null) {
                setDataInputTypes(count, tool.getDataInputTypes(count));
                count++;
            }

            count = 0;
            while (tool.getDataOutputTypes(count) != null) {
                setDataOutputTypes(count, tool.getDataOutputTypes(count));
                count++;
            }

            setDataInputTypes(tool.getDataInputTypes());
            setDataOutputTypes(tool.getDataOutputTypes());

            RenderingHint[] hints = tool.getRenderingHints();
            for (count = 0; count < hints.length; count++) {
                addRenderingHint(hints[count]);
            }

            String[] names = tool.getExtensionNames();
            for (count = 0; count < names.length; count++) {
                addExtension(names[count], tool.getExtension(names[count]));
            }

            int paramin = tool.getParameterInputNodeCount();
            String[] paramnames = new String[paramin];
            boolean[] trigger = new boolean[paramin];

            for (count = 0; count < paramin; count++) {
                paramnames[count] = tool.getParameterInputName(count);
                trigger[count] = tool.isParameterTriggerNode(count);
            }

            setParameterInputs(paramnames, trigger);

            int paramout = tool.getParameterOutputNodeCount();
            paramnames = new String[paramout];

            for (count = 0; count < paramout; count++) {
                paramnames[count] = tool.getParameterOutputName(count);
            }

            setParameterOutputs(paramnames);

            String[] params = tool.getParameterNames();
            for (count = 0; count < params.length; count++) {
                if (tool.getParameter(params[count]) != null) {
                    setParameter(params[count], tool.getParameter(params[count]));
                }

                if (tool.getParameterType(params[count]) != null) {
                    setParameterType(params[count], tool.getParameterType(params[count]));
                }
            }
        } catch (NodeException except) {
            throw (new TaskException(except));
        } catch (ProxyInstantiationException except) {
            throw (new TaskException(except));
        }
    }


    public String getVersion() {
        return version;
    }

    /**
     * @return the name of the unit defined by this Tool
     */
    public String getToolName() {
        return toolname;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDisplayPackage() {
        return displayPackage;
    }

    public TrianaProperties getProperties() {
        return properties;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setDisplayPackage(String displayPackage) {
        this.displayPackage = displayPackage;
    }

    public String getDefinitionType() {
        return definitionType;
    }


    public void setDefinitionType(String type) {
        this.definitionType = type;
    }

    /**
     * Used to set the tool name of this Tool.
     */
    public void setToolName(String toolName) {
        toolname = toolName;
    }

    /**
     * @return a Java style package name for this tool in the form [package].[package]. i.e. Common.Input
     */
    public String getToolPackage() {
        return packageName;
    }

    /**
     * Set the package name for this tool.
     */
    public void setToolPackage(String packageName) {
        this.packageName = packageName;
    }


    /**
     * Used by ToolTable to set the location of the toolbox this tool was loaded from
     */
    public void setToolBox(Toolbox toolboxpath) {
        this.toolbox = toolboxpath;
    }

    /**
     * @return the path to the toolbox that this tool has been loaded from.
     */
    public Toolbox getToolBox() {
        return toolbox;
    }


    /**
     * @return the proxies represented by this tool
     */
    public Proxy getProxy() {
        return proxy;
    }

    /**
     * Adds a proxy this tool
     */
    public void setProxy(Proxy proxy) throws TaskException {
        if (proxy == null) {
            removeProxy();
        } else if (!proxy.equals(this.proxy)) {
            this.proxy = proxy;

            RenderingHint[] hints = getRenderingHints();
            for (int count = 0; count < hints.length; count++) {
                if (hints[count].isProxyDependent()) {
                    removeRenderingHint(hints[count].getRenderingHint());
                }
            }
        }
    }

    /**
     * Removes the proxy for this tool
     */
    public void removeProxy() throws TaskException {
        if (proxy != null) {
            proxy = null;

            RenderingHint[] hints = getRenderingHints();
            for (int count = 0; count < hints.length; count++) {
                if (hints[count].isProxyDependent()) {
                    removeRenderingHint(hints[count].getRenderingHint());
                }
            }
        }
    }


    /**
     * Adds a rendering hint
     */
    public void addRenderingHint(RenderingHint hint) {
        hinttable.put(hint.getRenderingHint(), hint);
    }

    /**
     * Removes a rendering hint
     */
    public void removeRenderingHint(String hint) {
        hinttable.remove(hint);
    }

    /**
     * @return the rendering hints for this tool
     */
    public RenderingHint[] getRenderingHints() {
        return (RenderingHint[]) hinttable.values().toArray(new RenderingHint[hinttable.values().size()]);
    }

    /**
     * @return the rendering hints for the specified hint
     */
    public RenderingHint getRenderingHint(String hint) {
        if (hinttable.containsKey(hint)) {
            return (RenderingHint) hinttable.get(hint);
        } else {
            return null;
        }
    }

    /**
     * @return true if rendering hints exist for the specified hint
     */
    public boolean isRenderingHint(String hint) {
        return hinttable.containsKey(hint);
    }


    /**
     * @return the value associated with the specified name, or null if the parameter does not exist.
     */
    public Object getParameter(String name) {
        return getParameter(name, true);
    }

    /**
     * @return the value associated with the specified name, or null if the parameter does not exist.
     */
    Object getParameter(String name, boolean deserialize) {
        try {
            Object result = parameters.get(name);

            if (deserialize && (result instanceof SerializedObject)) {
                result = ((SerializedObject) result).getDeserialized();
                parameters.put(name, result);
            }

            return result;
        } catch (IOException except) {
            throw (new RuntimeException("Error Deserializing Parameter: " + except.getMessage(), except));
        }
    }


    /**
     * @return the type of the specified parameter, the default being Tool.UNKNOWN. Returns null if the parameter does
     *         not exist.
     */
    public String getParameterType(String name) {
        Object result = paramtypes.get(name);
        if (result != null) {
            return (String) result;
        } else if (isParameterName(name)) {
            return Tool.UNKNOWN_TYPE;
        } else {
            return null;
        }
    }

    /**
     * Used to set the parameters.
     */
    public void setParameter(String name, Object value) {
        if (name.indexOf('.') >= 0) {
            System.err.println(
                    "WARNING: " + getQualifiedToolName() + " contains an invalid parameter name (" + name + ")");
        }

        if (value == null) {
            parameters.remove(name);
        } else {
            parameters.put(name, value);
        }
    }

    /**
     * Used to set the parameter types.
     */
    public void setParameterType(String name, String type) {
        log.debug("setting parameter type for name:" + name + " to " + type);
        paramtypes.put(name, type);
    }

    /**
     * Removes the value associated with the specified name.
     */
    public void removeParameter(String name) {
        parameters.remove(name);
    }

    /**
     * @return the names of the parameters for this tool
     */
    public String[] getParameterNames() {
        return (String[]) parameters.keySet().toArray(new String[parameters.keySet().size()]);
    }

    /**
     * @return true if a value is set for the specified name
     */
    public boolean isParameterName(String name) {
        return parameters.containsKey(name);
    }


    /**
     * @return a brief description of what this tool does
     */
    public String getPopUpDescription() {
        if (!isParameterName(POP_UP_DESCRIPTION)) {
            setParameter(POP_UP_DESCRIPTION, DEFAULT_POP_UP_DESCRIPTION);
            setParameterType(POP_UP_DESCRIPTION, TRANSIENT);
        }
        return (String) getParameter(POP_UP_DESCRIPTION);
    }

    /**
     * Used to set the pop up description.
     */
    public void setPopUpDescription(String desc) {
        if (desc != null) {
            setParameter(POP_UP_DESCRIPTION, desc);
        } else {
            removeParameter(POP_UP_DESCRIPTION);
        }
    }


    /**
     * @return the location of the help file.
     */
    public String getHelpFile() {
        return (String) getParameter(HELP_FILE_PARAM);
    }

    /**
     * Used to set the help file.
     */
    public void setHelpFile(String help) {
        if (help != null) {
            setParameter(HELP_FILE_PARAM, help);
        } else {
            removeParameter(HELP_FILE_PARAM);
        }
    }


    /**
     * @return the data types accepted on the specified node index. If null is returned then the types returned by
     *         getDataInputTypes() should be assumed)
     */
    public String[] getDataInputTypes(int node) {
        if (node < nodeInputTypes.size()) {
            return (String[]) nodeInputTypes.get(node);
        } else {
            return null;
        }
    }

    /**
     * @return an array of input types for node indexes not covered by getDataInputTypes(int node)
     */
    public String[] getDataInputTypes() {
        return (String[]) inputTypes.toArray(new String[inputTypes.size()]);
    }

    /**
     * Used by ToolFactory to set the input types for each node
     */
    public void setDataInputTypes(int node, String[] types) {
        if (types == null) {
            nodeInputTypes.setSize(node);
        } else {
            while (nodeInputTypes.size() <= node) {
                nodeInputTypes.add(new String[0]);
            }

            nodeInputTypes.set(node, types);
        }
    }

    /**
     * Used by ToolFactory to set the input types.
     */
    public void setDataInputTypes(String[] types) {
        inputTypes = new Vector(Arrays.asList(types));
    }


    /**
     * @return an data types output by the specified node index. If null is returned then the types returned by
     *         getDataInputTypes() should be assumed)
     */
    public String[] getDataOutputTypes(int node) {
        if (node < nodeOutputTypes.size()) {
            return (String[]) nodeOutputTypes.get(node);
        } else {
            return null;
        }
    }

    /**
     * @return an array of output types for node indexes not covered by getDataOutputTypes(int node)
     */
    public String[] getDataOutputTypes() {
        return (String[]) outputTypes.toArray(new String[outputTypes.size()]);
    }


    /**
     * Used by ToolFactory to set the output types for each node
     */
    public void setDataOutputTypes(int node, String[] types) {
        if (types == null) {
            nodeOutputTypes.setSize(node);
        } else {
            while (nodeOutputTypes.size() <= node) {
                nodeOutputTypes.add(new String[0]);
            }

            nodeOutputTypes.set(node, types);
        }
    }

    /**
     * Used to set the output types.
     */
    public void setDataOutputTypes(String[] types) {
        outputTypes = new Vector(Arrays.asList(types));
    }


    /**
     * @return the number of input nodes (default value in ToolImp)
     */
    public int getDataInputNodeCount() {
        return inportNum;
    }

    /**
     * Used to set the number of input nodes.
     */
    public void setDataInputNodeCount(int nodeCount) throws NodeException {
        inportNum = nodeCount;
    }


    /**
     * @return the number of output nodes (default value in ToolImp)
     */
    public int getDataOutputNodeCount() {
        return outportNum;
    }

    /**
     * Used to set the number of output nodes.
     */
    public void setDataOutputNodeCount(int nodeCount) throws NodeException {
        outportNum = nodeCount;
    }


    /**
     * @return the number of parameter input nodes
     */
    public int getParameterInputNodeCount() {
        return inparamnames.size();
    }

    /**
     * @return the parameter name input on the specified node index
     */
    public String getParameterInputName(int index) {
        return ((InputNode) inparamnames.get(index)).getParameterName();
    }

    /**
     * @return true if the specified parameter input node is a trigger node
     */
    public boolean isParameterTriggerNode(int index) {
        return ((InputNode) inparamnames.get(index)).isTrigger();
    }


    /**
     * Used to set the names/number of input parameter nodes
     */
    public void setParameterInputs(String[] names, boolean[] trigger) throws NodeException {
        inparamnames.clear();

        for (int count = 0; count < names.length; count++) {
            inparamnames.add(new InputNode(names[count], trigger[count]));
        }
    }


    /**
     * @return the number of parameter output nodes
     */
    public int getParameterOutputNodeCount() {
        return outparamnames.size();
    }

    /**
     * @return the parameter name input on the specified node index
     */
    public String getParameterOutputName(int index) {
        return (String) outparamnames.get(index);
    }

    /**
     * Used to set the names/number of output parameter nodes
     */
    public void setParameterOutputs(String[] names) throws NodeException {
        outparamnames.clear();

        for (int count = 0; count < names.length; count++) {
            outparamnames.add(names[count]);
        }
    }


    /**
     * @return the number of input nodes (data + parameter)
     */
    public int getInputNodeCount() {
        return getDataInputNodeCount() + getParameterInputNodeCount();
    }

    /**
     * @return the number of output nodes (data + parameter)
     */
    public int getOutputNodeCount() {
        return getDataOutputNodeCount() + getParameterOutputNodeCount();
    }

    /**
     * @return the minimum number of input nodes for the tool.
     */
    public int getMinDataInputNodes() {
        try {
            return Integer.parseInt((String) getParameter(MIN_INPUT_NODES));
        } catch (NumberFormatException nfe) {
            return DEFAULT_MIN_NODES;
        }
    }

    /**
     * Sets the minimum number of input nodes for the tool.
     */
    public void setMinDataInputNodes(int nodes) {
        setParameter(MIN_INPUT_NODES, String.valueOf(nodes));
    }

    /**
     * @return the maximum number of input nodes.
     */
    public int getMaxDataInputNodes() {
        try {
            return Integer.parseInt((String) getParameter(MAX_INPUT_NODES));
        } catch (NumberFormatException nfe) {
            return DEFAULT_MAX_NODES;
        }
    }

    /**
     * Sets the minimum number of input nodes for the tool.
     */
    public void setMaxDataInputNodes(int nodes) {
        setParameter(MAX_INPUT_NODES, String.valueOf(nodes));
    }

    /**
     * @return the default number of input nodes
     */
    public int getDefaultDataInputNodes() {
        try {
            return Integer.parseInt((String) getParameter(DEFAULT_INPUT_NODES));
        } catch (NumberFormatException nfe) {
            return Math.max(Math.min(DEFAULT_IN_NODES, getMaxDataInputNodes()), getMinDataInputNodes());
        }
    }

    /**
     * Sets the minimum number of input nodes for the tool.
     */
    public void setDefaultDataInputNodes(int nodes) {
        setParameter(DEFAULT_INPUT_NODES, String.valueOf(nodes));
    }


    /**
     * @return the minimum number of output nodes
     */
    public int getMinDataOutputNodes() {
        try {
            return Integer.parseInt((String) getParameter(MIN_OUTPUT_NODES));
        } catch (NumberFormatException nfe) {
            return DEFAULT_MIN_NODES;
        }
    }

    /**
     * Sets the minimum number of input nodes for the tool.
     */
    public void setMinDataOutputNodes(int nodes) {
        setParameter(MIN_OUTPUT_NODES, String.valueOf(nodes));
    }

    /**
     * @return the maximum number of output nodes
     */
    public int getMaxDataOutputNodes() {
        try {
            return Integer.parseInt((String) getParameter(MAX_OUTPUT_NODES));
        } catch (NumberFormatException nfe) {
            return DEFAULT_MAX_NODES;
        }
    }

    /**
     * Sets the minimum number of input nodes for the tool.
     */
    public void setMaxDataOutputNodes(int nodes) {
        setParameter(MAX_OUTPUT_NODES, String.valueOf(nodes));
    }

    /**
     * @return the default number of output nodes
     */
    public int getDefaultDataOutputNodes() {
        try {
            return Integer.parseInt((String) getParameter(DEFAULT_OUTPUT_NODES));
        } catch (NumberFormatException nfe) {
            return Math.max(Math.min(DEFAULT_OUT_NODES, getMaxDataOutputNodes()), getMinDataOutputNodes());
        }
    }

    /**
     * Sets the minimum number of input nodes for the tool.
     */
    public void setDefaultDataOutputNodes(int nodes) {
        setParameter(DEFAULT_OUTPUT_NODES, String.valueOf(nodes));
    }


    /**
     * @return the location directory that held the file this tool was generated from.
     */
    public URL getDefinitionPath() {
        return url;
    }

    /**
     * Used by ToolTable to set the location of the file this tool was loaded from
     */
    public void setDefinitionPath(URL filepath) {
        this.url = filepath;
    }


    /**
     * @return the full-qualified (Java style) name of the tool, i.e. toolpackage.toolname
     */
    public String getQualifiedToolName() {
        if ((getToolPackage() != null) && (!getToolPackage().equals(""))) {
            return getToolPackage() + '.' + getToolName();
        } else {
            return getToolName();
        }
    }


    /**
     * @return the classname of the unit that this tool executes.
     */
    public String getUnitName() {
        return unitName;
    }

    /**
     * @return the package of the unit that this tool executes.
     */
    public String getUnitPackage() {
        return unitPackage;
    }


    public String toString() {
        if (toolname != null) {
            return toolname;
        } else {
            return "[" + super.toString() + "]";
        }
    }


    /**
     * Adds an extension
     */
    public void addExtension(String name, String extension) {
        extensiontable.put(name, extension);
    }

    /**
     * Removes an extension
     */
    public void removeExtension(String name) {
        extensiontable.remove(name);
    }

    /**
     * @return the value associated with the specified extension
     */
    public String getExtension(String name) {
        return (String) extensiontable.get(name);
    }

    /**
     * @return the names of the extensions for this tool
     */
    public String[] getExtensionNames() {
        return (String[]) extensiontable.keySet().toArray(new String[extensiontable.size()]);
    }

    /**
     * @return true if there is a value associated with the specified extension name
     */
    public boolean isExtensionName(String name) {
        return extensiontable.containsKey(name);
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setSubTitle(String subtext) {
        this.subTitle = subtext;

    }

    public String getSubTitle() {
        return subTitle;
    }


    private class InputNode {

        private String name;
        private boolean trigger;

        public InputNode(String name, boolean trigger) {
            this.name = name;
            this.trigger = trigger;
        }

        public String getParameterName() {
            return name;
        }

        public boolean isTrigger() {
            return trigger;
        }

    }

}
