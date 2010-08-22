package signalproc.converters;

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
import triana.types.MatrixType;
import triana.types.VectorType;


/**
 * Convert matrix to three vectors of index x, y and data z
 *
 * @author Rui Zhu
 * @version $Revision: 2921 $
 */
public class MatrixToVect extends Unit {

    private String order;    // convert order: row or column major

    /*
    * Called whenever there is data for the unit to process
    */

    public void process() throws Exception {
        MatrixType input = (MatrixType) getInputAtNode(0);

        // Insert main algorithm for MatrixToVect
        double[] xs = input.getXorYReal(0);
        double[] ys = input.getXorYReal(1);
        double[][] zs = input.getDataReal();

        double[] x = new double[xs.length * ys.length];
        double[] y = new double[xs.length * ys.length];
        double[] z = new double[xs.length * ys.length];

        if (order.startsWith("Row")) {
            int idx = 0;
            for (int i = 0; i < xs.length; i++) {
                for (int j = 0; j < ys.length; j++) {
                    x[idx] = xs[i];
                    y[idx] = ys[j];
                    z[idx] = zs[i][j];
                    idx++;
                }
            }
        } else {
            int idx = 0;
            for (int j = 0; j < ys.length; j++) {
                for (int i = 0; i < xs.length; i++) {
                    x[idx] = xs[i];
                    y[idx] = ys[j];
                    z[idx] = zs[i][j];
                    idx++;
                }
            }
        }
        outputAtNode(0, new VectorType(x));
        outputAtNode(1, new VectorType(y));
        outputAtNode(2, new VectorType(z));
    }


    /**
     * Called when the unit is created. Initialises the unit's properties and parameters.
     */
    public void init() {
        super.init();

        // Initialise node properties
        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(1);

        setDefaultOutputNodes(3);
        setMinimumOutputNodes(3);
        setMaximumOutputNodes(3);

        // Initialise parameter update policy
        setParameterUpdatePolicy(PROCESS_UPDATE);

        // Initialise pop-up description and help file location
        setPopUpDescription("Convert matrix to three vectors of index x, y and data z");
        setHelpFileLocation("MatrixToVect.html");

        // Define initial value and type of parameters
        defineParameter("order", "Row Major", USER_ACCESSIBLE);

        String guilines = "";
        guilines
                += "Convert matrix in: $title order Choice [Row major (Y changes fast)] [Column major (X changes fast)]\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        // Set unit variables to the values specified by the parameters
        order = (String) getParameter("rowMajor");
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up MatrixToVect (e.g. close open files) 
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables 
        if (paramname.equals("order")) {
            order = (String) value;
        }

    }


    /**
     * @return an array of the input types for MatrixToVect
     */
    public String[] getInputTypes() {
        return new String[]{"MatrixType"};
    }

    /**
     * @return an array of the output types for MatrixToVect
     */
    public String[] getOutputTypes() {
        return new String[]{"VectorType"};
    }

}



