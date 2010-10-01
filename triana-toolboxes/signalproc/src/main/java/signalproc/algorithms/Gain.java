package signalproc.algorithms;

import org.trianacode.taskgraph.Unit;
import triana.types.SampleSet;
import triana.types.util.Str;

/**
 * A Gain unit to ..
 *
 * @author ian
 * @version 2.0 13 Oct 2000
 */
public class Gain extends Unit {

    double gain = 1;


    /**
     * ********************************************* ** USER CODE of Gain goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        SampleSet input = (SampleSet) getInputAtNode(0);

        double dataIn[] = input.data;
        double dataOut[] = new double[dataIn.length];
        for (int i = 0; i < dataIn.length; ++i) {
            dataOut[i] = dataIn[i] * gain;
        }

        output(new SampleSet(input.samplingFrequency(), dataOut));
    }


    /**
     * Initialses information specific to Gain.
     */
    public void init() {
        super.init();

//        setUseGUIBuilder(true);
//
//        setResizableInputs(false);
//        setResizableOutputs(true);

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        String guilines = "";
        guilines += "Change Gain Setting here! $title gain Scroller 0 10 1\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format.
     */
//    public void setGUIInformation() {
//        addGUILine("Change Gain Setting here! $title gain Scroller 0 10 1");
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
//     * Saves Gain's parameters.
//     */
//    public void saveParameters() {
//        saveParameter("gain", gain);
//    }


    /**
     * Used to set each of Gain's parameters.
     */
    public void paramterUpdate(String name, Object value) {
        //updateGUIParameter(name, value);

        if (name.equals("gain")) {
            gain = Str.strToDouble((String) value);
        }
    }

    /**
     * Don't need to use this for GUI Builder units as everthing is updated by triana automatically
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to Gain, each separated by a white space.
     */
//    public String inputTypes() {
//        return "SampleSet";
//    }
//
//    /**
//     * @return a string containing the names of the types output from Gain, each separated by a white space.
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
        return "This is our gain control";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "Gain.html";
    }
}




