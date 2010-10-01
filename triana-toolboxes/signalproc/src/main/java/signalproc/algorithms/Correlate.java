package signalproc.algorithms;

import org.trianacode.gui.windows.ErrorDialog;
import org.trianacode.taskgraph.Unit;
import triana.types.Const;
import triana.types.SampleSet;
import triana.types.Spectrum;
import triana.types.VectorType;


/**
 * A Correlate unit to correlate the two inputs giving a similarity match (between 0 and 1 if used in conjunction with
 * the normalise unit) of the inputs.
 *
 * @author Ian Taylor
 * @version 1.0 alpha 02 Feb 1998
 */
public class Correlate extends Unit {

    /**
     * ********************************************* ** USER CODE of Correlate goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        Object in = getInputAtNode(0);
        Object in1 = getInputAtNode(1);

        if (in instanceof SampleSet) {
            SampleSet wave = (SampleSet) in;
            SampleSet wave1 = (SampleSet) in1;

            double data = correlate(wave.data, wave1.data);
            output(new Const(data));
            return;
        }


        if (in instanceof Spectrum) {
            Spectrum wave = (Spectrum) in;
            Spectrum wave1 = (Spectrum) in1;
            double data = correlate(wave.data, wave1.data);
            output(new Const(data));
            return;
        }


        if (in instanceof VectorType) {
            VectorType raw = (VectorType) in;
            VectorType raw1 = (VectorType) in1;

            double data = correlate(raw.getData(), raw1.getData());
            output(new Const(data));
            return;
        }

        new ErrorDialog(null, "Invalid Input Data to " + getToolName());
        stop();
    }


    public double correlate(double[] a1, double a2[]) {
        // correlates two vectors
        int i;
        double result = 0.0;

        if (a1.length != a2.length) {
            new ErrorDialog(null,
                    "Invalid Sizes in Input data " + getToolName());
            return 0; // no correlation wrong size!
        }

        for (i = 0; i < a1.length; ++i) {
            result += (a1[i] * a2[i]);
        }

        return result;
    }

    /**
     * Initialses information specific to Correlate.
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
     * Reset's Correlate
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
     * @return a string containing the names of the types allowed to be input to Correlate, each separated by a white
     *         space.
     */

    public String[] getInputTypes() {
        return new String[]{"triana.types.Spectrum", "triana.types.VectorType", "triana.types.SampleSet"};
    }

    public String[] getOutputTypes() {
        return new String[]{"triana.types.Const"};
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Outputs a similarity rating of the two correlated inputs";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "Correlate.html";
    }
}













