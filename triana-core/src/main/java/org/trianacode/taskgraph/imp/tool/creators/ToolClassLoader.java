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

package org.trianacode.taskgraph.imp.tool.creators;

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
 * @created Jun 24, 2009: 4:30:22 PM
 * @date $Date:$ modified by $Author:$
 */

public class ToolClassLoader extends URLClassLoader {

    static Logger log = Logger.getLogger("org.trianacode.taskgraph.imp.tool.creators.ToolClassLoader");

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
                addURL(u);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

}
