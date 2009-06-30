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
package triana.types.util;


/**
 * SpectralChannelFormat is a format for representing
 * frequency-domain data or data that have been put through a
 * Fourier transform. It contains methods to set and
 * read relevant additional information, such as the
 * frequency range of the data.
 *
 * <p>It implements the ChannelFormat interface
 * @author      Ian Taylor
 * @created     6 January 2001
 * @version     $Revision: 4048 $
 * @date        $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public class SpectralChannelFormat implements ChannelFormat {

    /**
     * The sampling rate specified as a integer. Default is 44100, CD quality
     */
    public int samplingRate = 44100;

    /**
     * The number of points contained within this spectral representation.
     * This is needed to calculate the spectral resolution
     */
    public int numberOfPoints = samplingRate;


    /**
     * The name for this channel
     */
    public String channelName = "Audio";

    /**
     * Variable indicating whether the data are stored as a two-sided
     * transform, <i>i.e.</i> containing both the positive and negative frequency
     * data.
     */
    public boolean twoSided = false;

    /**
     * Indicates whether the data represent a narrow bandwidth
     * derived from a full-band spectrum in the given dimension <i>dim</i>..
     */
    public boolean narrow;

    /**
     * Creates an SpectralChannelFormat Object with default values
     */
    public SpectralChannelFormat() {
    }

    /**
     * Creates an SpectralChannelFormat Object with a given sampling rate and sample size (in bits)
     * It formats the audio to the default PCM, names the chanel Audio.
     */
    public SpectralChannelFormat(int srate, int numberOfPoints,
                                 boolean twoSided, boolean narrow, String chanName) {
        samplingRate = srate;
        channelName = chanName;
        this.numberOfPoints = numberOfPoints;
        this.twoSided = twoSided;
        this.narrow = narrow;
    }

    /**
     * Creates an SpectralChannelFormat Object with a given sampling rate and sample size (in bits)
     * It formats the audio to the default PCM, names the chanel Audio.
     */
    public SpectralChannelFormat(int srate, int numberOfPoints,
                                 boolean twoSided, boolean narrow) {
        this(srate, numberOfPoints, twoSided, narrow, "Spectral");
    }

    /**
     * Returns the frequency resolution
     * of the data associated with the independent variable indexed
     * by the given value of <i>dim</i>, <i>i.e.</i>
     * the step in frequency from one data point to the next.
     *
     * @return double The frequency resolution
     */
    public double getFrequencyResolution() {
        return (double) numberOfPoints / (double) samplingRate;
    }

    /**
     * @return the name for this channel
     */
    public String getChannelName() {
        return channelName;
    }

    /**
     * @return the sampling rate for this channel
     */
    public int getSamplingRate() {
        return samplingRate;
    }

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
    public boolean isTwoSided() {
        return twoSided;
    }

    /**
     * Returns the number of points in the data set
     * whose transform could have led to the present data, or equivalently
     * the number of points in the two-sided full-bandwidth spectrum
     * from which the present spectrum could have been derived. This
     * depends on which independent variable one is examining (<i>dim</i>).
     *
     * @return int The number of points in the original data set
     */
    public int getOriginalN() {
        if (twoSided)
            return numberOfPoints;
        else
            return numberOfPoints * 2;  // BFS, Is this correct ?
    }

    /**
     * @return the number of points in this spectral representation.
     */
    public int getPoints() {
        return numberOfPoints;
    }

    /**
     * Returns <i>true</i> if the data represent a narrow bandwidth
     * derived from a full-band spectrum in the given dimension <i>dim</i>..
     *
     * @return boolean True if data are narrow-band
     */
    public boolean isNarrow() {
        return narrow;
    }

    /**
     * Returns the (non-negative) value
     * of the lowest frequency in the frequency band held in the object,
     * for the given dimension <i>dim</i>.
     *
     * @return double The lowest frequency represented in the given direction
     */
    public double getLowerFrequencyBound() {
        return 0.0; // BFS ?????
    }

    /**
     * Returns the (non-negative) value
     * of the highest frequency in the frequency band held in the object,
     * for the given dimension <i>dim</i>.
     *
     * @return double The highest frequency represented in the given direction
     */
    public double getUpperFrequencyBound() {
        return (double) samplingRate / 2.0;
    }


    /**
     * @return true if the given object has the same parameters
     * as this object
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof AudioChannelFormat))
            return false;
        SpectralChannelFormat au = (SpectralChannelFormat) obj;

        if ((getSamplingRate() == au.getSamplingRate()) &&
                (getPoints() == au.getPoints()) &&
                (getChannelName().equals(au.getChannelName())) &&
                (isNarrow() == au.isNarrow()) &&
                (isTwoSided() == au.isTwoSided()))
            return true;
        else
            return false;
    }

    /**
     * @return a string representation for this AudioChannelFormat object
     */
    public String toString() {
        return String.valueOf(getSamplingRate()) + " " +
                String.valueOf(getPoints()) + " " +
                getChannelName() + " " +
                isNarrow() + " " +
                String.valueOf(isTwoSided());
    }

    /**
     * Initialises this AudioChannelFormat object from the given string
     */
    public void setFromString(String str) {
        StringSplitter sp = new StringSplitter(str);

        samplingRate = Str.strToInt(sp.at(0));
        numberOfPoints = Str.strToShort(sp.at(1));
        channelName = sp.at(2);
        narrow = Str.strToBoolean(sp.at(3));
        twoSided = Str.strToBoolean(sp.at(4));
    }
}





