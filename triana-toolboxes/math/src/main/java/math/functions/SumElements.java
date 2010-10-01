package math.functions;

import org.trianacode.taskgraph.Unit;
import triana.types.Const;
import triana.types.GraphType;
import triana.types.util.FlatArray;
import triana.types.util.Str;

/**
 * A SumElements unit to compute sum or average of the elements of any data set.
 *
 * @author B F Schutz
 * @version 1.1 28 Feb 2001
 */
public class SumElements extends Unit {

    int dv = 0;
    String type = "Sum";


    /**
     * ********************************************* ** USER CODE of SumElements goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        GraphType input = (GraphType) getInputAtNode(0);

        FlatArray flatR, flatI;
        double[] dataR, dataI;
        int k, len;
        double answerR = Double.NaN;
        double answerI = Double.NaN;
        boolean complex = input.isDependentComplex(dv);

        if (input.isArithmeticArray(dv)) {
            if (complex) {
                flatR = new FlatArray(input.getDataArrayReal(dv));
                flatI = new FlatArray(input.getDataArrayImag(dv));
                dataR = (double[]) flatR.getFlatArray();
                dataI = (double[]) flatI.getFlatArray();
                len = dataR.length;
                answerR = 0;
                answerI = 0;
                for (k = 0; k < len; k++) {
                    answerR += dataR[k];
                    answerI += dataI[k];
                }
                if (type.equals("Average")) {
                    answerR /= len;
                    answerI /= len;
                }
            } else {
                flatR = new FlatArray(input.getDataArrayReal(dv));
                dataR = (double[]) flatR.getFlatArray();
                len = dataR.length;
                answerR = 0;
                for (k = 0; k < len; k++) {
                    answerR += dataR[k];
                }
                if (type.equals("Average")) {
                    answerR /= len;
                }
            }
            if (complex) {
                output(new Const(answerR, answerI));
            } else {
                output(new Const(answerR));
            }

        }


    }


    /**
     * Initialses information specific to SumElements.
     */
    public void init() {
        super.init();

//        setUseGUIBuilder(true);
//
//        setRequireDoubleInputs(false);
//        setCanProcessDoubleArrays(true);
//
//        setResizableInputs(false);
//        setResizableOutputs(true);

        setDefaultInputNodes(1);
        setDefaultOutputNodes(1);
        setMinimumInputNodes(1);
        setMinimumOutputNodes(1);

        String guilines = "";
        guilines += "Which dependent variable do you want to compute the sum of squares of? $title dv IntScroller 0 5 0\n";
        guilines += "Choose value to be computed: $title type Choice Sum Average\n";
        setGUIBuilderV2Info(guilines);        
    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format.
     */
//    public void setGUIInformation() {
//        addGUILine(
//                "Which dependent variable do you want to compute the sum of squares of? $title dv IntScroller 0 5 0");
//        addGUILine("Choose value to be computed: $title type Choice Sum Average");
//    }

    /**
     * Called when the reset button is pressed within the MainTriana Window
     */
    public void reset() {
        super.reset();
    }

    /**
     * Called when the stop button is pressed within the MainTriana Window
     */
    public void stopping() {
        super.stopping();
    }

    /**
     * Called when the start button is pressed within the MainTriana Window
     */
//    public void starting() {
//        super.starting();
//    }
//
//    /**
//     * Saves SumElements's parameters.
//     */
//    public void saveParameters() {
//        saveParameter("dv", dv);
//        saveParameter("type", type);
//    }


    /**
     * Used to set each of SumElements's parameters.
     */
    public void updateParameter(String name, Object value) {
        //updateGUIParameter(name, value);

        if (name.equals("dv")) {
            dv = Str.strToInt((String) value);
        }
        if (name.equals("type")) {
            type = (String) value;
        }
    }

    /**
     * Don't need to use this for GUI Builder units as everthing is updated by triana automatically
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to SumElements, each separated by a white
     *         space.
     */
    public String[] getInputTypes() {
        return new String[]{"triana.types.GraphType"};
    }

    public String[] getOutputTypes() {
        return new String[]{"triana.types.Const"};
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Compute sum or average of the elements of the data";
    }

    /**
     *
     * @returns the location of the help file for this unit.
     */
    public String getHelpFile() {
        return "SumElements.html";
    }
}




