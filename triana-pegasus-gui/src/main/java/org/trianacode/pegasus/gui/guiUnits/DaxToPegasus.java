package org.trianacode.pegasus.gui.guiUnits;

import org.trianacode.annotation.CustomGUIComponent;
import org.trianacode.annotation.Process;
import org.trianacode.annotation.Tool;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.pegasus.dax.DaxToPegasusUnit;
import org.trianacode.pegasus.dax.Displayer;
import org.trianacode.pegasus.extras.ProgressPopup;
import org.trianacode.taskgraph.annotation.TaskConscious;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 19/05/2011
 * Time: 21:49
 * To change this template use File | Settings | File Templates.
 */
@Tool
public class DaxToPegasus extends DaxToPegasusUnit implements TaskConscious, Displayer {

    JTextField urlField = new JTextField("");
    JTextField daxField = new JTextField("");
    JTextField propField = new JTextField("");
    JTextField rcField = new JTextField("");
    JTextField scField = new JTextField("");
    JTextField tcField = new JTextField("");

    JRadioButton jmdnsButton = new JRadioButton(auto, false);
    JRadioButton urlButton = new JRadioButton(manual, false);
    JRadioButton runLocalButton = new JRadioButton(local, false);
    private ProgressPopup popup;

    @Process
    public void fakeProcess(File file) {
        popup = new ProgressPopup("Finding Pegasus", 30);
        setParams();
        process(file);
        popup.finish();
        popup = null;
    }


    private void setParams() {
        task.setParameter(dax, daxField.getText());
        task.setParameter(prop, propField.getText());
        task.setParameter(rc, rcField.getText());
        task.setParameter(sites, scField.getText());
        task.setParameter(tc, tcField.getText());
        task.setParameter("manualURL", urlField.getText());
        task.setParameter("locationService", locationService);

    }

    private void getParams() {
        if (task != null) {
            try {
                daxField.setText((String) task.getParameter(dax));
                propField.setText((String) task.getParameter(prop));
                scField.setText((String) task.getParameter(sites));
                tcField.setText((String) task.getParameter(tc));
                rcField.setText((String) task.getParameter(rc));

                Object locationObject = task.getParameter("locationService");
                if (locationObject instanceof String && locationObject != null) {
                    locationService = (String) locationObject;
                    System.out.println("LocationService : " + locationService);
                    if (radioMap.containsKey(locationService)) {
                        System.out.println("Setting radioButton " + locationService + " to true.");
                        radioMap.get(locationService).setSelected(true);
                    }
                } else {
                    locationService = auto;
                }

                Object urlObject = task.getParameter("manualURL");
                if (urlObject instanceof String && urlObject != null && !((String) urlObject).equals("")) {
                    urlField.setText((String) urlObject);
                } else {
                    urlField.setText("http://localhost:8080/remotecontrol");
                }
            } catch (Exception e) {
                System.out.println("Error loading parameters\n ");
                e.printStackTrace();
            }
        }
    }

    private void fillMaps() {
        locationMap.put(dax, daxField);
        locationMap.put(prop, propField);
        locationMap.put(sites, scField);
        locationMap.put(tc, tcField);
        locationMap.put(rc, rcField);

        radioMap.put(auto, jmdnsButton);
        radioMap.put(manual, urlButton);
        radioMap.put(local, runLocalButton);
    }

    public void displayMessage(String string) {
        if (popup == null) {
            popup = new ProgressPopup("Finding Pegasus", 30);
        }
        popup.addText(string);
    }

    @CustomGUIComponent
    public Component getComponent() {
        ActionPerformer actionPerformer = new ActionPerformer();
        JPanel mainPane = new JPanel();
        mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));

        JPanel searchPane = new JPanel();
        searchPane.setLayout(new BorderLayout());
        searchPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Pegasus Location"));
        JPanel searchButtonPane = new JPanel(new GridLayout(3, 1));
        JPanel searchLabelPane = new JPanel(new GridLayout(3, 1));


//        JRadioButton jmdnsButton = new JRadioButton("AUTO ", (locationService.equals(auto)));
        jmdnsButton.setActionCommand(auto);
        jmdnsButton.addActionListener(actionPerformer);
//        JRadioButton urlButton = new JRadioButton("URL ", (locationService.equals(manual)));
        urlButton.setActionCommand(manual);
        urlButton.addActionListener(actionPerformer);
