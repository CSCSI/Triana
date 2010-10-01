package audio.processing.converters;

import org.trianacode.taskgraph.Unit;
import triana.types.audio.MultipleAudio;

/**
 * A Mono2Stereo unit to ..
 *
 * @author ian
 * @version 2.0 29 Dec 2000
 */
public class Mono2Stereo extends Unit {

    /**
     * ********************************************* ** USER CODE of Mono2Stereo goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        MultipleAudio left = (MultipleAudio) getInputAtNode(0);
        MultipleAudio right = (MultipleAudio) getInputAtNode(1);

        MultipleAudio stereo = new MultipleAudio(2);

        stereo.setChannel(0, left.getChannel(0), left.getChannelFormat(0));
        stereo.setChannel(1, right.getChannel(0), right.getChannelFormat(0));

        output(stereo);
    }

    /**
     * Initialses information specific to Mono2Stereo.
     */
    public void init() {
        super.init();

        setDefaultInputNodes(2);
        setMinimumInputNodes(2);
        setMaximumInputNodes(2);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(1);
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
     * Saves Mono2Stereo's parameters.
     */
    public void saveParameters() {
    }

    /**
     * Used to set each of Mono2Stereo's parameters. This should NOT be used to update this unit's user interface
     */
    public void setParameter(String name, String value) {
    }

    /**
     * Used to update the widget in this unit's user interface that is used to control the given parameter name.
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input
     */
    public String[] getInputTypes() {
        return new String[]{"triana.types.audio.MultipleAudio"};
    }

    /**
     * @return an array of the output types
     */
    public String[] getOutputTypes() {
        return new String[]{"triana.types.audio.MultipleAudio"};
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Put Mono2Stereo's brief description here";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "Mono2Stereo.html";
    }
}



