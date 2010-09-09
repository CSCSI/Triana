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

    /**
     * Constructs a new MainTrianaTask for viewing the specified task
     */
    public DaxFileTrianaTask(Task task) {
        super(task);
        setSize(450, 450);

     //   docView.setVisible(true);
     //   add(docView);
    }

    public void paintComponent(Graphics g){
        Color c = Color.red;
        g.setColor(Color.green);
        g.fillOval(0, 0, getSize().width, getSize().height);
        g.setColor(c);
       // g.fill3DRect(0, 0, getSize().width, getSize().height, !isSelected());
    }

//    public void mouseEntered(MouseEvent e){}
}
