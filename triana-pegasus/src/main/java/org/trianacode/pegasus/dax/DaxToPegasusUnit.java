package org.trianacode.pegasus.dax;

import org.apache.commons.logging.Log;
import org.thinginitself.http.Response;
import org.trianacode.annotation.CustomGUIComponent;
import org.trianacode.annotation.Parameter;
import org.trianacode.annotation.Process;
import org.trianacode.annotation.Tool;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.pegasus.extras.BareBonesBrowserLaunch;
import org.trianacode.pegasus.sendToPegasus.FindPegasus;
import org.trianacode.pegasus.sendToPegasus.MakeWorkflowZip;
import org.trianacode.pegasus.sendToPegasus.SendPegasusZip;
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


/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Nov 30, 2010
 * Time: 2:34:25 PM
 * To change this template use File | Settings | File Templates.
 */

@Tool
public class DaxToPegasusUnit implements TaskConscious, Displayer {

    File zipFile = null;
    public Task task;


    public HashMap<String, JTextField> locationMap = new HashMap<String, JTextField>();
    public HashMap<String, JRadioButton> radioMap = new HashMap<String, JRadioButton>();

    public static final String dax = "daxLocation";
    public static final String prop = "propLocation";
    public static final String rc = "rcLocation";
    public static final String sites = "sitesLocation";
    public static final String tc = "tcLocation";

    public static final String auto = "AUTO";
    public static final String manual = "URL";
    public static final String local = "LOCAL";


    @Parameter
    public String locationService = auto;
    @Parameter
    String manualURL = "";
    @Parameter
    String propLocation = "../bonjourpegasus/bin/config/properties";
    @Parameter
    String daxLocation = "../bonjourpegasus/bin/dax/diamond.dax";
    @Parameter
    String rcLocation = "../bonjourpegasus/bin/config/rc.data";
    @Parameter
    String tcLocation = "../bonjourpegasus/bin/config/tc.data";
    @Parameter
    String sitesLocation = "../bonjourpegasus/bin/config/sites.xml";


    @Process
    public void process(File file) {

        log("Uploading file " + file.getName() + " to Pegasus.");

        if (file.exists() && file.canRead()) {
            daxLocation = file.getAbsolutePath();
        }

        if (getAndCheckFiles() && zipFile != null) {
            displayMessage("All files good.");
            log("All files good");

            displayMessage("Pegasus locating : " + locationService);
            if (locationService.equals("AUTO")) {
                log("Auto");
                ServiceInfo pegasusInfo = FindPegasus.findPegasus(20000, this);

                if (pegasusInfo != null) {
                    displayMessage("Sending to Pegasus");
                    sendToPegasus(pegasusInfo);
                    displayMessage("Finished");
                }
            }
            if (locationService.equals("URL")) {
                log("Manual *" + manualURL + "*");
                sendToPegasus(manualURL);
            }
            if (locationService.equals("LOCAL")) {
                String condor_env = System.getenv("CONDOR_CONFIG");
                System.out.println("CONDOR_CONFIG : " + condor_env);
                displayMessage("CONDOR_CONFIG : " + condor_env);
                if (condor_env.equals("")) {
                    log("CONDOR_CONFIG environment variable not set");
                    displayMessage("CONDOR_CONFIG environment variable not set.");
                } else {
                    log("Running org.trianacode.pegasus.gui-plan locally");
                    runLocal();
                }
            }
            //           popup.finish();
        }
    }

    @CustomGUIComponent
    public Component getComponent() {
        return new JLabel("This is the non-gui version of this tool. " +
                "Please use DaxToPegasus from Triana-Pegasus-GUI for more options.");
    }

    public void displayMessage(String string) {
        log(string);
    }

    @Override
    public void setTask(Task task) {
        this.task = task;
    }

    private boolean getAndCheckFiles() {
        ArrayList<String> files = new ArrayList<String>();
        files.add(this.getPropertiesLocation());
        files.add(this.getDaxLocation());
        files.add(this.getRcLocation());
        files.add(this.getTcLocation());
        files.add(this.getSitesLocation());

        return checkExists(files);
    }

    private boolean checkExists(ArrayList files) {
        for (Object file : files) {
            String location = (String) file;
            File f = new File((String) file);
            if (!f.exists() && f.canRead()) {
                log("File " + location + " doesn't exist.");
                displayMessage("Error : file " + location + " not found");
                return false;
            }
        }

        try {
            log("Writing zip");
            zipFile = MakeWorkflowZip.makeZip(this.getDaxLocation(), this.getPropertiesLocation(), this.getRcLocation(), this.getSitesLocation(), this.getTcLocation());
            System.out.println("Zip created at location : " + zipFile.getCanonicalPath());
        } catch (IOException e) {
            log("Failed to make zip");
        }

        return true;
    }

