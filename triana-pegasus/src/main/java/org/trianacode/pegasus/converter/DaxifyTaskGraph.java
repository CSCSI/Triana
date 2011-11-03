package org.trianacode.pegasus.converter;

import org.apache.commons.lang.ArrayUtils;
import org.trianacode.TrianaInstance;
import org.trianacode.config.TrianaProperties;
import org.trianacode.enactment.Exec;
import org.trianacode.enactment.ExecutionService;
import org.trianacode.pegasus.dax.FileUnit;
import org.trianacode.pegasus.dax.JobUnit;
import org.trianacode.taskgraph.*;
import org.trianacode.taskgraph.imp.TaskFactoryImp;
import org.trianacode.taskgraph.imp.TaskImp;
import org.trianacode.taskgraph.imp.ToolImp;
import org.trianacode.taskgraph.proxy.ProxyInstantiationException;
import org.trianacode.taskgraph.proxy.java.JavaProxy;
import org.trianacode.taskgraph.tool.Tool;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 28/10/2011
 * Time: 15:16
 * To change this template use File | Settings | File Templates.
 */
public class DaxifyTaskGraph implements ExecutionService {

    public TaskGraph convert(TaskGraph taskGraph) {
        try {
            return daxifyTaskGraph(taskGraph, TaskFactory.DEFAULT_FACTORY_NAME, false, false, false);
        } catch (TaskGraphException e) {
            e.printStackTrace();
        }
        return null;
    }

