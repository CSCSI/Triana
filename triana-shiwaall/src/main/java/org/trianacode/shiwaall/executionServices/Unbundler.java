package org.trianacode.shiwaall.executionServices;

import org.shiwa.desktop.data.description.SHIWABundle;
import org.shiwa.desktop.data.description.bundle.BundleFile;
import org.shiwa.desktop.data.description.core.ExecutionMapping;
import org.shiwa.desktop.data.description.core.WorkflowImplementation;
import org.shiwa.desktop.data.description.handler.TransferPort;
import org.shiwa.desktop.data.description.handler.TransferSignature;
import org.shiwa.desktop.data.description.resource.ConfigurationResource;
import org.shiwa.desktop.data.description.resource.ReferableResource;
import org.shiwa.desktop.data.description.workflow.OutputPort;
import org.shiwa.desktop.data.description.workflow.SHIWAProperty;
import org.shiwa.desktop.data.util.DataUtils;
import org.shiwa.fgi.iwir.IWIR;
import org.trianacode.TrianaInstance;
import org.trianacode.enactment.AddonUtils;
import org.trianacode.enactment.Exec;
import org.trianacode.enactment.addon.ExecutionAddon;
import org.trianacode.enactment.io.IoConfiguration;
import org.trianacode.enactment.io.IoHandler;
import org.trianacode.enactment.io.IoMapping;
import org.trianacode.enactment.io.IoType;
import org.trianacode.enactment.logging.stampede.StampedeLog;
import org.trianacode.shiwaall.bundle.ShiwaBundleHelper;
import org.trianacode.shiwaall.iwir.importer.utils.ImportIwir;
import org.trianacode.taskgraph.*;
import org.trianacode.taskgraph.imp.TaskFactoryImp;
import org.trianacode.taskgraph.imp.TaskImp;
import org.trianacode.taskgraph.proxy.ProxyInstantiationException;
import org.trianacode.taskgraph.ser.DocumentHandler;
import org.trianacode.taskgraph.ser.XMLReader;
import org.trianacode.taskgraph.tool.Tool;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

// TODO: Auto-generated Javadoc
//import org.shiwa.desktop.data.description.core.Configuration;

//import org.shiwa.desktop.data.description.handler.Dependency;

/**
* Created by IntelliJ IDEA.
* User: Ian Harvey
* Date: 27/10/2011
* Time: 13:45
* To change this template use File | Settings | File Templates.
*/
public class Unbundler implements ExecutionAddon {
//    private ArrayList<Configuration> configurationList;
//    private ArrayList<WorkflowImplementation> workflowImplementations;
//    private ArrayList<Configuration> environmentConfigurationList;

    //    File configFile;
    /** The tool. */
Tool tool;
    //    private WorkflowImplementation workflowImplementation;
//    private boolean bundleInit = false;
//    private SHIWABundle shiwaBundle;
    /** The output files. */
    private ArrayList<File> outputFiles;
    //    private String workflowFilePath;
    /** The shiwa bundle helper. */
    private ShiwaBundleHelper shiwaBundleHelper;
//    private UUID parentUUID = null;

    /**
 * Instantiates a new unbundler.
 */
public Unbundler() {
    }

