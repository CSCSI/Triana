package org.trianacode.shiwaall.sendToPegasus;

import org.apache.commons.logging.Log;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.shiwaall.dax.Displayer;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import java.io.IOException;
import java.net.InetAddress;

// TODO: Auto-generated Javadoc
/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 07/03/2011
 * Time: 10:48
 * To change this template use File | Settings | File Templates.
 */
public class FindPegasus {


    /**
     * Log.
     *
     * @param s the s
     */
    private static void log(String s) {
        Log log = Loggers.DEV_LOGGER;
        log.debug(s);
        System.out.println(s);
    }

    /**
     * Find pegasus.
     *
     * @param timeout the timeout
     * @param displayer the displayer
     * @return the service info
     */
    public static ServiceInfo findPegasus(long timeout, Displayer displayer) {

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

            if (displayer != null) {
                displayer.displayMessage("Scanning network for Pegasus installations.");
            }
            while (!pl.foundSomething() && timeNow < (startTime + timeout)) {
                log("Nothing found, waiting again.");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
                timeNow = System.currentTimeMillis();
            }

            if (pl.foundSomething()) {
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
                    if (info.getName().toLowerCase().contains("org.trianacode.shiwaall.gui")) {
                        if (displayer != null) {
                            displayer.displayMessage("Found Pegasus : " + info.getURL());
                        }
                        pegasusInfo = info;
                        found = true;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            log("Something broke.");
            if (displayer != null) {
                displayer.displayMessage("Networking error");
            }
        } finally {
            if (jmdns != null) {
                try {
                    jmdns.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (displayer != null) {
                    displayer.displayMessage("Closing JmDNS");
                }
            }
        }

        if (found) {
            return pegasusInfo;
        } else {
            log("Pegasus is hiding... can't find it.");
            if (displayer != null) {
                displayer.displayMessage("Couldn't find Pegasus on local network.");
            }
            return null;
        }
    }
}
