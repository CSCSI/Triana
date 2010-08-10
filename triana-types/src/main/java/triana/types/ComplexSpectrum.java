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
 * ComplexSpectrum stores a one-dimensional array of double-precision complex numbers representing a complex Fourier
 * spectrum. It includes a Triplet giving the integer values of the index of this array, and five new parameters: a
 * double <i>resolution</i> giving the frequency resolution; a double <i>highestFrequency</i> giving the value of the
 * largest frequency represented in the data set; an int <i>nFull</i> giving the number of points in the original data
 * set from which the data here were derived (used if the data have been reduced to one-sided or narrow-band spectra);
 * and two boolean flags, (i) a flag <i>twoSided</i> that says whether the ComplexSpectrum is one-sided or two-sided,
 * and (ii) a flag <i>narrow</i> that says whether the data are a narrow bandwidth derived from a larger full-bandwidth
 * spectrum. ComplexSpectrum is the basic Triana class for holding one-dimensional Fourier tranforms. It is derived from
 * the VectorType class and implements the Spectral interface. </p><p> ComplexSpectrum implements the Triana model for
 * storing spectral data. The model is general enough to contain a multi-dimensional Fourier transform derived from a
 * complex data set containing an arbitrary number of points. It is complete enough to ensure that inverse Fourier
 * transforms can be done correctly automatically, even if the data set is a narrow-band spectrum extracted from a full
 * spectrum that obeys the Triana model. For two-sided spectra, the Triana storage model the same as that required by
 * the Triana FFT unit and is one of the standard FFT storage models.  For one-sided data it is not quite the same as
 * the close packing that is used by most Fast Fourier Transform (FFT) methods, so the Triana FFT unit converts from the
 * Triana storage model to its own internal format. The Triana model is described fully here. First we deal with
 * two-sided complete spectra, then one-sided. At the end we describe the way narrow bands are handled. The various
 * descriptions assume a one-dimensional data set (one independent variable), but they apply to each dimension of
 * multi-dimensional sets. </p><p> All spectral representations assume that the frequency values are uniformly spaced
 * and are represented by an integer index <i>k</i>. The value of <i>resolution</i> is the (positive) difference between
 * the frequencies associated with consecutive values of <i>k</i>. The index <i>k</i> is non-negative. </p><p> A
 * two-sided ComplexSpectrum contains spectral values at both negative and positive frequencies. Values at non-negative
 * frequencies are stored in the first part of the data vector in order of increasing frequency, and values at negative
 * frequencies are stored in the second part, again in order of increasing frequency. The details depend on whether the
 * total length of the spectral data set, <i>N</i>, is even or odd. </p><p> Two-sided, full-bandwidth spectra of even
 * length <i>N</i> (<i>nFull</i> = <i>N</i> even, <i>narrow</i> = <i>false</i>, <i>twoSided</i> = <i>true</i>):<br> The
 * frequency corresponding to the index <i>k</i> contained in the interval [0, <i>N</i>/2-1] is <i>k</i> *
 * <i>resolution</i>; the frequency corresponding to index <i>k</i> in [<i>N</i>/2, <i>N</i>-1] is (<i>k</i> - <i>N</i>)
 * * <i>resolution</i>. Thus, the highest absolute value of the frequency is associated with <i>k</i> = <i>N</i>/2. This
 * frequency is -(<i>N</i>/2) * <i>resolution</i>, and it is called the Nyquist frequency. It should be equal to
 * -<i>highestFrequency</i>. This is a consistency check. For even <i>N</i>, the spectral amplitude at this largest
 * negative frequency is the same as that which could be associated with the largest positive frequency, +(<i>N</i>/2) *
 * <i>resolution</i>. If this spectrum was or could have been derived as a Fourier transform of a real data set, then
 * the data values associated with <i>k</i> = 0 and <i>k</i> = <i>N</i>/2 are real, and the data values at indices
 * <i>k</i> and <i>N</i>-<i>k</i> are complex-conjugates of one another. Such a spectrum can be converted to a one-sided
 * representation without the loss of information. </p><p> Two-sided, full-bandwidth spectra of odd length <i>N</i>
 * (<i>nFull</i> = <i>N</i> odd, <i>narrow</i> = <i>false</i>, <i>twoSided</i> = <i>true</i>):<br> The frequency
 * corresponding to the index <i>k</i> contained in the interval [0, (<i>N</i>-1)/2] is <i>k</i> * <i>resolution</i>;
 * the frequency corresponding to index <i>k</i> in [(<i>N</i>+1)/2, <i>N</i>-1] is (<i>k</i> - <i>N</i>) *
 * <i>resolution</i>. Thus, the highest positive frequency is associated with <i>k</i> = (<i>N</i>-1)/2; the lowest
 * negative frequency with <i>k</i> = (<i>N</i>+1)/2; both these frequencies have absolute value (<i>N</i>-1)/2 *
 * <i>resolution</i>; and this is called the Nyquist frequency. This should equal <i>highestFrequency</i> (this is a
 * consistency check). The spectral amplitudes at the lowest and highest-frequency points are generally different when
 * <i>N</i> is odd. If this spectrum was or could have been derived as a Fourier transform of a real data set, then the
 * data value associated with <i>k</i> = 0 is real, and the data values at indices <i>k</i> and <i>N</i>-<i>k</i> are
 * complex-conjugates of one another. Such a spectrum can be converted to a one-sided representation without the loss of
 * information. </p><p> A one-sided ComplexSpectrum includes only non-negative frequencies. The data model contains
 * enough information to perform an inverse Fourier transform on the spectrum on the assumption that the resulting data
 * set will contain only real values. Thus, values at the negative frequencies that are not represented in the data set
 * will be assumed to be the complex conjugates of the values at the corresponding positive frequencies. Details depend
 * on whether the total number of data points in the corresponding two-sided spectrum was even or odd. For transforms of
 * real data, one-sided representations are preferable because they require only about half the memory of two-sided
 * representations, and they nevertheless contain the same information. </p><p> One-sided, full-bandwidth spectra of
 * length <i>N</i> derived from a two-sided spectrum with an even number of elements (<i>nFull</i> even, <i>narrow</i> =
 * <i>false</i>, <i>twoSided</i> = <i>false</i>):<br> In a one-sided spectrum containing <i>N</i> data points, the
 * frequency corresponding to element <i>k</i> contained in [0, <i>N</i>-1] is <i>k</i> * <i>resolution</i>. The highest
 * such frequency is (<i>N</i>-1) * <i>resolution</i>, and this should be equal to <i>highestFrequency</i>. This is a
 * consistency check. Since the orginal time-series is assumed to be real, the element at <i>k</i> = 0 should be real.
 * For this case, where <i>nFull</i> is even, the original spectrum (or time-series) must have contained 2*<i>N</i>-2
 * points. This should be the value of <i>nFull</i>, and this is a further consistency check. The highest-frequency
 * element of the current data set (index <i>N</i>-1) contains the value of the original spectrum at its highest
 * frequency (index <i>N</i>-1 in that set), and this should be real as well. Thus a two-sided full-bandwidth spectrum
 * derived from real data of length <i>nFull</i> = 2*<i>N</i>-2 can be converted to a one-sided spectrum just by
 * extracting the first <i>N</i> elements.  Since the first and last of these elements are real, this extracted set
 * contains 2*<i>N</i>-2 independent numbers. This storage model differs from those used by some FFT methods, typically
 * because in those methods the two real elements are combined into a single complex storage location, saving one
 * complex storage element. The storage saving is small and it would make manipulating spectra in Triana (such as adding
 * or graphing them) clumsy, so it is not implemented. FFT units in Triana must perform any requisite conversions to
 * their internal storage model themselves. </p><p> One-sided, full-bandwidth spectra of length <i>N</i> derived from a
 * two-sided spectrum with an odd number of elements (<i>nFull</i> odd, <i>narrow</i> = <i>false</i>, <i>twoSided</i> =
 * <i>false</i>):<br> In a one-sided spectrum containing <i>N</i> data points, the frequency corresponding to element
 * <i>k</i> contained in [0, <i>N</i>-1] is <i>k</i> * <i>resolution</i>. The highest such frequency is (<i>N</i>-1) *
 * <i>resolution</i>, and this should be equal to <i>highestFrequency</i>. This is a consistency check. Since the
 * orginal time-series is assumed to be real, the element at <i>k</i> = 0 should be real. For this case, where
 * <i>nFull</i> is odd, the original spectrum (or time-series) must have contained 2*<i>N</i> - 1 points. This should be
 * the value of <i>nFull</i>, and this is a further consistency check. The highest-frequency element of the current data
 * set (index <i>N</i>-1) contains the value of the original spectrum at its highest frequency (index <i>N</i>-1 in that
 * set), and this will in general be complex. Thus a two-sided full-bandwidth spectrum derived from real data of length
 * <i>nFull</i> = 2*<i>N</i>-1 can be converted to a one-sided spectrum just by extracting the first <i>N</i> elements.
 * Since the first of these elements is real, this extracted set contains 2*<i>N</i>-1 independent numbers. The remarks
 * in the previous paragraph about the data storage model for FFT methods apply here as well. </p><p> The Triana
 * spectral data model allows for the bandwidth of the data to be narrower than that of the set from which it was
 * derived, and the parameter <i>highestFrequency</i> is included in this class mainly to indicate the value of the
 * upper edge of the frequency band. The lower edge can be deduced from the other data, such as the number of points and
 * <i>resolution</i>. The Triana data storage model for narrow-band spectra places certain restrictions on the way such
 * spectra can be constructed, so as to preserve as much as possible of the information needed to invert the spectrum
 * back to a time-series. The information enables one to convert the data back to a full-bandwidth spectrum by padding
 * with zeros. </p><p> Two-sided, narrow-band spectra of length <i>N</i> derived from a two-sided spectrum with an even
 * number of elements (<i>nFull</i> even, <i>narrow</i> = <i>true</i>, <i>twoSided</i> = <i>true</i>):<br> Two-sided
 * narrow-band data must always contain data points for related positive and negative frequencies. Removing the upper
 * part of the spectrum requires removing the single highest-frequency value and then pairs of values for the contiguous
 * frequencies (each pair being a frequency and its negative), i.e. removing in total an odd number of data points.
 * Removing the lower part of the spectrum similarly requires removing an odd number of data points. Since the original
 * data set had an even number of elements, we can determine the nature of the bandwidth of the narrow-band spectrum as
 * follows. If <i>N</i> (actual number of elements) is even, then the band has been shrunk from both sides; the lower
 * frequency limit is <i>highestFrequency</i> - (<i>N</i>/2 - 1) * <i>resolution</i> and should not be zero. The whole
 * original frequency range can be reconstructed from these numbers and <i>nFull</i>. If <i>N</i> is odd, then either
 * the highest original frequency or the lowest is still present in the band; if the highest is still present then
 * <i>highestFrequency</i> = <i>nFull</i>/2 * <i>resolution</i>; if the lowest is still present then the lower frequency
 * limit in this case, <i>highestFrequency</i> - ( (<i>N</i>-1)/2 ) * <i>resolution</i>, must be zero. A consistency
 * test is that one of these conditions must hold if <i>N</i> is odd. </p><p> Two-sided, narrow-band spectra of length
 * <i>N</i> derived from a two-sided spectrum with an odd number of elements (<i>nFull</i> odd, <i>narrow</i> =
 * <i>true</i>, <i>twoSided</i> = <i>true</i>):<br> Two-sided narrow-band data must always contain data points for
 * related positive and negative frequencies. Removing the lower part of the spectrum requires removing the single
 * zero-frequency value and then pairs of values for the contiguous frequencies (each pair being a frequency and its
 * negative), i.e. removing in total an odd number of data points. This will make <i>N</i> (the actual number of
 * elements) even. Removing the upper part of the spectrum or a portion in the middle requires removing an even number
 * of data points in frequency pairs. This will make <i>N</i> odd. Therefore, if <i>N</i> is even, the lower part of the
 * spectrum has been removed, and the upper part may or may not have been removed. In this case, if
 * <i>highestFrequency</i> = (<i>nFull</i>-1)/2 * <i>resolution</i>, then the upper part of the original band is still
 * present. If <i>N</i> is odd, then the lower part of the spectrum is still present and the upper has been removed.
 * </p><p> One-sided narrow-band spectra of length <i>N</i> derived from a two-sided spectrum with an even number of
 * elements (<i>nFull</i> even, <i>narrow</i> = <i>true</i>, <i>twoSided</i> = <i>false</i>):<br> A one-sided spectrum
 * is assumed derived from a two-sided spectrum that came from a Fourier transform of a real data set. This is then made
 * narrow-band by removing frequencies at the lower and/or upper ends of the full band. If <i>highestFrequency</i> =
 * <i>nFull</i>/2 * <i>resolution</i>, then the top of the band is still present, and (for consistency) the
 * highest-frequency data value should be real. The lower frequency limit is <i>highestFrequency</i> - (<i>N</i>-1) *
 * <i>resolution</i>, and this should be larger than zero. If the top of the band is missing, then one can calculate the
 * lower frequency limit as above; if this is zero then the bottom of the band is still present, and the
 * lowest-frequency data value should be real (consistency again). If the lower frequency limit is positive, then the
 * bottom of the band is missing. These data are sufficient to reconstruct the two-sided narrow-band spectrum associated
 * with this data set on the assumption that the inverse transform produces a real data set. </p><p> One-sided
 * narrow-band spectra of length <i>N</i> derived from a two-sided spectrum with an odd number of elements (<i>nFull</i>
 * odd, <i>narrow</i> = <i>true</i>, <i>twoSided</i> = <i>false</i>):<br> A one-sided spectrum is assumed derived from a
 * two-sided spectrum that came from a Fourier transform of a real data set. This is then made narrow-band by removing
 * frequencies at the lower and/or upper ends of the full band. If <i>highestFrequency</i> = (<i>nFull</i>-1)/2 *
 * <i>resolution</i>, then the top of the band is still present, and (for consistency) the highest-frequency data value
 * should be real. The lower frequency limit is <i>highestFrequency</i> - (<i>N</i>-1) * <i>resolution</i>, and this
 * should be larger than zero. If the top of the band is missing, then one can calculate the lower frequency limit as
 * above; if this is zero then the bottom of the band is still present, and the lowest-frequency data value should be
 * real (consistency again). If the lower frequency limit is positive, then the bottom of the band is missing. These
 * data are sufficient to reconstruct the two-sided narrow-band spectrum associated with this data set on the assumption
 * that the inverse transform produces a real data set. </p><p> ComplexSpectrum contains a number of methods for
 * accessing and modifying these parameters.
 *
 * @author Ian Taylor
 * @author Bernard Schutz
 * @version $Revision: 4048 $
 * @see Spectral
 * @see SampleSet
 * @see Spectrum
 * @see GraphType
 * @see triana.types.util.Triplet
 */
