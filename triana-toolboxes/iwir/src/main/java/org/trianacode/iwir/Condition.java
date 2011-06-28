package org.trianacode.iwir;

/**
 * @author Andrew Harrison
 * @version 1.0.0 25/06/2011
 */
public interface Condition {

    /**
     * called on each iteration.
     * If no data is returned, then the iteration will cease
     *
     * @param current
     * @param data
     * @return
     */
    public Object[] iterate(int current, Object[] data);

}
