package org.trianacode.pegasus.dax;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Nov 1, 2010
 * Time: 2:49:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class DaxSettingObject implements Serializable {
    String fullOutput = "";
    private HashMap objectHash = new HashMap();

    public DaxSettingObject(){}

    public void addObject(String name, Object object){
        objectHash.put(name, object);
    }

    public void addFullOutput(String full){
        this.fullOutput = full;
    }

    public HashMap getHashMap(){
        return objectHash;
    }

    public String[] getKeys(){
        return new String[]{""};
    }

    public int getNumberFiles(){
        if(objectHash.containsKey("files")){
            try{
                return Integer.parseInt(objectHash.get("files").toString());
            }catch(Exception e){
                e.printStackTrace();
                return 1;
            }
        }
        return 1;
    }

    public void clear(){
        objectHash.clear();
    }

}
