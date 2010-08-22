package signalproc.time;

/*
 * Copyright (c) 1995 - 1998 University of Wales College of Cardiff
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


import triana.types.OldUnit;
import triana.types.VectorType;


/**
 * A Pad unit to add zeros at the end of a data set.
 *
 * @author Ian Taylor
 * @author Bernard Schutz
 * @version 2.0 10 September 2000
 */
public class Pad extends OldUnit {

    int pad;
    String method;
    String place;


    /**
     * ********************************************* ** USER CODE of Pad goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        VectorType data = (VectorType) getInputNode(0);
        VectorType newData = (VectorType) data.copyMe();

        int newSize;
        boolean before = place.equals("AtBeginning");

        if (method.equals("NearestPowerOf2")) {
            int k = newData.size();
            newSize = 1;

            while (newSize < k) {
                newSize *= 2;
            }
            if (newSize != k) {
                newData.extendWithZeros(newSize, before);
            } else {
                println("No change to data set in unit " + getName() + " because size is already an exact power of 2 ("
                        + String.valueOf(k) + ").");
            }
        } else if (method.equals("GivenPowerOf2")) {
            newSize = (int) Math.pow(2, pad);
            if (newSize > newData.size()) {
                newData.extendWithZeros(newSize, before);
            } else {
                println("No change to data set in unit " + getName() + " because given power of 2 ("
                        + String.valueOf(pad) + ") was too small for existing size (" + String.valueOf(newData.size())
                        + ").");
            }
        } else if (method.equals("GivenMultipleOfLength")) {
            if (pad > 1) {
                newData.extendWithZeros(pad * newData.size(), before);
            } else {
                println("No change to data set in unit " + getName() + " because given multiple (" + String.valueOf(pad)
                        + ") was less than or equal to 1.");
            }
        } else {
            if (pad > newData.size()) {
                newData.extendWithZeros(pad, before);
            } else {
                println("No change to data set in unit " + getName() + " because given new length ("
                        + String.valueOf(pad) + ") is smaller than existing length (" + String.valueOf(newData.size())
                        + ").");
            }
        }

        output(newData);
    }


    /**
     * Initialises information specific to Pad.
     */
    public void init() {
        super.init();

        setUseGUIBuilder(true);

        setResizableInputs(false);
        setResizableOutputs(true);
        pad = 1;
        method = "GivenMultipleOfLength";
        place = "AtEnd";
    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format (see Triana help).
     */
    public void setGUIInformation() {
        addGUILine("Choose where to add extra zeros to this data set: $title place Choice AtEnd AtBeginning");
        addGUILine(
                "Extend length of set to (choose method): $title method Choice GivenMultipleOfLength NearestPowerOf2 GivenPowerOf2 GivenNumberOfElements");
        addGUILine(
                "Give appropriate value (multiple (1 means do nothing), power of 2, or total number $title pad IntScroller 0 20 1");
    }

    /**
     * Resets Pad
     */
    public void reset() {
        super.reset();
        pad = 0;
//         setParameter("place", "AtEnd");
        //       setParameter("method", "GivenMultipleOfLength");
        //     setParameter("pad", "1");
    }

    /**
     * Saves Pad's parameters.
     */
    public void saveParameters() {
        saveParameter("place", place);
        saveParameter("method", method);
        saveParameter("pad", pad);
    }

    /**
     * Used to set each of Pad's parameters.
     */
    public void setParameter(String name, String value) {
        updateGUIParameter(name, value);

        if (name.equals("place")) {
            place = value;
        }

        if (name.equals("method")) {
            method = value;
        }

        if (name.equals("pad")) {
            pad = strToInt(value);
        }
    }

    /**
     * @return a string containing the names of the types allowed to be input to Pad, each separated by a white space.
     */
    public String inputTypes() {
        return "VectorType";
    }

    /**
     * @return a string containing the names of the types output from Pad, each separated by a white space.
     */
    public String outputTypes() {
        return "VectorType";
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Extends data set with zeros";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "Pad.html";
    }
}













