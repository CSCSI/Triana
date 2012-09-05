package org.trianacode.shiwaall.dax;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: Feb 4, 2011
 * Time: 2:13:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class ArgBuilder {

    /** The input files. */
    ArrayList<String> inputFiles;
    
    /** The output files. */
    ArrayList<String> outputFiles;
    
    /** The input switch. */
    String inputSwitch;
    
    /** The output switch. */
    String outputSwitch;
    
    /** The arg string. */
    String argString;


    /**
     * Instantiates a new arg builder.
     */
    public ArgBuilder() {
        argString = "";
        inputSwitch = "";
        outputFiles = new ArrayList<String>();
        inputFiles = new ArrayList<String>();
        outputSwitch = "";
    }

    /**
     * Adds the input file.
     *
     * @param filename the filename
     */
    public void addInputFile(String filename) {
        inputFiles.add(filename);
    }

    /**
     * Gets the inputs.
     *
     * @return the inputs
     */
    private String getInputs() {
        String inputs = "";
        for (String inputFile : inputFiles) {
            inputs += inputFile + " ";
        }
        return inputs;
    }

    /**
     * Adds the output file.
     *
     * @param filename the filename
     */
    public void addOutputFile(String filename) {
        outputFiles.add(filename);
    }

    /**
     * Gets the outputs.
     *
     * @return the outputs
     */
    private String getOutputs() {
        String outputs = "";
        for (String outputFile : outputFiles) {
            outputs += outputFile + " ";
        }
        return outputs;
    }

    /**
     * Sets the arg string.
     *
     * @param argument the new arg string
     */
    public void setArgString(String argument) {
        argString = argument;
    }

    /**
     * Sets the input switch.
     *
     * @param inSwitch the new input switch
     */
    public void setInputSwitch(String inSwitch) {
        inputSwitch = inSwitch;
    }

    /**
     * Sets the output switch.
     *
     * @param outSwitch the new output switch
     */
    public void setOutputSwitch(String outSwitch) {
        outputSwitch = outSwitch;
    }

    /**
     * Gets the arg string.
     *
     * @return the arg string
     */
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
