package org.trianacode.gui.desktop.frames;

import org.trianacode.gui.desktop.DesktopView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Nov 12, 2010
 */


public class CascadeAction extends AbstractAction {

    private FramesManager desk;
    private int offX;
    private int offY;

    public CascadeAction(FramesManager desk, int offX, int offY) {
        super("Cascade Frames");
        this.desk = desk;
        this.offX = offX;
        this.offY = offY;
    }

    public void actionPerformed(ActionEvent ev) {

        DesktopView[] allframes = desk.getViews();
        int count = allframes.length;
        if (count == 0) return;

        for (int i = 0; i < count; i++) {

            DesktopView view = allframes[i];
            if (view instanceof FramesView) {
                FramesView f = (FramesView) view;
                if (!f.isClosed() && f.isIcon()) {
                    try {
                        f.setIcon(false);
                    } catch (PropertyVetoException ignored) {
                    }
                }
                f.setBounds(new Rectangle(offX * (i + 1), offY * (i + 1), f.getWidth(), f.getHeight()));
                if (i == count - 1) {
                    desk.setSelected(f, true);
                }
            }
        }

    }
}
