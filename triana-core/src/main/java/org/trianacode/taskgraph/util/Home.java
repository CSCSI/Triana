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

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.logging.Logger;

/**
 * Class Description Here...
 *
 * @author Andrew Harrison
 * @version $Revision:$
 */

public class Home {

    static Logger logger = Logger.getLogger("org.trianacode.taskgraph.util.Home");
    private static String home = null;
    private static File runHome = null;
    private static String os = null;
    private static String arch = null;
    private static boolean isJarred = false;

    public static synchronized File runHome() {
        if (runHome != null) {
            return runHome;
        }
        logger.info("calculating Triana run home...");
        String fileSubPath = "triana-core/target/classes/org/trianacode/taskgraph/util/Home.class";
        try {
            URL url = Class.forName("org.trianacode.taskgraph.util.Home").getResource("Home.class");
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

    public static synchronized String home() {
        runHome();
        if (home != null) {
            return home;
        }
        logger.info("calculating Triana home");
        File appHome;
        String triana = "Triana4";
        File file = new File(System.getProperty("user.home"));
        if (!file.isDirectory()) {
            logger.severe("User home not a valid directory: " + file);
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
        logger.info("Triana support home : " + home);
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
