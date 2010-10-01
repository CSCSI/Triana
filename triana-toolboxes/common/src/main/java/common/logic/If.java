package common.logic;

import org.trianacode.gui.windows.ErrorDialog;
import org.trianacode.taskgraph.Unit;
import triana.types.Const;
import triana.types.util.Str;

import java.awt.event.ActionEvent;

/**
 * If takes two inputs: the first is the test value (Const) and the second is the data that gets routed to either the
 * first output or the second output according to whether the const value is larger or smaller than the test.  The test
 * value is a parameter.
 *
 * @author B.F. Schutz
 * @version 2.0 20 August 2000
 */
public class If extends Unit {
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
        //setOutputType(inputClass);

        boolean sendToFirst = false;

        if (!(input instanceof Const)) {
            new ErrorDialog(null, "First input to If must be a " +
                    "Constant!!!");
            //stop();
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

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        String guilines = "";
        guilines += "Test value $title threshold Scroller -10 10 0.0\n";
        setGUIBuilderV2Info(guilines);

//        myWindow = new ScrollerWindow(this, "Set test value.");
//        myWindow.setValues(0.0, 10, threshold);

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
//    public void saveParameters() {
//        saveParameter("threshold", threshold);
//    }

    /**
     * Loads If's parameters of from the parameter file.
     */
    public void parameterUpdate(String name, String value) {
        if (name.equals("threshold")) {
            threshold = Str.strToDouble(value);
        }
    }

    public String[] getInputTypes() {
        return new String[]{"triana.types.TrianaType", "triana.types.Const"};
    }

    /**
     * @return a string containing the names of the types output from Compare, each separated by a white space.
     */
    public String[] getOutputTypes() {
        return new String[]{"triana.types.TrianaType"};
    }

    /**
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
        //super.actionPerformed(e);   // we need this

        //if (e.getSource() == myWindow.slider) {
        //    threshold = myWindow.getValue();
        //    }
    }
}

















