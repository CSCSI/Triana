package common.logic;

import java.awt.Window;
import java.awt.event.ActionEvent;

import org.trianacode.taskgraph.Unit;
import triana.types.Arithmetic;
import triana.types.Const;

/**
 * A Compatible unit test two inputs for compatibility, as defined by GraphType.
 *
 * @author B.F. Schutz
 * @version 2.0 20 August 2000
 */

public class Compatible extends Unit {

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

        // Initialise node properties
        setDefaultInputNodes(2);
        setMinimumInputNodes(2);
        setMaximumInputNodes(2);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);
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
    public String[] getInputTypes() {
        return new String[]{"triana.types.GraphType", "triana.types.Const"};
    }

    /**
     * @return a string containing the names of the types output from Compare, each separated by a white space.
     */
    public String[] getOutputTypes() {
        return new String[]{"triana.types.Const"};
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
//    public void actionPerformed(ActionEvent e) {
//        super.actionPerformed(e);   // we need this
//
//    }
}

















