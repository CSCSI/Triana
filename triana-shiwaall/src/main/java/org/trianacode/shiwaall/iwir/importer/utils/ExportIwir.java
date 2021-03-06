package org.trianacode.shiwaall.iwir.importer.utils;

import org.apache.commons.lang.ArrayUtils;
import org.shiwa.fgi.iwir.*;
import org.trianacode.shiwaall.iwir.execute.Executable;
import org.trianacode.shiwaall.iwir.holders.AtomicTaskHolder;
import org.trianacode.taskgraph.Cable;
import org.trianacode.taskgraph.Node;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.proxy.java.JavaConstants;
import org.trianacode.taskgraph.tool.Tool;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 25/10/2011
 * Time: 15:22
 * To change this template use File | Settings | File Templates.
 */
public class ExportIwir {

    /** The cables. */
    private HashSet<Cable> cables = new HashSet<Cable>();

    /** The task hash map. */
    private HashMap<Task, AbstractTask> taskHashMap = new HashMap<Task, AbstractTask>();

    /** The std. */
    boolean std = false;

    /**
     * Task graph to iwir file.
     *
     * @param taskGraph the task graph
     * @param file the file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void taskGraphToIWIRFile(TaskGraph taskGraph, File file) throws IOException {
        System.out.println("Writing IWIR : " + file.getAbsolutePath());
        BlockScope blockScope = taskGraphToBlockScope(taskGraph);
        System.out.println(blockScope == null);
        writeIWIR(blockScope, file);
    }

    public HashMap<Task, AbstractTask> getTaskHashMap(){
        return taskHashMap;
    }

    /**
     * Std out.
     *
     * @param string the string
     */
    private void stdOut(String string){
        if(std){
            System.out.printf(string);
        }
    }

    /**
     * Write iwir Blockscope to xml file.
     *
     * @param blockScope the block scope
     * @param file the file
     * @return the file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private File writeIWIR(BlockScope blockScope, File file) throws IOException {
        IWIR iwir = new IWIR(file.getName());
        iwir.setTask(blockScope);
        iwir.asXMLFile(file);
        System.out.println("\n" + iwir.asXMLString());
        return file;
    }

    /**
     * Triana Taskgraph to IWIR blockscope conversion.
     * After recordingTasksAndCables() iterates through the cables in the Taskgraph to
     * create the connections within iwir
     *
     * @param taskGraph the task graph
     * @return the block scope
     */
    public BlockScope taskGraphToBlockScope(TaskGraph taskGraph) {
        BlockScope blockScope = recordTasksAndCables(taskGraph);

        System.out.println("\nTasks " + taskHashMap.toString());
        stdOut("Cables " + ArrayUtils.toString(cables.toArray()));
        for (Cable cable : cables) {
            if (cable != null) {
                stdOut("\n" + cable);

                Node receivingNode = cable.getReceivingNode().getTopLevelNode();
                stdOut("Cable sends data into " + receivingNode.getName());
                Node sendingNode = cable.getSendingNode().getTopLevelNode();
                stdOut("Cable receives data from " + sendingNode.getName());


                AbstractTask sendingIwirTask = taskHashMap.get(sendingNode.getTask());
                stdOut("sending task : " + sendingIwirTask.getUniqueId());
                AbstractTask receivingIwirTask = taskHashMap.get(receivingNode.getTask());
                stdOut("receiving task : " + receivingIwirTask.getUniqueId());


                if (sendingIwirTask.getParentTask() != receivingIwirTask.getParentTask()) {
                    stdOut("***Out of scope");

//                    AbstractCompoundTask sendingCompoundTask = ((AbstractCompoundTask)taskHashMap.get(
//                            sendingNode.getTopLevelNode().getTask()).getParentTask());
                    OutputPort outputPort = addOutputNodeChainToBlockScope(sendingNode);

//                    AbstractCompoundTask receivingCompoundTask = ((AbstractCompoundTask)taskHashMap.get(
//                            receivingNode.getTopLevelNode().getTask()).getParentTask());
                    InputPort inputPort = addInputNodeChainToBlockScope(receivingNode);

                    AbstractCompoundTask topLevelCompoundTask = ((AbstractCompoundTask) outputPort.getMyTask().getParentTask());

                    stdOut("Linking in scope " + outputPort
                            + " to " + inputPort + " scope "
                            + topLevelCompoundTask);
                    topLevelCompoundTask.addLink(outputPort, inputPort);


                } else {

                    OutputPort outputPort = new OutputPort("out" + (sendingNode.getNodeIndex() + 1), SimpleType.FILE);
                    sendingIwirTask.addOutputPort(outputPort);
                    InputPort inputPort = new InputPort("in" + (receivingNode.getNodeIndex() + 1), SimpleType.FILE);
                    receivingIwirTask.addInputPort(inputPort);
                    ((AbstractCompoundTask) sendingIwirTask.getParentTask()).addLink(outputPort, inputPort);
                    stdOut("Linked in scope " + outputPort + " to " + inputPort);
                }

            }
        }
        addIWIRGraphNodes(taskGraph);

        return blockScope;
    }

