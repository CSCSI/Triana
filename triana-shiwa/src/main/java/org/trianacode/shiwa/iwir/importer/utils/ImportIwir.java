package org.trianacode.shiwa.iwir.importer.utils;

import org.shiwa.desktop.data.transfer.FGIWorkflowReader;
import org.shiwa.fgi.iwir.*;
import org.trianacode.shiwa.iwir.factory.TaskHolderFactory;
import org.trianacode.taskgraph.*;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.proxy.ProxyInstantiationException;
import org.trianacode.taskgraph.tool.Tool;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 24/10/2011
 * Time: 14:49
 * To change this template use File | Settings | File Templates.
 */
public class ImportIwir {
    private HashMap<AbstractTask, Task> abstractHashMap = new HashMap<AbstractTask, Task>();
    private HashSet<DataLink> dataLinks = new HashSet<DataLink>();

    boolean std = false;
    private FGIWorkflowReader fgiWorkflowReader = null;

    private void stdOut(String string){
        if(std){
            System.out.printf(string);
        }
    }

    private void initFGIWorkflowReader(File fgiBundleFile) throws JAXBException, IOException {
        if(fgiBundleFile != null){
            System.out.println("fgiBundleFile = "
                    + fgiBundleFile.exists() + " "
                    + fgiBundleFile.getAbsolutePath());
            fgiWorkflowReader = new FGIWorkflowReader(fgiBundleFile);
        } else {
            System.out.println("No fgi bundle returned, " +
                    "best effort triana-only tools will be used");
        }
    }

    public TaskGraph taskFromIwir(IWIR iwir, File fgiBundle) throws TaskException, ProxyInstantiationException, CableException, JAXBException, IOException {

        initFGIWorkflowReader(fgiBundle);

        AbstractTask mainTask = iwir.getTask();
        TaskGraph taskGraph = TaskGraphManager.createTaskGraph();
        recordAbstractTasksAndDataLinks(mainTask, taskGraph);
        stdOut(taskGraph.toString());

//        stdOut("Abstract Tasks " + abstractHashMap.toString());
//        stdOut("DataLinks " + ArrayUtils.toString(dataLinks.toArray()));


        for (DataLink dataLink : dataLinks) {
//            stdOut("\nLink from " + dataLink.getFromPort() + " to " + dataLink.getToPort());
            AbstractPort outgoingPort = dataLink.getFromPort();
            AbstractPort incomingPort = dataLink.getToPort();

            stdOut(outgoingPort.getPredecessors().toString());
            stdOut(outgoingPort.getAllSuccessors().toString());

            stdOut(incomingPort.getPredecessors().toString());
            stdOut(incomingPort.getAllSuccessors().toString());

            AbstractTask sendingAbstract = outgoingPort.getMyTask();
            AbstractTask receivingAbstract = incomingPort.getMyTask();

            Task sendingTask = abstractHashMap.get(sendingAbstract);
            Task receivingTask = abstractHashMap.get(receivingAbstract);

//            stdOut("Will connect " + sendingTask + " to " + receivingTask);


            if (sendingTask == receivingTask.getParent()) {

                Node graphNode = ((TaskGraph) sendingTask).addDataInputNode(receivingTask.addDataInputNode());
                inputChain(outgoingPort, graphNode);
            }

            if (receivingTask == sendingTask.getParent()) {
                Node graphNode = ((TaskGraph) receivingTask).addDataOutputNode(sendingTask.addDataOutputNode());
                outputChain(incomingPort, graphNode);
            }

            //check both are atomic tasks
            if (sendingAbstract instanceof org.shiwa.fgi.iwir.Task
                    && receivingAbstract instanceof org.shiwa.fgi.iwir.Task) {

                if (sendingTask.getParent() == receivingTask.getParent()) {
                    TaskGraph scopeTaskgraph = sendingTask.getParent();
                    stdOut("Connecting "
                            + sendingTask.getQualifiedToolName() + " to "
                            + receivingTask.getQualifiedToolName() + " in "
                            + scopeTaskgraph.getQualifiedToolName()
                    );
                    scopeTaskgraph.connect(sendingTask.addDataOutputNode(), receivingTask.addDataInputNode());
                }
            }
        }
        return taskGraph;
    }

