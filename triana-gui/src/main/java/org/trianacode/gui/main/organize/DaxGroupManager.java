package org.trianacode.gui.main.organize;

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
    Vector<DaxGroupObject> groups = new Vector();
    Vector<DaxUnitObject> objects = new Vector();
    ArrayList<DaxUnitObject> specialTasks = new ArrayList();

    DaxGrid daxGrid = new DaxGrid();

    int rowMax = 0;

    public DaxUnitObject addTask(Task t, int level){

        for(DaxUnitObject d : objects){
            if(d.getTask() == t){
                return d;
            }
        }

        DaxUnitObject duo = new DaxUnitObject(t);

        DaxLevel daxLevel = daxGrid.getLevel(level);
        if(daxLevel != null){
            daxLevel.addUnit(duo);
        }
        else{
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

    private int numberOfGroupedUnits(){
        return groups.size();
    }

    public DaxUnitObject getDUOforTask(Task t){
        for(DaxUnitObject d : objects){
            if(d.getTask() == t){
                return d;
            }
        }
        return null;
    }

    public void setParams(DaxUnitObject duo){
        duo.setParams();
    }

    private void addSpecialTask(DaxUnitObject t){
        specialTasks.add(t);
    }

    public void placeSpecialTasks() {
        System.out.println("\n\nRearranging special tasks");
        for(DaxUnitObject duo : specialTasks){
            Task [] nextTasks = DaxOrganize.getNextTasks(duo.getTask());
            Task aNextTask = nextTasks[0];
            DaxUnitObject nextduo = getDUOforTask(aNextTask);
            int nextduoLevel = nextduo.getLevel().getLevelNumber();

            System.out.println("Task after root task " + duo.getTask() + " is level " + nextduoLevel);

            if(nextduoLevel != duo.getLevel().getLevelNumber()){
                duo.leaveLevel();
                daxGrid.getLevel(nextduoLevel - 1).addUnit(duo);

//                duo.setLevel(daxGrid.getLevel(nextduoLevel - 1));
//                duo.setRow(duo.getLevel().getFreeRow());
                setParams(duo);

            }

//            System.out.println("Special task " + duo.getTask().getToolName() + " has " + aNextTask.getToolName() +
//                    ", level " + nextduo.getLevel() + " after it. " +
//                    "Setting tasks level to " + duo.getLevel() + " on available row " + duo.getRow());
        }
    }

    public ArrayList getSpecialTasks() {
        return specialTasks;
    }

    public void setRoots(Task[] roots) {
        for(Task t : roots){
            DaxUnitObject duo = getDUOforTask(t);
            if(duo != null){
                addSpecialTask(duo);
            }
        }
    }

    public void tidyUp(){
        System.out.println("Grid has " + rowMax + " rows, " +
                + daxGrid.numberOfLevels() +" levels (columns)");

        for( int i = 0; i < daxGrid.numberOfLevels() ; i++){
            DaxLevel l = daxGrid.getLevel(i);
                l.tidyupRows();
            
            System.out.println("Level " + i + " contains " + l.levelSize() + " tasks.");
        }

    }

    public DaxGrid getGrid() {
        return daxGrid;
    }
}
