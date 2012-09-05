package org.trianacode.shiwaall.iwir.execute;

import org.trianacode.enactment.StreamToOutput;
import org.trianacode.taskgraph.Node;

import java.io.File;
import java.util.HashMap;

// TODO: Auto-generated Javadoc
/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 06/02/2012
 * Time: 13:16
 * To change this template use File | Settings | File Templates.
 */
public class ExecuteProcess implements ExecutableInterface {

    /** The executable url. */
    private String executableURL;
    
    /** The input switch. */
    private String inputSwitch = "";
    
    /** The output switch. */
    private String outputSwitch = "";
    
    /** The variable string. */
    private String variableString = "";
    
    /** The working directory. */
    private File workingDirectory = null;
    
    /** The inputs. */
    private Object[] inputs;
    
    /** The outputs. */
    private Object[] outputs;

    /**
     * Instantiates a new execute process.
     *
     * @param executableURL the executable url
     */
    public ExecuteProcess(String executableURL) {
        this.executableURL = executableURL;
    }

    /* (non-Javadoc)
     * @see org.trianacode.shiwaall.iwir.execute.ExecutableInterface#run()
     */
    @Override
    public void run() {
        runExec();
    }

    /* (non-Javadoc)
     * @see org.trianacode.shiwaall.iwir.execute.ExecutableInterface#run(java.util.HashMap, java.lang.Object[])
     */
    @Override
    public void run(HashMap<Node, Object> inputObjectAtNodeMap, Object[] outputs) {
        //To change body of implemented methods use File | Settings | File Templates.

    }

//    @Override
//    public void run(Object[] inputs) {
//        this.inputs = inputs;
//        runExec();
//    }
//
//    @Override
//    public void run(Object[] inputs, Object[] outputs) {
//        this.inputs = inputs;
//        this.outputs = outputs;
//        runExec();
//    }

    /**
 * Gets the input switch.
 *
 * @return the input switch
 */
public String getInputSwitch() {
        return inputSwitch;
    }

    /**
     * Sets the input switch.
     *
     * @param inputSwitch the new input switch
     */
    public void setInputSwitch(String inputSwitch) {
        this.inputSwitch = inputSwitch;
    }

    /**
     * Gets the output switch.
     *
     * @return the output switch
     */
    public String getOutputSwitch() {
        return outputSwitch;
    }

    /**
     * Sets the output switch.
     *
     * @param outputSwitch the new output switch
     */
    public void setOutputSwitch(String outputSwitch) {
        this.outputSwitch = outputSwitch;
    }

    /**
     * Gets the variable string.
     *
     * @return the variable string
     */
    public String getVariableString() {
        return variableString;
    }

    /**
     * Sets the variable string.
     *
     * @param variableString the new variable string
     */
    public void setVariableString(String variableString) {
        this.variableString = variableString;
    }

    /**
     * Sets the working directory.
     *
     * @param directory the new working directory
     */
    public void setWorkingDirectory(String directory) {
        File dir = new File(directory);
        if (dir.exists() && dir.isDirectory()) {
            workingDirectory = dir;
        }
    }

    /**
     * Gets the working directory.
     *
     * @return the working directory
     */
    public File getWorkingDirectory() {
        return workingDirectory;
    }

    /**
     * Run exec.
     */
    private void runExec() {
        String args = buildArgs();

        try {
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(args, new String[]{}, workingDirectory);
            StreamToOutput err = new StreamToOutput(process.getErrorStream(), executableURL + "_err");
            err.start();
            StreamToOutput out = new StreamToOutput(process.getInputStream(), executableURL + "_std.out");
            out.start();

            int returnCode = process.waitFor();
            System.out.println("Runtime process finished with code " + returnCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Builds the args.
     *
     * @return the string
     */
    private String buildArgs() {
        String inputString = "";

        if (inputs != null) {
            inputString += inputSwitch + " ";
            for (Object input : inputs) {
                if (input instanceof String) {
                    inputString += input + " ";
                }
            }
        }

        String outputString = "";
        if (outputs != null) {
            outputString += outputSwitch + " ";
            for (Object output : outputs) {
                if (output instanceof String) {
                    outputString += output + " ";
                }
            }
        }

        return executableURL + " " + inputString + outputString + variableString;
    }
}
