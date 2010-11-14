package org.trianacode.gui.desktop.tabbedPane;

import org.trianacode.gui.desktop.DesktopViewListener;
import org.trianacode.gui.desktop.TrianaDesktopView;
import org.trianacode.gui.desktop.TrianaDesktopViewManager;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.gui.main.TaskGraphPanel;
import org.trianacode.taskgraph.TaskGraph;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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

    JTabbedPane tabbedPane;
    TrianaDesktopView selected;

    public static TabManager manager = new TabManager();
    private java.util.List<TabView> tabs = new ArrayList<TabView>();
    private java.util.List<DesktopViewListener> listeners = new ArrayList<DesktopViewListener>();


    public static TabManager getManager() {
        return manager;
    }

    public TabManager() {
        this.tabbedPane = new TrianaTabbedPane();

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
                tabbedPane.remove(tab);
            }
        });
        close.setOpaque(false);
        close.setToolTipText("close this tab");
        close.setFocusable(false);
        close.setBorder(new EmptyBorder(0, 10, 0, 0));

        tabHeader.add(close, BorderLayout.EAST);
        tabbedPane.addTab(name, tab);
        tabbedPane.setSelectedComponent(tab);
        tabbedPane.setTabComponentAt(tabbedPane.getSelectedIndex(), tabHeader);

        return tab;
    }

    @Override
    public void remove(TrianaDesktopView tab) {
        for (DesktopViewListener listener : listeners) {
            listener.ViewClosed((TrianaDesktopView) tab);
        }
        if (tab instanceof TabView) {
            tabs.remove(tab);
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
        selected = panel;

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

    class TrianaTabbedPane extends JTabbedPane {
        public TrianaTabbedPane() {
            this.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent evt) {
                    final TabView tab = (TabView) tabbedPane.getSelectedComponent();
                    if (tab != null) {
     //                 selected = getDesktopViewFor(tab.getTaskgraphPanel());
                        selected = tab;
                        System.out.println("Selected : " + selected.toString());
                        SwingUtilities.invokeLater(new Runnable()
                        {
                            public void run()
                            {
                                tab.requestFocus();
                            }
                        });
                    } else {
                        selected = null;
                    }
                }
            });
        }

    }
}
