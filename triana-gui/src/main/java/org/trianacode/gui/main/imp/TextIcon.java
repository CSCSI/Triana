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

import org.trianacode.gui.hci.color.ColorManager;
import org.trianacode.gui.hci.color.TrianaColorConstants;
import org.trianacode.gui.main.TrianaLayoutConstants;

import javax.swing.*;
import java.awt.*;

/**
 * An component that displays text to fill its set size (i.e. the text expands
 * with the component)
 *
 * @author Ian Wang
 *         <<<<<<< TextIcon.java
 * @version $Revision: 4048 $
 *          >>>>>>> 1.3.4.1
 * @created <<<<<<< TextIcon.java
 * @date $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 * =======
 * @date $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 * >>>>>>> 1.3.4.1
 */

public class TextIcon extends AbstractButton {

    private String text = "";
    private String mintext = "012345678";

    private Font curfont;

    private double curfactor;
    private Dimension prefsize = new Dimension();


    public TextIcon() {
        this("");
    }

    public TextIcon(String text) {
        this(text, CENTER);
    }

    public TextIcon(String text, int align) {
        this.text = text;
        setHorizontalAlignment(align);
        setFont(TrianaLayoutConstants.DEFAULT_FONT);
        setBackground(new Color(0, 0, 0, 0));
    }


    /**
     * Notifies this component that it now has a parent component.
     * When this method is invoked, the chain of parent components is
     * set up with <code>KeyboardAction</code> event listeners.
     *
     * @see #registerKeyboardAction
     */
    public void addNotify() {
        super.addNotify();
        updatePreferredSize();
    }


    /**
     * Sets the font for the icon
     */
    public void setFont(Font font) {
        super.setFont(font);
        updatePreferredSize();
    }


    /**
     * @return the text displayed on the icon
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the text displayed on the icon
     */
    public void setText(String text) {
        this.text = text;
        updatePreferredSize();
    }


    /**
     * @return the string used to calculate the minium width of the icon
     */
    public String getMinimumText() {
        return mintext;
    }

    /**
     * Sets the string used to calculate the minium width of the icon
     */
    public void setMinimumText(String mintext) {
        this.mintext = mintext;
        updatePreferredSize();
    }


    private void updatePreferredSize() {
        FontMetrics fontmetrics = null;

        if (getGraphics() != null)
            fontmetrics = getGraphics().getFontMetrics(getFont());

        if (fontmetrics != null) {
            int width = Math.max(fontmetrics.stringWidth(text), fontmetrics.stringWidth(mintext));

            Dimension size = new Dimension(width, fontmetrics.getAscent() + fontmetrics.getDescent());
            setPreferredSize(size);
            setMinimumSize(size);

            prefsize = size;
            invalidate();

            if (getParent() != null) {
                getParent().validate();
                getParent().repaint();
            }
        }
    }


    protected void paintComponent(Graphics graphs) {
        super.paintComponent(graphs);

        Color gcol = graphs.getColor();
        Font gfont = graphs.getFont();
        Dimension size = getSize();

        graphs.setColor(getBackground());
        graphs.fillRect(0, 0, size.width, size.height);

        graphs.setColor(getForeground());

        double newfactor = Math.min(((double) size.width) / prefsize.width,
                ((double) size.height) / prefsize.height);

        FontMetrics fontmetrics;
        int width;

        if ((curfont == null) || (newfactor != curfactor)) {
            updateCurrentFont(newfactor);

            int shrinkcount = 0;

            do {
                fontmetrics = graphs.getFontMetrics(curfont);
                width = fontmetrics.stringWidth(text);

                if (width > size.width) {
                    shrinkCurrentFont();
                    shrinkcount++;
                }
            } while ((width > size.width) && (shrinkcount < 4));
        } else {
            fontmetrics = graphs.getFontMetrics(curfont);
            width = fontmetrics.stringWidth(text);
        }

        graphs.setColor(ColorManager.getColor(TrianaColorConstants.NAME_ELEMENT));
        graphs.setFont(curfont);

        int left;

        if (getHorizontalAlignment() == LEFT)
            left = 0;
        else if (getHorizontalAlignment() == RIGHT)
            left = size.width - width;
        else
            left = (size.width - width) / 2;

        graphs.drawString(text, left, fontmetrics.getAscent());

        graphs.setFont(gfont);
        graphs.setColor(gcol);
    }

    private void updateCurrentFont(double newfactor) {
        Font font = getFont();

        curfont = font.deriveFont((float) (font.getSize() * newfactor));
        curfactor = newfactor;
    }

    private void shrinkCurrentFont() {
        curfont = curfont.deriveFont((float) (curfont.getSize2D() * 0.9));
    }

}
