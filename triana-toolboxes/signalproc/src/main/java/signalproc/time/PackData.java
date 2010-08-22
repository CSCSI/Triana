package signalproc.time;

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


import org.trianacode.gui.windows.ErrorDialog;
import triana.types.OldUnit;
import triana.types.SampleSet;


/**
 * A PackData unit to repackage a data stream into SampleSets of a specified length with an optional overlap between
 * successive sets. Since the input and output are generally asynchronous, it only makes sense to use this OldUnit in
 * Continuous mode.
 *
 * @author Bernard Schutz
 * @version 1.1 25 July 2000
 */
public class PackData extends OldUnit {

    int samples = 1024;
    int overlap = 0;
    int startOfNewSet = 0;
    int oldElementsRemaining, newElementsVacant, oldCopyStart, newCopyStart;
    SampleSet input, output;
    double[] newdata, prevdata;
    int length, copylength, overlapStart;
    double inputAcqTime, outputAcqTime, nextAcqTime, samplingRate;
    boolean firstTimeCalled = true;


    /**
     * ********************************************* ** USER CODE of PackData goes here    ***
     * *********************************************
     */
    public void process() throws Exception {

        overlapStart = samples - overlap;

        input = (SampleSet) getInputNode(0);
        length = input.size();
        oldElementsRemaining = length;
        inputAcqTime = input.getAcquisitionTime();
        oldCopyStart = 0;
        if (firstTimeCalled) {
            samplingRate = input.getSamplingRate();
            firstTimeCalled = false;
            newdata = new double[samples];
            newElementsVacant = samples;
            nextAcqTime = inputAcqTime;
            copylength = Math.min(samples, length);
            newCopyStart = 0;
        } else {
            copylength = Math.min(length, newElementsVacant);
        }
        if (samplingRate != input.getSamplingRate()) {
            new ErrorDialog(null,
                    "Error! The sampling rate of the input data sets has changed during the sequence. Repackaging cannot continue.");
            stop();
            return;
        }

        while (oldElementsRemaining > 0) {
            System.arraycopy(input.getData(), oldCopyStart, newdata, newCopyStart, copylength);
            oldElementsRemaining -= copylength;
            oldCopyStart = length - oldElementsRemaining;
            newElementsVacant -= copylength;
            newCopyStart = samples - newElementsVacant;
            if (newElementsVacant == 0) {
                outputAcqTime = nextAcqTime;
                nextAcqTime = inputAcqTime + newCopyStart / samplingRate;
                output(new SampleSet(samplingRate, newdata, outputAcqTime));
                prevdata = newdata;
                newdata = new double[samples];
                if (overlap > 0) {
                    System.arraycopy(prevdata, overlapStart, newdata, 0, overlap);
                }
                newElementsVacant = overlapStart;
                newCopyStart = samples - overlapStart;
            }
            copylength = Math.min(oldElementsRemaining, newElementsVacant);
        }
    }


    /**
     * Initialses information specific to PackData.
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
        addGUILine("Number of samples in new data sets $title samples IntScroller 0 8192 1024");
        addGUILine("Overlapping samples with next data set $title overlap IntScroller 0 2048 0");
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
     * Saves PackData's parameters.
     */
    public void saveParameters() {
        saveParameter("samples", samples);
        saveParameter("overlap", overlap);
    }

    /**
     * Used to set each of PackData's parameters.
     */
    public void setParameter(String name, String value) {
        updateGUIParameter(name, value);

        if (name.equals("samples")) {
            samples = strToInt(value);
        }
        if (name.equals("overlap")) {
            overlap = strToInt(value);
        }
    }

    /**
     * @return a string containing the names of the types allowed to be input to PackData, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "SampleSet";
    }

    /**
     * @return a string containing the names of the types output from PackData, each separated by a white space.
     */
    public String outputTypes() {
        return "SampleSet";
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Readjust lengths of  SampleSets and allow overlaps";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "PackData.html";
    }
}




