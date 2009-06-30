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

package org.trianacode.taskgraph.imp;

import org.trianacode.taskgraph.*;
import org.trianacode.taskgraph.proxy.IncompatibleProxyException;
import org.trianacode.taskgraph.proxy.Proxy;
import org.trianacode.taskgraph.tool.Tool;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * An implementation of MultiTaskGraphFactory
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 * @created 14th December 2004
 * @date $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 *
 */


public class TaskGraphFactoryImp implements TaskGraphFactory {

    /**
     * A hashtable of the taskgraph factories keyed by factory type
     */
    private Hashtable factorytable = new Hashtable();

    /**
     * The task factory used to create group nodes
     */
    private TaskFactory localfactory = new TaskFactoryImp();


    /**
     * Constructs a taskgraph factory
     */
    public TaskGraphFactoryImp() {
    }


    /**
     * Registers a taskgraph factory for the specified proxy type.
     */
    public void registerTaskGraphFactory(String proxytype, TaskFactory factory) {
        if (!factorytable.containsKey(proxytype))
            factorytable.put(proxytype, new ArrayList());

        ArrayList list = (ArrayList) factorytable.get(proxytype);
        list.add(factory);
    }

    /**
     * Unregisters a taskgraph factory for a proxy type
     */
    public void unregisterTaskGraphFactory(String proxytype, TaskFactory factory) {
        if (factorytable.containsKey(proxytype)) {
            ArrayList list = (ArrayList) factorytable.get(proxytype);
            list.remove(factory);

            if (list.isEmpty())
                factorytable.remove(proxytype);
        }
    }

    /**
     * @return an array of the proxy types that have taskgraph factorys
     *         registered
     */
    public String[] getRegisteredProxyTypes() {
        return (String[]) factorytable.keySet().toArray(new String[factorytable.keySet().size()]);
    }

    /**
     * @return the registered taskgraph factory for the specified proxy
     *         (null if none registered)
     */
    public TaskFactory[] getRegisteredTaskGraphFactories(String proxytype) {
        if (factorytable.containsKey(proxytype)) {
            ArrayList list = (ArrayList) factorytable.get(proxytype);
            return (TaskFactory[]) list.toArray(new TaskFactory[list.size()]);
        } else
            return new TaskFactory[0];
    }

    /**
     * @return true if there is a registered taskgraph factory for the specified
     *         tool class
     */
    public boolean isRegisteredTaskGraphFactory(String proxytype) {
        return (factorytable.containsKey(proxytype));
    }

    /**
     * @return the registered taskgraph factory for the specified tool
     */
    private TaskFactory getRegisteredTaskGraphFactory(Tool tool) throws TaskException {
        Proxy proxy = tool.getProxy();
        TaskFactory[] factories;
        TaskFactory factory = null;

        if ((proxy != null) && (isRegisteredTaskGraphFactory(proxy.getType()))) {
            factories = getRegisteredTaskGraphFactories(proxy.getType());

            // Use only factory/factory specified in rendering hint
            if ((factories.length == 1) && (!tool.isRenderingHint(TASKGRAPH_FACTORY_RENDENRING_HINT)))
                factory = factories[0];
            else if (tool.isRenderingHint(TASKGRAPH_FACTORY_RENDENRING_HINT)) {
                RenderingHint hint = tool.getRenderingHint(TASKGRAPH_FACTORY_RENDENRING_HINT);
                String factoryname = (String) hint.getRenderingDetail(FACTORY_NAME);

                for (int count = 0; (count < factories.length) && (factory == null); count++)
                    if (factories[count].getFactoryName().equals(factoryname))
                        factory = factories[count];

                if (factory == null)
                    throw (new TaskException("Unknown TaskGraphFactory: " + factoryname));
            } else
                throw (new TaskException("Multiple TaskGraphFactory instances defined for Proxy type " + proxy.getType() + ": TaskGraphFactory Rendering Hint requried"));
        }

        if (factory == null) {
            if (tool.getProxy() == null)
                throw (new TaskException("No proxy set in " + tool.getToolName()));
            else
                throw (new TaskException("No TaskgraphFactory instance specified for Proxy type " + tool.getProxy().getType()));
        }

        return factory;
    }


    /**
     * Creates an empty taskgraph inherting properties from the specified taskgraph
     */
    public TaskGraph createTaskGraph(TaskGraph taskgraph, boolean preserveinst) throws TaskException {
        TaskGraphImp inittaskgraph = new TaskGraphImp(taskgraph, localfactory, preserveinst);
        inittaskgraph.init();

        return inittaskgraph;
    }

    /**
     * @return a new group task containing the specified tasks
     */
    public TaskGraph createGroupTask(Task[] tasks, TaskGraph parent, boolean preserveinst) throws TaskException {
        TaskGraphImp taskgraph = new TaskGraphImp(tasks, localfactory, preserveinst);
        taskgraph.setParent(parent);
        taskgraph.init();

        return taskgraph;
    }

    /**
     * @return a new task of type tool, optionally preserving the original
     *         instance id in the new task.
     */
    public Task createTask(Tool tool, TaskGraph parent, boolean preserveinst) throws TaskException {
        try {
            if ((tool.getProxy() == null) && (tool instanceof TaskGraph)) {
                return (Task) TaskGraphUtils.cloneTaskGraph((TaskGraph) tool, parent, preserveinst);
            } else {
                TaskFactory factory = getRegisteredTaskGraphFactory(tool);
                return factory.createTask(tool, parent, preserveinst);
            }

        } catch (TaskGraphException except) {
            throw (new TaskException(except.getMessage(), except));
        }

    }

    /**
     * @return a new cable connecting the specified nodes
     */
    public Cable createCable(Node sendnode, Node recnode) throws CableException {
        try {
            TaskFactory sendfactory = getRegisteredTaskGraphFactory(sendnode.getTopLevelTask());
            TaskFactory recfactory = getRegisteredTaskGraphFactory(recnode.getTopLevelTask());

            if (sendfactory == recfactory)
                return recfactory.createCable(sendnode, recnode);
            else {
                IncompatibleTypeException incompatexcept = null;

                try {
                    return sendfactory.createCable(sendnode, recnode);
                } catch (IncompatibleTypeException except) {
                    incompatexcept = except;
                } catch (CableException except) {
                }

                try {
                    return recfactory.createCable(sendnode, recnode);
                } catch (IncompatibleTypeException except) {
                    incompatexcept = except;
                } catch (CableException except) {
                }

                Tool sendtool = sendnode.getTopLevelTask();
                Tool rectool = recnode.getTopLevelTask();

                if (incompatexcept != null)
                    throw (incompatexcept);
                else
                    throw (new IncompatibleProxyException("Cannot connect " + sendtool.getToolName() + " to " + rectool.getToolName() + ": Incompatible proxy types"));
            }
        } catch (TaskException except) {
            throw (new CableException(except.getMessage()));
        }
    }

}
