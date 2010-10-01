package signalproc.dataparam;

import org.trianacode.taskgraph.Unit;
import triana.types.GraphType;

/**
 * A SetName unit to reset the name (title) of a data set.
 *
 * @author B F Schutz
 * @version 1.1 24 May 2001
 */
public class SetName extends Unit {

    String newName = "";


    /**
     * ********************************************* ** USER CODE of SetName goes here    ***
     * *********************************************
     */
    public void process() throws Exception {

        GraphType input = (GraphType) getInputAtNode(0);

        if (!newName.equals("")) {
            input.setTitle(newName);
        }

        output(input);

    }


    /**
     * Initialses information specific to SetName.
     */
    public void init() {
        super.init();

//        setUseGUIBuilder(true);
//
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
        String guilines = "";
        guilines += "New name of data set: $title newName TextField\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format.
     */
//    public void setGUIInformation() {
//        addGUILine("New name of data set: $title newName TextField");
//    }

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
//
//    /**
//     * Saves SetName's parameters.
//     */
//    public void saveParameters() {
//        saveParameter("newName", newName);
//    }

    /**
     * Used to set each of SetName's parameters.
     */
    public void parameterUpdate(String name, Object value) {
        //updateGUIParameter(name, value);

        if (name.equals("newName")) {
            newName = (String) value;
        }
    }

    /**
     * Don't need to use this for GUI Builder units as everthing is updated by triana automatically
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to SetName, each separated by a white
     *         space.
     */
//    public String inputTypes() {
//        return "GraphType";
//    }
//
//    /**
//     * @return a string containing the names of the types output from SetName, each separated by a white space.
//     */
//    public String outputTypes() {
//        return "GraphType";
//    }

    public String[] getInputTypes() {
        return new String[]{"triana.types.GraphType"};
    }

    public String[] getOutputTypes() {
        return new String[]{"triana.types.GraphType"};
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Resets the name of a data set";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "SetName.html";
    }
}




