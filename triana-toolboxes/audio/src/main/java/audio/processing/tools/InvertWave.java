package audio.processing.tools;

/*
 * Copyright (c) 1995 onwards, University of Wales College of Cardiff
 *
 * Permission to use and modify this software and its documentation for
 * any purpose is hereby granted without fee provided a written agreement
 * exists between the recipients and the University.
 *
 * Further conditions of use are that (i) the above copyright notice and
 * this permission notice appear in all copies of the software and
 * related documentation, and (ii) the recipients of the software and
 * documentation undertake not to copy or redistribute the software and
 * documentation to any other party.
 *
 * THE SOFTWARE IS PROVIDED "AS-IS" AND WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS, IMPLIED OR OTHERWISE, INCLUDING WITHOUT LIMITATION, ANY
 * WARRANTY OF MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.
 *
 * IN NO EVENT SHALL THE UNIVERSITY OF WALES COLLEGE OF CARDIFF BE LIABLE
 * FOR ANY SPECIAL, INCIDENTAL, INDIRECT OR CONSEQUENTIAL DAMAGES OF ANY
 * KIND, OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 * PROFITS, WHETHER OR NOT ADVISED OF THE POSSIBILITY OF DAMAGE, AND ON
 * ANY THEORY OF LIABILITY, ARISING OUT OF OR IN CONNECTION WITH THE USE
 * OR PERFORMANCE OF THIS SOFTWARE.
 */


import triana.types.OldUnit;
import triana.types.audio.MultipleAudio;


/**
 * A Invert unit to ..
 *
 * @author ian
 * @version 2.0 03 Jan 2001
 */
public class InvertWave extends OldUnit {

    /**
     * ********************************************* ** USER CODE of Invert goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        MultipleAudio audio = (MultipleAudio) getInputNode(0);

        Object audioL = audio.getChannel(0);
        Object audioR = audio.getChannel(1);

        if (audioL instanceof short[]) { // 16-bit audio
            if (!(audioR instanceof short[]) && (audioR != null)) {
                throw new Exception(
                        "Incompatible types in " + getName() + "\nLeft Audio is 16-bit and Right is 8 or 24-bit");
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
                        "Incompatible types in " + getName() + "\nLeft Audio is 16-bit and Right is 8 or 24-bit");
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
                        "Incompatible types in " + getName() + "\nLeft Audio is 16-bit and Right is 8 or 24-bit");
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

        setResizableInputs(false);
        setResizableOutputs(true);
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
    public void starting() {
        super.starting();
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
     * @return a string containing the names of the types allowed to be input to Invert, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "triana.types.audio.MultipleAudio";
    }

    /**
     * @return a string containing the names of the types output from Invert, each separated by a white space.
     */
    public String outputTypes() {
        return "triana.types.audio.MultipleAudio";
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



