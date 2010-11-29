package org.trianacode.gui.main.organize;

import org.trianacode.taskgraph.Cable;
import org.trianacode.taskgraph.Node;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskGraph;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Nov 25, 2010
 * Time: 8:56:29 AM
 * To change this template use File | Settings | File Templates.
 */
public class DaxOrganize {
    DaxGroupManager dgm = new DaxGroupManager();


    public DaxOrganize(TaskGraph t){
        
        HashMap levels = new HashMap();

        Task[] roots = getRootTasks(t);

        recurse( levels, roots, 0);

        dgm.setRoots(roots);        
        dgm.placeSpecialTasks();

        dgm.tidyUp();
        System.out.println("HashMap : " + levels +
                "\n\nSpecial tasks : " + dgm.getSpecialTasks());

    }

    private  void recurse(HashMap h, Task[] ts, int level){
        System.out.println("\n\nGrid has " + dgm.getGrid().numberOfLevels() + " levels.");
        if(ts.length > 0){
            for(Task task : ts){
                recLevel(h, task, level);
                Task[] nextLevel = getNextTasks(task);
                recurse(h, nextLevel, level+1);
            }
        }
    }

    private void recLevel( HashMap h, Task t, int level){
        System.out.println("Task " + t.getToolName() + " is on level " + level);

        DaxUnitObject duo = dgm.getDUOforTask(t);
        if(duo == null){
            duo = dgm.addTask(t, level);

        }
        else{
            int currentSetLevel = duo.getLevel().getLevelNumber();
            System.out.println("Task " + t.getToolName() + " already has a DaxUnitObject. " +
                    "Previously stored level :" + currentSetLevel + ", this route gives level : " + level);
            if(level > currentSetLevel){
//                duo.setLevel(dgm.getGrid().getLevel(level));
//                System.out.println("Changed level to " + duo.getLevel().getLevelNumber());
//                int newRow = duo.getLevel().getFreeRow();
//                duo.setRow(newRow);

                duo.leaveLevel();
                dgm.getGrid().getLevel(level).addUnit(duo);

                dgm.setParams(duo);
            }
            else{
                System.out.println("Left task " + t.getToolName() + " on level " + duo.getLevel().getLevelNumber());
            }
        }
        h.put(t,level);
    }




    public static Task[] getNextTasks(Task t){
        ArrayList nextTasks = new ArrayList();
        Node[] outputNodes = t.getDataOutputNodes();
        for( Node node : outputNodes){
            if(node.isConnected()){
                Cable c = node.getCable();
                if (c != null){
                    Node cableOutputNode = c.getReceivingNode();
                    if( cableOutputNode != null){
                        Task attachedTask = cableOutputNode.getTask();
                        nextTasks.add(attachedTask);
                    }
                }

            }
        }

        return (Task[])nextTasks.toArray(new Task[nextTasks.size()]);
    }

    private Task[] getRootTasks(TaskGraph t){
        Task[] allTasks = t.getTasks(false);
        ArrayList rootTasks = new ArrayList();

        for(Task task : allTasks){
            int numberInputNodes = task.getDataInputNodeCount();
            if (numberInputNodes == 0){
                rootTasks.add(task);
            }
        }

        System.out.println("Root tasks : " + rootTasks);

        return (Task[])rootTasks.toArray(new Task[rootTasks.size()]);
    }

}
