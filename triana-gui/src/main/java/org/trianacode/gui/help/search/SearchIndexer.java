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
package org.trianacode.gui.help.search;

import java.io.*;
import java.util.Enumeration;
import java.util.Vector;

/**
 * @version     $Revision: 4048 $
 * @date        $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public class SearchIndexer {

    private Vector listeners = null;
    private boolean caseSensitive = false;
    private File searchFile = null;
    private SearchResults results = null;

    public SearchIndexer(File searchFile, boolean caseSensitive) {
        setSearchFile(searchFile);
        setCaseSensitive(caseSensitive);

        listeners = new Vector();
    }

    public SearchIndexer(File searchFile) {
        this(searchFile, false);
    }

    public void addSearchIndexerListener(SearchIndexerListener listener) {
        listeners.addElement(listener);
    }

    public void removeSearchIndexerListener(SearchIndexerListener listener) {
        listeners.removeElement(listener);
    }

    protected void despatchSearchIndexerEvent(SearchIndexerEvent event) {
        SearchIndexerListener listener;

        for (Enumeration enumeration = listeners.elements(); enumeration.hasMoreElements();) {
            listener = (SearchIndexerListener) enumeration.nextElement();
            listener.indexUpdate(event);
        }
    }

    /**
     * Creates an index file from the source which can be used by
     * the search function.  If source is a single file then it is used
     * to generate an index.  If source is a directory then all the
     * files are indexed.
     */
    protected void indexFile(File file, FilenameFilter filter)
            throws IOException {

        // Basically this routine works through the file(s) building
        // a hashtable of all the words in the file(s).  Each one being
        // associated with a vector containing all the file(s) in which
        // that word was found.

        // If it is a directory then lets recurse through all the files
        // which fit the supplied filter

        System.out.println("Indexing: " + file.toString());

        if (file.isDirectory()) {
            String[] fileNames = null;

            // Get a list of the files
            try {
                if (filter != null) {
                    fileNames = file.list(filter);
                }
                else
                    fileNames = file.list();
            }
            catch (SecurityException ex) {
                ex.printStackTrace();
                return;
            }

            // And then parse them
            for (int i = 0; i < fileNames.length; i++) {
                indexFile(new File(file, fileNames[i]), filter);
            }

            return;
        }

        // It's a file and not a directory so let's parse it
        parseFile(file);
    }

    protected void indexFile(File file)
            throws IOException {
        indexFile(file, null);
    }

    protected void parseFile(File file)
            throws IOException {
        BufferedReader in = new BufferedReader(new
                InputStreamReader(new FileInputStream(file)));
        String line;

        while ((line = in.readLine()) != null) {
            parseLine(file, line);
        }

        in.close();
    }

    protected void parseLine(File file, String line) {
        StringBuffer sb = new StringBuffer();
        char[] charArray;
        int ptr;

        // Get an array of the characters in the line
        charArray = new char[line.length()];
        line.getChars(0, line.length(), charArray, 0);

        // Start parsing the line
        ptr = 0;

        for (; ;) {
            if (ptr >= charArray.length) break;

            if (Character.isLetterOrDigit(charArray[ptr])) {
                sb.append(charArray[ptr]);
            }
            else {
                if (sb.length() > 0) {
                    if (caseSensitive) {
                        results.add(sb.toString(), file);
                    }
                    else {
                        results.add(sb.toString().toLowerCase(), file);
                    }
                    sb = new StringBuffer();
                }
            }

            ptr++;
        }

        if (sb.length() > 0) {
            if (isCaseSensitive()) {
                results.add(sb.toString(), file);
            }
            else {
                results.add(sb.toString().toLowerCase(), file);
            }
        }
    }

    public SearchResults getSearchResults() {
        results = new SearchResults();

        try {
            indexFile(getSearchFile());
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        return results;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public File getSearchFile() {
        return searchFile;
    }

    public void setSearchFile(File searchFile) {
        this.searchFile = searchFile;
    }
}
