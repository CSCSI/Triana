package signalproc.algorithms;

import org.trianacode.taskgraph.Unit;
import triana.types.ComplexSpectrum;
import triana.types.Spectrum;

/**
 * PwrSpect takes an input ComplexSpectrum and outputs a Spectrum consisting of the squared magnitude of the complex
 * elements of the input.
 *
 * @author Ian Taylor
 * @author B F Schutz
 * @version 2.0 10 April 2000
 */
public class PwrSpect extends Unit {

    /**
     * ********************************************* ** USER CODE of PwrSpect goes here    ***
     * *********************************************
     */
    public void process() {
        ComplexSpectrum input;

        input = (ComplexSpectrum) getInputAtNode(0);

        double[] power = new double[input.size()];
        double[] re = input.getDataReal();
        double[] im = input.getDataImag();
        for (int i = 0; i < input.size(); ++i) {
            power[i] = (re[i] * re[i]) +
                    (im[i] * im[i]);
        }

        Spectrum output = new Spectrum(input.isTwoSided(), input.isNarrow(), input.size(), input.size(),
                input.getFrequencyResolution(), input.getUpperFrequencyBound());
        output.setData(power);
        output(output);  // output the modified input
    }


    /**
     * Initialses information specific to PwrSpect.
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
     * Reset's PwrSpect public void reset() { super.reset(); }
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
     * @return a string containing the names of the types allowed to be input to PwrSpect, each separated by a white
     *         space.
     */
//    public String inputTypes() {
//        return "ComplexSpectrum";
//    }
//
//    /**
//     * @return a string containing the names of the types output from PwrSpect, each separated by a white space.
//     */
//    public String outputTypes() {
//        return "Spectrum";
//    }

    public String[] getInputTypes() {
        return new String[]{"triana.types.ComplexSpectrum"};
    }

    /**
     * @return an array of the output types for MyMakeCurve
     */
    public String[] getOutputTypes() {
        return new String[]{"triana.types.Spectrum"};
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Converts a complex frequency sampleset into a power spectrum";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "PwrSpect.html";
    }
}













