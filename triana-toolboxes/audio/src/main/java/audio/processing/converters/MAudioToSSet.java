package audio.processing.converters;


import org.trianacode.taskgraph.Unit;
import triana.types.SampleSet;
import triana.types.audio.MultipleAudio;

/**
 * Outputs MultipleAudio Type as SampleSet
 *
 * @author Eddie Al-Shakarchi
 * @version $Revision: 4052 $
 */
public class MAudioToSSet extends Unit {

    public void process() throws Exception {

        MultipleAudio input = (MultipleAudio) getInputAtNode(0);
        SampleSet output;

        if (input instanceof MultipleAudio) {
            MultipleAudio au = input;
            double[] data = (double[]) au.getDataArrayRealAsDoubles(0);
            output = new SampleSet(au.getAudioChannelFormat(0).getSamplingRate(), data);
            output(output);
        }
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
        setPopUpDescription("Outputs MultipleAudio Type as SampleSet");
        setHelpFileLocation("MAudioToSSet.html");
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
        // Insert code to clean-up MAudioToSSet (e.g. close open files)
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
        return new String[]{"triana.types.audio.MultipleAudio"};
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
        return new String[]{"SampleSet"};
    }

}



