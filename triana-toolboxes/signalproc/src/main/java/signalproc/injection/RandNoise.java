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


import triana.types.Const;
import triana.types.EmptyingType;
import triana.types.GraphType;
import triana.types.OldUnit;
import triana.types.util.FlatArray;


/**
 * A RandNoise unit to add uniformly distributed noise to the elements of an input data array. The array can be real or
 * complex. The returned data type will be the same as the input. Parameters can be used to set the lower and upper
 * bounds on the distribution.
 * <p/>
 * This OldUnit obeys the conventions of Triana Type 2 data types.
 *
 * @author Bernard Schutz
 * @version 2.0 20 June 2000
 */
public class RandNoise extends OldUnit {

    /**
     * Offset parameters will be added to each element of the input array.
     */
    double lowerBound = 0.0;
    double upperBound = 1.0;
    boolean complexInput;
    double scale;

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Adds uniform noise to the elements of the input data.";
    }

    /**
     * ********************************************* ** USER CODE of RandNoise goes here    ***
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
        scale = upperBound - lowerBound;

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
                            inputdataR[j] += Math.random() * scale + lowerBound;
                            inputdataI[j] += Math.random() * scale + lowerBound;
                        }
                        ((GraphType) output).setDataArrayReal(tempR.restoreArray(false), dv);
                        ((GraphType) output).setDataArrayImag(tempI.restoreArray(false), dv);
                    } else {
                        for (j = 0; j < inputdataR.length; j++) {
                            inputdataR[j] += Math.random() * scale + lowerBound;
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
                i += Math.random() * scale + lowerBound;
                ((Const) output).setImag(i);
            }
            r += Math.random() * scale + lowerBound;
            ((Const) output).setReal(r);
        }

        output(output);

    }


    /**
     * Initialses information specific to RandNoise.
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

    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format.
     */
    public void setGUIInformation() {
        addGUILine("Set lower bound of uniform distribution $title lowerBound Scroller -1.0 1.0 0.0");
        addGUILine("Set upper bound of uniform distribution $title upperBound Scroller 1.0 10.0 1.0");
    }


    /**
     * Resets RandNoise
     */
    public void reset() {
        super.reset();
    }


    /**
     * Saves RandNoise's parameters.
     */
    public void saveParameters() {
        saveParameter("lowerBound", lowerBound);
        saveParameter("upperBound", upperBound);
    }


    /**
     * Used to set each of RandNoise's parameters.
     */
    public void setParameter(String name, String value) {
        updateGUIParameter(name, value);

        if (name.equals("lowerBound")) {
            lowerBound = strToDouble(value);
        }
        if (name.equals("upperBound")) {
            upperBound = strToDouble(value);
        }
    }


    /**
     * @return a string containing the names of the types allowed to be input to RandNoise, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "GraphType Const";
    }

    /**
     * @return a string containing the names of the types output from RandNoise, each separated by a white space.
     */
    public String outputTypes() {
        return "GraphType Const";
    }

    /**
     *
     * @returns the location of the help file for this unit.
     */
    public String getHelpFile() {
        return "RandNoise.html";
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



















