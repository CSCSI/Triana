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

import org.trianacode.gui.action.ActionDisplayOptions;
import org.trianacode.gui.action.ActionTable;
import org.trianacode.gui.action.Actions;
import org.trianacode.gui.action.files.OpenRecentListener;
import org.trianacode.gui.action.files.OptionsMenuHandler;
import org.trianacode.gui.action.tools.ToolImportAction;
import org.trianacode.gui.action.tools.ToolsMenuHandler;
import org.trianacode.gui.extensions.Extension;
import org.trianacode.gui.extensions.ExtensionManager;
import org.trianacode.taskgraph.tool.ToolTable;
import org.trianacode.util.Env;

import javax.swing.*;
import java.util.Vector;

/**
 * This is the main Triana menu that sits at the top of the main Application Frame. It almagamates
 * code from TrianaWindow, MainTrianaBaseWindow and ApplicationFrame.
 */
public class TrianaMainMenu extends JMenuBar implements Actions {

    /**
     * Reference to the ApplicationFrame that this menu is attached to.
     */
    private ApplicationFrame applicationFrame = null;

    /**
     * Menus
     */
    private JMenu fileMenu;
    private JMenu helpMenu;
    private JMenu toolsMenu;
    private JMenu editMenu;
    private JMenu optionsMenu;
    private JMenu recentMenu;
    private JMenu runMenu;
    //private JMenu servicesMenu;

    /**
     * The menu item for help.  You may need to access this if you want to change the ActionListener
     * for the help menu option i.e. send the event somewhere else. By default the event is handled
     * by this window itself.
     */
    public JMenuItem help;

    /**
     * The menu item for close.  You may need to access this if you want to change the
     * ActionListener for the close menu option i.e. send the event somewhere else. By default the
     * event is handled by this window itself
     */
    public JMenuItem close;

    /**
     * The listener for the recent files list.
     */
    private OpenRecentListener openRecentListener;


    public TrianaMainMenu(ApplicationFrame parentWindow, ToolTable tools) {
        super();
        applicationFrame = parentWindow;
        openRecentListener = new OpenRecentListener(applicationFrame);
        createMenus(tools);
    }

