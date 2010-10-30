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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Vector;

/**
 * Somewhere to put all those class loaders. This takes a brute force approach to finding a class by name.
 *
 * @author Andrew Harrison
 * @version $Revision:$
 */

public class ClassLoaders {

    static Log log = Loggers.CONFIG_LOGGER;


    /**
     * hash table of class loaders
     */
    private static java.util.Hashtable<String, ClassLoader> mappedLoaders
            = new java.util.Hashtable<String, ClassLoader>();
    private static Vector<ClassLoader> loaders = new Vector<ClassLoader>();


    /**
     * add a classloader to the list of loaders tried for any classname.
     *
     * @param loader
     */
    public static void addClassLoader(ClassLoader loader) {
        if (!loaders.contains(loader)) {
            loaders.add(loader);
        }
    }

    /**
     * remove a class loader from the list of loaders that are tried for any classname.
     *
     * @param loader
     */
    public static void removeClassLoader(ClassLoader loader) {
        loaders.remove(loader);
    }

    /**
     * Set the ClassLoader associated with the given className.
     *
     * @param className the name of a class
     */
    public static void setClassLoader(String className, ClassLoader loader) {
        if (className != null && loader != null) {
            mappedLoaders.put(className, loader);
        }
    }

    /**
     * Obtain the ClassLoader (if any) associated with the given className.
     *
     * @param className the name of a class
     * @return class loader
     */
    public static ClassLoader getClassLoader(String className) {
        if (className == null) {
            return null;
        }
        return mappedLoaders.get(className);
    }

    /**
     * Deregister the ClassLoader for a given className.
     *
     * @param className the name of a class
     */
    public static void removeClassLoader(String className) {
        mappedLoaders.remove(className);
    }


    public static Class forName(String className) throws ClassNotFoundException {
        className = getTextClassName(className);
        boolean isArray = false;
        int dims = 0;
        if (className.endsWith("[]")) {
            isArray = true;
            dims = className.substring(className.indexOf("[]"), className.length()).length() / 2;
            className = className.substring(0, className.indexOf("[]"));

        }
        Class cls;
        if (className.equals("boolean")) {
            cls = Boolean.TYPE;
        } else if (className.equals("char")) {
            cls = Character.TYPE;
        } else if (className.equals("byte")) {
            cls = Byte.TYPE;
        } else if (className.equals("short")) {
            cls = Short.TYPE;
        } else if (className.equals("int")) {
            cls = Integer.TYPE;
        } else if (className.equals("long")) {
            cls = Long.TYPE;
        } else if (className.equals("float")) {
            cls = Float.TYPE;
        } else if (className.equals("double")) {
            cls = Double.TYPE;
        } else if (className.equals("void")) {
            cls = void.class;
        } else {
            cls = loadClass(className);
        }
        if (isArray) {
            Object arr = Array.newInstance(cls, new int[dims]);
            cls = arr.getClass();
        }
        return cls;
    }

    public static String getTextClassName(String text) {
        if (text == null || !(isJVMName(text))) {
            return text;
        }
        String className = "";
        int index = 0;
        while (index < text.length() && text.charAt(index) == '[') {
            index++;
            className += "[]";
        }
        if (index < text.length()) {
            if (text.charAt(index) == 'B') {
                className = "byte" + className;
            } else if (text.charAt(index) == 'C') {
                className = "char" + className;
            } else if (text.charAt(index) == 'D') {
                className = "double" + className;
            } else if (text.charAt(index) == 'F') {
                className = "float" + className;
            } else if (text.charAt(index) == 'I') {
                className = "int" + className;
            } else if (text.charAt(index) == 'J') {
                className = "long" + className;
            } else if (text.charAt(index) == 'S') {
                className = "short" + className;
            } else if (text.charAt(index) == 'Z') {
                className = "boolean" + className;
            } else {
                className = text.substring(index + 1, text.indexOf(";")) + className;
            }
        }
        return className;
    }


