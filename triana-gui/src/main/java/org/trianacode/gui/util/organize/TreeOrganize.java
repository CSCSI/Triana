package org.trianacode.gui.util.organize;

import org.trianacode.gui.main.TaskGraphPanel;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.TaskGraphUtils;
import org.trianacode.taskgraph.interceptor.execution.PoisonTask;

import java.util.*;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Nov 22, 2010
 */
public class TreeOrganize {

    private TaskGraphPanel panel;
    private TaskGraph taskgraph;
    private Map<Integer, List<Task>> levels = new HashMap<Integer, List<Task>>();
    private List<Task> done = new ArrayList<Task>();
    private int longestList = 0;


    public TreeOrganize(TaskGraphPanel panel) {
        this.panel = panel;
        this.taskgraph = panel.getTaskGraph();
    }

    public void organize() {
        parse();
        for (Integer integer : levels.keySet()) {
            List<Task> tasks = levels.get(integer);
            setPoints(tasks, integer);
        }
        //TaskLayoutUtils.translateToOrigin(taskgraph.getTasks(false), 1);
        panel.getContainer().invalidate();
        panel.getContainer().validate();
        panel.getContainer().repaint();
    }

    private void setPoints(List<Task> tasks, int level) {
        int curr = 0;
        if (tasks.size() < longestList) {
            curr = (int) (longestList - tasks.size()) / 2;
        }
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (!(task instanceof PoisonTask)) {
                double x = 0;
                double y = 0;
                int ins = task.getInputNodeCount();
                if (ins > 3) {
                    x = ins * 0.2;
                    y = x;
                }
                System.out.println("TreeOrganize.setPoints setting xy:" + (level + 1 + x) + " " + (i + curr));
                task.setParameter(Task.GUI_X, (level + x) + "");
                task.setParameter(Task.GUI_Y, (i + curr + y) + "");
            }
        }
    }

    private void parse() {
        List<Task> roots = TaskGraphUtils.getRootTasks(taskgraph);
        for (int i = 0; i < roots.size(); i++) {
            Task task = roots.get(i);
            setLevel(task, 0, i);
        }
    }

    private void setLevel(Task task, int level, int y) {
        if (done.contains(task)) {
            return;
        }
        List<Task> tasks = levels.get(level);
        if (tasks == null) {
            tasks = new ArrayList<Task>();
            tasks.add(new PoisonTask());
        }
        for (int i = 0; i < y; i++) {
            if (tasks.size() - 1 < i) {
                tasks.add(new PoisonTask());
            }
        }
        tasks.add(y, task);
        Collection<List<Task>> all = levels.values();
        for (List<Task> taskList : all) {
            if (taskList.size() > longestList) {
                longestList = taskList.size();
            }
        }
        levels.put(level, tasks);
        done.add(task);
        List<Task> children = TaskGraphUtils.getSuccessors(task);
        for (Task child : children) {
            setLevel(child, ++level, tasks.size() - 1);
        }
    }
}
