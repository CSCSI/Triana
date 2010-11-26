package org.trianacode.gui.main.organize;

import org.trianacode.taskgraph.Task;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Nov 25, 2010
 * Time: 9:14:18 AM
 * To change this template use File | Settings | File Templates.
 */
public class DaxGroupObject {
    
    private int level;
    private int row;
    private Task task;

    public void setLevel(int level) {
        this.level = level;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getLevel() {
        return level;
    }

    public int getRow() {
        return row;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Task getTask() {
        return task;
    }
}
