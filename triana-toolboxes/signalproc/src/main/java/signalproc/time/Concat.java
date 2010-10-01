package signalproc.time;

import org.trianacode.gui.windows.ErrorDialog;
import org.trianacode.taskgraph.Unit;
import triana.types.ComplexSampleSet;
import triana.types.SampleSet;
import triana.types.TrianaType;
import triana.types.VectorType;

import java.awt.*;

/**
 * A Concat unit join together two or more data sets by appending one after another.
 *
 * @author B.F. Schutz
 * @version 2.0 20 September 20000
 */
public class Concat extends Unit {

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Concatenate (append) data sets at several input nodes into a single one";
    }

    /**
     * ********************************************* ** USER CODE of Concat goes here    ***
     * *********************************************
     */
    public void process() {

        double[] data = {0.0};
        double[] datai = {0.0};

        int nodes = getTask().getDataInputNodeCount();
        int jskip = 0;
        int j;

        Object[] input = new TrianaType[nodes];
        Object output = null;
        int[] length = new int[nodes];
        int[] cumLength = new int[nodes];
        int totalLength = 0;

        input[0] = getInputAtNode(0);
        Class inputClass = input[0].getClass();
        //setOutputType(inputClass);

        while ((input[0] == null) && (jskip < nodes)) {
            jskip++;
            input[0] = getInputAtNode(jskip);
        }

        if (input[0] instanceof SampleSet) {
            length[0] = ((SampleSet) input[0]).size();
            totalLength = length[0];
            cumLength[0] = totalLength;
            j = 1;
            while (j + jskip < nodes) {
                input[j] = getInputAtNode(j + jskip);
                while ((input[j] == null) && (jskip < nodes)) {
                    jskip++;
                    input[j] = getInputAtNode(j + jskip);
                }
                if (!(input[j] instanceof SampleSet)) {
                    new ErrorDialog(null, "First input to Concat " +
                            "was SampleSet, but input number " +
                            String.valueOf(j + jskip) + " was not. Unit fails.");
                    //stop();
                    return;
                } else if (((SampleSet) input[0]).getSamplingRate() != ((SampleSet) input[j]).getSamplingRate()) {
                    new ErrorDialog(null, "First input to Concat " +
                            "and input number " + String.valueOf(j + jskip) +
                            " do not have the same sampling frequency. " +
                            "Unit fails.");
                    //stop();
                    return;
                }
                length[j] = ((SampleSet) input[j]).size();
                totalLength += length[j];
                cumLength[j] = totalLength;
                j++;
            }
            data = new double[totalLength];
            for (int jj = 0; jj < j; jj++) {
                System.arraycopy(((SampleSet) input[jj]).getData(), 0, data, cumLength[jj] - length[jj], length[jj]);
            }
            output = new SampleSet(((SampleSet) input[0]).getSamplingRate(), data,
                    ((SampleSet) input[0]).getAcquisitionTime());
        } else if (input[0] instanceof ComplexSampleSet) {
            length[0] = ((ComplexSampleSet) input[0]).size();
            totalLength = length[0];
            cumLength[0] = totalLength;
            j = 1;
            while (j + jskip < nodes) {
                input[j] = getInputAtNode(j + jskip);
                while ((input[j] == null) && (jskip < nodes)) {
                    jskip++;
                    input[j] = getInputAtNode(j + jskip);
                }
                if (!(input[j] instanceof ComplexSampleSet)) {
                    new ErrorDialog(null, "First input to Concat " +
                            "was ComplexSampleSet, but input number " +
                            String.valueOf(j + jskip) + " was not. Unit fails.");
                    //stop();
                    return;
                } else if (((ComplexSampleSet) input[0]).getSamplingRate() != ((ComplexSampleSet) input[j])
                        .getSamplingRate()) {
                    new ErrorDialog(null, "First input to Concat " +
                            "and input number " + String.valueOf(j + jskip) +
                            " do not have the same sampling frequency. " +
                            "Unit fails.");
                    //stop();
                    return;
                }
                length[j] = ((ComplexSampleSet) input[j]).size();
                totalLength += length[j];
                cumLength[j] = totalLength;
                j++;
            }
            data = new double[totalLength];
            datai = new double[totalLength];
            for (int jj = 0; jj < j; jj++) {
                System.arraycopy(((ComplexSampleSet) input[jj]).getDataReal(), 0, data, cumLength[jj] - length[jj],
                        length[jj]);
                System.arraycopy(((ComplexSampleSet) input[jj]).getDataImag(), 0, datai, cumLength[jj] - length[jj],
                        length[jj]);
            }
            output = new ComplexSampleSet(((ComplexSampleSet) input[0]).getSamplingRate(), data, datai,
                    ((ComplexSampleSet) input[0]).getAcquisitionTime());
        } else if (input[0] instanceof VectorType) {
            length[0] = ((VectorType) input[0]).size();
            totalLength = length[0];
            cumLength[0] = totalLength;
            j = 1;
            while (j + jskip < nodes) {
                input[j] = getInputAtNode(j + jskip);
                while ((input[j] == null) && (jskip < nodes)) {
                    jskip++;
                    input[j] = getInputAtNode(j + jskip);
                }
                if (!(input[j] instanceof VectorType)) {
                    new ErrorDialog(null, "First input to Concat " +
                            "was VectorType, but input number " +
                            String.valueOf(j + jskip) + " was not. Unit fails.");
                    //stop();
                    return;
                }
                length[j] = ((VectorType) input[j]).size();
                totalLength += length[j];
                cumLength[j] = totalLength;
                j++;
            }
            data = new double[totalLength];
            for (int jj = 0; jj < j; jj++) {
                System.arraycopy(((VectorType) input[jj]).getData(), 0, data, cumLength[jj] - length[jj], length[jj]);
            }
            output = new VectorType(data);
        }


        output(output);

    }


    /**
     * Initialses information specific to Concat.
     */
    public void init() {
        super.init();

//        changeInputNodes(2);
//        setResizableInputs(true);
//        setResizableOutputs(true);

        setDefaultInputNodes(2);
        setDefaultOutputNodes(1);
    }

    /**
     * Resets Concat
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
     * @return a string containing the names of the types allowed to be input to Concat, each separated by a white
     *         space.
     */
//    public String inputTypes() {
//        return "VectorType";
//    }
//
//    /**
//     * @return a string containing the names of the types output from Concat, each separated by a white space.
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
     * @returns the location of the help file for this unit.
     */
    public String getHelpFile() {
        return "Concat.html";
    }


    /**
     * @return Concat's parameter window sp that triana can move and display it.
     */
    public Window getParameterWindow() {
        return null;
    }


    /**
     * Captures the events thrown out by Concat.
     */
//    public void actionPerformed(ActionEvent e) {
//        super.actionPerformed(e);   // we need this
//
//    }
}

















