package org.trianacode.shiwa.iwir.importer.utils;

import org.apache.commons.lang.ArrayUtils;
import org.shiwa.fgi.iwir.*;
import org.trianacode.enactment.AddonUtils;
import org.trianacode.shiwa.iwir.factory.TaskHolder;
import org.trianacode.shiwa.iwir.factory.TaskHolderFactory;
import org.trianacode.shiwa.test.InOut;
import org.trianacode.taskgraph.*;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.proxy.ProxyInstantiationException;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 24/10/2011
 * Time: 14:49
 * To change this template use File | Settings | File Templates.
 */
public class ImportIwir {
    private HashMap<AbstractTask, Task> abstractHashMap = new HashMap<AbstractTask, Task>();
    private HashSet<DataLink> dataLinks = new HashSet<DataLink>();

    public TaskGraph taskFromIwir(IWIR iwir) throws TaskException, ProxyInstantiationException, CableException {

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
                        AddonUtils.makeTool(
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
}
