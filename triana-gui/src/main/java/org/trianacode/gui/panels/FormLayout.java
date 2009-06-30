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
 * A layout manager for laying-out components in a form. It works like a Grid
 * layout with 2 columns except the height of each row is independent.
 *
 * @author      Ian Wang
 * @created     7th May 2003
 * @version     $Revision: 4048 $
 * @date        $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public class FormLayout implements LayoutManager {

    public static final String FULL_SPAN = "fullSpan";

    /**
     * the gaps between rows/columns
     */
    private int colgap;
    private int rowgap;

    /**
     * An array list of all the components that span two columns
     */
    private ArrayList span = new ArrayList();


    /**
     * Constructs a form layout
     */
    public FormLayout() {
    }

    /**
     * Constructs a form layout with the specified horizontal/vertical spacing
     * between rows/columns
     */
    public FormLayout(int rowgap, int columngap) {
        this.rowgap = rowgap;
        this.colgap = columngap;
    }


    public int getRowGap() {
        return rowgap;
    }

    public int getColumnGap() {
        return colgap;
    }


    public void addLayoutComponent(String s, Component component) {
        if (FULL_SPAN.equals(s) && (!span.contains(component)))
            span.add(component);
    }

    public void removeLayoutComponent(Component component) {
        span.remove(component);
    }


    public Dimension minimumLayoutSize(Container container) {
        int width1 = 0;
        int width2 = 0;
        int fullwidth = 0;
        int height = 0;

        Dimension minsize;
        boolean newcol = true;
        int tempheight = 0;

        Component[] comps = container.getComponents();

        for (int count = 0; count < comps.length; count++) {
            newcol = !newcol;

            Component comp = comps[count];
            minsize = comp.getMinimumSize();

            if (span.contains(comp)) {
                if (tempheight > 0)
                    height += tempheight + rowgap;

                fullwidth = minsize.width;
                height += minsize.height + rowgap;

                tempheight = 0;
                newcol = true;
            } else {
                if (minsize.height > tempheight)
                    tempheight = minsize.height;

                if ((!newcol) && (minsize.width > width1))
                    width1 = minsize.width;

                if ((newcol) && (minsize.width > width2))
                    width2 = minsize.width;

                if (newcol) {
                    height += tempheight + rowgap;
                    tempheight = 0;
                }
            }
        }

        if (tempheight > 0)
            height += tempheight + rowgap;

        Insets insets = container.getInsets();

        return new Dimension(Math.max(fullwidth, width1 + width2) + colgap + insets.left + insets.right, Math.max(height - rowgap, 0) + insets.top + insets.bottom);
    }

    public Dimension preferredLayoutSize(Container container) {
        int width1 = 0;
        int width2 = 0;
        int fullwidth = 0;
        int height = 0;

        Dimension prefsize;
        boolean newcol = true;
        int tempheight = 0;

        Component[] comps = container.getComponents();

        for (int count = 0; count < comps.length; count++) {
            newcol = !newcol;

            Component comp = comps[count];
            prefsize = comp.getPreferredSize();

            if (span.contains(comp)) {
                if (tempheight > 0)
                    height += tempheight + rowgap;

                fullwidth = prefsize.width;
                height += prefsize.height + rowgap;

                tempheight = 0;
                newcol = true;
            } else {
                if (prefsize.height > tempheight)
                    tempheight = prefsize.height;

                if ((!newcol) && (prefsize.width > width1))
                    width1 = prefsize.width;

                if ((newcol) && (prefsize.width > width2))
                    width2 = prefsize.width;

                if (newcol) {
                    height += tempheight + rowgap;
                    tempheight = 0;
                }
            }
        }

        if (tempheight > 0)
            height += tempheight + rowgap;

        Insets insets = container.getInsets();

        return new Dimension(Math.max(fullwidth, width1 + width2) + colgap + insets.left + insets.right, Math.max(height - rowgap, 0) + insets.top + insets.bottom);
    }


    public void layoutContainer(Container container) {
        Insets insets = container.getInsets();
        int y = insets.top;

        Component[] comps = container.getComponents();

        Dimension size1;
        Dimension size2;
        int width1 = 0;
        int height;

        for (int count = 0; count < comps.length; count = count + 2) {
            if (span.contains(comps[count]))
                count = count - 1;
            else {
                size1 = comps[count].getPreferredSize();

                if ((count + 1 < comps.length) && (!span.contains(comps[count + 1])))
                    size2 = comps[count + 1].getPreferredSize();
                else
                    size2 = new Dimension(0, 0);

                if (size1.width > width1)
                    width1 = size1.width;
            }
        }

        int width2 = container.getSize().width - width1 - insets.left - insets.right - rowgap;

        for (int count = 0; count < comps.length; count = count + 2) {
            if (span.contains(comps[count])) {
                size1 = comps[count].getPreferredSize();
                height = size1.height;

                comps[count].setBounds(insets.left, y, size1.width, size1.height);

                count = count - 1;
            } else {
                size1 = comps[count].getPreferredSize();

                if ((count + 1 < comps.length) && (!span.contains(comps[count + 1])))
                    size2 = comps[count + 1].getPreferredSize();
                else
                    size2 = new Dimension(0, 0);

                height = Math.max(size1.height, size2.height);

                comps[count].setBounds(insets.left, y, width1, height);

                if ((count + 1 < comps.length) && (!span.contains(comps[count + 1])))
                    comps[count + 1].setBounds(insets.left + colgap + width1, y, width2, height);

                if ((count + 1 < comps.length) && (span.contains(comps[count + 1])))
                    count = count - 1;
            }

            y += height + rowgap;
        }
    }

}
