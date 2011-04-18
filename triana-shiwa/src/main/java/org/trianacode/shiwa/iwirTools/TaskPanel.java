package org.trianacode.shiwa.iwirTools;

import org.trianacode.gui.panels.ParameterPanel;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 14/04/2011
 * Time: 17:33
 * To change this template use File | Settings | File Templates.
 */
public class TaskPanel extends ParameterPanel {
    @Override
    public void init() {
        getTask().setParameter("taskName", this.getTask().getToolName());

    }

    @Override
    public void reset() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void dispose() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
