package org.trianacode.shiwaall.iwir.execute;

import org.apache.commons.io.FileUtils;
import org.shiwa.fgi.iwir.InputPort;
import org.shiwa.fgi.iwir.OutputPort;
import org.trianacode.enactment.StreamToOutput;
import org.trianacode.taskgraph.Node;

import java.io.*;
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

    private HashMap<String, String> nodeNameToPortName;
    private String taskName = "";
    private HashMap<InputPort, File> inputPortToFileMap;
    private HashMap<OutputPort, File> outputPortToFileMap;


    public Executable(String taskType) {
        this.taskType = taskType;
        nodeNameToPortName = new HashMap<String, String>();
        inputPortToFileMap = new HashMap<InputPort, File>();
        outputPortToFileMap = new HashMap<OutputPort, File>();
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


    private void serialize(Object object, File file) {

        byte[] data = object.toString().getBytes();

        try {
            ObjectOutputStream outstream = new ObjectOutputStream(new FileOutputStream(file));

            for (int count = 0; count < data.length; count++) {
                outstream.writeObject(data[count]);
            }

            outstream.close();
        } catch (Exception except) {
        }
    }

    public void run(HashMap<Node, Object> inputs, Object[] outputs) {
        System.out.println("TaskType : " + taskType);
        System.out.println("Running with inputs, producing outputs");

        System.out.println(nodeNameToPortName.toString());
        System.out.println(inputPortToFileMap.toString());

        for(Node node : inputs.keySet()){
            File executableInputFile = getInputFileForNode(node);
            Object input = inputs.get(node);

            if(input instanceof String){
                try {
                    File file = new File((String) input);
                    if(file.exists()){
                        System.out.println("Input " + file.getAbsolutePath() + " exists " + file.exists());
                        FileUtils.copyFile(file, executableInputFile);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if(input instanceof File){
                if(((File) input).exists()){
                    try {
                        FileUtils.copyFile((File) input, executableInputFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                serialize(input, executableInputFile);
            }

        }

//        for (int i = 0; i < outputs.length; i++) {
//            String output = "";
//
//            for (int j = 0; j < inputs.length; j++) {
//                Object input = inputs[j];
//                System.out.println(input);
//                output += input.toString();
//            }
//
//            outputs[i] = output;
//        }
        System.out.println("TaskType : " + taskType);
        System.out.println("Running with inputs");
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
        return nodeNameToPortName;
    }

    public void addPort(String nodeName, String portName) {
        System.out.println(nodeName + " maps to " + portName);
        nodeNameToPortName.put(nodeName, portName);
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskName() {
        return taskName;
    }

    public void addInputFile(InputPort inputPort, File inputFile) {
        inputPortToFileMap.put(inputPort, inputFile);
    }

    private File getInputFileForNode(Node node){
        System.out.println("Looking for a file for " + node.getBottomLevelNode().getName());
        String portName = nodeNameToPortName.get(node.getBottomLevelNode().getName());
        if(portName != null){
            System.out.println("IWIR port " + portName);
            for(InputPort inputPort : inputPortToFileMap.keySet()){
                if(inputPort.getName().equals(portName)){
                    File file = inputPortToFileMap.get(inputPort);
                    System.out.println("Input node to file : " + file.getAbsolutePath());
                    return file;
                }
            }
        }
        return null;
    }

    public File getOutputFileForNode(Node node) {
        String portName = nodeNameToPortName.get(node.getBottomLevelNode().getName());
        if(portName != null) {
            System.out.println("IWIR output port " + portName);
            for(OutputPort outputPort : outputPortToFileMap.keySet()){
                if(outputPort.getName().equals(portName)){
                    File file = outputPortToFileMap.get(outputPort);
                    System.out.println("Output node to file : " + file.getAbsolutePath());
                    return file;
                }
            }
        }
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    public void addOutputFile(OutputPort outputPort, File outputFile) {
        outputPortToFileMap.put(outputPort, outputFile);
    }
}
