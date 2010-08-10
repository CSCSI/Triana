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

package org.trianacode.taskgraph;

import org.trianacode.taskgraph.tool.Tool;

/**
 * Class Description Here...
 *
 * @author Andrew Harrison
 * @version $Revision:$
 */

public class Parameter {


    private String name;
    private Object value;
    private String type = Tool.UNKNOWN_TYPE;
    private boolean trigger;

    public Parameter(String name, Object value, String type, boolean trigger) {
        this.name = name;
        this.value = value;
        this.type = type;
        this.trigger = trigger;
    }

    public Parameter(String name, Object value, String type) {
        this(name, value, type, false);
    }

    public Parameter(String name, Object value) {
        this(name, value, Tool.UNKNOWN_TYPE, false);
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type == null ? Tool.UNKNOWN_TYPE : type;
    }

    public boolean isTrigger() {
        return trigger;
    }

    public void setTrigger(boolean trigger) {
        this.trigger = trigger;
    }
}