    private void createMenus(ToolTable tools) {
        JMenuItem item;

        helpMenu = MenuMnemonics.getInstance().createMenu(Env.getString("Help"));
        fileMenu = MenuMnemonics.getInstance().createMenu(Env.getString("File"));

        fileMenu.add(new JMenuItem(ActionTable.getAction(NEW_ACTION)));
        fileMenu.add(new JMenuItem(ActionTable.getAction(OPEN_FILE_ACTION)));

        recentMenu = MenuMnemonics.getInstance().createMenu(Env.getString("Recent"));
        fileMenu.add(recentMenu);
        updateRecentMenu();
        fileMenu.add(new JMenuItem(ActionTable.getAction(RENDER_ACTION)));

        fileMenu.addSeparator();
        fileMenu.add(new JMenuItem(ActionTable.getAction(SAVE_ACTION)));
        fileMenu.add(new JMenuItem(ActionTable.getAction(SAVE_AS_ACTION)));
        fileMenu.add(new JMenuItem(ActionTable.getAction(CLOSE_ACTION)));
        fileMenu.addSeparator();
        fileMenu.add(new JMenuItem(ActionTable.getAction(IMPORT_ACTION)));
        fileMenu.add(new JMenuItem(ActionTable.getAction(EXPORT_ACTION)));
        fileMenu.addSeparator();
        fileMenu.add(new JMenuItem(ActionTable.getAction(PRINT_ACTION)));
        fileMenu.addSeparator();
        MenuMnemonics.getInstance().createMenuItem(Env.getString("Quit"), fileMenu, applicationFrame);
        MenuUtils.assignMnemonics(fileMenu);

        MenuMnemonics.getInstance().createMenuItem(Env.getString("Index"), helpMenu, applicationFrame);
        helpMenu.addSeparator();
        MenuMnemonics.getInstance().createMenuItem(Env.getString("GettingStarted"), helpMenu, applicationFrame);
        help = new JMenuItem(ActionTable.getAction(HELP_ACTION));
        help.setName(Env.getString("UnitHelp"));
        helpMenu.add(help);
        MenuMnemonics.getInstance().createMenuItem(Env.getString("Tutorial"), helpMenu, applicationFrame);
        MenuMnemonics.getInstance().createMenuItem(Env.getString("Manual"), helpMenu, applicationFrame);
        MenuMnemonics.getInstance().createMenuItem(Env.getString("JavaDoc"), helpMenu, applicationFrame);
        helpMenu.addSeparator();
        help = new JMenuItem(ActionTable.getAction(FIND_ACTION));
        helpMenu.add(help);
        MenuMnemonics.getInstance().createMenuItem(Env.getString("FindTool"), helpMenu, applicationFrame);
        helpMenu.addSeparator();
        MenuMnemonics.getInstance().createMenuItem(Env.getString("System"), helpMenu, applicationFrame);
        MenuMnemonics.getInstance().createMenuItem(Env.getString("About"), helpMenu, applicationFrame);

        editMenu = MenuMnemonics.getInstance().createMenu(Env.getString("Edit"));
        editMenu.add(new JMenuItem(ActionTable.getAction(CUT_ACTION)));
        editMenu.add(new JMenuItem(ActionTable.getAction(COPY_ACTION)));
        editMenu.add(new JMenuItem(ActionTable.getAction(PASTE_ACTION)));
        editMenu.addSeparator();
        editMenu.add(new JMenuItem(ActionTable.getAction(GROUP_ACTION)));
        editMenu.add(new JMenuItem(ActionTable.getAction(UNGROUP_ACTION)));
        editMenu.addSeparator();
        editMenu.add(new JMenuItem(ActionTable.getAction(SELECT_ALL_ACTION)));
        editMenu.add(new JMenuItem(ActionTable.getAction(DELETE_ACTION)));
        editMenu.add(new JMenuItem(ActionTable.getAction(CLEAR_ACTION)));
        MenuUtils.assignMnemonics(editMenu);

        toolsMenu = MenuMnemonics.getInstance().createMenu(Env.getString("tools"));
        ToolsMenuHandler toolsMenuHandler = new ToolsMenuHandler(tools);
        MenuMnemonics.getInstance().createMenuItem(Env.getString("newUnit"), toolsMenu, toolsMenuHandler);
        MenuMnemonics.getInstance().createMenuItem(Env.getString("compileGenerate"), toolsMenu, toolsMenuHandler);
        MenuMnemonics.getInstance().createMenuItem(Env.getString("compileAll"), toolsMenu, toolsMenuHandler);
        item = new JMenuItem(new ToolImportAction(applicationFrame.getTaskGraphFileHandler(), ActionDisplayOptions.DISPLAY_NAME, toolsMenu, tools));
        toolsMenu.add(item);
        toolsMenu.addSeparator();
        MenuMnemonics.getInstance().createMenuItem(Env.getString("editToolBoxPaths"), toolsMenu, toolsMenuHandler);
        toolsMenu.addSeparator();
        MenuMnemonics.getInstance().createMenuItem(Env.getString("generateCommandLineApp"), toolsMenu, toolsMenuHandler);

        Action[] extentions = ExtensionManager.getWorkflowExtensions(Extension.TOOL_TYPE);

        if (extentions.length > 0) {
            JMenu extmenu = new JMenu("Extensions");

            for (int count = 0; count < extentions.length; count++)
                extmenu.add(extentions[count]);

            toolsMenu.addSeparator();
            toolsMenu.add(extmenu);
        }

        /*servicesMenu = MenuMnemonics.getInstance().createMenu(Env.getString("services"));
        servicesMenu.add(new JMenuItem(ActionTable.getAction(DISCOVER_SERVICES_ACTION)));
        servicesMenu.add(new JMenuItem(ActionTable.getAction(IMPORT_SERVICE_ACTION)));
        servicesMenu.addSeparator();
        servicesMenu.add(new JMenuItem(ActionTable.getAction(CREATE_SERVICE_ACTION)));
        servicesMenu.add(new JMenuItem(ActionTable.getAction(DISTRIBUTE_PROTOSERVICES_ACTION)));
        servicesMenu.addSeparator();
        servicesMenu.add(new JMenuItem(ActionTable.getAction(CONFIGURE_PEER_ACTION)));
        servicesMenu.addSeparator();
        servicesMenu.add(new JMenuItem(ActionTable.getAction(DART_DISTRIBUTE_ACTION)));
        MenuUtils.assignMnemonics(servicesMenu);

        extentions = ExtensionManager.getWorkflowExtensions(Extension.SERVICE_TYPE);

        if (extentions.length > 0) {
            JMenu extmenu = new JMenu("Extensions");

            for (int count = 0; count < extentions.length; count++)
                extmenu.add(extentions[count]);

            servicesMenu.addSeparator();
            servicesMenu.add(extmenu);
        }*/

        optionsMenu = MenuMnemonics.getInstance().createMenu(Env.getString("Options"));
        OptionsMenuHandler optionsMenuHandler = new OptionsMenuHandler(applicationFrame.getTools());
        item = MenuMnemonics.getInstance().createCheckBoxMenuItem(Env.getString("DebugWindow"), optionsMenu, optionsMenuHandler);
        optionsMenuHandler.addMonitorDebugWindow(item);
        optionsMenu.addSeparator();
        MenuMnemonics.getInstance().createMenuItem(Env.getString("TrianaOptionTitle"), optionsMenu, optionsMenuHandler);

        runMenu = MenuMnemonics.getInstance().createMenu(Env.getString("Run"));
        runMenu.add(new JMenuItem(ActionTable.getAction(RUN_ACTION)));
        //runMenu.add(new JMenuItem(ActionTable.getAction(RUN_HISTORY_ACTION)));
        runMenu.add(new JMenuItem(ActionTable.getAction(PAUSE_ACTION)));
        runMenu.add(new JMenuItem(ActionTable.getAction(RESET_ACTION)));
        MenuUtils.assignMnemonics(runMenu);

        this.add(fileMenu);
        this.add(editMenu);
        this.add(runMenu);
        this.add(toolsMenu);
        //this.add(servicesMenu);

        this.add(optionsMenu);
    }

