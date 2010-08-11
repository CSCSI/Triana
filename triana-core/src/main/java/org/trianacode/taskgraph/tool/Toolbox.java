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

import org.trianacode.taskgraph.util.UrlUtils;

/**
 * Class Description Here...
 *
 * @author Andrew Harrison
 * @version $Revision:$
 */

public class Toolbox {

    public static enum Type {
        FILE,
        HTTP
    }

    private String path;
    private String type;
    private boolean isVirtual;
    private String name;

    public static final String INTERNAL = "internal";

    public Toolbox(String path, String type, String name, boolean virtual) {
        this.path = path;
        this.type = type;
        this.name = name;
        isVirtual = virtual;
    }

    public Toolbox(String path, String type, String name) {
        this(path, type, name, false);
    }

    public Toolbox(String path, String name) {
        this(path, "No Type", name, false);
    }

    public Toolbox(String path) {
        this(path, "No Type", UrlUtils.getLastPathComponent(path), false);
    }

    public Toolbox(String path, String name, boolean virtual) {
        this(path, "No Type", name, virtual);
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
