package math.functions;

import org.trianacode.taskgraph.Unit;
import triana.types.Const;
import triana.types.GraphType;

/**
 * A Minimum unit to compute minimum value of input data set.
 *
 * @author B F Schutz
 * @version 2.0 26 Feb 2001
 */
public class Minimum extends Unit {

    /**
     * ********************************************* ** USER CODE of Minimum goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        GraphType input = (GraphType) getInputAtNode(0);
        Const output;
        if (input.isDependentComplex(0)) {
            output = new Const(((double[]) input.minDependentVariablesReal())[0],
                    ((double[]) input.minDependentVariablesImag())[0]);
        } else {
            output = new Const(((double[]) input.minDependentVariablesReal())[0]);
        }

        output(output);

    }


    /**
     * Initialses information specific to Minimum.
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
        setMinimumOutputNodes(1);
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
     * Saves Minimum's parameters.
     */
    public void saveParameters() {
    }

    /**
     * Used to set each of Minimum's parameters. This should NOT be used to update this unit's user interface
     */
    public void setParameter(String name, String value) {
    }

    /**
     * Used to update the widget in this unit's user interface that is used to control the given parameter name.
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to Minimum, each separated by a white
     *         space.
     */
    public String[] getInputTypes() {
        return new String[]{"triana.types.GraphType"};
    }

    public String[] getOutputTypes() {
        return new String[]{"triana.types.Const"};
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Find minimum value of input set";
    }

    /**
     *
     * @returns the location of the help file for this unit.
     */
    public String getHelpFile() {
        return "MinMax.html";
    }
}