    /**
     * Called after creation to make sure that the ehlp menu is addedc at the end of the menu bar.
     */
    public void addHelp() {
        this.add(helpMenu);
    }


    /**
     * Called when a file is opened or saved, this method keeps a record of the most recently
     * accessed taskgraph files.
     * <p/>
     * TODO - question: do we need to explicitly remove listeners from the recent items we are
     * deleting?
     */
    public void updateRecentMenu() {
        String[] recentItems = Env.getRecentFilePaths();
        final Vector<String> shortNames = new Vector<String>();
        for (int i = 0; i < recentItems.length; i++) {
            if (!recentItems[i].equals("")) {
                shortNames.add(openRecentListener.addRecent(recentItems[i]));
            }
        }
        Thread thread = new Thread() {
            public void run() {
                recentMenu.removeAll();
                for (int i = 0; i < shortNames.size(); i++) {
                    MenuMnemonics.getInstance().createMenuItem(shortNames.get(i),
                            recentMenu, openRecentListener);
                }
            }
        };
        SwingUtilities.invokeLater(thread);
    }

    /**
     * Check the recent items list, if this item is not included add it to the end, losing the top
     * item.
     *
     * @param item the recent file to add
     */
    public void updateRecentMenu(String item) {
        Env.addRecentFilePath(item);
        updateRecentMenu();
    }

}