public class ComplexSpectrum extends VectorType implements Spectral, AsciiComm {

    /*
     * Begin with the new parameters added by ComplexSpectrum to the
     * data of the VectorType class from which it is derived.
     */

    /**
     * The frequency resolution, in Hz.
     */
    private double resolution;

    /**
     * The highest frequency represented by the data, in Hz.
     */
    private double highestFrequency;

    /**
     * The flag for a two-sided spectrum. This is <i>true</i> if the data is two-sided.
     */
    private boolean twoSided;

    /**
     * A flag that is <i>true</i> if the data represents a narrow bandwidth derived from a full-bandwidth spectrum. If
     * the data are one-sided but have not been filtered to a narrower bandwidth, this should be set to <i>false</i>.
     */
    private boolean narrow;

    /**
     * An integer giving the number of data points in the full two-sided spectrum from which the present spectrum is
     * derived. This number is the length of the data set from which the original spectrum could have been obtained by
     * Fourier transformation.
     */
    private int nFull;

    /*
     * Obsolete data and parameter definitions retained for consistency with
     * the earlier version of this type. They should not be used in new
     * units and will be removed when possible. The data items are treated
     * here as pointers to the data in the <i>dataContainer</i> and are kept
     * up to date by various methods.
     */

    /**
     * (Parameter that is kept for consistency with previous versions, but which is obsolete. It must be kept to the
     * right values by the <i>updateObsoletePointers</i> method and/or constructors and <i>set...</i> methods.) </p><p>
     * The sampling frequency -- this will not be supported in later versions, since it is part of the Signal interface
     * and not the Spectral interface
     */
    public double samplingFrequency;

