package org.trianacode.taskgraph.databus;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Random;

import org.trianacode.taskgraph.databus.packet.WorkflowDataPacket;

/**
 * Simple local implementation of datstore.  Local objects are converted into a local URL which can be used to uniquely
 * identify both the type of the data and the data itself. User: scmijt Date: Jul 23, 2010 Time: 3:15:41 PM To change
 * this template use File | Settings | File Templates.
 */
public class LocalDataBus implements DatabusInterface {
    private static HashMap<WorkflowDataPacket, Serializable> datastore = datastore = new HashMap();
    String TRIANA_PROTOCOL = "triana";

    int seed = new Random().nextInt();

    public LocalDataBus() {
    }

    public void putData(WorkflowDataPacket url, Serializable data) {
        datastore.put(url, data);
    }

    public Serializable get(WorkflowDataPacket packet) {
        System.out.println("Getting WorkflowDataPacket from store: " + packet.toString());
        return datastore.get(packet);
    }

    public void remove(WorkflowDataPacket packet) {
        datastore.remove(packet);
    }


    public WorkflowDataPacket addObject(Serializable data, boolean deleteAfterUse) {
        String datatype = data.getClass().getName();
        String identifier = Integer.toString(new Random(seed).nextInt()); // Andrew will cringe, but it'll work :)

        WorkflowDataPacket packet = null;

        String host = "localhost";

        try { // probably better ...
            host = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        try {
            packet = new WorkflowDataPacket(new URL("http", host, 8080, datatype + "-" + identifier), deleteAfterUse);
        } catch (MalformedURLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        // Here we Add to restless server once we hook that in.


        // lob the doofer into the whotsit.

        putData(packet, data);

        return packet;
    }
}
