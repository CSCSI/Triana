package org.trianacode.shiwaall.iwir.logic;

// TODO: Auto-generated Javadoc
/**
 * The Interface Condition.
 *
 * @author Andrew Harrison
 * @version 1.0.0 25/06/2011
 */
public interface Condition {

    /**
     * called on each iteration.
     * If no data is returned, then the iteration will cease
     *
     * @param current the current
     * @param data the data
     * @return the object[]
     */
    public Object[] iterate(int current, Object[] data);

}
