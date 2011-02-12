package org.trianacode.annotation;

/**
 * Interface that wants to know a little about it's name and how many connections it has.
 * The setters are called before the process method so the values can be retrieved in that method.
 * NOTE: these properties become available within the annotated class's @Process method
 *
 * @author Andrew Harrison
 * @version 1.0.0 Feb 11, 2011
 */
public interface TaskAware {

    public String getTaskName();

    public void setTaskName(String name);

    public String getTaskSubtitle();

    public void setTaskSubtitle(String subtitle);

    public int getInputNodeCount();

    public int getOutputNodeCount();

    public void setInputNodeCount(int count);

    public void setOutputNodeCount(int count);

}
