package math.functions;

import org.trianacode.gui.windows.ErrorDialog;
import org.trianacode.taskgraph.Unit;
import triana.types.Const;
import triana.types.EmptyingType;
import triana.types.GraphType;
import triana.types.util.FlatArray;
import triana.types.util.Str;

/**
 * A PowInteger unit to raise the elements of an input data array to an integer power. The array can be real or
 * complex.
 * <p/>
 * This Unit obeys the conventions of Triana Type 2 data types.
 *
 * @author Bernard Schutz
 * @version 2.1 13 January 2001
 */
public class PowInteger extends Unit {

    /**
     * Integer power parameter
     */
    int power = 1;

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Raises the elements of the input data set to integer powers";
    }

    /**
     * ********************************************* ** USER CODE of PowInteger goes here    ***
     * *********************************************
     */
    public void process() {


        Object input, output;
        int abspower;

        input = getInputAtNode(0);
        if (input instanceof EmptyingType) {
            return;
        }
        Class inputClass = input.getClass();
        //setOutputType(inputClass);
        output = input;

        if (input instanceof GraphType) {
            FlatArray tempR, tempI;
            int dv, j, k;
            double d, r, i;
            double[] inputdataR, inputdataI;
            for (dv = 0; dv < ((GraphType) input).getDependentVariables(); dv++) {
                if (((GraphType) input).isArithmeticArray(dv)) {
                    tempR = new FlatArray(((GraphType) input).getDataArrayReal(dv));
                    inputdataR = (double[]) tempR.getFlatArray();
                    if (((GraphType) input).isDependentComplex(dv)) {
                        tempI = new FlatArray(((GraphType) input).getDataArrayImag(dv));
                        inputdataI = (double[]) tempI.getFlatArray();
                        if (power == 0) {
                            for (j = 0; j < inputdataI.length; j++) {
                                if ((inputdataR[j] == 0.0) && (inputdataI[j] == 0.0)) {
                                    inputdataR[j] = Double.NaN;
                                    inputdataI[j] = Double.NaN;
                                    new ErrorDialog(null,
                                            getTask().getToolName() + ": Attempt to raise zero to power zero returns NaN.");
                                } else {
                                    inputdataR[j] = 1.0;
                                    inputdataI[j] = 0.0;
                                }
                            }
                        } else if (power > 1) {
                            for (j = 0; j < inputdataI.length; j++) {
                                r = inputdataR[j];
                                i = inputdataI[j];
                                for (k = 1; k < power; k++) {
                                    d = inputdataR[j];
                                    inputdataR[j] = d * r - i * inputdataI[j];
                                    inputdataI[j] = d * i + r * inputdataI[j];
                                }
                            }
                        } else if (power < 0) {
                            abspower = -power;
                            for (j = 0; j < inputdataI.length; j++) {
                                r = inputdataR[j];
                                i = inputdataI[j];
                                d = r * r + i * i;
                                if (d == 0.0) {
                                    inputdataR[j] = Double.POSITIVE_INFINITY;
                                    inputdataI[j] = 0.0;
                                } else {
                                    inputdataR[j] = r / d;
                                    inputdataI[j] = -i / d;
                                    r = inputdataR[j];
                                    i = inputdataI[j];
                                    if (abspower > 1) {
                                        for (k = 1; k < abspower; k++) {
                                            d = inputdataR[j];
                                            inputdataR[j] = d * r - i * inputdataI[j];
                                            inputdataI[j] = d * i + r * inputdataI[j];
                                        }
                                    }
                                }
                            }
                        }
                        ((GraphType) output).setDataArrayImag(tempI.restoreArray(false), dv);
                    } else {
                        if (power == 0) {
                            for (j = 0; j < inputdataR.length; j++) {
                                if (inputdataR[j] == 0.0) {
                                    inputdataR[j] = Double.NaN;
                                    new ErrorDialog(null,
                                            getTask().getToolName() + ": Attempt to raise zero to power zero returns NaN.");
                                } else {
                                    inputdataR[j] = 1;
                                }
                            }
                        } else if (power > 1) {
                            for (j = 0; j < inputdataR.length; j++) {
                                d = inputdataR[j];
                                for (k = 1; k < power; k++) {
                                    inputdataR[j] *= d;
                                }
                            }
                        } else if (power < 0) {
                            abspower = -power;
                            for (j = 0; j < inputdataR.length; j++) {
                                if (inputdataR[j] == 0.0) {
                                    inputdataR[j] = Double.POSITIVE_INFINITY;
                                } else {
                                    d = 1 / inputdataR[j];
                                    if (abspower > 1) {
                                        for (k = 1; k < abspower; k++) {
                                            inputdataR[j] *= d;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    ((GraphType) output).setDataArrayReal(tempR.restoreArray(false), dv);
                }
            }
        } else if (input instanceof Const) {
            double r, i, d, di, dd;
            int k;
            r = ((Const) input).getReal();
            if (((Const) input).isComplex()) {
                i = ((Const) input).getImag();
                if (power == 0) {
                    if ((r == 0.0) && (i == 0.0)) {
                        r = Double.NaN;
                        i = Double.NaN;
                        new ErrorDialog(null, getTask().getToolName() + ": Attempt to raise zero to power zero returns NaN.");
                    } else {
                        r = 1.0;
                        i = 0.0;
                    }
                } else if (power > 1) {
                    d = r;
                    di = i;
                    for (k = 1; k < power; k++) {
                        r = d * r - di * i;
                        i = d * i + di * r;
                    }
                } else if (power < 0) {
                    abspower = -power;
                    d = r * r + i * i;
                    if (d == 0) {
                        r = Double.POSITIVE_INFINITY;
                        i = 0.0;
                    } else {
                        r = r / d;
                        i = -i / d;
                        d = r;
                        di = i;
                        if (abspower > 1) {
                            for (k = 1; k < abspower; k++) {
                                dd = r;
                                r = dd * r - i * di;
                                i = d * i + di * dd;
                            }
                        }
                    }
                }
                ((Const) output).setImag(i);
            } else {
                if (power == 0) {
                    if (r == 0.0) {
                        r = Double.NaN;
                        new ErrorDialog(null, getTask().getToolName() + ": Attempt to raise zero to power zero returns NaN.");
                    } else {
                        r = 1.0;
                    }
                } else if (power > 1) {
                    d = r;
                    for (k = 1; k < power; k++) {
                        r *= d;
                    }
                } else if (power < 0) {
                    abspower = -power;
                    if (r == 0.0) {
                        r = Double.POSITIVE_INFINITY;
                    } else {
                        d = 1 / r;
                        r = d;
                        if (abspower > 1) {
                            for (k = 1; k < abspower; k++) {
                                r *= d;
                            }
                        }
                    }
                }
            }
            ((Const) output).setReal(r);
        }

        output(output);


    }


    /**
     * Initialses information specific to PowInteger.
     */
    public void init() {
        super.init();

//        setUseGUIBuilder(true);
//
//        setResizableInputs(false);
//        setResizableOutputs(true);
//        // This is to ensure that we receive arrays containing double-precision numbers
//        setRequireDoubleInputs(true);
//        setCanProcessDoubleArrays(true);

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        String guilines = "";
        guilines += "Give integer power $title power IntScroller -5 5 1\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format.
     */
//    public void setGUIInformation() {
//        addGUILine("Give integer power $title power IntScroller -5 5 1");
//
//    }


    /**
     * Resets PowInteger
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves PowInteger's parameters to the parameter file.
     */
//    public void saveParameters() {
//        saveParameter("power", power);
//    }

    /**
     * Used to set each of Cosine's parameters.
     */
    public void parameterUpdate(String name, Object value) {
        //updateGUIParameter(name, value);

        if (name.equals("power")) {
            power = Str.strToInt((String) value);
        }

    }

    /**
     * @return a string containing the names of the types allowed to be input to PowInteger, each separated by a white
     *         space.
     */
    public String[] getInputTypes() {
        return new String[]{"triana.types.GraphType", "triana.types.Const"};
    }

    public String[] getOutputTypes() {
        return new String[]{"triana.types.GraphType", "triana.types.Const"};
    }

    /**
     *
     * @returns the location of the help file for this unit.
     */
    public String getHelpFile() {
        return "PowInteger.html";
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


}



















