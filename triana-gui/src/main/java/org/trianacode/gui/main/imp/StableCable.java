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

import org.trianacode.gui.main.NodeComponent;
import org.trianacode.taskgraph.Node;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Vector;

/**
 * Graphical cable for connecting units on the GUI.
 *
 * @author Robert Davies
<<<<<<< StableCable.java
 * @version $Revision: 4048 $
 * @date $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
=======
 * @version $Revision: 4048 $
 * @date $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
>>>>>>> 1.3.2.1
 */
public class StableCable implements MouseListener, MouseMotionListener, DrawCable {

    /*
     * The default width of the cable
     */
    public static int DEFAULT_WIDTH = 5;

    /*
     * The number of pixels that a user can click within a point or line to be counted as a hit
     */
    public static int PIXELS = 5;


    /*
    * The size of the nodes relative to the width that mark the points on the cable
    */
    public static double NODE_SIZE_FACTOR = 0.8;

    /*
    * The distance relative to the width from a unit node to the first point.
    * This is only used in the case when x1+10 < x2, meaning that the left
    * connecting node is to the right of the right connecting unit.
     */
    public static double DISTANCE_FACTOR = 4;


    /*
    * The index offset relative to the width
    */
    public static double INDEXED_OFFSET_FACTOR = 2.2;

    /**
     * The type of the cable
     */
    private String type;

    /*
    * The JPanel to draw the cables on
    */
    protected Component surface;

    /**
     * the start and end node components for the cable (end is null if end point
     * is used)
     */
    private Component startcomp;
    private Component endcomp;


    /*
    * A vector of the points of the cable
    */
    private Vector points;

    /**
     * An flag indicating whether the initial points have not tampered with
     */
    private boolean valid = true;

    /**
     * the width of the cable
     */
    private double width = DEFAULT_WIDTH;

    /*
    * The colour of the cable (default = black)
    */
    private Color col = Color.black;

    /**
     * The background color of the cable (default = black)
     */
    private Color backcol = Color.black;

    /**
     * The colour of the nodes (default = drakgrey)
     */
    private Color nodecol = Color.darkGray;

    /*
    * if the left mouse button is pressed
    */
    boolean down = false;


    /**
     * The index of the point being dragged
     */
    int dragindex = -1;

    Point p1 = new Point(0, 0);


    public StableCable(Component panel, Component start) {
        this(DEFAULT_TYPE, panel, start);
    }

    public StableCable(String type, Component panel, Component start) {
        this.type = type;
        this.points = new Vector();
        this.surface = panel;
        this.startcomp = start;

        surface.addMouseListener(this);
        surface.addMouseMotionListener(this);

        calculateEndpoints();
    }

    public StableCable(Component panel, Component start, Component end) {
        this(DEFAULT_TYPE, panel, start, end);
    }

    public StableCable(String type, Component panel, Component start, Component end) {
        this.type = type;
        this.points = new Vector();
        this.surface = panel;
        this.startcomp = start;
        this.endcomp = end;

        surface.addMouseListener(this);
        surface.addMouseMotionListener(this);

        calculateEndpoints();
    }


    /**
     * @return the type of the cable
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of the cable
     */
    public void setType(String type) {
        this.type = type;
    }


    /**
     * @return the default width of the cable in pixels
     */
    public double getDefaultWidth() {
        return DEFAULT_WIDTH;
    }

    /**
     * @return the width of the cable in pixels
     */
    public double getWidth() {
        return width;
    }

    /**
     * Sets the width of the cable in pixels
     */
    public void setWidth(double width) {
        this.width = width;
    }


    /**
     * the distance between the component and the cable bend (in pixels)
     */
    private int getDistance() {
        return (int) (width * DISTANCE_FACTOR);
    }

    /**
     * the offset for each index (in pixels)
     */
    private int getIndexOffset() {
        return (int) (width * INDEXED_OFFSET_FACTOR);
    }

    /**
     * the size of each node (in pizels)
     */
    private int getNodeSize() {
        return (int) (width * NODE_SIZE_FACTOR);
    }


