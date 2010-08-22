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


import java.awt.event.ActionEvent;

import org.trianacode.gui.windows.ErrorDialog;
import triana.types.Const;
import triana.types.OldUnit;


/**
 * If takes two inputs: the first is the test value (Const) and the second is the data that gets routed to either the
 * first output or the second output according to whether the const value is larger or smaller than the test.  The test
 * value is a parameter.
 *
 * @author B.F. Schutz
 * @version 2.0 20 August 2000
 */
public class If extends OldUnit {
    /**
     * The UnitWindow for If
     */
    //ScrollerWindow myWindow;

    /**
     * Initial threshold parameter
     */
    double threshold = 0.0;


    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Outputs Const to node 0 if input is larger than threshold, or to node otherwise";
    }

    /**
     * ********************************************* ** USER CODE of If goes here    ***
     * *********************************************
     */
    public void process() {

        Object input, input2;

        input = getInputAtNode(0);
        input2 = getInputAtNode(1);
        Class inputClass = input2.getClass();
        setOutputType(inputClass);

        boolean sendToFirst = false;

        if (!(input instanceof Const)) {
            new ErrorDialog(null, "First input to If must be a " +
                    "Constant!!!");
            stop();
            return;
        }

        if (((Const) input).getReal() > threshold) {
            sendToFirst = true;
        }

        if (sendToFirst) {
            outputAtNode(0, input2);
        } else {
            outputAtNode(1, input2);
        }
    }


    /**
     * Initialses information specific to If.
     */
    public void init() {
        super.init();

        setResizableInputs(false);
        setResizableOutputs(false);

        setUseGUIBuilder(true);

//        myWindow = new ScrollerWindow(this, "Set test value.");
//        myWindow.setValues(0.0, 10, threshold);

    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format.
     */
    public void setGUIInformation() {
        addGUILine("Test value $title threshold Scroller -10 10 0.0");
    }

    /**
     * Reset's If
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves If's parameters to the parameter file.
     */
    public void saveParameters() {
        saveParameter("threshold", threshold);
    }

    /**
     * Loads If's parameters of from the parameter file.
     */
    public void setParameter(String name, String value) {
        if (name.equals("threshold")) {
            threshold = strToDouble(value);
        }
    }

    /**
     * @return a string containing the names of the types allowed to be input to If, each separated by a white space.
     */
    public String inputTypes() {
        return "Const TrianaType";
    }

    /**
     * @return a string containing the names of the types output from If, each separated by a white space.
     */
    public String outputTypes() {
        return "TrianaType";
    }

    /**
     *
     * @returns the location of the help file for this unit.
     */
    public String getHelpFile() {
        return "If.html";
    }


    /**
     * @return If's parameter window sp that Triana
     * can move and display it.
     */
    //   public Window getParameterWindow() {
    //       return myWindow;
    //       }


    /**
     * Captures the events thrown out by If.
     */
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);   // we need this

        //if (e.getSource() == myWindow.slider) {
        //    threshold = myWindow.getValue();
        //    }
    }
}

















