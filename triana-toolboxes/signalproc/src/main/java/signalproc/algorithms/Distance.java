package signalproc.algorithms;

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


import triana.types.OldUnit;
import triana.types.VectorType;


/**
 * A Distance unit to ..
 *
 * @author ian
 * @version 2.4 01 Jun 2001
 */
public class Distance extends OldUnit {

    String distanceMeasure = "Correlation";
    boolean normalize = true;
    int spacing = 1;


    /**
     * ********************************************* ** USER CODE of Distance goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        VectorType input = (VectorType) getInputAtNode(0);
        VectorType input1 = (VectorType) getInputAtNode(1);

        VectorType dataset;
        VectorType toCompare;

        if (input1.size() > input.size()) {
            dataset = input1;
            toCompare = input;
        } else {
            dataset = input;
            toCompare = input1;
        }

        int outputDataSize = dataset.size() / spacing;
        double[] dataOut = new double[outputDataSize];

        double set[] = dataset.getDataReal();
        double comp[] = toCompare.getDataReal();

        double setToComp[] = new double[comp.length];

        if (normalize) // normalize the data set to compare
        {
            normalize(comp);
        }

        int j;

        for (int i = 0; i < outputDataSize; ++i) {
            for (j = 0; j < comp.length; ++j) {
                if (((i * spacing) + j) >= set.length) {
                    setToComp[j] = 0.0;
                } else {
                    setToComp[j] = set[(i * spacing) + j];
                }
            }

            dataOut[i] = compare(comp, setToComp);
        }

        VectorType output = new VectorType(dataOut);

        output(output);
    }

    public double compare(double[] comp, double[] set) {
        if (normalize) // normalize each piece of the set
        {
            normalize(set);
        }

        double dist = 0.0;

        if (distanceMeasure.equals("Correlation")) {
            for (int i = 0; i < comp.length; ++i) {
                dist += (comp[i] * set[i]);
            }
        } else { // Euclidean Distance
            for (int i = 0; i < comp.length; ++i) {
                dist += Math.pow((comp[i] - set[i]), 2.0);
            }
            dist = Math.sqrt(dist);
        }
        return dist;
    }


    public double vectlength(double[] arr) {
        int i;
        double accum;

        accum = 0.0;
        for (i = 0; i < arr.length; ++i) {
            accum = accum + Math.pow(arr[i], 2.0);
        }
        return (Math.sqrt(accum));
    }

    public void normalize(double[] arr) { // normalizes a vector
        int i;
        double length;

        double[] arr1 = arr;

        length = vectlength(arr);

        if (length > 0)                  /* Watch out division by 0 */ {
            for (i = 0; i < arr.length; ++i) {
                arr1[i] = (arr[i] / length);
            }
        } else {
            for (i = 0; i < arr.length; ++i) {
                arr1[i] = 0;
            }
        }
    }

    /**
     * Initialses information specific to Distance.
     */
    public void init() {
        super.init();

        setUseGUIBuilder(true);

        setRequireDoubleInputs(false);
        setCanProcessDoubleArrays(false);

        setResizableInputs(false);
        setResizableOutputs(true);
    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format.
     */
    public void setGUIInformation() {
        addGUILine("Distance Measure : $title distanceMeasure Choice Correlation Euclidean");
        addGUILine("Normalize to Unity ? $title normalize Checkbox true");
        addGUILine("Spacing between comparisions $title spacing IntScroller 0 100 1");
    }

    /**
     * Called when the reset button is pressed within the MainTriana Window
     */
    public void reset() {
        super.reset();
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

    /**
     * Saves Distance's parameters.
     */
    public void saveParameters() {
        saveParameter("distanceMeasure", distanceMeasure);
        saveParameter("normalize", normalize);
        saveParameter("spacing", spacing);
    }


    /**
     * Used to set each of Distance's parameters.
     */
    public void setParameter(String name, String value) {
        updateGUIParameter(name, value);

        if (name.equals("distanceMeasure")) {
            distanceMeasure = value;
        }
        if (name.equals("normalize")) {
            normalize = strToBoolean(value);
        }
        if (name.equals("spacing")) {
            spacing = strToInt(value);
        }
    }

    /**
     * Don't need to use this for GUI Builder units as everthing is updated by triana automatically
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to Distance, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "VectorType";
    }

    /**
     * @return a string containing the names of the types output from Distance, each separated by a white space.
     */
    public String outputTypes() {
        return "VectorType";
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Measures Euclidean distance or Correlation of N-D Vectors within a dataset";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "Distance.html";
    }
}
