package org.trianacode.taskgraph.databus;

import java.util.HashMap;
import java.util.Map;

/**
 * The databus accepts a number of data stores that are capable of storing and retrieving objects using their URL as an
 * identifier.   Thus, objects are stored:
 * <p/>
 * put(url, object)
 * <p/>
 * into the store.
 * <p/>
 * Once the data has been retrieved
 * <p/>
 * User: scmijt Date: Jul 23, 2010 Time: 4:30:01 PM To change this template use File | Settings | File Templates.
 */
public class DataBus {

    private static Map<String, DataBusInterface> databuses = new HashMap<String, DataBusInterface>();

    public static DataBusInterface registerDataBus(DataBusInterface databus) {
        return databuses.put(databus.getProtocol(), databus);
    }

    public static DataBusInterface deregisterDataBus(DataBusInterface databus) {
        return databuses.remove(databus.getProtocol());
    }

    /**
     * Gets a databus for a particular protocol e.g. local, http
     *
     * @param protocol
     * @return
     */
    public static DataBusInterface getDataBus(String protocol) {
        if (protocol == null) {
            return null;
        }
        return databuses.get(protocol);
    }
}
