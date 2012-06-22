package org.trianacode.shiwa.bundle;

import org.shiwa.desktop.data.description.ConcreteBundle;
import org.shiwa.desktop.data.util.DataUtils;
import org.trianacode.annotation.Process;
import org.trianacode.annotation.TextFieldParameter;
import org.trianacode.annotation.Tool;
import org.trianacode.enactment.logging.stampede.StampedeLog;
import org.trianacode.shiwa.utils.BrokerUtils;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.annotation.TaskConscious;

import java.io.File;
import java.util.ArrayList;
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
        ArrayList<File> toSend = new ArrayList<File>();
        try {
            ShiwaBundleHelper shiwaBundleHelper = new ShiwaBundleHelper(new ConcreteBundle(new File(bundleFile)));
            BrokerUtils.prepareSubworkflow(task, UUID.randomUUID(), shiwaBundleHelper.getWorkflowImplementation());

            File tempBundleFile = File.createTempFile(
                    shiwaBundleHelper.getWorkflowImplementation().getDefinition().getFilename(), "tmp");

            File updatedBundle = DataUtils.bundle(tempBundleFile, shiwaBundleHelper.getWorkflowImplementation());

            toSend.add(updatedBundle);
        } catch (Exception e) {
            System.out.println("Issue with bundle " + bundleFile);
            e.printStackTrace();
        }

        if (toSend.size() == 1) {
            for (File bundle : toSend) {
                try {
                    String key = BrokerUtils.postBundle(
                            "http://s-vmc.cs.cf.ac.uk:7025/Broker/broker",
                            "*.triana",
                            bundle.getName() + BrokerUtils.getTimeStamp(),
                            bundle);

                    BrokerUtils.getResultBundle(
                            "http://s-vmc.cs.cf.ac.uk:7025/Broker/results",
                            key
                    );
                } catch (Exception e) {
                    System.out.println("Issue with bundle " + bundleFile);
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("Error creating bundle, not sending.");
        }
    }

    @Override
    public void setTask(Task task) {
        this.task = task;
        task.setParameter(StampedeLog.STAMPEDE_TASK_TYPE, StampedeLog.JobType.dax.desc);
    }
}
