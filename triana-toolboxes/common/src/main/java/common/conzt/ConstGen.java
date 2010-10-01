package common.conzt;

import org.trianacode.taskgraph.Unit;
import triana.types.Const;
import triana.types.util.Str;

/**
 * A ConstGen unit to Generate a Constant value and wrap it into a Const type.
 *
 * @author Ian Taylor
 * @version 1.0 19 May 2000
 */
public class ConstGen extends Unit {

    String constant = "0.0";
    String imagval = "0.0";

    /**
     * ********************************************* ** USER CODE of ConstGen goes here    ***
     * *********************************************
     */
    public void process() {
        output(new Const(Str.strToDouble(constant), Str.strToDouble(imagval)));
    }

    /**
     * Initialses information specific to ConstGen.
     */
    public void init() {
        super.init();

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);
        
        defineParameter("constant", "0.0", USER_ACCESSIBLE);
        defineParameter("imagval", "0.0", USER_ACCESSIBLE);
                
        String guilines = "";
        guilines += "Real Value : $title constant TextField 0.0\n";
        guilines += "Imag Value : $title imagval TextField 0.0\n";
        System.out.println("testing ConstGen.java");
        setGUIBuilderV2Info(guilines);
    }


//    public void setGUIInformation() {
//        addGUILine("Real Value : $title constant TextField 0.0");
//        addGUILine("Imag Value : $title imagval TextField 0.0");
//    }

    /**
     * Reset's ConstGen
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves ReplaceAll's parameters to the parameter file.
     */
//    public void saveParameters() {
//        saveParameter("constant", constant);
//        saveParameter("imagval", imagval);
//    }

    /**
     * Used to set each of ReplaceAll's parameters.
     */
    public void setParameter(String name, String value) {
        if (name.equals("constant")) {
            constant = value;
        }
        if (name.equals("imagval")) {
            imagval = value;
        }
    }

    /**
     * @return a string containing the names of the types allowed to be input to ConstGen, each separated by a white
     *         space.
     */
    public String[] getInputTypes() {
        return new String[]{};
    }

    /**
     * @return a string containing the names of the types output from Compare, each separated by a white space.
     */
    public String[] getOutputTypes() {
        return new String[]{"triana.types.Const"};
    }
    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Generates a constant";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "ConstGen.html";
    }
}

