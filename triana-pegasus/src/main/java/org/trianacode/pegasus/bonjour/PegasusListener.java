package org.trianacode.pegasus.bonjour;

import org.apache.commons.logging.Log;
import org.trianacode.enactment.logging.Loggers;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Jan 19, 2011
 * Time: 2:24:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class PegasusListener implements ServiceListener {

    JmDNS jmdns;
    String mdns_type;
    private ArrayList<ServiceInfo> services;

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
    public void serviceRemoved(ServiceEvent event) {
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
            ArrayList<ServiceInfo> temp = new ArrayList<ServiceInfo>();
            for (int i = 0; i < infos.length; i++) { temp.add(infos[i]);}
            services = temp;
        }
    }

    private void log(String s){
        Log log = Loggers.DEV_LOGGER;
        log.debug(s);
        System.out.println(s);
    }
}
