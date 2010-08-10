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

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;


/**
 * Class GPSCalendar provides a conversion between GPS time (in seconds or milliseconds) and normal dates (UTC time). It
 * uses the Java GregorianCalendar class to handle dates, and adds the information to normalize counting of GPS seconds.
 * The user creates an instance of this class by giving a time in conventional form -- by giving the year, month, etc --
 * or by giving the GPS time in seconds. Then the instance can return information about the time -- conventional UTC
 * time information or GPS information -- by using various get methods.
 * <p/>
 * The Calendar is constructed using the GMT time zone. This prevents offsets between dates computed using the local
 * time zone and those referred to the reference epoch of the Java Calendar and Date classes, namely January 1, 1970,
 * 00:00:00 GMT. All UTC times returned are thus referred to GMT.
 *
 * @author Rob Davies
 * @author Bernard Schutz
 * @version $Revision: 4048 $
 */
public class GPSCalendar extends GregorianCalendar {

    /**
     * The number of milliseconds from the reference epoch of the Calendar (which is the time January 1, 1970, 00:00:00
     * GMT) to the GPS reference epoch (January 6, 1980, 00:00:00 GMT).
     */
    private long gpsEpochMSec = 315964800000L;

    /**
     * Default empty constructor. The Calendar is set to the present time using the GMT time zone.
     */
    public GPSCalendar() {
        super(TimeZone.getTimeZone("GMT"));
        complete();
        computeFields();
        //setEpoch();
    }

    /**
     * Construct a Calendar using the GPS time in milliseconds to define the time and setting the time zone to GMT.
     */
    public GPSCalendar(long gpsMilliSec) {
        super(TimeZone.getTimeZone("GMT"));
        complete();
        //setEpoch();
        super.setTimeInMillis(gpsMilliSec + gpsEpochMSec);
        computeFields();
    }

    /**
     * Construct a GPSCalendar Calendar using conventional time notation (not hours) as referred to GMT.
     */
    public GPSCalendar(int year, int month, int day, int min, int sec) {
        super(TimeZone.getTimeZone("GMT"));
        complete();
        clear();
        set(year, month, day, min, sec);
        computeTime();
        //setEpoch();
    }

    /**
     * Construct a GPSCalendar Calendar using conventional time notation (including hours) as referred to GMT.
     */
    public GPSCalendar(int year, int month, int day, int hour, int min, int sec) {
        super(TimeZone.getTimeZone("GMT"));
        complete();
        clear();
        set(year, month, day, hour, min, sec);
        computeTime();
        //setEpoch();
    }

    /**
     * Set the Calendar time in milliseconds of the GPS reference epoch.

     private void setEpoch() {

     //GregorianCalendar gpsEpoch = new GregorianCalendar(1980,0,6,0,59,47);

     GregorianCalendar gpsEpoch = new GregorianCalendar(1980,0,6,0,0,0);
     gpsEpochMSec = gpsEpoch.getTime().getTime();

     //System.out.println("Date Object : " + gpsEpoch);
     System.out.println("Date MS : " + gpsEpochMSec );
     }
     */

    /**
     * Get the GPS seconds corresponding to the time set in the current Calendar. Fractions of a second are truncated.
     * Avoid using this method because its name is ambiguous. Use the identical method getGPSSeconds(), for which this
     * is an alias. Kept for compatibility with previous versions.
     *
     * @return long The GPS time in seconds corresponding to this time
     */
    public long getSeconds() {
        return getGPSSeconds();
    }

    /**
     * Get the GPS seconds corresponding to the time set in the current Calendar. Fractions of a second are truncated.
     *
     * @return long The GPS time in seconds corresponding to this time
     */
    public long getGPSSeconds() {
        long msec = super.getTimeInMillis();
        msec -= gpsEpochMSec;
        long sec = msec / 1000;

        //System.out.println("Date Object Cur : " + toString());
        //System.out.println("Date MS : " + getTimeInMillis());

        return sec;
    }

    /**
     * Get the GPS time in milliseconds corresponding to the time set in the current Calendar.
     *
     * @return long The GPS time in milliseconds corresponding to this time
     */
    public long getGPSMilliseconds() {

        long msec = super.getTimeInMillis();
        msec -= gpsEpochMSec;

        //System.out.println("Date Object Cur : " + toString());
        //System.out.println("Date MS : " + getTimeInMillis());

        return msec;
    }

    /**
     * Set the time of this Calendar by giving the GPS time in seconds.
     *
     * @param gpsTime The GPS time in seconds
     */
    public void setGPSSeconds(long gpsTime) {
        long time = (gpsTime * 1000) + gpsEpochMSec;
        super.setTimeInMillis(time);
    }

    /**
     * Get the time corresponding to the date of this Calendar in milliseconds from the reference time for Java
     * Calendars (not the GPS time in milliseconds).
     *
     * @return long The Calendar time in milliseconds
     */
    public long getTimeInMillis() {
        return super.getTimeInMillis();
    }


    /**
     * Get the year corresponding to the time held in this calendar
     *
     * @return int The year for this date
     */
    public int getUTCYear() {
        return get(Calendar.YEAR);
    }

    /**
     * Get the month corresponding to the time held in this calendar
     *
     * @return int The month for this date
     */
    public int getUTCMonth() {
        return get(Calendar.MONTH);
    }

    /**
     * Get the day of the month corresponding to the time held in this calendar
     *
     * @return int The day of the month for this date (range 1..31)
     */
    public int getUTCDay() {
        return get(Calendar.DATE);
    }

    /**
     * Get the day of the year corresponding to the time held in this calendar
     *
     * @return int The day of the week for this date (range 1..366)
     */
    public int getUTCDayOfYear() {
        return get(Calendar.DAY_OF_YEAR);
    }

    /**
     * Get the hour of the day corresponding to the time held in this calendar
     *
     * @return int The hour of the day for this date (on 24-hour clock)
     */
    public int getUTCHour() {
        return get(Calendar.HOUR_OF_DAY);
    }

    /**
     * Get the minute corresponding to the time held in this calendar
     *
     * @return int The minute of the hour for this date
     */
    public int getUTCMinute() {
        return get(Calendar.MINUTE);
    }

    /**
     * Get the second corresponding to the time held in this calendar
     *
     * @return int The second of the minute for this date
     */
    public int getUTCSecond() {
        return get(Calendar.SECOND);
    }

    /**
     * Get the millisecond corresponding to the time held in this calendar
     *
     * @return int The millisecond part of the second of the time for this date
     */
    public int getUTCMillisecond() {
        return get(Calendar.MILLISECOND);
    }


}

