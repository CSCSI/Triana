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

package org.trianacode.taskgraph.clipin;

import org.trianacode.taskgraph.Task;

import java.util.Hashtable;

/**
 * Class Description Here...
 *
 * @author Andrew Harrison
 * @version $Revision:$
 * @created Jun 28, 2009: 5:47:31 PM
 * @date $Date:$ modified by $Author:$
 */

public class ClipInStore {


    private Task task;
    private Hashtable clipins = new Hashtable();


    protected ClipInStore(Task task) {
        this.task = task;
    }


    protected Task getTask() {
        return task;

    }

    protected void setClipIn(String name, Object clipin) {
        clipins.put(name, clipin);
    }

    protected Object getClipIn(String name) {
        if (clipins.containsKey(name))
            return clipins.get(name);
        else
            return null;
    }

    protected String[] getClipInNames() {
        return (String[]) clipins.keySet().toArray(new String[clipins.keySet().size()]);
    }
}
