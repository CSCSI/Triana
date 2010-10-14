package org.trianacode.pegasus.string;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 15, 2010
 */

public class AlphabetPattern implements StringPattern {

    private int start;
    private int current;
    private int incrementInterval;
    private int currCount = 0;

    public AlphabetPattern(boolean uppercase, int incrementInterval) {
        if(uppercase) {
            start = 65;
        } else {
            start = 97;
        }
        current = start;
        this.incrementInterval = incrementInterval;
    }

    public AlphabetPattern() {
        this(false, 1);
    }

    public AlphabetPattern(boolean uppercase) {
        this(uppercase, 1);
    }

    public AlphabetPattern(int incrementInterval) {
        this(false, incrementInterval);
    }

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

    @Override
    public void resetCount() {
        
    }
}
