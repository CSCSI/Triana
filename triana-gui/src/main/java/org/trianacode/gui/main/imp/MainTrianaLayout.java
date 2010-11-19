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

package org.trianacode.gui.main.imp;

import org.trianacode.gui.main.*;
import org.trianacode.taskgraph.*;

import java.awt.*;
import java.util.ArrayList;

/**
 * The layout manager for positioning the triana tools on the workspace.
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 */

public class MainTrianaLayout implements LayoutManager, TaskLayoutDetails, ZoomLayout {

    public static final String TASK = "task";

    public static final String LEFT_FORSHOW = "leftForShow";
    public static final String RIGHT_FORSHOW = "rightForShow";


    private ArrayList tasks = new ArrayList();
    private ArrayList leftforshows = new ArrayList();
    private ArrayList rightforshows = new ArrayList();

    private int border;
    private int forshowspace;

    private int leftforshowwidth = -1;
    private int taskgraphwidth = -1;

    private double zoom;


    /**
     * Constructs a main triana layout with the specified border
     *
     * @param border       the border around the components
     * @param forshowspace the space between the rightmost componenet and the right hand for show tools.
     */
    public MainTrianaLayout(int border, int forshowspace, double zoom) {
        this.border = border;
        this.forshowspace = forshowspace;
        this.zoom = zoom;
    }


    public void addLayoutComponent(String str, Component comp) {
        if (str.equals(TASK)) {
            tasks.add(comp);
        } else if (str.equals(LEFT_FORSHOW)) {
            leftforshows.add(comp);
        } else if (str.equals(RIGHT_FORSHOW)) {
            rightforshows.add(comp);
        }
    }

    public void removeLayoutComponent(Component comp) {
        tasks.remove(comp);
        leftforshows.remove(comp);
        rightforshows.remove(comp);
    }


    /**
     * Sets the zoom factor for the layout
     */
    public void setZoom(double zoom) {
        this.zoom = zoom;

        leftforshowwidth = -1;
        taskgraphwidth = -1;
    }

    /**
     * @return the zoom factor for the layout
     */
    public double getZoom() {
        return zoom;
    }


    /**
     * @return the size of the left border of the workspace
     */
    public int getLeftBorder() {
        if ((leftforshowwidth == -1) || (leftforshowwidth == 0)) {
            return (int) border;
        } else {
            return (int) (border + leftforshowwidth + (forshowspace * zoom));
        }
    }

    /**
     * @return the size of the right border of the workspace
     */
    public int getTopBorder() {
        return border;
    }


    /**
     * @return the dimensions of a standard triana task
     */
    public TDimension getTaskDimensions() {
        TDimension defsize = new TDimension(TrianaLayoutConstants.DEFAULT_TOOL_SIZE.width,
                TrianaLayoutConstants.DEFAULT_TOOL_SIZE.height);
        return new TDimension((int) (defsize.getWidth() * zoom), (int) (defsize.getHeight() * zoom));
    }


    public Dimension minimumLayoutSize(Container container) {
        return preferredLayoutSize(container);
    }

    public Dimension preferredLayoutSize(Container container) {
        if (isStable(container)) {
            Component[] comps = container.getComponents();
            Dimension size;
            Point loc;
            int maxx = 0;
            int maxy = 0;

            recalculateWidths(false);

            for (int count = 0; count < comps.length; count++) {
                size = getSize(comps[count]);
                loc = getLocation(comps[count]);

                if (loc.x + size.width > maxx) {
                    maxx = loc.x + size.width;
                }

                if (loc.y + size.height > maxy) {
                    maxy = loc.y + size.height;
                }
            }

            return new Dimension(maxx + border, maxy + border);
        } else {
            return container.getSize();
        }
    }


    public void layoutContainer(Container container) {
        if (isStable(container)) {
            Component[] comps = container.getComponents();

            recalculateWidths(true);

            for (int count = 0; count < comps.length; count++) {
                comps[count].setSize(getSize(comps[count]));
                comps[count].setLocation(getLocation(comps[count]));
            }
        }
    }

    /**
     * Checks to see whether the taskgraph is in a stable state.
     *
     * @return true if the taskgraph is stable or the container is not a taskgraph panel.
     */
    private boolean isStable(Container container) {
        if (!(container instanceof TaskGraphPanel)) {
            return true;
        }

        return ((TaskGraphPanel) container).getTaskGraph().getControlTaskState() != TaskGraph.CONTROL_TASK_UNSTABLE;
    }


    /**
     * @return the location of the specified component
     */
    private Point getLocation(Component comp) {
        if (tasks.contains(comp)) {
            return getTaskLocation((TaskComponent) comp);
        } else if (leftforshows.contains(comp)) {
            return getForShowLocation((ForShowComponent) comp, true);
        } else if (rightforshows.contains(comp)) {
            return getForShowLocation((ForShowComponent) comp, false);
        } else {
            return new Point(0, 0);
        }
    }

    /**
     * @return the size of the specified component
     */
    private Dimension getSize(Component comp) {
        Dimension size = comp.getPreferredSize();
        return new Dimension((int) (size.width * zoom), (int) (size.height * zoom));
    }


    /**
     * @return the location of the triana task
     */
    private Point getTaskLocation(TaskComponent comp) {
        TPoint tp = TaskLayoutUtils.getPosition(comp.getTaskInterface(), this);
        return new Point((int) tp.getX(), (int) tp.getY());
    }

    /**
     * @return the location of the for show tool
     */
    private Point getForShowLocation(ForShowComponent comp, boolean input) {
        Node node = comp.getGroupNode();
        int ypos = (int) ((node.getAbsoluteNodeIndex() * (comp.getComponent().getPreferredSize().height + border))
                * zoom);

        if (input) {
            return new Point(border, border + ypos);
        } else {
            return new Point(border + leftforshowwidth + (forshowspace * 2) + taskgraphwidth, border + ypos);
        }
    }


    private void recalculateWidths(boolean force) {
        if (force || (leftforshowwidth == -1) || (taskgraphwidth == -1)) {
            leftforshowwidth = getLeftForShowWidth();
            taskgraphwidth = getTaskGraphWidth();
        }
    }

    private int getLeftForShowWidth() {
        Component[] comps = (Component[]) leftforshows.toArray(new Component[leftforshows.size()]);
        Node node;
        Dimension size;
        int width = 0;

        for (int count = 0; count < comps.length; count++) {
            node = ((ForShowComponent) comps[count]).getInternalNode();

            if (node.isInputNode()) {
                size = getSize(comps[count]);

                if (size.width > width) {
                    width = size.width;
                }
            }
        }

        return width;
    }

    private int getTaskGraphWidth() {
        Component[] comps = (Component[]) tasks.toArray(new Component[tasks.size()]);

        TPoint loc;
        double basewidth = getTaskDimensions().getWidth();
        double taskwidth;
        double taskgraphwidth = 0;

        for (int count = 0; count < comps.length; count++) {
            taskwidth = ((double) getSize(comps[count]).width) / basewidth;
            loc = TaskLayoutUtils.getPosition(((TaskComponent) comps[count]).getTaskInterface());

            if (loc.getX() + taskwidth > taskgraphwidth) {
                taskgraphwidth = loc.getX() + taskwidth;
            }
        }

        return (int) (taskgraphwidth * basewidth);
    }


}
