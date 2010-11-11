package org.trianacode.gui.desktop.tabbedPane;

import org.trianacode.gui.desktop.DesktopViewListener;
import org.trianacode.gui.desktop.TrianaDesktopView;
import org.trianacode.gui.desktop.TrianaDesktopViewManager;
import org.trianacode.gui.main.TaskGraphPanel;
import org.trianacode.taskgraph.TaskGraph;

import javax.swing.*;
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
public class TrianaTabManager implements TrianaDesktopViewManager {

    JTabbedPane tabbedPane;
    TrianaDesktopView selected;

    public static TrianaTabManager manager = new TrianaTabManager();
    private java.util.List<TrianaDesktopViewTab> tabs = new ArrayList<TrianaDesktopViewTab>();
    private java.util.List<DesktopViewListener> listeners = new ArrayList<DesktopViewListener>();


    public static TrianaTabManager getManager(){
        return manager;
    }

    public TrianaTabManager(){
        this.tabbedPane = new JTabbedPane();
        tabbedPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent evt) {
                TrianaDesktopViewTab tab = (TrianaDesktopViewTab)tabbedPane.getSelectedComponent();
                System.out.println("Selected panel is : " + tab.getTitle());
                selected = getDesktopViewFor(tab.getTaskgraphPanel());
            }
        });
    }

    @Override
    public Container getWorkspace() {
        return tabbedPane;
    }

    @Override
    public TrianaDesktopView newDesktopView(final TaskGraphPanel panel) {
        final TrianaDesktopViewTab tab = new TrianaDesktopViewTab(panel);
        String name = panel.getTaskGraph().getToolName();
        if(name.equals("")){
            name = "Untitled";
        }
        JPanel tabHeader = new JPanel(new BorderLayout());
        tabHeader.add(new JLabel(name), BorderLayout.CENTER);
        JButton close = new JButton("x");
        close.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent actionEvent) {
                tabs.remove(tab);
                tab.getTaskgraphPanel().dispose();
                tabbedPane.remove(tab);
            }
        });
        close.setOpaque(false);
        close.setPreferredSize(new Dimension(17, 17));
        close.setToolTipText("close this tab");
        close.setContentAreaFilled(false);
        close.setFocusable(false);
        close.setBorder(BorderFactory.createEtchedBorder());
        close.setBorderPainted(false);

        tabHeader.add(close, BorderLayout.EAST);
        tabbedPane.addTab(name, tab);
        tabbedPane.setSelectedComponent(tab);
        tabbedPane.setTabComponentAt(tabbedPane.getSelectedIndex(), tabHeader);
        tabs.add(tab);

        return tab;
    }

    @Override
    public void remove(TrianaDesktopView tab) {
        if (tab instanceof TrianaDesktopViewTab) {
            tabs.remove(tab);
        }
    }

    @Override
    public TrianaDesktopView getTaskgraphViewFor(TaskGraph taskgraph) {
        for (TrianaDesktopViewTab tab : tabs) {
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
        System.out.println("Views : " + tabs.size() + tabs);
        return tabs.toArray(new TrianaDesktopView[tabs.size()]);
    }

    @Override
    public void setSelected(TrianaDesktopView panel, boolean sel) {

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
        System.out.println("No view found for panel : " + panel);
        return null;
    }

    @Override
    public String getTitle(TrianaDesktopView view) {
        if (view instanceof TrianaDesktopViewTab) {
            return ((TrianaDesktopViewTab) view).getTitle();
        }
        return null;
    }

    @Override
    public void setTitle(TrianaDesktopView view, String title) {
        if (view instanceof TrianaDesktopViewTab) {
            ((TrianaDesktopViewTab) view).setTitle(title);
        }
    }
}
