package common.logic;

import org.trianacode.taskgraph.Unit;
import triana.types.Parameter;
import triana.types.util.Str;

/**
 * A ForNext unit to ..
 *
 * @author ian
 * @version 2.0 09 Jan 2001
 */
public class ForNext extends Unit {

    String start = "0";
    String stop = "10";
    String step = "1";

    /**
     * ********************************************* ** USER CODE of ForNext goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        int i = Str.strToInt(start);
        int tstep = Str.strToInt(step);
        int tstop = Str.strToInt(stop);


        while (i <= tstop) {
            output(new Parameter(i));
            i += tstep;
        }
    }

    /**
     * Initialses information specific to ForNext.
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
        guilines += "Start Value $title start TextField 0\n";
        guilines += "Stop Value $title stop TextField 10\n";
        guilines += "Step Size $title step TextField 1\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Called when the reset button is pressed within the MainTriana Window
     */
    public void reset() {
        super.reset();
    }

//    /**
//     * Saves ForNext's parameters.
//     */
//    public void saveParameters() {
//        saveParameter("start", start);
//        saveParameter("stop", stop);
//        saveParameter("step", step);
//    }


    /**
     * Used to set each of ForNext's parameters.
     */
    public void parameterUpdate(String name, String value) {        
        if (name.equals("start")) {
            start = value;
        }
        if (name.equals("stop")) {
            stop = value;
        }
        if (name.equals("step")) {
            step = value;
        }
    }

    /**
     * Don't need to use this for GUI Builder units as everthing is updated by triana automatically
     */
    public void updateWidgetFor(String name) {
    }

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
        return "Put ForNext's brief description here";
    }

    /**
     *
     * @returns the location of the help file for this unit.
     */
    public String getHelpFile() {
        return "ForNext.html";
    }
}