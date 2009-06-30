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

package org.trianacode.taskgraph;

import org.trianacode.taskgraph.event.TaskGraphCreatedEvent;
import org.trianacode.taskgraph.event.TaskGraphManagerListener;
import org.trianacode.taskgraph.imp.TaskGraphImp;
import org.trianacode.taskgraph.proxy.DefaultFactoryInit;
import org.trianacode.taskgraph.service.TrianaServer;
import org.trianacode.taskgraph.tool.Tool;
import org.trianacode.taskgraph.tool.ToolTable;

import java.util.*;
import java.util.logging.Logger;


/**
 * A abstract TaskGraphFactory implementation for creating taskgraphs that contain
 * a mixture of proxies
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 * @created 5th May 2004
 * @date $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 *
 */

public class TaskGraphManager {


    public static final String DEFAULT_FACTORY_TYPE = "Default";
    public static final String NON_RUNNABLE_FACTORY_TYPE = "NonRunnable";
    public static final String TOOL_DEF_FACTORY_TYPE = "ToolDef";
    static Logger logger = Logger.getLogger(TaskGraphManager.class.getName());


    /**
     * The tool table
     */
    private static ToolTable tooltable;


    /**
     * A hashtable of the taskgraph factories keyed by type
     */
    private static Hashtable factorytable = new Hashtable();

    /**
     * A hashtable of the factory type for each root taskgraph/tool
     */
    private static Hashtable typetable = new Hashtable();

    /**
     * A hashtable of the triana servers keyed by taskgraph
     */
    private static Hashtable servertable = new Hashtable();

    /**
     * A list of TaskGraphManagerListeners
     */
    private final static List listeners = Collections.synchronizedList(new ArrayList());


    /**
     * Initializes the taskgraph factories
     */
    public static void initTaskGraphManager() {
        DefaultFactoryInit.initTaskGraphManager();

        /*Object[] plugins = PluginLoader.getInstance().getInstances(PluginInit.class);

        for (int count = 0; count < plugins.length; count++)
            ((PluginInit) plugins[count]).initTaskGraphManager();*/
    }


    /**
     * Initializes the tool table. This method must be called only once, when
     * the application is first initialized.
     */
    public static void initToolTable(ToolTable tools) {
        if (tooltable != null)
            throw (new RuntimeException("Error: Tool Table already initialized!"));

        tooltable = tools;
    }

    /**
     * @return the tool table
     */
    public static ToolTable getToolTable() {
        return tooltable;
    }


    /**
     * Adds a TaskgraphManagerListener
     */
    public void addTaskgraphManagerListener(TaskGraphManagerListener listener) {
        if (!listeners.contains(listener))
            listeners.add(listener);
    }

    /**
     * Adds a TaskgraphManagerListener
     */
    public void removeTaskgraphManagerListener(TaskGraphManagerListener listener) {
        listeners.remove(listener);
    }


    /**
     * Registers a taskgraph factory for the specifed factory type
     */
    public static void registerTaskGraphFactory(String type, TaskGraphFactory factory) {
        factorytable.put(type, factory);
    }

    /**
     * Registers the taskgraph factory for the specifed factory type
     */
    public static void unregisterTaskGraphFactory(String type) {
        factorytable.remove(type);
    }

    /**
     * @return the taskgraph factory for the specified type
     */
    public static TaskGraphFactory getTaskGraphFactory(String type) {
        if (factorytable.containsKey(type))
            return (TaskGraphFactory) factorytable.get(type);
        else
            return null;
    }

    /**
     * @return true if there is taskgraph factory for the specified type
     */
    public static boolean isTaskGraphFactory(String type) {
        return factorytable.containsKey(type);
    }


    /**
     * @return the TrianaServer for the specified TaskGraph
     */
    public static TrianaServer getTrianaServer(TaskGraph taskgraph) {
        Task parent = (Task) taskgraph;

        while ((parent != null) && (!servertable.containsKey(parent)))
            parent = parent.getParent();

        if (servertable.containsKey(parent))
            return (TrianaServer) servertable.get(parent);
        else
            return null;
    }

