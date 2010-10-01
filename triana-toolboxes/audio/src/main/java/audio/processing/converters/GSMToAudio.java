package audio.processing.converters;

import org.trianacode.gui.windows.ErrorDialog;
import org.trianacode.taskgraph.Unit;
import triana.types.audio.gsm.decoder.GSMDecoder;
import triana.types.audio.AudioChannelFormat;
import triana.types.audio.MultipleAudio;

/**
 * A GSMToAudio unit to ..
 *
 * @author ian
 * @version 2.0 03 Jan 2001
 */
public class GSMToAudio extends Unit {

    GSMDecoder gsmdecoder = new GSMDecoder();

    /**
     * ********************************************* ** USER CODE of GSMToAudio goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        MultipleAudio audio = (MultipleAudio) getInputAtNode(0);
        AudioChannelFormat format = audio.getAudioChannelFormat(0);

        byte[] bytedata = null;

        if (format.getEncoding() == AudioChannelFormat.GSM) // formatted
        {
            bytedata = (byte[]) audio.getChannel(0);
        }

        if (bytedata == null) {
            ErrorDialog.show("Error in " + getTask().getToolName() + " Cannot convert given format to Audio\n" + "Data MUST be in GSM Format!");
            stop();
            return;
        }

        short[] audiochan = gsmdecoder.process(bytedata);
        MultipleAudio ma = new MultipleAudio(1);
        ma.setChannel(0, audiochan,
                new AudioChannelFormat(8000, (short) 16, AudioChannelFormat.PCM));
        output(ma);
    }


    /**
     * Initialses information specific to GSMToAudio.
     */
    public void init() {
        super.init();

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
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
     * Called when the start button is pressed within the MainTriana Window
     */
//    public void starting() {
//        super.starting();
//    }

    /**
     * Saves GSMToAudio's parameters.
     */
    public void saveParameters() {
    }

    /**
     * Used to set each of GSMToAudio's parameters. This should NOT be used to update this unit's user interface
     */
    public void setParameter(String name, String value) {
    }

    /**
     * Used to update the widget in this unit's user interface that is used to control the given parameter name.
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to GSMToAudio, each separated by a white
     *         space.
     */
    public String[] getInputTypes() {
        return new String[]{"triana.types.audio.MultipleAudio"};
    }

    /**
     * @return an array of the output types for this unit
     */
    public String[] getOutputTypes() {
        return new String[]{"triana.types.audio.MultipleAudio"};
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Converts from GSM To 16-Bit 8kHZ Audio";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "GSMToAudio.html";
    }
}



