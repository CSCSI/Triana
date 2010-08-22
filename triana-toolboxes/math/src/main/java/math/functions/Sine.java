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
 * A Sine unit to apply the sine function to the elements of an input data array. The array can be real or complex. The
 * returned data type will be real or complex as appropriate.
 * <p/>
 * This OldUnit obeys the conventions of Triana Type 2 data types.
 *
 * @author Bernard Schutz
 * @version 2.1 13 January 2001
 */
public class Sine extends OldUnit {

    /**
     * Offset parameters will be added to each element of the input array.
     */
    double normPhaseReal = 0.0;
    double normPhaseImag = 0.0;
    double phaseReal, phaseImag;
    boolean complex, complexInput, complexPhase;

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Applies the sine function to the elements of the input data.";
    }

    /**
     * ********************************************* ** USER CODE of Sine goes here    ***
     * *********************************************
     */
    public void process() {

        Object input, output;

        output = null;
        input = getInputAtNode(0);
        if (input instanceof EmptyingType) {
            return;
        }

        phaseReal = Math.PI * normPhaseReal;
        phaseImag = Math.PI * normPhaseImag;
        complexPhase = (phaseImag != 0.0);

        if ((input instanceof SampleSet) && complexPhase) {
            output = new ComplexSampleSet((SampleSet) input);
        } else if ((input instanceof Spectrum) && complexPhase) {
            output = new ComplexSpectrum((Spectrum) input);
        } else {
            output = input;
        }
        Class outputClass = output.getClass();
        setOutputType(outputClass);


        if (input instanceof GraphType) {
            FlatArray tempR, tempI;
            int dv, j;
            double d, drecip, f, offsetCosh, offsetSinh;
            double[] inputdataR, inputdataI;
            for (dv = 0; dv < ((GraphType) input).getDependentVariables(); dv++) {
                if (((GraphType) input).isArithmeticArray(dv)) {
                    tempR = new FlatArray(((GraphType) input).getDataArrayReal(dv));
                    inputdataR = (double[]) tempR.getFlatArray();
                    complexInput = ((GraphType) input).isDependentComplex(dv);
                    complex = (complexPhase || complexInput);
                    if (complex) {
                        if (complexInput) {
                            tempI = new FlatArray(((GraphType) input).getDataArrayImag(dv));
                            inputdataI = (double[]) tempI.getFlatArray();
                            for (j = 0; j < inputdataI.length; j++) {
                                d = Math.exp(inputdataI[j] + phaseImag);
                                drecip = 1.0 / d;
                                f = inputdataR[j] + phaseReal;
                                inputdataR[j] = (d + drecip) * Math.sin(f) * 0.5;
                                inputdataI[j] = (d - drecip) * Math.cos(f) * 0.5;
                            }
                            ((GraphType) output).setDataArrayReal(tempR.restoreArray(false), dv);
                            ((GraphType) output).setDataArrayImag(tempI.restoreArray(false), dv);
                        } else {
                            d = Math.exp(phaseImag);
                            drecip = 1.0 / d;
                            offsetCosh = (d + drecip) * 0.5;
                            offsetSinh = (d - drecip) * 0.5;
                            inputdataI = new double[inputdataR.length];
                            for (j = 0; j < inputdataI.length; j++) {
                                f = inputdataR[j] + phaseReal;
                                inputdataR[j] = offsetCosh * Math.sin(f);
                                inputdataI[j] = offsetSinh * Math.cos(f);
                            }
                            ((GraphType) output).setDataArrayReal(tempR.restoreArray(false), dv);
                            tempR.setFlatArray(inputdataI);
                            ((GraphType) output).setDataArrayImag(tempR.restoreArray(true), dv);
                        }
                    } else {
                        for (j = 0; j < inputdataR.length; j++) {
                            inputdataR[j] = Math.sin(inputdataR[j] + phaseReal);
                        }
                        ((GraphType) output).setDataArrayReal(tempR.restoreArray(false), dv);
                    }
                }
            }
        } else if (input instanceof Const) {
            double r, i, d, f, drecip;
            complexInput = ((Const) input).isComplex();
            complex = (complexPhase || complexInput);
            if (complex) {
                if (complexInput) {
                    d = Math.exp(((Const) input).getImag() + phaseImag);
                    drecip = 1.0 / d;
                    f = ((Const) input).getReal() + phaseReal;
                    r = (d + drecip) * Math.sin(f) * 0.5;
                    i = (d - drecip) * Math.cos(f) * 0.5;
                } else {
                    d = Math.exp(phaseImag);
                    drecip = 1.0 / d;
                    f = ((Const) input).getReal() + phaseReal;
                    r = (d + drecip) * Math.sin(f) * 0.5;
                    i = (d - drecip) * Math.cos(f) * 0.5;
                }
                ((Const) output).setImag(i);
                ((Const) output).setReal(r);
            } else {
                ((Const) output).setReal(Math.sin(((Const) input).getReal() + phaseReal));
            }
        }

        output(output);

    }


    /**
     * Initialses information specific to Sine.
     */
    public void init() {
        super.init();

        setUseGUIBuilder(true);

        setResizableInputs(false);
        setResizableOutputs(true);
        // This is to ensure that we receive arrays containing double-precision numbers
        setRequireDoubleInputs(true);
        setCanProcessDoubleArrays(true);

    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format.
     */
    public void setGUIInformation() {
        addGUILine(
                "Set real part of offset of argument (as a multiple of Pi) $title normPhaseReal Scroller -2.0 2.0 0.0");
        addGUILine(
                "Set imaginary part of offset of argument (as a multiple of Pi) $title normPhaseImag Scroller -2.0 2.0 0.0");
    }


    /**
     * Resets Sine
     */
    public void reset() {
        super.reset();
    }


    /**
     * Saves Sine's parameters.
     */
    public void saveParameters() {
        saveParameter("normPhaseReal", normPhaseReal);
        saveParameter("normPhaseImag", normPhaseImag);
    }


    /**
     * Used to set each of Sine's parameters.
     */
    public void setParameter(String name, String value) {
        updateGUIParameter(name, value);

        if (name.equals("normPhaseReal")) {
            normPhaseReal = strToDouble(value);
        }
        if (name.equals("normPhaseImag")) {
            normPhaseImag = strToDouble(value);
        }
    }


    /**
     * @return a string containing the names of the types allowed to be input to Sine, each separated by a white space.
     */
    public String inputTypes() {
        return "GraphType Const";
    }

    /**
     * @return a string containing the names of the types output from Sine, each separated by a white space.
     */
    public String outputTypes() {
        return "GraphType Const";
    }

    /**
     *
     * @returns the location of the help file for this unit.
     */
    public String getHelpFile() {
        return "Sine.html";
    }


    /**
     * Called when the stop button is pressed within the MainTriana Window
     */
    public void stopping() {
        super.stopping();
    }

    /**
     * Called when the start button is pressed within the MainTriana Window
     */
    public void starting() {
        super.starting();
    }


}



















