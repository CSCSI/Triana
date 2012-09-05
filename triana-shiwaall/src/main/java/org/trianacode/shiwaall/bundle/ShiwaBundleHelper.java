package org.trianacode.shiwaall.bundle;

import org.shiwa.desktop.data.description.ConcreteBundle;
import org.shiwa.desktop.data.description.SHIWABundle;
import org.shiwa.desktop.data.description.bundle.BundleFile;
import org.shiwa.desktop.data.description.core.*;
import org.shiwa.desktop.data.description.handler.TransferSignature;
import org.shiwa.desktop.data.description.resource.AggregatedResource;
import org.shiwa.desktop.data.description.resource.ConfigurationResource;
import org.shiwa.desktop.data.description.resource.ReferableResource;
import org.shiwa.desktop.data.description.workflow.Dependency;
import org.shiwa.desktop.data.description.workflow.InputPort;
import org.shiwa.desktop.data.description.workflow.OutputPort;
import org.shiwa.desktop.data.description.workflow.SHIWAProperty;
import org.shiwa.desktop.data.util.DataUtils;
import org.shiwa.desktop.data.util.exception.SHIWADesktopIOException;
import org.shiwa.desktop.data.util.properties.Locations;
import org.trianacode.error.ErrorEvent;
import org.trianacode.error.ErrorTracker;
import org.trianacode.shiwaall.handler.BundleUtils;
import org.trianacode.taskgraph.Node;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 10/04/2012
 * Time: 14:28
 * To change this template use File | Settings | File Templates.
 */
public class ShiwaBundleHelper {


    //    private String workflowFilePath;
    /** The concrete bundle. */
    private ConcreteBundle concreteBundle;

//    private UUID parentUUID = null;

    /**
 * Instantiates a new shiwa bundle helper.
 *
 * @param shiwaBundle the shiwa bundle
 * @throws SHIWADesktopIOException the sHIWA desktop io exception
 */
public ShiwaBundleHelper(SHIWABundle shiwaBundle) throws SHIWADesktopIOException {
        if(shiwaBundle instanceof ConcreteBundle){
            this.concreteBundle = (ConcreteBundle) shiwaBundle;
        } else {

        }
//        initialiseController(concreteBundle);
//        init();
    }

//    private void init() {
//        SHIWAProperty parentProperty = getShiwaProperty(parentUUIDstring);
//        if(parentProperty != null){
//            parentUUID = UUID.fromString(parentProperty.getValue());
//        }
////        List<SHIWAProperty> properties = getWorkflowImplementation().getProperties();
////        for (SHIWAProperty property : properties) {
////            if (property.getTitle().equals(parentUUIDstring)) {
////                System.out.println("Found parent uuid " + property.getValue());
////                parentUUID = UUID.fromString(property.getValue());
////            }
////        }
//    }

    /**
 * Instantiates a new shiwa bundle helper.
 *
 * @param bundlePath the bundle path
 * @throws SHIWADesktopIOException the sHIWA desktop io exception
 */
public ShiwaBundleHelper(String bundlePath) throws SHIWADesktopIOException {
        this.concreteBundle = new ConcreteBundle(new File(bundlePath));
//        initialiseController(concreteBundle);
//        init();
    }


