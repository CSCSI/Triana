package org.trianacode.annotation;

/**
 * Interface that wants to know a little about how many connections it has
 *
 * @author Andrew Harrison
 * @version 1.0.0 Feb 11, 2011
 */
public interface NodeAware {

    public int getInputNodeCount();

    public int getOutputNodeCount();

    public void setInputNodeCount(int count);

    public void setOutputNodeCount(int count);

}
