package org.trianacode.gui.panels;

import org.trianacode.error.ErrorEvent;
import org.trianacode.error.ErrorListener;
import org.trianacode.error.ErrorTracker;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.gui.windows.ErrorDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 23/08/2011
 * Time: 10:51
 * To change this template use File | Settings | File Templates.
 */
public class MsgBoxErrorListener implements ErrorListener {
    @Override
    public void errorOccurred(ErrorEvent errorEvent) {
        ErrorDialog.show(GUIEnv.getApplicationFrame(), errorEvent.getErrorMsg(), errorEvent.getThrown());
    }

    @Override
    public List<String> listenerInterest() {
        List<String> interest = new ArrayList<String>();
        interest.add(ErrorTracker.ALLERRORS);
        return interest;
    }
}
