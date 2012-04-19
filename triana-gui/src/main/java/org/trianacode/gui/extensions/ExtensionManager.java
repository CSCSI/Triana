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

package org.trianacode.gui.extensions;


import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.tool.Tool;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * The manager responsible for handling extension classes.
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 */

public class ExtensionManager {

    /**
     * a list of the extension instances
     */
    private static final ArrayList extlist = new ArrayList();


    /**
     * Register an extension
     */
    public static void registerExtension(Extension extension) {
        if (!extlist.contains(extension)) {
            extlist.add(extension);
        }
    }

    /**
     * Unregister an extension
     */
    public static void unregisterExtension(Extension extension) {
        extlist.remove(extension);
    }


    /**
     * @return an array of the workspace extension actions for the specified tool
     */
    public static Action[] getWorkspaceExtensions(Task tool) {
        ArrayList exts = new ArrayList();
        Action action;

        for (Iterator it = extlist.iterator(); it.hasNext(); ) {
            action = ((Extension) it.next()).getWorkspaceAction(tool);

            if (action != null) {
                exts.add(action);
            }
        }

        return (Action[]) exts.toArray(new Action[exts.size()]);
    }

    /**
     * @return an array of the tool tree extension actions for the specified tool
     */
    public static Action[] getTreeExtensions(Tool tool) {
        ArrayList exts = new ArrayList();
        Action action;

        for (Iterator it = extlist.iterator(); it.hasNext(); ) {
            action = ((Extension) it.next()).getTreeAction(tool);

            if (action != null) {
                exts.add(action);
            }
        }

        return (Action[]) exts.toArray(new Action[exts.size()]);
    }

    /**
     * @return an array of the workfloe actions of the specified type (Extension.TOOL_TYPE or Extension.SERVICE_TYPE)
     */
    public static Action[] getWorkflowExtensions(int type) {
        ArrayList exts = new ArrayList();
        Action action;

        for (Iterator it = extlist.iterator(); it.hasNext(); ) {
            action = ((Extension) it.next()).getWorkflowAction(type);

            if (action != null) {
                exts.add(action);
            }
        }

        return (Action[]) exts.toArray(new Action[exts.size()]);
    }

}
