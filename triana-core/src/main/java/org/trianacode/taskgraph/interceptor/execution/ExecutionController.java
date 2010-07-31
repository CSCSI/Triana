package org.trianacode.taskgraph.interceptor.execution;

import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.trianacode.enactment.TrianaExec;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskGraphException;
import org.trianacode.taskgraph.service.SchedulerException;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 22, 2010
 */

public class ExecutionController {

    private Task task;
    private ExecutionControlListener listener;
    private ExecutionQueue queue;
    private TrianaExec exec;
    private BlockingQueue<Task> currTask = new ArrayBlockingQueue<Task>(3);


    private Executor executor = Executors.newFixedThreadPool(3);

    public ExecutionController(Task task, ExecutionControlListener listener) {
        this.task = task;
        this.listener = listener;
    }

    public void begin() {
        String executionId = (String) task.getContextProperty(ExecutionBus.RECEIVE_ID);
        if (executionId == null) {
            executionId = UUID.randomUUID().toString();
            task.setContextProperty(ExecutionBus.RECEIVE_ID, executionId);
        }
        queue = new ExecutionQueue(executionId);
        ExecutionBus.addQueue(queue);
        try {
            exec = new TrianaExec(task);
            executor.execute(new DoneThread(exec));
            exec.run(new Object[0]);
            executor.execute(new RunThread());
        } catch (TaskGraphException e) {
            e.printStackTrace();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        try {
            Task currentTask = currTask.take();
            System.out.println("ExecutionController.resume " + currentTask.getToolName());
            queue.putReadyTask(currentTask);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class RunThread implements Runnable {

        @Override
        public void run() {
            System.out.println("ExecutionController$RunThread.run ENTER");
            while (true) {
                try {
                    Task curr = queue.getBlockingTask();
                    if (curr instanceof PoisonTask) {
                        System.out.println("ExecutionController$RunThread.run RECEIVED POISON TASK. JOB DONE");
                        listener.executionComplete(task);
                        break;
                    } else {
                        System.out.println(
                                "ExecutionController$RunThread.run RECEIVED TASK " + curr.getToolName());
                        currTask.put(curr);
                        listener.executionSuspended(curr);

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class DoneThread extends Thread {

        private TrianaExec exec;

        private DoneThread(TrianaExec exec) {
            this.exec = exec;
        }

        @Override
        public void run() {

            while (!exec.isFinished()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                queue.putBlockingTask(new PoisonTask());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
