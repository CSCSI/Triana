package org.trianacode.shiwa.pool;

import fr.insalyon.creatis.shiwapool.agent.engines.EnginePluginImpl;
import fr.insalyon.creatis.shiwapool.agent.engines.StatusHelper;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import org.shiwa.desktop.data.description.SHIWABundle;
import org.shiwa.desktop.data.description.WorkflowInstance;
import org.trianacode.shiwa.bundle.TrianaBundle;

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

        TrianaBundle trianaBundle = new TrianaBundle();
        SHIWABundle returnedBundle = trianaBundle.executeBundleReturnBundle(shiwaBundle, new String[]{});

        if (returnedBundle != null) {
            StatusHelper.setStatus(instanceID, WorkflowInstance.Status.FINISHED);
        } else {
            StatusHelper.setStatus(instanceID, WorkflowInstance.Status.FAILED);
        }
    }
}
