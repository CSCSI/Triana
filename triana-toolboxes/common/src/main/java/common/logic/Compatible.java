package common.logic;

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


import java.awt.Window;
import java.awt.event.ActionEvent;

import triana.types.Arithmetic;
import triana.types.Const;
import triana.types.OldUnit;

/**
 * A Compatible unit test two inputs for compatibility, as defined by GraphType.
 *
 * @author B.F. Schutz
 * @version 2.0 20 August 2000
 */
public class Compatible extends OldUnit {

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Tests inputs for compatibility";
    }

    /**
     * ********************************************* ** USER CODE of Compatible goes here    ***
     * *********************************************
     */
    public void process() {

        Object input, input2;
        input = getInputAtNode(0);
        input2 = getInputAtNode(1);

        if ((input instanceof Arithmetic) && (((Arithmetic) input).isCompatible(input2))) {
            output(new Const(1.0));
        } else {
            output(new Const(0.0));
        }
    }


    /**
     * Initialses information specific to Compatible.
     */
    public void init() {
        super.init();

        setResizableInputs(false);
        setResizableOutputs(true);

    }

    /**
     * Resets Compatible
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves Compatible's parameters to the parameter file.
     */
    public void saveParameters() {
    }

    /**
     * Loads Compatible's parameters of from the parameter file.
     */
    public void setParameter(String name, String value) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to Compatible, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "GraphType Const";
    }

    /**
     * @return a string containing the names of the types output from Compatible, each separated by a white space.
     */
    public String outputTypes() {
        return "Const";
    }

    /**
     *
     * @returns the location of the help file for this unit.
     */
    public String getHelpFile() {
        return "Compatible.html";
    }


    /**
     * @return Compatible's parameter window sp that triana can move and display it.
     */
    public Window getParameterWindow() {
        return null;
    }


    /**
     * Captures the events thrown out by Compatible.
     */
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);   // we need this

    }
}

















