package org.trianacode.shiwaall.gui.guiUnits;

import org.apache.commons.logging.Log;
import org.shiwa.desktop.data.transfer.WorkflowEngineHandler;
import org.shiwa.desktop.gui.SHIWADesktop;
import org.shiwa.pegasus.PegasusHandler;
import org.trianacode.annotation.CustomGUIComponent;
import org.trianacode.annotation.Tool;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.gui.main.organize.TaskGraphOrganize;
import org.trianacode.gui.panels.DisplayDialog;
import org.trianacode.shiwaall.dax.DaxCreatorV3;
import org.trianacode.shiwaall.dax.DaxReader;
import org.trianacode.shiwaall.dax.Displayer;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.annotation.TaskConscious;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.*;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 23/05/2011
 * Time: 14:34
 * To change this template use File | Settings | File Templates.
 */
@Tool
public class DaxCreator extends DaxCreatorV3 implements Displayer, TaskConscious {

    String propertiesText = "";

    String locationString = "";
    private JTextField nameField;
    private JTextField nameSpaceField;
    private JTextField locationField;
    private JCheckBox demoCheck;
    private JCheckBox publishCheck;


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
        if(publish){
            File siteFile = writeFile("site.xml", siteText);
            File propertiesFile = writeFile("propertiesrc", propertiesText);

            System.out.println("Current dir " + System.getProperty("user.dir"));
            File currentDir = new File(System.getProperty("user.dir"));
            File schemaDir = new File(currentDir, "schema");
            schemaDir.mkdirs();
            String property = System.setProperty(
                    "pegasus.home.schemadir",
                    schemaDir.getAbsolutePath()
            );

            System.out.println("sax " + System.getProperty("org.xml.sax.driver"));
            System.out.println(property);

            try {
                prepareSchemas(schemaDir);
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }

            PegasusHandler pegasusHandler = new PegasusHandler(daxFile, propertiesFile);

            System.out.println(pegasusHandler.getClass().getCanonicalName());
            if(pegasusHandler instanceof WorkflowEngineHandler){
                System.out.print("Is handler");
            }

            SHIWADesktop shiwaDesktop = new SHIWADesktop( pegasusHandler,
                    SHIWADesktop.ButtonOption.SHOW_TOOLBAR);

            DisplayDialog dialog = new DisplayDialog(
                    shiwaDesktop.getPanel(), "SHIWA Desktop");
        }

