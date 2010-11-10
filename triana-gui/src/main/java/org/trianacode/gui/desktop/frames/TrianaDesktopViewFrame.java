package org.trianacode.gui.desktop.frames;

import org.trianacode.gui.desktop.TrianaDesktopView;
import org.trianacode.gui.main.TaskGraphPanel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import java.awt.*;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Nov 10, 2010
 */
public class TrianaDesktopViewFrame extends JInternalFrame implements TrianaDesktopView {

    private TaskGraphPanel panel;

    public TrianaDesktopViewFrame(TaskGraphPanel panel) {
        super(panel.getTaskGraph().getToolName(),
                true, true, true, true);
        this.panel = panel;
        JScrollPane scrollerForMainTriana = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollerForMainTriana.setViewportView(panel.getContainer());
        Border b = new EtchedBorder(EtchedBorder.RAISED, Color.blue, Color.gray);
        scrollerForMainTriana.setViewportBorder(b);
        scrollerForMainTriana.doLayout();
        setLayout(new BorderLayout());
        getContentPane().add(scrollerForMainTriana);
    }

    @Override
    public TaskGraphPanel getTaskgraphPanel() {
        return panel;
    }
}
