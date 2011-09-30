package org.trianacode.shiwa.iwir.test;

import org.apache.commons.lang.ArrayUtils;
import org.shiwa.fgi.iwir.*;
import org.trianacode.TrianaInstance;
import org.trianacode.config.TrianaProperties;
import org.trianacode.taskgraph.*;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.imp.ToolImp;
import org.trianacode.taskgraph.proxy.ProxyInstantiationException;
import org.trianacode.taskgraph.proxy.java.JavaProxy;
import org.trianacode.taskgraph.ser.XMLReader;
import org.trianacode.taskgraph.ser.XMLWriter;
import org.trianacode.taskgraph.tool.Tool;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 23/09/2011
 * Time: 15:19
 * To change this template use File | Settings | File Templates.
 */
public class TaskGraphToIWIR {

    public static void main(String[] args) {
        try {
            new TaskGraphToIWIR();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TaskGraphException e) {
            e.printStackTrace();
        } catch (ProxyInstantiationException e) {
            e.printStackTrace();
        }
    }

    public TaskGraphToIWIR() throws IOException, TaskGraphException, ProxyInstantiationException {
        TrianaInstance trianaInstance = kickstartTriana();

        initTaskTypeToTool();

        TaskGraph taskGraph = createTaskGraph(trianaInstance);

        fillTaskgraph(taskGraph);

        File file = createTaskGraphFile(taskGraph, "initialTriana.xml");

        TaskGraph readTaskgraph = readTaskgraph(new File("xml/AAscopeChecker.xml"), trianaInstance);
//        TaskGraph readTaskgraph = readTaskgraph(file, trianaInstance);

        BlockScope blockScope = taskGraphToIWIR(readTaskgraph);

        File iwirFile = writeIWIR(blockScope, "taskToIWIR.xml");

        IWIR iwir = readIWIR(iwirFile);

        TaskGraph iwirTaskGraph = taskFromIwir(trianaInstance, iwir);

        createTaskGraphFile(iwirTaskGraph, "iwirToTaskgraph.xml");
    }

    private void initTaskTypeToTool() {
        TaskTypeToTool.addTaskType("InOut", InOut.class);
    }


    private HashMap<AbstractTask, Task> abstractHashMap = new HashMap<AbstractTask, Task>();

    private TaskGraph taskFromIwir(TrianaInstance trianaInstance, IWIR iwir) throws TaskException, ProxyInstantiationException {
        TaskGraph taskGraph = createTaskGraph(trianaInstance);
        AbstractTask mainTask = iwir.getTask();
        for (AbstractTask iwirTask : mainTask.getChildren()) {
            if (iwirTask instanceof org.shiwa.fgi.iwir.Task) {
                String type = ((org.shiwa.fgi.iwir.Task) iwirTask).getTasktype();
                Class clazz = TaskTypeToTool.getTaskFromType(type);
                if (clazz == null) {
                    clazz = InOut.class;
                    type = "InOut";
                }
                Task trianaTask = taskGraph.createTask(
                        makeTool(
                                clazz, iwirTask.getName(), taskGraph.getProperties()
                        )
                );
                trianaTask.setParameter("TaskType", type);
                abstractHashMap.put(iwirTask, trianaTask);
                addTaskGraphNodesFromIWIR(iwirTask, mainTask, trianaTask, taskGraph);
            } else {
                //TODO
            }
        }

        for (AbstractTask iwirTask : mainTask.getChildren()) {
            for (InputPort inputPort : iwirTask.getInputPorts()) {
                System.out.println("\n" + inputPort.getUniqueId());
                for (AbstractPort inputPortPredecessor : inputPort.getPredecessors()) {
                    AbstractTask sendingIWIR = inputPortPredecessor.getMyTask();
                    if (sendingIWIR != mainTask) {
                        System.out.println("pre " + inputPortPredecessor.getUniqueId()
                                + " on " + sendingIWIR.getUniqueId());
                        Task sendingTask = abstractHashMap.get(sendingIWIR);
                        Task receivingTask = abstractHashMap.get(iwirTask);

                        try {
                            taskGraph.connect(sendingTask.addDataOutputNode(), receivingTask.addDataInputNode());
                        } catch (CableException e) {
                            e.printStackTrace();
                        }
                    }

                }
//                for(AbstractPort inputPortSuccessor : inputPort.getSuccessors()){
//                    System.out.println("suc " + inputPortSuccessor.getUniqueId());
//                }
            }
        }

        return taskGraph;
    }

