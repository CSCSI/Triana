package org.trianacode.shiwaall.iwir.execute;

import org.apache.commons.io.FileUtils;
import org.shiwa.fgi.iwir.InputPort;
import org.shiwa.fgi.iwir.OutputPort;
import org.trianacode.enactment.StreamToOutput;
import org.trianacode.taskgraph.Node;

import java.io.*;
import java.util.HashMap;

// TODO: Auto-generated Javadoc
/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 24/06/2011
 * Time: 14:56
 * To change this template use File | Settings | File Templates.
 */
public class Executable implements ExecutableInterface, Serializable {

    /** The task type. */
    private String taskType;
    
    /** The Constant TASKTYPE. */
    public static final String TASKTYPE = "taskType";
    
    /** The Constant EXECUTABLE. */
    public static final String EXECUTABLE = "executable";
    
    /** The args. */
    private String[] args = new String[0];
    
    /** The working dir. */
    private File workingDir = null;
    
    /** The primary exec. */
    private String primaryExec = "";

    /** The node name to port name. */
    private HashMap<String, String> nodeNameToPortName;
    
    /** The task name. */
    private String taskName = "";
    
    /** The input port to file map. */
    private HashMap<InputPort, File> inputPortToFileMap;
    
    /** The output port to file map. */
    private HashMap<OutputPort, File> outputPortToFileMap;


    /**
     * Instantiates a new executable.
     *
     * @param taskType the task type
     */
    public Executable(String taskType) {
        this.taskType = taskType;
        nodeNameToPortName = new HashMap<String, String>();
        inputPortToFileMap = new HashMap<InputPort, File>();
        outputPortToFileMap = new HashMap<OutputPort, File>();
    }

    /**
     * Run process.
     */
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

    /* (non-Javadoc)
     * @see org.trianacode.shiwaall.iwir.execute.ExecutableInterface#run()
     */
    public void run() {
        System.out.println("TaskType : " + taskType);
        System.out.println("Running with no inputs or outputs");
        runProcess();
    }


    /**
     * Serialize.
     *
     * @param object the object
     * @param file the file
     */
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

    /* (non-Javadoc)
     * @see org.trianacode.shiwaall.iwir.execute.ExecutableInterface#run(java.util.HashMap, java.lang.Object[])
     */
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

    /**
     * Gets the task type.
     *
     * @return the task type
     */
    public String getTaskType() {
        return taskType;
    }

    /**
     * Sets the working dir.
     *
     * @param workingDir the new working dir
     */
    public void setWorkingDir(File workingDir) {
        this.workingDir = workingDir;
    }

    /**
     * Sets the args.
     *
     * @param args the new args
     */
    public void setArgs(String[] args) {
        this.args = args;
    }

    /**
     * Gets the working dir.
     *
     * @return the working dir
     */
    public File getWorkingDir() {
        return workingDir;
    }

    /**
     * Gets the args.
     *
     * @return the args
     */
    public String[] getArgs() {
        return args;
    }

    /**
     * Sets the primary exec.
     *
     * @param primaryExec the new primary exec
     */
    public void setPrimaryExec(String primaryExec) {
        this.primaryExec = primaryExec;
    }

    /**
     * Gets the primary exec.
     *
     * @return the primary exec
     */
    public String getPrimaryExec() {
        return primaryExec;
    }


    /**
     * Gets the ports.
     *
     * @return the ports
     */
    public HashMap<String, String> getPorts() {
        return nodeNameToPortName;
    }

    /**
     * Adds the port.
     *
     * @param nodeName the node name
     * @param portName the port name
     */
    public void addPort(String nodeName, String portName) {
        System.out.println(nodeName + " maps to " + portName);
        nodeNameToPortName.put(nodeName, portName);
    }

    /**
     * Sets the task name.
     *
     * @param taskName the new task name
     */
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    /**
     * Gets the task name.
     *
     * @return the task name
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * Adds the input file.
     *
     * @param inputPort the input port
     * @param inputFile the input file
     */
    public void addInputFile(InputPort inputPort, File inputFile) {
        inputPortToFileMap.put(inputPort, inputFile);
    }

    /**
     * Gets the input file for node.
     *
     * @param node the node
     * @return the input file for node
     */
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

    /**
     * Gets the output file for node.
     *
     * @param node the node
     * @return the output file for node
     */
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

    /**
     * Adds the output file.
     *
     * @param outputPort the output port
     * @param outputFile the output file
     */
    public void addOutputFile(OutputPort outputPort, File outputFile) {
        outputPortToFileMap.put(outputPort, outputFile);
    }
}
