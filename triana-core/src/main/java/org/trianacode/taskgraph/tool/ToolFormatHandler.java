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


    public List<Tool> getTools();

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

}
