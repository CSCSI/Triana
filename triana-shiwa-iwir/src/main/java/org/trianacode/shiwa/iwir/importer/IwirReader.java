package org.trianacode.shiwa.iwir.importer;

import org.shiwa.fgi.iwir.*;
import org.trianacode.config.TrianaProperties;
import org.trianacode.gui.extensions.AbstractFormatFilter;
import org.trianacode.gui.extensions.TaskGraphImporterInterface;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.gui.main.organize.DaxOrganize;
import org.trianacode.shiwa.iwir.creation.NodePortTranslator;
import org.trianacode.shiwa.iwir.creation.NodeProxy;
import org.trianacode.shiwa.iwir.factory.TaskHolder;
import org.trianacode.shiwa.iwir.factory.TaskHolderFactory;
import org.trianacode.shiwa.iwir.xslt.iwir.xsltTransformer;
import org.trianacode.taskgraph.*;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.imp.ToolImp;
import org.trianacode.taskgraph.proxy.ProxyFactory;
import org.trianacode.taskgraph.proxy.java.JavaProxy;
import org.trianacode.taskgraph.ser.XMLReader;
import org.trianacode.taskgraph.tool.Tool;

import javax.swing.filechooser.FileFilter;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 07/04/2011
 * Time: 12:25
 * To change this template use File | Settings | File Templates.
 */
public class IwirReader extends AbstractFormatFilter implements TaskGraphImporterInterface {

    private TrianaProperties trianaProperties;
    List<Task> allTrianaTasks;
    Map<String, AbstractTask> allAbstractTasks;
    NodePortTranslator nodePortTranslator;


    public IwirReader() {

    }

    @Override
    public String getFilterDescription() {
        return null;
    }

    @Override
    public FileFilter[] getChoosableFileFilters() {
        return new FileFilter[0];
    }

    @Override
    public FileFilter getDefaultFileFilter() {
        return null;
    }

    @Override
    public boolean hasOptions() {
        return false;
    }

    @Override
    public int showOptionsDialog(Component parent) {
        return 0;
    }

    public String toString() {
        return "IwirReader";
    }


