package org.trianacode.shiwa;

import org.shiwa.desktop.data.description.handler.Port;
import org.shiwa.desktop.data.description.handler.Signature;
import org.shiwa.desktop.data.transfer.SHIWADesktopExecutionListener;
import org.trianacode.TrianaInstance;
import org.trianacode.config.TrianaProperties;
import org.trianacode.enactment.Exec;
import org.trianacode.enactment.TrianaRun;
import org.trianacode.enactment.io.*;
import org.trianacode.gui.action.files.TaskGraphFileHandler;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.gui.panels.DisplayDialog;
import org.trianacode.taskgraph.*;
import org.trianacode.taskgraph.imp.ToolImp;
import org.trianacode.taskgraph.proxy.ProxyInstantiationException;
import org.trianacode.taskgraph.proxy.java.JavaProxy;
import org.trianacode.taskgraph.ser.XMLReader;
import org.trianacode.taskgraph.service.SchedulerException;
import org.trianacode.taskgraph.service.VariableDummyUnit;
import org.trianacode.taskgraph.tool.Tool;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 06/07/2011
 * Time: 14:22
 * To change this template use File | Settings | File Templates.
 */
public class TrianaShiwaListener implements SHIWADesktopExecutionListener {

    private TrianaInstance trianaInstance;
    private DisplayDialog dialog;

    public TrianaShiwaListener(TrianaInstance trianaInstance, DisplayDialog dialog) {
        this.trianaInstance = trianaInstance;
        this.dialog = dialog;

    }

    public IoConfiguration getIOConfigFromSignature(String name, Signature signature) {
        ArrayList<IoMapping> inputMappings = new ArrayList<IoMapping>();

        List<Port> inputPorts = signature.getInputs();
        for (Port inputPort : inputPorts) {
            if (inputPort.getValue() != null) {
                String portName = inputPort.getName();
                String portNumberString = portName.substring(portName.indexOf("_") + 1);

                String value = inputPort.getValue();
                boolean reference = inputPort.isReference();


                IoMapping ioMapping = new IoMapping(new IoType(value, "string", reference), portNumberString);
                inputMappings.add(ioMapping);
            }
        }

        IoConfiguration conf = new IoConfiguration(name, "0.1", inputMappings, new ArrayList<IoMapping>());

        List<IoMapping> mappings = conf.getInputs();
        for (IoMapping mapping : mappings) {
            System.out.println("  mapping:");
            System.out.println("    name:" + mapping.getNodeName());
            System.out.println("    type:" + mapping.getIoType().getType());
            System.out.println("    val:" + mapping.getIoType().getValue());
            System.out.println("    ref:" + mapping.getIoType().isReference());
        }

        return conf;
    }

    @Override
    public void digestWorkflow(File file, Signature signature) {
        System.out.println("Importing a bundle");
        try {
            if (GUIEnv.getApplicationFrame() != null) {
                TaskGraph taskGraph = TaskGraphFileHandler.openTaskgraph(file, true);
                if (signature.hasConfiguration()) {
                    createConfigUnit(taskGraph, getIOConfigFromSignature(taskGraph.getQualifiedToolName(), signature));
                }
            } else {
                System.out.println("No gui found, running headless");
                XMLReader reader = new XMLReader(new FileReader(file));
                Tool tool = reader.readComponent(trianaInstance.getProperties());
                if (tool instanceof TaskGraph) {
                    TaskGraph taskGraph = (TaskGraph) tool;
                    exec(taskGraph, getIOConfigFromSignature(taskGraph.getQualifiedTaskName(), signature));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeWorkflowInGUI(String toolName, Tool tool, Signature signature) throws TaskGraphException, SchedulerException {
        TrianaRun runner = new TrianaRun(tool);
        IoConfiguration ioc = getIOConfigFromSignature(toolName, signature);
        IoHandler handler = new IoHandler();
        NodeMappings mappings = handler.map(ioc, runner.getTaskGraph());

        runner.runTaskGraph();

        if (mappings != null) {
            System.out.println("Data mappings size : " + mappings.getMap().size());
            Iterator<Integer> it = mappings.iterator();
            while (it.hasNext()) {
                Integer integer = it.next();
                Object val = mappings.getValue(integer);
                System.out.println("Data : " + val.toString() + " will be sent to input number " + integer);
                runner.sendInputData(integer, val);
            }
        } else {
            System.out.println("Mappings was null");
        }

        while (!runner.isFinished()) {
            synchronized (this) {
                try {
                    wait(100);
                } catch (InterruptedException e) {

                }
            }
        }

        for (Node node : runner.getTaskGraph().getDataOutputNodes()) {
            int nodeIndex = node.getAbsoluteNodeIndex();
            Object out = runner.receiveOutputData(nodeIndex);
            System.out.println("node " + nodeIndex + " output:" + out);
        }
        runner.dispose();

    }

    private void exec(Task loadedTask, IoConfiguration conf) {
        Exec exec = new Exec(null);
        try {
            exec.execute(loadedTask, conf);
        } catch (Exception e) {
            System.out.println("Failed to load workflow back to Triana");
            e.printStackTrace();
        }
    }

    private void createConfigUnit(TaskGraph taskgraph, IoConfiguration ioConfiguration) {
        try {
            IoHandler handler = new IoHandler();
            NodeMappings mappings = handler.map(ioConfiguration, taskgraph);

            if (mappings != null) {


                Node[] inputNodes = new Node[taskgraph.getInputNodeCount()];
                for (int i = 0; i < taskgraph.getInputNodes().length; i++) {
                    Node inputNode = taskgraph.getInputNode(i);
                    inputNodes[i] = inputNode.getTopLevelNode();
                }
                Task varTask = taskgraph.createTask(getVarTool(taskgraph.getProperties()));
                varTask.setParameter("configSize", inputNodes.length);


                System.out.println("Data mappings size : " + mappings.getMap().size());
                Iterator<Integer> it = mappings.iterator();
                while (it.hasNext()) {
                    Integer integer = it.next();
                    Object val = mappings.getValue(integer);
                    System.out.println("Data : " + val.toString() + " will be sent to input number " + integer);
                    Node taskNode = inputNodes[integer];
                    Node addedNode = varTask.addDataOutputNode();

                    taskgraph.connect(addedNode, taskNode);
                    varTask.setParameter("var" + integer.toString(), val);
                }

                Object o = varTask.getParameter("configSize");
                int configSize = 0;
                if (o instanceof Integer) {
                    configSize = (Integer) o;
                }
                System.out.println("Multiple output config task added " + configSize);

            } else {
                System.out.println("Mappings was null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Tool getVarTool(TrianaProperties properties) throws TaskException, ProxyInstantiationException {
        ToolImp varTool = new ToolImp(properties);
        varTool.setToolPackage(VariableDummyUnit.class.getPackage().getName());
        varTool.setProxy(new JavaProxy(VariableDummyUnit.class.getSimpleName(), VariableDummyUnit.class.getPackage().getName()));
        varTool.setToolName("Configuration");
        TaskLayoutUtils.setPosition(varTool, new TPoint(1, 1));
        return varTool;
    }
}
