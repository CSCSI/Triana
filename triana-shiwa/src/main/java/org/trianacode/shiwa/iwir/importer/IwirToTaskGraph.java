package org.trianacode.shiwa.iwir.importer;

import org.apache.commons.logging.Log;
import org.shiwa.fgi.iwir.*;
import org.trianacode.config.TrianaProperties;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.gui.main.organize.DaxOrganize;
import org.trianacode.shiwa.iwir.exporter.NodePortTranslator;
import org.trianacode.shiwa.iwir.exporter.NodeProxy;
import org.trianacode.shiwa.iwir.factory.TaskHolder;
import org.trianacode.shiwa.iwir.factory.TaskHolderFactory;
import org.trianacode.taskgraph.*;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.imp.ToolImp;
import org.trianacode.taskgraph.proxy.java.JavaProxy;
import org.trianacode.taskgraph.service.RunnableTaskFactory;
import org.trianacode.taskgraph.tool.Tool;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 24/10/2011
 * Time: 14:43
 * To change this template use File | Settings | File Templates.
 */
public class IwirToTaskGraph {

    private TrianaProperties trianaProperties;
    List<Task> allTrianaTasks;
    List<AbstractTask> allAbstractTasks;
    Map<String, AbstractTask> abstractTaskMap;
    NodePortTranslator nodePortTranslator;
    private static Log devLog = Loggers.DEV_LOGGER;


    public TaskGraph importIWIR(File file, TrianaProperties properties) {
        this.trianaProperties = properties;
        allTrianaTasks = new ArrayList<Task>();
        abstractTaskMap = new HashMap<String, AbstractTask>();
        nodePortTranslator = new NodePortTranslator();
        allAbstractTasks = new ArrayList<AbstractTask>();

        IWIR iwir;
        try {
            iwir = new IWIR(file);
        } catch (FileNotFoundException e) {
            devLog.debug("Failed to load IWIR from file");
            return null;
        }

        AbstractTask rootTask = iwir.getTask();
        TaskGraph taskGraph = createTaskGraph(iwir.getWfname());
        recurseAbstractTasks(taskGraph, rootTask, null);

        attachCables(allAbstractTasks);

        DaxOrganize daxOrganize = new DaxOrganize(taskGraph);
        return taskGraph;
    }

    private List<AbstractTask> recurseAbstractTasks(TaskGraph taskGraph, AbstractTask rootTask, Task controlTask) {
        List<AbstractTask> tasks = new ArrayList<AbstractTask>();
        tasks.add(rootTask);

        if (rootTask.getChildren().size() > 0) {
            devLog.debug("\nRoot task " +
                    rootTask.getUniqueId() +
                    " has " +
                    rootTask.getChildren().size() +
                    " children.");

            //          TaskGraph innerTaskGraph = createTaskGraph(rootTask.getName());
            if (AbstractCompoundTask.class.isAssignableFrom(rootTask.getClass())) {// && !BlockScope.class.isAssignableFrom(rootTask.getClass())) {
                devLog.debug("Root task : " + rootTask.getUniqueId() + " is loopy.");

                Task innerControlTask = addTaskHolderToTaskgraph(taskGraph, rootTask, true);
                connectChildToControlTask(controlTask, innerControlTask, taskGraph);
                connectControlTask(rootTask, taskGraph, innerControlTask);

            } else {
                for (AbstractTask task : rootTask.getChildren()) {
                    recurseAbstractTasks(taskGraph, task, null);
                }
            }

//           addTaskgraph(taskGraph, innerTaskGraph);
        } else {
            Task addedTask = addTaskHolderToTaskgraph(taskGraph, rootTask, false);
            connectChildToControlTask(controlTask, addedTask, taskGraph);
        }
        allAbstractTasks.addAll(tasks);
        return tasks;
    }

