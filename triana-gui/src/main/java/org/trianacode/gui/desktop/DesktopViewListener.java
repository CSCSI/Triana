package org.trianacode.gui.desktop;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Nov 10, 2010
 */
public interface DesktopViewListener {

    public void ViewClosing(TrianaDesktopView view);

    public void ViewClosed(TrianaDesktopView view);

    public void ViewOpened(TrianaDesktopView view);

    public void desktopChanged(TrianaDesktopViewManager manager);
}
