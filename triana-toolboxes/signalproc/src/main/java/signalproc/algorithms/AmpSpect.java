package signalproc.algorithms;

import org.trianacode.taskgraph.Unit;
import triana.types.ComplexSpectrum;
import triana.types.Spectrum;

/**
 * AmpSpect computes the amplitude spectrum from an input ComplexSpectrum. The amplitude spectrum is the absolute
 * magnitude of the input complex numbers. The units outputs a Spectrum containing these values.
 *
 * @author Ian Taylor
 * @version 1.0 7 March 1997
 */
public class AmpSpect extends Unit {

    /**
     * ********************************************* ** USER CODE of AmpSpect goes here    ***
     * *********************************************
     */
    public void process() {
        ComplexSpectrum input;

        input = (ComplexSpectrum) getInputAtNode(0);

        double[] power = new double[input.size()];
        double[] re = input.getDataReal();
        double[] im = input.getDataImag();
        for (int i = 0; i < input.size(); ++i) {
            power[i] = Math.sqrt(re[i] * re[i]) +
                    (im[i] * im[i]);
        }

        Spectrum output = new Spectrum(input.isTwoSided(), input.isNarrow(), input.size(), input.size(),
                input.getFrequencyResolution(), input.getUpperFrequencyBound());
        output.setData(power);
        output(output);  // output the modified input
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Converts a complex frequency series into an amplitude spectrum";
    }

    /**
     * Initialses information specific to AmpSpect.
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
     * Reset's AmpSpect public void reset() { super.reset(); }
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
     * @return a string containing the names of the types allowed to be input to AmpSpect, each separated by a white
     *         space.
     */
    public String[] getInputTypes() {
        return new String[]{"triana.types.ComplexSpectrum"};
    }

    public String[] getOutputTypes() {
        return new String[]{"triana.types.Spectrum"};
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "AmpSpect.html";
    }
}













