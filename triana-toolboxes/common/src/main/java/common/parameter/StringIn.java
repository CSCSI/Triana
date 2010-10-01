package common.parameter;

import org.trianacode.taskgraph.Unit;
import triana.types.Parameter;

/**
 * A StringIn unit to ..
 *
 * @author Ian Taylor
 * @version 1.0 Final 27 Jul 2000
 */
public class StringIn extends Unit {

    String value = "";


    /**
     * ********************************************* ** USER CODE of StringIn goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        output(new Parameter(value));
    }


    /**
     * Initialses information specific to StringIn.
     */
    public void init() {
        super.init();

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);


        String guilines = "";
        guilines += "Input your text string in the following box $title value TextField\n";
        setGUIBuilderV2Info(guilines);

    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format.
     */
//    public void setGUIInformation() {
//        addGUILine("Input your text string in the following box $title value TextField");
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
//     * Saves StringIn's parameters.
//     */
//    public void saveParameters() {
//        saveParameter("value", value);
//    }
//

    /**
     * Used to set each of StringIn's parameters.
     */
    public void parameterUpdate(String name, String value) {
        //updateGUIParameter(name, value);

        if (name.equals("value")) {
            this.value = value;
        }
    }

    /**
     * Don't need to use this for GUI Builder units as everthing is updated by triana automatically
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to StringIn, each separated by a white
     *         space.
     */
     public String[] getInputTypes() {
        return new String[]{};
    }

    /**
     * @return a string containing the names of the types output from Compare, each separated by a white space.
     */
    public String[] getOutputTypes() {
        return new String[]{"triana.types.Parameter"};
    }
    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Outputs a line of text specified by the user";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "Parameters.html";
    }
}
