package math.functions;

import org.trianacode.gui.windows.ErrorDialog;
import org.trianacode.taskgraph.Unit;
import triana.types.ComplexSampleSet;
import triana.types.ComplexSpectrum;
import triana.types.Const;
import triana.types.GraphType;
import triana.types.SampleSet;
import triana.types.Spectrum;
import triana.types.util.FlatArray;

/**
 * Computes logarithm to base 10
 *
 * @author B F Schutz
 * @version $Revision: 2921 $
 */


public class Log10 extends Unit {


    /*
    * Called whenever there is data for the unit to process
    */

    public void process() throws Exception {


        Object input, output;

        input = getInputAtNode(0);
        output = input;
        double norm = 1.0 / Math.log(10.0);
        double norm2 = norm / 2.0;

        if (input instanceof GraphType) {

            FlatArray tempR, tempI;
            int dv, j;
            double d, e, thisAngle;
            double[] inputdataR, inputdataI;
            boolean makeComplex = false;

            for (dv = 0; dv < ((GraphType) input).getDependentVariables(); dv++) {
                if (((GraphType) input).isArithmeticArray(dv)) {
                    tempR = new FlatArray(((GraphType) input).getDataArrayReal(dv));
                    inputdataR = (double[]) tempR.getFlatArray();
                    if (((GraphType) input).isDependentComplex(dv)) {
                        tempI = new FlatArray(((GraphType) input).getDataArrayImag(dv));
                        inputdataI = (double[]) tempI.getFlatArray();
                        for (j = 0; j < inputdataI.length; j++) {
                            e = inputdataR[j];
                            d = Math.log(e * e + inputdataI[j] * inputdataI[j]) * norm2;
                            thisAngle = Math.atan2(inputdataI[j], inputdataR[j]) * norm;
                            inputdataI[j] = thisAngle;
                            inputdataR[j] = d;
                        }
                        ((GraphType) output).setDataArrayImag(tempI.restoreArray(false), dv);
                    } else {
                        inputdataI = new double[inputdataR.length];
                        FlatArray.initializeArray(inputdataI);
                        for (j = 0; j < inputdataR.length; j++) {
                            d = inputdataR[j];
                            if (d == 0) {
                                new ErrorDialog(null,
                                        "For input element " + String.valueOf(j) + " to unit " + getToolName()
                                                + "  is exactly zero. Logarithm set to negative infinity.");
                                inputdataR[j] = Double.NEGATIVE_INFINITY;
                            } else if (d < 0) {
                                makeComplex = true;
                                inputdataI[j] = Math.PI * norm;
                                inputdataR[j] = Math.log(-d) * norm;
                            } else {
                                inputdataR[j] = Math.log(d) * norm;
                            }
                        }
                    }
                    ((GraphType) output).setDataArrayReal(tempR.restoreArray(false), dv);
                    if (makeComplex) {
                        if (output instanceof SampleSet) {
                            output = new ComplexSampleSet((SampleSet) output);
                        } else if (output instanceof Spectrum) {
                            output = new ComplexSpectrum((Spectrum) output);
                        }
                        tempR.setFlatArray(inputdataI);
                        ((GraphType) output).setDataArrayImag(tempR.restoreArray(true), dv);
                    }
                }
            }
        } else if (input instanceof Const) {
            double r, i, d;
            r = ((Const) input).getReal();
            if (((Const) input).isComplex()) {
                i = ((Const) input).getImag();
                d = Math.log(r) * norm;
                i = Math.atan2(i, r) * norm;
                r = d;
                ((Const) output).setImag(i);
            } else {
                r = Math.log(r) * norm;
            }
            ((Const) output).setReal(r);
        }

        output(output);


    }


    /**
     * Called when the unit is created. Initialises the unit's properties and parameters.
     */
    public void init() {
        super.init();

        // Initialise node properties
        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(1);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        // Initialise parameter update policy
        setParameterUpdatePolicy(PROCESS_UPDATE);

        // Initialise pop-up description and help file location
        setPopUpDescription("Computes logarithm to base 10");
        setHelpFileLocation("Log10.html");
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up Log10 (e.g. close open files) 
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables 
    }


    /**
     * @return an array of the input types for Log10
     */
    public String[] getInputTypes() {
        return new String[]{"GraphType", "Const"};
    }

    /**
     * @return an array of the output types for Log10
     */
    public String[] getOutputTypes() {
        return new String[]{"GraphType", "Const"};
    }

}
