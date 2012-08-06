package org.trianacode.shiwaall.dax;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: Nov 1, 2010
 * Time: 2:49:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class DaxSettingObject implements Serializable {
    String fullOutput = "";
    private HashMap objectHash = new HashMap();

    public DaxSettingObject() {
    }

    public DaxSettingObject(HashMap map) {
        putHashMap(map);
    }

    public void addObject(String name, Object object) {
        objectHash.put(name, object);
    }

    public void addFullOutput(String full) {
        this.fullOutput = full;
    }

    public HashMap getHashMap() {
        return objectHash;
    }

    public void putHashMap(HashMap map) {
        objectHash = map;
    }

    public String[] getKeys() {
        return new String[]{""};
    }

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

    public void clear() {
        objectHash.clear();
    }

}
