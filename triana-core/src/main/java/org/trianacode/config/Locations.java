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

package org.trianacode.config;

import org.apache.commons.logging.Log;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.taskgraph.tool.ClassLoaders;
import org.trianacode.taskgraph.tool.FileToolboxLoader;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

/**
 * Detects/creates the Application directory getApplicationDataDir for storing application specific data.
 * this is  rehash of Home, which allows you to find out about the home directory of
 * the Jar (or dist) but also the home directory of triana where all the toolboxes live (getHomeProper).
 * It also allows you to find the application directory and the config file. The new config file
 * is named org.trianacode.properties and is in the app data directory.
 *
 * @author Andrew Harrison
 * @version $Revision:$
 */

public class Locations {

    private static Log logger = Loggers.CONFIG_LOGGER;

    private static String home = null;
    private static File runHome = null;
    private static String os = null;
    private static String arch = null;
    private static boolean isJarred = false;

    private static String defaultConfigFile;

    // Defines for property names

    static final String DEFAULT_PROPERTY_FILE = TrianaProperties.DOMAIN + ".properties";        // Property file


    /**
     * Initializes the Triana getApplicationDataDir
     */
    static {
        home = getApplicationDataDir();

        // make the app getApplicationDataDir if it doesn't exist

        File f = new File(home);
        if (!f.exists()) f.mkdir();

        defaultConfigFile = home + File.separator + DEFAULT_PROPERTY_FILE;

        f = new File(defaultConfigFile);

        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static String getToolboxForClass(Class cls) {
        String fullname = cls.getName();
        String pathName = fullname.replace('.', '/') + ".class";
        try {
            URL url = ClassLoaders.getResource(pathName);
            String fullPath = url.toURI().toASCIIString();
            if (fullPath.startsWith("jar:")) {
                int entryStart = fullPath.indexOf("!/");
                if (entryStart == -1) {
                    entryStart = fullPath.length();
                }
                String file = fullPath.substring(4, entryStart);
                return new File(new URI(file)).getAbsolutePath();
            } else { // presume file
                if (fullPath.indexOf(pathName) > -1) {
                    fullPath = fullPath.substring(0, fullPath.indexOf(pathName));
                }
                return new File(new URI(fullPath)).getAbsolutePath();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getToolboxForClass(String cls) {
        try {
            return getToolboxForClass(ClassLoaders.forName(cls));
        } catch (ClassNotFoundException e) {
            logger.warn(e);
        }
        return null;
    }

    /*
     * Gets the config file.
     *
     */

    public static String getDefaultConfigFile() {
        return defaultConfigFile;
    }


    public static synchronized File runHome() {
        return calculateRunHome();
    }

    /**
     * @return the absolute path to the user resource directory.
     */
    public static synchronized String getApplicationDataDir() {
        return calculateHome();
    }

    /**
     * returns the root directory for triana
     *
     * @return
     */
    public static String getHomeProper() {
        if (Locations.isJarred()) {
            File f = Locations.runHome();
            File p = f.getParentFile().getParentFile().getParentFile();
            return p.getAbsolutePath();
        } else {
            return Locations.runHome().getAbsolutePath(); // is this correct?
        }
    }

    public static String getDefaultToolboxRoot() {
        File f = Locations.runHome();
        File p;
        if (isJarred()) {
            p = new File(f.getParentFile(), "toolboxes");
        } else {
            p = new File(f, "toolboxes");
        }
        if (p != null) {
            File[] boxes = p.listFiles();
            StringBuilder sb = new StringBuilder();
            for (File box : boxes) {
                if (box.isDirectory()) {
                    sb.append(
                            "{" + FileToolboxLoader.LOCAL_TYPE + "}{" + box.getName() + "}" + box.getAbsolutePath() + ", ");
                }
            }
            String all = sb.toString();
            return all.substring(0, all.lastIndexOf(","));
        } else {
            return "";
        }

    }

    public static String getDefaultTemplateRoot() {
        File f = Locations.runHome();

        if (Locations.isJarred()) {
            return f.getAbsolutePath();
        } else {
            File p = new File(f, "triana-core");
            p = new File(p, "target");
            p = new File(f, "classes");
            return p.getAbsolutePath();
        }
    }

    public static String[] getToolboxes() {
        String paths = System.getProperty(TrianaProperties.TOOLBOX_SEARCH_PATH_PROPERTY);

        if (paths != null)
            return paths.split(",");
        else
            return new String[0];

    }


    private static synchronized File calculateRunHome() {
        if (runHome != null) {
            return runHome;
        }
        logger.debug("calculating Triana run home...");
        String fileSubPath = "triana-core/target/classes/org/trianacode/config/Locations.class";
        try {
            URL url = ClassLoaders.getResource("org/trianacode/config/Locations.class");
            String fullPath = url.toURI().toASCIIString();
            if (fullPath.startsWith("jar:")) {
                int entryStart = fullPath.indexOf("!/");
                if (entryStart == -1) {
                    entryStart = fullPath.length();
                }
                String file = fullPath.substring(4, entryStart);
                runHome = new File(new URI(file));
                isJarred = true;
            } else { // presume file
                if (fullPath.indexOf(fileSubPath) > -1) {
                    fullPath = fullPath.substring(0, fullPath.indexOf(fileSubPath));
                }
                runHome = new File(new URI(fullPath));
                isJarred = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("Triana runHome : " + runHome);
        return runHome;
    }

    public static boolean isJarred() {
        return isJarred;
    }

    private static synchronized String calculateHome() {
        runHome();
        if (home != null) {
            return home;
        }
        logger.debug("calculating Triana application data directory");
        File appHome;
        String triana = "Triana4";
        File file = new File(System.getProperty("user.home"));
        if (!file.isDirectory()) {
            logger.error("Application data directory not a valid directory: " + file);
            appHome = new File(triana);
        } else {
            String os = os();
            logger.debug("OS is " + os);
            if (os.indexOf("osx") > -1) {
                File libDir = new File(file, "Library/Application Support");
                libDir.mkdirs();
                appHome = new File(libDir, triana);
            } else if (os.equals("windows")) {
                String APPDATA = System.getenv("APPDATA");
                File appData = null;
                if (APPDATA != null) {
                    appData = new File(APPDATA);
                }
                if (appData != null && appData.isDirectory()) {
                    appHome = new File(appData, triana);
                } else {
                    logger.error("Could not find %APPDATA%: " + APPDATA);
                    appHome = new File(file, triana);
                }
            } else {
                appHome = new File(file, "." + triana.toLowerCase());
            }
        }
        if (!appHome.exists()) {
            if (appHome.mkdir()) {
            } else {
                logger.error("Could not create " + appHome);
            }
        }
        home = appHome.getAbsolutePath();
        logger.debug("Triana support application data directory : " + home);
        return home;
    }

    public static synchronized String arch() {
        arch = System.getProperty("os.arch").toLowerCase();
        return arch;
    }


    public static synchronized String os() {
        if (os != null) {
            return os;
        }
        os = System.getProperty("os.name");

        if (os.startsWith("Windows")) {
            os = "windows";
        } else if ((os.equals("SunOS")) || (os.equals("Solaris"))) {
            os = "solaris";
        } else if (os.equals("Digital Unix")) {
            os = "dec";
        } else if (os.equals("Linux")) {
            os = "linux";
        } else if ((os.equals("Irix")) || (os.equals("IRIX"))) {
            os = "irix";
        } else if (os.equals("Mac OS X")) {
            os = "osx";
        } else {
            os = "Not Recognised";
        }
        return os;
    }
}
