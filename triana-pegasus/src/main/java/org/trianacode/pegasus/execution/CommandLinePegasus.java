package org.trianacode.pegasus.execution;

import org.apache.commons.logging.Log;
import org.trianacode.TrianaInstance;
import org.trianacode.enactment.Exec;
import org.trianacode.enactment.ExecutionService;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.taskgraph.CableException;
import org.trianacode.taskgraph.Node;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.imp.ToolImp;
import org.trianacode.taskgraph.proxy.java.JavaProxy;
import org.trianacode.taskgraph.ser.XMLWriter;
import org.trianacode.taskgraph.tool.Tool;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 17/08/2011
 * Time: 18:14
 * To change this template use File | Settings | File Templates.
 */
public class CommandLinePegasus implements ExecutionService {

    private static String pegasusPackage = "org.trianacode.pegasus.dax";
    private static Log devLog = Loggers.DEV_LOGGER;

    @Override
    public String getServiceName() {
        return "CommandLinePegasus";
    }

    @Override
    public String getLongOption() {
        return "submit-to-pegasus";
    }

    @Override
    public String getShortOption() {
        return "dax";
    }

    @Override
    public String getDescription() {
        return "Takes a workflow, creates a dax, and submits to Pegasus";
    }

    @Override
    public void execute(Exec executeEngine, TrianaInstance engine, String workflow, Object tool, Object data, String[] args) throws Exception {
        devLog.debug("\nWill attempt to create and submit dax\n");

        if (tool instanceof TaskGraph) {
            initTaskgraph((TaskGraph) tool);
            executeEngine.execute((TaskGraph) tool, (String) data);
        } else {
            devLog.debug("Input file not a valid workflow");
            System.exit(1);
        }
    }

    public static Tool initTaskgraph(TaskGraph taskGraph) {
        devLog.debug("\nBegin init taskgraph for dax create/ submit.");

        Task creatorTask = null;
        Task submitTask = null;
//        if(creatorClass != null && submitClass != null){
        try {

            Node childNode = getTaskgraphChildNode(taskGraph);
            if (childNode != null && childNode.getTask() != submitTask) {
                taskGraph.connect(childNode, creatorTask.getDataInputNode(0));
            } else {
                devLog.debug("No child node available to attach daxCreator to.");
            }

            ToolImp creatorTool = new ToolImp(taskGraph.getProperties());
            //            initTool(creatorTool, creatorClass.getCanonicalName(), creatorClass.getPackage().getName(), 0, 1);
            initTool(creatorTool, "DaxCreatorV3", pegasusPackage, 1, 1);
            creatorTask = taskGraph.createTask(creatorTool);

            ToolImp submitTool = new ToolImp(taskGraph.getProperties());
            //            initTool(submitTool, submitClass.getCanonicalName(), submitClass.getPackage().getName(), 1, 0);
            initTool(submitTool, "DaxToPegasusUnit", pegasusPackage, 1, 0);
            submitTool.setParameter("locationService", "URL");

            submitTask = taskGraph.createTask(submitTool);
            submitTask.addParameterInputNode("manualURL").setTriggerNode(false);


//      If daxCreator and daxSubmit tasks were able to be instatiated, connect them together.
            if (creatorTask != null && submitTask != null) {
                try {

                    taskGraph.connect(creatorTask.getDataOutputNode(0), submitTask.getDataInputNode(0));
                    devLog.debug("Connected added tasks");
                } catch (CableException e) {
                    devLog.debug("Failed to connect task cables");
                    e.printStackTrace();
                }
            } else {
                devLog.debug("Tasks were null, not connected.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            BufferedWriter fileWriter = new BufferedWriter(new FileWriter("/Users/ian/pegterm.xml"));
            XMLWriter writer = new XMLWriter(fileWriter);
            writer.writeComponent(taskGraph);
        } catch (Exception e) {
            devLog.debug("Failed to write modified xml file");
            e.printStackTrace();
        }
        devLog.debug("Taskgraph initialised");
        return (Tool) taskGraph;
    }

    private static void initTool(ToolImp tool, String unitName, String unitPackage, int inNodes, int outNodes) {
        tool.setToolName(unitName);
        try {
            tool.setDataInputNodeCount(inNodes);
            tool.setDataOutputNodeCount(outNodes);
            tool.setToolPackage(unitPackage);
            tool.setProxy(new JavaProxy(unitName, unitPackage));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Node getTaskgraphChildNode(TaskGraph taskGraph) {
// Find a child task on the taskgraph to attach the daxCreator to, and connect it
        Node childNode = null;
        try {
            Task[] tasks = taskGraph.getTasks(false);
            ArrayList<Task> childTasks = new ArrayList<Task>();
            for (Task task : tasks) {
                if (task.getDataOutputNodeCount() == 0) {
                    childTasks.add(task);
                }
            }
            devLog.debug("These are the child tasks of the taskgraph (will use the first discovered): ");
            for (Task task : childTasks) {
                devLog.debug(task.getToolName());
            }

            if (childTasks.size() > 0) {
                childNode = childTasks.get(0).addDataOutputNode();
            }
        } catch (Exception e) {
            devLog.debug("Failed to add node to child leaf of taskgraph");
        }

        return childNode;
    }
}