    @Override
    public TaskGraph importWorkflow(File file, TrianaProperties properties) throws TaskGraphException, IOException {

        try {
            return importIWIR(file, properties);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    private TaskGraph importIWIR(File file, TrianaProperties properties) {
        this.trianaProperties = properties;
        allTrianaTasks = new ArrayList<Task>();
        allAbstractTasks = new HashMap<String, AbstractTask>();
        nodePortTranslator = new NodePortTranslator();

        IWIR iwir;
        try {
            iwir = new IWIR(file);
        } catch (FileNotFoundException e) {
            System.out.println("Failed to load IWIR from file");
            return null;
        }

        AbstractTask rootTask = iwir.getTask();
        TaskGraph taskGraph = createTaskGraph(iwir.getWfname());
        List<AbstractTask> endAbstractTasks = recurseAbstractTasks(taskGraph, rootTask);
        System.out.println(allAbstractTasks.size() == endAbstractTasks.size());
        System.out.println(allAbstractTasks.size() == allTrianaTasks.size());
        attachCables(endAbstractTasks);

        DaxOrganize daxOrganize = new DaxOrganize(taskGraph);
        nodePortTranslator.listAll();
        return taskGraph;
    }

    public void attachCables(List<AbstractTask> endAbstractTasks) {
        for (AbstractTask task : endAbstractTasks) {
            if (AbstractCompoundTask.class.isAssignableFrom(task.getClass())) {
                AbstractCompoundTask abstractCompoundTask = (AbstractCompoundTask) task;
                List<AbstractLink> links = abstractCompoundTask.getLinks();
                for (AbstractLink link : links) {
                    String from = link.getFrom();
                    String to = link.getTo();
                    if (DataLink.class.isAssignableFrom(link.getClass())) {
                        AbstractPort fromPort = ((DataLink) link).getFromPort();
                        AbstractPort toPort = ((DataLink) link).getToPort();

                        Node inputNode = nodePortTranslator.getNodeForAbstractPort(toPort);
                        Node outputNode = nodePortTranslator.getNodeForAbstractPort(fromPort);

                        if (inputNode != null && outputNode != null) {

                            System.out.println("\nIWIR Connecting from : " + from + " To : " + to);
                            System.out.println("TRIANA Connecting from : " + outputNode.getName() + " To : " + inputNode.getName());

                            TaskGraph outputScopedTaskGraph = outputNode.getTask().getParent();
                            TaskGraph inputScopedTaskGraph = inputNode.getTask().getParent();
                            if (outputScopedTaskGraph == inputScopedTaskGraph) {
                                try {

                                    System.out.println("    TaskGraph in scope is : " + outputScopedTaskGraph.getToolName());
                                    //TODO is this reasonable?
                                    Cable cable = outputScopedTaskGraph.connect(outputNode, inputNode);
                                    System.out.println("Cable connected : " + cable.isConnected());
                                } catch (Exception e) {
                                    System.out.println("*** *** Problem attaching cable : " + e.getMessage());
                                    e.printStackTrace();
                                } catch (Error err) {
                                    System.out.println("** ** Error somewhere : " + err.getMessage());
                                }
                            } else {
                                //TODO this is an issue..
                                System.out.println("    Nodes are in different taskgraph scopes");
                            }
                        }

                    }
                }
            }
        }

    }

    private List<AbstractTask> recurseAbstractTasks(TaskGraph taskGraph, AbstractTask rootTask) {
        List<AbstractTask> tasks = new ArrayList<AbstractTask>();
        tasks.add(rootTask);

        if (rootTask.getChildren().size() > 0) {
            System.out.println("\nRoot task " +
                    rootTask.getUniqueId() +
                    " has " +
                    rootTask.getChildren().size() +
                    " children.");

            TaskGraph innerTaskGraph = createTaskGraph(rootTask.getName());
            if (AbstractCompoundTask.class.isAssignableFrom(rootTask.getClass()) && !BlockScope.class.isAssignableFrom(rootTask.getClass())) {
                System.out.println("Root task : " + rootTask.getUniqueId() + " is loopy.");
                Task controlTask = addTaskHolderToTaskgraph(innerTaskGraph, rootTask);
                resolveNodes(innerTaskGraph, controlTask);
            }

//            for (AbstractTask task : rootTask.getChildren()) {
//                System.out.println("Child task : " + task.getUniqueId());
//            }
            for (AbstractTask task : rootTask.getChildren()) {
                tasks.addAll(recurseAbstractTasks(innerTaskGraph, task));
            }

            addTaskgraph(taskGraph, innerTaskGraph);
        } else {
            addTaskHolderToTaskgraph(taskGraph, rootTask);
        }

        return tasks;
    }

    private Task addTaskHolderToTaskgraph(TaskGraph taskGraph, AbstractTask abstractTask) {
        System.out.println("    Checking " + abstractTask.getUniqueId());

        if (shouldAddAbstractTask(taskGraph, abstractTask)) {
            try {
                TaskHolder taskHolder = TaskHolderFactory.getTaskHolderFactory().getTaskHolder(abstractTask);
                Tool tool = initTool(taskHolder);

                Task task = taskGraph.createTask(tool);
                System.out.println("Adding abstract task " + abstractTask.getUniqueId() + " to taskgraph " + taskGraph.getToolName());
                allTrianaTasks.add(task);
                allAbstractTasks.put(abstractTask.getUniqueId(), abstractTask);

                recordAbstractPorts(abstractTask, task);


                return task;

            } catch (TaskException e) {
                System.out.println("Failed to add tool to taskgraph.");
                e.printStackTrace();
            }
        } else {
            System.out.println("Abstract task with unique id " + abstractTask.getUniqueId() + " already exists, will not duplicate.");
        }
        return null;
    }

    private void recordAbstractPorts(AbstractTask abstractTask, Task task) {
        System.out.println("\nRecording ports for task : " + task.getDisplayName() + " abstract :" + abstractTask.getUniqueId());
        for (AbstractPort abstractPort : abstractTask.getInputPorts()) {
            Node inNode = null;
            try {
                inNode = task.addDataInputNode();
//                System.out.println("    Triana node : " + inNode.getName());
            } catch (NodeException e) {
                e.printStackTrace();
            }
            nodePortTranslator.addNodeProxy(new NodeProxy(inNode, abstractPort));
        }
        for (AbstractPort abstractPort : abstractTask.getOutputPorts()) {
            Node outNode = null;
            try {
                outNode = task.addDataOutputNode();
//                System.out.println("    Triana node : " + outNode.getName());
            } catch (NodeException e) {
                e.printStackTrace();
            }
            nodePortTranslator.addNodeProxy(new NodeProxy(outNode, abstractPort));
        }
    }

    private boolean shouldAddAbstractTask(TaskGraph taskGraph, AbstractTask abstractTask) {
        return !allAbstractTasks.containsKey(abstractTask.getUniqueId());
    }

    private AbstractTask getIWIRTaskFromTrianaTool(Tool trianaTool) {
        if (trianaTool.getProxy() instanceof JavaProxy) {
            Unit unit = ((JavaProxy) trianaTool.getProxy()).getUnit();
            if (unit instanceof TaskHolder) {
                AbstractTask iwirTask = ((TaskHolder) unit).getIWIRTask();
                if (iwirTask != null) {
                    return iwirTask;
                } else {
                    System.out.println("IWIR task is null in TaskHolder unit.");
                }
            } else {
                System.out.println("Task isn't a toolholder : " + trianaTool.getDisplayName());
            }
        }
        return null;
    }

    private TaskGraph createTaskGraph(String name) {
        TaskGraph taskGraph = null;
        try {
            taskGraph = TaskGraphManager.createTaskGraph();
            taskGraph.setToolName(name);

        } catch (TaskException e) {
            e.printStackTrace();
        }
        return taskGraph;
    }

    private void addTaskgraph(TaskGraph parent, TaskGraph child) {
        try {
//            TaskLayoutUtils.resolveGroupNodes(child);
            DaxOrganize daxOrganize = new DaxOrganize(child);
            parent.createTask(child);
        } catch (TaskException e) {
            System.out.println("Failed to add inner taskgraph (group) to taskgraph.");
            e.printStackTrace();
        } catch (TaskGraphException e) {
            System.out.println("Failed to resolve group nodes.");
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
            System.out.println(string);
        }

        String[] outputTypes = new String[tool.getDataOutputNodeCount()];
        for (int nodeCount = 0; nodeCount < tool.getDataOutputNodeCount(); nodeCount++) {
            outputTypes[nodeCount] = (Object.class.getCanonicalName());
        }
        tool.setDataOutputTypes(outputTypes);
        for (String string : tool.getDataOutputTypes()) {
            System.out.println(string);
        }
    }


    private void describeAbstractTask(AbstractTask abstractTask) {
        System.out.println("\nIWIR abstractTask, Name : " + abstractTask.getName());
        for (AbstractPort abstractPort : abstractTask.getInputPorts()) {
            System.out.println("Input port :" +
                    "\n     PortName : " + abstractPort.getName() +
                    "\n     UniqueID : " + abstractPort.getUniqueId()
//                    "\n     PortType : " + abstractPort.getPortType().name()
            );
        }
        for (AbstractPort abstractPort : abstractTask.getOutputPorts()) {
            System.out.println("Output port :" +
                    "\n     PortName : " + abstractPort.getName() +
                    "\n     UniqueID : " + abstractPort.getUniqueId()
//                    "\n     PortType : " + abstractPort.getPortType().name()
            );
        }

    }

    private ToolImp initTool(TaskHolder taskHolder) {
        ToolImp tool = null;
        ProxyFactory.initProxyFactory();
        try {
            tool = new ToolImp(trianaProperties);

            tool.setProxy(new JavaProxy(taskHolder, taskHolder.getClass().getSimpleName(), taskHolder.getClass().getPackage().getName()));

            tool.setToolName(taskHolder.getIWIRTask().getName());
            tool.setToolPackage(taskHolder.getClass().getPackage().getName());
//            tool.setDataInputNodeCount(taskHolder.getIWIRTask().getInputPorts().size());
//            tool.setDataOutputNodeCount(taskHolder.getIWIRTask().getOutputPorts().size());
        } catch (Exception e) {
            System.out.println("Failed to initialise tool from Unit.");
            e.printStackTrace();
        }
        return tool;
    }


    private TaskGraph parseTool(File file) {
        XMLReader reader;
        Tool tool = null;
        if (file.exists()) {
            try {
                BufferedReader filereader = new BufferedReader(new FileReader(file));
                reader = new XMLReader(filereader);
                System.out.println("Reading tool from file : " + file.getCanonicalPath());
                tool = reader.readComponent(GUIEnv.getApplicationFrame().getEngine().getProperties());

            } catch (IOException e) {
                System.out.println(file + " : not found");
            } catch (TaskGraphException e) {
                e.printStackTrace();
            }
        }
        if (tool instanceof TaskGraph) {
            TaskGraph tg = (TaskGraph) tool;
            DaxOrganize daxOrganize = new DaxOrganize(tg);
            return tg;
        } else {
            return null;
        }
    }

    private TaskGraph importUsingXSLT(File file, TrianaProperties properties) throws IOException {
        String root = "triana-shiwa/src/main/java/org/trianacode/shiwa/xslt/iwir/";

        if (file.exists() && file.canRead()) {
            String iwirPath = file.getAbsolutePath();
            String removeNamespacePath = root + "removeNamespace.xsl";
            String iwirTaskgraphTransformerPath = root + "iwir.xsl";
            String tempFileName = file.getName() + "-outputTemp.xml";
            String taskgraphFileName = file.getName() + "-taskgraph";

            File removeNamespace = new File(removeNamespacePath);
            File iwirTaskgraphTransformer = new File(iwirTaskgraphTransformerPath);

            if (removeNamespace.exists() && iwirTaskgraphTransformer.exists()) {

                xsltTransformer.doTransform(iwirPath, tempFileName, removeNamespacePath);
                System.out.println("Stripped namespace");

                xsltTransformer.doTransform(tempFileName, taskgraphFileName + ".xml", iwirTaskgraphTransformerPath);
                System.out.println("Created taskgraph file " + taskgraphFileName + ".xml");

                return parseTool(new File(taskgraphFileName + ".xml"));
            } else {
                System.out.println("Transform file not available. Attempting to use file from classloader");


                StreamSource iwirFile = new StreamSource(file);
                InputStream removeNamespaceTransformerInputStream = this.getClass().getResourceAsStream("/removeNamespace.xsl");
                StreamSource removeNamespaceTransformerSource = new StreamSource(removeNamespaceTransformerInputStream);
                InputStream transformerInputStream = this.getClass().getResourceAsStream("/iwir.xsl");
                StreamSource transformerSource = new StreamSource(transformerInputStream);

                if (removeNamespaceTransformerInputStream == null && transformerInputStream == null) {

                    System.out.println("Could not read from xslt transformer sources.");
                } else {

                    File removedNamespaceFile = File.createTempFile(taskgraphFileName + "sansNamespace", ".xml");
                    StreamResult streamResult = new StreamResult(removeNamespacePath);
                    xsltTransformer.doTransform(iwirFile, removeNamespaceTransformerSource, streamResult);
                    System.out.println("Created namespace-less file : " + removeNamespacePath);

                    StreamSource removedNamespaceSource = new StreamSource(removedNamespaceFile);
                    File taskgraphTempFile = File.createTempFile(taskgraphFileName, ".xml");
                    StreamResult taskgraphStreamResult = new StreamResult(taskgraphTempFile);
                    xsltTransformer.doTransform(removedNamespaceSource, transformerSource, taskgraphStreamResult);
                    System.out.println("Created taskgraph from iwir : " + taskgraphFileName + ".xml");

                    return parseTool(taskgraphTempFile);
                }
            }
        }
        return null;
    }


}
