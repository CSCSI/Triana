package common.output;

import org.trianacode.taskgraph.Unit;

/**
 * A Stopper unit to ..
 *
 * @author ian
 * @version 2.0 08 Sep 2000
 */
public class Stopper extends Unit {

    /**
     * ********************************************* ** USER CODE of Stopper goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        for (int count = 0; count < getTask().getDataInputNodeCount(); count++) {
            getInputAtNode(count);
        }
        // do nothing
    }


    /**
     * Initialses information specific to Stopper.
     */
    public void init() {
        super.init();

        setMinimumInputNodes(0);
        setMaximumInputNodes(Integer.MAX_VALUE);
        setDefaultInputNodes(1);

        setMinimumOutputNodes(0);
        setMaximumOutputNodes(0);
        setDefaultOutputNodes(0);

        setDefaultNodeRequirement(OPTIONAL);
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
     * Saves Stopper's parameters.
     */
    public void saveParameters() {
    }

    /**
     * Used to set each of Stopper's parameters. This should NOT be used to update this unit's user interface
     */
    public void setParameter(String name, String value) {
    }

    /**
     * Used to update the widget in this unit's user interface that is used to control the given parameter name.
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to Stopper, each separated by a white
     *         space.
     */
    public String[] getInputTypes() {
        return new String[]{"triana.types.TrianaType"};
    }

    /**
     * @return an array of the output types for Loop2
     */
    public String[] getOutputTypes() {
        return new String[]{};
    }
    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Receives data but does nothing with it";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "Stopper.html";
    }

}



