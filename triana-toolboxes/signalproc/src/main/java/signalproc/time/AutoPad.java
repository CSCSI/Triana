package signalproc.time;

import org.trianacode.taskgraph.Unit;
import triana.types.VectorType;


/**
 * A AutoPad unit to pad a data set to the nearest power of two. This has no parameters so it is very convenient to use.
 * It pads at the end of the given data. If the data are irregularly sampled the extra elements are added with regular
 * spacing.
 *
 * @author Ian Taylor
 * @author Bernard Schutz
 * @version 2.01 30 August 2000
 */
public class AutoPad extends Unit {

    /**
     * ********************************************* ** USER CODE of AutoPad goes here    ***
     * *********************************************
     */
    public void process() {
        VectorType input = (VectorType) getInputAtNode(0);

        VectorType output = (VectorType) input.copyMe();

        int k = output.size();
        int newSize = 1;

        while (newSize < k) {
            newSize *= 2;
        }
        if (newSize != k) {
            output.extendWithZeros(newSize, false);
        } else {
            System.out.println("No change to data set in unit " + getToolName() + " because size is already an exact power of 2 ("
                    + String.valueOf(k) + ").");
        }

        output(output);
    }


    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Adds zeros to the end of a data set, extending it to the nearest larger power of 2";
    }

    /**
     * Initialses information specific to AutoPad.
     */
    public void init() {
        super.init();

//        setResizableInputs(false);
//        setResizableOutputs(true);
    }


    /**
     * Reset's AutoPad
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves parameters
     */
    public void saveParameters() {
    }

    /**
     * Sets the parameters
     */
    public void setParameter(String name, String value) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to AutoPad, each separated by a white
     *         space.
     */
//    public String inputTypes() {
//        return "VectorType";
//    }
//
//    /**
//     * @return a string containing the names of the types output from AutoPad, each separated by a white space.
//     */
//    public String outputTypes() {
//        return "VectorType";
//    }

    public String[] getInputTypes() {
        return new String[]{"triana.types.VectorType"};
    }

    public String[] getOutputTypes() {
        return new String[]{"triana.types.VectorType"};
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "AutoPad.html";
    }
}













