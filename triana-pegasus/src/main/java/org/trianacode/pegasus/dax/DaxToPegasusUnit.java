package org.trianacode.pegasus.dax;

import org.apache.commons.logging.Log;
import org.thinginitself.http.Response;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.pegasus.bonjour.PegasusBonjourClient;
import org.trianacode.pegasus.extras.ProgressPopup;
import org.trianacode.pegasus.jmdns.JmDNS;
import org.trianacode.pegasus.jmdns.ServiceEvent;
import org.trianacode.pegasus.jmdns.ServiceInfo;
import org.trianacode.pegasus.jmdns.ServiceListener;
import org.trianacode.taskgraph.annotation.Parameter;
import org.trianacode.taskgraph.annotation.Process;
import org.trianacode.taskgraph.annotation.Tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Nov 30, 2010
 * Time: 2:34:25 PM
 * To change this template use File | Settings | File Templates.
 */

@Tool(panelClass="org.trianacode.pegasus.dax.DaxToPegasusPanel")
public class DaxToPegasusUnit {

    ProgressPopup popup;

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
    public void process(File file){
        popup = new ProgressPopup("Finding Pegasus", 30);
        log("Uploading file " + file.getName() + " to Pegasus.");

        if(file.exists() && file.canRead()){
            daxLocation = file.getAbsolutePath();
        }

        if(getAndCheckFiles()){
            popup.addText("All files good.");
            log("All files good");

            popup.addText("Pegasus locating : " + locationService);
            if(locationService.equals("AUTO")){
                log("Auto");
                ServiceInfo pegasusInfo = findPegasus(20000);

                if(pegasusInfo != null){
                    popup.addText("Sending to Pegasus");
                    sendToPegasus(pegasusInfo);
                    popup.addText("Finished");
                }
            }
            if(locationService.equals("URL")){
                log("Manual");
                sendToPegasus(manualURL);
            }
            if(locationService.equals("LOCAL")){
                String condor_env = System.getenv("CONDOR_CONFIG");
                System.out.println("CONDOR_CONFIG : " + condor_env);
                popup.addText("CONDOR_CONFIG : " + condor_env);
                if(condor_env.equals("")){
                    log("CONDOR_CONFIG environment variable not set");
                    popup.addText("CONDOR_CONFIG environment variable not set.");
                }else{
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

    private boolean checkExists(ArrayList files){
        for (Object file : files) {
            String location = (String) file;
            File f = new File((String)file);
            if (!f.exists() && f.canRead()) {
                log("File " + location + " doesn't exist.");
                popup.addTextNoProgress("Error : file " + location + " not found");
                return false;
            }
        }
        return true;
    }

    private ServiceInfo findPegasus(long timeout){
        log("Trying to find services with JmDNS");
        JmDNS jmdns = null;
        ServiceInfo pegasusInfo = null;
        boolean found = false;

        try {
            jmdns = JmDNS.create(InetAddress.getLocalHost());
            String typeString = "_http._tcp.local.";
            PegasusListener pl = new PegasusListener(jmdns, typeString);
            jmdns.addServiceListener(typeString, pl);

            long startTime = System.currentTimeMillis();
            long timeNow = 0;

            popup.addText("Scanning network for Pegasus installations.");
            popup.setUnsureTime();
            while(!pl.foundSomething() && timeNow < (startTime + timeout)){
                log("Nothing found, waiting again.");
                try {Thread.sleep(1000);}catch(InterruptedException e) {}
                timeNow = System.currentTimeMillis();
            }

            if(pl.foundSomething()){
                for (Object o : pl.getServices()) {
                    ServiceInfo info = (ServiceInfo) o;
                    log("\n       Found service : " + info.getName() +
                            "\n     Address " + info.getURL() +
                            "\n     " + info.getHostAddress() +
                            "\n     " + info.getDomain() +
                            "\n     " + info.getInetAddress() +
                            "\n     " + info.getPort() +
                            "\n     " + info.getServer() +
                            "\n     " + info.getApplication() +
                            "\n      " + info.toString() + "\n");
                    if (info.getName().contains("Pegasus")) {
                        popup.addText("Found Pegasus : " + info.getURL());
                        pegasusInfo = info;

                        found = true;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            log("Something broke.");
            popup.addTextNoProgress("Networking error");
        } finally{
            if (jmdns != null) {
                try {
                    jmdns.close();
                    popup.addText("Closing JmDNS");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if(found){
            return pegasusInfo;
        }else{
            log("Pegasus is hiding... can't find it.");
            popup.addText("Couldn't find Pegasus on local network.");
            return null;
        }
    }

    private void sendToPegasus(ServiceInfo info){
        popup.addText("Setting properties.");
        boolean foundAndSent = false;
        int attempt = 0;
        int port = info.getPort();

        while( !foundAndSent && attempt < 10){
            String url = ("http://" + info.getHostAddress() + ":" + port);
            log("Pegasus found at address " + url + ". Trying port " + port);
            String[] args = {url +"/remotecontrol",
                    this.getPropertiesLocation(),
                    this.getDaxLocation(),
                    this.getRcLocation(),
                    this.getTcLocation(),
                    this.getSitesLocation()};

            PegasusBonjourClient pbc = new PegasusBonjourClient();
            popup.addTextNoProgress("Parsing args : " + url);
            Response ret = pbc.parse(args);
            String result = ret.toString();


            if(ret.getOutcome().equals("Not Found")){
                log("Pegasus not responding on port " + port + "\n");
                port ++;
                attempt ++;
            }else{
                foundAndSent = true;
                popup.addText("Connection opened and info sent.");
                log("Connection opened and info sent.");
            }
            popup.addText(result);

        }
        log("Waiting");
        try {Thread.sleep(10000);}catch(InterruptedException e) {}
        log("Done");

    }

    private void sendToPegasus(String url){
        popup.addText("Setting properties.");

        String[] args = {url,
                this.getPropertiesLocation(),
                this.getDaxLocation(),
                this.getRcLocation(),
                this.getTcLocation(),
                this.getSitesLocation()};

        PegasusBonjourClient pbc = new PegasusBonjourClient();
        popup.addText("Parsing args : " + url);
        Response ret = pbc.parse(args);
        String result = ret.toString();

        if(ret.getOutcome().equals("Not Found")){
            log("Service could not be found");
            popup.addTextNoProgress("Service could not be found at this address.");
        }else{
            log("Connection opened and info sent.");
            popup.addText("Connection opened and info sent.");

        }
        popup.addText(result);


        log("Waiting");
        try {Thread.sleep(10000);}catch(InterruptedException e) {}
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

    private void log(String s){
        Log log = Loggers.DEV_LOGGER;
        log.debug(s);
        System.out.println(s);
    }

    class PegasusListener implements ServiceListener {

        JmDNS jmdns;
        String mdns_type;
        private ArrayList<org.trianacode.pegasus.jmdns.ServiceInfo> services;

        public PegasusListener(JmDNS jmdns, String mdns_type){
            this.jmdns = jmdns;
            this.mdns_type = mdns_type;
            services = new ArrayList<ServiceInfo>();
        }

        public ArrayList getServices(){return services;}

        @Override
        public void serviceAdded(ServiceEvent event) {
            log("Service added   : " + event.getName() + "." + event.getType());
            log("Found this ->" + event.getInfo().toString());
            refreshList();
        }

        @Override
        public void serviceRemoved(org.trianacode.pegasus.jmdns.ServiceEvent event) {
            log("Service removed : " + event.getName() + "." + event.getType());
            refreshList();
        }

        @Override
        public void serviceResolved(ServiceEvent event) {
            log("Service resolved: " + event.getInfo());
            refreshList();
        }
        public boolean foundSomething(){
            if(services.size() > 0){
                return true;
            }
            else{
                return false;
            }
        }

        private void refreshList(){
            log("Something happened");
            ServiceInfo[] infos = jmdns.list(mdns_type);
            if (infos != null && infos.length > 0) {
                ArrayList<org.trianacode.pegasus.jmdns.ServiceInfo> temp = new ArrayList<ServiceInfo>();
                for (int i = 0; i < infos.length; i++) { temp.add(infos[i]);}
                services = temp;
            }
        }
    }

    private void runLocal(){
        log("Running locally");
        List commmandStrVector = new ArrayList();
        String outputDir = System.getProperty("user.dir") + "/pegasus_output";

//            String []cmdarray = {"pegasus-plan", " -D pegasus.user.properties=" + propLocation, " --sites condorpool",
//                    "--dir " + System.getProperty("user.dir") + "/pegasus_output", "--output local", "--dax " + daxLocation, " --submit"
//            };
//        String[] cmdarray = (String[]) commmandStrVector.toArray(new String[commmandStrVector.size()]);

        String cmd = "pegasus-plan" + " -D pegasus.user.properties=" + propLocation + " --sites condorpool" +
                " --dir " + outputDir +
                " --output local" + " --dax " + daxLocation +" --submit";
        log("Running : " + cmd);
        popup.addText("Running : " + cmd);
        popup.setUnsureTime();

        runExec(cmd);
        popup.addText("Results in folder : " + outputDir);
        runExec("condor_q");

    }
    private void runExec(String cmd){
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
                popup.addTextNoProgress(out.toString());
            }
            inreader.close();
            popup.addText("Done.");

            log("Output from Executable :\n\n" + out.toString());
            log("Errors from Executable :\n\n" + errLog);
        } catch (Exception e){e.printStackTrace();}
    }
}


//TODO maybe thread this?
class JmDNSRun extends Thread{
    boolean running = false;

    public void JmDNS(){
    }


    public void run(){
        while(running){

        }
    }
}


