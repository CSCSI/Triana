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

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Class Description Here...
 *
 * @author Andrew Harrison
 * @version $Revision:$
 * @created Jun 25, 2009: 4:42:45 PM
 * @date $Date:$ modified by $Author:$
 */

public class TypeUtils {


    public static final String DOUBLE_ARRAY = "double[]";
    public static final String FLOAT_ARRAY = "float[]";
    public static final String LONG_ARRAY = "long[]";
    public static final String INT_ARRAY = "int[]";
    public static final String SHORT_ARRAY = "short[]";
    public static final String BYTE_ARRAY = "byte[]";


    public static double[] floatArrayToDouble(float[] from) {
        double[] to = new double[from.length];
        for (int i = 0; i < from.length; i++) {
            to[i] = (double) from[i];
        }
        return to;
    }

    /**
     * This function attempts to return the class representation for the
     * the given name of the Triana type. If prepends triana.types to start
     * of the name and looks for the class representation.
     */
    public static Class classForTrianaType(String name) {
        if (name.equals(Tool.UNKNOWN_DATA_TYPE) || name.equals(Tool.ANY_DATA_TYPE)) {
            try {
                return Class.forName("java.lang.Object");
            } catch (ClassNotFoundException except) {
                throw (new RuntimeException(except.getMessage()));
            }
        }

        boolean array = false;

        if (name.equals(DOUBLE_ARRAY))
            return new double[0].getClass();
        else if (name.equals(FLOAT_ARRAY))
            return new float[0].getClass();
        else if (name.equals(LONG_ARRAY))
            return new long[0].getClass();
        else if (name.equals(INT_ARRAY))
            return new int[0].getClass();
        else if (name.equals(SHORT_ARRAY))
            return new short[0].getClass();
        else if (name.equals(BYTE_ARRAY))
            return new byte[0].getClass();
        else if (name.endsWith("[]")) {
            array = true;
            name = name.substring(0, name.indexOf("[]"));
        }

        Class javaType = null;
        try {      // look for the full type Type next
            javaType = Class.forName(name);
        } catch (ClassNotFoundException cnf) {
            try {
                javaType = Class.forName("triana.types." + name);
            } catch (ClassNotFoundException e) {
                System.out.println("Type Error: " + name + " class not found!");
                return null;
            }
        }

        if (array)
            return Array.newInstance(javaType, 0).getClass();
        else
            return javaType;
    }

    /**
     * Attempts to convert the array of objects (Strings actually) to TrianaTypes
     */
    public static Class[] classForTrianaType(String[] types) {
        ArrayList typeClasses = new ArrayList();
        for (int i = 0; i < types.length; i++) {
            Class type = TypeUtils.classForTrianaType(types[i]);
            if (type != null)
                typeClasses.add(type);
        }
        return (Class[]) typeClasses.toArray(new Class[typeClasses.size()]);
    }

}
