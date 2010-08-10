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

import triana.types.util.FlatArray;
import triana.types.util.Triplet;

/**
 * VectorType is the basic class derived from GraphType to represent one-dimensional array data of type double. It does
 * not define extra parameters, so it is a "raw" data type. It can hold real or complex one-dimensional data sets.
 * </p><p> VectorType sets <i>independentVariables</i> = 1 and <i>dependentVariables</i> = 1 so it represents the values
 * of a scalar function of one independent variable. If that variable is sampled uniformly, then the object holds only
 * the sampled data and a Triplet indicating how the sampling is done. If the independent variable is sampled
 * irregularly, then the object holds the sampling values as well. Users can use method <i>isIndependentTriplet</i> (or
 * method <i>isUniform</i>) to see which case holds. </p><p> VectorType defines new methods that allow padding or
 * interpolation of the data with zeros. These are useful in many signal-analysis applications that will use types
 * derived from VectorType. </p><p> VectorType replaces the old Triana type RawData. For compatibility with that class
 * there are some obsolete functions and parameters added here. In later releases these should be removed. <p/> </p><p>
 *
 * @author Bernard Schutz
 * @version $Revision: 4048 $
 * @see TrianaType
 * @see GraphType
 * @see Arithmetic
 * @see MatrixType
 * @see triana.types.util.Triplet
 */
public class VectorType extends GraphType implements AsciiComm {

    /*
     * This class implements specific Constructors. It introduces no new
     * independent parameters. But it uses parameters from VectorType and
     * ensures that these are always set to the appropriate data of
     * Vector and its superior classes.
     */

    /**
     * (Parameter that is kept for consistency with previous versions, but which is obsolete. It must be kept to the
     * right values by the <i>updateObsoletePointers</i> method and/or constructors and <i>set...</i> methods.) </p><p>
     * The <i>x</i>-axis label. Now obsolete, replaced by <i>GraphType.labels</i>.
     */
    public String xlabel;

    /**
     * (Parameter that is kept for consistency with previous versions, but which is obsolete. It must be kept to the
     * right values by the <i>updateObsoletePointers</i> method and/or constructors and <i>set...</i> methods.) </p><p>
     * The <i>y</i>-axis label.  Now obsolete, replaced by <i>GraphType.labels</i>.
     */
    public String ylabel;

    /**
     * (Parameter that is kept for consistency with previous versions, but which is obsolete. It must be kept to the
     * right values by the <i>updateObsoletePointers</i> method and/or constructors and <i>set...</i> methods.) </p><p>
     * The <i>x</i> data.  Now obsolete, replaced by <i>TrianaType.dataContainer</i>.
     */
    public double[] x;

    /**
     * (Parameter that is kept for consistency with previous versions, but which is obsolete. It must be kept to the
     * right values by the <i>updateObsoletePointers</i> method and/or constructors and <i>set...</i> methods.) </p><p>
     * The <i>y</i> data.  Now obsolete, replaced by <i>TrianaType.dataContainer</i>.
     */
    public double[] y;


    /**
     * Creates a new empty VectorType, but it has to have specified dimensions.
     */
    public VectorType() {
        super(1, 1);
    }

    /**
     * Creates a new real VectorType with a uniformly sampled independent variable by using the argument array of
     * doubles as the data and assuming that the independent variable is simply the index of the data array, ie it runs
     * from 0 to length - 1 in steps of 1.
     *
     * @param y Dependent data array
     */
    public VectorType(double[] y) {
        super(1, 1);
        Triplet xTr = new Triplet(y.length, 0, 1);
        this.setX(xTr);
        this.setDataReal(y);
    }

    /**
     * Creates a new real VectorType using the first argument array of doubles as the sampling values of the independent
     * variable and the second argument array of doubles as the real data.
     *
     * @param x Independent data array
     * @param y Dependent data array
     */
    public VectorType(double[] x, double[] y) {
        super(1, 1);
        this.setX(x);
        this.setDataReal(y);
    }

