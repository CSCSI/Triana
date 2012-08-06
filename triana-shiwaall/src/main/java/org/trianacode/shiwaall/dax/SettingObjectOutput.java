package org.trianacode.shiwaall.dax;

import org.apache.commons.logging.Log;
import org.trianacode.annotation.Parameter;
import org.trianacode.annotation.Process;
import org.trianacode.annotation.Tool;
import org.trianacode.enactment.logging.Loggers;

import java.util.HashMap;
import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: Dec 10, 2010
 * Time: 1:07:20 PM
 * To change this template use File | Settings | File Templates.
 */

@Tool(panelClass = "org.trianacode.org.trianacode.shiwaall.gui.dax.SettingObjectOutputPanel")
public class SettingObjectOutput {
    private static Log devLog = Loggers.DEV_LOGGER;

    @Parameter
    HashMap map = new HashMap();


    @Process(gather = true)
    public DaxSettingObject process(List in) {
        for (Object o : in) {
            if (o instanceof DaxSettingObject) {
                HashMap thisMap = ((DaxSettingObject) o).getHashMap();
                devLog.debug("Incoming map : " + thisMap);
                map.putAll(thisMap);

            }
        }

        DaxSettingObject outputdso = new DaxSettingObject(map);
        devLog.debug("Output dso : " + outputdso);
        return outputdso;

    }

    private void setParam() {
    }
}
