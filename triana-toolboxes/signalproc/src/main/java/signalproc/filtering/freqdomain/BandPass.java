package signalproc.filtering.freqdomain;

/*
 * Copyright (c) 1995 onwards, University of Wales College of Cardiff
 *
 * Permission to use and modify this software and its documentation for
 * any purpose is hereby granted without fee provided a written agreement
 * exists between the recipients and the University.
 *
 * Further conditions of use are that (i) the above copyright notice and
 * this permission notice appear in all copies of the software and
 * related documentation, and (ii) the recipients of the software and
 * documentation undertake not to copy or redistribute the software and
 * documentation to any other party.
 *
 * THE SOFTWARE IS PROVIDED "AS-IS" AND WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS, IMPLIED OR OTHERWISE, INCLUDING WITHOUT LIMITATION, ANY
 * WARRANTY OF MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.
 *
 * IN NO EVENT SHALL THE UNIVERSITY OF WALES COLLEGE OF CARDIFF BE LIABLE
 * FOR ANY SPECIAL, INCIDENTAL, INDIRECT OR CONSEQUENTIAL DAMAGES OF ANY
 * KIND, OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 * PROFITS, WHETHER OR NOT ADVISED OF THE POSSIBILITY OF DAMAGE, AND ON
 * ANY THEORY OF LIABILITY, ARISING OUT OF OR IN CONNECTION WITH THE USE
 * OR PERFORMANCE OF THIS SOFTWARE.
 */


import triana.types.ComplexSpectrum;
import triana.types.GraphType;
import triana.types.OldUnit;
import triana.types.Spectrum;
import triana.types.TimeFrequency;
import triana.types.util.FlatArray;
import triana.types.util.SigAnalWindows;


/**
 * A BandPass unit to filter to a finite bandwidth in the frequency domain. The unit returns a spectral data set with
 * all frequencies outside the input bandwidth set to zero. The user can elect to return a narrow-band data set or one
 * padded with zeros, and can also select to have the result windowed in the frequency domain. The user can also elect
 * to reduce the Nyquist frequency to the upper band limit of the filtered spectrum; this means that when the spectrum
 * is inverted to a time-series, the time-series will be sampled at twice the rate of the upper bandwidth limit, not at
 * the rate at which the original data set was sampled.
 *
 * @author B F Schutz
 * @version 2.0 04 Mar 2001
 */
public class BandPass extends OldUnit {

    double centerBand = 100;
    double bandwidth = 10;
    boolean noZeros = false;
    boolean nyquist = false;
    String window = "Rectangle";


