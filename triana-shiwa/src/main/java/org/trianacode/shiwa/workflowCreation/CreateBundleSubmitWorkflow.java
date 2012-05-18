package org.trianacode.shiwa.workflowCreation;

import org.trianacode.annotation.CheckboxParameter;
import org.trianacode.annotation.TextFieldParameter;
import org.trianacode.annotation.Tool;
import org.trianacode.enactment.AddonUtils;
import org.trianacode.gui.hci.ApplicationFrame;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskException;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.TaskGraphManager;
import org.trianacode.taskgraph.annotation.TaskConscious;

import java.io.File;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 10/05/2012
 * Time: 17:40
 * To change this template use File | Settings | File Templates.
 */
@Tool
public class CreateBundleSubmitWorkflow implements TaskConscious {

    @TextFieldParameter
    public String address = "http://s-vmc.cs.cf.ac.uk:7025/Broker/broker";

    @TextFieldParameter
    private String routingKey = "*.triana";

    private Task task;

    @CheckboxParameter
    public boolean local = false;


    @org.trianacode.annotation.Process()
    public void process(List list) {

        try {
            TaskGraph taskGraph = TaskGraphManager.createTaskGraph();
            taskGraph.setToolName("submittingWorkflow");


            for (Object object : list) {
                if (object instanceof File) {
                    try {

                        if (!local) {
                            org.trianacode.taskgraph.tool.Tool tool1 =
                                    AddonUtils.makeTool("BundleToTrianaCloud",
                                            "org.trianacode.shiwa.bundle",
                                            ((File) object).getName(), taskGraph.getProperties());
                            Task task1 = taskGraph.createTask(tool1);
                            task1.setParameter("bundleFile", ((File) object).getAbsolutePath());

                        } else {
                            org.trianacode.taskgraph.tool.Tool tool1 =
                                    AddonUtils.makeTool("BundleToLocalTriana",
                                            "org.trianacode.shiwa.bundle",
                                            ((File) object).getName(), taskGraph.getProperties());
                            Task task1 = taskGraph.createTask(tool1);


                            String exec = "./triana.sh -n -p unbundle " + ((File) object).getAbsolutePath() + " " +
                                    File.createTempFile("output", "tmp").getAbsolutePath();

                            task1.setParameter("executable", exec);
                            task1.setParameter("runtimeDirectory", "triana-app/dist/");
                        }

                    } catch (Exception e) {

                    }
                }
            }

            taskGraph = recycleGraph(taskGraph);

            System.out.println("Taskgraph has " + taskGraph.getTasks(false).length + " tasks.");

            ApplicationFrame frame = GUIEnv.getApplicationFrame();
            frame.addParentTaskGraphPanel(taskGraph);

        } catch (TaskException e) {
            e.printStackTrace();
        }
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
//        task.setParameter(StampedeLog.STAMPEDE_TASK_TYPE, StampedeLog.JobType.dax.desc);
    }
}