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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * TimeFrequency is the class derived from MatrixType to represent
 * a two-dimensional array whose elements contain frequency information
 * associated with different times. The time-axis is the horizontal
 * dimension of the matrix (dimension 0) and the frequency axis is
 * the vertical dimension of the matrix (dimension 1).
 * TimeFrequency implements the Spectral Interface and
 * the Triana spectral data model, as described in the documentation
 * for ComplexSpectrum. TimeFrequency allows data to be real or
 * complex. It also implements the Signal Interface to provide
 * information along the time-dimension.
 * </p><p>
 * TimeFrequency assumes the variables are uniformly sampled in
 * both directions. It introduces new parameters <i>resolution</i>,
 * <i>twoSided</i>, <i>highestFrequency</i>, <i>narrow</i>, and
 * <i>nFull</i>, as in ComplexSpectrum. It further introduces
 * parameters <i>samplingRate</i> and <i>acquisitionTime</i>, as
 * in SampleSet. Finally it introduces <i>interval</i>, the
 * time-interval between successive time-sets along the time axis.
 * </p><p>
 * @see MatrixType
 * @see TrianaType
 * @see GraphType
 * @see SampleSet
 * @see ComplexSpectrum
 * @see Spectral
 * @see Signal
 *
 * @author      Bernard Schutz
 * @created     30 November 2000
 * @version     $Revision: 4048 $
 * @date        $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public class TimeFrequency extends MatrixType implements AsciiComm, Spectral, Signal {

    /*
     * New parameters are:
     */

    /**
     * The frequency resolution of the frequency-axis data, in Hz.
     */
    private double resolution;

    /**
     * The highest frequency represented along the frequency axis, in Hz.
     */
    private double highestFrequency;

    /**
     * The flag for a two-sided spectrum. This is <i>true</i> if the
     * frequency axis data is two-sided.
     */
    private boolean twoSided;

    /**
     * A flag that is <i>true</i> if the frequency data represents a narrow bandwidth derived
     * from a full-bandwidth spectrum. If the data are one-sided but have
     * not been filtered to a narrower bandwidth, this should be set to <i>false</i>.
     */
    private boolean narrow;

    /**
     * An integer giving the number of data points in the full two-sided
     * spectrum from which the present frequency spectrum is derived. This number is
     * the length of the data set from which the original spectrum
     * could have been obtained by Fourier transformation.
     */
    private int nFull;

    /**
     * The sampling frequency (in samples per second, or Hz).
     */
    private double samplingRate;

    /**
     * The time of acquisition of the first data sample of the first
     * data set. It is measured in seconds from the zero
     * of time, which must be externally defined by the experiment.
     */
    private double acquisitionTime;

    /**
     * The time interval between the start of each of the successive
     * time-series along the time axis. It is measured in seconds from the zero
     * of time, which must be externally defined by the experiment.
     */
    private double interval;

    /**
     * Creates a new empty TimeFrequency, with no parameters set
     */
    public TimeFrequency() {
        super();
    }

    /**
     * Creates a new TimeFrequency with arguments giving whether the data
     * are to be complex, the sidedness,
     * whether it is narrow-band or broad-band, the data length, the
     * number of points in the original broad-band spectrum, the frequency
     * resolution, the highest frequency in the current spectrum, the
     * interval between data sets along the time-axis, the number of
     * data sets, and the acquisition time of the first data point.
     * No memory is allocated for the data.
     *
     * @param complex True if the data is to be complex
     * @param ts True if the new TimeFrequency is to be two-sided
     * @param nrw True if the new TimeFrequency is to be narrow-band
     * @param lenf Number of frequency points in the current data set
     * @param nOrig Number of points in the original two-sided full spectrum
     * @param df Frequency resolution
     * @param hf Highest frequency in the current data set
     * @param iv The time-interval between sets along the time-axis
     * @param lent The number of time-sets
     * @param acTime The time of acquisition of the first sample of the first set
     */
    public TimeFrequency(boolean complex, boolean ts, boolean nrw, int lenf, int nOrig, double df, double hf, double iv, int lent, double acTime) {
        this();
        twoSided = ts;
        narrow = nrw;
        nFull = nOrig;
        if (!narrow) {
            if (twoSided) {
                if (nFull > lenf) {
                    System.out.println("Warning: attempt to create two-sided full-bandwidth ComplexSpectrum with too short a length of the input data set. Assume the data set is narrow-band.");
                    narrow = true;
                }
                else if (nFull < lenf) {
                    System.out.println("Warning: attempt to create two-sided full-bandwidth ComplexSpectrum with too few original samples. Assume that the original data set length is equal to the input data length.");
                    nFull = lenf;
                }
            }
            else {
                if (lenf < nFull / 2 + 1) { //integer division!
                    System.out.println("Warning: attempt to create one-sided full-bandwidth ComplexSpectrum with too short a length of the input data set. Assume the data set is narrow-band.");
                    narrow = true;
                }
                else if (lenf > nFull / 2 + 1) {
                    System.out.println("Warning: attempt to create one-sided full-bandwidth ComplexSpectrum with too few original samples. Assume that the original data set length is an even number whose one-sided length is the input data length.");
                    nFull = 2 * (lenf - 1);
                }
            }
        }
        if (!narrow) {
            if (df * (nFull / 2) != hf) { //integer division!
                System.out.println("Warning: attempt to create two-sided full-bandwidth ComplexSpectrum with inconsistency between the frequency resolution and the highest frequency. Use the frequency resolution to set the highest frequency.");
                hf = (nFull / 2) * df;
            }
        }
        else {
            if (df * (nFull / 2) < hf) {
                System.out.println("Warning: attempt to create two-sided narrow-bandwidth ComplexSpectrum with too large a highest frequency. Use the frequency resolution to set the highest frequency.");
                hf = (nFull / 2) * df;
            }
        }
        highestFrequency = hf;
        resolution = df;
        samplingRate = nFull * df;
        acquisitionTime = acTime;
        setXorY(new Triplet(lent, acTime, iv), 0);
        setXorY(new Triplet(lenf), 1);
    }


    /**
     * Creates a new TimeFrequency with arguments giving the 2D data matrix,
     * or two matrices if the data is complex, the sidedness,
     * whether it is narrow-band or broad-band, the
     * number of points in the original broad-band spectrum, the frequency
     * resolution, the highest frequency in the current spectrum, the
     * interval between data sets along the time-axis,
     * and the acquisition time of the first data point.
     * Matrices are arranged so that the first index is the time-step
     * and the second the frequency.
     *
     * @param zr the real part of the data
     * @param zi the imaginary part of the data
     * @param ts True if the new TimeFrequency is to be two-sided
     * @param nrw True if the new TimeFrequency is to be narrow-band
     * @param nOrig Number of points in the original two-sided full spectrum
     * @param df Frequency resolution
     * @param hf Highest frequency in the current data set
     * @param iv The time-interval between sets along the time-axis
     * @param acTime The time of acquisition of the first sample of the first set
     */
    public TimeFrequency(double[][] zr, double[][] zi, boolean ts, boolean nrw, int nOrig, double df, double hf, double iv, double acTime) {
        this((zi != null), ts, nrw, zr[0].length, nOrig, df, hf, iv, zr.length, acTime);
        if (zi != null)
            setData(zr, zi);
        else
            setData(zr);
    }

    /**
     * Creates a new TimeFrequency with a given complexity, sidedness, number of points
     * that there would be in a single two-sided spectrum, frequency resolution,
     * interval between time-sets, number of time-sets, and acquisition tim eof
     * the first sample. It does not allocate memory for the data. It
     * assumes that the frequency data are full-bandwidth.
     *
     * @param complex True if the data is to be complex
     * @param ts True if the new TimeFrequency is to be two-sided
     * @param nOrig Number of points in the original two-sided full spectrum
     * @param df Frequency resolution
     * @param iv The time-interval between sets along the time-axis
     * @param lent The number of time-sets
     * @param acTime The time of acquisition of the first sample of the first set
     */
    public TimeFrequency(boolean complex, boolean ts, int nOrig, double df, double iv, int lent, double acTime) {
        this(complex, ts, false, ((ts) ? nOrig : nOrig / 2 + 1), nOrig, df, df * (nOrig / 2), iv, lent, acTime);
    }

    /**
     * Creates a new, real, one-sided, wide-band TimeFrequency from a
     * given data matrix and given values of the time and frequency resolutions
     * and the acquisition time.
     *
     * @param z The input matix of real one-sided spectra
     * @param nOrig Number of points in the original two-sided full spectrum
     * @param df Frequency resolution
     * @param iv The time-interval between sets along the time-axis
     * @param acTime The time of acquisition of the first sample of the first set
     */
    TimeFrequency(double[][] z, int nOrig, double df, double iv, double acTime) {
        this(z, null, false, false, nOrig, df, df * (nOrig / 2), iv, acTime); //integer division!
    }




    /*
     * Methods that are required by the Spectral interface. Since the frequency
     * dimension is <i>dim=1</i>, methods that have arguments expect the value
     * 1; other values give returns that convey no information. Since there
     * is only one frequency dimension, we supply also
     * versions without the dimension argument <i>dim</i>.
     */

    /**
     * Returns the frequency resolution
     * of the data for the given dimension (independent variable). Since
     * the frequency dimension is 1, this argument must equal 1 for a sensible
     * return.
     * This form of the method is required by the Spectral interface.
     *
     * @param dim The index of the independent variable being queried
     * @return The frequency resolution
     */
    public double getFrequencyResolution(int dim) {
        if (dim == 1) return getFrequencyResolution();
        return 0;
    }

    /**
     * Returns the frequency resolution
     * of the data along the frequency axis.
     *
     * @return double The frequency resolution
     */
    public double getFrequencyResolution() {
        return resolution;
    }

    /**
     * Sets the frequency resolution of the data for the given dimension to the
     * given value.  Since the frequency dimension is 1, this argument must
     * be set to 1 or the method will have no effect. This
     * form of the method is required by the Spectral interface.
     *
     * @param dim The index of the independent variable being queried
     * @param f The frequency resolution
     */
    public void setFrequencyResolution(double f, int dim) {
        if (dim == 1) setFrequencyResolution(f);
    }

    /**
     * Sets the frequency resolution of the data along the frequency axis to the
     * given value.
     *
     * @param df The frequency resolution
     */
    public void setFrequencyResolution(double df) {
        resolution = df;
        setSamplingRate(df * nFull);
    }


    /**
     * Returns <i>true</i> if the frequency data are stored as a two-sided
     * transform, i.e. containing both the positive and negative frequency
     * data. If <i>false</i>, the data are one-sided, containing only positive
     * frequencies.
     *
     * @return boolean True if data are two-sided in frequency space
     */
    public boolean isTwoSided() {
        return twoSided;
    }

    /**
     * Sets the two-sidedness flag to the value of the argument.
     *
     * @param s True if the data will be two-sided
     */
    public void setTwoSided(boolean s) {
        twoSided = s;
    }

    /**
     * Returns the number of points in the data set in the frequency dimension
     * whose transform could have led to the present data, or equivalently
     * the number of points in the two-sided full-bandwidth spectrum
     * from which the present spectrum could have been derived. Since the
     * frequency dimension has index 1, the argument must equal 1 for a sensible
     * return.
     *
     * @param dim The index of the independent variable being queried
     * @return The number of points in the original data set
     */
    public int getOriginalN(int dim) {
        if (dim == 1) return getOriginalN();
        return 0;
    }

    /**
     * Returns the number of points in the data set associated with each time
     * (each column of the matrix)
     * whose transform could have led to the present data, or equivalently
     * the number of points in the two-sided full-bandwidth spectrum
     * from which the present spectrum could have been derived.
     *
     * @return int The number of points in the original data set
     */
    public int getOriginalN() {
        return nFull;
    }

    /**
     * Sets to the given first argument <i>nOrig</i> the number of points in
     * the data set associated with each time (each column of the matrix)
     * in the dimension given by the second argument <i>dim</i>
     * whose transform could have led to the present data, or equivalently
     * the number of points in the two-sided full-bandwidth spectrum
     * from which the present spectrum could have been derived. Since
     * the frequency dimension is index 1, the second argument must equal
     * 1 for a sensible return.
     *
     * @param dim The index of the independent variable being queried
     * @param nOrig The new number of points in the original data set
     */
    public void setOriginalN(int nOrig, int dim) {
        if (dim == 1) setOriginalN(nOrig);
    }

    /**
     * Sets to the given argument the number of points in the data set
     * associated with each time (each column of the matrix)
     * whose transform could have led to the present data, or equivalently
     * the number of points in the two-sided full-bandwidth spectrum
     * from which the present spectrum could have been derived.
     *
     * @param nOrig The new number of points in the original data set
     */
    public void setOriginalN(int nOrig) {
        nFull = nOrig;
    }

    /**
     * Returns true if the data for the given dimension represent a narrow
     * bandwidth derived from a full-band spectrum. Since the frequency
     * dimension has index 1, the argument must equal 1 for a ssensible return.
     *
     * @param dim The index of the independent variable being queried
     * @return True if data are narrow-band
     */
    public boolean isNarrow(int dim) {
        if (dim == 1) return isNarrow();
        return false;
    }

    /**
     * Returns true if the data represent a narrow bandwidth
     * derived from a full-band spectrum.
     *
     * @return True if data are narrow-band
     */
    public boolean isNarrow() {
        return narrow;
    }

    /**
     * Sets the narrow-bandedness flag associated with the given
     * dimension to the value of the argument. Since the frequency
     * dimension has index 1, the second argument must equal 1.
     *
     * @param n True if the data held are narrow-band
     * @param dim The index of the independent variable being set
     */
    public void setNarrow(boolean n, int dim) {
        if (dim == 1) setNarrow(n);
    }

    /**
     * Sets the narrow-bandedness flag to the value of the argument.
     *
     * @param n True if the data held are narrow-band
     */
    public void setNarrow(boolean n) {
        narrow = n;
    }

    /**
     * Returns the (non-negative) value
     * of the lowest frequency in the frequency band held in the object,
     * for the given dimension <i>dim</i>. Since the frequency dimension is
     * 1, the argument must equal 1 to give a sensible return.
     *
     * @param dim The index of the independent variable being queried
     * @return The lowest frequency represented in the given direction
     */
    public double getLowerFrequencyBound(int dim) {
        if (dim == 1) return getLowerFrequencyBound();
        return -1;
    }


    /**
     * Returns the (non-negative) value
     * of the lowest frequency in the frequency band held in the object.
     *
     * @return double The lowest frequency represented in the given direction
     */
    public double getLowerFrequencyBound() {
        int n = (getDataReal()[0]).length;
        double lower = 0.0;
        if (narrow) {
            if (!twoSided)
                lower = highestFrequency - (n - 1) * resolution;
            else {
                if (n % 2 == 0)
                    lower = highestFrequency - (n / 2 - 1) * resolution;
                else {
                    if (highestFrequency == getNyquist())
                        lower = highestFrequency - (n / 2) * resolution;
                    else
                        lower = 0.0;
                }
            }
        }
        return lower;
    }

    /**
     * Returns the (non-negative) value
     * of the highest frequency in the frequency band held in the object,
     * for the given dimension <i>dim</i>. Since the frequency dimension is
     * 1, the argument should equal 1 to give a sensible return.
     *
     * @param dim The index of the independent variable being queried
     * @return The highest frequency represented in the given direction
     */
    public double getUpperFrequencyBound(int dim) {
        if (dim == 1) return getUpperFrequencyBound();
        return 0;
    }

    /**
     * Returns the (non-negative) value
     * of the highest frequency in the frequency band held in the object.
     *
     * @return double The highest frequency represented in the given direction
     */
    public double getUpperFrequencyBound() {
        return highestFrequency;
    }


    /**
     * Sets or resets the (non-negative) value
     * of the highest frequency in the frequency band held in the object
     * for the given direction <i>dim</i> to the value of the given
     * argument <i>hf</i>. Since the frequency dimension is 1, the second
     * argument should equal1 for a sensible action.
     * This method should be used after altering
     * parameters or data in the object if they have
     * changed the upper frequency bound.
     * This should also set the
     * <i>isNarrow</i> flag if the given value is not the highest value
     * of the original-length spectrum.
     *
     * @param dim The index of the independent variable being queried
     * @param hf The new highest frequency represented in the given direction
     */
    public void setUpperFrequencyBound(double hf, int dim) {
        if (dim == 1) setUpperFrequencyBound(hf);
    }

    /**
     * Sets the (non-negative) value
     * of the highest frequency in the frequency band held in the object to
     * the given value. Should be used after altering
     * parameters or data in the object if they have
     * changed the upper frequency bound. This should also set the
     * <i>isNarrow</i> flag if the given value is not the highest value
     * of the original-length spectrum.
     *
     * @param hf The new highest frequency
     */
    public void setUpperFrequencyBound(double hf) {
        highestFrequency = hf;
        if (hf != (nFull / 2) * resolution) setNarrow(true); // integer division!!
    }

    /**
     * Returns the frequency values of the independent data points for
     * the given dimension, in the order of lowest frequency to highest,
     * regardless of how the data are stored internally. Since the frequency
     * dimension is index 1, the argument should equal 1 for a sensible return.
     *
     * @param dim The dimension of the independent variable
     * @return double[] Array of ordered frequency values
     */
    public double[] getFrequencyArray(int dim) {
        int j;
        double f;
        int ln = getDimensionLengths(dim);
        double res = getFrequencyResolution(dim);
        double lower = getLowerFrequencyBound(dim);
        double upper = getUpperFrequencyBound(dim);
        boolean twoSided = isTwoSided();
        double[] frequencies = new double[ln];

        if (lower == 0.0) {
            if (twoSided) {
                f = -upper;
                for (j = 0; j < ln; j++) {
                    frequencies[j] = f;
                    f += res;
                }
            }
            else {
                f = 0;
                for (j = 0; j < ln; j++) {
                    frequencies[j] = f;
                    f += res;
                }
            }
        }
        else {
            if (twoSided) {
                f = -upper;
                j = 0;
                while (f <= -lower) {
                    frequencies[j] = f;
                    f += res;
                    j++;
                }
                f = lower;
                while (j < ln) {
                    frequencies[j] = f;
                    f += res;
                    j++;
                }
            }
            else {
                f = lower;
                for (j = 0; j < ln; j++) {
                    frequencies[j] = f;
                    f += res;
                }
            }
        }
        return frequencies;
    }

    /**
     * Returns the real parts of the values of the spectrum (data points) in a
     * two-dimensional array, ordered so that in the frequency dimension
     * (which is the vertical dimension, index 1)
     * the values correspond to frequencies running from the lowest
     * to the highest. These
     * points then correspond to the values returned by
     * <i>getFrequencyArray</i> for the frequency dimension.
     *
     * @return Object Multidimensional arrray of ordered spectral values
     */
    public Object getOrderedSpectrumReal() {
        double[][] timeSpectrum = getDataReal();
        if (isTwoSided()) {
            int i, j, k;
            int lent = getDimensionLengths(0);
            int ln = getDimensionLengths(1);
            double[][] data = timeSpectrum;
            timeSpectrum = new double[lent][ln];
            int l2 = ln / 2; // the number of negative-f elements
            if ((ln % 2 != 0) && (getLowerFrequencyBound() != 0)) l2 = (ln + 1) / 2;
            for (i = 0; i < lent; i++) {
                for (k = 0, j = ln - l2; k < l2; k++, j++) timeSpectrum[i][k] = data[i][j];
                for (k = l2, j = 0; k < ln; k++, j++) timeSpectrum[i][k] = data[i][j];
            }
        }
        return timeSpectrum;
    }

    /**
     * Returns the imaginary parts of the values of the spectrum (data points) in a
     * multidimensional array, ordered so that in the frequency dimension
     * (index 1, the vertical dimension of the matrix)
     * the values correspond to frequencies running from the lowest
     * to the highest. These
     * points then correspond to the values returned by
     * <i>getFrequencyArray</i> for each dimension.
     *
     * @return Object Multidimensional arrray of ordered spectral values
     */
    public Object getOrderedSpectrumImag() {
        if (!isDependentComplex(0)) return null;
        double[][] timeSpectrum = getDataImag();
        if (isTwoSided()) {
            int i, j, k;
            int lent = getDimensionLengths(0);
            int ln = getDimensionLengths(1);
            double[][] data = timeSpectrum;
            timeSpectrum = new double[lent][ln];
            int l2 = ln / 2; // the number of negative-f elements
            if ((ln % 2 != 0) && (getLowerFrequencyBound() != 0)) l2 = (ln + 1) / 2;
            for (i = 0; i < lent; i++) {
                for (k = 0, j = ln - l2; k < l2; k++, j++) timeSpectrum[i][k] = data[i][j];
                for (k = l2, j = 0; k < ln; k++, j++) timeSpectrum[i][k] = data[i][j];
            }
        }
        return timeSpectrum;
    }

    /*
     * Methods that provide further information about the spectral
     * data set. These depend on the Triana storage model.
     */


    /**
     * Returns the Nyquist frequency, defined to be the highest frequency
     * that this data set could contain if it were not narrow-banded.
     * For full-bandwidth sets, this is the same value as is
     * returned by <i>getUpperFrequencyBound()</i>, but for narrow-band
     * data sets it may be higher.
     * </p><p>
     * The general formula is that if
     * the original number of data points (given by <i> nFull =
     * getOriginalN()</i>) is even, then the Nyquist frequency is
     * <i>nFull * resolution / 2</i>. If the original number of data points
     * is odd, then the Nyquist frequency is
     * <i>(nFull -1 ) * resolution / 2</i>.
     *
     * @return double The Nyquist frequency
     */
    public double getNyquist() {
        double nyquist = highestFrequency;
        if (narrow) nyquist = (nFull / 2) * resolution; //integer division!
        return nyquist;
    }


    /**
     * Implement methods of the Signal interface
     */

    /**
     * Returns the sampling frequency of the data. If
     * the data are irregularly sampled it returns 0.
     *
     * @return double The sampling frequency
     */
    public double getSamplingRate() {
        if (isUniform(0)) return samplingRate;
        return 0;
    }

    /**
     * Sets the sampling rate of the signal and
     * adjusts the data for the independent variable accordingly.
     * If the sampling frequency argument <i>r</i> is negative, nothing is done.
     * If it is zero, irregular sampling is assumed. If positive, the
     * sampling frequency is set to <i>r</i> and the values of the independent
     * variable are re-adjusted accordingly if necessary, assuming the
     * same starting time as before the change in the rate. This method
     * also writes the new sampling frequency into the <i>description</i>
     * StringVector. If
     * there is an audio format, it is updated with the new sampling rate.
     *
     * @param r The sampling rate
     */
    public void setSamplingRate(double r) {
        if (r < 0) return;
        samplingRate = r;
        if (r > 0) {
            if (isTriplet(0))
                getXorYTriplet(0).setStep(1 / r);
            else {
                double[] x = getXorYArray(0);
                setXorY(new Triplet(x.length, x[0], 1 / r), 0);
            }
        }
        else {
            if (isTriplet(0)) setXorY(null, 0);
        }
    }

    /**
     * Returns the acquisition time as a double
     * giving the number of seconds since the reference time, which
     * should be the same reference time as for the method <i>setAcquisitionTime</i>.
     * The time set is interpreted as the moment of acquisition
     * of the first sample in the data set.
     *
     * @return double The time of acquisition in seconds since the reference time
     */
    public double getAcquisitionTime() {
        return acquisitionTime;
    }

    /**
     * Sets the acquisition time. This
     * method also writes the acquisition time into the <i>description</i>
     * StringVector. If the given acquisition time is not equal
     * to the starting time of the independent variable, all values
     * of the independent variable are shifted by the difference
     * between the given time and the previous starting time.
     *
     * @param t The acquisition time in seconds
     */
    public void setAcquisitionTime(double t) {
        double[] d;
        int j;
        double shift;
        if (isTriplet(0))
            getXorYTriplet(0).setStart(t);
        else {
            d = getXorYArray(0);
            shift = t - d[0];
            if (shift != 0) for (j = 0; j < d.length; j++) d[j] += shift;
        }
        acquisitionTime = t;
    }

    /*
     * Methods unique to TimeFrequency
     */

    /**
     * Gets the interval between data sets, in seconds.
     *
     * @return double The interval between data sets
     */
    public double getInterval() {
        return interval;
    }

    /**
     * Sets the interval between data sets, in seconds.
     *
     * @param iv The new interval between data sets, in seconds
     */
    public void setInterval(double iv) {
        interval = iv;
    }

    /*
     * Methods that over-ride methods of higher types for Spectral data sets.
     */

    /**
     * Returns the independent data scaled the way they should be graphed.
     * The time-axis values are returned if the argument index is 0, the
     * frequency-axis values in the correct order are returned if the argument
     * is 1.
     *
     * @param dim The independent dimension under consideration
     * @return The scaled independent data values
     */
    public double[] getIndependentScaleReal(int dim) {
        double[] scale = null;
        if (dim == 0) scale = super.getIndependentScaleReal(0);
        if (dim == 1) scale = getFrequencyArray(dim);
        return scale;
    }

    /**
     * Returns null because the independent data on both axes are real.
     *
     * @param dim The independent dimension under consideration
     * @return The scaled independent data values
     */
    public double[] getIndependentScaleImag(int dim) {
        return null;
    }

    /**
     * Returns the real part of the dependent variable ordered so
     * that the frequency values all run monotonically upwards.
     * Since there is only one dependent variable the index must be 0.
     *
     * @return Object An array containing the rearranged data values
     */
    public Object getGraphArrayReal(int dv) {
        if (dv == 0) return getOrderedSpectrumReal();
        return null;
    }

    /**
     * Returns the imaginary part of the dependent variable ordered so
     * that the frequency values all run monotonically upwards.
     * Since there is only one dependent variable the index must be 0.
     *
     * @return Object An array containing the rearranged data values
     */
    public Object getGraphArrayImag(int dv) {
        if (dv == 0) return getOrderedSpectrumImag();
        return null;
    }



    /*
     * Implement methods that need to be overridden from superior classes.
     */

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
        TimeFrequency m = null;
        try {
            m = (TimeFrequency) getClass().newInstance();
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
     * Copies modifiable parameters from the argument object
     * to the current object. The copying is by value, not by
     * reference. Parameters are defined as data not held in
     * <i>dataContainer</i>. They are modifiable if they have
     * <i>set...</i> methods. Parameters that cannot be modified, but
     * which are set by constructors, should be placed correctly
     * into the copied object when it is constructed.
     * </p><p>
     * This must be overridden by any subclass that defines new parameters.
     * The overriding method should invoke its super method. It should use
     * the <i>set...</i> and <i>get...</i> methods for the parameters in question.
     * This method is protected so that it cannot be called except by
     * objects that inherit from this one. It is called by <i>copyMe</i>.
     *
     * @param source Data object that contains the data to be copied.
     */
    protected void copyParameters(TrianaType source) {
        super.copyParameters(source);
        nFull = ((TimeFrequency) source).getOriginalN();
        interval = ((TimeFrequency) source).getInterval();
        highestFrequency = ((TimeFrequency) source).getUpperFrequencyBound();
        setTwoSided(((TimeFrequency) source).isTwoSided());
        setNarrow(((TimeFrequency) source).isNarrow());
        setFrequencyResolution(((TimeFrequency) source).getFrequencyResolution());
        setAcquisitionTime(((TimeFrequency) source).getAcquisitionTime());
    }


    /**
     * Used when Triana types want to be able to
     * send ASCII data to other programs using strings.  This is used to
     * implement socket and to run other executables, written in C or
     * other languages. With ASCII you don't have to worry about
     * ENDIAN'ness as the conversions are all done via text. This is
     * obviously slower than binary communication since you have to format
     * the input and output within the other program.
     * </p><p>
     * This method must be overridden in every subclass that defines new
     * data or parameters. The overriding method should first call<<PRE>
     *      super.outputToStream(dos)
     * </PRE>to get output from superior classes, and then new parameters defined
     * for the current subclass must be output. Moreover, subclasses
     * that first dimension their data arrays must explicitly transfer
     * these data arrays.
     *
     * @param dos The data output stream
     */
    public void outputToStream(PrintWriter dos) throws IOException {
        super.outputToStream(dos);
        dos.println(getSamplingRate());
        dos.println(getAcquisitionTime());
        dos.println(getUpperFrequencyBound());
        dos.println(isTwoSided());
        dos.println(isNarrow());
        dos.println(getOriginalN());
        dos.println(getFrequencyResolution());
        dos.println(getInterval());
    }

    /**
     * Used when Triana types want to be able to
     * receive ASCII data from the output of other programs.  This is used to
     * implement socket and to run other executables, written in C or
     * other languages. With ASCII you don't have to worry about
     * ENDIAN'ness as the conversions are all done via text. This is
     * obviously slower than binary communication since you have to format
     * the input and output within the other program.
     * </p><p>
     * This method must be overridden in every subclass that defines new
     * data or parameters. The overriding method should first call<PRE>
     *      super.inputFromStream(dis)
     * </PRE>to get input from superior classes, and then new parameters defined
     * for the current subclass must be input. Moreover, subclasses
     * that first dimension their data arrays must explicitly transfer
     * these data arrays.
     *
     * @param dis The data input stream
     */
    public void inputFromStream(BufferedReader dis) throws IOException {
        super.inputFromStream(dis);
        setSamplingRate((Double.valueOf(dis.readLine())).doubleValue());
        acquisitionTime = (Double.valueOf(dis.readLine())).doubleValue();
        highestFrequency = (Double.valueOf(dis.readLine())).doubleValue();
        setTwoSided((Boolean.valueOf(dis.readLine())).booleanValue());
        setNarrow((Boolean.valueOf(dis.readLine())).booleanValue());
        nFull = (Integer.valueOf(dis.readLine())).intValue();
        setFrequencyResolution((Double.valueOf(dis.readLine())).doubleValue());
        interval = (Double.valueOf(dis.readLine())).doubleValue();
    }

    /**
     * Tests the argument object to determine if
     * it makes sense to perform arithmetic operations between
     * it and the current object.
     * </p><p>
     * In TimeFrequency, this method tests for compatibility with superior
     * classes, to see that the input object is a TimeFrequency, and to see
     * if the relevant parameters are equal. It does not enforce equality
     * of <i>acquisitionTime</i> because it may be desirable to compare
     * two data sets acquired at different times.
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
        if ((test) && (obj instanceof TimeFrequency)) {
            if (getFrequencyResolution() != ((TimeFrequency) obj).getFrequencyResolution()) return false;
            if (getUpperFrequencyBound() != ((TimeFrequency) obj).getUpperFrequencyBound()) return false;
            if (isTwoSided() != ((TimeFrequency) obj).isTwoSided()) return false;
            if (isNarrow() != ((TimeFrequency) obj).isNarrow()) return false;
            if (getSamplingRate() != ((TimeFrequency) obj).getSamplingRate()) return false;
            if (getInterval() != ((TimeFrequency) obj).getInterval()) return false;
        }
        return test;
    }

    /**
     * Determines whether the argument TrianaType is equal to
     * the current TimeFrequency. They are equal if the argument is
     * a TimeFrequency with the same size, parameters, and data.
     * </p><p>
     * This method must be over-ridden in derived types. In a derived
     * type called xxx the method should begin<PRE>
     *	     if ( !( obj instanceof xxx ) ) return false;
     *       if ( !isCompatible( obj ) ) return false;
     * </PRE>followed by tests that are specific to type xxx (testing its
     * own parameters) and then as a last line<PRE>
     * 	     return super.equals( obj );
     * </PRE>This line invokes the other equals methods up the chain to
     * GraphType. Each superior object tests its own parameters.
     * </p><p>
     * @param obj The object being tested
     * @return <i>true</i> if they are equal or <i>false</i> otherwise
     */
    public boolean equals(TrianaType obj) {
        if (!(obj instanceof TimeFrequency)) return false;
        if (!isCompatible(obj)) return false;
        if (getAcquisitionTime() != ((TimeFrequency) obj).getAcquisitionTime()) return false;
        return super.equals(obj);
    }

    /**
     * Class method that takes an input narrow-band TimeFrequency
     * and reduces <i>nFull</i> to be compatible with the upper
     * frequency bound, so that the set is no longer narrow-band
     * at the top. This is useful in doing resampling of time-series
     * data. The Nyquist frequency is the highest frequency allowed
     * by the sampling rate. If the input is not narrow-band, then
     * the method returns null.
     *
     * @param inputNarrow The input narrow-band spectrum
     * @param allocMem <i>True</i> if the output is a modified copy of input
     * @return The reduced-sampling-rate spectrum
     */
    public static TimeFrequency reduceNyquist(TimeFrequency inputNarrow, boolean allocMem) {

        if (!inputNarrow.isNarrow()) return null;

        TimeFrequency output = (allocMem) ? (TimeFrequency) inputNarrow.copyMe() : inputNarrow;

        if (output.size()[1] == 1) return output;

        double highF = output.getUpperFrequencyBound();
        double oldNyquist = output.getNyquist();
        if (oldNyquist == highF) return output;

        int oldN = output.getOriginalN();
        int newN, newNarrowN, high;
        boolean even = (oldN % 2 == 0);
        boolean twoSided = output.isTwoSided();
        boolean zeroPresent = (output.getLowerFrequencyBound() == 0);
        double res = output.getFrequencyResolution();
        boolean complex = output.isDependentComplex(0);
        double[][] mreal = output.getDataReal();
        double[][] mimag = null;
        if (complex) mimag = output.getDataImag();
        int sets = mreal.length;
        int len = mreal[0].length;
        double[] real, imag, temp;
        int k;

        if (even) {
            newN = (int) Math.round(2 * highF / res);
            if (!twoSided && complex)
                for (k = 0; k < sets; k++) mimag[k][len - 1] = 0;
            else {
                high = len / 2 - 1;
                newNarrowN = len - 1;
                for (k = 0; k < sets; k++) {
                    temp = new double[newNarrowN];
                    real = mreal[k];
                    System.arraycopy(real, 0, temp, 0, len / 2);
                    System.arraycopy(real, len / 2 + 1, temp, len / 2, newNarrowN - len / 2);
                    temp[high] = (real[high] + real[high + 1]) / 2; // ensure that spectra from real time-series go back to real time-series
                    mreal[k] = temp;
                }
                if (complex)
                    for (k = 0; k < sets; k++) {
                        temp = new double[newNarrowN];
                        imag = mimag[k];
                        System.arraycopy(imag, 0, temp, 0, len / 2);
                        System.arraycopy(imag, len / 2 + 1, temp, len / 2, newNarrowN - len / 2);
                        temp[high] = (imag[high] + imag[high + 1]) / 2; // ensure that spectra from real time-series go back to real time-series
                        mimag[k] = temp;
                    }
            }
        }
        else {
            newN = (int) Math.round(2 * highF / res + 1);  // if old set was odd, so is new set
        }
        output.setOriginalN(newN);
        output.setData(mreal, mimag);
        if (zeroPresent) output.setNarrow(false);
        output.setSamplingRate(highF * 2);
        return output;

    }


}
