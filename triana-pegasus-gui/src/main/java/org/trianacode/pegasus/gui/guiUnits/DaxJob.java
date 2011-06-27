package org.trianacode.pegasus.gui.guiUnits;

import org.apache.commons.logging.Log;
import org.trianacode.annotation.CustomGUIComponent;
import org.trianacode.annotation.Tool;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.pegasus.dax.Displayer;
import org.trianacode.pegasus.dax.JobUnit;
import org.trianacode.taskgraph.annotation.TaskConscious;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 23/05/2011
 * Time: 12:39
 * To change this template use File | Settings | File Templates.
 */
@Tool(renderingHints = {"DAX Job"}, minimumInputs = 1, minimumOutputs = 1)
public class DaxJob extends JobUnit implements Displayer, TaskConscious {


    JPanel upperPanel; // = new JPanel(new GridLayout(4, 2, 5, 5));
    JPanel lowerPanel; // = new JPanel();
    JPanel lowerPanel1; // = new JPanel(new GridLayout(3, 1, 5, 5));
    JPanel lowerPanelAuto; // = new JPanel(new GridLayout(1, 2, 5, 5));
    JPanel lowerPanelScatter; // = new JPanel(new GridLayout(1, 2, 5, 5));
    JPanel lowerPanelOne2One; // = new JPanel(new GridLayout(1, 2, 5, 5));

    JTextField nameField;
    JTextField execField;
    JTextField argsField;
    JLabel collectLabel;
    JLabel numberJobsLabel;
    JLabel numberInputFilesLabel;

    JComboBox filesPerJobCombo;
    JComboBox jobsCombo;
    JCheckBox collectionBox;
    JRadioButton autoCheck;
    JRadioButton scatterCheck;
    JRadioButton spreadCheck;
    JRadioButton one2oneCheck;


    @org.trianacode.annotation.Process(gather = true)
    public UUID fakeProcess(List list) {
        return this.process(list);
    }

    @CustomGUIComponent
    public Component getComponent() {
        JPanel mainPane = new JPanel();
        mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));

        ActionPerformer actionPerformer = new ActionPerformer();
        ItemPerformer itemPerformer = new ItemPerformer();

        upperPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        upperPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Job"));

        JLabel nameLabel = new JLabel("Job Name :");
        upperPanel.add(nameLabel);
        nameField = new JTextField(jobName);

