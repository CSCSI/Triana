package org.trianacode.shiwaall.bundle;

import org.shiwa.desktop.data.description.SHIWABundle;
import org.shiwa.desktop.data.description.bundle.BundleFile;
import org.shiwa.desktop.data.description.core.ExecutionMapping;
import org.shiwa.desktop.data.description.resource.ConfigurationResource;
import org.shiwa.desktop.data.description.resource.ReferableResource;
import org.shiwa.desktop.data.description.workflow.OutputPort;
import org.shiwa.desktop.data.util.DataUtils;
import org.shiwa.desktop.data.util.exception.SHIWADesktopIOException;
import org.trianacode.TrianaInstance;
import org.trianacode.enactment.Exec;
import org.trianacode.enactment.addon.CLIaddon;
import org.trianacode.shiwaall.executionServices.Unbundler;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.TaskGraphException;
import org.trianacode.taskgraph.proxy.ProxyInstantiationException;
import org.trianacode.taskgraph.tool.Tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//import org.shiwa.desktop.data.description.core.Configuration;

/**
* Created by IntelliJ IDEA.
* User: Ian Harvey
* Date: 24/02/2012
* Time: 16:59
* To change this template use File | Settings | File Templates.
*/
public class TrianaBundle {

    public File executeBundleReturnFile(SHIWABundle shiwaBundle, String[] trianaArgs) throws SHIWADesktopIOException {

//        WorkflowController workflowController = null;
        ShiwaBundleHelper shiwaBundleHelper = new ShiwaBundleHelper(shiwaBundle);
        try {
//            workflowController = new WorkflowController(shiwaBundle);
//            for (Configuration configuration : workflowController.getConfigurations()) {
//                if (configuration.getType() == Configuration.ConfigType.ENVIRONMENT_CONFIGURATION) {
//                    for (ConfigurationResource res : configuration.getResources()) {
////                        res.getId()
////                        workflowController.getWorkflowImplementation().getDependency()
////                        res.getBundleFile().getBytes();
////                        res.getReferableResource().
//                    }
//                }
//            }


            TrianaInstance engine = null;
            try {
                engine = new TrianaInstance(trianaArgs);
                engine.addExtensionClass(CLIaddon.class);
                engine.init();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Unbundler unbundler = new Unbundler(shiwaBundleHelper);
            File homeFolder = new File(System.getProperty("user.home"));
            File runFolder = null;
            if (homeFolder.exists()) {
                runFolder = new File(homeFolder, getTimeStamp());
                runFolder.mkdirs();
            }
            unbundler.setRuntimeOutputFolder(runFolder);
//            shiwaBundleHelper.prepareLibraryDependencies();

            Tool tool = null;
            String dataLocation = null;
            try {
                shiwaBundleHelper.prepareEnvironmentDependencies();
                tool = unbundler.getTool(engine, shiwaBundle);
                dataLocation = unbundler.getConfigFile().getAbsolutePath();
                unbundler.serializeOutputs((TaskGraph) tool);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TaskGraphException e) {
                e.printStackTrace();
            } catch (ProxyInstantiationException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Exec exec = new Exec(null);
            try {
                exec.execute(tool, dataLocation);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (engine != null) {
                engine.shutdown(-1);
            }

            ExecutionMapping execConfig = new ExecutionMapping();
            ArrayList<ConfigurationResource> configurationResourceArrayList = new ArrayList<ConfigurationResource>();

            ArrayList<File> outputs = unbundler.getOutputFiles();
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


            File tempBundle = File.createTempFile("bundle", ".tmp");
            System.out.println("Output bundle : " + tempBundle.getAbsolutePath());
            File bundleFile = DataUtils.bundle(tempBundle, shiwaBundle.getAggregatedResource());
            return bundleFile;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public byte[] executeBundle(SHIWABundle shiwaBundle, String[] trianaArgs) {
        try {
            return getFileBytes(executeBundleReturnFile(shiwaBundle, trianaArgs));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public SHIWABundle executeBundleReturnBundle(SHIWABundle shiwaBundle, String[] trianaArgs) {
        try {
            return new SHIWABundle(executeBundleReturnFile(shiwaBundle, trianaArgs));
        } catch (SHIWADesktopIOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getTimeStamp() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd_HH-mm-ss-SS_z");
        return dateFormat.format(new Date());
    }

    public static byte[] getFileBytes(File outfile) throws Exception {
        FileInputStream fis = new FileInputStream(outfile);
        long length = outfile.length();

        if (length > Integer.MAX_VALUE) {
            // File is too large
            throw new Exception("File too large");
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int) length];

        // Read in the bytes
        int offset = 0;
        int numRead;
        while (offset < bytes.length
                && (numRead = fis.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + outfile.getName());
        }

        // Close the input stream and return bytes
        fis.close();
        //return bytes;
        System.out.println(outfile.getPath());
        return bytes;
    }


    public static void main(String[] args) throws Exception {

        String filePath;
        if (args.length > 0) {
            filePath = args[0];
        } else {
            filePath = "C:\\Users\\Dave\\Desktop\\bundle2.zip";
        }

        File file = new File(filePath);
        System.out.println(file.getAbsolutePath());
        if (file.exists()) {
            SHIWABundle shiwaBundle = new SHIWABundle(file);

            byte[] bundleBytes = new TrianaBundle().executeBundle(shiwaBundle, new String[]{});

            System.out.println(bundleBytes.length + " sized bundle returned.");
        } else {
            System.out.println("File not found");
        }
//        }
    }

}
