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

package org.trianacode.taskgraph.imp.tool.creators.type;

import java.util.ArrayList;
import java.util.List;

/**
 * Class Description Here...
 *
 * @author Andrew Harrison
 * @version $Revision:$
 * @created Jun 26, 2009: 2:03:55 PM
 * @date $Date:$ modified by $Author:$
 */

public class ClassHierarchy {

    private String name;
    private String superClass;
    private List<String> interfaces = new ArrayList<String>();
    private String file;

    public ClassHierarchy(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getSuperClass() {
        return superClass;
    }

    public void setSuperClass(String superClass) {
        this.superClass = superClass;
    }

    public String[] getInterfaces() {
        return interfaces.toArray(new String[interfaces.size()]);
    }

    public void addInterface(String inf) {
        if(!interfaces.contains(inf)) {
            interfaces.add(inf);
        }
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String toString() {
        return "Name:" + name + " Superclass:" + superClass + " Interfaces:" + interfaces;
    }


}