    private TaskGraph daxifyTaskGraph(TaskGraph taskgraph, String factorytype, boolean presclone,
                                      boolean prestasks, boolean clonecontrol) throws TaskGraphException {
        try {
            TaskGraph clone = TaskGraphManager.createTaskGraph(taskgraph, factorytype, presclone);
            if (taskgraph.getToolName() != null) {
                clone.setToolName(taskgraph.getToolName());
            }

            Task[] tasks = taskgraph.getTasks(false);

            for (Task task1 : tasks) {
                if (task1 instanceof TaskGraph) {
                    clone.createTask(daxifyTaskGraph((TaskGraph) task1, TaskFactory.DEFAULT_FACTORY_NAME, false, false, false));
                } else {
                    Task daxTask = initDaxTask(task1, JobUnit.class);
                    clone.createTask(daxTask, prestasks);
                }
            }

            System.out.println("Daxified taskgraph has " + clone.getTasks(false).length + " tasks.");

            Cable[] cables = TaskGraphUtils.getInternalCables(tasks);

            System.out.println("Cables " + ArrayUtils.toString(cables));

            Task task;
            Node sendnode;
            Node recnode;

            try {
                for (int count = 0; count < cables.length; count++) {
                    task = clone.getTask(taskgraph.getTask(cables[count].getSendingNode()).getToolName());

                    if (cables[count].getSendingNode().isParameterNode()) {
                        sendnode = task.getParameterOutputNode(cables[count].getSendingNode().getNodeIndex());
                    } else {
                        sendnode = task.getDataOutputNode(cables[count].getSendingNode().getNodeIndex());
                    }

                    task = clone.getTask(taskgraph.getTask(cables[count].getReceivingNode()).getToolName());

                    if (cables[count].getReceivingNode().isParameterNode()) {
                        recnode = task.getParameterInputNode(cables[count].getReceivingNode().getNodeIndex());
                    } else {
                        recnode = task.getDataInputNode(cables[count].getReceivingNode().getNodeIndex());
                    }

                    addFileUnit(sendnode, recnode, clone);

//                    clone.connect(sendnode, recnode);
                }
            } catch (CableException e) {
                e.printStackTrace();
            }

            TaskGraph group = taskgraph;
            Node nodes[] = group.getDataInputNodes();
            Node node;
            Node clonenode;

            try {
                for (int count = 0; count < nodes.length; count++) {
//                    if (taskgraph.isControlTaskConnected()) {
//                        node = TaskGraphUtils.getControlNode(nodes[count]).getCable().getReceivingNode();
//                    } else {
                    node = nodes[count].getParentNode();
//                    }

                    if (node.isParameterNode()) {
                        clonenode = clone.getTask(taskgraph.getTask(node).getToolName())
                                .getParameterInputNode(node.getNodeIndex());
                    } else {
                        clonenode = clone.getTask(taskgraph.getTask(node).getToolName())
                                .getDataInputNode(node.getNodeIndex());
                    }

                    clone.setGroupNodeParent(clone.getDataInputNode(count), clonenode);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            nodes = group.getDataOutputNodes();

            try {
                for (int count = 0; count < nodes.length; count++) {
//                    if (taskgraph.isControlTaskConnected()) {
//                        node = TaskGraphUtils.getControlNode(nodes[count]).getCable().getSendingNode();
//                    } else {
                    node = nodes[count].getParentNode();
//                    }

                    if (node.isParameterNode()) {
                        clonenode = clone.getTask(taskgraph.getTask(node).getToolName())
                                .getParameterOutputNode(node.getNodeIndex());
                    } else {
                        clonenode = clone.getTask(taskgraph.getTask(node).getToolName())
                                .getDataOutputNode(node.getNodeIndex());
                    }

                    clone.setGroupNodeParent(clone.getDataOutputNode(count), clonenode);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (taskgraph.isControlTask() && clonecontrol) {
                clone.createControlTask(taskgraph.getControlTask(), prestasks);
//
//                if (taskgraph.isControlTaskConnected()) {
//                    connectControlTask(clone);
//                }
            }
            TaskGraphContext context = taskgraph.getContext();
            Collection<String> keys = context.getKeys();
            for (String key : keys) {
                clone.setContextProperty(key, context.getProperty(key));
            }


            return clone;
        } catch (ClassCastException except) {
            except.printStackTrace();
            throw (new TaskGraphException("cloningError" + ": " + "NodeError", except));
        } catch (TaskGraphException except) {
            except.printStackTrace();
            throw (new TaskGraphException("cloningError" + ": " + except.getMessage(), except));
        }
    }

    private void addFileUnit(Node sendnode, Node recnode, TaskGraph clone) throws CableException {
        try {
            Task fileTask = new TaskImp(
                    makeTool(FileUnit.class, "" + Math.random() * 100, clone.getProperties()),
                    new TaskFactoryImp(),
                    false
            );

            Task task = clone.createTask(fileTask, false);

            Node inNode = task.addDataInputNode();
            Node outNode = task.addDataOutputNode();

            clone.connect(sendnode, inNode);
            clone.connect(outNode, recnode);
        } catch (TaskException e) {
            e.printStackTrace();
        } catch (ProxyInstantiationException e) {
            e.printStackTrace();
        }
    }

    private Task initDaxTask(Task task, Class clazz) {
        Tool tool = null;
        try {
            tool = makeTool(clazz, task.getToolName(), task.getProperties());
        } catch (ProxyInstantiationException e) {
            e.printStackTrace();
        } catch (TaskException e) {
            e.printStackTrace();
        }
        try {
            Task daxTask = new TaskImp(tool, new TaskFactoryImp(), false);
            setParameters(task, daxTask);
            return daxTask;
        } catch (TaskException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setParameters(Task task, Task daxTask) {
        int count = 0;
        while (count < task.getInputNodeCount()) {
            try {
                daxTask.addDataInputNode();
            } catch (NodeException e) {
                e.printStackTrace();
            }
            count++;
        }
        count = 0;

        while (count < task.getOutputNodeCount()) {
            try {
                daxTask.addDataOutputNode();
            } catch (NodeException e) {
                e.printStackTrace();
            }
            count++;
        }
        //Todo - probably something technical and frustrating...
    }

    private Tool makeTool(Class clazz, String name, TrianaProperties properties) throws ProxyInstantiationException, TaskException {
        Tool tool = new ToolImp(properties);
        tool.setProxy(new JavaProxy(clazz.getSimpleName(), clazz.getPackage().getName()));
        tool.setToolPackage(clazz.getPackage().getName());
        tool.setToolName(name);
        System.out.println("New task, name : " + tool.getToolName());
        return tool;
    }

    @Override
    public String getServiceName() {
        return "taskgraph-to-daxJobs";
    }

    @Override
    public String getLongOption() {
        return "taskgraph-to-daxJobs";
    }

    @Override
    public String getShortOption() {
        return "daxJobs";
    }

    @Override
    public String getDescription() {
        return "An intermediary stage between a taskgraph and a dax, where units must input/output files.";
    }

    @Override
    public void execute(Exec execEngine, TrianaInstance engine, String workflow, Object workflowObject, Object inputData, String[] TrianaArgs) throws Exception {
    }

    @Override
    public Tool getTool(TrianaInstance instance, String workflowFilePath) throws Exception {
        return null;
    }

    @Override
    public Object getWorkflow(Tool tool) {
        return convert((TaskGraph) tool);
    }

    @Override
    public File getWorkflowFile(Tool tool) {
        return null;
    }

    @Override
    public File getConfigFile() throws IOException {
        return null;
    }
}