    /**
     * @return the start component of the stable cable
     */
    public Component getStartComponent() {
        return startcomp;
    }

    /**
     * @return the end component of the stable cable (null if unknown)
     */
    public Component getEndComponent() {
        return endcomp;
    }


    /**
     * @return the start point of this stable cable
     */
    public Point getStartPoint() {
        if (!points.isEmpty())
            return (Point) points.elementAt(0);
        else
            return new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);
    }

    /**
     * Set the point of the first node i.e. the start of the cable
     */
    protected void setStartPoint(Point p) {
        rescaleFromStart(p);

        if (!points.isEmpty())
            points.set(0, p);
        else
            points.add(p);

        recalculateMidPoints();
    }


    /**
     * @return the end point of this stable cable
     */
    public Point getEndPoint() {
        if (points.size() > 1)
            return (Point) points.elementAt(points.size() - 1);
        else
            return new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);
    }

    /**
     * Set the point of the last node i.e. the end of the cable
     */
    public void setEndPoint(Point p) {
        rescaleFromEnd(p);

        if (points.size() > 1)
            points.set(points.size() - 1, p);
        else
            points.add(p);

        recalculateMidPoints();
    }

    /**
     * Set the point of the last node (i.e. the end of the cable) relative to
     * the start point
     */
    public void setRelativeEndPoint(Point p) {
        Point startpoint = getStartPoint();
        Dimension size = startcomp.getSize();
        Point endpoint = new Point(startpoint.x + p.x - size.width, startpoint.y + p.y - (size.height / 2));

        if (points.size() > 1)
            points.set(points.size() - 1, endpoint);
        else
            points.add(endpoint);

        recalculateMidPoints();
    }

    /**
     * Resets the mid points to default locations
     */
    public void resetMidPoints() {
        valid = true;
        calculateInitialPoints();
    }

    /**
     * @return surface this cable is drawn upon
     */
    public Container getSurface() {
        return (Container) surface;
    }


    /**
     * Calculates the start and endpoints based on the current location of the
     * start and end components
     */
    private void calculateEndpoints() {
        Dimension size = startcomp.getSize();
        setStartPoint(SwingUtilities.convertPoint(startcomp, (size.width + 1) / 2, size.height / 2, surface));

        if (endcomp != null) {
            size = endcomp.getSize();
            setEndPoint(SwingUtilities.convertPoint(endcomp, size.width / 2, size.height / 2, surface));
        }
    }

    private void recalculateMidPoints() {
        if (valid)
            calculateInitialPoints();
    }

    private void calculateInitialPoints() {
        if (points.size() > 1) {
            points.insertElementAt(points.lastElement(), 1);
            points.setSize(2);

            Point p1 = (Point) points.elementAt(0);
            Point p2 = (Point) points.elementAt(1);

            if (p1.getY() == p2.getY()) {
            } else if (p1.getX() + (2 * getDistance()) < p2.getX()) {
                drawTwoPoint(p1, p2);
            } else {
                int y1 = (int) p1.getY();
                int y2 = (int) p2.getY();
                int x1 = (int) p1.getX();
                int x2 = (int) p2.getX();
                int hw = y1 + ((y2 - y1) / 2);

                if (p1.getY() > p2.getY()) {
                    x1 = getIndexedDistance(x1);
                    x2 = getReverseIndexedDistance(x2);
                } else {
                    x1 = getReverseIndexedDistance(x1);
                    x2 = getIndexedDistance(x2);
                }

                if (x1 + getDistance() <= x2 - getDistance())
                    drawTwoPoint(p1, p2);
                else {
                    points.insertElementAt(new Point(x1 + getDistance(), y1), 1);
                    points.insertElementAt(new Point(x1 + getDistance(), hw), 2);
                    points.insertElementAt(new Point(x2 - getDistance(), hw), 3);
                    points.insertElementAt(new Point(x2 - getDistance(), y2), 4);
                }
            }
        }
    }

    private void rescaleFromEnd(Point endpoint) {
        float xscale = 0;
        float yscale = 0;

        if (points.size() > 2) {
            Point oldend = (Point) points.get(points.size() - 1);
            Point start = (Point) points.get(0);
            Point midpoint;

            xscale = ((float) endpoint.x - start.x) / (oldend.x - start.x);
            yscale = ((float) endpoint.y - start.y) / (oldend.y - start.y);

            for (int count = 1; count < points.size() - 1; count++) {
                midpoint = (Point) points.get(count);
                midpoint.x = (int) ((midpoint.x - start.x) * xscale) + start.x;
                midpoint.y = (int) ((midpoint.y - start.y) * yscale) + start.y;
            }
        }
    }

    private void rescaleFromStart(Point startpoint) {
        float xscale = 0;
        float yscale = 0;

        if (points.size() > 2) {
            Point oldstart = (Point) points.get(0);
            Point end = (Point) points.get(points.size() - 1);
            Point midpoint;

            xscale = ((float) startpoint.x - end.x) / (oldstart.x - end.x);
            yscale = ((float) startpoint.y - end.y) / (oldstart.y - end.y);

            for (int count = 1; count < points.size() - 1; count++) {
                midpoint = (Point) points.get(count);
                midpoint.x = (int) ((midpoint.x - end.x) * xscale) + end.x;
                midpoint.y = (int) ((midpoint.y - end.y) * yscale) + end.y;
            }
        }

    }

    private void drawTwoPoint(Point p1, Point p2) {
        int x1 = (int) p1.getX();
        int x2 = (int) p2.getX();
        int hw = x1 + ((x2 - x1) / 2);

        if (p1.getY() > p2.getY())
            hw = getIndexedDistance(hw);
        else
            hw = getReverseIndexedDistance(hw);

        points.insertElementAt(new Point(hw, (int) p1.getY()), 1);
        points.insertElementAt(new Point(hw, (int) p2.getY()), 2);
    }


    private int getIndexedDistance(int dist) {
        int index = 0;
        int maxindex = 1;

        if ((startcomp instanceof NodeComponent) && (((NodeComponent) startcomp).getNode() != null)) {
            Node node = ((NodeComponent) startcomp).getNode();

            if (node.getTask() != null) {
                index = node.getAbsoluteNodeIndex();
                maxindex = node.getTask().getOutputNodeCount();
            }
        }


        return Math.max(dist - (getIndexOffset() * maxindex / 2) + (getIndexOffset() * index), getIndexOffset());
    }

    private int getReverseIndexedDistance(int dist) {
        int index = 0;
        int maxindex = 1;

        if ((startcomp instanceof NodeComponent) && (((NodeComponent) startcomp).getNode() != null)) {
            Node node = ((NodeComponent) startcomp).getNode();

            if (node.getTask() != null) {
                index = node.getAbsoluteNodeIndex();
                maxindex = node.getTask().getOutputNodeCount();
            }
        }

        return Math.max(dist - (getIndexOffset() * maxindex / 2) + (getIndexOffset() * (maxindex - index)), getIndexOffset());
    }

    /**
     * Draw the stable cable
     */
    public void drawCable(Graphics g) {
        if ((startcomp.getParent() != null) && ((endcomp == null) || (endcomp.getParent() != null))) {
            calculateEndpoints();

            drawCables(g);
            drawNodes(g);
        }
    }


    public Point getPointOnLine(int m, Point p) {
        Point p1 = (Point) points.elementAt(m);
        Point p2 = (Point) points.elementAt(m + 1);
        if (p1.getX() == p2.getX()) {
            return new Point((int) p1.getX(), (int) p.getY());
        } else if (p1.getY() == p2.getY()) {
            return new Point((int) p.getX(), (int) p1.getY());
        } else {
            int y = mod((int) (p.getY() - p1.getY()));
            int dy = 1; //getDY(p1,p2);
            return new Point((int) p.getX(), (int) p1.getY() + y * dy);
        }
    }

    public int getDY(Point p1, Point p2) {
        int dY = mod((int) (p2.getY() - p1.getY()));
        int dX = mod((int) (p2.getX() - p1.getX()));

        double length = Math.sqrt(dX * dX + dY * dY);
        double scale = (double) (getWidth()) / (2 * length);
        double ddy = -scale * (double) dY;
        return (int) ddy;
    }

    private void drawCables(Graphics g) {
        int size = points.size();

        for (int x = 0; x < 2; x++)
            for (int i = 0; i < size - 1; i++) {
                Point a = (Point) points.elementAt(i);
                Point b = (Point) points.elementAt(i + 1);

                int x1 = (int) a.getX();
                int y1 = (int) a.getY();
                int x2 = (int) b.getX();
                int y2 = (int) b.getY();

                if (x == 0) {
                    g.setColor(Color.black);
                    ThickLine.drawLine(x1, y1, x2, y2, (int) Math.max(getWidth(), 1), g);
                } else {
                    g.setColor(col);
                    ThickLine.drawLine(x1, y1, x2, y2, 1, g);
                }
            }
    }

    private void drawNodes(Graphics g) {
        int size = points.size();
        for (int i = 1; i < size - 1; i++) {
            Point p = (Point) points.elementAt(i);
            int x = (int) p.getX() - (int) Math.floor(getNodeSize() / 2);
            int y = (int) p.getY() - (int) Math.floor(getNodeSize() / 2);

            g.setColor(nodecol);
            g.fillOval(x, y, getNodeSize(), getNodeSize());
        }
    }

    /*
 	 * Calculates if a point lies on a cable, within PIXELS pixels
 	 */
    private boolean isPointOnLine(Point p) {

        double x1, x2, y1, y2;

        double x = p.getX();
        double y = p.getY();
        int size = points.size();
        for (int i = 0; i < size - 1; i++) {

            Point p1 = (Point) points.elementAt(i);
            Point p2 = (Point) points.elementAt(i + 1);

            if (p1.getX() <= p2.getX()) {
                x1 = p1.getX();
                x2 = p2.getX();
            } else {
                x1 = p2.getX();
                x2 = p1.getX();
            }

            if (p1.getY() <= p2.getY()) {
                y1 = p1.getY();
                y2 = p2.getY();
            } else {
                y1 = p2.getY();
                y2 = p1.getY();
            }

            if (((x >= x1 - PIXELS) && (x <= x2 + PIXELS)) &&
                    ((y >= y1 - PIXELS) && (y <= y2 + PIXELS))) {
                return true;
            }
        }

        return false;
    }

    private int getLeftPointIndex(Point p) {

        double x1, x2, y1, y2;

        int x = (int) p.getX();
        int y = (int) p.getY();
        int size = points.size();
        for (int i = 0; i < size - 1; i++) {


            Point p1 = (Point) points.elementAt(i);
            Point p2 = (Point) points.elementAt(i + 1);

            if (p1.getX() <= p2.getX()) {
                x1 = p1.getX();
                x2 = p2.getX();
            } else {
                x1 = p2.getX();
                x2 = p1.getX();
            }

            if (p1.getY() <= p2.getY()) {
                y1 = p1.getY();
                y2 = p2.getY();
            } else {
                y1 = p2.getY();
                y2 = p1.getY();
            }

            if (((x >= x1 - PIXELS) && (x <= x2 + PIXELS)) &&
                    ((y >= y1 - PIXELS) && (y <= y2 + PIXELS))) {
                return i;
            }
        }

        return 0;

    }

    private int mod(int n) {
        if (n > 0) {
            return n;
        } else {
            return -n;
        }
    }

    private void deleteNode(int index) {
        points.removeElementAt(index);
    }

    private boolean isNode(Point p) {
        int x1 = (int) p.getX();
        int y1 = (int) p.getY();
        int size = points.size();
        for (int i = 1; i < size - 1; i++) {
            Point p2 = (Point) points.elementAt(i);
            int x2 = (int) p2.getX();
            int y2 = (int) p2.getY();
            if ((mod(x1 - x2) <= PIXELS) && (mod(y2 - y1) <= PIXELS)) {
                return true;
            }
        }
        return false;
    }

    /*
	 * Returns a point that is closest to point p
	 */
    private int getPointIndex(Point p) {
        int x1 = (int) p.getX();
        int y1 = (int) p.getY();
        int size = points.size();
        for (int i = 0; i < size; i++) {
            Point p2 = (Point) points.elementAt(i);
            int x2 = (int) p2.getX();
            int y2 = (int) p2.getY();
            if ((mod(x1 - x2) <= PIXELS) && (mod(y2 - y1) <= PIXELS)) {
                return i;
            }
        }
        return -1;
    }

    public void mouseClicked(MouseEvent ev) {
        if (ev.getClickCount() == 2) {
            Point p = ev.getPoint();

            if (isNode(p)) {
                deleteNode(getPointIndex(p));
            } else if (isPointOnLine(p)) {
                int m = getLeftPointIndex(p);
                points.insertElementAt(p, m + 1);

                valid = false;

                surface.repaint();
            }
        }
    }

    public void mouseEntered(MouseEvent ev) {
    }

    public void mouseExited(MouseEvent ev) {
    }

    public void mousePressed(MouseEvent ev) {
        Point p = ev.getPoint();

        if (isNode(p)) {
            dragindex = getPointIndex(p);
            down = true;
        } else
            dragindex = -1;
    }

    public void mouseReleased(MouseEvent ev) {
        if (dragindex != -1) {
            down = false;
            valid = false;
        }

        surface.repaint();
    }

    public void mouseDragged(MouseEvent ev) {
        if ((down) && (dragindex != -1)) {
            points.setElementAt(new Point(ev.getX(), ev.getY()), dragindex);
            surface.repaint();
        }
    }

    public void mouseMoved(MouseEvent ev) {
    }

    public String toString() {
        String s = new String();
        s = s.concat(Integer.toString(points.size()) + ":");
        s = s.concat(Integer.toString(col.getRGB()) + ":");
        int size = points.size();
        for (int i = 0; i < size; i++) {
            Point p = (Point) points.elementAt(i);
            int x = (int) p.getX();
            int y = (int) p.getY();
            s = s.concat(Integer.toString(x) + "," + Integer.toString(y) + "/");
        }
        s = s.substring(0, s.length() - 1);
        return s;
    }

    /*public void setString(String s) {
        String[] delim = new String[1];
        delim[0] = new String(":");
        StringSplitter split1 = new StringSplitter(s, delim);
        int size = Integer.parseInt((String) split1.elementAt(0));
        col = new Color(Integer.parseInt((String) split1.elementAt(1)));
        delim[0] = new String("/");
        StringSplitter split2 = new StringSplitter((String) split1.elementAt(2), delim);
        delim[0] = new String(",");
        for (int i = 0; i < size; i++) {
            StringSplitter split3 = new StringSplitter((String) split2.elementAt(i), delim);
            int x = Integer.parseInt((String) split3.elementAt(0));
            int y = Integer.parseInt((String) split3.elementAt(1));
            Point p = new Point(x, y);
            points.add(p);
        }
    }*/


    /**
     * @return the color of this cable
     */
    public Color getColor() {
        return col;
    }

    /**
     * Sets the color of this cable
     */
    public void setColor(Color col) {
        this.col = col;
    }

    /**
     * @return the background color of this cable
     */
    public Color getBackgroundColor() {
        return backcol;
    }

    /**
     * Sets the color of this cable
     */
    public void setBackgroundColor(Color col) {
        this.backcol = col;
    }

    /**
     * @return the color of the nodes
     */
    public Color getNodeColor() {
        return nodecol;
    }

    /**
     * Sets the color of the nodes
     */
    public void setNodeColor(Color nodecol) {
        this.nodecol = nodecol;
    }

}
