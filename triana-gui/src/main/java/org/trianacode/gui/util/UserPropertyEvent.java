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

package org.trianacode.gui.util;

import java.util.EventObject;

/**
 * Class Description Here...
 *
 * @author Andrew Harrison
 * @version $Revision:$
 */


public class UserPropertyEvent extends EventObject {

    private String propname;
    private Object propval;


    public UserPropertyEvent(Object source, String propname, Object propval) {
        super(source);
        this.propname = propname;
        this.propval = propval;
    }


    /**
     * the name of the property
     */
    public String getPropertyName() {
        return propname;
    }

    /**
     * the new value for the property
     */
    public Object getPropertyValue() {
        return propval;
    }

}
