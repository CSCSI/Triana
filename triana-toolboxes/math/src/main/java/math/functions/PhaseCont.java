package math.functions;

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
 * A PhaseCont unit to smooth out jumps of 2 Pi in an inut sequence of real data representing a phase angle, so that the
 * angle moves smoothly to values larger than Pi or smaller than -Pi. </p><p> Most functions constrain the phase of a
 * complex number or the angle of a vector in the 2D plane to a principal range of (-Pi, Pi), for example the function
 * atan2. This OldUnit takes the output of such angle-finders and makes it continuous outside the principal range by
 * looking for big discontinuities (where the phase jumps from near -Pi to near +Pi, for example, and adds an
 * appropriate multiple of 2*Pi to the angle to eliminate the jump. </p><p> PhaseCont does not find the original phase.
 * It takes output from a OldUnit that does and smooths the phase. PhaseCont should only be used when one expects a
 * smoothly changing phase from one element to the next; it will not make sensible answers when applied, for example, to
 * phase noise. </p><p> This method can be used to find winding numbers of curves around the origin.
 *
 * @author B F Schutz
 * @version 1.1 13 January 2001
 */
public class PhaseCont extends OldUnit {

    boolean carryOver = true;

    VectorType input, output;
    double jump, lastPhase;
    double twoPi = 2.0 * Math.PI;


    /**
     * ********************************************* ** USER CODE of PhaseCont goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        input = (VectorType) getInputAtNode(0);
        output = (VectorType) input;
        setOutputType(output.getClass());

        double[] outputdata = output.getData();

        double lastAngle, thisAngle, diffAngle;
        if (carryOver) {
            lastAngle = lastPhase;
            thisAngle = outputdata[0];
            diffAngle = thisAngle - lastAngle;
            if (diffAngle > Math.PI) {
                jump -= twoPi;
            } else if (diffAngle < -Math.PI) {
                jump += twoPi;
            }
            outputdata[0] = thisAngle + jump;
            lastAngle = thisAngle;
        } else {
            lastAngle = outputdata[0];
            jump = 0.0;
        }

        for (int i = 1; i < outputdata.length; i++) {
            thisAngle = outputdata[i];
            diffAngle = thisAngle - lastAngle;
            if (diffAngle > Math.PI) {
                jump -= twoPi;
            } else if (diffAngle < -Math.PI) {
                jump += twoPi;
            }
            outputdata[i] = thisAngle + jump;
            lastAngle = thisAngle;
        }

        lastPhase = lastAngle;

        output(output);

    }


    /**
     * Initialses information specific to PhaseCont.
     */
    public void init() {
        super.init();

        setUseGUIBuilder(true);

        setResizableInputs(false);
        setResizableOutputs(true);
        // This is to ensure that we receive arrays containing double-precision numbers
        setRequireDoubleInputs(true);
        setCanProcessDoubleArrays(true);

        jump = 0.0;
        lastPhase = 0.0;

    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format.
     */
    public void setGUIInformation() {
        addGUILine("Continue phase from one input data set to next? $title carryOver Checkbox true");
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
     * Saves PhaseCont's parameters.
     */
    public void saveParameters() {
        saveParameter("carryOver", carryOver);
    }


    /**
     * Used to set each of PhaseCont's parameters.
     */
    public void setParameter(String name, String value) {
        updateGUIParameter(name, value);

        if (name.equals("carryOver")) {
            carryOver = strToBoolean(value);
        }
    }

    /**
     * Don't need to use this for GUI Builder units as everthing is updated by triana automatically
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to PhaseCont, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "VectorType";
    }

    /**
     * @return a string containing the names of the types output from PhaseCont, each separated by a white space.
     */
    public String outputTypes() {
        return "VectorType";
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Make phase angle continuous by allowing angles outside (-Pi, Pi).";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "PhaseCont.html";
    }
}




