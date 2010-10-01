package math.statistics;

import org.trianacode.taskgraph.Unit;
import triana.types.Const;
import triana.types.GraphType;
import triana.types.util.FlatArray;
import triana.types.util.Str;

/**
 * A Kurtosis unit to compute the kurtosis or sample kurtosis (normalized fourth moment about the mean minus 3) of any
 * data set. Kurtosis is normalized by the variance squared. "Sample" values differ from ordinary ones by normalization:
 * divide by n-1 instead of n. For complex data the method returns the kurtosis computed using the magnitudes of the
 * data points.
 *
 * @author B F Schutz
 * @version 1.0 28 Feb 2001
 */
public class Kurtosis extends Unit {

    int dv = 0;
    String type = "Kurtosis";


    /**
     * ********************************************* ** USER CODE of Kurtosis goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        GraphType input = (GraphType) getInputAtNode(0);

        FlatArray flatR, flatI;
        double[] dataR, dataI;
        double meanR, meanI, yI, yR, t1, t2;
        int k, len;
        double answer = Double.NaN;
        double variance = 0;
        double std = 0;
        boolean complex;

        if (input.isArithmeticArray(dv)) {
            complex = input.isDependentComplex(dv);
            if (complex) {
                flatR = new FlatArray(input.getDataArrayReal(dv));
                flatI = new FlatArray(input.getDataArrayImag(dv));
                meanR = 0;
                meanI = 0;
                dataR = (double[]) flatR.getFlatArray();
                dataI = (double[]) flatI.getFlatArray();
                len = dataR.length;
                for (k = 0; k < len; k++) {
                    meanR += dataR[k];
                    meanI += dataI[k];
                }
                meanR /= len;
                meanI /= len;
                answer = 0;
                variance = 0;
                for (k = 0; k < len; k++) {
                    yR = dataR[k] - meanR;
                    yI = dataI[k] - meanI;
                    t1 = yR * yR + yI * yI;
                    variance += t1;
                    answer += t1 * t1;
                }
            } else {
                flatR = new FlatArray(input.getDataArrayReal(dv));
                meanR = 0;
                dataR = (double[]) flatR.getFlatArray();
                len = dataR.length;
                for (k = 0; k < len; k++) {
                    meanR += dataR[k];
                }
                meanR /= len;
                answer = 0;
                variance = 0;
                for (k = 0; k < len; k++) {
                    yR = dataR[k] - meanR;
                    t1 = yR * yR;
                    variance += t1;
                    answer += t1 * t1;
                }
            }
            if ((len > 1) && type.equals("SampleKurtosis")) {
                len--;
            }
            variance /= len;
            answer = answer / (len * variance * variance) - 3;
            output(new Const(answer));
        }
    }

    /**
     * Initialses information specific to Kurtosis.
     */
    public void init() {
        super.init();

//        setUseGUIBuilder(true);
//
//        setRequireDoubleInputs(false);
//        setCanProcessDoubleArrays(false);
//
//        setResizableInputs(false);
//        setResizableOutputs(true);

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        String guilines = "";
        guilines += "Which dependent variable do you want the skewness of? $title dv IntScroller 0 5 0\n";
        guilines += "Choose value to be computed: $title type Choice Kurtosis SampleKurtosis\n";
        setGUIBuilderV2Info(guilines);

    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format.
     */
//    public void setGUIInformation() {
//        addGUILine("Which dependent variable do you want the skewness of? $title dv IntScroller 0 5 0");
//        addGUILine("Choose value to be computed: $title type Choice Kurtosis SampleKurtosis");
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

    /**
     * Saves Kurtosis's parameters.
     */
//    public void saveParameters() {
//        saveParameter("dv", dv);
//        saveParameter("type", type);
//    }


    /**
     * Used to set each of Kurtosis's parameters.
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
     * @return a string containing the names of the types allowed to be input to Kurtosis, each separated by a white
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
        return "Compute kurtosis (normalized 4th moment about the mean minus 3 )";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "Kurtosis.html";
    }
}




