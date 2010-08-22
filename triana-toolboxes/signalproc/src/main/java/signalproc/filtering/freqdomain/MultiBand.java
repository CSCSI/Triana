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


import org.trianacode.taskgraph.NodeException;
import org.trianacode.taskgraph.Task;
import triana.types.GraphType;
import triana.types.OldUnit;
import triana.types.Spectral;
import triana.types.util.SigAnalWindows;


/**
 * A MultiBand unit to split an input spectrum into a number of consecutive narrow bands of uniform width. The unit
 * returns a spectral data set for each band with all frequencies outside the bandwidth set to zero. The user can elect
 * to return narrow-band data sets or ones padded with zeros, and can also select to have the result windowed in the
 * frequency domain.
 *
 * @author B F Schutz
 * @version 2.0 05 Mar 2001
 */
public class MultiBand extends OldUnit {

    double lowLimit = 50;
    double highLimit = 1000;
    int nBands = 2;
    boolean noZeros = false;
    String window = "Rectangle";
    boolean nyquist = false;


    /**
     * ********************************************* ** USER CODE of MultiBand goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        GraphType input;
        GraphType output;

        input = (GraphType) getInputNode(0);

        double maxFreq = ((Spectral) input).getFrequencyResolution(0) * ((Spectral) input).getOriginalN(0) / 2.0;
        if (highLimit > maxFreq) {
            highLimit = maxFreq;
        }
        if (lowLimit < 0) {
            lowLimit = 0;
        }
        double bandwidth = (highLimit - lowLimit) / nBands;
        double lowEdge = lowLimit;
        double highEdge = lowLimit + bandwidth;
        int j;

        if (lowLimit == 0) {
            output = LowPass.filterToMax((GraphType) input.copyMe(), highEdge, noZeros, window, false);
        } else {
            output = BandPass.filterToBand((GraphType) input.copyMe(), lowEdge, highEdge, noZeros, window, nyquist);
        }

        outputAtNode(0, output);

        for (j = 1; j < nBands; j++) {
            lowEdge += bandwidth;
            highEdge += bandwidth;
            output = BandPass.filterToBand((GraphType) input.copyMe(), lowEdge, highEdge, noZeros, window, nyquist);
            outputAtNode(j, output);
        }

    }


    /**
     * Initialses information specific to MultiBand.
     */
    public void init() {
        super.init();

        setUseGUIBuilder(true);

        setRequireDoubleInputs(false);
        setCanProcessDoubleArrays(false);

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(1);
        setParameterUpdatePolicy(IMMEDIATE_UPDATE);

        setDefaultOutputNodes(2);
        setMinimumOutputNodes(2);
        setMaximumOutputNodes(Integer.MAX_VALUE);
    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format.
     */
    public void setGUIInformation() {
        addGUILine("Lowest frequency (Hz): $title lowLimit Scroller 0 1000 50");
        addGUILine("Highest frequency (Hz): $title highLimit Scroller 0 4000 1000");
        addGUILine("Number of bands between these frequencies: $title nBands IntScroller 0 10 2");
        addGUILine(
                "Output narrow-band? (Do not check if you want full-band output with zeros.) $title noZeros Checkbox false");
        addGUILine("Choose window for smoothing filter edges in frequency-domain $title window Choice " + SigAnalWindows
                .listOfWindows());
        addGUILine("Reduce Nyquist frequency of each band to its upper band limit? $title nyquist Checkbox false");
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
     * Saves MultiBand's parameters.
     */
    public void saveParameters() {
        saveParameter("lowLimit", lowLimit);
        saveParameter("highLimit", highLimit);
        saveParameter("nBands", nBands);
        saveParameter("noZeros", noZeros);
        saveParameter("window", window);
        saveParameter("nyquist", nyquist);
    }


    /**
     * Used to set each of MultiBand's parameters.
     */
    public void setParameter(String name, String value) {
        updateGUIParameter(name, value);

        if (name.equals("lowLimit")) {
            lowLimit = strToDouble(value);
        }
        if (name.equals("highLimit")) {
            highLimit = strToDouble(value);
        }
        if (name.equals("nBands")) {
            nBands = strToInt(value);
            Task task = getTask();

            try {
                while (nBands > task.getDataOutputNodeCount()) {
                    task.addDataOutputNode();
                }

                while (nBands < getTask().getDataOutputNodeCount()) {
                    task.removeDataOutputNode(task.getDataOutputNode(task.getDataOutputNodeCount() - 1));
                }
            } catch (NodeException except) {
                notifyError(except.getMessage());
            }
            if (name.equals("noZeros")) {
                noZeros = strToBoolean(value);
            }
            if (name.equals("window")) {
                window = value;
            }
            if (name.equals("nyquist")) {
                nyquist = strToBoolean(value);
            }
        }
    }

    /**
     * Don't need to use this for GUI Builder units as everthing is updated by triana automatically
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to MultiBand, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "ComplexSpectrum Spectrum TimeFrequency";
    }

    /**
     * @return a string containing the names of the types output from MultiBand, each separated by a white space.
     */
    public String outputTypes() {
        return "ComplexSpectrum Spectrum TimeFrequency";
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Divide input spectrum into a number of narrow bands";
    }

    /**
     * @returns the location of the help file for this unit.
     */
    public String getHelpFile() {
        return "MultiBand.html";
    }
}
