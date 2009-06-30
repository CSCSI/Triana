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
public class SearchResults implements Serializable {
    protected Vector hashVector;

    SearchResults() {
        this.hashVector = new Vector(0x4000);
    }

    void add(String word, Object object) {
        KeyValuePair element;
        Vector vector;

        // See if the word is already in the list.
        // If so add the object for that word
        for (Enumeration enumeration = hashVector.elements(); enumeration.hasMoreElements();) {
            element = (KeyValuePair) enumeration.nextElement();

            if (element.key.equals(word)) {
                if (!element.vector.contains(object)) element.vector.addElement(object);
                return;
            }
        }

        // Word isn't in the list so we need to add it
        vector = new Vector();
        vector.addElement(object);

        // Find it's alphabetical position to add
        if (hashVector.size() != 0) {
            KeyValuePair prevElement, nextElement;

            for (int i = 0; i < hashVector.size() - 1; i++) {
                prevElement = (KeyValuePair) hashVector.elementAt(i);
                nextElement = (KeyValuePair) hashVector.elementAt(i + 1);
                if ((prevElement.key.compareTo(word) < 0) &&
                        (nextElement.key.compareTo(word) > 0)) {
                    hashVector.insertElementAt(new KeyValuePair(word, vector), i + 1);
                    return;
                }
            }
        }

        // Add to end as default
        hashVector.addElement(new KeyValuePair(word, vector));
    }

    /*

    A more efficient insert routine that currently doesn't work!

    protected void orderedInsert(Vector hashVector, String word, Vector vector) {
      orderedInsert(hashVector, word, vector, 0, hashVector.size() - 1);
    }

    protected void orderedInsert(Vector hashVector, String word, Vector vector,
      int low, int high) {
      KeyValuePair element;

      if (high != low) {
        int mid = low + ((high - low) / 2);

        element = (KeyValuePair)hashVector.elementAt(mid);
        if (element.key.compareTo(word) < 0) {
          orderedInsert(hashVector, word, vector, low, mid);
        } else if (element.key.compareTo(word) > 0) {
          orderedInsert(hashVector, word, vector, mid, high);
        }

        return;
      }

      element = (KeyValuePair)hashVector.elementAt(low);

      if (element.key.compareTo(word) > 0) {
        if (low > 0) {
          hashVector.insertElementAt(new KeyValuePair(word, vector), low - 1);
        } else {
          hashVector.insertElementAt(new KeyValuePair(word, vector), 0);
        }
      } else {
        hashVector.insertElementAt(new KeyValuePair(word, vector), low + 1);
      }
    }
    */

    protected Vector get(String word) {
        KeyValuePair element;

        for (Enumeration enumeration = hashVector.elements(); enumeration.hasMoreElements();) {
            element = (KeyValuePair) enumeration.nextElement();

            if (element.key.equals(word)) {
                return element.vector;
            }
        }

        return null;
    }

    public String[] getWordArray() {
        KeyValuePair element;
        String[] array = null;

        if (hashVector.size() > 0) {
            array = new String[hashVector.size()];

            for (int i = 0; i < hashVector.size(); i++) {
                element = (KeyValuePair) hashVector.elementAt(i);
                array[i] = element.key;
            }
        }

        return array;
    }

    public File[] getDocumentArray(String word) {
        Vector vector = null;
        File[] files = null;

        if ((vector = get(word)) == null) return null;

        if (vector.size() > 0) {
            files = new File[vector.size()];

            for (int i = 0; i < vector.size(); i++)
                files[i] = (File) hashVector.elementAt(i);
        }

        return files;
    }

    public void save(File file) {
        hashVector.trimToSize();

        try {
            ObjectOutputStream out = new ObjectOutputStream(new
                    FileOutputStream(file));
            out.writeObject(this);
            out.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static SearchResults load(File file) {
        SearchResults sr = null;

        try {
            ObjectInputStream in = new ObjectInputStream(new
                    FileInputStream(file));
            sr = (SearchResults) in.readObject();
            in.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        return sr;
    }

    public String toString() {
        return hashVector.toString();
    }
}

class KeyValuePair implements Serializable {
    String key;
    Vector vector;

    KeyValuePair(String key, Vector vector) {
        this.key = key;
        this.vector = vector;
    }
}
