package org.trianacode.shiwaall.dax;

import org.apache.commons.logging.Log;
import org.thinginitself.http.Response;
import org.trianacode.annotation.CustomGUIComponent;
import org.trianacode.annotation.Parameter;
import org.trianacode.annotation.Process;
import org.trianacode.annotation.Tool;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.shiwaall.extras.BareBonesBrowserLaunch;
import org.trianacode.shiwaall.sendToPegasus.FindPegasus;
import org.trianacode.shiwaall.sendToPegasus.MakeWorkflowZip;
import org.trianacode.shiwaall.sendToPegasus.SendPegasusZip;
import org.trianacode.taskgraph.ParameterNode;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.annotation.TaskConscious;

import javax.jmdns.ServiceInfo;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


// TODO: Auto-generated Javadoc
/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: Nov 30, 2010
 * Time: 2:34:25 PM
 * To change this template use File | Settings | File Templates.
 */

@Tool
public class DaxToPegasusUnit implements TaskConscious, Displayer {

    /** The zip file. */
    File zipFile = null;
    
    /** The task. */
    public Task task;


    /** The location map. */
    public HashMap<String, JTextField> locationMap = new HashMap<String, JTextField>();
    
    /** The radio map. */
    public HashMap<String, JRadioButton> radioMap = new HashMap<String, JRadioButton>();

    /** The Constant dax. */
    public static final String dax = "daxLocation";
    
    /** The Constant prop. */
    public static final String prop = "propLocation";
    
    /** The Constant rc. */
    public static final String rc = "rcLocation";
    
    /** The Constant sites. */
    public static final String sites = "sitesLocation";
    
    /** The Constant tc. */
    public static final String tc = "tcLocation";

    /** The Constant auto. */
    public static final String auto = "AUTO";
    
    /** The Constant manual. */
    public static final String manual = "URL";
    
    /** The Constant local. */
    public static final String local = "LOCAL";

    /** The dev log. */
    Log devLog = Loggers.DEV_LOGGER;


    /** The location service. */
    @Parameter
    public String locationService = auto;
    
    /** The manual url. */
    @Parameter
    String manualURL = "";
    
    /** The prop location. */
    @Parameter
    String propLocation = "../bonjourpegasus/bin/config/properties";
    
    /** The dax location. */
    @Parameter
    String daxLocation = "../bonjourpegasus/bin/dax/diamond.dax";
    
    /** The rc location. */
    @Parameter
    String rcLocation = "../bonjourpegasus/bin/config/rc.data";
    
    /** The tc location. */
    @Parameter
    String tcLocation = "../bonjourpegasus/bin/config/tc.data";
    
    /** The sites location. */
    @Parameter
    String sitesLocation = "../bonjourpegasus/bin/config/sites.xml";


    /**
     * Process.
     *
     * @param object the object
     */
    @Process
    public void process(Object object) {

        File file = null;
        if (object instanceof File) {
            file = (File) object;
        }
        if (object instanceof String) {
            file = new File((String) object);
        }
        if (file != null) {
            devLog.debug("Uploading file " + file.getName() + " to Pegasus.");
            if (file.exists() && file.canRead()) {
                daxLocation = file.getAbsolutePath();
            }

            if (getAndCheckFiles() && zipFile != null) {
                displayMessage("All files good.");
                devLog.debug("All files good");

                displayMessage("Pegasus locating : " + locationService);
                if (locationService.equals("AUTO")) {
                    devLog.debug("Auto");
                    ServiceInfo pegasusInfo = FindPegasus.findPegasus(20000, this);

                    if (pegasusInfo != null) {
                        displayMessage("Sending to Pegasus");
                        sendToPegasus(pegasusInfo);
                        displayMessage("Finished");
                    }
                }
                if (locationService.equals("URL")) {
                    devLog.debug("Manual *" + manualURL + "*");
                    sendToPegasus(manualURL);
                }
                if (locationService.equals("LOCAL")) {
                    String condor_env = System.getenv("CONDOR_CONFIG");
                    System.out.println("CONDOR_CONFIG : " + condor_env);
                    displayMessage("CONDOR_CONFIG : " + condor_env);
                    if (condor_env.equals("")) {
                        devLog.debug("CONDOR_CONFIG environment variable not set");
                        displayMessage("CONDOR_CONFIG environment variable not set.");
                    } else {
                        devLog.debug("Running org.trianacode.shiwaall.gui-plan locally");
                        runLocal();
                    }
                }
            }
        }
    }

    /**
     * Gets the component.
     *
     * @return the component
     */
    @CustomGUIComponent
    public Component getComponent() {
        return new JLabel("This is the non-gui version of this tool. " +
                "Please use DaxToPegasus from Triana-Pegasus-GUI for more options.");
    }

