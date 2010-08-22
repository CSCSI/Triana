package signalproc.dataparam;

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


import triana.types.GraphType;
import triana.types.OldUnit;
import triana.types.Signal;
import triana.types.TimeFrequency;
import triana.types.VectorType;


/**
 * A SetAcqTime unit to reset the acquisition time.
 *
 * @author B F Schutz
 * @version 1.1 27 June 2001
 */
public class SetAcqTime extends OldUnit {

    double newTime = 0;
    double lastTime;
    boolean firstTimeCalled = true;


    /**
     * ********************************************* ** USER CODE of SetAcqTime goes here    ***
     * *********************************************
     */
    public void process() throws Exception {

        double timeToSet;
        int length;

        GraphType in = (GraphType) getInputNode(0);

        if (in instanceof Signal) {

            Signal input = (Signal) in;

            timeToSet = (firstTimeCalled) ? newTime : lastTime;
            firstTimeCalled = false;

            if (input instanceof VectorType) {
                lastTime = timeToSet + ((VectorType) input).size() / input.getSamplingRate();
            } else if (input instanceof TimeFrequency) {
                lastTime = timeToSet + ((TimeFrequency) input).getDimensionLengths(0) * ((TimeFrequency) input)
                        .getInterval();
            } else {
                lastTime = newTime;
            }

            input.setAcquisitionTime(timeToSet);

            output((GraphType) input);
        } else {
            output(in);
        }
    }


    /**
     * Initialses information specific to SetAcqTime.
     */
    public void init() {
        super.init();

        setUseGUIBuilder(true);

        setRequireDoubleInputs(false);
        setCanProcessDoubleArrays(false);

        setResizableInputs(false);
        setResizableOutputs(true);
    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format.
     */
    public void setGUIInformation() {
        addGUILine("Give new acquisition time $title newTime Scroller 0 100000 0");
    }

    /**
     * Called when the reset button is pressed within the MainTriana Window
     */
    public void reset() {
        super.reset();
        firstTimeCalled = true;
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
     * Saves SetAcqTime's parameters.
     */
    public void saveParameters() {
        saveParameter("newTime", newTime);
    }


    /**
     * Used to set each of SetAcqTime's parameters.
     */
    public void setParameter(String name, String value) {
        updateGUIParameter(name, value);

        if (name.equals("newTime")) {
            newTime = strToDouble(value);
        }
    }

    /**
     * Don't need to use this for GUI Builder units as everthing is updated by triana automatically
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to SetAcqTime, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "GraphType";
    }

    /**
     * @return a string containing the names of the types output from SetAcqTime, each separated by a white space.
     */
    public String outputTypes() {
        return "GraphType";
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Set a new acquisition time";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "SetAcqTime.html";
    }
}




