package org.trianacode.pegasus.dax;

import org.trianacode.gui.main.imp.MainTrianaTask;
import org.trianacode.taskgraph.Task;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Sep 10, 2010
 * Time: 2:45:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class DaxJobTrianaTask extends MainTrianaTask {

    Task task;

    public DaxJobTrianaTask(Task task) {
        super(task);
        this.task = task;
    }

    private Task getTask(){
        return task;
    }

    public void paintComponent(Graphics g){
        Color c = g.getColor();

        boolean collection = (Boolean)getTask().getParameter("collection");

        if(collection){
            g.setColor(Color.red.darker());
            g.fillRoundRect(5, 0, getSize().width - 5, getSize().height - 5, 5, 10);
            g.setColor(Color.red);
            g.fillRoundRect(0, 5, getSize().width - 5, getSize().height - 5, 5, 10);
        }else{
            g.setColor(Color.red);
            g.fillRoundRect(0, 0, getSize().width, getSize().height, 5, 10);
        }

        g.setColor(c);
        paintProcessProgress(g);

    }
}
