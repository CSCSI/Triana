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
 * A Recip unit to compute the reciprocals of the elements of an input data array. The array can be real or complex.
 * <p/>
 * This OldUnit obeys the conventions of Triana Type 2 data types.
 *
 * @author Bernard Schutz
 * @version 2.1 13 January 2001
 */
public class Recip extends OldUnit {

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Computes the reciprocals of the elements of the input data set";
    }

    /**
     * ********************************************* ** USER CODE of Recip goes here    ***
     * *********************************************
     */
    public void process() {


        Object input, output;

        input = getInputAtNode(0);
        if (input instanceof EmptyingType) {
            return;
        }
        Class inputClass = input.getClass();
        setOutputType(inputClass);
        output = input;

        if (input instanceof GraphType) {
            FlatArray tempR, tempI;
            int dv, j;
            double d;
            double[] inputdataR, inputdataI;
            for (dv = 0; dv < ((GraphType) input).getDependentVariables(); dv++) {
                if (((GraphType) input).isArithmeticArray(dv)) {
                    tempR = new FlatArray(((GraphType) input).getDataArrayReal(dv));
                    inputdataR = (double[]) tempR.getFlatArray();
                    if (((GraphType) input).isDependentComplex(dv)) {
                        tempI = new FlatArray(((GraphType) input).getDataArrayImag(dv));
                        inputdataI = (double[]) tempI.getFlatArray();
                        for (j = 0; j < inputdataI.length; j++) {
                            d = inputdataR[j] * inputdataR[j] + inputdataI[j] * inputdataI[j];
                            if (d == 0) {
                                inputdataR[j] = Double.POSITIVE_INFINITY;
                                inputdataI[j] = 0;
                            } else {
                                inputdataR[j] = inputdataR[j] / d;
                                inputdataI[j] = -inputdataI[j] / d;
                            }
                        }
                        ((GraphType) output).setDataArrayImag(tempI.restoreArray(false), dv);
                    } else {
                        for (j = 0; j < inputdataR.length; j++) {
                            inputdataR[j] = (inputdataR[j] == 0) ? Double.POSITIVE_INFINITY : 1.0 / inputdataR[j];
                        }
                    }
                    ((GraphType) output).setDataArrayReal(tempR.restoreArray(false), dv);
                }
            }
        } else if (input instanceof Const) {
            double r, i, d;
            r = ((Const) input).getReal();
            if (((Const) input).isComplex()) {
                i = ((Const) input).getImag();
                d = r * r + i * i;
                if (d == 0) {
                    r = Double.POSITIVE_INFINITY;
                    i = 0;
                } else {
                    r = r / d;
                    i = -i / d;
                }
                ((Const) output).setImag(i);
            } else {
                r = (r == 0) ? Double.POSITIVE_INFINITY : 1.0 / r;
            }
            ;
            ((Const) output).setReal(r);

        }

        output(output);


    }


    /**
     * Initialses information specific to Recip.
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
     * Resets Recip
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves Recip's parameters to the parameter file.
     */
    public void saveParameters() {
    }

    /**
     * Loads Recip's parameters of from the parameter file.
     */
    public void setParameter(String name, String value) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to Recip, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "GraphType Const";
    }

    /**
     * @return a string containing the names of the types output from Recip, each separated by a white space.
     */
    public String outputTypes() {
        return "GraphType Const";
    }

    /**
     *
     * @returns the location of the help file for this unit.
     */
    public String getHelpFile() {
        return "Recip.html";
    }


    /**
     * @return Recip's parameter window sp that Triana can move and display it.
     */
    public Window getParameterWindow() {
        return null;
    }


    /**
     * Captures the events thrown out by Recip.
     */
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);   // we need this

    }
}



















