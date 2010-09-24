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
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.trianacode.config.TrianaProperties;
import org.trianacode.taskgraph.util.UrlUtils;

/**
 * Class Description Here...
 *
 * @author Andrew Harrison
 * @version $Revision:$
 */

public class Toolbox {

    /**
     * constants for directory names where native and java libs are stored in a toolbox. For a toolbox to know where
     * these things are and have them loaded, and exposed, these naming convention must be followed.
     * <p/>
     * The OS-specific dir names should all be within a 'nativ' dir.
     */
    public static final String NATIVE_DIR = "nativ";
    public static final String WIN_32_DIR = "win32";
    public static final String WIN_64_DIR = "win64";
    public static final String OSX_DIR = "osx";
    public static final String OSX_32_DIR = "osx32";
    public static final String OSX_64_DIR = "osx64";
    public static final String NUX_32_DIR = "nux32";
    public static final String NUX_64_DIR = "nux64";
    public static final String LIB_DIR = "lib";
    public static final String HELP_DIR = "help";

    public static String[] nativeDirs =
            {
                    WIN_32_DIR,
                    WIN_64_DIR,
                    OSX_DIR,
                    OSX_32_DIR,
                    OSX_64_DIR,
                    NUX_32_DIR,
                    NUX_64_DIR
            };


    private String path;
    private String type;
    private boolean isVirtual;
    private String name;

    public static final String INTERNAL = "internal";
    private ToolClassLoader loader = new ToolClassLoader();

    TrianaProperties properties;

    public Toolbox(String path, String type, String name, boolean virtual, TrianaProperties properties) {
        this.path = path;
        this.properties=properties;
        this.type = type;
        this.name = name;
        isVirtual = virtual;
    }

    public Toolbox(String path, String type, String name, TrianaProperties properties) {
        this(path, type, name, false, properties);
    }

    public Toolbox(String path, String name, TrianaProperties properties) {
        this(path, "No Type", name, false, properties);
    }

    public Toolbox(String path, TrianaProperties properties) {
        this(path, "No Type", UrlUtils.getLastPathComponent(path), false, properties);
    }

    public Toolbox(String path, String name, boolean virtual, TrianaProperties properties) {
        this(path, "No Type", name, virtual, properties);
    }

    public Toolbox(File file, String type) {
        file.mkdirs();
        this.path = file.getAbsolutePath();
        this.type = type;
        this.name = file.getName();
        isVirtual = false;
    }

    public Toolbox(File file) {
        this(file, "No Type");
    }

    public TrianaProperties getProperties() {
        return properties;
    }

    public ClassLoader getClassLoader() {
        return loader;
    }

    /**
     * get relative paths pointing to library files (.class, .jar)
     *
     * @return
     */
    public List<String> getLibPaths() {
        return loader.getLibPaths();
    }

    /**
     * get a list of local files that can browsed by a user. Any file not equal to or a child of one of these strings
     * will have access denied.
     *
     * @return
     */
    public List<String> getVisibleRoots() {
        return loader.getVisibleRools();
    }

    /**
     * attempt to get a local file with a path that is a child of the root of the toolbox
     *
     * @param relativePath
     * @return
     */
    public File getFile(String relativePath) {
        return loader.getFile(relativePath);
    }

    public String getClassPath() {
        return loader.getClassPath();
    }

    public void loadTools() throws IOException {
        ClassLoaders.addClassLoader(loader);
        URL url = UrlUtils.toURL(getPath());
        loader.addToolBox(url);
        TypesMap.load(url);
    }

    public String getPath() {
        return path;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public boolean isVirtual() {
        return isVirtual;
    }
}