    /**
     * Sends dax related data to the org.trianacode.pegasus.gui server defined by the JmDNS search
     * If service not found on predicted port (normally 8080), will try 8081, 8082...8090.
     *
     * @param info
     */
    private void sendToPegasus(ServiceInfo info) {
        displayMessage("Setting properties.");
        boolean foundAndSent = false;
        int attempt = 0;
        int port = info.getPort();

        while (!foundAndSent && attempt < 10) {
            String url = ("http://" + info.getHostAddress() + ":" + port);
            log("Pegasus found at address " + url + ". Trying port " + port);
//            String[] args = {url + "/remotecontrol",
//                    this.getPropertiesLocation(),
//                    this.getDaxLocation(),
//                    this.getRcLocation(),
//                    this.getTcLocation(),
//                    this.getSitesLocation()};
//            Response ret = usePegasusBonjourClient(args);

            Response ret = SendPegasusZip.sendFile(url + "/remotecontrol", zipFile);
            if (ret == null) {
                System.out.println("Sent, but some error occurred. Received null");
            } else {
                try {

                    int responseCode = ret.getContext().getResponseCode();
                    if (responseCode == 200) {
                        System.out.println("TriPeg reports success queueing workflow on org.trianacode.pegasus.gui");
                    } else {
                        System.out.println("Error reported from TriPeg server");
                    }

                    InputStream stream = ret.getContext().getResponseEntity().getInputStream();
                    StringBuffer out = new StringBuffer();
                    byte[] b = new byte[4096];
                    for (int n; (n = stream.read(b)) != -1;) {
                        out.append(new String(b, 0, n));
                    }
                    String link = out.toString();


                    link = link.replaceAll("\\+", "%2B");
                    System.out.println("Received streamable : " + link);
                    link = url + "/remotecontrol?file=" + link;
                    displayMessage("Link : " + link);

                    BareBonesBrowserLaunch.openURL(link);

                } catch (Exception e) {
                    System.out.println("Failed to get response entity");
                }
                if (ret.getOutcome().equals("Not Found")) {
                    System.out.println("Sent zip, received : " + ret.toString());
                    displayMessage(ret.toString());
                    log("Pegasus not responding on port " + port + "\n");
                    port++;
                } else {
                    if (ret.getOutcome().equals("Accepted")) {
                        System.out.println("Sent zip, received : " + ret.toString());
                        displayMessage(ret.toString());
                    }
                    foundAndSent = true;
                    displayMessage("Connection opened and info sent.");
                    log("Connection opened and info sent.");
                }
            }
            attempt++;
        }
        log("Waiting");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
        }
        log("Done");

    }

    /**
     * Sends dax related data to a user specified, manually entered url
     *
     * @param url
     */
    private void sendToPegasus(String url) {

        if (url.equals("")) {
            ParameterNode[] nodes = task.getParameterInputNodes();
            int nodeInt = -1;
            for (ParameterNode node : nodes) {
                if (node.getParameterName().equals("manualURL")) {
                    System.out.println("Found parameterNode!!");
                }
            }
            Object urlParameter = task.getParameter("manualURL");

            if (urlParameter != null) {
                url = (String) urlParameter;
                System.out.println(url + " found in parameter manualURL");
            } else {
                System.out.println("parameter manualURL is null");
            }

        } else {
            System.out.println("A url is already set - not reading from parameter node.");
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
        log("Trying Pegasus at : " + url);
        Response ret = SendPegasusZip.sendFile(url, zipFile);
        if (ret != null) {
            if (ret.getOutcome().equals("Not Found")) {
                log("Service could not be found");
                displayMessage("Service could not be found at this address.");
            } else {
                try {
                    InputStream stream = ret.getContext().getResponseEntity().getInputStream();
                    StringBuffer out = new StringBuffer();
                    byte[] b = new byte[4096];
                    for (int n; (n = stream.read(b)) != -1;) {
                        out.append(new String(b, 0, n));
                    }
                    String link = out.toString();


                    link = link.replaceAll("\\+", "%2B");
                    System.out.println("Received streamable : " + link);
                    link = url + "?file=" + link;
                    displayMessage("Link : " + link);

                    BareBonesBrowserLaunch.openURL(link);

                } catch (Exception e) {
                    System.out.println("Failed to get response entity");
                }
                log("Connection opened and info sent.");
                displayMessage("Connection opened and info sent.");

            }
            displayMessage(ret.toString());
        } else {
            log("Fail");
        }
        log("Waiting");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
        }
        log("Done");

    }

    public String getPropertiesLocation() {
        return propLocation;
    }

    public String getDaxLocation() {
        return daxLocation;
    }

    public String getRcLocation() {
        return rcLocation;
    }

    public String getTcLocation() {
        return tcLocation;
    }

    public String getSitesLocation() {
        return sitesLocation;
    }

    private void log(String s) {
        Log log = Loggers.DEV_LOGGER;
        log.debug(s);
        System.out.println(s);
    }

    private void runLocal() {
        log("Running locally");
        List commmandStrVector = new ArrayList();
        String outputDir = System.getProperty("user.dir") + "/pegasus_output";

        String topDir = System.getProperty("user.dir");

        CatalogBuilder.buildSitesFile(topDir);
        CatalogBuilder.buildPropertiesFile(topDir);

//        String cmd = "org.trianacode.pegasus.gui-plan" + " -D org.trianacode.pegasus.gui.user.properties=" + propLocation + " --sites condorpool" +
//                " --dir " + outputDir +
//                " --output local" + " --dax " + daxLocation +" --submit";

        String cmd = "org.trianacode.pegasus.gui-plan" +
                " -D org.trianacode.pegasus.gui.user.properties=" + System.getProperty("user.dir") + File.separator + "properties" +
                " --sites condorpool" +
                " --dir " + outputDir +
                " --output local" + " --dax " + daxLocation + " --submit";

        log("Running : " + cmd);
        displayMessage("Running : " + cmd);

        runExec(cmd);
        displayMessage("Results in folder : " + outputDir);
        runExec("condor_q");
    }

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

            log("Output from Executable :\n\n" + out.toString());
            log("Errors from Executable :\n\n" + errLog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

