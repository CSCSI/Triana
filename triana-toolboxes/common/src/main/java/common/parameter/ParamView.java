package common.parameter;

import javax.swing.JTextField;
import org.trianacode.taskgraph.Unit;
import triana.types.Parameter;


/**
 * A ParamView unit to ..
 *
 * @author Ian Taylor
 * @version 1.0 alpha 07 Aug 1997
 */
public class ParamView extends Unit {
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

//    public void cleanUp() {
//        //super.cleanUp();
///*        if (simpleTextWindow !=null) {
//            simpleTextWindow.setVisible(false);
//            simpleTextWindow.dispose();
//            } */
//    }

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

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(1);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);
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
    public String[] getInputTypes() {
        return new String[]{"triana.types.Parameter"};
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
        return "Parameters.html";
    }
}
