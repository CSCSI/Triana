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

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

/**
 * @version     $Revision: 4048 $
 * @date        $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public class HTMLSearchIndexer extends SearchIndexer {

    private char[] html_comment_on;
    private char[] html_comment_off;

    private HTMLSearchResults results;

    private StringBuffer tag = new StringBuffer();
    private boolean inComment = false;
    private boolean inTag = false;

    // Part of nasty fudge
    private StringBuffer title = new StringBuffer();
    private boolean inTitle = false;

    private HTMLDocumentInfo docInfo;

    class HTMLFilenameFilter implements FilenameFilter {
        public boolean accept(File dir, String file) {
            String ext;
            int index;

            // Recurse in to directories
            if ((new File(dir, file)).isDirectory()) return true;

            // Ignore files without extensions
            if ((index = file.lastIndexOf(".")) < 0) return false;

            // Get the extension
            ext = file.substring(index + 1).toLowerCase();

            if (ext.equals("html")) return true;
            if (ext.equals("htm")) return true;

            return false;
        }
    }

    public final static void main(String[] args) {
        try {
            /*
            HTMLSearchResults results = HTMLSearchResults.loadHTMLSearchResults(new File("help.idx"));
            Vector vector = results.get(args[0]);
            System.out.println(vector.toString());
            */
            HTMLSearchIndexer indexer = new HTMLSearchIndexer(new File(args[0]));
            HTMLSearchResults results = indexer.getHTMLSearchResults();
            results.save(new File("help.idx"));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        //This was removed due to a bug in ant (the build tool).
        //With this in, it ant will exit after this has been run.
        //System.exit(0);
    }

    public HTMLSearchIndexer(File searchFile, boolean caseSensitive) {
        super(searchFile, caseSensitive);
        initArrays();
    }

    public HTMLSearchIndexer(File searchFile) {
        super(searchFile);
        initArrays();
    }

    private void initArrays() {
        html_comment_on = stringToArray("<--");
        html_comment_off = stringToArray("-->");
    }

    protected char[] stringToArray(String string) {
        char[] array = new char[string.length()];
        string.getChars(0, string.length(), array, 0);
        return array;
    }

    protected void parseFile(File file) throws IOException {
        docInfo = new HTMLDocumentInfo(file, "");
        super.parseFile(file);

        if (!title.toString().equals("Untitled")) {
            docInfo.setTitle(title.toString());
        }
        else {
            docInfo.setTitle(file.getName());
        }

        title = new StringBuffer();
    }

    protected boolean subArrayEquals(char[] bigArray, int offset,
                                     char[] smallArray) {
        if (bigArray.length < (smallArray.length + offset)) return false;

        for (int i = smallArray.length - 1; i >= 0; i--) {
            if (bigArray[i + offset] != smallArray[i]) return false;
        }

        return true;
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

            if (inTag) {
                if (charArray[ptr] == '>') {
                    String tagString = tag.toString().toLowerCase();

                    // Fudge time - quick and nasty solution
                    if (tagString.startsWith("title")) {
                        inTitle = true;
                    }
                    else if (tagString.startsWith("/title")) {
                        inTitle = false;
                    }

                    inTag = false;
                }
                else {
                    tag.append(Character.toLowerCase(charArray[ptr]));
                }

                ptr++;
            }
            else if (inTitle) {
                // Nasty fudge time - must improve
                if (charArray[ptr] == '<') {
                    inTag = true;
                    tag = new StringBuffer();
                }
                else
                    title.append(charArray[ptr]);

                ptr++;
            }
            else {
                if (Character.isLetterOrDigit(charArray[ptr])) {
                    if (!inTag && !inComment) sb.append(charArray[ptr]);
                    ptr++;
                }
                else {
                    if (sb.length() > 0) {
                        if (isCaseSensitive()) {
                            results.add(sb.toString(), docInfo);
                        }
                        else {
                            results.add(sb.toString().toLowerCase(), docInfo);
                        }
                        sb = new StringBuffer();
                    }

                    if (subArrayEquals(charArray, ptr, html_comment_on)) {
                        inComment = true;
                        ptr += html_comment_on.length;
                    }
                    else if (subArrayEquals(charArray, ptr, html_comment_off)) {
                        inComment = false;
                        ptr += html_comment_off.length;
                    }
                    else if (charArray[ptr] == '<') {
                        inTag = true;
                        tag = new StringBuffer();
                        ptr++;
                    }
                    else {
                        ptr++;
                    }
                }
            }
        }

        if (sb.length() > 0) {
            if (isCaseSensitive()) {
                results.add(sb.toString(), docInfo);
            }
            else {
                results.add(sb.toString().toLowerCase(), docInfo);
            }
        }
    }

    public HTMLSearchResults getHTMLSearchResults() {
        HTMLFilenameFilter filter = new HTMLFilenameFilter();
        results = new HTMLSearchResults();

        try {
            indexFile(getSearchFile(), filter);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        return results;
    }
}

