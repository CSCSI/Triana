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
 * A Scaler unit to rescale (by a given complex number) the elements of an input data array. The array can be real or
 * complex. The returned data type will be real or complex as appropriate.
 * <p/>
 * This OldUnit obeys the conventions of Triana Type 2 data types.
 *
 * @author Bernard Schutz
 * @version 2.1 13 January 2001
 */
public class Scaler extends OldUnit {

    /**
     * Offset parameters will be added to each element of the input array.
     */
    double scaleReal = 0.0;
    double scaleImag = 0.0;
    boolean complex, complexInput, complexScale;

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Multiplies a scale constant into the elements of the input data.";
    }

    /**
     * ********************************************* ** USER CODE of Scaler goes here    ***
     * *********************************************
     */
    public void process() {

        Object input, output;

        output = null;
        input = getInputAtNode(0);
        if (input instanceof EmptyingType) {
            return;
        }
        complexScale = (scaleImag != 0.0);
        if ((input instanceof SampleSet) && complexScale) {
            output = new ComplexSampleSet((SampleSet) input);
        } else if ((input instanceof Spectrum) && complexScale) {
            output = new ComplexSpectrum((Spectrum) input);
        } else {
            output = input;
        }
        Class outputClass = output.getClass();
        setOutputType(outputClass);

        if ((scaleImag != 0.0) || (scaleReal != 1.0)) {

            if (input instanceof GraphType) {
                int dv, j;
                double d, drecip, offsetCos, offsetSin;
                double[] inputdataR, inputdataI;
                for (dv = 0; dv < ((GraphType) input).getDependentVariables(); dv++) {
                    if (((GraphType) input).isArithmeticArray(dv)) {
                        complexInput = ((GraphType) input).isDependentComplex(dv);
                        if (complexInput) {
                            FlatArray.scaleArray(((GraphType) output).getDataArrayReal(dv),
                                    ((GraphType) output).getDataArrayImag(dv), scaleReal, scaleImag);
                        } else {
                            FlatArray.scaleArray(((GraphType) output).getDataArrayReal(dv), null, scaleReal, scaleImag);
                        }
                    }
                }
            } else if (input instanceof Const) {
                double r, i, d;
                r = ((Const) input).getReal();
                complexInput = ((Const) input).isComplex();
                complex = (complexScale || complexInput);
                if (complex) {
                    if (complexInput) {
                        i = ((Const) input).getImag();
                        d = r;
                        r = d * scaleReal - i * scaleImag;
                        i = i * scaleReal + d * scaleImag;
                    } else {
                        d = r;
                        r = d * scaleReal;
                        i = d * scaleImag;
                    }
                    ((Const) output).setImag(i);
                    ((Const) output).setReal(r);
                } else {
                    ((Const) output).setReal(r * scaleReal);
                }
            }

        }

        output(output);

    }


    /**
     * Initialses information specific to Scaler.
     */
    public void init() {
        super.init();

        setUseGUIBuilder(true);

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(1);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(1);
        setResizableInputs(false);
        setResizableOutputs(true);

    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format.
     */
    public void setGUIInformation() {
        addGUILine("Set real part of scaling multiplier $title scaleReal Scroller -100.0 100.0 0.0");
        addGUILine("Set imaginary part of scaling multiplier $title scaleImag Scroller -100.0 100.0 0.0");
    }


    /**
     * Resets Scaler
     */
    public void reset() {
        super.reset();
    }


    /**
     * Saves Scaler's parameters.
     */
    public void saveParameters() {
        saveParameter("scaleReal", scaleReal);
        saveParameter("scaleImag", scaleImag);
    }


    /**
     * Used to set each of Scaler's parameters.
     */
    public void setParameter(String name, String value) {
        updateGUIParameter(name, value);

        if (name.equals("scaleReal")) {
            scaleReal = strToDouble(value);
        }
        if (name.equals("scaleImag")) {
            scaleImag = strToDouble(value);
        }
    }


    /**
     * @return a string containing the names of the types allowed to be input to Scaler, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "GraphType Const";
    }

    /**
     * @return a string containing the names of the types output from Scaler, each separated by a white space.
     */
    public String outputTypes() {
        return "GraphType Const";
    }

    /**
     *
     * @returns the location of the help file for this unit.
     */
    public String getHelpFile() {
        return "Scaler.html";
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



















