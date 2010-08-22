package common.buffer;


import org.trianacode.taskgraph.Unit;


/**
 * Buffers and outputs any data
 *
 * @author Ian Wang
 * @version $Revision: 2921 $
 */
public class Buffer extends Unit {

    // parameter data type definitions
    private boolean output;
    private int outputon;
    private int triggercount;

    /**
     * the buffered data
     */
    private Object buffer;
    private int tooutputcount;

    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {
        if (isInputAtNode(0)) {
            buffer = getInputAtNode(0);

            if (output) {
                output(buffer);
            }
        }

        for (int trigcount = 1; trigcount < getInputNodeCount(); trigcount++) {
            if (isInputAtNode(trigcount)) {
                getInputAtNode(trigcount);
                tooutputcount++;
            }
        }

        if (buffer != null) {
            outputBuffer();
        }

        setParameter("count", String.valueOf(triggercount));
    }

    private void outputBuffer() {
        while (tooutputcount > 0) {
            setParameter("count", String.valueOf(++triggercount));
            tooutputcount--;

            if (triggercount == outputon) {
                output(buffer);

                triggercount = 0;
                setParameter("count", String.valueOf(triggercount));
            }
        }
    }


    /**
     * Called when the unit is created. Initialises the unit's properties and parameters.
     */
    public void init() {
        super.init();

        // Initialise node properties
        setDefaultInputNodes(2);
        setMinimumInputNodes(2);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        // Initialise parameter update policy
        setParameterUpdatePolicy(PROCESS_UPDATE);
        setDefaultNodeRequirement(OPTIONAL);

        // Initialise pop-up description and help file location
        setPopUpDescription("Buffers data, and outputs when a trigger is received (on node 2)");
        setHelpFileLocation("Buffer.html");

        // Define initial value and type of parameters
        defineParameter("output", "false", USER_ACCESSIBLE);
        defineParameter("outputon", "1", USER_ACCESSIBLE);
        defineParameter("count", "0", TRANSIENT);

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "Trigger Count $title count Label 0\n";
        guilines += "Output On Trigger Count $title outputon TextField 1\n";
        guilines += "Output Initial Data $title output Checkbox false\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        // Set unit variables to the values specified by the parameters
        output = new Boolean((String) getParameter("output")).booleanValue();
        outputon = new Integer((String) getParameter("outputon")).intValue();
        triggercount = 0;
        tooutputcount = 0;
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up Buffer (e.g. close open files)
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        if (paramname.equals("output")) {
            output = new Boolean((String) value).booleanValue();
        }

        if (paramname.equals("outputon")) {
            outputon = new Integer((String) value).intValue();
        }

        if (paramname.equals("count")) {
            triggercount = new Integer((String) value).intValue();
        }
    }


    /**
     * @return an array of the input types for Buffer
     */
    public String[] getInputTypes() {
        return new String[]{"java.lang.Object"};
    }

    /**
     * @return an array of the output types for Buffer
     */
    public String[] getOutputTypes() {
        return new String[]{"java.lang.Object"};
    }

}



