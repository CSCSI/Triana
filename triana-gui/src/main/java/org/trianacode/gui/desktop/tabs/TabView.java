package org.trianacode.gui.desktop.tabs;

import org.trianacode.gui.desktop.DesktopView;
import org.trianacode.gui.main.TaskGraphPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: Nov 10, 2010
 * Time: 5:47:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class TabView extends JPanel implements DesktopView {

    private TaskGraphPanel panel;
    private String title = "";

    public TabView(TaskGraphPanel panel) {
        super();
        setLayout(new BorderLayout());
        this.panel = panel;
        setTitle(panel.getTaskGraph().getToolName());

        JScrollPane scroll = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scroll.setViewportView(this.panel.getContainer());
        scroll.doLayout();
        add(scroll, BorderLayout.CENTER);
    }

    public TaskGraphPanel getTaskgraphPanel() {
        return panel;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String toString() {
        return ("Tab: " + getTitle() + " holds panel: " + panel.getTaskGraph().getToolName());
    }
}
