package math.functions;

import org.trianacode.taskgraph.Unit;
import triana.types.VectorType;
import triana.types.util.Str;

/**
 * A PhaseCont unit to smooth out jumps of 2 Pi in an inut sequence of real data representing a phase angle, so that the
 * angle moves smoothly to values larger than Pi or smaller than -Pi. </p><p> Most functions constrain the phase of a
 * complex number or the angle of a vector in the 2D plane to a principal range of (-Pi, Pi), for example the function
 * atan2. This Unit takes the output of such angle-finders and makes it continuous outside the principal range by
 * looking for big discontinuities (where the phase jumps from near -Pi to near +Pi, for example, and adds an
 * appropriate multiple of 2*Pi to the angle to eliminate the jump. </p><p> PhaseCont does not find the original phase.
 * It takes output from a Unit that does and smooths the phase. PhaseCont should only be used when one expects a
 * smoothly changing phase from one element to the next; it will not make sensible answers when applied, for example, to
 * phase noise. </p><p> This method can be used to find winding numbers of curves around the origin.
 *
 * @author B F Schutz
 * @version 1.1 13 January 2001
 */
public class PhaseCont extends Unit {

    boolean carryOver = true;

    VectorType input, output;
    double jump, lastPhase;
    double twoPi = 2.0 * Math.PI;


    /**
     * ********************************************* ** USER CODE of PhaseCont goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        input = (VectorType) getInputAtNode(0);
        output = (VectorType) input;
        //setOutputType(output.getClass());

        double[] outputdata = output.getData();

        double lastAngle, thisAngle, diffAngle;
        if (carryOver) {
            lastAngle = lastPhase;
            thisAngle = outputdata[0];
            diffAngle = thisAngle - lastAngle;
            if (diffAngle > Math.PI) {
                jump -= twoPi;
            } else if (diffAngle < -Math.PI) {
                jump += twoPi;
            }
            outputdata[0] = thisAngle + jump;
            lastAngle = thisAngle;
        } else {
            lastAngle = outputdata[0];
            jump = 0.0;
        }

        for (int i = 1; i < outputdata.length; i++) {
            thisAngle = outputdata[i];
            diffAngle = thisAngle - lastAngle;
            if (diffAngle > Math.PI) {
                jump -= twoPi;
            } else if (diffAngle < -Math.PI) {
                jump += twoPi;
            }
            outputdata[i] = thisAngle + jump;
            lastAngle = thisAngle;
        }

        lastPhase = lastAngle;

        output(output);

    }


    /**
     * Initialses information specific to PhaseCont.
     */
    public void init() {
        super.init();

//        setUseGUIBuilder(true);
//
//        setResizableInputs(false);
//        setResizableOutputs(true);
//        // This is to ensure that we receive arrays containing double-precision numbers
//        setRequireDoubleInputs(true);
//        setCanProcessDoubleArrays(true);
        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(1);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        jump = 0.0;
        lastPhase = 0.0;
                
        String guilines = "";
        guilines += "Continue phase from one input data set to next? $title carryOver Checkbox true\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format.
     */
//    public void setGUIInformation() {
//        addGUILine("Continue phase from one input data set to next? $title carryOver Checkbox true");
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
//     * Saves PhaseCont's parameters.
//     */
//    public void saveParameters() {
//        saveParameter("carryOver", carryOver);
//    }


    /**
     * Used to set each of PhaseCont's parameters.
     */
    public void parameterUpdate(String name, Object value) {
        //updateGUIParameter(name, value);

        if (name.equals("carryOver")) {
            carryOver = Str.strToBoolean((String) value);
        }
    }

    /**
     * Don't need to use this for GUI Builder units as everthing is updated by triana automatically
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to PhaseCont, each separated by a white
     *         space.
     */
    public String[] getInputTypes() {
        return new String[]{"triana.types.VectorType"};
    }

    public String[] getOutputTypes() {
        return new String[]{"triana.types.VectorType"};
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Make phase angle continuous by allowing angles outside (-Pi, Pi).";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "PhaseCont.html";
    }
}