    private void connectControlTask(AbstractTask rootTask, TaskGraph innerTaskGraph, Task innerControlTask) {
        for (AbstractTask task : rootTask.getChildren()) {
            recurseAbstractTasks(innerTaskGraph, task, innerControlTask);
        }
        devLog.debug("Control task with abstract : " + rootTask.getUniqueId());

        if (rootTask instanceof ParallelForEachTask) {

        }
        if (rootTask instanceof IfTask) {
            IfTask ifTask = (IfTask) rootTask;
            for (AbstractLink link : ifTask.getDataLinks()) {
                try {
                    attachCableFromAbstractLink(link);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void connectChildToControlTask(Task control, Task child, TaskGraph taskGraph) {
        try {
            taskGraph.connect(control.addDataOutputNode(), child.addDataInputNode());
            taskGraph.connect(child.addDataOutputNode(), control.addDataInputNode());
        } catch (Exception e) {
        }
    }

    private Cable attachCableFromAbstractLink(AbstractLink link) throws NodeException, CableException {
        Cable cable = null;
        String from = link.getFrom();
        String to = link.getTo();
        if (DataLink.class.isAssignableFrom(link.getClass())) {
            AbstractPort fromPort = ((DataLink) link).getFromPort();
            AbstractPort toPort = ((DataLink) link).getToPort();
            devLog.debug("\nIWIR Connecting from : " + from + " To : " + to);

            Node inputNode = nodePortTranslator.getNodeForAbstractPort(toPort.getUniqueId());
            Node outputNode = nodePortTranslator.getNodeForAbstractPort(fromPort.getUniqueId());

            if (inputNode != null && outputNode != null) {
                devLog.debug("TRIANA Connecting from : " + outputNode.getName() + " To : " + inputNode.getName());

                if (inputNode.isInputNode() && outputNode.isOutputNode()) {

                    if (inScope(outputNode, inputNode)) {
                        devLog.debug("    TaskGraph in scope is : " + getNodesTaskGraph(outputNode).getToolName());


                        //TODO is this reasonable?
                        cable = connectCable(getNodesTaskGraph(outputNode), outputNode, inputNode);

                    } else {
                        //TODO this is an issue.. Fairly random if this pans out.
                        //                attachOutOfScopeNodes(outputNode, inputNode);

                    }
                } else {
                    //      correctNodeType(outputNode, inputNode);
                }
            } else {
//                if (inputNode == null && outputNode != null) {
//                    devLog.debug(toPort.getUniqueId() + " needs to be created");
//                    getNodesTaskGraph(outputNode).addDataOutputNode(outputNode);
//                    devLog.debug("Taskgraph output node added");
//                }
//
//                if (outputNode == null && inputNode != null) {
//                    devLog.debug(fromPort.getUniqueId() + " needs to be created");
//                    getNodesTaskGraph(inputNode).addDataInputNode(inputNode);
//                    devLog.debug("Taskgraph input node added");
//                }
            }

        }
        return cable;
    }

    private void correctNodeType(Node outputNode, Node inputNode) throws NodeException {
        devLog.debug("Error : An output is supposed to connect to an input!");
        if (inputNode.isOutputNode()) {
            devLog.debug("Input node is an output node");
            Node newInputNode = inputNode.getTask().addDataInputNode();
            if (inScope(outputNode, newInputNode)) {
                connectCable(getNodesTaskGraph(newInputNode), outputNode, newInputNode);
            } else {
                attachOutOfScopeNodes(outputNode, newInputNode);
                devLog.debug("New inputNode is not in scope of output node");
            }

        }
        if (outputNode.isInputNode()) {
            devLog.debug("Output node is an input node");
            Node newOutputNode = outputNode.getTask().addDataOutputNode();
            if (inScope(newOutputNode, inputNode)) {
                connectCable(getNodesTaskGraph(newOutputNode), newOutputNode, inputNode);
            } else {
                devLog.debug("New outputNode is not in scope of input node");
                attachOutOfScopeNodes(newOutputNode, inputNode);
            }
            //                     outputNode.getTask().removeDataInputNode(outputNode);
        }
    }


    private TaskGraph getNodesTaskGraph(Node node) {
        return node.getTask().getParent();
    }

    private boolean inScope(Node outputNode, Node inputNode) {
        return getNodesTaskGraph(outputNode) == getNodesTaskGraph(inputNode);
    }

    public void attachCables(List<AbstractTask> endAbstractTasks) {
        devLog.debug("*********Attaching cables");
        for (AbstractTask task : endAbstractTasks) {
            if (AbstractCompoundTask.class.isAssignableFrom(task.getClass())) {
                AbstractCompoundTask abstractCompoundTask = (AbstractCompoundTask) task;
                List<AbstractLink> links = abstractCompoundTask.getLinks();
                for (AbstractLink link : links) {
                    try {
                        attachCableFromAbstractLink(link);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }


    private Cable connectCable(TaskGraph taskGraph, Node outputNode, Node inputNode) {
        Cable cable = null;
        try {
            cable = taskGraph.connect(outputNode, inputNode);
            devLog.debug("Cable connected : " +
                    cable.isConnected() + " " +
                    cable.getSendingNode().getName() + " " +
                    cable.getReceivingNode().getName());
        } catch (Exception e) {
            devLog.debug(e.getMessage());
        }
        return cable;
    }


    private void attachOutOfScopeNodes(Node outputNode, Node inputNode) throws NodeException {
        devLog.debug("    Nodes are in different taskgraph scopes");
        TaskGraph outputScopedTaskGraph = getNodesTaskGraph(outputNode);
        TaskGraph inputScopedTaskGraph = getNodesTaskGraph(inputNode);

        if (outputScopedTaskGraph.getParent() == inputScopedTaskGraph) {
            devLog.debug("output nodes taskgraph is inside input nodes taskgraph. ");
            Node scopeOutputNode = outputScopedTaskGraph.addDataOutputNode(outputNode);
            //          connectCable(outputScopedTaskGraph, outputNode, scopeOutputNode);
//            connectCable(inputScopedTaskGraph, scopeOutputNode, inputNode);

        }
        if (inputScopedTaskGraph.getParent() == outputScopedTaskGraph) {
            devLog.debug("input nodes taskgraph is inside output nodes taskgraph.");
            Node scopeInputNode = inputScopedTaskGraph.addDataInputNode(inputNode);
            //           connectCable(inputScopedTaskGraph, scopeInputNode, inputNode);
//            connectCable(outputScopedTaskGraph, outputNode, scopeInputNode);
        }
    }


    private Task addTaskHolderToTaskgraph(TaskGraph taskGraph, AbstractTask abstractTask, boolean control) {
//        devLog.debug("\n\nChecking " + abstractTask.getUniqueId());

        if (shouldAddAbstractTask(taskGraph, abstractTask)) {
            try {
                TaskHolder taskHolder = TaskHolderFactory.getTaskHolderFactory().getTaskHolder(abstractTask);
                Tool tool = initTool(taskHolder);

//                RunnableTaskFactory runnableTaskFactory = new RunnableTaskFactory();
//                Task task = runnableTaskFactory.createTask(tool, taskGraph, true);
//                taskGraph.createTask(task, true);
                Task task = taskGraph.createTask(tool);
                devLog.debug("Adding abstract task " + abstractTask.getUniqueId() + " to taskgraph " + taskGraph.getToolName());
                devLog.debug("Control = " + control);
                allTrianaTasks.add(task);
                abstractTaskMap.put(abstractTask.getUniqueId(), abstractTask);

                if (control) {

                    //             recordControlPorts(abstractTask, taskGraph, task);
                } else {
                    recordAbstractPorts(abstractTask, task);
                }

                return task;

            } catch (TaskException e) {
                devLog.debug("Failed to add tool to taskgraph.");
                e.printStackTrace();
            }
        } else {
            devLog.debug("Abstract task with unique id " + abstractTask.getUniqueId() + " already exists, will not duplicate.");
        }
        return null;
    }


    private void recordControlPorts(AbstractTask abstractTask, TaskGraph taskGraph, Task task) {
        devLog.debug("    Recording ports for control task : " + task.getQualifiedTaskName() + " abstract :" + abstractTask.getUniqueId());


        for (AbstractPort abstractPort : abstractTask.getInputPorts()) {
            Node inNode = null;
            try {
                inNode = taskGraph.addDataInputNode(task.addDataInputNode());
            } catch (NodeException e) {
                e.printStackTrace();
            }
            nodePortTranslator.addNodeProxy(new NodeProxy(inNode, abstractPort));
        }
        for (AbstractPort abstractPort : abstractTask.getOutputPorts()) {
            Node outNode = null;
            try {
                outNode = taskGraph.addDataOutputNode(task.addDataOutputNode());
            } catch (NodeException e) {
                e.printStackTrace();
            }
            nodePortTranslator.addNodeProxy(new NodeProxy(outNode, abstractPort));
        }
    }

    private void recordAbstractPorts(AbstractTask abstractTask, Task task) {
        devLog.debug("    Recording ports for task : " + task.getQualifiedTaskName() + " abstract :" + abstractTask.getUniqueId());
        for (AbstractPort abstractPort : abstractTask.getInputPorts()) {
            Node inNode = null;
            try {
                inNode = task.addDataInputNode();
//                devLog.debug("    Triana node : " + inNode.getName());
            } catch (NodeException e) {
                e.printStackTrace();
            }
            nodePortTranslator.addNodeProxy(new NodeProxy(inNode, abstractPort));
        }
        for (AbstractPort abstractPort : abstractTask.getOutputPorts()) {
            Node outNode = null;
            try {
                outNode = task.addDataOutputNode();
//                devLog.debug("    Triana node : " + outNode.getName());
            } catch (NodeException e) {
                e.printStackTrace();
            }
            nodePortTranslator.addNodeProxy(new NodeProxy(outNode, abstractPort));
        }
    }

    private boolean shouldAddAbstractTask(TaskGraph taskGraph, AbstractTask abstractTask) {
        return !abstractTaskMap.containsKey(abstractTask.getUniqueId());
    }

    private AbstractTask getIWIRTaskFromTrianaTool(Tool trianaTool) {
        if (trianaTool.getProxy() instanceof JavaProxy) {
            Unit unit = ((JavaProxy) trianaTool.getProxy()).getUnit();
            if (unit instanceof TaskHolder) {
                AbstractTask iwirTask = ((TaskHolder) unit).getIWIRTask();
                if (iwirTask != null) {
                    return iwirTask;
                } else {
                    devLog.debug("IWIR task is null in TaskHolder unit.");
                }
            } else {
                devLog.debug("Task isn't a toolholder : " + trianaTool.getDisplayName());
            }
        }
        return null;
    }

    private TaskGraph createTaskGraph(String name) {
        TaskGraph taskGraph = null;
        try {
            TaskGraph initTaskGraph = TaskGraphManager.createTaskGraph();
            taskGraph = (TaskGraph) TaskGraphManager.createTask(initTaskGraph, RunnableTaskFactory.DEFAULT_FACTORY_NAME, false);

            taskGraph.setToolName(name);

        } catch (TaskException e) {
            e.printStackTrace();
        }
        return taskGraph;
    }

    private void addTaskgraph(TaskGraph parent, TaskGraph child) {
        try {
//            TaskGraphOrganize.organizeTaskGraph(TaskGraphOrganize.GRAPH_ORGANIZE, parent);
            DaxOrganize daxOrganize = new DaxOrganize(child);
            parent.createTask(child);
        } catch (TaskException e) {
            devLog.debug("Failed to add inner taskgraph (group) to taskgraph.");
            e.printStackTrace();
        } catch (TaskGraphException e) {
            devLog.debug("Failed to resolve group nodes.");
            e.printStackTrace();
        }
    }

    private void resolveNodes(TaskGraph taskgraph, Task task) {
        for (Node node : task.getInputNodes()) {
            if (!node.isConnected()) {
                try {
                    taskgraph.addDataInputNode(node);
                } catch (NodeException e) {
                    e.printStackTrace();
                }
            }
        }
        for (Node node : task.getOutputNodes()) {
            if (!node.isConnected()) {
                try {
                    taskgraph.addDataOutputNode(node);
                } catch (NodeException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setNodeTypes(Tool tool) {

        String[] inputTypes = new String[tool.getDataInputNodeCount()];
        for (int nodeCount = 0; nodeCount < tool.getDataInputNodeCount(); nodeCount++) {
            inputTypes[nodeCount] = (Object.class.getCanonicalName());
        }
        tool.setDataInputTypes(inputTypes);
        for (String string : tool.getDataInputTypes()) {
            devLog.debug(string);
        }

        String[] outputTypes = new String[tool.getDataOutputNodeCount()];
        for (int nodeCount = 0; nodeCount < tool.getDataOutputNodeCount(); nodeCount++) {
            outputTypes[nodeCount] = (Object.class.getCanonicalName());
        }
        tool.setDataOutputTypes(outputTypes);
        for (String string : tool.getDataOutputTypes()) {
            devLog.debug(string);
        }
    }


    private void describeAbstractTask(AbstractTask abstractTask) {
        devLog.debug("\nIWIR abstractTask, Name : " + abstractTask.getName());
        for (AbstractPort abstractPort : abstractTask.getInputPorts()) {
            devLog.debug("Input port :" +
                    "\n     PortName : " + abstractPort.getName() +
                    "\n     UniqueID : " + abstractPort.getUniqueId()
//                    "\n     PortType : " + abstractPort.getPortType().name()
            );
        }
        for (AbstractPort abstractPort : abstractTask.getOutputPorts()) {
            devLog.debug("Output port :" +
                    "\n     PortName : " + abstractPort.getName() +
                    "\n     UniqueID : " + abstractPort.getUniqueId()
//                    "\n     PortType : " + abstractPort.getPortType().name()
            );
        }

    }

    private ToolImp initTool(TaskHolder taskHolder) {
        ToolImp tool = null;
        //       ProxyFactory.initProxyFactory();
        try {
            tool = new ToolImp(trianaProperties);

            tool.setProxy(new JavaProxy(taskHolder, taskHolder.getClass().getSimpleName(), taskHolder.getClass().getPackage().getName()));

            tool.setToolName(taskHolder.getIWIRTask().getName());
            tool.setToolPackage(taskHolder.getClass().getPackage().getName());
//            tool.setDataInputNodeCount(taskHolder.getIWIRTask().getInputPorts().size());
//            tool.setDataOutputNodeCount(taskHolder.getIWIRTask().getOutputPorts().size());
        } catch (Exception e) {
            devLog.debug("Failed to initialise tool from Unit.");
            e.printStackTrace();
        }
        return tool;
    }


//    private TaskGraph parseTool(File file) {
//        XMLReader reader;
//        Tool tool = null;
//        if (file.exists()) {
//            try {
//                BufferedReader filereader = new BufferedReader(new FileReader(file));
//                reader = new XMLReader(filereader);
//                devLog.debug("Reading tool from file : " + file.getCanonicalPath());
//                tool = reader.readComponent(GUIEnv.getApplicationFrame().getEngine().getProperties());
//
//            } catch (IOException e) {
//                devLog.debug(file + " : not found");
//            } catch (TaskGraphException e) {
//                e.printStackTrace();
//            }
//        }
//        if (tool instanceof TaskGraph) {
//            TaskGraph tg = (TaskGraph) tool;
//            DaxOrganize daxOrganize = new DaxOrganize(tg);
//            return tg;
//        } else {
//            return null;
//        }
//    }

//    private TaskGraph importUsingXSLT(File file, TrianaProperties properties) throws IOException {
//        String root = "triana-shiwa/src/main/java/org/trianacode/shiwa/xslt/iwir/";
//
//        if (file.exists() && file.canRead()) {
//            String iwirPath = file.getAbsolutePath();
//            String removeNamespacePath = root + "removeNamespace.xsl";
//            String iwirTaskgraphTransformerPath = root + "iwir.xsl";
//            String tempFileName = file.getName() + "-outputTemp.xml";
//            String taskgraphFileName = file.getName() + "-taskgraph";
//
//            File removeNamespace = new File(removeNamespacePath);
//            File iwirTaskgraphTransformer = new File(iwirTaskgraphTransformerPath);
//
//            if (removeNamespace.exists() && iwirTaskgraphTransformer.exists()) {
//
//                xsltTransformer.doTransform(iwirPath, tempFileName, removeNamespacePath);
//                devLog.debug("Stripped namespace");
//
//                xsltTransformer.doTransform(tempFileName, taskgraphFileName + ".xml", iwirTaskgraphTransformerPath);
//                devLog.debug("Created taskgraph file " + taskgraphFileName + ".xml");
//
//                return parseTool(new File(taskgraphFileName + ".xml"));
//            } else {
//                devLog.debug("Transform file not available. Attempting to use file from classloader");
//
//
//                StreamSource iwirFile = new StreamSource(file);
//                InputStream removeNamespaceTransformerInputStream = this.getClass().getResourceAsStream("/removeNamespace.xsl");
//                StreamSource removeNamespaceTransformerSource = new StreamSource(removeNamespaceTransformerInputStream);
//                InputStream transformerInputStream = this.getClass().getResourceAsStream("/iwir.xsl");
//                StreamSource transformerSource = new StreamSource(transformerInputStream);
//
//                if (removeNamespaceTransformerInputStream == null && transformerInputStream == null) {
//
//                    devLog.debug("Could not read from xslt transformer sources.");
//                } else {
//
//                    File removedNamespaceFile = File.createTempFile(taskgraphFileName + "sansNamespace", ".xml");
//                    StreamResult streamResult = new StreamResult(removeNamespacePath);
//                    xsltTransformer.doTransform(iwirFile, removeNamespaceTransformerSource, streamResult);
//                    devLog.debug("Created namespace-less file : " + removeNamespacePath);
//
//                    StreamSource removedNamespaceSource = new StreamSource(removedNamespaceFile);
//                    File taskgraphTempFile = File.createTempFile(taskgraphFileName, ".xml");
//                    StreamResult taskgraphStreamResult = new StreamResult(taskgraphTempFile);
//                    xsltTransformer.doTransform(removedNamespaceSource, transformerSource, taskgraphStreamResult);
//                    devLog.debug("Created taskgraph from iwir : " + taskgraphFileName + ".xml");
//
//                    return parseTool(taskgraphTempFile);
//                }
//            }
//        }
//        return null;
//    }


}
