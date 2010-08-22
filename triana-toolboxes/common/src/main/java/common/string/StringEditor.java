package common.string;

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
 * Edits a String or multiple strings. If the unit has multiple string inputs then they are appended to the editor.
 *
 * @author Matthew Shields
 * @version $Revision: 2921 $
 */
public class StringEditor extends Unit {

    // parameter data type definitions
    private String inputStr;
    static final String inputParamName = "INPUT_STRING";
    static final String outputParamName = "OUTPUT_STRING";
    private boolean edited;

    /**
     * This is called when the network is forcably stopped by the user. This should be over-ridden with the desired
     * tasks.
     */
    public void stopping() {
        synchronized (this) {
            edited = true;
            this.notifyAll();
        }
    }

    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {
        StringBuffer buff = new StringBuffer();
        Object obj;
        String str;

        for (int i = 0; i < getInputNodeCount(); i++) {
            obj = getInputAtNode(i);

            if (obj instanceof byte[]) {
                str = new String((byte[]) obj) + "\n";
            } else {
                str = obj.toString() + "\n";
            }

            buff.append(str);
        }

        setParameter(outputParamName, "");
        setParameter(inputParamName, buff.toString());
        edited = false;
        showParameterPanel();
        synchronized (this) {
            while (!edited) {
                try {
                    this.wait();
                }
                catch (InterruptedException e) {
                }
            }
        }
        output(inputStr);
    }


    /**
     * Called when the unit is created. Initialises the unit's properties and parameters.
     */
    public void init() {
        super.init();

        // Initialise node properties
        setDefaultInputNodes(1);
        setMinimumInputNodes(0);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        // Initialise parameter update policy
        setParameterUpdatePolicy(IMMEDIATE_UPDATE);

        // Initialise pop-up description and help file location
        setPopUpDescription("Edits a String, multiple inputs will be concatenated");
        setHelpFileLocation("StringEditor.html");

        // Define initial value and type of parameters
        defineParameter(inputParamName, "", USER_ACCESSIBLE);

        // Initialise custom panel interface
        setParameterPanelClass("common.string.StringEditorPanel");
        setParameterPanelInstantiate(ON_USER_ACCESS);
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        inputStr = (String) getParameter(inputParamName);
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        if (paramname.equals(outputParamName)) {
            inputStr = (String) value;
            synchronized (this) {
                edited = true;
                this.notifyAll();
            }
        }
    }


    /**
     * @return an array of the input types for StringGen
     */
    public String[] getInputTypes() {
        return new String[]{"java.lang.Object"};
    }

    /**
     * @return an array of the output types for StringGen
     */
    public String[] getOutputTypes() {
        return new String[]{"java.lang.Object"};
    }

}



