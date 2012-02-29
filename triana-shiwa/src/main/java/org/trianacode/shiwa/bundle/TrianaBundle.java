package org.trianacode.shiwa.bundle;

import org.shiwa.desktop.data.description.SHIWABundle;
import org.shiwa.desktop.data.description.bundle.BundleFile;
import org.shiwa.desktop.data.description.core.Configuration;
import org.shiwa.desktop.data.transfer.WorkflowController;
import org.shiwa.desktop.data.util.DataUtils;
import org.shiwa.desktop.data.util.exception.SHIWADesktopIOException;
import org.trianacode.TrianaInstance;
import org.trianacode.enactment.Exec;
import org.trianacode.enactment.addon.CLIaddon;
import org.trianacode.shiwa.executionServices.Unbundler;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.TaskGraphException;
import org.trianacode.taskgraph.proxy.ProxyInstantiationException;
import org.trianacode.taskgraph.tool.Tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 24/02/2012
 * Time: 16:59
 * To change this template use File | Settings | File Templates.
 */
public class TrianaBundle {

    public byte[] executeBundle(SHIWABundle shiwaBundle, String[] trianaArgs) {

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

        Unbundler unbundler = new Unbundler();
        Tool tool = null;
        String dataLocation = null;
        try {
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
            engine.shutdown(0);
        }

        try {
            WorkflowController workflowController = new WorkflowController(shiwaBundle);
            Configuration execConfig = new Configuration(Configuration.ConfigType.EXECUTION_CONFIGURATION);
            for (File outputFile : unbundler.getOutputFiles()) {
                BundleFile bf = DataUtils.createBundleFile(outputFile, execConfig.getId() + "/");
                bf.setType(BundleFile.FileType.INPUT_FILE);
                execConfig.getBundleFiles().add(bf);
            }

            workflowController.getWorkflowImplementation().getAggregatedResources().add(execConfig);
        } catch (SHIWADesktopIOException e) {
            e.printStackTrace();
        }

        try {
            File bundleFile = DataUtils.bundle(File.createTempFile("bundle", ".tmp"), shiwaBundle.getAggregatedResource());
            return getFileBytes(bundleFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private byte[] getFileBytes(File outfile) throws Exception {
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
            filePath = "/Users/ian/stuff/concatBundle.bundle.zip";
        }

        File file = new File(filePath);
        System.out.println(file.getAbsolutePath());
        if (file.exists()) {
            SHIWABundle shiwaBundle = new SHIWABundle(file);

            new TrianaBundle().executeBundle(shiwaBundle, new String[]{});
        } else {
            System.out.println("File not found");
        }
//        }
    }

}
