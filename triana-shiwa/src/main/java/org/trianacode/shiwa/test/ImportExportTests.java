package org.trianacode.shiwa.test;

import org.apache.commons.lang.ArrayUtils;
import org.shiwa.fgi.iwir.*;
import org.trianacode.TrianaInstance;
import org.trianacode.shiwa.iwir.factory.TaskHolder;
import org.trianacode.shiwa.iwir.factory.TaskHolderFactory;
import org.trianacode.shiwa.iwir.importer.utils.TaskTypeToTool;
import org.trianacode.shiwa.iwir.importer.utils.ToolUtils;
import org.trianacode.taskgraph.*;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.proxy.ProxyInstantiationException;
import org.trianacode.taskgraph.ser.XMLReader;
import org.trianacode.taskgraph.ser.XMLWriter;
import org.trianacode.taskgraph.tool.Tool;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 23/09/2011
 * Time: 15:19
 * To change this template use File | Settings | File Templates.
 */
public class ImportExportTests {

    public static void main(String[] args) {
        try {
            new ImportExportTests();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TaskGraphException e) {
            e.printStackTrace();
        } catch (ProxyInstantiationException e) {
            e.printStackTrace();
        }
    }

    public ImportExportTests() throws IOException, TaskGraphException, ProxyInstantiationException {
        TrianaInstance trianaInstance = kickstartTriana();

        initTaskTypeToTool();

        initTaskGraph(trianaInstance);

        TaskGraph graphInGraph = testGraphInGraph();

        TaskGraph taskGraph = TaskGraphManager.createTaskGraph();

        fillTaskgraph(taskGraph);

        File file = createTaskGraphFile(taskGraph, "initialTriana.xml");

        TaskGraph readTaskgraph = readTaskgraph(new File("../xml/aaaScopeChecker.xml"), trianaInstance);
//        TaskGraph readTaskgraph = readTaskgraph(file, trianaInstance);

        BlockScope blockScope = taskGraphToIWIR(readTaskgraph);

        File iwirFile = writeIWIR(blockScope, "taskToIWIR.xml");

        File blockScopeFile = new File("../xml/blockscope.xml");

//        IWIR iwir = readIWIR(iwirFile);
        IWIR iwir = readIWIR(blockScopeFile);


        TaskGraph iwirTaskGraph = taskFromIwir(iwir);

        createTaskGraphFile(iwirTaskGraph, "iwirToTaskgraph.xml");
    }

    private TaskGraph testGraphInGraph() throws TaskException, IOException, ProxyInstantiationException {
        TaskGraph outerTaskGraph = TaskGraphManager.createTaskGraph();
        outerTaskGraph.setToolName("outer");
        TaskGraph middleTaskGraph = TaskGraphManager.createTaskGraph();
        middleTaskGraph.setToolName("middle");
        TaskGraph innerTaskGraph = TaskGraphManager.createTaskGraph();
        innerTaskGraph.setToolName("inner");

        Class clazz = TaskTypeToTool.getTaskFromType("InOut");
//        Task taska = outerTaskGraph.createTask(makeTool(clazz, "InOuta", outerTaskGraph.getProperties()));
//        Task taskb = middleTaskGraph.createTask(makeTool(clazz, "InOutb", middleTaskGraph.getProperties()));
        Task taskc = innerTaskGraph.createTask(ToolUtils.makeTool(clazz, "InOutc", innerTaskGraph.getProperties()));

        middleTaskGraph.createTask(innerTaskGraph);
        outerTaskGraph.createTask(middleTaskGraph);

        innerTaskGraph.addDataInputNode(taskc.addDataInputNode());
//
//        Node parentNode;
//        Task scopeTask = taskc;
//        Task parentTask;
//        Node scopeNode = scopeTask.addDataInputNode();
//        while(scopeTask.getParent() != null){
//            parentTask = scopeTask.getParent();
//            parentNode = ((TaskGraph)parentTask).addDataInputNode(scopeNode);
//            scopeTask = scopeTask.getParent();
//            scopeNode = parentNode;
//        }


//        outerTaskGraph.addDataInputNode(taska.addDataInputNode());
//        middleTaskGraph.addDataInputNode(taskb.addDataInputNode());
//        innerTaskGraph.addDataInputNode(taskc.addDataInputNode());

        createTaskGraphFile(outerTaskGraph, "testOuter.xml");
//        System.exit(1);
        return outerTaskGraph;
    }

    private void initTaskTypeToTool() {
        TaskTypeToTool.addTaskType("InOut", InOut.class);
    }

