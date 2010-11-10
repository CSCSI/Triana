package org.trianacode.gui.desktop.frames;

import org.trianacode.gui.desktop.DesktopViewListener;
import org.trianacode.gui.desktop.TrianaDesktopView;
import org.trianacode.gui.desktop.TrianaDesktopViewManager;
import org.trianacode.gui.main.TaskGraphPanel;
import org.trianacode.taskgraph.TaskGraph;

import javax.swing.*;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Nov 9, 2010
 */
public class TrianaDesktopManager implements InternalFrameListener, ComponentListener, TrianaDesktopViewManager {

    private int offsetX = 20;
    private int offsetY = 20;
    private java.util.List<TrianaDesktopViewFrame> frames = new ArrayList<TrianaDesktopViewFrame>();
    private TrianaDesktopViewFrame selected = null;

    private static TrianaDesktopManager manager = new TrianaDesktopManager();
    private java.util.List<DesktopViewListener> listeners = new ArrayList<DesktopViewListener>();
    private Map<TrianaDesktopViewFrame, JButton> buttons = new HashMap<TrianaDesktopViewFrame, JButton>();

    public static TrianaDesktopManager getManager() {
        return manager;
    }

    private JPanel container;
    private JPanel butpanel;


    private JDesktopPane desktop;

    public TrianaDesktopManager() {
        container = new JPanel(new BorderLayout());
        butpanel = new JPanel();
        butpanel.setLayout(new BoxLayout(butpanel, BoxLayout.X_AXIS));
        butpanel.add(Box.createRigidArea(new Dimension(1, 30)));
        this.desktop = new JDesktopPane();
        this.desktop.setDesktopManager(new RestrictedDesktopManager());
        desktop.addComponentListener(this);
        container.add(butpanel, BorderLayout.NORTH);
        container.add(desktop, BorderLayout.CENTER);
    }

    public void setSize(TrianaDesktopViewFrame frame) {
        double dy = ((double) desktop.getSize().height);
        double dx = ((double) desktop.getSize().width);
        setSize(frame, dx, dy, true);
    }

    private void setSize(TrianaDesktopViewFrame frame, double dx, double dy, boolean init) {
        if (init) {
            TaskGraph taskgraph = frame.getTaskgraphPanel().getTaskGraph();
            if (taskgraph.getTasks(false).length == 0) {
                frame.setSize(new Dimension((int) Math.max(100, dx * 0.6), (int) Math.max(100, dy * 0.6)));
            }
        }
        if (frame.getX() + frame.getWidth() >= dx) {
            if (frame.getY() + frame.getHeight() >= dy) {
                frame.setSize(new Dimension((int) Math.max(dx - frame.getX(), 100), (int) Math.max(dy - frame.getY(), 100)));
            } else {
                frame.setSize(new Dimension((int) Math.max(dx - frame.getX(), 100), Math.max(frame.getHeight(), 100)));
            }
        } else {
            if (frame.getY() + frame.getHeight() >= dy) {
                if (frame.getX() + frame.getWidth() >= dx) {
                    frame.setSize(new Dimension((int) Math.max(dx - frame.getX(), 100), (int) Math.max(100, dy - frame.getY())));
                } else {
                    frame.setSize(new Dimension(Math.max(100, frame.getWidth()), (int) Math.max(100, dy - frame.getY())));
                }
            }
        }
    }


    @Override
    public void internalFrameOpened(InternalFrameEvent e) {
    }

    @Override
    public void internalFrameClosing(InternalFrameEvent e) {
        JInternalFrame frame = e.getInternalFrame();

        for (DesktopViewListener listener : listeners) {
            listener.ViewClosing((TrianaDesktopView) frame);
        }
    }

    @Override
    public void internalFrameClosed(InternalFrameEvent e) {
        JInternalFrame frame = e.getInternalFrame();
        frames.remove(frame);
        if (frame instanceof TrianaDesktopViewFrame) {
            JButton b = buttons.remove(frame);
            butpanel.remove(b);
            butpanel.repaint();
        }
        if (frames.size() > 0) {
            try {
                frames.get(frames.size() - 1).setSelected(true);
            } catch (PropertyVetoException e1) {
                e1.printStackTrace();
            }
        }
        for (DesktopViewListener listener : listeners) {
            listener.ViewClosed((TrianaDesktopView) frame);
        }
        frame.removeInternalFrameListener(this);
    }

    @Override
    public void internalFrameIconified(InternalFrameEvent e) {
    }

    @Override
    public void internalFrameDeiconified(InternalFrameEvent e) {
    }

