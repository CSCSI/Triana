package signalproc.algorithms;

import org.trianacode.taskgraph.Unit;
import triana.types.SampleSet;

/**
 * A AutoCorel unit to ..
 *
 * @author Ian
 * @version 1.0 alpha 05 Feb 1998
 */
public class AutoCorel extends Unit {

    /**
     * ********************************************* ** USER CODE of AutoCorel goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        SampleSet w1 = (SampleSet) getInputAtNode(0);

        double[] cross = new double[w1.size()];
        for (int i = 0; i < w1.size(); ++i) {
            cross[i] = 0.0;
        }

        int j = 0;

        do {
            for (int i = 0; i < w1.size(); ++i) {
                if ((i + j) < w1.size()) {
                    cross[j] += (w1.data[i] * w1.data[i + j]);
                }
            }
            ++j;
        }
        while (j < w1.size());

        output(new SampleSet(w1.samplingFrequency, cross));
    }


    /**
     * Initialses information specific to AutoCorel.
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
     * Reset's AutoCorel
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves AutoCorel's parameters to the parameter file.
     */
    public void saveParameters() {
    }

    /**
     * Loads AutoCorel's parameters of from the parameter file.
     */
    public void setParameter(String name, String value) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to AutoCorel, each separated by a white
     *         space.
     */
    public String[] getInputTypes() {
        return new String[]{"triana.types.SampleSet"};
    }

    public String[] getOutputTypes() {
        return new String[]{"triana.types.SampleSet"};
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Outputs the auto corelation of the input Sampleset";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "AutoCorel.html";
    }
}













