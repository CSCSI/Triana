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

/**
 * Class Description Here...
 *
 * @author Andrew Harrison
 * @version $Revision:$
 * @created Jul 3, 2009: 11:26:18 AM
 * @date $Date:$ modified by $Author:$
 */

public class Toolbox {

    private String path;
    private String type;
    private boolean isVirtual;

    public Toolbox(String path, String type, boolean virtual) {
        this.path = path;
        this.type = type;
        isVirtual = virtual;
    }

    public Toolbox(String path, String type) {
        this(path, type, false);
    }

    public Toolbox(String path) {
        this(path, "No Type", false);
    }

    public Toolbox(String path, boolean virtual) {
        this(path, "No Type", virtual);
    }

    public Toolbox(File file, String type) {
        file.mkdirs();
        this.path = file.getAbsolutePath();
        this.type = type;
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

    public boolean isVirtual() {
        return isVirtual;
    }
}
