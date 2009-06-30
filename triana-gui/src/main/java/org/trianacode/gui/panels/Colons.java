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

import javax.swing.*;
import java.awt.*;

/**
 * User: andrew
 * Date: 24-May-2004
 * Time: 21:06:06
 */
public class Colons extends JPanel {

    private Color fg = Color.GREEN;


    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        Color c = g2.getColor();
        g2.setColor(getForeground());
        Dimension d = getSize();

        int center = (d.width / 2);
        int halfHeight = (d.height / 2);
        int circ = center - (center / 2);

        paintColon(circ, halfHeight - (halfHeight / 3), center, g);
        paintColon(circ, halfHeight + (halfHeight / 3), center, g);

        g2.setColor(c);
    }

    private void paintColon( int x, int y, int dim, Graphics g) {
        g.fillOval(x, y, dim, dim);
    }

    public void addNotify() {
        super.addNotify();
        setBackground(getParent().getBackground());
        fg = getParent().getForeground();
        setForeground(getParent().getBackground());
    }

    public void blink(int blinks, int speed) {
        final int blinkcount = blinks * 2;
        final int blinkspeed = speed;

        Thread thread = new Thread() {
            public void run() {
                int count = 0;
                Color bg = getBackground();
                while(count < blinkcount) {
                    if(getForeground().equals(fg)) {
                        setForeground(bg);
                    }
                    else {
                        setForeground(fg);
                    }
                    count++;
                    try {
                        sleep(blinkspeed);
                    } catch(InterruptedException ie) {}
                }
            }
        };
        thread.setPriority(Thread.NORM_PRIORITY);
        thread.start();
    }
}

