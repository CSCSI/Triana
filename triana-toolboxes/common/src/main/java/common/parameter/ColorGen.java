package common.parameter;

import java.awt.Color;
import java.awt.event.ActionEvent;

import org.trianacode.taskgraph.Unit;
import triana.types.Parameter;
import triana.types.util.Str;
import triana.types.util.StringSplitter;

/**
 * A ColorGen unit to generate a color parameter type!!
 *
 * @author ian
 * @version 2 Final 15 Aug 2000
 */
public class ColorGen extends Unit {
    /**
     * The UnitPanel for ColorGen
     */
    //ColorGenWindow myPanel;
    Color color = Color.white;

    /**
     * ********************************************* ** USER CODE of ColorGen goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        output(new Parameter(color));
    }

    /**
     * Initialses information specific to ColorGen.
     */
    public void init() {
        super.init();

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        //myPanel = new ColorGenWindow();
        //myPanel.setObject(this);
        //myPanel.setConnectedColor(color);
    }

    /**
     * Reset's ColorGen
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

//    /**
//     * Called when the start button is pressed within the MainTriana Window
//     */
//    public void starting() {
//        super.starting();
//    }

//    /**
//     * Saves ColorGen's parameters.
//     */
//    public void saveParameters() {
//        saveParameter("color", toString(color));
//    }

    /**
     * Used to set each of ColorGen's parameters.
     */
    public void parameterUpdate(String name, String value) {
        if (name.equals("color")) {
            color = strToColor(value);
        }
    }

    /**
     * Used to update the widget in this unit's user interface that is used to control the given parameter name.
     */
    public void updateWidgetFor(String name) {
        //myPanel.setConnectedColor(color);
    }


    public String[] getInputTypes() {
        return new String[]{};
    }

    /**
     * @return a string containing the names of the types output from Compare, each separated by a white space.
     */
    public String[] getOutputTypes() {
        return new String[]{"triana.types.Parameter"};
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Put ColorGen's brief description here";
    }

    /**
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "ColorGen.html";
    }

    /**
     * @return ColorGen's parameter panel 
     */
/*    public UnitPanel getParameterPanel() {
        return myPanel;
        }*/

    /**
     * Captures the events thrown out by ColorGenWindow.
     */
//    public void actionPerformed(ActionEvent e) {
//        //super.actionPerformed(e);   // we need this
//    }

    public final static String toString(Color col) {
        return String.valueOf(col.getRed()) + ' ' + String.valueOf(col.getGreen()) + ' ' + String.valueOf(col.getBlue());
    }

    public final static Color strToColor(String value) {
        StringSplitter str = new StringSplitter(value);
        return new Color((int) Str.strToDouble(str.at(0)),
                (int) Str.strToDouble(str.at(1)),
                (int) Str.strToDouble(str.at(2)));
    }

}




