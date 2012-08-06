package org.trianacode.shiwaall.dax;

import org.trianacode.shiwaall.string.StringPattern;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 17, 2010
 */

public class FileCreator {

    private StringPattern pattern;

    public FileCreator(StringPattern pattern) {
        this.pattern = pattern;
    }

    public FileCreator() {
    }

    public StringPattern getPattern() {
        return pattern;
    }

    public void setPattern(StringPattern pattern) {
        this.pattern = pattern;
    }
}
