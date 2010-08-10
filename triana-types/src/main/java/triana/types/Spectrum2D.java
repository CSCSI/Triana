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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import triana.types.util.Triplet;

/**
 * Spectrum2D is the class derived from MatrixType to represent a two-dimensional array whose elements contain the
 * two-dimensional Fourier transform of a two-dimensional data set. Spectrum2D implements the Spectral Interface and the
 * Triana spectral data model, as described in the documentation for ComplexSpectrum, with the exception that it does
 * not allow data to be narrow-band or one-sided; these would make the storage in two dimensions excessively
 * complicated.  Spectrum2D allows data to be real or complex, so it combines the 2D analogues of Spectrum and
 * ComplexSpectrum. </p><p> Spectrum2D assumes the variables are uniformly sampled in both directions. It introduces new
 * parameter arrays <i>resolution</i>, <i>twoSided</i> (set to <i>true</i>, <i>highestFrequency</i>, <i>narrow</i> (set
 * to <i>false</i>), and <i>nFull</i>. These parameters have the same meaning as in ComplexSpectrum, but are
 * 2-dimensional arrays for the 2 spectral dims. </p><p>
 *
 * @author Bernard Schutz
 * @version $Revision: 4048 $
 * @see MatrixType
 * @see TrianaType
 * @see GraphType
 * @see Spectrum
 * @see ComplexSpectrum
 * @see Spectral
 */
public class Spectrum2D extends MatrixType implements AsciiComm, Spectral {

    /*
     * New parameters are:
     */

    /**
     * The frequency resolution of the data in each dimension, in Hz.
     */
    private double[] resolution = new double[2];

    /**
     * The highest frequency represented in each direction, in Hz.
     */
    private double[] highestFrequency = new double[2];

    /**
     * The flags for a two-sided spectrum. These are always <i>true</i> in this implementation, although in principle
     * one could be false without loss of information. A future revision of this type should allow this.
     */
    private boolean[] twoSided = {true, true};

    /**
     * Flags that are always <i>false</i>, because narrow-band spectra are not allowed by this implementation in 2D.
     */
    private boolean[] narrow = {false, false};

    /**
     * Integers giving the number of data points in the full two-sided spectrum from which the present frequency
     * spectrum is derived. These numbers are the lengths of the data sets in each dimension from which the original
     * spectrum could have been obtained by Fourier transformation. In the present implementation, which does not allow
     * narrow-banded or one-sided representations, <i>nFull</i> must equal the dimension of the data set in each
     * direction.
     */
    private int[] nFull = new int[2];


    /**
     * Creates a new empty Spectrum2D, with no parameters set
     */
    public Spectrum2D() {
        super();
    }

    /**
     * Creates a new Spectrum2D with arguments giving whether the data are to be complex. Arrays give the sidedness, the
     * data length, the number of points in the original broad-band spectrum, and the frequency resolution for each
     * dimension. The constructor allocates memory for the data but expects the data to be defined later.
     *
     * @param complex True if the data is to be complex
     * @param lengths Number of frequency points in the current data set
     * @param df      Frequency resolution in each dimension
     */
    public Spectrum2D(boolean complex, int[] lengths, double[] df) {
        this();
        nFull = lengths;
        for (int j = 0; j < 2; j++) {
            if (nFull[j] % 2 == 0) {
                highestFrequency[j] = df[j] * nFull[j] / 2;
            } else {
                highestFrequency[j] = df[j] * (nFull[j] + 1) / 2;
            }
            setFrequencyResolution(df[j], j);
        }
        setXorY(new Triplet(lengths[0]), 0);
        setXorY(new Triplet(lengths[1]), 1);
        if (complex) {
            setData(new double[lengths[0]][lengths[1]], new double[lengths[0]][lengths[1]]);
        } else {
            setData(new double[lengths[0]][lengths[1]]);
        }
    }


