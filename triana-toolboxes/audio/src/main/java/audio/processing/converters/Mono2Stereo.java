package audio.processing.converters;

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
 * A Mono2Stereo unit to ..
 *
 * @author ian
 * @version 2.0 29 Dec 2000
 */
public class Mono2Stereo extends OldUnit {

    /**
     * ********************************************* ** USER CODE of Mono2Stereo goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        MultipleAudio left = (MultipleAudio) getInputNode(0);
        MultipleAudio right = (MultipleAudio) getInputNode(1);

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

        setResizableInputs(false);
        setResizableOutputs(false);
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
     * @return a string containing the names of the types allowed to be input to Mono2Stereo, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "triana.types.audio.MultipleAudio";
    }

    /**
     * @return a string containing the names of the types output from Mono2Stereo, each separated by a white space.
     */
    public String outputTypes() {
        return "triana.types.audio.MultipleAudio";
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



