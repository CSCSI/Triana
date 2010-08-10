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
package org.trianacode.gui.help;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Properties;

import javax.swing.JFrame;

/**
 * @author Unknown
 * @version $Revision: 4048 $
 */
public class JFrameEx extends JFrame {
    protected Point cascadeLocation;

    public JFrameEx(String title) {
        super(title);
    }

    public void setLocation(Properties properties, String name, Point point) {
        Integer x = new Integer(point.x);
        Integer y = new Integer(point.y);

        properties.put(name + ".Location.x", x.toString());
        properties.put(name + ".Location.y", y.toString());
    }

    public void setLocation(Properties properties, String name, int x, int y) {
        setLocation(properties, name, new Point(x, y));
    }

    public void setLocation(Properties properties, String name) {
        setLocation(properties, name, getLocation());
    }

    public Point getLocation(Properties properties, String name) {
        Point point = getLocation();

        try {
            point.setLocation(
                    Integer.parseInt(properties.getProperty(name + ".Location.x").trim()),
                    Integer.parseInt(properties.getProperty(name + ".Location.y").trim()));
        }
        catch (NumberFormatException ex1) {
        }
        catch (NullPointerException ex2) {
        }

        return point;
    }

    public void setSize(Properties properties, String name,
                        Dimension dimension) {
        Integer width = new Integer(dimension.width);
        Integer height = new Integer(dimension.height);

        properties.put(name + ".Size.width", width.toString());
        properties.put(name + ".Size.height", height.toString());
    }

    public void setSize(Properties properties, String name,
                        int width, int height) {
        setSize(properties, name, new Dimension(width, height));
    }

    public void setSize(Properties properties, String name) {
        setSize(properties, name, getSize());
    }

    public Dimension getSize(Properties properties, String name) {
        Dimension dimension = getSize();

        try {
            dimension.setSize(
                    Integer.parseInt(properties.getProperty(name + ".Size.width").trim()),
                    Integer.parseInt(properties.getProperty(name +
                            ".Size.height").trim()));
        }
        catch (NumberFormatException ex1) {
        }
        catch (NullPointerException ex2) {
        }

        return dimension;
    }

    public void setBounds(Properties properties, String name,
                          Rectangle rectangle) {
        setLocation(properties, name, rectangle.x, rectangle.y);
        setSize(properties, name, rectangle.width, rectangle.height);
    }

    public void setBounds(Properties properties, String name, int x, int y,
                          int width, int height) {
        setLocation(properties, name, x, y);
        setSize(properties, name, width, height);
    }

    public void setBounds(Properties properties, String name, Point point,
                          Dimension dimension) {
        setLocation(properties, name, point);
        setSize(properties, name, dimension);
    }

    public void setBounds(Properties properties, String name) {
        setBounds(properties, name, getBounds());
    }

    public Rectangle getBounds(Properties properties, String name) {
        return new Rectangle(getLocation(properties, name),
                getSize(properties, name));
    }

    public void setCascadeLocation(Point cascadeSize) {
        this.cascadeLocation = cascadeSize;
    }

    public void setCascadeLocation(int x, int y) {
        setCascadeLocation(new Point(x, y));
    }

    public Point getCascadeLocation() {
        return cascadeLocation;
    }

    public void cascade(JFrame frame) {
        Point parentLocation = frame.getLocation();

        try {
            setLocation(parentLocation.x + cascadeLocation.x,
                    parentLocation.y + cascadeLocation.y);
        }
        catch (NullPointerException ex) {
            System.out.println(parentLocation);
        }
    }

    public void cascade(JFrame frame, Point cascadeLocation) {
        Point parentLocation = frame.getLocation();

        try {
            setLocation(parentLocation.x + cascadeLocation.x,
                    parentLocation.y + cascadeLocation.y);
        }
        catch (NullPointerException ex) {
            System.out.println(parentLocation);
        }
    }

    public void cascade(JFrame frame, int dx, int dy) {
        cascade(frame, new Point(dx, dy));
    }
}