    /* (non-Javadoc)
     * @see org.trianacode.shiwaall.dax.Displayer#displayMessage(java.lang.String)
     */
    public void displayMessage(String string) {
        devLog.debug(string);
    }

    /* (non-Javadoc)
     * @see org.trianacode.taskgraph.annotation.TaskConscious#setTask(org.trianacode.taskgraph.Task)
     */
    @Override
    public void setTask(Task task) {
        this.task = task;
    }

    /**
     * Gets the and check files.
     *
     * @return the and check files
     */
    private boolean getAndCheckFiles() {
        ArrayList<String> files = new ArrayList<String>();
        files.add(this.getPropertiesLocation());
        files.add(this.getDaxLocation());
        files.add(this.getRcLocation());
        files.add(this.getTcLocation());
        files.add(this.getSitesLocation());

        return checkExists(files);
    }

    /**
     * Check exists.
     *
     * @param files the files
     * @return true, if successful
     */
    private boolean checkExists(ArrayList files) {
        for (Object file : files) {
            String location = (String) file;
            File f = new File((String) file);
            if (!f.exists() && f.canRead()) {
                devLog.debug("File " + location + " doesn't exist.");
                displayMessage("Error : file " + location + " not found");
                return false;
            }
        }

        try {
            devLog.debug("Writing zip");
            zipFile = MakeWorkflowZip.makeZip(this.getDaxLocation(), this.getPropertiesLocation(), this.getRcLocation(), this.getSitesLocation(), this.getTcLocation());
            devLog.debug("Zip created at location : " + zipFile.getCanonicalPath());
        } catch (IOException e) {
            devLog.debug("Failed to make zip");
        }

        return true;
    }

