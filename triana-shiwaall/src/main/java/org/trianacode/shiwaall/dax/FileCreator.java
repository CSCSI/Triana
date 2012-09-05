package org.trianacode.shiwaall.dax;

import org.trianacode.shiwaall.string.StringPattern;

// TODO: Auto-generated Javadoc
/**
 * The Class FileCreator.
 *
 * @author Andrew Harrison
 * @version 1.0.0 Jul 17, 2010
 */

public class FileCreator {

    /** The pattern. */
    private StringPattern pattern;

    /**
     * Instantiates a new file creator.
     *
     * @param pattern the pattern
     */
    public FileCreator(StringPattern pattern) {
        this.pattern = pattern;
    }

    /**
     * Instantiates a new file creator.
     */
    public FileCreator() {
    }

    /**
     * Gets the pattern.
     *
     * @return the pattern
     */
    public StringPattern getPattern() {
        return pattern;
    }

    /**
     * Sets the pattern.
     *
     * @param pattern the new pattern
     */
    public void setPattern(StringPattern pattern) {
        this.pattern = pattern;
    }
}
