package org.trianacode.taskgraph.interceptor.execution;

import java.util.logging.Logger;

import org.trianacode.taskgraph.Node;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskGraphContext;
import org.trianacode.taskgraph.interceptor.Interceptor;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 20, 2010
 */

public class ExecutionInterceptor implements Interceptor {

    private static Logger log = Logger.getLogger(ExecutionInterceptor.class.getName());

    @Override
    public String getName() {
        return "EXECUTION_INTERCEPTOR";
    }

    @Override
    public boolean canMediate(Node sendNode, Node receiveNode) {
        return false;
    }

    @Override
    public Object interceptSend(Node sendNode, Node receiveNode, Object data) {
        log.info("ENTER");
        return intercept(sendNode, receiveNode, data, true);
    }

    @Override
    public Object interceptReceive(Node sendNode, Node receiveNode, Object data) {
        log.info("ENTER");
        return intercept(sendNode, receiveNode, data, false);
    }

    protected Object intercept(Node sendNode, Node receiveNode, Object data, boolean send) {
        Task task;
        if (send) {
            task = sendNode.getTask();
        } else {
            task = receiveNode.getTask();
        }
        Task parent = task;
        while (parent != null) {
            parent = parent.getParent();
        }
        TaskGraphContext context = parent.getContext();
        if (context == null) {
            return data;
        }
        String id;
        if (send) {
            id = (String) context.getProperty(ExecutionBus.SEND_ID);
        } else {
            id = (String) context.getProperty(ExecutionBus.RECEIVE_ID);
        }
        if (id == null) {
            return data;
        }
        ExecutionQueue queue = ExecutionBus.getQueue(id);
        if (queue == null) {
            return data;
        }
        try {
            queue.putBlockingTask(task);
            task = queue.getReadyTask();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return data;
    }
}
