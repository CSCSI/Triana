package org.trianacode.gui.util.organize;

import org.trianacode.gui.main.TaskComponent;
import org.trianacode.gui.main.TaskGraphPanel;
import org.trianacode.taskgraph.Node;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.TaskLayoutUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Nov 20, 2010
 */
public class OrganizableTaskGraph {

    private TaskGraphPanel panel;
    private TaskGraph taskgraph;
    private List<OrganizableTask> tasks = new ArrayList<OrganizableTask>();
    private List<Edge> edges = new ArrayList<Edge>();

    public OrganizableTaskGraph(TaskGraphPanel panel) {
        this.panel = panel;
        this.taskgraph = panel.getTaskGraph();
        TaskComponent[] comps = panel.getTaskComponents();
        for (TaskComponent comp : comps) {
            Task t = comp.getTaskInterface();
            OrganizableTask ot = new OrganizableTask(comp.getComponent(), t);
            tasks.add(ot);
        }
        for (OrganizableTask task : tasks) {
            Node[] nodes = task.getTask().getInputNodes();
            for (Node node : nodes) {
                if (node.isConnected()) {
                    Task tsk = node.getCable().getSendingTask();
                    Edge e = new Edge(getTask(tsk), task);
                    if (!edges.contains(e)) {
                        edges.add(e);
                    }
                }
            }
            nodes = task.getTask().getOutputNodes();
            for (Node node : nodes) {
                if (node.isConnected()) {
                    Task tsk = node.getCable().getReceivingTask();
                    Edge e = new Edge(task, getTask(tsk));
                    if (!edges.contains(e)) {
                        edges.add(e);
                    }
                }
            }
        }
    }

    public TaskGraphPanel getPanel() {
        return panel;
    }

    public Dimension getSize() {
        return panel.getContainer().getSize();
    }

    public void repaint() {
        for (OrganizableTask task : tasks) {
            task.updatePoint(panel.getLayoutDetails());
        }
        Task[] ts = new Task[tasks.size()];
        for (int i = 0; i < tasks.size(); i++) {
            OrganizableTask task = tasks.get(i);
            ts[i] = task.getTask();
        }
        TaskLayoutUtils.translateToOrigin(ts);
        panel.getContainer().invalidate();
        panel.getContainer().validate();
        panel.getContainer().repaint();
    }

    public void setPanel(TaskGraphPanel panel) {
        this.panel = panel;
    }

    public TaskGraph getTaskgraph() {
        return taskgraph;
    }

    public void setTaskgraph(TaskGraph taskgraph) {
        this.taskgraph = taskgraph;
    }

    public List<OrganizableTask> getTasks() {
        return tasks;
    }

    public void setTasks(List<OrganizableTask> tasks) {
        this.tasks = tasks;
    }

    public OrganizableTask getTask(Task t) {
        for (OrganizableTask task : tasks) {
            if (task.getTask() == t) {
                return task;
            }
        }
        return null;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public java.util.List<OrganizableTask> getSuccessors(OrganizableTask t) {
        java.util.List<OrganizableTask> ret = new ArrayList<OrganizableTask>();
        Node[] nodes = t.getTask().getOutputNodes();
        for (Node node : nodes) {
            if (node.isConnected()) {
                Task tsk = node.getCable().getReceivingTask();
                OrganizableTask ot = getTask(tsk);
                if (ot != null) {
                    ret.add(ot);
                }
            }
        }
        return ret;
    }

    public java.util.List<OrganizableTask> getPredecessors(OrganizableTask t) {
        java.util.List<OrganizableTask> ret = new ArrayList<OrganizableTask>();
        Node[] nodes = t.getTask().getInputNodes();
        for (Node node : nodes) {
            if (node.isConnected()) {
                Task tsk = node.getCable().getSendingTask();
                OrganizableTask ot = getTask(tsk);
                if (ot != null) {
                    ret.add(ot);
                }
            }
        }
        return ret;
    }

    public static class Edge {
        private OrganizableTask source;
        private OrganizableTask dest;

        public Edge(OrganizableTask source, OrganizableTask dest) {
            this.source = source;
            this.dest = dest;
        }

        public OrganizableTask getSource() {
            return source;
        }

        public OrganizableTask getDest() {
            return dest;
        }
    }
}
