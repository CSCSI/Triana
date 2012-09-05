package org.trianacode.shiwaall.string;

import java.io.Serializable;

// TODO: Auto-generated Javadoc
/**
 * The Class CounterPattern.
 *
 * @author Andrew Harrison
 * @version 1.0.0 Jul 15, 2010
 */

public class CounterPattern implements StringPattern, Serializable {
    
    /** The serial version uid. */
    private static long serialVersionUID = -1;
    

    /** The count. */
    private int count;
    
    /** The pad. */
    private String pad = "";
    
    /** The increment. */
    private int increment = 1;
    
    /** The increment interval. */
    private int incrementInterval;
    
    /** The curr count. */
    private int currCount = 0;

    /**
     * Instantiates a new counter pattern.
     *
     * @param count the count
     * @param padding the padding
     * @param increment the increment
     * @param incrementInterval the increment interval
     */
    public CounterPattern(int count, int padding, int increment, int incrementInterval) {
        this.count = count;
        for (int i = 0; i < padding; i++) {
            pad += "0";
        }
        this.increment = increment;
        this.incrementInterval = incrementInterval;
    }

    /**
     * Instantiates a new counter pattern.
     *
     * @param count the count
     * @param padding the padding
     * @param increment the increment
     */
    public CounterPattern(int count, int padding, int increment) {
        this(count, padding, increment, 1);
    }

    /**
     * Instantiates a new counter pattern.
     *
     * @param padding the padding
     * @param increment the increment
     */
    public CounterPattern(int padding, int increment) {
        this(0, padding, increment, 1);
    }

    /**
     * Instantiates a new counter pattern.
     *
     * @param padding the padding
     */
    public CounterPattern(int padding) {
        this(0, padding, 1, 1);
    }

    /**
     * Instantiates a new counter pattern.
     */
    public CounterPattern() {
        this(0, 4, 1, 1);
    }

    /* (non-Javadoc)
     * @see org.trianacode.shiwaall.string.StringPattern#resetCount()
     */
    public void resetCount(){
        count = 0;
    }

    /* (non-Javadoc)
     * @see org.trianacode.shiwaall.string.StringPattern#next()
     */
    public String next() {
        String cs = Integer.toString(count);
        currCount++;
        if(currCount % incrementInterval == 0) {
            count += increment;
        }
        int left = pad.length() - cs.length();
        if (left <= 0) {
            return cs;
        }
        return pad.substring(0, left) + cs;
    }
}