    /**
     * Creates the configuration.
     *
     * @param list the list
     * @return the mapping
     */
    public Mapping createConfiguration(List list) {
        if (list.size() > 0) {

            int inputObjectNo = 0;
            Mapping config = new DataMapping();

            for (ReferableResource referableResource : concreteBundle.getPrimaryConcreteTask().getSignature().getPorts()) {
                if (referableResource instanceof InputPort) {
                    ConfigurationResource configurationResource = new ConfigurationResource(referableResource);
                    //TODO serialize

                    Object inputObject = list.get(inputObjectNo);

                    if (inputObject instanceof File) {
                        try {
                            configurationResource.setRefType(ConfigurationResource.RefTypes.FILE_REF);
                            BundleFile bf = DataUtils.createBundleFile((File) inputObject, config.getId() + "/");
                            bf.setType(BundleFile.FileType.DATA_FILE);
                            config.getBundleFiles().add(bf);
                            configurationResource.setBundleFile(bf);
                        } catch (SHIWADesktopIOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        configurationResource.setValue(inputObject.toString());
                        configurationResource.setRefType(ConfigurationResource.RefTypes.INLINE_REF);
                    }
                    inputObjectNo++;
                    config.addResourceRef(configurationResource);
                }
            }
            getWorkflowImplementation().getAggregatedResources().add(config);
            return config;
        }
        return null;
    }

    /**
     * Gets the shiwa property.
     *
     * @param key the key
     * @return the shiwa property
     */
    public SHIWAProperty getShiwaProperty(String key) {
        List<SHIWAProperty> properties = concreteBundle.getPrimaryConcreteTask().getProperties();
        for (SHIWAProperty property : properties) {
            if (property.getTitle().equals(key)) {
                return property;
            }
        }
        return null;
    }

    /**
     * Prepare library dependencies.
     */
    public void prepareLibraryDependencies() {
        ArrayList<Dependency> deps = getDependencyForType("Library");

        for (Dependency dependency : deps) {
            ConfigurationResource confRes = getConfigurationResourceForDependency(dependency);
            try {
                writeConfigurationResourceToFile(confRes, null, outputLocation);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Gets the dependency for type.
     *
     * @param type the type
     * @return the dependency for type
     */
    public ArrayList<Dependency> getDependencyForType(String type) {
        ArrayList<Dependency> dependencies = new ArrayList<Dependency>();
        for (Dependency dependency : concreteBundle.getPrimaryConcreteTask().getDependencies()) {
            if (dependency.getDataType().equals(type)) {
                dependencies.add(dependency);
            }
        }
        return dependencies;
    }

    /**
     * Gets the configuration resource for dependency.
     *
     * @param dependency the dependency
     * @return the configuration resource for dependency
     */
    public ConfigurationResource getConfigurationResourceForDependency(Dependency dependency) {
        for (Mapping mapping : concreteBundle.getPrimaryMappings()) {
            for (ConfigurationResource configurationResource : mapping.getResources()) {
                if (dependency.getId().equals(configurationResource.getId())) {
                    return configurationResource;
                }
            }
        }
        return null;
    }

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

    /**
     * Gets the temp entry.
     *
     * @param relativePath the relative path
     * @return the temp entry
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public File getTempEntry(String relativePath) throws IOException {
        return DataUtils.inputStreamToFile(concreteBundle.getEntry(relativePath),
                Locations.getTempFile(relativePath.replaceAll("/", ".")));
    }

    /**
     * Extract to file.
     *
     * @param relativePath the relative path
     * @param file the file
     * @return the file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public File extractToFile(String relativePath, File file) throws IOException {
        return DataUtils.inputStreamToFile(concreteBundle.getEntry(relativePath), file);
    }

    /**
     * Bytes to file.
     *
     * @param bytes the bytes
     * @param file the file
     * @return the file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public File bytesToFile(byte[] bytes, File file) throws IOException {

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(bytes);
        fileOutputStream.close();
        return file;
    }

//    public File writeDefinitionFile() throws IOException {
//        File definitionTempFile = File.createTempFile(concreteBundle.getPrimaryConcreteTask().getDefinition().getFilename(), ".tmp");
//        definitionTempFile.deleteOnExit();
//        return writeDefinitionFile(definitionTempFile);
//    }
//
//    public File writeDefinitionFile(File file) throws IOException {
//        return bytesToFile(concreteBundle.getPrimaryConcreteTask().getDefinition().getBytes(), file);
//    }

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
    private File outputLocation = new File(System.getProperty("user.dir"));

    /**
     * Creates the default transfer signature.
     *
     * @return the transfer signature
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public TransferSignature createDefaultTransferSignature() throws IOException {
        if (hasDataConfiguration()) {
            return createTransferSignature(concreteBundle.getPrimaryConcreteTask(),
                    getFirstConfigurationOfType(DataMapping.class));
        }
        return null;
    }

    /**
     * Checks for data configuration.
     *
     * @return true, if successful
     */
    public boolean hasDataConfiguration() {
        for (Mapping mapping : concreteBundle.getPrimaryMappings()) {
            if(mapping instanceof DataMapping){
                return true;
            }
//            if (config.getType() == Configuration.ConfigType.DATA_CONFIGURATION) {
//                return true;
//            }
        }
        return false;
    }

    /**
     * Gets the first configuration of type.
     *
     * @param mappingType the mapping type
     * @return the first configuration of type
     */
    private Mapping getFirstConfigurationOfType(Class<? extends Mapping> mappingType) {
        for (Mapping mapping :  concreteBundle.getPrimaryMappings()) {

            if(mapping.getClass().isAssignableFrom(mappingType)){
                return mapping;
            }

//            if (config.getType() == configType) {
//                return config;
//            }
        }
        return null;
    }

    /**
     * Creates the transfer signature.
     *
     * @param workflow the workflow
     * @param configuration the configuration
     * @return the transfer signature
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public TransferSignature createTransferSignature(ConcreteTask workflow, Mapping configuration) throws IOException {
        TransferSignature signature = new TransferSignature();

        signature.setName(workflow.getDefinition().getFilename());

        if (workflow.getLanguage() != null) {
            signature.setLanguage(workflow.getLanguage().toString());
        }

        for (ReferableResource referableResource : workflow.getSignature().getPorts()) {
            if (referableResource instanceof InputPort) {
                String value = null;
                boolean isReference = false;

                if (configuration != null) {
                    for (ConfigurationResource portRef : configuration.getResources()) {
                        System.out.println(portRef.getReferableResource().getId() + " " + referableResource.getId());
                        if (portRef.getReferableResource().getId().equals(referableResource.getId())) {
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
//            String value = null;
//            TransferSignature.ValueType valueType = null;
//
//            if (configuration != null) {
//                for (ConfigurationResource portRef : getFirstConfigurationOfType(Configuration.ConfigType.ENVIRONMENT_CONFIGURATION).getResources()) {
//                    if (portRef.getReferableResource().getId().equals(dependency.getId())) {
//                        value = portRef.getValue();
//
//                        ConfigurationResource.RefTypes refType = portRef.getRefType();
//                        System.out.println("Value for " + portRef.getId() + " : " + value);
//
//                        if(refType == ConfigurationResource.RefTypes.INLINE_REF){
//                            valueType = TransferSignature.ValueType.INLINE_STRING;
//
//                        } else if (refType == ConfigurationResource.RefTypes.URI_REF){
//                            File success = download(value, new File("."), null);
//                            if(success.exists()){
//                                System.out.println("Fetched " + value + " to " + success.getAbsolutePath());
//                                value = success.getAbsolutePath();
//                            } else {
//                                System.out.println("Failed to fetch " + value);
//                            }
//
//                            valueType = TransferSignature.ValueType.INLINE_URI;
//                        } else if (refType == ConfigurationResource.RefTypes.FILE_REF){
//                            valueType =  TransferSignature.ValueType.BUNDLED_FILE;
////                            File tempFile = DataUtils.extractToTempFile(portRef);
//                            File tempFile = writeConfigurationResourceToFile(portRef, null);
//                            value = tempFile.getAbsolutePath();
//                        }
//                    }
//                }
//            }

//            signature.addOutput(dependency.getTitle(),
//                    dependency.getDataType(),
//                    dependency.getDescription(),
//                    value,
//                    valueType
//            );
//        }

        if (configuration != null) {
            signature.setHasConfiguration(true);
        }
        return signature;
    }

    /**
     * Save bundle.
     *
     * @param file the file
     * @return the file
     * @throws SHIWADesktopIOException the sHIWA desktop io exception
     */
    public File saveBundle(File file) throws SHIWADesktopIOException {
        return DataUtils.bundle(file, concreteBundle.getAggregatedResource());
    }

    /**
     * Download.
     *
     * @param urlSource the url source
     * @param downloadDir the download dir
     * @param localFile the local file
     * @return the file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static File download(String urlSource, File downloadDir, File localFile) throws IOException {
        File remoteFile = new File(urlSource);
        if (localFile == null) {
            localFile = new File(downloadDir, remoteFile.getName());
        }
        org.apache.commons.io.FileUtils.copyURLToFile(new URL(urlSource), localFile);
        return localFile;
    }


    /**
     * Prepare environment dependencies.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void prepareEnvironmentDependencies() throws IOException {
        for (Dependency dependency : concreteBundle.getPrimaryConcreteTask().getDependencies()) {

//            if (getFirstConfigurationOfType(Configuration.ConfigType.ENVIRONMENT_CONFIGURATION) != null) {
            if (getFirstConfigurationOfType(EnvironmentMapping.class) != null) {

                List<ConfigurationResource> config = getFirstConfigurationOfType(EnvironmentMapping.class).getResources();
                writeConfigResourcesToDisk(dependency, config, outputLocation);
            }
        }
    }

    /**
     * Write config resources to disk.
     *
     * @param dependency the dependency
     * @param config the config
     * @param outputLocation the output location
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static void writeConfigResourcesToDisk(
            Dependency dependency,
            List<ConfigurationResource> config,
            File outputLocation) throws IOException {
        String value = null;
        TransferSignature.ValueType valueType = null;
        for (ConfigurationResource portRef : config) {
            if (portRef.getReferableResource().getId().equals(dependency.getId())) {
                value = portRef.getValue();

                ConfigurationResource.RefTypes refType = portRef.getRefType();
                System.out.println("Value for " + portRef.getId() + " : " + value);

                if (refType == ConfigurationResource.RefTypes.INLINE_REF) {
                    valueType = TransferSignature.ValueType.INLINE_STRING;

                } else if (refType == ConfigurationResource.RefTypes.URI_REF) {
                    File success = download(value, new File("."), null);
                    if (success.exists()) {
                        System.out.println("Fetched " + value + " to " + success.getAbsolutePath());
                        value = success.getAbsolutePath();
                    } else {
                        System.out.println("Failed to fetch " + value);
                    }

                    valueType = TransferSignature.ValueType.INLINE_URI;
                } else if (refType == ConfigurationResource.RefTypes.FILE_REF) {
                    valueType = TransferSignature.ValueType.BUNDLED_FILE;
//                            File tempFile = DataUtils.extractToTempFile(portRef);
                    File tempFile = writeConfigurationResourceToFile(portRef, null, outputLocation);
                    value = tempFile.getAbsolutePath();
                }
            }
        }
    }

//    public UUID getParentUUID() {
//        SHIWAProperty parentProperty = getShiwaProperty(StampedeLog.PARENT_UUID_STRING);
//        if(parentProperty != null) {
//            return UUID.fromString(parentProperty.getValue());
//        } else {
//            return null;
//        }
//    }
//
//    public UUID getRunUUID() {
//        SHIWAProperty runProperty = getShiwaProperty(StampedeLog.RUN_UUID_STRING);
//        if(runProperty != null) {
//            return UUID.fromString(runProperty.getValue());
//        } else {
//            return null;
//        }
//    }

    /**
 * Gets the workflow implementation.
 *
 * @return the workflow implementation
 */
public WorkflowImplementation getWorkflowImplementation() {
        return (WorkflowImplementation) concreteBundle.getPrimaryConcreteTask();
    }

    /**
     * Bundle.
     *
     * @param temp the temp
     * @throws SHIWADesktopIOException the sHIWA desktop io exception
     */
    public void bundle(File temp) throws SHIWADesktopIOException {
        DataUtils.bundle(
                temp,
                getWorkflowImplementation());
    }

    /**
     * Gets the outputs.
     *
     * @param bundle the bundle
     * @return the outputs
     */
    public static HashMap<String, ConfigurationResource> getOutputs(File bundle) {

        HashMap<String, ConfigurationResource> results = new HashMap<String, ConfigurationResource>();
        try {
//            SHIWABundle shiwaBundle = new SHIWABundle(bundle);

            ConcreteBundle concreteBundle = new ConcreteBundle(bundle);
//            WorkflowController workflowController = new WorkflowController(shiwaBundle);

            for (Mapping configuration : concreteBundle.getPrimaryMappings()) {
                System.out.println("Config type : " + configuration.getClass().getCanonicalName());

                if (configuration instanceof ExecutionMapping) {
                    System.out.println("Received bundle has an exec config");

                    System.out.println(configuration.getAggregatedResources().size()
                            + " aggregated resources");

                    System.out.println("Exec config contains "
                            + configuration.getResources().size() + " resources.");
                    for (ConfigurationResource r : configuration.getResources()) {
                        results.put(r.getReferableResource().getTitle(), r);
                    }
                    System.out.println(results.size() + " outputs found.");
                    return results;
                }
            }
        } catch (SHIWADesktopIOException e) {
            System.out.println("Returned bundle was corrupt or null.");
            ErrorTracker.getErrorTracker().broadcastError(
                    new ErrorEvent(null, e, "Returned Bundle was corrupt or null")
            );
        }

        return null;
    }

    /**
     * Clear configs.
     */
    public void clearConfigs() {
        ArrayList<Mapping> dataConfigs = new ArrayList<Mapping>();
        for (AggregatedResource resource : getWorkflowImplementation().getAggregatedResources()) {
            if (resource instanceof DataMapping) {
//                if (((Configuration) resource).getType() == Configuration.ConfigType.DATA_CONFIGURATION) {
                dataConfigs.add((DataMapping) resource);
//                }
            }
        }
        for (Mapping configuration : dataConfigs) {
            getWorkflowImplementation().getAggregatedResources().remove(configuration);
        }
    }

    /**
     * Compile outputs.
     *
     * @param outputResources the output resources
     * @param outputNodeMap the output node map
     * @return the hash map
     */
    public HashMap<Node, Object> compileOutputs(HashMap<String, ConfigurationResource> outputResources, HashMap<String, Node> outputNodeMap) {
        HashMap<Node, Object> outputs = new HashMap<Node, Object>();
        System.out.println(outputResources.size() + " output objects.");
        for (String nodeName : outputResources.keySet()) {
            Node node = outputNodeMap.get(nodeName);
            ConfigurationResource resource = outputResources.get(nodeName);
            String resourceString = resource.getValue();

            if (resource.getRefType() == ConfigurationResource.RefTypes.INLINE_REF) {
                outputs.put(node, resourceString);
            } else if (resource.getRefType() == ConfigurationResource.RefTypes.URI_REF) {
                outputs.put(node, resourceString);
            } else if (resource.getRefType() == ConfigurationResource.RefTypes.FILE_REF) {
//                String outputString = new String(resource.getBundleFile().getBytes());
//                System.out.println("BundleFile at node : " + node + " contains : " + outputString);
//                outputs.put(node, outputString);
                String tempBundleLocation = resource.getBundleFile().getSystemPath();
                System.out.println("BundleFile at node : " + node + " temp location : " + tempBundleLocation);
                String outputString = BundleUtils.readFile(tempBundleLocation);
                outputs.put(node, outputString);
            }


            System.out.println("Node " + node.getAbsoluteNodeIndex() + " named : " + nodeName
                    + " outputs : " + outputs.get(node));
        }
        return outputs;
    }

    /**
     * Gets the task signature.
     *
     * @param inputPortMap the input port map
     * @param outputPortMap the output port map
     * @return the task signature
     */
    public void getTaskSignature(HashMap<String, InputPort> inputPortMap, HashMap<String, OutputPort> outputPortMap) {
        TaskSignature signature = concreteBundle.getPrimaryConcreteTask().getSignature();
        for (ReferableResource referableResource : signature.getPorts()) {
            if (referableResource instanceof InputPort) {
                InputPort inputPort = (InputPort) referableResource;
//                Class dataClass = XSDDataType.getClass(inputPort.getDataType());
//                if (dataClass != null) {
//                    inputDataTypes.add(dataClass.getCanonicalName());
//                }
                inputPortMap.put(referableResource.getTitle(), inputPort);
            }
            if (referableResource instanceof OutputPort) {
                OutputPort outputPort = (OutputPort) referableResource;
//                Class dataClass = XSDDataType.getClass(outputPort.getDataType());
//                if (dataClass != null) {
//                    outputDataTypes.add(dataClass.getCanonicalName());
//                }
                outputPortMap.put(referableResource.getTitle(), outputPort);
            }
        }
    }
}