    /**
     * Creates a new Spectrum2D with argument arrays giving the 2D data matrix, or two matrices if the data is complex,
     * and the frequency resolution in each dimension.
     *
     * @param zr the real part of the data
     * @param zi the imaginary part of the data, null if absent
     * @param df Frequency resolution
     */
    public Spectrum2D(double[][] zr, double[][] zi, double[] df) {
        this();
        nFull[0] = zr.length;
        nFull[1] = zr[0].length;
        for (int j = 0; j < 2; j++) {
            if (nFull[j] % 2 == 0) {
                highestFrequency[j] = df[j] * nFull[j] / 2;
            } else {
                highestFrequency[j] = df[j] * (nFull[j] + 1) / 2;
            }
            setFrequencyResolution(df[j], j);
        }
        setXorY(new Triplet(nFull[0]), 0);
        setXorY(new Triplet(nFull[1]), 1);
        if (zi != null) {
            setData(zr, zi);
        } else {
            setData(zr);
        }
    }


    /*
    * Methods that are required by the Spectral interface.
    */

    /**
     * Returns the frequency resolution of the data for the given dimension (independent variable).
     *
     * @param dim The index of the independent variable being queried
     * @return The frequency resolution
     */
    public double getFrequencyResolution(int dim) {
        return resolution[dim];
    }

    /**
     * Sets the frequency resolution of the data for the given dimension to the given value.
     *
     * @param dim The index of the independent variable being queried
     * @param df  The frequency resolution
     */
    public void setFrequencyResolution(double df, int dim) {
        resolution[dim] = df;
        if (nFull[dim] % 2 == 0) {
            highestFrequency[dim] = df * nFull[dim] / 2;
        } else {
            highestFrequency[dim] = df * (nFull[dim] + 1) / 2;
        }
    }


    /**
     * Returns <i>true</i>. Included because Spectral demands it.
     *
     * @return boolean True if data are two-sided in frequency space
     */
    public boolean isTwoSided() {
        return true;
    }

    /**
     * Does nothing.
     *
     * @param s True if the data will be two-sided
     */
    public void setTwoSided(boolean s) {
    }

    /**
     * Returns the number of points in the data set in the frequency dimension whose transform could have led to the
     * present data, or equivalently the number of points in the two-sided full-bandwidth spectrum from which the
     * present spectrum could have been derived.
     *
     * @param dim The index of the independent variable being queried
     * @return The number of points in the original data set
     */
    public int getOriginalN(int dim) {
        return nFull[dim];
    }

    /**
     * Sets to the given first argument <i>nOrig</i> the number of points in the data set in the dimension given by the
     * second argument <i>dim</i> whose transform could have led to the present data, or equivalently the number of
     * points in the two-sided full-bandwidth spectrum from which the present spectrum could have been derived.
     *
     * @param dim   The index of the independent variable being queried
     * @param nOrig The new number of points in the original data set
     */
    public void setOriginalN(int nOrig, int dim) {
        nFull[dim] = nOrig;
    }

    /**
     * Returns <i>false</i>. Included because Spectral demands it.
     *
     * @param dim The index of the independent variable being queried
     * @return True if data are narrow-band
     */
    public boolean isNarrow(int dim) {
        return false;
    }

    /**
     * Does nothing.
     *
     * @param n   True if the data held are narrow-band
     * @param dim The index of the independent variable being set
     */
    public void setNarrow(boolean n, int dim) {
    }

    /**
     * Returns the (non-negative) value of the lowest frequency in the frequency band held in the object, for the given
     * dimension <i>dim</i>.  In this version, all data start at zero frequency; no narrow-banding is allowed. This form
     * of the method is required by the Spectral interface.
     *
     * @param dim The index of the independent variable being queried
     * @return The lowest frequency represented in the given direction
     */
    public double getLowerFrequencyBound(int dim) {
        return 0.0;
    }

    /**
     * Returns the (non-negative) value of the highest frequency in the frequency band held in the object, for the given
     * dimension <i>dim</i>.
     *
     * @param dim The index of the independent variable being queried
     * @return The highest frequency represented in the given direction
     */
    public double getUpperFrequencyBound(int dim) {
        return highestFrequency[dim];
    }

