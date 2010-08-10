package org.trianacode.taskgraph.databus.packet;

import java.io.Serializable;
import java.net.URL;

/**
 * Simple wrapper for passing URLs between workflow units that allowfor adding extra metadata to be included along with
 * the transfer.  For now, we just provide a hint to whether that data should be deleted after use for garbage
 * colleciton.
 * <p/>
 * All initial planned DatabusInterfaces should implement a garbage colleciton mechanims. Initial ones are:
 * <p/>
 * 1. A local store (using a hashtable but exposed using REST also) 2. HTTP using the same mechanism - HTTP needs a
 * cleanup method http://server/triana/cleanup?dataid say 3. Attic, which should support the deletion of data from the
 * distributed data store.
 * <p/>
 * We'll use serialisation of this object for now but JSON later.
 * <p/>
 * User: Ian Taylor Date: Jul 24, 2010 Time: 12:42:20 PM To change this template use File | Settings | File Templates.
 */
public class WorkflowDataPacket implements Serializable {
    URL dataLocation;
    boolean deleteAfterUse;

    public WorkflowDataPacket(URL dataLocation, boolean deleteAfterUse) {
        this.dataLocation = dataLocation;
        this.deleteAfterUse = deleteAfterUse;
    }

    public URL getDataLocation() {
        return dataLocation;
    }

    public void setDataLocation(URL dataLocation) {
        this.dataLocation = dataLocation;
    }

    public boolean isDeleteAfterUse() {
        return deleteAfterUse;
    }

    public void setDeleteAfterUse(boolean deleteAfterUse) {
        this.deleteAfterUse = deleteAfterUse;
    }
}
