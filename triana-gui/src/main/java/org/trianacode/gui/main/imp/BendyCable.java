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

import javax.swing.*;
import java.awt.*;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;

/**
 * A bendy cable for linking components.
 *
 * @author Ian Wang
<<<<<<< BendyCable.java
 * @version $Revision: 4048 $
=======
 * @version $Revision: 4048 $
>>>>>>> 1.1.2.1
 * @created 15th November 2005
<<<<<<< BendyCable.java
 * @date $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
=======
 * @date $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
>>>>>>> 1.1.2.1
 */

public class BendyCable implements DrawCable {

    /*
     * The default width of the cable
     */
    public static int DEFAULT_WIDTH = 3;


    private static int TOP = 0;
    private static int RIGHT = 1;
    private static int BOTTOM = 2;
    private static int LEFT = 3;

    private static int BEND = 1;
    private static int S_BEND = 2;
    private static int U_BEND = 3;
    private static int U_BEND_PLUS_BEND = 4;
    private static int U_BEND_PLUS_U_BEND = 5;

    private static int MINIMUM_DIST_FACTOR = 5;
    private static int U_BEND_DIST_FACTOR = 20;

    private String type;
    private Component startcomp;
    private Component endcomp;
    private Point startpt;
    private Point endpt;
    private Container surface;

    private int startdir = RIGHT;
    private int enddir = LEFT;

    private double width = 1;
    private Color col = Color.black;

