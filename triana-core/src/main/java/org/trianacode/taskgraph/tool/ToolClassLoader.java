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

import org.apache.commons.logging.Log;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.taskgraph.util.UrlUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class Description Here...
 *
 * @author Andrew Harrison
 * @version $Revision:$
 */

public class ToolClassLoader extends URLClassLoader {

    static Log log = Loggers.TOOL_LOGGER;

    private List<String> visibleRoots = new ArrayList<String>();
    private List<String> libPaths = new ArrayList<String>();
    private File root = null;

    public ToolClassLoader(ClassLoader classLoader) {
        super(new URL[0], classLoader);
    }

    public ToolClassLoader() {
        this(ClassLoader.getSystemClassLoader());
    }


    public void addToolBox(URL toolbox) {
        addToolBox(toolbox, true, false);
    }

    public List<String> getLibPaths() {
        return Collections.unmodifiableList(libPaths);
    }


    private void addToolBox(URL toolbox, boolean first, boolean descend) {
        if (UrlUtils.isFile(toolbox)) {

            try {
                File box = new File(toolbox.toURI());
                if (!box.exists() || box.length() == 0 || box.getName().startsWith(".")) {
                    return;
                }
                if (first) {
                    root = box;
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
                            } else if (name.equals("help")) {
                                addPath(file.getAbsolutePath());
                                addToolBox(file.toURI().toURL(), false, true);
                            } else if (name.equals("src")) {
                                continue;
                            } else if (name.equals("CVS")) {
                                continue;
                            } else if (name.equals("nativ")) {
                                addPath(file.getAbsolutePath());
                                addToolBox(file.toURI().toURL(), false, true);
                            } else {
                                if (descend) {
                                    addPath(file.getAbsolutePath());
                                }
                                addToolBox(file.toURI().toURL(), false, descend);
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
                addURL(toolbox);
            }
        }

        log.debug("ToolClassLoader for TOOLBOX " + toolbox + " CLASSPATH:" + getClassPath());
    }

    private boolean isUnderHelp(File file) {
        File p = file;
        while (p != null) {
            if (p.getName().equals("help")) {
                return true;
            }
            p = p.getParentFile();
        }
        return false;
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

    public File getFile(String relativePath) {
        File f = new File(root, relativePath);
        if (f.exists() && f.length() > 0) {
            return f;
        }
        return null;
    }

    public List<String> getVisibleRools() {
        return Collections.unmodifiableList(visibleRoots);
    }


    private void addPath(String path) {
        File f = new File(path);
        if (f.exists()) {
            log.debug("parsing " + f.getAbsoluteFile());
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
                    String rootPath = UrlUtils.fromFile(root).toString();
                    String relPath = UrlUtils.fromFile(f).toString();
                    relPath = relPath.substring(relPath.indexOf(rootPath) + rootPath.length());
                    if (f.getName().equals("help")
                            || f.getName().equals("classes")
                            || f.getName().equals("lib")
                            || f.getName().equals("nativ")) {
                        visibleRoots.add(s);
                    }
                    if (s.indexOf("/help/") == -1) {       // TODO HACK ALERT
                        libPaths.add(relPath);
                    }

                    log.info("adding URL:" + u);
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
/*

    private String getNativeDir() {
        String binlib = null;
        String os = Locations.os();
        String arch = Locations.arch();
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
*/

}
