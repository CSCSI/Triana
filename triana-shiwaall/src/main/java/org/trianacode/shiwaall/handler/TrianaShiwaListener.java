package org.trianacode.shiwaall.handler;

import org.apache.commons.logging.Log;
import org.shiwa.desktop.data.description.handler.TransferPort;
import org.shiwa.desktop.data.description.handler.TransferSignature;
import org.shiwa.desktop.data.description.resource.ConfigurationResource;
import org.shiwa.desktop.data.transfer.ExecutionListener;
import org.shiwa.desktop.data.util.DataUtils;
import org.shiwa.desktop.gui.SHIWADesktop;
import org.shiwa.desktop.gui.util.listener.DefaultBundleReceivedListener;
import org.shiwa.fgi.iwir.IWIR;
import org.trianacode.TrianaInstance;
import org.trianacode.config.TrianaProperties;
import org.trianacode.enactment.AddonUtils;
import org.trianacode.enactment.Exec;
import org.trianacode.enactment.TrianaRun;
import org.trianacode.enactment.io.*;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.gui.action.files.TaskGraphFileHandler;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.shiwaall.dax.DaxReader;
import org.trianacode.shiwaall.iwir.importer.utils.ImportIwir;
import org.trianacode.shiwaall.utils.WorkflowUtils;
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

// TODO: Auto-generated Javadoc
/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 06/07/2011
 * Time: 14:22
 * To change this template use File | Settings | File Templates.
 *
 */
public class TrianaShiwaListener implements ExecutionListener {

    /** The triana instance. */
    private TrianaInstance trianaInstance;

    /** The dev log. */
    private static Log devLog = Loggers.DEV_LOGGER;

    /** The received listener. */
    private DefaultBundleReceivedListener receivedListener = null;

    /** The shiwa desktop. */
    private SHIWADesktop shiwaDesktop = null;


    /**
     * Instantiates a new triana shiwa listener.
     *
     * @param trianaInstance the triana instance
     */
    public TrianaShiwaListener(TrianaInstance trianaInstance) {
        this.trianaInstance = trianaInstance;
    }

