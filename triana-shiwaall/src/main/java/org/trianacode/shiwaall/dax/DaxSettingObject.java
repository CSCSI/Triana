package org.trianacode.shiwaall.dax;

import java.io.Serializable;
import java.util.HashMap;

// TODO: Auto-generated Javadoc
/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: Nov 1, 2010
 * Time: 2:49:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class DaxSettingObject implements Serializable {
    
    /** The full output. */
    String fullOutput = "";
    
    /** The object hash. */
    private HashMap objectHash = new HashMap();

    /**
     * Instantiates a new dax setting object.
     */
    public DaxSettingObject() {
    }

    /**
     * Instantiates a new dax setting object.
     *
     * @param map the map
     */
    public DaxSettingObject(HashMap map) {
        putHashMap(map);
    }

    /**
     * Adds the object.
     *
     * @param name the name
     * @param object the object
     */
    public void addObject(String name, Object object) {
        objectHash.put(name, object);
    }

    /**
     * Adds the full output.
     *
     * @param full the full
     */
    public void addFullOutput(String full) {
        this.fullOutput = full;
    }

    /**
     * Gets the hash map.
     *
     * @return the hash map
     */
    public HashMap getHashMap() {
        return objectHash;
    }

    /**
     * Put hash map.
     *
     * @param map the map
     */
    public void putHashMap(HashMap map) {
        objectHash = map;
    }

    /**
     * Gets the keys.
     *
     * @return the keys
     */
    public String[] getKeys() {
        return new String[]{""};
    }

    /**
     * Gets the file names.
     *
     * @return the file names
     */
    public String getFileNames() {
        if (objectHash.containsKey(ExecUnit.namesOfFiles)) {
            try {
                return objectHash.get(ExecUnit.namesOfFiles).toString();
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }
        return "";
    }

    /**
     * Gets the number files.
     *
     * @return the number files
     */
    public int getNumberFiles() {
        if (objectHash.containsKey(ExecUnit.numberOfFiles)) {
            try {
                return Integer.parseInt(objectHash.get(ExecUnit.numberOfFiles).toString());
            } catch (Exception e) {
                e.printStackTrace();
                return 1;
            }
        }
        return 1;
    }

    /**
     * Clear.
     */
    public void clear() {
        objectHash.clear();
    }

}
