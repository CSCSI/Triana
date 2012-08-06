package org.trianacode.shiwaall.workflowCreation;

import org.shiwa.desktop.data.description.SHIWABundle;
import org.trianacode.annotation.TextFieldParameter;
import org.trianacode.annotation.Tool;
import org.trianacode.enactment.logging.stampede.StampedeLog;
import org.trianacode.shiwaall.bundle.ShiwaBundleHelper152;
import org.trianacode.shiwaall.utils.BrokerUtils;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.annotation.TaskConscious;

import java.io.File;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 19/04/2012
 * Time: 15:42
 * To change this template use File | Settings | File Templates.
 */
@Tool
public class RunMultipleBundles implements TaskConscious {

    @TextFieldParameter
    public String address = "http://s-vmc.cs.cf.ac.uk:7025/Broker/broker";

    @TextFieldParameter
    private String routingKey = "*.triana";

    private Task task;


    @org.trianacode.annotation.Process()
    public void process(List list) {

        System.out.println("Submitting " + list.size() + " bundles.");
        for (Object object : list) {
            if (object instanceof File) {
                try {
                    ShiwaBundleHelper152 shiwaBundleHelper = new ShiwaBundleHelper152(new SHIWABundle((File) object));
                    String execBundleName = shiwaBundleHelper.getWorkflowImplementation().getTitle()
                            + "-" + BrokerUtils.getTimeStamp();
                    BrokerUtils.postBundle(address, routingKey, execBundleName, (File) object);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void setTask(Task task) {
        this.task = task;
        task.setParameter(StampedeLog.STAMPEDE_TASK_TYPE, StampedeLog.JobType.dax.desc);
    }
}
