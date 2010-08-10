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

import java.lang.reflect.Array;

import triana.types.util.Triplet;

/**
 * Curve is the basic class derived from GraphType to represent one-dimensional curves, i.e. parametrized n-tuples of
 * the form <PRE> ( x(t), y(t), ... ) </PRE>where <i>(x, y, ...)</i> are the coordinates of a point on the curve and
 * <i>t</i> is the parameter. Curve uses the GraphType convention that <i>(x, y, ...)</i> are the dependent variables,
 * and <i>t</i> is the independent variable. Only one independent variable is allowed, but any number of dependent
 * variables may be defined. Data are held as doubles. Curve does not define extra parameters, so it is a "raw" data
 * type. It can hold real or complex data sets. </p><p> Curve sets independentVariables = 1. If the independent variable
 * is sampled uniformly, then the object holds only the dependent data and a Triplet indicating how the sampling is
 * done. If the independent variable is sampled irregularly, then the object holds the sampling values as well. Users
 * can test <i>isIndependentTriplet(0)</i> (or method <i>isUniform()</i> ) to see which case holds. </p><p> Curve
 * replaces the old Triana type TwoD.
 *
 * @author Bernard Schutz
 * @version $Revision: 4048 $
 * @see TrianaType
 * @see GraphType
 * @see VectorType
 * @see triana.types.util.Triplet
 */
public class Curve extends GraphType implements AsciiComm {

    /*
     * This class implements specific Constructors. It introduces no new
     * independent parameters.
     */


    /**
     * Creates a new empty Curve in 2 dimensions by default
     */
    public Curve() {
        super(1, 2);
    }

    /**
     * Creates a new empty Curve in <i>n</i> dimensions.
     *
     * @param n The number of dimensions in which the curve is represented
     */
    public Curve(int n) {
        super(1, n);
    }

    /**
     * Creates a new real 2D Curve with a uniformly sampled independent variable and <i>(x,y)</i> data given by two 1D
     * arrays of doubles. The data arrays must be the same length, and the independent variable is their index,
     * <i>i.e.</i> it runs from 0 to (<i>length</i> - 1) in steps of 1. </p><p> If it is desired to have a different
     * parametrization, use this constructor to create the curve and then use the method <i>setIndependentArrayReal</i>
     * of GraphType to change the parametrization.
     *
     * @param x A one-dimensional array giving the x-values of points on the curve
     * @param y A one-dimensional array giving the y-values of points on the curve
     */
    public Curve(double[] x, double[] y) {
        super(1, 2);
        Triplet xTr = new Triplet(x.length, 0, 1);
        this.setIndependentTriplet(xTr, 0);
        this.setDataArrayReal(x, 0);
        if (y.length == x.length) {
            this.setDataArrayReal(y, 1);
        } else {
            this.setDataArrayReal(new double[x.length], 1);
        }
    }

    /**
     * Creates a new real 3D Curve with a uniformly sampled independent variable and <i>(x,y,z)</i> data given by three
     * 1D arrays of doubles. The data arrays must be the same length, and the independent variable is their index,
     * <i>i.e.</i> it runs from 0 to (<i>length</i> - 1) in steps of 1. </p><p> If it is desired to have a different
     * parametrization, use this constructor to create the curve and then use the methods <i>setIndependentArrayReal</i>
     * or <i>setIndependentTriplet</i> of GraphType to change the parametrization.
     *
     * @param x A one-dimensional array giving the x-values of points on the curve
     * @param y A one-dimensional array giving the y-values of points on the curve
     * @param z A one-dimensional array giving the z-values of points on the curve
     */
    public Curve(double[] x, double[] y, double[] z) {
        super(1, 3);
        Triplet xTr = new Triplet(x.length, 0, 1);
        this.setIndependentTriplet(xTr, 0);
        this.setDataArrayReal(x, 0);
        if (y.length == x.length) {
            this.setDataArrayReal(y, 1);
        } else {
            this.setDataArrayReal(new double[x.length], 1);
        }
        if (z.length == x.length) {
            this.setDataArrayReal(z, 2);
        } else {
            this.setDataArrayReal(new double[x.length], 2);
        }
    }


    /*
    * Methods that make access to data easier for programmers who do not
    * want to worry about the data model, ie which dimensions hold
    * the dependent and independent variables.
    */