    private IWIR readIWIR(File iwirFile) throws FileNotFoundException {
        return new IWIR(iwirFile);
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
        System.out.println("Reading " + file.getAbsolutePath());
        XMLReader xmlReader = new XMLReader(new FileReader(file));
        Tool tool = xmlReader.readComponent(trianaInstance.getProperties());
        return (TaskGraph) tool;
    }

    private TaskGraph fillTaskgraph(TaskGraph taskGraph) throws IOException, TaskException, ProxyInstantiationException, CableException {

        Tool tool0 = ToolUtils.makeTool(TaskTypeToTool.getTaskFromType("InOut"), "InOut", taskGraph.getProperties());
        Task task0 = taskGraph.createTask(tool0);
        task0.setParameter("TaskType", "InOut");
        taskGraph.addDataInputNode(task0.addDataInputNode());

        Tool tool1 = ToolUtils.makeTool(TaskTypeToTool.getTaskFromType("InOut"), "InOut", taskGraph.getProperties());
        Task task1 = taskGraph.createTask(tool1);
        task1.setParameter("TaskType", "InOut");

        Tool tool2 = ToolUtils.makeTool(TaskTypeToTool.getTaskFromType("InOut"), "InOut", taskGraph.getProperties());
        Task task2 = taskGraph.createTask(tool2);
        task2.setParameter("TaskType", "InOut");
        taskGraph.addDataOutputNode(task2.addDataOutputNode());

        taskGraph.connect(task0.addDataOutputNode(), task1.addDataInputNode());
        taskGraph.connect(task1.addDataOutputNode(), task2.addDataInputNode());

        taskGraph.setToolName("TestTaskgraph");
        return taskGraph;
    }

    private File createTaskGraphFile(TaskGraph taskGraph, String path) throws IOException {
        File outputFile = new File(path);
        XMLWriter outWriter = new XMLWriter(new PrintWriter(System.out));
        outWriter.writeComponent(taskGraph);

        XMLWriter fileWriter = new XMLWriter(new PrintWriter(outputFile));
        fileWriter.writeComponent(taskGraph);
        System.out.println("\nWrote file : " + outputFile.getAbsolutePath() + "\n");
        return outputFile;

    }

    private TrianaInstance kickstartTriana() throws IOException {
        TrianaInstance trianaInstance = new TrianaInstance();
        trianaInstance.init();
        return trianaInstance;
    }

    private void initTaskGraph(TrianaInstance trianaInstance) throws TaskException {
        TaskGraphManager.initTaskGraphManager(trianaInstance.getProperties());
    }

    private HashMap<AbstractTask, Task> abstractHashMap = new HashMap<AbstractTask, Task>();
    private HashSet<DataLink> dataLinks = new HashSet<DataLink>();

