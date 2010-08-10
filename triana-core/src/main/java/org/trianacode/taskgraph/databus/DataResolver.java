package org.trianacode.taskgraph.databus;

import java.io.Serializable;

import org.trianacode.taskgraph.databus.packet.WorkflowDataPacket;

/**
 * Simple data resolver that goes through the resolvers and attempts to return the data...
 * <p/>
 * User: scmijt Date: Jul 23, 2010 Time: 3:31:17 PM To change this template use File | Settings | File Templates.
 */
public class DataResolver {
    private WorkflowDataPacket workflowDataToResolve;
    private Serializable data;

    public DataResolver(WorkflowDataPacket workflowDataToResolve) {
        this.workflowDataToResolve = workflowDataToResolve;
        process();
    }


    private void process() {
        DataBus resolvers = DataBus.getDataBus();

        for (DatabusInterface store : resolvers) {
            data = store.get(workflowDataToResolve);
            if (data != null) {
                break;
            }
        }
    }

    public Serializable getResult() throws DataNotResolvableException {
        if (data == null) {
            throw new DataNotResolvableException("ERROR: " + workflowDataToResolve.toString() + " not found");
        }
        return data;
    }
}
