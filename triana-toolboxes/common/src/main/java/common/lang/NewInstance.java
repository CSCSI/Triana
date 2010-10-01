package common.lang;


import org.trianacode.taskgraph.Unit;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


/**
 * @author Andrew Harrison
 * @version $Revision: 1.16 $
 * @created 03 Jun 2006
 * @date $Date: 2004/06/11 15:59:20 $ modified by $Author: spxinw $
 * @todo
 */
public class NewInstance extends Unit {
    /*
    * Called whenever there is data for the unit to process
    */

    public void process() {
        java.lang.Class input = (java.lang.Class) getInputAtNode(0);
        int ins = getInputNodeCount() - 1;
        try {
            if (ins == 0) {
                output(input.newInstance());
            } else {
                Object[] paramsObj = new Object[ins];
                Class[] paramCls = new Class[ins];
                for (int i = 0; i < paramsObj.length; i++) {
                    paramsObj[i] = getInputAtNode(i + 1);
                    paramCls[i] = paramsObj[i].getClass();
                }
                Constructor con = input.getConstructor(paramCls);
                Object out = con.newInstance(paramsObj);
                output(out);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        // Insert main algorithm for NewInstance
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
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        // Initialise parameter update policy and output policy
        setParameterUpdatePolicy(PROCESS_UPDATE);
        setOutputPolicy(COPY_OUTPUT);

        // Initialise pop-up description and help file location
        setPopUpDescription("creates an instance of a class given the class and optional parameters");
        setHelpFileLocation("NewInstance.html");
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
        // Insert code to clean-up NewInstance (e.g. close open files) 
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
        String[][] str = new String[0][0];
        /*str[0][1] = "java.lang.Class";
        str[1][1] = "java.lang.Object";*/
        return str;
    }

    /**
     * @return an array of the input types accepted by nodes not covered
     *         by getNodeInputTypes().
     */
    public String[] getInputTypes() {
        return new String[]{"java.lang.Class", "java.lang.Object"};
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



