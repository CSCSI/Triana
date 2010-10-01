package signalproc.algorithms;

import org.trianacode.gui.windows.ErrorDialog;
import org.trianacode.taskgraph.Unit;
import triana.types.SampleSet;
import triana.types.Spectrum;
import triana.types.VectorType;

/**
 * A Normalise unit to normalise the input by dividing each element by the sqrt of the sum of the squares
 *
 * @author Ian Taylor
 * @version 1.0 alpha 02 Feb 1998
 */
public class Normalise extends Unit {

    /**
     * ********************************************* ** USER CODE of Normalise goes here    ***
     * *********************************************
     */
    public void process() throws Exception {

        Object in = getInputAtNode(0);

        if (in instanceof SampleSet) {
            SampleSet wave = (SampleSet) in;
            double[] data = normalise(wave.data);
            output(new SampleSet(wave.samplingFrequency, data));
            return;
        }

        if (in instanceof Spectrum) {
            Spectrum wave = (Spectrum) in;
            double[] data = normalise(wave.data);
            output(new Spectrum(wave.samplingFrequency, data));
            return;
        }

        if (in instanceof VectorType) {
            VectorType raw = (VectorType) in;

            double[] data = normalise(raw.getData());
            output(new VectorType(data));
            return;
        }

        new ErrorDialog(null, "Invalid Input Data to " + getToolName());
        stop();
    }


    public double vectlength(double[] arr) {
        int i;
        double accum;

        accum = 0.0;
        for (i = 0; i < arr.length; ++i) {
            accum = accum + Math.pow(arr[i], 2.0);
        }
        return (Math.sqrt(accum));
    }

    public double[] normalise(double[] arr) { // normalizes a vector
        int i;
        double length;

        double[] arr1 = new double[arr.length];

        length = vectlength(arr);
        if (length > 0)                  /* Watch out division by 0 */ {
            for (i = 0; i < arr.length; ++i) {
                arr1[i] = (arr[i] / length);
            }
        } else {
            for (i = 0; i < arr.length; ++i) {
                arr1[i] = 0;
            }
        }
        return arr1;
    }

    /**
     * Initialses information specific to Normalise.
     */
    public void init() {
        super.init();

//        setResizableInputs(false);
//        setResizableOutputs(true);

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);  
    }


    /**
     * Reset's Normalise
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves parameters
     */
    public void saveParameters() {
    }

    /**
     * Sets the parameters
     */
    public void setParameter(String name, String value) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to Normalise, each separated by a white
     *         space.
     */
//    public String inputTypes() {
//        return "Spectrum VectorType SampleSet";
//    }
//
//    /**
//     * @return a string containing the names of the types output from Normalise, each separated by a white space.
//     */
//    public String outputTypes() {
//        return "Spectrum VectorType SampleSet";
//    }

    public String[] getInputTypes() {
        return new String[]{"triana.types.Spectrum", "triana.types.VectorType", "triana.types.SampleSet"};
    }

    /**
     * @return an array of the output types for MyMakeCurve
     */
    public String[] getOutputTypes() {
        return new String[]{"triana.types.Spectrum", "triana.types.VectorType", "triana.types.SampleSet"};
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Normalises by dividing each element by the sqrt of the sum of the squares";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "Normalise.html";
    }
}













