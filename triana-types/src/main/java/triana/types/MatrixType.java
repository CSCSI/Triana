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

import triana.types.util.Triplet;

/**
 * MatrixType is the basic class derived from GraphType to represent
 * a two-dimensional array whose elements are of type double. It does
 * not define extra parameters, so it is a "raw" data type. It can hold
 * real or complex two-dimensional data sets.
 * </p><p>
 * MatrixType sets <i>GraphType.independentVariables</i> = 2 and
 * <i>GraphType.dependentVariables</i> = 1,
 * so it represents the values of a scalar function
 * of two independent variables. If the variables are sampled uniformly,
 * then the object holds only the sampled data and two Triplets indicating
 * how the sampling is done. If the independent variables are sampled
 * irregularly, then the object holds the sampling values as well.
 * Users can test method <i>isIndependentTriplet</i> (or method <i>isUniform</i> )
 * to see which case holds.
 * </p><p>
 * @see TrianaType
 * @see GraphType
 * @see Arithmetic
 * @see VectorType
 * @see triana.types.util.Triplet
 *
 * @author      Bernard Schutz
 * @created     30 December 2000
 * @version     $Revision: 4048 $
 * @date        $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public class MatrixType extends GraphType implements AsciiComm {

    /*
     * This class implements specific Constructors. It introduces no new
     * parameters.
     */

    /**
     * Creates a new empty MatrixType, but with the specific dimensions of this class
     */
    public MatrixType() {
        super(2, 1);
    }

    /**
     * Creates a new real MatrixType with a uniformly sampled independent
     * variables by using the 2D argument array <i>z</i> of doubles as
     * the data and assuming that the independent variables are simply the
     * indices of the data array, ie <i>x</i> runs from 0 to <i>z.length</i> - 1
     * in steps of 1 and <i>y</i> runs from 0 to <i>z[0].length</i> - 1 in steps of 1.
     *
     * @param z The two-dimensional matrix of data
     */
    public MatrixType(double[][] z) {
        super(2, 1);
        Triplet xTr = new Triplet(z.length, 0, 1);
        this.setIndependentTriplet(xTr, 0);
        Triplet yTr = new Triplet(z[0].length, 0, 1);
        this.setIndependentTriplet(yTr, 1);
        this.setDataArrayReal(z, 0);
    }

    /**
     * Creates a new real MatrixType using the first argument array of doubles
     * as the sampling values of the first independent variable (<i>x</i>),
     * the second argument array as the samples of the second independent
     * variable (<i>y</i>), and the third argument 2D array of doubles as the
     * dependent variable real data.
     *
     * @param x The values of the first independent variable
     * @param y The values of the second independent variable
     * @param z The two-dimensional matrix of data
     */
    public MatrixType(double[] x, double[] y, double[][] z) {
        super(2, 1);
        this.setIndependentArrayReal(x, 0);
        this.setIndependentArrayReal(y, 1);
        this.setDataArrayReal(z, 0);
    }

    /**
     * Creates a new real MatrixType with a uniformly sampled independent
     * variable by using the first argument as the as the Triplet that
     * determines how the first independent variable (<i>x</i>) is uniformly
     * sampled, the second Triplet argument to determine how the second
     * independent variable (<i>y</i>) is uniformly sampled, and
     * the third argument as the dependent variable real data.
     *
     * One can invoke this method by constructing the Triplet argument
     * in the reference statement to this Constructor, eg by using
     * an argument of the form <PRE>
     *      new Triplet(length, start, step)
     * </PRE>
     * @param xTr The Triplet giving the uniformly sampled values of the first independent variable
     * @param yTr The Triplet giving the uniformly sampled values of the second independent variable
     * @param z The two-dimensional matrix of data
     * @see triana.types.util.Triplet
     */
    public MatrixType(Triplet xTr, Triplet yTr, double[][] z) {
        super(2, 1);
        this.setIndependentTriplet(xTr, 0);
        this.setIndependentTriplet(yTr, 1);
        this.setDataArrayReal(z, 0);
    }

    /**
     * Creates a new complex-valued MatrixType with real uniformly-sampled
     * independent variables by using the first two (Triplet) arguments to
     * determine how the independent variables are uniformly sampled, the
     * third argument (double[][]) as the real part of the dependent variable
     * data, and the fourth argument (double[][]) as the imaginary part. If
     * either of the first two arguments is <i>null</i>, then the corresponding
     * independent variable will be a Triplet constructed by default from
     * the size of the input arrays.
     *
     * @param xTr The Triplet giving the uniformly sampled values of the first independent variable
     * @param yTr The Triplet giving the uniformly sampled values of the second independent variable
     * @param zr The two-dimensional matrix of real part of the data
     * @param zi The two-dimensional matrix of imaginary part of the data
     * @see triana.types.util.Triplet
     */
    public MatrixType(Triplet xTr, Triplet yTr, double[][] zr, double[][] zi) {
        super(2, 1);
        if (xTr != null)
            this.setIndependentTriplet(xTr, 0);
        else {
            Triplet x1Tr = new Triplet(zr.length, 0, 1);
            this.setIndependentTriplet(x1Tr, 0);
        }
        if (yTr != null)
            this.setIndependentTriplet(yTr, 1);
        else {
            Triplet y1Tr = new Triplet(zr[0].length, 0, 1);
            this.setIndependentTriplet(y1Tr, 1);
        }
        this.setDataArrayReal(zr, 0);
        this.setDataArrayImag(zi, 0);
    }

    /**
     * Creates a new complex-valued MatrixType with complex
     * independent variables by using the first two argument arrays of doubles
     * as the real part and imaginary part of the first independent variable
     * (<i>x</i>), the third and fourth arguments as the real and imaginary parts of
     * the second independent variable (<i>y</i>), and the fifth and sixth
     * argument double[][] arrays as the real and imaginary parts of the
     * dependent variable.
     */
    public MatrixType(double[] xr, double[] xi, double[] yr, double[] yi, double[][] zr, double[][] zi) {
        super(2, 1);
        this.setIndependentArrayReal(xr, 0);
        this.setIndependentArrayImag(xi, 0);
        this.setIndependentArrayReal(yr, 1);
        this.setIndependentArrayImag(yi, 1);
        this.setDataArrayReal(zr, 0);
        this.setDataArrayImag(zi, 0);
    }

    /*
     * Methods that make access to data easier for programmers who do not
     * want to worry about the data model, i.e. which dimensions hold
     * the dependent and independent variables.
     */

    /**
     * Returns an array containing the sizes or lengths of the two dimensions
     * of the dependent data array.
     *
     * @return int[] Lengths of the matrix columns and rows
     */
    public int[] size() {
        return getDimensionLengths();
    }

    /**
     * A synonym for <i>size</i>.
     *
     * @return int Lengths of the matrix columns and rows
     */
    public int[] length() {
        return size();
    }

    /**
     * Returns the (real part of the) data array for the dependent
     * variable.
     *
     * @return double[][]  The data array
     */
    public double[][] getData() {
        return (double[][]) getDataArrayReal(0);
    }

    /**
     * Returns the imaginary part of the data
     * array for the dependent variable.
     *
     * @return double[][]  The imaginary part of the data array
     */
    public double[][] getDataImag() {
        return (double[][]) getDataArrayImag(0);
    }

    /**
     * A synonym for <i>getData</i>.
     *
     * @return double[][]  The real part of the data array
     */
    public double[][] getDataReal() {
        return getData();
    }

    /**
     * Sets the argument double[][] array as the dependent
     * variable.
     *
     * @param data The new data
     */
    public void setData(double[][] data) {
        setDataArrayReal(data, 0);
    }

    /**
     * Sets the two argument double[][] arrays
     * as the real and imaginary parts of the dependent
     * variable.
     *
     * @param dataReal The real part of the new data
     * @param dataImag The imaginary part of the new data
     */
    public void setData(double[][] dataReal, double[][] dataImag) {
        setDataArrayReal(dataReal, 0);
        setDataArrayImag(dataImag, 0);
    }

    /**
     * Sets the data array(s) to zero if they have been allocated. Does
     * nothing if they have not been allocated.
     */
    public void initialiseData() {
        int j, k;
        double[][] d = getDataReal();
        if (d != null) for (j = 0; j < d.length; j++) for (k = 0; k < d[j].length; k++) d[j][k] = 0.0;
        d = getDataImag();
        if (d != null) for (j = 0; j < d.length; j++) for (k = 0; k < d[j].length; k++) d[j][k] = 0.0;
    }

    /**
     * Allocates the real part of the data to a double array of the given
     * lengths and then sets its elements to zero.
     *
     * @param xlen The size of the first dimension of the matrix
     * @param ylen The size of the second dimension of the matrix
     */
    public void initialiseDataReal(int xlen, int ylen) {
        int j, k;
        double[][] d = new double[xlen][ylen];
        for (j = 0; j < xlen; j++) for (k = 0; k < ylen; k++) d[j][k] = 0.0;
        setData(d);
    }

    /**
     * Allocates the real and imaginary parts of the data to double arrays
     * of the given lengths and then sets their elements to zero.
     *
     * @param xlen The size of the first dimension of the matrix
     * @param ylen The size of the second dimension of the matrix
     */
    public void initialiseDataComplex(int xlen, int ylen) {
        int j, k;
        double[][] dr = new double[xlen][ylen];
        double[][] di = new double[xlen][ylen];
        for (j = 0; j < xlen; j++)
            for (k = 0; k < ylen; k++) {
                dr[j][k] = 0.0;
                di[j][k] = 0.0;
            }
        setData(dr, di);
    }


    /**
     * Returns the real part of the data values for the given
     * <b>independent</b> variable.  If it is uniformly sampled, any Triplet is
     * converted to an array.
     *
     * @param dim The index of the independent variable being examined
     * @return double[]  The values of the given independent variable
     * @see triana.types.util.Triplet
     */
    public double[] getXorYArray(int dim) {
        if (isTriplet(dim)) return getIndependentTriplet(dim).convertToArray();
        return getIndependentArrayReal(dim);
    }

    /**
     * Method getXorYTriplet returns the Triplet that determines the values of
     * the given <b>independent</b> variable. If the data is not uniformly sampled
     * in this direction then it returns null.
     *
     * @param dim The index of the independent variable being examined
     * @return The given independent variable as a Triplet, or null if not uniformly sampled
     * @see triana.types.util.Triplet
     */
    public Triplet getXorYTriplet(int dim) {
        if (isUniform(dim)) {
            if (isTriplet(dim))
                return getIndependentTriplet(dim);
            else
                return new Triplet(getIndependentArrayReal(dim));
        }
        return null;
    }

    /**
     * Returns the imaginary part of the data
     * array for the given independent variable.
     *
     * @param dim The index of the independent variable being examined
     * @return The imaginary part of the given independent variable
     */
    public double[] getXorYImag(int dim) {
        if (isTriplet(dim)) return null;
        return getIndependentArrayImag(dim);
    }

    /**
     * A synonym for <i>getXYArray</i>.
     *
     * @param dim The index of the independent variable being examined
     * @return The real part of the given independent variable
     */
    public double[] getXorYReal(int dim) {
        return getXorYArray(dim);
    }

    /**
     * Sets the first argument Object as the independent
     * variable for the index given by the second argument. (This Object
     * must be either a Triplet or a double[].)
     *
     * @param data The new data, either a Triplet or a double[]
     * @param dim The index of the independent variable being set
     * @see triana.types.util.Triplet
     */
    public void setXorY(Object data, int dim) {
        if (data instanceof Triplet)
            setIndependentTriplet((Triplet) data, dim);
        else
            setIndependentArrayReal((double[]) data, dim);
    }

    /**
     * This variant of method setXorY sets the first two argument double[]
     * arrays as the real and imaginary parts of the independent
     * variable whose index is the third argument.
     *
     * @param xReal The real part of the independent variable
     * @param xImag The imaginary part of the independent variable
     * @param dim The index of the independent variable being set
     */
    public void setXorY(double[] xReal, double[] xImag, int dim) {
        setIndependentArrayReal(xReal, dim);
        setIndependentArrayImag(xImag, dim);
    }

    /*
     * Implement methods that need to be overridden from superior classes.
     */

    /**
     * Tests to make sure this Object obeys the MatrixType data model.
     *
     * @return boolean <I>True</I> if it obeys the GraphType model and if the values of independentVariables and dependentVariables are right.
     */
    public boolean testDataModel() {
        if (!super.testDataModel()) return false;
        if ((getIndependentVariables() != 2) || (getDependentVariables() != 1)) return false;
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
        MatrixType m = null;
        try {
            m = (MatrixType) getClass().newInstance();
            m.copyData(this);
            m.copyParameters(this);
        }
        catch (IllegalAccessException ee) {
            System.out.println("Illegal Access: " + ee.getMessage());
        }
        catch (InstantiationException ee) {
            System.out.println("Couldn't be instantiated: " + ee.getMessage());
        }
        return m;
    }


    /**
     * Tests the argument object to determine if
     * it makes sense to perform arithmetic operations between
     * it and the current object.
     * </p><p>
     * In MatrixType, this method only tests for compatibility with superior
     * classes, and to see that the input object is a Histogram.
     * </p><p>
     * Classes derived from this should over-ride this method with further
     * tests as appropriate. The over-riding method should normally have the
     * first lines <PRE>
     *      boolean test = super.isCompatible( obj );
     * </PRE>followed by other tests. If other types
     * not subclassed from GraphType or Const should be allowed to be
     * compatible then other tests must be implemented.
     *
     * @param obj The data object to be compared with the current one
     * @return <I>True</I> if the object can be combined with the current one
     */
    public boolean isCompatible(TrianaType obj) {
        boolean test = super.isCompatible(obj);
        if (test) return (obj instanceof MatrixType);
        return test;
    }


}



