package org.trianacode.enactment.io;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Oct 2, 2010
 */
public class IoType {

    private String value;
    private String type;

    public IoType(String value, String type) {
        this.value = value;
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public String getType() {
        return type;
    }
}