    /**
     * (Parameter that is kept for consistency with previous versions, but which is obsolete. It must be kept to the
     * right values by the <i>updateObsoletePointers</i> method and/or constructors and <i>set...</i> methods.) </p><p>
     * The frequency resolution -- this is obsolete because it is public, and has been replaced by the private parameter
     * <i>resolution</i>.
     */
    public double frequencyResolution;

    /**
     * (Parameter that is kept for consistency with previous versions, but which is obsolete. It must be kept to the
     * right values by the <i>updateObsoletePointers</i> method and/or constructors and <i>set...</i> methods.) </p><p>
     * The imaginary part of the signal
     */
    public double[] imag;

    /**
     * (Parameter that is kept for consistency with previous versions, but which is obsolete. It must be kept to the
     * right values by the <i>updateObsoletePointers</i> method and/or constructors and <i>set...</i> methods.) </p><p>
     * The real part of the signal
     */
    public double[] real;

    /*
     * Begin with Constructors.
     */


    /**
     * Create and empty ComplexSpectrum with no parameters set.
     */
    public ComplexSpectrum() {
        super();
    }

    /**
     * This is an obsolete Constructor that will be eliminated when possible. It uses <i>samplingFrequency</i>, a
     * parameter that is obsolete. It is distinguished from the constructors that should be used by not having a boolean
     * argument. This Constructor creates a new one-sided ComplexSpectrum with a certain sampling frequency and number
     * of points. It also allocates memory for the data but does not initialise it.  Use <i>initialiseData</i> to set
     * all elements to zero.
     *
     * @param sf     is the sampling frequency
     * @param points is the number of points
     * @see #initialiseData
     */
    public ComplexSpectrum(double sf, int points) {
        this();
        setFrequencyResolution((sf / 2.0) / (double) points);
        twoSided = false;
        setX(new Triplet(points));
        setData(new double[points], new double[points]);
        samplingFrequency = sf;
    }

    /**
     * This is an obsolete Constructor that creates a new one-sided ComplexSpectrum with a certain sampling frequency
     * and the actual allocated real data. The imaginary data is allocated and intialised to 0. It uses
     * <i>samplingFrequency</i>, a parameter that is obsolete. It is distinguished from the constructors that should be
     * used by not having a boolean argument.
     *
     * @param samplingFrequency is the sampling frequency
     * @param real              the actual allocated data
     */
    public ComplexSpectrum(double samplingFrequency, double[] real) {
        this(samplingFrequency, real.length);
        imag = new double[real.length];
        for (int k = 0; k < real.length; k++) {
            imag[k] = 0.0;
        }
        setData(real, imag);
    }

    /**
     * This is an obsolete Constructor that creates a new one-sided ComplexSpectrum with a certain sampling frequency
     * and the actual allocated complex data. It uses <i>samplingFrequency</i>, a parameter that is obsolete. It is
     * distinguished from the constructors that should be used by not having a boolean argument.
     *
     * @param samplingFrequency is the sampling frequency
     * @param real              The real part of the given data
     * @param imag              The imaginary part of the given data
     */
    public ComplexSpectrum(double samplingFrequency, double[] real, double[] imag) {
        this(samplingFrequency, real.length);
        setData(real, imag);
    }

