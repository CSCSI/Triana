package org.trianacode.shiwa.executionServices;

import org.shiwa.desktop.data.description.SHIWABundle;
import org.shiwa.desktop.data.description.bundle.BundleFile;
import org.shiwa.desktop.data.description.core.AbstractWorkflow;
import org.shiwa.desktop.data.description.core.Configuration;
import org.shiwa.desktop.data.description.core.WorkflowImplementation;
import org.shiwa.desktop.data.description.handler.TransferPort;
import org.shiwa.desktop.data.description.handler.TransferSignature;
import org.shiwa.desktop.data.description.resource.AggregatedResource;
import org.shiwa.desktop.data.description.resource.ConfigurationResource;
import org.shiwa.desktop.data.description.resource.ReferableResource;
import org.shiwa.desktop.data.description.workflow.InputPort;
import org.shiwa.desktop.data.description.workflow.OutputPort;
import org.shiwa.desktop.data.util.DataUtils;
import org.shiwa.desktop.data.util.SHIWADesktopIOException;
import org.shiwa.fgi.iwir.IWIR;
import org.trianacode.TrianaInstance;
import org.trianacode.enactment.Exec;
import org.trianacode.enactment.addon.BundleAddon;
import org.trianacode.enactment.addon.ExecutionAddon;
import org.trianacode.enactment.io.IoConfiguration;
import org.trianacode.enactment.io.IoHandler;
import org.trianacode.enactment.io.IoMapping;
import org.trianacode.enactment.io.IoType;
import org.trianacode.shiwa.iwir.importer.utils.ImportIwir;
import org.trianacode.taskgraph.TaskGraphException;
import org.trianacode.taskgraph.proxy.ProxyInstantiationException;
import org.trianacode.taskgraph.ser.DocumentHandler;
import org.trianacode.taskgraph.ser.XMLReader;
import org.trianacode.taskgraph.tool.Tool;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

//import org.shiwa.desktop.data.description.handler.Dependency;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 27/10/2011
 * Time: 13:45
 * To change this template use File | Settings | File Templates.
 */
public class Unbundler implements BundleAddon, ExecutionAddon {
    private ArrayList<Configuration> configurationList;
    private ArrayList<WorkflowImplementation> workflowImplementations;
    File configFile;
    Tool tool;
    private WorkflowImplementation workflowImplementation;
    private boolean bundleInit = false;
    private SHIWABundle shiwaBundle;

    @Override
    public String getServiceName() {
        return "UnBundler";
    }

    @Override
    public String getLongOption() {
        return "unbundle";
    }

    @Override
    public String getShortOption() {
        return "b";
    }

    @Override
    public String getDescription() {
        return "SHIWA Bundle Extraction in Triana";
    }

    @Override
    public void execute(Exec execEngine, TrianaInstance instance, String bundlePath, Object workflowObject, Object inputData, String[] args) throws Exception {
        tool = getTool(instance, bundlePath);
        configFile = getConfigFile();

        if (tool != null) {
            if (configFile != null) {
                execEngine.execute(tool, configFile.getAbsolutePath());
            } else {
                execEngine.execute(tool, null);
            }
        }
    }

