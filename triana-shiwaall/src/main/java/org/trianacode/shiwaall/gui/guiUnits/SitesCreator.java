package org.trianacode.shiwaall.gui.guiUnits;

import org.trianacode.shiwaall.extras.FileBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

class SitesCreator extends JDialog {
    File sitesFile = null;
    private JTextField clusterNameField;
    private JTextField hostnameField;
    private JTextField gateKeeperTypeField;
    private JTextField gateKeeperPortField;
    private JTextField schedulerField;
    private JTextField workDirField;
    private JTextField pegasusHomeField;
    private JTextField globusLocationField;


    public SitesCreator() {
        this.setModal(true);
        this.setLocationRelativeTo(this.getOwner());

        JPanel mainPanel = new JPanel(new GridLayout(9, 2));

        mainPanel.add(new JLabel("Cluster Name"));
        clusterNameField = new JTextField("Name");
        mainPanel.add(clusterNameField);

        mainPanel.add(new JLabel("Cluster Hostname"));
        hostnameField = new JTextField("");
        mainPanel.add(hostnameField);

        mainPanel.add(new JLabel("Cluster GateKeeper Type"));
        gateKeeperTypeField = new JTextField("gt5");
        mainPanel.add(gateKeeperTypeField);

        mainPanel.add(new JLabel("Cluster GateKeeper Port"));
        gateKeeperPortField = new JTextField("2119");
        mainPanel.add(gateKeeperPortField);

        mainPanel.add(new JLabel("Cluster Scheduler"));
        schedulerField = new JTextField("condor");
        mainPanel.add(schedulerField);

        mainPanel.add(new JLabel("Cluster Work Dir"));
        workDirField = new JTextField("/data/scratch");
        mainPanel.add(workDirField);

        mainPanel.add(new JLabel("Cluster Pegasus Home"));
        pegasusHomeField = new JTextField("/opt/org.trianacode.shiwaall.gui/3.0");
        mainPanel.add(pegasusHomeField);

        mainPanel.add(new JLabel("Cluster Globus Location"));
        globusLocationField = new JTextField("/opt/globus/default");
        mainPanel.add(globusLocationField);

        JButton ok = new JButton("Ok");
        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                okPressed();
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        mainPanel.add(ok);
        mainPanel.add(cancelButton);
        this.add(mainPanel);

        this.setTitle("Create sites file");
        this.pack();
        this.setVisible(true);
    }

    private void okPressed() {

        String clusterName = clusterNameField.getText();
        String hostname = hostnameField.getText();
        String gateKeeperType = gateKeeperTypeField.getText();
        String gateKeeperPort = gateKeeperPortField.getText();
        String schedular = schedulerField.getText();
        String workDir = workDirField.getText();
        String pegasusDir = pegasusHomeField.getText();
        String globusLocation = globusLocationField.getText();

        String sitesContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<sitecatalog xmlns=\"http://org.trianacode.shiwaall.gui.isi.edu/schema/sitecatalog\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
                " xsi:schemaLocation=\"http://org.trianacode.shiwaall.gui.isi.edu/schema/sitecatalog http://org.trianacode.shiwaall.gui.isi.edu/schema/sc-3.0.xsd\" version=\"3.0\">\n" +

                "    <site  handle=\"" + clusterName + "\" arch=\"x86\" os=\"LINUX\">\n" +
                "        <grid  type=\"" + gateKeeperType + "\" contact=\"" + clusterName + ":" + gateKeeperPort + "/jobmanager-fork\" scheduler=\"Fork\" jobtype=\"auxillary\"/>\n" +
                "        <grid  type=\"" + gateKeeperType + "\" contact=\"" + clusterName + ":" + gateKeeperPort + "/jobmanager-" + schedular + "\" scheduler=\"unknown\" jobtype=\"compute\"/>\n" +
                "        <head-fs>\n" +
                "            <scratch>\n" +
                "                <shared>\n" +
                "                    <file-server protocol=\"gsiftp\" url=\"gsiftp://" + hostname + "\" mount-point=\"" + workDir + "\"/>\n" +
                "                    <internal-mount-point mount-point=\"" + workDir + "\"/>\n" +
                "                </shared>\n" +
                "            </scratch>\n" +
                "            <storage>\n" +
                "                <shared>\n" +
                "                    <file-server protocol=\"gsiftp\" url=\"gsiftp://" + hostname + "\" mount-point=\"" + workDir + "\"/>\n" +
                "                    <internal-mount-point mount-point=\"" + workDir + "\"/>\n" +
                "                </shared>\n" +
                "            </storage>\n" +
                "        </head-fs>\n" +
                "        <replica-catalog  type=\"LRC\" url=\"rlsn://dummyValue.url.edu\" />\n" +
                "        <profile namespace=\"env\" key=\"PEGASUS_HOME\" >" + pegasusDir + "</profile>\n" +
                "        <profile namespace=\"env\" key=\"GLOBUS_LOCATION\" >" + globusLocation + "</profile>\n" +
                "    </site>\n" +
                "</sitecatalog>";

        new FileBuilder("condorsites.xml", sitesContent);
        sitesFile = new File("condorsites.xml");
        dispose();
    }

    public static Object getFile() {
        SitesCreator sitesCreator = new SitesCreator();
        return sitesCreator.getReturnValue();
    }

    private Object getReturnValue() {
        return sitesFile;
    }
}