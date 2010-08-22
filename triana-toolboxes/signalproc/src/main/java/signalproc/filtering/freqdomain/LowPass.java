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
 * A LowPass unit to perform a low-pass filter in the frequency domain. The unit returns a spectral data set with all
 * frequencies above the input lowPass set to zero. The user can elect to return a narrow-band data set or one padded
 * with zeros, and can also select to have the result windowed in the frequency domain.
 *
 * @author B F Schutz
 * @version 2.0 02 Mar 2001
 */
public class LowPass extends OldUnit {

    double lowPass = 100;
    boolean noZeros = false;
    boolean nyquist = false;
    String window = "Rectangle";


    /**
     * ********************************************* ** USER CODE of LowPass goes here    ***
     * *********************************************
     */
    public void process() throws Exception {

        GraphType input;
        GraphType output = null;

        input = (GraphType) getInputNode(0);
        output = filterToMax(input, lowPass, noZeros, window, nyquist);
        output(output);
    }

    public static GraphType filterToMax(GraphType input, double lowPass, boolean noZeros, String window,
                                        boolean nyquist) {

        int startVal, givenLow;
        double[] full, narrowR, narrowI;
        boolean newNarrow = true;
        boolean doWindow = !window.equals("Rectangle");
        GraphType output = input;
        if (nyquist) {
            noZeros = true;
        }

        if (input instanceof ComplexSpectrum) {
            ComplexSpectrum s = (ComplexSpectrum) input;
            if (lowPass < s.getUpperFrequencyBound()) {
                startVal = (int) Math.round(lowPass / s.getFrequencyResolution());
                givenLow = (int) Math.round(s.getLowerFrequencyBound() / s.getFrequencyResolution());
                full = FlatArray
                        .convertToFullSpectrum(s.getDataReal(), s.getOriginalN(), !s.isTwoSided(), true, s.isNarrow(),
                                givenLow);
                narrowR = FlatArray.convertToNarrowBand(full, false, givenLow, startVal);
                if (doWindow) {
                    SigAnalWindows.applyWindowToSpectral(narrowR, window, s.getLowerFrequencyBound(), true, true);
                }
                if (!noZeros) {
                    narrowR = FlatArray.convertToFullSpectrum(narrowR, s.getOriginalN(), false, true, true, givenLow);
                    newNarrow = false;
                }
                if (!s.isTwoSided()) {
                    narrowR = FlatArray.convertToOneSided(narrowR, s.getOriginalN(), newNarrow, (givenLow == 0));
                }
                full = FlatArray
                        .convertToFullSpectrum(s.getDataImag(), s.getOriginalN(), !s.isTwoSided(), false, s.isNarrow(),
                                givenLow);
                narrowI = FlatArray.convertToNarrowBand(full, false, givenLow, startVal);
                if (doWindow) {
                    SigAnalWindows.applyWindowToSpectral(narrowI, window, s.getLowerFrequencyBound(), true, true);
                }
                if (!noZeros) {
                    narrowI = FlatArray.convertToFullSpectrum(narrowI, s.getOriginalN(), false, false, true, givenLow);
                }
                if (!s.isTwoSided()) {
                    narrowI = FlatArray.convertToOneSided(narrowI, s.getOriginalN(), newNarrow, (givenLow == 0));
                }
                output = new ComplexSpectrum(s.isTwoSided(), newNarrow, narrowR, narrowI, s.getOriginalN(),
                        s.getFrequencyResolution(),
                        (newNarrow) ? startVal * s.getFrequencyResolution() : s.getUpperFrequencyBound());
                if (nyquist) {
                    output = ComplexSpectrum.reduceNyquist((ComplexSpectrum) output, false);
                }
            }
        } else if (input instanceof Spectrum) {
            Spectrum s = (Spectrum) input;
            if (lowPass < s.getUpperFrequencyBound()) {
                startVal = (int) Math.round(lowPass / s.getFrequencyResolution());
                givenLow = (int) Math.round(s.getLowerFrequencyBound() / s.getFrequencyResolution());
                full = FlatArray
                        .convertToFullSpectrum(s.getDataReal(), s.getOriginalN(), !s.isTwoSided(), true, s.isNarrow(),
                                givenLow);
                narrowR = FlatArray.convertToNarrowBand(full, false, givenLow, startVal);
                if (doWindow) {
                    SigAnalWindows.applyWindowToSpectral(narrowR, window, s.getLowerFrequencyBound(), true, true);
                }
                if (!noZeros) {
                    narrowR = FlatArray.convertToFullSpectrum(narrowR, s.getOriginalN(), false, true, true, givenLow);
                    newNarrow = false;
                }
                if (!s.isTwoSided()) {
                    narrowR = FlatArray.convertToOneSided(narrowR, s.getOriginalN(), newNarrow, (givenLow == 0));
                }
                output = new Spectrum(s.isTwoSided(), newNarrow, narrowR, s.getOriginalN(), s.getFrequencyResolution(),
                        (newNarrow) ? startVal * s.getFrequencyResolution() : s.getUpperFrequencyBound());
                if (nyquist) {
                    output = Spectrum.reduceNyquist((Spectrum) output, false);
                }
            }

        } else if (input instanceof TimeFrequency) {
            TimeFrequency tf = (TimeFrequency) input;
            if (lowPass < tf.getUpperFrequencyBound()) {
                boolean complex = tf.isDependentComplex(0);
                startVal = (int) Math.round(lowPass / tf.getFrequencyResolution());
                givenLow = (int) Math.round(tf.getLowerFrequencyBound() / tf.getFrequencyResolution());
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
                    narrowR = FlatArray.convertToNarrowBand(full, false, givenLow, startVal);
                    if (doWindow) {
                        SigAnalWindows.applyWindowToSpectral(narrowR, window, tf.getLowerFrequencyBound(), true, true);
                    }
                    if (!noZeros) {
                        narrowR = FlatArray
                                .convertToFullSpectrum(narrowR, tf.getOriginalN(), false, true, true, givenLow);
                        newNarrow = false;
                    }
                    if (!tf.isTwoSided()) {
                        narrowR = FlatArray.convertToOneSided(narrowR, tf.getOriginalN(), newNarrow, (givenLow == 0));
                    }
                    filteredMatrixR[k] = narrowR;
                }
                if (complex) {
                    matrixI = tf.getDataImag();
                    for (int k = 0; k < nSpectra; k++) {
                        spectrumI = matrixI[k];
                        full = FlatArray.convertToFullSpectrum(spectrumI, tf.getOriginalN(), !tf.isTwoSided(), true,
                                tf.isNarrow(), givenLow);
                        narrowI = FlatArray.convertToNarrowBand(full, false, givenLow, startVal);
                        if (doWindow) {
                            SigAnalWindows
                                    .applyWindowToSpectral(narrowI, window, tf.getLowerFrequencyBound(), true, true);
                        }
                        if (!noZeros) {
                            narrowI = FlatArray
                                    .convertToFullSpectrum(narrowI, tf.getOriginalN(), false, true, true, givenLow);
                            newNarrow = false;
                        }
                        if (!tf.isTwoSided()) {
                            narrowI = FlatArray
                                    .convertToOneSided(narrowI, tf.getOriginalN(), newNarrow, (givenLow == 0));
                        }
                        filteredMatrixI[k] = narrowI;
                    }
                }
                output = new TimeFrequency(filteredMatrixR, filteredMatrixI, tf.isTwoSided(), newNarrow,
                        tf.getOriginalN(), tf.getFrequencyResolution(),
                        (newNarrow) ? startVal * tf.getFrequencyResolution() : tf.getUpperFrequencyBound(),
                        tf.getInterval(), tf.getAcquisitionTime());
                if (nyquist) {
                    output = TimeFrequency.reduceNyquist((TimeFrequency) output, false);
                }
            }
        }
        return output;
    }

    /**
     * Initialses information specific to LowPass.
     */
    public void init() {
        super.init();

        setUseGUIBuilder(true);

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
        addGUILine("Give upper frequency limit (Hz) $title lowPass Scroller 0 1000 100");
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
     * Saves LowPass's parameters.
     */
    public void saveParameters() {
        saveParameter("lowPass", lowPass);
        saveParameter("noZeros", noZeros);
        saveParameter("nyquist", nyquist);
        saveParameter("window", window);
    }


    /**
     * Used to set each of LowPass's parameters.
     */
    public void setParameter(String name, String value) {
        updateGUIParameter(name, value);

        if (name.equals("lowPass")) {
            lowPass = strToDouble(value);
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
     * @return a string containing the names of the types allowed to be input to LowPass, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "ComplexSpectrum Spectrum TimeFrequency";
    }

    /**
     * @return a string containing the names of the types output from LowPass, each separated by a white space.
     */
    public String outputTypes() {
        return "ComplexSpectrum Spectrum TimeFrequency";
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Low-pass filter in frequency domain";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "LowPass.html";
    }
}