    /**
     * ********************************************* ** USER CODE of BandPass goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        GraphType input;
        GraphType output = null;
        double lowLimit = Math.max(centerBand - bandwidth / 2., 0.0);
        double highLimit = lowLimit + bandwidth;
        input = (GraphType) getInputNode(0);

        if (lowLimit == 0) {
            output = LowPass.filterToMax(input, highLimit, noZeros, window, nyquist);
        } else {
            output = filterToBand(input, lowLimit, highLimit, noZeros, window, nyquist);
        }

        output(output);
    }

    public static GraphType filterToBand(GraphType input, double lowLimit, double highLimit, boolean noZeros,
                                         String window, boolean nyquist) {

        GraphType output = input;
        int startVal, finishVal, givenLow, givenHigh;
        double[] full, narrowR, narrowI;
        boolean newNarrow = true;
        boolean doWindow = !window.equals("Rectangle");
        if (nyquist) {
            noZeros = true;
        }

        if (input instanceof ComplexSpectrum) {
            ComplexSpectrum s = (ComplexSpectrum) input;
            if ((lowLimit > s.getLowerFrequencyBound()) || (highLimit < s.getUpperFrequencyBound())) {
                startVal = (int) Math.round(Math.max(lowLimit, 0.0) / s.getFrequencyResolution());
                finishVal = (int) Math
                        .round(Math.min(highLimit, s.getOriginalN() * s.getFrequencyResolution() / 2.0) / s
                                .getFrequencyResolution());
                givenLow = (int) Math.round(s.getLowerFrequencyBound() / s.getFrequencyResolution());
                givenHigh = (int) Math.round(s.getUpperFrequencyBound() / s.getFrequencyResolution());
                full = FlatArray
                        .convertToFullSpectrum(s.getDataReal(), s.getOriginalN(), !s.isTwoSided(), true, s.isNarrow(),
                                givenLow);
                if (startVal < givenLow) {
                    startVal = givenLow;
                }
                if (finishVal > givenHigh) {
                    finishVal = givenHigh;
                }
                narrowR = FlatArray.convertToNarrowBand(full, false, startVal, finishVal);
                if (doWindow) {
                    SigAnalWindows.applyWindowToSpectral(narrowR, window, lowLimit, true, true);
                }
                if (!noZeros) {
                    narrowR = FlatArray.convertToFullSpectrum(narrowR, s.getOriginalN(), false, true, true, startVal);
                    newNarrow = false;
                }
                if (!s.isTwoSided()) {
                    narrowR = FlatArray.convertToOneSided(narrowR, s.getOriginalN(), newNarrow, false);
                }
                full = FlatArray
                        .convertToFullSpectrum(s.getDataImag(), s.getOriginalN(), !s.isTwoSided(), false, s.isNarrow(),
                                givenLow);
                narrowI = FlatArray.convertToNarrowBand(full, false, startVal, finishVal);
                if (doWindow) {
                    SigAnalWindows.applyWindowToSpectral(narrowI, window, lowLimit, true, true);
                }
                if (!noZeros) {
                    narrowI = FlatArray.convertToFullSpectrum(narrowI, s.getOriginalN(), false, false, true, startVal);
                }
                if (!s.isTwoSided()) {
                    narrowI = FlatArray.convertToOneSided(narrowI, s.getOriginalN(), newNarrow, false);
                }
                //System.out.println((new StringBuffer("BandPass: creating new complexspectrum with parameters startVal = ")).append(startVal ).append(", finishVal = ").append(finishVal).append(", narrowR.length = ").append(narrowR.length).append(", top frequency = ").append(( newNarrow ) ? ( finishVal * s.getFrequencyResolution() ) : ( s.getOriginalN() * s.getFrequencyResolution() / 2.0 )).toString() );
                output = new ComplexSpectrum(s.isTwoSided(), newNarrow, narrowR, narrowI, s.getOriginalN(),
                        s.getFrequencyResolution(), (newNarrow) ? (finishVal * s.getFrequencyResolution())
                                : (s.getOriginalN() * s.getFrequencyResolution() / 2.0));
                if (nyquist) {
                    output = ComplexSpectrum.reduceNyquist((ComplexSpectrum) output, false);
                }
            }
        } else if (input instanceof Spectrum) {
            Spectrum s = (Spectrum) input;
            if ((lowLimit > s.getLowerFrequencyBound()) || (highLimit < s.getUpperFrequencyBound())) {
                startVal = (int) Math.round(Math.max(lowLimit, 0.0) / s.getFrequencyResolution());
                finishVal = (int) Math
                        .round(Math.min(highLimit, s.getOriginalN() * s.getFrequencyResolution() / 2.0) / s
                                .getFrequencyResolution());
                givenLow = (int) Math.round(s.getLowerFrequencyBound() / s.getFrequencyResolution());
                givenHigh = (int) Math.round(s.getUpperFrequencyBound() / s.getFrequencyResolution());
                full = FlatArray
                        .convertToFullSpectrum(s.getDataReal(), s.getOriginalN(), !s.isTwoSided(), true, s.isNarrow(),
                                givenLow);
                if (startVal < givenLow) {
                    startVal = givenLow;
                }
                if (finishVal > givenHigh) {
                    finishVal = givenHigh;
                }
                narrowR = FlatArray.convertToNarrowBand(full, false, startVal, finishVal);
                if (doWindow) {
                    SigAnalWindows.applyWindowToSpectral(narrowR, window, lowLimit, true, true);
                }
                if (!noZeros) {
                    narrowR = FlatArray.convertToFullSpectrum(narrowR, s.getOriginalN(), false, true, true, startVal);
                    newNarrow = false;
                }
                if (!s.isTwoSided()) {
                    narrowR = FlatArray.convertToOneSided(narrowR, s.getOriginalN(), newNarrow, false);
                }
                output = new Spectrum(s.isTwoSided(), newNarrow, narrowR, s.getOriginalN(), s.getFrequencyResolution(),
                        (newNarrow) ? (finishVal * s.getFrequencyResolution())
                                : (s.getOriginalN() * s.getFrequencyResolution() / 2.0));
                if (nyquist) {
                    output = Spectrum.reduceNyquist((Spectrum) output, false);
                }
            }

        } else if (input instanceof TimeFrequency) {
            TimeFrequency tf = (TimeFrequency) input;
            if ((lowLimit > tf.getLowerFrequencyBound()) || (highLimit < tf.getUpperFrequencyBound())) {
                boolean complex = tf.isDependentComplex(0);
                startVal = (int) Math.round(Math.max(lowLimit, 0.0) / tf.getFrequencyResolution());
                finishVal = (int) Math
                        .round(Math.min(highLimit, tf.getOriginalN() * tf.getFrequencyResolution() / 2.0) / tf
                                .getFrequencyResolution());
                givenLow = (int) Math.round(tf.getLowerFrequencyBound() / tf.getFrequencyResolution());
                givenHigh = (int) Math.round(tf.getUpperFrequencyBound() / tf.getFrequencyResolution());
                if (startVal < givenLow) {
                    startVal = givenLow;
                }
                if (finishVal > givenHigh) {
                    finishVal = givenHigh;
                }
                double[][] matrixR = tf.getDataReal();
                double[][] matrixI = null;
                int nSpectra = matrixR.length;
                double[][] filteredMatrixR = new double[nSpectra][];
                double[][] filteredMatrixI = new double[nSpectra][];
                double[] spectrumR, spectrumI;
                for (int k = 0; k < nSpectra; k++) {
                    spectrumR = matrixR[k];
                    full = FlatArray
                            .convertToFullSpectrum(spectrumR, tf.getOriginalN(), !tf.isTwoSided(), true, tf.isNarrow(),
                                    givenLow);
                    narrowR = FlatArray.convertToNarrowBand(full, false, startVal, finishVal);
                    if (doWindow) {
                        SigAnalWindows.applyWindowToSpectral(narrowR, window, lowLimit, true, true);
                    }
                    if (!noZeros) {
                        narrowR = FlatArray
                                .convertToFullSpectrum(narrowR, tf.getOriginalN(), false, true, true, startVal);
                        newNarrow = false;
                    }
                    if (!tf.isTwoSided()) {
                        narrowR = FlatArray.convertToOneSided(narrowR, tf.getOriginalN(), newNarrow, false);
                    }
                    filteredMatrixR[k] = narrowR;
                }
                if (complex) {
                    matrixI = tf.getDataImag();
                    for (int k = 0; k < nSpectra; k++) {
                        spectrumI = matrixI[k];
                        full = FlatArray.convertToFullSpectrum(spectrumI, tf.getOriginalN(), !tf.isTwoSided(), false,
                                tf.isNarrow(), givenLow);
                        narrowI = FlatArray.convertToNarrowBand(full, false, startVal, finishVal);
                        if (doWindow) {
                            SigAnalWindows.applyWindowToSpectral(narrowI, window, lowLimit, true, true);
                        }
                        if (!noZeros) {
                            narrowI = FlatArray
                                    .convertToFullSpectrum(narrowI, tf.getOriginalN(), false, false, true, startVal);
                        }
                        if (!tf.isTwoSided()) {
                            narrowI = FlatArray.convertToOneSided(narrowI, tf.getOriginalN(), newNarrow, false);
                        }
                        filteredMatrixI[k] = narrowI;
                    }
                }
                output = new TimeFrequency(filteredMatrixR, filteredMatrixI, tf.isTwoSided(), newNarrow,
                        tf.getOriginalN(), tf.getFrequencyResolution(),
                        (newNarrow) ? (finishVal * tf.getFrequencyResolution())
                                : (tf.getOriginalN() * tf.getFrequencyResolution() / 2.0), tf.getInterval(),
                        tf.getAcquisitionTime());
                if (nyquist) {
                    output = TimeFrequency.reduceNyquist((TimeFrequency) output, false);
                }
            }
        }
        return output;
    }

    /**
     * Initialses information specific to BandPass.
     */
    public void init() {
        super.init();

        setUseGUIBuilder(true);

        setRequireDoubleInputs(false);
        setCanProcessDoubleArrays(false);

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(1);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format.
     */
    public void setGUIInformation() {
        addGUILine("Give central frequency of band (Hz) $title centerBand Scroller 0 1000 100");
        addGUILine("Give bandwidth (Hz) $title bandwidth Scroller 0 100 10");
        addGUILine(
                "Output narrow-band? (Do not check if you want full-band output with zeros.) $title noZeros Checkbox false");
        addGUILine("Choose window for smoothing filter edges in frequency-domain $title window Choice " + SigAnalWindows
                .listOfWindows());
        addGUILine("Reduce Nyquist frequency to upper band limit? $title nyquist Checkbox false");
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
    public void starting() {
        super.starting();
    }

    /**
     * Saves BandPass's parameters.
     */
    public void saveParameters() {
        saveParameter("centerBand", centerBand);
        saveParameter("bandwidth", bandwidth);
        saveParameter("noZeros", noZeros);
        saveParameter("nyquist", nyquist);
        saveParameter("window", window);
    }


    /**
     * Used to set each of BandPass's parameters.
     */
    public void setParameter(String name, String value) {
        updateGUIParameter(name, value);

        if (name.equals("centerBand")) {
            centerBand = strToDouble(value);
        }
        if (name.equals("bandwidth")) {
            bandwidth = strToDouble(value);
        }
        if (name.equals("noZeros")) {
            noZeros = strToBoolean(value);
        }
        if (name.equals("nyquist")) {
            nyquist = strToBoolean(value);
        }
        if (name.equals("window")) {
            window = value;
        }
    }

    /**
     * Don't need to use this for GUI Builder units as everthing is updated by triana automatically
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to BandPass, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "ComplexSpectrum Spectrum TimeFrequency";
    }

    /**
     * @return a string containing the names of the types output from BandPass, each separated by a white space.
     */
    public String outputTypes() {
        return "ComplexSpectrum Spectrum TimeFrequency";
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Filter to a narrow bandwidth in the frequency domain";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "BandPass.html";
    }
}




