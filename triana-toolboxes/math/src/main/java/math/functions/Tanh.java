package math.functions;

import org.trianacode.taskgraph.Unit;
import triana.types.ComplexSampleSet;
import triana.types.ComplexSpectrum;
import triana.types.Const;
import triana.types.EmptyingType;
import triana.types.GraphType;
import triana.types.SampleSet;
import triana.types.Spectrum;
import triana.types.util.FlatArray;
import triana.types.util.Str;

/**
 * A Tanh unit to apply the hyperbolic tangent function to the elements of an input data array. The array can be real or
 * complex. The returned data type will be real or complex as appropriate.
 * <p/>
 * This Unit obeys the conventions of Triana Type 2 data types.
 *
 * @author Bernard Schutz
 * @version 2.1 13 January 2001
 */
public class Tanh extends Unit {

    /**
     * Offset parameters will be added to each element of the input array.
     */
    double offsetReal = 0.0;
    double offsetImag = 0.0;
    boolean complex, complexInput, complexPhase;

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Applies the tanh function to the elements of the input data.";
    }

    /**
     * ********************************************* ** USER CODE of Tanh goes here    ***
     * *********************************************
     */
    public void process() {

        Object input, output;

        output = null;
        input = getInputAtNode(0);
        if (input instanceof EmptyingType) {
            return;
        }

        complexPhase = (offsetImag != 0.0);
        if ((input instanceof SampleSet) && complexPhase) {
            output = new ComplexSampleSet((SampleSet) input);
        } else if ((input instanceof Spectrum) && complexPhase) {
            output = new ComplexSpectrum((Spectrum) input);
        } else {
            output = input;
        }
        Class outputClass = output.getClass();
        //setOutputType(outputClass);


        if (input instanceof GraphType) {
            FlatArray tempR, tempI;
            int dv, j;
            double d, drecip, f, sin, cos, sinh, cosh, mag, re, im, sinh2, cosh2;
            double[] inputdataR, inputdataI;
            for (dv = 0; dv < ((GraphType) input).getDependentVariables(); dv++) {
                if (((GraphType) input).isArithmeticArray(dv)) {
                    tempR = new FlatArray(((GraphType) input).getDataArrayReal(dv));
                    inputdataR = (double[]) tempR.getFlatArray();
                    complexInput = ((GraphType) input).isDependentComplex(dv);
                    complex = (complexPhase || complexInput);
                    if (complex) {
                        if (complexInput) {
                            tempI = new FlatArray(((GraphType) input).getDataArrayImag(dv));
                            inputdataI = (double[]) tempI.getFlatArray();
                            for (j = 0; j < inputdataI.length; j++) {
                                d = Math.exp(inputdataR[j] + offsetReal);
                                drecip = 1.0 / d;
                                f = inputdataI[j] + offsetImag;
                                sin = Math.sin(f);
                                cos = Math.cos(f);
                                sinh = (d - drecip) * 0.5;
                                cosh = (d + drecip) * 0.5;
                                mag = cos * cos * cosh * cosh + sin * sin * sinh * sinh;
                                im = sin * cos;
                                re = sinh * cosh;
                                if (mag == 0) {
                                    if (re == 0) {
                                        inputdataR[j] = Double.NaN;
                                    } else {
                                        inputdataR[j] = (re > 0) ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
                                    }
                                    if (im == 0) {
                                        inputdataI[j] = Double.NaN;
                                    } else {
                                        inputdataI[j] = (im > 0) ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
                                    }
                                } else {
                                    inputdataR[j] = re / mag;
                                    inputdataI[j] = im / mag;
                                }
                            }
                            ((GraphType) output).setDataArrayReal(tempR.restoreArray(false), dv);
                            ((GraphType) output).setDataArrayImag(tempI.restoreArray(false), dv);
                        } else {
                            sin = Math.sin(offsetImag);
                            cos = Math.cos(offsetImag);
                            inputdataI = new double[inputdataR.length];
                            for (j = 0; j < inputdataI.length; j++) {
                                d = Math.exp(inputdataR[j] + offsetReal);
                                drecip = 1.0 / d;
                                cosh = (d + drecip) * 0.5;
                                sinh = (d - drecip) * 0.5;
                                mag = cos * cos * cosh * cosh + sin * sin * sinh * sinh;
                                im = sin * cos;
                                re = sinh * cosh;
                                if (mag == 0) {
                                    if (re == 0) {
                                        inputdataR[j] = Double.NaN;
                                    } else {
                                        inputdataR[j] = (re > 0) ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
                                    }
                                    if (im == 0) {
                                        inputdataI[j] = Double.NaN;
                                    } else {
                                        inputdataI[j] = (im > 0) ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
                                    }
                                } else {
                                    inputdataR[j] = re / mag;
                                    inputdataI[j] = im / mag;
                                }
                            }
                            ((GraphType) output).setDataArrayReal(tempR.restoreArray(false), dv);
                            tempR.setFlatArray(inputdataI);
                            ((GraphType) output).setDataArrayImag(tempR.restoreArray(true), dv);
                        }
                    } else {
                        for (j = 0; j < inputdataR.length; j++) {
                            d = Math.exp(inputdataR[j] + offsetReal);
                            drecip = 1.0 / d;
                            cosh2 = d + drecip;
                            sinh2 = d - drecip;
                            inputdataR[j] = sinh2 / cosh2;
                        }
                        ((GraphType) output).setDataArrayReal(tempR.restoreArray(false), dv);
                    }
                }
            }
        } else if (input instanceof Const) {
            double r, i, d, f, drecip, sin, cos, sinh, cosh, mag, re, im, cosh2, sinh2;
            complexInput = ((Const) input).isComplex();
            complex = (complexPhase || complexInput);
            if (complex) {
                if (complexInput) {
                    d = Math.exp(((Const) input).getReal() + offsetReal);
                    drecip = 1.0 / d;
                    f = ((Const) input).getImag() + offsetImag;
                    sin = Math.sin(f);
                    cos = Math.cos(f);
                    sinh = (d - drecip) * 0.5;
                    cosh = (d + drecip) * 0.5;
                    mag = cos * cos * cosh * cosh + sin * sin * sinh * sinh;
                    im = sin * cos;
                    re = sinh * cosh;
                    if (mag == 0) {
                        if (re == 0) {
                            r = Double.NaN;
                        } else {
                            r = (re > 0) ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
                        }
                        if (im == 0) {
                            i = Double.NaN;
                        } else {
                            i = (im > 0) ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
                        }
                    } else {
                        r = re / mag;
                        i = im / mag;
                    }
                } else {
                    d = Math.exp(((Const) input).getReal() + offsetReal);
                    drecip = 1.0 / d;
                    sin = Math.sin(offsetImag);
                    cos = Math.cos(offsetImag);
                    sinh = (d - drecip) * 0.5;
                    cosh = (d + drecip) * 0.5;
                    mag = cos * cos * cosh * cosh + sin * sin * sinh * sinh;
                    im = sin * cos;
                    re = sinh * cosh;
                    if (mag == 0) {
                        if (re == 0) {
                            r = Double.NaN;
                        } else {
                            r = (re > 0) ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
                        }
                        if (im == 0) {
                            i = Double.NaN;
                        } else {
                            i = (im > 0) ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
                        }
                    } else {
                        r = re / mag;
                        i = im / mag;
                    }
                }
                ((Const) output).setImag(i);
                ((Const) output).setReal(r);
            } else {
                d = Math.exp(((Const) input).getReal() + offsetReal);
                drecip = 1.0 / d;
                cosh2 = d + drecip;
                sinh2 = d - drecip;
                r = sinh2 / cosh2;
                ((Const) output).setReal(r);
            }
        }

        output(output);

    }


    /**
     * Initialses information specific to Tanh.
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
        guilines += "Set real part of offset of argument $title offsetReal Scroller -1.0 1.0 0.0\n";
        guilines += "Set imaginary part of offset of argument $title offsetImag Scroller -1.0 1.0 0.0\n";
        setGUIBuilderV2Info(guilines);               
    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format.
     */
//    public void setGUIInformation() {
//        addGUILine("Set real part of offset of argument $title offsetReal Scroller -1.0 1.0 0.0");
//        addGUILine("Set imaginary part of offset of argument $title offsetImag Scroller -1.0 1.0 0.0");
//    }


    /**
     * Resets Tanh
     */
    public void reset() {
        super.reset();
    }


    /**
     * Saves Tanh's parameters.
     */
//    public void saveParameters() {
//        saveParameter("offsetReal", offsetReal);
//        saveParameter("offsetImag", offsetImag);
//    }


    /**
     * Used to set each of Tanh's parameters.
     */
    public void parameterUpdate(String name, Object value) {
        //updateGUIParameter(name, value);

        if (name.equals("offsetReal")) {
            offsetReal = Str.strToDouble((String) value);
        }
        if (name.equals("offsetImag")) {
            offsetImag = Str.strToDouble((String) value);
        }
    }


    /**
     * @return a string containing the names of the types allowed to be input to Tanh, each separated by a white space.
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
        return "Tanh.html";
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



















