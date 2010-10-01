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
 * A ScalerText unit to rescale (by a given complex number) the elements of an input data array. The array can be real
 * or complex. The returned data type will be real or complex as appropriate.
 * <p/>
 * This Unit obeys the conventions of Triana Type 2 data types.
 *
 * @author Bernard Schutz
 * @version 2.1 13 January 2001
 */
public class ScalerText extends Unit {

    /**
     * Offset parameters will be added to each element of the input array.
     */
    double scaleReal = 0.0;
    double scaleImag = 0.0;
    boolean complex, complexInput, complexScale;

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Multiplies a scale constant into the elements of the input data.";
    }

    /**
     * ********************************************* ** USER CODE of ScalerText goes here    ***
     * *********************************************
     */
    public void process() {

        Object input, output;

        output = null;
        input = getInputAtNode(0);
        if (input instanceof EmptyingType) {
            return;
        }
        complexScale = (scaleImag != 0.0);
        if ((input instanceof SampleSet) && complexScale) {
            output = new ComplexSampleSet((SampleSet) input);
        } else if ((input instanceof Spectrum) && complexScale) {
            output = new ComplexSpectrum((Spectrum) input);
        } else {
            output = input;
        }
        Class outputClass = output.getClass();
        //setOutputType(outputClass);

        if ((scaleImag != 0.0) || (scaleReal != 1.0)) {

            if (input instanceof GraphType) {
                int dv, j;
                double d, drecip, offsetCos, offsetSin;
                double[] inputdataR, inputdataI;
                for (dv = 0; dv < ((GraphType) input).getDependentVariables(); dv++) {
                    if (((GraphType) input).isArithmeticArray(dv)) {
                        complexInput = ((GraphType) input).isDependentComplex(dv);
                        if (complexInput) {
                            FlatArray.scaleArray(((GraphType) output).getDataArrayReal(dv),
                                    ((GraphType) output).getDataArrayImag(dv), scaleReal, scaleImag);
                        } else {
                            FlatArray.scaleArray(((GraphType) output).getDataArrayReal(dv), null, scaleReal, scaleImag);
                        }
                    }
                }
            } else if (input instanceof Const) {
                double r, i, d;
                r = ((Const) input).getReal();
                complexInput = ((Const) input).isComplex();
                complex = (complexScale || complexInput);
                if (complex) {
                    if (complexInput) {
                        i = ((Const) input).getImag();
                        d = r;
                        r = d * scaleReal - i * scaleImag;
                        i = i * scaleReal + d * scaleImag;
                    } else {
                        d = r;
                        r = d * scaleReal;
                        i = d * scaleImag;
                    }
                    ((Const) output).setImag(i);
                    ((Const) output).setReal(r);
                } else {
                    ((Const) output).setReal(r * scaleReal);
                }
            }

        }

        output(output);

    }


    /**
     * Initialses information specific to ScalerText.
     */
    public void init() {
        super.init();

//        setUseGUIBuilder(true);

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(1);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(1);
//        setResizableInputs(false);
//        setResizableOutputs(true);

        String guilines = "";
        guilines += "Set real part of scaling multiplier $title scaleReal TextField scaleReal\n";
        guilines += "Set imag part of scaling multiplier $title scaleImag TextField scaleReal\n";
        setGUIBuilderV2Info(guilines);


    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format.
     */
//    public void setGUIInformation() {
//        addGUILine("Set real part of scaling multiplier $title scaleReal TextField scaleReal");
//        addGUILine("Set imag part of scaling multiplier $title scaleImag TextField scaleReal");
//    }


    /**
     * Resets ScalerText
     */
    public void reset() {
        super.reset();
    }


    /**
     * Saves ScalerText's parameters.
     */
//    public void saveParameters() {
//        saveParameter("scaleReal", scaleReal);
//        saveParameter("scaleImag", scaleImag);
//    }


    /**
     * Used to set each of ScalerText's parameters.
     */
    public void parameterUpdate(String name, Object value) {
        //updateGUIParameter(name, value);

        if (name.equals("scaleReal")) {
            scaleReal = Str.strToDouble((String) value);
        }
        if (name.equals("scaleImag")) {
            scaleImag = Str.strToDouble((String) value);
        }
    }


    /**
     * @return a string containing the names of the types allowed to be input to ScalerText, each separated by a white
     *         space.
     */
    public String[] getInputTypes() {
        return new String[]{"triana.types.GraphType", "triana.types.Const"};
    }

    public String[] getOutputTypes() {
        return new String[]{"triana.types.GraphType", "triana.types.Const"};
    }

    /**
     * @returns the location of the help file for this unit.
     */
    public String getHelpFile() {
        return "ScalerText.html";
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



















