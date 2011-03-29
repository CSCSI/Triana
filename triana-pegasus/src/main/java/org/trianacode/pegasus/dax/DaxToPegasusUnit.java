package org.trianacode.pegasus.dax;

import org.apache.commons.logging.Log;
import org.thinginitself.http.Response;
import org.trianacode.annotation.Parameter;
import org.trianacode.annotation.Process;
import org.trianacode.annotation.Tool;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.pegasus.extras.ProgressPopup;
import org.trianacode.pegasus.sendToPegasus.FindPegasus;
import org.trianacode.pegasus.sendToPegasus.MakeWorkflowZip;
import org.trianacode.pegasus.sendToPegasus.SendPegasusZip;

import javax.jmdns.ServiceInfo;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Nov 30, 2010
 * Time: 2:34:25 PM
 * To change this template use File | Settings | File Templates.
 */

@Tool(panelClass = "org.trianacode.pegasus.dax.DaxToPegasusPanel")
public class DaxToPegasusUnit {

    ProgressPopup popup;
    File zipFile = null;

    @Parameter
    String locationService = "AUTO";
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
        popup = new ProgressPopup("Finding Pegasus", 30);
        log("Uploading file " + file.getName() + " to Pegasus.");

        if (file.exists() && file.canRead()) {
            daxLocation = file.getAbsolutePath();
        }

        if (getAndCheckFiles() && zipFile != null) {
            popup.addText("All files good.");
            log("All files good");

            popup.addText("Pegasus locating : " + locationService);
            if (locationService.equals("AUTO")) {
                log("Auto");
                ServiceInfo pegasusInfo = FindPegasus.findPegasus(20000, popup);

                if (pegasusInfo != null) {
                    popup.addText("Sending to Pegasus");
                    sendToPegasus(pegasusInfo);
                    popup.addText("Finished");
                }
            }
            if (locationService.equals("URL")) {
                log("Manual");
                sendToPegasus(manualURL);
            }
            if (locationService.equals("LOCAL")) {
                String condor_env = System.getenv("CONDOR_CONFIG");
                System.out.println("CONDOR_CONFIG : " + condor_env);
                popup.addText("CONDOR_CONFIG : " + condor_env);
                if (condor_env.equals("")) {
                    log("CONDOR_CONFIG environment variable not set");
                    popup.addText("CONDOR_CONFIG environment variable not set.");
                } else {
                    log("Running pegasus-plan locally");
                    runLocal();
                }
            }
            popup.finish();
        }
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
                popup.addTextNoProgress("Error : file " + location + " not found");
                return false;
            }
        }

        try {
            log("Writing zip");
            zipFile = MakeWorkflowZip.makeZip(this.getDaxLocation(), this.getPropertiesLocation(), this.getRcLocation(), this.getSitesLocation(), this.getTcLocation());
        } catch (IOException e) {
            log("Failed to make zip");
        }

        return true;
    }
