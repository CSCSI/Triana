package org.trianacode.pegasus.dax;

import org.trianacode.gui.main.imp.MainTrianaTask;
import org.trianacode.taskgraph.Task;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Sep 9, 2010
 * Time: 1:39:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class DaxFileTrianaTask extends MainTrianaTask {

    //  private FileTrianaTaskDecoration docView = new FileTrianaTaskDecoration();
    Task task;
    /**
     * Constructs a new MainTrianaTask for viewing the specified task
     */
    public DaxFileTrianaTask(Task task) {
        super(task);
        this.task = task;
        setSize(450, 450);

        //   docView.setVisible(true);
        //   add(docView);
    }

    public Task getTask(){
        return task;
    }

    public void paintComponent(Graphics g){
        Color c = g.getColor();

        if(isCollection()){
            g.setColor(Color.cyan.darker());
            g.fillRoundRect(5, 0, getSize().width - 5, getSize().height - 5, 5, 10);
            g.setColor(Color.cyan);
            g.fillRoundRect(0, 5, getSize().width - 5, getSize().height - 5, 5, 10);

            g.setColor(Color.black);
            g.drawRoundRect(0, 5, getSize().width - 5, getSize().height - 6, 5, 10);
        }else{
            g.setColor(Color.cyan);
            g.fillRoundRect(0, 0, getSize().width, getSize().height, 5, 10);

            g.setColor(Color.black);
            g.drawRoundRect(0, 0, getSize().width-1, getSize().height-1, 5, 10);
        }


        g.setColor(c);
        paintProcessProgress(g);
    }

    private boolean isCollection(){
        Object o = getTask().getParameter("collection");
        if(o.equals("true")){
            return true;
        }else{
            return false;
        }
    }
}
