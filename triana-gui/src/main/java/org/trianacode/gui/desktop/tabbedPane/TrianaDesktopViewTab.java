package org.trianacode.gui.desktop.tabbedPane;

import org.trianacode.gui.desktop.TrianaDesktopView;
import org.trianacode.gui.main.TaskGraphPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Nov 10, 2010
 * Time: 5:47:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class TrianaDesktopViewTab extends JPanel implements TrianaDesktopView {

    private TaskGraphPanel panel;
    private String title = "";

    public TrianaDesktopViewTab(TaskGraphPanel panel){
        super();
        this.panel = panel;
        setTitle(panel.getTaskGraph().getToolName());

        JScrollPane scroll = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scroll.setViewportView(panel.getContainer());
        scroll.doLayout();
        setLayout(new BorderLayout());
        add(scroll);
    }

    public TaskGraphPanel getTaskgraphPanel() {
        return panel;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }
}
