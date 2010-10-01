package audio.processing.tools;

import org.trianacode.taskgraph.Unit;
import triana.types.audio.MultipleAudio;

/**
 * A Invert unit to invert a WaveForm
 *
 * @author ian
 * @version 2.0 03 Jan 2001
 */
public class InvertWave extends Unit {

    /**
     * ********************************************* ** USER CODE of Invert goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        MultipleAudio audio = (MultipleAudio) getInputAtNode(0);

        Object audioL = audio.getChannel(0);
        Object audioR = audio.getChannel(1);

        if (audioL instanceof short[]) { // 16-bit audio
            if (!(audioR instanceof short[]) && (audioR != null)) {
                throw new Exception(
                        "Incompatible types in " + getTask().getToolName() + "\nLeft Audio is 16-bit and Right is 8 or 24-bit");
            }
            if (audioL != null) { // left side
                short[] waveL = ((short[]) audioL);
                for (int i = 0; i < waveL.length; ++i) {
                    waveL[i] = (short) -waveL[i];
                }
            }
            if (audioR != null) { // right side
                short[] waveR = ((short[]) audioR);
                for (int i = 0; i < waveR.length; ++i) {
                    waveR[i] = (short) -waveR[i];
                }
            }
            output(audio);
        } // end 16-bit response filter
        else if (audioL instanceof byte[]) { // 8-bit audio
            if (!(audioR instanceof byte[]) && (audioR != null)) {
                throw new Exception(
                        "Incompatible types in " + getTask().getToolName() + "\nLeft Audio is 16-bit and Right is 8 or 24-bit");
            }
            if (audioL != null) { // left side
                byte[] waveL = ((byte[]) audioL);
                for (int i = 0; i < waveL.length; ++i) {
                    waveL[i] = (byte) -waveL[i];
                }
            }
            if (audioR != null) { // right side
                byte[] waveR = ((byte[]) audioR);
                for (int i = 0; i < waveR.length; ++i) {
                    waveR[i] = (byte) -waveR[i];
                }
            }
            output(audio);
        } // end 16-bit response filter
        else if (audioL instanceof int[]) { // 24 and 32-bit audio
            if (!(audioR instanceof int[]) && (audioR != null)) {
                throw new Exception(
                        "Incompatible types in " + getTask().getToolName() + "\nLeft Audio is 16-bit and Right is 8 or 24-bit");
            }
            if (audioL != null) { // left side
                int[] waveL = ((int[]) audioL);
                for (int i = 0; i < waveL.length; ++i) {
                    waveL[i] = (int) -waveL[i];
                }
            }
            if (audioR != null) { // right side
                int[] waveR = ((int[]) audioR);
                for (int i = 0; i < waveR.length; ++i) {
                    waveR[i] = (int) -waveR[i];
                }
            }
            output(audio);
        } // end 16-bit response filter
    }

    /**
     * Initialses information specific to Invert.
     */
    public void init() {
        super.init();

                // Initialise node properties
        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(1);

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
     * Saves Invert's parameters.
     */
    public void saveParameters() {
    }

    /**
     * Used to set each of Invert's parameters. This should NOT be used to update this unit's user interface
     */
    public void setParameter(String name, String value) {
    }

    /**
     * Used to update the widget in this unit's user interface that is used to control the given parameter name.
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return an array of the input types
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
        return "Invert the waveform(s) i.e. turns them upside down!";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "InvertWave.html";
    }
}