    private TaskGraph taskFromIwir(IWIR iwir) throws TaskException, ProxyInstantiationException, CableException {

        AbstractTask mainTask = iwir.getTask();
        TaskGraph taskGraph = recordAbstractTasksAndDataLinks(mainTask);
        System.out.println(taskGraph);

        System.out.println("Abstract Tasks " + abstractHashMap.toString());
        System.out.println("DataLinks " + ArrayUtils.toString(dataLinks.toArray()));


        for (DataLink dataLink : dataLinks) {
            System.out.println("\nLink from " + dataLink.getFromPort() + " to " + dataLink.getToPort());
            AbstractPort outgoingPort = dataLink.getFromPort();
            AbstractPort incomingPort = dataLink.getToPort();

            System.out.println(outgoingPort.getPredecessors());
            System.out.println(outgoingPort.getAllSuccessors());

            System.out.println(incomingPort.getPredecessors());
            System.out.println(incomingPort.getAllSuccessors());

            AbstractTask sendingAbstract = outgoingPort.getMyTask();
            AbstractTask receivingAbstract = incomingPort.getMyTask();

            Task sendingTask = abstractHashMap.get(sendingAbstract);
            Task receivingTask = abstractHashMap.get(receivingAbstract);

            System.out.println("Will connect " + sendingTask + " to " + receivingTask);

            //top level connections

            if (sendingTask == taskGraph && receivingAbstract instanceof org.shiwa.fgi.iwir.Task) {
                if (receivingTask.getParent() == sendingTask) {
                    taskGraph.addDataInputNode(receivingTask.addDataInputNode());
                }
            }
            if (receivingTask == taskGraph && sendingAbstract instanceof org.shiwa.fgi.iwir.Task) {
                if (sendingTask.getParent() == taskGraph) {
                    taskGraph.addDataOutputNode(sendingTask.addDataOutputNode());
                }
            }


            //check both are atomic tasks

            if (sendingAbstract instanceof org.shiwa.fgi.iwir.Task
                    && receivingAbstract instanceof org.shiwa.fgi.iwir.Task) {

                if (sendingTask.getParent() == receivingTask.getParent()) {
                    sendingTask.getParent().connect(sendingTask.addDataOutputNode(), receivingTask.addDataInputNode());
                }


            }


            Node outputNode;
            Node inputNode;
            TaskGraph scopeTaskGraph = sendingTask.getParent();


            //TODO Input chain
//            if(!(receivingTask instanceof TaskGraph)){
//                for (AbstractPort abstractPort : incomingPort.getPredecessors()){
//                    System.out.println("**predecessors " +
//                            abstractHashMap.get(abstractPort.getMyTask()).getToolName());
//                    if(abstractPort.getPredecessors().size() != 0){
//                        List<AbstractPort> ports = abstractPort.getPredecessors();
//                        for(AbstractPort port : ports){
//                            System.out.println(abstractHashMap.get(port.getMyTask()));
//                        }
//                    }


//                Node scopeNode = receivingTask.addDataInputNode();
//                System.out.println("Tasks input node " + scopeNode.getName());
//                Task topLevelTask;
//                List<AbstractPort> abstractPorts = incomingPort.getPredecessors();
//                while(abstractPorts.size() > 0){
//                    for(AbstractPort port : abstractPorts){
//                        topLevelTask = abstractHashMap.get(port.getMyTask());
//                        System.out.println("pre " + topLevelTask.getToolName());
//
//                        if(topLevelTask instanceof TaskGraph){
//                            TaskGraph scopeGraph = (TaskGraph)topLevelTask;
//                            System.out.println("node " + scopeNode.getName());
//                            System.out.println("in graph " + scopeGraph.getToolName());
//                            Node newNode = scopeGraph.addDataInputNode(scopeNode);
//                            System.out.println("new node " + newNode.getName());
//                            scopeNode = newNode;
//                        }
//                    }
//                    System.out.println(abstractPorts.get(0).getPredecessors().size());
//                    abstractPorts = abstractPorts.get(0).getPredecessors();
//                }
//
//            }

//            }


//            //TODO Output chain
//            if(!(sendingTask instanceof TaskGraph)){
//                for( AbstractPort abstractPort : outgoingPort.getAllSuccessors()){
//                    System.out.println("**successor " +
//                            abstractHashMap.get(abstractPort.getMyTask()).getToolName());
//                }
//
//            }

//            System.out.println("\nWill connect tasks " + sendingTask.getToolName()
//                    + " to " + receivingTask.getToolName());
//            if(sendingTask.getParent() == receivingTask.getParent() && scopeTaskGraph != null){
//                outputNode = sendingTask.addDataOutputNode();
//                inputNode = receivingTask.addDataInputNode();
//                System.out.println("In scope taskGraph : " + scopeTaskGraph);
//                scopeTaskGraph.connect(outputNode, inputNode);
//            } else {
//
//                System.out.println("Out of scope");
//            }

        }
//        addTaskGraphNodesFromIWIR(mainTask);
        return taskGraph;
    }

    private TaskGraph recordAbstractTasksAndDataLinks(AbstractTask mainTask) throws ProxyInstantiationException, TaskException {
        TaskGraph taskGraph = TaskGraphManager.createTaskGraph();
        taskGraph.setToolName(mainTask.getName());

        if (!(mainTask instanceof org.shiwa.fgi.iwir.Task) && !(mainTask instanceof BlockScope)) {
            TaskHolder taskHolder = TaskHolderFactory.getTaskHolderFactory().getTaskHolder(mainTask);
            System.out.println(taskHolder.getClass().getCanonicalName());
            taskGraph.createTask(ToolUtils.initTool(taskHolder, taskGraph.getProperties()));
        }

        dataLinks.addAll(((AbstractCompoundTask) mainTask).getDataLinks());
        abstractHashMap.put(mainTask, taskGraph);

        for (AbstractTask iwirTask : mainTask.getChildren()) {
            if (iwirTask instanceof org.shiwa.fgi.iwir.Task) {
                String type = ((org.shiwa.fgi.iwir.Task) iwirTask).getTasktype();
                Class clazz = TaskTypeToTool.getTaskFromType(type);
                if (clazz == null) {
                    clazz = InOut.class;
                    type = "InOut";
                }
                Task trianaTask = taskGraph.createTask(
                        ToolUtils.makeTool(
                                clazz, iwirTask.getName(), taskGraph.getProperties()
                        )
                );
                trianaTask.setParameter("TaskType", type);
                abstractHashMap.put(iwirTask, trianaTask);
            } else {
                if (iwirTask instanceof AbstractCompoundTask) {
                    TaskGraph innerTaskGraph = recordAbstractTasksAndDataLinks(iwirTask);
                    taskGraph.createTask(innerTaskGraph);
                }
            }
        }
        return taskGraph;
    }

