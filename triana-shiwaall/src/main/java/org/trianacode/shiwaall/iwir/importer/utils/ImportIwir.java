package org.trianacode.shiwaall.iwir.importer.utils;

import org.shiwa.desktop.data.transfer.FGIWorkflowReader;
import org.shiwa.fgi.iwir.*;
import org.trianacode.TrianaInstance;
import org.trianacode.enactment.AddonUtils;
import org.trianacode.shiwaall.iwir.execute.Executable;
import org.trianacode.shiwaall.iwir.factory.TaskHolderFactory;
import org.trianacode.shiwaall.test.InOut;
import org.trianacode.taskgraph.*;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.proxy.ProxyInstantiationException;
import org.trianacode.taskgraph.tool.Tool;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

// TODO: Auto-generated Javadoc
/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 24/10/2011
 * Time: 14:49
 * To change this template use File | Settings | File Templates.
 */
public class ImportIwir {

    /** The abstract hash map. */
    private HashMap<AbstractTask, Task> abstractHashMap = new HashMap<AbstractTask, Task>();

    /** The data links. */
    private HashSet<DataLink> dataLinks = new HashSet<DataLink>();

    /** The std. */
    boolean std = false;

    /** The fgi workflow reader. */
    private FGIWorkflowReader fgiWorkflowReader = null;

    /** The Constant IWIR_NODE. */
    public static final String IWIR_NODE = "iwirNode";

    /**
     * Std out.
     *
     * @param string the string
     */
    private void stdOut(String string){
        if(std){
            System.out.println(string);
        }
    }

    /**
     * The main method.
     *
     * @param args the arguments
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws CableException the cable exception
     * @throws JAXBException the jAXB exception
     * @throws ProxyInstantiationException the proxy instantiation exception
     * @throws TaskException the task exception
     */
    public static void main(String[] args) throws IOException, CableException, JAXBException, ProxyInstantiationException, TaskException {
        ImportIwir importIwir = new ImportIwir();

        IWIR iwir = new IWIR(new File("/Users/ian/Downloads/fgibundle/workflow.xml.iwir"));

        TrianaInstance engine = new TrianaInstance(args);
        engine.init();
        TaskGraph taskGraph = importIwir.taskFromIwir(iwir, null);
        System.exit(0);
    }

