package signalproc.filtering.freqdomain;

import org.trianacode.taskgraph.Unit;
import signalproc.algorithms.FullSpectrum;
import triana.types.ComplexSpectrum;
import triana.types.GraphType;
import triana.types.Spectral;
import triana.types.Spectrum;
import triana.types.TimeFrequency;

/**
 * A NegativeF unit to extract the negative frequencies in spectral data set. It sets the positive frequencies to zero.
 *
 * @author B F Schutz
 * @version 1.1 16 Mar 2001
 */
public class NegativeF extends Unit {

    /**
     * ********************************************* ** USER CODE of NegativeF goes here    ***
     * *********************************************
     */
    public void process() throws Exception {

        GraphType input = (GraphType) getInputAtNode(0);

        keepNegativeFrequencies(input);

        output(input);

    }

    public static void keepNegativeFrequencies(GraphType input) {

        double[] data;
        double[][] matrixR, matrixI;
        int ln, l2, j, k, nSpectra;
        boolean even;

        Spectral sp = (Spectral) input;
        if (!sp.isTwoSided()) {
            input = FullSpectrum.restoreFullSpectrum(input, false);
        }

        if (input instanceof ComplexSpectrum) {
            ComplexSpectrum s = (ComplexSpectrum) input;
            data = s.getDataReal();
            ln = data.length;
            l2 = ln / 2;      // the number of negative-f elements
            if ((ln % 2 != 0) && (s.getLowerFrequencyBound() != 0)) {
                l2 = (ln + 1) / 2;
            }
            for (j = ln - l2; j < ln; j++) {
                data[j] = 0.0;
            }
            data = s.getDataImag();
            for (j = ln - l2; j < ln; j++) {
                data[j] = 0.0;
            }
        } else if (input instanceof Spectrum) {
            Spectrum s = (Spectrum) input;
            data = s.getDataReal();
            ln = data.length;
            l2 = ln / 2;      // the number of negative-f elements
            if ((ln % 2 != 0) && (s.getLowerFrequencyBound() != 0)) {
                l2 = (ln + 1) / 2;
            }
            for (j = ln - l2; j < ln; j++) {
                data[j] = 0.0;
            }
        }

        if (input instanceof TimeFrequency) {
            TimeFrequency tf = (TimeFrequency) input;
            matrixR = tf.getDataReal();
            matrixI = null;
            boolean complex = tf.isDependentComplex(0);
            if (complex) {
                matrixI = tf.getDataImag();
            }
            nSpectra = matrixR.length;
            ln = matrixR[0].length;
            l2 = ln / 2;      // the number of negative-f elements
            if ((ln % 2 != 0) && (tf.getLowerFrequencyBound() != 0)) {
                l2 = (ln + 1) / 2;
            }
            for (k = 0; k < nSpectra; k++) {
                for (j = ln - l2; j < ln; j++) {
                    matrixR[k][j] = 0.0;
                }
            }
            if (complex) {
                for (k = 0; k < nSpectra; k++) {
                    for (j = ln - l2; j < ln; j++) {
                        matrixI[k][j] = 0.0;
                    }
                }
            }
        }
    }


    /**
     * Initialses information specific to NegativeF.
     */
    public void init() {
        super.init();

        // set these to true if your unit can process double-precision
        // arrays
//        setRequireDoubleInputs(false);
//        setCanProcessDoubleArrays(false);

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(1);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

    }

    /**
     * Called when the reset button is pressed within the MainTriana Window
     */
    public void reset() {
        super.reset();
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

    /**
     * Saves NegativeF's parameters.
     */
    public void saveParameters() {
    }

    /**
     * Used to set each of NegativeF's parameters. This should NOT be used to update this unit's user interface
     */
    public void setParameter(String name, String value) {
    }

    /**
     * Used to update the widget in this unit's user interface that is used to control the given parameter name.
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to NegativeF, each separated by a white
     *         space.
     */
//    public String inputTypes() {
//        return "ComplexSpectrum Spectrum TimeFrequency";
//    }
//
//    /**
//     * @return a string containing the names of the types output from NegativeF, each separated by a white space.
//     */
//    public String outputTypes() {
//        return "ComplexSpectrum Spectrum TimeFrequency";
//    }

    public String[] getInputTypes() {
        return new String[]{"triana.types.ComplexSpectrum", "triana.types.Spectrum", "triana.types.TimeFrequency"};
    }

    public String[] getOutputTypes() {
        return new String[]{"triana.types.ComplexSpectrum", "triana.types.Spectrum", "triana.types.TimeFrequency"};
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Zero all positive-frequency amplitudes of a spectral set";
    }

    /**
     * @return the location of the help file for this unit.
     */
    public String getHelpFile() {
        return "NegativeF.html";
    }
}



