package org.trianacode.pegasus.dax;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 16, 2010
 */

public class FileHolder {

    private String type;
    private String realName;

    public FileHolder(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }
}
