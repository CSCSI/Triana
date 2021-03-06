package org.trianacode.shiwaall.bundle;

import org.jivesoftware.smack.XMPPException;
import org.shiwa.desktop.data.description.ConcreteBundle;
import org.shiwa.desktop.data.description.resource.ConfigurationResource;
import org.shiwa.desktop.data.description.workflow.InputPort;
import org.shiwa.desktop.data.description.workflow.OutputPort;
import org.shiwa.desktop.data.util.exception.SHIWADesktopIOException;
import org.shiwa.desktop.data.util.monitors.BundleMonitor;
import org.shiwa.desktop.gui.SHIWADesktop;
import org.shiwa.desktop.gui.util.InterfaceUtils;
import org.shiwa.desktop.gui.util.listener.DefaultBundleReceivedListener;
import org.trianacode.annotation.CustomGUIComponent;
import org.trianacode.annotation.Tool;
import org.trianacode.enactment.logging.stampede.StampedeLog;
import org.trianacode.gui.panels.DisplayDialog;
import org.trianacode.shiwaall.utils.BrokerUtils;
import org.trianacode.taskgraph.CableException;
import org.trianacode.taskgraph.Node;
import org.trianacode.taskgraph.NodeException;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.annotation.TaskConscious;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

// TODO: Auto-generated Javadoc
/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 28/02/2012
 * Time: 16:54
 * To change this template use File | Settings | File Templates.
 */
@Tool
public class BundleSubmit implements TaskConscious {

    /** The input port map. */
    private HashMap<String, InputPort> inputPortMap;
    
    /** The output port map. */
    private HashMap<String, OutputPort> outputPortMap;
    
    /** The input node map. */
    private HashMap<String, Node> inputNodeMap;
    
    /** The output node map. */
    private HashMap<String, Node> outputNodeMap;
    
    /** The task. */
    private Task task;
    
    /** The bundle submit button group. */
    private ButtonGroup bundleSubmitButtonGroup;
    
    /** The concrete bundle. */
    private ConcreteBundle concreteBundle;
    
    /** The send url field. */
    private JTextField sendURLField;
    
    /** The get url field. */
    private JTextField getURLField;
    
    /** The ttl field. */
    private JTextField ttlField;
    
    /** The routing key field. */
    private JTextField routingKeyField;
    
    /** The default ttl. */
    private int defaultTTL = 30000;
    
    /** The shiwa bundle helper. */
    private ShiwaBundleHelper shiwaBundleHelper;

