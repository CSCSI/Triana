package org.trianacode.shiwa.iwir.importer;

import org.shiwa.fgi.iwir.*;
import org.trianacode.config.TrianaProperties;
import org.trianacode.gui.extensions.AbstractFormatFilter;
import org.trianacode.gui.extensions.TaskGraphImporterInterface;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.gui.main.organize.DaxOrganize;
import org.trianacode.shiwa.iwir.tasks.TaskHolder;
import org.trianacode.shiwa.iwir.tasks.TaskHolderFactory;
import org.trianacode.taskgraph.TaskException;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.TaskGraphException;
import org.trianacode.taskgraph.TaskGraphManager;
import org.trianacode.taskgraph.imp.ToolImp;
import org.trianacode.taskgraph.proxy.java.JavaProxy;
import org.trianacode.taskgraph.ser.XMLReader;
import org.trianacode.taskgraph.tool.Tool;

import javax.swing.filechooser.FileFilter;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 07/04/2011
 * Time: 12:25
 * To change this template use File | Settings | File Templates.
 */
public class IwirReader extends AbstractFormatFilter implements TaskGraphImporterInterface {

    public IwirReader() {

    }

    @Override
    public String getFilterDescription() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public FileFilter[] getChoosableFileFilters() {
        return new FileFilter[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public FileFilter getDefaultFileFilter() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean hasOptions() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int showOptionsDialog(Component parent) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
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

        IWIR iwir;
        try {
            iwir = new IWIR(file);
        } catch (FileNotFoundException e) {
            System.out.println("Failed to load IWIR from file");
            return null;
        }

        AbstractTask rootTask = iwir.getTask();
        TaskGraph taskGraph = createTaskGraph(rootTask.getName());
        List<AbstractTask> allTasks = recurseAbstractTasks(taskGraph, rootTask);


        DaxOrganize daxOrganize = new DaxOrganize(taskGraph);
        return taskGraph;
    }

    private List<AbstractTask> recurseAbstractTasks(TaskGraph taskGraph, AbstractTask rootTask) {
        List<AbstractTask> tasks = new ArrayList<AbstractTask>();
        tasks.add(rootTask);

        if (rootTask.getChildren().size() > 0) {
            TaskGraph innerTaskGraph = createTaskGraph(rootTask.getName());
            for (AbstractTask task : rootTask.getChildren()) {
                addTaskHolderToTaskgraph(innerTaskGraph, task);
                tasks.addAll(recurseAbstractTasks(innerTaskGraph, task));
            }
            DaxOrganize daxOrganize = new DaxOrganize(innerTaskGraph);
            addTaskgraph(taskGraph, innerTaskGraph);
        } else {
            addTaskHolderToTaskgraph(taskGraph, rootTask);
        }

        return tasks;
    }

    private void addTaskHolderToTaskgraph(TaskGraph taskGraph, AbstractTask abstractTask) {
//        describeAbstractTask(abstractTask);

        System.out.println("AbstractTask : " + abstractTask.getUniqueId());

        boolean shouldAddTask = true;
        for (org.trianacode.taskgraph.Task trianaTask : taskGraph.getTasks(true)) {
            if (trianaTask instanceof TaskHolder) {
                AbstractTask iwirTask = ((TaskHolder) trianaTask).getIWIRTask();
                System.out.println("Checking to see if : " + abstractTask.getUniqueId() +
                        " = " + iwirTask.getUniqueId());
                if (abstractTask.getUniqueId().equals(iwirTask.getUniqueId())) {
                    shouldAddTask = false;
                }
            } else {
                System.out.println("Task isn't a toolholder : " + trianaTask.getDisplayName());
            }
        }

        if (shouldAddTask) {
            TaskHolder taskHolder = TaskHolderFactory.getTaskHolderFactory().getTaskHolder(abstractTask);

            Tool tool = initTool(taskHolder, taskGraph.getProperties());
            try {
                taskGraph.createTask(tool);
            } catch (TaskException e) {
                System.out.println("Failed to add tool to taskgraph.");
                e.printStackTrace();
            }
        } else {
            System.out.println("IWIR task with this unique id already exists." +
                    " Will not duplicate : " + abstractTask.getUniqueId());
        }
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
            parent.createTask(child);
        } catch (TaskException e) {
            System.out.println("Failed to add inner taskgraph (group) to taskgraph.");
            e.printStackTrace();
        }
    }