    /**
     * Tests to see if the independent variable is uniformly sampled. It calls the <i>isUniform(0)</i> method of
     * GraphType.
     *
     * @return boolean <I>True</I> if the independent variable is sampled uniformly
     */
    public boolean isUniform() {
        return super.isUniform(0);
    }

    /**
     * Tests to see if the independent variable is represented by a Triplet. It calls the <i>isTriplet(0)</i> method of
     * GraphType.
     *
     * @return boolean <I>True</I> if the independent variable is given as a Triplet
     * @see triana.types.util.Triplet
     */
    public boolean isTriplet() {
        return super.isTriplet(0);
    }

    /**
     * Returns the size or length of each of the data arrays.
     *
     * @return int Length of the data set
     */
    public int size() {
        if (getDataArrayReal(0) == null) {
            return 0;
        }
        return Array.getLength(getDataArrayReal(0));
    }

    /**
     * Synonym for method <i>size</i>.
     *
     * @return int Length of the data set
     */
    public int length() {
        return size();
    }

    /**
     * Returns the real part of the data array for the given dependent variable.
     *
     * @param dv the dimension whose data is required
     * @return double[]  The data array
     */
    public double[] getData(int dv) {
        return (double[]) getDataArrayReal(dv);
    }

    /**
     * Returns the imaginary part of the data array for the given dependent variable.
     *
     * @param dv the dimension whose data is required
     * @return double[]  The imaginary part of the data array
     */
    public double[] getDataImag(int dv) {
        return (double[]) getDataArrayImag(dv);
    }

    /**
     * A synonym for <i>getData</i>.
     *
     * @param dv the dimension whose data is required
     * @return double[]  The real part of the data array
     */
    public double[] getDataReal(int dv) {
        return getData(dv);
    }

    /**
     * Sets the argument double[] array as the (real part of) the dependent variable for the given dimension.
     *
     * @param data The new data
     * @param dv   the dimension whose data is set
     */
    public void setDataReal(double[] data, int dv) {
        setDataArrayReal(data, dv);
    }

    /**
     * An alias for <i>setDataReal</i>, useful in derived classes where the data is assumed to be real all the time.
     *
     * @param data The new data
     * @param dv   the dimension whose data is set
     */
    public void setData(double[] data, int dv) {
        setDataArrayReal(data, dv);
    }

    /**
     * Sets the argument double[] array as the imaginary part of the dependent variable for the given dimension.
     *
     * @param data The new data
     * @param dv   the dimension whose data is set
     */
    public void setDataImag(double[] data, int dv) {
        setDataArrayImag(data, dv);
    }

    /**
     * Sets the two argument double[] arrays as the real and imaginary parts of the dependent variable for the given
     * dimension
     *
     * @param dataReal The real part of the new data
     * @param dataImag The imaginary part of the new data
     * @param dv       the dimension whose data is set
     */
    public void setData(double[] dataReal, double[] dataImag, int dv) {
        setDataArrayReal(dataReal, dv);
        setDataArrayImag(dataImag, dv);
    }

    /**
     * Sets the data array(s) to zero if they have been allocated. Does nothing if they have not been allocated.
     *
     * @param dv the dimension whose data is initialized
     */
    public void initialiseData(int dv) {
        int j;
        double[] d = getDataReal(dv);
        if (d != null) {
            for (j = 0; j < d.length; j++) {
                d[j] = 0.0;
            }
        }
        d = getDataImag(dv);
        if (d != null) {
            for (j = 0; j < d.length; j++) {
                d[j] = 0.0;
            }
        }
    }

    /**
     * Allocates the real part of the data for a given dimension to a double array of the given length and then sets its
     * elements to zero.
     *
     * @param len the length of the data set being initialized
     * @param dv  the dimension whose data is set
     */
    public void initialiseDataReal(int len, int dv) {
        double[] d = new double[len];
        for (int j = 0; j < len; j++) {
            d[j] = 0.0;
        }
        setData(d, dv);
    }

    /**
     * Allocates the real and imaginary parts of the data for the given dimension to double arrays of the given length
     * and then sets their elements to zero.
     *
     * @param len the length of the data set being initialized
     * @param dv  the dimension whose data is set
     */
    public void initialiseDataComplex(int len, int dv) {
        double[] dr = new double[len];
        double[] di = new double[len];
        for (int j = 0; j < len; j++) {
            dr[j] = 0.0;
            di[j] = 0.0;
        }
        setData(dr, di, dv);
    }


