package org.trianacode.shiwaall.iwir.exporter;

import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.gui.panels.ParameterPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

// TODO: Auto-generated Javadoc
/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 14/04/2011
 * Time: 12:05
 * To change this template use File | Settings | File Templates.
 */
public class IwirCreatorPanel extends ParameterPanel {

    /** The location string. */
    String locationString = "";
    
    /** The name field. */
    private JTextField nameField;
    
    /** The location field. */
    private JTextField locationField;
    
    /** The demo check. */
    private JCheckBox demoCheck;
    
    /** The demo. */
    private boolean demo = false;

    /* (non-Javadoc)
     * @see org.trianacode.gui.panels.ParameterPanel#init()
     */
    @Override
    public void init() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(3, 1));

        JPanel namePanel = new JPanel(new BorderLayout());
        JLabel nameLabel = new JLabel("Select filename : ");
        nameField = new JTextField("output");
        namePanel.add(nameLabel, BorderLayout.WEST);
        namePanel.add(nameField, BorderLayout.CENTER);

        JPanel locationPanel = new JPanel(new BorderLayout());
        JLabel locationLabel = new JLabel("Location : ");
        locationField = new JTextField();
        JButton locationButton = new JButton("...");
        locationButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setMultiSelectionEnabled(false);
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = chooser.showDialog(GUIEnv.getApplicationFrame(), "Location");
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File f = chooser.getSelectedFile();
                    if (f != null) {
                        String location = f.getAbsolutePath();
                        locationField.setText(location);
                    }
                }
            }

        });
        locationPanel.add(locationLabel, BorderLayout.WEST);
        locationPanel.add(locationField, BorderLayout.CENTER);
        locationPanel.add(locationButton, BorderLayout.EAST);


        mainPanel.add(namePanel);
        mainPanel.add(locationPanel);

        JPanel demoPanel = new JPanel();
        JLabel demoLabel = new JLabel("Demo? : ");
        demoCheck = new JCheckBox();
        demoCheck.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent ie) {
                if (demoCheck.isSelected()) {
                    demo = true;
                } else {
                    demo = false;
                }
            }
        });
        demoPanel.add(demoLabel);
        demoPanel.add(demoCheck);
        mainPanel.add(demoPanel);

        add(mainPanel);
    }

    /**
     * Update.
     */
    private void update() {

        locationString = nameField.getText();

        if (!locationField.getText().equals("")) {
            locationString = locationField.getText() + File.separator + locationString;
        }

        System.out.println("File location : " + locationString);
        this.getTask().setParameter("fileName", locationString);
        this.getTask().setParameter("demo", demo);

    }

    /* (non-Javadoc)
     * @see org.trianacode.gui.panels.ParameterPanel#okClicked()
     */
    public void okClicked() {
        update();
    }

    /* (non-Javadoc)
     * @see org.trianacode.gui.panels.ParameterPanel#applyClicked()
     */
    public void applyClicked() {
        update();
    }

    /* (non-Javadoc)
     * @see org.trianacode.gui.panels.ParameterPanel#reset()
     */
    @Override
    public void reset() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /* (non-Javadoc)
     * @see org.trianacode.gui.panels.ParameterPanel#dispose()
     */
    @Override
    public void dispose() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}

