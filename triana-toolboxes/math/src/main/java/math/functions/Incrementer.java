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
 * A Incrementer unit to add a given complex number to the elements of an input data array. The array can be real or
 * complex. The returned data type will be real or complex as appropriate.
 * <p/>
 * This Unit obeys the conventions of Triana Type 2 data types.
 *
 * @author Bernard Schutz
 * @version 1.0 26 February 2001
 */
public class Incrementer extends Unit {

    /**
     * Offset parameters will be added to each element of the input array.
     */
    double incrementReal = 0.0;
    double incrementImag = 0.0;
    boolean complex, complexInput, complexIncrement;

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Adds an increment constant to the elements of the input data.";
    }

    /**
     * ********************************************* ** USER CODE of Incrementer goes here    ***
     * *********************************************
     */
    public void process() {

        Object input, output;

        output = null;
        input = getInputAtNode(0);

        if (input instanceof EmptyingType) {
            return;
        }
        complexIncrement = (incrementImag != 0.0);
        if ((input instanceof SampleSet) && complexIncrement) {
            output = new ComplexSampleSet((SampleSet) input);
        } else if ((input instanceof Spectrum) && complexIncrement) {
            output = new ComplexSpectrum((Spectrum) input);
        } else {
            output = input;
        }
        Class outputClass = output.getClass();
        //setOutputType(outputClass);

        if ((incrementImag != 0.0) || (incrementReal != 0.0)) {

            if (input instanceof GraphType) {
                int dv, j;
                double d, drecip, offsetCos, offsetSin;
                double[] inputdataR, inputdataI;
                for (dv = 0; dv < ((GraphType) input).getDependentVariables(); dv++) {
                    if (((GraphType) input).isArithmeticArray(dv)) {
                        complexInput = ((GraphType) input).isDependentComplex(dv);
                        if (complexInput) {
                            FlatArray.incrementArray(((GraphType) output).getDataArrayReal(dv),
                                    ((GraphType) output).getDataArrayImag(dv), incrementReal, incrementImag);
                        } else {
                            FlatArray.incrementArray(((GraphType) output).getDataArrayReal(dv), null, incrementReal,
                                    incrementImag);
                        }
                    }
                }
            } else if (input instanceof Const) {
                double i = 0;
                double r = ((Const) input).getReal();
                complexInput = ((Const) input).isComplex();
                complex = (complexIncrement || complexInput);
                if (complex) {
                    ((Const) output).setImag(i + incrementImag);
                    ((Const) output).setReal(r + incrementReal);
                } else {
                    ((Const) output).setReal(r + incrementReal);
                }
            }

        }

        output(output);

    }


    /**
     * Initialses information specific to Incrementer.
     */
    public void init() {
        super.init();

//        setUseGUIBuilder(true);

//        setResizableInputs(false);
//        setResizableOutputs(true);

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);


        String guilines = "";
        guilines += "Set real part of increment $title incrementReal Scroller -100.0 100.0 0.0\n";
        guilines += "Set imaginary part of increment $title incrementImag Scroller -100.0 100.0 0.0\n";
        setGUIBuilderV2Info(guilines);


    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format.
     */
//    public void setGUIInformation() {
//        addGUILine("Set real part of increment $title incrementReal Scroller -100.0 100.0 0.0");
//        addGUILine("Set imaginary part of increment $title incrementImag Scroller -100.0 100.0 0.0");
//    }


    /**
     * Resets Incrementer
     */
    public void reset() {
        super.reset();
    }


    /**
     * Saves Incrementer's parameters.
     */
//    public void saveParameters() {
//        saveParameter("incrementReal", incrementReal);
//        saveParameter("incrementImag", incrementImag);
//    }


    /**
     * Used to set each of Incrementer's parameters.
     */
    public void parameterUpdate(String name, Object value) {
        //updateGUIParameter(name, value);

        if (name.equals("incrementReal")) {
            incrementReal = Str.strToDouble((String) value);
        }
        if (name.equals("incrementImag")) {
            incrementImag = Str.strToDouble((String) value);
        }
    }


    /**
     * @return a string containing the names of the types allowed to be input to Incrementer, each separated by a white
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
        return "Incrementer.html";
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



















