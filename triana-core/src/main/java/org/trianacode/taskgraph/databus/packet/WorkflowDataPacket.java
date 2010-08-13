package org.trianacode.taskgraph.databus.packet;

import java.io.Serializable;
import java.net.URI;

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

    private URI dataLocation;
    private boolean deleteAfterUse;

    public WorkflowDataPacket(URI dataLocation, boolean deleteAfterUse) {
        this.dataLocation = dataLocation;
        this.deleteAfterUse = deleteAfterUse;
    }

    public String getProtocol() {
        if (dataLocation != null) {
            return dataLocation.getScheme();
        }
        return null;
    }

    public URI getDataLocation() {
        return dataLocation;
    }

    public boolean isDeleteAfterUse() {
        return deleteAfterUse;
    }

    public void setDeleteAfterUse(boolean deleteAfterUse) {
        this.deleteAfterUse = deleteAfterUse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        WorkflowDataPacket that = (WorkflowDataPacket) o;

        if (dataLocation != null ? !dataLocation.equals(that.dataLocation) : that.dataLocation != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return dataLocation != null ? dataLocation.hashCode() : 0;
    }
}