    /**
     * Creates a new ComplexSpectrum with arguments giving the sidedness, whether it is narrow-band or broad-band, the
     * data length, the number of points in the original broad-band spectrum, the frequency resolution, and the highest
     * frequency in the current spectrum. It does not allocate data. Various checks are performed on the consistency of
     * the input parameters with the Triana spectral storage model, and error messages are printed to
     * <i>System.out</i>.
     *
     * @param ts    <i>True</i> if the new ComplexSpectrum is to be two-sided
     * @param nrw   <i>True</i> if the new ComplexSpectrum is to be narrow-band
     * @param len   Number of points in the current data set
     * @param nOrig Number of points in the original two-sided full spectrum
     * @param df    Frequency resolution
     * @param hf    Highest frequency in the current data set
     */
    public ComplexSpectrum(boolean ts, boolean nrw, int len, int nOrig, double df, double hf) {
        this();
        twoSided = ts;
        narrow = nrw;
        nFull = nOrig;
        if (!narrow) {
            if (twoSided) {
                if (nFull > len) {
                    System.out.println(
                            "Warning: attempt to create two-sided full-bandwidth ComplexSpectrum with too short a length of the input data set. Assume the data set is narrow-band.");
                    narrow = true;
                } else if (nFull < len) {
                    System.out.println(
                            "Warning: attempt to create two-sided full-bandwidth ComplexSpectrum with too few original samples. Assume that the original data set length is equal to the input data length.");
                    nFull = len;
                }
            } else {
                if (len < nFull / 2 + 1) { //integer division!
                    System.out.println(
                            "Warning: attempt to create one-sided full-bandwidth ComplexSpectrum with too short a length of the input data set. Assume the data set is narrow-band.");
                    narrow = true;
                } else if (len > nFull / 2 + 1) {
                    System.out.println(
                            "Warning: attempt to create one-sided full-bandwidth ComplexSpectrum with too few original samples. Assume that the original data set length is an even number whose one-sided length is the input data length.");
                    nFull = 2 * (len - 1);
                }
            }
        }
        if (!narrow) {
            if (df * (nFull / 2) != hf) { //integer division!
                //if ( df * ((double)nFull / 2.0) != hf) {
                System.out.println(
                        "Warning: attempt to create two-sided full-bandwidth ComplexSpectrum with inconsistency between the frequency resolution and the highest frequency. Use the frequency resolution to set the highest frequency.");
                hf = (nFull / 2) * df;
            }
        } else {
            if (df * (nFull / 2) < hf) {
                System.out.println(
                        "Warning: attempt to create two-sided narrow-bandwidth ComplexSpectrum with too large a highest frequency. Use the frequency resolution to set the highest frequency.");
                hf = (nFull / 2) * df;
            }
        }
        highestFrequency = hf;
        setFrequencyResolution(df);
        setX(new Triplet(len));
    }

    /**
     * Creates a new ComplexSpectrum with arguments giving the sidedness, whether it is narrow-band or broad-band, the
     * data arrays, the number of points in the original broad-band spectrum, the frequency resolution, and the highest
     * frequency in the current spectrum.
     *
     * @param ts    <i>True</i> if the new ComplexSpectrum is to be two-sided
     * @param nrw   <i>True</i> if the new ComplexSpectrum is to be narrow-band
     * @param real  Real part of the data
     * @param imag  Imaginary part of the data
     * @param nOrig Number of points in the original two-sided full spectrum
     * @param df    Frequency resolution
     * @param hf    Highest frequency in the current data set
     */
    public ComplexSpectrum(boolean ts, boolean nrw, double[] real, double[] imag, int nOrig, double df, double hf) {
        this(ts, nrw, real.length, nOrig, df, hf);
        setData(real, imag);
    }

    /**
     * Creates a new ComplexSpectrum with a given sidedness, number of points that there would be in the two-sided
     * spectrum, and frequency resolution. It does not allocate memory for the data. It assumes that the data are
     * full-bandwidth.
     *
     * @param ts    <i>True</i> if the new ComplexSpectrum is to be two-sided
     * @param nOrig Number of points in the equivalent two-sided spectrum
     * @param df    Frequency resolution
     */
    public ComplexSpectrum(boolean ts, int nOrig, double df) {
        this(ts, false, ((ts) ? nOrig : nOrig / 2 + 1), nOrig, df, df * (nOrig / 2));
    }

    /**
     * Creates a new ComplexSpectrum with given sidedness, data arrays, number of points in the two-sided spectrum, and
     * frequency resolution. It assumes that the data are full-bandwidth. The lengths of the input data arrays will be
     * shorter than the size of the two-sided spectrum if the input is one-sided.
     *
     * @param ts    <i>True</i> if the new ComplexSpectrum is to be two-sided
     * @param real  The real part of the input spectrum
     * @param imag  The imaginary part of the input spectrum
     * @param nOrig Number of points in the equivalent two-sided spectrum
     * @param df    Frequency resolution
     */
    public ComplexSpectrum(boolean ts, double[] real, double[] imag, int nOrig, double df) {
        this(ts, false, real, imag, nOrig, df, df * (nOrig / 2));
    }

    /**
     * Creates a ComplexSpectrum from a (real) Spectrum by setting the imaginary part of the data to zero. The second
     * argument is used to choose whether the real data are copied or passed by reference.
     *
     * @param s    The input data set
     * @param copy True if the new data are to be copied from the old, false if passed by reference
     */
    public ComplexSpectrum(Spectrum s, boolean copy) {
        this(s.isTwoSided(), s.isNarrow(), s.size(), s.getOriginalN(), s.getFrequencyResolution(),
                s.getUpperFrequencyBound());
        if (copy) {
            if (s.isTriplet()) {
                this.setX(s.getXTriplet().copy());
            } else {
                double[] oldX = s.getXArray();
                double[] newX = new double[oldX.length];
                System.arraycopy(oldX, 0, newX, 0, oldX.length);
                this.setX(newX);
            }
            double[] oldData = s.getData();
            double[] tmpr = new double[oldData.length];
            System.arraycopy(oldData, 0, tmpr, 0, oldData.length);
            double[] tmpi = new double[tmpr.length];
            //	    FlatArray.initializeArray( tmpi );
            this.setData(tmpr, tmpi);
        } else {
            if (s.isTriplet()) {
                this.setX(s.getXTriplet());
            } else {
                this.setX(s.getXArray());
            }
            this.setData(s.getData(), new double[s.getData().length]);
        }
    }

    /**
     * Creates a ComplexSpectrum from a (real) Spectrum by setting the imaginary part of the data to zero. The real data
     * are passed by reference.
     *
     * @param s The input data set
     */
    public ComplexSpectrum(Spectrum s) {
        this(s, false);
    }

