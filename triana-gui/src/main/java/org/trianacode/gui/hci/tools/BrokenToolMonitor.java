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


import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTree;

import org.trianacode.taskgraph.tool.*;
import org.trianacode.taskgraph.tool.Toolbox;

/**
 * A background thread that constantly checks whether any tools have been broken/ unbroken, repainting the tool tree
 * when a change occurs.
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 */

public class BrokenToolMonitor extends Thread implements ToolListener {

    /**
     * a hashtable of flags indicating whether a tool is broken, keyed by tool
     */
    private static Map<Tool, Boolean> brokentable = Collections.synchronizedMap(new HashMap<Tool, Boolean>());


    private ToolTable tooltable;
    private JTree tree;

    private boolean stopped = false;


    public BrokenToolMonitor(ToolTable tooltable) {
        this(tooltable, null);
    }

    public BrokenToolMonitor(ToolTable tooltable, JTree tree) {
        this.tooltable = tooltable;
        this.tree = tree;

        tooltable.addToolTableListener(this);

        setName("BrokenToolThread");
        setPriority(Thread.MIN_PRIORITY);
    }


    /**
     * @return true if the tool is broken TODO - the icon is not updated if the tool ceases to be broken!!
     */
    public static boolean isBroken(Tool tool) {
        if (!brokentable.containsKey(tool)) {
            checkTool(tool);
        }

        return (brokentable.get(tool)).booleanValue();
    }

    /**
     * Checks whether the specified tool is broken, returns true if the state of the tool in the broken tools table has
     * changed
     */
    private static boolean checkTool(Tool tool) {
        boolean broken = ToolTableUtils.isBroken(tool);

        if (brokentable.containsKey(tool)) {
            boolean exist = (brokentable.get(tool)).booleanValue();

            if (exist != broken) {
                brokentable.put(tool, new Boolean(broken));
                return true;
            }
        } else {
            brokentable.put(tool, new Boolean(broken));
            return true;
        }

        return false;
    }


    /**
     * @return the tree repainted by this monitor thread
     */
    public JTree getTree() {
        return tree;
    }

    /**
     * Sets the tree repainted by this monitor thread
     */
    public void setTree(JTree tree) {
        this.tree = tree;
    }


    /**
     * Stops the broken tools thread
     */
    public void stopThread() {
        stopped = true;
    }

    /**
     * @return true if the thread is stopped
     */
    public boolean isStopped() {
        return stopped;
    }


    public void run() {
        Tool tool;
        String[] toolnames = new String[0];

        while (!stopped) {
            int count = 0;
            toolnames = tooltable.getToolNames();
            if (toolnames.length > 0) {
                tool = tooltable.getTool(toolnames[count++]);

                if ((tool != null) && checkTool(tool) && (tree != null)) {
                    tree.repaint();
                }
            }
            try {
                Thread.sleep(1000 * 5);
            } catch (InterruptedException except) {
            }
        }
    }


    @Override
    public void toolsAdded(List<Tool> tools) {
    }

    @Override
    public void toolsRemoved(List<Tool> tools) {
        for (Tool tool : tools) {
            brokentable.remove(tool);
        }
    }

    /**
     * Called when a new tool is added
     */
    public void toolAdded(Tool tool) {
    }

    /**
     * Called when a tool is removed
     */
    public void toolRemoved(Tool tool) {
        brokentable.remove(tool);
    }

    /**
     * Called when a Tool Box is added
     */
    public void toolBoxAdded(Toolbox toolbox) {
    }

    /**
     * Called when a Tool Box is Removed
     */
    public void toolBoxRemoved(Toolbox toolbox) {
    }

}
