package org.trianacode.pegasus.dax;

import org.apache.commons.logging.Log;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.pegasus.bonjour.PegasusBonjourClient;
import org.trianacode.pegasus.jmdns.JmDNS;
import org.trianacode.pegasus.jmdns.ServiceEvent;
import org.trianacode.pegasus.jmdns.ServiceInfo;
import org.trianacode.pegasus.jmdns.ServiceListener;
import org.trianacode.taskgraph.annotation.Parameter;
import org.trianacode.taskgraph.annotation.Process;
import org.trianacode.taskgraph.annotation.Tool;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Nov 30, 2010
 * Time: 2:34:25 PM
 * To change this template use File | Settings | File Templates.
 */

@Tool(panelClass="org.trianacode.pegasus.dax.DaxToPegasusPanel")
public class DaxToPegasusUnit {

    @Parameter
    String propertiesLocation = "../bonjourpegasus/bin/config/properties";
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
        log("Uploading file " + file.getName() + " to Pegasus.");

        if(file.exists() && file.canRead()){
            daxLocation = file.getAbsolutePath();
        }

        if(getAndCheckFiles()){
            log("All files good");
            String address = findPegasus(20000);

            if(address != null){
                sendToPegasus(address);
            }
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
                return false;
            }
        }
        return true;
    }

    private String findPegasus(long timeout){
        log("Trying to find services with JmDNS");
        JmDNS jmdns = null;
        String pegasusAddress = "";
        boolean found = false;

        try {
            jmdns = JmDNS.create(InetAddress.getLocalHost());
            String typeString = "_http._tcp.local.";
            PegasusListener pl = new PegasusListener(jmdns, typeString);
            jmdns.addServiceListener(typeString, pl);

            long startTime = System.currentTimeMillis();
            long timeNow = 0;
            while(!pl.foundSomething() && timeNow < (startTime + timeout)){
                log("Nothing found, waiting again.");
                try {Thread.sleep(1000);}catch(InterruptedException e) {}
                timeNow = System.currentTimeMillis();
            }

            if(pl.foundSomething()){
                for (Object o : pl.getServices()) {
                    ServiceInfo info = (ServiceInfo) o;
                    log("Found service : " + info.getName() + " Address " + info.getURL());
                    if (info.getName().contains("Pegasus")) {
                        pegasusAddress = info.getURL();
                        found = true;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            log("Something broke.");
        } finally{
            if (jmdns != null) {
                try {
                    jmdns.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if(found){
            return pegasusAddress;
        }else{
            log("Pegasus is hiding... can't find it.");
            return null;
        }
    }

    private void sendToPegasus(String pegasusAddress){
        log("Pegasus found!! Address " + pegasusAddress);
        String[] args = {pegasusAddress +"/remotecontrol",
                this.getPropertiesLocation(),
                this.getDaxLocation(),
                this.getRcLocation(),
                this.getTcLocation(),
                this.getSitesLocation()};

        PegasusBonjourClient pbc = new PegasusBonjourClient();
        pbc.parse(args);
        log("Waiting");
        try {Thread.sleep(10000);}catch(InterruptedException e) {}
        log("Done");

    }

    public String getPropertiesLocation() {
        return propertiesLocation;
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


