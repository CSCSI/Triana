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

package org.trianacode.gui.panels;

import java.awt.*;
import java.util.ArrayList;


/**
 * this layout manager gets the size of the container parent and sets out digits
 * according to that, as opposed to getting the preferred size of the components
 * within the container. So you should set the preferred size of the container
 * if using this class, otherwise it defaults to digit size of width 20, height 40.
 * Allowed constraints are DIGIT and SEPARATOR. By default a separator is a quarter the
 * width of a digit. Use the constructor that takes a float to change this.
 * If no constraints are specified, then DIGIT is used.
 */
public class DigitalLayout implements LayoutManager, LayoutManager2, java.io.Serializable {
    int hgap;
    int vgap;
    float sepFraction;
    private ArrayList comps = new ArrayList();
    private int digitCount = 0;
    private int sepCount = 0;
    private int defaultWidth = 20;
    private int defaultHeight = 40;
    private boolean triedParentPrefSize = false;
    private boolean triedParentMinSize = false;


    public static final String DIGIT = "digit";
    public static final String SEPARATOR = "separator";

    public DigitalLayout() {
        this(0, 0, 0.25f);
    }

    public DigitalLayout(int hgap, int vgap) {
        this(hgap, vgap, 0.25f);
    }

    /**
     *
     * @param hgap
     * @param vgap
     * @param separatorFraction a float specifying the fraction of the
     * width of a digit that the separators should occupy. The default is .25
     */
    public DigitalLayout(int hgap, int vgap, float separatorFraction) {
        this.hgap = hgap;
        this.vgap = vgap;
        this.sepFraction = separatorFraction;
    }
    public int getHgap() {
        return hgap;
    }

    public void setHgap(int hgap) {
        this.hgap = hgap;
    }

    public int getVgap() {
        return vgap;
    }

    public void setVgap(int vgap) {
        this.vgap = vgap;
    }

    public void addLayoutComponent(String name, Component comp) {
        if(name != DIGIT || name != SEPARATOR) {
            throw new IllegalArgumentException("constraint must be either digit or separator");
        }
        comps.add(new Holder(comp, name));
        if(name == DIGIT) {
            digitCount++;
        } else {
            sepCount++;
        }
    }

    public void removeLayoutComponent(Component comp) {
    }



    public Dimension preferredLayoutSize(Container parent) {
        if(!triedParentPrefSize) {
            triedParentPrefSize = true;
            Dimension dim = parent.getPreferredSize();
            return dim;
        } else {
            return defaultDimension(parent);
        }
    }

    public Dimension minimumLayoutSize(Container parent) {
        if(!triedParentMinSize) {
            triedParentMinSize = true;
            Dimension dim = parent.getMinimumSize();
            return dim;
        } else {
            return defaultDimension(parent);
        }
    }

    private Dimension defaultDimension(Container parent) {
        Insets insets = parent.getInsets();
        int digitsWidth = (defaultWidth + (hgap * 2)) * digitCount;
        int sepsWidth = ((int)(defaultWidth * sepFraction) + (hgap * 2)) * digitCount;
        int totalWidth = insets.left + insets.right + digitsWidth + sepsWidth;
        int totalHeight = insets.top + insets.bottom + defaultHeight + (vgap * 2);
        return new Dimension(totalWidth, totalHeight);

    }

    public void layoutContainer(Container parent) {
        synchronized (parent.getTreeLock()) {
            Dimension d = parent.getSize();
            Insets insets = parent.getInsets();
            int pwidth = d.width - insets.left - insets.right;
            int pheight = d.height - insets.top - insets.bottom;

            if(digitCount == 0) {
                throw new IllegalArgumentException("at least one digit must be added to DigitalLayout.");
            }
            int digitWidth = (pwidth - ((hgap * 2)) * (digitCount + sepCount)) / digitCount;
            int digitHeight = pheight - (vgap * 2);
            int sepWidth = 0;
            for(int i = 0; i < sepCount; i++) {
                sepWidth = (int)(digitWidth * sepFraction);
                digitWidth -= sepWidth / digitCount;
            }
            int currX = insets.left + hgap;
            int currY = insets.top + vgap;
            for(int i = 0; i < comps.size(); i++) {
                Holder entry = (Holder)comps.get(i);
                Component comp = entry.getComp();
                String tag = entry.getTag();
                if (tag == SEPARATOR) {
                    comp.setBounds(currX, currY, sepWidth, digitHeight);
                    currX += sepWidth + (hgap * 2);
                } else if (tag == DIGIT) {
                    comp.setBounds(currX, currY, digitWidth, digitHeight);
                    currX += digitWidth + (hgap * 2);
                }
            }
        }
    }

    public void addLayoutComponent(Component comp, Object constraints) {
        if(constraints == DIGIT || constraints == SEPARATOR) {
            comps.add(new Holder(comp, (String)constraints));
            if(constraints == DIGIT) {
                digitCount++;
            } else {
                sepCount++;
            }
        } else {
            comps.add(new Holder(comp, DIGIT));
            digitCount++;
        }
    }

    public Dimension maximumLayoutSize(Container target) {
        return null;
    }

    public float getLayoutAlignmentX(Container target) {
        return 0;
    }

    public float getLayoutAlignmentY(Container target) {
        return 0;
    }

    public void invalidateLayout(Container target) {
    }

    class Holder {

        private Component comp;
        private String tag;


        Holder(Component comp, String tag) {
            this.comp = comp;
            this.tag = tag;
        }

        public Component getComp() {
            return comp;
        }

        public String getTag() {
            return tag;
        }


    }
}

