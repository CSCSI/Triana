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

/**
 * Type safe execution state class
 *
 * @author Matthew Shields
 * @version $Revision: 4048 $
 */
public final class ExecutionState {

    private String state;

    public static final ExecutionState NOT_INITIALIZED = new ExecutionState("not initialized");
    public static final ExecutionState NOT_EXECUTABLE = new ExecutionState("not executable");
    public static final ExecutionState SCHEDULED = new ExecutionState("scheduled");
    public static final ExecutionState RUNNING = new ExecutionState("running");
    public static final ExecutionState PAUSED = new ExecutionState("paused");
    public static final ExecutionState COMPLETE = new ExecutionState("complete");
    public static final ExecutionState RESETING = new ExecutionState("reseting");
    public static final ExecutionState RESET = new ExecutionState("reset");
    public static final ExecutionState ERROR = new ExecutionState("error");
    public static final ExecutionState SUSPENDED = new ExecutionState("suspended");
    public static final ExecutionState UNKNOWN = new ExecutionState("unknown");
    public static final ExecutionState LOCK = new ExecutionState("lock");


    private ExecutionState(String state) {
        this.state = state;
    }


    public String getState() {
        return state;
    }

    /**
     * @return a string representation of the object.
     */
    public String toString() {
        return state;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExecutionState)) {
            return false;
        }

        final ExecutionState executionState = (ExecutionState) o;

        if (state != null ? !state.equals(executionState.state) : executionState.state != null) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return (state != null ? state.hashCode() : 0);
    }

}
