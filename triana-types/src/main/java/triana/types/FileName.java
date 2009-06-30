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
package triana.types;


import org.trianacode.taskgraph.util.FileUtils;
import triana.types.util.Str;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Vector;

/**
 * FileName is a type which encapsulates a file name whether it be
 * a local disk file or an internet file. It is represented by a
 * String and you can determine what type it is by calling the
 * getFileType() function. This returns LOCAL if its a disk file
 * or HTTP if it is a HTTP protocol file.
 *
 * <p>The motivation behind this class is to deal simply with these
 * two file types so that triana can import data from the internet
 * as transparently as a local disk file.  These are the only real
 * protocols we use.  I'm sure there are others but we've not
 * included them here.
 *
 * @author      Ian Taylor
 * @author      Bernard Schutz
 * @created     23rd May 2000
<<<<<<< FileName.java
 * @version     $Revision: 4048 $
 * @date        $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
=======
 * @version     $Revision: 4048 $
 * @date        $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
>>>>>>> 1.6.22.1
 */
public class FileName extends TrianaType implements AsciiComm {

    public final static int INVALID = -1;
    public final static int LOCAL = 0;
    public final static int HTTP = 1;
    private int fileType = LOCAL;
    private String fileName;
    private int[] lineNos = new int[1];
    Vector fileNameList = new Vector();

    /**
     * Creates a new empty FileName.
     */
    public FileName() {
        setFile(null);
    }

    /**
     * Creates a new FileName from a string containing the full path and
     * name of the file.
     *
     * @param fn the full path and name of file.
     */
    public FileName(String fn) {
        setFile(fn);
    }

    /**
     * Creates a new FileName from a string containing the specified
     * path and name of the file.
     *
     * @param path the full path.
     * @param fn name of file.
     */
    public FileName(String path, String fn) {
        if (isHTTP(path))
            setFile(path.endsWith("/") ? path + fn : path + "/" + fn);
        else
            setFile(path.endsWith(File.separator) ? path + fn :
                    path + File.separator + fn);
    }

    /**
     * Creates a new FileName from another File object
     *
     * @param fn a file object representing the file.
     */
    public FileName(File fn) {
        fileType = LOCAL;
        setFile(fn.getPath());  // in File, "path" is the whole name, not just the directory path
    }

    public FileName(URL url) {
        fileType = HTTP;
        setFile(url.toString());
    }

    /**
     * Adds the filename to the list of filenames. Upon creation of a FileName
     * there is one object in this list of this fileName object
     */
    public void add(FileName fn) {
        if (size() == 0)
            fileNameList.add(this);
        fileNameList.add(fn);
    }

    /**
     * @return true if this filename object stores a list of filenames
     */
    public boolean hasMultiple() {
        if (size() == 0)
            return false;
        else
            return true;
    }

    /**
     * Gets the ith filename in this list of filenames.
     */
    public FileName get(int i) {
        if (size() == 0)
            return this;
        return (FileName) fileNameList.get(i);
    }

    /**
     * Gets the number of filenames in list filenames.
     */
    public int size() {
        return fileNameList.size();
    }

    /**
     * Sets the interesting line numbers within the file which units can use
     * to display the file at the appropriate place. For example,
     * this function is called by the Grep unit.
     */
    public void setLineNumbers(int ln[]) {
        lineNos = ln;
    }

    /**
     * Gets the line number within this file
     */
    public int[] getLineNumbers() {
        return lineNos;
    }

    public static boolean isHTTP(String file) {
        if (file == null) return false;
        if (file.indexOf("://") != -1) {
            if (!file.startsWith("file"))
                return true;
            else
                return false;
        }
        else
            return false;
    }

    /**
     * Sets the file to the given string and works out if its a HTTP
     * address or not
     */
    public void setFile(String file) {
        fileName = file;
        if (isHTTP(fileName))
            fileType = HTTP;
        else
            fileType = LOCAL;
    }

    /**
     * @return the full file and path
     */
    public String getFile() {
        return fileName;
    }

    /**
     * @return the document title of this file name.  If this file name is
     * a html file then it returns the title of this file otherwise it
     * returns the name of the file without the directory.
     */
    public String getDocumentTitle() {
        return getHTMLTitle(fileName);
    }

    /**
     * @return the document title of this file name.  If this file name is
     * a html file then it returns the title of this file otherwise it
     * returns the name of the file without the directory.
     */
    public static String getHTMLTitle(String fn) {
        String contents = FileUtils.readFile(fn);
        String lc = contents.toLowerCase();

        if ((lc.indexOf("<title>") != -1) && (lc.indexOf("</title>") != -1))
            return contents.substring(lc.indexOf("<title>") + 7,
                                      lc.indexOf("</title>")).trim();
        else {
            if (isHTTP(fn))
                return fn.substring(fn.lastIndexOf("/") + 1);
            else
                return fn.substring(fn.lastIndexOf(File.separator) + 1);
        }
    }

