package org.trianacode.shiwa.iwir.test;

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

        TaskGraph taskGraph = createTaskGraph(trianaInstance);

        fillTaskgraph(taskGraph);

        File file = createTaskGraphFile(taskGraph);

        TaskGraph readTaskgraph = readTaskgraph(file, trianaInstance);

        BlockScope blockScope = taskGraphToIWIR(readTaskgraph);

        File iwirFile = writeIWIR(blockScope);

        IWIR iwir = readIWIR(iwirFile);

        TaskGraph iwirTaskGraph = taskFromIwir(trianaInstance, iwir);

        createTaskGraphFile(iwirTaskGraph);
    }

    private TaskGraph taskFromIwir(TrianaInstance trianaInstance, IWIR iwir) throws TaskException, ProxyInstantiationException {
        TaskGraph taskGraph = createTaskGraph(trianaInstance);
        for (AbstractTask task : iwir.getTask().getChildren()) {
            taskGraph.createTask(makeTool(InOut.class, taskGraph.getProperties()));
        }
        return taskGraph;
    }

    private IWIR readIWIR(File iwirFile) throws FileNotFoundException {
        IWIR iwir = new IWIR(iwirFile);
        return iwir;
    }

    private File writeIWIR(BlockScope blockScope) throws IOException {
        IWIR iwir = new IWIR(blockScope.getName());
        iwir.setTask(blockScope);
        File file = new File("iwirOutput.xml");
        iwir.asXMLFile(file);
        System.out.println(iwir.asXMLString());
        return file;
    }

    private BlockScope taskGraphToIWIR(TaskGraph taskGraph) {
        BlockScope blockScope = new BlockScope(taskGraph.getToolName());
        for (Task task : taskGraph.getTasks(true)) {
            if (task instanceof TaskGraph) {

            }
            org.shiwa.fgi.iwir.Task iwirTask = new org.shiwa.fgi.iwir.Task(task.getQualifiedTaskName(), "");
            blockScope.addTask(iwirTask);
            for (Node node : task.getDataInputNodes()) {
                if (node.getBottomLevelTask() == taskGraph) {
                    InputPort inputNodePort = new InputPort(node.getTopLevelNode().getName(), SimpleType.STRING);
                    iwirTask.addInputPort(inputNodePort);
                    InputPort inputBlockPort = new InputPort(node.getBottomLevelNode().getName(), SimpleType.STRING);
                    blockScope.addInputPort(inputBlockPort);
                    blockScope.addLink(inputBlockPort, inputNodePort);
                }
            }

            for (Node node : task.getDataOutputNodes()) {
                if (node.getBottomLevelTask() == taskGraph) {
                    OutputPort outputNodePort = new OutputPort(node.getTopLevelNode().getName(), SimpleType.STRING);
                    iwirTask.addOutputPort(outputNodePort);
                    OutputPort outputBlockPort = new OutputPort(node.getBottomLevelNode().getName(), SimpleType.STRING);
                    blockScope.addOutputPort(outputBlockPort);
                    blockScope.addLink(outputNodePort, outputBlockPort);
                }
            }
        }
        return blockScope;
    }

    private TaskGraph readTaskgraph(File file, TrianaInstance trianaInstance) throws IOException, TaskGraphException {
        XMLReader xmlReader = new XMLReader(new FileReader(file));
        Tool tool = xmlReader.readComponent(trianaInstance.getProperties());
        return (TaskGraph) tool;
    }

    private TaskGraph fillTaskgraph(TaskGraph taskGraph) throws IOException, TaskException, ProxyInstantiationException {
        Tool tool = makeTool(InOut.class, taskGraph.getProperties());
        Task task = taskGraph.createTask(tool);
        taskGraph.addDataInputNode(task.addDataInputNode());
        taskGraph.addDataOutputNode(task.addDataOutputNode());
        taskGraph.setToolName("TestTaskgraph");
        return taskGraph;
    }

    private Tool makeTool(Class clazz, TrianaProperties properties) throws ProxyInstantiationException, TaskException {
        Tool tool = new ToolImp(properties);
        tool.setProxy(new JavaProxy(clazz.getSimpleName(), clazz.getPackage().getName()));
        tool.setToolName("Test");
        tool.setToolPackage(InOut.class.getPackage().getName());
        return tool;
    }

    private File createTaskGraphFile(TaskGraph taskGraph) throws IOException, TaskException, ProxyInstantiationException {
        File outputFile = new File("testOutput.xml");
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
