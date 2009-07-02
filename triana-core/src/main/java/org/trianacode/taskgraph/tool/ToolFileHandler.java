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
import java.io.FilenameFilter;

/**
 * handles where directories are relating to a tool.
 * This is work in progress, but the aim is to centralize
 * finding all these files.
 * <p/>
 * In particular, we should handle virtual toolboxes
 *
 * @author Andrew Harrison
 * @version $Revision:$
 * @created Jul 2, 2009: 3:36:10 PM
 * @date $Date:$ modified by $Author:$
 */

public class ToolFileHandler {

    public static File getXMLDirecotry(Tool tool) {
        return getDir(tool, tool.getToolBox(), "xml");
    }

    public static File getSourceDirectory(Tool tool) {
        return getDir(tool, tool.getToolBox(), "src");
    }

    public static File getClassesDirectory(Tool tool) {
        return getDir(tool, tool.getToolBox(), "classes");
    }

    public static File getLibDirectory(Tool tool) {
        return getDir(tool, tool.getToolBox(), "lib");
    }

    public static File getRoot(Tool tool, String toolbox) {
        if (toolbox == null) {
            return null;
        }
        File f = new File(toolbox);
        if (!f.exists() || f.length() == 0) {
            return null;
        }

        File def = new File(tool.getDefinitionPath());
        if (!def.exists() || def.length() == 0) {
            return null;
        }
        if (!isContained(def, f)) {
            return null;
        }
        return f;
    }

    private static boolean isContained(File tool, File toolbox) {
        File parent = tool.getParentFile();
        while (parent != null) {
            if (parent.getAbsolutePath().equals(toolbox.getAbsolutePath())) {
                return true;
            }
            parent = parent.getParentFile();
        }
        return false;
    }

    private static File getDir(Tool tool, String toolbox, String name) {

        File root = getRoot(tool, toolbox);
        if (root == null) {
            return null;
        }
        return createDir(root, name);
    }

    private static File createDir(File toolbox, String name) {
        File f = findDir(toolbox, name);
        if (f == null) {
            f = new File(toolbox, name);
            f.mkdirs();
        }
        return f;
    }


    private static File findDir(File parent, String name) {
        File[] files = parent.listFiles(new FilenameFilter() {

            public boolean accept(File file, String s) {
                if (!file.isDirectory() || s.startsWith(".") || s.startsWith("CVS")) {
                    return false;
                }
                return true;
            }
        });
        if (files == null) {
            return null;
        }
        for (File file : files) {
            if (file.getName().equals(name)) {
                return file;
            }
        }
        for (File file : files) {
            File f = findDir(file, name);
            if (f != null) {
                return f;
            }

        }
        return null;
    }


}
