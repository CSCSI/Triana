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

import org.apache.commons.logging.Log;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.taskgraph.tool.creators.type.ClassHierarchy;
import org.trianacode.taskgraph.tool.creators.type.ClassParser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class Description Here...
 *
 * @author Andrew Harrison
 * @version $Revision:$
 */

public class TypesMap {

    private static Log log = Loggers.LOGGER;


    private static Map<String, ClassHierarchy> allByName = new ConcurrentHashMap<String, ClassHierarchy>();

    private static Map<String, Map<String, ClassHierarchy>> allByType
            = new ConcurrentHashMap<String, Map<String, ClassHierarchy>>();
    private static Map<String, ClassHierarchy> annotated = new ConcurrentHashMap<String, ClassHierarchy>();
    private static ClassParser parser = new ClassParser();
    private static Map<String, String> deadEnds = new ConcurrentHashMap<String, String>();

    public static void load(URL url) throws IOException {
        Map<String, ClassHierarchy> hiers = parser.readURL(url);
        for (String s : hiers.keySet()) {
            ClassHierarchy ch = hiers.get(s);
            if (ch.isAnnotated()) {
                if (annotated.get(ch.getFile()) == null) {
                    annotated.put(ch.getFile(), ch);
                }
            } else {
                if (allByName.get(s) == null) {
                    allByName.put(s, ch);
                }
            }
        }
    }

    /**
     * get a class hierarchy based on the full path to the class under scrutiny (will be the full path to a file in the
     * case of of a non-zipped file, or a jar: URL pointing to an entry if jarred), and the type to match against, e.g.
     * a getClass().getName() type string.
     *
     * @param path
     * @param type
     * @return
     */
    public static ClassHierarchy isType(String path, String type) {

        Map<String, ClassHierarchy> mapped = allByType.get(type);
        if (mapped != null && mapped.get(path) != null) {
            return mapped.get(path);
        }
        for (String hier : allByName.keySet()) {
            ClassHierarchy ch = isType(allByName, hier, type, true);
            if (ch != null) {
                addToTypes(type, ch);
                if (ch.getFile().equals(path)) {
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
            ClassHierarchy ch = isType(allByName, hier, type, true);
            if (ch != null) {
                ret.add(ch);
            }
        }
        long end = System.currentTimeMillis();
        //System.out.println("TypeMap.find time:" + (end - now));
        return new ArrayList<ClassHierarchy>(ret);
    }

    public static ClassHierarchy getAnnotated(String path) throws IOException {
        return annotated.get(path);
    }


    private static ClassHierarchy isType(Map<String, ClassHierarchy> hiers, String hier, String type,
                                         boolean mustBeConcrete) {
        ClassHierarchy ch = hiers.get(hier);
        if (ch == null) {
            return null;
        }
        if (deadEnds.get(hier) != null) {
            return null;
        }
        if (!ch.isPublic()) {
            return null;
        }
        if (!ch.isConcrete() && mustBeConcrete) {
            return null;
        }

        String[] intfs = ch.getInterfaces();
        for (String intf : intfs) {
            if (intf.equals(type)) {
                return ch;
            }
        }

        String superClass = ch.getSuperClass();
        if (superClass != null) {
            if (superClass.equals(type)) {
                return ch;
            }
            try {
                Class cls = ClassLoaders.forName(superClass);
                Class sc = cls.getSuperclass();
                while (sc != null) {
                    if (sc.getName().equals(type)) {
                        return ch;
                    }
                    sc = sc.getSuperclass();
                }
            } catch (ClassNotFoundException e) {
                log.info("Failed to load unit:" + e.getMessage());
            }
        }
        deadEnds.put(hier, type);
        return null;

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
