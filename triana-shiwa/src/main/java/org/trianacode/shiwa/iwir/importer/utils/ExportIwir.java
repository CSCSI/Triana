package org.trianacode.shiwa.iwir.importer.utils;

import org.apache.commons.lang.ArrayUtils;
import org.shiwa.fgi.iwir.*;
import org.trianacode.taskgraph.Cable;
import org.trianacode.taskgraph.Node;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskGraph;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 25/10/2011
 * Time: 15:22
 * To change this template use File | Settings | File Templates.
 */
public class ExportIwir {

    private HashSet<Cable> cables = new HashSet<Cable>();
    private HashMap<Task, AbstractTask> taskHashMap = new HashMap<Task, AbstractTask>();

    public void taskGraphToIWIRFile(TaskGraph taskGraph, File file) throws IOException {
        System.out.println(file.getAbsolutePath());
        BlockScope blockScope = taskGraphToBlockScope(taskGraph);
        System.out.println(blockScope == null);
        writeIWIR(blockScope, file);
    }

    private File writeIWIR(BlockScope blockScope, File file) throws IOException {
        IWIR iwir = new IWIR(blockScope.getName());
        iwir.setTask(blockScope);
        iwir.asXMLFile(file);
        System.out.println("\n" + iwir.asXMLString());
        return file;
    }