//        if(task != null){
//            changeToolName(task.getToolName());
//        }
        upperPanel.add(nameField);

        JLabel jobExecLabel = new JLabel("Executable location");
        upperPanel.add(jobExecLabel);
        execField = new JTextField("");
        execField.setText(exec);
        upperPanel.add(execField);


        JLabel argsLabel = new JLabel("Job Args :");
        upperPanel.add(argsLabel);
        argsField = new JTextField("");
        if (task != null) {
            argsField.setText((String) task.getParameter("args"));
        }
        upperPanel.add(argsField);

        collectionBox = new JCheckBox("Collection", collection);
        collectionBox.addItemListener(itemPerformer);
        upperPanel.add(collectionBox);

        collectLabel = new JLabel("");
        if (collection) {
            collectLabel.setText("Collection of jobs.");
        } else {
            collectLabel.setText("Not a collection");
        }
        upperPanel.add(collectLabel);

        mainPane.add(upperPanel);

        lowerPanel = new JPanel();
        lowerPanel.setLayout(new BoxLayout(lowerPanel, BoxLayout.Y_AXIS));
        lowerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Job Collection Options"));

        lowerPanel1 = new JPanel(new GridLayout(3, 1, 5, 5));
        lowerPanelAuto = new JPanel(new GridLayout(1, 2, 5, 5));
        lowerPanelScatter = new JPanel(new GridLayout(1, 2, 5, 5));
        lowerPanelOne2One = new JPanel(new GridLayout(1, 2, 5, 5));

        autoCheck = new JRadioButton("Auto (Fully connect all incoming nodes)", autoConnect);
        scatterCheck = new JRadioButton("Scatter (Each job takes n-files as input) ", !autoConnect);
        spreadCheck = new JRadioButton("Spread (Connect incoming files to n-jobs) ", !autoConnect);
        one2oneCheck = new JRadioButton("One-2-one (Job duplicated to number of input files)", !autoConnect);

        ButtonGroup radios = new ButtonGroup();
        radios.add(autoCheck);
        radios.add(scatterCheck);
        radios.add(spreadCheck);
        radios.add(one2oneCheck);

        autoCheck.addItemListener(itemPerformer);
        scatterCheck.addItemListener(itemPerformer);
        spreadCheck.addItemListener(itemPerformer);
        one2oneCheck.addItemListener(itemPerformer);

        lowerPanel1.add(autoCheck);
        lowerPanel1.add(scatterCheck);
        lowerPanel1.add(spreadCheck);
        lowerPanel1.add(one2oneCheck);

        numberJobsLabel = new JLabel("No. jobs : " + numberOfJobs);

        numberInputFilesLabel = new JLabel("No. Files/job : " + fileInputsPerJob);

        final String[] numbers = new String[100];
        for (int i = 1; i < 100; i++) {
            numbers[i] = "" + i;
        }

        jobsCombo = new JComboBox(numbers);
        jobsCombo.setActionCommand("jobsCombo");
        jobsCombo.setSelectedItem("" + numberOfJobs);
        jobsCombo.addActionListener(actionPerformer);

        lowerPanelAuto.add(numberJobsLabel);
        lowerPanelAuto.add(jobsCombo);

        filesPerJobCombo = new JComboBox(numbers);
        filesPerJobCombo.setActionCommand("filesPerJobCombo");
        filesPerJobCombo.setSelectedItem("" + fileInputsPerJob);
        filesPerJobCombo.addActionListener(actionPerformer);

        lowerPanelScatter.add(numberInputFilesLabel);
        lowerPanelScatter.add(filesPerJobCombo);

        lowerPanel.add(lowerPanel1);
        lowerPanel.add(lowerPanelAuto);
        lowerPanel.add(lowerPanelScatter);
        mainPane.add(lowerPanel);


        setEnabling(lowerPanel, collection);
        if (collection) {
            setEnabling(lowerPanelAuto, autoConnect);
            setEnabling(lowerPanelScatter, !autoConnect);
        }

        String word = "no";
        if (task != null) {
            word = task.getQualifiedTaskName();
        }
        mainPane.add(new JLabel("Task? " + word));

        JButton apply = new JButton("Apply");
        apply.setActionCommand("apply");
        apply.addActionListener(actionPerformer);
        mainPane.add(apply);
        return mainPane;
    }


    public void apply() {
        changeToolName(nameField.getText());
        jobName = nameField.getText();
        args = argsField.getText();
        if(!collection){
            connectPattern = DaxJob.AUTO_CONNECT;
        }
        setParams();
    }

    public void setEnabling(Component c, boolean enable) {
        c.setEnabled(enable);
        if (c instanceof Container) {
            Component[] arr = ((Container) c).getComponents();
            for (Component anArr : arr) {
                setEnabling(anArr, enable);
            }
        }
    }

    private void log(String s) {
        Log log = Loggers.DEV_LOGGER;
        log.debug(s);
//        System.out.println(s);
    }

    @Override
    public void displayMessage(String string) {
        log(string);
    }

    class ActionPerformer implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
            System.out.println("An action ");
            if (ae.getActionCommand().equals("apply")) {
                apply();
            }
            if (ae.getActionCommand().equals("jobsCombo")) {
                numberOfJobs = Integer.parseInt((String) jobsCombo.getSelectedItem());
                numberJobsLabel.setText("No. files : " + numberOfJobs);
            }
            if (ae.getActionCommand().equals("filesPerJobCombo")) {
                fileInputsPerJob = Integer.parseInt((String) filesPerJobCombo.getSelectedItem());
                numberInputFilesLabel.setText("No. files : " + fileInputsPerJob);
            }
        }
    }

    class ItemPerformer implements ItemListener {
        public void itemStateChanged(ItemEvent itemEvent) {
            System.out.println("An item");
            if (itemEvent.getSource() == collectionBox) {
                if (collectionBox.isSelected()) {
                    collectLabel.setText("Collection of jobs.");
                    collection = true;
                    setEnabling(lowerPanel, true);
                } else {
                    collectLabel.setText("Not a collection");
                    collection = false;
                    setEnabling(lowerPanel, false);
                }
            }
            if (itemEvent.getSource() == autoCheck) {
                if (autoCheck.isSelected()) {
                    connectPattern = JobUnit.AUTO_CONNECT;
                    setEnabling(lowerPanelAuto, true);
                    setEnabling(lowerPanelScatter, false);
                    setEnabling(lowerPanelOne2One, false);
                }
            }

            if (itemEvent.getSource() == scatterCheck) {
                if (scatterCheck.isSelected()) {
                    connectPattern = JobUnit.SCATTER_CONNECT;
                    setEnabling(lowerPanelAuto, false);
                    setEnabling(lowerPanelScatter, true);
                    setEnabling(lowerPanelOne2One, false);
                }
            }
            if (itemEvent.getSource() == spreadCheck) {
                if (spreadCheck.isSelected()) {
                    connectPattern = JobUnit.SPREAD_CONNECT;
                    setEnabling(lowerPanelAuto, true);
                    setEnabling(lowerPanelScatter, false);
                    setEnabling(lowerPanelOne2One, false);
                }
            }
            if (itemEvent.getSource() == one2oneCheck) {
                if (one2oneCheck.isSelected()) {
                    connectPattern = JobUnit.ONE2ONE_CONNECT;
                    setEnabling(lowerPanelAuto, false);
                    setEnabling(lowerPanelScatter, false);
                    setEnabling(lowerPanelOne2One, true);
                }
            }
        }
    }
}
