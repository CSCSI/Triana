package common.parameter;

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


import javax.swing.JTextField;
import triana.types.OldUnit;
import triana.types.Parameter;


/**
 * A ParamView unit to ..
 *
 * @author Ian Taylor
 * @version 1.0 alpha 07 Aug 1997
 */
public class ParamView extends OldUnit {
    JTextField text;

    /**
     * ********************************************* ** USER CODE of ParamView goes here    ***
     * *********************************************
     */
    public void process() {
/*        if ( simpleTextWindow == null) { // create simple textwindow
simpleTextWindow =  new UnitWindow(this, getName() + " : Text View");
text = new JTextField(30);
simpleTextWindow.getContentPane().add(text, BorderLayout.CENTER);
simpleTextWindow.setLocation(getScreenX()+200, getScreenY()+50);
simpleTextWindow.pack();
}

if (!simpleTextWindow.isVisible())
simpleTextWindow.setVisible(true); */

        Parameter c = (Parameter) getInputAtNode(0);
        getTask().setParameter(ParamViewPanel.PARAM_VALUE, String.valueOf(c.getParameter()));

/*        if (c.getConnectedColor() !=null)
            text.setBackground(c.getConnectedColor());
        else
            text.setText(c.getParameter());*/
    }

    public void cleanUp() {
        super.cleanUp();
/*        if (simpleTextWindow !=null) {
            simpleTextWindow.setVisible(false);
            simpleTextWindow.dispose();
            } */
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Displays a Parameter in a Window";
    }

    /**
     * Initialses information specific to ParamView.
     */
    public void init() {
        super.init();

        setResizableInputs(false);
        setResizableOutputs(true);
    }


    /**
     * Reset's ParamView
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves ParamView's parameters to the parameter file.
     */
    public void saveParameters() {
    }

    /**
     * Loads ParamView's parameters of from the parameter file.
     */
    public void setParameter(String name, String value) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to ParamView, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "Parameter";
    }

    /**
     * @return a string containing the names of the types output from ParamView, each separated by a white space.
     */
    public String outputTypes() {
        return "none";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "Parameters.html";
    }
}