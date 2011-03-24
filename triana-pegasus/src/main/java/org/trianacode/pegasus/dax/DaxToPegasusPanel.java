package org.trianacode.pegasus.dax;

import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.gui.panels.ParameterPanel;
import org.trianacode.pegasus.extras.FileBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Nov 30, 2010
 * Time: 2:37:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class DaxToPegasusPanel extends ParameterPanel implements ActionListener {

    HashMap<String, JTextField> locationMap = new HashMap<String, JTextField>();
    HashMap<String, JRadioButton> radioMap = new HashMap<String, JRadioButton>();

    private static final String dax = "daxLocation";
    private static final String prop = "propLocation";
    private static final String rc = "rcLocation";
    private static final String sites = "sitesLocation";
    private static final String tc = "tcLocation";

    private static final String auto = "AUTO";
    private static final String manual = "URL";
    private static final String local = "LOCAL";

    String locationService = auto;

    JTextField urlField = new JTextField("");
    JTextField daxField = new JTextField("");
    JTextField propField = new JTextField("");
    JTextField rcField = new JTextField("");
    JTextField scField = new JTextField("");
    JTextField tcField = new JTextField("");

    JRadioButton jmdnsButton = new JRadioButton(auto, false);
    JRadioButton urlButton = new JRadioButton(manual, false);
    JRadioButton runLocalButton = new JRadioButton(local, false);

    private void setParams(){
        getTask().setParameter(dax, daxField.getText());
        getTask().setParameter(prop, propField.getText());
        getTask().setParameter(rc, scField.getText());
        getTask().setParameter(sites, tcField.getText());
        getTask().setParameter(tc, rcField.getText());
        getTask().setParameter("manualURL", urlField.getText());
        getTask().setParameter("locationService", locationService);

    }

    private void getParams(){
        try{
            daxField.setText((String)getParameter(dax));
            propField.setText((String)getParameter(prop));
            scField.setText((String)getParameter(sites));
            tcField.setText((String)getParameter(tc));
            rcField.setText((String)getParameter(rc));

            Object locationObject = getParameter("locationService");
            if(locationObject instanceof String && locationObject != null){
                locationService = (String)locationObject;
                System.out.println("LocationService : " + locationService);
                if(radioMap.containsKey(locationService)){
                    System.out.println("Setting radioButton " + locationService + " to true.");
                    radioMap.get(locationService).setSelected(true);
                }
            }else{
                locationService = auto;
            }

            Object urlObject = getParameter("manualURL");
            if(urlObject instanceof String && urlObject != null && !((String)urlObject).equals("")){
                urlField.setText((String)urlObject);
            }else{
                urlField.setText("http://localhost:8080/remotecontrol");
            }
        }catch(Exception e){
            System.out.println("Error loading parameters\n ");
            e.printStackTrace();
        }


    }

    private void fillMaps(){
        locationMap.put(dax, daxField);
        locationMap.put(prop, propField);
        locationMap.put(sites, scField);
        locationMap.put(tc, tcField);
        locationMap.put(rc, rcField);

        radioMap.put(auto, jmdnsButton);
        radioMap.put(manual, urlButton);
        radioMap.put(local, runLocalButton);
    }

    private void apply(){
        setParams();
    }

    public void applyClicked(){apply();}
    public void okClicked(){apply();}

    @Override
    public void init() {
        JPanel mainPane = new JPanel();
        mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));

        JPanel searchPane = new JPanel();
        searchPane.setLayout(new BorderLayout());
        searchPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Pegasus Location"));
        JPanel searchButtonPane = new JPanel(new GridLayout(3,1));
        JPanel searchLabelPane = new JPanel(new GridLayout(3,1));


//        JRadioButton jmdnsButton = new JRadioButton("AUTO ", (locationService.equals(auto)));
        jmdnsButton.setActionCommand(auto);
        jmdnsButton.addActionListener(this);
//        JRadioButton urlButton = new JRadioButton("URL ", (locationService.equals(manual)));
        urlButton.setActionCommand(manual);
        urlButton.addActionListener(this);
