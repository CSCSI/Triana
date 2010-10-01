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
 * A Phase unit to compute the phase of the complex numbers in an input data array. The phase returned is larger than
 * -Pi and smaller than or equal to +Pi.
 * <p/>
 * This Unit obeys the conventions of Triana Type 2 data types.
 *
 * @author Bernard Schutz
 * @version 2.1 13 January 2001
 */
public class Phase extends Unit {

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Returns the phase of the complex elements of the input data set";
    }

    /**
     * ********************************************* ** USER CODE of Phase goes here    ***
     * *********************************************
     */
    public void process() {


        Object input, output;

        output = null;
        input = getInputAtNode(0);
        if (input instanceof EmptyingType) {
            return;
        }
        if (input instanceof ComplexSampleSet) {
            output = new SampleSet((ComplexSampleSet) input);
            System.out.println("Input is ComplexSampleSet");
        } else if (input instanceof ComplexSpectrum) {
            output = new Spectrum((ComplexSpectrum) input);
            System.out.println("Input is ComplexSpectrum");
        } else {
            output = input;
        }
        Class outputClass = output.getClass();
        //setOutputType(outputClass);
        System.out.println("Output class is " + outputClass.getName());


        if (input instanceof GraphType) {
            FlatArray tempR, tempI;
            int dv, j;
            double[] inputdataR, inputdataI;
            for (dv = 0; dv < ((GraphType) input).getDependentVariables(); dv++) {
                if (((GraphType) input).isArithmeticArray(dv)) {
                    tempR = new FlatArray(((GraphType) input).getDataArrayReal(dv));
                    inputdataR = (double[]) tempR.getFlatArray();
                    if (((GraphType) input).isDependentComplex(dv)) {
                        tempI = new FlatArray(((GraphType) input).getDataArrayImag(dv));
                        inputdataI = (double[]) tempI.getFlatArray();
                        for (j = 0; j < inputdataI.length; j++) {
                            System.out.println("Checkpoint inside loop");
                            inputdataR[j] = Math.atan2(inputdataI[j], inputdataR[j]);
                        }
                        ((GraphType) output).setDataArrayReal(tempR.restoreArray(false), dv);
                    } else {
                        ((GraphType) output).setDataArrayReal(null, dv);
                    }
                }
            }
        } else if (input instanceof Const) {
            double r, i;
            r = ((Const) input).getReal();
            if (((Const) input).isComplex()) {
                i = ((Const) input).getImag();
                r = Math.atan2(r, i);
            } else {
                r = 0.0;
            }
            ((Const) output).setReal(r);

        }

        output(output);


    }


    /**
     * Initialses information specific to Phase.
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
        setMaximumInputNodes(1);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);                       
    }

    /**
     * Resets Phase
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves Phase's parameters to the parameter file.
     */
    public void saveParameters() {
    }

    /**
     * Loads Phase's parameters of from the parameter file.
     */
    public void setParameter(String name, String value) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to Phase, each separated by a white
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
        return "Phase.html";
    }


    /**
     * @return Phase's parameter window sp that Triana can move and display it.
     */
    public Window getParameterWindow() {
        return null;
    }


    /**
     * Captures the events thrown out by Phase.
     */
//    public void actionPerformed(ActionEvent e) {
//        super.actionPerformed(e);   // we need this
//
//    }
}



