    /**
     * Creates a new real VectorType with a uniformly sampled independent variable by using the first argument as the as
     * the Triplet that determines how the independent variable is uniformly sampled and the second argument as the real
     * data. </p><p> One can invoke this method by constructing the Triplet argument in the reference statement to this
     * Constructor, eg by using an argument of the form <PRE> new Triplet(length, start, step) </PRE>
     *
     * @param xTr the generator of the independent variable
     * @param y   the data of the dependent variable
     * @see triana.types.util.Triplet
     */
    public VectorType(Triplet xTr, double[] y) {
        super(1, 1);
        this.setX(xTr);
        this.setDataReal(y);
    }

    /**
     * Creates a new complex-valued VectorType with a real uniformly-sampled independent variable by using the first
     * (Triplet) argument to determine how the independent variable is uniformly sampled, the second argument (double[])
     * as the real part of the data, and the third argument (double[]) as the imaginary part. </p><p> One can invoke
     * this method by constructing the Triplet argument in the reference statement to this Constructor, eg by using an
     * argument of the form <PRE> new Triplet(length, start, step) </PRE>
     *
     * @param xTr the generator of the independent variable
     * @param yr  the data of the real part of the dependent variable
     * @param yi  the data of the imaginary part of the dependent variable
     * @see triana.types.util.Triplet
     */
    public VectorType(Triplet xTr, double[] yr, double[] yi) {
        super(1, 1);
        this.setX(xTr);
        this.setDataReal(yr);
        this.setDataImag(yi);
    }

    /**
     * Creates a new complex-valued VectorType with a complex independent variable by using the first two argument
     * arrays of doubles as the real part and imaginary part of the indpendent variable, and the second two argument
     * arrays of doubles as the real part and imaginary part of the dependent variable.
     *
     * @param xr the real part of the data of the independent variable
     * @param xi the imaginary part of the data of the independent variable
     * @param yr the real part of the data of the dependent variable
     * @param yi the imaginary part of the data of the dependent variable
     */
    public VectorType(double[] xr, double[] xi, double[] yr, double[] yi) {
        super(1, 1);
        this.setX(xr, xi);
        this.setDataReal(yr);
        this.setDataImag(yi);
    }

    /*
     * Methods that make access to data easier for programmers who do not
     * want to worry about the data model, ie which dimensions hold
     * the dependent and independent variables.
     */

    /**
     * Tests to see if the independent variable is uniformly sampled. It calls the <i>isUniform</i> method of GraphType
     * for index 0.
     *
     * @return boolean <i>True</i> if the independent variable is sampled uniformly
     */
    public boolean isUniform() {
        return super.isUniform(0);
    }

    /**
     * Tests to see if the independent variable is represented by a Triplet. It calls the <i>isTriplet</i> method of
     * GraphType for index 0.
     *
     * @return boolean <i>True</i> if the independent variable is given as a Triplet
     * @see triana.types.util.Triplet
     */
    public boolean isTriplet() {
        return super.isTriplet(0);
    }

    /**
     * Returns the size or length of the data array.
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
     * A synonym for <i>size</i>.
     *
     * @return int Length of the data set
     */
    public int length() {
        return size();
    }

    /**
     * Returns the real part of the data array for the given dependent variable. This is just an alias for
     * <i>getDataArrayReal(0)</i> that allows users not to worry about the index, since in VectorType there is only one
     * dependent data set.
     *
     * @return double[] The (real part of the ) data array
     */
    public double[] getDataReal() {
        return (double[]) getDataArrayReal(0);
    }

    /**
     * Returns the real part of the array that will be graphed for the given dependent variable. This is just an alias
     * for <i>getGraphArrayReal(0)</i> that allows users not to worry about the index, since in VectorType there is only
     * one dependent data set. This method should be not be over-ridden by derived classes, even if the independent data
     * are stored in an unconventional manner, for example non-monotonic. Instead, the method <i>getGraphArrayReal</i>
     * of GraphType should be overridden.
     *
     * @return double[] The (real part of the) graph array
     * @see GraphType
     */
    public double[] getGraphReal() {
        return (double[]) getGraphArrayReal(0);
    }

    /**
     * Returns the imaginary part of the data array for the dependent variable. This is just an alias for
     * <i>getDataArrayImag(0)</i> that allows users not to worry about the index, since in VectorType there is only one
     * dependent data set.
     *
     * @return double[]  The imaginary part of the data array
     */
    public double[] getDataImag() {
        return (double[]) getDataArrayImag(0);
    }

