package org.trianacode.shiwaall.dax;

import java.util.List;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 16, 2010
 */

public class FileType {

    private String type;
    private List<String> names;

    public FileType(String type, List<String> names) {
        this.type = type;
        this.names = names;
    }

    public String getType() {
        return type;
    }

    public List<String> getNames() {
        return names;
    }
}