    /**
     * Returns the real part of the data array for the independent variable.  If it is uniformly sampled, any Triplet is
     * converted to an array.
     *
     * @return double[]  The independent variable
     * @see triana.types.util.Triplet
     */
    public double[] getXArray() {
        if (isTriplet(0)) {
            return getIndependentTriplet(0).convertToArray();
        }
        return getIndependentArrayReal(0);
    }

    /**
     * Returns the Triplet giving the independent data. If the data is not uniformly sampled then it returns null.
     *
     * @return Triplet  The independent variable as a Triplet, or null if not uniformly sampled
     * @see triana.types.util.Triplet
     */
    public Triplet getXTriplet() {
        if (isUniform()) {
            if (isTriplet(0)) {
                return getIndependentTriplet(0);
            } else {
                return new Triplet(getIndependentArrayReal(0));
            }
        }
        return null;
    }

    /**
     * Returns the imaginary part of the data array for the independent variable.
     *
     * @return double[]  The imaginary part of the independent variable
     */
    public double[] getXImag() {
        if (isTriplet(0)) {
            return null;
        }
        return getIndependentArrayImag(0);
    }

    /**
     * A synonym for <i>getXArray</i>.
     *
     * @return double[]  The real part of the independent variable
     */
    public double[] getXReal() {
        return getXArray();
    }

    /**
     * Overrides a method of the same name in GraphType, to return the real parts of values for the independent axes of
     * a graph. </p><p> The Graphing method must be careful how it uses this method. The use depends on the
     * dimensionality of the grapher. If it is a 2D grapher, it can display only a two-dimensional projection of the
     * Curve. It can access the data for the two chosen dimensions with either the present method or
     * <i>getGraphArrayReal</i>. Using the present method, the grapher should supply an integer argument equal to the
     * index of the desired dimension.
     *
     * @param dim The dimension of the Curve that is desired
     * @return The real part of the data values along this dimension
     */
    public double[] getIndependentScaleReal(int dim) {
        return getDataReal(dim);
    }

    /**
     * Overrides a method of the same name in GraphType, to return the imaginary parts of values for the independent
     * axes of a graph. </p><p> The Graphing method must be careful how it uses this method. The use depends on the
     * dimensionality of the grapher. If it is a 2D grapher, it can display only a two-dimensional projection of the
     * Curve. It can access the data for the two chosen dimensions with either the present method or
     * <i>getGraphArrayImag</i>. Using the present method, the grapher should supply an integer argument equal to the
     * index of the desired dimension.
     *
     * @param dim The dimension of the Curve that is desired
     * @return The imaginary part of the data values along this dimension
     */
    public double[] getIndependentScaleImag(int dim) {
        return getDataImag(dim);
    }

    /**
     * Overrides a method of the same name in GraphType, to return the real part of the values for the dependent axes of
     * a graph. </p><p> The Graphing method must be careful how it uses this method. The use depends on the
     * dimensionality of the grapher. If it is a 2D grapher, it can display only a two-dimensional projection of the
     * Curve. It can access the data for the two chosen dimensions with either the present method or
     * <i>getIndependentScaleReal</i>. Using the present method, the grapher should supply an integer argument equal to
     * one less than the index of the desired dimension in Curve. This is an arbitrary shift to make 2D graphers easy to
     * use: they get <i>y</i>-data for the vertical axis by using the present method with argument 0.
     *
     * @param dv One less than the dimension of the Curve that is desired
     * @return The real part of the data values along this dimension
     */
    public Object getGraphArrayReal(int dv) {
        return getDataReal(dv + 1);
    }

    /**
     * Overrides a method of the same name in GraphType, to return the imaginary part of the values for the dependent
     * axes of a graph. </p><p> The Graphing method must be careful how it uses this method. The use depends on the
     * dimensionality of the grapher. If it is a 2D grapher, it can display only a two-dimensional projection of the
     * Curve. It can access the data for the two chosen dimensions with either the present method or
     * <i>getIndependentScaleImag</i>. Using the present method, the grapher should supply an integer argument equal to
     * one less than the index of the desired dimension in Curve. This is an arbitrary shift to make 2D graphers easy to
     * use: they get <i>y</i>-data for the vertical axis by using the present method with argument 0.
     *
     * @param dv One less than the dimension of the Curve that is desired
     * @return The imaginary part of the data values along this dimension
     */
    public Object getGraphArrayImag(int dv) {
        return getDataImag(dv + 1);
    }

