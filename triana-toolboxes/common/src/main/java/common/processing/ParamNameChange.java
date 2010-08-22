package common.processing;

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

import org.trianacode.taskgraph.NodeException;
import org.trianacode.taskgraph.ParameterNode;
import org.trianacode.taskgraph.Unit;


/**
 * Allows a parameters name to be changed without affecting the value
 *
 * @author Steven Lewis
 * @version $Revision: 2921 $
 */
public class ParamNameChange extends Unit {

    private String IParam;
    String inputName;
    String outputName;
    private String OParam;

    /*
    * Called whenever there is data for the unit to process
    */

    public void process() throws Exception {
        // Insert main algorithm for ParamNameChange
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

        setDefaultOutputNodes(0);
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(0);

        // Initialise parameter update policy and output policy
        setParameterUpdatePolicy(PROCESS_UPDATE);
        setOutputPolicy(CLONE_MULTIPLE_OUTPUT);

        // Initialise pop-up description and help file location
        setPopUpDescription("Allows a parameters name to be changed without affecting the value ");
        setHelpFileLocation("ParamNameChange.html");

        defineParameter("IParam", "", USER_ACCESSIBLE);
        defineParameter("OParam", "", USER_ACCESSIBLE);

        // Initialise custom panel interface
        setParameterPanelClass("common.processing.ParamNameChangePanel");
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        IParam = (String) getParameter("IParam");
        OParam = (String) getParameter("OParam");

    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up ParamNameChange (e.g. close open files) 
    }


    public void parameterUpdate(String paramname, Object value) {
        try {
            if (paramname.equals("IParam") && (!((String) value).equals(""))) {
                ParameterNode[] paramNodes = getTask().getParameterInputNodes();
                for (int count = 0; count < paramNodes.length; count++) {
                    getTask().removeParameterInputNode(getTask().getParameterInputNode(count));
                }
                System.out.println("Creating Input Node parameter " + (String) value);
                inputName = (String) value;
                defineParameter(inputName, "", USER_ACCESSIBLE);
                try {
                    getTask().addParameterInputNode(inputName);
                }
                catch (NodeException e) {
                    e.printStackTrace(System.out);
                }
            }
            if (paramname.equals("OParam") && (!((String) value).equals(""))) {
                ParameterNode[] paramNodes = getTask().getParameterOutputNodes();
                for (int count = 0; count < paramNodes.length; count++) {
                    getTask().removeParameterOutputNode(getTask().getParameterOutputNode(count));
                }
                System.out.println("Creating output Node parameter " + (String) value);
                outputName = (String) value;
                defineParameter(outputName, "", USER_ACCESSIBLE);
                try {
                    getTask().addParameterOutputNode(outputName);
                    setParameter(outputName, inputName);

                }
                catch (NodeException e) {
                }
            }
            if (paramname.equals(inputName)) {
                setParameter(outputName, (String) value);
            }
        }
        catch (Exception e) {
            System.out.println("Unable to perform function at this time");
            resetParameterInputNode(inputName);
            resetParameterOutputNode(outputName);
        }
    }


    /**
     * @return an array of the types accepted by each input node. For node indexes not covered the types specified by
     *         getInputTypes() are assumed.
     */
    public String[][] getNodeInputTypes() {
        return new String[0][0];
    }

    public void resetParameterInputNode(String param) {
        try {
            ParameterNode[] paramNodes = getTask().getParameterInputNodes();
            for (int count = 0; count < paramNodes.length; count++) {
                if (paramNodes[count].getParameterName().equals("param")) {
                    getTask().removeParameterInputNode(getTask().getParameterInputNode(count));
                }
            }
            if (!(param.equals(""))) {
                getTask().addParameterInputNode(param);
            }
        }
        catch (NodeException e) {
            e.printStackTrace(System.out);
        }
    }

    public void resetParameterOutputNode(String param) {
        try {
            ParameterNode[] paramNodes = getTask().getParameterOutputNodes();
            for (int count = 0; count < paramNodes.length; count++) {
                if (paramNodes[count].getParameterName().equals("param")) {
                    getTask().removeParameterOutputNode(getTask().getParameterOutputNode(count));
                }
            }
            if (!(param.equals(""))) {
                getTask().addParameterOutputNode(param);
            }
        }
        catch (NodeException e) {
            e.printStackTrace(System.out);
        }
    }

    /**
     * @return an array of the input types accepted by nodes not covered by getNodeInputTypes().
     */
    public String[] getInputTypes() {
        return new String[]{};
    }


    /**
     * @return an array of the types output by each output node. For node indexes not covered the types specified by
     *         getOutputTypes() are assumed.
     */
    public String[][] getNodeOutputTypes() {
        return new String[0][0];
    }

    /**
     * @return an array of the input types output by nodes not covered by getNodeOutputTypes().
     */
    public String[] getOutputTypes() {
        return new String[]{};
    }

}



