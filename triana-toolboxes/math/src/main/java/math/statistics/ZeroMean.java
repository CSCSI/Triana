package math.statistics;

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


import triana.types.GraphType;
import triana.types.OldUnit;
import triana.types.util.FlatArray;

/**
 * A ZeroMean unit to subtract the mean from all the data sets in a GraphType.
 *
 * @author B F Schutz
 * @version 1.0 28 Feb 2001
 */
public class ZeroMean extends OldUnit {

    /**
     * ********************************************* ** USER CODE of ZeroMean goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        GraphType input = (GraphType) getInputAtNode(0);

        FlatArray flat;
        double mean;
        double[] data;
        int dv, len, k;

        for (dv = 0; dv < input.getDependentVariables(); dv++) {
            if (input.isArithmeticArray(dv)) {
                flat = new FlatArray(input.getDataArrayReal(dv));
                mean = 0;
                data = (double[]) flat.getFlatArray();
                len = data.length;
                for (k = 0; k < len; k++) {
                    mean += data[k];
                }
                mean /= len;
                for (k = 0; k < len; k++) {
                    data[k] -= mean;
                }
                flat.restoreArray();
                if (input.isDependentComplex(dv)) {
                    flat = new FlatArray(input.getDataArrayImag(dv));
                    mean = 0;
                    data = (double[]) flat.getFlatArray();
                    len = data.length;
                    for (k = 0; k < len; k++) {
                        mean += data[k];
                    }
                    mean /= len;
                    for (k = 0; k < len; k++) {
                        data[k] -= mean;
                    }
                    flat.restoreArray();
                }
            }
        }

    }


    /**
     * Initialses information specific to ZeroMean.
     */
    public void init() {
        super.init();

        // set these to true if your unit can process double-precision
        // arrays
        setRequireDoubleInputs(false);
        setCanProcessDoubleArrays(false);

        setResizableInputs(false);
        setResizableOutputs(true);
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
     * Saves ZeroMean's parameters.
     */
    public void saveParameters() {
    }

    /**
     * Used to set each of ZeroMean's parameters. This should NOT be used to update this unit's user interface
     */
    public void setParameter(String name, String value) {
    }

    /**
     * Used to update the widget in this unit's user interface that is used to control the given parameter name.
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to ZeroMean, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "GraphType";
    }

    /**
     * @return a string containing the names of the types output from ZeroMean, each separated by a white space.
     */
    public String outputTypes() {
        return "GraphType";
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Subtracts the mean of each data set from each element of the set";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "ZeroMean.html";
    }
}



