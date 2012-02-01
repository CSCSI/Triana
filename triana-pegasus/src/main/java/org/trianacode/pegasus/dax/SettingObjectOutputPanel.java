package org.trianacode.pegasus.dax;

import org.apache.commons.logging.Log;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.gui.panels.ParameterPanel;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: Dec 10, 2010
 * Time: 1:09:17 PM
 * To change this template use File | Settings | File Templates.
 */

public class SettingObjectOutputPanel extends ParameterPanel {

    private JTextArea outputArea = new JTextArea();
    private HashMap map = new HashMap();
    private static Log devLog = Loggers.DEV_LOGGER;


    @Override
    public void init() {
        getParams();

        this.setLayout(new BorderLayout());
        outputArea.setRows(10);
        this.add(outputArea, BorderLayout.CENTER);
        this.revalidate();

        JLabel headerLabel = new JLabel("Setting object contains : ");
        add(headerLabel, BorderLayout.NORTH);

        Set mapKeys = map.keySet();

        for (Iterator i = mapKeys.iterator(); i.hasNext(); ) {
            Object o = i.next();

            devLog.debug("Key is of class : " + o.getClass());

            String string = o.toString();
            string += " : " + map.get(o);
            devLog.debug(string);
            outputArea.append("\n" + string);

        }

    }

    private void getParams() {
        Object o = getTask().getParameter("map");
        if (o instanceof HashMap) {
            map = (HashMap) o;
        }
    }

    @Override
    public void reset() {
    }

    @Override
    public void dispose() {
    }
}
