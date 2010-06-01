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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Logger;

/**
 * Class Description Here...
 *
 * @author Andrew Harrison
 * @version $Revision:$
 */

public class ToolClassLoader extends URLClassLoader {

    static Logger log = Logger.getLogger("org.trianacode.taskgraph.tool.ToolClassLoader");

    private static ToolClassLoader loader = new ToolClassLoader();


    public ToolClassLoader(ClassLoader classLoader) {
        this(classLoader, new String[0]);
    }

    public ToolClassLoader(ClassLoader classLoader, String... paths) {
        super(new URL[0], classLoader);
        log.fine("created with parent " + getParent().getClass().getName());
        for (String path : paths) {
            addPath(path);
        }
    }


    public ToolClassLoader() {
        this(ClassLoader.getSystemClassLoader());
    }

    public void addToolBox(String toolbox) {
        File box = new File(toolbox);
        if (!box.exists() || box.length() == 0) {
            return;
        }
        if (box.isDirectory()) {
            addPath(box.getAbsolutePath());
            File[] files = box.listFiles();
            if (files == null) {
                return;
            }
            for (File file : files) {
                if (file.isDirectory()) {
                    if (file.getName().equals("classes")) {
                        addPath(file.getAbsolutePath());
                    } else {
                        addToolBox(file.getAbsolutePath());
                    }
                } else {
                    if (file.getName().endsWith(".jar")) {
                        addPath(file.getAbsolutePath());
                    }
                }
            }
        } else {
            if (box.getName().endsWith(".jar")) {
                addPath(box.getAbsolutePath());
            }
        }
        System.out.println("ToolClassLoader.addToolBox CLASSPATH:" + getClassPath());
    }


    public void addPath(String path) {
        log.fine("adding path:" + path);
        File f = new File(path);
        if (f.exists()) {
            log.fine("parsing " + f.getAbsoluteFile());
            try {
                String s = f.toURI().toURL().toString();
                if (f.isDirectory() && !s.endsWith("/")) {
                    s += "/";
                }
                URL u = new URL(s);
                URL[] all = getURLs();
                boolean add = true;
                for (URL url : all) {
                    if (url.equals(u)) {
                        add = false;
                        break;
                    }
                }
                if (add) {
                    addURL(u);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }


    public String getClassPath() {
        StringBuilder classPath = new StringBuilder();
        URL[] paths = getURLs();
        for (int i = 0; i < paths.length; i++) {
            URL path = paths[i];
            String s = path.getFile();
            File f = new File(s);
            classPath.append(f.getAbsolutePath());
            if (i < paths.length - 1) {
                classPath.append(System.getProperty("path.separator"));
            }
        }
        return classPath.toString();
    }

    public static ToolClassLoader getLoader() {
        return loader;
    }


}
