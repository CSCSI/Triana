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
package signalproc.algorithms;

import org.trianacode.gui.windows.ErrorDialog;
import org.trianacode.taskgraph.Unit;
import triana.types.Curve;
import triana.types.VectorType;
import triana.types.util.FlatArray;

/**
 * A MakeCurve unit to convert a complex VectorType input or two/three real VectorType inputs into a Curve, which
 * displays the data as a parametrized curve in 2 dimensions. If there is only one input and it is real, the y-values of
 * the output are zeroed and a warning is given. If there are more than 3 inputs the extra ones are ignored.
 *
 * @author B F Schutz
 * @version $Revision: 2921 $
 */

public class MakeCurve extends Unit {

    /**
     * ********************************************* ** USER CODE of MakeCurve goes here    ***
     * *********************************************
     */
    public void process() throws Exception {

        VectorType input, input2, input3;
        Curve output = new Curve();
        int numInputs = getInputNodeCount();

        input = (VectorType) getInputAtNode(0);
        if (numInputs >= 3) {
            input2 = (VectorType) getInputAtNode(1);
            input3 = (VectorType) getInputAtNode(2);
            if (input.size() == input2.size() && input.size() == input3.size()) {
                output = new Curve(input.getDataReal(), input2.getDataReal(), input3.getDataReal());
                output.setIndependentLabels(0, input.getDependentLabels(0));
                output.setDependentLabels(0, input2.getDependentLabels(0));
                output.setDependentLabels(1, input3.getDependentLabels(0));
            }
        } else if (numInputs == 2) {
            input2 = (VectorType) getInputAtNode(1);
            if (input.size() == input2.size()) {
                output = new Curve(input.getDataReal(), input2.getDataReal());
                output.setIndependentLabels(0, input.getDependentLabels(0));
                output.setDependentLabels(0, input2.getDependentLabels(0));
            }
        } else {
            if (input.isDependentComplex(0)) {
                output = new Curve(input.getDataReal(), input.getDataImag());
                output.setIndependentLabels(0, input.getIndependentLabels(0));
                output.setDependentLabels(0, input.getDependentLabels(0));
            } else {
                double[] tmp = new double[input.size()];
                FlatArray.initializeArray(tmp);
                output = new Curve(input.getDataReal(), tmp);

                ErrorDialog.show("Warning: There is only one input to " + getToolName()
                        + " and it is real. The curve will have its y-values set to zero.");
            }
        }


        output(output);


    }


    /**
     * Initialses information specific to MakeCurve.
     */
    public void init() {
        super.init();

        setDefaultInputNodes(3);
        setMinimumInputNodes(1);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        // Initialise parameter update policy
        setParameterUpdatePolicy(PROCESS_UPDATE);

        // Initialise pop-up description and help file location
        setPopUpDescription("Makes a curve from input VectorTypes");
        setHelpFileLocation("MakeCurve.html");
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up MyMakeCurve (e.g. close open files) 
    }

    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables 
    }


    /**
     * @return an array of the input types for MyMakeCurve
     */
    public String[] getInputTypes() {
        return new String[]{"VectorType"};
    }

    /**
     * @return an array of the output types for MyMakeCurve
     */
    public String[] getOutputTypes() {
        return new String[]{"Curve"};
    }
}