        return daxFile;
    }

    private void prepareSchemas(File outputDir) throws IOException, URISyntaxException {
        InputStream zipStream = this.getClass().getClassLoader().getResourceAsStream("schemas/schema.zip");
//        URL zipUrl = Thread.currentThread().getContextClassLoader().getResource("schemas/schema.zip");

        if(zipStream != null){

            File file = File.createTempFile("schemazips", ".zip");
            file.deleteOnExit();
            copyStreams(zipStream, new FileOutputStream(file));

            ZipFile zipFile = new ZipFile(file);

            for(Enumeration e = zipFile.entries(); e.hasMoreElements();){
                ZipEntry zipEntry = (ZipEntry) e.nextElement();
                File outputFile = new File(outputDir, zipEntry.getName());
                copyStreams(zipFile.getInputStream(zipEntry), new FileOutputStream(outputFile));
            }
        } else {
            System.out.println("Didnt find url");
        }
    }

    private void copyStreams(InputStream inStream, FileOutputStream outStream) throws IOException {
        byte[] buf = new byte[1024];
        int l;
        while ((l = inStream.read(buf)) >= 0) {
            outStream.write(buf, 0, l);
        }
        inStream.close();
        outStream.close();
    }


    private void update() {

        locationString = nameField.getText();

        if (!locationField.getText().equals("")) {
            locationString = locationField.getText() + java.io.File.separator + locationString;
        }

        devLog.debug("File location : " + locationString);
        task.setParameter("fileName", locationString);
        task.setParameter("demo", demo);
        task.setParameter("namespace", nameSpaceField.getText());

    }

    public File writeFile(String name, String contents) {
        File file = new File(name);
        BufferedWriter out = null;
        try {
            FileWriter fstream = new FileWriter(file);
            out = new BufferedWriter(fstream);
            // Create file
            out.write(contents);
            //Close the output stream
            out.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }
        return file;
    }

    @CustomGUIComponent
    public Component getComponent() {
        final JPanel guiComponent = new JPanel();
        guiComponent.setLayout(new BoxLayout(guiComponent, BoxLayout.Y_AXIS));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(7, 1));

        JPanel nameSpacePanel = new JPanel(new BorderLayout());
        JLabel namespaceLabel = new JLabel("Namespace : ");
        nameSpaceField = new JTextField("namespace");
        nameSpacePanel.add(namespaceLabel, BorderLayout.WEST);
        nameSpacePanel.add(nameSpaceField, BorderLayout.CENTER);
        mainPanel.add(nameSpacePanel);

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

        JPanel catalogPanel = new JPanel();
        catalogPanel.setLayout(new GridLayout(1,2));

        JButton propertiesButton = new JButton("Properties");
        propertiesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new PropertiesFrame(locationField.getText());
            }
        });
        catalogPanel.add(propertiesButton);

        JButton siteButton = new JButton("Site");
        siteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new SiteFrame(locationField.getText());
            }
        });
        catalogPanel.add(siteButton);
        mainPanel.add(catalogPanel);

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

        JPanel publishPanel = new JPanel();
        JLabel publishLabel = new JLabel("Publish? : ");
        publishCheck = new JCheckBox();
        publishCheck.setSelected(publish);
        publishCheck.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent ie) {
                if (publishCheck.isSelected()) {
                    publish = true;
                } else {
                    publish = false;
                }
            }
        });
        publishPanel.add(publishLabel);
        publishPanel.add(publishCheck);
        mainPanel.add(publishPanel);

        guiComponent.add(mainPanel);
        return guiComponent;
    }


    @Override
    public void displayMessage(String string) {
        devLog.debug(string);
    }
    class PropertiesFrame extends JDialog {
        public PropertiesFrame(String topDir) {
            this.setModal(true);
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            if(propertiesText.equals("")){
                propertiesText = "pegasus.catalog.site=XML3\n" +
                        "pegasus.catalog.site.file=" + topDir + File.separator + "sites.xml\n" +
                        "\n" +
                        "pegasus.catalog.transformation.file=tc\n" +
                        "pegasus.catalog.transformation=Text\n" +
                        "\n" +
                        "pegasus.dir.useTimestamp=true\n" +
                        "pegasus.data.configuration=condorio\n" +
                        "pegasus.condor.logs.symlink=false\n" +
                        "pegasus.dir.storage.deep=false";
            }

            final JTextArea propertiesArea = new JTextArea(propertiesText);
            JScrollPane scrollPane = new JScrollPane(propertiesArea);
            panel.add(scrollPane);

            panel.add(scrollPane);
            JButton ok = new JButton("Ok");
            ok.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    propertiesText = (propertiesArea.getText());
                    dispose();
                }
            });
            panel.add(ok);
            this.add(panel);
            this.setSize(600, 400);
            this.setVisible(true);
            this.setTitle("propertiesrc");
        }
    }

    String siteText = "";
    class SiteFrame extends JDialog {
        public SiteFrame(String topDir) {
            this.setModal(true);
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            if(siteText.equals("")){
                siteText = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<sitecatalog xmlns=\"http://pegasus.isi.edu/schema/sitecatalog\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://pegasus.isi.edu/schema/sitecatalog http://pegasus.isi.edu/schema/sc-3.0.xsd\" version=\"3.0\">\n" +
                        "    <site handle=\"local\" arch=\"x86\" os=\"LINUX\">\n" +
                        "        <grid  type=\"gt2\" contact=\"localhost/jobmanager-fork\" scheduler=\"Fork\" jobtype=\"auxillary\"/>\n" +
                        "        <head-fs>\n" +
                        "            <scratch>\n" +
                        "                <shared>\n" +
                        "                    <file-server protocol=\"file\" url=\"file://\" mount-point=\"/home/ubuntu/DART/work/2012-08-01_113119/scratch\"/>\n" +
                        "                    <internal-mount-point mount-point=\"/home/ubuntu/DART/work/2012-08-01_113119/scratch\"/>\n" +
                        "                </shared>\n" +
                        "            </scratch>\n" +
                        "            <storage>\n" +
                        "                <shared>\n" +
                        "                    <file-server protocol=\"file\" url=\"file://\" mount-point=\"/home/ubuntu/DART/work/2012-08-01_113119/outputs\"/>\n" +
                        "                    <internal-mount-point mount-point=\"/home/ubuntu/DART/work/2012-08-01_113119/outputs\"/>\n" +
                        "                </shared>\n" +
                        "            </storage>\n" +
                        "        </head-fs>\n" +
                        "        <replica-catalog  type=\"LRC\" url=\"rlsn://dummyValue.url.edu\" />\n" +
                        "        <profile namespace=\"env\" key=\"PEGASUS_HOME\" >/usr/bin/..</profile>\n" +
                        "        <profile namespace=\"env\" key=\"GLOBUS_TCP_PORT_RANGE\" >40000,50000</profile>\n" +
                        "    </site>\n" +
                        "\n" +
                        "    <site handle=\"local-condor\" arch=\"x86\" os=\"LINUX\">\n" +
                        "        <grid  type=\"gt2\" contact=\"localhost/jobmanager-fork\" scheduler=\"Fork\" jobtype=\"auxillary\"/>\n" +
                        "        <head-fs>\n" +
                        "            <scratch>\n" +
                        "                <shared>\n" +
                        "                    <file-server protocol=\"file\" url=\"file://\" mount-point=\"/home/ubuntu/DART/work/2012-08-01_113119/local-condor/scratch\"/>\n" +
                        "                    <internal-mount-point mount-point=\"/home/ubuntu/DART/work/2012-08-01_113119/local-condor/scratch\"/>\n" +
                        "                </shared>\n" +
                        "            </scratch>\n" +
                        "            <storage>\n" +
                        "                <shared>\n" +
                        "                    <file-server protocol=\"file\" url=\"file://\" mount-point=\"/home/ubuntu/DART/work/2012-08-01_113119/local-condor/outputs\"/>\n" +
                        "                    <internal-mount-point mount-point=\"/home/ubuntu/DART/work/2012-08-01_113119/local-condor/outputs\"/>\n" +
                        "                </shared>\n" +
                        "            </storage>\n" +
                        "        </head-fs>\n" +
                        "        <replica-catalog  type=\"LRC\" url=\"rlsn://dummyValue.url.edu\" />\n" +
                        "        \n" +
                        "        <profile namespace=\"env\" key=\"PEGASUS_HOME\" >/usr/bin/</profile>\n" +
                        "        <profile namespace=\"env\" key=\"GLOBUS_TCP_PORT_RANGE\" >40000,50000</profile>\n" +
                        "        <profile namespace=\"pegasus\" key=\"style\">condor</profile>\n" +
                        "        <profile namespace=\"condor\" key=\"universe\">vanilla</profile>\n" +
                        "    </site>\n" +
                        "</sitecatalog>";
            }

            final JTextArea siteArea = new JTextArea(siteText);
            JScrollPane scrollPane = new JScrollPane(siteArea);
            panel.add(scrollPane);

            panel.add(scrollPane);
            JButton ok = new JButton("Ok");
            ok.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    siteText = (siteArea.getText());
                    dispose();
                }
            });
            panel.add(ok);
            this.add(panel);
            this.setSize(600, 400);
            this.setVisible(true);
            this.setTitle("site.xml");
        }
    }


}