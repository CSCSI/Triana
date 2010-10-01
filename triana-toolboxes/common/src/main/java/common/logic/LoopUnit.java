package common.logic;

import java.util.ArrayList;
import java.util.Iterator;

import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.Unit;
import triana.types.TrianaType;
import triana.types.util.Str;


/**
 * Loop provides the user with a facility to create loops within the network
 *
 * @author Shalil Majithia
 * @version $Revision: 2921 $
 */
public class LoopUnit extends Unit {

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
            input = (TrianaType) getInputAtNode(0);
            loopStarting = false;
        } else {
            input = (TrianaType) getInputAtNode(1);
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

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);    }

    /**
     * Reset Loop
     */
    public void reset() {
        super.reset();
    }


    /**
     * Saves Loop parameters.
     */
//    public void saveParameters() {
//        saveParameter("start", startParameterValue);
//        saveParameter("end", endParameterValue);
//        saveParameter("increment", incrementValue);
//    }

    /**
     * Used to set each of Loop parameters.
     */
    public void parameterUpdate(String name, String value) {
//        updateGUIParameter(name, value);

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
        return "This Unit is used to implement a Loop";
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

    public String[] getInputTypes() {
        return new String[]{"java.lang.Object"};
    }

    /**
     * @return a string containing the names of the types output from Compare, each separated by a white space.
     */
    public String[] getOutputTypes() {
        return new String[]{"java.lang.Object"};
    }



}






