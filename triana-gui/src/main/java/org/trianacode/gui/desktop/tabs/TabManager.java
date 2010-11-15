package org.trianacode.gui.desktop.tabs;

import org.trianacode.gui.desktop.DesktopViewListener;
import org.trianacode.gui.desktop.TrianaDesktopView;
import org.trianacode.gui.desktop.TrianaDesktopViewManager;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.gui.main.TaskGraphPanel;
import org.trianacode.taskgraph.TaskGraph;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Nov 10, 2010
 * Time: 5:27:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class TabManager implements TrianaDesktopViewManager {

    private JTabbedPane tabbedPane;
    private TrianaDesktopView selected;
    private java.util.List<TabView> tabs = new ArrayList<TabView>();
    private java.util.List<DesktopViewListener> listeners = new ArrayList<DesktopViewListener>();

    private static TabManager manager = new TabManager();

    public static TabManager getManager() {
        return manager;
    }

    public TabManager() {
        this.tabbedPane = new JTabbedPane();

    }

    @Override
    public Container getWorkspace() {
        return tabbedPane;
    }

    @Override
    public TrianaDesktopView newDesktopView(final TaskGraphPanel panel) {
        final TabView tab = new TabView(panel);
        tabs.add(tab);

        String name = panel.getTaskGraph().getToolName();
        if (name.equals("")) {
            name = "Untitled";
        }
        JPanel tabHeader = new JPanel(new BorderLayout());
        JLabel nameLab = new JLabel(name);
        nameLab.setBorder(new EmptyBorder(0, 0, 0, 0));
        tabHeader.setOpaque(false);
        tabHeader.add(nameLab, BorderLayout.CENTER);
        JButton close = new JButton(GUIEnv.getIcon("close.png"));
        close.setRolloverEnabled(true);
        close.setRolloverIcon(GUIEnv.getIcon("closehover.png"));
        close.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                remove(tab);
                for (DesktopViewListener listener : listeners) {
                    listener.ViewClosed(tab);
                }
            }
        });
        close.setOpaque(false);
        close.setToolTipText("close this tab");
        close.setFocusable(false);
        close.setBorder(new EmptyBorder(0, 10, 0, 0));

        tabHeader.add(close, BorderLayout.EAST);
        tabbedPane.addTab(null, tab);
        tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, tabHeader);
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);

        return tab;
    }

    @Override
    public void remove(TrianaDesktopView tab) {
        if (tab instanceof TabView) {
            tabs.remove(tab);
            tabbedPane.remove((TabView) tab);
        }
        for (DesktopViewListener listener : listeners) {
            listener.ViewClosed(tab);
        }
    }

    @Override
    public TrianaDesktopView getTaskgraphViewFor(TaskGraph taskgraph) {
        for (TabView tab : tabs) {
            TaskGraphPanel panel = tab.getTaskgraphPanel();
            if (panel.getTaskGraph() == taskgraph) {
                return tab;
            }
        }
        return null;
    }

    @Override
    public void addDesktopViewListener(DesktopViewListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    @Override
    public void removeDesktopViewListener(DesktopViewListener listener) {
        listeners.remove(listener);
    }

    @Override
    public TrianaDesktopView[] getViews() {
        return tabs.toArray(new TrianaDesktopView[tabs.size()]);
    }

    @Override
    public void setSelected(TrianaDesktopView panel, boolean sel) {
        int tabs = tabbedPane.getTabCount();
        for (int i = 0; i < tabs; i++) {
            Component c = tabbedPane.getTabComponentAt(i);
            if (c == panel) {
                tabbedPane.setSelectedIndex(i);
                selected = panel;
            }
        }
    }

    @Override
    public TrianaDesktopView getSelected() {
        return selected;
    }

    @Override
    public TrianaDesktopView getDesktopViewFor(TaskGraphPanel panel) {
        TrianaDesktopView[] views = getViews();
        for (TrianaDesktopView view : views) {
            if (view.getTaskgraphPanel() == panel) {
                return view;
            }
        }
        return null;
    }

    @Override
    public String getTitle(TrianaDesktopView view) {
        if (view instanceof TabView) {
            return ((TabView) view).getTitle();
        }
        return null;
    }

    @Override
    public void setTitle(TrianaDesktopView view, String title) {
        if (view instanceof TabView) {
            ((TabView) view).setTitle(title);
        }
    }

    @Override
    public void desktopRemoved() {
        tabs.clear();
        listeners.clear();
        tabbedPane.removeAll();
        selected = null;
    }

    @Override
    public void desktopAdded() {
        for (DesktopViewListener listener : listeners) {
            listener.desktopChanged(this);
        }
    }

    @Override
    public TrianaDesktopView getDropTarget(int x, int y, Component source) {
        for (TabView tab : tabs) {
            if (tab.isVisible()) {
                int landingPosX = x -
                        (tab.getLocationOnScreen().x - source.getLocationOnScreen().x);
                int landingPosY = y -
                        (tab.getLocationOnScreen().y - source.getLocationOnScreen().y);
                if (tab.contains(landingPosX, landingPosY)) {
                    return tab;
                }
            }
        }
        return null;
    }

}
