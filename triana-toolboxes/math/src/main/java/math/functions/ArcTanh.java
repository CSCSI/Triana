package math.functions;

import java.awt.Window;
import java.awt.event.ActionEvent;

import org.trianacode.taskgraph.Unit;
import triana.types.ComplexSampleSet;
import triana.types.ComplexSpectrum;
import triana.types.Const;
import triana.types.EmptyingType;
import triana.types.GraphType;
import triana.types.SampleSet;
import triana.types.Spectrum;
import triana.types.util.FlatArray;


/**
 * An ArcTanh unit to compute the inverse hyperbolic tangent of the elements of an input data object. The input data can
 * be either GraphType or Const. The function is applied to each element of each arithmetic dependent-data array of an
 * input GraphType, regardless of dimensionality.
 * <p/>
 * The input data arrays can be real or complex. If the data are real and in the range [-1, 1], then the returned data
 * set will be real. If the data are real but at least one has absolute value greater than 1.0, or if the input data are
 * complex, then the output data will be complex. If necessary input SampleSet and Spectrum sets will be converted to
 * ComplexSampleSet and ComplexSpectrum on output.
 * <p/>
 * This Unit obeys the conventions of Triana Type 2 data types.
 *
 * @author Bernard Schutz
 * @version 2.1 13 January 2001
 */
public class ArcTanh extends Unit {

