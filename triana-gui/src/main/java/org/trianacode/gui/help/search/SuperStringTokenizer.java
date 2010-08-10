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

import java.util.Vector;

/**
 * @version $Revision: 4048 $
 */
public class SuperStringTokenizer {
    protected Vector delims;
    protected char[] charArray;
    protected boolean returnDelims;
    protected int ptr = 0;
    protected Object[] delimArray = null;

    public SuperStringTokenizer(String string, boolean returnDelims) {
        charArray = new char[string.length()];
        string.getChars(0, string.length(), charArray, 0);
        delims = new Vector();
        this.returnDelims = returnDelims;
    }

    public SuperStringTokenizer(String string) {
        this(string, false);
    }

    protected boolean subArrayEquals(char[] bigArray, int offset,
                                     char[] smallArray) {
        if (bigArray.length < (smallArray.length + offset)) {
            return false;
        }

        for (int i = smallArray.length - 1; i >= 0; i--) {
            if (bigArray[i + offset] != smallArray[i]) {
                return false;
            }
        }

        return true;
    }

    public void addDelimiter(String delim) {
        char[] delimChars = new char[delim.length()];
        delim.getChars(0, delim.length(), delimChars, 0);
        delims.addElement(delimChars);
    }

    public boolean hasMoreTokens() {
        return ptr < charArray.length;
    }

    public String nextToken() {
        StringBuffer sb = new StringBuffer();

        if (delimArray == null) {
            // Use the JDK1.1 version - toArray() is JDK1.2
            delimArray = new Object[delims.size()];
            delims.copyInto(delimArray);
        }

        for (; ;) {
            // Check we are within bounds
            if (ptr >= charArray.length) {
                break;
            }

            // Work through the delimiters
            for (int i = 0; i < delimArray.length; i++) {
                // If it's a delimiter
                if (subArrayEquals(charArray, ptr, (char[]) delimArray[i])) {

                    // Then return the token if there is one
                    if (sb.length() > 0) {
                        return sb.toString();
                    }

                    // Go past the delimiter
                    ptr += ((char[]) delimArray[i]).length;

                    // or if we are returning found delimiters return it
                    if (returnDelims) {
                        return new String((char[]) delimArray[i]);
                    }

                    // or get next token via recursion (this happens if there
                    // are two delimiters in a row)
                    return nextToken();
                }
            }

            sb.append(charArray[ptr++]);
        }

        return sb.toString();
    }

    public boolean isDelimiter(String delim) {
        char[] array = new char[delim.length()];
        delim.getChars(0, delim.length(), array, 0);

        if (delimArray == null) {
            // Use the JDK1.1 version - toArray() is JDK1.2
            delimArray = new Object[delims.size()];
            delims.copyInto(delimArray);
        }

        for (int i = 0; i < delimArray.length; i++) {
            if (subArrayEquals(array, 0, (char[]) delimArray[i])) {
                return true;
            }
        }

        return false;
    }
}
