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
 * A HighPass unit to perform a low-pass filter in the frequency domain. The unit returns a spectral data set with all
 * frequencies above the input highPass set to zero. The user can elect to return a narrow-band data set or one padded
 * with zeros, and can also select to have the result windowed in the frequency domain.
 *
 * @author B F Schutz
 * @version 2.0 02 Mar 2001
 */
public class HighPass extends OldUnit {

    double highPass = 100;
    boolean noZeros = false;
    String window = "Rectangle";


    /**
     * ********************************************* ** USER CODE of HighPass goes here    ***
     * *********************************************
     */
    public void process() throws Exception {

        GraphType input;
        GraphType output = null;

        input = (GraphType) getInputNode(0);
        output = filterToMin(input, highPass, noZeros, window);
        output(output);
    }


    public static GraphType filterToMin(GraphType input, double highPass, boolean noZeros, String window) {

        int startVal, givenLow, givenHigh;
        double[] full, narrowR, narrowI;
        boolean newNarrow = true;
        boolean doWindow = !window.equals("Rectangle");
        GraphType output = input;

        if (input instanceof ComplexSpectrum) {
            ComplexSpectrum s = (ComplexSpectrum) input;
            if (highPass > s.getLowerFrequencyBound()) {
                startVal = (int) Math.round(highPass / s.getFrequencyResolution());
                givenLow = (int) Math.round(s.getLowerFrequencyBound() / s.getFrequencyResolution());
                givenHigh = (int) Math.round(s.getUpperFrequencyBound() / s.getFrequencyResolution());
                full = FlatArray
                        .convertToFullSpectrum(s.getDataReal(), s.getOriginalN(), !s.isTwoSided(), true, s.isNarrow(),
                                givenLow);
                narrowR = FlatArray.convertToNarrowBand(full, false, startVal, givenHigh);
                if (doWindow) {
                    SigAnalWindows.applyWindowToSpectral(narrowR, window, highPass, true, true);
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
                narrowI = FlatArray.convertToNarrowBand(full, false, startVal, givenHigh);
                if (doWindow) {
                    SigAnalWindows.applyWindowToSpectral(narrowI, window, highPass, true, true);
                }
                if (!noZeros) {
                    narrowI = FlatArray.convertToFullSpectrum(narrowI, s.getOriginalN(), false, false, true, startVal);
                }
                if (!s.isTwoSided()) {
                    narrowI = FlatArray.convertToOneSided(narrowI, s.getOriginalN(), newNarrow, false);
                }
                output = new ComplexSpectrum(s.isTwoSided(), newNarrow, narrowR, narrowI, s.getOriginalN(),
                        s.getFrequencyResolution(), (newNarrow) ? s.getUpperFrequencyBound()
                                : (s.getOriginalN() * s.getFrequencyResolution() / 2.0));
            }
        } else if (input instanceof Spectrum) {
            Spectrum s = (Spectrum) input;
            if (highPass > s.getLowerFrequencyBound()) {
                startVal = (int) Math.round(highPass / s.getFrequencyResolution());
                givenLow = (int) Math.round(s.getLowerFrequencyBound() / s.getFrequencyResolution());
                givenHigh = (int) Math.round(s.getUpperFrequencyBound() / s.getFrequencyResolution());
                full = FlatArray
                        .convertToFullSpectrum(s.getDataReal(), s.getOriginalN(), !s.isTwoSided(), true, s.isNarrow(),
                                givenLow);
                narrowR = FlatArray.convertToNarrowBand(full, false, startVal, givenHigh);
                if (doWindow) {
                    SigAnalWindows.applyWindowToSpectral(narrowR, window, highPass, true, true);
                }
                if (!noZeros) {
                    narrowR = FlatArray.convertToFullSpectrum(narrowR, s.getOriginalN(), false, true, true, startVal);
                    newNarrow = false;
                }
                if (!s.isTwoSided()) {
                    narrowR = FlatArray.convertToOneSided(narrowR, s.getOriginalN(), newNarrow, false);
                }
                output = new Spectrum(s.isTwoSided(), newNarrow, narrowR, s.getOriginalN(), s.getFrequencyResolution(),
                        (newNarrow) ? s.getUpperFrequencyBound()
                                : (s.getOriginalN() * s.getFrequencyResolution() / 2.0));
            }

        } else if (input instanceof TimeFrequency) {
            TimeFrequency tf = (TimeFrequency) input;
            if (highPass > tf.getLowerFrequencyBound()) {
                boolean complex = tf.isDependentComplex(0);
                startVal = (int) Math.round(highPass / tf.getFrequencyResolution());
                givenLow = (int) Math.round(tf.getLowerFrequencyBound() / tf.getFrequencyResolution());
                givenHigh = (int) Math.round(tf.getUpperFrequencyBound() / tf.getFrequencyResolution());
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
                    narrowR = FlatArray.convertToNarrowBand(full, false, startVal, givenHigh);
                    if (doWindow) {
                        SigAnalWindows.applyWindowToSpectral(narrowR, window, highPass, true, true);
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
                        narrowI = FlatArray.convertToNarrowBand(full, false, startVal, givenHigh);
                        if (doWindow) {
                            SigAnalWindows.applyWindowToSpectral(narrowI, window, highPass, true, true);
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
                        tf.getOriginalN(), tf.getFrequencyResolution(), (newNarrow) ? tf.getUpperFrequencyBound()
                                : (tf.getOriginalN() * tf.getFrequencyResolution() / 2.0), tf.getInterval(),
                        tf.getAcquisitionTime());
            }
        }
        return output;
    }


    /**
     * Initialses information specific to HighPass.
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
        addGUILine("Give lower frequency limit (Hz) $title highPass Scroller 0 1000 100");
        addGUILine(
                "Output narrow-band? (Do not check if you want full-band output with zeros.) $title noZeros Checkbox false");
        addGUILine("Choose window for smoothing filter edges in frequency-domain $title window Choice " + SigAnalWindows
                .listOfWindows());
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
     * Saves HighPass's parameters.
     */
    public void saveParameters() {
        saveParameter("highPass", highPass);
        saveParameter("noZeros", noZeros);
        saveParameter("window", window);
    }


    /**
     * Used to set each of HighPass's parameters.
     */
    public void setParameter(String name, String value) {
        updateGUIParameter(name, value);

        if (name.equals("highPass")) {
            highPass = strToDouble(value);
        }
        if (name.equals("noZeros")) {
            noZeros = strToBoolean(value);
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
     * @return a string containing the names of the types allowed to be input to HighPass, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "ComplexSpectrum Spectrum TimeFrequency";
    }

    /**
     * @return a string containing the names of the types output from HighPass, each separated by a white space.
     */
    public String outputTypes() {
        return "ComplexSpectrum Spectrum TimeFrequency";
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "High-pass filter in frequency domain";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "HighPass.html";
    }
}




