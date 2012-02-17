package org.trianacode.shiwa.handler;

import org.apache.commons.logging.Log;
import org.shiwa.desktop.data.description.handler.TransferPort;
import org.shiwa.desktop.data.description.handler.TransferSignature;
import org.shiwa.desktop.data.transfer.ExecutionListener;
import org.trianacode.TrianaInstance;
import org.trianacode.config.TrianaProperties;
import org.trianacode.enactment.AddonUtils;
import org.trianacode.enactment.Exec;
import org.trianacode.enactment.TrianaRun;
import org.trianacode.enactment.addon.ConversionAddon;
import org.trianacode.enactment.io.*;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.gui.action.files.TaskGraphFileHandler;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.gui.panels.DisplayDialog;
import org.trianacode.shiwa.utils.WorkflowUtils;
import org.trianacode.taskgraph.*;
import org.trianacode.taskgraph.imp.ToolImp;
import org.trianacode.taskgraph.proxy.ProxyInstantiationException;
import org.trianacode.taskgraph.proxy.java.JavaProxy;
import org.trianacode.taskgraph.ser.DocumentHandler;
import org.trianacode.taskgraph.ser.XMLReader;
import org.trianacode.taskgraph.service.SchedulerException;
import org.trianacode.taskgraph.service.VariableDummyUnit;
import org.trianacode.taskgraph.tool.Tool;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 06/07/2011
 * Time: 14:22
 * To change this template use File | Settings | File Templates.
 */
public class TrianaShiwaListener implements ExecutionListener {

    private TrianaInstance trianaInstance;
    private DisplayDialog dialog;
    private static Log devLog = Loggers.DEV_LOGGER;


    public TrianaShiwaListener(TrianaInstance trianaInstance, DisplayDialog dialog) {
        this.trianaInstance = trianaInstance;
        this.dialog = dialog;

    }

    public IoConfiguration getIOConfigFromSignature(String name, TransferSignature signature) {
        ArrayList<IoMapping> inputMappings = new ArrayList<IoMapping>();

        List<TransferPort> inputPorts = signature.getInputs();
        int portNumber = 0;
        for (TransferPort inputPort : inputPorts) {
            if (inputPort.getValue() != null) {
                String portName = inputPort.getName();
//                String portNumberString = portName.substring(portName.indexOf("_") + 1);
                String portNumberString = String.valueOf(portNumber);

                String value = inputPort.getValue();
                boolean reference;
                if (inputPort.getValueType() == TransferSignature.ValueType.BUNDLED_FILE) {
                    reference = true;
                } else {
                    reference = false;
                }
                IoMapping ioMapping = new IoMapping(new IoType(value, "string", reference), portNumberString);
                inputMappings.add(ioMapping);
                portNumber++;
            }
        }

        IoConfiguration conf = new IoConfiguration(name, "0.1", inputMappings, new ArrayList<IoMapping>());

        List<IoMapping> mappings = conf.getInputs();
        for (IoMapping mapping : mappings) {
            devLog.debug("  mapping:");
            devLog.debug("    name:" + mapping.getNodeName());
            devLog.debug("    type:" + mapping.getIoType().getType());
            devLog.debug("    val:" + mapping.getIoType().getValue());
            devLog.debug("    ref:" + mapping.getIoType().isReference());
        }

        return conf;
    }

    @Override
    public void digestWorkflow(File file, TransferSignature signature) {
        devLog.debug("Importing a bundle");
        try {
            if (GUIEnv.getApplicationFrame() != null) {
                String workflowType = WorkflowUtils.getWorkflowType(file, signature);
                TaskGraph taskGraph;
                if (workflowType.equals(AddonUtils.TASKGRAPH_FORMAT)) {
                    taskGraph = TaskGraphFileHandler.openTaskgraph(file, true);
                } else if (workflowType.equals(AddonUtils.IWIR_FORMAT)) {
                    ConversionAddon conversionAddon = (ConversionAddon) AddonUtils.getService(trianaInstance, "IWIR-To-Task", ConversionAddon.class);
                    if (conversionAddon != null) {
                        taskGraph = (TaskGraph) conversionAddon.workflowToTool(file);
                        taskGraph = GUIEnv.getApplicationFrame().addParentTaskGraphPanel(taskGraph);
                    } else {
                        System.out.println("FAil");
                        return;
                    }
                } else {
                    System.out.println("Fails");
                    return;
                }

                if (signature.hasConfiguration()) {
                    createConfigUnit(taskGraph, getIOConfigFromSignature(taskGraph.getQualifiedToolName(), signature));
                }

            } else {
                devLog.debug("No gui found, running headless");
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


    private void executeWorkflowInGUI(String toolName, Tool tool, TransferSignature signature) throws TaskGraphException, SchedulerException {
        TrianaRun runner = new TrianaRun(tool);
        IoConfiguration ioc = getIOConfigFromSignature(toolName, signature);
        IoHandler handler = new IoHandler();
        NodeMappings mappings = handler.map(ioc, runner.getTaskGraph());

        runner.runTaskGraph();

        if (mappings != null) {
            devLog.debug("Data mappings size : " + mappings.getMap().size());
            Iterator<Integer> it = mappings.iterator();
            while (it.hasNext()) {
                Integer integer = it.next();
                Object val = mappings.getValue(integer);
                devLog.debug("Data : " + val.toString() + " will be sent to input number " + integer);
                runner.sendInputData(integer, val);
            }
        } else {
            devLog.debug("Mappings was null");
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
            devLog.debug("node " + nodeIndex + " output:" + out);
        }
        runner.dispose();

    }

    private void exec(Task loadedTask, IoConfiguration conf) throws IOException {
        DocumentHandler documentHandler = new DocumentHandler();
        new IoHandler().serialize(documentHandler, conf);
        File tempConfFile = File.createTempFile(conf.getToolName() + "_confFile", ".dat");
        documentHandler.output(new FileWriter(tempConfFile), true);


        Exec exec = new Exec(null);
        try {
            exec.execute(loadedTask, tempConfFile.getAbsolutePath());
        } catch (Exception e) {
            devLog.debug("Failed to load workflow back to Triana");
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


                devLog.debug("Data mappings size : " + mappings.getMap().size());
                Iterator<Integer> it = mappings.iterator();
                while (it.hasNext()) {
                    Integer integer = it.next();
                    Object val = mappings.getValue(integer);
                    devLog.debug("Data : " + val.toString() + " will be sent to input number " + integer);
                    Node taskNode = getNodeInScope(inputNodes[integer], taskgraph);
                    Node addedNode = varTask.addDataOutputNode();


                    taskgraph.connect(addedNode, taskNode);
                    varTask.setParameter("var" + integer.toString(), val);
                }

                Object o = varTask.getParameter("configSize");
                int configSize = 0;
                if (o instanceof Integer) {
                    configSize = (Integer) o;
                }
                devLog.debug("Multiple output config task added " + configSize);

            } else {
                devLog.debug("Mappings was null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Node getNodeInScope(Node inputNode, TaskGraph taskGraph) {
        Node scopeNode = inputNode.getTopLevelNode();
        while (scopeNode.getTask().getParent() != taskGraph && scopeNode != null) {
            scopeNode = scopeNode.getChildNode();
        }
        return scopeNode;
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
