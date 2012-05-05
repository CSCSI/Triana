package org.trianacode.shiwa.workflowCreation;

import org.shiwa.desktop.data.description.SHIWABundle;
import org.trianacode.annotation.TextFieldParameter;
import org.trianacode.annotation.Tool;
import org.trianacode.shiwa.bundle.ShiwaBundleHelper;
import org.trianacode.shiwa.utils.BrokerUtils;

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
public class RunMultipleBundles {

    @TextFieldParameter
    public String address = "http://s-vmc.cs.cf.ac.uk:7025/Broker/broker";

    @TextFieldParameter
    private String routingKey = "*.triana";

    @org.trianacode.annotation.Process()
    public void process(List list) {

        System.out.println("Submitting " + list.size() + " bundles.");
        for (Object object : list) {
            if (object instanceof File) {
                try {
                    ShiwaBundleHelper shiwaBundleHelper = new ShiwaBundleHelper(new SHIWABundle((File) object));
                    String execBundleName = shiwaBundleHelper.getWorkflowImplementation().getTitle()
                            + "-" + BrokerUtils.getTimeStamp();
                    BrokerUtils.postBundle(address, routingKey, execBundleName, (File) object);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
