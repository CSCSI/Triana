package signalproc.converters;

import org.trianacode.taskgraph.Unit;
import triana.types.SampleSet;
import triana.types.VectorType;

/**
 * A SSetTo2D unit to ..
 *
 * @author Ian Taylor
 * @version 1.0 alpha 13 May 2000
 */
public class SSetToVect extends Unit {

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Converts a SampleSet to a VectorType";
    }

    /**
     * ********************************************* Main routine of SpectTo2D which takes in a SampleSet and converts
     * it into a 2D data type for input to the Grapher. *********************************************
     */
    public void process() {
        SampleSet wave = (SampleSet) getInputAtNode(0);

        VectorType spect2D = convert(wave);

        output(spect2D);
    }


    /**
     * Converts a SampleSet data type to a VectorType
     * <p/>
     * return a VectorType
     */
    public static synchronized VectorType convert(SampleSet s) {
        String labelx = "Time (Seconds)";
        String labely = "Amp";

        // work out x coordinates :-

/*        double xcoords[] = new double[s.size()];

        double timeInc = 1.0 / s.samplingFrequency;

        for (int i=0; i< s.size(); ++i)
            xcoords[i] = i*timeInc; */

        s.x = s.getXArray();
        s.y = s.getData();

        s.setIndependentLabels(0, labelx);
        s.setDependentLabels(0, labely);

        return s;
    }

    /**
     * Initialses information specific to SSetTo2D.
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
     * Reset's SSetTo2D
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves parameters.
     */
    public void saveParameters() {
    }

    /**
     * Used to set each of the parameters.
     */
    public void setParameter(String name, String value) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to SSetTo2D, each separated by a white
     *         space.
     */
//    public String inputTypes() {
//        return "SampleSet";
//    }
//
//    /**
//     * @return a string containing the names of the types output from SSetTo2D, each separated by a white space.
//     */
//    public String outputTypes() {
//        return "VectorType";
//    }

    public String[] getInputTypes() {
        return new String[]{"triana.types.SampleSet"};
    }

    public String[] getOutputTypes() {
        return new String[]{"triana.types.VectorType"};
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "converters.html";
    }
}














