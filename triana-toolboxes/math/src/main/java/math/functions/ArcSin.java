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
 * An ArcSin unit to compute the inverse sine of the elements of an input data object. The input data can be either
 * GraphType or Const. The function is applied to each element of each arithmetic dependent-data array of an input
 * GraphType, regardless of dimensionality.
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
public class ArcSin extends Unit {

    double Pi2 = Math.PI / 2.0;

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Computes the inverse sine of the elements of the input data set";
    }

    /**
     * ********************************************* ** USER CODE of ArcSin goes here    ***
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
            double p, q, ppq, tmp1, tmp2;
            double[] inputdataR, inputdataI;
            for (dv = 0; dv < ((GraphType) input).getDependentVariables(); dv++) {
                if (((GraphType) input).isArithmeticArray(dv)) {
                    tempR = new FlatArray(((GraphType) input).getDataArrayReal(dv));
                    inputdataR = (double[]) tempR.getFlatArray();
                    if (((GraphType) input).isDependentComplex(dv)) {
                        tempI = new FlatArray(((GraphType) input).getDataArrayImag(dv));
                        inputdataI = (double[]) tempI.getFlatArray();
                        for (j = 0; j < inputdataI.length; j++) {
                            if (inputdataI[j] != 0.0) {
                                tmp1 = 1.0 + inputdataR[j];
                                tmp2 = inputdataI[j] * inputdataI[j];
                                p = Math.sqrt(tmp1 * tmp1 + tmp2);
                                tmp1 -= 2.0;
                                q = Math.sqrt(tmp1 * tmp1 + tmp2);
                                ppq = (p + q) * 0.5;
                                inputdataR[j] = Math.asin(inputdataR[j] / ppq);
                                tmp1 = Math.log(ppq + Math.sqrt(ppq * ppq - 1.0));
                                inputdataI[j] = (inputdataI[j] < 0.0) ? -tmp1 : tmp1;
                            } else {
                                if (inputdataR[j] >= 1.0) {
                                    inputdataI[j] = Math
                                            .log(inputdataR[j] + Math.sqrt(inputdataR[j] * inputdataR[j] - 1.0));
                                    inputdataR[j] = Pi2;
                                } else {
                                    if (inputdataR[j] > -1.0) {
                                        inputdataI[j] = 0.0;
                                        inputdataR[j] = Math.asin(inputdataR[j]);
                                    } else {
                                        inputdataI[j] = Math
                                                .log(-inputdataR[j] + Math.sqrt(inputdataR[j] * inputdataR[j] - 1.0));
                                        inputdataR[j] = -Pi2;
                                    }
                                }
                            }
                        }
                        ((GraphType) output).setDataArrayReal(tempR.restoreArray(false), dv);
                        ((GraphType) output).setDataArrayImag(tempI.restoreArray(false), dv);
                    } else {
                        if ((FlatArray.maxArray(inputdataR) <= 1.0) &&
                                (FlatArray.minArray(inputdataR) >= -1.0)) {
                            for (j = 0; j < inputdataR.length; j++) {
                                inputdataR[j] = Math.asin(inputdataR[j]);
                            }
                            ((GraphType) output).setDataArrayReal(tempR.restoreArray(false), dv);
                        } else {
                            //setText("WARNING: SEE DEBUG WINDOW!");
                            System.out.println("Warning: Some input real data to " + getTask().getToolName()
                                    + " are out of range (abs(data) < 1). Output will be complex.");
                            inputdataI = new double[inputdataR.length];
                            for (j = 0; j < inputdataI.length; j++) {
                                if (Math.abs(inputdataR[j]) <= 1) {
                                    inputdataI[j] = 0.0;
                                    inputdataR[j] = Math.asin(inputdataR[j]);
                                } else if (inputdataR[j] >= 1.0) {
                                    inputdataI[j] = Math
                                            .log(inputdataR[j] + Math.sqrt(inputdataR[j] * inputdataR[j] - 1.0));
                                    inputdataR[j] = Pi2;
                                } else {
                                    inputdataI[j] = Math
                                            .log(-inputdataR[j] + Math.sqrt(inputdataR[j] * inputdataR[j] - 1.0));
                                    inputdataR[j] = -Pi2;
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
            double r, i, p, q, ppq, tmp1, tmp2;
            r = ((Const) input).getReal();
            i = 0;
            if (((Const) input).isComplex()) {
                i = ((Const) input).getImag();
                if (i != 0.0) {
                    tmp1 = 1.0 + r;
                    tmp2 = i * i;
                    p = Math.sqrt(tmp1 * tmp1 + tmp2);
                    tmp1 -= 2.0;
                    q = Math.sqrt(tmp1 * tmp1 + tmp2);
                    ppq = (p + q) * 0.5;
                    r = Math.asin(r / ppq);
                    tmp1 = Math.log(ppq + Math.sqrt(ppq * ppq - 1.0));
                    i = (i < 0.0) ? -tmp1 : tmp1;
                } else {
                    if (r >= 1.0) {
                        i = Math.log(r + Math.sqrt(r * r - 1.0));
                        i = Pi2;
                    } else {
                        if (r > -1.0) {
                            i = 0.0;
                            r = Math.asin(r);
                        } else {
                            i = Math.log(-r + Math.sqrt(r * r - 1.0));
                            r = -Pi2;
                        }
                    }
                }
                ((Const) output).setImag(i);
            } else if (Math.abs(r) <= 1.0) {
                r = Math.asin(r);
            } else {
                //setText("WARNING: SEE DEBUG WINDOW!");
                System.out.println("Warning: Input Const to " + getTask().getToolName() + " is real but out of range, (abs(" + String.valueOf(r)
                        + ") > 1). Output will be a complex Const.");
                if (r > 0) {
                    i = Math.log(r + Math.sqrt(r * r - 1.0));
                    r = Pi2;
                } else {
                    i = Math.log(-r + Math.sqrt(r * r - 1.0));
                    r = -Pi2;
                }
                ((Const) output).setImag(i);
            }
            ((Const) output).setReal(r);

        }

        output(output);


    }


    /**
     * Initialses information specific to ArcSin.
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
     * Resets ArcSin
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves ArcSin's parameters to the parameter file.
     */
    public void saveParameters() {
    }

    /**
     * Loads ArcSin's parameters of from the parameter file.
     */
    public void setParameter(String name, String value) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to ArcSin, each separated by a white
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
        return "ArcSin.html";
    }


    /**
     * @return ArcSin's parameter window sp that Triana can move and display it.
     */
    public Window getParameterWindow() {
        return null;
    }


    /**
     * Captures the events thrown out by ArcSin.
     */
//    public void actionPerformed(ActionEvent e) {
//        super.actionPerformed(e);   // we need this
//
//    }
}



















