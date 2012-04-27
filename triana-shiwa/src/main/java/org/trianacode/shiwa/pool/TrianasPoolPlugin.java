package org.trianacode.shiwa.pool;

import fr.insalyon.creatis.shiwapool.agent.engines.EnginePluginImpl;
import fr.insalyon.creatis.shiwapool.agent.engines.StatusHelper;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import org.shiwa.desktop.data.description.SHIWABundle;
import org.shiwa.desktop.data.description.WorkflowInstance;
import org.trianacode.config.cl.ArgumentParsingException;
import org.trianacode.enactment.plugins.Plugins;
import org.trianacode.shiwa.bundle.ShiwaBundleHelper;

import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 31/03/2012
 * Time: 12:58
 * To change this template use File | Settings | File Templates.
 */
@PluginImplementation
public class TrianasPoolPlugin extends EnginePluginImpl {
    public TrianasPoolPlugin() {
        super("triana-taskgraph");
    }

    @Override
    public void submit(int instanceID, SHIWABundle shiwaBundle, String s) {
        StatusHelper.setStatus(instanceID, WorkflowInstance.Status.RUNNING);

//        TrianaBundle trianaBundle = new TrianaBundle();
//        SHIWABundle returnedBundle = trianaBundle.executeBundleReturnBundle(shiwaBundle, new String[]{});

        File tempReturnFile = null;
        try {
            ShiwaBundleHelper shiwaBundleHelper = new ShiwaBundleHelper(shiwaBundle);
            File temp = File.createTempFile("bundle", "tmp");
            shiwaBundleHelper.saveBundle(temp);

            tempReturnFile = File.createTempFile("return", "tmp");

            String executableString = "./triana.sh -n -p unbundle "
                    + temp.getAbsolutePath() + " "
                    + tempReturnFile.getAbsolutePath();

            Plugins.exec(executableString.split(" "));
        } catch (ArgumentParsingException e) {
            e.printStackTrace();
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
}