    /**
     * Added by I. Taylor, August 2001 : This function sets the default labeling scheme used for the axis by this data
     * type. All constructors call this function to set default values for the axis.
     */
    public void setDefaultAxisLabelling() {
        String labelx = "Frequency (Hz)";
        String labely = "Amplitude";
        setIndependentLabels(0, labelx);
        setDependentLabels(0, labely);
    }


    /*
     * Methods that are required by the Spectral interface. Many of them
     * are designed for multi-dimensional data sets, so we supply here
     * a version without the dimension index <i>dim</i> for each method
     * in order to make them natural to use for one-dimensional sets.
     */

    /**
     * Returns the frequency resolution of the data for the given dimension (independent variable). Since there is only
     * one dimension in this object, this argument is ignored. This form of the method is required by the Spectral
     * interface.
     *
     * @param dim The index of the independent variable being queried
     * @return The frequency resolution
     */
    public double getFrequencyResolution(int dim) {
        return getFrequencyResolution();
    }

    /**
     * Returns the frequency resolution of the data.
     *
     * @return double The frequency resolution
     */
    public double getFrequencyResolution() {
        return resolution;
    }

    /**
     * Sets the frequency resolution of the data for the given dimension to the given value. Since the data are one
     * dimensional, the dimension argument is in fact ignored. This form of the method is required by the Spectral
     * interface.
     *
     * @param dim The index of the independent variable being queried
     * @param df  The frequency resolution
     */
    public void setFrequencyResolution(double df, int dim) {
        setFrequencyResolution(df);
    }

    /**
     * Sets the frequency resolution of the data to the given value.
     */
    public void setFrequencyResolution(double df) {
        resolution = df;
        samplingFrequency = df * nFull; //obsolete parameter
        updateObsoletePointers();
    }


    /**
     * Returns <i>true</i> if the data are stored as a two-sided transform, i.e. containing both the positive and
     * negative frequency data. If <i>false</i>, the data are one-sided, containing only positive frequencies.
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
     * Returns the number of points in the data set in the given dimension whose transform could have led to the present
     * data, or equivalently the number of points in the two-sided full-bandwidth spectrum from which the present
     * spectrum could have been derived. Because the set is one-dimensional, the dimension argument is ignored. This
     * form of the method is required by the Spectral interface.
     *
     * @param dim The index of the independent variable being queried
     * @return The number of points in the original data set
     */
    public int getOriginalN(int dim) {
        return getOriginalN();
    }

    /**
     * Returns the number of points in the data set whose transform could have led to the present data, or equivalently
     * the number of points in the two-sided full-bandwidth spectrum from which the present spectrum could have been
     * derived.
     *
     * @return int The number of points in the original data set
     */
    public int getOriginalN() {
        return nFull;
    }

    /**
     * Sets to the given first argument <i>nOrig</i> the number of points in the data set in the dimension given by the
     * second argument <i>dim</i> whose transform could have led to the present data, or equivalently the number of
     * points in the two-sided full-bandwidth spectrum from which the present spectrum could have been derived. Because
     * the data are one-dimensional, the value of <i>dim</i> is ignored.
     *
     * @param dim   The index of the independent variable being queried
     * @param nOrig The new number of points in the original data set
     */
    public void setOriginalN(int nOrig, int dim) {
        setOriginalN(nOrig);
    }

    /**
     * Sets to the given argument the number of points in the data set whose transform could have led to the present
     * data, or equivalently the number of points in the two-sided full-bandwidth spectrum from which the present
     * spectrum could have been derived.
     *
     * @param nOrig The new number of points in the original data set
     */
    public void setOriginalN(int nOrig) {
        nFull = nOrig;
    }

    /**
     * Returns true if the data for the given dimension represent a narrow bandwidth derived from a full-band spectrum.
     * This form of the method is required by the Spectral interface.
     *
     * @param dim The index of the independent variable being queried
     * @return True if data are narrow-band
     */
    public boolean isNarrow(int dim) {
        return isNarrow();
    }

    /**
     * Returns true if the data represent a narrow bandwidth derived from a full-band spectrum.
     *
     * @return True if data are narrow-band
     */
    public boolean isNarrow() {
        return narrow;
    }

