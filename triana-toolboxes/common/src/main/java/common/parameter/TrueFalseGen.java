package common.parameter;

import org.trianacode.taskgraph.Unit;
import triana.types.Parameter;

/**
 * A TrueFalseGen unit to create a boolean Parameter
 *
 * @author B F Schutz
 * @version 2.0 27 Feb 2001
 */
public class TrueFalseGen extends Unit {

    String value = "false";


    /**
     * ********************************************* ** USER CODE of TrueFalseGen goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        System.out.println(value);
        output(new Parameter(value));
    }


    /**
     * Initialses information specific to TrueFalseGen.
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
        guilines += "True or false? $title value TextField false\n";
        setGUIBuilderV2Info(guilines);
    }

    public void parameterUpdate(String paramname, Object value) {
        if (paramname.equals("value")) {
            this.value = (String) value;
        }
    }

//    /**
//     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
//     *         Such lines must in the specified GUI text format.
//     */
//    public void setGUIInformation() {
//        addGUILine("True or false? $title value TextField false");
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
//     * Saves TrueFalseGen's parameters.
//     */
//    public void saveParameters() {
//        saveParameter("value", value);
//    }


    /**
     * Used to set each of TrueFalseGen's parameters.
     */
    public void parameterUpdate(String name, String value) {
        //updateGUIParameter(name, value);

        if (name.equals("value")) {
            value = value;
        }
    }

    /**
     * Don't need to use this for GUI Builder units as everthing is updated by triana automatically
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to TrueFalseGen, each separated by a white
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
        return "Input a boolean parameter value";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "TrueFalseGen.html";
    }
}




