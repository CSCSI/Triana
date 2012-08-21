package org.trianacode.shiwaall.workflowCreation;

import org.shiwa.desktop.data.description.ConcreteBundle;
import org.shiwa.desktop.data.description.bundle.BundleFile;
import org.shiwa.desktop.data.description.core.DataMapping;
import org.shiwa.desktop.data.description.core.Mapping;
import org.shiwa.desktop.data.description.core.WorkflowImplementation;
import org.shiwa.desktop.data.description.resource.AggregatedResource;
import org.shiwa.desktop.data.description.workflow.SHIWAProperty;
import org.shiwa.desktop.data.util.exception.SHIWADesktopIOException;
import org.trianacode.annotation.CustomGUIComponent;
import org.trianacode.annotation.Tool;
import org.trianacode.enactment.logging.stampede.StampedeLog;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.shiwaall.bundle.ShiwaBundleHelper;
import org.trianacode.shiwaall.utils.BrokerUtils;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.annotation.TaskConscious;
import org.trianacode.taskgraph.ser.XMLWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//import org.shiwa.desktop.data.description.core.Configuration;

/**
* Created by IntelliJ IDEA.
* User: Ian Harvey
* Date: 19/04/2012
* Time: 14:43
* To change this template use File | Settings | File Templates.
*/
@Tool
public class BundleEditor implements TaskConscious {

//    @TextFieldParameter
    public String bundlePath = "/Users/ian/dartBundle.zip";
    private ShiwaBundleHelper shiwaBundleHelper;
    private Task task;

    @org.trianacode.annotation.Process
    public ArrayList<File> process(List list) {

        System.out.println("Creating " + list.size() + " bundles");
        ArrayList<File> bundles = new ArrayList<File>();

        File inputBundleFile = new File(bundlePath);
        System.out.println("input exists : " + inputBundleFile.exists());

        if(inputBundleFile.exists()){
            try {
                shiwaBundleHelper = new ShiwaBundleHelper(new ConcreteBundle(inputBundleFile));

                clearConfigs(shiwaBundleHelper.getWorkflowImplementation());

                WorkflowImplementation impl = shiwaBundleHelper.getWorkflowImplementation();

                for (Object object : list) {
                    if (object instanceof TaskGraph) {
                        TaskGraph taskGraph = (TaskGraph) object;

                        cleanProperties();
                        UUID runUUID = UUID.randomUUID();

                        BrokerUtils.prepareSubworkflow(
                                task, runUUID, shiwaBundleHelper.getWorkflowImplementation()
                        );

                        System.out.println("Adding imp " + taskGraph.getToolName());
                        impl.setDefinition(
                                new BundleFile(
                                        getWorkflowDefinition(taskGraph), taskGraph.getToolName()));

                        File temp = File.createTempFile(taskGraph.getToolName() + "-", "");
                        File b = shiwaBundleHelper.saveBundle(temp);
                        System.out.println("Made " + b.getAbsolutePath());
                        bundles.add(b);
                    }
                }
            } catch (SHIWADesktopIOException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bundles;
    }

    @CustomGUIComponent
    public Component getGUI() {
        loadParams();

        JPanel mainPane = new JPanel();
        mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));

        JPanel locationPanel = new JPanel(new BorderLayout());
        JLabel locationLabel = new JLabel("File Path : ");
        final JTextField locationField = new JTextField(20);
        locationField.setText(bundlePath);
        JButton locationButton = new JButton("...");
        locationButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setMultiSelectionEnabled(false);
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

                File file = new File(bundlePath);
                if (file.exists()) {
                    chooser.setCurrentDirectory(file.getParentFile());
                }
                int returnVal = chooser.showDialog(GUIEnv.getApplicationFrame(), "File");
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File f = chooser.getSelectedFile();
                    if (f != null) {
                        bundlePath = f.getAbsolutePath();
                        locationField.setText(bundlePath);
                        task.setParameter("filePath", bundlePath);
                    }
                }
            }
        });
        locationPanel.add(locationLabel, BorderLayout.WEST);
        locationPanel.add(locationField, BorderLayout.CENTER);
        locationPanel.add(locationButton, BorderLayout.EAST);

        mainPane.add(locationPanel);

        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                locationField.setText("");
                task.setParameter("filePath", "");
            }
        });
        mainPane.add(clearButton);
        return mainPane;
    }

    private void loadParams() {
        String fileString = (String) task.getParameter("bundlePath");
        if (fileString != null && new File(fileString).exists()) {
            bundlePath = fileString;
        }
    }


    private void cleanProperties() {
        List<SHIWAProperty> props = shiwaBundleHelper.getWorkflowImplementation().getProperties();
        ArrayList<SHIWAProperty> toRemove = new ArrayList<SHIWAProperty>();

        for (SHIWAProperty p : props) {
            if (p.getTitle().equals(StampedeLog.PARENT_UUID_STRING) ||
                    p.getTitle().equals(StampedeLog.RUN_UUID_STRING) ||
                    p.getTitle().equals(StampedeLog.JOB_ID) ||
                    p.getTitle().equals(StampedeLog.JOB_INST_ID)) {
                toRemove.add(p);
            }
        }

        for (SHIWAProperty rem : toRemove) {
            props.remove(rem);
        }
    }

    private void clearConfigs(WorkflowImplementation workflowImplementation) {
        ArrayList<Mapping> dataConfigs = new ArrayList<Mapping>();
        for (AggregatedResource resource : workflowImplementation.getAggregatedResources()) {
            if (resource instanceof DataMapping) {
//                if (((Configuration) resource).getType() == Configuration.ConfigType.DATA_CONFIGURATION) {
                dataConfigs.add((DataMapping) resource);
//                }
            }
        }
        for (Mapping configuration : dataConfigs) {
            workflowImplementation.getAggregatedResources().remove(configuration);
        }
    }

    public InputStream getWorkflowDefinition(Task task) {
        try {
            File temp = File.createTempFile("publishedTaskgraphTemp", ".xml");
            temp.deleteOnExit();
            XMLWriter writer = new XMLWriter(new BufferedWriter(new FileWriter(temp)));
            writer.writeComponent(task);
            writer.close();
            return new FileInputStream(temp);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void setTask(Task task) {
        this.task = task;
    }
}
