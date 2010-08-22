package common.stream;


import org.trianacode.taskgraph.Unit;
import triana.types.clipins.SequenceClipIn;


/**
 * Adds sequence information to a data stream
 *
 * @author Ian Wang
 * @version $Revision: 2921 $
 */
public class Sequence extends Unit {

    // parameter data type definitions
    private boolean override;
    private boolean fixed;
    private int seqlength;

    private int count = 0;
    private String groupid;


    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {
        Object input = getInputAtNode(0);

        if (override || (!isClipInName(SequenceClipIn.SEQUENCE_CLIPIN_TAG))) {
            SequenceClipIn clipin;
            SequenceClipIn subclipin = null;

            if (isClipInName(SequenceClipIn.SEQUENCE_CLIPIN_TAG)) {
                subclipin = (SequenceClipIn) getClipIn(SequenceClipIn.SEQUENCE_CLIPIN_TAG);
            }

            if (count == 0) {
                groupid = getToolName() + ":" + System.currentTimeMillis() + ":" + ((int) Math.random() * 1000000);
            }

            if (fixed) {
                if (seqlength <= 0) {
                    notifyError("Invalid Fixed Sequenece Length: " + seqlength);
                }

                clipin = new SequenceClipIn(groupid, count++, seqlength);

                if (count >= seqlength) {
                    count = 0;
                }
            } else {
                clipin = new SequenceClipIn(groupid, count++);
            }

            clipin.setSubSequenceClipIn(subclipin);

            putClipIn(SequenceClipIn.SEQUENCE_CLIPIN_TAG, clipin);
        }

        output(input);
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
        setPopUpDescription("Adds sequence information to a data stream");
        setHelpFileLocation("Sequence.html");

        // Define initial value and type of parameters
        defineParameter("override", "false", USER_ACCESSIBLE);
        defineParameter("fixed", "false", USER_ACCESSIBLE);
        defineParameter("seqlength", "1", USER_ACCESSIBLE);

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "Override existing sequence info $title override Checkbox false\n";
        guilines += "Use fixed sequence length $title fixed Checkbox false\n";
        guilines += "Sequence length $title seqlength TextField 1\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        // Set unit variables to the values specified by the parameters
        override = new Boolean((String) getParameter("override")).booleanValue();
        fixed = new Boolean((String) getParameter("fixed")).booleanValue();
        seqlength = new Integer((String) getParameter("seqlength")).intValue();

        count = 0;
        groupid = null;
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up Sequence (e.g. close open files) 
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables 
        if (paramname.equals("override")) {
            override = new Boolean((String) value).booleanValue();
        }

        if (paramname.equals("fixed")) {
            fixed = new Boolean((String) value).booleanValue();
        }

        if (paramname.equals("seqlength")) {
            seqlength = new Integer((String) value).intValue();
        }
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



