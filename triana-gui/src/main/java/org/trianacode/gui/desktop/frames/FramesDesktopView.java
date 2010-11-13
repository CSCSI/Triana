package org.trianacode.gui.desktop.frames;

import org.trianacode.gui.desktop.TrianaDesktopView;
import org.trianacode.gui.main.TaskGraphPanel;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Nov 10, 2010
 */
public class FramesDesktopView extends JInternalFrame implements TrianaDesktopView {

    private TaskGraphPanel panel;

    public FramesDesktopView(TaskGraphPanel panel) {
        super(panel.getTaskGraph().getToolName(),
                true, true, true, false);
        this.panel = panel;

        JScrollPane scrollerForMainTriana = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollerForMainTriana.setViewportView(panel.getContainer());
        Border b = new EtchedBorder(EtchedBorder.RAISED, Color.blue, Color.gray);
        scrollerForMainTriana.setViewportBorder(b);
        scrollerForMainTriana.doLayout();
        setLayout(new BorderLayout());
        getContentPane().add(scrollerForMainTriana);
        if (getBorder() instanceof CompoundBorder) {
            // fix for big spaces around frames on mac - not perfect
            if ("Aqua".equals(UIManager.getLookAndFeel().getID())) {
                CompoundBorder cb = (CompoundBorder) getBorder();
                setBorder(new CompoundBorder(new EmptyBorder(0, 0, 1, 1), cb.getInsideBorder()));
            }
        }

    }

    @Override
    public TaskGraphPanel getTaskgraphPanel() {
        return panel;
    }

    private static class ShadowBorder extends AbstractBorder {

        private static final Insets INSETS = new Insets(0, 0, 1, 1);

        public Insets getBorderInsets(Component c) {
            return INSETS;
        }

        public void paintBorder(Component c, Graphics g,
                                int x, int y, int w, int h) {

            Color shadow = UIManager.getColor("controlShadow");
            if (shadow == null) {
                shadow = Color.GRAY;
            }
            g.translate(x, y);
            g.setColor(shadow);
            g.fillRect(w - 1, 3, 1, h - 3);
            g.fillRect(3, h - 1, w - 2, 1);
//            g.fillRect(w, 1, 1, h - 1);
//            g.fillRect(1, h - 1, w, 1);

            g.translate(-x, -y);
        }
    }

}
