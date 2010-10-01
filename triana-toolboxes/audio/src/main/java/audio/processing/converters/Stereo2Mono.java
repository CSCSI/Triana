package audio.processing.converters;

import org.trianacode.taskgraph.Unit;
import triana.types.audio.MultipleAudio;

/**
 * A Stereo2Mono unit to ..
 *
 * @author ian
 * @version 2.0 29 Dec 2000
 */
public class Stereo2Mono extends Unit {

    /**
     * ********************************************* ** USER CODE of Stereo2Mono goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        MultipleAudio input = (MultipleAudio) getInputAtNode(0);

        MultipleAudio left = new MultipleAudio(1);
        left.setChannel(0, input.getChannel(0), input.getChannelFormat(0));

        MultipleAudio right = new MultipleAudio(1);
        right.setChannel(0, input.getChannel(1), input.getChannelFormat(1));

        outputAtNode(0, left);
        outputAtNode(1, right);
    }


    /**
     * Initialses information specific to Stereo2Mono.
     */
    public void init() {
        super.init();

        setDefaultInputNodes(1);
        setMinimumInputNodes(0);
        setMaximumInputNodes(1);
        setDefaultOutputNodes(2);
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(Integer.MAX_VALUE);
    }

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
     * Saves Stereo2Mono's parameters.
     */
    public void saveParameters() {
    }

    /**
     * Used to set each of Stereo2Mono's parameters. This should NOT be used to update this unit's user interface
     */
    public void setParameter(String name, String value) {
    }

    /**
     * Used to update the widget in this unit's user interface that is used to control the given parameter name.
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to Stereo2Mono, each separated by a white
     *         space.
     */
    public String[] getInputTypes() {
        return new String[]{"triana.types.audio.MultipleAudio"};
    }

    /**
     * @return an array of the output types for FuzzyBox
     */
    public String[] getOutputTypes() {
        return new String[]{"triana.types.audio.MultipleAudio"};
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Takes in a stereo signal and outputs 2 mono signals";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "Stereo2Mono.html";
    }
}



