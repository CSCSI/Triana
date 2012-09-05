package org.trianacode.shiwaall.string;

import java.io.Serializable;

// TODO: Auto-generated Javadoc
/**
 * The Class AlphabetPattern.
 *
 * @author Andrew Harrison
 * @version 1.0.0 Jul 15, 2010
 */

public class AlphabetPattern implements StringPattern, Serializable{

    /** The serial version uid. */
    private static long serialVersionUID = -1;


    /** The start. */
    private int start;
    
    /** The current. */
    private int current;
    
    /** The increment interval. */
    private int incrementInterval;
    
    /** The curr count. */
    private int currCount = 0;

    /**
     * Instantiates a new alphabet pattern.
     *
     * @param uppercase the uppercase
     * @param incrementInterval the increment interval
     */
    public AlphabetPattern(boolean uppercase, int incrementInterval) {
        if(uppercase) {
            start = 65;
        } else {
            start = 97;
        }
        current = start;
        this.incrementInterval = incrementInterval;
    }

    /**
     * Instantiates a new alphabet pattern.
     */
    public AlphabetPattern() {
        this(false, 1);
    }

    /**
     * Instantiates a new alphabet pattern.
     *
     * @param uppercase the uppercase
     */
    public AlphabetPattern(boolean uppercase) {
        this(uppercase, 1);
    }

    /**
     * Instantiates a new alphabet pattern.
     *
     * @param incrementInterval the increment interval
     */
    public AlphabetPattern(int incrementInterval) {
        this(false, incrementInterval);
    }

    /* (non-Javadoc)
     * @see org.trianacode.shiwaall.string.StringPattern#next()
     */
    public String next() {
        String ret = new String(new char[]{(char)current});
        currCount++;
        if(currCount % incrementInterval == 0) {
            current++;
        }
        if(current - 26 >= start) {
            current = start;
        }
        return ret;
    }

    /* (non-Javadoc)
     * @see org.trianacode.shiwaall.string.StringPattern#resetCount()
     */
    @Override
    public void resetCount() {
        
    }
}
