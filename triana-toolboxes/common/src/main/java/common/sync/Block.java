package common.sync;

import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.Unit;

/**
 * A Block unit to ..
 *
 * @author ian
 * @version 1.0 beta 14 Sep 1999
 */
public class Block extends Unit {

    /**
     * ********************************************* ** USER CODE of Block goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        Task task = getTask();
        Object data;

        for (int count = 0; count < task.getDataInputNodeCount(); ++count) {
            data = getInputAtNode(count);

            if (count < task.getDataOutputNodeCount()) {
                outputAtNode(count, data);
            }
        }
    }


    /**
     * Initialses information specific to Block.
     */
    public void init() {
        super.init();

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);
    }


    /**
     * Reset's Block
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves Block's parameters.
     */
    public void saveParameters() {
    }

    /**
     * Used to set each of Block's parameters.
     */
    public void setParameter(String name, String value) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to Block, each separated by a white
     *         space.
     */
    public String[] getInputTypes() {
        return new String[]{"triana.types.TrianaType"};
    }

    /**
     * @return a string containing the names of the types output from Compare, each separated by a white space.
     */
    public String[] getOutputTypes() {
        return new String[]{"triana.types.TrianaType"};
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Put Block's brief description here";
    }

    /**
     *
     * @returns the location of the help file for this unit.
     */
    public String getHelpFile() {
        return "Block.html";
    }

}













