package math.functions;

/*
 * Copyright (c) 1995 onwards, University of Wales College of Cardiff
 *
 * Permission to use and modify this software and its documentation for
 * any purpose is hereby granted without fee provided a written agreement
 * exists between the recipients and the University.
 *
 * Further conditions of use are that (i) the above copyright notice and
 * this permission notice appear in all copies of the software and
 * related documentation, and (ii) the recipients of the software and
 * documentation undertake not to copy or redistribute the software and
 * documentation to any other party.
 *
 * THE SOFTWARE IS PROVIDED "AS-IS" AND WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS, IMPLIED OR OTHERWISE, INCLUDING WITHOUT LIMITATION, ANY
 * WARRANTY OF MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.
 *
 * IN NO EVENT SHALL THE UNIVERSITY OF WALES COLLEGE OF CARDIFF BE LIABLE
 * FOR ANY SPECIAL, INCIDENTAL, INDIRECT OR CONSEQUENTIAL DAMAGES OF ANY
 * KIND, OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 * PROFITS, WHETHER OR NOT ADVISED OF THE POSSIBILITY OF DAMAGE, AND ON
 * ANY THEORY OF LIABILITY, ARISING OUT OF OR IN CONNECTION WITH THE USE
 * OR PERFORMANCE OF THIS SOFTWARE.
 */


import java.awt.Window;
import java.awt.event.ActionEvent;

import triana.types.Const;
import triana.types.EmptyingType;
import triana.types.GraphType;
import triana.types.OldUnit;
import triana.types.util.FlatArray;


/**
 * An ArcSinh unit to compute the inverse hyperbolic sine of the elements of an input data object. The input data can be
 * either GraphType or Const. The function is applied to each element of each arithmetic dependent-data array of an
 * input GraphType, regardless of dimensionality.
 * <p/>
 * The input data arrays can be real or complex. If the data are real, then the returned data set will be real. If the
 * data are complex, then the output data will be complex.
 * <p/>
 * This OldUnit obeys the conventions of Triana Type 2 data types.
 *
 * @author Bernard Schutz
 * @version 2.1 13 January 2001
 */
public class ArcSinh extends OldUnit {

