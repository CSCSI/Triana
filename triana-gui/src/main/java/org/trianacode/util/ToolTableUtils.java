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

package org.trianacode.util;

import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.imp.ToolImp;
import org.trianacode.taskgraph.proxy.java.JavaProxy;
import org.trianacode.taskgraph.tool.ClassLoaders;
import org.trianacode.taskgraph.tool.Tool;
import org.trianacode.taskgraph.tool.ToolTable;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

/**
 * Class Description Here...
 *
 * @author Andrew Harrison
 * @version $Revision:$
 * @created Jun 25, 2009: 4:44:41 PM
 * @date $Date:$ modified by $Author:$
 */

public class ToolTableUtils {

    static Logger log = Logger.getLogger("org.trianacode.util.ToolTableUtils");


    /**
     * Internal method that removes the directory detail above the toolbox level and
     * returns the path as a Java style package path.
     */
    public static String qualifyPath(String qualifiedName, ToolTable tools) {
        String fullPath = qualifiedName;
        String[] toolboxes = tools.getToolBoxes();
        for (int i = 0; i < toolboxes.length; i++) {
            String toolboxpath = toolboxes[i] + Env.separator();
            qualifiedName = fullPath.replace(toolboxpath, "");
            if (!fullPath.equals(qualifiedName)) {
                break;
            }
        }
        qualifiedName = qualifiedName.replace(Env.separator(), ".");
        return qualifiedName;
    }


    /**
     * Method to try and return the helpfile for the given tool. Checks first to see if
     * the method is a valid URL, then a valid file and finally it checks to see if there
     * is an appropriate file in the "help" directory for the tool.
     *
     * @TODO using this line currently
     * return "file://" + file.toString();
     * really should be using file.toUrl().toString() but it doesn't seem to work
     */
    public static String getUnitHelpFilePath(Tool tool) {
        String helpLocation1 = tool.getToolBox() + File.separatorChar + tool.getToolPackage().replace('.',
                File.separatorChar) + File.separatorChar + "help" + Env.separator();
        String helpLocation2 = null;

        if (tool.getProxy() instanceof JavaProxy) {
            helpLocation2 = tool.getToolBox() + File.separatorChar + ((JavaProxy) tool.getProxy()).getUnitPackage().replace(
                    '.', File.separatorChar) + File.separatorChar + "help" + Env.separator();
        }

        if (tool.isParameterName(ToolImp.HELP_FILE_PARAM)) {
            String helpfile = tool.getHelpFile();

            try {
                URL url = new URL(helpfile);
                return url.toString();
            }
            catch (MalformedURLException e) {
                if (new File(helpLocation1 + helpfile).exists()) {
                    return helpLocation1 + helpfile;
                } else if ((helpLocation2 != null) && (new File(helpLocation2 + helpfile).exists())) {
                    return helpLocation2 + helpfile;
                }
            }
        }

        if (new File(helpLocation1 + tool.getToolName() + ".html").exists()) {
            return helpLocation1 + tool.getToolName() + ".html";
        } else if ((helpLocation2 != null) && (new File(helpLocation2 + tool.getToolName() + ".html").exists())) {
            return helpLocation2 + tool.getToolName() + ".html";
        } else {
            return helpLocation1 + tool.getToolName() + ".html";
        }
    }

    /**
     * Returns the string representation for the path of this tools source code file.
     *
     * @param tool The tool we want the source code for.
     * @return the absolute path to the source file
     */
    public static String getUnitSourceFilePath(Tool tool) {
        if (!(tool.getProxy() instanceof JavaProxy)) {
            return null;
        }

        JavaProxy proxy = (JavaProxy) tool.getProxy();

        StringBuffer buff = new StringBuffer(tool.getToolBox());
        if (buff.charAt(buff.length() - 1) != File.separatorChar) {
            buff.append(File.separator);
        }
        buff.append(proxy.getUnitPackage().replace(".", File.separator));
        buff.append(File.separator);
        buff.append("src");
        buff.append(File.separator);

        buff.append(proxy.getUnitName().substring(proxy.getUnitName().lastIndexOf('.') + 1));
        buff.append(".java");
        return buff.toString();
    }

    /**
     * @return true if the tool is broken (invalid parameter panel, invalid
     *         xml file version)
     */
    public static boolean isBroken(Tool tool) {

        if (!(tool instanceof TaskGraph)) {
            if (tool.isParameterName(Tool.PARAM_PANEL_CLASS) && tool.getParameter(
                    Tool.PARAM_PANEL_CLASS).equals("Invalid") && (!tool.isParameterName(
                    Tool.GUI_BUILDER)) && (!tool.isParameterName(Tool.OLD_GUI_BUILDER))) {
                return true;
            } else if (tool.getProxy() instanceof JavaProxy) {
                String name = ((JavaProxy) tool.getProxy()).getFullUnitName();
                try {
                    ClassLoaders.forName(name);
                } catch (ClassNotFoundException e) {
                    log.warning("Broken tool: " + name);
                    return true;
                }

                if (tool.isParameterName(Tool.PARAM_PANEL_CLASS) && (!tool.getParameter(
                        Tool.PARAM_PANEL_CLASS).equals("Invalid"))) {
                    try {
                        ClassLoaders.forName((String) tool.getParameter(Tool.PARAM_PANEL_CLASS));
                    } catch (ClassNotFoundException e) {
                        log.warning("Broken tool: " + name);
                        return true;
                    }
                }
            }
        }

        return false;

    }
}
