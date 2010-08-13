package org.trianacode.taskgraph.databus;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.UUID;

import org.trianacode.taskgraph.databus.packet.WorkflowDataPacket;

/**
 * Simple local implementation of datstore.  Local objects are converted into a local URL which can be used to uniquely
 * identify both the type of the data and the data itself. User: scmijt Date: Jul 23, 2010 Time: 3:15:41 PM To change
 * this template use File | Settings | File Templates.
 */
public class LocalDataBus implements DataBusInterface {

    public static final String LOCAL_PROTOCOL = "local";

    private static HashMap<WorkflowDataPacket, Serializable> datastore = new HashMap();

    public LocalDataBus() {
    }

    @Override
    public String getProtocol() {
        return LOCAL_PROTOCOL;
    }

    public void putData(WorkflowDataPacket url, Serializable data) {
        datastore.put(url, data);
    }

    public Serializable get(WorkflowDataPacket packet) throws DataNotResolvableException {
        System.out.println("Getting WorkflowDataPacket from store: " + packet.getDataLocation());
        Serializable s = datastore.get(packet);
        if (s == null) {
            throw new DataNotResolvableException("No data for packet:" + packet.getDataLocation());
        }
        if (packet.isDeleteAfterUse()) {
            datastore.remove(packet);
        }
        return s;
    }

    public void remove(WorkflowDataPacket packet) {
        datastore.remove(packet);
    }


    public WorkflowDataPacket addObject(Serializable data, boolean deleteAfterUse) {
        //String identifier = Integer.toString(new Random(seed).nextInt());
        // Andrew will cringe, but it'll work :)

        // hahaha - I did indeed :-)
        // because it always creates the same identifier!!

        WorkflowDataPacket packet;

        try {
            packet = new WorkflowDataPacket(new URI("local:" + UUID.randomUUID().toString()), deleteAfterUse);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
        // lob the doofer into the whotsit.
        putData(packet, data);
        return packet;
    }
}
