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

package org.trianacode.taskgraph.ser;

import java.util.HashMap;

/**
 * Class Description Here...
 *
 * @author Andrew Harrison
 * @version $Revision:$
 */

public class ObjectDeserializationManager {

    /**
     * Hash map of ObjectDeserializer instances keyed by name
     */
    private static final HashMap deserializers = new HashMap();


    /**
     * Registers a named ObjectDesrializer
     */
    public static void registerObjectDeserializer(String name, ObjectDeserializer deserializer) {
        deserializers.put(name, deserializer);
    }

    /**
     * Unregisters a named ObjectDeserializer
     */
    public static void unregisterObjectDeserializer(String name) {
        deserializers.remove(name);
    }


    /**
     * @return the ObjectDeserializer with the specified name
     */
    public static ObjectDeserializer getObjectDeserializer(String name) {
        return (ObjectDeserializer) deserializers.get(name);
    }

}
