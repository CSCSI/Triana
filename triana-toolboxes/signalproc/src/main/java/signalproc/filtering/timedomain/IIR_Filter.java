package signalproc.filtering.timedomain;

import java.util.StringTokenizer;

import org.trianacode.taskgraph.Unit;
import triana.types.VectorType;
import triana.types.util.Str;
import triana.types.util.StringVector;

/**
 * A IIR_Filter unit to implement an IIR filter in the time domain, ie for input data {x(k)}, the filter coefficients
 * {a(k),b(k)} lead to output {y{k}):<\p><p> y(n) = a(0)x(n) + a(1)x(n-1) + a(2)x(n-2) + ... - b(0)y(n-1) - ...<\p><p>
 * Note that the b*y terms are subtracted, not added. The user gives an arbitrary number of coefficients for both
 * sequences a and b, and the unit applies them to the input and previously computed data. The unit maintains continuity
 * across successive input data sets if the user requests it. The computation starts off as if the first data set had
 * been preceded by a sequence of zeros.
 *
 * @author B F Schutz
 * @version 1.0 21 Feb 2002
 */
public class IIR_Filter extends Unit {

    String CoeffsX = "0.5 0.5";
    String CoeffsY = "0.1";
    boolean continuity = true;

    boolean firstData = true;
    double[] coefX, coefY;
    double[] previousData = {0};
    double[] previousResult = {0};
    int filterSizeX = 0;
    int filterSizeY = 0;


    /**
     * ********************************************* ** USER CODE of FIR_Filter goes here    ***
     * *********************************************
     */
    public void process() throws Exception {

        int i, j, k, m;
        VectorType wave = (VectorType) getInputAtNode(0);

        int length = wave.size();
        double out[] = new double[length];
        double in[] = new double[filterSizeX + length];
        System.arraycopy(wave.getData(), 0, in, filterSizeX, length);

        if (continuity) {
            if (firstData) {
                firstData = false;
                previousResult = new double[filterSizeY];
            } else {
                System.arraycopy(previousData, 0, in, 0, filterSizeX);
            }
        } else {
            previousResult = new double[filterSizeY];
        }

        double av;

        for (i = filterSizeX, m = 0; i < filterSizeX + length; ++i, m++) {
            av = 0.0;
            for (j = 0, k = i; j < filterSizeX; ++j, --k) {
                av += in[k] * coefX[j];
            }
            for (j = 0, k = m - 1; (j < filterSizeY) && (k >= 0); ++j, --k) {
                av -= out[k] * coefY[j];
            }
            if (m < filterSizeY) {
                for (j = m, k = filterSizeY - 1; (j < filterSizeY) && (k >= 0); ++j, --k) {
                    av -= previousResult[k] * coefY[j];
                }
            }
            out[m] = av;
        }

        wave.setData(out);
        if (filterSizeX > 0) {
            System.arraycopy(in, length - filterSizeX, previousData, 0, filterSizeX);
        }
        if (filterSizeY > 0) {
            System.arraycopy(out, length - filterSizeY, previousResult, 0, filterSizeY);
        }

        output(wave);
    }


    private void parseCoeffsX() {

        StringTokenizer st = new StringTokenizer(CoeffsX);
        StringVector sv = new StringVector();

        while (st.hasMoreTokens()) {
            sv.addElement(st.nextToken());
        }
        filterSizeX = sv.size();

        coefX = new double[filterSizeX];
        previousData = new double[filterSizeX];

        for (int i = 0; i < filterSizeX; ++i) {
            coefX[i] = Str.strToDouble(sv.at(i));
            System.out.println("X-coefficient " + String.valueOf(i) + " " + String.valueOf(coefX[i]));
        }
    }


    private void parseCoeffsY() {

        StringTokenizer st = new StringTokenizer(CoeffsY);
        StringVector sv = new StringVector();

        while (st.hasMoreTokens()) {
            sv.addElement(st.nextToken());
        }
        filterSizeY = sv.size();

        coefY = new double[filterSizeY];
        previousResult = new double[filterSizeY];

        for (int i = 0; i < filterSizeY; ++i) {
            coefY[i] = Str.strToDouble(sv.at(i));
            System.out.println("Y-coefficient " + String.valueOf(i) + " " + String.valueOf(coefY[i]));
        }
    }


    /**
     * Initialses information specific to IIR_Filter.
     */
    public void init() {
        super.init();

//        setUseGUIBuilder(true);
//
//        setRequireDoubleInputs(false);
//        setCanProcessDoubleArrays(false);

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(1);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        firstData = true;

        String guilines = "";
        guilines += "Enter the x-coefficients of the IIR filter below (any number of them, separated by spaces)  $title CoeffsX TextField 0.5 0.5\n";
        guilines += "Enter the y-coefficients of the IIR filter below  $title CoeffsY TextField 0.1\n";
        guilines += "Check here if you want filter continued across successive input sets $title continuity Checkbox true\n";
        setGUIBuilderV2Info(guilines);                        
    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format.
     */
//    public void setGUIInformation() {
//        addGUILine(
//                "Enter the x-coefficients of the IIR filter below (any number of them, separated by spaces)  $title CoeffsX TextField 0.5 0.5");
//        addGUILine("Enter the y-coefficients of the IIR filter below  $title CoeffsY TextField 0.1");
//        addGUILine(
//                "Check here if you want filter continued across successive input sets $title continuity Checkbox true");
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
     * Saves IIR_Filter's parameters.
     */
//    public void saveParameters() {
//        saveParameter("CoeffsX", CoeffsX);
//        saveParameter("CoeffsY", CoeffsY);
//        saveParameter("continuity", continuity);
//    }

    /**
     * Used to set each of IIR_Filter's parameters.
     */
    public void parameterUpdate(String name, Object value) {
        //updateGUIParameter(name, value);

        if (name.equals("CoeffsX")) {
            CoeffsX = (String) value;
            parseCoeffsX();
        }
        if (name.equals("CoeffsY")) {
            CoeffsY = (String) value;
            parseCoeffsY();
        }
        if (name.equals("continuity")) {
            continuity = Str.strToBoolean((String) value);
        }
    }

    /**
     * Don't need to use this for GUI Builder units as everthing is updated by triana automatically
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to IIR_Filter, each separated by a white
     *         space.
     */
//    public String inputTypes() {
//        return "VectorType";
//    }
//
//    /**
//     * @return a string containing the names of the types output from IIR_Filter, each separated by a white space.
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
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Make IIR filter out of coeffs {a(k), b(k)}: y(n) = a(0)x(n) + a(1)x(n-1) + ... + b(1)y(n-1) + ...";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "IIR_Filter.html";
    }
}
