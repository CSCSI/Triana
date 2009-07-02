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
package org.trianacode.gui.hci;


import org.trianacode.gui.hci.color.ColorTableEntry;
import org.trianacode.gui.main.TaskGraphPanel;
import org.trianacode.gui.windows.ErrorDialog;
import org.trianacode.gui.windows.Help;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.service.TrianaClient;
import org.trianacode.taskgraph.util.FileUtils;
import org.trianacode.util.Env;

import javax.swing.*;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.MouseListener;
import java.util.Hashtable;
import java.util.TreeMap;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * Class GUIEnv sets stores classes which are accessed from other GUI objects
 * e.g. a number of object need access to the main ApplicationFrame
 *
 * @author Ian Taylor
 * @version $Revision: 4048 $
 * @date $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public class GUIEnv {

    static Logger logger = Logger.getLogger("org.trianacode.gui.hci.GUIEnv");

    private static ApplicationFrame app = null;

    private static MouseListener mouseList;

    private static boolean standalone = true;

    /**
     * A list of the GAPCable Colours for Triana Types. If these are edited
     * then this is non null and represents the users choice of colours.
     */
    private static TreeMap cableColours = null;
    private static String iconTheme = "crystalicons";

    /**
     * static initializer block
     */
    static {
        GUIEnv.cableColours = new TreeMap();
    }

    /**
     * Only can set within this package. Sets the main application window.
     * Called from within ApplicationFrame. DO NOT CALL FROM ELSEWHERE
     */
    static void setApplicationFrame(ApplicationFrame ap) {
        app = ap;
    }

    /**
     * @return a reference to the main Triana application window
     */
    public static ApplicationFrame getApplicationFrame() {
        return app;
    }

    public static TaskGraphPanel getSelectedTaskGraphPanel() {
        return app.getSelectedTaskGraphPanel();
    }


    /**
     * Remove the specified main triana from the workspace
     */
    public static void removeTaskGraphContainer(TaskGraphPanel cont) {
        JInternalFrame internal = app.getInternalFrameFor(cont);
        internal.doDefaultCloseAction();
    }


    /**
     * @return an array all the main trianas that are open within the application
     */
    public static TaskGraphPanel[] getTaskGraphPanels() {
        return app.getTaskGraphPanels();
    }


    /**
     * Gets the MainTriana panel which is conatined within the given JInternalFrame
     */
    public static TaskGraphPanel getTaskGraphPanelFor(JInternalFrame fr) {
        return app.getTaskGraphPanelFor(fr);
    }

    /**
     * @return the MainTriana panel which is representing the specified task
     *         graph, or null if the task isn't represented
     */
    public static TaskGraphPanel getTaskGraphPanelFor(TaskGraph group) {
        return app.getTaskGraphPanelFor(group);
    }

    /**
     * @return the TrianaClient for the specified task (null if unknown)
     */
    public static TrianaClient getTrianaClientFor(Task task) {
        TaskGraph parent;

        if (task instanceof TaskGraph)
            parent = (TaskGraph) task;
        else
            parent = task.getParent();

        TrianaClient client = null;

        while ((parent != null) && (client == null)) {
            client = app.getTrianaClient(parent);

            if (client == null)
                parent = parent.getParent();
        }

        return client;
    }


    /**
     * Sets whether auto connecting of tasks is enabled
     */
    public static void setAutoConnect(boolean state) {
        Env.setUserProperty("autoconnect", String.valueOf(state));
    }

    /**
     * @return true if auto connecting of tasks is enabled
     */
    public static boolean isAutoConnect() {
        String state = (String) Env.getUserProperty("autoconnect");
        if (state == null) {
            setAutoConnect(false);
            return false;
        } else {
            return (new Boolean(state)).booleanValue();
        }
    }

    /**
     * Sets whether smooth cables is enabled
     */
    public static void setSmoothCables(boolean state) {
        Env.setUserProperty("smoothcables", String.valueOf(state));
    }

    /**
     * @return whether cables are smooth
     */
    public static boolean isSmoothCables() {
        String state = (String) Env.getUserProperty("smoothcables");
        if (state == null) {
            setAutoConnect(true);
            return true;
        } else {
            return (new Boolean(state)).booleanValue();
        }
    }

    /**
     * Sets whether restore from last state at startup is set
     */
    public static void setRestoreLast(boolean state) {
        Env.setUserProperty("restoreLast", String.valueOf(state));
    }

    /**
     * @return whether restore from last state at startup is set, defaults to false
     */
    public static boolean restoreLast() {
        String state = (String) Env.getUserProperty("restoreLast");
        if (state == null) {
            setRestoreLast(false);
            return false;
        } else {
            return (new Boolean(state)).booleanValue();
        }
    }

    /**
     * Sets the mouse listener for the tools to the one specified
     */
    public static void setMouseListener(MouseListener m) {
        mouseList = m;
    }

    /**
     * Sets the action listener to the one specified
     */
    public static MouseListener getMouseListener() {
        return mouseList;
    }

    /**
     * Static method to display an HTML page using the default web browser.
     */
    public static void openURL(String url) {
        if (getHTMLViewerCommand().equals(Env.getString("defaultViewer"))) {
            if (Help.tryActualFile(url) == -1)
                Help.setFile(url);
        } else {
            showEditorFor(getHTMLViewerCommand(), url);
        }

    }

    /**
     * Runs the given editor (the editor string is the command-line
     * for the editor) for the particular fileName.
     */
    public static void showEditorFor(String editor, String filename) {
        try {
            Runtime runtime = Runtime.getRuntime();
            String command = editor + " " + filename;
            runtime.exec(command);  // execute it!
        } catch (Exception ee) {
            ErrorDialog.show("Sorry, Error executing " + editor +
                    "\nPlease, go to the Options menu in the MainTrianaWindow\n" +
                    "and choose a valid editor for this file type.");
        }
    }


    /**
     * @return the state of popup desciptions
     */
    public static boolean showPopUpDescriptions() {
        String popup = (String) Env.getUserProperty(Env.POPUP_DESC_STR);
        if (popup == null) {
            setPopUpDescriptions(true);
            return true;
        }
        return (new Boolean(popup)).booleanValue();
    }

    /**
     * Enables/disables the showing of popup desciptions
     */
    public static void setPopUpDescriptions(boolean state) {
        Env.setUserProperty(Env.POPUP_DESC_STR, String.valueOf(state));
    }

    /**
     * @return whether or not to show extended tool tips
     */
    public static boolean showExtendedDescriptions() {
        String extended = (String) Env.getUserProperty(Env.EXTENDED_POPUP);
        if (extended == null) {
            setExtendedDescriptions(false);
            return false;
        }
        return (new Boolean(extended)).booleanValue();
    }

    /**
     * Set whether or not to display extended tool tips.
     */
    public static void setExtendedDescriptions(boolean state) {
        Env.setUserProperty(Env.EXTENDED_POPUP, String.valueOf(state));
    }

    /**
     * @return whether or not to show extended tool tips
     */
    public static boolean showNodeEditIcons() {
        String icons = (String) Env.getUserProperty(Env.NODE_EDIT_ICONS);
        if (icons == null) {
            setNodeEditIcons(true);
            return true;
        }
        return (new Boolean(icons)).booleanValue();
    }

    /**
     * Set whether or not to display extended tool tips.
     */
    public static void setNodeEditIcons(boolean state) {
        Env.setUserProperty(Env.NODE_EDIT_ICONS, String.valueOf(state));
    }

    /**
     * @return the java editor command i.e. the editor chosen by the user
     */
    public static String getJavaEditorCommand() {
        return (String) Env.getUserProperty(Env.CODE_EDITOR_STR);
    }

    /**
     * sets the java editor command i.e. the editor chosen by the user
     */
    public static void setJavaEditorCommand(String cmd) {
        Env.setUserProperty(Env.CODE_EDITOR_STR, cmd);
    }

    /**
     * @return the HTML editor command i.e. the editor chosen by the user
     */
    public static String getHTMLEditorCommand() {
        return (String) Env.getUserProperty(Env.HELP_EDITOR_STR);
    }

    /**
     * @return the HTML viewer command i.e. the editor chosen by the user
     */
    public static String getHTMLViewerCommand() {
        return (String) Env.getUserProperty(Env.HELP_VIEWER_STR);
    }


    /**
     * sets the HTML editor command i.e. the editor chosen by the user
     */
    public static void setHTMLEditorCommand(String cmd) {
        Env.setUserProperty(Env.HELP_EDITOR_STR, cmd);
    }

    /**
     * sets the HTML viewer command i.e. the editor chosen by the user
     */
    public static void setHTMLViewerCommand(String cmd) {
        Env.setUserProperty(Env.HELP_VIEWER_STR, cmd);
    }

    /**
     * Sets the tip of the day dialof to off or on.
     */
    public static void setTipOfTheDay(boolean state) {
        Env.setUserProperty(Env.TIP_STR, String.valueOf(state));
    }

    /**
     * Sets the Color Table property so it can be saved
     *
     * @param entry
     */
    public static void setColorTableEntry(ColorTableEntry entry) {
        Env.setColorTableEntry(entry);
    }

    public static ColorTableEntry[] getColorTableEntries() {
        return Env.getColorTableEntries();
    }

    /**
     * @return the color table map.
     */
    public static Hashtable getColorTable() {
        return (Hashtable) Env.getUserProperty(Env.COLOR_TABLE_STR);
    }

    /**
     * Gets the tip of the day dialof off or on.
     */
    public static boolean getTipOfTheDay() {
        String tip = (String) Env.getUserProperty(Env.TIP_STR);
        if (tip == null) {
            setTipOfTheDay(true);
            return true;
        }
        return (new Boolean(tip)).booleanValue();
    }

    /**
     * Sets the tip of the day number.
     */
    public static void setTipOfTheDay(int num) {
        Env.setUserProperty(Env.TIP_NUM_STR, String.valueOf(num));
    }

    /**
     * Called when we are running Triana as an applet.
     */
    public static void setApplet(boolean mode, String homeDir, Applet appl) {
        Env.appletHome = homeDir;
        Env.applet = appl;
    }

    /**
     * Called when we are running Triana as an applet.
     */
    public static void setAsApplet(String homeDir, Applet applet) {
        setApplet(true, homeDir, applet);
    }

    /**
     * @return the refernce to the applet object if this run is an applet,
     *         null otherwise.
     */
    public static Applet getApplet() {
        return Env.applet;
    }

    /**
     * @return a hashtable containing each Triana type and its
     *         associated colour.  These are taken from the system types
     *         file or from the users configuration file
     */
    public static TreeMap getCableColours() {  // set up colours
        return cableColours;
    }

    /**
     * Returns the colour of the cable for this type.
     */
    public static Color getCableColour(String type) {
        Object col = cableColours.get(type.substring(type.lastIndexOf(".") + 1));
        if (col instanceof Color)
            return (Color) col;
        else
            return Color.black;
    }

    public static void setCableColor(String type, Color color) {
        cableColours.put(type, color);
    }

    /**
     * Returns the colour of the cable for this class type.
     */
    public static Color getCableColour(Class type) {
        return getCableColour(type.getName());
    }

    /**
     * loads default colours for the types from the system's type file.  This
     * is used when Triana is first run.
     */
    public static void loadDefaultColours() {
        Vector<String> types = Env.getTrianaTypesAndDefaultColors();
        for (int i = 0; i < types.size(); ++i) {
            Color c;
            Vector<String> sv = FileUtils.splitLine(types.get(i));
            if (sv.size() == 4)  // they have a colour
                c = new Color(Integer.parseInt(sv.get(1)),
                        Integer.parseInt(sv.get(2)), Integer.parseInt(sv.get(3)));
            else
                c = new Color(0, 0, 0); // set to black if no colour is set
            if (sv.size() != 0)
                cableColours.put(sv.get(0), c);
        }
    }


    public static ImageIcon getIcon(String file) {
        ImageIcon sysIcon = FileUtils.getSystemImageIcon(file);
        if (sysIcon == null)
            logger.severe("Icon " + file + " not found.");
        return sysIcon;
    }

    /**
     * Returns the name of the icon to use when TrianaWindow is
     * iconified.
     *
     * @return name of the icon.
     */
    private static String getTrianaIconName() {
        return "triana.png";
    }

    /**
     * Returns an image to use as the icon when TrianaWindow is
     * iconified.
     *
     * @return image to use.
     */
    public static Image getTrianaIcon() {
        return FileUtils.getSystemImage(getTrianaIconName());
    }

    public static ImageIcon getTrianaImageIcon() {
        return new ImageIcon(FileUtils.getSystemImage(getTrianaIconName()));
    }
}










