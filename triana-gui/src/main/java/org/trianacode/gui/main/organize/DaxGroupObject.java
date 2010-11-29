package org.trianacode.gui.main.organize;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Nov 28, 2010
 * Time: 6:39:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class DaxGroupObject {
    private int level;
    private String toolname;
    private ArrayList<DaxUnitObject> tasks = new ArrayList();

    public int getLevel() {
        return level;
    }

    public String getToolname() {
        return toolname;
    }

    public void addTask(DaxUnitObject duo) {
        tasks.add(duo);
    }
}
