package signalproc.algorithms;

import org.trianacode.taskgraph.Unit;
import triana.types.ComplexSampleSet;
import triana.types.ComplexSpectrum;

/**
 * A Conjugate unit to take the complex conjugate of the input
 *
 * @author ian
 * @version 1.0 alpha 02 Apr 1997
 */
public class Conjugate extends Unit {


    /**
     * Conjugate takes a complex input i.e. either a ComplexSpectrum or a ComplexSampleSet and outputs its complex
     * conjugate.
     */
    public void process() {
        Object input;

        input = getInputAtNode(0);

        if (input instanceof ComplexSpectrum) {
            for (int i = 0; i < ((ComplexSpectrum) input).size(); ++i) {
                ((ComplexSpectrum) input).imag[i] = -((ComplexSpectrum) input).imag[i];
            }
        } else {
            for (int i = 0; i < ((ComplexSampleSet) input).size(); ++i) {
                ((ComplexSampleSet) input).imag[i] = -((ComplexSampleSet) input).imag[i];
            }
        }

        output(input);  // change result to incorporate other inputs
    }


    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Performs the Complex Conjugate on the complex input series";
    }

    /**
     * Initialses information specific to Conjugate.
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
     * Reset's Conjugate public void reset() { super.reset(); }
     * <p/>
     * /** Saves parameters
     */
    public void saveParameters() {
    }

    /**
     * Sets the parameters
     */
    public void setParameter(String name, String value) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to Conjugate, each separated by a white
     *         space.
     */

    public String[] getInputTypes() {
        return new String[]{"triana.types.ComplexSampleSet", "triana.types.ComplexSpectrum"};
    }

    public String[] getOutputTypes() {
        return new String[]{"triana.types.ComplexSampleSet", "triana.types.ComplexSpectrum"};
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "Conjugate.html";
    }
}













