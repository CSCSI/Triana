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

package org.trianacode.taskgraph.interceptor;

import org.trianacode.taskgraph.Node;

/**
 * Interceptors are given a crack at looking at data
 * before sending from a node, and before receiving.
 * They get called at the cable level.
 *
 * Interceptors are triggered based on parameters
 *
 * @author Andrew Harrison
 * @version $Revision:$
 * @created Jun 29, 2009: 10:32:31 PM
 * @date $Date:$ modified by $Author:$
 */
public interface Interceptor {

    public String getName();

    public boolean canMediate(Node sendNode, Node receiveNode);

    public Object interceptSend(Node sendNode, Node receiveNode, Object data);

    public Object interceptReceive(Node sendNode, Node receiveNode, Object data);

}
