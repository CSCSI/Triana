package org.trianacode.gui.desktop.frames;

import org.trianacode.gui.desktop.DesktopViewListener;
import org.trianacode.gui.desktop.TrianaDesktopView;
import org.trianacode.gui.desktop.TrianaDesktopViewManager;
import org.trianacode.gui.hci.DropDownButton;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.gui.main.TaskGraphPanel;
import org.trianacode.taskgraph.TaskGraph;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
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
public class FramesDesktopManager implements InternalFrameListener, ComponentListener, TrianaDesktopViewManager {

    public static final int MIN_WIDTH = 150;
    public static final int MIN_HEIGHT = 100;

    public static final int MIN_D_WIDTH = 500;
    public static final int MIN_D_HEIGHT = 400;

    private int offsetX = 20;
    private int offsetY = 20;
    private java.util.List<FramesDesktopView> frames = new ArrayList<FramesDesktopView>();
    private FramesDesktopView selected = null;

    private static FramesDesktopManager manager = new FramesDesktopManager();
    private java.util.List<DesktopViewListener> listeners = new ArrayList<DesktopViewListener>();
    private Map<FramesDesktopView, JButton> buttons = new HashMap<FramesDesktopView, JButton>();

    public static FramesDesktopManager getManager() {
        return manager;
    }

    private JPanel container;
    private JPanel butpanel;
    private JButton layout;

    private int currMaxX = 0;
    private int currMaxY = 0;
    private ComponentListener frameListener = new FrameListener();


    private JDesktopPane desktop;

    public FramesDesktopManager() {
        init();
    }

