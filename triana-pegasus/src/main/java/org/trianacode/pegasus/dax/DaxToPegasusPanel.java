package org.trianacode.pegasus.dax;

import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.gui.panels.ParameterPanel;

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
        scFieldPane.add(scSelectButton, BorderLayout.EAST);

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
