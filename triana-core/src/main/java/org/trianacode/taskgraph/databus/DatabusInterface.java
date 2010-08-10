package org.trianacode.taskgraph.databus;

import java.io.Serializable;

import org.trianacode.taskgraph.databus.packet.WorkflowDataPacket;

/**
 * Interface for defining new databus component for storing and retrieving data that act as an interface between sending
 * and receing data in Triana.
 * <p/>
 * User: scmijt Date: Jul 23, 2010 Time: 3:15:00 PM To change this template use File | Settings | File Templates.
 */
public interface DatabusInterface {

    /**
     * Puts data into the databus
     *
     * @param packet
     * @param data
     */
    public void putData(WorkflowDataPacket packet, Serializable data);

    /**
     * Gets the data out of the store with the provided WorkflowDataPacket
     *
     * @param packet
     * @return
     */
    public Serializable get(WorkflowDataPacket packet);

    /**
     * Removes the URL and the data from the store.
     *
     * @param packet
     */
    public void remove(WorkflowDataPacket packet);

    /**
     * Adds an object to the databus and returns the WorkflowDataPacket that can be used to identify the object.
     *
     * @param data
     * @return
     */
    public WorkflowDataPacket addObject(Serializable data, boolean deleteAfterUse);


}
