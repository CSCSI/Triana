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
package org.trianacode.gui;

import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.gui.util.Env;

import javax.swing.*;
import java.awt.*;


/**
 * This class provides methods for calculating the size of the screen which Triana is currently running on. Based on the
 * size OldUnit Programmers can compensate when creating OldUnit Windows so that they appear relavive to the size of the
 * screen and not way out of proportion. The ratio's are calculated relative to a machine running a 1024 x 768 computer.
 * If the machine has a better resolution than this then Triana will appear larger so that it occupies the same
 * proportion than it does on an SVGA screen.</p> <p/> <p>Triana uses this class extensively when creating its graphical
 * user interface.  </p>
 * <p/>
 * This class also provides some sueful routines which Triana uses. For example there is a function here which displays
 * Triana's title screen.
 *
 * @author Ian Taylor
 * @version $Revision: 4048 $
 */
public class Display {
    /**
     * The ratio to scale the screen in the X direction.
     */
    public static double ratioX;


    /**
     * The ratio to scale the screen in the Y direction.
     */
    public static double ratioY;

    /**
     * The size of the screen (in pixels) in the X direction.
     */
    public static int screenX;

    /**
     * The size of the screen (in pixels) in the Y direction.
     */
    public static int screenY;


    static {
        // calculate screen size

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        screenX = dim.width;
        screenY = dim.height;

        // looks good on a SVGA screen so scale so that everything looks
        // like this.

        ratioX = (float) screenX / 1024.0;
        ratioY = (float) screenY / 768.0;

        // However on the notebook I'll experiment :-

        if ((screenX == 640) || (screenY == 480)) {
            ratioX = 0.8;
            ratioY = 0.7;
        }
    }


    /**
     * This routine calibrates the X size or coordinate sent to it, so that things look the same no matter what computer
     * you are on i.e. it scales the application to the size of this computer's screen.
     */
    public static int x(int size) {
        int newX = (int) ((double) size * ratioX);

        // add one if number gets truncated

        if (newX != ((double) size * ratioX)) {
            ++newX;
        }

        return newX;
    }

    /**
     * This routine calibrates the Y size or coordinate sent to it, so that things look the same no matter what computer
     * you are on i.e. it scales the application to the size of this computer's screen.
     */
    public static int y(int size) {
        int newY = (int) ((double) size * ratioY);

        // add one if number gets truncated

        if (newY != ((double) size * ratioY)) {
            ++newY;
        }

        return newY;
    }

    /**
     * Makes sure that the specified JFrame doesn't dissappear off the screen by checking the x and y coordinates of its
     * desired position and clipping them so that it fits onto the particular screen.
     *
     * @param fr the frame to be clipped into the screen size
     * @param x  the desired x coordinate
     * @param y  the desired y coordinate
     */
    public static Point clipFrameToScreen(Component fr, int x, int y) {
        int clipX, clipY;
        int scrY = screenY;

        if (Env.os().equals("Windows 95")) {
            scrY = screenY - 20;
        }

        if ((x + fr.getSize().width + 5) > screenX) {
            clipX = screenX - fr.getSize().width - 5;
        } else {
            clipX = x;
        }

        if ((y + fr.getSize().height + 3) > scrY) {
            clipY = scrY - fr.getSize().height - 3;
        } else {
            clipY = y;
        }

        return new Point(clipX, clipY);
    }

    public static void clipFrameToScreen(Component fr) {
        fr.setLocation(clipFrameToScreen(fr, fr.getLocation().x, fr.getLocation().y));
    }

    /**
     * Makes sure that the specified JFrame doesn't dissappear off the screen by checking the x and y coordinates of its
     * desired position and clipping them so that it fits onto the particular screen. OldUnit Programmers should use
     * this function to place their displaying windows on the screen. Subclasses of ParameterPanel don't need to call
     * this as it is called automatically.
     *
     * @param fr the frame to be clipped into the screen size
     */
    public static Point clipFrameToScreen(Window fr, Point p) {
        return clipFrameToScreen((Component) fr, p.x, p.y);
    }