    public BlockScope taskGraphToBlockScope(TaskGraph taskGraph) {
        BlockScope blockScope = recordTasksAndCables(taskGraph);

        System.out.println("\nTasks " + taskHashMap.toString());
        System.out.println("Cables " + ArrayUtils.toString(cables.toArray()));
        for (Cable cable : cables) {
            if (cable != null) {
                System.out.println("\n" + cable);

                Node receivingNode = cable.getReceivingNode().getTopLevelNode();
                System.out.println("Cable sends data into " + receivingNode.getName());
                Node sendingNode = cable.getSendingNode().getTopLevelNode();
                System.out.println("Cable receives data from " + sendingNode.getName());


                AbstractTask sendingIwirTask = taskHashMap.get(sendingNode.getTask());
                System.out.println("sending task : " + sendingIwirTask.getUniqueId());
                AbstractTask receivingIwirTask = taskHashMap.get(receivingNode.getTask());
                System.out.println("receiving task : " + receivingIwirTask.getUniqueId());


                if (sendingIwirTask.getParentTask() != receivingIwirTask.getParentTask()) {
                    System.out.println("***Out of scope");

//                    AbstractCompoundTask sendingCompoundTask = ((AbstractCompoundTask)taskHashMap.get(
//                            sendingNode.getTopLevelNode().getTask()).getParentTask());
                    OutputPort outputPort = addOutputNodeChainToBlockScope(sendingNode);

//                    AbstractCompoundTask receivingCompoundTask = ((AbstractCompoundTask)taskHashMap.get(
//                            receivingNode.getTopLevelNode().getTask()).getParentTask());
                    InputPort inputPort = addInputNodeChainToBlockScope(receivingNode);

                    AbstractCompoundTask topLevelCompoundTask = ((AbstractCompoundTask) outputPort.getMyTask().getParentTask());

                    System.out.println("Linking in scope " + outputPort
                            + " to " + inputPort + " scope "
                            + topLevelCompoundTask);
                    topLevelCompoundTask.addLink(outputPort, inputPort);


                } else {

                    OutputPort outputPort = new OutputPort("out" + (sendingNode.getNodeIndex() + 1), SimpleType.STRING);
                    sendingIwirTask.addOutputPort(outputPort);
                    InputPort inputPort = new InputPort("in" + (receivingNode.getNodeIndex() + 1), SimpleType.STRING);
                    receivingIwirTask.addInputPort(inputPort);
                    ((AbstractCompoundTask) sendingIwirTask.getParentTask()).addLink(outputPort, inputPort);
                    System.out.println("Linked in scope " + outputPort + " to " + inputPort);
                }

            }
        }
        addIWIRGraphNodes(taskGraph);

        return blockScope;
    }

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
                Object typeObject = task.getParameter("TaskType");
                String type = "";
                if (typeObject != null) {
                    type = (String) typeObject;
                }
                org.shiwa.fgi.iwir.Task iwirTask = new org.shiwa.fgi.iwir.Task(task.getToolName().replaceAll(" ", "_"), type);
                taskHashMap.put(task, iwirTask);
                blockScope.addTask(iwirTask);
            }
        }
        return blockScope;
    }


    private void addIWIRGraphNodes(TaskGraph taskGraph) {
        for (Node node : taskGraph.getInputNodes()) {
            addInputNodeChainToBlockScope(node);
        }
        for (Node node : taskGraph.getOutputNodes()) {
            addOutputNodeChainToBlockScope(node);
        }
    }

    private InputPort addInputNodeChainToBlockScope(Node node) {
        System.out.println("\n Input chain with node : " + node);
        System.out.println("top " + node.getTopLevelNode());
        System.out.println("bottom " + node.getBottomLevelNode());
        InputPort inputBlockPort = new InputPort("in" + (node.getBottomLevelNode().getNodeIndex() + 1), SimpleType.STRING);
        taskHashMap.get(node.getBottomLevelTask()).addInputPort(inputBlockPort);

        if (node.getTopLevelNode() == node.getBottomLevelNode()) {
            System.out.println("Single node, no scope issues :)");
            return inputBlockPort;
        }

        Node scopeNode = node.getBottomLevelNode();
        InputPort scopePort = inputBlockPort;
        while (scopeNode.getParentNode() != node.getTopLevelNode()) {
            scopeNode = scopeNode.getParentNode();
            InputPort newPort = new InputPort("in" + (scopeNode.getNodeIndex() + 1), SimpleType.STRING);
            taskHashMap.get(scopeNode.getTask()).addInputPort(newPort);
            System.out.println("added " + scopeNode + " to input chain");
            ((AbstractCompoundTask) newPort.getMyTask().getParentTask()).addLink(scopePort, newPort);
            scopePort = newPort;
        }

        Node topLevelNode = node.getTopLevelNode();
        InputPort taskInputPort = new InputPort("in" + (topLevelNode.getNodeIndex() + 1), SimpleType.STRING);

        AbstractTask iwirTask = taskHashMap.get(topLevelNode.getTask());
        iwirTask.addInputPort(taskInputPort);

        System.out.println("Trying to add " + taskInputPort.getUniqueId()
                + " to end of input chain - previous node "
                + scopePort.getUniqueId());
        ((AbstractCompoundTask) iwirTask.getParentTask()).addLink(scopePort, taskInputPort);

        System.out.println("Returning " + inputBlockPort.getUniqueId());
        return inputBlockPort;
    }

    private OutputPort addOutputNodeChainToBlockScope(Node node) {
        System.out.println("\n Output chain with node : " + node);
        System.out.println("top " + node.getTopLevelNode());
        System.out.println("bottom " + node.getBottomLevelNode());
        OutputPort outputBlockPort = new OutputPort("out" + (node.getBottomLevelNode().getNodeIndex() + 1), SimpleType.STRING);
        taskHashMap.get(node.getBottomLevelTask()).addOutputPort(outputBlockPort);

        if (node.getTopLevelNode() == node.getBottomLevelNode()) {
            System.out.println("Single node, no scope issues :)");
            return outputBlockPort;
        }

        Node scopeNode = node.getBottomLevelNode();
        OutputPort scopePort = outputBlockPort;
        while (scopeNode.getParentNode() != node.getTopLevelNode()) {
            scopeNode = scopeNode.getParentNode();
            OutputPort newPort = new OutputPort("out" + (scopeNode.getNodeIndex() + 1), SimpleType.STRING);
            taskHashMap.get(scopeNode.getTask()).addOutputPort(newPort);
            System.out.println("added " + scopeNode + " to output chain");
            ((AbstractCompoundTask) newPort.getMyTask().getParentTask()).addLink(newPort, scopePort);
            scopePort = newPort;
        }

        Node topLevelNode = node.getTopLevelNode();
        OutputPort taskOutputPort = new OutputPort("out" + (topLevelNode.getNodeIndex() + 1), SimpleType.STRING);

        AbstractTask iwirTask = taskHashMap.get(topLevelNode.getTask());
        iwirTask.addOutputPort(taskOutputPort);

        ((AbstractCompoundTask) iwirTask.getParentTask()).addLink(taskOutputPort, scopePort);

        System.out.println("Returning " + outputBlockPort.getUniqueId());
        return outputBlockPort;
    }

}
