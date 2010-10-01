package signalproc.algorithms;

import org.trianacode.taskgraph.Unit;
import triana.types.VectorType;
import triana.types.util.Str;


/**
 * A Shift unit to cyclicly shift a VectorType data set to the right.
 *
 * @author B F Schutz
 * @version 1.0 05 Mar 2001
 */
public class Shift extends Unit {

    int shift = 1;


    /**
     * ********************************************* ** USER CODE of Shift goes here    ***
     * *********************************************
     */
    public void process() throws Exception {

        VectorType input = (VectorType) getInputAtNode(0);

        if (shift != 0) {
            shiftData(input, shift);
        }

        output(input);

    }


    public static void shiftData(VectorType input, int shift) {

        cyclicShift(input.getDataReal(), shift);

        if (input.isDependentComplex(0)) {
            cyclicShift(input.getDataImag(), shift);
        }

    }

    public static void cyclicShift(double[] data, int shift) {

        int len = data.length;
        shift = shift % len;
        double[] overhang;
        int first;

        if (shift == 0) {
            return;
        } else if (shift > 0) {
            overhang = new double[shift];
            first = len - shift;
            System.arraycopy(data, first, overhang, 0, shift);
            System.arraycopy(data, 0, data, shift, first);
            System.arraycopy(overhang, 0, data, 0, shift);
        } else if (shift < 0) {
            overhang = new double[-shift];
            first = len + shift;
            System.arraycopy(data, 0, overhang, 0, -shift);
            System.arraycopy(data, -shift, data, 0, first);
            System.arraycopy(overhang, 0, data, first, -shift);
        }
    }


    /**
     * Initialses information specific to Shift.
     */
    public void init() {
        super.init();

//        setUseGUIBuilder(true);
//
//        setRequireDoubleInputs(false);
//        setCanProcessDoubleArrays(false);
//
//        setResizableInputs(false);
//        setResizableOutputs(true);

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        String guilines = "";
        guilines += "Number of elements to shift to the right $title shift IntScroller -100 100 0\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format.
     */
//    public void setGUIInformation() {
//        addGUILine("Number of elements to shift to the right $title shift IntScroller -100 100 0");
//    }

    /**
     * Called when the reset button is pressed within the MainTriana Window
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

    /**
     * Called when the start button is pressed within the MainTriana Window
     */
//    public void starting() {
//        super.starting();
//    }
//
//    /**
//     * Saves Shift's parameters.
//     */
//    public void saveParameters() {
//        saveParameter("shift", shift);
//    }
//

    /**
     * Used to set each of Shift's parameters.
     */
    public void parameterUpdate(String name, Object value) {
        //updateGUIParameter(name, value);

        if (name.equals("shift")) {
            shift = Str.strToInt((String) value);
        }
    }

    /**
     * Don't need to use this for GUI Builder units as everthing is updated by triana automatically
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to Shift, each separated by a white
     *         space.
     */
//    public String inputTypes() {
//        return "VectorType";
//    }
//
//    /**
//     * @return a string containing the names of the types output from Shift, each separated by a white space.
//     */
//    public String outputTypes() {
//        return "VectorType";
//    }
    
    public String[] getInputTypes() {
        return new String[]{"triana.types.VectorType"};
    }

    /**
     * @return an array of the output types for MyMakeCurve
     */
    public String[] getOutputTypes() {
        return new String[]{"triana.types.VectorType"};
    }


    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Cyclic shift of data to the right";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "Shift.html";
    }
}




