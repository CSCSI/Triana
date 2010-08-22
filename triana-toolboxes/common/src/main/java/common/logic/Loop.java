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

package common.logic;

import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.TaskGraphUtils;
import org.trianacode.taskgraph.Unit;
import org.trianacode.taskgraph.event.ParameterUpdateEvent;
import org.trianacode.taskgraph.event.TaskDisposedEvent;
import org.trianacode.taskgraph.event.TaskListener;
import org.trianacode.taskgraph.event.TaskNodeEvent;
import org.trianacode.taskgraph.event.TaskPropertyEvent;
import org.trianacode.taskgraph.service.SchedulerException;
import org.trianacode.taskgraph.tool.ClassLoaders;

/**
 * Control unit for looping over groups
 *
 * @author Ian Wang
 * @version $Revision: 2921 $
 */

public class Loop extends Unit implements TaskListener {

    // the class that determines when the loop exits
    private ExitCondition condition = null;

    // an error that is thrown when the unit is run with an invalid condition unit
    private String error = "Unknown";

    // a flag indicating whether looping is enabled
    private boolean loopenabled = false;

    // the number of iterations run in the current loop
    private int iterations = 0;

    // the total number of iterations run since last reset
    private int totalIterations = 0;

    // a flag indicating whether the loop is currently in a loop
    private boolean inloop = false;

    // the number of input/output nodes from the loop
    private int loopin = 1;
    private int loopout = 1;

    // the number of wakeups received by this unit
    private int wakeups = 0;

    // the number of wakeups handled (data reads/no data wakeups)
    private int wakehandled = 0;

    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {
        if (condition == null) {
            notifyError("Condition Unit Error: " + error);
        }

        if ((loopin == 0) && (loopout == 0)) {
            runZeroLoop();
        } else {
            runLoop();
        }
    }

    /**
     * Runs the loop when more than zero input/output nodes
     */
    private void runLoop() {
        Object[] data;
        wakeups++;

        try {
            while (isDataReady()) {
                if (!inloop) {
                    initLoop();
                }

                data = getData();
                wakehandled += Math.max(data.length, 1);

                if (loopenabled) {
                    runIteration(data, !isExitLoop(data));
                } else {
                    runIteration(data, !inloop);
                }
            }
        } catch (InvalidEquationException except) {
            notifyError(except.getMessage());
        } catch (SchedulerException except) {
        }
    }

    /**
     * Runs the loop zero input/output nodes
     */
    private void runZeroLoop() throws InvalidEquationException {
        Object[] data = new Object[0];
        initLoop();

        boolean run = (!loopenabled) || (!isExitLoop(data));

        try {
            while (run) {
                runIteration(data, true);
                run = loopenabled && (!isExitLoop(data));
            }

            inloop = false;
        } catch (InvalidEquationException except) {
            notifyError(except.getMessage());
        } catch (SchedulerException except) {
        }
    }

    /**
     * @return true if the exit condition is met
     */
    private boolean isExitLoop(Object[] data) throws InvalidEquationException {
        return condition.isExitLoop(data);
    }

    /**
     * Initialize a the loop before any iterations
     */
    private void initLoop() throws InvalidEquationException {
        iterations = 0;
        setParameter("iterations", String.valueOf(iterations));

        if (loopenabled) {
            condition.init();
        }
    }

    /**
     * Runs an iteration of the loop
     */
    private void runIteration(Object[] data, boolean loop) throws InvalidEquationException, SchedulerException {
        if (loopenabled) {
            condition.iteration();
        }

        if (loop) {
            if (TaskGraphUtils.isControlTask(getTask())) {
                getControlInterface().runGroup();
            }

            setParameter("iterations", String.valueOf(++iterations));
            setParameter("totalIterations", String.valueOf(++totalIterations));
        }

        if (inloop) {
            if (loop && (loopin > 0)) {
                for (int count = 0; count < Math.min(loopin, data.length); count++) {
                    outputAtNode(loopout + count, data[count]);
                }
            } else {
                for (int count = 0; count < loopout; count++) {
                    outputAtNode(count, data[count]);
                }
            }
        } else {
            if (loop && (loopin > 0)) {
                for (int count = 0; count < loopin; count++) {
                    outputAtNode(loopout + count, data[count]);
                }
            } else {
                for (int count = 0; count < Math.min(loopout, data.length); count++) {
                    outputAtNode(count, data[count]);
                }
            }
        }

        inloop = loop;
    }

    /**
     * @return true if the data to run an iteration is ready
     */
    private boolean isDataReady() {
        boolean ready = true;
        int min = 0;
        int max = loopin;

        if (inloop) {
            min = loopin;
            max = loopin + loopout;
        }

        if ((loopin == 0) && (loopout == 0)) {
            return true;
        } else if (min == max) {
            return wakeups > wakehandled;
        } else {
            for (int count = min; (count < max) && ready; count++) {
                ready = ready && isInputAtNode(count);
            }

            return ready;
        }
    }


