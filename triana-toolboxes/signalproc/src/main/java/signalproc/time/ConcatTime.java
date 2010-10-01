package signalproc.time;

import javax.swing.JOptionPane;
import org.trianacode.taskgraph.Unit;
import triana.types.ComplexSampleSet;
import triana.types.SampleSet;
import triana.types.Signal;
import triana.types.VectorType;
import triana.types.util.Str;

/**
 * A ConcatTime unit to concatenate successive input data sets into one.
 *
 * @author Ian Taylor, Bernard Schutz
 * @version 2.0 20 September 2000
 */
public class ConcatTime extends Unit {

    int sets = 1;
    int count = 0;
    int part = 1;
    double[] data = new double[1];
    double[] datai = new double[1];

    int type = 0;

    /**
     * ********************************************* ** USER CODE of ConcatTime goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        VectorType wave = (VectorType) getInputAtNode(0);
        double acquisition = 0;
        int i;
        part = wave.size();

        if (count == 0) {
            int length = part * sets;
            data = new double[length];
            if (wave.isDependentComplex(0)) {
                datai = new double[length];
            }
            if (wave instanceof Signal) {
                acquisition = ((Signal) wave).getAcquisitionTime();
            }

            if (wave instanceof SampleSet) {
                type = 1;
            } else if (wave instanceof ComplexSampleSet) {
                type = 2;
            } else {
                type = 3;
            }
        }

        boolean error;

        if (wave instanceof SampleSet) {
            error = type != 1;
        } else if (wave instanceof ComplexSampleSet) {
            error = type != 2;
        } else {
            error = type != 3;
        }

        if (error) {
            JOptionPane.showMessageDialog(null, "Error: Data type mismatch between input sets", getToolName(),
                    JOptionPane.ERROR_MESSAGE);
            reset();
        } else {
//        System.out.println("Copying to " + (count*part));
            System.arraycopy(wave.getDataReal(), 0, data, (count * part), part);
            if (wave.isDependentComplex(0)) {
                System.arraycopy(wave.getDataImag(), 0, datai, (count * part), part);
            }

            if (count == (sets - 1)) {
                count = 0;
                if (wave instanceof SampleSet) {
                    output(new SampleSet(((SampleSet) wave).getSamplingRate(), data, acquisition));
                } else if (wave instanceof ComplexSampleSet) {
                    output(new ComplexSampleSet(((ComplexSampleSet) wave).getSamplingRate(), data, datai, acquisition));
                } else {
                    output(new VectorType(data));
                }
            } else {
                ++count;
            }
        }
    }


    /**
     * Initialses information specific to ConcatTime.
     */
    public void init() {
        super.init();

//        setUseGUIBuilder(true);
//
//        setResizableInputs(false);
//        setResizableOutputs(true);
        sets = 1;

        String guilines = "";
        guilines += "Enter the number of time series data to concatenate ? $title sets IntScroller 1 100 1\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format (see Triana help).
     */
//    public void setGUIInformation() {
//        addGUILine("Enter the number of time series data to concatenate ? $title sets IntScroller 1 100 1");
//    }

    /**
     * Resets ConcatTime
     */
    public void reset() {
        super.reset();
        count = 0;
        type = 0;
    }

    /**
     * Saves ConcatTime's parameters.
     */
//    public void saveParameters() {
//        saveParameter("sets", sets);
//    }

    /**
     * Used to set each of ConcatTime's parameters.
     */
    public void parameterUpdate(String name, Object value) {
        //updateGUIParameter(name, value);

        if (name.equals("sets")) {
            sets = Str.strToInt((String) value);
            count = 0;
        }
    }

    /**
     * @return a string containing the names of the types allowed to be input to ConcatTime, each separated by a white
     *         space.
     */
//    public String inputTypes() {
//        return "VectorType";
//    }
//
//    /**
//     * @return a string containing the names of the types output from ConcatTime, each separated by a white space.
//     */
//    public String outputTypes() {
//        return "VectorType";
//    }

    public String[] getInputTypes() {
        return new String[]{"triana.types.VectorType"};
    }

    public String[] getOutputTypes() {
        return new String[]{"triana.types.VectorType"};
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Concatenates (appends) successive input data sets into one.";
    }

    /**
     *
     * @returns the location of the help file for this unit.
     */
    public String getHelpFile() {
        return "ConcatTime.html";
    }
}













