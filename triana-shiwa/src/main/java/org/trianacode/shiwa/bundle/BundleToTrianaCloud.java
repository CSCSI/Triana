package org.trianacode.shiwa.bundle;

import org.shiwa.desktop.data.description.SHIWABundle;
import org.shiwa.desktop.data.util.DataUtils;
import org.trianacode.annotation.Process;
import org.trianacode.annotation.TextFieldParameter;
import org.trianacode.annotation.Tool;
import org.trianacode.enactment.logging.stampede.StampedeLog;
import org.trianacode.shiwa.utils.BrokerUtils;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.annotation.TaskConscious;

import java.io.File;
import java.util.List;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 10/05/2012
 * Time: 17:31
 * To change this template use File | Settings | File Templates.
 */

@Tool
public class BundleToTrianaCloud implements TaskConscious {

    @TextFieldParameter
    public String bundleFile = "";
    private Task task;

    @Process(gather = true)
    public void process(List list) {

        for (Object object : list) {
            if (object instanceof String) {
                bundleFile = String.valueOf(object);
            }
            if (object instanceof File) {
                bundleFile = ((File) object).getAbsolutePath();
            }
        }

        try {
            ShiwaBundleHelper shiwaBundleHelper = new ShiwaBundleHelper(new SHIWABundle(new File(bundleFile)));
            BrokerUtils.prepareSubworkflow(task, UUID.randomUUID(), shiwaBundleHelper.getWorkflowImplementation());

            File tempBundleFile = File.createTempFile(
                    shiwaBundleHelper.getWorkflowImplementation().getDefinition().getFilename(), "tmp");

            File updatedBundle = DataUtils.bundle(tempBundleFile, shiwaBundleHelper.getWorkflowImplementation());

            BrokerUtils.postBundle(
                    "http://s-vmc.cs.cf.ac.uk:7025/Broker/broker",
                    "*.triana",
                    updatedBundle.getName() + BrokerUtils.getTimeStamp(),
                    updatedBundle);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setTask(Task task) {
        this.task = task;
        task.setParameter(StampedeLog.STAMPEDE_TASK_TYPE, StampedeLog.JobType.dax.desc);
    }
}
