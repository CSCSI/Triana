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
 * Created by IntelliJ IDEA.
 * User: Andrew
 * Date: 23-May-2004
 * Time: 16:21:45
 * To change this template use File | Settings | File Templates.
 */
public class RetroDigit extends JPanel {
    private Line[] lines = new Line[7];
    private int digit = 0;


    static boolean[][] digits = new boolean[][]{
        {true, true, true, false, true, true, true}, // 0
        {false, false, true, false, false, false, true}, // 1
        {false, true, true, true, true, true, false}, // 2
        {false, true, true, true, false, true, true}, // 3
        {true, false, true, true, false, false, true}, // 4
        {true, true, false, true, false, true, true}, // 5
        {true, true, false, true, true, true, true}, // 6
        {false, true, true, false, false, false, true}, // 7
        {true, true, true, true, true, true, true}, // 8
        {true, true, true, true, false, true, true}, // 9
    };

    static final int TOP_LEFT = 0;
    static final int TOP = 1;
    static final int TOP_RIGHT = 2;
    static final int CENTER = 3;
    static final int BOTTOM_LEFT = 4;
    static final int BOTTOM = 5;
    static final int BOTTOM_RIGHT = 6;


    public RetroDigit() {
        super();
        for (int i = 0; i < lines.length; i++) {
            lines[i] = new Line(i);

        }
    }


    public void addNotify() {
        super.addNotify();
        setBackground(getParent().getBackground());
        setForeground(getParent().getForeground());
    }

    public void setDigit(int digit) {
        this.digit = digit;
        for (int i = 0; i < lines.length; i++) {
            lines[i].setVisibility(digit);
        }
    }

    public int getDigit() {
        return digit;
    }


    public void blink(int blinks, int speed) {
        final int blinkcount = blinks * 2;
        final int blinkspeed = speed;

        Thread thread = new Thread() {
            public void run() {
                int count = 0;
                Color bg = getBackground();
                Color fg = getForeground();
                while(count < blinkcount) {
                    if(getForeground().equals(fg)) {
                        setForeground(bg);
                    }
                    else {
                        setForeground(fg);
                    }
                    getToolkit().beep();
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
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        Color c = g2.getColor();
        g2.setColor(getForeground());
        Dimension d = getSize();

        int inset = d.width / 20;
        int lineWidth = (d.width / 8);
        int halfHeight = (d.height / 2);
        int halfWidth = d.width - (lineWidth * 2);

        for (int i = 0; i < lines.length; i++) {
            lines[i].drawLine(lineWidth, halfHeight, halfWidth, inset, g2);
        }
        g2.setColor(c);
    }

    class Line {

        private boolean visible = true;
        private int pos;

        Line(int pos) {
            this.pos = pos;
        }

        public boolean isVisible() {
            return visible;
        }

        public void setVisible(boolean visible) {
            this.visible = visible;
        }

        public void setVisibility(int digit) {
            setVisible(digits[digit][pos]);
        }

        public int getPosition() {
            return pos;
        }

        public void drawLine(int width, int length, int wlength, int inset, Graphics2D g2) {
            if (isVisible()) {
                switch (pos) {
                    case TOP_LEFT:
                        paint(inset, inset, width, length, true, g2);
                        break;
                    case TOP:
                        paint(inset * 2, inset, width, wlength, false, g2);
                        break;
                    case TOP_RIGHT:
                        paint(wlength + inset, inset, width, length, true, g2);
                        break;
                    case CENTER:
                        paint(inset * 2, length - inset, width, wlength, false, g2);
                        break;
                    case BOTTOM_LEFT:
                        paint(inset, length - inset, width, length, true, g2);
                        break;
                    case BOTTOM:
                        paint(inset * 2, (length * 2) - (inset * 3), width, wlength, false, g2);
                        break;
                    case BOTTOM_RIGHT:
                        paint(wlength + inset, length - inset, width, length, true, g2);
                        break;
                }

            }

        }

        private void paint(int x, int y, int width, int length, boolean vertical, Graphics2D g2) {
            int[] exes = new int[6];
            int[] whys = new int[6];
            if (vertical) {
                exes[0] = x + (width / 2);
                exes[1] = x + width;
                exes[2] = x + width;
                exes[3] = x + (width / 2);
                exes[4] = x;
                exes[5] = x;

                whys[0] = y + (width / 2);
                whys[1] = y + width;
                whys[2] = y + (length - width);
                whys[3] = y + (length - (width / 2));
                whys[4] = y + (length - width);
                whys[5] = y + width;
            } else {
                exes[0] = x + (width / 2);
                exes[1] = x + width;
                exes[2] = x + (length - width);
                exes[3] = x + (length - (width / 2));
                exes[4] = x + (length - width);
                exes[5] = x + width;

                whys[0] = y + (width / 2);
                whys[1] = y;
                whys[2] = y;
                whys[3] = y + (width / 2);
                whys[4] = y + width;
                whys[5] = y + width;
            }
            g2.fillPolygon(exes, whys, 6);
        }
    }
}