    private void addTaskGraphNodesFromIWIR(AbstractTask iwirTask, AbstractTask mainTask, Task trianaTask, TaskGraph taskGraph) throws NodeException {
        for (InputPort inputPort : iwirTask.getInputPorts()) {
            System.out.println("\n" + inputPort.getUniqueId());
            for (AbstractPort inputPortPredecessor : inputPort.getPredecessors()) {
                if (inputPortPredecessor.getMyTask() == mainTask) {
                    System.out.println("pre " + inputPortPredecessor.getUniqueId());
                    taskGraph.addDataInputNode(trianaTask.addDataInputNode());
                }
            }
//            for(AbstractPort inputPortSuccessor : inputPort.getSuccessors()){
//                System.out.println("suc " + inputPortSuccessor.getUniqueId());
//            }
        }

        for (OutputPort outputPort : iwirTask.getOutputPorts()) {
            System.out.println(outputPort.getUniqueId());
//            for(AbstractPort outputPortPredecessor : outputPort.getPredecessors()){
//                System.out.println("pre " + outputPortPredecessor.getUniqueId());
//            }
            for (AbstractPort outputPortSuccessor : outputPort.getSuccessors()) {
                if (outputPortSuccessor.getMyTask() == mainTask) {
                    System.out.println("suc " + outputPortSuccessor.getUniqueId()
                            + " on " + outputPortSuccessor.getMyTask().getUniqueId());
                    taskGraph.addDataOutputNode(trianaTask.addDataOutputNode());
                }
            }
        }
    }

    private IWIR readIWIR(File iwirFile) throws FileNotFoundException {
        IWIR iwir = new IWIR(iwirFile);
        return iwir;
    }

    private File writeIWIR(BlockScope blockScope, String path) throws IOException {
        IWIR iwir = new IWIR(blockScope.getName());
        iwir.setTask(blockScope);
        File file = new File(path);
        iwir.asXMLFile(file);
        System.out.println("\n" + iwir.asXMLString());
        return file;
    }

    private HashSet<Cable> cables = new HashSet<Cable>();
    private HashMap<Task, AbstractTask> taskHashMap = new HashMap<Task, AbstractTask>();

    private BlockScope taskGraphToIWIR(TaskGraph taskGraph) {
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

    private TaskGraph readTaskgraph(File file, TrianaInstance trianaInstance) throws IOException, TaskGraphException {
        XMLReader xmlReader = new XMLReader(new FileReader(file));
        Tool tool = xmlReader.readComponent(trianaInstance.getProperties());
        return (TaskGraph) tool;
    }

    private TaskGraph fillTaskgraph(TaskGraph taskGraph) throws IOException, TaskException, ProxyInstantiationException, CableException {

        Tool tool0 = makeTool(TaskTypeToTool.getTaskFromType("InOut"), "InOut", taskGraph.getProperties());
        Task task0 = taskGraph.createTask(tool0);
        task0.setParameter("TaskType", "InOut");
        taskGraph.addDataInputNode(task0.addDataInputNode());

        Tool tool1 = makeTool(TaskTypeToTool.getTaskFromType("InOut"), "InOut", taskGraph.getProperties());
        Task task1 = taskGraph.createTask(tool1);
        task1.setParameter("TaskType", "InOut");

        Tool tool2 = makeTool(TaskTypeToTool.getTaskFromType("InOut"), "InOut", taskGraph.getProperties());
        Task task2 = taskGraph.createTask(tool2);
        task2.setParameter("TaskType", "InOut");
        taskGraph.addDataOutputNode(task2.addDataOutputNode());

        taskGraph.connect(task0.addDataOutputNode(), task1.addDataInputNode());
        taskGraph.connect(task1.addDataOutputNode(), task2.addDataInputNode());

        taskGraph.setToolName("TestTaskgraph");
        return taskGraph;
    }

    private Tool makeTool(Class clazz, String name, TrianaProperties properties) throws ProxyInstantiationException, TaskException {
        Tool tool = new ToolImp(properties);
        tool.setProxy(new JavaProxy(clazz.getSimpleName(), clazz.getPackage().getName()));
        tool.setToolPackage(clazz.getPackage().getName());
        tool.setToolName(name);
        return tool;
    }

    private File createTaskGraphFile(TaskGraph taskGraph, String path) throws IOException, TaskException, ProxyInstantiationException {
        File outputFile = new File(path);
        XMLWriter writer = new XMLWriter(new PrintWriter(outputFile));
        writer.writeComponent(taskGraph);
        return outputFile;

    }

    private TrianaInstance kickstartTriana() throws IOException {
        TrianaInstance trianaInstance = new TrianaInstance();
        trianaInstance.init();
        return trianaInstance;
    }

    private TaskGraph createTaskGraph(TrianaInstance trianaInstance) throws TaskException {
        TaskGraphManager.initTaskGraphManager(trianaInstance.getProperties());
        TaskGraph taskGraph = TaskGraphManager.createTaskGraph();
        return (taskGraph);
    }

}
