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


public class StripeAction extends AbstractAction {

    private FramesDesktopManager desk;

    public StripeAction(FramesDesktopManager desk) {
        super("Stripe Frames");
        this.desk = desk;
    }

    public void actionPerformed(ActionEvent ev) {

        TrianaDesktopView[] allframes = desk.getViews();
        int count = allframes.length;
        if (count == 0) return;

        int rows = count;

        Dimension size = desk.getDesktop().getSize();

        int w = size.width;
        int h = size.height / rows;
        int x = 0;
        int y = 0;

        for (int i = 0; i < rows; i++) {
            TrianaDesktopView view = allframes[i];
            if (view instanceof FramesDesktopView) {
                FramesDesktopView f = (FramesDesktopView) view;
                if (!f.isClosed() && f.isIcon()) {
                    try {
                        f.setIcon(false);
                    } catch (PropertyVetoException ignored) {
                    }
                }
                if (f.isMaximum()) {
                    try {
                        f.setMaximum(false);
                    } catch (PropertyVetoException e) {

                    }
                }
                f.setBounds(x, y, w, h);
                y += h;
            }
        }
    }
}
