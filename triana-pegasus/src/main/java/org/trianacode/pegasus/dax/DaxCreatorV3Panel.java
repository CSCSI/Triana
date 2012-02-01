//package org.trianacode.pegasus.dax;
//
//import org.trianacode.gui.hci.GUIEnv;
//import org.trianacode.gui.panels.ParameterPanel;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.event.ItemEvent;
//import java.awt.event.ItemListener;
//import java.io.File;
//
///**
//* Created by IntelliJ IDEA.
//* User: Ian Harvey
//* Date: Jan 17, 2011
//* Time: 9:28:17 PM
//* To change this template use File | Settings | File Templates.
//*/
//public class DaxCreatorV3Panel extends ParameterPanel {
//
//    String locationString = "";
//    private JTextField nameField;
//    private JTextField locationField;
//    private JCheckBox demoCheck;
//    private boolean demo = false;
//
//    @Override
//    public void init() {
//        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
//
//        JPanel mainPanel = new JPanel();
//        mainPanel.setLayout(new GridLayout(3,1));
//
//        JPanel namePanel = new JPanel(new BorderLayout());
//        JLabel nameLabel = new JLabel("Select filename : ");
//        nameField = new JTextField("output");
//        namePanel.add(nameLabel, BorderLayout.WEST);
//        namePanel.add(nameField, BorderLayout.CENTER);
//
//        JPanel locationPanel = new JPanel(new BorderLayout());
//        JLabel locationLabel = new JLabel("Location : ");
//        locationField = new JTextField();
//        JButton locationButton = new JButton("...");
//        locationButton.addActionListener(new ActionListener(){
//            public void actionPerformed(ActionEvent e){
//                JFileChooser chooser = new JFileChooser();
//                chooser.setMultiSelectionEnabled(false);
//                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//                int returnVal = chooser.showDialog(GUIEnv.getApplicationFrame(), "Location");
//                if (returnVal == JFileChooser.APPROVE_OPTION) {
//                    File f = chooser.getSelectedFile();
//                    if (f != null) {
//                        String location = f.getAbsolutePath();
//                        locationField.setText(location);
//                    }
//                }
//            }
//
//        });
//        locationPanel.add(locationLabel, BorderLayout.WEST);
//        locationPanel.add(locationField, BorderLayout.CENTER);
//        locationPanel.add(locationButton, BorderLayout.EAST);
//
//
//        mainPanel.add(namePanel);
//        mainPanel.add(locationPanel);
//
//        JPanel demoPanel = new JPanel();
//        JLabel demoLabel = new JLabel("Demo? : ");
//        demoCheck = new JCheckBox();
//        demoCheck.addItemListener(new ItemListener() {
//            public void itemStateChanged(ItemEvent ie) {
//                if (demoCheck.isSelected()) {
//                    demo = true;
//                } else {
//                    demo = false;
//                }
//            }
//        });
//        demoPanel.add(demoLabel);
//        demoPanel.add(demoCheck);
//        mainPanel.add(demoPanel);
//
//        add(mainPanel);
//    }
//
//    private void update(){
//
//        locationString = nameField.getText();
//
//        if (!locationField.getText().equals("")){
//            locationString = locationField.getText() + File.separator + locationString;
//        }
//
//        this.getTask().setParameter("fileName", locationString);
//        this.getTask().setParameter("demo", demo);
//
//    }
//
//    public void okClicked(){
//        update();
//    }
//    public void applyClicked(){
//        update();
//    }
//
//    @Override
//    public void reset() {
//        //To change body of implemented methods use File | Settings | File Templates.
//    }
//
//    @Override
//    public void dispose() {
//        //To change body of implemented methods use File | Settings | File Templates.
//    }
//}
//
