package org.trianacode.pegasus.dax;

import org.trianacode.gui.panels.ParameterPanel;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Dec 10, 2010
 * Time: 1:09:17 PM
 * To change this template use File | Settings | File Templates.
 */

public class SettingObjectOutputPanel extends ParameterPanel {

    private JTextArea outputArea = new JTextArea();
    private HashMap map = new HashMap();

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

        for(Iterator i = mapKeys.iterator(); i.hasNext();){
            Object o = i.next();

            System.out.println("Key is of class : " + o.getClass());

            String string = o.toString();
            string += " : " + map.get(o);
            System.out.println(string);
            outputArea.append("\n" + string);

        }

    }

    private void getParams(){
        Object o = getTask().getParameter("map");
        if(o instanceof HashMap){
            map = (HashMap)o;
        }
    }

    @Override
    public void reset() {
    }

    @Override
    public void dispose() {
    }
}
