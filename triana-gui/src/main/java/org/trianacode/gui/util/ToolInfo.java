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

package org.trianacode.gui.util;

import org.trianacode.taskgraph.tool.Tool;

import java.io.File;

/**
 * Class Description Here...
 *
 * @author Andrew Harrison
 * @version $Revision:$
 */

public class ToolInfo {


    private Tool tool;
    private String xmlFile;
    private long modified;

    public ToolInfo(Tool tool, String xmlFile) {
        this.tool = tool;
        this.xmlFile = xmlFile;

        this.modified = new File(xmlFile).lastModified();
    }

    public ToolInfo(Tool tool, String xmlFile, long modified) {
        this.tool = tool;
        this.xmlFile = xmlFile;
        this.modified = modified;
    }

    public Tool getTool() {
        return tool;
    }

    public String getQualifiedName() {
        return tool.getQualifiedToolName();
    }

    public String getXMLFileName() {
        return xmlFile;
    }

    public long getLastModified() {
        return modified;
    }

}
