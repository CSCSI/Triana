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


/**
 * Const is a wrapper for basic base number types e.g. int, float, long,
 * short and double. It stores a single real or complex number of one of
 * those types.
 *
 * @author      Ian Taylor
 * @author      Bernard Schutz
 * @created     13 January 2001
 * @version     $Revision: 4048 $
 * @date        $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public class Const extends Number implements Arithmetic, Comparable {

    private double real = 0;
    private double imag = 0;


    /**
     * Creates a new Const with default 0 value
     */
    public Const() {
    }

    /**
     * Creates a new real Const for double input.
     *
     * @param num the double value.
     */
    public Const(double num) {
        this.real = num;
    }

    /**
     * Creates a new complex Const from a number input.
     *
     * @param number the number value.
     */
    public Const(Number number) {
        if ((number instanceof Const) && (((Const) number).isComplex())) {
            this.real = ((Const) number).getReal();
            this.imag = ((Const) number).getImag();
        } else
            this.real = number.doubleValue();
    }

    /**
     * Creates a new Const from the specified string, which should be of the
     * format "1.0", or "1.0 + 1.0i" for complex numbers.
     *
     * @param str the const string
     */
    public Const(String str) throws NumberFormatException {
        this.real = getReal(str);
        this.imag = getImag(str);
    }

    /**
     * Creates a new complex Const for double input.
     *
     * @param r the real double value.
     * @param i the imaginary double value.
     */
    public Const(double r, double i) {
        this.real = r;
        this.imag = i;
    }


    /**
     * @return a string representing the complex number
     */
    private static String getComplexString(double real, double imag) {
        if ((imag == Double.NaN) || (imag == 0))
            return String.valueOf(real);
        else
            return String.valueOf(real) + " + " + String.valueOf(imag) + "i";
    }

    /**
     * @return the real part from a string
     */
    private static double getReal(String str) throws NumberFormatException {
        if (str.indexOf('+') == -1)
            return new Double(str.trim()).doubleValue();
        else
            return new Double(str.substring(0, str.indexOf('+')).trim()).doubleValue();
    }

    /**
     * @return the real part from a string
     */
    private static double getImag(String str) throws NumberFormatException {
        if ((str.indexOf('+') == -1) || (str.indexOf('i') == -1))
            return 0;
        else
            return new Double(str.substring(str.indexOf('+'), str.indexOf('i')).trim()).doubleValue();
    }


    /**
     * @return boolean <i>True</i> if the imaginary part is non-zero, <i>false</i> otherwise
     */
    public boolean isComplex() {
        return (getImag() != Double.NaN) && (getImag() != 0);
    }

    /**
     * @return the real part of the Const
     */
    public double getReal() {
        return real;
    }

    /**
     * @return the imaginary part of the Const
     */
    public double getImag() {
        return imag;
    }


    /**
     * Set the real part to the given double.
     */
    public void setReal(double r) {
        this.real = r;
    }

    /**
     * Set the imaginary part to the given double.
     */
    public void setImag(double i) {
        this.imag = i;
    }

    /**
     * Set the real part and imaginary parts to the given double inputs.
     */
    public void setComplex(double r, double i) {
        this.real = r;
        this.imag = i;
    }


    /**
     * Tests that the given object is a Const. No test needed on the contents.
     */
    public boolean isCompatible(Object obj) {
        return (obj instanceof Number);
    }


    /**
     * Returns <i>true</i> if the specified Object has the same value as
     * the Const (the Object must be an instance of number).
     *
     * @param num The object to be tested
     * @return <I>True</I> if the given Object is a Const whose contents equal those of the current one
     */
    public boolean equals(Object num) {
        if (!(num instanceof Number)) return false;

        Const cst = new Const((Number) num);

        return (cst.getReal() == getReal()) && (cst.getImag() == getImag());
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.<p>
     */
    public int compareTo(Object num) {
        if (!(num instanceof Number))
            throw (new ClassCastException("Cannot compare Const with " + num.getClass().getName()));

        Const cst = new Const((Number) num);

        if (getReal() < cst.getReal())
            return -1;
        else if (getReal() > cst.getReal())
            return 1;
        else if (getImag() < cst.getImag())
            return -1;
        else if (getImag() > cst.getImag())
            return 1;
        else
            return 0;
    }

    /**
     * Adds this object and the given number
     *
     * @param obj the object containing the number for the arithmetic
     * @return the result of the addition
     */
    public Arithmetic add(Object obj) throws ClassCastException {
        if (obj instanceof Number) {
            Const cst = new Const((Number) obj);

            cst.setReal(getReal() + cst.getReal());
            cst.setImag(getImag() + cst.getImag());

            return cst;
        } else
            throw(new ClassCastException("Invalid arithmetic: Cannot add Const and " + obj.getClass().getName()));
    }

    /**
     * Subtracts the number from this object
     *
     * @param obj the object containing the number for the arithmetic
     * @return the result of the subtraction
     */
    public Arithmetic subtract(Object obj) throws ClassCastException {
        if (obj instanceof Number) {
            Const cst = new Const((Number) obj);

            cst.setReal(getReal() - cst.getReal());
            cst.setImag(getImag() - cst.getImag());

            return cst;
        } else
            throw(new ClassCastException("Invalid arithmetic: Cannot subtract Const and " + obj.getClass().getName()));
    }

    /**
     * Multiplies this objectand the give number
     *
     * @param obj the object containing the number for the arithmetic
     * @return the result of the multiplication
     */
    public Arithmetic multiply(Object obj) throws ClassCastException {
        if (obj instanceof Number) {
            Const cst = new Const((Number) obj);

            double or = cst.getReal();
            double oi = cst.getImag();
            double tr = getReal();
            double ti = getImag();

            cst.setReal(tr * or - ti * oi);
            cst.setImag(tr * oi + ti * or);

            return cst;
        } else
            throw(new ClassCastException("Invalid arithmetic: Cannot multipy Const and " + obj.getClass().getName()));
    }

    /**
     * Divides this object by the given object
     *
     * @param obj the object containing the number for the arithmetic
     * @return the result of the division
     */
    public Arithmetic divide(Object obj) throws ClassCastException {
        if (obj instanceof Number) {
            Const cst = new Const((Number) obj);

            double or = cst.getReal();
            double oi = cst.getImag();
            double mag = or * or + oi * oi;
            or = or / mag;
            oi = oi / mag;
            double tr = getReal();
            double ti = getImag();

            cst.setReal(tr * or + ti * oi);
            cst.setImag(-tr * oi + ti * or);

            return cst;
        } else
            throw(new ClassCastException("Invalid arithmetic: Cannot divide Const by " + obj.getClass().getName()));
    }

    /**
     * Returns the value of the specified number as an <code>int</code>.
     * This may involve rounding or truncation.
     *
     * @return  the numeric value represented by this object after conversion
     *          to type <code>int</code>.
     */
    public int intValue() {
        return new Double(getReal()).intValue();
    }

    /**
     * Returns the value of the specified number as a <code>long</code>.
     * This may involve rounding or truncation.
     *
     * @return  the numeric value represented by this object after conversion
     *          to type <code>long</code>.
     */
    public long longValue() {
        return new Double(getReal()).longValue();
    }

    /**
     * Returns the value of the specified number as a <code>float</code>.
     * This may involve rounding.
     *
     * @return  the numeric value represented by this object after conversion
     *          to type <code>float</code>.
     */
    public float floatValue() {
        return new Double(getReal()).floatValue();
    }

    /**
     * Returns the value of the specified number as a <code>double</code>.
     * This may involve rounding.
     *
     * @return  the numeric value represented by this object after conversion
     *          to type <code>double</code>.
     */
    public double doubleValue() {
        return getReal();
    }


    /**
     * @return  a string representation of the object.
     */
    public String toString() {
        if (isComplex())
            return getComplexString(real, imag);
        else
            return String.valueOf(real);
    }

}