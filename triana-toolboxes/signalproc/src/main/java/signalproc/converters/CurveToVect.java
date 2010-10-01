package signalproc.converters;

import org.trianacode.taskgraph.Unit;
import triana.types.Curve;
import triana.types.VectorType;

/**
 * A CurveToVect unit to ..
 *
 * @author Ian Taylor
 * @version 1.0 alpha 13 May 1997
 */
public class CurveToVect extends Unit {

    /**
     * ********************************************* Main routine of CurveToVect which takes in a spectrum and converts
     * it into a vector data type for input to the Grapher. *********************************************
     */
    public void process() {
        Curve curv = (Curve) getInputAtNode(0);

        VectorType curv2D = convert(curv);

        output(curv2D);
    }


    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Converts Curve to a VectorType";
    }

    /**
     * Converts a Curve data type to a VectorType
     *
     * @return a VectorType
     */
    public static synchronized VectorType convert(Curve c) {

        VectorType v = new VectorType(c.getDataReal(1));

        String labelx = "x";
        String labely = "y";

        v.setX(c.getDataReal(0));

        v.setIndependentLabels(0, labelx);
        v.setDependentLabels(0, labely);

        return v;
    }

    /**
     * Initialses information specific to CurveToVect.
     */
    public void init() {
        super.init();

//        setResizableInputs(false);
//        setResizableOutputs(true);
        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);
    }


    /**
     * Resets CurveToVect
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves parameters.
     */
    public void saveParameters() {
    }

    /**
     * Used to set each of the parameters.
     */
    public void setParameter(String name, String value) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to CurveToVect, each separated by a white
     *         space.
     */
//    public String inputTypes() {
//        return "Curve";
//    }
//
//    /**
//     * @return a string containing the names of the types output from CurveToVect, each separated by a white space.
//     */
//    public String outputTypes() {
//        return "VectorType";
//    }

    public String[] getInputTypes() {
        return new String[]{"triana.types.Curve"};
    }

    public String[] getOutputTypes() {
        return new String[]{"triana.types.VectorType"};
    }


    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "Converters.html";
    }
}














