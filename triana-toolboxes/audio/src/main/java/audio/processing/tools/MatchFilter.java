package audio.processing.tools;

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


import java.util.Iterator;
import java.util.TreeSet;

import triana.types.Document;
import triana.types.OldUnit;
import triana.types.audio.MultipleAudio;

/**
 * A MatchFilter unit to ..
 *
 * @author ian
 * @version 2.0 31 Dec 2000
 */
public class MatchFilter extends OldUnit {
    StringBuffer output;
    int count = 0;

    int matchNum = 10;
    TreeSet matches = new TreeSet();


    /**
     * ********************************************* ** USER CODE of MatchFilter goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        ++count;
        matches.clear();
        MultipleAudio wave = (MultipleAudio) getInputNode(0);
        MultipleAudio filter = (MultipleAudio) getInputNode(1);

        Object waveAudio = wave.getChannel(0);

        Object filterAudio = filter.getChannel(0);

        if (waveAudio instanceof short[]) { // 16-bit audio
            double min = 0.0;
            double max = 0.0;

            if (!(filterAudio instanceof short[])) {
                throw new Exception(
                        "Incompatible types in " + getName() + "\nWave is 16-bit and filter is 8 or 24-bit");
            }
            short[] waveAud = ((short[]) waveAudio);
            short[] filtAud = ((short[]) filterAudio);

            int length = waveAud.length - filtAud.length;
            int filtLen = filtAud.length;
            int j;
            double sum;

            for (int i = 0; i < length; ++i) {
                sum = 0.0;
                for (j = 0; j < filtLen; ++j) {
                    sum += waveAud[i + j] * filtAud[j];
                }
                if (sum > min) { // add to the list
                    if (sum > max) {
                        max = sum;
                    }
                    matches.add(new Val(sum, i)); // index in in waveAudio
                    if (matches.size() > matchNum) {
                        // System.out.println(matches);
                        matches.remove(matches.first()); // lowest val
                        min = ((Val) matches.first()).val;
                    }
                }
            }

            StringBuffer b = new StringBuffer();
            Iterator i = matches.iterator();
            Val v;
            while (i.hasNext()) { // reSCale now for efficiency
                v = (Val) i.next();
                v.val /= (double) filtAud.length; // scale back
                b.append(v.toString());
                b.append("\n");
            }

            output.append("Run : " + String.valueOf(count));
            output.append("\n");
            output.append(b.toString());
            output.append("\n");

            Document out = new Document();
            out.setText(output.toString());
            output(out);
        }
    }


    /**
     * Initialses information specific to MatchFilter.
     */
    public void init() {
        super.init();

        setUseGUIBuilder(true);
        setResizableInputs(false);
        setResizableOutputs(true);
        output = new StringBuffer();
        count = 0;
    }

    public void setGUIInformation() {
        addGUILine("Number of matchNum (Best match output first) ? $title matchNum IntScroller 0 100 10");
    }

    /**
     * Called when the reset button is pressed within the MainTriana Window
     */
    public void reset() {
        super.reset();
        output = new StringBuffer();
        count = 0;
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
     * Saves MatchFilter's parameters.
     */
    public void saveParameters() {
        saveParameter("matchNum", matchNum);
    }

    /**
     * Used to set each of MatchFilter's parameters. This should NOT be used to update this unit's user interface
     */
    public void setParameter(String name, String value) {
        updateGUIParameter(name, value);

        if (name.equals("matchNum")) {
            matchNum = strToInt(value);
        }
    }

    /**
     * Used to update the widget in this unit's user interface that is used to control the given parameter name.
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to MatchFilter, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "triana.types.audio.MultipleAudio";
    }

    /**
     * @return a string containing the names of the types output from MatchFilter, each separated by a white space.
     */
    public String outputTypes() {
        return "Document";
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Creates a response from the correlation of the\n" +
                " second input with the first at each point";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "MatchFilter.html";
    }

    private class Val implements Comparable {
        double val;
        int index;

        public Val(double v, int ind) {
            val = v;
            index = ind;
        }

        public int compareTo(Object o) {
            Val v = (Val) o;
            if (val < v.val) {
                return -1;
            }
            if (val == v.val) {
                return 0;
            } else {
                return 1;
            }
        }

        public String toString() {
            return "Response at Index[" + index + "] = " + val;
        }
    }
}



