package org.trianacode.annotation;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Feb 11, 2011
 */
public class AbstractNodeAware implements NodeAware {

    private int inputCount = 0;
    private int outputCount = 0;

    @Override
    public int getInputNodeCount() {
        return inputCount;
    }

    @Override
    public int getOutputNodeCount() {
        return outputCount;
    }

    @Override
    public void setInputNodeCount(int count) {
        this.inputCount = count;
    }

    @Override
    public void setOutputNodeCount(int count) {
        this.outputCount = count;
    }

}
