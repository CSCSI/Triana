package org.trianacode.shiwa.bundle;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.shiwa.desktop.data.description.SHIWABundle;
import org.shiwa.desktop.data.description.core.Configuration;
import org.shiwa.desktop.data.description.core.WorkflowImplementation;
import org.shiwa.desktop.data.description.core.WorkflowSignature;
import org.shiwa.desktop.data.description.resource.AggregatedResource;
import org.shiwa.desktop.data.description.resource.ConfigurationResource;
import org.shiwa.desktop.data.description.resource.ReferableResource;
import org.shiwa.desktop.data.description.workflow.InputPort;
import org.shiwa.desktop.data.transfer.WorkflowController;
import org.shiwa.desktop.data.util.DataUtils;
import org.shiwa.desktop.data.util.exception.SHIWADesktopIOException;
import org.shiwa.desktop.data.util.monitors.BundleMonitor;
import org.shiwa.desktop.gui.util.listener.DefaultBundleReceivedListener;
import org.trianacode.annotation.CustomGUIComponent;
import org.trianacode.annotation.Tool;
import org.trianacode.taskgraph.NodeException;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.annotation.TaskConscious;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 28/02/2012
 * Time: 16:54
 * To change this template use File | Settings | File Templates.
 */
@Tool
public class RunBundleInPool implements TaskConscious {

    private ArrayList<InputPort> inputPortArray;
    private Task task;
    private WorkflowController workflowController;
    private ButtonGroup bundleSubmitButtonGroup;
    private JTextField urlField;

