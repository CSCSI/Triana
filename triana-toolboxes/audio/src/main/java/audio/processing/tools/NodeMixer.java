package audio.processing.tools;

import org.trianacode.taskgraph.Unit;
import triana.types.audio.MultipleAudio;

/**
 * Signal Mixing Unit which allows user to simultaneously play many audio files at one time. This unit takes n inputs
 * and gives m outputs however would usually be used to mix units to one output. The user can set a level of
 * normalisation of the summed audio signals - this avoids distortion caused when the sum of the mixed signals is
 * greater than the bandwidth available for 16-bit data.
 *
 * @author Eddie Al-Shakarchi
 * @version $Revision: 4052 $
 */

public class NodeMixer extends Unit {

    private float normal;
    NormaliserEffect normalise = null;

    /**
     * Called whenever there is data for the unit to process. This method creates gets the initial input stream and use as
     * comparison against other streams in order to find the biggest array - then sets the output stream to this size. The
     * individual streams are added together then normalised to avoid clipping caused when the sum of the mixed signals is
     * greater than the bandwidth available for 16-bit data.
     */

    public void process() throws Exception {

        // Get initial input stream and use as comparison against other streams
        // in order to find the biggest array - then

        MultipleAudio first = (MultipleAudio) getInputAtNode(0);

        Object buffer;
        buffer = first.getChannel(0);
        short[] test;
        test = (short[]) buffer;

        int maxArraySize = test.length;

        // Create multipleAudio unit for output
        MultipleAudio out = new MultipleAudio(1);
        int nodeCount = getInputNodeCount();

        MultipleAudio[] inputs = new MultipleAudio[nodeCount];
        inputs[0] = first;

        // Create array of multipleAudios from the nodes.This excludes the case of n = 0
        // as this has already been set

        for (int n = 1; n < nodeCount; n++) {
            inputs[n] = (MultipleAudio) getInputAtNode(n);
        }


        // Find longest array so that size of output array can be set
        for (int n = 0; n < nodeCount; n++) // For each node
        {
            for (int i = 0; i < inputs[n].getChannels(); i++) // For each channel in that node
            {
                buffer = inputs[n].getChannel(i);
                if (buffer instanceof short[]) // Shorts are used for 16bit data
                {
                    short[] inData;
                    inData = (short[]) buffer;

                    if (inData.length > maxArraySize) {
                        maxArraySize = inData.length;
                    }
                }
            }
        }

        System.out.println("maxArraysize = " + maxArraySize);

        Object in;
        short[] finalOutput = new short[maxArraySize]; // Create new arrays with a total size of
        int[] output = new int[maxArraySize];          // whatever length the biggest array had

        // Add the signals together
        for (int n = 0; n < nodeCount; n++) // For each node
        {
            for (int i = 0; i < inputs[n].getChannels(); i++) // For each channel in that node
            {
                in = inputs[n].getChannel(i);
                if (in instanceof short[]) // Shorts are used for 16bit data
                {
                    short[] inputData;
                    inputData = (short[]) in;

                    for (int j = 0; j < inputData.length; j++) { // For each element in array
                        output[j] += (int) (inputData[j]); // adds to output each time for each channel
                    }
                }
            }
        }

        // This is to normalise the output array. In normalisation the entire
        // array is scaled - as opposed to estimating how much to reduce the gain.
        // Avoids clipping the summed data.

        // Instantiates new normaliserEffect object
        if (normalise == null) {
            normalise = new NormaliserEffect(normal);
        }

        finalOutput = normalise.process(output);

        out.setChannel(0, finalOutput, first.getChannelFormat(0));
        output(out);
    }

    /**
     * Called when the unit is created. Initialises the unit's properties and parameters.
     */
    public void init() {
        super.init();

        // Initialise node properties
        setDefaultInputNodes(3);
        setMinimumInputNodes(0);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        // Initialise parameter update policy
        setParameterUpdatePolicy(IMMEDIATE_UPDATE);

        // Initialise pop-up description and help file location
        setPopUpDescription("");
        setHelpFileLocation("NodeMixer.html");

        // Define initial value and type of parameters
        defineParameter("normal", "100", USER_ACCESSIBLE);

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "Set level of normalisation $title normal Scroller 0 100 95 false\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        // Set unit variables to the values specified by the parameters
        normal = new Float((String) getParameter("normal")).floatValue();
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up NodeMixer (e.g. close open files)
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables
        if (paramname.equals("normal")) {
            normal = new Float((String) value).floatValue();
            if (normalise != null) {
                normalise.setNormalLevel(normal);
            }
        }
    }

    /**
     * @return an array of the input types for NodeMixer
     */
    public String[] getInputTypes() {
        return new String[]{"triana.types.audio.MultipleAudio"};
    }

    /**
     * @return an array of the output types for NodeMixer
     */
    public String[] getOutputTypes() {
        return new String[]{"triana.types.audio.MultipleAudio"};
    }

}



