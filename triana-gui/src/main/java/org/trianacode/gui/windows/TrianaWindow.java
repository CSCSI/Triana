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
package org.trianacode.gui.windows;


import org.trianacode.gui.Display;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.gui.help.AboutDialog;
import org.trianacode.gui.help.search.FindWordDialog;
import org.trianacode.gui.help.search.FindWordEvent;
import org.trianacode.gui.help.search.FindWordListener;
import org.trianacode.gui.help.search.HTMLSearchResults;
import org.trianacode.taskgraph.util.FileUtils;
import org.trianacode.util.Env;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

/**
 * Triana Window creates a basic Triana Window with common functionalities and
 * appearance of all Triana Windows.  Triana window subclasses a frame and
 * implements a Window and an ActionListener within this class to handle
 * window closed events and some default menu operations.
 *
 * @author Ian Taylor
 * @version $Revision: 4048 $
 * @created Jan 2000
 * @date $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public abstract class TrianaWindow extends JFrame
        implements ActionListener, ItemListener, FindWordListener {

    /**
     * The JMenuBar for all Triana windows
     */
    transient protected JMenuBar trianaMenuBar;


    /**
     * Triana Window's default font : 11 point italic Sans-Serif
     */
    public static Font defaultFont = new Font("Sans-Serif", Font.ITALIC, Display.x(11));

    /**
     * The find word pop up
     */
    public FindWordDialog findWordDialog = null;

    public TrianaWindow() {
        super();

        setIconImage(GUIEnv.getTrianaIcon());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                cleanUp();
                System.exit(1);
            }

            public void windowClosed(WindowEvent e) {
            }
        });
    }

    /**
     * This class provide a basic window with a Window menu containing a
     * close option (which hides the window) and a Help menu which calls the
     * function called help.  Help does nothing at this stage and should
     * be subclassed to provide help.
     * <h2>Note</h2>
     * TrianaWindow does not show (i.e. setVisible(true)) the window.
     * This should be done by the windows inherited from this
     * The window is by default set to be a window in another application.
     */
    public TrianaWindow(String name) {
        this();
        setTitle(name);
    }

    /**
     * Calls TrianaWindow(String) to initialise the menu's and then resize's
     * to a specific width and height.
     *
     * @param width  the desired width of the window
     * @param height the desired height of the window
     */
    public TrianaWindow(String name, int width, int height) {
        this(name);

        // resize the window using given parameters

        setSize(width, height);
    }


    /**
     * Returns the name of the icon to use when TrianaWindow is
     * iconified.
     *
     * @return name of the icon.
     */
    public String getMyIconName() {
        return "triana.gif";
    }

    /**
     * Returns an image to use as the icon when TrianaWindow is
     * iconified.
     *
     * @return image to use.
     */
    public Image getMyIcon() {
        return FileUtils.getImage(Env.home() + File.separator + "system" +
                File.separator + "icons" + File.separator + getMyIconName());
    }


    /**
     * A convenience method to close RMFrames.
     */
    public void close() {
        try {
            if (getContentPane() == null) return;
        }
        catch (Exception e) {
            return;
        }

        removeAll(getContentPane());
        setJMenuBar(null);
        setVisible(false);
    }

    /**
     * Drill down and whack all components in all containers.
     *
     * @param _cont The Container to drill into.
     */
    private void removeAll(Container _cont) {
        while (_cont.getComponentCount() > 0) {
            System.out.println("Widgets Left ... " + _cont.getComponentCount());
            Component inner = _cont.getComponent(0);
            if (inner instanceof Container) {
                removeAll((Container) inner);
            }
            _cont.remove(0);
        }
    }

    /**
     * Called when the user wants to close the window. If the window
     * is in another application then the window is just made
     * invisible, but if it is a stand alone application then a
     * really Quit ? window is given to ask the user if he/she
     * really wants to quit or not.
     * TODO refactor this out, it's not needed for all TrianaWindow subclasses
     */
    public void cleanUp() {
        //No-op for now
    }

    /**
     * Does Nothing, just here so we can register as Item listeners
     */
    public void itemStateChanged(ItemEvent e) {
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e) {
        String label = e.getActionCommand();

        if (label.equals(getString("Help"))) {
            showHelp();
        } else if (label.equals(getString("Index"))) {
            showHelpFor("Index");
        } else if (label.equals(getString("Tutorial"))) {
            showHelpFor("tutorials/index");
        } else if (label.equals(getString("UnitHelp"))) {
            showHelpFor("unit/index");
        } else if (label.equals(getString("Manual"))) {
            showHelpFor("manual/index");
        } else if (label.equals(getString("JavaDoc"))) {
            showHelpFor("JavaDoc/index");
        } else if (label.equals(getString("GettingStarted"))) {
            showHelpFor("tutorials/gettingstarted/index");
        } else if (label.equals(getString("About"))) {
            AboutDialog ab = new AboutDialog(this, false);
            ab.dispose();
        } else if (label.equals(getString("Quit"))) {
            QuestionWindow con = new QuestionWindow(GUIEnv.getApplicationFrame(), "Really Quit " + getName() + "?");

            if (con.reply == con.YES) {
                cleanUp();
                System.exit(1);
            }
            return;
        } else if (label.equals(getString("Refresh"))) {
            refreshMe();
        } else if (label.equals(getString("FindTool"))) {
            showFindWordDialog("toolhelp.idx");
        } else if (label.equals(Env.getString("System"))) {
            /*
            TODO
            if (Cicerone.invokeCicerone() == null) {
                System.out.println("Error invoking Cicerone");
            }*/
        }
    }

    public void writeToFile(String name) {
        try {
            FileOutputStream f = new FileOutputStream(name);
            ObjectOutputStream s = new ObjectOutputStream(f);
            s.writeObject(this);
            s.flush();
            f.close();
        }
        catch (Exception ee) {
            ErrorDialog.show(this, ee);
        }

    }

    /**
     * Looks in the Resource Bundle (<i>messages</i>) for the
     * text for the key <i>key</i>.  If it is found then
     * the text is returned.  If not then the function returns
     * a blank string and gives an error message
     */
    public String getString(String key) {
        try {
            return Env.getString(key);
        }
        catch (Exception e) {
            ErrorDialog.show(this, key + " " + Env.getString("NotFound"), e);
            return "";
        }
    }

    /**
     * Repaints the window by default. Override this function to
     * put your window specific refreshing.
     */
    public void refreshMe() {
        repaint();
    }

    /**
     * This overrides the basic setLocation method within component to
     * clip the window so that it always fits on the screen. If the
     * user enters values which would make some part of the window fall
     * off the end of the screen then the values are alterred accordingly.
     */
    public void setLocation(int x, int y) {
        Point p = Display.clipFrameToScreen(this, x, y);
        super.setLocation(p.x, p.y);
    }

    /**
     * Purely abstract at this stage.  It's up to the specific windows
     * themselves to return the name of their specific helpFile. If you
     * don't wan't to define help then return "none" otherwise return the
     * help file.
     */
    public abstract String getHelpFile();

    /**
     * displays the getHelpfile() in a browser.
     */
    public void showHelp() {
        GUIEnv.openURL(getHelpFile());
    }

    /**
     * Displays the given help file in a browser.
     * later.
     */
    public void showHelpFor(String helpfile) {
        helpfile += ".html";
        GUIEnv.openURL(Env.home() + "help" + File.separator + helpfile);
    }

    public void showFindWordDialog(String indexFileName) {
//        System.out.println(indexFileName);
        if (findWordDialog != null) {
            findWordDialog.dispose();
        }

//        System.out.println( new String(Env.home() + File.separator + "system" + File.separator + "indexes" + File.separator + indexFileName));

        try {
            String fn = Env.home() + File.separator + "system" +
                    File.separator + "indexes" + File.separator + indexFileName;
            if (indexFileName.equals("help.idx")) {
                findWordDialog = new FindWordDialog(this,
                        HTMLSearchResults.loadHTMLSearchResults(new
                                File(fn)), FindWordDialog.MAIN);
                findWordDialog.setTitle("Triana Main Help Finder");
            } else {
                findWordDialog = new FindWordDialog(this,
                        HTMLSearchResults.loadHTMLSearchResults(new
                                File(fn)), FindWordDialog.TOOLS);
                findWordDialog.setTitle("Triana ToolImp Help Finder");
            }

            findWordDialog.addFindWordListener(this);
            findWordDialog.setVisible(true);
        }
        catch (Exception ex) {
            // ex.printStackTrace();
        }
    }

    public void wordFound(FindWordEvent e) {
        GUIEnv.openURL(Env.home() + e.getFile().toString());
    }

}