//        JRadioButton runLocalButton = new JRadioButton("LOCAL ", (locationService.equals(local)));
        runLocalButton.setActionCommand(local);
        runLocalButton.addActionListener(actionPerformer);

        ButtonGroup bgroup = new ButtonGroup();
        bgroup.add(jmdnsButton);
        bgroup.add(urlButton);
        bgroup.add(runLocalButton);

        JLabel jmdnsLabel = new JLabel("Find Pegasus on local network automatically.");
        JLabel runLocalLabel = new JLabel("Use a local Pegasus installation.");

        searchButtonPane.add(urlButton);
        searchLabelPane.add(urlField);
        searchButtonPane.add(jmdnsButton);
        searchLabelPane.add(jmdnsLabel);
        searchButtonPane.add(runLocalButton);
        searchLabelPane.add(runLocalLabel);
        searchPane.add(searchButtonPane, BorderLayout.WEST);
        searchPane.add(searchLabelPane, BorderLayout.CENTER);

        JPanel selectionsPane = new JPanel(new GridLayout(5, 3));
        selectionsPane.setBorder(javax.swing.BorderFactory.createTitledBorder("File locations"));

        JPanel daxFieldPane = new JPanel(new BorderLayout());
        JLabel daxLabel = new JLabel("Dax File:");
        JButton daxSelectButton = new JButton("Find");
        daxSelectButton.setActionCommand(dax);
        daxSelectButton.addActionListener(actionPerformer);
        daxFieldPane.add(daxLabel, BorderLayout.WEST);
        daxFieldPane.add(daxField, BorderLayout.CENTER);
        daxFieldPane.add(daxSelectButton, BorderLayout.EAST);

        JPanel propFieldPane = new JPanel(new BorderLayout());
        JLabel propLabel = new JLabel("Properties File :");
        JButton propSelectButton = new JButton("Find");
        propSelectButton.setActionCommand(prop);
        propSelectButton.addActionListener(actionPerformer);
        propFieldPane.add(propLabel, BorderLayout.WEST);
        propFieldPane.add(propField, BorderLayout.CENTER);
        propFieldPane.add(propSelectButton, BorderLayout.EAST);

        JPanel rcFieldPane = new JPanel(new BorderLayout());
        JLabel rcLabel = new JLabel("Replica Catalog :");
        JButton rcSelectButton = new JButton("Find");
        rcSelectButton.setActionCommand(rc);
        rcSelectButton.addActionListener(actionPerformer);
        rcFieldPane.add(rcLabel, BorderLayout.WEST);
        rcFieldPane.add(rcField, BorderLayout.CENTER);
        rcFieldPane.add(rcSelectButton, BorderLayout.EAST);

        JPanel scFieldPane = new JPanel(new BorderLayout());
        JLabel scLabel = new JLabel("Site Catalog :");
        JButton scSelectButton = new JButton("Find");
        scSelectButton.setActionCommand(sites);
        scSelectButton.addActionListener(actionPerformer);
        scFieldPane.add(scLabel, BorderLayout.WEST);
        scFieldPane.add(scField, BorderLayout.CENTER);

        JPanel scButtonPanel = new JPanel(new GridLayout(0, 2));
        JButton createSiteButton = new JButton("Create");
        createSiteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                File sitesFile = (File) SitesCreator.getFile();
                if (sitesFile != null && sitesFile.exists()) {
                    locationMap.get(sites).setText(sitesFile.getAbsolutePath());
                }
            }
        });
        scButtonPanel.add(scSelectButton);
        scButtonPanel.add(createSiteButton);
        scFieldPane.add(scButtonPanel, BorderLayout.EAST);

        JPanel tcFieldPane = new JPanel(new BorderLayout());
        JLabel tcLabel = new JLabel("Transformation Catalog :");
        JButton tcSelectButton = new JButton("Find");
        tcSelectButton.setActionCommand(tc);
        tcSelectButton.addActionListener(actionPerformer);
        tcFieldPane.add(tcLabel, BorderLayout.WEST);
        tcFieldPane.add(tcField, BorderLayout.CENTER);
        tcFieldPane.add(tcSelectButton, BorderLayout.EAST);

        selectionsPane.add(daxFieldPane);
        selectionsPane.add(propFieldPane);
        selectionsPane.add(rcFieldPane);
        selectionsPane.add(scFieldPane);
        selectionsPane.add(tcFieldPane);

        mainPane.add(searchPane);
        mainPane.add(selectionsPane);

        fillMaps();

        getParams();
        return mainPane;
    }

    class ActionPerformer implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
            if (locationMap.containsKey(ae.getActionCommand())) {
                JFileChooser chooser = new JFileChooser();
                chooser.setMultiSelectionEnabled(false);
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int returnVal = chooser.showDialog(GUIEnv.getApplicationFrame(), "File");
                String filePath = null;
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File f = chooser.getSelectedFile();
                    if (f != null) {
                        filePath = f.getAbsolutePath();
                    }
                }
                if (filePath != null) {
                    (locationMap.get(ae.getActionCommand())).setText(filePath);
                }
            }
            if (radioMap.containsKey(ae.getActionCommand())) {
                locationService = ae.getActionCommand();
                System.out.println("LocationService : " + locationService);

            }
        }
    }
}
