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
 * A Sinh unit to apply the hyperbolic sine function to the elements of an input data array. The array can be real or
 * complex. The returned data type will be real or complex as appropriate.
 * <p/>
 * This OldUnit obeys the conventions of Triana Type 2 data types.
 *
 * @author Bernard Schutz
 * @version 2.1 13 January 2001
 */
public class Sinh extends OldUnit {

    /**
     * Offset parameters will be added to each element of the input array.
     */
    double offsetReal = 0.0;
    double offsetImag = 0.0;
    boolean complex, complexInput, complexOffset;

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Applies the sinh function to the elements of the input data.";
    }

    /**
     * ********************************************* ** USER CODE of Sinh goes here    ***
     * *********************************************
     */
    public void process() {

        Object input, output;

        output = null;
        input = getInputAtNode(0);
        if (input instanceof EmptyingType) {
            return;
        }
        complexOffset = (offsetImag != 0.0);
        if ((input instanceof SampleSet) && complexOffset) {
            output = new ComplexSampleSet((SampleSet) input);
        } else if ((input instanceof Spectrum) && complexOffset) {
            output = new ComplexSpectrum((Spectrum) input);
        } else {
            output = input;
        }
        Class outputClass = output.getClass();
        setOutputType(outputClass);

        if (input instanceof GraphType) {
            FlatArray tempR, tempI;
            int dv, j;
            double d, drecip, offsetCos, offsetSin;
            double[] inputdataR, inputdataI;
            for (dv = 0; dv < ((GraphType) input).getDependentVariables(); dv++) {
                if (((GraphType) input).isArithmeticArray(dv)) {
                    tempR = new FlatArray(((GraphType) input).getDataArrayReal(dv));
                    inputdataR = (double[]) tempR.getFlatArray();
                    complexInput = ((GraphType) input).isDependentComplex(dv);
                    complex = (complexOffset || complexInput);
                    if (complex) {
                        if (complexInput) {
                            tempI = new FlatArray(((GraphType) input).getDataArrayImag(dv));
                            inputdataI = (double[]) tempI.getFlatArray();
                            for (j = 0; j < inputdataI.length; j++) {
                                d = Math.exp(inputdataR[j] + offsetReal);
                                drecip = 1.0 / d;
                                inputdataR[j] = (d - drecip) * Math.cos(inputdataI[j] + offsetImag) * 0.5;
                                inputdataI[j] = (d + drecip) * Math.sin(inputdataI[j] + offsetImag) * 0.5;
                            }
                            ((GraphType) output).setDataArrayReal(tempR.restoreArray(false), dv);
                            ((GraphType) output).setDataArrayImag(tempI.restoreArray(false), dv);
                        } else {
                            offsetCos = Math.cos(offsetImag) * 0.5;
                            offsetSin = Math.sin(offsetImag) * 0.5;
                            inputdataI = new double[inputdataR.length];
                            for (j = 0; j < inputdataI.length; j++) {
                                d = Math.exp(inputdataR[j] + offsetReal);
                                drecip = 1.0 / d;
                                inputdataR[j] = (d - drecip) * offsetCos;
                                inputdataI[j] = (d + drecip) * offsetSin;
                            }
                            ((GraphType) output).setDataArrayReal(tempR.restoreArray(false), dv);
                            tempR.setFlatArray(inputdataI);
                            ((GraphType) output).setDataArrayImag(tempR.restoreArray(true), dv);
                        }
                    } else {
                        for (j = 0; j < inputdataR.length; j++) {
                            d = Math.exp(inputdataR[j] + offsetReal);
                            inputdataR[j] = (d - 1.0 / d) * 0.5;
                        }
                        ((GraphType) output).setDataArrayReal(tempR.restoreArray(false), dv);
                    }
                }
            }
        } else if (input instanceof Const) {
            double r, i, d;
            r = ((Const) input).getReal();
            complexInput = ((Const) input).isComplex();
            complex = (complexOffset || complexInput);
            if (complex) {
                if (complexInput) {
                    i = ((Const) input).getImag();
                    d = Math.exp(r + offsetReal);
                    r = (d - 1.0 / d) * Math.cos(i + offsetImag) * 0.5;
                    i = (d + 1.0 / d) * Math.sin(i + offsetImag) * 0.5;
                } else {
                    d = Math.exp(r + offsetReal);
                    r = (d - 1.0 / d) * Math.cos(offsetImag) * 0.5;
                    i = (d + 1.0 / d) * Math.sin(offsetImag) * 0.5;
                }
                ((Const) output).setImag(i);
                ((Const) output).setReal(r);
            } else {
                d = Math.exp(r + offsetReal);
                r = (d - 1.0 / d) * 0.5;
                ((Const) output).setReal(r);
            }
        }

        output(output);

    }


    /**
     * Initialses information specific to Sinh.
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
        addGUILine("Set real part of offset of argument $title offsetReal Scroller -1.0 1.0 0.0");
        addGUILine("Set imaginary part of offset of argument $title offsetImag Scroller -1.0 1.0 0.0");
    }


    /**
     * Resets Sinh
     */
    public void reset() {
        super.reset();
    }


    /**
     * Saves Sinh's parameters.
     */
    public void saveParameters() {
        saveParameter("offsetReal", offsetReal);
        saveParameter("offsetImag", offsetImag);
    }


    /**
     * Used to set each of Sinh's parameters.
     */
    public void setParameter(String name, String value) {
        updateGUIParameter(name, value);

        if (name.equals("offsetReal")) {
            offsetReal = strToDouble(value);
        }
        if (name.equals("offsetImag")) {
            offsetImag = strToDouble(value);
        }
    }


    /**
     * @return a string containing the names of the types allowed to be input to Sinh, each separated by a white space.
     */
    public String inputTypes() {
        return "GraphType Const";
    }

    /**
     * @return a string containing the names of the types output from Sinh, each separated by a white space.
     */
    public String outputTypes() {
        return "GraphType Const";
    }

    /**
     *
     * @returns the location of the help file for this unit.
     */
    public String getHelpFile() {
        return "Sinh.html";
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



