    /**
     * sets the fgiWorkflowReader for this bundle.
     *
     * @param fgiBundleFile the fgi bundle file
     * @throws JAXBException the jAXB exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
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

    /**
     * Task from iwir.
     *
     * @param iwir the iwir
     * @param fgiBundle the fgi bundle
     * @return the task graph
     * @throws TaskException the task exception
     * @throws ProxyInstantiationException the proxy instantiation exception
     * @throws CableException the cable exception
     * @throws JAXBException the jAXB exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public TaskGraph taskFromIwir(IWIR iwir, File fgiBundle) throws TaskException, ProxyInstantiationException, CableException, JAXBException, IOException {

        initFGIWorkflowReader(fgiBundle);

        //create a new taskgraph for the workflow
        AbstractTask mainTask = iwir.getTask();
        TaskGraph taskGraph = TaskGraphManager.createTaskGraph();

        //recurse the iwir structure, add to taskgraph
        recordAbstractTasksAndDataLinks(mainTask, taskGraph);
        stdOut(taskGraph.toString());


        //The following block of code looks for root IWIR ports which
        //map to more than one task. Triana likes one 2 one mappings, IWIR allows one 2 many.

        // Make a copy of all the data links to iterate through
        ArrayList<DataLink> copyOfDataLinks = new ArrayList<DataLink>();
        copyOfDataLinks.addAll(dataLinks);

        //for matching ports to new "splitting" tasks
        HashMap<AbstractPort, Task> alternativeNodes = new HashMap<AbstractPort, Task>();

        // map of the root input port to the multiple datalink it has connected to it
        HashMap<AbstractPort, ArrayList<DataLink>> collisions = new HashMap<AbstractPort, ArrayList<DataLink>>();

        //iterate through datalinks
        for (DataLink dataLink : dataLinks) {

            //check to see if ports task is the root taskgraph.
            if(abstractHashMap.get(dataLink.getFromPort().getMyTask()) == taskGraph
                    && !collisions.containsKey(dataLink.getFromPort())
                    ){

                AbstractPort sendingPort = dataLink.getFromPort();
                ArrayList<DataLink> collidingLinks = new ArrayList<DataLink>();


                //iterate through the copy of the links
                for(DataLink anotherDataLink : copyOfDataLinks) {


                    //if the link is not itself && they have the same "from" port, its a collision
                    if(
                            dataLink != anotherDataLink &&
                            dataLink.getFrom().equals(anotherDataLink.getFrom())){

                        System.out.println("####### port collision, " + dataLink.getFrom()
                                + " : " + dataLink.getTo()
                                + " : " + anotherDataLink.getTo()
                        );

                        //build up a list of colliding datalinks from this port
                        collidingLinks.add(anotherDataLink);
                    }
                }


                //if there is more than one datalink on this port,
                // add the original link too, and store the port
                if(collidingLinks.size() > 0){
                    System.out.println("port " + sendingPort.getName() + " collides with " + collidingLinks.size());
                    collidingLinks.add(dataLink);
                    collisions.put(sendingPort, collidingLinks);
                }
            }
        }


        //go through the colliding ports
        for(AbstractPort abstractPort : collisions.keySet()){

            Task scopedTaskgraph = abstractHashMap.get(abstractPort.getMyTask());
            if( scopedTaskgraph instanceof TaskGraph){
                System.out.println("Will create now splitting task in " + taskGraph.getToolName());

                //create the splitting tool which passes one input to multiple receiveing nodes
                Tool tool = AddonUtils.makeTool(InOut.class, abstractPort.getUniqueId(), scopedTaskgraph.getProperties());

                Task task = taskGraph.createTask(tool);

//                taskGraph.connect(task.addDataOutputNode(), abstractHashMap.get(abstractPort.getMyTask()).addDataInputNode());

                //add connection from splitting node to each new port.
                for(DataLink anotherDataLink : collisions.get(abstractPort)){

                    Task receivingTask = abstractHashMap.get(anotherDataLink.getToPort().getMyTask());
                    Node receivingNode = receivingTask.addDataInputNode();

                    taskGraph.connect(task.addDataOutputNode(), receivingNode);

                    //Record the links for data matching up later
                    if(receivingTask.isParameterName(Executable.EXECUTABLE)){
                        Executable executable = (Executable) receivingTask.getParameter(Executable.EXECUTABLE);
                        executable.addPort(receivingNode.getTopLevelNode().getName(), anotherDataLink.getToPort().getName());
                        receivingTask.setParameter(Executable.EXECUTABLE, executable);
                    }

                }

                //for future reference, this port now maps to one specific task
                alternativeNodes.put(abstractPort, task);
            }

        }


        //continue with standard cable creation.

        HashSet<AbstractPort> toIgnore = new HashSet<AbstractPort>();

        for (DataLink dataLink : dataLinks) {
            if(!toIgnore.contains(dataLink.getFromPort())){

                stdOut("\nLink from " + dataLink.getFromPort() +
                        " (" + dataLink.getFromPort().getType().toString() + ") " +
                        " to " + dataLink.getToPort() +
                        " (" + dataLink.getToPort().getType().toString() + ")"
                );

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

                if(alternativeNodes.keySet().contains(outgoingPort)){
                    receivingTask = alternativeNodes.get(outgoingPort);
                    toIgnore.add(outgoingPort);
                }

//            stdOut("Will connect " + sendingTask + " to " + receivingTask);


                if (sendingTask == receivingTask.getParent()) {
                    Node receivingNode = receivingTask.addDataInputNode();
                    Node graphNode = ((TaskGraph) sendingTask).addDataInputNode(receivingNode);
                    inputChain(outgoingPort, graphNode);

                    TaskGraph parentGraph = ((TaskGraph)sendingTask);
                    parentGraph.setParameter(outgoingPort.getName(), graphNode.getAbsoluteNodeIndex());
                    System.out.println("fromPort " + outgoingPort.getName()
                            + " graphPort " + graphNode.getName()
                            + " index " + graphNode.getAbsoluteNodeIndex()
                    );

                    if(receivingTask.isParameterName(Executable.EXECUTABLE)){
                        Executable executable = (Executable) receivingTask.getParameter(Executable.EXECUTABLE);
                        executable.addPort(receivingNode.getTopLevelNode().getName(), incomingPort.getName());
                        receivingTask.setParameter(Executable.EXECUTABLE, executable);
                    }
                }

                if (receivingTask == sendingTask.getParent()) {
                    Node sendingNode = sendingTask.addDataOutputNode();
                    Node graphNode = ((TaskGraph) receivingTask).addDataOutputNode(sendingNode);
                    outputChain(incomingPort, graphNode);

                    if(sendingTask.isParameterName(Executable.EXECUTABLE)){
                        Executable executable = (Executable) sendingTask.getParameter(Executable.EXECUTABLE);
                        executable.addPort(sendingNode.getTopLevelNode().getName(), outgoingPort.getName());
                        sendingTask.setParameter(Executable.EXECUTABLE, executable);
                    }
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
                        Node sendingNode = sendingTask.addDataOutputNode();
                        Node receivingNode = receivingTask.addDataInputNode();
                        scopeTaskgraph.connect(sendingNode, receivingNode);

                        if(sendingTask.isParameterName(Executable.EXECUTABLE)){
                            Executable executable = (Executable) sendingTask.getParameter(Executable.EXECUTABLE);
                            executable.addPort(sendingNode.getTopLevelNode().getName(), outgoingPort.getName());
                            sendingTask.setParameter(Executable.EXECUTABLE, executable);
                        }
                        if(receivingTask.isParameterName(Executable.EXECUTABLE)){
                            Executable executable = (Executable) receivingTask.getParameter(Executable.EXECUTABLE);
                            executable.addPort(receivingNode.getTopLevelNode().getName(), incomingPort.getName());
                            receivingTask.setParameter(Executable.EXECUTABLE, executable);
                        }
                    }
                }
            }
        }
        return taskGraph;
    }

    /**
     * Input chain.
     *
     * @param outgoingPort the outgoing port
     * @param inputNode the input node
     */
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

