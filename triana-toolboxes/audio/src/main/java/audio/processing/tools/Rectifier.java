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


import triana.types.EmptyingType;
import triana.types.OldUnit;
import triana.types.SampleSet;
import triana.types.TrianaType;
import triana.types.audio.MultipleAudio;


/**
 * A Rectifier unit to ..
 *
 * @author ian
 * @version 2.0 26 Jan 2001
 */
public class Rectifier extends OldUnit {

    String halfOrFull = "Half";
    boolean bypass = false;


    /**
     * ********************************************* ** USER CODE of Rectifier goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        TrianaType input;

        input = getInputNode(0);
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

        setUseGUIBuilder(true);

        setRequireDoubleInputs(false);
        setCanProcessDoubleArrays(false);

        setResizableInputs(false);
        setResizableOutputs(true);
    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format.
     */
    public void setGUIInformation() {
        addGUILine("Rectifier Type ? $title halfOrFull Choice Half Full");
        addGUILine("Bypass ? $title bypass Checkbox false");
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
     * Saves Rectifier's parameters.
     */
    public void saveParameters() {
        saveParameter("halfOrFull", halfOrFull);
        saveParameter("bypass", bypass);
    }


    /**
     * Used to set each of Rectifier's parameters.
     */
    public void setParameter(String name, String value) {
        updateGUIParameter(name, value);

        if (name.equals("halfOrFull")) {
            halfOrFull = value;
        }
        if (name.equals("bypass")) {
            bypass = strToBoolean(value);
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
    public String inputTypes() {
        return "MultipleAudio SampleSet";
    }

    /**
     * @return a string containing the names of the types output from Rectifier, each separated by a white space.
     */
    public String outputTypes() {
        return "MultipleAudio SampleSet";
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




