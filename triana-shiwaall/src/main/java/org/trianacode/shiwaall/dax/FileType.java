package org.trianacode.shiwaall.dax;

import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class FileType.
 *
 * @author Andrew Harrison
 * @version 1.0.0 Jul 16, 2010
 */

public class FileType {

    /** The type. */
    private String type;
    
    /** The names. */
    private List<String> names;

    /**
     * Instantiates a new file type.
     *
     * @param type the type
     * @param names the names
     */
    public FileType(String type, List<String> names) {
        this.type = type;
        this.names = names;
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
     * Gets the names.
     *
     * @return the names
     */
    public List<String> getNames() {
        return names;
    }
}
