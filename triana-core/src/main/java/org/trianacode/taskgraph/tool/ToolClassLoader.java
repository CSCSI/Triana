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
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.trianacode.taskgraph.util.Home;
import org.trianacode.taskgraph.util.UrlUtils;

/**
 * Class Description Here...
 *
 * @author Andrew Harrison
 * @version $Revision:$
 */

public class ToolClassLoader extends URLClassLoader {

    static Logger log = Logger.getLogger("org.trianacode.taskgraph.tool.ToolClassLoader");

    private List<URL> nativeDirs = new ArrayList<URL>();

    private List<URL> helpDirs = new ArrayList<URL>();

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


    public void addToolBox(URL toolbox) {
        if (UrlUtils.isFile(toolbox)) {
            try {
                File box = new File(toolbox.toURI());
                if (!box.exists() || box.length() == 0 || box.getName().startsWith(".")) {
                    return;
                }
                if (box.isDirectory()) {
                    File[] files = box.listFiles();
                    if (files == null) {
                        return;
                    }
                    for (File file : files) {
                        String name = file.getName();
                        if (name.startsWith(".")) {
                            continue;
                        }
                        if (file.isDirectory()) {
                            if (name.equals("classes")) {
                                addPath(file.getAbsolutePath());
                            } else if (name.equals(Toolbox.HELP_DIR)) {
                                helpDirs.add(UrlUtils.fromFile(file));
                            } else if (name.equals("src")) {
                                continue;
                            } else if (name.equals("CVS")) {
                                continue;
                            } else {
                                if (isNative(file)) {
                                    nativeDirs.add(UrlUtils.fromFile(file));
                                }
                                addToolBox(file.toURI().toURL());
                            }
                        } else {
                            if (name.endsWith(".jar")) {
                                addPath(file.getAbsolutePath());
                            }
                        }
                    }
                } else {
                    if (box.getName().endsWith(".jar")) {
                        addPath(box.getAbsolutePath());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            URL[] all = getURLs();
            boolean add = true;
            for (URL url : all) {
                if (url.equals(toolbox)) {
                    add = false;
                    break;
                }
            }
            if (add) {
                if (isNative(toolbox)) {
                    nativeDirs.add(toolbox);
                } else if (isHelp(toolbox)) {
                    helpDirs.add(toolbox);
                }
                addURL(toolbox);
            }
        }

        log.info("ToolClassLoader for TOOLBOX " + toolbox + " CLASSPATH:" + getClassPath());
    }

    private boolean isNative(File file) {
        if (file.getParent() != null && file.getParent().equals(Toolbox.NATIVE_DIR)) {
            for (String s : Toolbox.nativeDirs) {
                if (file.getName().equals(s)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isNative(URL url) {
        String path = url.getPath();
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        for (String s : Toolbox.nativeDirs) {
            if (path.endsWith(Toolbox.NATIVE_DIR + "/" + s)) {
                return true;
            }
        }
        return false;
    }

    private boolean isHelp(URL url) {
        String path = url.getPath();
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        if (path.endsWith(Toolbox.HELP_DIR)) {
            return true;
        }
        return false;
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

    protected String findLibrary(String name) {
        System.out.println("ToolClassLoader.findLibrary called with name:" + name);
        //String archDir = getNativeDir();
        String lib = System.mapLibraryName(name);
        URL url = this.getResource(lib);
        if (url == null) {
            return null;
        }
        try {
            return url.toURI().toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getNativeDir() {
        String binlib = null;
        String os = Home.os();
        String arch = Home.arch();
        if (os.equals("osx")) {
            binlib = Toolbox.OSX_DIR;
        } else if (os.equals("windows")) {
            if (arch.equals("x86")) {
                binlib = Toolbox.WIN_32_DIR;
            } else {
                binlib = Toolbox.WIN_64_DIR;
            }
        } else {
            if (arch.equals("i386")) {
                binlib = Toolbox.NUX_32_DIR;
            } else {
                binlib = Toolbox.NUX_64_DIR;
            }
        }
        return binlib;
    }

}
