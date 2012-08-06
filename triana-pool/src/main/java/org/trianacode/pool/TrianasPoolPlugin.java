package org.trianacode.pool;

import fr.insalyon.creatis.shiwapool.agent.engines.EnginePluginImpl;
import fr.insalyon.creatis.shiwapool.agent.engines.StatusHelper;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import org.apache.commons.io.FileUtils;
import org.shiwa.desktop.data.description.SHIWABundle;
import org.shiwa.desktop.data.description.WorkflowInstance;
import org.shiwa.desktop.data.util.exception.SHIWADesktopIOException;
import org.trianacode.util.StreamToOutput;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 31/03/2012
 * Time: 12:58
 * To change this template use File | Settings | File Templates.
 */
@PluginImplementation
public class TrianasPoolPlugin extends EnginePluginImpl {
    private File tempReturnFile;

    public TrianasPoolPlugin() {
        super("Triana Taskgraph");
    }

    @Override
    public void submit(int instanceID, SHIWABundle shiwaBundle, String s) {
        System.out.println("Running " + instanceID);
        StatusHelper.setStatus(instanceID, WorkflowInstance.Status.RUNNING);

//        TrianaBundle trianaBundle = new TrianaBundle();
//        SHIWABundle returnedBundle = trianaBundle.executeBundleReturnBundle(shiwaBundle, new String[]{});

        tempReturnFile = null;
        try {
            ShiwaBundleHelper152 shiwaBundleHelper = new ShiwaBundleHelper152(shiwaBundle);
            File temp = File.createTempFile("bundle", "tmp");
            shiwaBundleHelper.saveBundle(temp);

            tempReturnFile = File.createTempFile("return", "tmp");

            String executableString = "./triana.sh -n -p unbundle "
                    + temp.getAbsolutePath() + " "
                    + tempReturnFile.getAbsolutePath();

            System.out.println("Will run : " + executableString);


            List<String> options = new ArrayList<String>();
            String[] optionsStrings = executableString.split(" ");
            Collections.addAll(options, optionsStrings);

            ProcessBuilder processBuilder = new ProcessBuilder(optionsStrings);
            Process p = processBuilder.start();

            new StreamToOutput(p.getInputStream(), "std.out").start();
            new StreamToOutput(p.getErrorStream(), "std.err").start();

            p.waitFor();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        if (tempReturnFile != null && tempReturnFile.exists()) {
            StatusHelper.setStatus(instanceID, WorkflowInstance.Status.FINISHED);
        } else {
            StatusHelper.setStatus(instanceID, WorkflowInstance.Status.FAILED);
        }
    }

    @Override
    public void createOutputBundle(String executionDirectory, String outputBundlePath) throws SHIWADesktopIOException {
        try {
            FileUtils.copyFile(tempReturnFile, new File(outputBundlePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
