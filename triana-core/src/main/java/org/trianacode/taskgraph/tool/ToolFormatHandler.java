/*
 * Copyright 2004 - 2009 University of Cardiff.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.trianacode.taskgraph.tool;

import java.io.File;
import java.net.URL;
import java.util.List;

/**
 * Class Description Here...
 *
 * @author Andrew Harrison
 * @version $Revision:$
 * @created Jul 3, 2009: 10:19:19 AM
 * @date $Date:$ modified by $Author:$
 */
public interface ToolFormatHandler {

    public static final String XML_PATH = "xml";
    public static final String SRC_PATH = "src";
    public static final String CLASSES_PATH = "classes";

    public Tool getTool(String fullname);

    public ToolStatus add(Tool tool) throws ToolException;

    public List<ToolStatus> add(File f, String toolbox) throws ToolException;

    public void delete(Tool tool);

    public void remove(Tool tool);

    public void removeDefinitionPath(String path);

    public void clear();

    public String[] getToolNames();

    public Tool[] getTools();

    public Tool[] getTools(String path);

    public File toFile(URL url);


    /**
     * gets the root directory of the tool.
     * A root can potentially contain multiple tools. i.e.,
     * a root may be shared.
     *
     * @return
     */
    public URL getRoot(Tool tool);

    /**
     * gets the root logical directory that contains the definition
     *
     * @return
     */
    public URL getDefinitionRoot(Tool tool);

    /**
     * get the definition URL which can be read from.
     * This could be different from the Tool's getDefinitionPath, for example
     * if it's in a jar file.
     *
     * @return
     */
    public URL getDefinition(Tool tool);

    /**
     * write the definition
     *
     * @param location
     * @return
     */
    public boolean writeTool(Tool tool, URL location);

    public boolean isModifiable(Tool tool);

    public boolean isModified(Tool tool);

    public boolean isMovable(Tool tool);

    public static class ToolStatus {

        public static enum Status {
            OK,
            REMOVED,
            NOT_ADDED,
            NOT_MODIFIED,
            NULL_DEFINITION_PATH,
            NULL_TOOL,
            UNKNOWN_FORMAT

        }

        private Tool tool;
        private Status status;

        public ToolStatus(Tool tool, Status status) {
            this.tool = tool;
            this.status = status;
        }

        public Tool getTool() {
            return tool;
        }

        public Status getStatus() {
            return status;
        }
    }

}
