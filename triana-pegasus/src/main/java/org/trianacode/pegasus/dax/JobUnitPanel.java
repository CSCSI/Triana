package org.trianacode.pegasus.dax;

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
    private JPanel lowerPanel = new JPanel(new GridLayout(2,2,5,5));
    private JTextField nameField = new JTextField("");
    private JTextField argsField = new JTextField("");
    private int numberOfJobs;

    JLabel collect = new JLabel("Not a collection");

    public boolean isAutoCommitByDefault(){return true;}

    @Override
    public void init() {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        upperPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Job"));
        JLabel nameLabel = new JLabel("Job Name :");
        upperPanel.add(nameLabel);

        nameField.setText((String) getParameter("jobName"));
        upperPanel.add(nameField);

        JLabel argsLabel = new JLabel("Job Args :");
        upperPanel.add(argsLabel);

        argsField.setText((String) getParameter("args"));
        upperPanel.add(argsField);

        final JCheckBox collection = new JCheckBox("Collection", isCollection());
        collection.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent ie) {
                if (collection.isSelected()) {
                    collect.setText("Collection of jobs.");
                    setParameter("collection", "true");
                    setEnabling(lowerPanel, true);
                } else {
                    collect.setText("Not a collection");
                    setParameter("collection", "false");
                    setEnabling(lowerPanel, false);
                }
            }
        });
        upperPanel.add(collection);
        upperPanel.add(collect);

        add(upperPanel);

        lowerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Job Collection Options"));
        //     JPanel lowerPanel1 = new JPanel(new GridLayout(3, 2, 5, 5));
        final JLabel numberLabel = new JLabel("No. Jobs : 1");

        final JSlider slide = new JSlider(1, 999, 1);
        slide.setMajorTickSpacing(100);
        slide.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent changeEvent) {
                numberOfJobs = slide.getValue();
                numberLabel.setText("No. jobs : " + numberOfJobs);
            }
        });
        lowerPanel.add(numberLabel);
        lowerPanel.add(slide);
        add(lowerPanel);

        //To change body of implemented methods use File | Settings | File Templates.
    }

    private void setParams(){
        setParameter("args", argsField.getText());
        setParameter("numberOfJobs", numberOfJobs);
    }

    public void changeToolName(String name){
        System.out.println("Changing tool " + getTask().getToolName() + " to : " + name);
        setParameter("jobName", name);
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
}
