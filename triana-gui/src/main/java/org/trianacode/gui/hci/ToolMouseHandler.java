/*
 * The University of Wales, Cardiff Triana Project Software License (Based
 * on the Apache Software License Version 1.1)
 *
 * Copyright (c) 2007 University of Wales, Cardiff. All rights reserved.
 *
 * Redistribution and use of the software in source and binary forms, with
 * or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1.  Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 * 2.  Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 * 3. The end-user documentation included with the redistribution, if any,
 *    must include the following acknowledgment: "This product includes
 *    software developed by the University of Wales, Cardiff for the Triana
 *    Project (http://www.trianacode.org)." Alternately, this
 *    acknowledgment may appear in the software itself, if and wherever
 *    such third-party acknowledgments normally appear.
 *
 * 4. The names "Triana" and "University of Wales, Cardiff" must not be
 *    used to endorse or promote products derived from this software
 *    without prior written permission. For written permission, please
 *    contact triana@trianacode.org.
 *
 * 5. Products derived from this software may not be called "Triana," nor
 *    may Triana appear in their name, without prior written permission of
 *    the University of Wales, Cardiff.
 *
 * 6. This software may not be sold, used or incorporated into any product
 *    for sale to third parties.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN
 * NO EVENT SHALL UNIVERSITY OF WALES, CARDIFF OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 *
 * ------------------------------------------------------------------------
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Triana Project. For more information on the
 * Triana Project, please see. http://www.trianacode.org.
 *
 * This license is based on the BSD license as adopted by the Apache
 * Foundation and is governed by the laws of England and Wales.
 *
 */
package org.trianacode.gui.hci;


import org.trianacode.gui.hci.tools.TaskGraphViewManager;
import org.trianacode.gui.main.*;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskLayoutUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * ToolMouseHandler handles all mouse events on task components and on the panel taskgraph window.
 *
 * @author Ian Taylor
 * @version $Revision: 4048 $
 */
