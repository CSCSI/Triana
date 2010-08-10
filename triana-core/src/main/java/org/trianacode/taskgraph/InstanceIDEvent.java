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

import java.util.EventObject;

import org.trianacode.taskgraph.tool.Tool;

/**
 * The event generated when an instance id is registered/unregistered with a tool.
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 */

public class InstanceIDEvent extends EventObject {

    public static final int ID_REGISTERED = 0;
    public static final int ID_UNREGISTERED = 1;

    private int eventid;
    private String instanceid;
    private Tool tool;
    private int idcount;

    /**
     * @param source     the source for this event
     * @param eventid    the event id (ID_REGISTERED/ID_UNREGISTERED)
     * @param instanceid the instance id
     * @param tool       the tool that the id is (un)registered for.
     * @param idcount    the number of tasks existing with the instance id
     */
    public InstanceIDEvent(Object source, int eventid, String instanceid, Tool tool, int idcount) {
        super(source);
        this.eventid = eventid;
        this.instanceid = instanceid;
        this.tool = tool;
        this.idcount = idcount;
    }


    /**
     * @return the event id (ID_REGISTERED/ID_UNREGISTERED)
     */
    public int getEventID() {
        return eventid;
    }

    /**
     * @return the instance id
     */
    public String getInstanceID() {
        return instanceid;
    }

    /**
     * @return the tool that the id is (un)registered for
     */
    public Tool getTool() {
        return tool;
    }

    /**
     * @return the number of tasks existing with the instance id
     */
    public int getIDCount() {
        return idcount;
    }

}