    /**
     * Returns the imaginary part of the array that will be graphed for the given dependent variable. This is just an
     * alias for <i>getGraphArrayImag(0)</i> that allows users not to worry about the index, since in VectorType there
     * is only one dependent data set. This method should be not be over-ridden by derived classes, even if the
     * independent data are stored in an unconventional manner, for example non-monotonic. Instead, the method
     * <i>getGraphArrayImag</i> of GraphType should be overridden.
     *
     * @return double[] The imaginary part of graph array
     * @see GraphType
     */
    public double[] getGraphImag() {
        return (double[]) getGraphArrayImag(0);
    }

    /**
     * A synonym for <i>getDataReal</i> for use when data is known to be only real.
     *
     * @return double[]  The real data array
     */
    public double[] getData() {
        return getDataReal();
    }

    /**
     * A synonym for <i>getGraphReal</i> for use when the data is known to be real. This method should not be
     * over-ridden in derived classes.
     *
     * @return double[]  The real graph array
     */
    public double[] getGraph() {
        return getGraphReal();
    }

    /**
     * Sets the argument double[] array as the (real part of) the dependent variable.
     *
     * @param data The new data
     */
    public void setDataReal(double[] data) {
        setDataArrayReal(data, 0);
    }

    /**
     * An alias for <i>setDataReal</i>, useful in derived classes where the data is assumed to be real all the time.
     *
     * @param data The new data
     */
    public void setData(double[] data) {
        setDataArrayReal(data, 0);
    }

    /**
     * Sets the argument double[] array as the imaginary part of the dependent variable.
     *
     * @param data The new data
     */
    public void setDataImag(double[] data) {
        setDataArrayImag(data, 0);
    }

    /**
     * Sets the two argument double[] arrays as the real and imaginary parts of the dependent variable.
     *
     * @param dataReal The real part of the new data
     * @param dataImag The imaginary part of the new data
     */
    public void setData(double[] dataReal, double[] dataImag) {
        setDataArrayReal(dataReal, 0);
        setDataArrayImag(dataImag, 0);
    }

    /**
     * Sets the data array(s) to zero if they have been allocated. Does nothing if they have not been allocated.
     */
    public void initialiseData() {
        int j;
        double[] d = getDataReal();
        if (d != null) {
            for (j = 0; j < d.length; j++) {
                d[j] = 0.0;
            }
        }
        d = getDataImag();
        if (d != null) {
            for (j = 0; j < d.length; j++) {
                d[j] = 0.0;
            }
        }
    }


    /**
     * Allocates the real part of the data to a double array of the given length and then sets its elements to zero.
     *
     * @param len The length of the data array being created
     */
    public void initialiseDataReal(int len) {
        double[] d = new double[len];
        for (int j = 0; j < len; j++) {
            d[j] = 0.0;
        }
        setData(d);
    }

    /**
     * Allocates the real and imaginary parts of the data to double arrays of the given length and then sets their
     * elements to zero.
     *
     * @param len The length of the data arrays being created
     */
    public void initialiseDataComplex(int len) {
        double[] dr = new double[len];
        double[] di = new double[len];
        for (int j = 0; j < len; j++) {
            dr[j] = 0.0;
            di[j] = 0.0;
        }
        setData(dr, di);
    }

    /**
     * Returns the Triplet giving the independent data. If the data is not uniformly sampled then it returns
     * <i>null</i>.
     *
     * @return Triplet The independent variable as a Triplet, or <i>null</i> if not uniformly sampled
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
     * Returns the real part of the data array for the independent variable.  If it is uniformly sampled, any Triplet is
     * converted to an array.
     *
     * @return double[]  The real part of independent variable
     * @see triana.types.util.Triplet
     */
    public double[] getXReal() {
        if (isTriplet(0)) {
            return getIndependentTriplet(0).convertToArray();
        }
        return getIndependentArrayReal(0);
    }

