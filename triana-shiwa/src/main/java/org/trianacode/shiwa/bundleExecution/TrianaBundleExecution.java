package org.trianacode.shiwa.bundleExecution;

import org.shiwa.desktop.data.description.SHIWABundle;
import org.shiwa.desktop.data.description.core.AbstractWorkflow;
import org.shiwa.desktop.data.description.core.Configuration;
import org.shiwa.desktop.data.description.core.WorkflowImplementation;
import org.shiwa.desktop.data.description.handler.Constraint;
import org.shiwa.desktop.data.description.handler.Port;
import org.shiwa.desktop.data.description.handler.Signature;
import org.shiwa.desktop.data.description.resource.AggregatedResource;
import org.shiwa.desktop.data.description.resource.ConfigurationResource;
import org.shiwa.desktop.data.description.resource.ReferableResource;
import org.shiwa.desktop.data.description.workflow.Dependency;
import org.shiwa.desktop.data.description.workflow.InputPort;
import org.shiwa.desktop.data.description.workflow.OutputPort;
import org.shiwa.desktop.data.util.SHIWADesktopIOException;
import org.shiwa.fgi.iwir.IWIR;
import org.trianacode.TrianaInstance;
import org.trianacode.enactment.Exec;
import org.trianacode.enactment.ExecutionService;
import org.trianacode.enactment.io.IoConfiguration;
import org.trianacode.enactment.io.IoHandler;
import org.trianacode.enactment.io.IoMapping;
import org.trianacode.enactment.io.IoType;
import org.trianacode.shiwa.iwir.importer.utils.ImportIwir;
import org.trianacode.taskgraph.ser.DocumentHandler;
import org.trianacode.taskgraph.ser.XMLReader;
import org.trianacode.taskgraph.tool.Tool;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 27/10/2011
 * Time: 13:45
 * To change this template use File | Settings | File Templates.
 */
public class TrianaBundleExecution implements ExecutionService {
    private ArrayList<Configuration> configurationList;
    private ArrayList<WorkflowImplementation> workflowImplementations;
    File configFile;
    Tool tool;

    @Override
    public String getServiceName() {
        return "Bundle Executor";
    }

    @Override
    public String getLongOption() {
        return "bundle";
    }

    @Override
    public String getShortOption() {
        return "b";
    }

    @Override
    public String getDescription() {
        return "SHIWA Bundle Execution in Triana";
    }

    @Override
    public void execute(Exec execEngine, TrianaInstance engine, String bundlePath, Object workflowObject, Object inputData, String[] args) throws Exception {
        SHIWABundle shiwaBundle = new SHIWABundle(new File(bundlePath));
        initBundle(shiwaBundle);

        WorkflowImplementation workflowImplementation = chooseImp();
        if (workflowImplementation != null) {
            if (configurationList.size() > 0) {
                Signature signature = buildSignature(workflowImplementation, configurationList.get(0));
                configFile = getIOConfigFromSignature(signature);

            }

            byte[] definitionBytes = workflowImplementation.getDefinition().getBytes();

            if (workflowImplementation.getEngine().equalsIgnoreCase("Triana")) {
                XMLReader reader = new XMLReader(new InputStreamReader(new ByteArrayInputStream(definitionBytes)));
                tool = reader.readComponent(engine.getProperties());

            } else if (workflowImplementation.getLanguage().getShortId().equalsIgnoreCase("IWIR")) {

                File definitionTempFile = File.createTempFile(workflowImplementation.getDefinition().getFilename(), ".tmp");
                definitionTempFile.deleteOnExit();
                FileOutputStream fileOutputStream = new FileOutputStream(definitionTempFile);
                fileOutputStream.write(definitionBytes);
                fileOutputStream.close();

                IWIR iwir = new IWIR(definitionTempFile);
                ImportIwir iwirImporter = new ImportIwir();
                tool = iwirImporter.taskFromIwir(iwir);

                System.out.println("Definition name " + workflowImplementation.getDefinition().getFilename());
                System.out.println("Toolname " + tool.getToolName());
                tool.setToolName(workflowImplementation.getDefinition().getFilename());
                System.out.println("Toolname " + tool.getToolName());

            } else {
                System.out.println("Bundle contains IWIR workflow, but no importer is present");
                System.exit(1);
            }

            if (tool != null) {
                if (configFile != null) {
                    execEngine.execute(tool, configFile.getAbsolutePath());
                } else {
                    execEngine.execute(tool, "");
                }
            }

        }
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

    private Signature buildSignature(WorkflowImplementation workflow, Configuration configuration) {
        Signature signature = new Signature();

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

        for (Dependency dependency : workflow.getDependencies()) {
            Constraint constraint = new Constraint(dependency.getTitle(), dependency.getDataType(), dependency.getDescription());

            if (configuration != null) {
                for (ConfigurationResource dependencyRef : configuration.getResources()) {
                    if (dependencyRef.getReferableResource() == dependency) {
                        if (dependencyRef.getRefType() == ConfigurationResource.RefTypes.FILE_REF) {
                            constraint.setValueReference(dependencyRef.getValue());
                        } else {
                            constraint.setValue(dependencyRef.getValue());
                        }
                    }
                }
            }

            signature.addConstraint(constraint);
        }

        if (configuration != null) {
            signature.setHasConfiguration(true);
        }
        return signature;
    }

    private File getIOConfigFromSignature(Signature signature) throws IOException {

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

    private void initBundle(SHIWABundle bundle) throws SHIWADesktopIOException {
        bundle.init();

        workflowImplementations = new ArrayList<WorkflowImplementation>();

        configurationList = new ArrayList<Configuration>();

        AggregatedResource aggregatedResource = bundle.getAggregatedResource();

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
    }


}
