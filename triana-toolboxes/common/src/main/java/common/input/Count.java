package common.input;

import org.trianacode.taskgraph.Unit;
import triana.types.Const;
import triana.types.util.Str;

/**
 * A Count unit to increment an output Const each time it is activated.
 *
 * @author Ian Taylor
 * @author B F Schut
 * @version 1.01 20 August 2000
 */
public class Count extends Unit {

    double st = 0.0;
    double inc = 1.0;
    double end = Double.MAX_VALUE;
    double curr = 0.0;

    /**
     * ********************************************* ** USER CODE of Count goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        if (((inc > 0) && (curr <= end)) || ((inc < 0) && (curr >= end))) {
            output(new Const(curr));

            curr = curr + inc;

            if (((inc > 0) && (curr <= end)) || ((inc < 0) && (curr >= end))) {
                setParameter("current", (Object) String.valueOf(curr));
            } else {
                setParameter("current", (Object) "N/A");
            }
        }
    }

    /**
     * Initialses information specific to Count.
     */
    public void init() {
        super.init();

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        defineParameter("start", String.valueOf(st), USER_ACCESSIBLE);
        defineParameter("increment", String.valueOf(inc), USER_ACCESSIBLE);

        if (end == Double.MAX_VALUE) {
            defineParameter("end", "", USER_ACCESSIBLE);
        } else {
            defineParameter("end", String.valueOf(end), USER_ACCESSIBLE);
        }

        defineParameter("current", String.valueOf(curr), USER_ACCESSIBLE);

        String guilines = "";
        guilines += "Starting Value $title start TextField 0.0\n";
        guilines += "Increment $title increment TextField 1.0\n";
        guilines += "End Value $title end TextField\n";
        guilines += "Next Value $title current Label 0.0\n";
        setGUIBuilderV2Info(guilines);        
    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format (see Triana help).
     */
//    public void setGUIInformation() {
//        addGUILine("Starting Value $title start TextField 0.0");
//        addGUILine("Increment $title increment TextField 1.0");
//        addGUILine("End Value $title end TextField");
//        addGUILine("Next Value $title current Label 0.0");
//    }

    /**
     * Reset's Count
     */
    public void reset() {
        super.reset();
        st = Str.strToDouble((String) getParameter("start"));
        inc = Str.strToDouble((String) getParameter("increment"));

        if (getParameter("end").equals("")) {
            if (inc > 0) {
                end = Double.MAX_VALUE;
            } else {
                end = Double.MIN_VALUE;
            }
        } else {
            end = Str.strToDouble((String) getParameter("end"));
        }

        curr = Str.strToDouble((String) getParameter("start"));
    }


    /**
     * Used to set each of Count's parameters.
     */
    public void setParameter(String name, String value) {
        if (name.equals("start")) {
            st = Str.strToDouble(value);
            curr = st;
        }
        if (name.equals("increment")) {
            inc = Str.strToDouble(value);
        }
        if (name.equals("end")) {
            if (value.equals("")) {
                if (inc > 0) {
                    end = Double.MAX_VALUE;
                } else {
                    end = Double.MIN_VALUE;
                }
            } else {
                end = Str.strToDouble(value);
            }
        }

        if (((inc > 0) && (curr <= end)) || ((inc < 0) && (curr >= end))) {
            setParameter("current", (Object) String.valueOf(curr));
        } else {
            setParameter("current", (Object) "N/A");
        }
    }

    /**
     * @return a string containing the names of the types allowed to be input to Count, each separated by a white
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
        return "Increments its output Const each time it is activated";
    }

    /**
     *
     * @returns the location of the help file for this unit.
     */
    public String getHelpFile() {
        return "Count.html";
    }
}