    @Override
    public Tool getTool(TrianaInstance instance, String workflowFilePath) throws IOException, TaskGraphException, ProxyInstantiationException {
        initBundle(workflowFilePath);

        workflowImplementation = chooseImp();
        if (workflowImplementation != null) {

            byte[] definitionBytes = workflowImplementation.getDefinition().getBytes();

            if (workflowImplementation.getEngine().equalsIgnoreCase("Triana")) {
                XMLReader reader = new XMLReader(new InputStreamReader(new ByteArrayInputStream(definitionBytes)));
                tool = reader.readComponent(instance.getProperties());

            } else if (workflowImplementation.getLanguage().getShortId().equalsIgnoreCase("IWIR")) {

                IWIR iwir = new IWIR(getWorkflowFile(workflowFilePath));
                ImportIwir iwirImporter = new ImportIwir();
                tool = iwirImporter.taskFromIwir(iwir);

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

    @Override
    public File getConfigFile() throws IOException {
        if (workflowImplementation == null || configurationList.size() < 1) {
            return null;
        } else {
            TransferSignature signature = buildSignature(workflowImplementation, configurationList.get(0));
            configFile = getIOConfigFromSignature(signature);
        }
        return configFile;
    }

    private WorkflowImplementation chooseImp() {
        WorkflowImplementation chosenImp = null;
        for (WorkflowImplementation implementation : workflowImplementations) {
            System.out.println(implementation.getEngine());
            if (implementation.getEngine().equalsIgnoreCase("Triana")) {
                chosenImp = implementation;
            }
        }

        if (chosenImp == null) {
            for (WorkflowImplementation implementation : workflowImplementations) {
                if (implementation.getLanguage().getShortId().equalsIgnoreCase("IWIR")) {
                    chosenImp = implementation;
                }
            }
        }
        return chosenImp;
    }

    private TransferSignature buildSignature(WorkflowImplementation workflow, Configuration configuration) {
        TransferSignature signature = new TransferSignature();

        signature.setName(workflow.getDefinition().getFilename());
        System.out.println("Signatures name " + signature.getName());

        for (ReferableResource referableResource : workflow.getSignature().getPorts()) {
            if (referableResource instanceof InputPort) {
                String value = null;
                boolean isReference = false;

                if (configuration != null) {
                    for (ConfigurationResource portRef : configuration.getResources()) {
                        if (portRef.getReferableResource().getId() == referableResource.getId()) {
                            value = portRef.getValue();
                            isReference = (portRef.getRefType() == ConfigurationResource.RefTypes.FILE_REF);
                        }
                    }
                }

                if (value != null) {
                    if (isReference) {
                        signature.addInputReference(referableResource.getTitle(), referableResource.getDataType(), value);
                    } else {
                        signature.addInput(referableResource.getTitle(), referableResource.getDataType(), value);
                    }
                } else {
                    signature.addInput(referableResource.getTitle(), referableResource.getDataType());
                }
            } else if (referableResource instanceof OutputPort) {
                signature.addOutput(referableResource.getTitle(), referableResource.getDataType());
            }
        }

//        for (Dependency dependency : workflow.getDependencies()) {
////            Constraint constraint = new Constraint(dependency.getTitle(), dependency.getDataType(), dependency.getDescription());
//            Dependency constraint = new Dependency(dependency.getTitle(), dependency.getDataType(), dependency.getDescription());
//
//            if (configuration != null) {
//                for (ConfigurationResource dependencyRef : configuration.getResources()) {
//                    if (dependencyRef.getReferableResource() == dependency) {
//                        if (dependencyRef.getRefType() == ConfigurationResource.RefTypes.FILE_REF) {
////                            constraint.setValueReference(dependencyRef.getValue());
//                            constraint.setValueType(TransferSignature.ValueType.BUNDLED_FILE);
//                        } else {
//                            constraint.setValue(dependencyRef.getValue());
//                        }
//                    }
//                }
//            }
//
//            signature.addConstraint(constraint);
//        }

        if (configuration != null) {
            signature.setHasConfiguration(true);
        }
        return signature;
    }

    private File getIOConfigFromSignature(TransferSignature signature) throws IOException {

        ArrayList<IoMapping> inputMappings = new ArrayList<IoMapping>();

        List<TransferPort> inputPorts = signature.getInputs();
        for (TransferPort inputPort : inputPorts) {
            if (inputPort.getValue() != null) {
                String portName = inputPort.getName();
                String portNumberString = portName.substring(portName.indexOf("_") + 1);

                String value = inputPort.getValue();
                boolean reference = inputPort.getValueType() == TransferSignature.ValueType.BUNDLED_FILE;

                IoMapping ioMapping = new IoMapping(new IoType(value, "string", reference), portNumberString);
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

    private void initBundle(String workflowFilePath) throws SHIWADesktopIOException {
        shiwaBundle = new SHIWABundle(new File(workflowFilePath));
        shiwaBundle.init();

        workflowImplementations = new ArrayList<WorkflowImplementation>();

        configurationList = new ArrayList<Configuration>();

        AggregatedResource aggregatedResource = shiwaBundle.getAggregatedResource();

        if (aggregatedResource instanceof AbstractWorkflow) {
            for (AggregatedResource resource : aggregatedResource.getAggregatedResources()) {
                if (resource instanceof WorkflowImplementation) {
                    workflowImplementations.add((WorkflowImplementation) resource);
                } else if (resource instanceof Configuration) {
                    Configuration configuration = (Configuration) resource;
                    if (configuration.getType() == Configuration.ConfigType.DATA_CONFIGURATION) {
                        configurationList.add(configuration);
                    }
                }
            }

        } else if (aggregatedResource instanceof WorkflowImplementation) {
            workflowImplementations.add((WorkflowImplementation) aggregatedResource);

            for (AggregatedResource resource : aggregatedResource.getAggregatedResources()) {
                if (resource instanceof AbstractWorkflow) {

                } else if (resource instanceof Configuration) {
                    Configuration configuration = (Configuration) resource;
                    if (configuration.getType() == Configuration.ConfigType.DATA_CONFIGURATION) {
                        configurationList.add(configuration);
                    }
                }
            }
        }
        bundleInit = true;
    }

    @Override
    public File getWorkflowFile(String bundlePath) throws IOException {
        if (!bundleInit) {
            initBundle(bundlePath);
        }
        File definitionTempFile = File.createTempFile(workflowImplementation.getDefinition().getFilename(), ".tmp");
        definitionTempFile.deleteOnExit();
        FileOutputStream fileOutputStream = new FileOutputStream(definitionTempFile);
        fileOutputStream.write(workflowImplementation.getDefinition().getBytes());
        fileOutputStream.close();
        return definitionTempFile;
    }

    @Override
    public void setWorkflowFile(String bundlePath, File file) throws IOException {
        if (!bundleInit) {
            initBundle(bundlePath);
        }

        boolean previousDef = false;
        String language = "";
        byte[] defBytes = null;

        if (workflowImplementation != null) {
            if (workflowImplementation.getDefinition() != null) {
                language = workflowImplementation.getLanguage().getShortId();
                defBytes = workflowImplementation.getDefinition().getBytes();
                previousDef = true;
            }

            BundleFile definitionBundleFile = new BundleFile(new FileInputStream(file), file.getName());
            workflowImplementation.setDefinition(definitionBundleFile);

            if (previousDef) {
                BundleFile backupDefinition = new BundleFile(language, defBytes, "", BundleFile.FileType.BUNDLE_FILE);
                workflowImplementation.getBundleFiles().add(backupDefinition);
            }
        }

    }

    @Override
    public File getConfigFile(String bundlePath) {
        return null;
    }

    @Override
    public Object getWorkflowObject(String bundlePath) {
        return null;
    }

    @Override
    public File saveBundle(String fileName) throws IOException {
        if (bundleInit) {
            shiwaBundle.getAggregatedResource().setBaseURI(DataUtils.BASE_URI);
            DataUtils.bundle(new File(fileName), shiwaBundle.getAggregatedResource());
        }
        return null;
    }
}
