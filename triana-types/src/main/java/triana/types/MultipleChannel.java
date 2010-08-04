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
import java.util.Vector;

import triana.types.util.ChannelFormat;
import triana.types.util.Str;
import triana.types.util.Triplet;

/**
 * MultipleChannel is a base class for representing multiple channeled data. Each channel. Each channel can have its own
 * particular format by object that implement the ChannelFormat interface. Furthermore, each channel can contain complex
 * data so that, for example, this type could be used to store multiple channels of complex spectra.
 * <p/>
 * <p>The 2 subclasses which use this class are MultipleAudio and MultipleSpectra. MultipleAudio is a type to store
 * multiple channels of time-series data. This could be used to store stereo music or multiple channels from a recording
 * device. MultipleSpectra is used to contain multiple channels of frequency domain data.
 *
 * @author Ian Taylor
 * @version $Revision: 4048 $
 * @see GraphType
 * @see triana.types.util.ChannelFormat
 * @see triana.types.audio.MultipleAudio
 * @see MultipleSpectra
 */
public abstract class MultipleChannel extends GraphType implements AsciiComm {
    /**
     * Used to store the audio format for each channel (for data or real data)
     */
    protected Vector channelFormat;

    /**
     * variable used to store the current maximum length of a dependent data set
     */
    private int maxDependentLength = -1;

    private boolean complex = false;

    /**
     * Creates an empty non-complex MultipleChannel Object.
     */
    public MultipleChannel() {
        super();
    }

    /**
     * Creates a MultipleChannel object with a specific number of channels
     */
    public MultipleChannel(int channels) {
        this();
        setDimensions(1, channels);
    }

    /**
     * Sets the number of data channels.
     */
    public void setDimensions(int indChans, int channels) {
        channelFormat = new Vector(channels);
        channelFormat.setSize(channels);
        super.setDimensions(indChans, channels);
    }

    /**
     * Inserts a channel into this MultipleChannel Type at the particular position
     */
    public void setChannel(int channelNo, Object data, ChannelFormat format) {
        setDataArrayReal(data, channelNo);
        channelFormat.setElementAt(format, channelNo);
        int length = 0;

        if (data instanceof byte[]) {
            length = ((byte[]) data).length;
        } else if (data instanceof short[]) {
            length = ((short[]) data).length;
        } else if (data instanceof int[]) {
            length = ((int[]) data).length;
        } else if (data instanceof double[]) {
            length = ((double[]) data).length;
        }

        if (maxDependentLength < length) { // we just have one triplet but need the one with
            // the most number of points.
            setIndependentTriplet(new Triplet(length, 0, 1 / format.getSamplingRate()), channelNo);
            maxDependentLength = length;
        }

        //     addDescription("MultipleChannel, Channel " + channelNo);
    }

    /**
     * Inserts a imaginary part of the given channel into this MultipleChannel Type at the particular position. The
     * format for this channel is specified by the format given by the real component.
     */
    public void setChannelImag(int channelNo, Object data) {
        setDataArrayImag(data, channelNo);
        // no need to add a triple here as this is taken care of by the
        // real part of this channel.
    }

    /**
     * Inserts a channel whose data contains shorts into this MultipleChannel object at the particular position
     */
    public void setChannel(int channelNo, short[] data, ChannelFormat format) {
        setChannel(channelNo, (Object) data, format);
    }

    /**
     * Inserts a channel whose data contains bytes into this MultipleChannel object at the particular position
     */
    public void setChannel(int channelNo, byte[] data, ChannelFormat format) {
        setChannel(channelNo, (Object) data, format);
    }

    /**
     * Inserts a channel whose data contains ints into this MultipleChannel object at the particular position
     */
    public void setChannel(int channelNo, int[] data, ChannelFormat format) {
        setChannel(channelNo, (Object) data, format);
    }

    /**
     * Inserts a channel whose data contains doubles into this MultipleChannel object at the particular position
     */
    public void setChannel(int channelNo, double[] data, ChannelFormat format) {
        setChannel(channelNo, (Object) data, format);
    }

    /**
     * Inserts the imaginary part of the given channel whose data contains shorts into this MultipleChannel object at
     * the particular position
     */
    public void setChannelImag(int channelNo, short[] data) {
        setChannelImag(channelNo, (Object) data);
    }

    /**
     * Inserts the imaginary part of the given channel whose data contains bytes into this MultipleChannel object at the
     * particular position
     */
    public void setChannelImag(int channelNo, byte[] data) {
        setChannelImag(channelNo, (Object) data);
    }

    /**
     * Inserts the imaginary part of the given channel whose data contains ints into this MultipleChannel object at the
     * particular position
     */
    public void setChannelImag(int channelNo, int[] data) {
        setChannelImag(channelNo, (Object) data);
    }

    /**
     * Inserts the imaginary part of the given channel whose data contains doubles into this MultipleChannel object at
     * the particular position
     */
    public void setChannelImag(int channelNo, double[] data) {
        setChannelImag(channelNo, (Object) data);
    }

    /**
     * @return the data at the requested channel. This could be any type of java object array i.e. short[], int[],
     *         byte[], double[] etc
     */
    public Object getChannel(int channelNo) {
        if (channelNo >= getChannels()) {
            return null;
        }
        return getDataArrayReal(channelNo);
    }

    /**
     * @return the imaginary data at the requested channel. This could be any type of java object array i.e. short[],
     *         int[], byte[], double[] etc
     */
    public Object getChannelImag(int channelNo) {
        if (channelNo >= getChannels()) {
            return null;
        }
        return getDataArrayImag(channelNo);
    }

