/*
 * The University of Wales, Cardiff Triana Project Software License (Based
 * on the Apache Software License Version 1.1)
 *
 * Copyright (c) 2007 University of Wales, Cardiff. All rights reserved.
 *
 * Redistribution and use of the software in source and binary forms, with
 * or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1.  Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 * 2.  Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 * 3. The end-user documentation included with the redistribution, if any,
 *    must include the following acknowledgment: "This product includes
 *    software developed by the University of Wales, Cardiff for the Triana
 *    Project (http://www.trianacode.org)." Alternately, this
 *    acknowledgment may appear in the software itself, if and wherever
 *    such third-party acknowledgments normally appear.
 *
 * 4. The names "Triana" and "University of Wales, Cardiff" must not be
 *    used to endorse or promote products derived from this software
 *    without prior written permission. For written permission, please
 *    contact triana@trianacode.org.
 *
 * 5. Products derived from this software may not be called "Triana," nor
 *    may Triana appear in their name, without prior written permission of
 *    the University of Wales, Cardiff.
 *
 * 6. This software may not be sold, used or incorporated into any product
 *    for sale to third parties.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN
 * NO EVENT SHALL UNIVERSITY OF WALES, CARDIFF OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 *
 * ------------------------------------------------------------------------
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Triana Project. For more information on the
 * Triana Project, please see. http://www.trianacode.org.
 *
 * This license is based on the BSD license as adopted by the Apache
 * Foundation and is governed by the laws of England and Wales.
 *
 */
package org.trianacode.gui.help;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @version $Revision: 4048 $
 */
public class TrianaHelpProperties extends PropertiesEx {

    /**
     * Returns the applications getApplicationDataDir directory.  If it is null it uses the value set in TrianaCalcDefaults.
     */
    public final static String getHomeDirectory() {
        String homeDirectory;

        // Get the getApplicationDataDir directory from a command line property.  If it hasn't
        // been defined then default to either /usr/local/TrianaCalc for UNIX
        // or C:\TrianaCalc for Windows otherwise it is set to null;
        if ((homeDirectory = System.getProperty("trianahelphome").trim()) == null) {
            homeDirectory = "/usr/local/TrianaHelp";
        }

        if (homeDirectory.lastIndexOf(File.separator) != homeDirectory.length()) {
            homeDirectory = homeDirectory + File.separator;
        }

        return homeDirectory;
    }

    /**
     * Get the user's getApplicationDataDir directory
     *
     * @return The user's getApplicationDataDir directory as a String
     */
    public final static String getUserHomeDirectory() {
        return System.getProperty("user.getApplicationDataDir");
    }

    /**
     * Get the user's name
     *
     * @return The user name as a String
     */
    public final static String getUserName() {
        return System.getProperty("user.name");
    }

    /**
     *
     */
    public final static String getSettingsDirectory() {
        String directory;

        if (System.getProperty("os.name").trim().toLowerCase().indexOf("windows") > -1) {
            directory = getUserHomeDirectory() + File.separator + "tcalc" +
                    File.separator + "help";
        } else {
            directory = getUserHomeDirectory() + File.separator + ".trianahelp";
        }

        return directory;
    }

    /**
     *
     */
    public void loadUserSettings() {
        try {
            loadPropertiesFile(getSettingsDirectory() +
                    File.separator + "thelprc");
        }
        catch (FileNotFoundException ex1) {
            System.err.println("[TrianaHelpProperties: Can't open settings file]");
        }
        catch (IOException ex2) {
            System.err.println("[TrianaHelpProperties: Can't open settings file]");
        }
    }

    /**
     *
     */
    public void saveUserSettings() {
        try {
            savePropertiesFile(getSettingsDirectory(),
                    "thelprc", "TrianaHelp main settings");
        }
        catch (IOException ex2) {
            System.err.println("[TrianaHelpProperties: Can't open settings file]");
        }
    }

    /**
     * Saves properties to a file.
     *
     * @param path   The path of the file to save
     * @param file   The name of the file to save
     * @param header A text comment which is used as a header in the file
     * @see #savePropertiesFile(File, String)
     * @see #savePropertiesFile(String, String)
     * @see java.util.Properties#save
     */
    public void savePropertiesFile(String path, String file, String header)
            throws IOException {
        File propertiesFile = new File(path, file);

        if (!propertiesFile.exists()) {
            checkDirectory(path);
        }

        savePropertiesFile(propertiesFile, header);
    }

    public void checkDirectory(String path) {
        File directory = new File(path);

        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                System.err.println("[TrianaCalcProperties: Can't create directory]");
            }
        }
    }
}



