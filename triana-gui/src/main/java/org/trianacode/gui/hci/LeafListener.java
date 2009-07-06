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

import com.tomtessier.scrollabledesktop.BaseInternalFrame;
import org.trianacode.gui.action.ActionTable;
import org.trianacode.gui.action.Actions;
import org.trianacode.gui.action.ToolSelectionHandler;
import org.trianacode.gui.action.ToolSelectionListener;
import org.trianacode.gui.action.clipboard.ClipboardActionInterface;
import org.trianacode.gui.action.clipboard.ClipboardPasteInterface;
import org.trianacode.gui.hci.tools.TaskGraphViewManager;
import org.trianacode.gui.main.TaskGraphPanel;
import org.trianacode.gui.panels.OptionPane;
import org.trianacode.taskgraph.TaskException;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.TaskGraphException;
import org.trianacode.taskgraph.TaskGraphUtils;
import org.trianacode.taskgraph.imp.ToolImp;
import org.trianacode.taskgraph.ser.XMLWriter;
import org.trianacode.taskgraph.service.TrianaClient;
import org.trianacode.taskgraph.tool.Tool;
import org.trianacode.taskgraph.tool.ToolTable;
import org.trianacode.util.Env;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


/**
 * LeafListener handles mouse events from Triana Tools in the Tool Box Tree View. It handles the
 * drag and drop of toolTables from the tree to a desktop and the pop-up menu for the toolTables and
 * packages in the tree.
 *
 * @author Ian Taylor
 * @version $Revision: 4048 $
 * @date $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public class LeafListener implements MouseListener, MouseMotionListener, TreeSelectionListener,
        ClipboardActionInterface, ClipboardPasteInterface, ToolSelectionHandler, Actions {

    private static final long minTimeDiff = 500;

    private MouseEvent lastEvent = null; // A reference to the last mouse event
    private MouseEvent lastClickEvent = null; // The last click event -- a bug in X Windows fix!
    private MouseEvent startDrag = null; // The last start drag event
    private Point mtStartDrag;
    private Tool selectedTool; // The current lead selected tool

    private Component workspaceContainer; // The desktop that contains MainTriana panels
    private JTree toolBoxTree; // The tree that this listener belongs to

    /**
     * the main tool table
     */
    private ToolTable toolTable;

    private JPopupMenu packageMenu = new JPopupMenu("Package Menu");
    private JPopupMenu multipleMenu = new JPopupMenu("Tools Menu");

    /**
     * a list of the tool selection handlers
     */
    private ArrayList sellisteners = new ArrayList();


    public LeafListener(JTree tree, Component workspaceContainer, ToolTable tools) {
        this.workspaceContainer = workspaceContainer;
        this.toolBoxTree = tree;
        this.toolTable = tools;

        tree.addTreeSelectionListener(this);

        initPackageMenu();
        initMultipleMenu();
    }


    /**
     * Adds a listener to be notified when the tool selection changes
     */
    public void addToolSelectionListener(ToolSelectionListener listener) {
        if (!sellisteners.contains(listener))
            sellisteners.add(listener);
    }

    /**
     * Removes a listener from being notified when the tool selection changes
     */
    public void removeToolSelectionListener(ToolSelectionListener listener) {
        sellisteners.remove(listener);
    }


    /**
     * @return true if only a single tool is selected
     */
    public boolean isSingleSelectedTool() {
        return (selectedTool != null) && (getSelectedTools().length == 1);
    }

    /**
     * @return the currently selected tool (null if none selected)
     */
    public Tool getSelectedTool() {
        return selectedTool;
    }

    /**
     * @return an array of the currently selected tools
     */
    public Tool[] getSelectedTools() {
        ArrayList sellist = new ArrayList();
        TreePath[] paths = toolBoxTree.getSelectionPaths();

        if (paths != null)
            for (int count = 0; count < paths.length; count++)
                addNode((DefaultMutableTreeNode) paths[count].getLastPathComponent(), sellist);

        return (Tool[]) sellist.toArray(new Tool[sellist.size()]);
    }

    /**
     * @return the currently selected taskgraph (usually parent of selected tool)
     */
    public TaskGraph getSelectedTaskgraph() {
        return null;
    }


    /**
     * Returns a array of containing the tools stored in the selected branches
     * of the tree, this is done by recursively traversing the nodes
     * children.
     * <p/>
     * For a single tool an array containing a copy of that tool is returned;
     * for a package it is all the tools in that package and sub packages.
     *
     * @param repackage a flag indicating whether the tools are repackaged so
     *                  their tool package reflects the correct paste subpackage.
     */
    private Tool[] getSelectedTools(boolean repackage) {
        ArrayList tools = new ArrayList();
        DefaultMutableTreeNode[] nodes = getSelectedNodes();

        recurseTools(nodes, tools, repackage);

        return (Tool[]) tools.toArray(new Tool[tools.size()]);
    }

    /**
     * @return the triana client responsible for the selected tools (null if none)
     */
    public TrianaClient getSelectedTrianaClient() {
        return null;
    }


    /**
     * Copy selected Tools to the Clipboard.
     */
    public void copyToClipboard() throws TaskException {
        handleCopy();
    }

    /**
     * Copy selected Tools to the Clipboard and delete them from the Container they are located in.
     */
    public void cutToClipboard() throws TaskException {
        handleCut();
    }

    /**
     * Delete the selected Tool.
     */
    public void deleteTools(boolean files) {
        handleDelete(true, files);
    }

    /**
     * Rename the selected Tool or Group.
     */
    public void renameTool() {
        DefaultMutableTreeNode node = getSelectedNode();
        handleRename(node);
    }

    /**
     * Paste Tools from the Clipboard to this container.
     */
    public void pasteFromClipboard() throws TaskException {
        DefaultMutableTreeNode node = getSelectedNode();
        handlePaste(node);
    }


    private void initPackageMenu() {
        packageMenu.add(new JMenuItem(ActionTable.getAction(CUT_ACTION)));
        packageMenu.add(new JMenuItem(ActionTable.getAction(COPY_ACTION)));
        packageMenu.add(new JMenuItem(ActionTable.getAction(PASTE_ACTION)));
        packageMenu.add(new JMenuItem(ActionTable.getAction(DELETE_ACTION)));
        packageMenu.add(new JMenuItem(ActionTable.getAction(DELETE_REFERENCES_ACTION)));
        packageMenu.add(new JMenuItem(ActionTable.getAction(RENAME_ACTION)));
        packageMenu.add(new JMenuItem(ActionTable.getAction(COMPILE_ACTION)));
    }

    private void initMultipleMenu() {
        multipleMenu.add(new JMenuItem(ActionTable.getAction(CUT_ACTION)));
        multipleMenu.add(new JMenuItem(ActionTable.getAction(COPY_ACTION)));
        multipleMenu.add(new JMenuItem(ActionTable.getAction(DELETE_ACTION)));
        multipleMenu.add(new JMenuItem(ActionTable.getAction(DELETE_REFERENCES_ACTION)));
        multipleMenu.add(new JMenuItem(ActionTable.getAction(COMPILE_ACTION)));
    }

    /**
     * @return the tool or null if this event does not come from a leaf node containing a
     *         Tool
     */
    private Tool getTool(MouseEvent event) {
        DefaultMutableTreeNode node = getNode(event);

        if (node == null)
            return null;

        if (node.getUserObject() instanceof Tool)
            return (Tool) node.getUserObject();
        else
            return null;

    }

    /**
     * Return the selected node in the tree.
     */
    private DefaultMutableTreeNode getNode(MouseEvent e) {
        Component source = e.getComponent();
        if (source instanceof JTree) {
            JTree jt = (JTree) source;
            TreePath t = jt.getClosestPathForLocation(e.getX(), e.getY());
            return ((DefaultMutableTreeNode) t.getLastPathComponent());
        }
        return null;
    }

    /**
     * Used by getTools to recursively build an array list of the tools
     * stored in the specified nodes and their children
     */
    private void recurseTools(DefaultMutableTreeNode[] nodes, ArrayList list, boolean repackage) {
        for (int count = 0; count < nodes.length; count++)
            recurseTools(nodes[count], list, "", repackage);
    }

    /**
     * Used by getTools to recursively build an array list of the tools
     * stored in the specified nodes and their children
     */
    private void recurseTools(DefaultMutableTreeNode node, ArrayList list, String pack, boolean repackage) {
        if (node.getUserObject() instanceof String) {
            if (!pack.equals(""))
                pack += ".";

            pack += (String) node.getUserObject();
        } else if (node.getUserObject() instanceof Tool) {
            if (repackage) {
                try {
                    Tool copy = TaskGraphUtils.cloneTool((Tool) node.getUserObject());
                    copy.setToolPackage(pack);

                    list.add(copy);
                } catch (TaskException except) {
                }
            } else
                list.add(node.getUserObject());
        }

        if (!node.isLeaf())
            for (int count = 0; count < node.getChildCount(); count++)
                recurseTools((DefaultMutableTreeNode) node.getChildAt(count), list, pack, repackage);
    }


    private DefaultMutableTreeNode getSelectedNode() {
        return (DefaultMutableTreeNode) toolBoxTree.getLastSelectedPathComponent();
    }

    private DefaultMutableTreeNode[] getSelectedNodes() {
        TreePath[] paths = toolBoxTree.getSelectionPaths();
        ArrayList nodes = new ArrayList();

        if (paths != null) {
            for (int count = 0; count < paths.length; count++)
                if (paths[count].getLastPathComponent() instanceof DefaultMutableTreeNode)
                    nodes.add(paths[count].getLastPathComponent());
        }

        return (DefaultMutableTreeNode[]) nodes.toArray(new DefaultMutableTreeNode[nodes.size()]);
    }

    /**
     * If the user object at the tree node is a Tool this returns the package for that tool. If it
     * is a package node then the node hierarchy is used to suggest a package.
     * TODO - this bases the suggested package on the visible nodes in the tree, which may be mangled
     * due to the filter being imposed on the tree
     */
    private String getPackage(DefaultMutableTreeNode node) {
        if (node.getUserObject() instanceof Tool)
            return ((Tool) node.getUserObject()).getToolPackage();
        else {
            String pack = "";

            while (!node.isRoot()) {
                if (!pack.equals(""))
                    pack = "." + pack;

                pack = node.getUserObject().toString() + pack;
                node = (DefaultMutableTreeNode) node.getParent();
            }

            return pack;
        }
    }

    /**
     * This handles the double clicking of the toolTables and pop-up menu selection. Each tool has
     * its own defined function for double clicking which is activated here.
     */
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            long timeDiff = minTimeDiff;
            if (lastClickEvent != null)
                timeDiff = e.getWhen() - lastClickEvent.getWhen();
            if ((e.getClickCount() == 2) || (timeDiff < minTimeDiff))
                doubleClick(e);
            lastEvent = e;
            lastClickEvent = e;
        }
    }

    /**
     * Acts upon a double-Click event
     */
    public void doubleClick(MouseEvent e) {
        handleOpenGroup(getTool(e));
    }

    /**
     * Invoked when the mouse exits a component.
     */
    public void mouseExited(MouseEvent e) {
    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     */
    public void mousePressed(MouseEvent e) {
        mtStartDrag = new Point(e.getX(), e.getY());

        lastEvent = e;
        if (e.isPopupTrigger()) {
            if (!toolBoxTree.isPathSelected(toolBoxTree.getPathForLocation(e.getX(), e.getY())))
                toolBoxTree.setSelectionPath(toolBoxTree.getPathForLocation(e.getX(), e.getY()));

            if (toolBoxTree.getSelectionCount() > 1)
                multipleMenu.show(e.getComponent(), e.getX(), e.getY());
            else if (getNode(e).getUserObject() instanceof String)
                packageMenu.show(e.getComponent(), e.getX(), e.getY());
            else if (selectedTool != null)
                TaskGraphViewManager.getTreePopup(selectedTool).show(e.getComponent(), e.getX(), e.getY());
        }
    }

    /**
     * Invoked when a mouse button is pressed on a component and then dragged.  Mouse drag events
     * will continue to be delivered to the component where the first originated until the mouse
     * button is released (regardless of whether the mouse position is within the bounds of the
     * component).
     */
    public void mouseDragged(MouseEvent e) {
        boolean setStartDrag = false;

        if (!SwingUtilities.isLeftMouseButton(e))
            return;  // only can drag with the left mouse button

        if (selectedTool == null)
            return;

        if ((lastEvent != null) && (lastEvent.getID() == MouseEvent.MOUSE_PRESSED))
            setStartDrag = true;

        if ((lastEvent != null) && (lastEvent.getID() == MouseEvent.MOUSE_MOVED) &&
                (!Env.os().equals("windows")))
            setStartDrag = true;

        // this should be impossible BUT windows 95 it seems it isn't!!

        if ((lastEvent != null) && (lastEvent.getID() == MouseEvent.MOUSE_RELEASED) &&
                (Env.os().equals("windows"))) {
            setStartDrag = true;
        }

        if (setStartDrag) {
            startDrag = lastEvent;
            DragWindow.DRAG_WINDOW.setTool(selectedTool, toolTable);
        } else if (startDrag == null)
            return;

        lastEvent = e;

        Component source = e.getComponent();

        int absX = source.getLocationOnScreen().x + startDrag.getX() + e.getX() -
                (DragWindow.DRAG_WINDOW.getSize().width / 2);
        int absY = source.getLocationOnScreen().y + startDrag.getY() + e.getY() -
                (DragWindow.DRAG_WINDOW.getSize().height / 2);
        Point pMove = new Point(absX - mtStartDrag.x, absY - mtStartDrag.y);

        DragWindow.DRAG_WINDOW.setLocation(pMove);

        if (!DragWindow.DRAG_WINDOW.isVisible()) {
            DragWindow.DRAG_WINDOW.setVisible(true);
            DragWindow.DRAG_WINDOW.repaint();
        }
    }

    /**
     * Invoked when a mouse button has been released on a component.
     */
    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
            if (!toolBoxTree.isPathSelected(toolBoxTree.getPathForLocation(e.getX(), e.getY())))
                toolBoxTree.setSelectionPath(toolBoxTree.getPathForLocation(e.getX(), e.getY()));

            if (toolBoxTree.getSelectionCount() > 1)
                multipleMenu.show(e.getComponent(), e.getX(), e.getY());
            else if (getNode(e).getUserObject() instanceof String)
                packageMenu.show(e.getComponent(), e.getX(), e.getY());
            else if (selectedTool != null)
                TaskGraphViewManager.getTreePopup(selectedTool).show(e.getComponent(), e.getX(), e.getY());
        } else if (startDrag != null) {
            DragWindow.DRAG_WINDOW.setVisible(false);

            Component source = e.getComponent();
            Component landed = workspaceContainer;
            Component parent;
            int landingPosX;
            int landingPosY;

            // find the main triana window unit is dropped on (if it is dropped on one)
            do {
                parent = landed;

                landingPosX = e.getX() -
                        (parent.getLocationOnScreen().x - source.getLocationOnScreen().x);
                landingPosY = e.getY() -
                        (parent.getLocationOnScreen().y - source.getLocationOnScreen().y);

                landed = parent.getComponentAt(landingPosX, landingPosY);
            } while ((landed != null) && (landed != parent) &&
                    (!(landed instanceof BaseInternalFrame)));

            // Base Internal frmaes are the containers for our MainTriana objects
            if (landed instanceof BaseInternalFrame) { // ok we have a drop target
                // locate the mainTriana panel
                TaskGraphPanel cont = GUIEnv.getTaskGraphPanelFor((BaseInternalFrame) landed);
                cont.getContainer().requestFocus();
                // landing position within this frame
                landingPosX = e.getX() - (cont.getContainer().getLocationOnScreen().x -
                        source.getLocationOnScreen().x);
                landingPosY = e.getY() - (cont.getContainer().getLocationOnScreen().y -
                        source.getLocationOnScreen().y);

                createTool(cont, DragWindow.DRAG_WINDOW.getTool(),
                        landingPosX - (DragWindow.DRAG_WINDOW.getSize().width / 2),
                        landingPosY - (DragWindow.DRAG_WINDOW.getSize().height / 2));
                ((BaseInternalFrame) landed).selectFrameAndAssociatedButtons();
            }


            lastEvent = e;
            startDrag = null;
        }
    }

    public void createTool(TaskGraphPanel cont, Tool tool, int x, int y) {
        TaskGraphHandler.createTask(tool, cont, x, y);
    }

    /**
     * Invoked when the mouse enters a component.
     */
    public void mouseEntered(MouseEvent e) {
        if (e == null)
            return;
        lastEvent = e;
    }

    /**
     * Invoked when the mouse button has been moved on a component (with no buttons no down).
     */
    public void mouseMoved(MouseEvent e) {
    }

    /**
     * Opens the group tool in the main triana window
     */
    private void handleOpenGroup(Tool tool) {
        if (tool instanceof TaskGraph) {
            final TaskGraph initgraph = (TaskGraph) tool;
            final TrianaProgressBar progressBar = new TrianaProgressBar("loading: " + initgraph.getToolName(), false);
            Thread openThread = new Thread(new Runnable() {
                public void run() {
                    GUIEnv.getApplicationFrame().addParentTaskGraphPanel(initgraph);
                    progressBar.disposeProgressBar();
                }
            });
            openThread.setPriority(Thread.NORM_PRIORITY);
            openThread.setName("Triana Open Group");
            openThread.start();
        }
    }

    /**
     * Cuts the selected tool/package of toolTables from the tree
     */
    private void handleCut() {
        Tool[] seltools = getSelectedTools();
        Set<String> nonXML = new HashSet<String>();

        for (Tool seltool : seltools) {
            if (!seltool.getDefinitionType().equals(Tool.DEFINITION_TRIANA_XML)) {
                nonXML.add(seltool.getToolName());
            }
        }
        if (nonXML.size() > 0) {
            int lines = 1;
            StringBuilder sb = new StringBuilder("Tools to be converted to XML:\n");
            for (String s : nonXML) {
                sb.append(s).append("\n");
                lines++;
                if (lines > 14) {
                    sb.append("...\n");
                    break;
                }
            }
            boolean ok = OptionPane.showOkCancel(sb.toString(), "Tool Conversion", workspaceContainer);
            if (!ok) {
                return;
            }
        }
        Boolean others = checkAffectedTools(seltools);
        if (others != null && others == false) {
            return;
        }
        handleCopy();
        handleDelete(false, true);
    }

    /**
     * Copies the selected tool/package of toolTables from the tree
     */
    private void handleCopy() {
        try {
            Clipboard.putTools(getSelectedTools(true), true);
        } catch (TaskGraphException except) {
            throw (new RuntimeException("Copy Error: " + except.getMessage()));
        }
    }

    /**
     * Pastes the specified toolTables stored into the tree.
     */
    private void handlePaste(DefaultMutableTreeNode node) {
        PasteHandler handler = new PasteHandler(toolTable, Env.getString("pasteToolsInto"));
        handler.handlePaste(Clipboard.getTools(true), getPackage(node));
    }

    /**
     * shows the user other affected tools when deleting.
     * A null returned value means there were no other affected tools.
     * true means the user was ok with losing the other tools.
     * false means they were not.
     *
     * @param selectedTools
     * @return
     */
    private Boolean checkAffectedTools(Tool[] selectedTools) {
        Map<String, Set<String>> allAffected = new HashMap<String, Set<String>>();
        for (Tool seltool : selectedTools) {
            String def = seltool.getDefinitionPath();
            Tool[] affected = toolTable.getTools(def);
            if (affected.length > 0) {
                Set<String> others = allAffected.get(def);
                if (others == null) {
                    others = new HashSet<String>();
                }
                for (Tool tool : affected) {
                    others.add(tool.getToolName());
                }
                allAffected.put(def, others);
            }
        }
        if (allAffected.size() > 0) {
            StringBuilder sb = new StringBuilder("\nDeleting: ");
            int lines = 0;
            for (String s : allAffected.keySet()) {
                if (lines > 14) {
                    sb.append("...\n");
                    break;
                }
                sb.append(s).append(":\n").append("The following tools will be deleted:\n");
                lines += 2;
                Set<String> aff = allAffected.get(s);
                for (String s1 : aff) {
                    if (lines > 14) {
                        break;
                    }
                    sb.append(s1).append("\n");
                    lines++;
                }
            }
            int reply = JOptionPane.showConfirmDialog(GUIEnv.getApplicationFrame(),
                    sb.toString() + "\nDo you wish to continue?",
                    "Delete Files", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, GUIEnv.getTrianaIcon());
            if (reply == JOptionPane.OK_OPTION) {
                return true;
            } else {
                return false;
            }
        }
        return null;
    }

    /**
     * Deletes the xml files for the specified toolTables
     */
    private void handleDelete(boolean prompt, boolean files) {
        int reply = JOptionPane.OK_OPTION;
        Tool[] seltools = getSelectedTools();
        if (files) {
            Boolean b = checkAffectedTools(seltools);
            if (b) {
                return;
            } else if (!b) {
                prompt = false;
            }
        }
        DefaultMutableTreeNode[] nodes = getSelectedNodes();
        String del = files ? Env.getString("Delete") : Env.getString("DeleteReferences");
        String q1 = files ? Env.getString("deleteTool") : Env.getString("removeTool");
        String q2 = files ? Env.getString("deletePackage") : Env.getString("removePackage");
        String q3 = files ? Env.getString("deleteSelected") : Env.getString("removeSelected");
        if (prompt) {
            if (nodes.length == 1) {
                if (nodes[0].getUserObject() instanceof Tool) {
                    String toolname = ((Tool) nodes[0].getUserObject()).getToolName();
                    reply = JOptionPane.showConfirmDialog(GUIEnv.getApplicationFrame(),
                            q1 + " " + toolname + "?",
                            del, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, GUIEnv.getTrianaIcon());
                } else {
                    String pack = getPackage(nodes[0]);
                    reply = JOptionPane.showConfirmDialog(GUIEnv.getApplicationFrame(),
                            q2 + " " + pack + "?",
                            del, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, GUIEnv.getTrianaIcon());
                }
            } else {
                reply = JOptionPane.showConfirmDialog(GUIEnv.getApplicationFrame(),
                        q3 + "?",
                        del, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, GUIEnv.getTrianaIcon());
            }
        }


        Tool[] deltools = new Tool[seltools.length];
        System.arraycopy(seltools, 0, deltools, 0, seltools.length);

        if (reply == JOptionPane.OK_OPTION) {
            for (int count = 0; count < deltools.length; count++) {

                toolTable.deleteTool(deltools[count], files);
            }
        }
    }

    /**
     * Renames a tool or a package
     */
    private void handleRename(DefaultMutableTreeNode node) {
        if (node.getUserObject() instanceof Tool)
            handleRenameTool((Tool) node.getUserObject());
        else
            handleRenamePackage(node);
    }

    /**
     * Renames a tool
     */
    private void handleRenameTool(Tool tool) {
        boolean copy = false;
        if (!tool.getDefinitionType().equals(Tool.DEFINITION_TRIANA_XML) || !toolTable.isModifiable(tool)) {
            int reply = JOptionPane.showConfirmDialog(GUIEnv.getApplicationFrame(),
                    tool.getToolName() + " is not defined in XML. Do want to create a copy to rename?",
                    Env.getString("Rename"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, GUIEnv.getTrianaIcon());
            if (reply != JOptionPane.OK_OPTION) {
                return;
            } else {
                copy = true;
            }
        }

        String name = (String) JOptionPane.showInputDialog(GUIEnv.getApplicationFrame(), Env.getString("newNameFor") +
                " " +
                tool.getToolName() +
                "?", Env.getString("Rename"),
                JOptionPane.QUESTION_MESSAGE, GUIEnv.getTrianaIcon(), null, tool.getToolName());

        if ((name != null) && (!name.equals(tool.getToolName()))) {
            try {
                if (copy) {
                    Tool other = copyTool(tool, name);
                    if (other == null) {
                        return;
                    }
                    tool = other;
                }
                tool.setToolName(name);
                XMLWriter writer = new XMLWriter(new BufferedWriter(new FileWriter(tool.getDefinitionPath())));
                writer.writeComponent(tool);
                writer.close();
                toolTable.refreshLocation(tool.getDefinitionPath(), tool.getToolBox());
            } catch (IOException except) {
                throw (new RuntimeException("Error writing xml for " + tool.getToolName() + ": " + except.getMessage()));
            } catch (TaskException e) {
                e.printStackTrace();
            }

        }
    }

    private Tool copyTool(Tool tool, String newName) throws TaskException {
        ToolImp newTool = new ToolImp(tool);
        String path = tool.getDefinitionPath();
        File f = new File(path);
        if (!f.exists() || f.length() == 0) {
            OptionPane.showError("Could not get root file for tool definition", "Error", GUIEnv.getApplicationFrame());
            return null;
        } else {
            f = new File(f.getParentFile(), newName + ".xml");
            newTool.setToolName(newName);
            newTool.setDefinitionPath(f.getAbsolutePath());
            newTool.setDefinitionType(Tool.DEFINITION_TRIANA_XML);
        }
        return newTool;
    }


    /**
     * Renames a package TODO fix this
     */
    private void handleRenamePackage(DefaultMutableTreeNode node) {
        JOptionPane.showMessageDialog(GUIEnv.getApplicationFrame(), "Cannot rename package",
                Env.getString("Rename"), JOptionPane.OK_OPTION, GUIEnv.getTrianaIcon());
    }


    /**
     * Called whenever the value of the selection changes.
     *
     * @param event the event that characterizes the change.
     */
    public void valueChanged(TreeSelectionEvent event) {
        if (event.getSource() == toolBoxTree) {
            if (event.getNewLeadSelectionPath() != null)
                selectedTool = convertNodeToTool((DefaultMutableTreeNode) event.getNewLeadSelectionPath().getLastPathComponent());
            else
                selectedTool = null;

        }
    }

    private void addNode(DefaultMutableTreeNode node, ArrayList sellist) {
        Tool tool = convertNodeToTool(node);

        if (tool != null)
            sellist.add(tool);
        else
            addPackage(node, sellist);
    }

    private void addPackage(DefaultMutableTreeNode node, ArrayList sellist) {
        for (int count = 0; count < node.getChildCount(); count++)
            addNode((DefaultMutableTreeNode) node.getChildAt(count), sellist);
    }

    private Tool convertNodeToTool(DefaultMutableTreeNode node) {
        if ((node != null) && (node.getUserObject() instanceof Tool))
            return (Tool) node.getUserObject();
        else
            return null;
    }

}