    /**
     * Process.
     *
     * @param list the list
     * @return the hash map
     * @throws SHIWADesktopIOException the sHIWA desktop io exception
     */
    @org.trianacode.annotation.Process(gather = true, multipleOutputNodes = true)
    public HashMap<Node, Object> process(List list) throws SHIWADesktopIOException {
        if (concreteBundle != null) {
            try{
                shiwaBundleHelper = new ShiwaBundleHelper(concreteBundle);
//                WorkflowImplementation workflowImplementation;
//                if(concreteBundle.getPrimaryConcreteTask() instanceof WorkflowImplementation){
//                    workflowImplementation = (WorkflowImplementation) concreteBundle.getPrimaryConcreteTask();

                shiwaBundleHelper.createConfiguration(list);

//                workflowImplementation = shiwaBundleHelper.getWorkflowImplementation();
                if (task.getInputNodeCount() != inputPortMap.size()) {

                } else {
                    if (list.size() > 0) {
                        shiwaBundleHelper.clearConfigs();
                        shiwaBundleHelper.createConfiguration(list);
//                        workflowImplementation.getAggregatedResources().add(config);

                    }
                }
                BrokerUtils.prepareSubworkflow(
                        task, UUID.randomUUID(), shiwaBundleHelper.getWorkflowImplementation()
                );

                File tempBundleFile = new File("testBundle.bundle");
//                    DataUtils.bundle(tempBundleFile, workflowImplementation);

                shiwaBundleHelper.bundle(tempBundleFile);
                if (bundleSubmitButtonGroup.getSelection().getActionCommand().equals("cgiPool")) {
                    doPool(tempBundleFile);
                } else {
                    return doBroker(tempBundleFile);
                }
            } catch (SHIWADesktopIOException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }


    /**
     * Do pool.
     *
     * @param tempBundleFile the temp bundle file
     */
    private void doPool(File tempBundleFile) {
        try {
            fr.insalyon.creatis.shiwapool.client.Main.main(
                    new String[]{"--submitBundle", tempBundleFile.getAbsolutePath()}
            );
        } catch (SHIWADesktopIOException e) {
            e.printStackTrace();
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }

    /**
     * Do broker.
     *
     * @param tempBundleFile the temp bundle file
     * @return the hash map
     */
    private HashMap<Node, Object> doBroker(File tempBundleFile) {
        String execBundleName = concreteBundle.getPrimaryConcreteTask().getTitle()
                + "-" + BrokerUtils.getTimeStamp();

        String uuid = BrokerUtils.postBundle(
                sendURLField.getText(), routingKeyField.getText(), execBundleName, tempBundleFile);

        System.out.println("Sent");
        int ttl;
        try {
            ttl = Integer.parseInt(ttlField.getText());
        } catch (NumberFormatException ex) {
            ttl = defaultTTL;
        }
        String key = BrokerUtils.waitForExec(getURLField.getText(), ttl, uuid, execBundleName);
        System.out.println("Got key : " + key);
        if (key != null) {
            File bundle = BrokerUtils.getResultBundle(getURLField.getText(), key);

            HashMap<String, ConfigurationResource> outputs = ShiwaBundleHelper.getOutputs(bundle);
            if (outputs != null) {
                return shiwaBundleHelper.compileOutputs(outputs, outputNodeMap);
            } else {
                return null;
            }
        }
        return null;
    }

//    private HashMap<Node, Object> compileOutputs(HashMap<String, ConfigurationResource> outputResources) {
//        HashMap<Node, Object> outputs = new HashMap<Node, Object>();
//        System.out.println(outputResources.size() + " output objects.");
//        for (String nodeName : outputResources.keySet()) {
//            Node node = outputNodeMap.get(nodeName);
//            ConfigurationResource resource = outputResources.get(nodeName);
//            String resourceString = resource.getValue();
//
//            if (resource.getRefType() == ConfigurationResource.RefTypes.INLINE_REF) {
//                outputs.put(node, resourceString);
//            } else if (resource.getRefType() == ConfigurationResource.RefTypes.URI_REF) {
//                outputs.put(node, resourceString);
//            } else if (resource.getRefType() == ConfigurationResource.RefTypes.FILE_REF) {
////                String outputString = new String(resource.getBundleFile().getBytes());
////                System.out.println("BundleFile at node : " + node + " contains : " + outputString);
////                outputs.put(node, outputString);
//                String tempBundleLocation = resource.getBundleFile().getSystemPath();
//                System.out.println("BundleFile at node : " + node + " temp location : " + tempBundleLocation);
//                String outputString = BundleUtils.readFile(tempBundleLocation);
//                outputs.put(node, outputString);
//            }
//
//
//            System.out.println("Node " + node.getAbsoluteNodeIndex() + " named : " + nodeName
//                    + " outputs : " + outputs.get(node));
//        }
//        return outputs;
//    }


//    private HashMap<String, ConfigurationResource> getOutputs(File bundle) {
//
//        HashMap<String, ConfigurationResource> results = new HashMap<String, ConfigurationResource>();
//        try {
////            SHIWABundle shiwaBundle = new SHIWABundle(bundle);
//
//            ConcreteBundle concreteBundle = new ConcreteBundle(bundle);
////            WorkflowController workflowController = new WorkflowController(shiwaBundle);
//
//            for (Mapping configuration : concreteBundle.getPrimaryMappings()) {
//                System.out.println("Config type : " + configuration.getClass().getCanonicalName());
//
//                if (configuration instanceof ExecutionMapping) {
//                    System.out.println("Received bundle has an exec config");
//
//                    System.out.println(configuration.getAggregatedResources().size()
//                            + " aggregated resources");
//
//                    System.out.println("Exec config contains "
//                            + configuration.getResources().size() + " resources.");
//                    for (ConfigurationResource r : configuration.getResources()) {
//                        results.put(r.getReferableResource().getTitle(), r);
//                    }
//                    System.out.println(results.size() + " outputs found.");
//                    return results;
//                }
//            }
//        } catch (SHIWADesktopIOException e) {
//            System.out.println("Returned bundle was corrupt or null.");
//            ErrorTracker.getErrorTracker().broadcastError(
//                    new ErrorEvent(task, e, "Returned Bundle was corrupt or null")
//            );
//        }
//
//        return null;
//    }

    /**
 * Gets the gui.
 *
 * @return the gui
 */
@CustomGUIComponent
    public Component getGUI() {
        JPanel mainPane = new JPanel();
        mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));

        JPanel bundlePanel = new JPanel(new BorderLayout());
        JLabel locationLabel = new JLabel("Bundle : ");
        JTextField locationField = new JTextField(20);
        String filePath = "/Users/ian/stuff/concat-two.zip";
        locationField.setText(filePath);
        JButton locationButton = new JButton("Get bundle");
        locationButton.addActionListener(new BundleParameter(mainPane, locationField));

        bundlePanel.add(locationLabel, BorderLayout.WEST);
        bundlePanel.add(locationField, BorderLayout.CENTER);
        bundlePanel.add(locationButton, BorderLayout.EAST);

        mainPane.add(bundlePanel);

        final JPanel sendURLPanel = new JPanel(new BorderLayout());
        final JLabel sendURLLabel = new JLabel("Send URL : ");
        sendURLField = new JTextField("http://s-vmc.cs.cf.ac.uk:7025/Broker/broker");
        sendURLPanel.add(sendURLLabel, BorderLayout.WEST);
        sendURLPanel.add(sendURLField, BorderLayout.CENTER);

        mainPane.add(sendURLPanel);

        final JPanel getURLPanel = new JPanel(new BorderLayout());
        JLabel getURLLabel = new JLabel("Results URL : ");
        getURLField = new JTextField("http://s-vmc.cs.cf.ac.uk:7025/Broker/results");
        getURLPanel.add(getURLLabel, BorderLayout.WEST);
        getURLPanel.add(getURLField, BorderLayout.CENTER);

        mainPane.add(getURLPanel);

        final JPanel routingKeyPanel = new JPanel(new BorderLayout());
        JLabel routingKeyLabel = new JLabel("Routing Key : ");
        routingKeyField = new JTextField("*.triana");
        routingKeyPanel.add(routingKeyLabel, BorderLayout.WEST);
        routingKeyPanel.add(routingKeyField, BorderLayout.CENTER);

        mainPane.add(routingKeyPanel);

        JPanel ttlPanel = new JPanel(new BorderLayout());
        JLabel ttlLabel = new JLabel("Execution wait time (ms) : ");
        ttlField = new JTextField("" + defaultTTL);
        ttlPanel.add(ttlLabel, BorderLayout.WEST);
        ttlPanel.add(ttlField, BorderLayout.CENTER);

        mainPane.add(ttlPanel);

        JPanel sendToPanel = new JPanel();
        sendToPanel.setLayout(new BoxLayout(sendToPanel, BoxLayout.Y_AXIS));
        bundleSubmitButtonGroup = new ButtonGroup();

        final JRadioButton cgiPoolRadio = new JRadioButton("CGI Pool");
        final JRadioButton webServerRadio = new JRadioButton("Web server");

        ActionListener radioListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JRadioButton radioButton = (JRadioButton) actionEvent.getSource();
                if (radioButton == webServerRadio) {
                    if (radioButton.isSelected()) {
                        setEnabling(sendURLPanel, true);
                        setEnabling(getURLPanel, true);
                        setEnabling(routingKeyPanel, true);
                    }
                }
                if (radioButton == cgiPoolRadio) {
                    if (radioButton.isSelected()) {
                        setEnabling(sendURLPanel, false);
                        setEnabling(getURLPanel, false);
                        setEnabling(routingKeyPanel, false);
                    }
                }
            }
        };

