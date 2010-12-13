package org.trianacode.pegasus.dax;

import org.trianacode.taskgraph.annotation.Parameter;
import org.trianacode.taskgraph.annotation.Process;
import org.trianacode.taskgraph.annotation.Tool;

import java.util.HashMap;
import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Dec 10, 2010
 * Time: 1:07:20 PM
 * To change this template use File | Settings | File Templates.
 */

@Tool (panelClass = "org.trianacode.pegasus.dax.SettingObjectOutputPanel")
public class SettingObjectOutput {

    @Parameter
    HashMap map = new HashMap();


    @Process(gather = true)
    public DaxSettingObject process(List in){
        for(Object o : in){
            if(o instanceof DaxSettingObject){
                HashMap thisMap = ((DaxSettingObject) o).getHashMap();
                System.out.println("Incoming map : " + thisMap);
                map.putAll(thisMap);

            }
        }

        DaxSettingObject outputdso = new DaxSettingObject(map);
        System.out.println("Output dso : " + outputdso);        
        return outputdso;

    }

    private void setParam(){
    }
}
