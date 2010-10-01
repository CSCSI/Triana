package audio.processing.tools;

import org.trianacode.taskgraph.Unit;
import triana.types.SampleSet;
import triana.types.util.Str;

/**
 * A PitchDet unit to ..
 *
 * @author ian
 * @version 2.0 09 Nov 2000
 */
public class PitchDet extends Unit {
    int filterSize = 100;
    int filterDet = 200;
    int thresh = 50;
    boolean mute = false;

    /**
     * ********************************************* ** USER CODE of PitchDet goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        SampleSet input = (SampleSet) getInputAtNode(0);

        double dataIn[] = input.data;
        double dataOut[] = new double[dataIn.length];
        double filter[] = new double[filterSize];
        int i = 0, j, k;
        double sum;

        while (i < (dataIn.length - filterSize - filterDet)) {
            for (j = 0; j < filterSize; ++j) {
                filter[j] = dataIn[i + j];
            }

            for (k = 20; k < filterDet; ++k) {
                sum = 0.0;
                for (j = 0; j < filterSize; ++j) {
                    sum += (filter[j] - dataIn[i + j + k]);
                }
                if (Math.abs(sum) < thresh) {
                    System.out.println("Pitch = " + k + " val = " + sum);
                    break;
                }
            }

            if (k < filterSize - 1) // found pitch
            {
                for (j = 0; j < filterSize; ++j) {
                    dataOut[i + j] = dataIn[i + j];
                }
            } else {
                for (j = 0; j < filterSize; ++j) {
                    dataOut[i + j] = 0.0;
                }
            }   // no pitch cancel waveform

            i += k; // go to next point where pitch is
        }

        output(new SampleSet(input.samplingFrequency(), dataOut));
    }


    /**
     * Initialses information specific to PitchDet.
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
        guilines += "Enter Size Of Pitch Template (samples) $title filterSize IntScroller 0 200 100\n";
        guilines += "Enter Number of Samples to apply pitch template to $title filterDet IntScroller 0 500 200\n";
        guilines += "Enter Threshold $title thresh IntScroller 0 200 50\n";
        guilines += "Mute $title mute Checkbox false\n";
        setGUIBuilderV2Info(guilines);
    }
//
//    /**
//     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
//     *         Such lines must in the specified GUI text format.
//     */
//    public void setGUIInformation() {
//        addGUILine("Enter Size Of Pitch Template (samples) $title filterSize IntScroller 0 200 100");
//        addGUILine("Enter Number of Samples to apply pitch template to $title filterDet IntScroller 0 500 200");
//        addGUILine("Enter Threshold $title thresh IntScroller 0 200 50");
//        addGUILine("Mute $title mute Checkbox false");
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
        if (name.equals("filterSize")) {
            filterSize = Str.strToInt(value);
        }
        if (name.equals("filterDet")) {
            filterSize = Str.strToInt(value);
        }
        if (name.equals("thresh")) {
            thresh = Str.strToInt(value);
        }
        if (name.equals("mute")) {
            mute = Str.strToBoolean(value);
        }
    }

    /**
     * Don't need to use this for GUI Builder units as everthing is updated by triana automatically
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to PitchDet, each separated by a white
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
        return "Put PitchDet's brief description here";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "PitchDet.html";
    }
}




