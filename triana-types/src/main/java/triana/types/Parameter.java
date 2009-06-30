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
 * Parameter is a type which encapsulates a String which stores the contents
 * of a Parameter so that it can be exported from or imported into a OldUnit.
 * Parameters can be of various type <i>e.g.</i> a string, a double value, an
 * integer, or a color.  All parameters are stored as a String representation
 * internally and converted back and forth on input and output.
 * </p><p>
 * To extend this type to represent
 * new kinds of parameters, the programmer simply needs to convert
 * between the parameter type and
 * a String and to provide relevant methods for doing this.
 *</p><p>
 * Parameter inherits directly from TrianaType. Do not confuse the Class
 * Parameter with the term "parameter" used in TrianaType data types
 * or in Triana units. In data types the term refers to data associated
 * with types that is not stored in the <i>dataContainer</i> Hashtable. In
 * Units the term refers to data controlling the operation of the OldUnit
 * that the user can set using the user interface (parameter window) of
 * the OldUnit. The Class is used to transfer OldUnit parameters
 * (not data-type parameters) from one OldUnit to another.
 *
 * @see TrianaType
 * @author      Ian Taylor
 * @created     28 August 2000
 * @version     $Revision: 4048 $
 * @date        $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public class Parameter extends TrianaType {

    /**
     * String to hold the data of this object.
     */
    private static String parameter = "param";

    /**
     * Creates a null Parameter.
     */
    public Parameter() {
    }

    /**
     * Creates a new Parameter with the contents set to the specified String.
     *
     * @param param the String containing the contents of the Parameter.
     */
    public Parameter(Object param) {
        setParameter(param);
    }

    /**
     * Creates a new Parameter for a short.
     *
     * @param param the value
     */
    public Parameter(short param) {
        setParameter(String.valueOf(param));
    }

    /**
     * Creates a new Parameter for an int.
     *
     * @param param the value
     */
    public Parameter(int param) {
        setParameter(String.valueOf(param));
    }

    /**
     * Creates a new Parameter for a float.
     *
     * @param param the value
     */
    public Parameter(float param) {
        setParameter(String.valueOf(param));
    }

    /**
     * Creates a new Parameter for a double.
     *
     * @param param the value
     */
    public Parameter(double param) {
        setParameter(String.valueOf(param));
    }

    /**
     * Creates a new Parameter for a char.
     *
     * @param param the value
     */
    public Parameter(char param) {
        setParameter(String.valueOf(param));
    }

    /**
     * Creates a new Parameter for a boolean.
     *
     * @param param the value
     */
    public Parameter(boolean param) {
        setParameter(String.valueOf(param));
    }


    /**
     * Returns a reference to the contents of this Parameter.
     *
     * @return The contents of this parameter
     */
    public Object getParameter() {
        return getFromContainer(parameter);
    }


    /**
     * Sets the given Object to be the contents of the parameter object.
     *
     * @param newParam The new contents
     */
    protected void setParameter(Object newParam) {
        if (newParam != null)
            insertIntoContainer(parameter, newParam);
        else
            deleteFromContainer(parameter);
    }

    /**
     * Returns <i>true</i> if the given Object is of Parameter
     * type and has the same contents as this object.
     *
     * @param parameter The object being compared to this one
     * @return boolean <i>True</i> if given object has same contents as this one
     */
    public boolean equals(Object parameter) {
        if (!(parameter instanceof Parameter))
            return false;

        Parameter d = (Parameter) parameter;

        if (!getParameter().equals(d.getParameter()))
            return false;

        return true;
    }

    /**
     * This is one of the most important methods of Triana data.
     * types. It returns a copy of the type invoking it. This <b>must</b>
     * be overridden for every derived data type derived. If not, the data
     * cannot be copied to be given to other units. Copying must be done by
     * value, not by reference.
     * </p><p>
     * To override, the programmer should not invoke the <i>super.copyMe</i> method.
     * Instead, create an object of the current type and call methods
     * <i>copyData</i> and <i>copyParameters</i>. If these have been written correctly,
     * then they will do the copying.  The code should read, for type YourType:
     * <PRE>
     *        YourType y = null;
     *        try {
     *            y = (YourType)getClass().newInstance();
     *	          y.copyData( this );
     *	          y.copyParameters( this );
     *            y.setLegend( this.getLegend() );
     *            }
     *        catch (IllegalAccessException ee) {
     *            System.out.println("Illegal Access: " + ee.getMessage());
     *            }
     *        catch (InstantiationException ee) {
     *            System.out.println("Couldn't be instantiated: " + ee.getMessage());
     *            }
     *        return y;
     * </PRE>
     * </p><p>
     * The copied object's data should be identical to the original. The
     * method here modifies only one item: a String indicating that the
     * object was created as a copy is added to the <i>description</i>
     * StringVector.
     *
     * @return TrianaType Copy by value of the current Object except for an
     updated <i>description</i>
     */
    public TrianaType copyMe() {
        Parameter d = null;
        try {
            d = (Parameter) getClass().newInstance();
            d.copyData(this);
            d.copyParameters(this);
        }
        catch (IllegalAccessException ee) {
            System.out.println("Illegal Access: " + ee.getMessage());
        }
        catch (InstantiationException ee) {
            System.out.println("Couldn't be instantiated: " + ee.getMessage());
        }
        return d;
    }

    /**
     * Copies the Parameter by value from the given source, not by reference.
     *
     * @param source The Parameter object being copied
     */
    public void copyData(TrianaType source) {
        setParameter(((Parameter) source).getParameter());
    }


    /**
     * Copies modifiable parameters from the given source.
     *
     * @param source The Parameter object being copied
     */
    public void copyParameters(TrianaType source) {
    }


    public String toString() {
        return getParameter().toString();
    }



}



