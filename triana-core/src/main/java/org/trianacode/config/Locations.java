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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.logging.Logger;

/**
 * Detects/creates the Application directory getApplicationDataDir for storing application specific data.
 * this is  rehash of Home, which allows you to find out about the home directory of
 *  the Jar (or dist) but also the home directory of triana where all the toolboxes live (getHomeProper).
 *  It also allows you to find the application directory and the config file. The new config file
 *  is named org.trianacode.properties and is in the app data directory.
 *
 * @author Andrew Harrison
 * @version $Revision:$
 */

public class Locations {

    static Logger logger = Logger.getLogger("org.trianacode.config.Locations");
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
            return Locations.runHome().getParentFile().getAbsolutePath(); // is this correct?
        }
    }

    public static String[] getInternalToolboxes() {
        String paths = System.getProperty(TrianaProperties.TOOLBOX_SEARCH_PATH_PROPERTY);

        if (paths!=null)
            return paths.split(",");
        else
            return new String[0];
        
    }


    private static synchronized File calculateRunHome() {
        if (runHome != null) {
            return runHome;
        }
        logger.info("calculating Triana run hom...");
        String fileSubPath = "triana-core/target/classes/org/trianacode/taskgraph/util/Locations.class";
        try {
            URL url = Class.forName("org.trianacode.config.Locations").getResource("Locations.class");
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
        logger.info("calculating Triana application data directory");
        File appHome;
        String triana = "Triana4";
        File file = new File(System.getProperty("user.home"));
        if (!file.isDirectory()) {
            logger.severe("Application data directory not a valid directory: " + file);
            appHome = new File(triana);
        } else {
            String os = os();
            logger.fine("OS is " + os);
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
                    logger.severe("Could not find %APPDATA%: " + APPDATA);
                    appHome = new File(file, triana);
                }
            } else {
                appHome = new File(file, "." + triana.toLowerCase());
            }
        }
        if (!appHome.exists()) {
            if (appHome.mkdir()) {
            } else {
                logger.severe("Could not create " + appHome);
            }
        }
        home = appHome.getAbsolutePath();
        logger.info("Triana support application data directory : " + home);
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
