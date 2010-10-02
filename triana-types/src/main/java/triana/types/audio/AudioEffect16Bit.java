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

package triana.types.audio;

/**
 * This abstract class is a modification of the AudioEffect.java file created
 * by Dr Ian Taylor of Cardiff University. This class is a base class for
 * creating audio effects that work on chunks of data.This class is intended
 * to make it convenient for operating on a chunk by chunk basis, and provides
 * a forward memory buffer that can be initialised and use with a time delay
 * based algorithm.
 * <p/>
 * When new data arrives it is inserted into an array with the previous and
 * forward data memory prepended and appended to it. By default the previous
 * locations are filled with the previous data (i.e. the current data will be
 * copied into it for the next iteration) and the forward data locations can
 * either be added to the next data processed or multiplied to them. The default
 * mode is to add the values to the next data set that arrives.
 * <p/>
 * The AudioEffect class was modified (with the authors permission) to remove a
 * redundant 'backbuffer' array, and to work using shorts (for 16bit data) instead
 * of doubles.
 *
 * @author Dr Ian Taylor
 * @author Eddie Al-Shakarchi
 * @version $Revision: 4048 $
 * @created 19 Mar 2004 (Revised)
 * @date $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */


public abstract class AudioEffect16Bit {
    /**
     * An identifier for adding the forward memory locations to the next data received by an AudioEffect. Use
     * setForwardMathOperation to set the forward math mode.
     *
     * @see #setForwardMathOperation(int)
     */
    public final static int ADD = 0;

    /**
     * An identifier for multiplying the forward memory locations to the next data received by an AudioEffect. Use
     * setForwardMathOperation to set the forward math mode.
     *
     * @see #setForwardMathOperation(int)
     */
    public final static int MULTIPLY = 1;

    /**
     * An identifier for ignoring the forward memory locations when receiving future inputs. Use setForwardMathOperation
     * to set the forward math mode.
     *
     * @see #setForwardMathOperation(int)
     */
    public final static int DO_NOTHING = 2;

    // add by default.
    private int mathMode = ADD;

    private int currentSize = -1;

    private short[] forwardMemory = null;
    private short[] currentData = null;

    private short[] givenData = null; // resuse the data that has been given for output
    private int givenDataSize = -1;
    private int startPos;

    /**
     * Creates an audio effect with a particular forward memory size.  Enter 0 for no memory.
     */

    public AudioEffect16Bit(int forwardMemSize) {
        forwardMemory = new short[forwardMemSize];
    }

    public void initialiseForward(short val) {
        for (int i = 0; i < forwardMemory.length; ++i) {
            forwardMemory[i] = val;
        }
    }

    /**
     * Initializes the forward values to the values specified. The array given must be the same size as the size of the
     * previous memory array otherwise the memory will not be initialized and an exception will be thrown
     */
    public void initialiseForward(short[] vals) throws IncompatibleSizeException {
        if (vals.length != forwardMemory.length) {
            throw new IncompatibleSizeException("Given vals array incompatible size with forwardMemory array");
        }

        for (int i = 0; i < forwardMemory.length; ++i) {
            forwardMemory[i] = vals[i];
        }
    }

    /**
     * An function setting the behaviour of how AudioEffect applies the forward memory locations to the next data
     * received by an AudioEffect. The default mode is ADD i.e. add them to the next data chunk but other can be
     * specified e.g. MULTIPLY and DO_NOTHING
     */
    public void setForwardMathOperation(int mode) {
        this.mathMode = mode;
    }

    /**
     * Gets the starting position of the current memory chunk with the previous and forward memory prepended and
     * appended to it
     */
    public int getStart() {
        return startPos;
    }

    /**
     * Gets data array
     */
    public short[] getData() {
        return currentData;
    }

    /**
     * This function inserts the given data into an array which comprises of the given data and the forward data
     * respectively. To get a reference to the data use getData() and to get the starting point of the current data use
     * getStart().
     */
    public void preProcess(short data[]) {
        // take a copy.
        givenDataSize = data.length;
        givenData = data;


        int newSize = data.length + forwardMemory.length;
        if (currentSize != newSize) {
            currentData = new short[newSize];
            currentSize = newSize;
        }

        // apply math operation to current samples.

        if (forwardMemory.length > 0) {
            if (mathMode == ADD) { // ONLY process the forwardMemory samples i.e. could be < data
                for (int i = 0; i < forwardMemory.length; ++i) {
                    data[i] += forwardMemory[i];
                }
            } else if (mathMode == MULTIPLY) {
                for (int i = 0; i < forwardMemory.length; ++i) {
                    data[i] *= forwardMemory[i];
                }
            }
        }

        // copy current memory
        try {
            startPos = (int) currentData[0];
            System.arraycopy(data, 0, currentData, 0, data.length);
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }
}
