package org.trianacode.gui.desktop.frames;

import org.trianacode.gui.desktop.TrianaDesktopView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Nov 12, 2010
 */


public class CascadeAction extends AbstractAction {

    private FramesDesktopManager desk;
    private int offX;
    private int offY;

    public CascadeAction(FramesDesktopManager desk, int offX, int offY) {
        super("Cascade Frames");
        this.desk = desk;
        this.offX = offX;
        this.offY = offY;
    }

    public void actionPerformed(ActionEvent ev) {

        TrianaDesktopView[] allframes = desk.getViews();
        int count = allframes.length;
        if (count == 0) return;

        for (int i = 0; i < count; i++) {

            TrianaDesktopView view = allframes[i];
            if (view instanceof FramesDesktopView) {
                FramesDesktopView f = (FramesDesktopView) view;
                if (!f.isClosed() && f.isIcon()) {
                    try {
                        f.setIcon(false);
                    } catch (PropertyVetoException ignored) {
                    }
                }
                f.setBounds(new Rectangle(offX * i, offY * i, f.getWidth(), f.getHeight()));
                if (i == count - 1) {
                    desk.setSelected(f, true);
                }
            }
        }

    }
}
