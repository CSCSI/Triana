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
package triana.types.clipins;

import org.trianacode.taskgraph.clipin.AttachInfo;
import org.trianacode.taskgraph.clipin.ClipIn;


/**
 * MessageClipIn is an Object containing three Strings: a message, the name
 * of the sender of the message, and the name of the intended recipient of
 * the message. It is designed to allow units to pass information along a
 * processing network from one to another. The sender and recipient are
 * expected to be units, and the Strings should identify them. These can
 * be class names or other names known to units using this ClipIn. All
 * of the Strings can be altered by any Units that receive the ClipIn using
 * methods provided here. If the recipient field is left as the empty
 * String, then any receiving Unit may assume that the message is intended
 * for it.
 *
 * @author      Bernard Schutz
 * @created     31 December 2002
 * @version     $Revision: 4048 $
 * @date        $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public class MessageClipIn extends Object implements ClipIn {
    private String message = "";
    private String sender = "";
    private String recipient = "";


    /**
     * Constructs an empty MessageClipIn.
     */
    public MessageClipIn() {
    }

    /**
     * Constructs a MessageClipIn with no recipient.
     *
     * @param text The message being sent
     * @param from The sender of the message
     */
    public MessageClipIn(String text, String from) {
        sender = from;
        message = text;
    }

    /**
     * Constructs a MessageClipIn with a recipient.
     *
     * @param text The message being sent
     * @param from The sender of the message
     * @param to The intended recipient of the message
     */
    public MessageClipIn(String text, String from, String to) {
        sender = from;
        recipient = to;
        message = text;
    }

    /*
     * Implement the methods of the ClipIn interface
     */

    /**
     * This method is called before the clip-in enters a task's
     * clip-in bucket. This occurs when either the data it is attached
     * to is input by the task, or when the unit directly adds the
     * clip-in to its bucket.
     *
     * @param info info about the task the clip-in is being attached to
     */
    public void initializeAttach(AttachInfo info) {
    }

    /**
     * This method is called when the clip-in is removed from a
     * task's clip-in bucket. This occurs when either the data it is
     * attached to is output by the task, or when the unit directly
     * remove the clip-in from its bucket.
     *
     * @param info info about the task the clip-in is being removed from
     */

    public void finalizeAttach(AttachInfo info) {
    }

    /**
     * Clones the ClipIn to an identical one. This is a copy by value,
     * not by reference. This method must be implemented for each class
     * in a way that depends on the contents of the ClipIn.
     *
     * @return a copy by value of the current ClipIn
     */
    public Object clone() {
        return new MessageClipIn(message, sender, recipient);
    }


    /*
     * Implement methods specific to MessageClipIn
     */

    /**
     * Sets the message.
     *
     * @param text The new value of the message
     */
    public void setMessage(String text) {
        message = text;
    }

    /**
     * Sets the sender.
     *
     * @param from The new value of the sender
     */
    public void setSender(String from) {
        sender = from;
    }

    /**
     * Sets the recipient.
     *
     * @param to The new value of the recipient
     */
    public void setRecipient(String to) {
        recipient = to;
    }

    /**
     * Returns the current message.
     *
     * @return String The current message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the current sender.
     *
     * @return String The current sender
     */
    public String getSender() {
        return sender;
    }

    /**
     * Returns the current recipient.
     *
     * @return String The current recipient
     */
    public String getRecipient() {
        return recipient;
    }


}









