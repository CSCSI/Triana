package common.lang;

import org.trianacode.taskgraph.Unit;


/**
 * @author Andrew Harrison
 * @version $Revision: 1.16 $
 * @created 03 Jun 2006
 * @date $Date: 2004/06/11 15:59:20 $ modified by $Author: spxinw $
 * @todo
 */
public class IfElse extends Unit {


    /*
    * Called whenever there is data for the unit to process
    */

    public void process() throws Exception {
        java.lang.Boolean test = (java.lang.Boolean) getInputAtNode(0);
        Object in1 = getInputAtNode(1);
        Object in2 = null;
        if (getInputNodeCount() > 2)
            in2 = getInputAtNode(2);
        if (test.booleanValue())
            output(in1);
        else if (in2 != null)
            output(in2);

        // Insert main algorithm for IfElse
    }


    /**
     * Called when the unit is created. Initialises the unit's properties and
     * parameters.
     */
    public void init() {
        super.init();

        // Initialise node properties
        setDefaultInputNodes(2);
        setMinimumInputNodes(2);
        setMaximumInputNodes(3);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        // Initialise parameter update policy and output policy
        setParameterUpdatePolicy(PROCESS_UPDATE);
        setOutputPolicy(CLONE_MULTIPLE_OUTPUT);

        // Initialise pop-up description and help file location
        setPopUpDescription("If the input value is true, then this returns the value from the 'if' input (node 2)." +
                "Otherwise it outputs the 'else' input (node 3).");
        setHelpFileLocation("IfElse.html");
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values
     * specified by the parameters.
     */
    public void reset() {
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up IfElse (e.g. close open files) 
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables 
    }


    /**
     * @return an array of the types accepted by each input node. For node indexes
     *         not covered the types specified by getInputTypes() are assumed.
     */
    public String[][] getNodeInputTypes() {
        return new String[0][0];
    }

    /**
     * @return an array of the input types accepted by nodes not covered
     *         by getNodeInputTypes().
     */
    public String[] getInputTypes() {
        return new String[]{"java.lang.Boolean", "java.lang.Object", "java.lang.Object"};
    }


    /**
     * @return an array of the types output by each output node. For node indexes
     *         not covered the types specified by getOutputTypes() are assumed.
     */
    public String[][] getNodeOutputTypes() {
        return new String[0][0];
    }

    /**
     * @return an array of the input types output by nodes not covered
     *         by getNodeOutputTypes().
     */
    public String[] getOutputTypes() {
        return new String[]{"java.lang.Object"};
    }

}