    public static Class loadClass(String name) throws ClassNotFoundException {
        final String className = name;

        // Get the class within a doPrivleged block
        Object ret =
                AccessController.doPrivileged(new PrivilegedAction() {
                    public Object run() {
                        log.debug("trying to find " + className);
                        try {
                            // Check if the class is a registered class then
                            // use the classloader for that class.
                            ClassLoader classLoader = getClassLoader(className);
                            if (classLoader == null) {
                                throw new ClassNotFoundException();
                            }
                            return Class.forName(className, true, classLoader);
                        } catch (ClassNotFoundException cnfe) {
                        }
                        //check the list of loaders
                        for (int i = 0; i < loaders.size(); i++) {
                            ClassLoader classLoader = loaders.get(i);
                            log.debug("next class loader:" + classLoader);
                            try {
                                return Class.forName(className, true, classLoader);
                            } catch (ClassNotFoundException cnfe) {

                            }
                        }
                        try {
                            // Try the context class loader
                            ClassLoader classLoader =
                                    Thread.currentThread().getContextClassLoader();
                            return Class.forName(className, true, classLoader);
                        } catch (ClassNotFoundException cnfe2) {
                            try {
                                // Try the classloader that loaded this class.
                                ClassLoader classLoader =
                                        ClassLoaders.class.getClassLoader();
                                return Class.forName(className, true, classLoader);
                            } catch (ClassNotFoundException cnfe3) {
                                // Try the default class loader.
                                try {
                                    return Class.forName(className);
                                } catch (Throwable e) {
                                    // Still not found, return exception
                                    return e;
                                }
                            }
                        }
                    }
                });
        // If the class was located, return it.  Otherwise throw exception
        if (ret instanceof Class) {
            return (Class) ret;
        } else if (ret instanceof ClassNotFoundException) {
            throw (ClassNotFoundException) ret;
        } else {
            throw new ClassNotFoundException(name);
        }
    }

    public static URL getResource(String name) {
        final String className = name;

        // Get the class within a doPrivleged block
        Object ret =
                AccessController.doPrivileged(new PrivilegedAction() {
                    public Object run() {
                        log.debug("trying to find " + className);

                        // Check if the class is a registered class then
                        // use the classloader for that class.
                        ClassLoader classLoader = getClassLoader(className);
                        if (classLoader != null) {
                            URL url = classLoader.getResource(className);
                            if (url != null) {
                                return url;
                            }
                        }
                        //check the list of loaders
                        for (int i = 0; i < loaders.size(); i++) {
                            classLoader = loaders.get(i);
                            log.debug("next class loader:" + classLoader);
                            URL url = classLoader.getResource(className);
                            if (url != null) {
                                return url;
                            }
                        }

                        // Try the context class loader
                        classLoader =
                                Thread.currentThread().getContextClassLoader();
                        log.debug("next class loader:" + classLoader);
                        URL url = classLoader.getResource(className);
                        if (url != null) {
                            return url;
                        }

                        // Try the classloader that loaded this class.
                        classLoader =
                                ClassLoaders.class.getClassLoader();
                        log.debug("next class loader:" + classLoader);
                        url = classLoader.getResource(className);
                        if (url != null) {
                            return url;
                        }

                        classLoader = ClassLoader.getSystemClassLoader();
                        log.debug("next class loader:" + classLoader);
                        return classLoader.getResource(className);

                    }

                });
        if (ret instanceof URL) {
            return (URL) ret;
        }
        return null;

    }

    public static InputStream getResourceAsStream(String name) {
        URL url = getResource(name);
        if (url != null) {
            try {
                return url.openStream();
            } catch (IOException e) {
                return null;
            }
        }
        return null;
    }


    private static boolean isJVMName(String text) {
        return text.startsWith("[") ||
                (text.startsWith("L") && text.indexOf(";") > -1) ||
                text.equals("B") ||
                text.equals("C") ||
                text.equals("D") ||
                text.equals("F") ||
                text.equals("I") ||
                text.equals("J") ||
                text.equals("S") ||
                text.equals("Z");
    }

}