    /**
     * Sets or resets the (non-negative) value of the highest frequency in the frequency band held in the object for the
     * given direction <i>dim</i> to the value of the given argument <i>hf</i>.  Since narrow-banding is not allowed,
     * this can only be reset if the frequency resolution is changed. This method makes that change.
     *
     * @param dim The index of the independent variable being queried
     * @param hf  The new highest frequency represented in the given direction
     */
    public void setUpperFrequencyBound(double hf, int dim) {
        highestFrequency[dim] = hf;
        if (nFull[dim] % 2 == 0) {
            resolution[dim] = hf / nFull[dim] * 2;
        } else {
            resolution[dim] = hf / (nFull[dim] + 1) * 2;
        }
    }

    /**
     * Returns the frequency values of the independent data points for the given dimension, in the order of lowest
     * frequency to highest, regardless of how the data are stored internally.
     *
     * @param dim The dimension of the independent variable
     * @return double[] Array of ordered frequency values
     */
    public double[] getFrequencyArray(int dim) {
        int j;
        double f;
        int ln = getDimensionLengths(dim);
        double res = getFrequencyResolution(dim);
        double upper = getUpperFrequencyBound(dim);
        double[] frequencies = new double[ln];

        f = -upper;
        for (j = 0; j < ln; j++) {
            frequencies[j] = f;
            f += res;
        }
        return frequencies;
    }

    /**
     * Returns the real parts of the values of the spectrum (data points) in a two-dimensional array, ordered so that in
     * both dimensions the values correspond to frequencies running from the lowest to the highest, regardless of the
     * internal data model. These points then correspond to the values returned by <i>getFrequencyArray</i> for each
     * dimension.
     *
     * @return Object Multidimensional arrray of ordered spectral values
     */
    public Object getOrderedSpectrumReal() {
        int i, j, k, l, ix, jx, i0, j0;
        double[][] orderedSpectrum = new double[nFull[0]][nFull[1]];
        double[][] data = getDataReal();
        if (nFull[0] % 2 == 0) {
            ix = nFull[0] / 2;
            i0 = ix;
        } else {
            ix = (nFull[0] - 1) / 2;
            i0 = ix + 1;
        }
        if (nFull[1] % 2 == 0) {
            jx = nFull[0] / 2;
            j0 = jx;
        } else {
            jx = (nFull[1] - 1) / 2;
            j0 = jx + 1;
        }
        for (k = 0, i = i0; k < ix; k++, i++) {
            for (l = 0, j = j0; l < jx; l++, j++) {
                orderedSpectrum[k][l] = data[i][j];
            }
        }
        for (k = ix, i = 0; k < nFull[0]; k++, i++) {
            for (l = 0, j = j0; l < jx; l++, j++) {
                orderedSpectrum[k][l] = data[i][j];
            }
        }
        for (k = 0, i = i0; k < ix; k++, i++) {
            for (l = jx, j = 0; l < nFull[1]; l++, j++) {
                orderedSpectrum[k][l] = data[i][j];
            }
        }
        for (k = ix, i = 0; k < nFull[0]; k++, i++) {
            for (l = jx, j = 0; l < nFull[0]; l++, j++) {
                orderedSpectrum[k][l] = data[i][j];
            }
        }
        return orderedSpectrum;
    }

    /**
     * Returns the imaginary parts of the values of the spectrum (data points) in a multidimensional array, ordered so
     * that in each dimension the values correspond to frequencies running from the lowest to the highest, regardless of
     * the internal data model. These points then correspond to the values returned by <i>getFrequencyArray</i> for each
     * dimension.
     *
     * @return Object Multidimensional arrray of ordered spectral values
     */
    public Object getOrderedSpectrumImag() {
        int i, j, k, l, ix, jx, i0, j0;
        double[][] orderedSpectrum = new double[nFull[0]][nFull[1]];
        double[][] data = getDataImag();
        if (nFull[0] % 2 == 0) {
            ix = nFull[0] / 2;
            i0 = ix;
        } else {
            ix = (nFull[0] - 1) / 2;
            i0 = ix + 1;
        }
        if (nFull[1] % 2 == 0) {
            jx = nFull[0] / 2;
            j0 = jx;
        } else {
            jx = (nFull[1] - 1) / 2;
            j0 = jx + 1;
        }
        for (k = 0, i = i0; k < ix; k++, i++) {
            for (l = 0, j = j0; l < jx; l++, j++) {
                orderedSpectrum[k][l] = data[i][j];
            }
        }
        for (k = ix, i = 0; k < nFull[0]; k++, i++) {
            for (l = 0, j = j0; l < jx; l++, j++) {
                orderedSpectrum[k][l] = data[i][j];
            }
        }
        for (k = 0, i = i0; k < ix; k++, i++) {
            for (l = jx, j = 0; l < nFull[1]; l++, j++) {
                orderedSpectrum[k][l] = data[i][j];
            }
        }
        for (k = ix, i = 0; k < nFull[0]; k++, i++) {
            for (l = jx, j = 0; l < nFull[0]; l++, j++) {
                orderedSpectrum[k][l] = data[i][j];
            }
        }
        return orderedSpectrum;
    }

