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
 * A Power unit to compute the result of raising the elements of an input data array to an arbitrary complex power. The
 * array can be real or complex. The returned data will be real or complex as appropriate.
 * <p/>
 * This OldUnit obeys the conventions of Triana Type 2 data types.
 *
 * @author Bernard Schutz
 * @version 2.1 13 January 2001
 */
public class Power extends OldUnit {

    /**
     * The power set in the parameter window is held here.
     */
    double powReal = 0.0;
    double powImag = 0.0;
    boolean complex, complexInput, complexPower;

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Raises the elements of the input data set to a power";
    }

    /**
     * ********************************************* ** USER CODE of Power goes here    ***
     * *********************************************
     */
    public void process() {


        Object input, output;
        double TwoPi = Math.PI * 2.0;

        output = null;
        input = getInputAtNode(0);
        if (input instanceof EmptyingType) {
            return;
        }
        complexPower = (powImag != 0.0);
        if ((input instanceof SampleSet) && complexPower) {
            output = new ComplexSampleSet((SampleSet) input);
        } else if ((input instanceof SampleSet) && (FlatArray.minArray(((SampleSet) input).getData()) < 0.0)) {
            output = new ComplexSampleSet((SampleSet) input);
        } else if ((input instanceof Spectrum) && complexPower) {
            output = new ComplexSpectrum((Spectrum) input);
        } else if ((input instanceof Spectrum) && (FlatArray.minArray(((Spectrum) input).getData()) < 0.0)) {
            output = new ComplexSpectrum((Spectrum) input);
        } else {
            output = input;
        }
        Class outputClass = output.getClass();
        setOutputType(outputClass);


        if ((powReal != 1.0) || (powImag != 0.0)) {
            if (input instanceof GraphType) {
                FlatArray tempR, tempI;
                int dv, j;
                double mag, phase, magout, phaseout;
                double[] inputdataR, inputdataI;
                for (dv = 0; dv < ((GraphType) input).getDependentVariables(); dv++) {
                    if (((GraphType) input).isArithmeticArray(dv)) {
                        tempR = new FlatArray(((GraphType) input).getDataArrayReal(dv));
                        inputdataR = (double[]) tempR.getFlatArray();
                        complexInput = ((GraphType) input).isDependentComplex(dv);
                        complex = (complexPower || complexInput);
                        if (complex) {
                            if (complexInput) {
                                tempI = new FlatArray(((GraphType) input).getDataArrayImag(dv));
                                inputdataI = (double[]) tempI.getFlatArray();
                                for (j = 0; j < inputdataI.length; j++) {
                                    mag = Math.sqrt(inputdataR[j] * inputdataR[j] + inputdataI[j] * inputdataI[j]);
                                    if (mag > 0.0) {
                                        phase = Math.atan2(inputdataI[j], inputdataR[j]);
                                        magout = Math.pow(mag, powReal) * Math.exp(-phase * powImag);
                                        phaseout = phase * powReal + Math.log(mag) * powImag;
                                        inputdataR[j] = magout * Math.cos(phaseout);
                                        inputdataI[j] = magout * Math.sin(phaseout);
                                    } else {
                                        if (powReal < 0.0) {
                                            inputdataR[j] = Double.NaN;
                                            inputdataI[j] = Double.NaN;
                                        } else {
                                            inputdataR[j] = 0.0;
                                            inputdataI[j] = 0.0;
                                        }
                                    }
                                }
                                ((GraphType) output).setDataArrayReal(tempR.restoreArray(false), dv);
                                ((GraphType) output).setDataArrayImag(tempI.restoreArray(false), dv);
                            } else {
                                inputdataI = new double[inputdataR.length];
                                for (j = 0; j < inputdataI.length; j++) {
                                    mag = Math.abs(inputdataR[j]);
                                    if (mag > 0.0) {
                                        phase = (inputdataR[j] < 0) ? Math.PI : 0.0;
                                        magout = Math.pow(mag, powReal) * Math.exp(-phase * powImag);
                                        phaseout = phase * powReal + Math.log(mag) * powImag;
                                        inputdataR[j] = magout * Math.cos(phaseout);
                                        inputdataI[j] = magout * Math.sin(phaseout);
                                    } else {
                                        if (powReal < 0.0) {
                                            inputdataR[j] = Double.NaN;
                                            inputdataI[j] = Double.NaN;
                                        } else {
                                            inputdataR[j] = 0.0;
                                            inputdataI[j] = 0.0;
                                        }
                                    }
                                }
                                ((GraphType) output).setDataArrayReal(tempR.restoreArray(false), dv);
                                tempR.setFlatArray(inputdataI);
                                ((GraphType) output).setDataArrayImag(tempR.restoreArray(true), dv);
                            }
                        } else if (FlatArray.minArray(inputdataR) < 0.0) {
                            inputdataI = new double[inputdataR.length];
                            for (j = 0; j < inputdataR.length; j++) {
                                if (inputdataR[j] < 0.0) {
                                    magout = Math.pow(Math.abs(inputdataR[j]), powReal);
                                    phaseout = Math.PI * powReal;
                                    inputdataR[j] = magout * Math.cos(phaseout);
                                    inputdataI[j] = magout * Math.sin(phaseout);
                                } else if (inputdataR[j] == 0.0) {
                                    if (powReal <= 0.0) {
                                        inputdataR[j] = Double.NaN;
                                        inputdataI[j] = 0.0;
                                    } else {
                                        inputdataR[j] = 0.0;
                                        inputdataI[j] = 0.0;
                                    }
                                } else {
                                    inputdataR[j] = Math.pow(Math.abs(inputdataR[j]), powReal);
                                    inputdataI[j] = 0.0;
                                }
                            }
                            ((GraphType) output).setDataArrayReal(tempR.restoreArray(false), dv);
                            tempR.setFlatArray(inputdataI);
                            ((GraphType) output).setDataArrayImag(tempR.restoreArray(true), dv);
                        } else {
                            for (j = 0; j < inputdataR.length; j++) {
                                inputdataR[j] = Math.pow(inputdataR[j], powReal);
                            }
                            ((GraphType) output).setDataArrayReal(tempR.restoreArray(false), dv);
                        }
                    }
                }
            } else if (input instanceof Const) {
                double r, i, mag, phase, magout, phaseout;
                r = ((Const) input).getReal();
                complexInput = (((Const) input).isComplex());
                complex = (complexPower || complexInput);
                if (complex) {
                    if (complexInput) {
                        i = ((Const) input).getImag();
                        mag = Math.sqrt(r * r + i * i);
                        if (mag > 0.0) {
                            phase = Math.atan2(i, r);
                            magout = Math.pow(mag, powReal) * Math.exp(-phase * powImag);
                            phaseout = phase * powReal + Math.log(mag) * powImag;
                            r = mag * Math.cos(phase);
                            i = mag * Math.sin(phase);
                        } else {
                            if (powReal <= 0.0) {
                                r = Double.NaN;
                                i = Double.NaN;
                            } else {
                                r = 0.0;
                                i = 0.0;
                            }
                        }
                        ((Const) output).setReal(r);
                        ((Const) output).setImag(i);
                    } else {
                        if (r != 0.0) {
                            mag = Math.abs(r);
                            phase = (r < 0) ? Math.PI : 0.0;
                            magout = Math.pow(mag, powReal) * Math.exp(-phase * powImag);
                            phaseout = phase * powReal + Math.log(mag) * powImag;
                            r = magout * Math.cos(phaseout);
                            i = magout * Math.sin(phaseout);
                        } else {
                            if (powReal <= 0.0) {
                                r = Double.NaN;
                                i = 0.0;
                            } else {
                                r = 0.0;
                                i = 0.0;
                            }
                        }
                        ((Const) output).setReal(r);
                        ((Const) output).setImag(i);
                    }
                } else if (r < 0) {
                    magout = Math.pow(Math.abs(r), powReal);
                    phaseout = Math.PI * powReal;
                    r = magout * Math.cos(phaseout);
                    i = magout * Math.sin(phaseout);
                    ((Const) output).setReal(r);
                    ((Const) output).setImag(i);
                } else if (r == 0.0) {
                    if (powReal <= 0.0) {
                        r = Double.NaN;
                    } else {
                        r = 0.0;
                    }
                    ((Const) output).setReal(r);
                } else {
                    ((Const) output).setReal(Math.pow(r, powReal));
                }
            }
        }

        output(output);


    }


    /**
     * Initialses information specific to Power.
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
        addGUILine("Give real part of the exponent $title powReal Scroller -10.0 10.0 1.0");
        addGUILine("Give imaginary part of the exponent $title powImag Scroller -10.0 10.0 0.0");
    }


    /**
     * Resets Power
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves Power's parameters to the parameter file.
     */
    public void saveParameters() {
        saveParameter("powReal", powReal);
        saveParameter("powImag", powImag);
    }

    /**
     * Used to set each of Power's parameters.
     */
    public void setParameter(String name, String value) {
        updateGUIParameter(name, value);

        if (name.equals("powReal")) {
            powReal = strToDouble(value);
        }
        if (name.equals("powImag")) {
            powImag = strToDouble(value);
        }
    }


    /**
     * @return a string containing the names of the types allowed to be input to Power, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "GraphType Const";
    }

    /**
     * @return a string containing the names of the types output from Power, each separated by a white space.
     */
    public String outputTypes() {
        return "GraphType Const";
    }

    /**
     *
     * @returns the location of the help file for this unit.
     */
    public String getHelpFile() {
        return "Power.html";
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



















