package org.trianacode.pegasus.dax;

import org.trianacode.gui.main.imp.MainTrianaTask;
import org.trianacode.taskgraph.Task;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
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
    Color color = Color.cyan;

    /**
     * Constructs a new MainTrianaTask for viewing the specified task
     */
    public DaxFileTrianaTask(Task task) {
        super(task);
        this.task = task;
        //   docView.setVisible(true);
        //   add(docView);
    }

    public Task getTask(){
        return task;
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Color c = g.getColor();
//        g.setColor(getBackground());
//        g.fillRect(0, 0, getSize().width, getSize().height);

        this.setOpaque(false);

        Color fileUnitColor = color;
        Color shadow = color.darker();
        Color fileUnitShadow = shadow;
        if(this.isSelected()){
            fileUnitColor = color.darker();
            fileUnitShadow = shadow.darker();
        }

        if(isCollection()){
            Border shadowBorder = BorderFactory.createEmptyBorder();
            TitledBorder title = BorderFactory.createTitledBorder(shadowBorder, "" + getNumberOfFiles());
            title.setTitleJustification(TitledBorder.CENTER);
            title.setTitlePosition(TitledBorder.BELOW_BOTTOM);
            this.setBorder(title);

            g.setColor(fileUnitShadow);
            g.fillRoundRect(5, 0, getSize().width - 5, getSize().height - 5, 5, 10);
            g.setColor(fileUnitColor);
            g.fillRoundRect(0, 5, getSize().width - 5, getSize().height - 5, 5, 10);

            g.setColor(Color.black);
            g.drawRoundRect(0, 5, getSize().width - 5, getSize().height - 6, 5, 10);
        }else{
            Border shadowBorder = BorderFactory.createEmptyBorder();
            this.setBorder(shadowBorder);
            g.setColor(fileUnitColor);
            g.fillRoundRect(0, 0, getSize().width, getSize().height, 5, 10);

            g.setColor(Color.black);
            g.drawRoundRect(0, 0, getSize().width-1, getSize().height-1, 5, 10);
        }

        g.setColor(c);
        paintProcessProgress(g);
    }

    private boolean isCollection(){
        Object o = getTask().getParameter("collection");
        if(o.equals(true)){
            return true;
        }else{
            return false;
        }
    }

    private int getNumberOfFiles(){
        Object o = getTask().getParameter("numberOfFiles");
        //     System.out.println("Returned object from param *numberOfFiles* : " + o.getClass().getCanonicalName() + " : " + o.toString());
        if(o != null){
            int value = (Integer)o;
            if(value > 1 ){
                return value;
            }
            return 1;
        }
        return 1;
    }
}
