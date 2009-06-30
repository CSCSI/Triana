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
 * Arithmetic is an Interface that should be implemented by any
 * Triana data types that need to define standard arithmetic:
 * adding, subtracting, multiplying, and dividing
 * the current object by another. The Arithmetic interface also provides
 * a compatibility test: <i>isCompatible</i> ensures that an
 * object to be combined with the current object has an acceptable type
 * and contents, as decided by the programmer for the type that implements
 * the interface. The interface also provides the test <i>equals</i>
 * which tests for strict equality of the argument and current objects.
 * Programmers should normally implement the interface in such a way that
 * arithmetic is provided at least between objects of the same class and with
 * objects of the class Const.
 *
 * @see TrianaType
 * @see GraphType
 * @see Const
 *
 * @author      Ian Taylor
 * @author      Bernard Schutz
 * @created     20 August 2000
 * @version     $Revision: 4048 $
 * @date        $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public interface Arithmetic {


    /**
     * Tests the argument object to determine if
     * it makes sense to perform arithmetic operations between
     * it and the current object.  The definition of compatibility
     * is up to the programmer who implements the interface, but it should
     * test not only that the object is of a suitable type but also that
     * its data are compatible: arrays of the same size, etc. It can be
     * used in the arithmetic methods to provide a graceful exit if the
     * two objects cannot sensibly be combined.
     *
     * @param obj The data object to be compared with the current one
     * @return boolean True if the object can be combined with the current one
     */
    public boolean isCompatible(Object obj);


    /**
     * Tests if the argument object is equal to the
     * current object. The definition of "equal" is up to the programmer
     * who implements the interface.
     *
     * @param obj The data object to be compared with the current one
     * @return boolean True if the object equals the current one
     */
    public boolean equals(Object obj);

    /**
     * Adds the argument object to the current object.
     * Users should invoke method <i>isCompatible</i> to test whether this
     * operation makes sense.
     *
     * @param obj The new data object to be added to the current one
     * @return the object representing result of the addition. The object
     * should be the same type as the original current.
     *
     * @throws ClassCastException if addition with the specified object is
     * not valid
     */
    public Arithmetic add(Object obj) throws ClassCastException;

    /**
     * Subtracts the argument object from the current object.
     * Users should invoke method <i>isCompatible</i> to test whether this
     * operation makes sense.
     *
     * @param obj The new data object to be subtracted from the current one
     * @return the object representing result of the subtraction. The object
     * should be the same type as the original current.
     *
     * @throws ClassCastException if subtraction with the specified object is
     * not valid
     */
    public Arithmetic subtract(Object obj) throws ClassCastException;

    /**
     * Multiplies the current object by the argument object.
     * It should invoke method <i>isCompatible</i> to test whether this
     * operation makes sense.
     *
     * @param obj The new data object to be multiplied into the current one
     * @return the object representing result of the multiplication. The object
     * should be the same type as the original current.
     *
     * @throws ClassCastException if multiplication with the specified object is
     * not valid
     */
    public Arithmetic multiply(Object obj) throws ClassCastException;

    /**
     * Divides the current object by the argument object.
     * It should invoke method <i>isCompatible</i> to test whether this
     * operation makes sense.
     *
     * @param obj The new data object to be divided into the current one
     * @return the object representing result of the division. The object
     * should be the same type as the original current.
     *
     * @throws ClassCastException if division with the specified object is
     * not valid
     */
    public Arithmetic divide(Object obj) throws ClassCastException;

}





