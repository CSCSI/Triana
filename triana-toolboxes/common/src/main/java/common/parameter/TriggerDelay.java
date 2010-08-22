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

import org.trianacode.taskgraph.Unit;


/**
 * Trigger after certain time of delay
 *
 * @author Rui Zhu
 * @version $Revision: 2921 $
 */
public class TriggerDelay extends Unit {

    // parameter data type definitions
    private long delay;


    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {
        // Insert main algorithm for TriggerDelay
        try {
            Thread.sleep(delay * 1000);
        } catch (InterruptedException e) {
        }
        output(new triana.types.Parameter(new Long(delay)));
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
        setPopUpDescription("Trigger after certain time of delay");
        setHelpFileLocation("TriggerDelay.html");

        // Define initial value and type of parameters
        defineParameter("delay", "0", USER_ACCESSIBLE);

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "Delay (s) $title delay TextField 0\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        // Set unit variables to the values specified by the parameters
        delay = new Long((String) getParameter("delay")).longValue();
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up TriggerDelay (e.g. close open files) 
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables 
        if (paramname.equals("delay")) {
            delay = new Long((String) value).longValue();
        }
    }


    /**
     * @return an array of the input types for TriggerDelay
     */
    public String[] getInputTypes() {
        return new String[]{};
    }

    /**
     * @return an array of the output types for TriggerDelay
     */
    public String[] getOutputTypes() {
        return new String[]{"Parameter"};
    }

}



