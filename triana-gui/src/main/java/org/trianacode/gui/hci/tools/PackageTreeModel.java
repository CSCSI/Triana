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
package org.trianacode.gui.hci.tools;

import org.trianacode.taskgraph.tool.Tool;
import org.trianacode.taskgraph.tool.Toolbox;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.net.URL;
import java.util.Hashtable;
import java.util.List;

/**
 * A model for laying out and updating a tree of tools.
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 */
public class PackageTreeModel extends DefaultTreeModel {

    /**
     * The default tree root text
     */
    private static final String ROOT_NAME = "Packages";


    /**
     * The currently loaded tools
     */
    private Toolbox box;

    /**
     * A hashtable of package nodes, keyed by package name
     */
    private Hashtable nodes = new Hashtable();

    public PackageTreeModel(Toolbox box) {
        super(new DefaultMutableTreeNode(ROOT_NAME));
        this.box = box;
        repopulate();
    }

    /**
     * Clears and reloads the tree
     */
    private void repopulate() {
        nodes.clear();
        setRoot(new DefaultMutableTreeNode(ROOT_NAME));
        nodes.put("", getRoot());
        List<Tool> ts = box.getTools();
        for (Tool t : ts) {
            insertTool(t);
        }
    }

    /**
     * Inserts a tool into the tree
     */
    public void insertTool(final Tool tool) {
        final String toolPackage = tool.getToolPackage();

        if (toolPackage != null && toolPackage.length() > 0) {
            if (SwingUtilities.isEventDispatchThread()) {
                addPackages(toolPackage);
            } else {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        addPackages(toolPackage);
                    }
                });
            }
        }
    }

    /**
     * Recursively creates nodes for the specified packages (if they don't already exist)
     */
    private void addPackages(String pack) {
        if (!nodes.containsKey(pack)) {
            String sup;
            String sub;

            if (pack.lastIndexOf('.') > -1) {
                sup = pack.substring(0, pack.lastIndexOf('.'));
                sub = pack.substring(pack.lastIndexOf('.') + 1);
                addPackages(sup);
            } else {
                sup = "";
                sub = pack;
            }
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) nodes.get(sup);
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(sub);
            insertInto(node, parent);
            nodes.put(pack, node);
        }
    }

    /**
     * Alphabetically inserts the new node into its parent
     */
    private void insertInto(DefaultMutableTreeNode newnode, DefaultMutableTreeNode parent) {
        Object newobj = newnode.getUserObject();
        DefaultMutableTreeNode child;
        boolean inserted = false;
        int count = 0;
        int compare;

        while ((count < parent.getChildCount()) && (!inserted)) {
            child = (DefaultMutableTreeNode) parent.getChildAt(count);

            if (newobj instanceof String) {
                if (!(child.getUserObject() instanceof String)) {
                    compare = -1;
                } else {
                    compare = newobj.toString().compareToIgnoreCase(child.getUserObject().toString());
                }
            } else {
                if (child.getUserObject() instanceof String) {
                    compare = 1;
                } else {
                    compare = newobj.toString().compareToIgnoreCase(child.getUserObject().toString());
                }
            }

            // distinguish between different tools with the same name
            if ((compare == 0) && (newobj instanceof Tool) && (child.getUserObject() instanceof Tool)) {
                URL newloc = ((Tool) newobj).getDefinitionPath();
                URL childloc = ((Tool) child.getUserObject()).getDefinitionPath();
                compare = newloc.toString().compareToIgnoreCase(childloc.toString());
            }

            if (compare == 0) {
                removeNodeFromParent(child);
            }

            if (compare <= 0) {
                insertNodeInto(newnode, parent, count);
                inserted = true;
            } else {
                count++;
            }
        }

        if (!inserted) {
            insertNodeInto(newnode, parent, count);
        }
    }

}
