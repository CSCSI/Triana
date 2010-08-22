package common.conzt;

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
 * A ConstView unit to ..
 *
 * @author Ian Taylor
 * @version 2.0 07 Aug 2000
 */
public class ConstView extends OldUnit {
//    UnitWindow simpleTextWindow = null;
//    JTextField text;

    public static String CONST_VALUE = "constValue";

    /**
     * ********************************************* ** USER CODE of ConstView goes here    ***
     * *********************************************
     */
    public void process() {
        Number c = (Number) getInputAtNode(0);
        getTask().setParameter(CONST_VALUE, c.toString());
    }

    public void cleanUp() {
        super.cleanUp();
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Displays a Const in a Window";
    }

    /**
     * Initialses information specific to ConstView.
     */
    public void init() {
        super.init();

        setResizableInputs(false);
        setResizableOutputs(true);

        setParameterPanelClass("common.conzt.ConstViewPanel");
    }


    /**
     * Reset's ConstView
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves ConstView's parameters to the parameter file.
     */
    public void saveParameters() {
    }

    /**
     * Loads ConstView's parameters of from the parameter file.
     */
    public void setParameter(String name, String value) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to ConstView, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "java.lang.Number triana.types.Const";
    }

    /**
     * @return a string containing the names of the types output from ConstView, each separated by a white space.
     */
    public String outputTypes() {
        return "none";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "ConstView.html";
    }

}













