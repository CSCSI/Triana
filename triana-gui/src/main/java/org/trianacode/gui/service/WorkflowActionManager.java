/*
 * Copyright 2004 - 2009 University of Cardiff.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.trianacode.gui.service;

import java.util.ArrayList;
import java.util.Hashtable;

import org.trianacode.taskgraph.ExecutionState;
import org.trianacode.taskgraph.TaskGraph;

/**
 * Class Description Here...
 *
 * @author Andrew Harrison
 * @version $Revision:$
 */

public class WorkflowActionManager implements WorkflowActions, WorkflowVerifierConstants {


    // A hashtable of lists of workflow verifiers, keyed by action.
    private static Hashtable vtable = new Hashtable();


    /**
     * Registers a workflow verifier using its default workflow actions. Note that workflow verifiers are consulted in
     * reverse of the order they are registered.
     */
    public static void registerWorkflowAction(WorkflowVerifier verifier) {
        registerWorkflowAction(verifier.getDefaultWorkflowActions(), verifier);
    }

    /**
     * Registers a workflow verifier, overriding its default workflow actions with the specified actions. Note that
     * workflow verifiers are consulted in reverse of the order they are registered.
     */
    public static void registerWorkflowAction(String[] actions, WorkflowVerifier verifier) {
        for (int count = 0; count < actions.length; count++) {
            if (!vtable.containsKey(actions[count])) {
                vtable.put(actions[count], new ArrayList());
            }

            ArrayList list = (ArrayList) vtable.get(actions[count]);

            if (!list.contains(verifier)) {
                list.add(0, verifier);
            }
        }
    }


    /**
     * Unregisters a workflow verifier using its default workflow actions.
     */
    public static void unregisterWorkflowAction(WorkflowVerifier verifier) {
        unregisterWorkflowAction(verifier.getDefaultWorkflowActions(), verifier);
    }

    /**
     * Unregisters a workflow verifier from the specified actions.
     */
    public static void unregisterWorkflowAction(String[] actions, WorkflowVerifier verifier) {
        ArrayList list;

        for (int count = 0; count < actions.length; count++) {
            if (vtable.containsKey(actions[count])) {
                list = (ArrayList) vtable.get(actions[count]);
                list.remove(verifier);

                if (list.isEmpty()) {
                    vtable.remove(list);
                }
            }
        }
    }


    /**
     * Verify the specified workflow for the specified action.
     *
     * @return AUTHORIZE, CANCEL, RESET or RESET_AND_RUN
     */
    public static int authorizeWorkflowAction(String action, TaskGraph taskgraph, ExecutionState state)
            throws WorkflowException {
        int result = AUTHORIZE;

        if (vtable.containsKey(action)) {
            ArrayList list = (ArrayList) vtable.get(action);
            WorkflowVerifier[] verifiers = (WorkflowVerifier[]) list.toArray(new WorkflowVerifier[list.size()]);

            for (int count = 0; (count < verifiers.length) && (result == AUTHORIZE); count++) {
                result = verifiers[count].authorizeWorkflowAction(action, taskgraph, state);
            }
        }

        return result;
    }
}
