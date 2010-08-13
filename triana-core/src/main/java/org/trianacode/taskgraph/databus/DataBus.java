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

    private static Map<String, DatabusInterface1> databuses = new HashMap<String, DatabusInterface1>();

    static LocalDataBus local = new LocalDataBus();

    static {
        // local imp by default - add others as and when we add them...
        registerDataBus(local);
    }

    public static DatabusInterface1 registerDataBus(DatabusInterface1 databus) {
        return databuses.put(databus.getProtocol(), databus);
    }

    public static DatabusInterface1 deregisterDataBus(DatabusInterface1 databus) {
        return databuses.remove(databus.getProtocol());
    }

    /**
     * Gets a databus for a particular protocol e.g. local, http
     *
     * @param protocol
     * @return
     */
    public static DatabusInterface1 getDataBus(String protocol) {
        if (protocol == null) {
            return null;
        }
        return databuses.get(protocol);
    }
}