        cgiPoolRadio.setActionCommand("cgiPool");
        cgiPoolRadio.addActionListener(radioListener);

        webServerRadio.setSelected(true);
        webServerRadio.setActionCommand("webServer");
        webServerRadio.addActionListener(radioListener);


        bundleSubmitButtonGroup.add(cgiPoolRadio);
        bundleSubmitButtonGroup.add(webServerRadio);
        sendToPanel.add(cgiPoolRadio);
        sendToPanel.add(webServerRadio);

        mainPane.add(sendToPanel);

        JButton viewBundle = new JButton("View Bundle");
        viewBundle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (concreteBundle != null) {
                    SHIWADesktop shiwaDesktop = new SHIWADesktop(SHIWADesktop.ButtonOption.SHOW_TOOLBAR);
                    try {
                        shiwaDesktop.openBundle(concreteBundle);
                        Image icon = InterfaceUtils.X16_ICON.getImage();
                        new DisplayDialog(shiwaDesktop.getPanel(), "Shiwa Desktop", icon);
                    } catch (SHIWADesktopIOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        mainPane.add(viewBundle);

        return mainPane;
    }

    /**
     * Sets the enabling.
     *
     * @param parent the parent
     * @param enabled the enabled
     */
    private void setEnabling(Container parent, boolean enabled) {
        for (Component component : parent.getComponents()) {
            if (component instanceof Container) {
                setEnabling((Container) component, enabled);
            }
            component.setEnabled(enabled);
        }
    }


    /* (non-Javadoc)
     * @see org.trianacode.taskgraph.annotation.TaskConscious#setTask(org.trianacode.taskgraph.Task)
     */
    @Override
    public void setTask(Task task) {
        this.task = task;
        task.setParameter(StampedeLog.STAMPEDE_TASK_TYPE, StampedeLog.JobType.dax.desc);
    }


//    private Mapping createConfiguration(List list) {
//        int inputObjectNo = 0;
//        Mapping config = new DataMapping();
//
//        for (ReferableResource referableResource : concreteBundle.getPrimaryConcreteTask().getSignature().getPorts()) {
//            if (referableResource instanceof InputPort) {
//                ConfigurationResource configurationResource = new ConfigurationResource(referableResource);
//                //TODO serialize
//
//                Object inputObject = list.get(inputObjectNo);
//
//                if (inputObject instanceof File) {
//                    try {
//                        configurationResource.setRefType(ConfigurationResource.RefTypes.FILE_REF);
//                        BundleFile bf = DataUtils.createBundleFile((File) inputObject, config.getId() + "/");
//                        bf.setType(BundleFile.FileType.DATA_FILE);
//                        config.getBundleFiles().add(bf);
//                        configurationResource.setBundleFile(bf);
//                    } catch (SHIWADesktopIOException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    configurationResource.setValue(inputObject.toString());
//                    configurationResource.setRefType(ConfigurationResource.RefTypes.INLINE_REF);
//                }
//                inputObjectNo++;
//                config.addResourceRef(configurationResource);
//            }
//        }
//        return config;
//    }

//    private void clearConfigs(WorkflowImplementation workflowImplementation) {
//        ArrayList<Mapping> dataConfigs = new ArrayList<Mapping>();
//        for (AggregatedResource resource : workflowImplementation.getAggregatedResources()) {
//            if (resource instanceof DataMapping) {
////                if (((Configuration) resource).getType() == Configuration.ConfigType.DATA_CONFIGURATION) {
//                    dataConfigs.add((DataMapping) resource);
////                }
//            }
//        }
//        for (Mapping configuration : dataConfigs) {
//            workflowImplementation.getAggregatedResources().remove(configuration);
//        }
//    }

    /**
 * Inits the.
 */
private void init() {
//        workflowController = null;
        try {
//            workflowController = new WorkflowController(shiwaBundle);
            System.out.println("Loaded " + concreteBundle.getPrimaryConcreteTask().getTitle());
        } catch (Exception e) {
            e.printStackTrace();
        }

        inputPortMap = new HashMap<String, InputPort>();
        ArrayList<String> inputDataTypes = new ArrayList<String>();
        outputPortMap = new HashMap<String, OutputPort>();
        ArrayList<String> outputDataTypes = new ArrayList<String>();
        shiwaBundleHelper.getTaskSignature(inputPortMap, outputPortMap);
//        TaskSignature signature = concreteBundle.getPrimaryConcreteTask().getSignature();
//        for (ReferableResource referableResource : signature.getPorts()) {
//            if (referableResource instanceof InputPort) {
//                InputPort inputPort = (InputPort) referableResource;
////                Class dataClass = XSDDataType.getClass(inputPort.getDataType());
////                if (dataClass != null) {
////                    inputDataTypes.add(dataClass.getCanonicalName());
////                }
//                inputPortMap.put(referableResource.getTitle(), inputPort);
//            }
//            if (referableResource instanceof OutputPort) {
//                OutputPort outputPort = (OutputPort) referableResource;
////                Class dataClass = XSDDataType.getClass(outputPort.getDataType());
////                if (dataClass != null) {
////                    outputDataTypes.add(dataClass.getCanonicalName());
////                }
//                outputPortMap.put(referableResource.getTitle(), outputPort);
//            }
//        }

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
        task.setDataInputTypes(new String[]{"java.lang.Object"});
        task.setDataOutputTypes(new String[]{"java.lang.Object"});

        outputNodeMap = new HashMap<String, Node>();
        String[] outputPorts = new String[outputPortMap.size()];
        outputPortMap.keySet().toArray(outputPorts);
        for (int i = 0; i < outputPorts.length; i++) {
            String name = outputPorts[i];
            outputNodeMap.put(name, task.getOutputNode(i));
        }
    }

    /**
     * Adds the nodes.
     *
     * @param size the size
     * @param input the input
     * @throws NodeException the node exception
     */
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

    /**
     * Removes the nodes.
     *
     * @param size the size
     * @param input the input
     */
    private void removeNodes(int size, boolean input) {
        if (input) {
            while (task.getInputNodeCount() > size) {
                Node node = task.getInputNode(task.getDataInputNodeCount() - 1);
                try {
                    task.getParent().disconnect(node.getCable());
                } catch (CableException ignored) {
                }
                task.removeDataInputNode(node);
            }
        } else {
            while (task.getOutputNodeCount() > size) {
                Node node = task.getOutputNode(task.getDataOutputNodeCount() - 1);
                try {
                    task.getParent().disconnect(node.getCable());
                } catch (CableException ignored) {
                }
                task.removeDataOutputNode(node);
            }
        }
    }

    /**
     * The Class BundleParameter.
     */
    private class BundleParameter extends DefaultBundleReceivedListener implements ActionListener {
        
        /** The main pane. */
        private JPanel mainPane;
        
        /** The location field. */
        private JTextField locationField;

        /**
         * Instantiates a new bundle parameter.
         *
         * @param mainPane the main pane
         * @param locationField the location field
         */
        public BundleParameter(JPanel mainPane, JTextField locationField) {
            this.mainPane = mainPane;
            this.locationField = locationField;
        }

        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            BundleMonitor.addListener(this);
            open(mainPane);
        }

        /* (non-Javadoc)
         * @see org.shiwa.desktop.data.transfer.BundleReceivedListener#acceptBundleFile(java.io.File)
         */
        @Override
        public void acceptBundleFile(File file) {
            if (file != null) {
                try {
                    concreteBundle = new ConcreteBundle(file);
//                    shiwaBundle = new SHIWABundle(file);
                    locationField.setText(file.getAbsolutePath());
                    init();
                } catch (SHIWADesktopIOException e) {
                    e.printStackTrace();
                }
            }
        }

        /* (non-Javadoc)
         * @see org.shiwa.desktop.gui.util.listener.DefaultBundleReceivedListener#dispose()
         */
        @Override
        public void dispose() {
        }
    }


}
