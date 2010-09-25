package org.trianacode.taskgraph.interceptor.execution;

import java.io.File;
import java.io.FileReader;

import org.trianacode.TrianaInstance;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.ser.XMLReader;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 22, 2010
 */

public class ExecutionControlTest implements ExecutionControlListener {

    private ExecutionController controller;
    private Task task;

    public ExecutionControlTest(Task task) {
        this.task = task;
        this.controller = new ExecutionController(task, this);
    }

    public static void main(String[] args) throws Exception {

        File file = new File(args[0]);

        TrianaInstance engine=null;
        try {
            engine = new TrianaInstance(args, true, null);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            System.exit(1);
        }
        
        XMLReader reader = new XMLReader(new FileReader(file));
        ExecutionControlTest test = new ExecutionControlTest((Task) reader.readComponent());
        test.start();

    }

    public void start() {
        controller.begin();
    }

    public void executionSuspended(Task task) {
        System.out.println("ExecutionTest.executionSuspended FOR TASK:" + task.getToolName());
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        controller.resume();
        System.out.println("ExecutionTest.executionSuspended resumed execution");

    }

    public void executionComplete(Task task) {
        System.out.println("ExecutionTest.executionComplete TASK HAS FINISHED");

    }
}
