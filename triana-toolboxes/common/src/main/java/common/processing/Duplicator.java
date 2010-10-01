package common.processing;

import org.trianacode.taskgraph.Unit;

/**
 * A Duplicator unit to duplicate the input and pass it to all the output nodes.
 *
 * @author Ian Taylor
 * @version 1.0 alpha 21 Jan 1998
 */
public class Duplicator extends Unit {

    /**
     * ********************************************* ** USER CODE of Duplicator goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        Object input = getInputAtNode(0);

        output(input);
    }


    /**
     * Initialses information specific to Duplicator.
     */
    public void init() {
        super.init();

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(2);
        setMinimumOutputNodes(2);
        setMaximumOutputNodes(Integer.MAX_VALUE);
    }


    /**
     * Reset's Duplicator
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves parameters
     */
    public void saveParameters() {
    }

    /**
     * Sets the parameters
     */
    public void setParameter(String name, String value) {
    }


    /**
     * This method should be overridden to return an array of the data input types accepted by this unit (returns
     * triana.types.TrianaType by default).
     *
     * @return an array of the input types for this unit
     */
    public String[] getInputTypes() {
        return new String[]{"java.lang.Object"};
    }

    /**
     * This method should be overridden to return an array of the data output types accepted by this unit (returns
     * triana.types.TrianaType by default).
     *
     * @return an array of the output types for yhis unit
     */
    public String[] getOutputTypes() {
        return new String[]{"java.lang.Object"};
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Duplicates the input by passing a copy to each output node";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "Duplicator.html";
    }
}













