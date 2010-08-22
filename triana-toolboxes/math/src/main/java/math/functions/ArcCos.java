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

import triana.types.ComplexSampleSet;
import triana.types.ComplexSpectrum;
import triana.types.Const;
import triana.types.EmptyingType;
import triana.types.GraphType;
import triana.types.OldUnit;
import triana.types.SampleSet;
import triana.types.Spectrum;
import triana.types.util.FlatArray;


/**
 * An ArcCos unit to compute the inverse cosine of the elements of an input data object. The input data can be either
 * GraphType or Const. The function is applied to each element of each arithmetic dependent-data array of an input
 * GraphType, regardless of dimensionality.
 * <p/>
 * The input data arrays can be real or complex. If the data are real and in the range [-1, 1], then the returned data
 * set will be real. If the data are real but at least one has absolute value greater than 1.0, or if the input data are
 * complex, then the output data will be complex. If necessary input SampleSet and Spectrum sets will be converted to
 * ComplexSampleSet and ComplexSpectrum on output.
 * <p/>
 * This OldUnit obeys the conventions of Triana Type 2 data types.
 *
 * @author Bernard Schutz
 * @version 2.1 13 January 2001
 */
public class ArcCos extends OldUnit {

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Computes the inverse cosine of the elements of the input data set";
    }

    /**
     * ********************************************* ** USER CODE of ArcCos goes here    ***
     * *********************************************
     */
    public void process() {


        Object input, output;

        output = null;
        input = getInputAtNode(0);
        if (input instanceof EmptyingType) {
            return;
        }
        if (input instanceof SampleSet) {
            if ((FlatArray.maxArray(((SampleSet) input).getData()) > 1.0) ||
                    (FlatArray.minArray(((SampleSet) input).getData()) < -1.0)) {
                output = new ComplexSampleSet((SampleSet) input);
            } else {
                output = input;
            }
        } else if (input instanceof Spectrum) {
            if ((FlatArray.maxArray(((Spectrum) input).getData()) > 1.0) ||
                    (FlatArray.minArray(((Spectrum) input).getData()) < -1.0)) {
                output = new ComplexSpectrum((Spectrum) input);
            } else {
                output = input;
            }
        } else {
            output = input;
        }
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
                            if (inputdataI[j] != 0.0) {
                                tmp1 = 1.0 + inputdataR[j];
                                tmp2 = inputdataI[j] * inputdataI[j];
                                p = Math.sqrt(tmp1 * tmp1 + tmp2);
                                tmp1 -= 2.0;
                                q = Math.sqrt(tmp1 * tmp1 + tmp2);
                                ppq = (p + q) * 0.5;
                                inputdataR[j] = Math.acos(inputdataR[j] / ppq);
                                tmp1 = Math.log(ppq + Math.sqrt(ppq * ppq - 1.0));
                                inputdataI[j] = (inputdataI[j] < 0.0) ? tmp1 : -tmp1;
                            } else {
                                if (inputdataR[j] >= 1.0) {
                                    inputdataI[j] = Math
                                            .log(inputdataR[j] + Math.sqrt(inputdataR[j] * inputdataR[j] - 1.0));
                                    inputdataR[j] = 0.0;
                                } else {
                                    if (inputdataR[j] > -1.0) {
                                        inputdataI[j] = 0.0;
                                        inputdataR[j] = Math.acos(inputdataR[j]);
                                    } else {
                                        inputdataI[j] = Math
                                                .log(-inputdataR[j] + Math.sqrt(inputdataR[j] * inputdataR[j] - 1.0));
                                        inputdataR[j] = Math.PI;
                                    }
                                }
                            }
                        }
                        tempR.restoreArray();
                        tempI.restoreArray();
                        ((GraphType) output).setDataArrayReal(((GraphType) input).getDataArrayReal(dv), dv);
                        ((GraphType) output).setDataArrayImag(((GraphType) input).getDataArrayImag(dv), dv);
                    } else {
                        if ((FlatArray.maxArray(inputdataR) <= 1.0) &&
                                (FlatArray.minArray(inputdataR) >= -1.0)) {
                            for (j = 0; j < inputdataR.length; j++) {
                                inputdataR[j] = Math.acos(inputdataR[j]);
                            }
                            tempR.restoreArray();
                            ((GraphType) output).setDataArrayReal(((GraphType) input).getDataArrayReal(dv), dv);
                        } else {
                            setText("WARNING: SEE DEBUG WINDOW!");
                            println("Warning: Some input real data to " + getName()
                                    + " are out of range (abs(data) < 1). Output will be complex.");
                            inputdataI = new double[inputdataR.length];
                            for (j = 0; j < inputdataI.length; j++) {
                                if (Math.abs(inputdataR[j]) <= 1) {
                                    inputdataI[j] = 0.0;
                                    inputdataR[j] = Math.acos(inputdataR[j]);
                                } else if (inputdataR[j] >= 1.0) {
                                    inputdataI[j] = Math
                                            .log(inputdataR[j] + Math.sqrt(inputdataR[j] * inputdataR[j] - 1.0));
                                    inputdataR[j] = 0.0;
                                } else {
                                    inputdataI[j] = Math
                                            .log(-inputdataR[j] + Math.sqrt(inputdataR[j] * inputdataR[j] - 1.0));
                                    inputdataR[j] = Math.PI;
                                }
                            }
                            tempR.restoreArray();
                            ((GraphType) output).setDataArrayReal(((GraphType) input).getDataArrayReal(dv), dv);
                            tempR.setFlatArray(inputdataI);
                            ((GraphType) output).setDataArrayImag(tempR.restoreArray(true), dv);
                        }
                    }
                }
            }
        } else if (input instanceof Const) {
            double r, i, p, q, ppq, tmp1, tmp2;
            r = ((Const) input).getReal();
            i = 0;
            if (((Const) input).isComplex()) {
                i = ((Const) input).getImag();
                if (i != 0.0) {
                    tmp1 = 1.0 + r;
                    tmp2 = i * i;
                    p = Math.sqrt(tmp1 * tmp1 + tmp2);
                    tmp1 -= 2.0;
                    q = Math.sqrt(tmp1 * tmp1 + tmp2);
                    ppq = (p + q) * 0.5;
                    r = Math.acos(r / ppq);
                    tmp1 = Math.log(ppq + Math.sqrt(ppq * ppq - 1.0));
                    i = (i < 0.0) ? tmp1 : -tmp1;
                } else {
                    if (r >= 1.0) {
                        i = Math.log(r + Math.sqrt(r * r - 1.0));
                        i = 0.0;
                    } else {
                        if (r > -1.0) {
                            i = 0.0;
                            r = Math.acos(r);
                        } else {
                            i = Math.log(-r + Math.sqrt(r * r - 1.0));
                            r = Math.PI;
                        }
                    }
                }
                ((Const) output).setImag(i);
            } else if (Math.abs(r) <= 1.0) {
                r = Math.acos(r);
            } else {
                setText("WARNING: SEE DEBUG WINDOW!");
                println("Warning: Input Const to " + getName() + " is real but out of range, (abs(" + String.valueOf(r)
                        + ") > 1). Output will be a complex Const.");
                if (r > 0) {
                    i = Math.log(r + Math.sqrt(r * r - 1.0));
                    r = 0.0;
                } else {
                    i = Math.log(-r + Math.sqrt(r * r - 1.0));
                    r = Math.PI;
                }
                ((Const) output).setImag(i);
            }
            ((Const) output).setReal(r);
        }

        output(output);


    }


    /**
     * Initialses information specific to ArcCos.
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
     * Resets ArcCos
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves ArcCos's parameters to the parameter file.
     */
    public void saveParameters() {
    }

    /**
     * Loads ArcCos's parameters of from the parameter file.
     */
    public void setParameter(String name, String value) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to ArcCos, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "GraphType Const";
    }

    /**
     * @return a string containing the names of the types output from ArcCos, each separated by a white space.
     */
    public String outputTypes() {
        return "GraphType Const";
    }

    /**
     *
     * @returns the location of the help file for this unit.
     */
    public String getHelpFile() {
        return "ArcCos.html";
    }


    /**
     * @return ArcCos's parameter window sp that Triana can move and display it.
     */
    public Window getParameterWindow() {
        return null;
    }


    /**
     * Captures the events thrown out by ArcCos.
     */
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);   // we need this

    }
}



