    /**
     * Gets the iO config from signature.
     *
     * @param taskGraph the taskgraph
     * @param signature the signature
     * @return the iO config from signature
     */
    public IoConfiguration getIOConfigFromSignature(TaskGraph taskGraph, TransferSignature signature) {

//        System.out.println("building signature");

        ArrayList<IoMapping> inputMappings = new ArrayList<IoMapping>();

        List<TransferPort> inputPorts = signature.getInputs();
        int portNumber = 0;
        for (TransferPort inputPort : inputPorts) {
            if (inputPort.getValue() != null) {
                String portName = inputPort.getName();
//                String portNumberString = portName.substring(portName.indexOf("_") + 1);

//                String portNumberString = getCorrectPortNumber(taskGraph, portName);
                Object portNumberObject = taskGraph.getParameter(portName);
                String portNumberString = "";
                if(portNumberObject != null){
                    portNumberString = ((Integer)portNumberObject).toString();
                    System.out.println(portName + " : " + portNumberString);
                }
//                String portNumberString = String.valueOf(portNumber);

                String value = inputPort.getValue();

                boolean reference;
                if (inputPort.getValueType() == TransferSignature.ValueType.BUNDLED_FILE) {
                    reference = true;
                } else {
                    reference = false;
                }

                System.out.println("\n" +
                        "Data " + value +
                        " to port " + inputPort.getName() +
                        " portNumber " + portNumberString +
                        " reference " + reference +
                        " " + inputPort.getType() +
                         " " + inputPort.getValueType().toString()
                );

                IoMapping ioMapping = new IoMapping(new IoType(value, "string", reference), portNumberString);
                inputMappings.add(ioMapping);
                portNumber++;
            }
        }

        IoConfiguration conf = new IoConfiguration(taskGraph.getQualifiedToolName(), "0.1", inputMappings, new ArrayList<IoMapping>());

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

//    private String getCorrectPortNumber(TaskGraph taskGraph, String portName) {
//        System.out.println("Looking for portName " + portName);
//
//        for(Node node : taskGraph.getInputNodes()){
//            node = node.getTopLevelNode();
//
////            System.out.println("taskgraph has node named " + node.getName());
//            Task task = node.getTopLevelTask();
//
//            if(task.getProxy() instanceof JavaProxy){
//                JavaProxy javaProxy = (JavaProxy)task.getProxy();
//                Unit unit = javaProxy.getUnit();
//
//                if(unit.getClass().getCanonicalName().equals(AtomicTaskHolder.class.getCanonicalName())){
//                    Object executableObject = task.getParameter(Executable.EXECUTABLE);
//                    if (executableObject != null) {
//                        Executable executable = (Executable) executableObject;
//
//                        HashMap<String, String> portNames = executable.getPorts();
//
//                        String tasksPortName = portNames.get(node.getName());
//
//                        if(tasksPortName != null){
//                            System.out.println("looking for " + portName + " found " + tasksPortName);
//                            if(tasksPortName.equals(portName)){
//                                System.out.println("taskgraph node " +
//                                        taskGraph.getNodeIndex(node.getBottomLevelNode()));
//                                return String.valueOf(taskGraph.getNodeIndex(node.getBottomLevelNode()));
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        return "";
//    }

    /**
     * Write configuration resource to file.
     *
     * @param configurationResource the configuration resource
     * @param file the file
     * @param outputLocation the output location
     * @return the file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static File writeConfigurationResourceToFile(ConfigurationResource configurationResource, File file, File outputLocation) throws IOException {
        String longName = configurationResource.getBundleFile().getFilename();
        if (file == null) {
            file = new File(outputLocation, longName.substring(longName.lastIndexOf("/") + 1));
        }
        System.out.println("   >> Made : " + file.getAbsolutePath());

        return DataUtils.extractToFile(configurationResource, file);
    }

    /* (non-Javadoc)
     * @see org.shiwa.desktop.data.transfer.ExecutionListener#digestWorkflow(java.io.File, java.io.File, org.shiwa.desktop.data.description.handler.TransferSignature)
     */
    @Override
    public void digestWorkflow(final File workflowDefinitionFile, final File fgiBundleFile, final TransferSignature signature) {
        devLog.debug("Importing a bundle");

        if (receivedListener != null) {
            receivedListener.dispose();
        }
        if(shiwaDesktop != null){
            shiwaDesktop.dispose();
            shiwaDesktop = null;
        }

        try {
            if (GUIEnv.getApplicationFrame() != null) {
                final String workflowType = WorkflowUtils.getWorkflowType(workflowDefinitionFile, signature);
                if (workflowType == null) {
                    System.out.println("No workflow type detected");
                } else {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {

                            try{

//                                TrianaProgressBar trianaProgressBar =
//                                        new TrianaProgressBar("Loading " + workflowType + " workflow", false);
//                                trianaProgressBar.start();

                                TaskGraph taskGraph;

                                if (workflowType.equals(AddonUtils.TASKGRAPH_FORMAT)) {
                                    System.out.println("Taskgraph");
                                    taskGraph = TaskGraphFileHandler.openTaskgraph(workflowDefinitionFile, true);
                                } else if (workflowType.equals(AddonUtils.IWIR_FORMAT)) {
                                    System.out.println("IWIR");
                                    IWIR iwir = new IWIR(workflowDefinitionFile);
                                    taskGraph = new ImportIwir().taskFromIwir(iwir, fgiBundleFile);

                                    taskGraph = GUIEnv.getApplicationFrame().addParentTaskGraphPanel(taskGraph);

                                } else if (workflowType.equals(AddonUtils.DAX_FORMAT)) {
                                    taskGraph = new DaxReader().importWorkflow(workflowDefinitionFile, TaskGraphManager.createTaskGraph().getProperties());
                                    taskGraph = GUIEnv.getApplicationFrame().addParentTaskGraphPanel(taskGraph);
                                } else {
                                    System.out.println("Failed to recognise type : " + workflowType);
                                    return;
                                }

                                if (signature.hasConfiguration()) {
                                    createConfigUnit(taskGraph, getIOConfigFromSignature(taskGraph, signature));
                                }
//                                trianaProgressBar.disposeProgressBar();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
                    thread.start();
                }
            } else {
                devLog.debug("No gui found, running headless");
                XMLReader reader = new XMLReader(new FileReader(workflowDefinitionFile));
                Tool tool = reader.readComponent(trianaInstance.getProperties());
                if (tool instanceof TaskGraph) {
                    TaskGraph taskGraph = (TaskGraph) tool;
                    IoConfiguration io = getIOConfigFromSignature(taskGraph, signature);

                    DocumentHandler handler = new DocumentHandler();
                    new IoHandler().serialize(handler, io);
                    handler.output(System.out, true);

                    exec(taskGraph, io);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /* (non-Javadoc)
     * @see org.shiwa.desktop.data.transfer.ExecutionListener#ignoreBundle()
     */
    @Override
    public boolean ignoreBundle() {
        return false;
    }


    /**
     * Execute workflow in gui.
     *
     * @param toolName the tool name
     * @param tool the tool
     * @param signature the signature
     * @throws TaskGraphException the task graph exception
     * @throws SchedulerException the scheduler exception
     */
    private void executeWorkflowInGUI(String toolName, Tool tool, TransferSignature signature) throws TaskGraphException, SchedulerException {
        TrianaRun runner = new TrianaRun(tool);
        IoConfiguration ioc = getIOConfigFromSignature((TaskGraph) tool, signature);
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

    /**
     * Exec.
     *
     * @param loadedTask the loaded task
     * @param conf the conf
     * @throws IOException Signals that an I/O exception has occurred.
     */
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

    /**
     * Creates the config unit.
     *
     * @param taskgraph the taskgraph
     * @param ioConfiguration the io configuration
     */
    private void createConfigUnit(TaskGraph taskgraph, IoConfiguration ioConfiguration) {
        try {
            IoHandler handler = new IoHandler();
            DocumentHandler documentHandler = new DocumentHandler();

            handler.serialize(documentHandler, ioConfiguration);
            documentHandler.output(System.out, true);
            NodeMappings mappings = handler.map(ioConfiguration, taskgraph);

            if (mappings != null) {

                Node[] inputNodes = new Node[taskgraph.getInputNodeCount()];
                for (int i = 0; i < taskgraph.getInputNodes().length; i++) {
                    Node inputNode = taskgraph.getInputNode(i);
                    inputNodes[i] = inputNode.getTopLevelNode();
                }

                Tool varTool = getVarTool(taskgraph.getProperties());
                varTool.setParameter("configSize", inputNodes.length);

                Iterator<Integer> itr = mappings.iterator();
                while (itr.hasNext()) {
                    Integer integer = itr.next();
                    Object val = mappings.getValue(integer);
                    varTool.setParameter("var" + integer.toString(), val);
                }

                Task varTask = taskgraph.createTask(varTool);

                devLog.debug("Data mappings size : " + mappings.getMap().size());
                Iterator<Integer> it = mappings.iterator();
                while (it.hasNext()) {
                    Integer integer = it.next();
//                    Object val = mappings.getValue(integer);
//                    devLog.debug("Data : " + val.toString() + " will be sent to input number " + integer);
                    Node taskNode = getNodeInScope(inputNodes[integer], taskgraph);
                    Node addedNode = varTask.addDataOutputNode();

                    taskgraph.connect(addedNode, taskNode);
//                    varTask.setParameter("var" + integer.toString(), val);
                }

                Object o = varTask.getParameter("configSize");
                int configSize = 0;
                if (o instanceof Integer) {
                    configSize = (Integer) o;
                }
                System.out.println("Multiple output config task added " + configSize);

            } else {
                devLog.debug("Mappings was null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the received listener.
     *
     * @param receivedListener the new received listener
     */
    public void setReceivedListener(DefaultBundleReceivedListener receivedListener) {
        this.receivedListener = receivedListener;
    }

    /**
     * Gets the node in scope.
     *
     * @param inputNode the input node
     * @param taskGraph the task graph
     * @return the node in scope
     */
    private Node getNodeInScope(Node inputNode, TaskGraph taskGraph) {
        Node scopeNode = inputNode.getTopLevelNode();
        while (scopeNode.getTask().getParent() != taskGraph && scopeNode != null) {
            scopeNode = scopeNode.getChildNode();
        }
        return scopeNode;
    }

    /**
     * Gets the var tool.
     *
     * @param properties the properties
     * @return the var tool
     * @throws TaskException the task exception
     * @throws ProxyInstantiationException the proxy instantiation exception
     */
    private Tool getVarTool(TrianaProperties properties) throws TaskException, ProxyInstantiationException {
        ToolImp varTool = new ToolImp(properties);
        varTool.setToolPackage(VariableDummyUnit.class.getPackage().getName());
        varTool.setProxy(new JavaProxy(VariableDummyUnit.class.getSimpleName(), VariableDummyUnit.class.getPackage().getName()));
        varTool.setToolName("Configuration");
        TaskLayoutUtils.setPosition(varTool, new TPoint(1, 1));
        return varTool;
    }

    /**
     * Adds the shiwa desktop.
     *
     * @param shiwaDesktop the shiwa desktop
     */
    public void addSHIWADesktop(SHIWADesktop shiwaDesktop) {
        this.shiwaDesktop = shiwaDesktop;
    }
}