    /**
     * Sets the TrianaServer for the specified TaskGraph
     */
    public static void setTrianaServer(TaskGraph taskgraph, TrianaServer server) {
        servertable.put(taskgraph, server);
    }


    /**
     * Initializes the factory type for the specified tool/taskgraph
     */
    public static void registerFactoryType(Tool tool, String type) {
        typetable.put(tool, type);
    }

    /**
     * @return the factory type for the specfied tool. If not type is initialized
     *         then the type for its parent (or parent's parent etc.) is returned.
     *         (null if not type initialized)
     */
    public static String getTaskGraphFactoryType(Tool tool) {
        if (typetable.containsKey(tool))
            return (String) typetable.get(tool);
        else if (tool instanceof Task) {
            Task parent = (Task) tool;

            while ((parent != null) && (!typetable.containsKey(parent))) {
                if (parent.getParent() != null)
                    parent = parent.getParent();
                else
                    parent = null;
            }

            if ((parent != null) && (typetable.containsKey(parent)))
                return (String) typetable.get(parent);
        }

        return null;
    }

    /**
     * @return the taskgraph factory for the specified tool.
     */
    public static TaskGraphFactory getTaskGraphFactory(Tool tool) {
        String type = getTaskGraphFactoryType(tool);

        if (type == null)
            throw (new RuntimeException("TaskGraphFactory type not registered for " + tool.getToolName() + " (or its parents)"));

        return (TaskGraphFactory) factorytable.get(type);
    }


    /**
     * @return a new root task/taskgraph instantiated with the specified factory
     *         type
     */
    public static Task createTask(Tool tool, String factorytype, boolean preserveinst) throws TaskException {
        try {
            if (!isTaskGraphFactory(factorytype))
                throw (new RuntimeException("TaskGraphFactory not registered for " + factorytype + " type"));

            Task task;

            if (tool instanceof TaskGraph) {
                if (preserveinst)
                    task = (Task) TaskGraphUtils.cloneTaskGraph((TaskGraph) tool, factorytype);
                else
                    task = (Task) TaskGraphUtils.copyTaskGraph((TaskGraph) tool, factorytype);
            } else {
                task = getTaskGraphFactory(factorytype).createTask(tool, null, preserveinst);
            }

            return task;
        } catch (TaskGraphException except) {
            throw (new TaskException(except));
        }
    }

    /**
     * Creates a empty root taskgraph using the non-runnable factory
     */
    public static TaskGraph createTaskGraph() throws TaskException {
        return createTaskGraph(new TaskGraphImp(), NON_RUNNABLE_FACTORY_TYPE, false);
    }

    /**
     * Creates a empty root taskgraph
     */
    public static TaskGraph createTaskGraph(String factorytype) throws TaskException {
        return createTaskGraph(new TaskGraphImp(), factorytype, false);
    }

    /**
     * Creates a root taskgraph based on the specified taskgraph
     */
    public static TaskGraph createTaskGraph(TaskGraph taskgraph, String factorytype, boolean preserveinst) throws TaskException {
        if (!isTaskGraphFactory(factorytype))
            throw (new RuntimeException("TaskGraphFactory not registered for " + factorytype + " type"));

        TaskGraph inittaskgraph = getTaskGraphFactory(factorytype).createTaskGraph(taskgraph, preserveinst);
        registerFactoryType(inittaskgraph, factorytype);

        notifyTaskGraphCreated(taskgraph, factorytype);

        return inittaskgraph;
    }

    private static void notifyTaskGraphCreated(TaskGraph taskgraph, String factorytype) {
        TaskGraphCreatedEvent event = new TaskGraphCreatedEvent(taskgraph, factorytype);

        synchronized(listeners) {
            Iterator it = listeners.iterator();

            while(it.hasNext())
                ((TaskGraphManagerListener) it.next()).taskgraphCreated(event);
        }
    }

}
