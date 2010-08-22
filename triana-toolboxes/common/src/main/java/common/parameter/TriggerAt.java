package common.parameter;

/**********************************************************************
 The University of Wales, Cardiff Triana Project Software License (Based
 on the Apache Software License Version 1.1)

 Copyright (c) 2003 University of Wales, Cardiff. All rights reserved.

 Redistribution and use of the software in source and binary forms, with
 or without modification, are permitted provided that the following
 conditions are met:

 1.  Redistributions of source code must retain the above copyright
 notice, this list of conditions and the following disclaimer.

 2.  Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions and the following disclaimer in the
 documentation and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any,
 must include the following acknowledgment: "This product includes
 software developed by the University of Wales, Cardiff for the Triana
 Project (http://www.trianacode.org)." Alternately, this
 acknowledgment may appear in the software itself, if and wherever
 such third-party acknowledgments normally appear.

 4. The names "Triana" and "University of Wales, Cardiff" must not be
 used to endorse or promote products derived from this software
 without prior written permission. For written permission, please
 contact triana@trianacode.org.

 5. Products derived from this software may not be called "Triana," nor
 may Triana appear in their name, without prior written permission of
 the University of Wales, Cardiff.

 6. This software may not be sold, used or incorporated into any product
 for sale to third parties.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN
 NO EVENT SHALL UNIVERSITY OF WALES, CARDIFF OR ITS CONTRIBUTORS BE
 LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 THE POSSIBILITY OF SUCH DAMAGE.

 ------------------------------------------------------------------------

 This software consists of voluntary contributions made by many
 individuals on behalf of the Triana Project. For more information on the
 Triana Project, please see. http://www.trianacode.org.

 This license is based on the BSD license as adopted by the Apache
 Foundation and is governed by the laws of England and Wales.

 **********************************************************************/

import java.util.Calendar;
import java.util.TimeZone;

import org.trianacode.taskgraph.Unit;


/**
 * Trigger at certain time
 *
 * @author Rui Zhu
 * @version $Revision: 2921 $
 */
public class TriggerAt extends Unit {

    // parameter data type definitions
    private int year;
    private int month;
    private int date;
    private int hour;
    private int minute;
    private int second;
    private String zone;


    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {
        // Insert main algorithm for TriggerAt
        //TimeZone tz = TimeZone.getTimeZone("GMT+01");
        Calendar cal;
        if (zone.equals("UTC")) {
            cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        } else {
            cal = Calendar.getInstance();
        }
        cal.set(year, month - 1, date, hour, minute, second);
        long w = cal.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
        if (w > 0) {
            try {
                Thread.sleep(w);
            } catch (InterruptedException e) {
            }
        }

        output(new triana.types.Parameter(new Long(w)));

        //System.out.println("setting: " + cal.getTime());
        //System.out.println("sleep (ms): " + w);
    }


    /**
     * Called when the unit is created. Initialises the unit's properties and parameters.
     */
    public void init() {
        super.init();

        // Initialise node properties
        setDefaultInputNodes(0);
        setMinimumInputNodes(0);
        setMaximumInputNodes(0);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        // Initialise parameter update policy
        setParameterUpdatePolicy(PROCESS_UPDATE);

        // Initialise pop-up description and help file location
        setPopUpDescription("Trigger at certain time");
        setHelpFileLocation("TriggerAt.html");

// not suitable here
// 	Calendar cal = Calendar.getInstance();
// 	String sY = String.valueOf(cal.get(Calendar.YEAR));
// 	String sM = String.valueOf(cal.get(Calendar.MONTH)+1);
// 	String sd = String.valueOf(cal.get(Calendar.DATE));
// 	String sH = String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
// 	String sm = String.valueOf(cal.get(Calendar.MINUTE));
// 	String ss = String.valueOf(cal.get(Calendar.SECOND));

        // Define initial value and type of parameters
        defineParameter("year", "2003", USER_ACCESSIBLE);
        defineParameter("month", "10", USER_ACCESSIBLE);
        defineParameter("date", "3", USER_ACCESSIBLE);
        defineParameter("hour", "20", USER_ACCESSIBLE);
        defineParameter("minute", "0", USER_ACCESSIBLE);
        defineParameter("second", "0", USER_ACCESSIBLE);
        defineParameter("zone", "System Default", USER_ACCESSIBLE);

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "Year $title year TextField 0\n";
        guilines += "Month $title month TextField 0\n";
        guilines += "Date $title date TextField 0\n";
        guilines += "Hour (0-23) $title hour TextField 0\n";
        guilines += "Minute $title minute TextField 0\n";
        guilines += "Second $title second TextField 0\n";
        guilines += "Zone $title zone Choice [System Default] [UTC] 0\n";
        setGUIBuilderV2Info(guilines);

    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        // Set unit variables to the values specified by the parameters
        year = new Integer((String) getParameter("year")).intValue();
        month = new Integer((String) getParameter("month")).intValue();
        date = new Integer((String) getParameter("date")).intValue();
        hour = new Integer((String) getParameter("hour")).intValue();
        minute = new Integer((String) getParameter("minute")).intValue();
        second = new Integer((String) getParameter("second")).intValue();
        zone = (String) getParameter("zone");
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up TriggerAt (e.g. close open files) 
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables 
        if (paramname.equals("year")) {
            year = new Integer((String) value).intValue();
        }

        if (paramname.equals("month")) {
            month = new Integer((String) value).intValue();
        }

        if (paramname.equals("date")) {
            date = new Integer((String) value).intValue();
        }

        if (paramname.equals("hour")) {
            hour = new Integer((String) value).intValue();
        }

        if (paramname.equals("minute")) {
            minute = new Integer((String) value).intValue();
        }

        if (paramname.equals("second")) {
            second = new Integer((String) value).intValue();
        }

        if (paramname.equals("zone")) {
            zone = (String) value;
        }
    }


    /**
     * @return an array of the input types for TriggerAt
     */
    public String[] getInputTypes() {
        return new String[]{};
    }

    /**
     * @return an array of the output types for TriggerAt
     */
    public String[] getOutputTypes() {
        return new String[]{"Parameter"};
    }

}



