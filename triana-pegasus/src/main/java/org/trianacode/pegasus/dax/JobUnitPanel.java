package org.trianacode.pegasus.dax;

import org.apache.commons.logging.Log;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.gui.panels.ParameterPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Sep 10, 2010
 * Time: 2:25:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class JobUnitPanel extends ParameterPanel {
    private JPanel upperPanel = new JPanel(new GridLayout(3,2,5,5));
    private JPanel lowerPanel = new JPanel();
    private JTextField nameField = new JTextField("");
    private JTextField argsField = new JTextField("");
    private int numberOfJobs = 1;
    private int fileInputsPerJob = 1;
    private boolean collection = false;

    JLabel collectLabel = new JLabel("");

    //  public boolean isAutoCommitByDefault(){return true;}

    @Override
    public void init() {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        upperPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Job"));
        JLabel nameLabel = new JLabel("Job Name :");
        upperPanel.add(nameLabel);

        changeToolName(getTask().getToolName());
        upperPanel.add(nameField);

        JLabel argsLabel = new JLabel("Job Args :");
        upperPanel.add(argsLabel);

        argsField.setText((String) getParameter("args"));
        upperPanel.add(argsField);

        final JCheckBox collectionBox = new JCheckBox("Collection", isCollection());
        collectionBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent ie) {
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
        });
        upperPanel.add(collectionBox);

        if(isCollection()){
            collectLabel.setText("Collection of jobs.");
        }else{
            collectLabel.setText("Not a collection");
        }
        upperPanel.add(collectLabel);

        add(upperPanel);

        lowerPanel.setLayout(new BoxLayout(lowerPanel, BoxLayout.Y_AXIS));
        lowerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Job Collection Options"));
        JPanel lowerPanel1 = new JPanel(new GridLayout(1, 2, 5, 5));
        final JPanel lowerPanel2 = new JPanel(new GridLayout(1, 2, 5, 5));
        final JPanel lowerPanel3 = new JPanel(new GridLayout(1, 2, 5, 5));

        final JCheckBox autoCheck = new JCheckBox("Auto (Fully connect all incoming nodes)", false);
        final JCheckBox manualCheck = new JCheckBox("Manual", false);

        autoCheck.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent ie) {
                if (autoCheck.isSelected()) {
                    setEnabling(lowerPanel2, true);
                    setEnabling(lowerPanel3, false);
                    manualCheck.setSelected(false);
                } else {
                    setEnabling(lowerPanel2, false);
                    setEnabling(lowerPanel3, true);
                    manualCheck.setSelected(true);
                }
            }
        });
        manualCheck.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent ie) {
                if (manualCheck.isSelected()) {
                    setEnabling(lowerPanel2, false);
                    setEnabling(lowerPanel3, true);
                    autoCheck.setSelected(false);
                } else {
                    setEnabling(lowerPanel2, true);
                    setEnabling(lowerPanel3, false);
                    autoCheck.setSelected(true);
                }
            }
        });

        lowerPanel1.add(autoCheck);
        lowerPanel1.add(manualCheck);

        final JLabel numberLabel = new JLabel("No. Jobs : " + numberOfJobs);
        final JSlider jobSlide = new JSlider(1, 999, 1);
        jobSlide.setMajorTickSpacing(100);
        jobSlide.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent changeEvent) {
                numberOfJobs = jobSlide.getValue();
                numberLabel.setText("No. jobs : " + numberOfJobs);
            }
        });
        lowerPanel2.add(numberLabel);
        lowerPanel2.add(jobSlide);

        final JLabel numberInputFilesLabel = new JLabel("No. Files/job : " + fileInputsPerJob);
        final JSlider fileSlide = new JSlider(1, 999, 1);
        fileSlide.setMajorTickSpacing(100);
        fileSlide.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent changeEvent) {
                fileInputsPerJob = fileSlide.getValue();
                numberInputFilesLabel.setText("No. Files/job : " + fileInputsPerJob);
            }
        });
        lowerPanel3.add(numberInputFilesLabel);
        lowerPanel3.add(fileSlide);

        lowerPanel.add(lowerPanel1);
        lowerPanel.add(lowerPanel2);
        lowerPanel.add(lowerPanel3);
        add(lowerPanel);

        setEnabling(lowerPanel, isCollection());
    }

    private void setParams(){
        getTask().setParameter("args", argsField.getText());
        getTask().setParameter("numberOfJobs", numberOfJobs);
        getTask().setParameter("collection", collection);

    }

    public void changeToolName(String name){
        nameField.setText(name);
        log("Changing tool " + getTask().getToolName() + " to : " + name);
        getTask().setParameter("jobName", name);
        getTask().setToolName(name);
    }

    public void apply(){
        changeToolName(nameField.getText());
        setParams();
    }

    public void applyClicked(){ apply();}
    public void okClicked(){ apply();};

    public void setEnabling(Component c,boolean enable) {
        c.setEnabled(enable);
        if (c instanceof Container)
        {
            Component [] arr = ((Container) c).getComponents();
            for (int j=0;j<arr.length;j++) { setEnabling(arr[j],enable); }
        }
    }

    private boolean isCollection(){
        if(getParameter("collection").equals("true")){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public void reset() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void dispose() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
    private void log(String s){
        Log log = Loggers.DEV_LOGGER;
        log.debug(s);
        //System.out.println(s);
    }
}
