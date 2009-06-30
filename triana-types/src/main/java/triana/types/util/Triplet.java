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

import java.io.Serializable;

/**
 * Triplet is an Object containing three numbers that can generate
 * a uniform sequence of numbers, which can be used to represent
 * an array index or an independent variable in, say, graphical
 * applications.
 * The elements of Triplet are numbers giving the integer
 * length of the sequence and the doubles start and step of the
 * sequence. Various methods are provided
 * for creating and reading Triplets and converting them to equivalent
 * arrays of numbers.
 *
 * @author      Bernard Schutz
 * @created     28 August 2000
 * @version     $Revision: 4048 $
 * @date        $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public class Triplet extends Object implements Serializable {
    private int length;
    private double start;
    private double step;

    /**
     * Constructs an empty Triplet.
     * capacity and the specified capacityIncrement.
     */
    public Triplet() {
    }

    /**
     * Constructs a Triplet with the specified <i>length</i>, <i>start</i>, and <i>step</i>.
     * @param l The number of elements in the uniformly spaced set.
     * @param st The first value of the set.
     * @param sp The interval (uniform) between values in the set.
     */
    public Triplet(int l, double st, double sp) {
        length = l;
        start = st;
        step = sp;
    }

    /**
     * Constructs a Triplet with the specified <i>length</i> and default values
     * of <i>start</i> (0) and <i>step</i> (1).
     * @param l The number of elements in the uniformly spaced set.
     */
    public Triplet(int l) {
        length = l;
        start = 0.0;
        step = 1.0;
    }

    /**
     * Constructs a Triplet with the specified <i>length</i> and starting value,
     * and with a default value of the <i>step</i> (1).
     * @param l The number of elements in the uniformly spaced set.
     * @param st The first value of the set.
     */
    public Triplet(int l, double st) {
        length = l;
        start = st;
        step = 1.0;
    }

    /**
     * Constructs a Triplet from an array of doubles that should
     * contain a uniformly spaced set of values. Users are
     * responsible for testing the array using method <i>testUniform</i>, below.
     */
    public Triplet(double[] array) {
        length = array.length;
        start = array[0];
        step = array[1] - array[0];
    }


    /**
     * Returns the <i>length</i> element of the Triplet.
     */
    public int getLength() {
        return length;
    }

    /**
     * Returns the <i>start</i> element of the Triplet.
     */
    public double getStart() {
        return start;
    }

    /**
     * Returns the <i>step</i> element of the Triplet.
     */
    public double getStep() {
        return step;
    }

    /**
     * Returns the last element of the sequence generated by the Triplet.
     */
    public double getLast() {
        return start + step * (length - 1);
    }

    /**
     * Sets the <i>length</i> element of the Triplet.
     */
    public void setLength(int l) {
        length = l;
    }

    /**
     * Sets the <i>start</i> element of the Triplet.
     */
    public void setStart(double st) {
        start = st;
    }

    /**
     * Sets the <i>step</i> element of the Triplet.
     */
    public void setStep(double sp) {
        step = sp;
    }

    /**
     * Returns an array of doubles generated by the Triplet.
     */
    public double[] convertToArray() {
        double[] values = new double[length];
	if (length > 0) {
	    values[0] = start;
	    if (length > 1) for (int k = 1; k < length; k++) values[k] = values[k - 1] + step;
	}
        return values;
    }

    /**
     * Class method that tests a one-dimensional array to see if it is uniform
     * and can therefore be converted to a Triplet.
     */
    public static boolean testUniform(double[] values) {
        int length = values.length;
        if (length < 3) return true;
        double step = values[1] - values[0];
        double newStep = step;
        for (int k = 2; k < length; k++) {
            newStep = values[k] - values[k - 1];
            if (newStep != step) return false;
        }
        return true;
    }

    /**
     * Class method that converts a uniformly spaced one-dimensional
     * array of doubles to a Triplet. This does not test whether the
     * array is uniform: use method <i>testUniform</i> to do that. It just uses
     * the first values and length of the array to generate the Triplet.
     */
    public static Triplet convertToTriplet(double[] values) {
        int length = values.length;
        if (length == 1) return new Triplet(1, values[0], 0);
        return new Triplet(length, values[0], values[1] - values[0]);
    }

    /**
     * @return a string representation of this Triplet in the form :- <br>
     * el0 el1 el2 \n </p>
     */
    public final String toAString() {
        String s = String.valueOf(length) + " " + String.valueOf(start) + " " + String.valueOf(step) + "\n";
        return s;
    }

    /**
     * @return a string representation of this Triplet in the form :- <br>
     * el0 \n el1 \n el2 \n </p>
     */
    public final String toAColumn() {
        String s = String.valueOf(length) + "\n" + String.valueOf(start) + "\n" + String.valueOf(step) + "\n";
        return s;
    }

    /**
     * Copy by value not by reference
     */
    public Triplet copy() {
        Triplet tr = new Triplet(length, start, step);
        return tr;
    }
}









