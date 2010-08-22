/*
 * The University of Wales, Cardiff Triana Project Software License (Based
 * on the Apache Software License Version 1.1)
 *
 * Copyright (c) 2003 University of Wales, Cardiff. All rights reserved.
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
 */
package signalproc.output;


import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.Unit;
import triana.types.GraphType;
import triana.types.ProcessDoublesInterface;
import triana.types.TrianaType;


/**
 * A SGTGrapher unit.
 *
 * @author Rob Davies
 * @version $Revision $
 */
public class SGTGrapher extends Unit implements ProcessDoublesInterface {


    TrianaType input;
    private int counter = 0;
    signalproc.output.GraphLabels graphLabels = null;
    int lineStyle;

    public void process() throws Exception {
        getTask().setParameter("numberOfInputs", String.valueOf(getInputNodeCount()));


        for (int count = 0; count < getInputNodeCount(); count++) { // loop over all input nodes
            getTask().setParameterType("SGTGraphData_" + count, Task.TRANSIENT);

            if (getTask().getDataInputNode(count).isConnected()) { // only get data from connected input nodes
                input = (TrianaType) getInputAtNode(count);

                if (isClipInName(GraphLabels.Graph_Labels_ClipIn_Tag)) {
                    System.out.println("SGTGrapher, node " + count + " getting clipIn");
                    Object obj = getClipIn(GraphLabels.Graph_Labels_ClipIn_Tag);
                    if (obj instanceof signalproc.output.GraphLabels) {
                        graphLabels = (signalproc.output.GraphLabels) obj;
                        getTask().setParameter("clipInExists", "true");
                        getTask().setParameter("lineStyle", String.valueOf(graphLabels.getLineStyle()));
                        getTask().setParameter("mainTitle", graphLabels.getTitle());
                        getTask().setParameter("xtitle", graphLabels.getXTitle());
                        getTask().setParameter("ytitle", graphLabels.getYTitle());
                        getTask().setParameter("lineKeyTitle", graphLabels.getLineKeyTitle());
                        getTask().setParameter("markType", String.valueOf(graphLabels.getMarkType()));
                        getTask().setParameter("markSize", String.valueOf(graphLabels.getMarkSize()));
                    }
                }

                if (input instanceof GraphType) { // if the input os of type GraphType
                    getTask().setParameter("SGTGraphData_" + count, input);
                }
            } else {
                getTask().setParameter("SGTGraphData_" + count, "notConnected");
            }
            getTask().setParameter("clipInExists", "false"); // reset for the next dataaset
        }

        getTask().setParameter("finished", String.valueOf(++counter));
    }


    /**
     * Initialses information specific to SGTGrapher.
     */
    public void init() {
        super.init();
        setDefaultInputNodes(1);
        setDefaultOutputNodes(0);
        setMinimumInputNodes(1);
        setMaximumInputNodes(Integer.MAX_VALUE);
        setMaximumOutputNodes(0);
        // set these to true if your unit can process double-precision
        // arrays       setRequireDoubleInputs(false);
        setParameterUpdatePolicy(Task.IMMEDIATE_UPDATE);

        // Initialise pop-up description and help file location
        setPopUpDescription("A graphical-displaying unit for rendering input signals");
        setHelpFileLocation("SGTGrapher.html");

        setParameterPanelClass("signalproc.output.SGTGrapherPanel");

        defineParameter("requireDoubleInputs", new Boolean(false), INTERNAL);
        defineParameter("canProcessDoubleArrays", new Boolean(false), INTERNAL);
        defineParameter("xtitle", "", TRANSIENT);
        defineParameter("ytitle", "", TRANSIENT);
        defineParameter("lineStyle", "-1", TRANSIENT);
        defineParameter("clipInExists", "false", TRANSIENT);
        defineParameter("lineKeyTitle", "", TRANSIENT);
        defineParameter("markType", "-1", TRANSIENT);
        defineParameter("markSize", "-1", TRANSIENT);
        defineParameter("finished", "0", TRANSIENT);
        defineParameter("numberOfInputs", "0", TRANSIENT);

        setDefaultNodeRequirement(ESSENTIAL_IF_CONNECTED);
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        // Set unit variables to the values specified by the parameters
    }

    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables
    }


    /**
     * @return a string containing the names of the types allowed to be input to SGTGrapher, each separated by a white
     *         space.
     */
    public String[] getInputTypes() {
        return new String[]{"GraphType"};
    }

    /**
     * @return a string containing the names of the types output from SGTGrapher, each separated by a white space.
     */
    public String[] getOutputTypes() {
        return new String[]{"none"};
    }

    public boolean getRequireDoubleInputs() {
        return ((Boolean) getParameter("requireDoubleInputs")).booleanValue();
    }

    public boolean getCanProcessDoubleArrays() {
        return ((Boolean) getParameter("canProcessDoubleArrays")).booleanValue();
    }

    public void setRequireDoubleInputs(boolean state) {
        setParameter("requireDoubleInputs", new Boolean(state));
    }

    public void setCanProcessDoubleArrays(boolean state) {
        setParameter("canProcessDoubleArrays", new Boolean(state));
    }

}

