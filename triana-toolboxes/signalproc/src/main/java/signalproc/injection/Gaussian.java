package signalproc.injection;

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


import java.util.Random;

import triana.types.Const;
import triana.types.EmptyingType;
import triana.types.GraphType;
import triana.types.OldUnit;
import triana.types.util.FlatArray;


/**
 * A Gaussian unit to add normally distributed noise to the elements of an input data array. The array can be real or
 * complex. The returned data type will be the same as the input. Parameters can be used to choose the standard
 * deviation and mean of the distribution.
 * <p/>
 * This OldUnit obeys the conventions of Triana Type 2 data types.
 *
 * @author Bernard Schutz
 * @version 2.0 20 June 2000
 */
public class Gaussian extends OldUnit {

    /**
     * Offset parameters will be added to each element of the input array.
     */
    double mean = 0.0;
    double stdDev = 1.0;
    boolean complexInput;
    double scale;

    /**
     * The Random generator class instance
     */
    Random generator;


    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Adds Gaussian noise to the elements of the input data.";
    }

    /**
     * ********************************************* ** USER CODE of Gaussian goes here    ***
     * *********************************************
     */
    public void process() {

        Object input, output;

        output = null;
        long time = System.currentTimeMillis();

        input = getInputAtNode(0);
        if (input instanceof EmptyingType) {
            return;
        }
        output = input;
        Class outputClass = output.getClass();
        setOutputType(outputClass);
        scale = stdDev;

        if (input instanceof GraphType) {
            FlatArray tempR, tempI;
            int dv, j;
            double[] inputdataR, inputdataI;
            for (dv = 0; dv < ((GraphType) input).getDependentVariables(); dv++) {
                if (((GraphType) input).isArithmeticArray(dv)) {
                    tempR = new FlatArray(((GraphType) input).getDataArrayReal(dv));
                    inputdataR = (double[]) tempR.getFlatArray();
                    complexInput = ((GraphType) input).isDependentComplex(dv);
                    if (complexInput) {
                        tempI = new FlatArray(((GraphType) input).getDataArrayImag(dv));
                        inputdataI = (double[]) tempI.getFlatArray();
                        for (j = 0; j < inputdataI.length; j++) {
                            inputdataR[j] += (generator.nextGaussian()) * scale + mean;
                            inputdataI[j] += (generator.nextGaussian()) * scale + mean;
                        }
                        ((GraphType) output).setDataArrayReal(tempR.restoreArray(false), dv);
                        ((GraphType) output).setDataArrayImag(tempI.restoreArray(false), dv);
                    } else {
                        for (j = 0; j < inputdataR.length; j++) {
                            inputdataR[j] += (generator.nextGaussian()) * scale + mean;
                        }
                        ((GraphType) output).setDataArrayReal(tempR.restoreArray(false), dv);
                    }
                }
            }
        } else if (input instanceof Const) {
            double r, i;
            r = ((Const) input).getReal();
            complexInput = ((Const) input).isComplex();
            if (complexInput) {
                i = ((Const) input).getImag();
                i += (generator.nextGaussian()) * scale + mean;
                ((Const) output).setImag(i);
            }
            r += (generator.nextGaussian()) * scale + mean;
            ((Const) output).setReal(r);
        }

        output(output);
    }


    /**
     * Initialses information specific to Gaussian.
     */
    public void init() {
        super.init();
        setUseGUIBuilder(true);

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(1);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        setResizableInputs(false);
        setResizableOutputs(true);


        generator = new Random();

    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format.
     */
    public void setGUIInformation() {
        addGUILine("Set mean of normal distribution $title mean Scroller -1.0 1.0 0.0");
        addGUILine("Set standard deviation of normal distribution $title stdDev Scroller 1.0 10.0 1.0");
    }


    /**
     * Resets Gaussian
     */
    public void reset() {
        super.reset();
    }


    /**
     * Saves Gaussian's parameters.
     */
    public void saveParameters() {
        saveParameter("mean", mean);
        saveParameter("stdDev", stdDev);
    }


    /**
     * Used to set each of Gaussian's parameters.
     */
    public void setParameter(String name, String value) {
        updateGUIParameter(name, value);

        if (name.equals("mean")) {
            mean = strToDouble(value);
        }
        if (name.equals("stdDev")) {
            stdDev = strToDouble(value);
        }
    }


    /**
     * @return a string containing the names of the types allowed to be input to Gaussian, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "GraphType Const";
    }

    /**
     * @return a string containing the names of the types output from Gaussian, each separated by a white space.
     */
    public String outputTypes() {
        return "GraphType Const";
    }

    /**
     *
     * @returns the location of the help file for this unit.
     */
    public String getHelpFile() {
        return "Gaussian.html";
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



















