package signalproc.algorithms;

import org.trianacode.taskgraph.Unit;
import triana.types.SampleSet;


/**
 * A CrossCorel unit to ..
 *
 * @author Ian
 * @version 1.0 alpha 05 Feb 1998
 */
public class CrossCorel extends Unit {

    /**
     * ********************************************* ** USER CODE of CrossCorel goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        SampleSet w1 = (SampleSet) getInputAtNode(0);
        SampleSet w2 = (SampleSet) getInputAtNode(1);

        double[] cross = new double[w1.size()];
        for (int i = 0; i < w1.size(); ++i) {
            cross[i] = 0.0;
        }

        int j = 0;

        do {
            for (int i = 0; i < w1.size(); ++i) {
                if ((i + j) < w1.size()) {
                    cross[j] += (w1.data[i] * w2.data[i + j]);
                }
            }
            ++j;
        }
        while (j < w1.size());

        output(new SampleSet(w1.samplingFrequency, cross));
    }


    /**
     * Initialses information specific to CrossCorel.
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
     * Reset's CrossCorel
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
     * @return a string containing the names of the types allowed to be input to CrossCorel, each separated by a white
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
        return "Applies the cross correlation algoithm on the two sample set inputs";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "CrossCorel.html";
    }
}













