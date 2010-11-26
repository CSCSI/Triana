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
    Vector<DaxGroupObject> objects = new Vector();
    ArrayList<DaxGroupObject> specialTasks = new ArrayList();

    public DaxGroupObject addTask(Task t, int level){

        for(DaxGroupObject d : objects){
            if(d.getTask() == t){
                return d;
            }
        }

        DaxGroupObject dgo = initDaxGroupObject(t, level);
                if(t.getDataOutputNodeCount() > 5){
            addSpecialTask(dgo);
        }

        objects.add(dgo);
        return dgo;
    }

    public DaxGroupObject getDGOforTask(Task t){
        for(DaxGroupObject d : objects){
            if(d.getTask() == t){
                return d;
            }
        }
        return null;
    }

    private DaxGroupObject initDaxGroupObject(Task t, int level){
        DaxGroupObject dgo = new DaxGroupObject();
        dgo.setTask(t);
        dgo.setLevel(level);
        dgo.setRow(getFreeRow(level));
        return dgo;
    }

    private void addSpecialTask(DaxGroupObject t){
        specialTasks.add(t);
    }

    public int getFreeRow(int level) {
        int freeRow = 0;
        for(DaxGroupObject dgo : objects){
            if(dgo.getLevel() == level){
                int thisRow = dgo.getRow();
                if(thisRow >= freeRow){
                    freeRow = thisRow;
                }
            }

        }
        System.out.println("Last used row in level : " + level + " is : " + freeRow);
        return (freeRow +1);
    }

    public void placeSpecialTasks() {
        for(DaxGroupObject dgo : specialTasks){
            Task [] nextTasks = DaxOrganize.getNextTasks(dgo.getTask());
            DaxGroupObject nextDGO = getDGOforTask(nextTasks[1]);
            dgo.setLevel(nextDGO.getLevel() - 1);
            dgo.setRow(getFreeRow(dgo.getLevel()));

        }
    }

    public ArrayList getSpecialTasks() {
        return specialTasks;
    }
}
