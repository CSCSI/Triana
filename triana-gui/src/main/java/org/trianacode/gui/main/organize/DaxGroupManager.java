package org.trianacode.gui.main.organize;

import org.apache.commons.logging.Log;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.taskgraph.Task;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Nov 25, 2010
 * Time: 9:15:02 AM
 * To change this template use File | Settings | File Templates.
 */
public class DaxGroupManager {
    Vector<DaxGroupObject> groups = new Vector<DaxGroupObject>();
    Vector<DaxUnitObject> objects = new Vector<DaxUnitObject>();
    ArrayList<DaxUnitObject> specialTasks = new ArrayList<DaxUnitObject>();

    DaxGrid daxGrid = new DaxGrid();

    public DaxUnitObject addTask(Task t, int level) {

        for (DaxUnitObject d : objects) {
            if (d.getTask() == t) {
                return d;
            }
        }

        DaxUnitObject duo = new DaxUnitObject(t);

        DaxLevel daxLevel = daxGrid.getLevel(level);
        if (daxLevel != null) {
            daxLevel.addUnit(duo);
        } else {
            daxLevel = new DaxLevel();
            daxLevel.addUnit(duo);
            daxGrid.addLevel(daxLevel);
        }

        duo.setLevel(daxLevel);
        duo.setRow(daxLevel.getFreeRow());
        setParams(duo);

        objects.add(duo);
        return duo;
    }

    private int numberOfGroupedUnits() {
        return groups.size();
    }

    public DaxUnitObject getDUOforTask(Task t) {
        for (DaxUnitObject d : objects) {
            if (d.getTask() == t) {
                return d;
            }
        }
        return null;
    }

    public void setParams(DaxUnitObject duo) {
        duo.setParams();
    }

    private void addSpecialTask(DaxUnitObject t) {
        specialTasks.add(t);
    }

    public void placeSpecialTasks() {
        log("\n\nRearranging special tasks");
        for (DaxUnitObject duo : specialTasks) {
            Task[] nextTasks = DaxOrganize.getNextTasks(duo.getTask());
            if (nextTasks.length > 0) {
                Task aNextTask = nextTasks[0];
                DaxUnitObject nextduo = getDUOforTask(aNextTask);
                int nextduoLevel = nextduo.getLevel().getLevelNumber();

                log("Task after root task " + duo.getTask() + " is level " + nextduoLevel);

                if (nextduoLevel != duo.getLevel().getLevelNumber()) {
                    duo.leaveLevel();
                    daxGrid.getLevel(nextduoLevel - 1).addUnit(duo);
                    setParams(duo);

                }
            }
        }
    }

    public ArrayList getSpecialTasks() {
        return specialTasks;
    }

    public void setRoots(Task[] roots) {
        for (Task t : roots) {
            DaxUnitObject duo = getDUOforTask(t);
            if (duo != null) {
                addSpecialTask(duo);
            }
        }

        for (Task t : roots) {
            DaxUnitObject duo = getDUOforTask(t);
            if (duo != null) {
                if (duo.getTask().getOutputNodeCount() > 5) {
                    addSpecialTask(duo);
                }
            }
        }
    }

    public void tidyUp() {
        log("Grid has " + daxGrid.numberOfRows() + " rows, " +
                +daxGrid.numberOfLevels() + " levels (columns)");

        for (int i = 0; i < daxGrid.numberOfLevels(); i++) {
            DaxLevel l = daxGrid.getLevel(i);
            l.tidyupRows();

            log("Level " + i + " contains " + l.levelSize() + " tasks.");
        }

    }

    public DaxGrid getGrid() {
        return daxGrid;
    }

    private void log(String text) {
        Log log = Loggers.DEV_LOGGER;
        log.debug(text);
        //     System.out.println(text);
    }
}
