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


public class TileAction extends AbstractAction {

    private FramesDesktopManager desk;

    public TileAction(FramesDesktopManager desk) {
        super("Tile Frames");
        this.desk = desk;
    }

    public void actionPerformed(ActionEvent ev) {

        TrianaDesktopView[] allframes = desk.getViews();
        int count = allframes.length;
        if (count == 0) return;

        int sqrt = (int) Math.sqrt(count);
        int rows = sqrt;
        int cols = sqrt;
        if (rows * cols < count) {
            cols++;
            if (rows * cols < count) {
                rows++;
            }
        }
        Dimension size = desk.getDesktop().getSize();

        int w = size.width / cols;
        int h = size.height / rows;
        int x = 0;
        int y = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols && ((i * cols) + j < count); j++) {
                int next = (i * cols) + j;
                boolean last = next == count - 1 ? true : false;
                TrianaDesktopView view = allframes[next];
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
                    if (last) {
                        f.setBounds(x, y, size.width - x, h);
                        desk.setSelected(f, true);
                    } else {
                        f.setBounds(x, y, w, h);
                        x += w;
                    }
                }
            }
            y += h;
            x = 0;
        }
    }
}

