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

import org.trianacode.taskgraph.ExecutionState;
import org.trianacode.taskgraph.TaskGraph;

/**
 * Class Description Here...
 *
 * @author Andrew Harrison
 * @version $Revision:$
 * @created Jun 25, 2009: 1:31:19 PM
 * @date $Date:$ modified by $Author:$
 */
public interface WorkflowVerifier extends WorkflowVerifierConstants {

    /**
     * @return the default actions handled by this verifier
     */
    public String[] getDefaultWorkflowActions();


    /**
     * Verify the specified workflow for the specified action.
     *
     * @return AUTHORIZE, CANCEL, RESET or RESET_AND_RUN
     */
    public int authorizeWorkflowAction(String action, TaskGraph taskgraph, ExecutionState state) throws WorkflowException;

}