    /**
     * @return the number of samples contained within the given channel
     */
    public int getChannelLength(int channelNo) {
        if (channelNo >= getChannels()) {
            return -1;
        }
        return getDependentVariableDimensions(channelNo)[0];
    }

    /**
     * @return the number of samples contained within the given imaginary component of the given channel
     */
    public int getChannelLengthImag(int channelNo) {
        if (channelNo >= getChannels()) {
            return -1;
        }
        return getDependentVariableDimensions(channelNo)[1];
    }

    /**
     * @return the format of the data at the requested channel.
     */
    public ChannelFormat getChannelFormat(int channelNo) {
        if (channelNo >= getChannels()) {
            return null;
        }
        return (ChannelFormat) channelFormat.elementAt(channelNo);
    }

    /*
     * Sets the vector used to store the formats for each channel to the one
     * specified
     */

    private void setChannelFormatContainer(Vector container) {
        channelFormat = container;
    }

    /*
     * Sets the vector used to store the formats for each channel to the one
     * specified
     */

    public Vector getChannelFormatContainer() {
        return channelFormat;
    }

    /*
     * Gets the number of channels in this muliple audio data set
     */

    public int getChannels() {
        return channelFormat.size();
    }

    /*
    * @return TrianaType Copy by value of the current Object except for an
updated <i>description</i>
    */

    public TrianaType copyMe() {
        MultipleChannel v = null;
        try {
            v = (MultipleChannel) getClass().newInstance();
            v.setDimensions(1, getChannels());
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
     * Copies modifiable parameters from the argument object to the current object. The copying is by value, not by
     * reference. Parameters are defined as data not held in <i>dataContainer</i>. They are modifiable if they have
     * <i>set...</i> methods. Parameters that cannot be modified, but which are set by constructors, should be placed
     * correctly into the copied object when it is constructed. </p><p> In MultipleChannel, only the parameters
     * <i>samplingRate</i>, <i>acquisitionTime</i>, and <i>uniformlySampled</i> need to be copied. The obsolete
     * parameter <i>samplingFrequency</i> is generated automatically when the other parameters are copied and set.
     * </p><p> This must be overridden by any subclass that defines new parameters. The overriding method should invoke
     * its super method. It should use the <i>set...</i> and <i>get...</i> methods for the parameters in question. This
     * method is protected so that it cannot be called except by objects that inherit from this one. It is called by
     * <i>copyMe</i>.
     *
     * @param source Data object that contains the data to be copied.
     */
    protected void copyParameters(TrianaType source) {
        super.copyParameters(source);
        if (source instanceof MultipleChannel) {
            setChannelFormatContainer((Vector) ((MultipleChannel) source).getChannelFormatContainer().clone());
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
        int els = channelFormat.size();
        dos.println(String.valueOf(els));
        for (int i = 0; i < els; ++i) {
            dos.println(channelFormat.elementAt(i).toString());
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
        String first = dis.readLine();
        int els = Str.strToInt(first);
        channelFormat = new Vector();
        ChannelFormat au;
        for (int i = 0; i < els; ++i) {
            au = createChannelFormatFrom(dis.readLine());
            channelFormat.addElement(au);
        }
    }


    /**
     * This function creates a ChannelFormat object from the given String using the toString and setFromString()
     * functions with a ChannelFormat
     */
    public abstract ChannelFormat createChannelFormatFrom(String line);


    /**
     * Tests the argument object to determine if it makes sense to perform arithmetic operations between it and the
     * current object. </p><p> In Mutiple Audio, this method first tests for compatibility with superior classes, and
     * then (if the input object is a Mutiple Audio) tests that the input has the same channel count. </p><p> Classes
     * derived from this should over-ride this method with further tests as appropriate. The over-riding method should
     * normally have the first lines <PRE> boolean test = super.isCompatible( obj ); </PRE>followed by other tests. If
     * other types not subclassed from GraphType or Const should be allowed to be compatible then other tests must be
     * implemented.
     *
     * @param obj The data object to be compared with the current one
     * @return <I>True</I> if the object can be combined with the current one
     */
    public boolean isCompatible(TrianaType obj) {
        boolean test = super.isCompatible(obj);
        if ((test) && (obj instanceof MultipleChannel)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Determines whether the argument TrianaType is equal to the current MultipleChannel. They are equal if the
     * argument is a MultipleChannel with the same size, parameters, and data. </p><p> This method must be over-ridden
     * in derived types. In a derived type called xxx the method should begin<PRE> if ( !( obj instanceof xxx ) ) return
     * false; if ( !isCompatible( obj ) ) return false; </PRE>followed by tests that are specific to type xxx (testing
     * its own parameters) and then as a last line<PRE> return super.equals( obj ); </PRE>This line invokes the other
     * equals methods up the chain to GraphType. Each superior object tests its own parameters. </p><p>
     *
     * @param obj The object being tested
     * @return <i>true</i> if they are equal or <i>false</i> otherwise
     */
    public boolean equals(TrianaType obj) {
        if (!(obj instanceof MultipleChannel)) {
            return false;
        }
        MultipleChannel ma = (MultipleChannel) obj;
        Vector acfContainer = ma.getChannelFormatContainer();

        int els = channelFormat.size();
        int els2 = acfContainer.size();

        if (els != els2) {
            return false;
        }

        for (int i = 0; i < els; ++i) {
            if (!(channelFormat.elementAt(i
            ).equals(acfContainer.elementAt(i)))) {
                return false;
            }
        }

        return super.equals(obj);
    }
}