//
//    private ServiceInfo findPegasus(long timeout){
//        log("Trying to find services with JmDNS");
//        JmDNS jmdns = null;
//        ServiceInfo pegasusInfo = null;
//        boolean found = false;
//
//        try {
//            jmdns = JmDNS.create(InetAddress.getLocalHost());
//            String typeString = "_http._tcp.local.";
//            PegasusListener pl = new PegasusListener(jmdns, typeString);
//            jmdns.addServiceListener(typeString, pl);
//
//            long startTime = System.currentTimeMillis();
//            long timeNow = 0;
//
//            popup.addText("Scanning network for Pegasus installations.");
//            popup.setUnsureTime();
//            while(!pl.foundSomething() && timeNow < (startTime + timeout)){
//                log("Nothing found, waiting again.");
//                try {Thread.sleep(1000);}catch(InterruptedException e) {}
//                timeNow = System.currentTimeMillis();
//            }
//
//            if(pl.foundSomething()){
//                for (Object o : pl.getServices()) {
//                    ServiceInfo info = (ServiceInfo) o;
//                    log("\n       Found service : " + info.getName() +
//                            "\n     Address " + info.getURL() +
//                            "\n     " + info.getHostAddress() +
//                            //                  "\n     " + info.getDomain() +
//                            "\n     " + info.getInetAddress() +
//                            "\n     " + info.getPort() +
//                            "\n     " + info.getServer() +
//                            //                  "\n     " + info.getApplication() +
//                            "\n      " + info.toString() + "\n");
//                    if (info.getName().toLowerCase().contains("pegasus")) {
//                        popup.addText("Found Pegasus : " + info.getURL());
//                        pegasusInfo = info;
//
//                        found = true;
//                    }
//                }
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            log("Something broke.");
//            popup.addTextNoProgress("Networking error");
//        } finally{
//            if (jmdns != null) {
//                try {
//                    jmdns.close();
//                } catch(IOException e){e.printStackTrace();}
//                popup.addText("Closing JmDNS");
//            }
//        }
//
//        if(found){
//            return pegasusInfo;
//        }else{
//            log("Pegasus is hiding... can't find it.");
//            popup.addText("Couldn't find Pegasus on local network.");
//            return null;
//        }
//    }

    /**
     * Sends dax related data to the pegasus server defined by the JmDNS search
     * If service not found on predicted port (normally 8080), will try 8081, 8082...8090.
     *
     * @param info
     */
    private void sendToPegasus(ServiceInfo info) {
        popup.addText("Setting properties.");
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
                if (ret.getOutcome().equals("Not Found")) {
                    System.out.println("Sent zip, received : " + ret.toString());
                    popup.addText(ret.toString());
                    log("Pegasus not responding on port " + port + "\n");
                    port++;
                } else {
                    if (ret.getOutcome().equals("Accepted")) {
                        System.out.println("Sent zip, received : " + ret.toString());
                        popup.addText(ret.toString());
                    }
                    foundAndSent = true;
                    popup.addText("Connection opened and info sent.");
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
        popup.addText("Setting properties.");
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
        Response ret = SendPegasusZip.sendFile(url + "/remotecontrol", zipFile);
        if (ret != null) {
            if (ret.getOutcome().equals("Not Found")) {
                log("Service could not be found");
                popup.addTextNoProgress("Service could not be found at this address.");
            } else {
                log("Connection opened and info sent.");
                popup.addText("Connection opened and info sent.");

            }
            popup.addText(ret.toString());
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

//    private Response usePegasusBonjourClient(String[] args){
//        PegasusBonjourClient pbc = new PegasusBonjourClient();
//        popup.addText("Parsing args : " + args[0]);
//        Response ret = pbc.parse(args);
//        if (ret != null) {
//            System.out.println("Response : " + ret.toString());
//        }
//        return ret;
//    }

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

//        String cmd = "pegasus-plan" + " -D pegasus.user.properties=" + propLocation + " --sites condorpool" +
//                " --dir " + outputDir +
//                " --output local" + " --dax " + daxLocation +" --submit";

        String cmd = "pegasus-plan" +
                " -D pegasus.user.properties=" + System.getProperty("user.dir") + File.separator + "properties" +
                " --sites condorpool" +
                " --dir " + outputDir +
                " --output local" + " --dax " + daxLocation + " --submit";

        log("Running : " + cmd);
        popup.addText("Running : " + cmd);
        popup.setUnsureTime();

        runExec(cmd);
        popup.addText("Results in folder : " + outputDir);
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
            popup.addText(out.toString());
            popup.addText("Errors : " + errLog);
            popup.addText("Done.");

            log("Output from Executable :\n\n" + out.toString());
            log("Errors from Executable :\n\n" + errLog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


//TODO maybe thread this?
//class JmDNSRun extends Thread{
//    boolean running = false;
//
//    public void JmDNS(){
//    }
//
//
//    public void run(){
//        while(running){
//
//        }
//    }
//}


