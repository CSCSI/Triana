package common.buffer;


import java.util.Vector;

import org.trianacode.taskgraph.Unit;
import triana.types.Const;
import triana.types.SampleSet;


/**
 * Buffers and outputs any data
 *
 * @author Ian Wang
 * @version $Revision: 2921 $
 */
public class ConstBuffer extends Unit {

    // parameter data type definitions
    private int number;
    private Vector buffer;
    SampleSet output;

    /**
     * the buffered data
     */

    /*
     * Called whenever there is data for the unit to process
     */
    public void process() throws Exception {

        if (isInputAtNode(0)) {

            buffer.remove(0);
            buffer.add((Const) getInputAtNode(0));

            output = new SampleSet(1, number);

            for (int i = 0; i < buffer.size(); ++i) {
                output.data[i] = Double.parseDouble((buffer.elementAt(i)).toString());
            }

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

        // Initialise parameter update policy
        setParameterUpdatePolicy(PROCESS_UPDATE);
        setDefaultNodeRequirement(OPTIONAL);

        // Initialise pop-up description and help file location
        setPopUpDescription("Buffers and outputs any data");
        setHelpFileLocation("ConstBuffer.html");

        // Define initial value and type of parameters
        defineParameter("number", "10", USER_ACCESSIBLE);

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "Enter length of output SampleSet: $title number TextField 1\n";
        setGUIBuilderV2Info(guilines);

    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        // Set unit variables to the values specified by the parameters
        number = Integer.parseInt((String) getParameter("number"));
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up ConstBuffer (e.g. close open files) 
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables 
        if (paramname.equals("number")) {
            number = Integer.parseInt((String) value);
            System.out.println("set number= " + number + " in parameterUpdate");
            buffer = new Vector(number);
            for (int i = 0; i < number; ++i) {
                buffer.add(i, new Const(0));
            }
        }
    }


    /**
     * @return an array of the input types for ConstBuffer
     */
    public String[] getInputTypes() {
        return new String[]{"Const"};
    }

    /**
     * @return an array of the output types for ConstBuffer
     */
    public String[] getOutputTypes() {
        return new String[]{"SampleSet"};
    }

}