    private Stroke stroke = new BasicStroke((float) width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    private int mindist = (int) width * MINIMUM_DIST_FACTOR;
    private int udist = (int) width * U_BEND_DIST_FACTOR;


    public BendyCable(Component startcomp, Component endcomp, Container surface) {
        this(DEFAULT_TYPE, startcomp, endcomp, surface);
    }

    public BendyCable(String type, Component startcomp, Component endcomp, Container surface) {
        this.type = type;
        this.startcomp = startcomp;
        this.endcomp = endcomp;
        this.surface = surface;

        calaculateEndpoints();
    }

    public BendyCable(Component startcomp, Container surface) {
        this(DEFAULT_TYPE, startcomp, surface);
    }

    public BendyCable(String type, Component startcomp, Container surface) {
        this.type = type;
        this.startcomp = startcomp;
        this.surface = surface;

        calaculateEndpoints();
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


    private Point getDirPoint(Component comp, Container surface, int dir) {
        Dimension size = comp.getSize();
        Point pos;

        if (dir == TOP)
            pos = new Point(size.width / 2, 0);
        else if (dir == RIGHT)
            pos = new Point(size.width, (size.height / 2));
        else if (dir == BOTTOM)
            pos = new Point((size.width / 2), size.height);
        else
            pos = new Point(0, (size.height / 2));

        return SwingUtilities.convertPoint(comp, pos.x, pos.y, surface);
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
        return startpt;
    }

    /**
     * @return the end point of this stable cable
     */
    public Point getEndPoint() {
        return endpt;
    }

    /**
     * Set the point of the last node i.e. the end of the cable
     */
    public void setEndPoint(Point p) {
        this.endpt = p;
    }

    /**
     * Set the point of the last node (i.e. the end of the cable) relative to
     * the start point
     */
    public void setRelativeEndPoint(Point p) {
        Point startpoint = getStartPoint();
        Dimension size = startcomp.getSize();
        endpt = new Point(startpoint.x + p.x - size.width, startpoint.y + p.y - (size.height / 2));
    }

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
        this.mindist = (int) width * MINIMUM_DIST_FACTOR;
        this.udist = (int) width * U_BEND_DIST_FACTOR;

        this.stroke = new BasicStroke((float) width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    }

    /**
     * @return surface this cable is drawn upon
     */
    public Container getSurface() {
        return surface;
    }


    private void calaculateEndpoints() {
        this.startpt = getDirPoint(startcomp, surface, startdir);

        if (endcomp != null)
            this.endpt = getDirPoint(endcomp, surface, enddir);
    }


    public void drawCable(Graphics g) {
        calaculateEndpoints();

        if (endpt != null) {
            Point offendpt = new Point((int) (endpt.x - (2 * width)), this.endpt.y);

            Graphics2D graphs = (Graphics2D) g;
            graphs.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color defaultcol = graphs.getColor();
            Stroke defaultstr = graphs.getStroke();
            graphs.setColor(col);
            graphs.setStroke(stroke);

            drawBend(startpt, offendpt, graphs);

            int[] xpt = new int[] {endpt.x, (int) (endpt.x - (2 * width)), (int) (endpt.x - (2 * width))};
            int[] ypt = new int[] {endpt.y, (int) (endpt.y - (1.5 * width)), (int) (endpt.y + (1.5 * width))};
            graphs.fillPolygon(xpt, ypt, 3);

            graphs.setColor(defaultcol);
            graphs.setStroke(defaultstr);
        }
    }

    private void drawBend(Point startpt, Point endpt, Graphics2D graphs) {
        Shape shape;
        int bend = getBendType(startpt, endpt);

        if (bend == BEND)
            shape = getBend(startpt, endpt, startdir);
        else if (bend == S_BEND)
            shape = getSBend(startpt, endpt, startdir);
        else if (bend == U_BEND)
            shape = getUBend(startpt, endpt, startdir, udist);
        else if (bend == U_BEND_PLUS_BEND)
            shape = getUBendPlusBend(startpt, endpt);
        else
            shape = getUBendPlusUBend(startpt, endpt);

        graphs.draw(shape);
    }

    /**
     * @return the type of bends required to get from start to end
     */
    private int getBendType(Point startpos, Point endpos) {
        boolean infront;
        int bends;

        if (startdir == TOP)
            infront = (startpos.getY() - mindist) > endpos.getY();
        else if (startdir == RIGHT)
            infront = (startpos.getX() + mindist) < endpos.getX();
        else if (startdir == BOTTOM)
            infront = (startpos.getY() + mindist) < endpos.getY();
        else
            infront = (startpos.getX() - mindist) > endpos.getX();

        if (startdir == enddir) {
            bends = U_BEND;
        } else if ((startdir % 2) != (enddir % 2)) {
            if (infront)
                bends = BEND;
            else
                bends = U_BEND_PLUS_BEND;
        } else {
            if (infront)
                bends = S_BEND;
            else
                bends = U_BEND_PLUS_U_BEND;
        }

        return bends;
    }

    /**
     * @return an array of shapes making the cable
     */
    private Shape getBend(Point startpos, Point endpos, int dir) {
        if (dir % 2 == 0)
            return new QuadCurve2D.Double(startpos.getX(), startpos.getY(), startpos.getX(), endpos.getY(), endpos.getX(), endpos.getY());
        else
            return new QuadCurve2D.Double(startpos.getX(), startpos.getY(), endpos.getX(), startpos.getY(), endpos.getX(), endpos.getY());
    }

    /**
     * @return an array of shapes making the cable
     */
    private Shape getSBend(Point startpos, Point endpos, int dir) {
        if (dir % 2 == 0)
            return new CubicCurve2D.Double(startpos.getX(), startpos.getY(), startpos.getX(), endpos.getY(), endpos.getX(), startpos.getY(), endpos.getX(), endpos.getY());
        else
            return new CubicCurve2D.Double(startpos.getX(), startpos.getY(), endpos.getX(), startpos.getY(), startpos.getX(), endpos.getY(), endpos.getX(), endpos.getY());
    }

    /**
     * @return an array of shapes making the cable
     */
    private Shape getUBend(Point startpos, Point endpos, int dir, int ubenddist) {
        Point2D ctrlpt;

        if (dir == TOP)
            ctrlpt = new Point.Double((startpos.getX() + endpos.getX()) / 2, Math.min(startpos.getX(), endpos.getX()) - ubenddist);
        else if (dir == RIGHT)
            ctrlpt = new Point.Double(Math.max(startpos.getX(), endpos.getX()) + ubenddist, (startpos.getY() + endpos.getY()) / 2);
        else if (dir == BOTTOM)
            ctrlpt = new Point.Double((startpos.getX() + endpos.getX()) / 2, Math.max(startpos.getX(), endpos.getX()) + ubenddist);
        else
            ctrlpt = new Point.Double(Math.min(startpos.getX(), endpos.getX()) - ubenddist, (startpos.getY() + endpos.getY()) / 2);

        return new QuadCurve2D.Double(startpos.getX(), startpos.getY(), ctrlpt.getX(), ctrlpt.getY(), endpos.getX(), endpos.getY());
    }

    /**
     * @return an array of shapes making the cable
     */
    private Shape getUBendPlusBend(Point startpos, Point endpos) {
        Point midpt = new Point((int) ((startpos.getX() + endpos.getX()) / 2), (int) ((startpos.getY() + endpos.getY()) / 2));

        Shape ubend = getUBend(startpos, midpt, startdir, udist);
        Shape bend = getBend(midpt, endpos, (startdir + 2) % 4);

        GeneralPath path = new GeneralPath(ubend);
        path.append(bend, true);

        return path;
    }

    /**
     * @return an array of shapes making the cable
     */
    private Shape getUBendPlusUBend(Point startpos, Point endpos) {
        Point midpt = new Point((int) ((startpos.getX() + endpos.getX()) / 2), (int) ((startpos.getY() + endpos.getY()) / 2));
        int ubenddist = getUBendDistance(startpos, endpos);

        Shape ubend = getUBend(startpos, midpt, startdir, ubenddist);
        Shape bend = getUBend(midpt, endpos, (startdir + 2) % 4, ubenddist);

        GeneralPath path = new GeneralPath(ubend);
        path.append(bend, true);

        return path;
    }

    private int getUBendDistance(Point startpos, Point endpos) {
        return (int) Math.max(Math.min(startpos.getX() - endpos.getX() + mindist, udist), 0);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("BendyCable");
        Container cont = frame.getContentPane();
        cont.add(new TestPanel());


        frame.setSize(600, 600);
        frame.setVisible(true);
    }

    private static class TestPanel extends JPanel {

        BendyCable cable;

        public TestPanel() {
            setLayout(null);

            JButton but1 = new JButton();
            but1.setPreferredSize(new Dimension(50, 50));
            but1.setLocation(100, 100);

            JButton but2 = new JButton();
            but2.setPreferredSize(new Dimension(50, 50));
            but2.setLocation(200, 200);

            add(but1);
            add(but2);

            cable = new BendyCable(but1, but2, this);
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            cable.drawCable(g);
        }

    }

}
