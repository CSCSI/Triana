package org.trianacode.config.cl;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Oct 1, 2010
 */
public class Option {

    private String shortOpt;
    private String longOpt;
    private String value;
    private String description;
    private boolean multiple = false;

    public Option(String shortOpt, String longOpt, String description) {
        this(shortOpt, longOpt, null, description);
    }

    public Option(String shortOpt, String description) {
        this(shortOpt, "", "", description);
    }

    public Option(String shortOpt, String longOpt, String value, String description) {
        this(shortOpt, longOpt, value, description, false);
    }

    public Option(String shortOpt, String longOpt, String value, String description, boolean multiple) {
        if (shortOpt == null || shortOpt.length() == 0) {
            throw new IllegalArgumentException("Option must have a short option");
        }
        if (longOpt == null) {
            longOpt = "";
        }
        if (description == null) {
            description = "no description";
        }
        this.shortOpt = shortOpt;
        this.longOpt = longOpt;
        this.value = value;
        this.description = description;
        this.multiple = false;
    }

    public String getShortOpt() {
        return shortOpt;
    }

    public String getValue() {
        return value;
    }

    public String getLongOpt() {
        return longOpt;
    }

    public String getDescription() {
        return description;
    }

    public boolean isMultiple() {
        return multiple;
    }
}
