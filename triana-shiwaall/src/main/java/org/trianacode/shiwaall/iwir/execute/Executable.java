package org.trianacode.shiwaall.iwir.execute;

import org.trianacode.enactment.StreamToOutput;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 24/06/2011
 * Time: 14:56
 * To change this template use File | Settings | File Templates.
 */
public class Executable implements ExecutableInterface, Serializable {

    private String taskType;
    public static final String TASKTYPE = "taskType";
    public static final String EXECUTABLE = "executable";
    private String[] args = new String[0];
    private File workingDir = null;
    private String primaryExec = "";

    private HashMap<String, String> ports;
    private String taskName = "";

    public Executable(String taskType) {
        System.out.println("New Executable " + taskType);
        this.taskType = taskType;
        ports = new HashMap<String, String>();
    }

    private void runProcess() {
        try{
            ProcessBuilder pb = new ProcessBuilder(args);
            if(workingDir != null){
                pb.directory(workingDir);
            }
            Process process = pb.start();

            System.out.println("Running " + process.toString());
            new StreamToOutput(process.getErrorStream(), "err").start();
            new StreamToOutput(process.getInputStream(), "std.out").start();

            int returnCode = process.waitFor();
            System.out.println("Runtime process finished with code " + returnCode);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void run() {
        System.out.println("TaskType : " + taskType);
        System.out.println("Running with no inputs or outputs");
        runProcess();
    }

    public void run(Object[] inputs) {
        System.out.println("TaskType : " + taskType);
        System.out.println("Running with inputs");
        for (int i = 0; i < inputs.length; i++) {
            Object input = inputs[i];
            System.out.println(input);
        }
        runProcess();
    }

    public void run(Object[] inputs, Object[] outputs) {
        System.out.println("TaskType : " + taskType);
        System.out.println("Running with inputs, producing outputs");
        for (int i = 0; i < outputs.length; i++) {
            String output = "";

            for (int j = 0; j < inputs.length; j++) {
                Object input = inputs[j];
                System.out.println(input);
                output += input.toString();
            }

            outputs[i] = output;
        }
        runProcess();
    }

    public String getTaskType() {
        return taskType;
    }

    public void setWorkingDir(File workingDir) {
        this.workingDir = workingDir;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public File getWorkingDir() {
        return workingDir;
    }

    public String[] getArgs() {
        return args;
    }

    public void setPrimaryExec(String primaryExec) {
        this.primaryExec = primaryExec;
    }

    public String getPrimaryExec() {
        return primaryExec;
    }


    public HashMap<String, String> getPorts() {
        return ports;
    }

    public void addPort(String nodeName, String portName) {
        ports.put(nodeName, portName);
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskName() {
        return taskName;
    }
}
