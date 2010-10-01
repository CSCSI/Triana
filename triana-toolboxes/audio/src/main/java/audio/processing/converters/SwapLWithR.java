package audio.processing.converters;

import org.trianacode.taskgraph.Unit;
import triana.types.audio.MultipleAudio;


/**
 * A SwapLWithR unit to ..
 *
 * @author ian
 * @version 2.0 03 Jan 2001
 */
public class SwapLWithR extends Unit {

    /**
     * ********************************************* ** USER CODE of SwapLWithR goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        MultipleAudio input = (MultipleAudio) getInputAtNode(0);

        MultipleAudio output = new MultipleAudio(2);

        output.setChannel(0, input.getChannel(0), input.getChannelFormat(0));

        output.setChannel(1, input.getChannel(1), input.getChannelFormat(1));
        output(output);
    }


    /**
     * Initialses information specific to SwapLWithR.
     */
    public void init() {
        super.init();

        setDefaultInputNodes(2);
        setMinimumInputNodes(2);
        setMaximumInputNodes(2);

        setDefaultOutputNodes(2);
        setMinimumOutputNodes(2);
        setMaximumOutputNodes(2);
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
     * Saves SwapLWithR's parameters.
     */
    public void saveParameters() {
    }

    /**
     * Used to set each of SwapLWithR's parameters. This should NOT be used to update this unit's user interface
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
        return "Swaps the left and right stereo channels around";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "SwapLWithR.html";
    }
}