    private void init() {
        container = new JPanel(new BorderLayout());
        this.desktop = new JDesktopPane();
        this.desktop.setDesktopManager(new RestrictedDesktopManager());
        this.desktop.addComponentListener(this);
        this.desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
        JPanel topPanel = new JPanel(new BorderLayout());

        butpanel = new JPanel();
        butpanel.setLayout(new BoxLayout(butpanel, BoxLayout.X_AXIS));
        JPopupMenu popup = new JPopupMenu();
        popup.add(new JMenuItem(new TileAction(this)));
        popup.add(new JMenuItem(new CascadeAction(this, offsetX, offsetY)));
        popup.add(new JMenuItem(new StripeAction(this)));
        layout = new DropDownButton(GUIEnv.getIcon("layout-dropdown.png"), popup);
        topPanel.add(layout, BorderLayout.WEST);
        topPanel.add(butpanel, BorderLayout.CENTER);
        container.add(topPanel, BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        Border b = new EtchedBorder(EtchedBorder.RAISED, Color.lightGray, Color.gray);
        scroll.setBorder(b);

        scroll.setViewportView(desktop);
        scroll.doLayout();
        container.add(scroll, BorderLayout.CENTER);

    }

    public JDesktopPane getDesktop() {
        return desktop;
    }

    public void setSize(FramesDesktopView frame) {
        double dy = ((double) desktop.getSize().height);
        double dx = ((double) desktop.getSize().width);
        setSize(frame, dx, dy, true);
    }

    private void setSize(FramesDesktopView frame, double dx, double dy, boolean init) {
        if (init) {
            TaskGraph taskgraph = frame.getTaskgraphPanel().getTaskGraph();
            if (taskgraph.getTasks(false).length == 0) {
                frame.setSize(new Dimension((int) Math.max(MIN_WIDTH, dx * 0.6), (int) Math.max(MIN_HEIGHT, dy * 0.6)));
            }
        }
        if (frame.getX() + frame.getWidth() >= dx) {
            if (frame.getY() + frame.getHeight() >= dy) {
                frame.setSize(new Dimension((int) Math.max(dx - frame.getX(), MIN_WIDTH), (int) Math.max(dy - frame.getY(), MIN_HEIGHT)));
            } else {
                frame.setSize(new Dimension((int) Math.max(dx - frame.getX(), MIN_WIDTH), Math.max(frame.getHeight(), MIN_HEIGHT)));
            }
        } else {
            if (frame.getY() + frame.getHeight() >= dy) {
                if (frame.getX() + frame.getWidth() >= dx) {
                    frame.setSize(new Dimension((int) Math.max(dx - frame.getX(), MIN_WIDTH), (int) Math.max(MIN_HEIGHT, dy - frame.getY())));
                } else {
                    frame.setSize(new Dimension(Math.max(MIN_WIDTH, frame.getWidth()), (int) Math.max(MIN_HEIGHT, dy - frame.getY())));
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
        if (frame instanceof FramesDesktopView) {
            JButton b = buttons.remove(frame);
            butpanel.remove(b);
            butpanel.revalidate();
            butpanel.repaint();
        }
        resizeDesktop();
        if (frames.size() > 0) {
            setSelected(frames.get(frames.size() - 1), true);
        }
        for (DesktopViewListener listener : listeners) {
            listener.ViewClosed((TrianaDesktopView) frame);
        }
        frame.removeComponentListener(frameListener);
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
        if (frame instanceof FramesDesktopView) {
            setSelected((FramesDesktopView) frame, true);
            JButton b = buttons.get(frame);
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
        if (frame instanceof FramesDesktopView) {
            JButton b = buttons.get(frame);
            if (b != null) {
                b.setSelected(false);
            }
        }
    }

    public void componentResized(ComponentEvent e) {
        resizeDesktop();
    }

    public void componentMoved(ComponentEvent e) {

    }

    public void componentShown(ComponentEvent e) {
        resizeDesktop();
    }

    public void componentHidden(ComponentEvent e) {
        resizeDesktop();
    }

    @Override
    public Container getWorkspace() {
        return container;
    }

    @Override
    public TrianaDesktopView newDesktopView(TaskGraphPanel panel) {
        FramesDesktopView frame = new FramesDesktopView(panel);
        frame.addInternalFrameListener(this);
        frame.setBounds(offsetX * (frames.size() + 1), offsetY * (frames.size() + 1), 400, 300);
        frame.pack();
        setSize(frame);
        frames.add(frame);
        String name = panel.getTaskGraph().getToolName();
        if (name.length() > 16) {
            name = name.substring(0, 13) + "...";
        }
        for (JButton button : buttons.values()) {
            button.setSelected(false);
        }
        JButton b = new JButton(name);
        b.setSelected(true);
        b.addActionListener(new ButtonListener());
        buttons.put(frame, b);
        butpanel.add(b);
        resizeDesktop();
        frame.addComponentListener(frameListener);
        desktop.add(frame);
        frame.setVisible(true);
        return frame;
    }

    private void resizeDesktop() {
        currMaxX = MIN_D_WIDTH;
        currMaxY = MIN_D_HEIGHT;
        if (currMaxX < butpanel.getPreferredSize().width) {
            currMaxX = butpanel.getPreferredSize().width;
        }
        for (FramesDesktopView frame : frames) {
            if (!frame.isMaximum()) {
                if (frame.getX() + frame.getWidth() > currMaxX) {
                    currMaxX = frame.getX() + frame.getWidth();
                }
                if (frame.getY() + frame.getHeight() > currMaxY) {
                    currMaxY = frame.getY() + frame.getHeight();
                }
            }
        }
        desktop.setPreferredSize(new Dimension(currMaxX, currMaxY));
        container.revalidate();
    }

    @Override
    public void remove(TrianaDesktopView view) {
        if (view instanceof FramesDesktopView) {
            ((FramesDesktopView) view).doDefaultCloseAction();
            desktop.remove((FramesDesktopView) view);
            JButton b = buttons.remove(view);
            if (b != null) {
                butpanel.remove(b);
                butpanel.revalidate();
            }
            resizeDesktop();
        }
    }

    @Override
    public TrianaDesktopView getTaskgraphViewFor(TaskGraph taskgraph) {
        for (FramesDesktopView frame : frames) {
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
        if (view instanceof FramesDesktopView) {
            try {
                if (sel) {
                    ((FramesDesktopView) view).setSelected(true);
                    ((FramesDesktopView) view).toFront();
                    selected = (FramesDesktopView) view;
                } else {
                    ((FramesDesktopView) view).setSelected(false);
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
        if (view instanceof FramesDesktopView) {
            return ((FramesDesktopView) view).getTitle();
        }
        return null;
    }

    @Override
    public void setTitle(TrianaDesktopView view, String title) {
        if (view instanceof FramesDesktopView) {
            ((FramesDesktopView) view).setTitle(title);
        }
    }

    @Override
    public void desktopRemoved() {
        selected = null;
        for (FramesDesktopView frame : frames) {
            desktop.getDesktopManager().closeFrame(frame);
            desktop.remove(frame);
            JButton b = buttons.remove(frame);
            if (b != null) {
                butpanel.remove(b);
                butpanel.revalidate();
            }
        }
        frames.clear();
        listeners.clear();
        buttons.clear();

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
        Component target;
        int landingPosX = x -
                (desktop.getLocationOnScreen().x - source.getLocationOnScreen().x);
        int landingPosY = y -
                (desktop.getLocationOnScreen().y - source.getLocationOnScreen().y);
        target = desktop.getComponentAt(landingPosX, landingPosY);
        if (target instanceof TrianaDesktopView) {
            return (TrianaDesktopView) target;
        }
        return null;
    }

    private class ButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof JButton) {
                for (FramesDesktopView frame : buttons.keySet()) {
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
                newX = desktop.getWidth() - f.getWidth();
            }
            if (newY + f.getHeight() > desktop.getHeight()) {
                newY = desktop.getHeight() - f.getHeight();
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

    private class FrameListener implements ComponentListener {

        @Override
        public void componentResized(ComponentEvent e) {
        }

        @Override
        public void componentMoved(ComponentEvent e) {
            resizeDesktop();
        }

        @Override
        public void componentShown(ComponentEvent e) {
        }

        @Override
        public void componentHidden(ComponentEvent e) {
        }
    }

}