    /**
     * Sets the argument Triplet as the independent variable.
     *
     * @param tr The new data
     * @see triana.types.util.Triplet
     */
    public void setX(Triplet tr) {
        setIndependentTriplet(tr, 0);
    }

    /**
     * Sets the argument double[] as the independent variable.
     *
     * @param data The new data for the independent variable
     */
    public void setX(Object data) {
        setIndependentArrayReal((double[]) data, 0);
    }

    /**
     * Sets the two argument double[] arrays as the real and imaginary parts of the independent variable.
     *
     * @param xReal The real part of the independent variable
     * @param xImag The imaginary part of the independent variable
     */
    public void setX(double[] xReal, double[] xImag) {
        setIndependentArrayReal(xReal, 0);
        setIndependentArrayImag(xImag, 0);
    }


    /*
     * Implement methods that need to be overridden from superior classes.
     */

    /**
     * Tests to make sure this Object obeys the Curve data model.
     *
     * @return boolean <I>True</I> if it obeys the GraphType model and if the values of independentVariables and
     *         dependentVariables are right.
     */
    public boolean testDataModel() {
        if (!super.testDataModel()) {
            return false;
        }
        if ((getIndependentVariables() != 1) || (getDependentVariables() <= 1)) {
            return false;
        }
        return true;
    }

    /**
     * This is one of the most important methods of Triana data. types. It returns a copy of the type invoking it. This
     * <b>must</b> be overridden for every derived data type derived. If not, the data cannot be copied to be given to
     * other units. Copying must be done by value, not by reference. </p><p> To override, the programmer should not
     * invoke the <i>super.copyMe</i> method. Instead, create an object of the current type and call methods
     * <i>copyData</i> and <i>copyParameters</i>. If these have been written correctly, then they will do the copying.
     * The code should createTool, for type YourType: <PRE> YourType y = null; try { y =
     * (YourType)getClass().newInstance(); y.copyData( this ); y.copyParameters( this ); y.setLegend( this.getLegend()
     * ); } catch (IllegalAccessException ee) { System.out.println("Illegal Access: " + ee.getMessage()); } catch
     * (InstantiationException ee) { System.out.println("Couldn't be instantiated: " + ee.getMessage()); } return y;
     * </PRE> </p><p> The copied object's data should be identical to the original. The method here modifies only one
     * item: a String indicating that the object was created as a copy is added to the <i>description</i> StringVector.
     *
     * @return TrianaType Copy by value of the current Object except for an updated <i>description</i>
     */
    public TrianaType copyMe() {
        Curve v = null;
        try {
            v = (Curve) getClass().newInstance();
            v.copyData(this);
            v.copyParameters(this);
        }
        catch (IllegalAccessException ee) {
            System.out.println("Illegal Access: " + ee.getMessage());
        }
        catch (InstantiationException ee) {
            System.out.println("Couldn't be instantiated: " + ee.getMessage());
        }
        return v;
    }


    /**
     * Tests the argument object to determine if it makes sense to perform arithmetic operations between it and the
     * current object. </p><p> In Curve, this method first tests for compatibility with superior classes, and then (if
     * the input object is a Curve) tests that the input has the same uniformity and same number of dependent variables
     * as the current object. (Independent variable number is tested in GraphType.) </p><p> Classes derived from this
     * should over-ride this method with further tests as appropriate. The over-riding method should normally have the
     * first lines <PRE> boolean test = super.isCompatible( obj ); </PRE>followed by other tests. If other types not
     * subclassed from GraphType or Const should be allowed to be compatible then other tests must be implemented.
     *
     * @param obj The data object to be compared with the current one
     * @return <I>True</I> if the object can be combined with the current one
     */
    public boolean isCompatible(TrianaType obj) {
        boolean test = super.isCompatible(obj);
        if ((test) && (obj instanceof Curve)) {
            if (isUniform() != ((Curve) obj).isUniform()) {
                return false;
            }
            if (getDependentVariables() != ((Curve) obj).getDependentVariables()) {
                return false;
            }
        }
        return test;
    }


}
