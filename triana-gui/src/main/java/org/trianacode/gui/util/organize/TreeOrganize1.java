package org.trianacode.gui.util.organize;

import org.trianacode.gui.main.TaskGraphPanel;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.TaskGraphUtils;

import java.util.*;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Nov 23, 2010
 */
public class TreeOrganize1 {


    private TaskGraphPanel panel;
    private TaskGraph taskgraph;
    private Map<Integer, GridColumn> levels = new HashMap<Integer, GridColumn>();
    private List<Task> done = new ArrayList<Task>();
    private int longestList = 0;


    public TreeOrganize1(TaskGraphPanel panel) {
        this.panel = panel;
        this.taskgraph = panel.getTaskGraph();
    }

    public void organize() {
        parse();
        Set<Integer> ints = levels.keySet();
        Integer[] sorted = ints.toArray(new Integer[ints.size()]);
        Arrays.sort(sorted);
        for (Integer integer : ints) {
            GridColumn row = levels.get(integer);
            setPoints(row);
        }
        //TaskLayoutUtils.translateToOrigin(taskgraph.getTasks(false), 1);
        panel.getContainer().invalidate();
        panel.getContainer().validate();
        panel.getContainer().repaint();
    }

    public double getHeight(int level) {
        GridColumn col = levels.get(level);
        return col.getMaxHeight();

    }

    private void setPoints(GridColumn column) {
        int curr = 0;
        int len = column.getLength();

        for (int i = 0; i < len; i++) {
            Gridbox box = column.getBox(i);
            Task task = box.getTask();
            if (task != null) {
                double x = 0;
                double y = 0;
                if (i > 0) {
                    GridColumn prev = levels.get(column.getX());
                    if (prev != null) {
                        x += prev.getColumnWidth();
                        y += prev.getMaxHeight();
                    }
                }
                System.out.println("TreeOrganize1.setPoints task:" + task.getToolName());
                System.out.println("TreeOrganize1.setPoints x:" + box.getX() + x);
                System.out.println("TreeOrganize1.setPoints y:" + box.getY() + y);
                task.setParameter(Task.GUI_X, (box.getX() + x) + "");
                task.setParameter(Task.GUI_Y, (box.getY() + y) + "");
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
        GridColumn row = levels.get(level);
        if (row == null) {
            row = new GridColumn(y);
            levels.put(level, row);
        }
        Collection<GridColumn> all = levels.values();
        for (GridColumn taskList : all) {
            if (taskList.getLength() > longestList) {
                longestList = taskList.getLength();
            }
        }
        row.addBox(level, new Gridbox(level, y, task));
        done.add(task);
        List<Task> children = TaskGraphUtils.getSuccessors(task);
        int currY = y;
        for (Task child : children) {
            currY++;
            setLevel(child, level, currY);

        }
    }


}
