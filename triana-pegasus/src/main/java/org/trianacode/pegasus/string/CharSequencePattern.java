package org.trianacode.pegasus.string;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 15, 2010
 */

public class CharSequencePattern implements StringPattern {

    private CharSequence sequence;

    public CharSequencePattern(CharSequence sequence) {
        this.sequence = sequence;
    }

    public String next() {
        return sequence.toString();
    }
}
