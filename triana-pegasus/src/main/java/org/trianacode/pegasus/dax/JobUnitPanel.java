package org.trianacode.pegasus.dax;

import org.trianacode.gui.panels.ParameterPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private JPanel upperPanel = new JPanel(new GridLayout(2,2,5,5));
    private JPanel lowerPanel = new JPanel(new GridLayout(2,2,5,5));
    private JTextField nameField = new JTextField("");
    JLabel collect = new JLabel("Not a collection");


    @Override
    public void init() {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        upperPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("File"));
        JLabel nameLabel = new JLabel("File Name :");
        upperPanel.add(nameLabel);

        nameField.setText((String) getParameter("fileName"));
        nameField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                //             setParameter("fileName", nameField.getText());
                apply();
            }
        });
        upperPanel.add(nameField);

        final JCheckBox collection = new JCheckBox("Collection", isCollection());
        collection.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent ie) {
                if (collection.isSelected()) {
                    collect.setText("Collection of jobs.");
                    setParameter("collection", true);
                    setEnabling(lowerPanel, true);
                } else {
                    collect.setText("Not a collection");
                    setParameter("collection", false);
                    setEnabling(lowerPanel, false);
                }
            }
        });
        upperPanel.add(collection);
        upperPanel.add(collect);

        add(upperPanel);

        add(lowerPanel);

        //To change body of implemented methods use File | Settings | File Templates.
    }

    private void apply(){
        setParameter("fileName", nameField.getText());
        getTask().setToolName(nameField.getText());
    }

    public void setEnabling(Component c,boolean enable) {
        c.setEnabled(enable);
        if (c instanceof Container)
        {
            Component [] arr = ((Container) c).getComponents();
            for (int j=0;j<arr.length;j++) { setEnabling(arr[j],enable); }
        }
    }
    
    private boolean isCollection(){
        return (Boolean)getParameter("collection");
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
