package math.functions;

/*
 * Copyright (c) 1995 - 1998 University of Wales College of Cardiff
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


import org.trianacode.gui.windows.ErrorDialog;
import triana.types.Arithmetic;
import triana.types.OldUnit;

/**
 * A Sum unit to keep a sum of the inputs
 *
 * @author ian
 * @version 1.0 beta 23 Sep 1998
 */
public class Sum extends OldUnit {

    Object sum = null;

    /**
     * ********************************************* ** USER CODE of Sum goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        Object nextInput;

        nextInput = getInputAtNode(0);

        setOutputType(nextInput.getClass());

        if (sum == null) {
            sum = nextInput;
        } else {
            Arithmetic s = (Arithmetic) sum;
            if (s.isCompatible(nextInput)) {
                s = s.add(nextInput);
            } else {
                ErrorDialog.show("Incompatible data sets in " + getName());
                stop();
            }
        }
        output(sum);
    }


    /**
     * Initialses information specific to Sum.
     */
    public void init() {
        super.init();

        setResizableInputs(false);
        setResizableOutputs(true);
    }


    /**
     * Resets Sum
     */
    public void reset() {
        super.reset();
        sum = null;
    }

    /**
     * Saves Sum's parameters.
     */
    public void saveParameters() {
    }

    /**
     * Used to set each of Sum's parameters.
     */
    public void setParameter(String name, String value) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to Sum, each separated by a white space.
     */
    public String inputTypes() {
        return "Arithmetic";
    }

    /**
     * @return a string containing the names of the types output from Sum, each separated by a white space.
     */
    public String outputTypes() {
        return "Arithmetic";
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Outputs the summation of the inputs.  Reset clears the Buffer";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "Sum.html";
    }
}













