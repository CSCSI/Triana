package org.trianacode.enactment.io;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Oct 2, 2010
 */
public class IoConfiguration {

    private String toolName;
    private String toolVersion;

    private IoMapping[] inputs;

    public IoConfiguration(String toolName, String toolVersion, IoMapping... inputs) {
        this.toolName = toolName;
        this.toolVersion = toolVersion;
        this.inputs = inputs;
    }

    public IoConfiguration(IoMapping... inputs) {
        this(null, null, inputs);
    }

    public IoConfiguration(String toolName, IoMapping... inputs) {
        this(toolName, null, inputs);
    }

    public String getToolName() {
        return toolName;
    }

    public String getToolVersion() {
        return toolVersion;
    }

    public IoMapping[] getInputs() {
        return inputs;
    }
}
