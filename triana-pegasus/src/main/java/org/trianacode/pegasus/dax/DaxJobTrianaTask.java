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
 * Date: Sep 10, 2010
 * Time: 2:45:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class DaxJobTrianaTask extends MainTrianaTask {

    Task task;
    DaxJobSubtitle sub = new DaxJobSubtitle();

    public DaxJobTrianaTask(Task task) {
        super(task);
        this.task = task;

        sub.setVisible(true);
        add(sub);
        invalidateSize();
        validate();
    }

    private Task getTask(){
        return task;
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Color c = g.getColor();
//        g.setColor(getBackground());
//        g.fillRect(0, 0, getSize().width, getSize().height);

        this.setOpaque(false);

        if(isSelected()){
            if(isCollection()){
                Border shadow = BorderFactory.createEmptyBorder();
                TitledBorder title = BorderFactory.createTitledBorder(shadow, "" + getNumberOfFiles());
                title.setTitleJustification(TitledBorder.RIGHT);
                title.setTitlePosition(TitledBorder.ABOVE_TOP);
                this.setBorder(title);

                g.setColor(Color.red.darker().darker());
                g.fillRoundRect(5, 0, getSize().width - 5, getSize().height - 5, 5, 10);
                g.setColor(Color.red.darker());
                g.fillRoundRect(0, 5, getSize().width - 5, getSize().height - 5, 5, 10);
                g.setColor(Color.black);
                g.drawRoundRect(0, 5, getSize().width - 5, getSize().height - 6, 5, 10);

            }else{
                Border shadow = BorderFactory.createEmptyBorder();
                this.setBorder(shadow);

                g.setColor(Color.red.darker());
                g.fillRoundRect(0, 0, getSize().width-1, getSize().height-1, 5, 10);

                g.setColor(Color.black);
//            String subtitle = (String)getTool().getParameter("name");
//            if(subtitle == null){
//                subtitle = "";
//            }
//            g.drawString(subtitle, (int)(getSize().width *0.15), (int)(getSize().height * 0.9));

                g.drawRoundRect(0, 0, getSize().width-1, getSize().height-1, 5, 10);
            }
        }
        else{
            if(isCollection()){
                Border shadow = BorderFactory.createEmptyBorder();
                TitledBorder title = BorderFactory.createTitledBorder(shadow, "" + getNumberOfFiles());
                title.setTitleJustification(TitledBorder.RIGHT);
                title.setTitlePosition(TitledBorder.ABOVE_TOP);
                this.setBorder(title);

                g.setColor(Color.red.darker());
                g.fillRoundRect(5, 0, getSize().width - 5, getSize().height - 5, 5, 10);
                g.setColor(Color.red);
                g.fillRoundRect(0, 5, getSize().width - 5, getSize().height - 5, 5, 10);
                g.setColor(Color.black);
                g.drawRoundRect(0, 5, getSize().width - 5, getSize().height - 6, 5, 10);

            }else{
                Border shadow = BorderFactory.createEmptyBorder();
                this.setBorder(shadow);

                g.setColor(Color.red);
                g.fillRoundRect(0, 0, getSize().width-1, getSize().height-1, 5, 10);

                g.setColor(Color.black);
//            String subtitle = (String)getTool().getParameter("name");
//            if(subtitle == null){
//                subtitle = "";
//            }
//            g.drawString(subtitle, (int)(getSize().width *0.15), (int)(getSize().height * 0.9));

                g.drawRoundRect(0, 0, getSize().width-1, getSize().height-1, 5, 10);
            }
        }

        g.setColor(c);
        paintProcessProgress(g);

    }

    private boolean isCollection(){
        Object o = getTask().getParameter("collection");
        if(o.equals(true))
        {
            return true;
        }
        else{
            return false;
        }
    }

    private int getNumberOfFiles(){
        Object o = getTask().getParameter("numberOfJobs");
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