    /**
     * Returns a reference to this FileName not including any path information
     */
    public String getFileNoPath() {
        if (fileType == HTTP)
            return fileName.substring(fileName.lastIndexOf("/") + 1);
        else
            return fileName.substring(fileName.lastIndexOf(File.separator) + 1);
    }

    /**
     * Returns a reference to the path of this file name
     */
    public String getPath() {
        if (fileType == HTTP)
            return fileName.substring(0, fileName.lastIndexOf("/"));
        else
            return fileName.substring(0, fileName.lastIndexOf(File.separator));
    }

    /**
     * @return the filetype i.e. LOCAL (i.e. disk file) or HTTP, (on the
     * internet).
     */
    public int getFileType() {
        return fileType;
    }

    /**
     * This function returns <i>true</i> if the specified object <i>fn</i>
     * is the same type and has the same contents as this object
     */
    public boolean equals(Object fn) {
        if (!(fn instanceof FileName))
            return false;

        return ((FileName) fn).getFile().equals(this.getFile());
    }

    /**
     * @return a copy of a FileName. This <b>must</b> be implemented for
     * every TrianaType.
     * @see TrianaType#copyMe
     */
    public TrianaType copyMe() {
        FileName f = null;
        try {
            f = (FileName) getClass().newInstance();
            f.copyData(this);
            f.copyParameters(this);
        }
        catch (IllegalAccessException ee) {
            System.out.println("Illegal Access: " + ee.getMessage());
        }
        catch (InstantiationException ee) {
            System.out.println("Couldn't be instantiated: " + ee.getMessage());
        }
        return f;
    }

    /**
     * This method does nothing here, but is required by TrianaType.
     *
     * @param source The object being copied from
     */
    public void copyData(TrianaType source) {   // not needed
    }

    /**
     * Method to copy the data for this class. Copies by value, not reference.
     *
     * @param source The object being copied from
     */
    public void copyParameters(TrianaType source) {
        setFile(((FileName) source).getFile());
        if ((((FileName) source).lineNos != null) && (((FileName) source).lineNos.length != 0)) {
            int[] lines = ((FileName) source).lineNos;
            int[] newlines = new int[lines.length];
            for (int i = 0; i < lines.length; ++i)
                newlines[i] = lines[i];
            setLineNumbers(newlines);
        }
    }

    /**
     * This function is used when Triana types want to be able to send their
     * data via an output stream to other programs using strings.  This
     * can be used to implement socket and to run other executables,
     * written in C etc. With ASCII you don'y have to worry about
     * ENDIAN'ness as the conversions are all done via text. This is
     * obviously slower than the binary version since you have to format
     * the input and output within the another program.
     * </p><p>
     * This method must be overridden in every subclass that defines new
     * data or parameters. The overriding method should first call<PRE>
     *      super.outputToStream(dos)
     * </PRE>to get output from superior classes, and then new parameters defined
     * for the current subclass must be output. Moreover, subclasses
     * that first dimension their data arrays must explicitly transfer
     * these data arrays.
     * </p><p>
     *
     * @param dos The data output stream
     */
    public void outputToStream(PrintWriter dos) throws IOException {
        dos.println(fileName);
        if (lineNos != null) {
            dos.println(lineNos.length);
            for (int i = 0; i < lineNos.length; ++i)
                dos.println(lineNos[i]);
        }
    }

    /**
     * This function is used when Triana types want to be able to receive
     * ASCII data from the output of other programs.  This is used to
     * implement socket and to run other executables, written in C etc.
     * With ASCII you don'y have to worry about
     * ENDIAN'ness as the conversions are all done via text. This is
     * obviously slower than the binary version since you have to format
     * the input and output within the another program.
     * </p><p>
     * This method must be overridden in every subclass that defines new
     * data or parameters. The overriding method should first call<PRE>
     *      super.inputFromStream(dis)
     * </PRE>to get input from superior classes, and then new parameters defined
     * for the current subclass must be input. Moreover, subclasses
     * that first dimension their data arrays must explicitly transfer
     * these data arrays.
     * </p><p>
     *
     * @param dis The data input stream
     */
    public void inputFromStream(BufferedReader dis) throws IOException {
        String line = dis.readLine();
        setFile(line);
        int lines = Str.strToInt(dis.readLine());
        if (lines != 0) {
            lineNos = new int[lines];
            for (int i = 0; i < lineNos.length; ++i)
                lineNos[i] = Str.strToInt(dis.readLine());
        }
    }

    /*
     * Method updateObsoletePointers is used to make the new
     * types derived from TrianaType backward-compatible with
     * older types. It must be called by any method that
     * modifies data in dataContainer or in any other variable
     * that replaces a storage location used previously by any
     * type. It must be implemented (over-ridden) in any type
     * that re-defines storage or access methods to any
     * variable. The implementation should assign the
     * new variables to the obsolete ones, and ensure that
     * obsolete access methods retrieve data from the new
     * locations. Any over-riding method should finish
     * with the line
     *       super.updateObsoletePointers;
     */
    protected void updateObsoletePointers() {
        super.updateObsoletePointers();
    }

    public String toString() {
        return getFile();
    }

}




