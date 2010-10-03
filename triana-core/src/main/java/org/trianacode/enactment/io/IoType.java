package org.trianacode.enactment.io;

/**
 * If an IoType is a reference, then the value is URI.
 * The value of an IoType can be null if represents just a type, or an output.
 *
 * @author Andrew Harrison
 * @version 1.0.0 Oct 2, 2010
 */
public class IoType {

    private String value;
    private String type;
    private boolean reference;

    public IoType(String value, String type) {
        this(value, type, false);
    }

    public IoType(String type) {
        this(null, type, false);
    }

    public IoType(String value, String type, boolean reference) {
        this.value = value;
        this.type = type;
        this.reference = reference;
    }

    public String getValue() {
        return value;
    }

    public String getType() {
        return type;
    }

    public boolean isReference() {
        return reference;
    }
}