    /**
     * Record tasks and cables, produces iwir Tasks
     *
     * @param taskGraph the task graph
     * @return the block scope
     */
    private BlockScope recordTasksAndCables(TaskGraph taskGraph) {
        BlockScope blockScope = new BlockScope(taskGraph.getToolName().replaceAll(" ", "_"));
        taskHashMap.put(taskGraph, blockScope);

        for (Task task : taskGraph.getTasks(false)) {
            for (Node node : task.getDataInputNodes()) {
                cables.add(node.getCable());
            }
            for (Node node : task.getDataOutputNodes()) {
                cables.add(node.getCable());
            }

            if (task instanceof TaskGraph) {
                // TODO correct wild optimism
                blockScope.addTask(recordTasksAndCables((TaskGraph) task));
            } else {
                String tasktype;
                System.out.println("Exporting task of class " + task.getClass().getCanonicalName());

                Map<String, Object> map = task.getProxy().getInstanceDetails();

                Object unitPackage = map.get(JavaConstants.UNIT_PACKAGE);
                Object unitName = map.get(JavaConstants.UNIT_NAME);
                stdOut(unitPackage + " " + unitName);
                if (unitName != null && unitPackage != null) {
                    tasktype = unitPackage.toString() + "." + unitName.toString();
                } else {
                    tasktype = task.getToolName();
                }
                if(tasktype.equals(AtomicTaskHolder.class.getCanonicalName())){
                    Object taskTypeObject = task.getParameter(Executable.TASKTYPE);
                    if (taskTypeObject != null) {
                        tasktype = (String) taskTypeObject;
                    } else {
                        tasktype = task.getToolName();
                    }
                }
                System.out.println("Tasktype for " + task.getToolName() + " " + tasktype);

                org.shiwa.fgi.iwir.Task iwirTask = new org.shiwa.fgi.iwir.Task(task.getToolName().replaceAll(" ", "_"), tasktype);
                for (String name : task.getParameterNames()) {
                    String type = task.getParameterType(name);
                    if (type.equals(Tool.USER_ACCESSIBLE)) {
                        iwirTask.addProperty(new Property(name, task.getParameter(name).toString()));
                    }
                }

                taskHashMap.put(task, iwirTask);
                blockScope.addTask(iwirTask);
            }
        }
        return blockScope;
    }


    /**
     * Adds the iwir graph nodes.
     *
     * @param taskGraph the task graph
     */
    private void addIWIRGraphNodes(TaskGraph taskGraph) {
        for (Node node : taskGraph.getInputNodes()) {
            addInputNodeChainToBlockScope(node);
        }
        for (Node node : taskGraph.getOutputNodes()) {
            addOutputNodeChainToBlockScope(node);
        }
    }

