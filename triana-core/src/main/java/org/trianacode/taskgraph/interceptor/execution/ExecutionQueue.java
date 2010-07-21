package org.trianacode.taskgraph.interceptor.execution;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import org.trianacode.taskgraph.Task;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 21, 2010
 */

public class ExecutionQueue {

    private String name;
    private BlockingQueue<Task> blockingTasks = new SynchronousQueue<Task>();
    private BlockingQueue<Task> readyTasks = new SynchronousQueue<Task>();

    public ExecutionQueue(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void putBlockingTask(Task task) throws InterruptedException {
        blockingTasks.put(task);
    }

    public Task getBlockingTask() throws InterruptedException {
        return blockingTasks.poll(60L, TimeUnit.SECONDS);
    }

    public void putReadyTask(Task task) throws InterruptedException {
        readyTasks.put(task);
    }

    public Task getReadyTask() throws InterruptedException {
        return readyTasks.poll(60L, TimeUnit.SECONDS);
    }

}
