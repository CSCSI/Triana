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
 * A Sqrt unit to compute the square root of the elements of an input data array. The array can be real or complex. For
 * complex data the root returned is in the upper half of the complex plane. For real data the positive root is
 * returned.
 * <p/>
 * This Unit obeys the conventions of Triana Type 2 data types.
 *
 * @author Bernard Schutz
 * @version 2.1 13 January 2001
 */
public class Sqrt extends Unit {

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Forms the square root of the elements of the input data set";
    }

    /**
     * ********************************************* ** USER CODE of Sqrt goes here    ***
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
            if (FlatArray.minArray(((SampleSet) input).getData()) < 0) {
                output = new ComplexSampleSet((SampleSet) input);
            } else {
                output = input;
            }
        } else if (input instanceof Spectrum) {
            if (FlatArray.minArray(((Spectrum) input).getData()) < 0) {
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
            double mag, phase;
            double[] inputdataR, inputdataI;
            for (dv = 0; dv < ((GraphType) input).getDependentVariables(); dv++) {
                if (((GraphType) input).isArithmeticArray(dv)) {
                    tempR = new FlatArray(((GraphType) input).getDataArrayReal(dv));
                    inputdataR = (double[]) tempR.getFlatArray();
                    if (((GraphType) input).isDependentComplex(dv)) {
                        tempI = new FlatArray(((GraphType) input).getDataArrayImag(dv));
                        inputdataI = (double[]) tempI.getFlatArray();
                        for (j = 0; j < inputdataI.length; j++) {
                            mag = Math.sqrt(Math.sqrt(inputdataR[j] * inputdataR[j] + inputdataI[j] * inputdataI[j]));
                            phase = Math.atan2(inputdataI[j], inputdataR[j]) * 0.5;
                            if (phase < 0) {
                                phase += Math.PI;
                            }
                            inputdataR[j] = mag * Math.cos(phase);
                            inputdataI[j] = mag * Math.sin(phase);
                        }
                        ((GraphType) output).setDataArrayReal(tempR.restoreArray(false), dv);
                        ((GraphType) output).setDataArrayImag(tempI.restoreArray(false), dv);
                    } else {
                        if (FlatArray.minArray(inputdataR) >= 0) {
                            for (j = 0; j < inputdataR.length; j++) {
                                inputdataR[j] = Math.sqrt(inputdataR[j]);
                            }
                            ((GraphType) output).setDataArrayReal(tempR.restoreArray(false), dv);
                        } else {
                            //setText("WARNING: SEE DEBUG WINDOW!");
                            System.out.println("Warning: Some input real data to " + getTask().getToolName()
                                    + " are out of range (data < 0). Output will be complex.");
                            inputdataI = new double[inputdataR.length];
                            for (j = 0; j < inputdataI.length; j++) {
                                mag = Math.sqrt(Math.abs(inputdataR[j]));
                                if (inputdataR[j] < 0) {
                                    inputdataR[j] = 0.0;
                                    inputdataI[j] = mag;
                                } else {
                                    inputdataR[j] = mag;
                                    inputdataI[j] = 0.0;
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
            double r, i, mag, phase;
            r = ((Const) input).getReal();
            if (((Const) input).isComplex()) {
                i = ((Const) input).getImag();
                mag = Math.sqrt(Math.sqrt(r * r + i * i));
                phase = Math.atan2(i, r);
                if (phase < 0) {
                    phase += Math.PI;
                }
                r = mag * Math.cos(phase);
                i = mag * Math.sin(phase);
                ((Const) output).setImag(i);
            } else {
                if (r >= 0) {
                    r = Math.sqrt(r);
                } else {
                    //setText("WARNING: SEE DEBUG WINDOW!");
                    System.out.println("Warning: Input Const to " + getTask().getToolName() + " is real but out of range, (" + String.valueOf(r)
                            + " < 0). Output will be a complex Const.");
                    r = 0.0;
                    i = Math.sqrt(Math.abs(r));
                    ((Const) output).setImag(i);
                }
            }
            ((Const) output).setReal(r);

        }

        output(output);


    }


    /**
     * Initialses information specific to Sqrt.
     */
    public void init() {
        super.init();

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
    }

    /**
     * Resets Sqrt
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves Sqrt's parameters to the parameter file.
     */
    public void saveParameters() {
    }

    /**
     * Loads Sqrt's parameters of from the parameter file.
     */
    public void setParameter(String name, String value) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to Sqrt, each separated by a white space.
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
        return "Sqrt.html";
    }


    /**
     * @return Sqrt's parameter window sp that Triana can move and display it.
     */
    public Window getParameterWindow() {
        return null;
    }


    /**
     * Captures the events thrown out by Sqrt.
     */
//    public void actionPerformed(ActionEvent e) {
//        super.actionPerformed(e);   // we need this
//
//    }
}



















