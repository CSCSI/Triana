package common.conzt;

import org.trianacode.taskgraph.Unit;

/**
 * A ConstView unit to ..
 *
 * @author Ian Taylor
 * @version 2.0 07 Aug 2000
 */
public class ConstView extends Unit {

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

//    public void cleanUp() {
//        super.cleanUp();
//    }

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

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

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
    public String[] getInputTypes() {
        return new String[]{"triana.types.Number", "triana.types.Const"};
    }

    /**
     * @return a string containing the names of the types output from Compare, each separated by a white space.
     */
    public String[] getOutputTypes() {
        return new String[]{};
    }
    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "ConstView.html";
    }

}













