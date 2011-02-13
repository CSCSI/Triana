package org.trianacode.taskgraph.annotation;

import java.lang.reflect.Method;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Feb 13, 2011
 */
public class MethodDescriptor {


    private Method method;
    private String[] inputs;
    private String[] outputs;
    private boolean gather;
    private boolean flatten;
    private boolean isArray = false;

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String[] getInputs() {
        return inputs;
    }

    public void setInputs(String[] inputs) {
        this.inputs = inputs;
    }

    public String[] getOutputs() {
        return outputs;
    }

    public void setOutputs(String[] outputs) {
        this.outputs = outputs;
    }

    public boolean isGather() {
        return gather;
    }

    public void setGather(boolean gather) {
        this.gather = gather;
    }

    public boolean isFlatten() {
        return flatten;
    }

    public void setFlatten(boolean flatten) {
        this.flatten = flatten;
    }

    public boolean isArray() {
        return isArray;
    }

    public void setArray(boolean array) {
        isArray = array;
    }
}
