package math.functions;

import org.trianacode.gui.windows.ErrorDialog;
import org.trianacode.taskgraph.Unit;
import triana.types.Arithmetic;

/**
 * A Sum unit to keep a sum of the inputs
 *
 * @author ian
 * @version 1.0 beta 23 Sep 1998
 */
public class Sum extends Unit {

    Object sum = null;

    /**
     * ********************************************* ** USER CODE of Sum goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        Object nextInput;

        nextInput = getInputAtNode(0);

        //setOutputType(nextInput.getClass());

        if (sum == null) {
            sum = nextInput;
        } else {
            Arithmetic s = (Arithmetic) sum;
            if (s.isCompatible(nextInput)) {
                s = s.add(nextInput);
            } else {
                ErrorDialog.show("Incompatible data sets in " + getToolName());
                stop();
            }
        }
        output(sum);
    }


    /**
     * Initialses information specific to Sum.
     */
    public void init() {
        super.init();

//        setResizableInputs(false);
//        setResizableOutputs(true);

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);
        
    }


    /**
     * Resets Sum
     */
    public void reset() {
        super.reset();
        sum = null;
    }

    /**
     * Saves Sum's parameters.
     */
    public void saveParameters() {
    }

    /**
     * Used to set each of Sum's parameters.
     */
    public void setParameter(String name, String value) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to Sum, each separated by a white space.
     */
    public String[] getInputTypes() {
        return new String[]{"triana.types.GraphType", "triana.types.Const"};
    }

    public String[] getOutputTypes() {
        return new String[]{"triana.types.GraphType", "triana.types.Const"};
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Outputs the summation of the inputs.  Reset clears the Buffer";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "Sum.html";
    }
}













