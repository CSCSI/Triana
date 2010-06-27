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


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Class Description Here...
 *
 * @author Andrew Harrison
 * @version $Revision:$
 */

public class Base64ObjectDeserializer implements ObjectDeserializer {

    public static final String BASE64_OBJECT_DESERIALIZER = "org.trianacode.taskgraph.ser.Base64ObjectDeserializer";


    public Object deserializeObject(String serialized) throws IOException {
        try {
            byte[] bytes = Base64.decode(serialized);
            ObjectInputStream in = new TrianaObjectInputStream(new ByteArrayInputStream(bytes));
            return in.readObject();
        }
        catch (Exception e) {
            return serialized;
        }

    }
}
