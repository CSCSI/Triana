package common.sync;


import org.trianacode.taskgraph.Unit;


/**
 * Merges multiple streams of data
 *
 * @author Ian Wang
 * @version $Revision: 2921 $
 */
public class Merge extends Unit {

    // parameter data type definitions
    private String order = "";
    private int ptr = 0;


    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {
        boolean output = true;

        while (output) {
            output = false;

            if (order.trim().equals("")) {
                output = handleNoOrderOutput();
            } else {
                output = handleOrderOutput();
            }
        }
    }

    private boolean handleNoOrderOutput() {
        boolean output = false;

        for (int count = 0; count < getInputNodeCount(); count++) {
            if (isInputAtNode(count)) {
                output(getInputAtNode(count));
                output = true;
            }
        }

        return output;
    }

    private boolean handleOrderOutput() {
        if (ptr >= order.length()) {
            ptr = 0;
        }

        char nodechar = order.charAt(ptr);

        try {
            int nodeidx = Integer.parseInt(String.valueOf(nodechar));

            if (nodeidx - 1 > getInputNodeCount()) {
                notifyError("Node Index Out Of Range" + nodeidx);
            } else if (isInputAtNode(nodeidx - 1)) {
                output(getInputAtNode(nodeidx - 1));
                ptr++;
                return true;
            }
        } catch (NumberFormatException except) {
            notifyError("Invalid Node Index: " + nodechar);
        }

        return false;
    }

    /**
     * Called when the unit is created. Initialises the unit's properties and parameters.
     */
    public void init() {
        super.init();

        // Initialise node properties
        setDefaultInputNodes(2);
        setMinimumInputNodes(0);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        // Initialise parameter update policy
        setParameterUpdatePolicy(PROCESS_UPDATE);
        setDefaultNodeRequirement(OPTIONAL);

        // Initialise pop-up description and help file location
        setPopUpDescription("Merges multiple streams of data");
        setHelpFileLocation("Merge.html");

        // Define initial value and type of parameters
        defineParameter("order", "", USER_ACCESSIBLE);

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "Merge Node Order (e.g. 1212) $title order TextField \n";
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
        // Insert code to clean-up Merge (e.g. close open files) 
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
     * @return an array of the input types for Merge
     */
    public String[] getInputTypes() {
        return new String[]{"java.lang.Object"};
    }

    /**
     * @return an array of the output types for Merge
     */
    public String[] getOutputTypes() {
        return new String[]{"java.lang.Object"};
    }

}



