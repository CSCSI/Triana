package org.trianacode.shiwaall.sendToPegasus;


import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: Jan 19, 2011
 * Time: 2:24:36 PM
 * To change this template use File | Settings | File Templates.
 *
 * @see PegasusEvent
 */
public class PegasusListener implements ServiceListener {

    /** The jmdns. */
    JmDNS jmdns;
    
    /** The mdns_type. */
    String mdns_type;
    
    /** The services. */
    private ArrayList<ServiceInfo> services;

    /**
     * Instantiates a new pegasus listener.
     *
     * @param jmdns the jmdns
     * @param mdns_type the mdns_type
     */
    public PegasusListener(JmDNS jmdns, String mdns_type) {
        this.jmdns = jmdns;
        this.mdns_type = mdns_type;
        services = new ArrayList<ServiceInfo>();
    }

    /**
     * Gets the services.
     *
     * @return the services
     */
    public ArrayList getServices() {
        return services;
    }

    /* (non-Javadoc)
     * @see javax.jmdns.ServiceListener#serviceAdded(javax.jmdns.ServiceEvent)
     */
    public void serviceAdded(ServiceEvent event) {
        log("\nService added : " + event.getName());
        refreshList();
    }

    /* (non-Javadoc)
     * @see javax.jmdns.ServiceListener#serviceResolved(javax.jmdns.ServiceEvent)
     */
    public void serviceResolved(ServiceEvent event) {
        log("\nService resolved : " + event.getName());
        listInfo(event);
        refreshList();
    }

    /* (non-Javadoc)
     * @see javax.jmdns.ServiceListener#serviceRemoved(javax.jmdns.ServiceEvent)
     */
    public void serviceRemoved(ServiceEvent event) {
        log("\nService removed : " + event.getName());
        listInfo(event);
        refreshList();
    }

    /**
     * Found something.
     *
     * @return true, if successful
     */
    public boolean foundSomething() {
        if (services.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * List info.
     *
     * @param event the event
     */
    private void listInfo(ServiceEvent event) {
        ServiceInfo info = event.getInfo();
        System.out.println("Event : " + event.getName() +
                "\nType : " + event.getType() +
                "\nHost : " + info.getHostAddress() +
                "\nURL : " + info.getURL() +
                "\nApplication : " + info.getApplication() +
                "\nAddress : " + info.getInetAddress() +
                "\nPort : " + info.getPort() +
                "\nProtocol : " + info.getProtocol() +
                "\nServer : " + info.getServer() +
                "\nNiceString : " + info.getNiceTextString()
        );
    }

    /**
     * Refresh list.
     */
    private void refreshList() {
        ServiceInfo[] infos = jmdns.list(mdns_type);
        if (infos != null && infos.length > 0) {
            ArrayList<ServiceInfo> temp = new ArrayList<ServiceInfo>();
            for (int i = 0; i < infos.length; i++) {
                temp.add(infos[i]);
            }
            services = temp;
        }
    }

    /**
     * Log.
     *
     * @param s the s
     */
    private void log(String s) {
        ;
        System.out.println(s);
    }
}
