package org.trianacode.shiwaall.string;

import java.io.Serializable;

// TODO: Auto-generated Javadoc
/**
 * The Class CharSequencePattern.
 *
 * @author Andrew Harrison
 * @version 1.0.0 Jul 15, 2010
 */

public class CharSequencePattern implements StringPattern, Serializable {

    /** The serial version uid. */
    private static long serialVersionUID = -1;



    /** The sequence. */
    private CharSequence sequence;

    /**
     * Instantiates a new char sequence pattern.
     *
     * @param sequence the sequence
     */
    public CharSequencePattern(CharSequence sequence) {
        this.sequence = sequence;
    }

    /* (non-Javadoc)
     * @see org.trianacode.shiwaall.string.StringPattern#next()
     */
    public String next() {
        return sequence.toString();
    }

    /* (non-Javadoc)
     * @see org.trianacode.shiwaall.string.StringPattern#resetCount()
     */
    @Override
    public void resetCount() {

    }
}
