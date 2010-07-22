package org.trianacode.taskgraph.interceptor.execution;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskGraphException;
import org.trianacode.taskgraph.interceptor.Interceptor;
import org.trianacode.taskgraph.interceptor.InterceptorChain;
import org.trianacode.taskgraph.service.SchedulerException;
import org.trianacode.taskgraph.service.TrianaExec;
import org.trianacode.taskgraph.util.ExtensionFinder;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 22, 2010
 */

public class ExecutionController {

    private Task task;
    private ExecutionControlListener listener;
    private Task currentTask = null;
    private ExecutionQueue queue;
    private TrianaExec exec;

    private Executor executor = Executors.newFixedThreadPool(3);

    public ExecutionController(Task task, ExecutionControlListener listener) {
        this.task = task;
        this.listener = listener;
        initExtensions();
    }

    private void initExtensions() {
        List ext = new ArrayList<Class>();

        ext.add(Interceptor.class);
        Map<Class, List<Object>> en = ExtensionFinder.services(ext);
        Set<Class> keys = en.keySet();
        for (Class key : keys) {
            if (key.equals(Interceptor.class)) {
                List<Object> exts = en.get(key);
                for (Object o : exts) {
                    Interceptor e = (Interceptor) o;
                    InterceptorChain.register(e);
                }
            }
        }
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
        if (currentTask != null) {
            System.out.println("ExecutionController.resume " + currentTask.getToolName());
            try {
                queue.putReadyTask(currentTask);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            executor.execute(new RunThread());
        }
    }

    private class RunThread implements Runnable {

        @Override
        public void run() {
            System.out.println("ExecutionController$RunThread.run ENTER");
            try {
                currentTask = queue.getBlockingTask();
                if (currentTask instanceof PoisonTask) {
                    System.out.println("ExecutionController$RunThread.run RECEIVED POISON TASK. JOB DONE");
                    currentTask = null;
                    listener.executionComplete(task);
                } else {
                    listener.executionSuspended(currentTask);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
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
                    Thread.sleep(1000);
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