    double Pi2 = Math.PI / 2.0;

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Computes the inverse hyperbolic sine of the elements of the input data set";
    }

    /**
     * ********************************************* ** USER CODE of ArcSinh goes here    ***
     * *********************************************
     */
    public void process() {


        Object input, output;

        output = null;
        input = getInputAtNode(0);
        if (input instanceof EmptyingType) {
            return;
        }
        output = input;
        Class outputClass = output.getClass();
        setOutputType(outputClass);


        if (input instanceof GraphType) {
            FlatArray tempR, tempI;
            int dv, j;
            double p, q, ppq, tmp1, tmp2;
            double[] inputdataR, inputdataI;
            for (dv = 0; dv < ((GraphType) input).getDependentVariables(); dv++) {
                if (((GraphType) input).isArithmeticArray(dv)) {
                    tempR = new FlatArray(((GraphType) input).getDataArrayReal(dv));
                    inputdataR = (double[]) tempR.getFlatArray();
                    if (((GraphType) input).isDependentComplex(dv)) {
                        tempI = new FlatArray(((GraphType) input).getDataArrayImag(dv));
                        inputdataI = (double[]) tempI.getFlatArray();
                        for (j = 0; j < inputdataI.length; j++) {
                            if (inputdataR[j] != 0.0) {
                                tmp1 = 1.0 + inputdataI[j];
                                tmp2 = inputdataR[j] * inputdataR[j];
                                p = Math.sqrt(tmp1 * tmp1 + tmp2);
                                tmp1 -= 2.0;
                                q = Math.sqrt(tmp1 * tmp1 + tmp2);
                                ppq = (p + q) * 0.5;
                                inputdataI[j] = Math.asin(inputdataI[j] / ppq);
                                tmp1 = Math.log(ppq + Math.sqrt(ppq * ppq - 1.0));
                                inputdataR[j] = (inputdataR[j] < 0.0) ? -tmp1 : tmp1;
                            } else {
                                if (inputdataI[j] >= 1.0) {
                                    inputdataR[j] = Math
                                            .log(inputdataI[j] + Math.sqrt(inputdataI[j] * inputdataI[j] - 1.0));
                                    inputdataI[j] = Pi2;
                                } else {
                                    if (inputdataI[j] > -1.0) {
                                        inputdataR[j] = 0.0;
                                        inputdataI[j] = Math.asin(inputdataI[j]);
                                    } else {
                                        inputdataR[j] = Math
                                                .log(-inputdataI[j] + Math.sqrt(inputdataI[j] * inputdataI[j] - 1.0));
                                        inputdataI[j] = -Pi2;
                                    }
                                }
                            }
                        }
                        ((GraphType) output).setDataArrayReal(tempR.restoreArray(false), dv);
                        ((GraphType) output).setDataArrayImag(tempI.restoreArray(false), dv);
                    } else {
                        for (j = 0; j < inputdataR.length; j++) {
                            inputdataR[j] = Math.log(inputdataR[j] + Math.sqrt(inputdataR[j] * inputdataR[j] + 1.0));
                        }
                        ((GraphType) output).setDataArrayReal(tempR.restoreArray(false), dv);
                    }
                }
            }
        } else if (input instanceof Const) {
            double r, i, p, q, ppq, tmp1, tmp2;
            r = ((Const) input).getReal();
            if (((Const) input).isComplex()) {
                i = ((Const) input).getImag();
                if (r != 0.0) {
                    tmp1 = 1.0 + i;
                    tmp2 = r * r;
                    p = Math.sqrt(tmp1 * tmp1 + tmp2);
                    tmp1 -= 2.0;
                    q = Math.sqrt(tmp1 * tmp1 + tmp2);
                    ppq = (p + q) * 0.5;
                    i = Math.asin(i / ppq);
                    tmp1 = Math.log(ppq + Math.sqrt(ppq * ppq - 1.0));
                    r = (r < 0.0) ? -tmp1 : tmp1;
                } else {
                    if (i >= 1.0) {
                        r = Math.log(i + Math.sqrt(i * i - 1.0));
                        i = Pi2;
                    } else {
                        if (i > -1.0) {
                            r = 0.0;
                            i = Math.asin(i);
                        } else {
                            r = Math.log(-i + Math.sqrt(i * i - 1.0));
                            i = -Pi2;
                        }
                    }
                }
                ((Const) output).setImag(i);
            } else {
                r = Math.log(r + Math.sqrt(r * r + 1.0));
            }
            ((Const) output).setReal(r);

        }

        output(output);


    }


    /**
     * Initialses information specific to ArcSinh.
     */
    public void init() {
        super.init();

        setResizableInputs(false);
        setResizableOutputs(true);
        // This is to ensure that we receive arrays containing double-precision numbers
        setRequireDoubleInputs(true);
        setCanProcessDoubleArrays(true);

    }

    /**
     * Resets ArcSinh
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves ArcSinh's parameters to the parameter file.
     */
    public void saveParameters() {
    }

    /**
     * Loads ArcSinh's parameters of from the parameter file.
     */
    public void setParameter(String name, String value) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to ArcSinh, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "GraphType Const";
    }

    /**
     * @return a string containing the names of the types output from ArcSinh, each separated by a white space.
     */
    public String outputTypes() {
        return "GraphType Const";
    }

    /**
     *
     * @returns the location of the help file for this unit.
     */
    public String getHelpFile() {
        return "ArcSinh.html";
    }


    /**
     * @return ArcSinh's parameter window sp that Triana can move and display it.
     */
    public Window getParameterWindow() {
        return null;
    }


    /**
     * Captures the events thrown out by ArcSinh.
     */
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);   // we need this

    }
}



















