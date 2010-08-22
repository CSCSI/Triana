package common.processing;

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

/**
 * A Duplicator unit to duplicate the input and pass it to all the output nodes.
 *
 * @author Ian Taylor
 * @version 1.0 alpha 21 Jan 1998
 */
public class Duplicator extends OldUnit {

    /**
     * ********************************************* ** USER CODE of Duplicator goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        Object input = getInputAtNode(0);

        output(input);
    }


    /**
     * Initialses information specific to Duplicator.
     */
    public void init() {
        super.init();

        setResizableInputs(false);
        setResizableOutputs(true);
    }


    /**
     * Reset's Duplicator
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves parameters
     */
    public void saveParameters() {
    }

    /**
     * Sets the parameters
     */
    public void setParameter(String name, String value) {
    }


    /**
     * This method should be overridden to return an array of the data input types accepted by this unit (returns
     * triana.types.TrianaType by default).
     *
     * @return an array of the input types for this unit
     */
    public String[] getInputTypes() {
        return new String[]{"java.lang.Object"};
    }

    /**
     * This method should be overridden to return an array of the data output types accepted by this unit (returns
     * triana.types.TrianaType by default).
     *
     * @return an array of the output types for yhis unit
     */
    public String[] getOutputTypes() {
        return new String[]{"java.lang.Object"};
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Duplicates the input by passing a copy to each output node";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "Duplicator.html";
    }
}













