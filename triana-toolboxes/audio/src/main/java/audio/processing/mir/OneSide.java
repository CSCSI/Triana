package audio.processing.mir;

import org.trianacode.taskgraph.Unit;
import signalproc.algorithms.FFTC;
import triana.types.ComplexSpectrum;
import triana.types.GraphType;
import triana.types.Spectrum;
import triana.types.TimeFrequency;
import triana.types.util.FlatArray;

/**
 * @author Eddie Al-Shakarchi
 * @version $Revision: 2915 $
 */
public class OneSide extends Unit {


    /*
    * Called whenever there is data for the unit to process
    */

    public void process() throws Exception {

        GraphType input = (GraphType) getInputAtNode(0);
        GraphType output = null;
        int sym = -1;
        double[] real, imag;
        double[][] mreal, mimag;

        if (input instanceof Spectrum) {
            if (((Spectrum) input).isTwoSided()) {
                real = ((Spectrum) input).getData();
                sym = FFTC.testConjugateSymmetry(real, null);
                if (sym != 1) {
                    println("Information will be lost on conversion to one-sided!.");
                }
                real = FlatArray.convertToOneSided(real, ((Spectrum) input).getOriginalN(), ((Spectrum) input).isNarrow(),
                                (((Spectrum) input).getLowerFrequencyBound() == 0.0));
                output = new Spectrum(false, ((Spectrum) input).isNarrow(), real, ((Spectrum) input).getOriginalN(),
                        ((Spectrum) input).getFrequencyResolution(), ((Spectrum) input).getUpperFrequencyBound());
            } else {
                output = (Spectrum) input;
            }
        } else if (input instanceof ComplexSpectrum) {
            if (((ComplexSpectrum) input).isTwoSided()) {
                real = ((ComplexSpectrum) input).getDataReal();
                imag = ((ComplexSpectrum) input).getDataImag();
                sym = FFTC.testConjugateSymmetry(real, imag);
                if (sym != 1) {
                    println("Information will be lost on conversion to one-sided!.");
                }
                real = FlatArray.convertToOneSided(real, ((ComplexSpectrum) input).getOriginalN(),
                        ((ComplexSpectrum) input).isNarrow(),
                        (((ComplexSpectrum) input).getLowerFrequencyBound() == 0.0));
                imag = FlatArray.convertToOneSided(imag, ((ComplexSpectrum) input).getOriginalN(),
                        ((ComplexSpectrum) input).isNarrow(),
                        (((ComplexSpectrum) input).getLowerFrequencyBound() == 0.0));
                output = new ComplexSpectrum(false, ((ComplexSpectrum) input).isNarrow(), real, imag,
                        ((ComplexSpectrum) input).getOriginalN(), ((ComplexSpectrum) input).getFrequencyResolution(),
                        ((ComplexSpectrum) input).getUpperFrequencyBound());
            } else {
                output = (ComplexSpectrum) input;
            }
        } else if (input instanceof TimeFrequency) {
            if (input.isDependentComplex(0)) {
                if (((TimeFrequency) input).isTwoSided()) {
                    mreal = (double[][]) ((TimeFrequency) input).getDataArrayReal(0);
                    mimag = (double[][]) ((TimeFrequency) input).getDataArrayImag(0);
                    sym = FFTC.testConjugateSymmetry(mreal[0], mimag[0]);
                    if (sym != 1) {
                        println("Information will be lost on conversion to one-sided!.");
                    }
                    int rows = mreal.length;
                    for (int k = 0; k < rows; k++) {
                        mreal[k] = FlatArray.convertToOneSided(mreal[k], ((TimeFrequency) input).getOriginalN(),
                                ((TimeFrequency) input).isNarrow(),
                                (((TimeFrequency) input).getLowerFrequencyBound() == 0.0));
                        mimag[k] = FlatArray.convertToOneSided(mimag[k], ((TimeFrequency) input).getOriginalN(),
                                ((TimeFrequency) input).isNarrow(),
                                (((TimeFrequency) input).getLowerFrequencyBound() == 0.0));
                    }
                    output = new TimeFrequency(mreal, mimag, false, ((TimeFrequency) input).isNarrow(),
                            ((TimeFrequency) input).getOriginalN(), ((TimeFrequency) input).getFrequencyResolution(),
                            ((TimeFrequency) input).getUpperFrequencyBound(), ((TimeFrequency) input).getInterval(),
                            ((TimeFrequency) input).getAcquisitionTime());
                } else {
                    output = (TimeFrequency) input;
                }
            } else {
                if (((TimeFrequency) input).isTwoSided()) {
                    mreal = (double[][]) ((TimeFrequency) input).getDataArrayReal(0);
                    sym = FFTC.testConjugateSymmetry(mreal[0], null);
                    if (sym != 1) {
                        println("Information will be lost on conversion to one-sided!.");
                    }
                    int rows = mreal.length;
                    for (int k = 0; k < rows; k++) {
                        mreal[k] = FlatArray.convertToOneSided(mreal[k], ((TimeFrequency) input).getOriginalN(),
                                ((TimeFrequency) input).isNarrow(),
                                (((TimeFrequency) input).getLowerFrequencyBound() == 0.0));
                    }
                    output = new TimeFrequency(mreal, null, false, ((TimeFrequency) input).isNarrow(),
                            ((TimeFrequency) input).getOriginalN(), ((TimeFrequency) input).getFrequencyResolution(),
                            ((TimeFrequency) input).getUpperFrequencyBound(), ((TimeFrequency) input).getInterval(),
                            ((TimeFrequency) input).getAcquisitionTime());
                } else {
                    output = (TimeFrequency) input;
                }
            }
        }
        if (input.getTitle() != null) {
            output.setTitle(input.getTitle());
        }
        output(output);

    }

    public void println(String text) {
        print(text + "\n");
    }

    public void print(String text) {
        // TODO
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
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        // Initialise parameter update policy and output policy
        setParameterUpdatePolicy(PROCESS_UPDATE);
        setOutputPolicy(CLONE_MULTIPLE_OUTPUT);

        // Initialise pop-up description and help file location
        setPopUpDescription("");
        setHelpFileLocation("OneSide.html");
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
        // Insert code to clean-up OneSide (e.g. close open files) 
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables 
    }


    /**
     * @return an array of the types accepted by each input node. For node indexes not covered the types specified by
     *         getInputTypes() are assumed.
     */
    public String[][] getNodeInputTypes() {
        return new String[0][0];
    }

    /**
     * @return an array of the input types accepted by nodes not covered by getNodeInputTypes().
     */
    public String[] getInputTypes() {
        return new String[]{"ComplexSpectrum", "Spectrum", "TimeFrequency"};
    }


    /**
     * @return an array of the types output by each output node. For node indexes not covered the types specified by
     *         getOutputTypes() are assumed.
     */
    public String[][] getNodeOutputTypes() {
        return new String[0][0];
    }

    /**
     * @return an array of the input types output by nodes not covered by getNodeOutputTypes().
     */
    public String[] getOutputTypes() {
        return new String[]{"ComplexSpectrum", "Spectrum", "TimeFrequency"};
    }

}