//        JRadioButton runLocalButton = new JRadioButton("LOCAL ", (locationService.equals(local)));
        runLocalButton.setActionCommand(local);
        runLocalButton.addActionListener(this);

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

        JPanel selectionsPane = new JPanel(new GridLayout(5,3));
        selectionsPane.setBorder(javax.swing.BorderFactory.createTitledBorder("File locations"));

        JPanel daxFieldPane = new JPanel(new BorderLayout());
        JLabel daxLabel = new JLabel("Dax File:");
        JButton daxSelectButton = new JButton("Find");
        daxSelectButton.setActionCommand(dax);
        daxSelectButton.addActionListener(this);
        daxFieldPane.add(daxLabel, BorderLayout.WEST);
        daxFieldPane.add(daxField, BorderLayout.CENTER);
        daxFieldPane.add(daxSelectButton, BorderLayout.EAST);

        JPanel propFieldPane = new JPanel(new BorderLayout());
        JLabel propLabel = new JLabel("Properties File :");
        JButton propSelectButton = new JButton("Find");
        propSelectButton.setActionCommand(prop);
        propSelectButton.addActionListener(this);
        propFieldPane.add(propLabel, BorderLayout.WEST);
        propFieldPane.add(propField, BorderLayout.CENTER);
        propFieldPane.add(propSelectButton, BorderLayout.EAST);

        JPanel rcFieldPane = new JPanel(new BorderLayout());
        JLabel rcLabel = new JLabel("Replica Catalog :");
        JButton rcSelectButton = new JButton("Find");
        rcSelectButton.setActionCommand(rc);
        rcSelectButton.addActionListener(this);
        rcFieldPane.add(rcLabel, BorderLayout.WEST);
        rcFieldPane.add(rcField, BorderLayout.CENTER);
        rcFieldPane.add(rcSelectButton, BorderLayout.EAST);

        JPanel scFieldPane = new JPanel(new BorderLayout());
        JLabel scLabel = new JLabel("Site Catalog :");
        JButton scSelectButton = new JButton("Find");
        scSelectButton.setActionCommand(sites);
        scSelectButton.addActionListener(this);
        scFieldPane.add(scLabel, BorderLayout.WEST);
        scFieldPane.add(scField, BorderLayout.CENTER);

        JPanel scButtonPanel = new JPanel(new GridLayout(0, 2));
        JButton createSiteButton = new JButton("Create");
        createSiteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                File sitesFile = (File)SitesCreator.getFile();
                if(sitesFile != null && sitesFile.exists()){
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
        tcSelectButton.addActionListener(this);
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

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(mainPane);
        fillMaps();
        getParams();
    }

    @Override
    public void reset() {
    }

    @Override
    public void dispose() {
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if(locationMap.containsKey(ae.getActionCommand())){
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
            if(filePath != null){
                (locationMap.get(ae.getActionCommand())).setText(filePath);
            }
        }
        if(radioMap.containsKey(ae.getActionCommand())){
            locationService = ae.getActionCommand();
            System.out.println("LocationService : " + locationService);

        }
    }

}

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


    public SitesCreator(){
        this.setModal(true);
        this.setLocationRelativeTo(this.getOwner());

        JPanel mainPanel = new JPanel(new GridLayout(9,2));

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
        pegasusHomeField = new JTextField("/opt/pegasus/3.0");
        mainPanel.add(pegasusHomeField);

        mainPanel.add(new JLabel("Cluster Globus Location"));
        globusLocationField = new JTextField("/opt/globus/default");
        mainPanel.add(globusLocationField);

        JButton ok = new JButton("Ok");
        ok.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                okPressed();
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
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
                "<sitecatalog xmlns=\"http://pegasus.isi.edu/schema/sitecatalog\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
                " xsi:schemaLocation=\"http://pegasus.isi.edu/schema/sitecatalog http://pegasus.isi.edu/schema/sc-3.0.xsd\" version=\"3.0\">\n" +

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
                "        <profile namespace=\"env\" key=\"GLOBUS_LOCATION\" >" + globusLocation +"</profile>\n" +
                "    </site>\n" +
                "</sitecatalog>";

        new FileBuilder("condorsites.xml", sitesContent);
        sitesFile = new File("condorsites.xml");
        dispose();
    }

    public static Object getFile(){
        SitesCreator sitesCreator = new SitesCreator();
        return sitesCreator.getReturnValue();
    }

    private Object getReturnValue(){
        return sitesFile;
    }
}
