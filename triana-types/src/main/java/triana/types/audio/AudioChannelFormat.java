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
package triana.types.audio;


import javax.sound.sampled.AudioFormat;
import triana.types.util.ChannelFormat;
import triana.types.util.Str;
import triana.types.util.StringSplitter;


/**
 * The AudioChannelFormat includes aspects of the sound format other than the number of channels. This class consists of
 * the following parameters :
 * <p/>
 * <ol> <li> sample rate : the sample rate, specified as an int <li> sample size : the number of bits used to store each
 * sample <li> channel name : the name of this channel <li> audio encoding : the audio encoding used e.g. PCM, U_LAW,
 * GSM etc </ol>
 * <p/>
 * One ubiquitous type of audio encoding is pulse-code modulation (PCM), which is simply a linear (proportional)
 * representation of the sound waveform. With PCM, the number stored in each sample is proportional to the instantaneous
 * amplitude of the sound pressure at that point in time. The numbers are frequently signed or unsigned integers.
 * Besides PCM, other encodings include mu-law and a-law, which are nonlinear mappings of the sound amplitude that are
 * often used for recording speech.
 *
 * @version $Revision: 4048 $
 */
public class AudioChannelFormat implements java.io.Serializable, ChannelFormat {

    /**
     * Set once in the static constructor
     */
    static boolean bigendian = true;

    static {
        try {
            String os = System.getProperty("os.name");
            if (os.startsWith("Windows")) {
                bigendian = false;
            }
        }
        catch (Exception ee) { // ha ha! windows by default ...
            bigendian = false;
        }
    }

    /**
     * The PCM (pulse-code modulation) format for audio.
     */
    public final static int PCM = 0;

    /**
     * The PCM (pulse-code modulation) format for audio.
     */
    public final static int PCM_UNSIGNED = 1;

    /**
     * The A_LAW format for audio.
     */
    public final static int U_LAW = 2;

    /**
     * The A_LAW  format for audio.
     */
    public final static int A_LAW = 3;

    /**
     * The A_LAW  format for audio.
     */
    public final static int GSM = 4;

    /**
     * The sampling rate specified as a integer. Default is 44100, CD quality
     */
    public int samplingRate = 44100;

    /**
     * The sampling size specified in bits. Default is 16, CD quality
     */
    public short sampleSize = 16;

    /**
     * The name for this channel
     */
    public String channelName = "Audio";

    /**
     * The encoding for this piece of audio e.g. PCM, U_LAW, MP3 etc. Default is PCM.
     */
    public int encoding = PCM;

    /**
     * Creates an AudioChannelFormat Object with default values
     */
    public AudioChannelFormat() {
    }

    /**
     * Creates an AudioChannelFormat Object with a given sampling rate and sample size (in bits) It formats the audio to
     * the default PCM, names the chanel Audio.
     */
    public AudioChannelFormat(int srate, short sampSize) {
        samplingRate = srate;
        sampleSize = sampSize;
        encoding = PCM;
        channelName = "Audio";
    }

    /**
     * Creates an AudioChannelFormat Object with a given sampling rate, sample size and format for this audio.
     */
    public AudioChannelFormat(int srate, short sampSize, int format) {
        this(srate, sampSize);
        encoding = format;
    }

    /**
     * Creates an AudioChannelFormat Object with a given sampling rate, sample size, format for this audio and a name
     * for this channel
     */
    public AudioChannelFormat(int srate, short sampSize, int format, String channelName) {
        this(srate, sampSize);
        encoding = format;
        this.channelName = channelName;
    }

    /**
     * Creates an AudioChannelFormat from the given AudioChannelFormat Object.
     */
    public AudioChannelFormat(AudioChannelFormat au) {
        this(au.getSamplingRate(), au.getSampleSize(), au.getEncoding(), au.getChannelName());
    }


    /**
     * Creates an AudioChannelFormat from the given javax.sound.sampled AudioFormat Object.
     */
    public AudioChannelFormat(AudioFormat au) {
        this((int) au.getSampleRate(), (short) au.getSampleSizeInBits());
        encoding = getEncoding(au.getEncoding());
    }

    /**
     * Gets a Javax.sound.sampled AudioFormat Object from this format
     */
    public AudioFormat getAudioFormat() {
        int channels = 1;
        return new AudioFormat(getAudioFormatEncoding(encoding),
                samplingRate, sampleSize, channels,
                getSampleSizeInBytes() * channels, (float) samplingRate, bigendian);
    }


    /**
     * translates between the AudioChannelFormat (our format) and the AudioFormat Object in java sound.
     */
    public static AudioFormat.Encoding getAudioFormatEncoding(int encoding) {
        if (encoding == PCM_UNSIGNED) {
            return AudioFormat.Encoding.PCM_UNSIGNED;
        } else if (encoding == PCM) {
            return AudioFormat.Encoding.PCM_SIGNED;
        } else if (encoding == A_LAW) {
            return AudioFormat.Encoding.ALAW;
        } else if (encoding == U_LAW) {
            return AudioFormat.Encoding.ULAW;
        } else {
            return AudioFormat.Encoding.PCM_SIGNED;
        }
    }

    /**
     * translates between the AudioChannelFormat (our format) and the AudioFormat Object in java sound.
     */
    public static int getEncoding(AudioFormat.Encoding encoding) {
        if (encoding == AudioFormat.Encoding.PCM_UNSIGNED) {
            return PCM_UNSIGNED;
        } else if (encoding == AudioFormat.Encoding.PCM_SIGNED) {
            return PCM;
        } else if (encoding == AudioFormat.Encoding.ALAW) {
            return A_LAW;
        } else if (encoding == AudioFormat.Encoding.ULAW) {
            return U_LAW;
        } else {
            return PCM;
        }
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
     * @return the number of bits used to store each sample
     */
    public short getSampleSize() {
        return sampleSize;
    }

    /**
     * @return the encoding for this chunk of audio
     */
    public int getEncoding() {
        return encoding;
    }

    /**
     * @return the number of bytes used to store each sample
     */
    public int getSampleSizeInBytes() {
        int bytes = sampleSize / 8;
        if ((sampleSize % 8) > 0) {
            ++bytes;
        }
        return bytes;
    }

    /**
     * @return true if the given object has the same parameters as this object
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof AudioChannelFormat)) {
            return false;
        }
        AudioChannelFormat au = (AudioChannelFormat) obj;

        if ((getSamplingRate() == au.getSamplingRate()) &&
                (getSampleSize() == au.getSampleSize()) &&
                (getChannelName().equals(au.getChannelName())) &&
                (getEncoding() == au.getEncoding())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @return a string representation for this AudioChannelFormat object
     */
    public String toString() {
        return String.valueOf(getSamplingRate()) + " " +
                String.valueOf(getSampleSize()) + " " +
                getChannelName() + " " +
                String.valueOf(getEncoding());
    }

    /**
     * Initialises this AudioChannelFormat object from the given string
     */
    public void setFromString(String str) {
        StringSplitter sp = new StringSplitter(str);

        samplingRate = Str.strToInt(sp.at(0));
        sampleSize = Str.strToShort(sp.at(1));
        channelName = sp.at(2);
        encoding = Str.strToInt(sp.at(3));
    }
}


