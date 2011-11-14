package org.trianacode.gui.main.organize;

import org.apache.commons.logging.Log;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.gui.main.imp.ForShowTool;
import org.trianacode.taskgraph.Cable;
import org.trianacode.taskgraph.Node;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskGraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Nov 25, 2010
 * Time: 8:56:29 AM
 * To change this template use File | Settings | File Templates.
 */
public class DaxOrganize {
    DaxGroupManager dgm;

    private static void log(String text) {
        Log log = Loggers.DEV_LOGGER;
        log.debug(text);
//        System.out.println(text);
    }

    public DaxOrganize(final TaskGraph t) {
        dgm = new DaxGroupManager(t);

//        Thread thread = new Thread(){
//            public void run(){
//
        log("Using dax organise");
        HashMap levels = new HashMap<Task, Integer>();

        ArrayList<Task> roots = getRootTasks(t);

        recurse(levels, roots, 0);
        dgm.setRoots(roots);
        dgm.placeSpecialTasks();

        dgm.tidyUp();
        log("HashMap : " + levels +
                "\n\nSpecial tasks : " + dgm.getSpecialTasks());
//    }
//        };
//        thread.start();
    }

    private void recurse(HashMap h, ArrayList<Task> ts, int level) {
        log("\n\nGrid has " + dgm.getGrid().numberOfLevels() + " levels.");
        if (ts.size() > 0) {
            for (Task task : ts) {
                recLevel(h, task, level);
                ArrayList<Task> nextLevel = getNextTasks(task);
//
//                Node[] inputNodes = task.getDataInputNodes();
//                for(Node node : inputNodes){
//                    if(nextLevel.contains(node.getCable().getSendingTask())){
//                        System.out.println("Task : " + task.getToolName());
//                        ts.remove(task);
//                    }
//                }
                //TODO figure out how to stop infinite loops
//                if (level < dgm.getNumberOfTasks() + 1) {
                recurse(h, nextLevel, level + 1);
//                }
            }
        }
    }

    private void recLevel(HashMap h, Task t, int level) {
        log("Task " + t.getToolName() + " is on level " + level);

        DaxUnitObject duo = dgm.getDUOforTask(t);
        if (duo == null) {
            duo = dgm.addTask(t, level);

        } else {
            int currentSetLevel = duo.getLevel().getLevelNumber();
            log("Task " + t.getToolName() + " already has a DaxUnitObject. " +
                    "Previously stored level :" + currentSetLevel + ", this route gives level : " + level);
            if (level > currentSetLevel) {
                duo.leaveLevel();
                dgm.getGrid().getLevel(level).addUnit(duo);
                dgm.setParams(duo);
            } else {
                log("Left task " + t.getToolName() + " on level " + duo.getLevel().getLevelNumber());
            }
        }
        h.put(t, level);
    }

    public static ArrayList<Task> getNextTasks(Task t) {
        ArrayList<Task> nextTasks = new ArrayList<Task>();
        Node[] outputNodes = t.getDataOutputNodes();
        for (Node node : outputNodes) {
            if (node.isConnected()) {
                Cable c = node.getCable();
                if (c != null) {
                    Node cableOutputNode = c.getReceivingNode();
                    if (cableOutputNode != null) {
                        Task attachedTask = cableOutputNode.getTask();
                        nextTasks.add(attachedTask);
                    }
                }
            }
        }
//        return (Task[]) nextTasks.toArray(new Task[nextTasks.size()]);
        return nextTasks;
    }

    private static ArrayList<Task> getRootTasks(TaskGraph taskGraph) {
        Node[] rootNodeArray = taskGraph.getInputNodes();
        log("Input nodes : " + rootNodeArray.length);
        ArrayList<Node> rootNodes = new ArrayList<Node>(Arrays.asList(rootNodeArray));
        log("Input nodes : " + rootNodes.size());

        for (Iterator<Node> iterator = rootNodes.iterator(); iterator.hasNext(); ) {
            Node next = iterator.next();
            if (next.isConnected()) {
                Cable cable = next.getCable();
                Node node = cable.getReceivingNode();
                System.out.println("Incoming node connected to " + node.toString() + " on task " + node.getTask().getToolName());
            } else {
                System.out.println("Node " + next.toString() + " is not connected");
            }
        }

        Task[] allTasks = taskGraph.getTasks(false);
        ArrayList<Task> rootTasks = new ArrayList<Task>();

        for (Task task : allTasks) {
            int numberInputNodes = task.getDataInputNodeCount();
            if (numberInputNodes == 0) {
                rootTasks.add(task);
            }

            log("\n" + task.getToolName());
            for (Node node : task.getInputNodes()) {
                log("An input node");
                if (node.isConnected()) {
                    log("Node is connected");
                    Cable nodeCable = node.getCable();
                    Task sendingTask = nodeCable.getSendingTask();
                    if (sendingTask != null) {
                        log("Task " + task.getToolName() + " receives from " + sendingTask.toString());
                        if (node.isConnected() && sendingTask instanceof ForShowTool) {
                            log("For show tool");
                        }
                    } else {
                        log("Task " + task.getToolName() + " has a node but no sending task");
                    }
                } else {
                    log("Node not connected apparently...");
                    rootTasks.add(task);
                    if (node.isTopLevelNode()) {
                        log("is top level node");
                    }
                }
                if (rootNodes.contains(node)) {
                    System.out.println(node.toString() + " Could be a proper dummynode");
                }
            }
        }

        log("Root tasks : " + rootTasks);
//        return (Task[]) rootTasks.toArray(new Task[rootTasks.size()]);
        return rootTasks;
    }

}
