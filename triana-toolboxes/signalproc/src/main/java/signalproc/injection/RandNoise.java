package signalproc.injection;

import org.trianacode.taskgraph.Unit;
import triana.types.Const;
import triana.types.EmptyingType;
import triana.types.GraphType;
import triana.types.util.FlatArray;
import triana.types.util.Str;

/**
 * A RandNoise unit to add uniformly distributed noise to the elements of an input data array. The array can be real or
 * complex. The returned data type will be the same as the input. Parameters can be used to set the lower and upper
 * bounds on the distribution.
 * <p/>
 * This Unit obeys the conventions of Triana Type 2 data types.
 *
 * @author Bernard Schutz
 * @version 2.0 20 June 2000
 */
public class RandNoise extends Unit {

    /**
     * Offset parameters will be added to each element of the input array.
     */
    double lowerBound = 0.0;
    double upperBound = 1.0;
    boolean complexInput;
    double scale;

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Adds uniform noise to the elements of the input data.";
    }

    /**
     * ********************************************* ** USER CODE of RandNoise goes here    ***
     * *********************************************
     */
    public void process() {

        Object input, output;

        output = null;
        input = getInputAtNode(0);
        if (input instanceof EmptyingType) {
            return;
        }
        output = input;
        Class outputClass = output.getClass();
        //setOutputType(outputClass);
        scale = upperBound - lowerBound;

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
                            inputdataR[j] += Math.random() * scale + lowerBound;
                            inputdataI[j] += Math.random() * scale + lowerBound;
                        }
                        ((GraphType) output).setDataArrayReal(tempR.restoreArray(false), dv);
                        ((GraphType) output).setDataArrayImag(tempI.restoreArray(false), dv);
                    } else {
                        for (j = 0; j < inputdataR.length; j++) {
                            inputdataR[j] += Math.random() * scale + lowerBound;
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
                i += Math.random() * scale + lowerBound;
                ((Const) output).setImag(i);
            }
            r += Math.random() * scale + lowerBound;
            ((Const) output).setReal(r);
        }

        output(output);

    }


    /**
     * Initialses information specific to RandNoise.
     */
    public void init() {
        super.init();

//        setUseGUIBuilder(true);


        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(1);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        String guilines = "";
        guilines += "Set lower bound of uniform distribution $title lowerBound Scroller -1.0 1.0 0.0\n";
        guilines += "Set upper bound of uniform distribution $title upperBound Scroller 1.0 10.0 1.0\n";
        setGUIBuilderV2Info(guilines);

//        setResizableInputs(false);
//        setResizableOutputs(true);

    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format.
     */
//    public void setGUIInformation() {
//        addGUILine("Set lower bound of uniform distribution $title lowerBound Scroller -1.0 1.0 0.0");
//        addGUILine("Set upper bound of uniform distribution $title upperBound Scroller 1.0 10.0 1.0");
//    }

    /**
     * Resets RandNoise
     */
    public void reset() {
        super.reset();
    }


    /**
     * Saves RandNoise's parameters.
     */
//    public void saveParameters() {
//        saveParameter("lowerBound", lowerBound);
//        saveParameter("upperBound", upperBound);
//    }


    /**
     * Used to set each of RandNoise's parameters.
     */
    public void parameterUpdate(String name, Object value) {
        //updateGUIParameter(name, value);

        if (name.equals("lowerBound")) {
            lowerBound = Str.strToDouble((String) value);
        }
        if (name.equals("upperBound")) {
            upperBound = Str.strToDouble((String) value);
        }
    }


    /**
     * @return a string containing the names of the types allowed to be input to RandNoise, each separated by a white
     *         space.
     */
//    public String inputTypes() {
//        return "GraphType Const";
//    }
//
//    /**
//     * @return a string containing the names of the types output from RandNoise, each separated by a white space.
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
        return "RandNoise.html";
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



