    /**
     * Instantiates a new unbundler.
     *
     * @param shiwaBundleHelper the shiwa bundle helper
     */
    public Unbundler(ShiwaBundleHelper shiwaBundleHelper) {
        this.shiwaBundleHelper = shiwaBundleHelper;
    }

//    private void initBundle(String workflowFilePath) throws SHIWADesktopIOException {
//        this.workflowFilePath = workflowFilePath;
//        shiwaBundle = new SHIWABundle(new File(workflowFilePath));
//        readBundle();
//    }
//
//    public void initBundle(SHIWABundle shiwaBundle) {
//        this.shiwaBundle = shiwaBundle;
//        readBundle();
//    }

//    public void readBundle() {
//        workflowImplementations = new ArrayList<WorkflowImplementation>();
//
//        configurationList = new ArrayList<Configuration>();
//        environmentConfigurationList = new ArrayList<Configuration>();
//
//        AggregatedResource aggregatedResource = shiwaBundle.getAggregatedResource();
//
//        if (aggregatedResource instanceof AbstractWorkflow) {
//            for (AggregatedResource resource : aggregatedResource.getAggregatedResources()) {
//                if (resource instanceof WorkflowImplementation) {
//                    workflowImplementations.add((WorkflowImplementation) resource);
//                } else if (resource instanceof Configuration) {
//                    Configuration configuration = (Configuration) resource;
//                    if (configuration.getType() == Configuration.ConfigType.DATA_CONFIGURATION) {
//                        configurationList.add(configuration);
//                    }
//                    if (configuration.getType() == Configuration.ConfigType.ENVIRONMENT_CONFIGURATION) {
//                        environmentConfigurationList.add(configuration);
//                    }
//                }
//            }
//
//        } else if (aggregatedResource instanceof WorkflowImplementation) {
//            workflowImplementations.add((WorkflowImplementation) aggregatedResource);
//
//            for (AggregatedResource resource : aggregatedResource.getAggregatedResources()) {
//                if (resource instanceof AbstractWorkflow) {
//
//                } else if (resource instanceof Configuration) {
//                    Configuration configuration = (Configuration) resource;
//                    if (configuration.getType() == Configuration.ConfigType.DATA_CONFIGURATION) {
//                        configurationList.add(configuration);
//                    }
//                    if (configuration.getType() == Configuration.ConfigType.ENVIRONMENT_CONFIGURATION) {
//                        environmentConfigurationList.add(configuration);
//                    }
//                }
//            }
//        }
//
//
//        bundleInit = true;
//    }

    /* (non-Javadoc)
 * @see org.trianacode.enactment.addon.ExecutionAddon#getTool(org.trianacode.TrianaInstance, java.lang.String)
 */
@Override
    public Tool getTool(TrianaInstance instance, String workflowFilePath) throws IOException, TaskGraphException, ProxyInstantiationException, JAXBException {
//        initBundle(workflowFilePath);
        return getTool(instance);
    }

    /**
     * Gets the tool.
     *
     * @param instance the instance
     * @return the tool
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws TaskGraphException the task graph exception
     * @throws ProxyInstantiationException the proxy instantiation exception
     * @throws JAXBException the jAXB exception
     */
    private Tool getTool(TrianaInstance instance) throws IOException, TaskGraphException, ProxyInstantiationException, JAXBException {
//        workflowImplementation = chooseImp();
        WorkflowImplementation workflowImplementation = shiwaBundleHelper.getWorkflowImplementation();
        if (workflowImplementation != null) {

//            byte[] definitionBytes = workflowImplementation.getDefinition().getBytes();
            String definitionLocation = workflowImplementation.getDefinition().getSystemPath();
            File definitionFile = new File(definitionLocation);

            if (workflowImplementation.getEngine().equalsIgnoreCase("Triana")) {
                XMLReader reader = new XMLReader(new InputStreamReader(new FileInputStream(definitionFile)));
                tool = reader.readComponent(instance.getProperties());

            } else if (workflowImplementation.getLanguage().getShortId().equalsIgnoreCase("IWIR")) {

//                IWIR iwir = new IWIR(shiwaBundleHelper.writeDefinitionFile());
                IWIR iwir = new IWIR(definitionFile);
                ImportIwir iwirImporter = new ImportIwir();
                tool = iwirImporter.taskFromIwir(iwir, null);

                System.out.println("Definition name " + workflowImplementation.getDefinition().getFilename());
                System.out.println("Toolname " + tool.getToolName());
                tool.setToolName(workflowImplementation.getDefinition().getFilename());
                System.out.println("Toolname " + tool.getToolName());

            } else {
                System.out.println("Bundle contains IWIR workflow, but no importer is present");
//                System.exit(1);
            }
        }
        return tool;
    }

    /* (non-Javadoc)
     * @see org.trianacode.enactment.addon.ExecutionAddon#getConfigFile()
     */
    @Override
    public File getConfigFile() throws IOException {

        if (shiwaBundleHelper.hasDataConfiguration()) {
            TransferSignature transferSignature = shiwaBundleHelper.createDefaultTransferSignature();
            File configFile = getIOConfigFromSignature(transferSignature);
            System.out.println(configFile.getAbsolutePath());
            return configFile;
        } else {
            return null;
        }

    }

