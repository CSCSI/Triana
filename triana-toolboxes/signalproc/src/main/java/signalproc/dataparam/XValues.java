package signalproc.dataparam;

import org.trianacode.taskgraph.Unit;
import triana.types.VectorType;
import triana.types.util.Triplet;


/**
 * A XValues unit to return the values of the independent variable as a VectorType.
 *
 * @author B F Schutz
 * @version 2.0 28 Feb 2001
 */
public class XValues extends Unit {

    /**
     * ********************************************* ** USER CODE of XValues goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        VectorType input = (VectorType) getInputAtNode(0);
        if (input.isIndependentComplex(0)) {
            output(new VectorType(new Triplet(input.size()), input.getXReal(), input.getXImag()));
        } else {
            output(new VectorType(input.getXReal()));
        }

    }


    /**
     * Initialses information specific to XValues.
     */
    public void init() {
        super.init();

        // set these to true if your unit can process double-precision
        // arrays
//        setRequireDoubleInputs(false);
//        setCanProcessDoubleArrays(false);
//
//        setResizableInputs(false);
//        setResizableOutputs(true);
        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(1);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(Integer.MAX_VALUE);
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
//    public void starting() {
//        super.starting();
//    }

    /**
     * Saves XValues's parameters.
     */
    public void saveParameters() {
    }

    /**
     * Used to set each of XValues's parameters. This should NOT be used to update this unit's user interface
     */
    public void setParameter(String name, String value) {
    }

    /**
     * Used to update the widget in this unit's user interface that is used to control the given parameter name.
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to XValues, each separated by a white
     *         space.
     */
//    public String inputTypes() {
//        return "VectorType";
//    }
//
//    /**
//     * @return a string containing the names of the types output from XValues, each separated by a white space.
//     */
//    public String outputTypes() {
//        return "VectorType";
//    }

    public String[] getInputTypes() {
        return new String[]{"triana.types.VectorType"};
    }

    public String[] getOutputTypes() {
        return new String[]{"triana.types.VectorType"};
    }


    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Return the array of x-values (independent variable)";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "XValues.html";
    }
}



