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

package org.trianacode.taskgraph.util;

import org.apache.commons.logging.Log;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.taskgraph.tool.ClassLoaders;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * Implements 1.3 ServiceProvider discovery mechanism. Adds the Java classpath as a search path by default. Other paths
 * can be added. TODO - make this cache entries for a one time hit?
 *
 * @author Andrew Harrison
 * @version $Revision:$
 */

public class ExtensionFinder {

    static Log log = Loggers.CONFIG_LOGGER;

    private static List<File> searchDirs = new ArrayList<File>();


    static {
        String cp = System.getProperty("java.class.path");
        String sep = System.getProperty("path.separator");
        if (cp != null && cp.length() > 0) {
            String[] paths = cp.split(sep);
            for (String path : paths) {

                File f = new File(path);
                if (f.exists()) {
                    log.debug("adding search path:" + f.getAbsolutePath());
                    searchDirs.add(f);
                }
            }
        }
    }

    public static void addSearchPaths(List<File> files) {
        for (File file : files) {
            if (!searchDirs.contains(file)) {
                searchDirs.add(file);
            }
        }
    }

    public static Map<Class, List<Object>> services(List<Class> providers) {
        Map<Class, List<Object>> ret = new HashMap<Class, List<Object>>();
        for (File searchDir : searchDirs) {
            Map<Class, List<Object>> map = getProviders(providers, searchDir);
            for (Class aClass : map.keySet()) {
                List<Object> objs = ret.get(aClass);
                if (objs == null) {
                    ret.put(aClass, map.get(aClass));
                } else {
                    objs.addAll(map.get(aClass));
                    ret.put(aClass, objs);
                }
            }
        }
        return ret;
    }

    public static List<Object> services(Class provider) {
        List provs = new ArrayList<Class>();
        provs.add(provider);
        Map<Class, List<Object>> ret = services(provs);
        if (ret != null && ret.get(provider) != null) {
            return ret.get(provider);
        }
        return new ArrayList<Object>();
    }


    public static Map<Class, List<Object>> getProviders(List<Class> providers, File file) {
        log.debug("searching for providers:" + file.getAbsolutePath());
        log.debug("*** Looking for extensions in : " + file.getAbsolutePath());
        Map<Class, List<Object>> ret = new HashMap<Class, List<Object>>();
        if (file.isDirectory()) {
            File meta = new File(file, "META-INF");
            if (meta.exists()) {
                File services = new File(meta, "services");
                if (services.exists()) {
                    for (Class provider : providers) {
                        File prov = new File(services, provider.getName());
                        if (prov.exists()) {

                            List<Object> impls = null;
                            try {
                                BufferedReader reader = new BufferedReader(
                                        new InputStreamReader(new FileInputStream(prov)));
                                String line;
                                impls = new ArrayList<Object>();
                                List<String> done = new ArrayList<String>();
                                while ((line = reader.readLine()) != null) {
                                    log.debug("got next service provider:" + line);
                                    log.debug("*** Found : " + line);
                                    try {
                                        if (!done.contains(line)) {
                                            Class cls = ClassLoaders.forName(line);
                                            if (provider.isAssignableFrom(cls)) {
                                                Object p = cls.newInstance();
                                                impls.add(p);
                                            }
                                            done.add(line);
                                        }
                                    } catch (Exception e1) {
                                        log.debug("Exception thrown trying to load service provider class " + line + ":"
                                                + FileUtils.formatThrowable(e1));
                                    }
                                }
                            } catch (IOException e) {
                                log.debug("error thrown while reading file:" + e.getMessage());
                            }
                            if (impls.size() > 0) {
                                List<Object> exist = ret.get(provider);
                                if (exist == null) {
                                    exist = new ArrayList<Object>();
                                }
                                exist.addAll(impls);
                                ret.put(provider, exist);
                            }
                        }
                    }
                }
            }
            File[] children = file.listFiles(new FilenameFilter() {
                public boolean accept(File file, String s) {
                    if (s.endsWith(".jar")) {
                        return true;
                    }
                    return false;
                }
            });
            if (children != null) {
                for (File child : children) {
                    Map<Class, List<Object>> map = getProviders(providers, child);
                    for (Class aClass : map.keySet()) {
                        List<Object> objs = ret.get(aClass);
                        if (objs == null) {
                            ret.put(aClass, map.get(aClass));
                        } else {
                            objs.addAll(map.get(aClass));
                            ret.put(aClass, objs);
                        }
                    }
                }
            }
        } else {
            if (file.getName().endsWith(".jar")) {
                try {
                    JarFile jf = new JarFile(file);
                    ZipEntry entry = jf.getEntry("META-INF/services/");
                    if (entry != null) {
                        for (Class provider : providers) {
                            ZipEntry e = jf.getEntry("META-INF/services/" + provider.getName());
                            if (e != null) {
                                InputStream zin = jf.getInputStream(e);
                                BufferedReader reader = new BufferedReader(new InputStreamReader(zin));
                                String line;
                                List<Object> impls = new ArrayList<Object>();
                                List<String> done = new ArrayList<String>();
                                while ((line = reader.readLine()) != null) {
                                    log.debug("got next service provider:" + line);
                                    // check if the class is in this jar
                                    ZipEntry sp = jf.getEntry(line.replace(".", "/") + ".class");
                                    if (sp != null) {
                                        try {
                                            if (!done.contains(line)) {
                                                Class cls = ClassLoaders.forName(line);
                                                if (provider.isAssignableFrom(cls)) {
                                                    Object prov = cls.newInstance();
                                                    impls.add(prov);
                                                }
                                                done.add(line);
                                            }
                                        } catch (Exception e1) {
                                            log.debug("Exception thrown trying to load service provider class " + line, e1);
                                        }
                                    }
                                }
                                if (impls.size() > 0) {
                                    List<Object> exist = ret.get(provider);
                                    if (exist == null) {
                                        exist = new ArrayList<Object>();
                                    }
                                    exist.addAll(impls);
                                    ret.put(provider, exist);
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    log.debug("Exception thrown trying to load service providers from file " + file, e);
                }
            }

        }
        return ret;
    }


}
