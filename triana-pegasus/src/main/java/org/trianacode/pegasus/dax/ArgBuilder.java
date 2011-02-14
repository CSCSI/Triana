package org.trianacode.pegasus.dax;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Feb 4, 2011
 * Time: 2:13:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class ArgBuilder {

    ArrayList<String> inputFiles;
    ArrayList<String> outputFiles;
    String inputSwitch;
    String outputSwitch;
    String argString;


    public ArgBuilder() {
        argString = "";
        inputSwitch = "";
        outputFiles = new ArrayList<String>();
        inputFiles = new ArrayList<String>();
        outputSwitch = "";
    }

    public void addInputFile(String filename) {
        inputFiles.add(filename);
    }

    private String getInputs() {
        String inputs = "";
        for (String inputFile : inputFiles) {
            inputs += inputFile + " ";
        }
        return inputs;
    }

    public void addOutputFile(String filename) {
        outputFiles.add(filename);
    }

    private String getOutputs() {
        String outputs = "";
        for (String outputFile : outputFiles) {
            outputs += outputFile + " ";
        }
        return outputs;
    }

    public void setArgString(String argument) {
        argString = argument;
    }

    public void setInputSwitch(String inSwitch) {
        inputSwitch = inSwitch;
    }

    public void setOutputSwitch(String outSwitch) {
        outputSwitch = outSwitch;
    }

    public String getArgString() {
        String fullString = argString + " ";

        if (!inputSwitch.equals("")) {
            fullString += inputSwitch + " " +
                    getInputs();
        }
        if (!outputSwitch.equals("")) {
            fullString += outputSwitch + " " +
                    getOutputs();
        }
        return fullString;
    }

}
