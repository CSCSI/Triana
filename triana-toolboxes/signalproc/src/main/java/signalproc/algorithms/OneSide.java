package signalproc.algorithms;

import org.trianacode.taskgraph.Unit;
import triana.types.ComplexSpectrum;
import triana.types.GraphType;
import triana.types.Spectrum;
import triana.types.TimeFrequency;
import triana.types.util.FlatArray;

/**
 * A OneSide unit to convert two-sided spectra to one-sided spectra. If the input spectrum is not conjugate-symmetric,
 * then information will be lost. The input must be one-dimensional.
 *
 * @author B F Schutz
 * @version 1.1 09 January 2001
 */
public class OneSide extends Unit {

    /**
     * ********************************************* ** USER CODE of OneSide goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        GraphType input = (GraphType) getInputAtNode(0);
        GraphType output = null;
        int sym;
        double[] real, imag;
        double[][] mreal, mimag;
        if (input instanceof Spectrum) {
            if (((Spectrum) input).isTwoSided()) {
                real = ((Spectrum) input).getData();
                sym = FFTC.testConjugateSymmetry(real, null);
                if (sym != 1) {
                    System.out.println("Information will be lost on conversion to one-sided!.");
                }
                real = FlatArray
                        .convertToOneSided(real, ((Spectrum) input).getOriginalN(), ((Spectrum) input).isNarrow(),
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
                    System.out.println("Information will be lost on conversion to one-sided!.");
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
                        System.out.println("Information will be lost on conversion to one-sided!.");
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
                        System.out.println("Information will be lost on conversion to one-sided!.");
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


    /**
     * Initialses information specific to OneSide.
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
     * Saves OneSide's parameters.
     */
    public void saveParameters() {
    }

    /**
     * Used to set each of OneSide's parameters. This should NOT be used to update this unit's user interface
     */
    public void setParameter(String name, String value) {
    }

    /**
     * Used to update the widget in this unit's user interface that is used to control the given parameter name.
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to OneSide, each separated by a white
     *         space.
     */
//    public String inputTypes() {
//        return "ComplexSpectrum Spectrum TimeFrequency";
//    }
//
//    /**
//     * @return a string containing the names of the types output from OneSide, each separated by a white space.
//     */
//    public String outputTypes() {
//        return "ComplexSpectrum Spectrum TimeFrequency";
//    }
//

    public String[] getInputTypes() {
        return new String[]{"triana.types.ComplexSpectrum", "triana.types.Spectrum", "triana.types.TimeFrequency"};
    }

    /**
     * @return an array of the output types for MyMakeCurve
     */
    public String[] getOutputTypes() {
        return new String[]{"triana.types.ComplexSpectrum", "triana.types.Spectrum", "triana.types.TimeFrequency"};
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Converts two-sided spectrum to one-sided";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "OneSide.html";
    }
}



