package common.processing;

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


import org.trianacode.gui.windows.ErrorDialog;
import triana.types.GraphType;
import triana.types.OldUnit;

/**
 * A ReplaceData unit to replace the dependent data of the first input GraphType data type with the corresponding data
 * of the second GraphType input. This enables users to manufacture new data and encapsulate it into an existing data
 * type, using the old type's other parameter values. The unit checks that the two inputs have the same number of
 * independent dimensions and dependent variables, and that in each independent dimension the sizes of the arrays are
 * the same. If they are not, then the output is the same as the first input, with no substitution.
 *
 * @author B F Schutz
 * @version 1.0 10 Jun 2001
 */
public class ReplaceData extends OldUnit {

    /**
     * ********************************************* ** USER CODE of ReplaceData goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        GraphType template = (GraphType) getInputNode(0);
        GraphType dataCarrier = (GraphType) getInputNode(1);
        GraphType output = template;
        if (template.getIndependentVariables() != dataCarrier.getIndependentVariables()) {
            new ErrorDialog(null, getName() + " -- input data sets have different number of independent dimensions,\n"
                    + String.valueOf(template.getIndependentVariables()) + " and "
                    + String.valueOf(dataCarrier.getIndependentVariables()) + ". No substitution made.");
        } else if (template.getDependentVariables() != dataCarrier.getDependentVariables()) {
            new ErrorDialog(null, getName() + " -- input data sets have different number of dependent variables,\n"
                    + String.valueOf(template.getDependentVariables()) + " and "
                    + String.valueOf(dataCarrier.getDependentVariables()) + ". No substitution made.");
        } else {
            int[] templateDims = template.getDimensionLengths();
            int[] dataDims = dataCarrier.getDimensionLengths();
            boolean passed = true;
            for (int k = 0; k < templateDims.length; k++) {
                if (templateDims[k] != dataDims[k]) {
                    new ErrorDialog(null,
                            getName() + " -- input data sets have different sizes. \nFor independent dimension "
                                    + String.valueOf(k) + " the lengths are " + String.valueOf(templateDims[k])
                                    + " and " + String.valueOf(dataDims[k]) + ". \nNo substitution made.");
                    passed = false;
                    break;
                }
            }
            if (passed) {
                for (int dv = 0; dv < template.getDependentVariables(); dv++) {
                    output.setDataArrayReal(dataCarrier.getDataArrayReal(dv), dv);
                }
            }
        }

        output(output);

    }


    /**
     * Initialses information specific to ReplaceData.
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
     * Saves ReplaceData's parameters.
     */
    public void saveParameters() {
    }

    /**
     * Used to set each of ReplaceData's parameters. This should NOT be used to update this unit's user interface
     */
    public void setParameter(String name, String value) {
    }

    /**
     * Used to update the widget in this unit's user interface that is used to control the given parameter name.
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to ReplaceData, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "GraphType";
    }

    /**
     * @return a string containing the names of the types output from ReplaceData, each separated by a white space.
     */
    public String outputTypes() {
        return "GraphType";
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Replace the data arrays of the first input with those of the second.";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "ReplaceData.html";
    }
}