public class ToolMouseHandler implements
        MouseListener, MouseMotionListener, ContainerListener {

    private static final int DO_NOTHING_ON_RELEASE = 0;
    private static final int SELECT_ONLY_ON_RELEASE = 1;
    private static final int DESELECT_ON_RELEASE = 2;
    private static final int SELECT_NONE_ON_RELEASE = 4;

    /**
     * the panel taskgraph component this mouse handler deals with
     */
    private TaskGraphPanel panel;

    /**
     * the handler for node components
     */
    private NodeMouseHandler nodehandler;


    /**
     * the point on the component where the drag started
     */
    private Point dragpoint;

    /**
     * a flag indicating the action on mouse release
     */
    private int onrelease = DO_NOTHING_ON_RELEASE;


    public ToolMouseHandler(TaskGraphPanel panel) {
        this.panel = panel;
        this.nodehandler = new NodeMouseHandler(panel);

        monitorComponent(panel.getContainer());

        panel.getContainer().addMouseListener(this);
        panel.getContainer().addMouseMotionListener(this);
    }


    /**
     * Called when a new component is added, recursively sets the tool/node mouse handler for that component and any
     * subcomponents.
     */
    private void monitorComponent(Component comp) {
        if (comp instanceof NodeComponent) {
            comp.addMouseListener(nodehandler);
            comp.addMouseMotionListener(nodehandler);
        } else if ((comp instanceof TaskComponent) || (comp instanceof TaskSubComponent)) {
            comp.addMouseListener(this);
            comp.addMouseMotionListener(this);
        }

        if (comp instanceof Container) {
            ((Container) comp).addContainerListener(this);

            Component[] comps = ((Container) comp).getComponents();
            for (int count = 0; count < comps.length; count++) {
                monitorComponent(comps[count]);
            }
        }
    }

    /**
     * Called when a component is removed, recursively removes the tool/node mouse handler for that component and any
     * subcomponents.
     */
    private void unmonitorComponent(Component comp) {
        if (comp instanceof NodeComponent) {
            comp.removeMouseListener(nodehandler);
            comp.removeMouseMotionListener(nodehandler);
        } else if ((comp instanceof TaskComponent) || (comp instanceof TaskSubComponent)) {
            comp.removeMouseListener(this);
            comp.removeMouseMotionListener(this);
        }

        if (comp instanceof Container) {
            ((Container) comp).removeContainerListener(this);

            Component[] comps = ((Container) comp).getComponents();
            for (int count = 0; count < comps.length; count++) {
                unmonitorComponent(comps[count]);
            }
        }
    }


    /**
     * @return the source for the mouse event
     */
    private TaskComponent getTaskComponent(MouseEvent event) {
        if (event.getSource() instanceof TaskComponent) {
            return (TaskComponent) event.getSource();
        } else if (event.getSource() instanceof TaskSubComponent) {
            return ((TaskSubComponent) event.getSource()).getMainTaskComponent();
        } else {
            return null;
        }
    }


    /**
     * Select all the task components in the taskgraph
     */
    private void selectAll(boolean selected) {
        TaskComponent[] comps = panel.getTaskComponents();

        for (int count = 0; count < comps.length; count++) {
            comps[count].setSelected(selected);
        }
    }

    /**
     * Select all the task components in the taskgraph
     */
    private void selectOnly(TaskComponent comp) {
        TaskComponent[] comps = panel.getTaskComponents();

        for (int count = 0; count < comps.length; count++) {
            comps[count].setSelected(comps[count] == comp);
        }
    }

    /**
     * Select all the task components in the taskgraph
     */
    private TaskComponent[] getSelected() {
        TaskComponent[] comps = panel.getTaskComponents();
        ArrayList selected = new ArrayList();

        for (int count = 0; count < comps.length; count++) {
            if (comps[count].isSelected()) {
                selected.add(comps[count]);
            }
        }

        return (TaskComponent[]) selected.toArray(new TaskComponent[selected.size()]);
    }


    /**
     * Moves the unit specified and any other that are selected to the new location, clipping if necessary.
     */
    public void moveSelected(TaskComponent comp, Point newPosition) {
        int xmove = newPosition.x - comp.getComponent().getLocation().x;
        int ymove = newPosition.y - comp.getComponent().getLocation().y;

        TaskComponent[] tools = getSelected();
        Task[] tasks = new Task[tools.length];

        for (int count = 0; count < tasks.length; count++) {
            tasks[count] = tools[count].getTaskInterface();
        }

        TaskLayoutUtils.translate(tasks, xmove, ymove, panel.getLayoutDetails());

        panel.getContainer().invalidate();
        panel.getContainer().validate();
        panel.getContainer().repaint();
    }


    /**
     * Sets the origin and size of the temporary selection box
     */
    public void setSelectionBox(int x, int y, int width, int height) {
        if (panel instanceof SelectionBoxInterface) {
            ((SelectionBoxInterface) panel).setSelectionBox(new Point(x, y), new Dimension(width, height));

            int originx = Math.min(x, x + width);
            int originy = Math.min(y, y + height);

            TaskComponent[] tasks = panel.getTaskComponents();
            Rectangle selbounds = new Rectangle(originx, originy, Math.abs(width), Math.abs(height));
            Rectangle bounds;

            for (int count = 0; count < tasks.length; count++) {
                bounds = tasks[count].getComponent().getBounds();
                tasks[count].setSelected(selbounds.intersects(bounds));
            }
        }
    }

    /**
     * Clears the temporary selection box so it is no longer shown
     */
    public void clearSelectionBox() {
        if (panel instanceof SelectionBoxInterface) {
            ((SelectionBoxInterface) panel).clearSelectionBox();
        }
    }


    /**
     * Invoked when the mouse button has been clicked (pressed and released) on a component.
     */
    public void mouseClicked(MouseEvent event) {
        TaskComponent task = getTaskComponent(event);

        if (task != null) {
            if (SwingUtilities.isLeftMouseButton(event) && (event.getClickCount() >= 2)) {
                selectOnly(task);
                handleDoubleClick(task.getTaskInterface(), event);
            }
        }
    }

    private void handleDoubleClick(Task task, MouseEvent mevt) {
        Action action = TaskGraphViewManager.getTaskAction(task);
        ActionEvent event = new ActionEvent(mevt.getSource(), ActionEvent.ACTION_PERFORMED,
                (String) action.getValue(Action.ACTION_COMMAND_KEY), mevt.getWhen(), mevt.getModifiers());

        action.actionPerformed(event);
    }

    /**
     * Invoked when the mouse enters a component.
     */
    public void mouseEntered(MouseEvent event) {
    }

    /**
     * Invoked when the mouse exits a component.
     */
    public void mouseExited(MouseEvent event) {
    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     */
    public void mousePressed(MouseEvent event) {
        TaskComponent task = getTaskComponent(event);

        if (event.isPopupTrigger()) {
            doPopupMenu(event, task);
        } else if (task == null) {
            onrelease = SELECT_NONE_ON_RELEASE;
        } else {
            if (event.isControlDown()) {
                if (task.isSelected()) {
                    onrelease = DESELECT_ON_RELEASE;
                } else {
                    onrelease = DO_NOTHING_ON_RELEASE;
                }
            } else if (SwingUtilities.isRightMouseButton(event)) {
                if (!task.isSelected()) {
                    selectOnly(task);
                }
                onrelease = DO_NOTHING_ON_RELEASE;
            } else {
                if (task.isSelected()) {
                    onrelease = DO_NOTHING_ON_RELEASE;
                    //onrelease = SELECT_NONE_ON_RELEASE;

                } else {
                    selectOnly(task);
                }
            }
            task.setSelected(true);
        }

        dragpoint = event.getPoint();
    }

    private void doPopupMenu(MouseEvent event, TaskComponent task) {
        JPopupMenu menu = null;

        if (task == null) {
            menu = TaskGraphViewManager.getOpenGroupPopup(panel.getTaskGraph());
        } else if (task.isSelected() && (getSelected().length > 1)) {
            TaskComponent[] comps = getSelected();
            Task[] tasks = new Task[comps.length];

            for (int count = 0; count < comps.length; count++) {
                tasks[count] = comps[count].getTaskInterface();
            }

            menu = TaskGraphViewManager.getMultipleSelectionPopup(panel.getTaskGraph(), tasks);
        } else {
            selectOnly(task);
            onrelease = DO_NOTHING_ON_RELEASE;
            menu = TaskGraphViewManager.getWorkspacePopup(task.getTaskInterface());
        }

        if ((menu != null) && (event.getSource() instanceof Component)) {
            menu.show((Component) event.getSource(), event.getX(), event.getY());
        }
    }


    /**
     * Invoked when a mouse button has been released on a component.
     */
    public void mouseReleased(MouseEvent event) {
        TaskComponent task = getTaskComponent(event);

        if (event.isPopupTrigger()) {
            doPopupMenu(event, task);
        } else if ((onrelease == DESELECT_ON_RELEASE) && (task != null)) {
            task.setSelected(false);
        } else if ((onrelease == SELECT_ONLY_ON_RELEASE) && (task != null)) {
            selectOnly(task);
        } else if (onrelease == SELECT_NONE_ON_RELEASE) {
            selectAll(false);
        }

        clearSelectionBox();

        dragpoint = null;
        onrelease = DO_NOTHING_ON_RELEASE;
    }

    /**
     * Invoked when a mouse button is pressed on a component and then dragged.  <code>MOUSE_DRAGGED</code> events will
     * continue to be delivered to the component where the drag originated until the mouse button is released
     * (regardless of whether the mouse position is within the bounds of the component).
     * <p/>
     * Due to platform-dependent Drag&Drop implementations, <code>MOUSE_DRAGGED</code> events may not be delivered
     * during a native Drag&Drop operation.
     */
    public void mouseDragged(MouseEvent event) {
        TaskComponent task = getTaskComponent(event);

        if (dragpoint != null) {
            if (task == null) {
                setSelectionBox(dragpoint.x, dragpoint.y, event.getPoint().x - dragpoint.x,
                        event.getPoint().y - dragpoint.y);
            } else if (task != null) {
                Point dest = SwingUtilities.convertPoint(task.getComponent(), event.getPoint(), panel.getContainer());
                dest.translate(-dragpoint.x, -dragpoint.y);

                if (panel.getContainer().contains(dest)) {
                    moveSelected(task, dest);
                }
            }
        }

        onrelease = DO_NOTHING_ON_RELEASE;
    }

    /**
     * Invoked when the mouse cursor has been moved onto a component but no buttons have been pushed.
     */
    public void mouseMoved(MouseEvent event) {
    }


    /**
     * Invoked when a component has been added to the container.
     */
    public void componentAdded(ContainerEvent event) {
        monitorComponent(event.getChild());
    }

    /**
     * Invoked when a component has been removed from the container.
     */
    public void componentRemoved(ContainerEvent event) {
        unmonitorComponent(event.getChild());
    }

}


















