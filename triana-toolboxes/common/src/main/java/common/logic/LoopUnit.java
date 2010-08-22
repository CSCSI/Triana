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

package common.logic;


import java.util.ArrayList;
import java.util.Iterator;

import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskGraph;
import triana.types.OldUnit;
import triana.types.TrianaType;
import triana.types.util.Str;


/**
 * Loop provides the user with a facility to create loops within the network
 *
 * @author Shalil Majithia
 * @version $Revision: 2921 $
 */
public class LoopUnit extends OldUnit {

    private String parameterName;
    private int startParameterValue = 0;
    private int endParameterValue = 0;
    private int incrementValue = 0;
    private boolean loopStarting = true;

    //updated internally
    private int currentParameterValue = 0;

    //implements Loop functionality

    public void process() throws Exception {
        TrianaType input;

        if (loopStarting) {
            input = (TrianaType) getInputNode(0);
            loopStarting = false;
        } else {
            input = (TrianaType) getInputNode(1);
        }

        if (loopCompleted()) {
            outputAtNode(0, input);
        } else {
            outputAtNode(1, input);
        }

        updateParameterValue();
    }


    public boolean loopCompleted() {
        return (currentParameterValue == endParameterValue);
    }

    public void updateParameterValue() {
        currentParameterValue = currentParameterValue + incrementValue;
    }

    //initialise Loop object information

    public void init() {
        super.init();

        setUseGUIBuilder(true);
        setResizableInputs(false);
        setResizableOutputs(false);
    }

    /**
     * Reset Loop
     */
    public void reset() {
        super.reset();
    }


    /**
     * Saves Loop parameters.
     */
    public void saveParameters() {
        saveParameter("start", startParameterValue);
        saveParameter("end", endParameterValue);
        saveParameter("increment", incrementValue);
    }

    /**
     * Used to set each of Loop parameters.
     */
    public void setParameter(String name, String value) {
        updateGUIParameter(name, value);

        if (name.equals("start")) {
            startParameterValue = Str.strToInt(value);
        }
        if (name.equals("end")) {
            endParameterValue = Str.strToInt(value);
        }
        if (name.equals("increment")) {
            incrementValue = Str.strToInt(value);
        }
    }


    /*
    /**
     * This returns a <b>brief!</b> description of what the unit does. The
     * text here is shown in a pop up window when the user puts the mouse
     * over the unit icon for more than a second.
     */

    public String getPopUpDescription() {
        return "This OldUnit is used to implement a Loop";
    }

    /**
     * @return the location of the help file for this unit.
     */
    public String getHelpFile() {
        return "Loop.html";
    }

    /**
     * @return the parameters of the tasks contained within the loop
     */

    public String[] getLoopTaskParams() {
        return getLoopTaskParams(this.getTask());
    }

    /**
     * @return the parameters of the tasks contained within the loop
     */

    public String[] getLoopTaskParams(Task ti) {
        //get array of top level tasks within this group
        Task[] topLevelTasks = ti.getParent().getTasks(false);

        //create arraylist to store all tasks
        ArrayList allTasks = new ArrayList();

        //store tasks in arraylist
        for (int i = 0; i < topLevelTasks.length; i++) {

            if (topLevelTasks[i] instanceof TaskGraph) {
                getLoopTaskParams(topLevelTasks[i]);
            } else {
                allTasks.add(topLevelTasks[i]);
            }
        }

        //arraylist to store parameter names
        ArrayList paramNames = new ArrayList();

        //Iterator to iterate through allTasks
        Iterator e = allTasks.iterator();

        //get and store parameter names into paramnames arraylist
        while (e.hasNext()) {
            String[] temp = ((Task) e.next()).getParameterNames();
            for (int count = 0; count < temp.length; count++) {
                paramNames.add(temp[count]);
            }

        }

        //convert arraylist to string []
        String[] parameterNames = new String[paramNames.size()];
        Iterator yai = paramNames.iterator();

        int index = 0;
        while (yai.hasNext()) {
            parameterNames[index] = (String) yai.next();
            index++;
        }


        return parameterNames;
    }

}






