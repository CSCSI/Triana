package common.sync;

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


import org.trianacode.taskgraph.Task;
import triana.types.OldUnit;

/**
 * A Block unit to ..
 *
 * @author ian
 * @version 1.0 beta 14 Sep 1999
 */
public class Block extends OldUnit {

    /**
     * ********************************************* ** USER CODE of Block goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        Task task = getTask();
        Object data;

        for (int count = 0; count < task.getDataInputNodeCount(); ++count) {
            data = getInputAtNode(count);

            if (count < task.getDataOutputNodeCount()) {
                outputAtNode(count, data);
            }
        }
    }


    /**
     * Initialses information specific to Block.
     */
    public void init() {
        super.init();

        setResizableInputs(true);
        setResizableOutputs(true);
    }


    /**
     * Reset's Block
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves Block's parameters.
     */
    public void saveParameters() {
    }

    /**
     * Used to set each of Block's parameters.
     */
    public void setParameter(String name, String value) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to Block, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "TrianaType";
    }

    /**
     * @return a string containing the names of the types output from Block, each separated by a white space.
     */
    public String outputTypes() {
        return "TrianaType";
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Put Block's brief description here";
    }

    /**
     *
     * @returns the location of the help file for this unit.
     */
    public String getHelpFile() {
        return "Block.html";
    }

}