    /**
     * Sets the narrow-bandedness flag associated with the given dimension to the value of the argument. Since the data
     * are one-dimensional, the value of <i>dim</i> is ignored. This form of the method is required by the Spectral
     * interface.
     *
     * @param n   True if the data held are narrow-band
     * @param dim The index of the independent variable being set
     */
    public void setNarrow(boolean n, int dim) {
        setNarrow(n);
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
     * Returns the (non-negative) value of the lowest frequency in the frequency band held in the object, for the given
     * dimension <i>dim</i>. The data are one-dimensional so <i>dim</i> is ignored. This form of the method is required
     * by the Spectral interface.
     *
     * @param dim The index of the independent variable being queried
     * @return The lowest frequency represented in the given direction
     */
    public double getLowerFrequencyBound(int dim) {
        return getLowerFrequencyBound();
    }


    /**
     * Returns the (non-negative) value of the lowest frequency in the frequency band held in the object.
     *
     * @return double The lowest frequency represented in the given direction
     */
    public double getLowerFrequencyBound() {
        int n = getDataReal().length;
        double lower = 0.0;
        if (narrow) {
            if (!twoSided) {
                lower = highestFrequency - (n - 1) * resolution;
            } else {
                if (n % 2 == 0) {
                    lower = highestFrequency - (n / 2 - 1) * resolution;
                } else {
                    if (highestFrequency == getNyquist()) {
                        lower = highestFrequency - (n / 2) * resolution;
                    } else {
                        lower = 0.0;
                    }
                }
            }
        }
        return lower;
    }


    /**
     * Returns the (non-negative) value of the highest frequency in the frequency band held in the object, for the given
     * dimension <i>dim</i>. The data are one-dimensional so <i>dim</i> is ignored. This form of the method is required
     * by the Spectral interface.
     *
     * @param dim The index of the independent variable being queried
     * @return The highest frequency represented in the given direction
     */
    public double getUpperFrequencyBound(int dim) {
        return getUpperFrequencyBound();
    }

    /**
     * Returns the (non-negative) value of the highest frequency in the frequency band held in the object.
     *
     * @return double The highest frequency represented in the given direction
     */
    public double getUpperFrequencyBound() {
        return highestFrequency;
    }


    /**
     * Sets or resets the (non-negative) value of the highest frequency in the frequency band held in the object for the
     * given direction <i>dim</i> to the value of the given argument <i>hf</i>.  Should be used after altering
     * parameters or data in the object if they have changed the upper frequency bound. Because the data are
     * one-dimensional, the value of <i>dim</i> is ignored.
     *
     * @param dim The index of the independent variable being queried
     * @param hf  The new highest frequency represented in the given direction
     */
    public void setUpperFrequencyBound(double hf, int dim) {
        setUpperFrequencyBound(hf);
    }

    /**
     * Sets the (non-negative) value of the highest frequency in the frequency band held in the object to the given
     * value. Should be used after altering parameters or data in the object if they have changed the upper frequency
     * bound. This should also set the <i>isNarrow</i> flag if the given value is not the highest value of the
     * original-length spectrum.
     *
     * @param hf The new highest frequency
     */
    public void setUpperFrequencyBound(double hf) {
        highestFrequency = hf;
        if (hf != (nFull / 2) * resolution) {
            setNarrow(true);
        } //integer division!
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
            } else {
                f = 0;
                for (j = 0; j < ln; j++) {
                    frequencies[j] = f;
                    f += res;
                }
            }
        } else {
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
            } else {
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
     * Returns the real parts of the values of the spectrum (data points) in a multidimensional array, ordered so that
     * in each dimension the values correspond to frequencies running from the lowest to the highest, regardless of the
     * internal data model. These points then correspond to the values returned by <i>getFrequencyArray</i> for each
     * dimension.
     *
     * @return Object Multidimensional arrray of ordered spectral values
     */
    public Object getOrderedSpectrumReal() {
        double[] spectrum = getDataReal();
        if (isTwoSided()) {
            int j, k;
            double[] data = spectrum;
            int ln = data.length;
            spectrum = new double[ln];
            int l2 = ln / 2;      // the number of negative-f elements
            if ((ln % 2 != 0) && (getLowerFrequencyBound() != 0)) {
                l2 = (ln + 1) / 2;
            }
            for (k = 0, j = ln - l2; k < l2; k++, j++) {
                spectrum[k] = data[j];
            }
            for (k = l2, j = 0; k < ln; k++, j++) {
                spectrum[k] = data[j];
            }
        }
        return spectrum;
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
        if (!isDependentComplex(0)) {
            return null;
        }
        double[] spectrum = getDataImag();
        if (isTwoSided()) {
            int j, k;
            double[] data = spectrum;
            int ln = data.length;
            spectrum = new double[ln];
            int l2 = ln / 2;      // the number of negative-f elements
            if ((ln % 2 != 0) && (getLowerFrequencyBound() != 0)) {
                l2 = (ln + 1) / 2;
            }
            for (k = 0, j = ln - l2; k < l2; k++, j++) {
                spectrum[k] = data[j];
            }
            for (k = l2, j = 0; k < ln; k++, j++) {
                spectrum[k] = data[j];
            }
        }
        return spectrum;
    }


    /*
     * Methods that provide further information about the spectral
     * data set. These depend on the Triana storage model.
     */

    /**
     * Returns the Nyquist frequency, defined to be the highest frequency that this data set could contain if it were
     * not narrow-banded. For full-bandwidth sets, this is the same value as is returned by
     * <i>getUpperFrequencyBound()</i>, but for narrow-band data sets it may be higher. </p><p> The general formula is
     * that if the original number of data points (given by <i> nFull = getOriginalN()</i>) is even, then the Nyquist
     * frequency is <i>nFull * resolution / 2</i>. If the original number of data points is odd, then the Nyquist
     * frequency is <i>(nFull -1 ) * resolution / 2</i>.
     *
     * @return double The Nyquist frequency
     */
    public double getNyquist() {
        double nyquist = highestFrequency;
        if (narrow) {
            nyquist = (nFull / 2) * resolution;
        } //integer division!
        return nyquist;
    }

    /**
     * Returns the sampling frequency that a time-series would have had if it had led to the present spectral data set.
     *
     * @return double The sampling frequency
     */
    public double getSamplingRate() {
        return resolution * nFull;
    }

    /*
     * Obsolete methods that are included for compatibility with earlier versions of
     * ComplexSpectrum.
     */

    /**
     * Obsolete method for obtaining sampling frequency. Will be withdrawn.
     *
     * @return the sampling frequency
     */
    public double samplingFrequency() {
        return getSamplingRate();
    }


    /**
     * Obsolete method for obtaining the number of data points. Will be withdrawn.
     *
     * @return the number of points in this ComplexSpectrum
     */
    public int points() {
        return size();
    }

    /**
     * Obsolete method for obtaining frequency resolution. Will be withdrawn.
     *
     * @return the frequency resolution
     */
    public double frequencyResolution() {
        return getFrequencyResolution();
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
        return getOrderedSpectrumReal();
    }

    /**
     * Returns the imaginary part of the dependent variable ordered so that the frequency values all run monotonically
     * upwards.
     *
     * @return Object An array containing the rearranged data values
     */
    public Object getGraphArrayImag(int dv) {
        return getOrderedSpectrumImag();
    }


    /*
    * Methods that must be over-ridden from superior classes.
    */

    /**
     * Extends the data set to a longer set by padding with zeros, keeping the frequency resolution unchanged. If the
     * new length is shorter than the old (as given by method <i>getOriginalN</i>, then nothing is done. This method
     * does <i>not</i> use the method of the same name in VectorType. Instead it takes advantage of the narrow-band
     * feature of the Triana spectral data model to add zeros "virtually", without increasing the storage required. The
     * way this is done depends on the value of the given parameter <i>front</i>. </p><p> If <i>front</i> is
     * <i>true</i>, then nothing is done and a warning message is printed to the debug stream, because it is not
     * possible in the Triana spectral model to extend a spectrum "below" zero-frequency. Negative frequencies are
     * already included in the spectrum either explictly or implicitly, so there is no room for more points at the
     * "front" of the data set. Even if the data set is narrow-band and does not include zero frequency, the data down
     * to zero frequency are already set to zero implicitly, so that there is no way to add more of them. </p><p> If the
     * boolean argument <i>front</i> is <i>false</i>, then the method extends the spectrum to higher frequencies, but
     * the extra zeros are added "virtually", <i>i.e.</i> by extending the length <i>nFull</i> of the original data set
     * and not adding the zeros explicitly. If the original data set was broad-band, it is marked as narrow-band after
     * extension. </p><p> The details of how this works are best understood by thinking of the original (un-extended)
     * data set as having been obtained from the final (extended one) by low-pass filtering. It is a consequence of the
     * Triana spectral data model that applying a low-pass filter to a full-bandwidth data set of any length
     * <i>nFull</i> results in a data set whose new value of <i>nFull</i> is odd. If the data set before extension has
     * an odd number of elements, then the extension requires only the actions described above. </p><p> On the other
     * hand, if the data set before extension has an even value of <i>nFull</i>, then it must be converted to one with
     * an odd number before it can be extended. This is done in this method by identifying all the elements of the old
     * set with those of the new one with the same frequency except for the highest frequency. In the new (odd-length)
     * set there are two elements for the highest frequency, one at positive frequency and one at negative. In the old
     * (even-length) set there is only one, at the negative frequency. The elements of the new one are determined as
     * follows: the negative-frequency element is set equal to the old negative-frequency element divided by sqrt(2),
     * and the positive-frequency element is set equal to the complex-conjugate of this. This choice is rather
     * arbitrary, but it ensures that, if this data set represents a Fourier transform, then the <i>total power</i> is
     * unchanged. Of course, if the original data is narrow-band and has already lost its highest frequencies, then the
     * new element is just set to zero, and this is done "virtually", by adjusting various parameters. If the data set
     * is one-sided, then only the positive-frequency element is added. When an element is added, then the independent
     * variable Triplet is extended by one as well.
     *
     * @param newLength The new length of the data set
     * @param front     True if padding is at the front, false for padding at the back
     */
    public void extendWithZeros(int newLength, boolean front) {
        int oldLength = getOriginalN();
        double[] newDataReal, newDataImag;
        if (newLength <= oldLength) {
            return;
        }
        if (front) {
            System.out.println(
                    "Warning: attempt to extend a ComplexSpectrum by adding zeros at the beginning. This is illegal. Data set unchanged.");
            return;
        }

        if (!isNarrow()) {
            setNarrow(true);
        }
        setOriginalN(newLength);
        boolean oldEven = (oldLength % 2 == 0);

        if (oldEven) {
            int halfLength = oldLength / 2;
            if (getFrequencyResolution() * halfLength == getUpperFrequencyBound()) {
                if (isTwoSided()) {
                    int extendedLength = oldLength + 1;
                    int halfLengthPlusOne = halfLength + 1;
                    int halfLengthMinusOne = halfLength - 1;
                    newDataReal = new double[extendedLength];
                    System.arraycopy(getDataReal(), 0, newDataReal, 0, halfLengthPlusOne);
                    newDataReal[halfLength] /= Math.sqrt(2.0);
                    System.arraycopy(getDataReal(), halfLengthPlusOne, newDataReal, halfLengthPlusOne,
                            halfLengthMinusOne);
                    newDataReal[oldLength] = newDataReal[halfLength];
                    newDataImag = new double[halfLength];
                    System.arraycopy(getDataImag(), 0, newDataImag, 0, halfLengthPlusOne);
                    newDataImag[halfLength] /= Math.sqrt(2.0);
                    System.arraycopy(getDataImag(), halfLengthPlusOne, newDataImag, halfLengthPlusOne,
                            halfLengthMinusOne);
                    newDataImag[oldLength] = -newDataImag[halfLength];
                    setData(newDataReal, newDataImag);
                    setX(new Triplet(extendedLength));
                } else {
                    getDataReal()[halfLength] /= Math.sqrt(2.0);
                }
            }
        }
    }

    /**
     * Inserts zeros in between existing elements of the data set. The integer argument <i>factor</i> gives the number
     * of zeros per existing data point that must be inserted. The boolean argument <i>before</i> regulates whether the
     * zeros should be inserted before each element (if <i>true</i>) or after (<i>false</i>). The new values of the
     * independent variable are interpolated between those of the old ones. </p><p> If the argument <i>factor</i> is
     * zero or negative, nothing is done. </p><p> In ComplexSpectrum, the interpolation is done in such a way that the
     * frequency resolution is increased and the pre-existing data points remain at the same value of the frequency.
     * This means that it is illegal to add zeroes before the first element, which corresponds to zero frequency. So if
     * <i>before</i> is <i>true</i> then the method returns without doing anything, and prints a warning. </p><p> If the
     * value of <i>before</i> is <i>false</i>, then the interpolation is done in such a way that the frequency
     * resolution is divided by (<i>factor</i> + 1) and the original number of points is changed by (<i>factor</i> + 1).
     * This ensures that the original data points remain at the same values of the frequency. The requisite number of
     * zeros is added after each data point. If the spectrum is two-sided, then the way interpolation as done in
     * <i>VectorType</i> is correct here too, and only the parameters are changed, as just described. If the spectrum is
     * one-sided, then ordinary interpolation adds too many at the end, and this method removes the extra ones. </p><p>
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
        if (before) {
            System.out.println(
                    "Warning: attempt to extend a ComplexSpectrum by adding zeros at the beginning. This is illegal. Data set unchanged.");
            return;
        }

        super.interpolateZeros(factor, false); // basic operation is normal interpolation
        frequencyResolution /= factor + 1;
        nFull *= factor + 1;

        if (!isTwoSided()) { // if two-sided, then basic interpolation suffices -- only remove extra zeros if one-sided
            int extendedLength = getDataReal().length;
            int reducedLength = nFull / 2
                    + 1; // integer division gives the right answer for one-sided size for any nFull
            if (reducedLength < extendedLength) { // remove the highest extra zeros: too many have been added
                double[] newDataReal = new double[reducedLength];
                System.arraycopy(getDataReal(), 0, newDataReal, 0, reducedLength);
                double[] newDataImag = new double[reducedLength];
                System.arraycopy(getDataImag(), 0, newDataImag, 0, reducedLength);
                setData(newDataReal, newDataImag);
            }
        }

        setX(new Triplet(size()));
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
        ComplexSpectrum c = null;
        try {
            c = (ComplexSpectrum) getClass().newInstance();
            c.copyData(this);
            c.copyParameters(this);
        }
        catch (IllegalAccessException ee) {
            System.out.println("Illegal Access: " + ee.getMessage());
        }
        catch (InstantiationException ee) {
            System.out.println("Couldn't be instantiated: " + ee.getMessage());
        }
        return c;
    }

    /**
     * Copies modifiable parameters from the argument object to the current object. The copying is by value, not by
     * reference. Parameters are defined as data not held in <i>dataContainer</i>. They are modifiable if they have
     * <i>set...</i> methods. Parameters that cannot be modified, but which are set by constructors, should be placed
     * correctly into the copied object when it is constructed. </p><p> In ComplexSpectrum, the new parameters are
     * <i>resolution</i>, <i>highestFrequency</i>, <i>twoSided</i>, <i>narrow</i>, <i>nFull</i>. The obsolete parameters
     * <i>samplingFrequency</i> and <i>frequencyResolution</i> are generated automatically when the other parameters are
     * copied and set. </p><p> This must be overridden by any subclass that defines new parameters. The overriding
     * method should invoke its <i>super</i> method. It should use the <i>set...</i> and <i>get...</i> methods for the
     * parameters in question. This method is protected so that it cannot be called except by objects that inherit from
     * this one. It is called by <i>copyMe()</i>.
     *
     * @param source Data object that contains the data to be copied.
     */
    protected void copyParameters(TrianaType source) {
        super.copyParameters(source);
        setFrequencyResolution(((ComplexSpectrum) source).getFrequencyResolution());
        highestFrequency = ((ComplexSpectrum) source).getUpperFrequencyBound();
        setTwoSided(((ComplexSpectrum) source).isTwoSided());
        setNarrow(((ComplexSpectrum) source).isNarrow());
        nFull = ((ComplexSpectrum) source).getOriginalN();
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
        dos.println(getUpperFrequencyBound());
        dos.println(isTwoSided());
        dos.println(isNarrow());
        dos.println(getOriginalN());
        dos.println(getFrequencyResolution());
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
        highestFrequency = (Double.valueOf(dis.readLine())).doubleValue();
        setTwoSided((Boolean.valueOf(dis.readLine())).booleanValue());
        setNarrow((Boolean.valueOf(dis.readLine())).booleanValue());
        nFull = (Integer.valueOf(dis.readLine())).intValue();
        setFrequencyResolution((Double.valueOf(dis.readLine())).doubleValue());
    }

    /**
     * Tests the argument object to determine if it makes sense to perform arithmetic operations between it and the
     * current object. </p><p> In ComplexSpectrum, the method first tests that the input object is compatible with
     * superior classes, and then (if it is a ComplexSpectrum( it tests if the input has the same frequency resolution,
     * upper frequency bound, two-sidedness, and narrow-bandedness. </p><p> Classes derived from this should over-ride
     * this method with further tests as appropriate. The over-riding method should normally have the first lines <PRE>
     * boolean test = super.isCompatible( obj ); </PRE>followed by other tests. If other types not subclassed from
     * GraphType or Const should be allowed to be compatible then other tests must be implemented.
     *
     * @param obj The data object to be compared with the current one
     * @return <I>True</I> if the object can be combined with the current one
     */
    public boolean isCompatible(TrianaType obj) {
        boolean test = super.isCompatible(obj);
        if ((test) && (obj instanceof ComplexSpectrum)) {
            if (getFrequencyResolution() != ((ComplexSpectrum) obj).getFrequencyResolution()) {
                return false;
            }
            if (getUpperFrequencyBound() != ((ComplexSpectrum) obj).getUpperFrequencyBound()) {
                return false;
            }
            if (isTwoSided() != ((ComplexSpectrum) obj).isTwoSided()) {
                return false;
            }
            if (isNarrow() != ((ComplexSpectrum) obj).isNarrow()) {
                return false;
            }
        }
        return test;
    }


    /**
     * Determines whether the argument TrianaType is equal to the current ComplexSpectrum. They are equal if the
     * argument is a ComplexSpectrum with the same size, parameters, and data. </p><p> This method must be over-ridden
     * in derived types. In a derived type called xxx the method should begin<PRE> if ( !( obj instanceof xxx ) ) return
     * false; if ( !isCompatible( obj ) ) return false; </PRE>followed by tests that are specific to type xxx (testing
     * its own parameters) and then as a last line<PRE> return super.equals( obj ); </PRE>This line invokes the other
     * equals methods up the chain to GraphType. Each superior object tests its own parameters.
     *
     * @param obj The object being tested
     * @return true if they are equal or false otherwise
     */
    public boolean equals(TrianaType obj) {
        if (!(obj instanceof ComplexSpectrum)) {
            return false;
        }
        if (!isCompatible(obj)) {
            return false;
        }
        return super.equals(obj);
    }

    /*
     * Used to make the new
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
        frequencyResolution = resolution;
        real = getDataReal();
        imag = getDataImag();
        super.updateObsoletePointers();
    }

    /*
     * Class methods.
     /*


    /**
     * Class method that takes an input narrow-band ComplexSpectrum
     * and reduces <i>nFull</i> to be compatible with the upper
     * frequency bound, so that the set is no longer narrow-band
     * at the top. This is useful in doing resampling of time-series
     * data. The Nyquist frequency is the highest frequency allowed
     * by the sampling rate. If the input is not narrow-band, then
     * the method returns null.
     *
     * @param ComplexSpectrum inputNarrow The input narrow-band spectrum
     * @param boolean allocMem <i>True</i> if the output is a modified copy of input
     * @return ComplexSpectrum The reduced-sampling-rate spectrum
     */

    public static ComplexSpectrum reduceNyquist(ComplexSpectrum inputNarrow, boolean allocMem) {

        if (!inputNarrow.isNarrow()) {
            return null;
        }

        ComplexSpectrum output = (allocMem) ? (ComplexSpectrum) inputNarrow.copyMe() : inputNarrow;

        if (output.size() == 1) {
            return output;
        }

        double highF = output.getUpperFrequencyBound();
        double oldNyquist = output.getNyquist();
        if (oldNyquist == highF) {
            return output;
        }

        int oldN = output.getOriginalN();
        int newN, newNarrowN, high;
        boolean even = (oldN % 2 == 0);
        boolean twoSided = output.isTwoSided();
        boolean zeroPresent = (output.getLowerFrequencyBound() == 0);
        double res = output.getFrequencyResolution();
        double[] real = output.getDataReal();
        double[] imag = output.getDataImag();
        int len = real.length;
        double[] temp;

        if (even) {
            newN = (int) Math.round(2 * highF / res);
            if (!twoSided) {
                imag[len - 1] = 0;
            } else {
                high = len / 2 - 1;
                newNarrowN = len - 1;
                temp = new double[newNarrowN];
                System.arraycopy(real, 0, temp, 0, len / 2);
                System.arraycopy(real, len / 2 + 1, temp, len / 2, newNarrowN - len / 2);
                temp[high] = (real[high] + real[high + 1])
                        / 2; // ensure that spectra from real time-series go back to real time-series
                real = temp;
                temp = new double[newNarrowN];
                System.arraycopy(imag, 0, temp, 0, len / 2);
                System.arraycopy(imag, len / 2 + 1, temp, len / 2, newNarrowN - len / 2);
                temp[high] = (imag[high] + imag[high + 1])
                        / 2; // ensure that spectra from real time-series go back to real time-series
                imag = temp;
            }
        } else {
            newN = (int) Math.round(2 * highF / res + 1);  // if old set was odd, so is new set
        }
        output.setOriginalN(newN);
        output.setData(real, imag);
        if (zeroPresent) {
            output.setNarrow(false);
        }

        return output;

    }


}











