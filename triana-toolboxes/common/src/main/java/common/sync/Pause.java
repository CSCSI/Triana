package common.sync;

import java.awt.Window;
import java.awt.event.ActionEvent;

import org.trianacode.gui.windows.ScrollerWindow;
import org.trianacode.taskgraph.Unit;
import triana.types.util.Str;


/**
 * A Pause unit to enter a delay in a triana network
 *
 * @author Ian Taylor
 * @version 1.0 alpha 21 Aug 2000
 */
public class Pause extends Unit {

    // some examples of parameters

    public double pause;

    /**
     * The UnitWindow for Pause
     */
    ScrollerWindow myWindow;
    String parameterName = "pause";
    double min = 0.0, max = 10.0;

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "A unit to wait a specified time before continuing";
    }

    /**
     * ********************************************* ** USER CODE of Pause goes here    ***
     * *********************************************
     */
    public void process() {
        Object input;
        input = getInputAtNode(0);

        try {
            Thread.sleep((long) (pause * 1000.0));
        }
        catch (InterruptedException e) {
        }

        output(input);
    }

    /**
     * Initialses information specific to Pause.
     */
    public void init() {
        super.init();

//        setResizableInputs(false);
//        setResizableOutputs(true);

/*        pause = 0.1;

        myWindow = new ScrollerWindow(this, "Pause (in seconds)  ?");
        myWindow.setParameterName(parameterName);        
        myWindow.setValues(0.0, 1.0, pause);
        myWindow.updateWidgets();*/


        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        String guilines = "";
        guilines += "Pause (secs) $title " + parameterName + " Scroller 0 1 0.1\n";
        setGUIBuilderV2Info(guilines);

    }


//    public void setGUIInformation() {
//        addGUILine("Pause (secs) $title " + parameterName + " Scroller 0 1 0.1");
//    }


    /**
     * Reset's Pause
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves Pause's parameters to the parameter file.
     */
//    public void saveParameters() {
//        saveParameter(parameterName, pause);
//        saveParameter("minimum", min);
//        saveParameter("maximum", max);
//    }

    /**
     * Loads Pause's parameters of from the parameter file.
     */
    public void setParameter(String name, String value) {
        if (name.equals(parameterName)) {
            pause = Str.strToDouble(value);
        } else if (name.equals("minimum")) {
            min = Str.strToDouble(value);
        } else if (name.equals("maximum")) {
            max = Str.strToDouble(value);
        }
    }

    /**
     * Used to update the widget in this unit's user interface that is used to control the given parameter name.
     */
    public void updateWidgetFor(String name) {
        myWindow.setValues(min, max, pause);
        myWindow.updateWidgets();
    }

    /**
     * over-rides the basic clean-up routine and destroys all windows relevant to Pause
     */
//    public void cleanUp() {
//        super.cleanUp();
//        if (myWindow != null) {
//            myWindow.dispose();
//        }
//    }


    /**
     * @return a string containing the names of the types allowed to be input to Pause, each separated by a white
     *         space.
     */
    public String[] getInputTypes() {
        return new String[]{"java.lang.Object"};
    }

    /**
     * @return a string containing the names of the types output from Compare, each separated by a white space.
     */
    public String[] getOutputTypes() {
        return new String[]{"java.lang.Object"};
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "Pause.html";
    }

    /**
     * @return Pause's parameter window sp that Triana can move and display it.
     */
    public Window getParameterWindow() {
        return myWindow;
    }


    /**
     * Captures the events thrown out by ScrollerWindow.
     */
//    public void actionPerformed(ActionEvent e) {
//        super.actionPerformed(e);   // we need this
//
//        //if (e.getSource() == myWindow.slider) {
//        //    pause = myWindow.getValue();
//        //    }
//    }
}


















