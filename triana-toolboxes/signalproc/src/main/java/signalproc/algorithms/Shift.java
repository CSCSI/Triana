package signalproc.algorithms;

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
import triana.types.VectorType;


/**
 * A Shift unit to cyclicly shift a VectorType data set to the right.
 *
 * @author B F Schutz
 * @version 1.0 05 Mar 2001
 */
public class Shift extends OldUnit {

    int shift = 1;


    /**
     * ********************************************* ** USER CODE of Shift goes here    ***
     * *********************************************
     */
    public void process() throws Exception {

        VectorType input = (VectorType) getInputNode(0);

        if (shift != 0) {
            shiftData(input, shift);
        }

        output(input);

    }


    public static void shiftData(VectorType input, int shift) {

        cyclicShift(input.getDataReal(), shift);

        if (input.isDependentComplex(0)) {
            cyclicShift(input.getDataImag(), shift);
        }

    }

    public static void cyclicShift(double[] data, int shift) {

        int len = data.length;
        shift = shift % len;
        double[] overhang;
        int first;

        if (shift == 0) {
            return;
        } else if (shift > 0) {
            overhang = new double[shift];
            first = len - shift;
            System.arraycopy(data, first, overhang, 0, shift);
            System.arraycopy(data, 0, data, shift, first);
            System.arraycopy(overhang, 0, data, 0, shift);
        } else if (shift < 0) {
            overhang = new double[-shift];
            first = len + shift;
            System.arraycopy(data, 0, overhang, 0, -shift);
            System.arraycopy(data, -shift, data, 0, first);
            System.arraycopy(overhang, 0, data, first, -shift);
        }
    }


    /**
     * Initialses information specific to Shift.
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
        addGUILine("Number of elements to shift to the right $title shift IntScroller -100 100 0");
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
     * Saves Shift's parameters.
     */
    public void saveParameters() {
        saveParameter("shift", shift);
    }


    /**
     * Used to set each of Shift's parameters.
     */
    public void setParameter(String name, String value) {
        updateGUIParameter(name, value);

        if (name.equals("shift")) {
            shift = strToInt(value);
        }
    }

    /**
     * Don't need to use this for GUI Builder units as everthing is updated by triana automatically
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to Shift, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "VectorType";
    }

    /**
     * @return a string containing the names of the types output from Shift, each separated by a white space.
     */
    public String outputTypes() {
        return "VectorType";
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Cyclic shift of data to the right";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "Shift.html";
    }
}




