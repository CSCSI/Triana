package signalproc.algorithms;

import org.trianacode.taskgraph.Unit;
import triana.types.ComplexSpectrum;
import triana.types.GraphType;
import triana.types.Spectrum;
import triana.types.TimeFrequency;
import triana.types.util.FlatArray;

/**
 * A FullSpectrum unit to convert a one-sided or narrow-band spectrum into a full-band spectrum. In the narrow-band
 * case, zeros are added for the missing elements. In the one-sided case, the full transform is made to be
 * conjugate-symmetric, <i>i.e.</i> it is assumed that the inverse transform will be purely real.
 *
 * @author B F Schutz
 * @version 1.1 09 January 2001
 */
public class FullSpectrum extends Unit {

    /**
     * ********************************************* ** USER CODE of FullSpectrum goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        GraphType input = (GraphType) getInputAtNode(0);

        GraphType output = restoreFullSpectrum(input, false);

        output(output);

    }

    public static GraphType restoreFullSpectrum(GraphType input, boolean allocMem) {

        double[] real, imag;
        double[][] mreal, mimag;
        GraphType output = input;

        if (input instanceof Spectrum) {
            if (!((Spectrum) input).isTwoSided() || ((Spectrum) input).isNarrow()) {
                real = ((Spectrum) input).getData();
                real = FlatArray.convertToFullSpectrum(real, ((Spectrum) input).getOriginalN(),
                        !((Spectrum) input).isTwoSided(), true, ((Spectrum) input).isNarrow(), (int) Math
                                .round(((Spectrum) input).getLowerFrequencyBound() / ((Spectrum) input)
                                        .getFrequencyResolution()));
                output = new Spectrum(true, real, ((Spectrum) input).getOriginalN(),
                        ((Spectrum) input).getFrequencyResolution());
            } else {
                output = (allocMem) ? (Spectrum) input.copyMe() : (Spectrum) input;
            }
        }
        if (input instanceof ComplexSpectrum) {
            if (!((ComplexSpectrum) input).isTwoSided() || ((ComplexSpectrum) input).isNarrow()) {
                real = ((ComplexSpectrum) input).getDataReal();
                imag = ((ComplexSpectrum) input).getDataImag();
                real = FlatArray.convertToFullSpectrum(real, ((ComplexSpectrum) input).getOriginalN(),
                        !((ComplexSpectrum) input).isTwoSided(), true, ((ComplexSpectrum) input).isNarrow(), (int) Math
                                .round(((ComplexSpectrum) input).getLowerFrequencyBound() / ((ComplexSpectrum) input)
                                        .getFrequencyResolution()));
                /*
                println("Calling convertToFullSpectrum with arguments:");
                println("nfull = " + String.valueOf(((Spectral)input).getOriginalN( 0 )));
                println("oneside = " + String.valueOf( !((ComplexSpectrum)input).isTwoSided() ) );
                println("narrow = " + String.valueOf( ((Spectral)input).isNarrow( 0 ) ) );
                println("low index = " + String.valueOf((int)Math.round(((Spectral)input).getLowerFrequencyBound( 0 )/((Spectral)input).getFrequencyResolution( 0 ))) );
                */
                int pd;
                /*
                println("Real data full spectrum:");
                for( pd = 0; pd < real.length; pd++ ) println(String.valueOf(real[pd]));
                */
                imag = FlatArray.convertToFullSpectrum(imag, ((ComplexSpectrum) input).getOriginalN(),
                        !((ComplexSpectrum) input).isTwoSided(), false, ((ComplexSpectrum) input).isNarrow(), (int) Math
                                .round(((ComplexSpectrum) input).getLowerFrequencyBound() / ((ComplexSpectrum) input)
                                        .getFrequencyResolution()));
                /*
                println("Imaginary data full spectrum:");
                for( pd = 0; pd < imag.length; pd++ ) println(String.valueOf(imag[pd]));
                */
                output = new ComplexSpectrum(true, real, imag, ((ComplexSpectrum) input).getOriginalN(),
                        ((ComplexSpectrum) input).getFrequencyResolution());
            } else {
                output = (allocMem) ? (ComplexSpectrum) input.copyMe() : (ComplexSpectrum) input;
            }
        }
        if (input instanceof TimeFrequency) {
            if (!((TimeFrequency) input).isTwoSided() || ((TimeFrequency) input).isNarrow()) {
                mreal = (double[][]) ((TimeFrequency) input).getDataArrayReal(0);
                mimag = (double[][]) ((TimeFrequency) input).getDataArrayImag(0);
                int rows = mreal.length;
                for (int k = 0; k < rows; k++) {
                    mreal[k] = FlatArray.convertToFullSpectrum(mreal[k], ((TimeFrequency) input).getOriginalN(),
                            !((TimeFrequency) input).isTwoSided(), true, ((TimeFrequency) input).isNarrow(), (int) Math
                                    .round(((TimeFrequency) input).getLowerFrequencyBound() / ((TimeFrequency) input)
                                            .getFrequencyResolution()));
                    mimag[k] = FlatArray.convertToFullSpectrum(mimag[k], ((TimeFrequency) input).getOriginalN(),
                            !((TimeFrequency) input).isTwoSided(), true, ((TimeFrequency) input).isNarrow(), (int) Math
                                    .round(((TimeFrequency) input).getLowerFrequencyBound() / ((TimeFrequency) input)
                                            .getFrequencyResolution()));
                }
                output = new TimeFrequency(mreal, mimag, true, false, ((TimeFrequency) input).getOriginalN(),
                        ((TimeFrequency) input).getFrequencyResolution(),
                        ((TimeFrequency) input).getUpperFrequencyBound(), ((TimeFrequency) input).getInterval(),
                        ((TimeFrequency) input).getAcquisitionTime());
            } else {
                output = (allocMem) ? (TimeFrequency) input.copyMe() : (TimeFrequency) input;
            }
        }

        return output;

    }


    /**
     * Initialses information specific to FullSpectrum.
     */
    public void init() {
        super.init();

//        setResizableInputs(false);
//        setResizableOutputs(true);
        setMinimumInputNodes(1);
        setMaximumInputNodes(Integer.MAX_VALUE);
        setDefaultInputNodes(1);
        setMinimumOutputNodes(1);
        setDefaultOutputNodes(1);
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
     * Saves FullSpectrum's parameters.
     */
    public void saveParameters() {
    }

    /**
     * Used to set each of FullSpectrum's parameters. This should NOT be used to update this unit's user interface
     */
    public void setParameter(String name, String value) {
    }

    /**
     * Used to update the widget in this unit's user interface that is used to control the given parameter name.
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to FullSpectrum, each separated by a white
     *         space.
     */
//    public String inputTypes() {
//        return "ComplexSpectrum Spectrum";
//    }
//
//    /**
//     * @return a string containing the names of the types output from FullSpectrum, each separated by a white space.
//     */
//    public String outputTypes() {
//        return "ComplexSpectrum Spectrum";
//    }

    public String[] getInputTypes() {
        return new String[]{"triana.types.Spectrum", "triana.types.ComplexSpectrum"};
    }

    public String[] getOutputTypes() {
        return new String[]{"triana.types.Spectrum", "triana.types.ComplexSpectrum"};
    }
    

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Converts a narrow-band and/or one-sided spectrum to full width";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "FullSpectrum.html";
    }
}



