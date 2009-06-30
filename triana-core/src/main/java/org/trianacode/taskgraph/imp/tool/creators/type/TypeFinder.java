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

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * reads class files to find out what interface or class they extend.
 *
 * @author Andrew Harrison
 * @version $Revision:$
 * @created Jun 26, 2009: 2:23:43 PM
 * @date $Date:$ modified by $Author:$
 */

public class TypeFinder {

    private String type;
    private File file;
    private ClassParser parser = new ClassParser();

    public TypeFinder(String type, File file) {
        this.type = convert(type);
        this.file = file;
    }

    public List<String[]> find() throws IOException {
        Set<String[]> ret = new HashSet<String[]>();
        Map<String, ClassHierarchy> hiers = parser.readFile(file);
        
        for (String hier : hiers.keySet()) {
            if (isType(hiers, hier)) {
                ret.add(new String[]{hier, hiers.get(hier).getFile()});
            }
        }
        return new ArrayList<String[]>(ret);
    }

    private boolean isType(Map<String, ClassHierarchy> hiers, String hier) {
        ClassHierarchy ch = hiers.get(hier);
        if(ch == null) {
            return false;
        }
        String[] intfs = ch.getInterfaces();
        for (String intf : intfs) {
            if (intf.equals(type)) {
                return true;
            }
        }
        for (String intf : intfs) {
            boolean is = isType(hiers, intf);
            if(is) {
                return true;
            }
        }
        
        String superClass = ch.getSuperClass();
        if (superClass.equals(type)) {
            return true;
        } else {
            return isType(hiers, superClass);
        }
    }


    private String convert(String type) {
        if (type.endsWith(".class")) {
            type = type.substring(0, type.length() - 6);
        }
        type = type.replace(File.separator, ".");
        type = type.replace("/", ".");
        return type;

    }

    public static void main(String[] args) {
        try {
            List<String[]> matches = new TypeFinder("org.trianacode.taskgraph.tool.Tool", new File("/Users/scmabh/work/maven/triana/triana2/triana-core/target/classes")).find();
            for (String[] match : matches) {
                System.out.println("TypeFinder.main matched:" + match[0] + ":" + match[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
