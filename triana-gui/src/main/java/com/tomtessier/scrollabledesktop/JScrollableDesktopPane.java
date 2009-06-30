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

package com.tomtessier.scrollabledesktop;

import javax.swing.*;
import java.awt.*;

/**
 * This code is from a JavaWorld <a href="http://www.javaworld.com/javaworld/jw-11-2001/jw-1130-jscroll.html">
 * article</a> by Tom Tessier
 *
 * The main scrollable desktop class.
 * <BR><BR>
 * JScrollableDesktopPane builds upon JDesktopPane and JScrollPane to provide
 * a complete virtual desktop environment that enables easy access to internal
 * frames that may have been positioned offscreen. This access is made
 * possible via real-time creation and manipulation of the desktop preferred size.
 * <BR><BR>
 * A toolbar provides a set of buttons along the top of the screen, with each
 * button matched to a corresponding internal frame. When one of these buttons
 * is clicked, the associated frame is centered upon the virtual desktop and
 * selected. The buttons within the toolbar automatically resize as more buttons
 * are added beyond the width of the container.
 * <BR><BR>
 * A JMenuBar may be registered with the scrollable desktop so that the
 * application can provide access to the internal frames via its own menu bar.
 * When the registration is complete, a new JMenu entitled "Window" is added to
 * the supplied JMenuBar, a menu containing <code>Tile</code>,
 * <code>Cascade</code>, and <code>Close</code> options along with dynamically
 * updated shortcuts to any internal frames currently upon the scrollable
 * desktop. The <code>Tile</code> and <code>Cascade</code> options provided by
 * the "Window" menu affect the positions of the internal frames upon the
 * scrollable desktop. <code>Cascade</code> positions each internal frame one
 * after the other in a diagonal sequence crosswise the screen, while
 * <code>Tile</code> positions and resizes the internal frames to fill up
 * all available screen real estate, with no single frame overlapping any other.
 * <BR><BR>
 * JScrollableDesktopPane is simply a JPanel and as such may be added to any
 * suitable JPanel container, such as a JFrame. The addition of new internal
 * frames to the JScrollableDesktopPane and the registration of menu bars for
 * use by the scrollable desktop is relatively simple: The <code>add</code>
 * method creates a new internal frame and returns a reference to the
 * JInternalFrame instance that was created, while the
 * <code>registerMenuBar</code> method registers the menubar for use by the
 * scrollable desktop. A JMenuBar object may also be registered by passing it
 * as a constructor parameter to the JScrollableDesktopPane.
 * <BR><BR>
 * An example usage follows:
 * <BR><BR>
 * <code><pre>
 *    JFrame f = new JFrame("Scrollable Desktop");
 *    f.setSize(300,300);
 *    // prepare the menuBar
 *    JMenuBar menuBar = new JMenuBar();
 *    f.setJMenuBar(menuBar);
 *
 *    // create the scrollable desktop instance and add it to the JFrame
 *    JScrollableDesktopPane scrollableDesktop =
 *          new JScrollableDesktopPane(menuBar);
 *    f.getContentPane().add(scrollableDesktop);
 *    f.setVisible(true);
 *
 *    // add a frame to the scrollable desktop
 *    JPanel frameContents = new JPanel();
 *    frameContents.add(
 *          new JLabel("Hello and welcome to JScrollableDesktopPane."));
 *
 *    scrollableDesktop.add(frameContents);
 * </pre></code>
 *
 * JScrollableDesktopPane has been tested under Java 2 JDK versions
 * 1.3.1-b24 on Linux and jdk1.3.0_02 on Windows and Intel Solaris.
 *
 * @author <a href="mailto:tessier@gabinternet.com">Tom Tessier</a>
 * @version 1.0  12-Aug-2001
 */


