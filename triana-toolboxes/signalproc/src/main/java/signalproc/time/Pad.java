package signalproc.time;

import org.trianacode.taskgraph.Unit;
import triana.types.VectorType;
import triana.types.util.Str;

/**
 * A Pad unit to add zeros at the end of a data set.
 *
 * @author Ian Taylor
 * @author Bernard Schutz
 * @version 2.0 10 September 2000
 */
public class Pad extends Unit {

    int pad;
    String method;
    String place;


    /**
     * ********************************************* ** USER CODE of Pad goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        VectorType data = (VectorType) getInputAtNode(0);
        VectorType newData = (VectorType) data.copyMe();

        int newSize;
        boolean before = place.equals("AtBeginning");

        if (method.equals("NearestPowerOf2")) {
            int k = newData.size();
            newSize = 1;

            while (newSize < k) {
                newSize *= 2;
            }
            if (newSize != k) {
                newData.extendWithZeros(newSize, before);
            } else {
                System.out.println("No change to data set in unit " + getToolName() + " because size is already an exact power of 2 ("
                        + String.valueOf(k) + ").");
            }
        } else if (method.equals("GivenPowerOf2")) {
            newSize = (int) Math.pow(2, pad);
            if (newSize > newData.size()) {
                newData.extendWithZeros(newSize, before);
            } else {
                System.out.println("No change to data set in unit " + getToolName() + " because given power of 2 ("
                        + String.valueOf(pad) + ") was too small for existing size (" + String.valueOf(newData.size())
                        + ").");
            }
        } else if (method.equals("GivenMultipleOfLength")) {
            if (pad > 1) {
                newData.extendWithZeros(pad * newData.size(), before);
            } else {
                System.out.println("No change to data set in unit " + getToolName() + " because given multiple (" + String.valueOf(pad)
                        + ") was less than or equal to 1.");
            }
        } else {
            if (pad > newData.size()) {
                newData.extendWithZeros(pad, before);
            } else {
                System.out.println("No change to data set in unit " + getToolName() + " because given new length ("
                        + String.valueOf(pad) + ") is smaller than existing length (" + String.valueOf(newData.size())
                        + ").");
            }
        }

        output(newData);
    }


    /**
     * Initialises information specific to Pad.
     */
    public void init() {
        super.init();

//        setUseGUIBuilder(true);
//
//        setResizableInputs(false);
//        setResizableOutputs(true);
        pad = 1;
        method = "GivenMultipleOfLength";
        place = "AtEnd";

        String guilines = "";
        guilines += "Choose where to add extra zeros to this data set: $title place Choice AtEnd AtBeginning\n";
        guilines += "Extend length of set to (choose method): $title method Choice GivenMultipleOfLength NearestPowerOf2 GivenPowerOf2 GivenNumberOfElements\n";
        guilines += "Give appropriate value (multiple (1 means do nothing), power of 2, or total number $title pad IntScroller 0 20 1\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format (see Triana help).
     */
//    public void setGUIInformation() {
//        addGUILine("Choose where to add extra zeros to this data set: $title place Choice AtEnd AtBeginning");
//        addGUILine(
//                "Extend length of set to (choose method): $title method Choice GivenMultipleOfLength NearestPowerOf2 GivenPowerOf2 GivenNumberOfElements");
//        addGUILine(
//                "Give appropriate value (multiple (1 means do nothing), power of 2, or total number $title pad IntScroller 0 20 1");
//    }

    /**
     * Resets Pad
     */
    public void reset() {
        super.reset();
        pad = 0;
//         setParameter("place", "AtEnd");
        //       setParameter("method", "GivenMultipleOfLength");
        //     setParameter("pad", "1");
    }

    /**
     * Saves Pad's parameters.
     */
//    public void saveParameters() {
//        saveParameter("place", place);
//        saveParameter("method", method);
//        saveParameter("pad", pad);
//    }

    /**
     * Used to set each of Pad's parameters.
     */
    public void parameterUpdate(String name, Object value) {
        //updateGUIParameter(name, value);

        if (name.equals("place")) {
            place = (String) value;
        }

        if (name.equals("method")) {
            method = (String) value;
        }

        if (name.equals("pad")) {
            pad = Str.strToInt((String) value);
        }
    }

    /**
     * @return a string containing the names of the types allowed to be input to Pad, each separated by a white space.
     */
//    public String inputTypes() {
//        return "VectorType";
//    }
//
//    /**
//     * @return a string containing the names of the types output from Pad, each separated by a white space.
//     */
//    public String outputTypes() {
//        return "VectorType";
//    }
//
    public String[] getInputTypes() {
        return new String[]{"triana.types.VectorType"};
    }

    public String[] getOutputTypes() {
        return new String[]{"triana.types.VectorType"};
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Extends data set with zeros";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "Pad.html";
    }
}













