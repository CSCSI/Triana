package org.trianacode.gui.util.organize;

import org.trianacode.taskgraph.Task;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Nov 23, 2010
 */
public class Gridbox {

    private int x;
    private int y;
    private double width = 0;
    private double height = 0;
    private Task task;

    public Gridbox(int x, int y, Task task) {
        this.x = x;
        this.y = y;
        this.task = task;
        if (task != null) {
            int ins = task.getInputNodeCount();
            if (ins > 3) {
                width += ins * 0.2;
                height += ins * 0.2;
            }
            int outs = task.getOutputNodeCount();
            if (outs > 3) {
                int diff = outs - ins;
                if (diff > 0) {
                    width += diff * 0.2;
                    height += diff * 0.2;
                }
                //x += width;
            }
            int name = task.getToolName().length();
            if (name > 10) {
                int diff = name - 10;
                width += diff * 0.5;
            }

        }
        System.out.println("Gridbox.Gridbox:" + task + " x:" + x + " y:" + y + " width:" + width + " height:" + height);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public Task getTask() {
        return task;
    }
}
