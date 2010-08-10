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

import triana.types.util.ChannelFormat;
import triana.types.util.SpectralChannelFormat;

/**
 * MultipleSpectra stores many channels of frequency data.Each channel can have its own particular format of the data
 * e.g. sampling frequency, number of points, whether the data is one or two sided, whether it is narrow etc
 *
 * @author Ian Taylor
 * @version $Revision: 4048 $
 * @see ChannelFormat
 * @see SpectralChannelFormat
 * @see MultipleChannel
 */
public class MultipleSpectra extends MultipleChannel implements AsciiComm {

    /**
     * Creates an empty MultipleSpectra Object with no channels added.
     */
    public MultipleSpectra() {
        super();
    }

    /**
     * Creates a MultipleSpectra object with a specific number of channels
     */
    public MultipleSpectra(int channels) {
        super(channels);
    }

    /**
     * Added by I. Taylor, August 2001 : This function sets the default labeling scheme used for all GraphTypes. Default
     * values are 'X' for the X-axis and 'Y' for the Y axis.  The various subclasses can override this function with
     * their specific axis-labelling conventions.
     */
    public void setDefaultAxisLabelling() {
        String labelx = "Frequency (Hz)";
        String labely = "Amplitude";
        setIndependentLabels(0, labelx);
        setDependentLabels(0, labely);
    }

    /**
     * @return the format of the data at the requested channel.
     */
    public SpectralChannelFormat getSpectralChannelFormat(int channelNo) {
        return (SpectralChannelFormat) super.getChannelFormat(channelNo);
    }

    /**
     * This function creates a ChannelFormat object from the given String using the toString and setFromString()
     * functions with a ChannelFormat
     */
    public ChannelFormat createChannelFormatFrom(String line) {
        SpectralChannelFormat au = new SpectralChannelFormat();
        au.setFromString(line);
        return au;
    }

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
        if ((test) && (obj instanceof MultipleSpectra)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Determines whether the argument TrianaType is equal to the current MultipleSpectra. They are equal if the
     * argument is a MultipleSpectra with the same size, parameters, and data. </p><p> This method must be over-ridden
     * in derived types. In a derived type called xxx the method should begin<PRE> if ( !( obj instanceof xxx ) ) return
     * false; if ( !isCompatible( obj ) ) return false; </PRE>followed by tests that are specific to type xxx (testing
     * its own parameters) and then as a last line<PRE> return super.equals( obj ); </PRE>This line invokes the other
     * equals methods up the chain to GraphType. Each superior object tests its own parameters. </p><p>
     *
     * @param obj The object being tested
     * @return <i>true</i> if they are equal or <i>false</i> otherwise
     */
    public boolean equals(TrianaType obj) {
        if (!(obj instanceof MultipleSpectra)) {
            return false;
        }
        return super.equals(obj);
    }
}






