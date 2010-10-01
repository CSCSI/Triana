package signalproc.dataparam;

import org.trianacode.taskgraph.Unit;
import triana.types.GraphType;
import triana.types.SampleSet;


/**
 * A SetLabels unit to reset the axis labels of a data set.
 *
 * @author David Churches
 * @version 1.1 06 Nov 2003
 */
public class SetLabels extends Unit {

    String xlabel = "";
    String ylabel = "";


    /**
     * ********************************************* ** USER CODE of SetLabels goes here    ***
     * *********************************************
     */
    public void process() throws Exception {

        GraphType input = (SampleSet) getInputAtNode(0);

        if (!xlabel.equals("")) {
            input.setIndependentLabels(0, xlabel);
        }
        if (!ylabel.equals("")) {
            input.setDependentLabels(0, ylabel);
        }

        output(input);
    }


    /**
     * Initialses information specific to SetLabels.
     */
    public void init() {
        super.init();

//        setUseGUIBuilder(true);
//
//        setRequireDoubleInputs(false);
//        setCanProcessDoubleArrays(false);

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(1);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(Integer.MAX_VALUE);
        String guilines = "";
        guilines += "New x-axis label: $title xlabel TextField\n";
        guilines += "New y-axis label: $title ylabel TextField\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format.
     */
//    public void setGUIInformation() {
//        addGUILine("New x-axis label: $title xlabel TextField");
//        addGUILine("New y-axis label: $title ylabel TextField");
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
//     * Saves SetLabels's parameters.
//     */
//    public void saveParameters() {
//        saveParameter("xlabel", xlabel);
//        saveParameter("ylabel", ylabel);
//    }

    /**
     * Used to set each of SetLabels's parameters.
     */
    public void parameterUpdate(String name, Object value) {
        //updateGUIParameter(name, value);

        if (name.equals("xlabel")) {
            xlabel = (String) value;
        }
        if (name.equals("ylabel")) {
            ylabel = (String) value;
        }
    }

    /**
     * Don't need to use this for GUI Builder units as everthing is updated by triana automatically
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to SetLabels, each separated by a white
     *         space.
     */
//    public String inputTypes() {
//        return "SampleSet";
//    }
//
//    /**
//     * @return a string containing the names of the types output from SetLabels, each separated by a white space.
//     */
//    public String outputTypes() {
//        return "SampleSet";
//    }

    public String[] getInputTypes() {
        return new String[]{"triana.types.SampleSet"};
    }

    public String[] getOutputTypes() {
        return new String[]{"triana.types.SampleSet"};
    }
    
    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Resets the axis labels of a SampleSet";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "SetLabels.html";
    }
}




