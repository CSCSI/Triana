package common.array;


import org.trianacode.taskgraph.Unit;
import triana.types.clipins.SequenceClipIn;


/**
 * Turns an array into a stream of data
 *
 * @author Ian Wang
 * @version $Revision: 2921 $
 */
public class ArrayToStream extends Unit {


    /*
    * Called whenever there is data for the unit to process
    */

    public void process() throws Exception {
        java.lang.Object[] input = (java.lang.Object[]) getInputAtNode(0);
        String groupid = getToolName() + ":" + System.currentTimeMillis() + ":" + ((int) Math.random() * 1000000);

        for (int count = 0; count < input.length; count++) {
            SequenceClipIn clipin = new SequenceClipIn(groupid, count, input.length);
            clipin.setComponentType(input.getClass().getComponentType().getName());

            putClipIn(SequenceClipIn.SEQUENCE_CLIPIN_TAG, clipin);
            output(input[count]);
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
        setPopUpDescription("Turns an array into a stream of data");
        setHelpFileLocation("ArrayToStream.html");
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
        // Insert code to clean-up ArrayToStream (e.g. close open files) 
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
        return new String[]{"java.lang.Object[]"};
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