    @Override
    public void internalFrameActivated(InternalFrameEvent e) {
        JInternalFrame frame = e.getInternalFrame();
        if (frame instanceof TrianaDesktopViewFrame) {
            selected = (TrianaDesktopViewFrame) frame;
            JButton b = buttons.get(selected);
            if (b != null) {
                b.setSelected(true);
            }
        }
        for (DesktopViewListener listener : listeners) {
            listener.ViewOpened((TrianaDesktopView) frame);
        }
    }

    @Override
    public void internalFrameDeactivated(InternalFrameEvent e) {
        JInternalFrame frame = e.getInternalFrame();
        if (frame instanceof TrianaDesktopViewFrame) {
            selected = (TrianaDesktopViewFrame) frame;
            JButton b = buttons.get(selected);
            if (b != null) {
                b.setSelected(false);
            }
        }
    }

    public void componentResized(ComponentEvent e) {
        double dy = ((double) desktop.getSize().height);
        double dx = ((double) desktop.getSize().width);
        for (int i = 0; i < frames.size(); i++) {
            TrianaDesktopViewFrame frame = frames.get(i);
            setSize(frame, dx, dy, false);
        }
    }

    public void componentMoved(ComponentEvent e) {

    }

    public void componentShown(ComponentEvent e) {

    }

    public void componentHidden(ComponentEvent e) {

    }

    @Override
    public Container getWorkspace() {
        return container;
    }

    @Override
    public TrianaDesktopView newDesktopView(TaskGraphPanel panel) {
        TrianaDesktopViewFrame frame = new TrianaDesktopViewFrame(panel);
        frame.addInternalFrameListener(this);
        desktop.add(frame);
        try {
            frame.setSelected(true);
            frame.setIcon(false);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
        frame.setBounds(offsetX * frames.size(), offsetY * frames.size(), 400, 300);
        frame.pack();
        setSize(frame);
        frame.setVisible(true);
        frames.add(frame);
        String name = panel.getTaskGraph().getToolName();
        if (name.length() > 16) {
            name = name.substring(0, 13) + "...";
        }
        JButton b = new JButton(name);
        b.addActionListener(new ButtonListener());
        buttons.put(frame, b);
        butpanel.add(b);
        return frame;
    }

    @Override
    public void remove(TrianaDesktopView view) {
        if (view instanceof TrianaDesktopViewFrame) {
            ((TrianaDesktopViewFrame) view).doDefaultCloseAction();
            desktop.remove((TrianaDesktopViewFrame) view);
            JButton b = buttons.remove(view);
            if (b != null) {
                butpanel.remove(b);
            }
        }
    }

    @Override
    public TrianaDesktopView getTaskgraphViewFor(TaskGraph taskgraph) {
        for (TrianaDesktopViewFrame frame : frames) {
            TaskGraphPanel panel = frame.getTaskgraphPanel();
            if (panel.getTaskGraph() == taskgraph) {
                return frame;
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
        return frames.toArray(new TrianaDesktopView[frames.size()]);
    }

    @Override
    public void setSelected(TrianaDesktopView view, boolean sel) {
        if (view instanceof TrianaDesktopViewFrame) {
            try {
                if (sel) {
                    ((TrianaDesktopViewFrame) view).setSelected(true);
                    ((TrianaDesktopViewFrame) view).toFront();
                } else {
                    ((TrianaDesktopViewFrame) view).setSelected(false);

                }
            } catch (PropertyVetoException e) {
                e.printStackTrace();
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
        if (view instanceof TrianaDesktopViewFrame) {
            return ((TrianaDesktopViewFrame) view).getTitle();
        }
        return null;
    }

    @Override
    public void setTitle(TrianaDesktopView view, String title) {
        if (view instanceof TrianaDesktopViewFrame) {
            ((TrianaDesktopViewFrame) view).setTitle(title);
        }
    }

    private class ButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof JButton) {
                for (TrianaDesktopViewFrame frame : buttons.keySet()) {
                    JButton b = buttons.get(frame);
                    if (b == e.getSource()) {
                        setSelected(frame, true);
                        break;
                    }
                }
            }
        }
    }

    private class RestrictedDesktopManager extends DefaultDesktopManager {

        public void dragFrame(JComponent f, int newX, int newY) {
            if (newX + f.getWidth() > desktop.getWidth()) {
                newX = f.getX();
            }
            if (newY + f.getHeight() > desktop.getHeight()) {
                newY = f.getY();
            }
            if (newX < 0) {
                newX = 0;
            }
            if (newY < 0) {
                newY = 0;
            }
            super.dragFrame(f, newX, newY);
        }

        public void activateFrame(JInternalFrame f) {
            if (f == null) {
                return;
            }
        }

    }
}
