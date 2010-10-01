package signalproc.time;

import org.trianacode.taskgraph.Unit;
import triana.types.VectorType;
import triana.types.util.Str;

/**
 * A Stretch unit to enlarge a data set by inserting a certain number of zeros at regular intervals.
 *
 * @author B.F. Schutz
 * @version 2.0 alpha 20 September 2000
 */
public class Stretch extends Unit {

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Inserts zeros at regular intervals";
    }


    /**
     * Parameter telling whether to insert before or after element
     */
    String place = "AfterEachElement";

    /**
     * Parameter giving number of zeros inserted at each insertion
     */
    int pad = 0;

    /**
     * ********************************************* ** USER CODE of Stretch goes here    ***
     * *********************************************
     */
    public void process() {

        VectorType input = (VectorType) getInputAtNode(0);
        VectorType output = (VectorType) input.copyMe();

        boolean before = place.equals("BeforeEachElement");

        if (pad < 1) {
            System.out.println("Data not modified by " + getToolName() + " because given number of zeros to be inserted ("
                    + String.valueOf(pad) + ") is zero or negative.");
        } else {
            output.interpolateZeros(pad, before);
        }

        output(output);
    }


    /**
     * Initialses information specific to Stretch.
     */
    public void init() {
        super.init();

//        setUseGUIBuilder(true);
//
//        setResizableInputs(false);
//        setResizableOutputs(true);

        place = "AfterEachElement";
        pad = 0;

        String guilines = "";
        guilines += "Choose where to add extra zeros: $title place Choice AfterEachElement BeforeEachElement\n";
        guilines += "Give number of zeros to add in each location $title pad IntScroller 0 15 0\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format (see Triana help).
     */
//    public void setGUIInformation() {
//        addGUILine("Choose where to add extra zeros: $title place Choice AfterEachElement BeforeEachElement");
//        addGUILine("Give number of zeros to add in each location $title pad IntScroller 0 15 0");
//    }

    /**
     * Resets Stretch
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves Stretch's parameters to the parameter file.
     */
//    public void saveParameters() {
//        saveParameter("place", place);
//        saveParameter("pad", pad);
//    }

    public void parameterUpdate(String name, Object value) {
        if (name.equals("place")) {
            place = (String) value;
        }
        if (name.equals("pad")) {
            pad = Str.strToInt((String) value);
        }
    }

    /**
     * @return a string containing the names of the types allowed to be input to Stretch, each separated by a white
     *         space.
     */
//    public String inputTypes() {
//        return "VectorType";
//    }
//
//    /**
//     * @return a string containing the names of the types output from Stretch, each separated by a white space.
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
        return "Stretch.html";
    }


}

















