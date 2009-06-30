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

import java.io.*;
import java.util.Properties;

/**
 * @version     $Revision: 4048 $
 * @date        $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public class PropertiesEx extends Properties {

    /**
     * Loads properties from a file.
     * @param file The file to load
     * @see #loadPropertiesFile(String)
     * @see #loadPropertiesFile(String, String)
     * @see java.util.Properties#load
     */
    public void loadPropertiesFile(File file)
            throws IOException, FileNotFoundException {
        FileInputStream in = new FileInputStream(file);
        load(in);
        in.close();
    }


    /**
     * Loads properties from a file.
     * @param file The full path and name of the file to load
     * @see #loadPropertiesFile(File)
     * @see #loadPropertiesFile(String, String)
     * @see java.util.Properties#load
     */
    public void loadPropertiesFile(String file)
            throws IOException, FileNotFoundException {
        loadPropertiesFile(new File(file));
    }


    /**
     * Loads properties from a file.
     * @param path The path of the file to load
     * @param file The name of the file to load
     * @see #loadPropertiesFile(File)
     * @see #loadPropertiesFile(String)
     * @see java.util.Properties#load
     */
    public void loadPropertiesFile(String path, String file)
            throws IOException, FileNotFoundException {
        loadPropertiesFile(new File(path, file));
    }

    /**
     * Saves properties to a file.
     * @param file The file to save
     * @param header A text comment which is used as a header in the file
     * @see #savePropertiesFile(String, String)
     * @see java.util.Properties#save
     */
    public void savePropertiesFile(File file, String header)
            throws IOException {
        FileOutputStream out = new FileOutputStream(file);
        store(out, header);
        out.close();
    }

    /**
     * Saves properties to a file.
     * @param file The full path and name of the file to save
     * @param header A text comment which is used as a header in the file
     * @see #savePropertiesFile(File, String)
     * @see java.util.Properties#save
     */
    public void savePropertiesFile(String file, String header)
            throws IOException {
        savePropertiesFile(new File(file), header);
    }

    /**
     * Sets the value of a property in the property list.  <B>Note</B> this
     * method is defined in Java 2 but not in Java 1.1 hence its definition here.
     * @param name Name of the property
     * @param value Value of the property
     * @see #getProperty
     * @see java.util.Hashtable#put
     */
    public Object setProperty(String name, String value) {
        return put(name, value);
    }
}



