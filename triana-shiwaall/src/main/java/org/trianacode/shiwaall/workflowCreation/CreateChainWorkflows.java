package org.trianacode.shiwaall.workflowCreation;

import org.trianacode.annotation.CheckboxParameter;
import org.trianacode.annotation.Process;
import org.trianacode.annotation.TextFieldParameter;
import org.trianacode.enactment.AddonUtils;
import org.trianacode.taskgraph.*;
import org.trianacode.taskgraph.annotation.TaskConscious;
import org.trianacode.taskgraph.proxy.ProxyInstantiationException;
import org.trianacode.taskgraph.tool.Tool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 12/04/2012
 * Time: 18:12
 * To change this template use File | Settings | File Templates.
 */
@org.trianacode.annotation.Tool
public class CreateChainWorkflows implements TaskConscious {

    @TextFieldParameter
    public String tasksPerWorkflowString = "30";

    @CheckboxParameter
    public boolean zip = false;
    private Task task;

    @Process(gather = true)
    public ArrayList<TaskGraph> process(List list) {

        ArrayList<TaskGraph> allTaskGraphs = new ArrayList<TaskGraph>();

        ArrayList<String> allStrings = new ArrayList<String>();
        if (list.size() > 0) {
            for (Object object : list) {
                if (object instanceof String[]) {
                    String[] array = ((String[]) list.get(0));
                    Collections.addAll(allStrings, array);
                }
            }
        }

        int tasksPerWorkflow = Integer.parseInt(tasksPerWorkflowString);

        int start = 0;
        int end = tasksPerWorkflow;

        double tomake = Math.ceil((double) allStrings.size() / (double) tasksPerWorkflow);

        if (tomake < 1) {
            tomake = 1;
        }

        for (int i = 0; i < tomake; i++) {
            if (end > allStrings.size()) {
                end = allStrings.size();
            }

            System.out.println("Start " + start + " end " + end);
            List<String> listItem = allStrings.subList(start, end);
            TaskGraph taskGraph = makeTaskGraph(start, end, listItem);

            if (zip) {
                try {
                    addZipper(taskGraph);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            taskGraph = recycleGraph(taskGraph);
            System.out.println("Taskgraph outputs " + taskGraph.getOutputNodeCount() + " " +
                    Arrays.toString(taskGraph.getDataOutputTypes()));

            if (taskGraph != null) {
                System.out.println("Adding taskgraph " + taskGraph.getToolName());
                allTaskGraphs.add(taskGraph);
            }

            start += tasksPerWorkflow;
            end += tasksPerWorkflow;
//            if (end > (allStrings.size() - 1)) {
//                end = (allStrings.size() - 1);
//            }

        }

        System.out.println(allTaskGraphs.size() + " taskgraphs created");
        return allTaskGraphs;
    }

    private void addZipper(TaskGraph taskGraph) throws ProxyInstantiationException, TaskGraphException {
        ArrayList<Task> endTasks = new ArrayList<Task>();
        for (Task task : taskGraph.getTasks(false)) {
            if (task.getOutputNodeCount() == 0) {
                endTasks.add(task);
            }
        }

        Tool zipTool = AddonUtils.makeTool("ZipFiles", "common.file", "zipper", taskGraph.getProperties());

        Task zipTask = taskGraph.createTask(zipTool);
        zipTask.setParameter("files", "./results");

        for (Task task : endTasks) {
            taskGraph.connect(task.addDataOutputNode(), zipTask.addDataInputNode());
        }

        taskGraph.addDataOutputNode(zipTask.addDataOutputNode());

//        Node node = zipTask.addDataOutputNode();
//        TaskLayoutUtils.resolveGroupNodes(taskGraph);

    }

    private TaskGraph makeTaskGraph(int start, int end, List<String> strings) {
        try {

            TaskGraph taskGraph = TaskGraphManager.createTaskGraph();
            taskGraph.setToolName(start + "-" + (end - 1));

            int pos = 0;

            Task prev1 = null;
            Task prev2 = null;
            Task prev3 = null;
            Task prev4 = null;

            while (pos < strings.size()) {
                String string1 = strings.get(pos);

                Tool tool1 = AddonUtils.makeTool("ExecuteString", "common.processing", "exec" + pos, taskGraph.getProperties());
                Task task1 = taskGraph.createTask(tool1);
                task1.setParameter("executable", string1);
                if (prev1 != null) {
                    Node input1 = task1.addDataInputNode();
                    taskGraph.connect(prev1.addDataOutputNode(), input1);
                }
                prev1 = task1;


                if (pos + 1 < strings.size()) {
                    String string2 = strings.get(pos + 1);
                    Tool tool2 = AddonUtils.makeTool("ExecuteString", "common.processing", "exec" + (pos + 1), taskGraph.getProperties());
                    Task task2 = taskGraph.createTask(tool2);
                    task2.setParameter("executable", string2);
                    if (prev2 != null) {
                        Node input2 = task2.addDataInputNode();
                        taskGraph.connect(prev2.addDataOutputNode(), input2);
                    }
                    prev2 = task2;
                }

                if (pos + 2 < strings.size()) {
                    String string3 = strings.get(pos + 2);
                    Tool tool3 = AddonUtils.makeTool("ExecuteString", "common.processing", "exec" + (pos + 2), taskGraph.getProperties());
                    Task task3 = taskGraph.createTask(tool3);
                    task3.setParameter("executable", string3);
                    if (prev3 != null) {
                        Node input3 = task3.addDataInputNode();
                        taskGraph.connect(prev3.addDataOutputNode(), input3);
                    }
                    prev3 = task3;
                }


                if (pos + 3 < strings.size()) {
                    String string4 = strings.get(pos + 3);
                    Tool tool4 = AddonUtils.makeTool("ExecuteString", "common.processing", "exec" + (pos + 3), taskGraph.getProperties());
                    Task task4 = taskGraph.createTask(tool4);
                    task4.setParameter("executable", string4);
                    if (prev4 != null) {
                        Node input4 = task4.addDataInputNode();
                        taskGraph.connect(prev4.addDataOutputNode(), input4);
                    }
                    prev4 = task4;
                }

                pos = pos + 4;
            }

            return taskGraph;
        } catch (TaskException e) {
            e.printStackTrace();
        } catch (ProxyInstantiationException e) {
            e.printStackTrace();
        } catch (CableException e) {
            e.printStackTrace();
        }
        return null;
    }


    public TaskGraph recycleGraph(TaskGraph taskGraph) {
        try {
            return (TaskGraph) TaskGraphManager.createTask(taskGraph, TaskGraphManager.DEFAULT_FACTORY_TYPE, false);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void setTask(Task task) {
        this.task = task;
    }
}
