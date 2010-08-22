package common.input;

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


import triana.types.Const;
import triana.types.OldUnit;
import triana.types.util.Str;

/**
 * A Count unit to increment an output Const each time it is activated.
 *
 * @author Ian Taylor
 * @author B F Schut
 * @version 1.01 20 August 2000
 */
public class Count extends OldUnit {

    double st = 0.0;
    double inc = 1.0;
    double end = Double.MAX_VALUE;
    double curr = 0.0;

    /**
     * ********************************************* ** USER CODE of Count goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        if (((inc > 0) && (curr <= end)) || ((inc < 0) && (curr >= end))) {
            output(new Const(curr));

            curr = curr + inc;

            if (((inc > 0) && (curr <= end)) || ((inc < 0) && (curr >= end))) {
                setParameter("current", (Object) String.valueOf(curr));
            } else {
                setParameter("current", (Object) "N/A");
            }
        }
    }


    /**
     * Initialses information specific to Count.
     */
    public void init() {
        super.init();

        setUseGUIBuilder(true);

        setResizableInputs(false);
        setResizableOutputs(true);

        defineParameter("start", String.valueOf(st), USER_ACCESSIBLE);
        defineParameter("increment", String.valueOf(inc), USER_ACCESSIBLE);

        if (end == Double.MAX_VALUE) {
            defineParameter("end", "", USER_ACCESSIBLE);
        } else {
            defineParameter("end", String.valueOf(end), USER_ACCESSIBLE);
        }

        defineParameter("current", String.valueOf(curr), USER_ACCESSIBLE);
    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format (see Triana help).
     */
    public void setGUIInformation() {
        addGUILine("Starting Value $title start TextField 0.0");
        addGUILine("Increment $title increment TextField 1.0");
        addGUILine("End Value $title end TextField");
        addGUILine("Next Value $title current Label 0.0");
    }

    /**
     * Reset's Count
     */
    public void reset() {
        super.reset();
        st = Str.strToDouble((String) getParameter("start"));
        inc = Str.strToDouble((String) getParameter("increment"));

        if (getParameter("end").equals("")) {
            if (inc > 0) {
                end = Double.MAX_VALUE;
            } else {
                end = Double.MIN_VALUE;
            }
        } else {
            end = Str.strToDouble((String) getParameter("end"));
        }

        curr = Str.strToDouble((String) getParameter("start"));
    }


    /**
     * Used to set each of Count's parameters.
     */
    public void setParameter(String name, String value) {
        if (name.equals("start")) {
            st = Str.strToDouble(value);
            curr = st;
        }
        if (name.equals("increment")) {
            inc = Str.strToDouble(value);
        }
        if (name.equals("end")) {
            if (value.equals("")) {
                if (inc > 0) {
                    end = Double.MAX_VALUE;
                } else {
                    end = Double.MIN_VALUE;
                }
            } else {
                end = Str.strToDouble(value);
            }
        }

        if (((inc > 0) && (curr <= end)) || ((inc < 0) && (curr >= end))) {
            setParameter("current", (Object) String.valueOf(curr));
        } else {
            setParameter("current", (Object) "N/A");
        }
    }

    /**
     * @return a string containing the names of the types allowed to be input to Count, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "none";
    }

    /**
     * @return a string containing the names of the types output from Count, each separated by a white space.
     */
    public String outputTypes() {
        return "Const";
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Increments its output Const each time it is activated";
    }

    /**
     *
     * @returns the location of the help file for this unit.
     */
    public String getHelpFile() {
        return "Count.html";
    }
}













