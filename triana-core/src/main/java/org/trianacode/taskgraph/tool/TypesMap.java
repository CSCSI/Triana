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

import org.trianacode.taskgraph.tool.creators.type.ClassHierarchy;
import org.trianacode.taskgraph.tool.creators.type.ClassParser;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class Description Here...
 *
 * @author Andrew Harrison
 * @version $Revision:$
 * @created Jul 5, 2009: 12:20:12 AM
 * @date $Date:$ modified by $Author:$
 */

public class TypesMap {

    private static Map<String, ClassHierarchy> allByName = new ConcurrentHashMap<String, ClassHierarchy>();
    private static Map<String, Map<String, ClassHierarchy>> allByType = new ConcurrentHashMap<String, Map<String, ClassHierarchy>>();
    private static ClassParser parser = new ClassParser();
    private static Set<String> deadEnds = new HashSet<String>();

    public static void load(File file) throws IOException {
        Map<String, ClassHierarchy> hiers = parser.readFile(file);
        allByName.putAll(hiers);
    }

    /**
     * get a class hierarchy based on the a File (could be class file or jar),
     * the full path to the class under scrutiny (will be the full path to the file in the case
     * of of a non-zipped file, or a jar: URL pointing to an entry if jarred), and the
     * type to match against, e.g. a getClass().getName() type string.
     *
     * @param file
     * @param path
     * @param type
     * @return
     */
    public static ClassHierarchy isType(File file, String path, String type) {
        Map<String, ClassHierarchy> mapped = allByType.get(type);
        if (mapped != null && mapped.get(path) != null) {
            return mapped.get(path);
        }
        for (String hier : allByName.keySet()) {
            if (isType(allByName, hier, type)) {
                ClassHierarchy ch = allByName.get(hier);
                addToTypes(type, ch);
                if (ch.getFile().equals(file.getAbsolutePath()) && ch.getFile().equals(path)) {
                    return ch;
                }
            }
        }
        return null;
    }

    private static void addToTypes(String type, ClassHierarchy ch) {
        Map<String, ClassHierarchy> types = allByType.get(type);
        if (types == null) {
            types = new HashMap<String, ClassHierarchy>();
        }
        types.put(ch.getFile(), ch);
        allByType.put(type, types);

    }


    public static List<ClassHierarchy> find(String type) throws IOException {
        long now = System.currentTimeMillis();
        Set<ClassHierarchy> ret = new HashSet<ClassHierarchy>();
        for (String hier : allByName.keySet()) {
            if (isType(allByName, hier, type)) {
                ret.add(allByName.get(hier));
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("TypeFinder.find time:" + (end - now));
        return new ArrayList<ClassHierarchy>(ret);
    }

    private static boolean isType(Map<String, ClassHierarchy> hiers, String hier, String type) {
        ClassHierarchy ch = hiers.get(hier);
        if (ch == null || deadEnds.contains(hier)) {
            return false;
        }
        String[] intfs = ch.getInterfaces();
        for (String intf : intfs) {
            if (intf.equals(type)) {
                return true;
            }
        }
        for (String intf : intfs) {
            boolean is = isType(hiers, intf, type);
            if (is) {
                return true;
            } else {
                System.out.println("TypeFinder.isType adding deadend:" + intf);
                deadEnds.add(intf);
            }
        }

        String superClass = ch.getSuperClass();
        if (superClass.equals(type)) {
            return true;
        } else {
            boolean b = isType(hiers, superClass, type);
            if (!b) {
                System.out.println("TypeFinder.isType adding deadend:" + hier);
                deadEnds.add(hier);
            }
            return b;
        }
    }


    private static String convert(String type) {
        if (type.endsWith(".class")) {
            type = type.substring(0, type.length() - 6);
        }
        type = type.replace(File.separator, ".");
        type = type.replace("/", ".");
        return type;

    }


}
