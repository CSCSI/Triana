package org.trianacode.shiwa.workflowCreation;

import org.trianacode.annotation.CustomGUIComponent;
import org.trianacode.annotation.Process;
import org.trianacode.annotation.TextFieldParameter;
import org.trianacode.enactment.AddonUtils;
import org.trianacode.taskgraph.*;
import org.trianacode.taskgraph.proxy.ProxyInstantiationException;
import org.trianacode.taskgraph.tool.Tool;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
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
public class CreateChainWorkflows {

    @TextFieldParameter
    public String numberOfWorkflows = "";

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


        int start = 0;
        int end = 30;
//        List<List<String>> sublists = new ArrayList<List<String>>();

        for (int i = 0; i < (allStrings.size() / 30); i++) {

            List<String> listItem = allStrings.subList(start, end);
//            sublists.add(listItem);

            start += 30;
            end += 30;
            if (end > (allStrings.size() - 1)) {
                end = (allStrings.size() - 1);
            }

            TaskGraph taskGraph = makeTaskGraph(start, end, listItem);

            if (taskGraph != null) {
                System.out.println("Adding taskgraph " + taskGraph.getToolName());
                allTaskGraphs.add(taskGraph);
            }
        }
//
//        for(List<String> strings : sublists){
//            TaskGraph taskGraph = makeTaskGraph(strings);
//
//            if(taskGraph != null){
//                allTaskGraphs.add(taskGraph);
//            }
//        }

        System.out.println(allTaskGraphs.size() + " taskgraphs created");
        return allTaskGraphs;
    }

    private TaskGraph makeTaskGraph(int start, int end, List<String> strings) {
        try {

            TaskGraph taskGraph = TaskGraphManager.createTaskGraph();
            taskGraph.setToolName(start + "-" + end);

            int pos = 0;

            Node output1 = null;
            Node output2 = null;
            Node output3 = null;
            Node output4 = null;

            while (pos < strings.size()) {
                String string1 = strings.get(pos);

                Tool tool1 = AddonUtils.makeTool("ExecuteString", "common.processing", "exec" + pos, taskGraph.getProperties());
                Task task1 = taskGraph.createTask(tool1);
                task1.setParameter("executable", string1);
                if (output1 != null) {
                    Node input1 = task1.addDataInputNode();
                    taskGraph.connect(output1, input1);
                }
                output1 = task1.addDataOutputNode();


                if (pos + 1 < strings.size()) {
                    String string2 = strings.get(pos + 1);
                    Tool tool2 = AddonUtils.makeTool("ExecuteString", "common.processing", "exec" + (pos + 1), taskGraph.getProperties());
                    Task task2 = taskGraph.createTask(tool2);
                    task2.setParameter("executable", string2);
                    if (output2 != null) {
                        Node input2 = task2.addDataInputNode();
                        taskGraph.connect(output2, input2);
                    }
                    output2 = task2.addDataOutputNode();
                }

                if (pos + 2 < strings.size()) {
                    String string3 = strings.get(pos + 2);
                    Tool tool3 = AddonUtils.makeTool("ExecuteString", "common.processing", "exec" + (pos + 2), taskGraph.getProperties());
                    Task task3 = taskGraph.createTask(tool3);
                    task3.setParameter("executable", string3);
                    if (output3 != null) {
                        Node input3 = task3.addDataInputNode();
                        taskGraph.connect(output3, input3);
                    }
                    output3 = task3.addDataOutputNode();
                }


                if (pos + 3 < strings.size()) {
                    String string4 = strings.get(pos + 3);
                    Tool tool4 = AddonUtils.makeTool("ExecuteString", "common.processing", "exec" + (pos + 3), taskGraph.getProperties());
                    Task task4 = taskGraph.createTask(tool4);
                    task4.setParameter("executable", string4);
                    if (output4 != null) {
                        Node input4 = task4.addDataInputNode();
                        taskGraph.connect(output4, input4);
                    }
                    output4 = task4.addDataOutputNode();
                }

                pos = pos + 4;
            }

//            allTaskGraphs.add(taskGraph);

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


    @CustomGUIComponent
    public Component getGUI() {
        JPanel mainPane = new JPanel();
        mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
        return mainPane;
    }
}
