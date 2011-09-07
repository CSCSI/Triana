package org.trianacode.pegasus.gui.guiUnits;

import org.apache.commons.logging.Log;
import org.trianacode.annotation.CustomGUIComponent;
import org.trianacode.annotation.Tool;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.gui.main.organize.TaskGraphOrganize;
import org.trianacode.pegasus.dax.DaxCreatorV3;
import org.trianacode.pegasus.dax.DaxReader;
import org.trianacode.pegasus.dax.Displayer;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.annotation.TaskConscious;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 23/05/2011
 * Time: 14:34
 * To change this template use File | Settings | File Templates.
 */
@Tool
public class DaxCreator extends DaxCreatorV3 implements Displayer, TaskConscious {

    String locationString = "";
    private JTextField nameField;
    private JTextField locationField;
    private JCheckBox demoCheck;
    private static Log devLog = Loggers.DEV_LOGGER;


    @org.trianacode.annotation.Process(gather = true)
    public File fakeProcess(List list) {
        update();
        File daxFile = this.process(list);
        if (demo && daxFile.exists() && daxFile.canRead()) {
            displayMessage("Displaying demo of " + daxFile.getAbsolutePath());
            DaxReader dr = new DaxReader();
            try {

                //demo button - opens a new taskgraph (gui only) and adds it to the ApplicationFrame
                TaskGraph t = dr.importWorkflow(daxFile, GUIEnv.getApplicationFrame().getEngine().getProperties());
                TaskGraph tg = GUIEnv.getApplicationFrame().addParentTaskGraphPanel(t);
                TaskGraphOrganize.organizeTaskGraph(TaskGraphOrganize.DAX_ORGANIZE, tg);

                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().edit(daxFile);
                }
            } catch (Exception e) {
                displayMessage("Error opening *" + daxFile.getName() + "* demo taskgraph : " + e);
                e.printStackTrace();
            }
        } else {
            displayMessage("Not displaying demo, or file not found/accessible : " + daxFile.getAbsolutePath());
        }
        return daxFile;
    }

    private void update() {

        locationString = nameField.getText();

        if (!locationField.getText().equals("")) {
            locationString = locationField.getText() + java.io.File.separator + locationString;
        }

        devLog.debug("File location : " + locationString);
        task.setParameter("fileName", locationString);
        task.setParameter("demo", demo);

    }

    @CustomGUIComponent
    public Component getComponent() {
        final JPanel guiComponent = new JPanel();
        guiComponent.setLayout(new BoxLayout(guiComponent, BoxLayout.Y_AXIS));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(3, 1));

        JPanel namePanel = new JPanel(new BorderLayout());
        JLabel nameLabel = new JLabel("Select filename : ");
        nameField = new JTextField("output");
        namePanel.add(nameLabel, BorderLayout.WEST);
        namePanel.add(nameField, BorderLayout.CENTER);

        JPanel locationPanel = new JPanel(new BorderLayout());
        JLabel locationLabel = new JLabel("Location : ");
        locationField = new JTextField(System.getProperty("user.dir"));
        JButton locationButton = new JButton("...");
        locationButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setMultiSelectionEnabled(false);
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = chooser.showDialog(guiComponent, "Location");
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    java.io.File f = chooser.getSelectedFile();
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
        demoCheck.setSelected(demo);
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

        guiComponent.add(mainPanel);
        return guiComponent;
    }

    @Override
    public void displayMessage(String string) {
        devLog.debug(string);
    }
}
