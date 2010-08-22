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
 * LnRESS, IMPLIED OR OTHERWISE, INCLUDING WITHOUT LIMITATION, ANY
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

import org.trianacode.gui.windows.ErrorDialog;
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
 * A Ln unit to apply the natural logarithm function to the elements of an input data array. The array can be real or
 * complex. If real, the output is normally real, but if there are negative elements then a complex data set will be
 * output.
 * <p/>
 * This OldUnit obeys the conventions of Triana Type 2 data types.
 *
 * @author Bernard Schutz
 * @version 2.1 13 January 2001
 */
public class Ln extends OldUnit {


    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Applies the ln function to the elements of the input data.";
    }

    /**
     * ********************************************* ** USER CODE of Ln goes here    ***
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
            double d, e, thisAngle;
            double[] inputdataR, inputdataI;
            boolean makeComplex = false;

            for (dv = 0; dv < ((GraphType) input).getDependentVariables(); dv++) {
                if (((GraphType) input).isArithmeticArray(dv)) {
                    tempR = new FlatArray(((GraphType) input).getDataArrayReal(dv));
                    inputdataR = (double[]) tempR.getFlatArray();
                    if (((GraphType) input).isDependentComplex(dv)) {
                        tempI = new FlatArray(((GraphType) input).getDataArrayImag(dv));
                        inputdataI = (double[]) tempI.getFlatArray();
                        for (j = 0; j < inputdataI.length; j++) {
                            e = inputdataR[j];
                            d = Math.log(e * e + inputdataI[j] * inputdataI[j]) * 0.5;
                            thisAngle = Math.atan2(inputdataI[j], inputdataR[j]);
                            inputdataI[j] = thisAngle;
                            inputdataR[j] = d;
                        }
                        ((GraphType) output).setDataArrayImag(tempI.restoreArray(false), dv);
                    } else {
                        inputdataI = new double[inputdataR.length];
                        FlatArray.initializeArray(inputdataI);
                        for (j = 0; j < inputdataR.length; j++) {
                            d = inputdataR[j];
                            if (d == 0) {
                                new ErrorDialog(null, "For input element " + String.valueOf(j) + " to unit " + getName()
                                        + "  is exactly zero. Logarithm set to negative infinity.");
                                inputdataR[j] = Double.NEGATIVE_INFINITY;
                            } else if (d < 0) {
                                makeComplex = true;
                                inputdataI[j] = Math.PI;
                                inputdataR[j] = Math.log(-d);
                            } else {
                                inputdataR[j] = Math.log(d);
                            }
                        }
                    }
                    ((GraphType) output).setDataArrayReal(tempR.restoreArray(false), dv);
                    if (makeComplex) {
                        if (output instanceof SampleSet) {
                            output = new ComplexSampleSet((SampleSet) output);
                        } else if (output instanceof Spectrum) {
                            output = new ComplexSpectrum((Spectrum) output);
                        }
                        setOutputType(output.getClass());
                        tempR.setFlatArray(inputdataI);
                        ((GraphType) output).setDataArrayImag(tempR.restoreArray(true), dv);
                    }
                }
            }
        } else if (input instanceof Const) {
            double r, i, d;
            r = ((Const) input).getReal();
            if (((Const) input).isComplex()) {
                i = ((Const) input).getImag();
                d = Math.log(r);
                i = Math.atan2(i, r);
                r = d;
                ((Const) output).setImag(i);
            } else {
                r = Math.log(r);
            }
            ((Const) output).setReal(r);

        }

        output(output);


    }


    /**
     * Initialses information specific to Ln.
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
     * Resets Ln
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves Ln's parameters to the parameter file.
     */
    public void saveParameters() {
    }

    /**
     * Loads Ln's parameters of from the parameter file.
     */
    public void setParameter(String name, String value) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to Ln, each separated by a white space.
     */
    public String inputTypes() {
        return "GraphType Const";
    }

    /**
     * @return a string containing the names of the types output from Ln, each separated by a white space.
     */
    public String outputTypes() {
        return "GraphType Const";
    }

    /**
     *
     * @returns the location of the help file for this unit.
     */
    public String getHelpFile() {
        return "Ln.html";
    }


    /**
     * @return Ln's parameter window sp that triana can move and display it.
     */
    public Window getParameterWindow() {
        return null;
    }


    /**
     * Captures the events thrown out by Ln.
     */
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);   // we need this

    }
}



















