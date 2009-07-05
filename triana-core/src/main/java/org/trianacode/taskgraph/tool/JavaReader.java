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

import org.trianacode.taskgraph.TaskException;
import org.trianacode.taskgraph.Unit;
import org.trianacode.taskgraph.imp.ToolImp;
import org.trianacode.taskgraph.proxy.java.JavaProxy;
import org.trianacode.taskgraph.tool.creators.type.TypeFinder;
import org.trianacode.taskgraph.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Creates a Tool based on a Java Unit.
 *
 * @author Andrew Harrison
 * @version $Revision:$
 * @created Jun 26, 2009: 11:59:02 AM
 * @date $Date:$ modified by $Author:$
 */

public class JavaReader {

    static Logger log = Logger.getLogger("org.trianacode.taskgraph.tool.JavaReader");


    static {
        ClassLoaders.addClassLoader(ToolClassLoader.getLoader());
    }

    public List<Tool> createTools(String path) throws IOException {

        List<Tool> tools = new ArrayList<Tool>();
        ToolClassLoader.getLoader().addToolBox(path);
        File f = new File(path);
        if (f.exists()) {
            TypeFinder finder = new TypeFinder(Unit.class.getName(), f);
            List<String[]> units = finder.find();
            for (String[] unit : units) {
                Tool tool = read(unit[0], path, unit[1]);
                if (tool != null) {
                    tools.add(tool);
                }
            }
        }
        return tools;
    }

    public Tool read(String className, String toolbox, String classFile) {
        try {
            ToolImp tool = new ToolImp();
            tool.setDefinitionType(Tool.DEFINITION_JAVA_CLASS);
            tool.setToolName(getClassName(className));
            tool.setToolPackage(getPackageName(className));
            tool.setDefinitionPath(classFile);
            tool.setToolBox(toolbox);
            tool.setProxy(new JavaProxy(tool.getToolName(), tool.getToolPackage()));
            return tool;
        } catch (TaskException e) {
            log.warning("Error creating tool:" + FileUtils.formatThrowable(e));
            return null;
        }
    }


    private String getPackageName(String fullname) {
        if (fullname.endsWith(".class")) {
            fullname = fullname.substring(0, fullname.length() - 6);
        }
        int index = fullname.indexOf(".");
        if (index > 0) {
            return fullname.substring(0, fullname.lastIndexOf("."));
        }
        return "unknown";
    }

    private String getClassName(String fullname) {
        if (fullname.endsWith(".class")) {
            fullname = fullname.substring(0, fullname.length() - 6);
        }
        int index = fullname.indexOf(".");
        if (index > 0) {
            return fullname.substring(fullname.lastIndexOf(".") + 1, fullname.length());
        }
        return fullname;
    }

}
