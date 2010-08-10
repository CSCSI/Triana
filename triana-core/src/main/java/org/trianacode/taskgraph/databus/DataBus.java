package org.trianacode.taskgraph.databus;

import java.util.ArrayList;

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
public class DataBus extends ArrayList<DatabusInterface> {

    public enum DataBusType {
        LOCAL_HTTP, ATTIC
    }

    ;

    private static DataBus databuses = new DataBus();

    static LocalDataBus local = new LocalDataBus();

    static {
        // register new databuses here
        databuses.add(local); // local imp by default - add others as and when we add them...
    }


    /**
     * Gets the databus objects that are registered on this VM.
     *
     * @return
     */
    public static DataBus getDataBus() {
        return databuses;
    }

    /**
     * Gets a databus for a particular databus type e.g. local, http
     *
     * @param type
     * @return
     */
    public static DatabusInterface getDataBusFor(DataBusType type) {
        if (type.equals(DataBusType.LOCAL_HTTP)) {
            return local;
        } else {
            return null;
        }
    }
}
