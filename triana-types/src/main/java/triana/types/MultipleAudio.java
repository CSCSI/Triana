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

import triana.types.util.AudioChannelFormat;
import triana.types.util.ChannelFormat;

import javax.sound.sampled.AudioFormat;

/**
 * MutipleAudio stores many channels of sampled data.Each channel can
 * have its own particular audio format of the data e.g. the encoding,
 * such as MU_LAW, PCM and number of bits used to
 * record the data. This is essential for performing sound transformations and
 * writing audio data.
 *
 * @see ChannelFormat
 * @see AudioChannelFormat
 * @see MultipleChannel
 *
 * @author      Ian Taylor
 * @created     6 January 2001
 * @version     $Revision: 4048 $
 * @date        $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public class MultipleAudio extends MultipleChannel implements AsciiComm {
    /**
     * The identifier for continuous processing. This helps units
     * determine what kind of data they are dealing with and therefore
     * in this case what previous and forward memory bufferes they
     * require for processing the current data. Continuous processing
     * is when the data stream is split into smaller chunks for
     * processing continuous streams of audio.
     */
    public final static int CONTINUOUS_PROCESSING = 0;

    /**
     * The identifier for fixed length processing e.g. a whole song or
     * entire audio part. This is the default.
     */
    public final static int FIXED_LENGTH_PROCESSING = 1;

    public int processingMode = FIXED_LENGTH_PROCESSING;

    /**
     * The endian'ness of the machine that produced the data. This is
     * common to all channels
     */
    static boolean bigendian = true;


    static {
        try {
            String os = System.getProperty("os.name");
            if (os.startsWith("Windows"))
                bigendian = false;
        }
        catch (Exception ee) { // ha ha! windows by default ...
            bigendian = false;
        }
    }

    /**
     * Creates an empty MultipleAudio Object with no channels added.
     */
    public MultipleAudio() {
        super();
    }

    /**
     * Creates a MultipleAudio object with a specific number of channels
     */
    public MultipleAudio(int channels) {
        super(channels);
    }

    /**
     * Added by I. Taylor, August 2001 : This function sets the default labeling
     * scheme used for all GraphTypes. Default values are 'X' for the X-axis and
     * 'Y' for the Y axis.  The various subclasses can override this function
     * with their specific axis-labelling conventions.
     */
    public void setDefaultAxisLabelling() {
        String labelx = "Time";
        String labely = "Amplitude";
        setIndependentLabels(0, labelx);
        setDependentLabels(0, labely);
    }

    /**
     * @return the format of the data at the requested channel.
     */
    public AudioChannelFormat getAudioChannelFormat(int channelNo) {
        return (AudioChannelFormat) super.getChannelFormat(channelNo);
    }

    /**
     * Gets a Javax.sound.sampled AudioFormat Object for this multiple
     * audio object.  This returns an approximation to the audio format
     * object used to describe the contents of this audio object
     * containing many channels. For stereo audio , for example, this works
     * fine as each channel has the same parameters i.e. sampling rate etc but
     * for more compilcated sounds this will fail.
     */
    public AudioFormat getAudioFormat() {
        AudioChannelFormat au = getAudioChannelFormat(0);
        int channels = getChannels();
        return new AudioFormat(au.getAudioFormatEncoding(au.encoding),
                               au.samplingRate, au.sampleSize, channels,
                               au.getSampleSizeInBytes() * channels, (float) au.samplingRate, bigendian);
    }

    /**
     * A function to set the type of processing mode that this data
     * should be subjected to. there are 2 choice : CONTINUOUS_PROCESSING
     * is set when the data stream is split into smaller chunks for
     * processing continuous streams of audio and FIXED_LENGTH_PROCESSING
     * is set when the entire audio part is available for processing
     * in one chunk.  This helps units determine how to treat
     * the audio.
     */
    public void setProcessingMode(int mode) {
        this.processingMode = mode;
    }


    /**
     * Finds the range for the given data and works out what sampling resolution
     * it will fit into, <i>e.g.</i> CD quality (16-bit resolution). Allowed
     * return values are 8, 16, and 32 bits.
     *
     * @param dataIn The input data being examined
     * @return int The number of bits of resolution needed to store the input adequately
     */
    public static int getNumberOfBitsToStore(double[] dataIn) {
        double max = 0;
        for (int i = 0; i < dataIn.length; ++i)
            max = Math.max(Math.abs(dataIn[i]), max);

        if (max < 128)
            return 8;
        else if (max < 32768)
            return 16;
        else // assume maximum is 32 bit integer
            return 32;
    }


    /**
     * This function creates a ChannelFormat object from the given String
     * using the toString and setFromString() functions with a ChannelFormat
     */
    public ChannelFormat createChannelFormatFrom(String line) {
        AudioChannelFormat au = new AudioChannelFormat();
        au.setFromString(line);
        return au;
    }


    /**
     * Tests the argument object to determine if
     * it makes sense to perform arithmetic operations between
     * it and the current object.
     * </p><p>
     * In Mutiple Audio, this method first tests for compatibility with superior
     * classes, and then (if the input object is a Mutiple Audio) tests that
     * the input has the same channel count.
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
        if ((test) && (obj instanceof MultipleAudio))
            return true;
        else
            return false;
    }

    /**
     * Determines whether the argument TrianaType is equal to
     * the current MultipleAudio. They are equal if the argument is
     * a MultipleAudio with the same size, parameters, and data.
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
        if (!(obj instanceof MultipleAudio)) return false;
        return super.equals(obj);
    }
}






