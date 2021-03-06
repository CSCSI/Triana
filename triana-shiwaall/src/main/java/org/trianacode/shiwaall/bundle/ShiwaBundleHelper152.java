//package org.trianacode.shiwaall.bundle;
//
//import org.shiwa.desktop.data.description.SHIWABundle;
//import org.shiwa.desktop.data.description.core.Configuration;
//import org.shiwa.desktop.data.description.core.WorkflowImplementation;
//import org.shiwa.desktop.data.description.handler.TransferSignature;
//import org.shiwa.desktop.data.description.resource.ConfigurationResource;
//import org.shiwa.desktop.data.description.resource.ReferableResource;
//import org.shiwa.desktop.data.description.workflow.Dependency;
//import org.shiwa.desktop.data.description.workflow.InputPort;
//import org.shiwa.desktop.data.description.workflow.OutputPort;
//import org.shiwa.desktop.data.description.workflow.SHIWAProperty;
//import org.shiwa.desktop.data.transfer.WorkflowController;
//import org.shiwa.desktop.data.util.DataUtils;
//import org.shiwa.desktop.data.util.exception.SHIWADesktopIOException;
//import org.shiwa.desktop.data.util.properties.Locations;
//import org.trianacode.error.ErrorEvent;
//import org.trianacode.error.ErrorTracker;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
///**
//* Created by IntelliJ IDEA.
//* User: Ian Harvey
//* Date: 10/04/2012
//* Time: 14:28
//* To change this template use File | Settings | File Templates.
//*/
//public class ShiwaBundleHelper152 extends WorkflowController {
//
//
//    //    private String workflowFilePath;
//    private SHIWABundle shiwaBundle;
//
////    private UUID parentUUID = null;
//
//    public ShiwaBundleHelper152(SHIWABundle shiwaBundle) throws SHIWADesktopIOException {
//        this.shiwaBundle = shiwaBundle;
//        initialiseController(shiwaBundle);
////        init();
//    }
//
////    private void init() {
////        SHIWAProperty parentProperty = getShiwaProperty(parentUUIDstring);
////        if(parentProperty != null){
////            parentUUID = UUID.fromString(parentProperty.getValue());
////        }
//////        List<SHIWAProperty> properties = getWorkflowImplementation().getProperties();
//////        for (SHIWAProperty property : properties) {
//////            if (property.getTitle().equals(parentUUIDstring)) {
//////                System.out.println("Found parent uuid " + property.getValue());
//////                parentUUID = UUID.fromString(property.getValue());
//////            }
//////        }
////    }
//
//    public ShiwaBundleHelper152(String bundlePath) throws SHIWADesktopIOException {
//        this.shiwaBundle = new SHIWABundle(new File(bundlePath));
//        initialiseController(shiwaBundle);
////        init();
//    }
//
//    public SHIWAProperty getShiwaProperty(String key) {
//        List<SHIWAProperty> properties = getWorkflowImplementation().getProperties();
//        for (SHIWAProperty property : properties) {
//            if (property.getTitle().equals(key)) {
//                return property;
//            }
//        }
//        return null;
//    }
//
//    public void prepareLibraryDependencies() {
//        ArrayList<Dependency> deps = getDependencyForType("Library");
//
//        for (Dependency dependency : deps) {
//            ConfigurationResource confRes = getConfigurationResourceForDependency(dependency);
//            try {
//                writeConfigurationResourceToFile(confRes, null);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public static HashMap<String, ConfigurationResource> getOutputs(File bundle) {
//
//        HashMap<String, ConfigurationResource> results = new HashMap<String, ConfigurationResource>();
//        try {
//            SHIWABundle shiwaBundle = new SHIWABundle(bundle);
//
//            WorkflowController workflowController = new WorkflowController(shiwaBundle);
//
//            for (Configuration configuration : workflowController.getConfigurations()) {
//                System.out.println("Config type : " + configuration.getType().getString());
//                if (configuration.getType() == Configuration.ConfigType.EXECUTION_CONFIGURATION) {
//                    System.out.println("Received bundle has an exec config");
//
//                    System.out.println(configuration.getAggregatedResources().size()
//                            + " aggregated resources");
//
//                    System.out.println("Exec config contains "
//                            + configuration.getResources().size() + " resources.");
//                    for (ConfigurationResource r : configuration.getResources()) {
//                        results.put(r.getReferableResource().getTitle(), r);
//                    }
//                    System.out.println(results.size() + " outputs found.");
//                    return results;
//                }
//            }
//        } catch (SHIWADesktopIOException e) {
//            System.out.println("Returned bundle was corrupt or null.");
//            ErrorTracker.getErrorTracker().broadcastError(
//                    new ErrorEvent(task, e, "Returned Bundle was corrupt or null")
//            );
//        }
//
//        return null;
//    }
//
//    public ArrayList<Dependency> getDependencyForType(String type) {
//        ArrayList<Dependency> dependencies = new ArrayList<Dependency>();
//        for (Dependency dependency : getWorkflowImplementation().getDependencies()) {
//            if (dependency.getDataType().equals(type)) {
//                dependencies.add(dependency);
//            }
//        }
//        return dependencies;
//    }
//
//    public ConfigurationResource getConfigurationResourceForDependency(Dependency dependency) {
//        for (Configuration config : getConfigurations()) {
//            for (ConfigurationResource configurationResource : config.getResources()) {
//                if (dependency.getId().equals(configurationResource.getId())) {
//                    return configurationResource;
//                }
//            }
//        }
//        return null;
//    }
//
//    public File writeConfigurationResourceToFile(ConfigurationResource configurationResource, File file) throws IOException {
//        String longName = configurationResource.getBundleFile().getFilename();
//        if (file == null) {
//            file = new File(outputLocation, longName.substring(longName.lastIndexOf("/") + 1));
//        }
//        System.out.println("   >> Made : " + file.getAbsolutePath());
//
//        return DataUtils.extractToFile(configurationResource, file);
//    }
//
//    public File getTempEntry(String relativePath) throws IOException {
//        return DataUtils.inputStreamToFile(shiwaBundle.getEntry(relativePath),
//                Locations.getTempFile(relativePath.replaceAll("/", "."), false));
//    }
//
//    public File extractToFile(String relativePath, File file) throws IOException {
//        return DataUtils.inputStreamToFile(shiwaBundle.getEntry(relativePath), file);
//    }
//
//    public File bytesToFile(byte[] bytes, File file) throws IOException {
//
//        FileOutputStream fileOutputStream = new FileOutputStream(file);
//        fileOutputStream.write(bytes);
//        fileOutputStream.close();
//        return file;
//    }
//
////    public File writeDefinitionFile() throws IOException {
////        File definitionTempFile = File.createTempFile(getWorkflowImplementation().getDefinition().getFilename(), ".tmp");
////        definitionTempFile.deleteOnExit();
////        return writeDefinitionFile(definitionTempFile);
////    }
//
////    public File writeDefinitionFile(File file) throws IOException {
////        return bytesToFile(getWorkflowImplementation().getDefinition().getBytes(), file);
////    }
//
//    private File createFileInRuntimeFolder(String filename) {
//        return new File(outputLocation, filename);
//    }
//
//    public void setRuntimeOutputFolder(File outputFolder) {
//        outputLocation = outputFolder;
//        outputLocation.mkdirs();
//    }
//
//    private File outputLocation = new File(System.getProperty("user.dir"));
//
//    public TransferSignature createDefaultTransferSignature() throws IOException {
//        if (hasDataConfiguration()) {
//            return createTransferSignature(getWorkflowImplementation(),
//                    getFirstConfigurationOfType(Configuration.ConfigType.DATA_CONFIGURATION));
//        }
//        return null;
//    }
//
//    public boolean hasDataConfiguration() {
//        for (Configuration config : getConfigurations()) {
//            if (config.getType() == Configuration.ConfigType.DATA_CONFIGURATION) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    private Configuration getFirstConfigurationOfType(Configuration.ConfigType configType) {
//        for (Configuration config : getConfigurations()) {
//            if (config.getType() == configType) {
//                return config;
//            }
//        }
//        return null;
//    }
//
//    public TransferSignature createTransferSignature(WorkflowImplementation workflow, Configuration configuration) throws IOException {
//        TransferSignature signature = new TransferSignature();
//
//        signature.setName(workflow.getDefinition().getFilename());
//
//        if (workflow.getLanguage() != null) {
//            signature.setLanguage(workflow.getLanguage().toString());
//        }
//
//        for (ReferableResource referableResource : workflow.getSignature().getPorts()) {
//            if (referableResource instanceof InputPort) {
//                String value = null;
//                boolean isReference = false;
//
//                if (configuration != null) {
//                    for (ConfigurationResource portRef : configuration.getResources()) {
//                        System.out.println(portRef.getReferableResource().getId() + " " + referableResource.getId());
//                        if (portRef.getReferableResource().getId().equals(referableResource.getId())) {
//                            value = portRef.getValue();
//                            isReference = (portRef.getRefType() == ConfigurationResource.RefTypes.FILE_REF);
//                        }
//                    }
//                }
//
//                if (value != null) {
//                    if (isReference) {
//                        signature.addInputReference(referableResource.getTitle(), referableResource.getDataType(), value);
//                    } else {
//                        signature.addInput(referableResource.getTitle(), referableResource.getDataType(), value);
//                    }
//                } else {
//                    signature.addInput(referableResource.getTitle(), referableResource.getDataType());
//                }
//            } else if (referableResource instanceof OutputPort) {
//                signature.addOutput(referableResource.getTitle(), referableResource.getDataType());
//            }
//        }
//
//
////        for (Dependency dependency : workflow.getDependencies()) {
////            String value = null;
////            TransferSignature.ValueType valueType = null;
////
////            if (configuration != null) {
////                for (ConfigurationResource portRef : getFirstConfigurationOfType(Configuration.ConfigType.ENVIRONMENT_CONFIGURATION).getResources()) {
////                    if (portRef.getReferableResource().getId().equals(dependency.getId())) {
////                        value = portRef.getValue();
////
////                        ConfigurationResource.RefTypes refType = portRef.getRefType();
////                        System.out.println("Value for " + portRef.getId() + " : " + value);
////
////                        if(refType == ConfigurationResource.RefTypes.INLINE_REF){
////                            valueType = TransferSignature.ValueType.INLINE_STRING;
////
////                        } else if (refType == ConfigurationResource.RefTypes.URI_REF){
////                            File success = download(value, new File("."), null);
////                            if(success.exists()){
////                                System.out.println("Fetched " + value + " to " + success.getAbsolutePath());
////                                value = success.getAbsolutePath();
////                            } else {
////                                System.out.println("Failed to fetch " + value);
////                            }
////
////                            valueType = TransferSignature.ValueType.INLINE_URI;
////                        } else if (refType == ConfigurationResource.RefTypes.FILE_REF){
////                            valueType =  TransferSignature.ValueType.BUNDLED_FILE;
//////                            File tempFile = DataUtils.extractToTempFile(portRef);
////                            File tempFile = writeConfigurationResourceToFile(portRef, null);
////                            value = tempFile.getAbsolutePath();
////                        }
////                    }
////                }
////            }
//
////            signature.addOutput(dependency.getTitle(),
////                    dependency.getDataType(),
////                    dependency.getDescription(),
////                    value,
////                    valueType
////            );
////        }
//
//        if (configuration != null) {
//            signature.setHasConfiguration(true);
//        }
//        return signature;
//    }
//
//    public File saveBundle(File file) throws SHIWADesktopIOException {
//        return DataUtils.bundle(file, shiwaBundle.getAggregatedResource());
//    }
//
//    public static File download(String urlSource, File downloadDir, File localFile) throws IOException {
//        File remoteFile = new File(urlSource);
//        if (localFile == null) {
//            localFile = new File(downloadDir, remoteFile.getName());
//        }
//        org.apache.commons.io.FileUtils.copyURLToFile(new URL(urlSource), localFile);
//        return localFile;
//    }
//
//
//    public void prepareEnvironmentDependencies() throws IOException {
//        for (Dependency dependency : getWorkflowImplementation().getDependencies()) {
//            String value = null;
//            TransferSignature.ValueType valueType = null;
//
//            if (getFirstConfigurationOfType(Configuration.ConfigType.ENVIRONMENT_CONFIGURATION) != null) {
//                for (ConfigurationResource portRef : getFirstConfigurationOfType(Configuration.ConfigType.ENVIRONMENT_CONFIGURATION).getResources()) {
//                    if (portRef.getReferableResource().getId().equals(dependency.getId())) {
//                        value = portRef.getValue();
//
//                        ConfigurationResource.RefTypes refType = portRef.getRefType();
//                        System.out.println("Value for " + portRef.getId() + " : " + value);
//
//                        if (refType == ConfigurationResource.RefTypes.INLINE_REF) {
//                            valueType = TransferSignature.ValueType.INLINE_STRING;
//
//                        } else if (refType == ConfigurationResource.RefTypes.URI_REF) {
//                            File success = download(value, new File("."), null);
//                            if (success.exists()) {
//                                System.out.println("Fetched " + value + " to " + success.getAbsolutePath());
//                                value = success.getAbsolutePath();
//                            } else {
//                                System.out.println("Failed to fetch " + value);
//                            }
//
//                            valueType = TransferSignature.ValueType.INLINE_URI;
//                        } else if (refType == ConfigurationResource.RefTypes.FILE_REF) {
//                            valueType = TransferSignature.ValueType.BUNDLED_FILE;
////                            File tempFile = DataUtils.extractToTempFile(portRef);
//                            File tempFile = writeConfigurationResourceToFile(portRef, null);
//                            value = tempFile.getAbsolutePath();
//                        }
//                    }
//                }
//            }
//
//        }
//    }
//
////    public UUID getParentUUID() {
////        SHIWAProperty parentProperty = getShiwaProperty(StampedeLog.PARENT_UUID_STRING);
////        if(parentProperty != null) {
////            return UUID.fromString(parentProperty.getValue());
////        } else {
////            return null;
////        }
////    }
////
////    public UUID getRunUUID() {
////        SHIWAProperty runProperty = getShiwaProperty(StampedeLog.RUN_UUID_STRING);
////        if(runProperty != null) {
////            return UUID.fromString(runProperty.getValue());
////        } else {
////            return null;
////        }
////    }
//}