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

package org.trianacode.interceptors;

import org.trianacode.taskgraph.interceptor.Interceptor;
import org.trianacode.taskgraph.Node;


/**
 * Class Description Here...
 *
 * @author Andrew Harrison
 * @version $Revision:$
 * @created Jun 29, 2009: 11:18:12 PM
 * @date $Date:$ modified by $Author:$
 */

public class LoggingInterceptor implements Interceptor {
    public String getName() {
        return getClass().getName();
    }

    public boolean canMediate(Node sendNode, Node receiveNode) {
        return false;
    }

    public Object interceptSend(Node sendNode, Node receiveNode, Object data) {
        System.out.println("LoggingInterceptor.interceptSend: INTERCEPTED:" + data);
        return data;
    }

    public Object interceptReceive(Node sendNode, Node receiveNode, Object data) {
        System.out.println("LoggingInterceptor.interceptReceive INTERCEPTED:" + data);
        return data;
    }
}