    @org.trianacode.annotation.Process(gather = true)
    public File process(List list) {
        if (workflowController != null) {
            if (task.getInputNodeCount() != inputPortArray.size()) {

            } else {
                if (list.size() > 0) {
                    try {
                        clearConfigs(workflowController.getWorkflowImplementation());

                        Configuration config = createConfiguration(list);
                        workflowController.getWorkflowImplementation().getAggregatedResources().add(config);

                        File tempBundleFile = new File("testBundle.bundle");
                        DataUtils.bundle(tempBundleFile, workflowController.getWorkflowImplementation());

                        if (bundleSubmitButtonGroup.getSelection().getActionCommand().equals("cgiPool")) {
                        } else {
                            postBundle(urlField.getText(), tempBundleFile);

                        }

                    } catch (SHIWADesktopIOException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    private void postBundle(String hostAddress, File tempBundleFile) {

        try {

            FileBody fileBody = new FileBody(tempBundleFile);
            StringBody routing = new StringBody("thing.triana");
            StringBody numtasks = new StringBody("1");
            StringBody name = new StringBody(workflowController.getWorkflowImplementation().getTitle()
                    + workflowController.getWorkflowImplementation().getUuid());

            MultipartEntity multipartEntity = new MultipartEntity();
            multipartEntity.addPart("file", fileBody);
            multipartEntity.addPart("routingkey", routing);
            multipartEntity.addPart("numtasks", numtasks);
            multipartEntity.addPart("name", name);

            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(hostAddress);
            httpPost.setEntity(multipartEntity);
            HttpResponse response = client.execute(httpPost);

            InputStream input = response.getEntity().getContent();
            InputStreamReader isr = new InputStreamReader(input);
            BufferedReader br = new BufferedReader(isr);

            String line = null;
            while ((line = br.readLine()) != null) {
                System.out.printf("\n%s", line);
            }
            client.getConnectionManager().shutdown();
        } catch (Exception e) {
        }
    }

    @CustomGUIComponent
    public Component getGUI() {
        JPanel mainPane = new JPanel();
        mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));

        JPanel locationPanel = new JPanel(new BorderLayout());
        JLabel locationLabel = new JLabel("Bundle : ");
        final JTextField locationField = new JTextField(20);
        String filePath = "";
        locationField.setText(filePath);
        JButton locationButton = new JButton("Get bundle");
        locationButton.addActionListener(new BundleParameter(mainPane, locationField));

        locationPanel.add(locationLabel, BorderLayout.WEST);
        locationPanel.add(locationField, BorderLayout.CENTER);
        locationPanel.add(locationButton, BorderLayout.EAST);
        mainPane.add(locationPanel);


        JPanel sendToPanel = new JPanel();
        sendToPanel.setLayout(new BoxLayout(sendToPanel, BoxLayout.Y_AXIS));

        JPanel urlPanel = new JPanel(new BorderLayout());
        JLabel urlLabel = new JLabel("URL : ");
        urlField = new JTextField("http://s-vmc.cs.cf.ac.uk:7025/Broker/broker");
        urlPanel.add(urlLabel, BorderLayout.WEST);
        urlPanel.add(urlField, BorderLayout.CENTER);

        sendToPanel.add(urlPanel);

        bundleSubmitButtonGroup = new ButtonGroup();
        JRadioButton cgiPoolRadio = new JRadioButton("CGI Pool");
        cgiPoolRadio.setActionCommand("cgiPool");
        JRadioButton webServerRadio = new JRadioButton("Web server");
        webServerRadio.setActionCommand("webServer");
        bundleSubmitButtonGroup.add(cgiPoolRadio);
        bundleSubmitButtonGroup.add(webServerRadio);
        sendToPanel.add(cgiPoolRadio);
        sendToPanel.add(webServerRadio);

        mainPane.add(sendToPanel);

        return mainPane;
    }


    @Override
    public void setTask(Task task) {
        this.task = task;
    }


    private Configuration createConfiguration(List list) {
        int inputObjectNo = 0;
        Configuration config = new Configuration(Configuration.ConfigType.DATA_CONFIGURATION);

        for (ReferableResource referableResource : workflowController.getWorkflowImplementation().getSignature().getPorts()) {
            if (referableResource instanceof InputPort) {
                ConfigurationResource configurationResource = new ConfigurationResource(referableResource);
                //TODO serialize
                configurationResource.setValue(list.get(inputObjectNo).toString());
                inputObjectNo++;
                configurationResource.setRefType(ConfigurationResource.RefTypes.INLINE_REF);
                config.addResourceRef(configurationResource);
            }
        }
        return config;
    }

    private void clearConfigs(WorkflowImplementation workflowImplementation) {
        ArrayList<Configuration> dataConfigs = new ArrayList<Configuration>();
        for (AggregatedResource resource : workflowImplementation.getAggregatedResources()) {
            if (resource instanceof Configuration) {
                if (((Configuration) resource).getType() == Configuration.ConfigType.DATA_CONFIGURATION) {
                    dataConfigs.add((Configuration) resource);
                }
            }
        }
        for (Configuration configuration : dataConfigs) {
            workflowImplementation.getAggregatedResources().remove(configuration);
        }
    }

    private void init(SHIWABundle shiwaBundle) {
        try {
            workflowController = new WorkflowController(shiwaBundle);
            WorkflowSignature signature = workflowController.getWorkflowImplementation().getSignature();

            inputPortArray = new ArrayList<InputPort>();
            for (ReferableResource referableResource : signature.getPorts()) {
                if (referableResource instanceof InputPort) {
                    inputPortArray.add((InputPort) referableResource);
                }
            }

            int currentNodeCount = task.getInputNodeCount();

            if (inputPortArray.size() > currentNodeCount) {
                removeNodes(inputPortArray.size());
            } else {
                try {
                    addNodes(inputPortArray.size());
                } catch (NodeException e) {
                    e.printStackTrace();
                }
            }
        } catch (SHIWADesktopIOException e) {
            e.printStackTrace();
        }
    }

    private void addNodes(int size) throws NodeException {
        while (task.getInputNodeCount() < size) {
            task.addDataInputNode();
        }
    }

    private void removeNodes(int size) {
        while (task.getInputNodeCount() > size) {
            task.removeDataInputNode(task.getInputNode(task.getInputNodeCount()));
        }
    }

    private class BundleParameter extends DefaultBundleReceivedListener implements ActionListener {

        private JPanel mainPane;
        private JTextField locationField;

        public BundleParameter(JPanel mainPane, JTextField locationField) {
            this.mainPane = mainPane;
            this.locationField = locationField;
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            BundleMonitor.addListener(this);
            open(mainPane);
        }

        @Override
        public void acceptBundleFile(File file) {
            if (file != null) {
                try {
                    SHIWABundle shiwaBundle = new SHIWABundle(file);
                    locationField.setText(file.getAbsolutePath());
                    init(shiwaBundle);
                } catch (SHIWADesktopIOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void dispose() {
        }
    }


}
