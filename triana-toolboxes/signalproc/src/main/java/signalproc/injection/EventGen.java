package signalproc.injection;

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


import java.util.Date;
import java.util.Random;

import triana.types.OldUnit;
import triana.types.SampleSet;
import triana.types.util.TrianaSort;


/**
 * A EventGen unit to generate time-series events and add them to an input data stream. The events can be of several
 * types.
 *
 * @author Bernard Schutz
 * @version 1.0 Final 25 Jun 2000
 */
public class EventGen extends OldUnit {

    String type = "Gaussian_noise";
    double amplitude = 1;
    double duration = 0.01;
    double interval = 1;
    double scale = 0;
    boolean firstSet = true;
    double thisAmplitude, thisScale, samplingRate, listDuration;
    long meanInterval, sampleOffset, thisDuration;
    int numEvents = 1000;
    double[] eventLocations = new double[numEvents];
    long[] eventTimes = new long[numEvents];
    SampleSet input, output;
    double[] data;
    int length, currentIndex;
    Random generator = new Random((new Date()).getTime());

    /**
     * ********************************************* ** USER CODE of EventGen goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        int j;
        boolean tryDuplicateEventTimes;

        input = (SampleSet) getInputNode(0);
        if (firstSet) {
            setup();
            firstSet = false;
        }
        output = (SampleSet) input.copyMe();
        data = output.getData();
        length = output.size();
        if (((length + sampleOffset) > listDuration) || (output.getSamplingRate() != samplingRate) || (meanInterval
                != Math.round(samplingRate * interval))) {
            setup();
        }

        if (currentIndex < numEvents) {
            for (j = (int) sampleOffset; ((j < length + (int) sampleOffset) && (currentIndex < numEvents)); j++) {
                if (j == eventTimes[currentIndex]) {
                    generateEvent(j - (int) sampleOffset);
                    currentIndex += 1;
                    tryDuplicateEventTimes = true;
                    while ((currentIndex < numEvents) && tryDuplicateEventTimes) {
                        if (eventTimes[currentIndex] == j) {
                            currentIndex += 1;
                        } else {
                            tryDuplicateEventTimes = false;
                        }
                    }
                }
            }
        }
        sampleOffset += length;

        output(output);

    }

    private void setup() {
        int j;
        sampleOffset = 0;
        samplingRate = input.getSamplingRate();
        meanInterval = Math.round(samplingRate * interval);
        listDuration = samplingRate * interval * numEvents;
        for (j = 0; j < numEvents; j++) {
            eventLocations[j] = listDuration * generator.nextDouble();
        }
        eventLocations = TrianaSort.mergeSort(eventLocations, 1.0);
        for (j = 0; j < numEvents; j++) {
            eventTimes[j] = Math.round(eventLocations[j]);
        }
        currentIndex = 0;
    }

    private void generateEvent(int j) {
        int k;
        double phaseScale;
        thisAmplitude = 2.0 * amplitude * generator.nextDouble();
        thisDuration = Math.round(2.0 * samplingRate * duration * generator.nextDouble());
        if (thisDuration == 0) {
            thisDuration = 1;
        }
        thisDuration = Math.min(thisDuration, length - j);
        if (type.equals("Gaussian_noise")) {
            for (k = j; k < thisDuration + j; k++) {
                data[k] += generator.nextGaussian() * thisAmplitude + scale;
            }
        } else if (type.equals("Uniform_noise")) {
            for (k = j; k < thisDuration + j; k++) {
                data[k] += (generator.nextDouble() - 0.5) * 2.0 * thisAmplitude + scale;
            }
        } else if (type.equals("Linear_chirp")) {
            phaseScale = Math.PI * scale / samplingRate / thisDuration;
            for (k = 0; k < thisDuration; k++) {
                data[k + j] += thisAmplitude * Math.sin(phaseScale * k * k);
            }
        } else if (type.equals("Ring")) {
            phaseScale = 2 * Math.PI * scale / samplingRate;
            for (k = 0; k < thisDuration; k++) {
                data[k + j] += thisAmplitude * Math.sin(phaseScale * k);
            }
        }
    }


    /**
     * Initialses information specific to EventGen.
     */
    public void init() {
        super.init();
        firstSet = true;
        setUseGUIBuilder(true);

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(1);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        setResizableInputs(false);
        setResizableOutputs(true);
    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format.
     */
    public void setGUIInformation() {
        addGUILine("Choose event type $title type Choice Gaussian_noise Uniform_noise Linear_chirp Ring");
        addGUILine("Amplitude $title amplitude Scroller 0 10 1");
        addGUILine("Mean duration (sec) $title duration Scroller 0 1 0.01");
        addGUILine("Mean interval between events (sec) $title interval Scroller 0 10 1");
        addGUILine("Scale: noise mean, chirp freq (Hz), ring freq (Hz) $title scale Scroller 0 10000 0");
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
     * Saves EventGen's parameters.
     */
    public void saveParameters() {
        saveParameter("type", type);
        saveParameter("amplitude", amplitude);
        saveParameter("duration", duration);
        saveParameter("interval", interval);
        saveParameter("scale", scale);
    }

    /**
     * Used to set each of EventGen's parameters.
     */
    public void setParameter(String name, String value) {
        updateGUIParameter(name, value);

        if (name.equals("type")) {
            type = value;
        }
        if (name.equals("amplitude")) {
            amplitude = strToDouble(value);
        }
        if (name.equals("duration")) {
            duration = strToDouble(value);
        }
        if (name.equals("interval")) {
            interval = strToDouble(value);
        }
        if (name.equals("scale")) {
            scale = strToDouble(value);
        }
    }

    /**
     * @return a string containing the names of the types allowed to be input to EventGen, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "SampleSet";
    }

    /**
     * @return a string containing the names of the types output from EventGen, each separated by a white space.
     */
    public String outputTypes() {
        return "SampleSet";
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Adds random events to an input data stream";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "EventGen.html";
    }
}




