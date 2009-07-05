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

package org.trianacode.gui.action.taskgraph;

import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.gui.service.WorkflowActions;
import org.trianacode.gui.service.WorkflowException;
import org.trianacode.gui.service.WorkflowVerifier;
import org.trianacode.taskgraph.ExecutionState;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.TaskGraphUtils;
import org.trianacode.util.Env;

import javax.swing.*;

/**
 * Checks a pre-run workflow to see whether it has any ProtoServices, and if
 * so prompts the user to distribute these services or cancel the run.
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 * @created 29th July 2004
 * <<<<<<< TrianaWorkflowVerifier.java
 * @date $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 * =======
 * @date $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 * >>>>>>> 1.2.2.1
 */

public class TrianaWorkflowVerifier implements WorkflowVerifier {

    /**
     * @return the default actions handled by this verifier
     */
    public String[] getDefaultWorkflowActions() {
        return new String[]{WorkflowActions.RUN_ACTION};
    }

    /**
     * Verify the specified workflow for the specified action.
     *
     * @return true if the action should preceed.
     */
    public int authorizeWorkflowAction(String action, TaskGraph taskgraph, ExecutionState state) throws WorkflowException {
        Task[] tasks = TaskGraphUtils.getAllTasksRecursive(taskgraph, true);
        boolean error = (state == ExecutionState.ERROR);
        boolean suspend = false;

        for (int count = 0; (count < tasks.length) && (!error) && (!suspend); count++) {
            Task task = tasks[count];

            if (task.getExecutionState() == ExecutionState.SUSPENDED)
                suspend = true;
        }

        if (error) {
            String name = taskgraph.getToolName();
            if (name.equals(""))
                name = "Workflow";

            String[] options = new String[]{"Reset and ReRun", "Cancel"};

            int result = JOptionPane.showOptionDialog(GUIEnv.getApplicationFrame(), name + " contains error state tasks!", Env.getString("Run") + " Error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, GUIEnv.getTrianaIcon(), options, options[0]);

            if (result == 0)
                return RESET_AND_RUN;
            else
                return CANCEL;
        } else if (suspend) {
            String name = taskgraph.getToolName();
            if (name.equals(""))
                name = "Workflow";

            JOptionPane.showMessageDialog(GUIEnv.getApplicationFrame(), "Cannot run " + name + " as it contains suspended tasks! Please try again later.", Env.getString("Run") + " Error", JOptionPane.ERROR_MESSAGE, GUIEnv.getTrianaIcon());
            return CANCEL;
        } else
            return AUTHORIZE;
    }

}
