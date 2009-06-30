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

package org.trianacode.gui.components.map;


import org.trianacode.taskgraph.TDimension;
import org.trianacode.taskgraph.TaskLayoutDetails;

import java.awt.*;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * The layout manager responsible for positioning components on the map.
 *
 * @author      Ian Wang
 * @created     20th September 2004
 * @version     $Revision: 4048 $
 * @date        $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $

 */

public class MapLayout implements LayoutManager, TaskLayoutDetails {

    /**
     * the default size of the map
     */
    private Dimension mapsize;

    /**
     * the size factor for the components (<1 = smaller >1 = larger)
     */
    private double compfactor = 1;

    /**
     * a hashtable for the point on the map of each location
     */
    private Hashtable loctable = new Hashtable();

    /**
     * a hashtable for the location of each component
     */
    private Hashtable comptable = new Hashtable();

    /**
     * the current x/y scale for components
     */
    private double cscale = 1.0;


    /**
     * Constructs a map layout for the specified mapsize
     */
    public MapLayout(Dimension mapsize) {
        this.mapsize = mapsize;
    }


    /**
     * Constructs a map layout for the specified mapsize
     *
     * @param compfactor a scale factor for the components (default = 1).
     */
    public MapLayout(Dimension mapsize, double compfactor) {
        this.mapsize = mapsize;
        this.compfactor = compfactor;
    }



    /**
     * Sets the map size used by this layout manager
     */
    public void setMapSize(Dimension mapsize) {
        this.mapsize = mapsize;
    }

    /**
     * @return the current map size
     */
    public Dimension getMapsize() {
        return mapsize;
    }


    /**
     * Sets the poin on the map for the specified location
     */
    public void setMapPoint(String location, Point point) {
        loctable.put(location, point);
    }

    /**
     * @return the point (0<=x<=1, 0<=y<=1) on the map for the specified location
     */
    public Point getMapPoint(String location) {
        if (loctable.containsKey(location))
            return (Point) loctable.get(location);
        else
            return null;
    }

    /**
     * @return true if there is a point registered for the specified location
     */
    public boolean isMapPoint(String location) {
        return loctable.containsKey(location);
    }


    /**
     * @return the size of the left border of the workspace
     */
    public int getLeftBorder() {
        return 0;
    }

    /**
     * @return the size of the right border of the workspace
     */
    public int getTopBorder() {
        return 0;
    }


    /**
     * @return the dimensions of a standard triana task
     */
    public TDimension getTaskDimensions() {
        Dimension defsize = MapLayoutConstants.DEFAULT_TOOL_SIZE;
        return new TDimension((int) (defsize.width * compfactor * cscale), (int) (defsize.height * compfactor * cscale));
    }


    /**
     * If the layout manager uses a per-component string,
     * adds the component <code>comp</code> to the layout,
     * associating it
     * with the string specified by <code>name</code>.
     *
     * @param name the string to be associated with the component
     * @param comp the component to be added
     */
    public void addLayoutComponent(String name, Component comp) {
        comptable.put(comp, name);
    }

    /**
     * Removes the specified component from the layout.
     *
     * @param comp the component to be removed
     */
    public void removeLayoutComponent(Component comp) {
        comptable.remove(comp);
    }


    /**
     * Calculates the minimum size dimensions for the specified
     * container, given the components it contains.
     *
     * @param parent the component to be laid out
     * @see #preferredLayoutSize
     */
    public Dimension minimumLayoutSize(Container parent) {
        return preferredLayoutSize(parent);
    }

    /**
     * Calculates the preferred size dimensions for the specified
     * container, given the components it contains.
     *
     * @param parent the container to be laid out
     * @see #minimumLayoutSize
     */
    public Dimension preferredLayoutSize(Container parent) {
        return mapsize;
    }

    /**
     * Lays out the specified container.
     *
     * @param parent the container to be laid out
     */
    public void layoutContainer(Container parent) {
        Enumeration enumeration = comptable.keys();
        Dimension parentsize = parent.getSize();

        cscale = Math.min(((double) parentsize.width) / mapsize.width, ((double) parentsize.height) / mapsize.height);

        TDimension compsize = getTaskDimensions();
        Component comp;
        Point pos;
        int noloc = 0;

        while (enumeration.hasMoreElements()) {
            comp = (Component) enumeration.nextElement();
            comp.setSize(new Dimension((int)compsize.getWidth(), (int)compsize.getHeight()));

            pos = getLocation((String) comptable.get(comp));

            if (pos != null) {
                comp.setLocation((int) ((parentsize.width * (pos.getX() / mapsize.width) - (compsize.getWidth() / 2))),
                        (int) ((parentsize.height * (pos.getY() / mapsize.height) - (compsize.getHeight() / 2))));
            } else
                comp.setLocation((int)compsize.getWidth() * noloc++, 0);
        }
    }

    /**
     * @return the location for the specified comp, or null if not known
     */
    private Point getLocation(String location) {
        Enumeration enumeration = loctable.keys();
        String key;
        Point loc = null;
        int matchlen = 0;

        while(enumeration.hasMoreElements()) {
            key = (String) enumeration.nextElement();

            if ((key.length() > matchlen) && (location.startsWith(key))) {
                loc = (Point) loctable.get(key);
                matchlen = key.length();
            }
        }

        return loc;
    }

}
