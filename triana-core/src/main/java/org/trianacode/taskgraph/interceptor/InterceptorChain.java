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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An interceptor intercepts data while it's on the cable.
 *
 * @author Andrew Harrison
 * @version $Revision:$
 */

public class InterceptorChain {

    private static Map<String, Interceptor> interceptors = new ConcurrentHashMap<String, Interceptor>();

    public static void register(Interceptor interceptor) {
        interceptors.put(interceptor.getName(), interceptor);
    }

    public static void deregister(Interceptor interceptor) {
        interceptors.remove(interceptor.getName());
    }

    public static Interceptor getInterceptor(String name) {
        return interceptors.get(name);
    }

    public static boolean canConnect(Node sendNode, Node receiveNode) {
        for (String s : interceptors.keySet()) {
            Interceptor interceptor = interceptors.get(s);
            boolean b = interceptor.canMediate(sendNode, receiveNode);
            if (b) {
                return true;
            }
        }
        return false;
    }

    public static Object interceptSend(Node sendNode, Node receiveNode, Object data) {
        for (String s : interceptors.keySet()) {
            Interceptor interceptor = interceptors.get(s);
            data = interceptor.interceptSend(sendNode, receiveNode, data);
        }

        return data;
    }

    public static Object interceptReceive(Node sendNode, Node receiveNode, Object data) {
        for (String s : interceptors.keySet()) {
            Interceptor interceptor = interceptors.get(s);
            data = interceptor.interceptReceive(sendNode, receiveNode, data);
        }
        return data;
    }
}
