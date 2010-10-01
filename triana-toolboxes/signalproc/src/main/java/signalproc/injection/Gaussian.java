package signalproc.injection;

import java.util.Random;

import org.trianacode.taskgraph.Unit;
import triana.types.Const;
import triana.types.EmptyingType;
import triana.types.GraphType;
import triana.types.util.FlatArray;
import triana.types.util.Str;

/**
 * A Gaussian unit to add normally distributed noise to the elements of an input data array. The array can be real or
 * complex. The returned data type will be the same as the input. Parameters can be used to choose the standard
 * deviation and mean of the distribution.
 * <p/>
 * This Unit obeys the conventions of Triana Type 2 data types.
 *
 * @author Bernard Schutz
 * @version 2.0 20 June 2000
 */
public class Gaussian extends Unit {

    /**
     * Offset parameters will be added to each element of the input array.
     */
    double mean = 0.0;
    double stdDev = 1.0;
    boolean complexInput;
    double scale;

    /**
     * The Random generator class instance
     */
    Random generator;


    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Adds Gaussian noise to the elements of the input data.";
    }

    /**
     * ********************************************* ** USER CODE of Gaussian goes here    ***
     * *********************************************
     */
    public void process() {

        Object input, output;

        output = null;
        long time = System.currentTimeMillis();

        input = getInputAtNode(0);
        if (input instanceof EmptyingType) {
            return;
        }
        output = input;
        Class outputClass = output.getClass();
        //setOutputType(outputClass);
        scale = stdDev;

        if (input instanceof GraphType) {
            FlatArray tempR, tempI;
            int dv, j;
            double[] inputdataR, inputdataI;
            for (dv = 0; dv < ((GraphType) input).getDependentVariables(); dv++) {
                if (((GraphType) input).isArithmeticArray(dv)) {
                    tempR = new FlatArray(((GraphType) input).getDataArrayReal(dv));
                    inputdataR = (double[]) tempR.getFlatArray();
                    complexInput = ((GraphType) input).isDependentComplex(dv);
                    if (complexInput) {
                        tempI = new FlatArray(((GraphType) input).getDataArrayImag(dv));
                        inputdataI = (double[]) tempI.getFlatArray();
                        for (j = 0; j < inputdataI.length; j++) {
                            inputdataR[j] += (generator.nextGaussian()) * scale + mean;
                            inputdataI[j] += (generator.nextGaussian()) * scale + mean;
                        }
                        ((GraphType) output).setDataArrayReal(tempR.restoreArray(false), dv);
                        ((GraphType) output).setDataArrayImag(tempI.restoreArray(false), dv);
                    } else {
                        for (j = 0; j < inputdataR.length; j++) {
                            inputdataR[j] += (generator.nextGaussian()) * scale + mean;
                        }
                        ((GraphType) output).setDataArrayReal(tempR.restoreArray(false), dv);
                    }
                }
            }
        } else if (input instanceof Const) {
            double r, i;
            r = ((Const) input).getReal();
            complexInput = ((Const) input).isComplex();
            if (complexInput) {
                i = ((Const) input).getImag();
                i += (generator.nextGaussian()) * scale + mean;
                ((Const) output).setImag(i);
            }
            r += (generator.nextGaussian()) * scale + mean;
            ((Const) output).setReal(r);
        }

        output(output);
    }


    /**
     * Initialses information specific to Gaussian.
     */
    public void init() {
        super.init();
        //setUseGUIBuilder(true);

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(1);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

//        setResizableInputs(false);
//        setResizableOutputs(true);

        String guilines = "";
        guilines += "Set mean of normal distribution $title mean Scroller -1.0 1.0 0.0\n";
        guilines += "Set standard deviation of normal distribution $title stdDev Scroller 1.0 10.0 1.0\n";
        setGUIBuilderV2Info(guilines);

        generator = new Random();

    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format.
     */
//    public void setGUIInformation() {
//        addGUILine("Set mean of normal distribution $title mean Scroller -1.0 1.0 0.0");
//        addGUILine("Set standard deviation of normal distribution $title stdDev Scroller 1.0 10.0 1.0");
//    }

    /**
     * Resets Gaussian
     */
    public void reset() {
        super.reset();
    }


    /**
     * Saves Gaussian's parameters.
     */
//    public void saveParameters() {
//        saveParameter("mean", mean);
//        saveParameter("stdDev", stdDev);
//    }

    /**
     * Used to set each of Gaussian's parameters.
     */
    public void parameterUpdate(String name, Object value) {
        //updateGUIParameter(name, value);

        if (name.equals("mean")) {
            mean = Str.strToDouble((String) value);
        }
        if (name.equals("stdDev")) {
            stdDev = Str.strToDouble((String) value);
        }
    }


    /**
     * @return a string containing the names of the types allowed to be input to Gaussian, each separated by a white
     *         space.
     */
//    public String inputTypes() {
//        return "GraphType Const";
//    }
//
//    /**
//     * @return a string containing the names of the types output from Gaussian, each separated by a white space.
//     */
//    public String outputTypes() {
//        return "GraphType Const";
//    }

    public String[] getInputTypes() {
        return new String[]{"triana.types.GraphType", "triana.type.Const"};
    }

    public String[] getOutputTypes() {
        return new String[]{"triana.types.GraphType", "triana.type.Const"};
    }

    /**
     *
     * @returns the location of the help file for this unit.
     */
    public String getHelpFile() {
        return "Gaussian.html";
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



