    /**
     * Output chain.
     *
     * @param iwirReceivingPort the iwir receiving port
     * @param graphOutputNode the graph output node
     * @throws NodeException the node exception
     * @throws CableException the cable exception
     */
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


    /**
     * Creates the from iwir task.
     *
     * @param iwirTask the iwir task
     * @param tg the tg
     * @return the task
     * @throws TaskException the task exception
     * @throws JAXBException the jAXB exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ProxyInstantiationException the proxy instantiation exception
     */
    private Task createFromIWIRTask(AbstractTask iwirTask, TaskGraph tg) throws TaskException, JAXBException, IOException, ProxyInstantiationException {

        //if the iwirTask is a standard IWIR Atomic Task, try to find and/or make a tool for it.
        //this requires the taskType string, the name and the taskgraph properties

        System.out.println("Making " + iwirTask.getUniqueId());

        Task trianaTask = TaskTypeRepo.getTaskFromType(
                (org.shiwa.fgi.iwir.Task) iwirTask, fgiWorkflowReader, tg, true);
//        Tool newTask = TaskTypeRepo.getToolFromType(
//                (org.shiwa.fgi.iwir.Task) iwirTask, tg.getProperties());

        System.out.println();

        //add the iwir property strings to the triana task
        for (Property property : iwirTask.getProperties()) {
            trianaTask.setParameter(property.getName(), property.getValue());
            trianaTask.setParameterType(property.getName(), Tool.USER_ACCESSIBLE);
        }

        return trianaTask;
    }

    /**
     * Adds the nodes.
     *
     * @param mainTask the main task
     * @param task the task
     * @param tg the tg
     * @throws NodeException the node exception
     */
    private void addNodes(AbstractTask mainTask, Task task, TaskGraph tg) throws NodeException {
        Executable executable = null;
        if(task.isParameterName(Executable.EXECUTABLE)){
            executable = (Executable) task.getParameter(Executable.EXECUTABLE);
        }
        for (AbstractPort port : mainTask.getAllInputPorts()) {
            System.out.println("Input port " + port.getName() + " " + port.getClass().getCanonicalName());
            Node newNode = null;
            if (port instanceof InputPort) {
                newNode = tg.addDataInputNode(task.addDataInputNode());
            }
            if (port instanceof LoopPort || port instanceof LoopElement) {
//                newNode = task.addParameterInputNode("loop");
                newNode = tg.addDataInputNode(task.addDataInputNode());
                stdOut("Loop port found " + port.getName());
            }
            if(newNode != null && executable != null){
                executable.addPort(newNode.getTopLevelNode().getName(), port.getName());
            }

        }
        for (AbstractPort port : mainTask.getAllOutputPorts()) {
            Node newNode = null;
            if (port instanceof OutputPort) {
                newNode = tg.addDataOutputNode(task.addDataOutputNode());
            }
            if (port instanceof LoopPort) {
                newNode = task.addParameterOutputNode("loop");
            }
            if(newNode != null && executable != null){
                executable.addPort(newNode.getTopLevelNode().getName(), port.getName());
            }
        }
        if(executable != null){
            task.setParameter(Executable.EXECUTABLE, executable);
        }
    }

    /**
     * Record abstract tasks and data links.
     *
     * @param mainTask the main task
     * @param tg the tg
     * @return the task graph
     * @throws ProxyInstantiationException the proxy instantiation exception
     * @throws TaskException the task exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws JAXBException the jAXB exception
     */
    private TaskGraph recordAbstractTasksAndDataLinks(AbstractTask mainTask, TaskGraph tg) throws ProxyInstantiationException, TaskException, IOException, JAXBException {
//        TaskGraph taskGraph = TaskGraphManager.createTaskGraph();
//        taskGraph.setToolName(mainTask.getName());
        tg.setToolName(mainTask.getName());

        //If there is only one task in the workflow, the mainTask with be a Task (ie not Compound)

        if(mainTask instanceof org.shiwa.fgi.iwir.Task) {
            abstractHashMap.put(mainTask, tg);

            //create a new Triana Task from the IWIR info, and add to taskgraph
            Task task = createFromIWIRTask(mainTask, tg);

            addNodes(mainTask, task, tg);

        } else {

            dataLinks.addAll(((AbstractCompoundTask) mainTask).getDataLinks());
            abstractHashMap.put(mainTask, tg);

            if (!(mainTask instanceof org.shiwa.fgi.iwir.Task) && !(mainTask instanceof BlockScope)) {
                Task controlTask = TaskHolderFactory.getTaskHolderFactory().addTaskHolder(mainTask, tg);

//                Task controlTask = tg.createTask(ToolUtils.initTool(taskHolder, tg.getProperties()));
//            taskGraph.createTask(ToolUtils.initTool(taskHolder, taskGraph.getProperties()));
                abstractHashMap.put(mainTask, controlTask);

                addNodes(mainTask, controlTask, tg);
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
