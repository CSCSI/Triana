package signalproc.converters;

import org.trianacode.taskgraph.Unit;
import triana.types.ComplexSpectrum;
import triana.types.VectorType;

/**
 * A CSpecToRaw unit to ..
 *
 * @author Ian Taylor
 * @version 1.0 alpha 21 May 1997
 */
public class CSpecToVect extends Unit {

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Converts a ComplexSpectrum into VectorType";
    }

    /**
     * CSpecToRaw main routine.
     */
    public void process() {
        ComplexSpectrum wave = (ComplexSpectrum) getInputAtNode(0);

        double d[] = new double[wave.size() * 2];

        for (int i = 0; i < wave.size(); ++i) {
            d[i] = wave.real[i];
        }

        for (int i = 0; i < wave.size(); ++i) {
            d[i + wave.size()] = wave.imag[i];
        }

        output(new VectorType(d));
    }


    /**
     * Initialses information specific to SSetToRaw.
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
     * Reset's the unit
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
     * @return a string containing the names of the types allowed to be input to this unit, each separated by a white
     *         space.
     */
//    public String inputTypes() {
//        return "ComplexSpectrum";
//    }
//
//    /**
//     * @return a string containing the names of the types output from this unit, each separated by a white space.
//     */
//    public String outputTypes() {
//        return "VectorType";
//    }

    public String[] getInputTypes() {
        return new String[]{"triana.types.ComplexSpectrum"};
    }

    public String[] getOutputTypes() {
        return new String[]{"triana.types.VectorType"};
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "converters.html";
    }
}














