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

import org.trianacode.gui.hci.GUIEnv;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.*;
import java.awt.event.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;


/**
 * The main window for the Triana Help application.  This class contains the
 * entire GUI functionality of TrianaHelp.
 *
 * @author Melanie Rhianna Lewis
 * @version $Revision: 4048 $
 * @date $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 * @see HtmlPane
 */
public class TrianaHelpFrame extends JFrameEx {
    // The pane which actually contains the HTML document
    private HtmlPane htmlPane;

    private JFileChooser fileChooser;
    private JButton copyButton, backButton, forwardButton;
    private JMenuItem editCopy, goBack, goForwards;
    private HistoryDialog historyDialog;

    // A vector which contains the CloseFrameListeners
    private Vector closeFrameVector;

    // A string which contains the path to the images
    private String imagePath;

    private TrianaHelpProperties properties;

    public TrianaHelpFrame(String title, String helpFile) {
        super(title);

        addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                processCloseFrameEvent(new CloseFrameEvent(TrianaHelpFrame.this));
                setBounds(properties, "Help.Window");
                properties.saveUserSettings();
            }
        });

        initFrame(helpFile);
        setIconImage(getImageResource("/triana/help/images/frame-icon.jpg"));

        properties = new TrianaHelpProperties();
        properties.loadUserSettings();
        setBounds(getBounds(properties, "Help.Window"));
        setCascadeLocation(50, 50);
    }

    public TrianaHelpFrame(String helpFile) {
        this("Triana Help", helpFile);
    }

    public TrianaHelpFrame() {
        this("Triana Help", null);
    }

    public void initFrame(String file) {
        // System.out.println("[Starting TrianaHelp]");

        // Initialise the CloseFrameEvent stuff
        closeFrameVector = new Vector();
        addWindowListener(new WindowClosingAction());

        // Create a menu bar
        // System.out.println("[Creating menu]");
        JMenuBar menuBar = new JMenuBar();
        addMenus(menuBar);
        setJMenuBar(menuBar);

        // Create a tool bar
        // System.out.println("[Creating tool bar]");
        JToolBar toolBar = new JToolBar();
        addToolBarButtons(toolBar);

        // Create the HtmlPane and attach our SelectionListener class to it
        // to keep to copy button and menu item updated.
        // System.out.println("[Creating html pane]");
        if (file != null) {
            htmlPane = new HtmlPane(file);
        } else {
            htmlPane = new HtmlPane();
        }
        htmlPane.getEditorPane().addCaretListener(new SelectionListener());

        // Get the history list and add the custom UrlEventListener to it
        htmlPane.getUrlHistory().addUrlEventListener(new NavigationListener());

        // Store the index file
        if (file != null) {
            htmlPane.setIndex(file);
        }

        // Put all the bits in to a single JPanel and make it the
        // content pane.
        // System.out.println("[Creating main frame]");
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(toolBar, BorderLayout.NORTH);
        contentPane.add(htmlPane, BorderLayout.CENTER);
        setContentPane(contentPane);

        // Create the file chooser dialog
        fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Open file...");

        // Create the history dialog
        historyDialog = new HistoryDialog(this, "History", true,
                htmlPane.getUrlHistory());

        setSize(new Dimension(600, 400));
    }

    /**
     * Creates the menus and menu items.  It creates instances of the Action
     * classes that contain the functionality of the menu items.  It also
     * gives the menu items appropriate accelerators.
     *
     * @param menuBar The menuBar on which to create the menus.
     */
    private void addMenus(JMenuBar menuBar) {
        JMenuItem menuItem;
        JMenu menu, subMenu;

        // Create the File menu
        // ********************

        // First create the menu and then the submenu
        menu = new JMenu("File");
        subMenu = new JMenu("Open");

        menuItem = new JMenuItem("File...");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
                Event.CTRL_MASK));
        menuItem.addActionListener(new FileOpenAction());
        subMenu.add(menuItem);

        menuItem = new JMenuItem("Location...");
        menuItem.addActionListener(new FileOpenURLAction());
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L,
                Event.CTRL_MASK));
        subMenu.add(menuItem);

        menu.add(subMenu);
        menu.addSeparator();

        menuItem = new JMenuItem("Close");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4,
                Event.META_MASK));
        ;
        menuItem.addActionListener(new FileCloseAction());
        menu.add(menuItem);
        menuBar.add(menu);

        // Create the Edit menu
        // ********************

        menu = new JMenu("Edit");

        // We need to keep this item hence the separate variable
        editCopy = new JMenuItem("Copy");
        editCopy.addActionListener(new EditCopyAction());
        editCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
                Event.CTRL_MASK));
        ;
        editCopy.setEnabled(false);
        menu.add(editCopy);
        menu.addSeparator();

        menuItem = new JMenuItem("Select all");
        menuItem.addActionListener(new EditSelectAllAction());
        menu.add(menuItem);
        menuBar.add(menu);

        // Create the Go menu
        // ******************

        menu = new JMenu("Go");

        // We need to keep this item hence the separate variable
        goBack = new JMenuItem("Back");
        goBack.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0));
        goBack.setEnabled(false);
        goBack.addActionListener(new GoBackAction());
        menu.add(goBack);

        // We need to keep this item hence the separate variable
        goForwards = new JMenuItem("Forwards");
        goForwards.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0));
        goForwards.setEnabled(false);
        goForwards.addActionListener(new GoForwardsAction());
        menu.add(goForwards);

        menuItem = new JMenuItem("Index");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I,
                Event.CTRL_MASK));
        ;
        menuItem.addActionListener(new GoIndexAction());
        menu.add(menuItem);
        menu.addSeparator();

        menuItem = new JMenuItem("History...");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H,
                Event.CTRL_MASK));
        ;
        menuItem.addActionListener(new GoHistoryAction());
        menu.add(menuItem);
        menuBar.add(menu);

        // Create the Help menu
        // ********************

        menu = new JMenu("Help");

        menuItem = new JMenuItem("Help");
        menuItem.addActionListener(new HelpHelpAction());
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        menu.add(menuItem);

        menu.addSeparator();

        menuItem = new JMenuItem("About TrianaHelp...");
        menuItem.addActionListener(new HelpAboutAction());
        menu.add(menuItem);
        menuBar.add(menu);

        // The help menu gets displayed on the right
        // menuBar.setHelpMenu(menu);
    }

    /**
     * Creates the buttons on a toolbar.  It creates instances of the Action
     * classes that contain the functionality of the tool bar buttons.  It also
     * supplies the buttons with an appropriate tool tip message.
     *
     * @param toolBar The toolBar on which to create the buttons.
     */
    private void addToolBarButtons(JToolBar toolBar) {
        JButton button;

        // The file open button
        button = new JButton(GUIEnv.getIcon("open.png"));
        button.setToolTipText("Open a file");
        button.addActionListener(new FileOpenAction());
        toolBar.add(button);

        // The location open button
        button = new JButton(GUIEnv.getIcon("world.png"));
        button.setToolTipText("Open a URL");
        button.addActionListener(new FileOpenURLAction());
        toolBar.add(button);

        toolBar.addSeparator();

        // The copy button
        // We need to keep this item hence the separate variable
        copyButton = new JButton(GUIEnv.getIcon("copy.png"));
        copyButton.setToolTipText("Copy selected text");
        copyButton.setEnabled(false);
        copyButton.addActionListener(new EditCopyAction());
        toolBar.add(copyButton);

        toolBar.addSeparator();

        // The previous page button
        backButton = new JButton(GUIEnv.getIcon("left.png"));
        backButton.setToolTipText("Go back");
        backButton.setEnabled(false);
        backButton.addActionListener(new GoBackAction());
        toolBar.add(backButton);

        // The next page button
        forwardButton = new JButton(GUIEnv.getIcon("right.png"));
        forwardButton.setToolTipText("Go forwards");
        forwardButton.setEnabled(false);
        forwardButton.addActionListener(new GoForwardsAction());
        toolBar.add(forwardButton);

        // The index page button
        button = new JButton(GUIEnv.getIcon("help.png"));
        button.setToolTipText("Show index");
        button.addActionListener(new GoIndexAction());
        toolBar.add(button);
    }

    private Image getImageResource(String name) {
        Image image = null;

        try {
            int c;
            InputStream in = this.getClass().getResourceAsStream(name);
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            for (; ;) {
                if ((c = in.read()) < 0) break;
                out.write(c);
            }

            image = this.getToolkit().createImage(out.toByteArray());
        }
        catch (Exception e) {
            System.err.println("Error trying to load " + name + " from jar file.");
            e.printStackTrace();
        }

        return image;
    }

    /*

    ********** SUPERCEDED FUNCTIONS **********

    The images are now loaded from the resource tree using getImageResouce()
    as defined above.

    public void setImagePath(String imagePath) {
      if (imagePath != null) {
        if (imagePath.equals("")) {
          this.imagePath = imagePath + File.pathSeparator;
        } else {
          this.imagePath = imagePath;
        }
      } else {
        this.imagePath = "";
      }
    }

    public String getImagePath() {
      return imagePath;
    }*/

    /**
     * Set the instance of the scroll pane which holds the html editor.
     */
    public void setHtmlPane(HtmlPane htmlPane) {
        this.htmlPane = htmlPane;
    }

    /**
     * Returns the instance of the scroll pane which holds the html editor.
     */
    public HtmlPane getHtmlPane() {
        return htmlPane;
    }

    // A whole bunch of actions which are the functionality of the window

    /**
     *
     */
    private class FileOpenAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            File file;
            String string;

            if (fileChooser.showOpenDialog(TrianaHelpFrame.this) ==
                    JFileChooser.APPROVE_OPTION) {
                file = fileChooser.getSelectedFile();
                string = "file:" + file.getAbsolutePath();
                getHtmlPane().setPage(string);
            }
        }
    }

    /**
     *
     */
    private class FileOpenURLAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String string = (String) JOptionPane.showInputDialog(TrianaHelpFrame.this,
                    "Enter URL to open", "Open URL...", JOptionPane.QUESTION_MESSAGE, GUIEnv.getTrianaImageIcon(), null, null);

            if (string != null) {
                string.trim();
                getHtmlPane().setPage(string);
            }
        }
    }

    /**
     *
     */
    private class EditCopyAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            getHtmlPane().getEditorPane().copy();
        }
    }

    /**
     *
     */
    private class EditSelectAllAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            getHtmlPane().getEditorPane().selectAll();
        }
    }

    /**
     *
     */
    private class GoBackAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            getHtmlPane().goBack();
        }
    }

    /**
     *
     */
    private class GoForwardsAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            getHtmlPane().goForwards();
        }
    }

    /**
     *
     */
    private class GoIndexAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            getHtmlPane().goIndex();
        }
    }

    /**
     *
     */
    private class GoHistoryAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            URL url = historyDialog.showDialog();

            if (url != null) getHtmlPane().setPage(url);
        }
    }

    public class HelpHelpAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                URL url = this.getClass().getResource("/triana/help/html/index.html");
                getHtmlPane().setPage(url);
            }
            catch (Exception ex) {
                System.err.println("Error trying to load index.html from jar file.");
                ex.printStackTrace();
            }
        }
    }

    public class HelpAboutAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            AboutDialog about = new AboutDialog(TrianaHelpFrame.this);
        }
    }

    /**
     * A class which listens for a CaretEvent and if a selection has
     * been made it enables the copy menu item and button on the tool bar.
     *
     * @see javax.swing.event.CaretEvent
     * @see javax.swing.event.CaretListener
     */
    private class SelectionListener implements CaretListener {
        public void caretUpdate(CaretEvent e) {

            // Dot is the end of the selection and Mark the start.  If they are
            // the same there isn't a selection.
            boolean selected = (e.getDot() != e.getMark());

            editCopy.setEnabled(selected);
            copyButton.setEnabled(selected);
        }
    }

    /**
     * A class which listens for a UrlEvent from the UrlHistory object
     * owned by the HtmlPane.  A UrlEvent is a custom event sent by the
     * UrlHistory class when it is changed in some way.
     *
     * @see UrlHistory
     * @see UrlEvent
     * @see UrlEventListener
     */
    private class NavigationListener implements UrlEventListener {
        public void indexChanged(UrlEvent e) {
            // System.out.println("+++ Catching UrlEvent +++");
            // e.getIndex() get the current position within the Url list
            // e.countUrls() count the total number of Urls in the list

            int urlIndex = e.getIndex();
            boolean backState = (urlIndex > 0);
            boolean forwardState = (urlIndex < (e.countUrls() - 1));

            goBack.setEnabled(backState);
            backButton.setEnabled(backState);
            goForwards.setEnabled(forwardState);
            forwardButton.setEnabled(forwardState);
        }
    }

    // -- Start of CloseFrame methods --

    /**
     * A private class which responds to the close window widget being used by
     * generating a CloseFrameEvent.
     *
     * @see CloseFrameEvent
     */
    private class WindowClosingAction extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            processCloseFrameEvent(new CloseFrameEvent(TrianaHelpFrame.this));
        }
    }

    /**
     * A private class which is an action which generates a CloseFrameEvent.
     * This is usually used as an ActionListener for a button or menu item.
     *
     * @see CloseFrameEvent
     */
    private class FileCloseAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            processCloseFrameEvent(new CloseFrameEvent(TrianaHelpFrame.this));
            setBounds(properties, "Help.Window");
            properties.saveUserSettings();
        }
    }

    /**
     * Adds a CloseFrameListener to the Frame
     *
     * @param listener The CloseFrameListener to add
     * @see CloseFrameListener
     */
    public void addCloseFrameListener(CloseFrameListener listener) {
        closeFrameVector.addElement(listener);
    }

    /**
     * Dispatches a CloseFrameEvent to the CloseFrameListeners registered with
     * the Frame.
     *
     * @param event The CloseFrameEvent to dispatch
     * @see CloseFrameListener
     * @see CloseFrameEvent
     */
    public void processCloseFrameEvent(CloseFrameEvent event) {
        CloseFrameListener listener;
        Enumeration e = closeFrameVector.elements();

        while (e.hasMoreElements()) {
            listener = (CloseFrameListener) e.nextElement();
            listener.frameClosing(event);
        }
    }

    // -- End of CloseFrame methods --
}