    /**
     * Returns an array containing the real part of the data scale for the independent variable.  This should be not be
     * over-ridden by derived classes, even if the independent data are stored in an unconventional manner, for example
     * non-monotonic. Instead, the method <i>getIndependentScaleReal</i> of GraphType should be overridden.
     *
     * @return double[]  The real part of independent variable's scale
     * @see GraphType
     */
    public double[] getScaleReal() {
        return getIndependentScaleReal(0);
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
     * Returns an array containing the imaginary part of the data scale for the independent variable.  This should be
     * not be over-ridden by derived classes, even if the independent data are stored in an unconventional manner, for
     * example non-monotonic. Instead, the method <i>getIndependentScaleImag</i> of GraphType should be overridden.
     *
     * @return double[]  The imaginary part of independent variable's scale
     * @see GraphType
     */
    public double[] getScaleImag() {
        return getIndependentScaleImag(0);
    }

    /**
     * A synonym for <i>getXReal</i> for use when the data are known to be real.
     *
     * @return double[]  The (real part of the) independent variable
     */
    public double[] getXArray() {
        return getXReal();
    }

    /**
     * A synonym for <i>getScaleReal</i> for use when the data are known to be real. Do not override this method in
     * derived classes.
     *
     * @return double[]  The (real part of the) independent variable's scale
     */
    public double[] getScale() {
        return getScaleReal();
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
    public void setX(double[] data) {
        setIndependentArrayReal(data, 0);
    }

    /**
     * Sets the argument double[] as the (real part of the) independent variable. Synonym for <i>SetX</i>.
     *
     * @param data The new data for the real part of the independent variable
     */
    public void setXReal(double[] data) {
        setIndependentArrayReal(data, 0);
    }

    /**
     * Sets the argument double[] as the imaginary part of the independent variable.
     *
     * @param data The new data for the imaginary part of the independent variable
     */
    public void setXImag(double[] data) {
        setIndependentArrayImag(data, 0);
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
     * Implement new methods for padding or interpolating with zeros.
     */

    /**
     * Extends the data set (both real and imaginary parts) to a longer set by padding with zeros. The given integer
     * argument gives the new length after padding, and the boolean argument determines whether the padding is at the
     * front or the back. </p><p> If the new length is shorter than the old, nothing is done. </p><p> Derived types
     * should override this if necessary to provide for the correct handling of parameters and other special features.
     *
     * @param newLength The new length of the data set
     * @param front     True if padding is at the front, false for padding at the back
     */
    public void extendWithZeros(int newLength, boolean front) {
        int oldsize = size();
        int j;
        if (newLength <= oldsize) {
            return;
        }
        //System.out.println("extending with zeros to new length = " + String.valueOf( newLength ) );
        double[] newReal = new double[newLength];
        FlatArray.initializeArray(newReal);
        if (front) {
            System.arraycopy(getDataReal(), 0, newReal, newLength - oldsize, oldsize);
        } else {
            System.arraycopy(getDataReal(), 0, newReal, 0, oldsize);
        }
        setDataReal(newReal);

        setDimensionLengths(newLength, 0);

        if (isDependentComplex(0)) {
            double[] newImag = new double[newLength];
            FlatArray.initializeArray(newImag);
            if (front) {
                System.arraycopy(getDataImag(), 0, newImag, newLength - oldsize, oldsize);
            } else {
                System.arraycopy(getDataImag(), 0, newImag, 0, oldsize);
            }
            setDataImag(newImag);
        }

        if (isUniform(0)) {
            if (!isTriplet(0)) {
                setIndependentTriplet(Triplet.convertToTriplet(getXArray()), 0);
            }
            getIndependentTriplet(0).setLength(newLength);
        } else {
            double[] x = getXArray();
            double[] newX = new double[newLength];
            double dx;
            if (front) {
                System.arraycopy(x, 0, newX, newLength - oldsize, oldsize);
                dx = x[1] - x[0];
                for (j = newLength - oldsize; j > 0; j--) {
                    newX[j - 1] = newX[j] - dx;
                }
            } else {
                System.arraycopy(x, 0, newX, 0, oldsize);
                dx = x[x.length - 1] - x[x.length - 2];
                for (j = oldsize; j < newLength; j++) {
                    newX[j] = newX[j - 1] + dx;
                }
            }
            setX(newX);

            if (isIndependentComplex(0)) {
                double[] x1 = getXImag();
                double[] newX1 = new double[newLength];
                double dx1;
                if (front) {
                    System.arraycopy(x1, 0, newX1, newLength - oldsize, oldsize);
                    dx1 = x1[1] - x1[0];
                    for (j = newLength - oldsize; j > 0; j--) {
                        newX1[j - 1] = newX1[j] - dx1;
                    }
                } else {
                    System.arraycopy(x1, 0, newX1, 0, oldsize);
                    dx1 = x1[x1.length - 1] - x1[x1.length - 2];
                    for (j = oldsize; j < newLength; j++) {
                        newX1[j] = newX1[j - 1] + dx1;
                    }
                }
                setXImag(newX1);
            }
        }


    }

    /**
     * Inserts zeros in between existing elements of the data set. The integer argument <i>factor</i> gives the number
     * of zeros per existing data point that must be inserted. The boolean argument <i>before</i> regulates whether the
     * zeros should be inserted before each element (if <i>true</i>) or after (<i>false</i>). The new values of the
     * independent variable are interpolated between those of the old ones, using uniform interpolation even if the data
     * are sampled irregularly. </p><p> If the argument <i>factor</i> is zero or negative, nothing is done. </p><p>
     * Derived types should override this if necessary to provide for the correct handling of parameters and other
     * special features.
     *
     * @param factor The number of zeros per data point to be inserted
     * @param before <i>True</i> if the zeros go before each point, <i>false</i> if after
     */
    public void interpolateZeros(int factor, boolean before) {
        if (factor <= 0) {
            return;
        }

        int jump = factor + 1;
        int size = getDataReal().length;
        int newLength = size * jump;
        int j, k, m, start;

        double[] newReal = new double[newLength];
        double[] oldReal = getDataReal();
        FlatArray.initializeArray(newReal);
        start = (before) ? factor : 0;
        for (j = 0, k = start; j < size; j++, k += jump) {
            newReal[k] = oldReal[j];
        }
        setDataReal(newReal);
        setDimensionLengths(newLength, 0);

        if (isDependentComplex(0)) {
            double[] newImag = new double[newLength];
            double[] oldImag = getDataImag();
            FlatArray.initializeArray(newImag);
            start = (before) ? factor : 0;
            for (j = 0, k = start; j < size; j++, k += jump) {
                newImag[k] = oldImag[j];
            }
            setDataImag(newImag);
        }

        if (isUniform(0)) {
            if (!isTriplet(0)) {
                setIndependentTriplet(Triplet.convertToTriplet(getXArray()), 0);
            }
            getIndependentTriplet(0).setLength(newLength);
            getIndependentTriplet(0).setStep(getIndependentTriplet(0).getStep() / (factor + 1));
            if (before) {
                getIndependentTriplet(0)
                        .setStart(getIndependentTriplet(0).getStart() - factor * getIndependentTriplet(0).getStep());
            }
        } else {
            double[] x = getXArray();
            double[] newX = new double[newLength];
            double dx;
            if (before) {
                dx = (x[1] - x[0]) / (factor + 1);
                k = 0;
                for (m = 0; m < factor; m++) {
                    newX[k] = x[0] - (factor - k) * dx;
                    k += 1;
                }
                newX[k] = x[0];
                for (j = 1; j < x.length; j++) {
                    dx = x[j] - x[j - 1];
                    for (m = 0; m < factor; m++) {
                        newX[k + 1] = newX[k] + dx;
                        k = +1;
                    }
                    newX[k++] = x[j];
                }
            } else {
                k = 0;
                newX[0] = x[0];
                for (j = 1; j < x.length; j++) {
                    dx = x[j] - x[j - 1];
                    for (m = 0; m < factor; m++) {
                        newX[k + 1] = newX[k] + dx;
                        k += 1;
                    }
                    newX[k++] = x[j];
                }
                dx = (x[x.length - 1] - x[x.length - 2]) / (factor + 1);
                for (m = 0; m < factor; m++) {
                    newX[k] = newX[k - 1] + dx;
                    k += 1;
                }
            }
            setX(newX);

            if (isIndependentComplex(0)) {
                double[] x1 = getXImag();
                double[] newX1 = new double[newLength];
                double dx1;
                if (before) {
                    dx1 = (x1[1] - x1[0]) / (factor + 1);
                    k = 0;
                    for (m = 0; m < factor; m++) {
                        newX1[k] = x1[0] - (factor - k) * dx1;
                        k += 1;
                    }
                    newX1[k] = x1[0];
                    for (j = 1; j < x.length; j++) {
                        dx1 = x1[j] - x1[j - 1];
                        for (m = 0; m < factor; m++) {
                            newX1[k + 1] = newX1[k] + dx;
                            k = +1;
                        }
                        newX1[k++] = x1[j];
                    }
                } else {
                    k = 0;
                    newX1[0] = x1[0];
                    for (j = 1; j < x.length; j++) {
                        dx1 = x1[j] - x1[j - 1];
                        for (m = 0; m < factor; m++) {
                            newX1[k + 1] = newX1[k] + dx;
                            k += 1;
                        }
                        newX1[k++] = x1[j];
                    }
                    dx1 = (x1[x1.length - 1] - x1[x1.length - 2]) / (factor + 1);
                    for (m = 0; m < factor; m++) {
                        newX1[k] = newX1[k - 1] + dx1;
                        k += 1;
                    }
                }
                setXImag(newX1);
            }
        }
    }


    /*
    * Implement methods that need to be overridden from superior classes.
    */

    /**
     * Tests to make sure this Object obeys the VectorType data model.
     *
     * @return boolean <i>True</i> if it obeys the GraphType model and if the values of <i>independentVariables</i> and
     *         <i>dependentVariables</i> are right.
     */
    public boolean testDataModel() {
        if (!super.testDataModel()) {
            return false;
        }
        if ((getIndependentVariables() != 1) || (getDependentVariables() != 1)) {
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
        VectorType v = null;
        try {
            v = (VectorType) getClass().newInstance();
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
     * current object. </p><p> In VectorType, this method tests first for compatibility with superior classes and then
     * tests whether the input is a VectorType data object with the same value of <i>isUniform</i> as the current one.
     * It does <b>not</b> require that the independent data values should be the same, since one might want to add
     * together subsequent data sets, <i>e.g.</i> when one performs averages. </p><p> Classes derived from this should
     * over-ride this method with further tests as appropriate. The over-riding method should normally have the first
     * lines <PRE> boolean test = super.isCompatible( obj ); </PRE>followed by other tests. If other types not
     * subclassed from GraphType or Const should be allowed to be compatible then other tests must be implemented.
     *
     * @param obj The data object to be compared with the current one
     * @return <I>True</I> if the object can be combined with the current one
     */
    public boolean isCompatible(TrianaType obj) {
        boolean test = super.isCompatible(obj);
        if ((test) && (obj instanceof VectorType)) {
            test = (isUniform() == ((VectorType) obj).isUniform());
        }
        return test;
    }

    /*
     * Method updateObsoletePointers is used to make the new
     * types derived from TrianaType backward-compatible with
     * older types. It must be called by any method that
     * modifies data in <i>dataContainer</i> or in any other variable
     * that replaces a storage location used previously by any
     * type. It must be implemented (over-ridden) in any type
     * that re-defines storage or access methods to any
     * variable. The implementation should assign the
     * new variables to the obsolete ones, and ensure that
     * obsolete access methods retrieve data from the new
     * locations. Any over-riding method should finish
     * with the line<PRE>
     *       super.updateObsoletePointers;
     * </PRE>
     */

    protected void updateObsoletePointers() {
//        if( isTriplet( 0 ) ) x = null;
        // getIndependentTriplet( 0 ).convertToArray(); // IT MEM FIX !!! storing full x array
        // we don't need to do this as any unit which needs
        // x calculates it anyway.
        // else x = getIndependentArrayReal( 0 );
        y = (double[]) getDataArrayReal(0);
        xlabel = getIndependentLabels(0);
        ylabel = getDependentLabels(0);
        super.updateObsoletePointers();
    }


}