    private void inputChain(AbstractPort outgoingPort, Node inputNode) {
        try {
            for (DataLink dataLink : dataLinks) {
                if (dataLink.getToPort() == outgoingPort) {
                    //TODO
                    Task scopedTask = abstractHashMap.get(dataLink.getFromPort().getMyTask());
                    if (!(scopedTask instanceof TaskGraph)) {
                        Node outputNode = scopedTask.addDataOutputNode();
                        scopedTask.getParent().connect(outputNode, inputNode);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void outputChain(AbstractPort iwirReceivingPort, Node graphOutputNode) throws NodeException, CableException {
        for (DataLink dataLink : dataLinks) {
            if (dataLink.getFromPort() == iwirReceivingPort) {
                //TODO
                Task scopedTask = abstractHashMap.get(dataLink.getToPort().getMyTask());
                if (!(scopedTask instanceof TaskGraph)) {
                    Node inputNode = scopedTask.addDataInputNode();
                    scopedTask.getParent().connect(graphOutputNode, inputNode);
                }
            }
        }
    }


    private Task createFromIWIRTask(AbstractTask iwirTask, TaskGraph tg) throws TaskException, JAXBException, IOException {
        String taskType = ((org.shiwa.fgi.iwir.Task) iwirTask).getTasktype();

        //if the iwirTask is a standard IWIR Atomic Task, try to find and/or make a tool for it.
        //this requires the taskType string, the name and the taskgraph properties

        Tool newTask = TaskTypeToTool.getToolFromType(
                (org.shiwa.fgi.iwir.Task) iwirTask, fgiWorkflowReader, tg.getProperties());

        Task trianaTask = tg.createTask(newTask);

        //add the iwir property strings to the triana task
        for (Property property : iwirTask.getProperties()) {
            trianaTask.setParameter(property.getName(), property.getValue());
            trianaTask.setParameterType(property.getName(), Tool.USER_ACCESSIBLE);
        }


        return trianaTask;
    }


    private TaskGraph recordAbstractTasksAndDataLinks(AbstractTask mainTask, TaskGraph tg) throws ProxyInstantiationException, TaskException, JAXBException, IOException {
//        TaskGraph taskGraph = TaskGraphManager.createTaskGraph();
//        taskGraph.setToolName(mainTask.getName());
        tg.setToolName(mainTask.getName());

        if(mainTask instanceof org.shiwa.fgi.iwir.Task){
            abstractHashMap.put(mainTask, tg);
//            Task task = TaskHolderFactory.getTaskHolderFactory().addTaskHolder(mainTask, tg);

            Task task = createFromIWIRTask(mainTask, tg);

            for (AbstractPort port : mainTask.getAllInputPorts()) {
                if (port instanceof InputPort) {
                    tg.addDataInputNode(task.addDataInputNode());
                }
                if (port instanceof LoopPort) {
                    task.addParameterInputNode("loop");
                }
            }
            for (AbstractPort port : mainTask.getAllOutputPorts()) {
                if (port instanceof OutputPort) {
                    tg.addDataOutputNode(task.addDataOutputNode());
                }
                if (port instanceof LoopPort) {
                    task.addParameterOutputNode("loop");
                }
            }
        } else {

            dataLinks.addAll(((AbstractCompoundTask) mainTask).getDataLinks());
            abstractHashMap.put(mainTask, tg);

            if (!(mainTask instanceof org.shiwa.fgi.iwir.Task) && !(mainTask instanceof BlockScope)) {
                Task controlTask = TaskHolderFactory.getTaskHolderFactory().addTaskHolder(mainTask, tg);
//            stdOut(taskHolder.getClass().getCanonicalName());

//                Task controlTask = tg.createTask(ToolUtils.initTool(taskHolder, tg.getProperties()));
//            taskGraph.createTask(ToolUtils.initTool(taskHolder, taskGraph.getProperties()));
                abstractHashMap.put(mainTask, controlTask);

                for (AbstractPort port : mainTask.getAllInputPorts()) {
                    if (port instanceof InputPort) {
                        tg.addDataInputNode(controlTask.addDataInputNode());
                    }
                    if (port instanceof LoopPort) {
                        controlTask.addParameterInputNode("loop");
                    }
                }
                for (AbstractPort port : mainTask.getAllOutputPorts()) {
                    if (port instanceof OutputPort) {
                        tg.addDataOutputNode(controlTask.addDataOutputNode());
                    }
                    if (port instanceof LoopPort) {
                        controlTask.addParameterOutputNode("loop");
                    }
                }
            }


            for (AbstractTask iwirTask : mainTask.getChildren()) {
                if (iwirTask instanceof org.shiwa.fgi.iwir.Task) {
//                    String taskType = ((org.shiwa.fgi.iwir.Task) iwirTask).getTasktype();
//
//                    //if the iwirTask is a standard IWIR Atomic Task, try to find and/or make a tool for it.
//                    //this requires the taskType string, the name and the taskgraph properties
//
//                    Tool newTask = TaskTypeToTool.getToolFromType(
//                            (org.shiwa.fgi.iwir.Task) iwirTask, fgiWorkflowReader, tg.getProperties());
//
//                    Task trianaTask = tg.createTask(newTask);
//                    trianaTask.setToolName(iwirTask.getName());
//                    trianaTask.setParameter(Executable.TASKTYPE, taskType);
//
//                    //add the iwir property strings to the triana task
//                    for (Property property : iwirTask.getProperties()) {
//                        trianaTask.setParameter(property.getName(), property.getValue());
//                        trianaTask.setParameterType(property.getName(), Tool.USER_ACCESSIBLE);
//                    }
                    Task trianaTask = createFromIWIRTask(iwirTask, tg);
                    abstractHashMap.put(iwirTask, trianaTask);
                } else {
                    if (iwirTask instanceof AbstractCompoundTask) {

                        TaskGraph innerTaskGraph = TaskGraphManager.createTaskGraph();
                        TaskGraph concreteTaskGraph = (TaskGraph) tg.createTask(innerTaskGraph);
                        recordAbstractTasksAndDataLinks(iwirTask, concreteTaskGraph);

//                    TaskGraph innerTaskGraph = recordAbstractTasksAndDataLinks(iwirTask);
//                    TaskGraph concreteTaskGraph = (TaskGraph) taskGraph.createTask(innerTaskGraph);
                    }
                }
            }
        }
        return tg;
    }


    //top level connections

//            if (sendingTask == taskGraph && receivingAbstract instanceof org.shiwa.fgi.iwir.Task) {
//                if (receivingTask.getParent() == sendingTask) {
//                    stdOut("Connecting " + receivingTask + " to the parent graph");
//                    taskGraph.addDataInputNode(receivingTask.addDataInputNode());
//                }
//            }
//
//            if (receivingTask == taskGraph && sendingAbstract instanceof org.shiwa.fgi.iwir.Task) {
//                if (sendingTask.getParent() == taskGraph) {
//                    stdOut("Connecting " + sendingTask + " to the parent graph");
//                    taskGraph.addDataOutputNode(sendingTask.addDataOutputNode());
//                }
//            }
//            Node outputNode;
//            Node inputNode;
//            TaskGraph scopeTaskGraph = sendingTask.getParent();


    //TODO Input chain
//            if(!(receivingTask instanceof TaskGraph)){
//                for (AbstractPort abstractPort : incomingPort.getPredecessors()){
//                    stdOut("**predecessors " +
//                            abstractHashMap.get(abstractPort.getMyTask()).getToolName());
//                    if(abstractPort.getPredecessors().size() != 0){
//                        List<AbstractPort> ports = abstractPort.getPredecessors();
//                        for(AbstractPort port : ports){
//                            stdOut(abstractHashMap.get(port.getMyTask()));
//                        }
//                    }
//
//
//                    Node scopeNode = receivingTask.addDataInputNode();
//                    stdOut("Tasks input node " + scopeNode.getName());
//                    Task topLevelTask;
//                    List<AbstractPort> abstractPorts = incomingPort.getPredecessors();
//                    while(abstractPorts.size() > 0){
//                        for(AbstractPort port : abstractPorts){
//                            topLevelTask = abstractHashMap.get(port.getMyTask());
//                            stdOut("pre " + topLevelTask.getToolName());
//
//                            if(topLevelTask instanceof TaskGraph){
//                                TaskGraph scopeGraph = (TaskGraph)topLevelTask;
//                                stdOut("node " + scopeNode.getName());
//                                stdOut("in graph " + scopeGraph.getToolName());
//                                Node newNode = scopeGraph.addDataInputNode(scopeNode);
//                                stdOut("new node " + newNode.getName());
//                                scopeNode = newNode;
//                            }
//                        }
//                        stdOut(abstractPorts.get(0).getPredecessors().size());
//                        abstractPorts = abstractPorts.get(0).getPredecessors();
//                    }
//
//                }
//
//            }


//            //TODO Output chain
//            if(!(sendingTask instanceof TaskGraph)){
//                for( AbstractPort abstractPort : outgoingPort.getAllSuccessors()){
//                    stdOut("**successor " +
//                            abstractHashMap.get(abstractPort.getMyTask()).getToolName());
//                }
//
//            }

//            stdOut("\nWill connect tasks " + sendingTask.getToolName()
//                    + " to " + receivingTask.getToolName());
//            if(sendingTask.getParent() == receivingTask.getParent() && scopeTaskGraph != null){
//                outputNode = sendingTask.addDataOutputNode();
//                inputNode = receivingTask.addDataInputNode();
//                stdOut("In scope taskGraph : " + scopeTaskGraph);
//                scopeTaskGraph.connect(outputNode, inputNode);
//            } else {
//
//                stdOut("Out of scope");
//            }
//
//        }
////        addTaskGraphNodesFromIWIR(mainTask);
//        return taskGraph;
//    }
}
