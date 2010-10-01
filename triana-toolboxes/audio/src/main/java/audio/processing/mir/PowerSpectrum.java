package audio.processing.mir;

import org.trianacode.taskgraph.Unit;
import triana.types.ComplexSpectrum;
import triana.types.Spectrum;

/**
 * @author Eddie Al-Shakarchi
 * @version $Revision: 2915 $
 */
public class PowerSpectrum extends Unit {

    /*
    * Called whenever there is data for the unit to process
    */

    public void process() throws Exception {
        ComplexSpectrum input;

        input = (ComplexSpectrum) getInputAtNode(0);

        double[] power = new double[input.size()];
        double[] re = input.getDataReal();
        double[] im = input.getDataImag();
        for (int i = 0; i < input.size(); ++i) {
            power[i] = (re[i] * re[i]) + (im[i] * im[i]);
        }

        Spectrum output = new Spectrum(input.isTwoSided(), input.isNarrow(), input.size(), input.size(),
                input.getFrequencyResolution(), input.getUpperFrequencyBound());
        output.setData(power);
        output(output);  // output the modified input
    }


    /**
     * Called when the unit is created. Initialises the unit's properties and parameters.
     */
    public void init() {
        super.init();

        // Initialise node properties
        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(1);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        // Initialise parameter update policy and output policy
        setParameterUpdatePolicy(PROCESS_UPDATE);
        setOutputPolicy(CLONE_MULTIPLE_OUTPUT);

        // Initialise pop-up description and help file location
        setPopUpDescription("");
        setHelpFileLocation("PowerSpectrum.html");
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up PowerSpectrum (e.g. close open files) 
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables 
    }


    /**
     * @return an array of the types accepted by each input node. For node indexes not covered the types specified by
     *         getInputTypes() are assumed.
     */
    public String[][] getNodeInputTypes() {
        return new String[0][0];
    }

    /**
     * @return an array of the input types accepted by nodes not covered by getNodeInputTypes().
     */
    public String[] getInputTypes() {
        return new String[]{"java.lang.Object"};
    }


    /**
     * @return an array of the types output by each output node. For node indexes not covered the types specified by
     *         getOutputTypes() are assumed.
     */
    public String[][] getNodeOutputTypes() {
        return new String[0][0];
    }

    /**
     * @return an array of the input types output by nodes not covered by getNodeOutputTypes().
     */
    public String[] getOutputTypes() {
        return new String[]{"java.lang.Object"};
    }

}