    double Pi2 = Math.PI * 0.5;

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Computes the inverse hyperbolic tangent of the elements of the input data set";
    }

    /**
     * ********************************************* ** USER CODE of ArcTanh goes here    ***
     * *********************************************
     */
    public void process() {


        Object input, output;

        output = null;
        input = getInputAtNode(0);
        if (input instanceof EmptyingType) {
            return;
        }
        if (input instanceof SampleSet) {
            if ((FlatArray.maxArray(((SampleSet) input).getData()) > 1.0) ||
                    (FlatArray.minArray(((SampleSet) input).getData()) < -1.0)) {
                output = new ComplexSampleSet((SampleSet) input);
            } else {
                output = input;
            }
        } else if (input instanceof Spectrum) {
            if ((FlatArray.maxArray(((Spectrum) input).getData()) > 1.0) ||
                    (FlatArray.minArray(((Spectrum) input).getData()) < -1.0)) {
                output = new ComplexSpectrum((Spectrum) input);
            } else {
                output = input;
            }
        } else {
            output = input;
        }
        Class outputClass = output.getClass();
        //setOutputType(outputClass);


        if (input instanceof GraphType) {
            FlatArray tempR, tempI;
            int dv, j;
            double y2, xp, xm;
            double[] inputdataR, inputdataI;
            for (dv = 0; dv < ((GraphType) input).getDependentVariables(); dv++) {
                if (((GraphType) input).isArithmeticArray(dv)) {
                    tempR = new FlatArray(((GraphType) input).getDataArrayReal(dv));
                    inputdataR = (double[]) tempR.getFlatArray();
                    if (((GraphType) input).isDependentComplex(dv)) {
                        tempI = new FlatArray(((GraphType) input).getDataArrayImag(dv));
                        inputdataI = (double[]) tempI.getFlatArray();
                        for (j = 0; j < inputdataI.length; j++) {
                            y2 = inputdataI[j] * inputdataI[j];
                            xp = 1.0 + inputdataR[j];
                            xm = 1.0 - inputdataR[j];
                            inputdataR[j] = Math.log((xp * xp + y2) / (xm * xm + y2)) * 0.25;
                            inputdataI[j] = Math.atan2(2 * inputdataI[j], xp * xm - y2) * 0.5;
                        }
                        ((GraphType) output).setDataArrayReal(tempR.restoreArray(false), dv);
                        ((GraphType) output).setDataArrayImag(tempI.restoreArray(false), dv);
                    } else {
                        if ((FlatArray.maxArray(inputdataR) <= 1.0) &&
                                (FlatArray.minArray(inputdataR) >= -1.0)) {
                            for (j = 0; j < inputdataR.length; j++) {
                                if (Math.abs(inputdataR[j]) < 1.0) {
                                    inputdataR[j] = Math.log((1.0 + inputdataR[j]) / (1.0 - inputdataR[j])) * 0.5;
                                } else if (Math.abs(inputdataR[j]) == 1.0) {
                                    inputdataR[j] = Double.POSITIVE_INFINITY;
                                } else {
                                    inputdataR[j] = Double.NEGATIVE_INFINITY;
                                }
                            }
                            ((GraphType) output).setDataArrayReal(tempR.restoreArray(false), dv);
                        } else {
                            //setText("WARNING: SEE DEBUG WINDOW!");
                            System.out.println("Warning: Some input real data to " + getTask().getToolName()
                                    + " are out of range (abs(data) > 1). Output will be complex.");
                            inputdataI = new double[inputdataR.length];
                            for (j = 0; j < inputdataI.length; j++) {
                                if (Math.abs(inputdataR[j]) < 1) {
                                    inputdataR[j] = Math.log((1.0 + inputdataR[j]) / (1.0 - inputdataR[j])) * 0.5;
                                    inputdataI[j] = 0.0;
                                } else if (inputdataR[j] == 1.0) {
                                    inputdataR[j] = Double.POSITIVE_INFINITY;
                                } else if (inputdataR[j] == -1.0) {
                                    inputdataR[j] = Double.NEGATIVE_INFINITY;
                                } else {
                                    inputdataR[j] = Math.log(Math.abs((1.0 + inputdataR[j]) / (1.0 - inputdataR[j])))
                                            * 0.5;
                                    inputdataI[j] = Pi2;
                                }
                            }
                            ((GraphType) output).setDataArrayReal(tempR.restoreArray(false), dv);
                            tempR.setFlatArray(inputdataI);
                            ((GraphType) output).setDataArrayImag(tempR.restoreArray(true), dv);
                        }
                    }
                }
            }
        } else if (input instanceof Const) {
            double r, i, y2, xp, xm;
            r = ((Const) input).getReal();
            if (((Const) input).isComplex()) {
                i = ((Const) input).getImag();
                y2 = i * i;
                xp = 1.0 + r;
                xm = 1.0 - r;
                r = Math.log((xp * xp + y2) / (xm * xm + y2)) * 0.25;
                i = Math.atan2(2 * i, xp * xm - y2) * 0.5;
                ((Const) output).setImag(i);
            } else {
                if (Math.abs(r) < 1.0) {
                    r = Math.log((1.0 + r) / (1.0 - r)) * 0.5;
                } else if (r == 1.0) {
                    r = Double.POSITIVE_INFINITY;
                } else if (r == -1.0) {
                    r = Double.NEGATIVE_INFINITY;
                } else {
                    //setText("WARNING: SEE DEBUG WINDOW!");
                    System.out.println("Warning: Input Const to " + getTask().getToolName() + " is real but out of range, (abs("
                            + String.valueOf(r) + ") > 1). Output will be a complex Const.");
                    r = Math.log(Math.abs((1.0 + r) / (1.0 - r))) * 0.5;
                    i = Pi2;
                    ((Const) output).setImag(i);
                }
            }
            ((Const) output).setReal(r);

        }

        output(output);


    }


    /**
     * Initialses information specific to ArcTanh.
     */
    public void init() {
        super.init();

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);


//        setResizableInputs(false);
//        setResizableOutputs(true);
//        // This is to ensure that we receive arrays containing double-precision numbers
//        setRequireDoubleInputs(true);
//        setCanProcessDoubleArrays(true);

    }

    /**
     * Resets ArcTanh
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves ArcTanh's parameters to the parameter file.
     */
    public void saveParameters() {
    }

    /**
     * Loads ArcTanh's parameters of from the parameter file.
     */
    public void setParameter(String name, String value) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to ArcTanh, each separated by a white
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
        return "ArcTanh.html";
    }


    /**
     * @return ArcTanh's parameter window sp that Triana can move and display it.
     */
    public Window getParameterWindow() {
        return null;
    }


    /**
     * Captures the events thrown out by ArcTanh.
     */
//    public void actionPerformed(ActionEvent e) {
//        super.actionPerformed(e);   // we need this
//
//    }
}



















