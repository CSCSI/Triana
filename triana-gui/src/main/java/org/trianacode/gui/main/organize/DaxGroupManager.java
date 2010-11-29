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
        Task t = duo.getTask();
        String levelValue = String.valueOf(duo.getLevel().getLevelNumber() * 2);
        String rowValue = String.valueOf(duo.getRow() * 2);

        System.out.println("Setting value to level: " + levelValue + " row: " + rowValue);

        t.setParameter(Task.GUI_X, levelValue);
        t.setParameter(Task.GUI_Y, rowValue);
    }



    private void addSpecialTask(DaxUnitObject t){
        specialTasks.add(t);
    }

//
//    public int getFreeRow(int level) {
//        int freeRow = 0;
//        for(DaxUnitObject duo : objects){
//            if(duo.getLevel().getLevelNumber() == level){
//                int thisRow = duo.getRow();
//                if(thisRow >= freeRow){
//                    freeRow = thisRow;
//                }
//            }
//
//        }
//        System.out.println("Last used row in level : " + level + " is : " + freeRow);
//        if(freeRow + 1 > rowMax){
//            rowMax = freeRow + 1;
//        }
//        return (freeRow +1);
//    }

    public void placeSpecialTasks() {
        System.out.println("\n\nRearranging special tasks");
        for(DaxUnitObject duo : specialTasks){
            Task [] nextTasks = DaxOrganize.getNextTasks(duo.getTask());
            Task aNextTask = nextTasks[0];
            DaxUnitObject nextduo = getDUOforTask(aNextTask);
            int nextduoLevel = nextduo.getLevel().getLevelNumber();

            if(nextduoLevel != duo.getLevel().getLevelNumber()){
                duo.setLevel(daxGrid.getLevel(nextduoLevel - 1));
                duo.setRow(duo.getLevel().getFreeRow());
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

        for( DaxUnitObject duo : objects){
            int thisLevel = duo.getLevel().getLevelNumber();
            int thisRow = duo.getRow();
        }
    }

    public DaxGrid getGrid() {
        return daxGrid;
    }
}
