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

import org.trianacode.taskgraph.imp.InstanceIDFactoryImp;
import org.trianacode.taskgraph.tool.Tool;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * A static manager class that keeps track of the current instance ids.
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 * @created 2nd August 2004
 * @date $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 *
 */

public class InstanceIDManager {

    /**
     * The InstanceIDFactory used to generate new instance ids
     */
    private static InstanceIDFactory idfactory = new InstanceIDFactoryImp();

    /**
     * An list of the instance id listeners
     */
    private static ArrayList listeners = new ArrayList();

    /**
     * A hashtable of instance ids keyed by tool/task
     */
    private static Hashtable ids = new Hashtable();


    /**
     * Sets the instance id factory
     */
    public static void setInstanceIDFactory(InstanceIDFactory factory) {
        idfactory = factory;
    }

    /**
     * @return the instance id factory
     */
    public static InstanceIDFactory getInstanceIDFactory() {
        return idfactory;
    }


    /**
     * Adds an instance id listener
     */
    public static void addInstanceIDListener(InstanceIDListener listener) {
        if (!listeners.contains(listener))
            listeners.add(listener);
    }

    /**
     * Removes an instance id listener
     */
    public static void removeInstanceIDListener(InstanceIDListener listener) {
        listeners.remove(listener);
    }


    /**
     * @return true if an instance id is registered for the specified tool
     */
    public static boolean isRegisteredID(Tool tool) {
        return ids.containsKey(tool);
    }

    /**
     * @return the instance id for the specified tool (or null if not registered)
     */
    public static String getRegisteredID(Tool tool) {
        if (ids.containsKey(tool))
            return (String) ids.get(tool);
        else
            return null;
    }


    /**
     * Registers the instance id for a tool
     */
    public static void registerID(Tool tool, String id) {
        if (!ids.containsKey(tool)) {
            ids.put(tool, id);
            notifyIDRegistered(id, tool, getIDCount(id));
        } else
            throw (new RuntimeException("Attempt to register multiple ids for a single tool"));
    }

    /**
     * Registers an instance ID for the specified task. This is ID is either
     * new or preserved from the original tool as specified by the preserveinst
     * flag
     *
     * @param task         the task an instance id is being registered for
     * @param tool         the tool used to instantiate the task
     * @param preserveinst a flag indicating whether the instance id is preserved
     *                     from the original tool.
     * @return the instance ID for the task
     */
    public static String registerID(Task task, Tool tool, boolean preserveinst) {
        String id;

        if (preserveinst && (isRegisteredID(tool)))
            id = getRegisteredID(tool);
        else
            id = idfactory.generateID(task);

        InstanceIDManager.registerID(task, id);
        return id;
    }

    /**
     * Unregisters the instance id for a task
     */
    public static void unregisterID(Task task) {
        if (ids.containsKey(task)) {
            String id = task.getInstanceID();

            ids.remove(task);
            notifyIDUnregistered(id, task, getIDCount(id));
        }
    }

    /**
     * @return a count of the occurances of the instance id in the idtable
     */
    private static int getIDCount(String id) {
        Enumeration enumeration = ids.elements();
        int count = 0;

        while (enumeration.hasMoreElements()) {
            if (id.equals(enumeration.nextElement()))
                count++;
        }

        return count;
    }


    private static void notifyIDRegistered(String id, Tool tool, int idcount) {
        InstanceIDEvent event = new InstanceIDEvent(tool, InstanceIDEvent.ID_REGISTERED, id, tool, idcount);
        InstanceIDListener[] copy = (InstanceIDListener[]) listeners.toArray(new InstanceIDListener[listeners.size()]);

        for (int count = 0; count < copy.length; count++)
            copy[count].instanceIDRegistered(event);
    }

    private static void notifyIDUnregistered(String id, Tool tool, int idcount) {
        InstanceIDEvent event = new InstanceIDEvent(tool, InstanceIDEvent.ID_UNREGISTERED, id, tool, idcount);
        InstanceIDListener[] copy = (InstanceIDListener[]) listeners.toArray(new InstanceIDListener[listeners.size()]);

        for (int count = 0; count < copy.length; count++)
            copy[count].instanceIDUnregistered(event);
    }

}
