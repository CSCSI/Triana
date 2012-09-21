package org.trianacode.shiwaall.iwir.execute;

import org.apache.commons.io.FileUtils;
import org.shiwa.fgi.iwir.AbstractPort;
import org.trianacode.enactment.StreamToOutput;
import org.trianacode.taskgraph.Node;
import org.trianacode.taskgraph.Task;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
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
//    private transient HashMap<String, String> nodeNameToPortName;

    /** The task name. */
    private String taskName = "";

    /** The input port to file map. */
//    private transient HashMap<InputPort, File> inputPortToFileMap;

    /** The output port to file map. */
//    private transient HashMap<OutputPort, File> outputPortToFileMap;

    private String JSDLstring = "";

//    private Tool tool = null;

    //transient, as Executable node can't be serialised
    private transient ArrayList<ExecutableNode> executableNodes;


    // Used only when saving and loading these tasks, don't use them otherwise
    private HashMap<Integer, String> inputNodeNumberToFilename = new HashMap<Integer, String>();
    private HashMap<Integer, String> outputNodeNumberToFilename = new HashMap<Integer, String>();

    /**
     * Instantiates a new executable.
     *
     * @param taskType the task type
     */
    public Executable(String taskType) {
        this.taskType = taskType;
//        nodeNameToPortName = new HashMap<String, String>();
//        inputPortToFileMap = new HashMap<InputPort, File>();
//        outputPortToFileMap = new HashMap<OutputPort, File>();

        executableNodes = new ArrayList<ExecutableNode>();
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

//        System.out.println(nodeNameToPortName.toString());
//        System.out.println(inputPortToFileMap.toString());

        for(Node node : inputs.keySet()){
            File executableInputFile = getInputFileForNode(node);

            Object input = inputs.get(node);

            if(input instanceof String){
                try {
                    File file = new File((String) input);
                    if(file.exists()){
                        System.out.println("Executable input : " + executableInputFile);
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
//    public HashMap<String, String> getPorts() {
//        return nodeNameToPortName;
//    }

    /**
     * Adds the port.
     *
     * @param nodeName the node name
     * @param portName the port name
     */
//    public void addPort(String nodeName, String portName) {
//        System.out.println(nodeName + " maps to " + portName);
//        nodeNameToPortName.put(nodeName, portName);
//    }

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
//    public void addInputFile(InputPort inputPort, File inputFile) {
//        inputPortToFileMap.put(inputPort, inputFile);
//    }

    /**
     * Gets the input file for node.
     *
     * @param node the node
     * @return the input file for node
     */
    private File getInputFileForNode(Node node){
//        System.out.println("Looking for a file for " + node.getBottomLevelNode().getName());
//        String portName = nodeNameToPortName.get(node.getBottomLevelNode().getName());
//        if(portName != null){
//            System.out.println("IWIR port " + portName);
//            for(InputPort inputPort : inputPortToFileMap.keySet()){
//                if(inputPort.getName().equals(portName)){
//                    File file = inputPortToFileMap.get(inputPort);
//                    System.out.println("Input node to file : " + file.getAbsolutePath());
//                    return file;
//                }
//            }
//        }

//        ArrayList<ExecutableNode> execNodes = getExecutableNodes(true);
        for(ExecutableNode executableNode : executableNodes) {
            if(executableNode.getNode() != null) {
//                System.out.println(node + " " + executableNode.getNode());

                //TODO ouch
                if(executableNode.getNode().getName().equals(node.getName())){
                    String filename = executableNode.getFilename();
                    if(filename != null && !filename.equals("")) {
                        return new File(workingDir, filename);
                    }
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
//        String portName = nodeNameToPortName.get(node.getBottomLevelNode().getName());
//        if(portName != null) {
//            System.out.println("IWIR output port " + portName);
//            for(OutputPort outputPort : outputPortToFileMap.keySet()){
//                if(outputPort.getName().equals(portName)){
//                    File file = outputPortToFileMap.get(outputPort);
//                    System.out.println("Output node to file : " + file.getAbsolutePath());
//                    return file;
//                }
//            }
//        }

//        ArrayList<ExecutableNode> execNodes = getExecutableNodes(false);
        for(ExecutableNode executableNode : executableNodes) {
            if(executableNode.getNode() != null) {
                if(executableNode.getNode().getName().equals(node.getName())){
                    String filename = executableNode.getFilename();
                    if(filename != null && !filename.equals("")) {
                        return new File(workingDir, filename);
                    }
                }
            }
        }
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    /**
     * Adds the output file.
     *
     //     * @param outputPort the output port
     //     * @param outputFile the output file
     */
//    public void addOutputFile(OutputPort outputPort, File outputFile) {
//        outputPortToFileMap.put(outputPort, outputFile);
//    }

    @Override
    public String toString(){
        return primaryExec;
    }

    public void setJSDLstring(String JSDLstring) {
        this.JSDLstring = JSDLstring;
    }

    public String getJSDLstring() {
        return JSDLstring;
    }

//    public void setTool(Tool tool) {
//        this.tool = tool;
//    }

//    public Tool getTool() {
//        return tool;
//    }


    private void updatedExeceutableNode() {
        System.out.println("Updated Executable node" + Arrays.toString(executableNodes.toArray()));

        Task task = null;
        //get a the task without having to remember a reference to it
        //keeps things tidy and serializable
        for(ExecutableNode executableNode : executableNodes){
            if(executableNode.getNode() != null){
                task = executableNode.getNode().getTask();
            }
        }

        if(task != null) {
            for(Node node : task.getInputNodes()) {
                File file = getOutputFileForNode(node);
                if(file != null){
                    inputNodeNumberToFilename.put(node.getNodeIndex(), file.getName());
                }
            }
            System.out.println("in node to file : " + inputNodeNumberToFilename.toString());

            for(Node node : task.getOutputNodes()) {
                File file = getOutputFileForNode(node);
                if(file != null) {
                    outputNodeNumberToFilename.put(node.getNodeIndex(), file.getName());
                }
            }
            System.out.println("out node to file : " + outputNodeNumberToFilename.toString());
        }
    }

    private boolean firstTime = true;
    //Called to rebuild the executableNode objects after deserializing
    public void init(Task task) {

//        System.out.println("Task " + task + " init, for some reason.");

        if(firstTime) {
            firstTime = !firstTime;
        } else {
            System.out.println("\nSecond init, loading from serials");
            executableNodes = new ArrayList<ExecutableNode>();

            confirmWorkingDir();


            System.out.println("in node to file : " + inputNodeNumberToFilename.toString());
            System.out.println("out node to file : " + outputNodeNumberToFilename.toString());

            for(Integer integer : inputNodeNumberToFilename.keySet()) {
                Node node = task.getInputNode(integer);
                if(node != null) {
                    addExecutableNodeMapping(node, inputNodeNumberToFilename.get(integer));
                }
            }
            for(Integer integer : outputNodeNumberToFilename.keySet()) {
                Node node = task.getOutputNode(integer);
                if(node != null) {
                    addExecutableNodeMapping(node, outputNodeNumberToFilename.get(integer));
                }
            }
        }
    }

    private void confirmWorkingDir() {

        if(workingDir == null || !workingDir.exists()) {
            System.out.println("Task " + taskName + " is missing.");
            if(!primaryExec.equals("")) {
                File file = new File(primaryExec);
                if(file.exists()) {
                    workingDir = new File(file.getParent());
                }
            } else {
                String userDir = System.getenv("user.dir");
                if(userDir != null) {
                    workingDir = new File(userDir);
                }
            }
            System.out.println("Working dir for "
                    + taskName + " set to " + workingDir.getAbsolutePath());
        }
    }

    private void addExecutableNode(ExecutableNode executableNode) {
        executableNodes.add(executableNode);
        updatedExeceutableNode();
        System.out.println("Node added : " + executableNode);
    }

    public void addExecutableNodeMapping(Node node, String fileName) {
        System.out.println("\nnode, file");
        boolean update = false;
        for(ExecutableNode executableNode : executableNodes){
            if(executableNode.getNode() == node){
                executableNode.setNode(node);
                executableNode.setFilename(fileName);
                update = true;
            }
        }
        if(!update){
            addExecutableNode(new ExecutableNode(node, fileName));
        } else {
            updatedExeceutableNode();
        }
    }

    public void addExecutableNodeMapping(Node node, AbstractPort abstractPort) {
        System.out.println("\nnode, port");
        boolean update = false;
        for(ExecutableNode executableNode : executableNodes){
            if(executableNode.getNode() == node || executableNode.getAbstractPort() == abstractPort){
                executableNode.setNode(node);
                executableNode.setAbstractPort(abstractPort);
                update = true;
            }
        }
        if(!update){
            addExecutableNode(new ExecutableNode(node, abstractPort));
        } else {
            updatedExeceutableNode();
        }
    }

    public void addExecutableNodeMapping(AbstractPort abstractPort, String fileName) {
        System.out.println("\nport, name");
        boolean update = false;
        for(ExecutableNode executableNode : executableNodes){
            if(executableNode.getAbstractPort() == abstractPort){
                executableNode.setFilename(fileName);
                executableNode.setAbstractPort(abstractPort);
                update = true;
            }
        }
        if(!update){
            addExecutableNode(new ExecutableNode(abstractPort, fileName));
        } else {
            updatedExeceutableNode();
        }
    }

    public ArrayList<ExecutableNode> getInputNodes(){
        return getExecutableNodes(true);
    }

    private ArrayList<ExecutableNode> getExecutableNodes(boolean inputs) {
        ArrayList<ExecutableNode> returning = new ArrayList<ExecutableNode>();

        if(executableNodes == null){
            executableNodes = new ArrayList<ExecutableNode>();
        }

        for(ExecutableNode executableNode : executableNodes){
            if(executableNode.getNode() != null){
                if(executableNode.getNode().isInputNode() == inputs){
                    returning.add(executableNode);
                }
            } else if(executableNode.getAbstractPort() != null){
                if(executableNode.getAbstractPort().isInputPort() == inputs){
                    returning.add(executableNode);
                }
            }
        }
        return returning;
    }

    public ArrayList<ExecutableNode> getOutputNodes(){
        return getExecutableNodes(false);
    }

    public void removeExecutableNode(Node node) {
        ArrayList<ExecutableNode> removing = new ArrayList<ExecutableNode>();
        for(ExecutableNode executableNode : executableNodes) {
            if(executableNode.getNode() == node) {
                removing.add(executableNode);
            }
            if(executableNode.getNode().getName().equals(node.getName())){
                removing.add(executableNode);
            }
        }
        for(ExecutableNode executableNode : removing) {
            executableNodes.remove(executableNode);
        }
        updatedExeceutableNode();
    }

    public ArrayList<File> getWorkingDirFileWhichAreNotInputsOrOutputs() {
        ArrayList<File> returnedFiles = new ArrayList<File>();
        if(workingDir.exists() && workingDir.isDirectory()) {
            File[] files = workingDir.listFiles();
            for(File file : files) {
                boolean add = !isInputOrOutput(file);
                if(add && file.isFile()) {
                    System.out.println("Adding to bundle " + file.getAbsolutePath());
                    returnedFiles.add(file);
                }
            }
        }
        return returnedFiles;
    }

    private boolean isInputOrOutput(File file) {
        for(ExecutableNode executableNode : executableNodes) {
            String filename = executableNode.getFilename();
            if(filename.equals(file.getName())){
                return true;
            }
        }
        return false;
    }
}
