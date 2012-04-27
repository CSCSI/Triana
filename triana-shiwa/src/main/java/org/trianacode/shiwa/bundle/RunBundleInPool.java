package org.trianacode.shiwa.bundle;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.shiwa.desktop.data.description.SHIWABundle;
import org.shiwa.desktop.data.description.bundle.BundleFile;
import org.shiwa.desktop.data.description.core.Configuration;
import org.shiwa.desktop.data.description.core.WorkflowImplementation;
import org.shiwa.desktop.data.description.core.WorkflowSignature;
import org.shiwa.desktop.data.description.resource.AggregatedResource;
import org.shiwa.desktop.data.description.resource.ConfigurationResource;
import org.shiwa.desktop.data.description.resource.ReferableResource;
import org.shiwa.desktop.data.description.workflow.InputPort;
import org.shiwa.desktop.data.description.workflow.OutputPort;
import org.shiwa.desktop.data.transfer.WorkflowController;
import org.shiwa.desktop.data.util.DataUtils;
import org.shiwa.desktop.data.util.exception.SHIWADesktopIOException;
import org.shiwa.desktop.data.util.monitors.BundleMonitor;
import org.shiwa.desktop.data.util.vocab.XSDDataType;
import org.shiwa.desktop.gui.SHIWADesktop;
import org.shiwa.desktop.gui.util.listener.DefaultBundleReceivedListener;
import org.trianacode.annotation.CustomGUIComponent;
import org.trianacode.annotation.Tool;
import org.trianacode.error.ErrorEvent;
import org.trianacode.error.ErrorTracker;
import org.trianacode.gui.panels.DisplayDialog;
import org.trianacode.taskgraph.Node;
import org.trianacode.taskgraph.NodeException;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.annotation.TaskConscious;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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

    private HashMap<String, InputPort> inputPortMap;
    private HashMap<String, OutputPort> outputPortMap;
    private HashMap<String, Node> inputNodeMap;
    private HashMap<String, Node> outputNodeMap;
    private Task task;
    private WorkflowController workflowController;
    private ButtonGroup bundleSubmitButtonGroup;
    private SHIWABundle shiwaBundle;
    private String execBundleName;
    private JTextField sendURLField;
    private JTextField getURLField;
    private JTextField ttlField;
    private JTextField routingKeyField;
    private int defaultTTL = 30000;

    @org.trianacode.annotation.Process(gather = true, multipleOutputNodes = true)
    public HashMap<Node, Object> process(List list) {
        if (workflowController != null) {
            if (task.getInputNodeCount() != inputPortMap.size()) {

            } else {
                if (list.size() > 0) {
                    clearConfigs(workflowController.getWorkflowImplementation());

                    Configuration config = createConfiguration(list);
                    workflowController.getWorkflowImplementation().getAggregatedResources().add(config);

                }

                try {

                    File tempBundleFile = new File("testBundle.bundle");
                    DataUtils.bundle(tempBundleFile, workflowController.getWorkflowImplementation());

                    if (bundleSubmitButtonGroup.getSelection().getActionCommand().equals("cgiPool")) {
                    } else {
                        String uuid = postBundle(sendURLField.getText(), tempBundleFile);

                        System.out.println("Sent");
                        int ttl;
                        try {
                            ttl = Integer.parseInt(ttlField.getText());
                        } catch (NumberFormatException ex) {
                            ttl = defaultTTL;
                        }
                        String key = waitForExec(getURLField.getText(), ttl, uuid);
                        System.out.println("Got key : " + key);
                        if (key != null) {
                            File bundle = getResultBundle(getURLField.getText(), key);

                            HashMap<String, ConfigurationResource> outputs = getOutputs(bundle);
                            if (outputs != null) {
                                return compileOutputs(outputs);
                            } else {
                                return null;
                            }
                        }
                    }
                } catch (SHIWADesktopIOException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        return null;
    }

    private String getTimeStamp() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd_HH-mm-ss-SS_z");
        return dateFormat.format(new Date());
    }

    private HashMap<Node, Object> compileOutputs(HashMap<String, ConfigurationResource> outputResources) {
        HashMap<Node, Object> outputs = new HashMap<Node, Object>();
        System.out.println(outputResources.size() + " output objects.");
        for (String nodeName : outputResources.keySet()) {
            Node node = outputNodeMap.get(nodeName);
            ConfigurationResource resource = outputResources.get(nodeName);
            String resourceString = resource.getValue();

            if (resource.getRefType() == ConfigurationResource.RefTypes.INLINE_REF) {
                outputs.put(node, resourceString);
            } else if (resource.getRefType() == ConfigurationResource.RefTypes.URI_REF) {
                outputs.put(node, resourceString);
            } else if (resource.getRefType() == ConfigurationResource.RefTypes.FILE_REF) {
                String outputString = new String(resource.getBundleFile().getBytes());
                System.out.println("BundleFile at node : " + node + " contains : " + outputString);
                outputs.put(node, outputString);
            }


            System.out.println("Node " + node.getAbsoluteNodeIndex() + " named : " + nodeName
                    + " outputs : " + outputs.get(node));
        }
        return outputs;
    }


    private HashMap<String, ConfigurationResource> getOutputs(File bundle) {

        HashMap<String, ConfigurationResource> results = new HashMap<String, ConfigurationResource>();
        try {
            SHIWABundle shiwaBundle = new SHIWABundle(bundle);

            WorkflowController workflowController = new WorkflowController(shiwaBundle);

            for (Configuration configuration : workflowController.getConfigurations()) {
                System.out.println("Config type : " + configuration.getType().getString());
                if (configuration.getType() == Configuration.ConfigType.EXECUTION_CONFIGURATION) {
                    System.out.println("Received bundle has an exec config");

                    System.out.println(configuration.getAggregatedResources().size()
                            + " aggregated resources");

                    System.out.println("Exec config contains "
                            + configuration.getResources().size() + " resources.");
                    for (ConfigurationResource r : configuration.getResources()) {
                        results.put(r.getReferableResource().getTitle(), r);
                    }
                    System.out.println(results.size() + " outputs found.");
                    return results;
                }
            }
        } catch (SHIWADesktopIOException e) {
            System.out.println("Returned bundle was corrupt or null.");
            ErrorTracker.getErrorTracker().broadcastError(
                    new ErrorEvent(task, e, "Returned Bundle was corrupt or null")
            );
        }

        return null;
    }

    private File getResultBundle(String url, String key) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url + "?action=file&key=" + key);
            System.out.println("Getting JSON from " + httpGet.getURI());

            HttpResponse response = client.execute(httpGet);
            InputStream input = response.getEntity().getContent();

            File bundle = File.createTempFile("received", ".zip");
            OutputStream out = new FileOutputStream(bundle);
            byte buf[] = new byte[1024];
            int len;
            while ((len = input.read(buf)) > 0)
                out.write(buf, 0, len);
            out.close();
            input.close();

            System.out.println("Got bundle at : " + bundle.getAbsolutePath());

            client.getConnectionManager().shutdown();
            return bundle;

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String waitForExec(String url, int i, String uuid) {
        while (i > 0) {
            String key = getJSONReply(url, uuid);
            if (key != null) {
                return key;
            } else {
                try {
                    System.out.println("No key, sleeping. " + i + " remaining");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i -= 1000;
            }
        }
        return null;
    }


    private String getJSONReply(String url, String uuid) {
        try {
            HttpClient client = new DefaultHttpClient();
//            HttpGet httpGet = new HttpGet(url + "?action=json");
            HttpGet httpGet = new HttpGet(url + "?action=byid&&uuid=" + uuid);

            HttpResponse response = client.execute(httpGet);
            System.out.println(response.getEntity().getContentLength() + " from json");
            InputStream input = response.getEntity().getContent();
            InputStreamReader isr = new InputStreamReader(input);
            BufferedReader br = new BufferedReader(isr);

            String line = null;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                System.out.printf(line);
                stringBuilder.append(line);
            }
            client.getConnectionManager().shutdown();

            JSONTokener jsonTokener = new JSONTokener(stringBuilder.toString());
            JSONArray jsonArray = new JSONArray(jsonTokener);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String name = jsonObject.getString("name");

                if (name.equals(execBundleName)) {
                    String key = jsonObject.getString("key");
                    System.out.println("Name = " + execBundleName + " key = " + key);
                    return key;
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String postBundle(String hostAddress, File tempBundleFile) {

        String line = null;
        try {
            FileBody fileBody = new FileBody(tempBundleFile);
            StringBody routing = new StringBody(routingKeyField.getText());
            StringBody numtasks = new StringBody("1");
            execBundleName = workflowController.getWorkflowImplementation().getTitle()
                    + "-" + getTimeStamp();
            StringBody name = new StringBody(execBundleName);

            MultipartEntity multipartEntity = new MultipartEntity();
            multipartEntity.addPart("file", fileBody);
            multipartEntity.addPart("routingkey", routing);
            multipartEntity.addPart("numtasks", numtasks);
            multipartEntity.addPart("name", name);

            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(hostAddress);
            httpPost.setEntity(multipartEntity);
            System.out.println("Sending " + httpPost.getEntity().getContentLength()
                    + " bytes to " + hostAddress);
            HttpResponse response = client.execute(httpPost);

            InputStream input = response.getEntity().getContent();
            InputStreamReader isr = new InputStreamReader(input);
            BufferedReader br = new BufferedReader(isr);

            line = null;
            while ((line = br.readLine()) != null) {
                System.out.printf("\n%s", line);
            }
            client.getConnectionManager().shutdown();
        } catch (Exception ignored) {
        }
        return line;
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

        JPanel sendURLPanel = new JPanel(new BorderLayout());
        JLabel sendURLLabel = new JLabel("Send URL : ");
        sendURLField = new JTextField("http://s-vmc.cs.cf.ac.uk:7025/Broker/broker");
        sendURLPanel.add(sendURLLabel, BorderLayout.WEST);
        sendURLPanel.add(sendURLField, BorderLayout.CENTER);

        sendToPanel.add(sendURLPanel);

        JPanel getURLPanel = new JPanel(new BorderLayout());
        JLabel getURLLabel = new JLabel("Results URL : ");
        getURLField = new JTextField("http://s-vmc.cs.cf.ac.uk:7025/Broker/results");
        getURLPanel.add(getURLLabel, BorderLayout.WEST);
        getURLPanel.add(getURLField, BorderLayout.CENTER);

        sendToPanel.add(getURLPanel);

        JPanel routingKeyPanel = new JPanel(new BorderLayout());
        JLabel routingKeyLabel = new JLabel("Routing Key : ");
        routingKeyField = new JTextField("*.triana");
        routingKeyPanel.add(routingKeyLabel, BorderLayout.WEST);
        routingKeyPanel.add(routingKeyField, BorderLayout.CENTER);

        sendToPanel.add(routingKeyPanel);

        JPanel ttlPanel = new JPanel(new BorderLayout());
        JLabel ttlLabel = new JLabel("Execution wait time (ms) : ");
        ttlField = new JTextField("" + defaultTTL);
        ttlPanel.add(ttlLabel, BorderLayout.WEST);
        ttlPanel.add(ttlField, BorderLayout.CENTER);

        sendToPanel.add(ttlPanel);

        bundleSubmitButtonGroup = new ButtonGroup();
        JRadioButton cgiPoolRadio = new JRadioButton("CGI Pool");
        cgiPoolRadio.setActionCommand("cgiPool");
        JRadioButton webServerRadio = new JRadioButton("Web server");
        webServerRadio.setSelected(true);
        webServerRadio.setActionCommand("webServer");
        bundleSubmitButtonGroup.add(cgiPoolRadio);
        bundleSubmitButtonGroup.add(webServerRadio);
        sendToPanel.add(cgiPoolRadio);
        sendToPanel.add(webServerRadio);

        mainPane.add(sendToPanel);

        JButton viewBundle = new JButton("View Bundle");
        viewBundle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (shiwaBundle != null) {
                    SHIWADesktop shiwaDesktop = new SHIWADesktop(SHIWADesktop.ButtonOption.SHOW_TOOLBAR);
                    try {
                        shiwaDesktop.openBundle(shiwaBundle);
                        new DisplayDialog(shiwaDesktop.getPanel(), "Shiwa Desktop");
                    } catch (SHIWADesktopIOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        mainPane.add(viewBundle);

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

                Object inputObject = list.get(inputObjectNo);

                if (inputObject instanceof File) {
                    try {
                        configurationResource.setRefType(ConfigurationResource.RefTypes.FILE_REF);
                        BundleFile bf = DataUtils.createBundleFile((File) inputObject, config.getId() + "/");
                        bf.setType(BundleFile.FileType.INPUT_FILE);
                        config.getBundleFiles().add(bf);
                        configurationResource.setBundleFile(bf);
                    } catch (SHIWADesktopIOException e) {
                        e.printStackTrace();
                    }
                } else {
                    configurationResource.setValue(inputObject.toString());
                    configurationResource.setRefType(ConfigurationResource.RefTypes.INLINE_REF);
                }
                inputObjectNo++;
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
        workflowController = null;
        try {
            workflowController = new WorkflowController(shiwaBundle);
            System.out.println("Loaded " + workflowController.getWorkflowImplementation().getTitle());
        } catch (Exception e) {
            e.printStackTrace();
        }

        inputPortMap = new HashMap<String, InputPort>();
        ArrayList<String> inputDataTypes = new ArrayList<String>();
        outputPortMap = new HashMap<String, OutputPort>();
        ArrayList<String> outputDataTypes = new ArrayList<String>();
        WorkflowSignature signature = workflowController.getWorkflowImplementation().getSignature();
        for (ReferableResource referableResource : signature.getPorts()) {
            if (referableResource instanceof InputPort) {
                InputPort inputPort = (InputPort) referableResource;
                Class dataClass = XSDDataType.getClass(inputPort.getDataType());
                if (dataClass != null) {
                    inputDataTypes.add(dataClass.getCanonicalName());
                }
                inputPortMap.put(referableResource.getTitle(), inputPort);
            }
            if (referableResource instanceof OutputPort) {
                OutputPort outputPort = (OutputPort) referableResource;
                Class dataClass = XSDDataType.getClass(outputPort.getDataType());
                if (dataClass != null) {
                    outputDataTypes.add(dataClass.getCanonicalName());
                }
                outputPortMap.put(referableResource.getTitle(), outputPort);
            }
        }

        String[] inputTypes = new String[inputDataTypes.size()];
        inputDataTypes.toArray(inputTypes);
        task.setDataInputTypes(inputTypes);

        String[] outputTypes = new String[outputDataTypes.size()];
        outputDataTypes.toArray(outputTypes);
        task.setDataOutputTypes(outputTypes);

        System.out.println("Bundle sig has " + inputPortMap.size() +
                " inputs. Current input nodes " + task.getInputNodeCount() +
                " Bundle sig has " + outputPortMap.size() +
                " outputs. Current output nodes " + task.getOutputNodeCount()
        );

        if (inputPortMap.size() < task.getInputNodeCount()) {
            removeNodes(inputPortMap.size(), true);
        }
        if (inputPortMap.size() > task.getInputNodeCount()) {
            try {
                addNodes(inputPortMap.size(), true);
            } catch (NodeException e) {
                e.printStackTrace();
            }
        }

        inputNodeMap = new HashMap<String, Node>();
        String[] inputPorts = new String[inputPortMap.size()];
        inputPortMap.keySet().toArray(inputPorts);
        for (int i = 0; i < inputPorts.length; i++) {
            String name = inputPorts[i];
            inputNodeMap.put(name, task.getInputNode(i));
        }

        if (outputPortMap.size() < task.getOutputNodeCount()) {
            removeNodes(outputPortMap.size(), false);
        }
        if (outputPortMap.size() > task.getOutputNodeCount()) {
            try {
                addNodes(outputPortMap.size(), false);
            } catch (NodeException e) {
                e.printStackTrace();
            }
        }
        outputNodeMap = new HashMap<String, Node>();
        String[] outputPorts = new String[outputPortMap.size()];
        outputPortMap.keySet().toArray(outputPorts);
        for (int i = 0; i < outputPorts.length; i++) {
            String name = outputPorts[i];
            outputNodeMap.put(name, task.getOutputNode(i));
        }
    }

    private void addNodes(int size, boolean input) throws NodeException {
        if (input) {
            while (task.getInputNodeCount() < size) {
                task.addDataInputNode();
            }
        } else {
            while (task.getOutputNodeCount() < size) {
                task.addDataOutputNode();
            }
        }
    }

    private void removeNodes(int size, boolean input) {
        if (input) {
            while (task.getInputNodeCount() > size) {
                Node node = task.getInputNode(task.getDataInputNodeCount() - 1);
                node.disconnect();
                task.removeDataInputNode(node);
            }
        } else {
            while (task.getOutputNodeCount() > size) {
                Node node = task.getOutputNode(task.getDataOutputNodeCount() - 1);
                node.disconnect();
                task.removeDataOutputNode(node);
            }
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
                    shiwaBundle = new SHIWABundle(file);
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
