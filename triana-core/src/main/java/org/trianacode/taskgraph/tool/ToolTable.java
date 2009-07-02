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


/**
 * An interface to the currently loaded tools.
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 * @created 12th Feb 2003
 * @date $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public interface ToolTable {

    // The standard tool box types
    public static final String DEFAULT_TOOLBOX = "Default";
    public static final String USER_TOOLBOX = "User";
    public static final String DATA_TOOLBOX = "Data";
    public static final String REMOTE_TOOLBOX = "Remote";
    public static final String NO_TYPE_SET = "No Type";

    /**
     * Called to initialise the tool table
     */
    public void init();


    /**
     * @return a list of the current tool box paths
     */
    public String[] getToolBoxes();

    /**
     * @return the tool box path of the specified type (null if no tool box specified for that
     *         type)
     */
    public String getToolBox(String type);

    /**
     * @return the type for the specified tool box path (null if no type specified for the tool box
     *         path)
     */
    public String getToolBoxType(String toolbox);


    /**
     * Add a tool box path to the current tool boxes
     */
    public void addToolBox(String path);

    /**
     * Remove a tool box path from the current tool boxes
     */
    public void removeToolBox(String path);


    /**
     * @return the tool with the specified name (if there are multiple tools with the same name then
     *         the first found is returned)
     */
    public Tool getTool(String toolName);

    /**
     * @return an array of the tools with the specified name
     */
    public Tool[] getTools(String toolName);

    /**
     * @return true if a tool with the given name exists
     */
    public boolean isTool(String toolName);

    /**
     * @return an array of the names of the currently loaded tools
     */
    public String[] getToolNames();


    /**
     * Inserts a copy of the tool into the specified package. Combined with deleteTool
     * this can be used to cut + paste tools.
     *
     * @param tool    the tool being pasted
     * @param pack    the package of the pasted tool
     * @param toolbox the toolbox the tool is pasted into (ignore if irrelevant)
     */
    public void insertTool(Tool tool, String pack, String toolbox);

    /**
     * Deletes the specified tool.
     */
    public void deleteTool(Tool tool, boolean files);


    /**
     * Notifies the tool table to update the tool loaded from the specified location, such as when a
     * tool is created. The location should be in a form understanded by the tool table (e.g. XML
     * file location, tool server network address), and is ignored if not understood.
     *
     * @param location the location of the file
     * @param toolbox  the toolbox the location is in (specify null if unknown)
     */
    public void refreshLocation(String location, String toolbox);


    /**
     * Adds a listener to be notified when new tools are added/removed
     */
    public void addToolTableListener(ToolTableListener listener);

    /**
     * Removes a listener from being notified when new tools are added/removed
     */
    public void removeToolTableListener(ToolTableListener listener);

    /**
     * Associates a toolbox type with a toolbox. Only one toolbox can be associated with a type but
     * many types van be associated witha single toolbox.
     * <p/>
     * If the tool box to be set is not in the list of current tool boxes then it is added first.
     *
     * @param toolbox the tool box we want associated with a type
     * @param type    the type we want to associate with the toolbox
     * @return the previous toolbox of the specified type or null if it did not have one.
     */
    public String setToolBoxType(String toolbox, String type);

    /**
     * Returns the list of current toolbox types
     *
     * @return an array of the current tool box type values
     */
    public String[] getToolBoxTypes();

    /**
     * Generate a new file location to store a pasted tool
     *
     * @param toolname tool to be created
     * @param pack     tool package name
     * @param toolbox  toolbox to create the tool in
     * @return a unused file to store the xml for the specified tool in
     */
    public String getPasteFileLocation(String toolname, String pack, String toolbox);

    /**
     * Utility method to load all the tools found in the named tool box, ignoring the default toolboxes. It empties all
     * caches so should not be used inconjunction with other load tool mechanisms.
     *
     * @param toolbox the tool box to load from.
     */
    void blockingLoadToolsFromToolBox(String toolbox);

    /**
     * Utility method to load all the tools in the known tool boxes, blocking until finished. This method avoids using
     * any the threaded reload tools methods so should not be used to load tools from toolboxes that may change
     * over time.
     */
    void blockingLoadTools();
}
