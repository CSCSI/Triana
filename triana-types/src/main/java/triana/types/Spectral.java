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
 * Spectral is an interface that can be implemented by any
 * Triana data types that include data that represent
 * frequency-domain data or that have been put through a
 * Fourier transform. It contains methods to set and
 * read relevant additional information, such as the
 * frequency range of the data.
 * </p><p>
 * Spectral is consistent with the Triana model for
 * storing spectal information, which is fully described in
 * the documentation for the ComplexSpectrum and Spectrum types.
 * In particular, it assumes that the object stores the
 * value of the highest frequency contained in the data set
 * (used for narrow-band data) but not the lowest; therefore
 * there is a method for setting the highest frequency but
 * not one for setting the lowest.
 * </p><p>
 * Spectral assumes one aspect of the GraphType data
 * model, which is that data are organized as functions
 * of a set of independent variables, and many of the
 * methods of Spectrum take the integer index of the
 * appropriate independent variable (dimension) as
 * an argument. This allows milti-dimensional
 * spectral data to have different properties associated
 * with different dimensions.
 *
 * @see Spectrum
 * @see ComplexSpectrum
 * @see GraphType
 *
 * @author      Bernard Schutz
 * @created     30 December 2000
 * @version     $Revision: 4048 $
 * @date        $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public interface Spectral {


    /**
     * Returns the frequency resolution
     * of the data associated with the independent variable indexed
     * by the given value of <i>dim</i>, <i>i.e.</i>
     * the step in frequency from one data point to the next.
     *
     * @param dim The index of the independent variable being queried
     * @return double The frequency resolution
     */
    public double getFrequencyResolution(int dim);

    /**
     * Sets the frequency resolution of the data
     * associated with the independent variable indexed by the given value of <i>dim</i>.
     *
     * @param dim The index of the independent variable being queried
     * @param f The frequency resolution
     */
    public void setFrequencyResolution(double f, int dim);


    /**
     * Returns <i>true</i> if the data are stored as a two-sided
     * transform, <i>i.e.</i> containing both the positive and negative frequency
     * data. If <i>false</i>, the data are one-sided, containing only positive
     * frequencies. If the data set is multi-dimensional, then one-sided
     * means that only positive frequencies for the first independent
     * variable, <i>dim</i> = 0, are stored. Thus, there is no <i>dim</i> parameter for
     * this method.
     *
     * @return boolean True if data are two-sided in frequency space
     */
    public boolean isTwoSided();

    /**
     * Sets the two-sided flag to the value of the argument.
     * As for method <i>isTwoSided</i>, there is no parameter <i>dim</i>.
     *
     * @param s True if the data will be two-sided
     */
    public void setTwoSided(boolean s);

    /**
     * Returns the number of points in the data set
     * whose transform could have led to the present data, or equivalently
     * the number of points in the two-sided full-bandwidth spectrum
     * from which the present spectrum could have been derived. This
     * depends on which independent variable one is examining (<i>dim</i>).
     *
     * @param dim The index of the independent variable being queried
     * @return The number of points in the original data set
     */
    public int getOriginalN(int dim);

    /**
     * Sets to the given argument the number of points in the data set
     * whose transform could have led to the present data, or equivalently
     * the number of points in the two-sided full-bandwidth spectrum
     * from which the present spectrum could have been derived. For
     * multidimensional sets this represents the length in the given dimension.
     *
     * @param nOrig The new number of points in the original data set
     * @param dim The index of the independent variable dimension
     */
    public void setOriginalN(int nOrig, int dim);

    /**
     * Returns <i>true</i> if the data represent a narrow bandwidth
     * derived from a full-band spectrum in the given dimension <i>dim</i>..
     *
     * @param dim The index of the independent variable being queried
     * @return True if data are narrow-band
     */
    public boolean isNarrow(int dim);

    /**
     * Sets the narrow-band flag for the given dimension <i>dim</i>
     * to the value of the argument.
     *
     * @param n True if the data held are narrow-band
     * @param dim The index of the independent variable being set
     */
    public void setNarrow(boolean n, int dim);

    /**
     * Returns the (non-negative) value
     * of the lowest frequency in the frequency band held in the object,
     * for the given dimension <i>dim</i>.
     *
     * @param dim The index of the independent variable being queried
     * @return The lowest frequency represented in the given direction
     */
    public double getLowerFrequencyBound(int dim);

    /**
     * Returns the (non-negative) value
     * of the highest frequency in the frequency band held in the object,
     * for the given dimension <i>dim</i>.
     *
     * @param dim The index of the independent variable being queried
     * @return The highest frequency represented in the given direction
     */
    public double getUpperFrequencyBound(int dim);

    /**
     * Sets the (non-negative) value
     * of the highest frequency in the frequency band held in the object
     * for the given direction <i>dim</i> to the given value <i>hf</i>.
     * This should also set the
     * <i>isNarrow</i> flag if the given value is not the highest value
     * of the original-length spectrum.
     *
     * @param dim The index of the independent variable being queried
     * @param hf The new highest frequency represented in the given direction
     */
    public void setUpperFrequencyBound(double hf, int dim);

    /**
     * Returns the frequency values of the independent data points for
     * the given dimension, in the order of lowest frequency to highest,
     * regardless of how the data are stored internally.
     *
     * @param dim The dimension of the independent variable
     * @return double[] Array of ordered frequency values
     */
    public double[] getFrequencyArray(int dim);

    /**
     * Returns the real parts of the values of the spectrum (data points) in a
     * multidimensional array, ordered so that in each dimension
     * the values correspond to frequencies running from the lowest
     * to the highest, regardless of the internal data model. These
     * points then correspond to the values returned by
     * <i>getFrequencyArray</i> for each dimension.
     *
     * @return Object Multidimensional arrray of ordered spectral values
     */
    public Object getOrderedSpectrumReal();

    /**
     * Returns the imaginary parts of the values of the spectrum (data points) in a
     * multidimensional array, ordered so that in each dimension
     * the values correspond to frequencies running from the lowest
     * to the highest, regardless of the internal data model. These
     * points then correspond to the values returned by
     * <i>getFrequencyArray</i> for each dimension.
     *
     * @return Object Multidimensional arrray of ordered spectral values
     */
    public Object getOrderedSpectrumImag();

}





