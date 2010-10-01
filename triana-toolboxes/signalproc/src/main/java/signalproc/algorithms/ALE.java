package signalproc.algorithms;

import org.trianacode.taskgraph.Unit;
import triana.types.SampleSet;
import triana.types.util.Str;


/**
 * A ALE unit to ..
 *
 * @author ian
 * @version 2.0 18 Sep 2000
 */
public class ALE extends Unit {
    String type = "NLMS";
    int tapSpacing = 1;
    String stepsize = "auto";
    int N = 200;
    ALEProcessor ale = null;

    /**
     * ********************************************* ** USER CODE of ALE goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        if (ale == null) {
            ale = new ALEProcessor(N, 1, stepsize, type, false);
            //ale.setObject(this);
        }

        SampleSet input = (SampleSet) getInputAtNode(0);
        SampleSet output = (SampleSet) input.copyMe();

        ale.process(output.data);

        output(output);
    }


    /**
     * Initialses information specific to ALE.
     */
    public void init() {
        super.init();

//        setUseGUIBuilder(true);
//
//        setResizableInputs(false);
//        setResizableOutputs(true);

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(1);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        String guilines = "";
        guilines += "Adaptive Filter Type $title type Choice NLMS LMS\n";
        guilines += "Enter Step Size (or choose 'auto') $title stepsize TextField auto\n";
        guilines += "Enter Number of Taps : $title numberOfTaps IntScroller 0 1000 200\n";
        guilines += "Enter Tap Size : $title tapSpacing IntScroller 0 100 1\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format.
     */
//    public void setGUIInformation() {
//        addGUILine("Adaptive Filter Type $title type Choice NLMS LMS");
//        addGUILine("Enter Step Size (or choose 'auto') $title stepsize TextField auto");
//        addGUILine("Enter Number of Taps : $title numberOfTaps IntScroller 0 1000 200");
//        addGUILine("Enter Tap Size : $title tapSpacing IntScroller 0 100 1");
//    }

    /**
     * Called when the reset button is pressed within the MainTriana Window
     */
    public void reset() {
        super.reset();
        if (ale != null) {
            ale.reset();
        }
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
//     * Saves ALE's parameters.
//     */
//    public void saveParameters() {
//        saveParameter("type", type);
//        saveParameter("stepsize", stepsize);
//        saveParameter("numberOfTaps", N);
//        saveParameter("tapSpacing", tapSpacing);
//    }

    /**
     * Used to set each of ALE's parameters.
     */
    public void parameterUpdate(String name, Object value) {
        //updateGUIParameter(name, value);

        if (ale == null) {
            ale = new ALEProcessor(N, 1, stepsize, type, false);
            //ale.setObject(this);
        }

        if (name.equals("type")) {
            type = (String) value;
            if (ale != null) {
                ale.setWeightUpdateType(type);
            }
        }
        if (name.equals("stepsize")) {
            stepsize = (String) value;
            if (ale != null) {
                ale.setStepSize(stepsize);
            }
        }
        if (name.equals("numberOfTaps")) {
            N = Str.strToInt((String) value);
            if (ale != null) {
                ale.setNumberOfTaps(N);
            }
        }
        if (name.equals("tapSpacing")) {
            tapSpacing = Str.strToInt((String) value);
            if (ale != null) {
                ale.setTapSpacing(tapSpacing);
            }
        }
    }

    /**
     * Don't need to use this for GUI Builder units as everthing is updated by triana automatically
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to ALE, each separated by a white space.
     */
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
        return "Adaptive Noise Cancelling to remove noise from a signal";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "ALE.html";
    }
}