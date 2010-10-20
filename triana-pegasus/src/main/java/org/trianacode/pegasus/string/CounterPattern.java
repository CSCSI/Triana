package org.trianacode.pegasus.string;

import java.io.Serializable;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 15, 2010
 */

public class CounterPattern implements StringPattern, Serializable {
    private static long serialVersionUID = -1;
    

    private int count;
    private String pad = "";
    private int increment = 1;
    private int incrementInterval;
    private int currCount = 0;

    public CounterPattern(int count, int padding, int increment, int incrementInterval) {
        this.count = count;
        for (int i = 0; i < padding; i++) {
            pad += "0";
        }
        this.increment = increment;
        this.incrementInterval = incrementInterval;
    }

    public CounterPattern(int count, int padding, int increment) {
        this(count, padding, increment, 1);
    }

    public CounterPattern(int padding, int increment) {
        this(0, padding, increment, 1);
    }

    public CounterPattern(int padding) {
        this(0, padding, 1, 1);
    }

    public CounterPattern() {
        this(0, 4, 1, 1);
    }

    public void resetCount(){
        count = 0;
    }

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
