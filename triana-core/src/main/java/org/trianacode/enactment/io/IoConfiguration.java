package org.trianacode.enactment.io;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Oct 2, 2010
 */
public class IoConfiguration {

    private String toolName;
    private String toolVersion;

    private List<IoMapping> inputs = new ArrayList<IoMapping>();
    private List<IoMapping> outputs = new ArrayList<IoMapping>();

    public IoConfiguration(String toolName, String toolVersion, List<IoMapping> inputs, List<IoMapping> outputs) {
        this.toolName = toolName;
        this.toolVersion = toolVersion;
        if (inputs != null) {
            this.inputs = inputs;
        }
        if (outputs != null) {
            this.outputs = outputs;
        }
    }

    public IoConfiguration(List<IoMapping> inputs, List<IoMapping> outputs) {
        this(null, null, inputs, outputs);
    }

    public IoConfiguration() {
        this(null, null, null, null);
    }

    public IoConfiguration(String toolName, List<IoMapping> inputs, List<IoMapping> outputs) {
        this(toolName, null, inputs, outputs);
    }

    public IoConfiguration(String toolName, String version) {
        this(toolName, version, null, null);
    }

    public IoConfiguration(String toolName) {
        this(toolName, null, null, null);
    }

    public String getToolName() {
        return toolName;
    }

    public String getToolVersion() {
        return toolVersion;
    }

    public List<IoMapping> getInputs() {
        return inputs;
    }

    public void addOutput(IoMapping mapping) {
        outputs.add(mapping);
    }

    public void addInput(IoMapping mapping) {
        inputs.add(mapping);
    }

    public List<IoMapping> getOutputs() {
        System.out.println("IOMappings : " + Arrays.toString(outputs.toArray()));
        return outputs;
    }
}
