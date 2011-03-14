package org.trianacode.pegasus.sendToPegasus;

import org.apache.commons.logging.Log;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.pegasus.extras.ProgressPopup;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import java.io.IOException;
import java.net.InetAddress;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 07/03/2011
 * Time: 10:48
 * To change this template use File | Settings | File Templates.
 */
public class FindPegasus {


    private static void log(String s){
        Log log = Loggers.DEV_LOGGER;
        log.debug(s);
        System.out.println(s);
    }

    public static ServiceInfo findPegasus(long timeout, ProgressPopup popup){

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

            if(popup != null){
                popup.addText("Scanning network for Pegasus installations.");
                popup.setUnsureTime();
            }
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
                            //                  "\n     " + info.getDomain() +
                            "\n     " + info.getInetAddress() +
                            "\n     " + info.getPort() +
                            "\n     " + info.getServer() +
                            //                  "\n     " + info.getApplication() +
                            "\n      " + info.toString() + "\n");
                    if (info.getName().toLowerCase().contains("pegasus")) {
                        if(popup != null){
                            popup.addText("Found Pegasus : " + info.getURL());
                        }
                        pegasusInfo = info;
                        found = true;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            log("Something broke.");
            if(popup != null){
                popup.addTextNoProgress("Networking error");
            }
        } finally{
            if (jmdns != null) {
                try {
                    jmdns.close();
                } catch(IOException e){e.printStackTrace();}
                if(popup != null){
                    popup.addText("Closing JmDNS");
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
}
