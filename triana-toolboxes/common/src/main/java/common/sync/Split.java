package common.sync;


import org.trianacode.taskgraph.Unit;


/**
 * Splits an input stream into multiple output streams.
 *
 * @author Ian Wang
 * @version $Revision: 2921 $
 */
public class Split extends Unit {

    // parameter data type definitions
    private String order;
    private int ptr = 0;

    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {
        Object input = getInputAtNode(0);

        if (order.trim().equals("")) {
            handleNoOrderOutput(input);
        } else {
            handleOrderOutput(input);
        }
    }

    private void handleNoOrderOutput(Object input) {
        output(input);
    }

    private void handleOrderOutput(Object input) {
        if (ptr >= order.length()) {
            ptr = 0;
        }

        char nodechar = order.charAt(ptr);

        if (nodechar == '+') {
            nodechar = order.charAt(--ptr);
        }

        try {
            int nodeidx = Integer.parseInt(String.valueOf(nodechar));

            if (nodeidx - 1 > getOutputNodeCount()) {
                notifyError("Node Index Out Of Range" + nodeidx);
            } else {
                outputAtNode(nodeidx - 1, input);
                ptr++;
            }
        } catch (NumberFormatException except) {
            notifyError("Invalid Node Index: " + nodechar);
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

        setDefaultOutputNodes(2);
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        // Initialise parameter update policy
        setParameterUpdatePolicy(PROCESS_UPDATE);

        // Initialise pop-up description and help file location
        setPopUpDescription("Splits an input stream into multiple output streams");
        setHelpFileLocation("Split.html");

        // Define initial value and type of parameters
        defineParameter("order", "", USER_ACCESSIBLE);

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "Split Node Order (e.g. 1212 or 12+) $title order TextField \n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        // Set unit variables to the values specified by the parameters
        order = (String) getParameter("order");
        ptr = 0;
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up Split (e.g. close open files) 
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables 
        if (paramname.equals("order")) {
            order = (String) value;
            ptr = 0;
        }
    }


    /**
     * @return an array of the input types for Split
     */
    public String[] getInputTypes() {
        return new String[]{"java.lang.Object"};
    }

    /**
     * @return an array of the output types for Split
     */
    public String[] getOutputTypes() {
        return new String[]{"java.lang.Object"};
    }

}