    public static Point clipFrameToScreen(Component fr, Point p) {
        return clipFrameToScreen(fr, p.x, p.y);
    }

    public static Point clipFrameToScreen(Window fr, int x, int y) {
        return clipFrameToScreen((Component) fr, x, y);
    }

    /**
     * Puts the window in the middle of the screen.
     *
     * @param fr the frame to be placed in the middle of the screen
     */
    public static void centralise(Window fr) {
        fr.setLocation((screenX / 2) - (fr.getSize().width / 2),
                (screenY / 2) - (fr.getSize().height / 2));
    }


    /**
     * @return an anchor point for the specified object. If the object is a menu in a pop-up window then this is based
     *         on the invoker, if the object is not a component/invalid then comp is centralised.
     */
    public static Point getAnchorPoint(Object anchor, Component comp) {
        Component anchcomp = null;

        if (anchor instanceof JPopupMenu) {
            anchcomp = ((JPopupMenu) anchor).getInvoker();
        } else if ((anchor instanceof Component) && (((Component) anchor).getParent() instanceof JPopupMenu)) {
            anchcomp = ((JPopupMenu) ((Component) anchor).getParent()).getInvoker();
        } else if (anchor instanceof Component) {
            anchcomp = (Component) comp;
        }

        Point pos;

        if ((anchcomp != null) && anchcomp.isShowing()) {
            pos = anchcomp.getLocationOnScreen();
        } else {
            pos = new Point((screenX - comp.getSize().width) / 2, (screenY - comp.getSize().height) / 2);
        }

        return clipFrameToScreen(comp, pos);
    }


    public static JWindow showTitleScreen() {
        JWindow screen = new JWindow();
        JPanel content = (JPanel) screen.getContentPane();

        int width = 143;  // 10 extra for Border
        int height = 250; // 40 extra for Border

        int x = (screenX - width) / 2;
        int y = (screenY - height) / 2;
        screen.setBounds(x, y, width, height);
        JPanel j = getTrianaLogo();
        content.add(j);
        screen.setVisible(true);

        // FileUtils.playSystemAudio("trianaTheme.wav");

        do {
            Thread.yield();
        }
        while (!j.isVisible());
        return screen;
    }

    public static JPanel getTrianaLogo() {
        JPanel outer = new JPanel(new BorderLayout());
        JPanel content = new JPanel(new GridLayout(7, 2));
        ImageIcon ic = GUIEnv.getTrianaIcon();
        JLabel label = new JLabel(ic);
        outer.add(label, BorderLayout.NORTH);

        label = new JLabel("Core Developers:", JLabel.CENTER);
        content.add(label);
        label = new JLabel();
        content.add(label);
        label = new JLabel("Ian Taylor", JLabel.CENTER);
        content.add(label);
        label = new JLabel("Matthew Shields", JLabel.CENTER);
        content.add(label);
        label = new JLabel("Ian Wang", JLabel.CENTER);
        content.add(label);
        label = new JLabel("Bernard Shutz", JLabel.CENTER);
        content.add(label);
        label = new JLabel("Andrew Harrison", JLabel.CENTER);
        content.add(label);
        label = new JLabel("Rui Zhu", JLabel.CENTER);
        content.add(label);
        label = new JLabel();
        content.add(label);
        label = new JLabel();
        content.add(label);
        label = new JLabel("Tool Developers", JLabel.CENTER);
        content.add(label);
        label = new JLabel();
        content.add(label);
        label = new JLabel("Rob Davies", JLabel.CENTER);
        content.add(label);
        label = new JLabel("David Churches", JLabel.CENTER);
        content.add(label);
        JPanel inner = new JPanel(new BorderLayout());
        inner.add(content, BorderLayout.NORTH);
        outer.add(inner, BorderLayout.CENTER);

        content.setOpaque(true);
        content.setBorder(BorderFactory.createLineBorder(Color.black, 1));

        return outer;
    }
}