    /**
     * Adds the input node chain to block scope.
     *
     * @param node the node
     * @return the input port
     */
    private InputPort addInputNodeChainToBlockScope(Node node) {
        stdOut("\n Input chain with node : " + node);
        stdOut("top " + node.getTopLevelNode());
        stdOut("bottom " + node.getBottomLevelNode());
        InputPort inputBlockPort = new InputPort("in" + (node.getBottomLevelNode().getNodeIndex() + 1), SimpleType.FILE);
        taskHashMap.get(node.getBottomLevelTask()).addInputPort(inputBlockPort);

        if (node.getTopLevelNode() == node.getBottomLevelNode()) {
            stdOut("Single node, no scope issues :)");
            return inputBlockPort;
        }

        Node scopeNode = node.getBottomLevelNode();
        InputPort scopePort = inputBlockPort;
        while (scopeNode.getParentNode() != node.getTopLevelNode()) {
            scopeNode = scopeNode.getParentNode();
            InputPort newPort = new InputPort("in" + (scopeNode.getNodeIndex() + 1), SimpleType.FILE);
            taskHashMap.get(scopeNode.getTask()).addInputPort(newPort);
            stdOut("added " + scopeNode + " to input chain");
            ((AbstractCompoundTask) newPort.getMyTask().getParentTask()).addLink(scopePort, newPort);
            scopePort = newPort;
        }

        Node topLevelNode = node.getTopLevelNode();
        InputPort taskInputPort = new InputPort("in" + (topLevelNode.getNodeIndex() + 1), SimpleType.FILE);

        AbstractTask iwirTask = taskHashMap.get(topLevelNode.getTask());
        iwirTask.addInputPort(taskInputPort);

        stdOut("Trying to add " + taskInputPort.getUniqueId()
                + " to end of input chain - previous node "
                + scopePort.getUniqueId());
        ((AbstractCompoundTask) iwirTask.getParentTask()).addLink(scopePort, taskInputPort);

        stdOut("Returning " + inputBlockPort.getUniqueId());
        return inputBlockPort;
    }

    /**
     * Adds the output node chain to block scope.
     *
     * @param node the node
     * @return the output port
     */
    private OutputPort addOutputNodeChainToBlockScope(Node node) {
        stdOut("\n Output chain with node : " + node);
        stdOut("top " + node.getTopLevelNode());
        stdOut("bottom " + node.getBottomLevelNode());
        OutputPort outputBlockPort = new OutputPort("out" + (node.getBottomLevelNode().getNodeIndex() + 1), SimpleType.FILE);
        taskHashMap.get(node.getBottomLevelTask()).addOutputPort(outputBlockPort);

        if (node.getTopLevelNode() == node.getBottomLevelNode()) {
            stdOut("Single node, no scope issues :)");
            return outputBlockPort;
        }

        Node scopeNode = node.getBottomLevelNode();
        OutputPort scopePort = outputBlockPort;
        while (scopeNode.getParentNode() != node.getTopLevelNode()) {
            scopeNode = scopeNode.getParentNode();
            OutputPort newPort = new OutputPort("out" + (scopeNode.getNodeIndex() + 1), SimpleType.FILE);
            taskHashMap.get(scopeNode.getTask()).addOutputPort(newPort);
            stdOut("added " + scopeNode + " to output chain");
            ((AbstractCompoundTask) newPort.getMyTask().getParentTask()).addLink(newPort, scopePort);
            scopePort = newPort;
        }

        Node topLevelNode = node.getTopLevelNode();
        OutputPort taskOutputPort = new OutputPort("out" + (topLevelNode.getNodeIndex() + 1), SimpleType.FILE);

        AbstractTask iwirTask = taskHashMap.get(topLevelNode.getTask());
        iwirTask.addOutputPort(taskOutputPort);

        ((AbstractCompoundTask) iwirTask.getParentTask()).addLink(taskOutputPort, scopePort);

        stdOut("Returning " + outputBlockPort.getUniqueId());
        return outputBlockPort;
    }

}
