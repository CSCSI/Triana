package org.trianacode.shiwaall.iwir.execute;

import org.trianacode.enactment.StreamToOutput;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 06/02/2012
 * Time: 13:16
 * To change this template use File | Settings | File Templates.
 */
public class ExecuteProcess implements ExecutableInterface {

    private String executableURL;
    private String inputSwitch = "";
    private String outputSwitch = "";
    private String variableString = "";
    private File workingDirectory = null;
    private Object[] inputs;
    private Object[] outputs;

    public ExecuteProcess(String executableURL) {
        this.executableURL = executableURL;
    }

    @Override
    public void run() {
        runExec();
    }

    @Override
    public void run(Object[] inputs) {
        this.inputs = inputs;
        runExec();
    }

    @Override
    public void run(Object[] inputs, Object[] outputs) {
        this.inputs = inputs;
        this.outputs = outputs;
        runExec();
    }

    public String getInputSwitch() {
        return inputSwitch;
    }

    public void setInputSwitch(String inputSwitch) {
        this.inputSwitch = inputSwitch;
    }

    public String getOutputSwitch() {
        return outputSwitch;
    }

    public void setOutputSwitch(String outputSwitch) {
        this.outputSwitch = outputSwitch;
    }

    public String getVariableString() {
        return variableString;
    }

    public void setVariableString(String variableString) {
        this.variableString = variableString;
    }

    public void setWorkingDirectory(String directory) {
        File dir = new File(directory);
        if (dir.exists() && dir.isDirectory()) {
            workingDirectory = dir;
        }
    }

    public File getWorkingDirectory() {
        return workingDirectory;
    }

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
