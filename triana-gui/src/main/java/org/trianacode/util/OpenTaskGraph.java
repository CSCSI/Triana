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

import java.util.Vector;

/**
 * Class Description Here...
 *
 * @author Andrew Harrison
 * @version $Revision:$
 * @created Jun 25, 2009: 10:15:53 AM
 * @date $Date:$ modified by $Author:$
 */

public class OpenTaskGraph {


    private String rootFileName;
    private String name;
    private Vector children = new Vector();

    public void addChild(OpenTaskGraph child) {
        children.add(child);
    }

    public OpenTaskGraph[] getChildren() {
        return (OpenTaskGraph[]) children.toArray(new OpenTaskGraph[children.size()]);
    }

    public String getRootFileName() {
        return rootFileName;
    }

    public void setRootFileName(String rootFileName) {
        this.rootFileName = rootFileName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