    private void addTaskGraphNodesFromIWIR(AbstractTask iwirTask) throws NodeException {

        Task task = abstractHashMap.get(iwirTask);
        if (task instanceof TaskGraph) {
            TaskGraph taskGraph = (TaskGraph) task;

            AbstractCompoundTask abstractCompoundTask = (AbstractCompoundTask) iwirTask;
            List<DataLink> inputLinks = abstractCompoundTask.getDataLinks();
            for (DataLink dataLink : inputLinks) {
                Task fromTask = abstractHashMap.get(dataLink.getFromPort().getMyTask());
                Task toTask = abstractHashMap.get(dataLink.getToPort().getMyTask());
                if (fromTask == toTask.getParent()) {

                    System.out.println(toTask + " has an inputNode attached to the parent taskgraph");
                    taskGraph.addDataInputNode(toTask.addDataInputNode());
                }

                if (toTask == fromTask.getParent()) {
                    System.out.println(fromTask + " has an outputNode attached to the parent taskgraph");
                    taskGraph.addDataOutputNode(fromTask.addDataOutputNode());
                }
            }


//        for (AbstractTask iwirTask : mainTask.getChildren()) {
//            for (InputPort inputPort : iwirTask.getInputPorts()) {
//                System.out.println("\n" + inputPort.getUniqueId());
//                for (AbstractPort inputPortPredecessor : inputPort.getPredecessors()) {
//                    AbstractTask sendingIWIR = inputPortPredecessor.getMyTask();
//                    if (sendingIWIR != mainTask) {
//                        System.out.println("pre " + inputPortPredecessor.getUniqueId()
//                                + " on " + sendingIWIR.getUniqueId());
//                        Task sendingTask = abstractHashMap.get(sendingIWIR);
//                        Task receivingTask = abstractHashMap.get(iwirTask);
//
//                        if(sendingTask.getParent() == receivingTask.getParent()){
//                            try {
//                                taskGraph.connect(sendingTask.addDataOutputNode(), receivingTask.addDataInputNode());
//                            } catch (CableException e) {
//                                e.printStackTrace();
//                            }
//                        } else {
//                            System.out.println("*** More scoping issues");
//                        }
//                    }
//
//                }
////                for(AbstractPort inputPortSuccessor : inputPort.getSuccessors()){
////                    System.out.println("suc " + inputPortSuccessor.getUniqueId());
////                }
//            }
//        }


//            for (InputPort inputPort : iwirTask.getInputPorts()) {
//                System.out.println("\n" + inputPort.getUniqueId());
//
//                for (AbstractPort inputPortPredecessor : inputPort.getPredecessors()) {
//                    if (inputPortPredecessor.getMyTask() == iwirTask) {
//                        System.out.println("pre " + inputPortPredecessor.getUniqueId());
//                        taskGraph.addDataInputNode(taskGraph.addDataInputNode());
//                    }
//                }
////            for(AbstractPort inputPortSuccessor : inputPort.getSuccessors()){
////                System.out.println("suc " + inputPortSuccessor.getUniqueId());
////            }
//            }
//
//            for (OutputPort outputPort : iwirTask.getOutputPorts()) {
//                System.out.println(outputPort.getUniqueId());
////            for(AbstractPort outputPortPredecessor : outputPort.getPredecessors()){
////                System.out.println("pre " + outputPortPredecessor.getUniqueId());
////            }
//                for (AbstractPort outputPortSuccessor : outputPort.getSuccessors()) {
//                    if (outputPortSuccessor.getMyTask() == iwirTask) {
//                        System.out.println("suc " + outputPortSuccessor.getUniqueId()
//                                + " on " + outputPortSuccessor.getMyTask().getUniqueId());
//                        taskGraph.addDataOutputNode(taskGraph.addDataOutputNode());
//                    }
//                }
//            }
        }
    }
}