    /*
     * Methods that over-ride methods of higher types for Spectral data sets.
     */

    /**
     * Returns the independent data scaled the way they should be graphed.
     *
     * @param dim The independent dimension under consideration
     * @return The scaled independent data values
     */
    public double[] getIndependentScaleReal(int dim) {
        return getFrequencyArray(dim);
    }

    /**
     * Returns null because the data is real.
     *
     * @param dim The independent dimension under consideration
     * @return The scaled independent data values
     */
    public double[] getIndependentScaleImag(int dim) {
        return null;
    }

    /**
     * Returns the real part of the dependent variable ordered so that the frequency values all run monotonically
     * upwards.
     *
     * @return Object An array containing the rearranged data values
     */
    public Object getGraphArrayReal(int dv) {
        if (dv == 0) {
            return getOrderedSpectrumReal();
        }
        return null;
    }

    /**
     * Returns the imaginary part of the dependent variable ordered so that the frequency values all run monotonically
     * upwards.
     *
     * @return Object An array containing the rearranged data values
     */
    public Object getGraphArrayImag(int dv) {
        if (dv == 0) {
            return getOrderedSpectrumImag();
        }
        return null;
    }


    /*
    * Implement methods that need to be overridden from superior classes.
    */

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
        Spectrum2D s = null;
        try {
            s = (Spectrum2D) getClass().newInstance();
            s.copyData(this);
            s.copyParameters(this);
        }
        catch (IllegalAccessException ee) {
            System.out.println("Illegal Access: " + ee.getMessage());
        }
        catch (InstantiationException ee) {
            System.out.println("Couldn't be instantiated: " + ee.getMessage());
        }
        return s;
    }

    /**
     * Copies modifiable parameters from the argument object to the current object. The copying is by value, not by
     * reference. Parameters are defined as data not held in <i>dataContainer</i>. They are modifiable if they have
     * <i>set...</i> methods. Parameters that cannot be modified, but which are set by constructors, should be placed
     * correctly into the copied object when it is constructed. </p><p> This must be overridden by any subclass that
     * defines new parameters. The overriding method should invoke its super method. It should use the <i>set...</i> and
     * <i>get...</i> methods for the parameters in question. This method is protected so that it cannot be called except
     * by objects that inherit from this one. It is called by <i>copyMe</i>.
     *
     * @param source Data object that contains the data to be copied.
     */
    protected void copyParameters(TrianaType source) {
        super.copyParameters(source);
        for (int dim = 0; dim < 2; dim++) {
            setFrequencyResolution(((Spectrum2D) source).getFrequencyResolution(dim), dim);
            highestFrequency[dim] = ((Spectrum2D) source).getUpperFrequencyBound(dim);
            nFull[dim] = ((Spectrum2D) source).getOriginalN(dim);
        }
    }


    /**
     * Used when Triana types want to be able to send ASCII data to other programs using strings.  This is used to
     * implement socket and to run other executables, written in C or other languages. With ASCII you don't have to
     * worry about ENDIAN'ness as the conversions are all done via text. This is obviously slower than binary
     * communication since you have to format the input and output within the other program. </p><p> This method must be
     * overridden in every subclass that defines new data or parameters. The overriding method should first call<<PRE>
     * super.outputToStream(dos) </PRE>to get output from superior classes, and then new parameters defined for the
     * current subclass must be output. Moreover, subclasses that first dimension their data arrays must explicitly
     * transfer these data arrays.
     *
     * @param dos The data output stream
     */
    public void outputToStream(PrintWriter dos) throws IOException {
        super.outputToStream(dos);
        for (int dim = 0; dim < 2; dim++) {
            dos.println(getUpperFrequencyBound(dim));
            dos.println(getOriginalN(dim));
            dos.println(getFrequencyResolution(dim));
        }
    }

    /**
     * Used when Triana types want to be able to receive ASCII data from the output of other programs.  This is used to
     * implement socket and to run other executables, written in C or other languages. With ASCII you don't have to
     * worry about ENDIAN'ness as the conversions are all done via text. This is obviously slower than binary
     * communication since you have to format the input and output within the other program. </p><p> This method must be
     * overridden in every subclass that defines new data or parameters. The overriding method should first call<PRE>
     * super.inputFromStream(dis) </PRE>to get input from superior classes, and then new parameters defined for the
     * current subclass must be input. Moreover, subclasses that first dimension their data arrays must explicitly
     * transfer these data arrays.
     *
     * @param dis The data input stream
     */
    public void inputFromStream(BufferedReader dis) throws IOException {
        super.inputFromStream(dis);
        for (int dim = 0; dim < 2; dim++) {
            highestFrequency[dim] = (Double.valueOf(dis.readLine())).doubleValue();
            nFull[dim] = (Integer.valueOf(dis.readLine())).intValue();
            setFrequencyResolution((Double.valueOf(dis.readLine())).doubleValue(), dim);
        }
    }

    /**
     * Tests the argument object to determine if it makes sense to perform arithmetic operations between it and the
     * current object. </p><p> In Spectrum2D, this method tests for compatibility with superior classes, to see that the
     * input object is a Spectrum2D, and to see if the relevant parameters are equal. It does not enforce equality of
     * <i>acquisitionTime</i> because it may be desirable to compare two data sets acquired at different times. </p><p>
     * Classes derived from this should over-ride this method with further tests as appropriate. The over-riding method
     * should normally have the first lines <PRE> boolean test = super.isCompatible( obj ); </PRE>followed by other
     * tests. If other types not subclassed from GraphType or Const should be allowed to be compatible then other tests
     * must be implemented.
     *
     * @param obj The data object to be compared with the current one
     * @return <I>True</I> if the object can be combined with the current one
     */
    public boolean isCompatible(TrianaType obj) {
        boolean test = super.isCompatible(obj);
        if ((test) && (obj instanceof Spectrum2D)) {
            for (int dim = 0; dim < 2; dim++) {
                if (getFrequencyResolution(dim) != ((Spectrum2D) obj).getFrequencyResolution(dim)) {
                    return false;
                }
                if (getUpperFrequencyBound(dim) != ((Spectrum2D) obj).getUpperFrequencyBound(dim)) {
                    return false;
                }
            }
        }
        return test;
    }

    /**
     * Determines whether the argument TrianaType is equal to the current Spectrum2D. They are equal if the argument is
     * a Spectrum2D with the same size, parameters, and data. </p><p> This method must be over-ridden in derived types.
     * In a derived type called xxx the method should begin<PRE> if ( !( obj instanceof xxx ) ) return false; if (
     * !isCompatible( obj ) ) return false; </PRE>followed by tests that are specific to type xxx (testing its own
     * parameters) and then as a last line<PRE> return super.equals( obj ); </PRE>This line invokes the other equals
     * methods up the chain to GraphType. Each superior object tests its own parameters. </p><p>
     *
     * @param obj The object being tested
     * @return <i>true</i> if they are equal or <i>false</i> otherwise
     */
    public boolean equals(TrianaType obj) {
        if (!(obj instanceof Spectrum2D)) {
            return false;
        }
        if (!isCompatible(obj)) {
            return false;
        }
        return super.equals(obj);
    }


}



