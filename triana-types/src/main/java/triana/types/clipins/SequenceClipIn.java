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

/**
 * A index clip-in specifies the position of an object in a index, and
 * optionally either the total number of objects in that index and when
 * this is the last item in the index.
 *
 * As with arrays in Java, the indexes of a sequence clipins start a 0 and
 * finish at (length - 1). If the length is not specified then it is set to -1.
 *
 * @author      Ian Wang
 * @created     7th July 2004
 * @version     $Revision: 4048 $
 * @date        $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 * @todo
 */

public class SequenceClipIn extends Object {

    public static final String SEQUENCE_CLIPIN_TAG = "Sequence";

    private String groupid;
    private SequenceClipIn subclipin;

    private int index;
    private int length = -1;
    private boolean last = false;
    private String classname;


    /**
     * Constructs a SequenceClipIn with the specified index number
     */
    public SequenceClipIn(String groupid, int index) {
        this.groupid = groupid;
        this.index = index;
    }

    /**
     * Constructs a SequenceClipIn with the specified index number and a
     * maximum index number.
     */
    public SequenceClipIn(String groupid, int index, int length) {
        this.groupid = groupid;
        this.index = index;
        this.length = length;
        this.last = (index == length - 1);
    }

    /**
     * Constructs a SequenceClipIn with the specified index number and a
     * flag indicating this is the last in the index.
     */
    public SequenceClipIn(String groupid, int index, boolean last) {
        this.groupid = groupid;
        this.index = index;
        this.last = last;
    }

    /**
     * @return the unique group id identifying which sequence this clip-in
     * belongs to
     */
    public String getGroupID() {
        return groupid;
    }


    /**
     * @return the current index integer (0 <= index < lenght)
     */
    public int getIndex() {
        return index;
    }

    /**
     * @return the length of the sequence (or -1 if unknown)
     */
    public int getLength() {
        return length;
    }

    /**
     * @return true if this is the last in the sequence
     */
    public boolean isLastInSequence() {
        return last;
    }


    /**
     * @return the java type for components in this sequence (null if unknown)
     */
    public String getComponentType() {
        return classname;
    }

    /**
     * Sets the java type for components in this sequence
     */
    public void setComponentType(String classname) {
        this.classname = classname;
    }


    /**
     * @return the sequence clipin for the subsequence (null if none)
     */
    public SequenceClipIn getSubSequenceClipIn() {
        return subclipin;
    }

    /**
     * Sets the sequence clipin for a subsequence
     */
    public void setSubSequenceClipIn(SequenceClipIn subclipin) {
        this.subclipin = subclipin;
    }

}









