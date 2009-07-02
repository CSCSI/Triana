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
import java.util.logging.Logger;

/**
 * Class Description Here...
 *
 * @author Andrew Harrison
 * @version $Revision:$
 * @created Jul 1, 2009: 1:31:57 PM
 * @date $Date:$ modified by $Author:$
 */

public class Home {

    static Logger logger = Logger.getLogger("org.trianacode.taskgraph.util.Home");
    private static String home = null;
    private static String os = null;


    public static synchronized String home() {
        if (home != null) {
            return home;
        }
        logger.info("calculating Triana home");
        File appHome;
        String triana = "Triana";
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

        return home;
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
