package org.trianacode.taskgraph.interceptor.execution;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 21, 2010
 */

public class ExecutionBus {

    public static final String SEND_ID = "org.trianacode.taskgraph.interceptor.execution.ExecutionBus.SendId";
    public static final String RECEIVE_ID = "org.trianacode.taskgraph.interceptor.execution.ExecutionBus.ReceiveId";

    private static Map<String, ExecutionQueue> queues = new ConcurrentHashMap<String, ExecutionQueue>();

    private ExecutionBus() {
    }

    public static void addQueue(ExecutionQueue queue) {
        queues.put(queue.getName(), queue);
    }

    public static void removeQueue(ExecutionQueue queue) {
        queues.remove(queue.getName());
    }

    public static ExecutionQueue getQueue(String name) {
        return queues.get(name);
    }

}