    /**
     * @return an array of the input data on this iteration
     */
    private Object[] getData() {
        Object[] data;
        boolean ready = true;
        int min = 0;
        int max = loopin;

        if (inloop) {
            min = loopin;
            max = loopin + loopout;
        }

        data = new Object[max - min];

        for (int count = min; (count < max) && ready; count++) {
            data[count - min] = getInputAtNode(count);
        }

        return data;
    }

    /**
     * @return true if the task is acting a group control task
     */
    private boolean isControlTask() {
        Task task = getTask();
        TaskGraph parent = task.getParent();

        if (parent != null) {
            return parent.getControlTask() == task;
        } else {
            return false;
        }
    }


    /**
     * Loads the class specified in the conditionUnit parameter
     */
    private void loadConditionUnit() {
        String unit = (String) getParameter("conditionUnit");

        try {
            Class cls = ClassLoaders.forName(unit);

            condition = (ExitCondition) cls.newInstance();
            condition.setTask(getTask());
        } catch (ClassNotFoundException except) {
            condition = null;
            error = "Condition unit class not found!";
        } catch (InstantiationException except) {
            condition = null;
            error = "Error instantiating condition unit";
        } catch (IllegalAccessException except) {
            condition = null;
            error = except.getMessage();
        } catch (ClassCastException except) {
            condition = null;
            error = "Condition unit not instance of ExitCondition interface";
        }
    }

    /**
     * Updates the input/output node counts
     */
    private void updateNodeCount() {
        Task task = getTask();

        if (isControlTask()) {
            loopin = Math.min(task.getParent().getDataInputNodeCount(), task.getDataInputNodeCount());
            loopout = Math.min(task.getParent().getDataOutputNodeCount(), task.getDataOutputNodeCount());
        } else {
            loopin = task.getDataInputNodeCount() / 2;
            loopout = task.getDataInputNodeCount() - loopin;
        }
    }


    /**
     * Called when the unit is created. Initialises the unit's properties and parameters.
     */
    public void init() {
        super.init();

        // Initialise node properties
        setDefaultInputNodes(2);
        setMinimumInputNodes(2);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(2);
        setMinimumOutputNodes(2);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        // Initialise parameter update policy
        setParameterUpdatePolicy(PROCESS_UPDATE);
        setDefaultNodeRequirement(OPTIONAL);

        // Initialise pop-up description and help file location
        setPopUpDescription("A conditional looping unit");
        setHelpFileLocation("Loop.html");

        // Define initial value and type of parameters
        defineParameter("conditionUnit", "Common.Logic.DefaultExitCondition", USER_ACCESSIBLE);
        defineParameter("enabled", "false", USER_ACCESSIBLE);
        defineParameter("iterations", "0", USER_ACCESSIBLE);
        defineParameter("totalIterations", "0", USER_ACCESSIBLE);

        // Initialise custom panel interface
        setParameterPanelClass("common.logic.LoopPanel");

        // Add loop as task listener to itself
        getTask().addTaskListener(this);

        loadConditionUnit();
        updateNodeCount();
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        // Set unit variables to the values specified by the parameters
        loopenabled = new Boolean((String) getParameter("enabled")).booleanValue();

        resetLoop();
    }

    /**
     * Resets all the loop counts
     */
    private void resetLoop() {
        iterations = 0;
        totalIterations = 0;

        setParameter("iterations", "0");
        setParameter("totalIterations", "0");
        inloop = false;
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up Loop2 (e.g. close open files)
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        if (paramname.equals("conditionUnit")) {
            loadConditionUnit();
        }

        if (paramname.equals("enabled")) {
            boolean enable = new Boolean((String) value).booleanValue();

            if (enable != loopenabled) {
                loopenabled = enable;
                resetLoop();
            }
        }

        if (paramname.equals("iterations")) {
            iterations = new Integer((String) value).intValue();
        }

        if (paramname.equals("totalIterations")) {
            totalIterations = new Integer((String) value).intValue();
        }
    }


    /**
     * @return an array of the input types for Loop2
     */
    public String[] getInputTypes() {
        return new String[]{"java.lang.Object"};
    }

    /**
     * @return an array of the output types for Loop2
     */
    public String[] getOutputTypes() {
        return new String[]{"java.lang.Object"};
    }

    /**
     * Called when a data input node is added.
     */
    public void nodeAdded(TaskNodeEvent event) {
        updateNodeCount();
    }

    /**
     * Called before a data input node is removed.
     */
    public void nodeRemoved(TaskNodeEvent event) {
        if (event.isInputNode() && event.isDataNode()) {
            if (event.getNodeIndex() < loopin) {
                loopin--;
            } else {
                loopout--;
            }
        }
    }

    /**
     * Called when the core properties of a task change i.e. its name, whether it is running continuously etc.
     */
    public void taskPropertyUpdate(TaskPropertyEvent event) {
    }

    /**
     * Called when the value of a parameter is changed, including when a parameter is removed.
     */
    public void parameterUpdated(ParameterUpdateEvent event) {
    }

    /**
     * Called before the task is disposed
     */
    public void taskDisposed(TaskDisposedEvent event) {
    }

}