    /**
     * Sends dax related data to the org.trianacode.shiwaall.gui server defined by the JmDNS search
     * If service not found on predicted port (normally 8080), will try 8081, 8082...8090.
     *
     * @param info the info
     */
    private void sendToPegasus(ServiceInfo info) {
        displayMessage("Setting properties.");
        boolean foundAndSent = false;
        int attempt = 0;
        int port = info.getPort();

        while (!foundAndSent && attempt < 10) {
            String url = ("http://" + info.getHostAddress() + ":" + port);
            devLog.debug("Pegasus found at address " + url + ". Trying port " + port);
//            String[] args = {url + "/remotecontrol",
//                    this.getPropertiesLocation(),
//                    this.getDaxLocation(),
//                    this.getRcLocation(),
//                    this.getTcLocation(),
//                    this.getSitesLocation()};
//            Response ret = usePegasusBonjourClient(args);

            Response ret = SendPegasusZip.sendFile(url + "/remotecontrol", zipFile);
            if (ret == null) {
                devLog.debug("Sent, but some error occurred. Received null");
            } else {
                try {

                    int responseCode = ret.getContext().getResponseCode();
                    if (responseCode == 200) {
                        devLog.debug("TriPeg reports success queueing workflow on pegasus");
                    } else {
                        devLog.debug("Error reported from TriPeg server");
                    }

                    InputStream stream = ret.getContext().getResponseEntity().getInputStream();
                    StringBuffer out = new StringBuffer();
                    byte[] b = new byte[4096];
                    for (int n; (n = stream.read(b)) != -1; ) {
                        out.append(new String(b, 0, n));
                    }
                    String link = out.toString();


                    link = link.replaceAll("\\+", "%2B");
                    devLog.debug("Received streamable : " + link);
                    link = url + "/remotecontrol?file=" + link;
                    displayMessage("Link : " + link);

                    BareBonesBrowserLaunch.openURL(link);

                } catch (Exception e) {
                    devLog.debug("Failed to get response entity");
                }
                if (ret.getOutcome().equals("Not Found")) {
                    devLog.debug("Sent zip, received : " + ret.toString());
                    displayMessage(ret.toString());
                    devLog.debug("Pegasus not responding on port " + port + "\n");
                    port++;
                } else {
                    if (ret.getOutcome().equals("Accepted")) {
                        devLog.debug("Sent zip, received : " + ret.toString());
                        displayMessage(ret.toString());
                    }
                    foundAndSent = true;
                    displayMessage("Connection opened and info sent.");
                    devLog.debug("Connection opened and info sent.");
                }
            }
            attempt++;
        }
        devLog.debug("Waiting");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
        }
        devLog.debug("Done");

    }

    /**
     * Sends dax related data to a user specified, manually entered url.
     *
     * @param url the url
     */
    private void sendToPegasus(String url) {

        if (url.equals("")) {
            ParameterNode[] nodes = task.getParameterInputNodes();
            int nodeInt = -1;
            for (ParameterNode node : nodes) {
                if (node.getParameterName().equals("manualURL")) {
                    devLog.debug("Found parameterNode!!");
                }
            }
            Object urlParameter = task.getParameter("manualURL");

            if (urlParameter != null) {
                url = (String) urlParameter;
                devLog.debug(url + " found in parameter manualURL");
            } else {
                devLog.debug("parameter manualURL is null");
            }

        } else {
            devLog.debug("A url is already set - not reading from parameter node.");
        }

        displayMessage("Setting properties.");
//        String[] args = {url,
//                this.getPropertiesLocation(),
//                this.getDaxLocation(),
//                this.getRcLocation(),
//                this.getTcLocation(),
//                this.getSitesLocation()};
//
//        Response ret = null;
//        ret = usePegasusBonjourClient(args);
//
        url += "/remotecontrol";
        devLog.debug("Trying Pegasus at : " + url);
        Response ret = SendPegasusZip.sendFile(url, zipFile);
        if (ret != null) {
            if (ret.getOutcome().equals("Not Found")) {
                devLog.debug("Service could not be found");
                displayMessage("Service could not be found at this address.");
            } else {
                try {
                    InputStream stream = ret.getContext().getResponseEntity().getInputStream();
                    StringBuffer out = new StringBuffer();
                    byte[] b = new byte[4096];
                    for (int n; (n = stream.read(b)) != -1; ) {
                        out.append(new String(b, 0, n));
                    }
                    String link = out.toString();


                    link = link.replaceAll("\\+", "%2B");
                    devLog.debug("Received streamable : " + link);
                    link = url + "?file=" + link;
                    displayMessage("Link : " + link);

                    BareBonesBrowserLaunch.openURL(link);

                } catch (Exception e) {
                    devLog.debug("Failed to get response entity");
                }
                devLog.debug("Connection opened and info sent.");
                displayMessage("Connection opened and info sent.");

            }
            displayMessage(ret.toString());
        } else {
            devLog.debug("Fail");
        }
        devLog.debug("Waiting");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
        }
        devLog.debug("Done");

    }

    /**
     * Gets the properties location.
     *
     * @return the properties location
     */
    public String getPropertiesLocation() {
        return propLocation;
    }

    /**
     * Gets the dax location.
     *
     * @return the dax location
     */
    public String getDaxLocation() {
        return daxLocation;
    }

    /**
     * Gets the rc location.
     *
     * @return the rc location
     */
    public String getRcLocation() {
        return rcLocation;
    }

    /**
     * Gets the tc location.
     *
     * @return the tc location
     */
    public String getTcLocation() {
        return tcLocation;
    }

    /**
     * Gets the sites location.
     *
     * @return the sites location
     */
    public String getSitesLocation() {
        return sitesLocation;
    }

    /**
     * Run local.
     */
    private void runLocal() {
        devLog.debug("Running locally");
        List commmandStrVector = new ArrayList();
        String outputDir = System.getProperty("user.dir") + "/pegasus_output";

        String topDir = System.getProperty("user.dir");

        CatalogBuilder.buildSitesFile(topDir);
        CatalogBuilder.buildPropertiesFile(topDir);

//        String cmd = "org.trianacode.shiwaall.gui-plan" + " -D org.trianacode.shiwaall.gui.user.properties=" + propLocation + " --sites condorpool" +
//                " --dir " + outputDir +
//                " --output local" + " --dax " + daxLocation +" --submit";

        String cmd = "org.trianacode.shiwaall.gui-plan" +
                " -D org.trianacode.shiwaall.gui.user.properties=" + System.getProperty("user.dir") + File.separator + "properties" +
                " --sites condorpool" +
                " --dir " + outputDir +
                " --output local" + " --dax " + daxLocation + " --submit";

        devLog.debug("Running : " + cmd);
        displayMessage("Running : " + cmd);

        runExec(cmd);
        displayMessage("Results in folder : " + outputDir);
        runExec("condor_q");
    }

    /**
     * Run exec.
     *
     * @param cmd the cmd
     */
    private void runExec(String cmd) {
        try {
            Runtime runtime = Runtime.getRuntime();
            java.lang.Process process = runtime.exec(cmd);  // execute command

            BufferedReader errorreader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String str;
            String errLog = "";
            boolean errors = false;
            while ((str = errorreader.readLine()) != null) {
                errors = true;
                errLog += str + "\n";
            }
            errorreader.close();

            BufferedReader inreader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder out = new StringBuilder();
            str = "";
            while ((str = inreader.readLine()) != null) {
                out.append(str).append("\n");
            }
            inreader.close();
            displayMessage(out.toString());
            displayMessage("Errors : " + errLog);
            displayMessage("Done.");

            devLog.debug("Output from Executable :\n\n" + out.toString());
            devLog.debug("Errors from Executable :\n\n" + errLog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

