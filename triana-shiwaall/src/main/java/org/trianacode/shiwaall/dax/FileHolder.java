package org.trianacode.shiwaall.dax;

// TODO: Auto-generated Javadoc
/**
 * The Class FileHolder.
 *
 * @author Andrew Harrison
 * @version 1.0.0 Jul 16, 2010
 */

public class FileHolder {

    /** The type. */
    private String type;
    
    /** The real name. */
    private String realName;

    /**
     * Instantiates a new file holder.
     *
     * @param type the type
     */
    public FileHolder(String type) {
        this.type = type;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Gets the real name.
     *
     * @return the real name
     */
    public String getRealName() {
        return realName;
    }

    /**
     * Sets the real name.
     *
     * @param realName the new real name
     */
    public void setRealName(String realName) {
        this.realName = realName;
    }
}