public class JScrollableDesktopPane extends JPanel
        implements DesktopConstants {

    private static int count; // count used solely to name untitled frames

    private DesktopMediator desktopMediator;
    private ImageIcon defaultFrameIcon;


    /**
     * creates the JScrollableDesktopPane object, registers a menubar, and assigns
     *      a default internal frame icon.
     *
     * @param mb the menubar with which to register the scrollable desktop
     * @param defaultFrameIcon the default icon to use within the title bar of
     *      internal frames.
     */
    public JScrollableDesktopPane(JMenuBar mb, ImageIcon defaultFrameIcon) {
        this();
        registerMenuBar(mb);
        this.defaultFrameIcon = defaultFrameIcon;
    }

    /**
     * creates the JScrollableDesktopPane object and registers a menubar.
     *
     * @param mb the menubar with which to register the scrollable desktop
     */
    public JScrollableDesktopPane(JMenuBar mb) {
        this();
        registerMenuBar(mb);
    }

    /**
     * creates the JScrollableDesktopPane object.
     */
    public JScrollableDesktopPane() {
        setLayout(new BorderLayout());
        desktopMediator = new DesktopMediator(this);
    }


    /**
     * adds an internal frame to the scrollable desktop
     *
     * @param frameContents the contents of the internal frame
     *
     * @return the JInternalFrame that was created
     */
    public JInternalFrame add(JPanel frameContents) {
        return add("Untitled " + count++,
                   defaultFrameIcon, frameContents, true, -1, -1);
    }

    /**
     * adds an internal frame to the scrollable desktop
     *
     * @param title the title displayed in the title bar of the internal frame
     * @param frameContents the contents of the internal frame
     *
     * @return the JInternalFrame that was created
     */
    public JInternalFrame add(String title, JPanel frameContents) {
        return add(title,
                   defaultFrameIcon, frameContents, true, -1, -1);
    }

    /**
     * adds an internal frame to the scrollable desktop
     *
     * @param title the title displayed in the title bar of the internal frame
     * @param frameContents the contents of the internal frame
     * @param isClosable <code>boolean</code> indicating whether internal frame
     *          is closable
     *
     * @return the JInternalFrame that was created
     */
    public JInternalFrame add(String title, JPanel frameContents,
                              boolean isClosable) {
        return add(title,
                   defaultFrameIcon, frameContents, isClosable, -1, -1);
    }

    /**
     * adds an internal frame to the scrollable desktop
     *
     * @param title the title displayed in the title bar of the internal frame
     * @param icon the icon displayed in the title bar of the internal frame
     * @param frameContents the contents of the internal frame
     * @param isClosable <code>boolean</code> indicating whether internal frame
     *          is closable
     *
     * @return the JInternalFrame that was created
     */
    public JInternalFrame add(String title, ImageIcon icon,
                              JPanel frameContents, boolean isClosable) {
        return add(title, icon, frameContents, isClosable, -1, -1);
    }


    /**
     * adds an internal frame to the scrollable desktop.
     * <BR><BR>
     * Propogates the call to DesktopMediator.
     *
     * @param title the title displayed in the title bar of the internal frame
     * @param icon the icon displayed in the title bar of the internal frame
     * @param frameContents the contents of the internal frame
     * @param isClosable <code>boolean</code> indicating whether internal frame
     *          is closable
     * @param x x coordinates of internal frame within the scrollable desktop.
     * @param y y coordinates of internal frame within the scrollable desktop
     *
     * @return the JInternalFrame that was created
     */
    public JInternalFrame add(String title, ImageIcon icon,
                              JPanel frameContents,
                              boolean isClosable, int x, int y) {

        return desktopMediator.add(
                title, icon, frameContents, isClosable, x, y);

    }

    /**
     * adds an internal frame to the scrollable desktop.
     *
     * @param f the internal frame of class BaseInternalFrame to add
     */
    public void add(JInternalFrame f) {
        add(f, -1, -1);
    }

    /**
     * adds an internal frame to the scrollable desktop.
     * <BR><BR>
     * Propogates the call to DesktopMediator.
     *
     * @param f the internal frame of class BaseInternalFrame to add
     * @param x x coordinates of internal frame within the scrollable desktop.
     * @param y y coordinates of internal frame within the scrollable desktop
     */
    public void add(JInternalFrame f, int x, int y) {
        desktopMediator.add(f, x, y);
    }


    /**
     * removes the specified internal frame from the scrollable desktop
     *
     * @param f the internal frame to remove
     */
    public void remove(JInternalFrame f) {
        f.dispose();
    }


    /**
     * registers a menubar to which the "Window" menu may be applied.
     * <BR><BR>
     * Propogates the call to DesktopMediator.
     *
     * @param mb the menubar to register
     */
    public void registerMenuBar(JMenuBar mb) {
        desktopMediator.registerMenuBar(mb);
    }

    /**
     * registers a default icon for display in the title bars of
     *    internal frames
     *
     * @param defaultFrameIcon the default icon
     */
    public void registerDefaultFrameIcon(ImageIcon defaultFrameIcon) {
        this.defaultFrameIcon = defaultFrameIcon;
    }


    /**
     * returns the internal frame currently selected upon the
     * virtual desktop.
     * <BR><BR>
     * Propogates the call to DesktopMediator.
     *
     * @return a reference to the active JInternalFrame
     */
    public JInternalFrame getSelectedFrame() {
        return desktopMediator.getSelectedFrame();
    }

    /**
     * selects the specified internal frame upon the virtual desktop.
     * <BR><BR>
     * Propogates the call to DesktopMediator.
     *
     * @param f the internal frame to select
     */
    public void setSelectedFrame(JInternalFrame f) {
        desktopMediator.setSelectedFrame(f);
    }

    /**
     *  flags the specified internal frame as "contents changed." Used to
     * notify the user when the contents of an inactive internal frame
     * have changed.
     * <BR><BR>
     * Propogates the call to DesktopMediator.
     *
     * @param f the internal frame to flag as "contents changed"
     */
    public void flagContentsChanged(JInternalFrame f) {
        desktopMediator.flagContentsChanged(f);
    }


}
