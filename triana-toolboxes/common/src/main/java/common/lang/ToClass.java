package common.lang;

import org.trianacode.taskgraph.Unit;
import org.trianacode.taskgraph.tool.ClassLoaders;


/**
 * tries to convert a String or Object to a Class
 *
 * @author Andrew Harrison
 * @version $Revision: 1.16 $
 * @created 03 Jun 2006
 * @date $Date: 2004/06/11 15:59:20 $ modified by $Author: spxinw $
 * @todo
 */
public class ToClass extends Unit {


    /*
    * Called whenever there is data for the unit to process
    */

    public void process() throws Exception {
        Object o = getInputAtNode(0);
        if (o instanceof String) {
            try {
                Class cls = ClassLoaders.forName((String) o);
                output(cls);
            } catch (Exception e) {
                // if we can't find it we return it as a string
                output(String.class);
            }
        } else {
            output(o.getClass());
        }
    }


    /**
     * Called when the unit is created. Initialises the unit's properties and
     * parameters.
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
        setPopUpDescription("tries to convert a String or Object to a Class");
        setHelpFileLocation("ToClass.html");
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
        // Insert code to clean-up ToClass (e.g. close open files) 
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
        return new String[]{"java.lang.Object"};
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
        return new String[]{"java.lang.Class"};
    }

}



