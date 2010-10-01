package audio.processing.tools;

import org.trianacode.taskgraph.Unit;
import triana.types.EmptyingType;
import triana.types.SampleSet;
import triana.types.TrianaType;
import triana.types.audio.MultipleAudio;
import triana.types.util.Str;

/**
 * A Rectifier unit to ..
 *
 * @author ian
 * @version 2.0 26 Jan 2001
 */
public class Rectifier extends Unit {

    String halfOrFull = "Half";
    boolean bypass = false;

    /**
     * ********************************************* ** USER CODE of Rectifier goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        TrianaType input;

        input = (TrianaType) getInputAtNode(0);
        if (input instanceof EmptyingType) {
            return;
        }

        if (input instanceof SampleSet) {
            int j;
            double[] inputdataR;
            inputdataR = ((SampleSet) input).getData();

            if (halfOrFull.equals("Half")) {
                for (j = 0; j < inputdataR.length; j++) {
                    if (inputdataR[j] < 0.0) {
                        inputdataR[j] = 0.0;
                    }
                }
            } else {
                for (j = 0; j < inputdataR.length; j++) {
                    inputdataR[j] = Math.abs(inputdataR[j]);
                }
            }
            output(input);
        }

        if (input instanceof MultipleAudio) {
            MultipleAudio audio = (MultipleAudio) input;
            int j;
            Object chan;

            for (int i = 0; i < audio.getChannels(); ++i) {
                chan = audio.getChannel(i);

                if (chan instanceof short[]) { // FOR SHORTS
                    short[] inputdataR;
                    inputdataR = (short[]) chan;
                    if (halfOrFull.equals("Half")) {
                        for (j = 0; j < inputdataR.length; j++) {
                            if (inputdataR[j] < 0) {
                                inputdataR[j] = 0;
                            }
                        }
                    } else {
                        for (j = 0; j < inputdataR.length; j++) {
                            inputdataR[j] = (short) Math.abs(inputdataR[j]);
                        }
                    }
                }
                if (chan instanceof int[]) { // FOR INTS
                    int[] inputdataR;
                    inputdataR = (int[]) chan;
                    if (halfOrFull.equals("Half")) {
                        for (j = 0; j < inputdataR.length; j++) {
                            if (inputdataR[j] < 0) {
                                inputdataR[j] = 0;
                            }
                        }
                    } else {
                        for (j = 0; j < inputdataR.length; j++) {
                            inputdataR[j] = (int) Math.abs(inputdataR[j]);
                        }
                    }
                }
                if (chan instanceof double[]) { // FOR doubles
                    double[] inputdataR;
                    inputdataR = (double[]) chan;
                    if (halfOrFull.equals("Half")) {
                        for (j = 0; j < inputdataR.length; j++) {
                            if (inputdataR[j] < 0) {
                                inputdataR[j] = 0.0;
                            }
                        }
                    } else {
                        for (j = 0; j < inputdataR.length; j++) {
                            inputdataR[j] = Math.abs(inputdataR[j]);
                        }
                    }
                }
            } // end channels
            output(audio);
        }
    }

    /**
     * Initialses information specific to Rectifier.
     */
    public void init() {
        super.init();

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(1);
        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        String guilines = "";
        guilines += "Rectifier Type ? $title halfOrFull Choice Half Full\n";
        guilines += "Bypass ? $title bypass Checkbox false\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format.
     */
//    public void setGUIInformation() {
//
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

    public void parameterUpdate(String name, String value) {
        // Code to update local variables
        if (name.equals("halfOrFull")) {
            halfOrFull = value;
        }
        if (name.equals("bypass")) {
            bypass = Str.strToBoolean(value);
        }
    }

    /**
     * Don't need to use this for GUI Builder units as everthing is updated by triana automatically
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to Rectifier, each separated by a white
     *         space.
     */
    public String[] getInputTypes() {
        return new String[]{"triana.types.SampleSet"};
    }
    /**
     * @return a string containing the names of the types output from MatchFilter, each separated by a white space.
     */

    public String[] getOutputTypes() {
        return new String[]{"triana.types.SampleSet"};
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Half or Full Wave Rectifier";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "Rectifier.html";
    }
}




