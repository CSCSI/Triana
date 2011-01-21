package org.trianacode.pegasus.bonjour;


import javax.jmdns.JmDNS;
import javax.jmdns.JmmDNS;
import javax.jmdns.ServiceInfo;
import java.io.IOException;
import java.net.InetAddress;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Jan 19, 2011
 * Time: 2:09:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class PegasusService {

    public PegasusService(String serviceType, int port, String description) throws IOException {
        InetAddress[] addresses = JmmDNS.NetworkTopologyDiscovery.Factory.getInstance().getInetAddresses();
        InetAddress addr = null;
        if (addresses.length > 0) {
            for (InetAddress address : addresses) {
                if(!(address.getHostAddress().contains(":"))){
                    addr = address;
                }
            }
        }
        if(addr == null || addr.isLoopbackAddress()){
            addr = InetAddress.getLocalHost();
        }
        JmDNS jmdns = JmDNS.create(addr);

        System.out.println("Hostaddress : " + addr + " Address : " + jmdns.getInterface());

        jmdns.addServiceListener(serviceType, new PegasusListener(jmdns, serviceType));
        jmdns.registerService(ServiceInfo.create(serviceType, description, port, description));
    }

    public static void main(String[] args) throws IOException {
        new PegasusService("_http._tcp.local.", 8080, "Pegasus");
    }
}