    /**
     * Store shiwa property.
     *
     * @param executionProperties the execution properties
     * @param shiwaBundleHelper the shiwa bundle helper
     * @param key the key
     */
    private void storeShiwaProperty(HashMap<String, String> executionProperties, ShiwaBundleHelper shiwaBundleHelper, String key) {
        SHIWAProperty property = shiwaBundleHelper.getShiwaProperty(key);
        if (property != null) {
            executionProperties.put(key, property.getValue());
        }
    }

    /* (non-Javadoc)
     * @see org.trianacode.enactment.addon.ExecutionAddon#execute(org.trianacode.TrianaInstance, java.util.List)
     */
    @Override
    public void execute(TrianaInstance engine, List<String> pluginArguments) throws Exception {
//        String[] correctedArgs = null;

        shiwaBundleHelper = new ShiwaBundleHelper(pluginArguments.get(1));
        shiwaBundleHelper.prepareEnvironmentDependencies();
//        parentUUID = shiwaBundleHelper.getParentUUID();

        HashMap<String, String> executionProperties = new HashMap<String, String>();
        storeShiwaProperty(executionProperties, shiwaBundleHelper, StampedeLog.JOB_ID);
        storeShiwaProperty(executionProperties, shiwaBundleHelper, StampedeLog.PARENT_UUID_STRING);
        storeShiwaProperty(executionProperties, shiwaBundleHelper, StampedeLog.RUN_UUID_STRING);
        storeShiwaProperty(executionProperties, shiwaBundleHelper, StampedeLog.JOB_INST_ID);

//        runUUID = shiwaBundleHelper.getRunUUID();

        tool = getTool(engine);
        serializeOutputs((TaskGraph) tool);
        File configFile = getConfigFile();

        if (tool != null) {

            try {
                Exec execEngine = new Exec(null);

                System.out.println(executionProperties.toString());
                execEngine.setExecutionProperties(executionProperties);
//            execEngine.setParentUUID(parentUUID);

                timeOutput("KIERAN! Executing bundle at t:");

                if (configFile != null) {
                    execEngine.execute(tool, configFile.getAbsolutePath());
                } else {
                    execEngine.execute(tool, null);
                }

                timeOutput("KIERAN! Completing bundle at t:");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (engine != null) {
            engine.shutdown(-1);
        }

        System.out.println(createOutputBundle(pluginArguments.get(2)).getAbsolutePath());
    }

    /* (non-Javadoc)
     * @see org.trianacode.enactment.addon.ExecutionAddon#execute(org.trianacode.enactment.Exec, org.trianacode.TrianaInstance, java.lang.String, java.lang.Object, java.lang.Object, java.lang.String[])
     */
    @Override
    public void execute(Exec execEngine, TrianaInstance instance, String bundlePath, Object workflowObject, Object inputData, String[] args) throws Exception {
        tool = getTool(instance, bundlePath);
        File configFile = getConfigFile();

        if (tool != null) {
            if (configFile != null) {
//                execEngine.setParentUUID(parentUUID);
                execEngine.execute(tool, configFile.getAbsolutePath());
            } else {
                execEngine.execute(tool, null);
            }
        }
    }


    /**
     * Creates the output bundle.
     *
     * @param outputPath the output path
     * @return the file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public File createOutputBundle(String outputPath) throws IOException {
//        Configuration execConfig = new Configuration(Configuration.ConfigType.EXECUTION_CONFIGURATION);
        ExecutionMapping execConfig = new ExecutionMapping();
        ArrayList<ConfigurationResource> configurationResourceArrayList = new ArrayList<ConfigurationResource>();

        ArrayList<File> outputs = getOutputFiles();
        ArrayList<OutputPort> outputPorts = new ArrayList<OutputPort>();

        for (ReferableResource resource : shiwaBundleHelper.getWorkflowImplementation().getSignature().getPorts()) {
            if (resource instanceof OutputPort) {
                outputPorts.add((OutputPort) resource);
            }
        }

        for (int i = 0; i < outputs.size(); i++) {
            //TODO check name
            ConfigurationResource configurationResource = new ConfigurationResource(outputPorts.get(i));
            File outputFile = outputs.get(i);
            System.out.println(outputFile.getAbsolutePath());
            BundleFile bf = DataUtils.createBundleFile(outputFile, execConfig.getId() + "/");
            bf.setType(BundleFile.FileType.DATA_FILE);
            execConfig.getBundleFiles().add(bf);
            configurationResource.setBundleFile(bf);
            configurationResource.setRefType(ConfigurationResource.RefTypes.FILE_REF);
            execConfig.addResourceRef(configurationResource);

            configurationResourceArrayList.add(configurationResource);
        }

        execConfig.setResources(configurationResourceArrayList);

        shiwaBundleHelper.getWorkflowImplementation().getAggregatedResources().add(execConfig);

        File outputBundle;
        if (outputPath == null || outputPath.equals("")) {
            outputBundle = File.createTempFile("bundle", ".tmp");
        } else {
            outputBundle = new File(outputPath);
        }

        System.out.println("Output bundle : " + outputBundle.getAbsolutePath());
//            File bundleFile = DataUtils.bundle(tempBundle,  shiwaBundle.getAggregatedResource());

        return shiwaBundleHelper.saveBundle(outputBundle);
    }

    /**
     * Gets the iO config from signature.
     *
     * @param signature the signature
     * @return the iO config from signature
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private File getIOConfigFromSignature(TransferSignature signature) throws IOException {

        ArrayList<IoMapping> inputMappings = new ArrayList<IoMapping>();

        List<TransferPort> inputPorts = signature.getInputs();
        for (TransferPort inputPort : inputPorts) {
            if (inputPort.getValue() != null) {
                String portName = inputPort.getName();
                String portNumberString = portName.substring(portName.indexOf("_") + 1);

                String value = inputPort.getValue();
                boolean reference = inputPort.getValueType() == TransferSignature.ValueType.BUNDLED_FILE;
                if (reference) {
                    try {
//                        value = shiwaBundle.getTempEntry(value).getAbsolutePath();
                        value = shiwaBundleHelper.getTempEntry(value).getAbsolutePath();
                    } catch (Exception ignored) {
                    }
                }

                IoMapping ioMapping = new IoMapping(new IoType(value, "string", false), portNumberString);
                inputMappings.add(ioMapping);
            }
        }

        IoConfiguration conf = new IoConfiguration(signature.getName(), "0.1", inputMappings, new ArrayList<IoMapping>());

        List<IoMapping> mappings = conf.getInputs();
        for (IoMapping mapping : mappings) {
            System.out.println("  mapping:");
            System.out.println("    name:" + mapping.getNodeName());
            System.out.println("    type:" + mapping.getIoType().getType());
            System.out.println("    val:" + mapping.getIoType().getValue());
            System.out.println("    ref:" + mapping.getIoType().isReference());
        }

        DocumentHandler documentHandler = new DocumentHandler();
        new IoHandler().serialize(documentHandler, conf);
        File tempConfFile = File.createTempFile(conf.getToolName() + "_confFile", ".dat");
        documentHandler.output(new FileWriter(tempConfFile), true);

        return tempConfFile;
    }

//    @Override
//    public File getWorkflowFile(String bundlePath) throws IOException {
////        if (!bundleInit) {
////            initBundle(bundlePath);
////        }
////        return bytesToFile(workflowImplementation.getDefinition().getBytes());
//        return shiwaBundleHelper.writeDefinitionFile();
//    }
//
//
//
//    @Override
//    public void setWorkflowFile(String bundlePath, File file) throws IOException {
////        if (!bundleInit) {
////            initBundle(bundlePath);
////        }
//
////        boolean previousDef = false;
////        String language = "";
////        byte[] defBytes = null;
////
////        if (workflowImplementation != null) {
////            if (workflowImplementation.getDefinition() != null) {
////                language = workflowImplementation.getLanguage().getShortId();
////                defBytes = workflowImplementation.getDefinition().getBytes();
////                previousDef = true;
////            }
////
////            BundleFile definitionBundleFile = new BundleFile(new FileInputStream(file), file.getName());
////            workflowImplementation.setDefinition(definitionBundleFile);
////
////            if (previousDef) {
////                BundleFile backupDefinition = new BundleFile(language, defBytes, "", BundleFile.FileType.BUNDLE_FILE);
////                workflowImplementation.getBundleFiles().add(backupDefinition);
////            }
////        }
//
//    }
//
//    @Override
//    public File getConfigFile(String bundlePath) {
//        return null;
//    }
//
//    @Override
//    public Object getWorkflowObject(String bundlePath) {
//        return null;
//    }
//
//    @Override
//    public File saveBundle(String fileName) throws IOException {
////        if (bundleInit) {
////            shiwaBundle.getAggregatedResource().setBaseURI(DataUtils.BASE_URI);
////            DataUtils.bundle(new File(fileName), shiwaBundle.getAggregatedResource());
////        }
//        return shiwaBundleHelper.saveBundle(new File(fileName));
//    }

    /**
 * Gets the tool.
 *
 * @param trianaInstance the triana instance
 * @param shiwaBundle the shiwa bundle
 * @return the tool
 * @throws IOException Signals that an I/O exception has occurred.
 * @throws TaskGraphException the task graph exception
 * @throws ProxyInstantiationException the proxy instantiation exception
 * @throws JAXBException the jAXB exception
 */
public Tool getTool(TrianaInstance trianaInstance, SHIWABundle shiwaBundle) throws IOException, TaskGraphException, ProxyInstantiationException, JAXBException {
//        initBundle(shiwaBundle);
        return getTool(trianaInstance);
    }

    /* (non-Javadoc)
     * @see org.trianacode.enactment.addon.CLIaddon#getServiceName()
     */
    @Override
    public String getServiceName() {
        return "UnBundler";
    }

    /* (non-Javadoc)
     * @see org.trianacode.enactment.addon.CLIaddon#getLongOption()
     */
    @Override
    public String getLongOption() {
        return "unbundle";
    }

    /* (non-Javadoc)
     * @see org.trianacode.enactment.addon.CLIaddon#getShortOption()
     */
    @Override
    public String getShortOption() {
        return "b";
    }

    /* (non-Javadoc)
     * @see org.trianacode.enactment.addon.CLIaddon#getDescription()
     */
    @Override
    public String getDescription() {
        return "SHIWA Bundle Extraction in Triana";
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getServiceName();
    }

    /* (non-Javadoc)
     * @see org.trianacode.enactment.addon.CLIaddon#getUsageString()
     */
    @Override
    public String getUsageString() {
        return "Usage for " + getLongOption() + ".\n" +
                "eg triana.sh -n -p " + getLongOption() + " input-bundle.zip output-bundle.zip" +
                "-n for headless triana" +
                "-p to declare a plugin to use" +
                "input-bundle is the bundle which will be executed." +
                "output-bundle is the name of the bundle created after execution, which holds the output data."
                ;
    }

    /**
     * Serialize outputs.
     *
     * @param taskGraph the task graph
     * @throws TaskException the task exception
     */
    public void serializeOutputs(TaskGraph taskGraph) throws TaskException {
//        if (bundleInit) {
        outputFiles = new ArrayList<File>();
        for (Node outputNode : taskGraph.getOutputNodes()) {
            Node toolNode = outputNode.getTopLevelNode();

            Task outputTask = outputNode.getTask();
            String[] nodeOutputTypes = outputTask.getDataOutputTypes(toolNode.getAbsoluteNodeIndex());
            String outputType;
            if (nodeOutputTypes != null) {
                outputType = nodeOutputTypes[0];
            } else {
                String[] taskOutputTypes = TaskGraphUtils.getAllDataOutputTypes(outputTask);
                if (taskOutputTypes != null) {
                    outputType = taskOutputTypes[0];
                } else {
                    outputType = Object.class.getCanonicalName();
                }
            }

            try {
                if (outputType.equals(String.class.getCanonicalName())) {
                    addStringTool(toolNode, taskGraph);
                } else if (outputType.equals(File.class.getCanonicalName())) {
                    addFileRenameTool(toolNode, taskGraph);
                } else {
                    addSerializeTool(toolNode, taskGraph);
                }
            } catch (CableException e) {
                e.printStackTrace();
            } catch (ProxyInstantiationException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Adds the file rename tool.
     *
     * @param toolNode the tool node
     * @param taskGraph the task graph
     * @throws CableException the cable exception
     * @throws TaskException the task exception
     * @throws ProxyInstantiationException the proxy instantiation exception
     */
    private void addFileRenameTool(Node toolNode, TaskGraph taskGraph) throws CableException, TaskException, ProxyInstantiationException {
        String fileName = "Output_" + toolNode.getAbsoluteNodeIndex();

        Task fileTask = new TaskImp(
                AddonUtils.makeTool(
                        "RenameFile",
                        "common.file",
                        fileName,
                        taskGraph.getProperties()),
                new TaskFactoryImp(),
                false
        );
        Task task = taskGraph.createTask(fileTask, false);

        File file = createFileInRuntimeFolder(fileName);
        task.setParameter("filePath", file.getAbsolutePath());
        outputFiles.add(file);

        Node inNode = task.addDataInputNode();
        taskGraph.connect(toolNode, inNode);
        fileIterator++;
    }

    /**
     * Gets the output files.
     *
     * @return the output files
     */
    public ArrayList<File> getOutputFiles() {
        return outputFiles;
    }


    /** The file iterator. */
    private int fileIterator = 0;

    /**
     * Adds the serialize tool.
     *
     * @param toolNode the tool node
     * @param taskGraph the task graph
     * @throws CableException the cable exception
     * @throws ProxyInstantiationException the proxy instantiation exception
     * @throws TaskException the task exception
     */
    private void addSerializeTool(Node toolNode, TaskGraph taskGraph) throws CableException, ProxyInstantiationException, TaskException {
        String serialName = "Output_" + toolNode.getAbsoluteNodeIndex();

        Task fileTask = new TaskImp(
                AddonUtils.makeTool(
                        "Serialize",
                        "common.output",
                        serialName,
                        taskGraph.getProperties()),
                new TaskFactoryImp(),
                false
        );
        Task task = taskGraph.createTask(fileTask, false);

        File file = createFileInRuntimeFolder(serialName);
        task.setParameter("filename", file.getAbsolutePath());
        outputFiles.add(file);
        task.setParameter("type", "Java Serialize");

        Node inNode = task.addDataInputNode();
        taskGraph.connect(toolNode, inNode);
        fileIterator++;
    }

    /**
     * Adds the string tool.
     *
     * @param toolNode the tool node
     * @param taskGraph the task graph
     * @throws CableException the cable exception
     * @throws ProxyInstantiationException the proxy instantiation exception
     * @throws TaskException the task exception
     */
    private void addStringTool(Node toolNode, TaskGraph taskGraph) throws CableException, ProxyInstantiationException, TaskException {
        String stringName = "Output_" + toolNode.getAbsoluteNodeIndex();

        Task fileTask = new TaskImp(
                AddonUtils.makeTool(
                        "StringToFile",
                        "common.file",
                        stringName,
                        taskGraph.getProperties()),
                new TaskFactoryImp(),
                false
        );
        Task task = taskGraph.createTask(fileTask, false);

        File file = createFileInRuntimeFolder(stringName);
        task.setParameter("filename", file.getAbsolutePath());
        outputFiles.add(file);

        Node inNode = task.addDataInputNode();
        taskGraph.connect(toolNode, inNode);
        fileIterator++;
    }

    /**
     * Creates the file in runtime folder.
     *
     * @param filename the filename
     * @return the file
     */
    private File createFileInRuntimeFolder(String filename) {
        return new File(outputLocation, filename);
    }

    /**
     * Sets the runtime output folder.
     *
     * @param outputFolder the new runtime output folder
     */
    public void setRuntimeOutputFolder(File outputFolder) {
        outputLocation = outputFolder;
        outputLocation.mkdirs();
    }

    /** The output location. */
    private File outputLocation = null;

    /**
     * Time output.
     *
     * @param s the s
     */
    public static void timeOutput(String s){
        String d;
        Format formatter;
        Date date = new Date();
        formatter = new SimpleDateFormat("hh:mm:ss");
        d = formatter.format(date);
        System.out.println(s+d);
    }
}
