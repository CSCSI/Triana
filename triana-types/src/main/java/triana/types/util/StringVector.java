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
package triana.types.util;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * StringVector is a class that extends ArrayList in a convenient way to  deal only with strings. It provides has
 * shortcut names for the methods $elementAt^ (=$at^), $firstElement^ (=$first^) and $lastElement^ (=$last^).
 *
 * @author Ian Taylor
 * @version $Revision: 4048 $
 */
public class StringVector extends ArrayList {

    /**
     * Constructs an empty StringVector with the specified storage capacity.
     *
     * @param initialCapacity the initial storage capacity of the vector
     */
    public StringVector(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Constructs a StringVector from a String by splitting the String by new line and adding them one by one to the
     * StringVector.
     */
    public StringVector(String str) throws Exception {
        this(10);
        BufferedReader br = new BufferedReader(new StringReader(str));
        String st;
        while ((st = br.readLine()) != null) {
            add(st);
        }
        br.close();
    }

    /**
     * Constructs an empty vector.
     */
    public StringVector() {
        this(10);
    }

    /**
     * Returns the String element at the specified index.
     *
     * @param index the index of the desired element
     * @throws ArrayIndexOutOfBoundsException If an invalid index was given.
     */
    public final String at(int index) {
        Object o = super.get(index);
        return o instanceof String ? (String) o : null;
    }

    /**
     * Returns the first element of the sequence.
     *
     * @throws NoSuchElementException If the sequence is empty.
     */
    public final String first() {
        return (String) super.get(0);
    }

    /**
     * Returns the last element of the sequence.
     *
     * @throws NoSuchElementException If the sequence is empty.
     */
    public final Object last() {
        return (String) super.get(size() - 1);
    }

    /**
     * @return a string representation of this vector in the form :- <br> el1 el2 el3 el4 ..... el(size-1) </p>
     */
    public final String toAString() {
        String s = "";
        for (int i = 0; i < size(); ++i) {
            s = s + (String) get(i) + " ";
        }
        s = s + "\n";
        return s;
    }

    /**
     * @return a string representation of this vector in the form :- <br> el1 <br> el2 <br> el3 <br> el4 <br> .....
     *         el(size-1)  </p>
     */
    public final String toNewLineString() {
        String s = "";
        for (int i = 0; i < size(); ++i) {
            s = s + (String) get(i) + "\n";
        }
        return s;
    }

    /**
     * @return a double array representation of this vector. Only useful when you know that the StringVector contains a
     *         a list of Strings which you know are all doubles. </p> el1 el2 el3 el4 ..... el(size-1) \n </p>
     */
    public final double[] toDoubles() {
        double[] d = new double[size()];

        for (int i = 0; i < size(); ++i) {
            d[i] = Str.strToDouble(((String) get(i)).trim());
        }

        return d;
    }

    /**
     * @return an int array representation of this vector. Only useful when you know that the StringVector contains a a
     *         list of Strings which you know are all integers. </p> el1 el2 el3 el4 ..... el(size-1) \n </p>
     */
    public final int[] toInts() {
        int[] d = new int[size()];

        for (int i = 0; i < size(); ++i) {
            d[i] = Str.strToInt(((String) get(i)).trim());
        }

        return d;
    }

    /**
     * @return a StringVector that is a proper copy (not just a reference) of the current StringVector.
     */
    public StringVector copy() {
        StringVector sv = new StringVector(this.size());
        for (int j = 0; j < this.size(); j++) {
            sv.add(Str.copy((String) this.get(j)));
        }
        return sv;
    }

    /**
     * copies this string vector into the object array
     */
    public void copyInto(Object[] objects) {
        toArray(objects);
    }


    public Enumeration elements() {
        return new StringTokenizer(toAString());
    }

    /**
     * Sorts the string vector into ascending alpabetical order.
     */
    public void sort() {
        Collections.sort(this);
    }

    /**
     * Adds the elements of the given StringVector to the end of the present one, by reference.
     *
     * @param s The StringVector to be appended
     */
    public void append(StringVector s) {
        ensureCapacity(size() + s.size());
        for (int i = 0; i < s.size(); i++) {
            add(s.at(i));
        }
    }

// For backward compatibility ONLY, try to update classes

    public void addElement(Object el) {
        add(el);
    }

    public Object elementAt(int el) {
        return get(el);
    }

    public void removeElementAt(int i) {
        remove(i);
    }

    public void setElementAt(String str, int i) {
        set(i, str);
    }

    public int indexOf(Object el, int s) {
        for (int i = s; s < size(); ++i) {
            if (at(i).indexOf((String) el) != -1) {
                return i;
            }
        }

        return -1;
    }

    public void removeAllElements() {
        if (size() == 0) {
            return;
        }
        removeRange(0, size() - 1);
    }
}
