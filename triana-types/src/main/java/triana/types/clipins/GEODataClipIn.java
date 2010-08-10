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
package triana.types.clipins;


import java.text.SimpleDateFormat;

import org.trianacode.taskgraph.clipin.AttachInfo;
import org.trianacode.taskgraph.clipin.ClipIn;
import triana.types.util.GPSCalendar;

/**
 * GEODataClipIn is an Object containing information about data derived from the GEO or other gravitational wave
 * detector. The data fields are: detector name, channel name, GPS time of first data element, UTC calendar date String
 * of the time of the first data element, orginal precision of data (in bits), whether the data has been calibrated or
 * de-whitened, and a quality indicator for the data. These data supplement the data that are held in data sets that
 * this ClipIn may be attached to.
 * <p/>
 * There are methods for setting and getting the two data. The dates are inter-convertible, and when one is changed by a
 * set method then the other is automatically updated.
 *
 * @author Bernard Schutz
 * @version $Revision: 4048 $
 */

public class GEODataClipIn extends Object implements ClipIn {
    private String detector;
    private String channelName;
    private long gpsMilliseconds;
    private String gpsDate;
    private int precision;
    private boolean calibrated;
    private boolean dewhitened;
    private GPSCalendar time;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd  hh:mm:ss");


    /**
     * Constructs an empty GEODataClipIn.
     */
    public GEODataClipIn() {
    }

    /**
     * Constructs a GEODataClipIn using the desired input data, with the time given in GPS milliseconds.
     */
    public GEODataClipIn(String det, String name, int prec, boolean cal, boolean dewhite, long gpsMs) {
        detector = det;
        channelName = name;
        precision = prec;
        calibrated = cal;
        dewhitened = dewhite;
        gpsMilliseconds = gpsMs;
        time = new GPSCalendar(gpsMs);
        gpsDate = dateFormat.format(time.getTime());
    }

    /**
     * Constructs a GEODataClipIn using the desired input data, with the time given by calendar arguments.
     */
    public GEODataClipIn(String det, String name, int prec, boolean cal, boolean dewhite, int yr, int mo, int da,
                         int hr, int min, int sec) {
        detector = det;
        channelName = name;
        precision = prec;
        calibrated = cal;
        dewhitened = dewhite;
        time = new GPSCalendar(yr, mo, da, hr, min, sec);
        gpsMilliseconds = time.getGPSMilliseconds();
        gpsDate = dateFormat.format(time.getTime());
    }


    /*
     * Implement the methods of the ClipIn interface
     */

    /**
     * This method is called before the clip-in enters a task's clip-in bucket. This occurs when either the data it is
     * attached to is input by the task, or when the unit directly adds the clip-in to its bucket.
     *
     * @param info info about the task the clip-in is being attached to
     */
    public void initializeAttach(AttachInfo info) {
    }

    /**
     * This method is called when the clip-in is removed from a task's clip-in bucket. This occurs when either the data
     * it is attached to is output by the task, or when the unit directly remove the clip-in from its bucket.
     *
     * @param info info about the task the clip-in is being removed from
     */

    public void finalizeAttach(AttachInfo info) {
    }

    /**
     * Clones the ClipIn to an identical one. This is a copy by value, not by reference. This method must be implemented
     * for each class in a way that depends on the contents of the ClipIn.
     *
     * @return a copy by value of the current ClipIn
     */
    public Object clone() {
        return new GEODataClipIn(detector, channelName, precision, calibrated, dewhitened, gpsMilliseconds);
    }

    /*
     * Implement methods specific to GEODataClipIn
     */

    /**
     * Sets the detector name.
     *
     * @param name The new detector name
     */
    public void setDetector(String name) {
        detector = name;
    }


    /**
     * Sets the channel name.
     *
     * @param chan The new channel name
     */
    public void setChannel(String chan) {
        channelName = chan;
    }


    /**
     * Sets the precision of the acquired data, in bits.
     *
     * @param bits The new precision
     */
    public void setPrecision(int bits) {
        precision = bits;
    }


    /**
     * Sets the calibration state of the data
     *
     * @param cal The new calibration state
     */
    public void setCalibrated(boolean cal) {
        calibrated = cal;
    }


    /**
     * Sets the dewhitening state of the data
     *
     * @param dewhite The new whitening state
     */
    public void setDewhitened(boolean dewhite) {
        dewhitened = dewhite;
    }


    /**
     * Sets the GPS time in milliseconds of the first sample of the data
     *
     * @param gps The new first sample time in milliseconds
     */
    public void setGPS(long gps) {
        gpsMilliseconds = gps;
        time = new GPSCalendar(gps);
        gpsDate = dateFormat.format(time.getTime());
    }


    /**
     * Sets the GPS time of the first sample of the data by giving calendar information
     *
     * @param yr  The year of the first sample (4 digits)
     * @param mo  The month of the first sample (January = 0)
     * @param da  The day of the month of the first sample (starting at 1)
     * @param hr  The hour of the day of the first sample
     * @param min The minute of the hour of the first sample
     * @param sec The second of the minute of the first sample
     */
    public void setGPS(int yr, int mo, int da, int hr, int min, int sec) {
        time = new GPSCalendar(yr, mo, da, hr, min, sec);
        gpsMilliseconds = time.getGPSMilliseconds();
        gpsDate = dateFormat.format(time.getTime());
    }


    /**
     * Returns the detector name.
     *
     * @return String The detector name
     */
    public String getDetector() {
        return detector;
    }


    /**
     * Returns the channel name.
     *
     * @return String The channel name
     */
    public String getChannel() {
        return channelName;
    }


    /**
     * Returns the precision of the acquired data, in bits.
     *
     * @return int The precision
     */
    public int getPrecision() {
        return precision;
    }


    /**
     * Returns the calibration state of the data
     *
     * @return boolean The calibration state: true if calibrated
     */
    public boolean getCalibrated() {
        return calibrated;
    }


    /**
     * Returns the dewhitening state of the data
     *
     * @return boolean The dewhitening state: true if de-whitened
     */
    public boolean getDewhitened() {
        return dewhitened;
    }


    /**
     * Returns the GPS time in milliseconds of the first sample of the data
     *
     * @return long The first sample time in milliseconds
     */
    public long getGPSMs() {
        return gpsMilliseconds;
    }


    /**
     * Returns the GPS time of the first sample of the data by giving calendar information
     *
     * @return String The time of the first sample of the data as a UTC calendar date String
     */
    public String getGPSDateString() {
        return gpsDate;
    }


}