    private void describeAbstractTask(AbstractTask abstractTask) {
        System.out.println("\nIWIR abstractTask, Name : " + abstractTask.getName());
        for (AbstractPort abstractPort : abstractTask.getInputPorts()) {
            System.out.println("Input port :" +
                    "\n     PortName : " + abstractPort.getName() +
                    "\n     UniqueID : " + abstractPort.getUniqueId() +
                    "\n     PortType : " + abstractPort.getPortType().name()
            );
        }
        for (AbstractPort abstractPort : abstractTask.getOutputPorts()) {
            System.out.println("Output port :" +
                    "\n     PortName : " + abstractPort.getName() +
                    "\n     UniqueID : " + abstractPort.getUniqueId() +
                    "\n     PortType : " + abstractPort.getPortType().name()
            );
        }

    }

    private ToolImp initTool(TaskHolder taskHolder, TrianaProperties properties) {
        ToolImp tool = null;
        try {
            tool = new ToolImp(properties);
            tool.setProxy(new JavaProxy(taskHolder.getClass().getSimpleName(), taskHolder.getClass().getPackage().getName()));
            tool.setToolName(taskHolder.getIWIRTask().getName());
            tool.setToolPackage(taskHolder.getClass().getPackage().getName());

            tool.setDataInputNodeCount(taskHolder.getIWIRTask().getInputPorts().size());
            tool.setDataOutputNodeCount(taskHolder.getIWIRTask().getOutputPorts().size());
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


    private IWIR testIwir() {

        IWIR crossProduct = null;
        try {
            crossProduct = build();

            // to stdout
            System.out.println(crossProduct.asXMLString());

            // to file
            crossProduct.asXMLFile(new File("crossProduct.xml"));

            // form file
            crossProduct = new IWIR(new File("crossProduct.xml"));

            // to stdout
            System.out.println(crossProduct.asXMLString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return crossProduct;

    }

    private IWIR build() {
        IWIR i = new IWIR("crossProduct");

        ParallelForEachTask forEach1 = new ParallelForEachTask("foreach1");
        forEach1.addInputPort(new InputPort("collB", new CollectionType(
                SimpleType.FILE)));
        forEach1.addLoopElement(new LoopElement("collA", new CollectionType(
                SimpleType.FILE)));

        ParallelForEachTask forEach2 = new ParallelForEachTask("foreach2");
        forEach2.addInputPort(new InputPort("elementA", SimpleType.FILE));
        forEach2.addLoopElement(new LoopElement("collB", new CollectionType(
                SimpleType.FILE)));

        Task a = new Task("A", "consumer");
        a.addInputPort(new InputPort("elementA", SimpleType.FILE));
        a.addInputPort(new InputPort("elementB", SimpleType.FILE));
        a.addOutputPort(new OutputPort("res", SimpleType.FILE));

        forEach2.addTask(a);
        forEach2.addOutputPort(new OutputPort("res", new CollectionType(
                SimpleType.FILE)));
        forEach2.addLink(forEach2.getPort("elementA"), a.getPort("elementA"));
        forEach2.addLink(forEach2.getPort("collB"), a.getPort("elementB"));
        forEach2.addLink(a.getPort("res"), forEach2.getPort("res"));

        forEach1.addTask(forEach2);
        forEach1.addOutputPort(new OutputPort("res", new CollectionType(
                new CollectionType(SimpleType.FILE))));
        forEach1.addLink(forEach1.getPort("collA"),
                forEach2.getPort("elementA"));
        forEach1.addLink(forEach1.getPort("collB"), forEach2.getPort("collB"));
        forEach1.addLink(forEach2.getPort("res"), forEach1.getPort("res"));

        i.setTask(forEach1);

        return i;
    }
}
