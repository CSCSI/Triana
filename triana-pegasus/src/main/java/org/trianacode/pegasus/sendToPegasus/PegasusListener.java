package org.trianacode.pegasus.sendToPegasus;


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

    public void serviceAdded(ServiceEvent event) {
        log("\nService added : " + event.getName());
        refreshList();
    }

    public void serviceResolved(ServiceEvent event) {
        log("\nService resolved : " + event.getName());
        listInfo(event);
        refreshList();
    }

    public void serviceRemoved(ServiceEvent event) {
        log("\nService removed : " + event.getName());
        listInfo(event);
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

    private void listInfo(ServiceEvent event){
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

    private void refreshList(){
        ServiceInfo[] infos = jmdns.list(mdns_type);
        if (infos != null && infos.length > 0) {
            ArrayList<ServiceInfo> temp = new ArrayList<ServiceInfo>();
            for (int i = 0; i < infos.length; i++) { temp.add(infos[i]);}
            services = temp;
        }
    }

    private void log(String s){;
        System.out.println(s);
    }
}
