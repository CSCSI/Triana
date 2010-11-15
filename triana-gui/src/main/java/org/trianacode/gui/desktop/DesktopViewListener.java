package org.trianacode.gui.desktop;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Nov 10, 2010
 */
public interface DesktopViewListener {

    public void ViewClosing(DesktopView view);

    public void ViewClosed(DesktopView view);

    public void ViewOpened(DesktopView view);

    public void desktopChanged(DesktopViewManager manager);
}
