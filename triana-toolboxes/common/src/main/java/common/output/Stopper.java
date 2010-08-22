package common.output;

import triana.types.OldUnit;

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


/**
 * A Stopper unit to ..
 *
 * @author ian
 * @version 2.0 08 Sep 2000
 */
public class Stopper extends OldUnit {

    /**
     * ********************************************* ** USER CODE of Stopper goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        for (int count = 0; count < getTask().getDataInputNodeCount(); count++) {
            getInputAtNode(count);
        }
        // do nothing
    }


    /**
     * Initialses information specific to Stopper.
     */
    public void init() {
        super.init();

        setMinimumInputNodes(0);
        setMaximumInputNodes(Integer.MAX_VALUE);
        setDefaultInputNodes(1);

        setMinimumOutputNodes(0);
        setMaximumOutputNodes(0);
        setDefaultOutputNodes(0);

        setDefaultNodeRequirement(OPTIONAL);
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
     * Saves Stopper's parameters.
     */
    public void saveParameters() {
    }

    /**
     * Used to set each of Stopper's parameters. This should NOT be used to update this unit's user interface
     */
    public void setParameter(String name, String value) {
    }

    /**
     * Used to update the widget in this unit's user interface that is used to control the given parameter name.
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to Stopper, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "TrianaType";
    }

    /**
     * @return a string containing the names of the types output from Stopper, each separated by a white space.
     */
    public String outputTypes() {
        return "none";
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Receives data but does nothing with it";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "Stopper.html";
    }

}



