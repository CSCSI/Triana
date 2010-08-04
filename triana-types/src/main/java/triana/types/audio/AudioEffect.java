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
 * AudioEffect : a base class for creating audio effect that work on chunks of data.  The idea behind this class is to
 * make it convenient for operating on a chunk by chunk basis.  Most algorithms operate on a continuous stream of
 * samples or on a specific sized amount of data where no memory is required for future use.  This class provides a
 * backwards and forward memory that you can initialise and use with your algorithm.  For example a reverb or echo
 * effect may require future memory for storing the echos or reverberations within that may be added or multiplied with
 * the next data to be processed.
 * <p/>
 * You can set the size of the previous and next memory chunks. Care must be taken that you don't go out of the bounds
 * of the maximum forward and backward memory.
 * <p/>
 * When new data arrives it is inserted into an array with the previous and forward data memory prepended and appended
 * to it. By default the previous locations are filled with the previous data (i.e. the current data will be copied into
 * it for the next iteration) and the forward data locations can either be added to the next data processed or
 * multiplied to them. The default mode is to add the values to the next data set that arrives. NOTE that you must take
 * care to set the initial values so that they don't effect the first data set i.e. If you're adding then initialse
 * Forward values to 0 or Multiplying set them to 1.
 * <p/>
 * Process the next data chunk by calling preProcess and then get access to the the data array and the starting point by
 * calling getData() and getStart(), respectively. Call postProcess to get the new data in the appropriate dimension.
 * <p/>
 * NOTE : This class currently assumes that the previous and forward memory sizes are the the same size or smaller than
 * the current chunk of data. FUTURE extensions could involve allowing extended lengths of the previous and forward
 * memory sizes. I think that this covers most applications however except things like Large Hall Reverb which can
 * produce an effect that may span several future data sets.
 *
 * @author Ian Taylor
 * @version $Revision: 4048 $
 */
public abstract class AudioEffect {
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

    private double[] backwardMemory = null;
    private double[] forwardMemory = null;
    private double[] currentData = null;

    private double[] givenData = null; // resuse the data that has been given for output
    private int givenDataSize = -1;
    private int startPos;

    /**
     * Creates an audio effect with a particular backward and forward memory size.  Enter 0 for no memory in either
     * direction.
     */
    public AudioEffect(int backwardMemSize, int forwardMemSize) {
        backwardMemory = new double[backwardMemSize];
        forwardMemory = new double[forwardMemSize];
    }

    /**
     * Initializes the previous memory values to the value specified.
     */
    public void initialisePrevious(double val) {
        for (int i = 0; i < backwardMemory.length; ++i) {
            backwardMemory[i] = val;
        }
    }

    /**
     * Initializes the previous memory values to the values specified. The array given must be the same size as the size
     * of the previous memory array otherwise the memory will not be initialized and an exception will be thrown
     */
    public void initialisePrevious(double vals[]) throws IncompatibleSizeException {
        if (vals.length != backwardMemory.length) {
            throw new IncompatibleSizeException("Given vals array incompatible size with backwardMemory array");
        }

        for (int i = 0; i < backwardMemory.length; ++i) {
            backwardMemory[i] = vals[i];
        }
    }

    /**
     * Initializes the forward memory values to the value specified.
     */
    public void initialiseForward(double val) {
        for (int i = 0; i < forwardMemory.length; ++i) {
            forwardMemory[i] = val;
        }
    }

    /**
     * Initializes the forward values to the values specified. The array given must be the same size as the size of the
     * previous memory array otherwise the memory will not be initialized and an exception will be thrown
     */
    public void initialiseForward(double vals[]) throws IncompatibleSizeException {
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
    public double[] getData() {
        return currentData;
    }

    /**
     * This function inserts the given data into an array which comprises of the previous data, the given data and the
     * forward data respectively. To get a reference to the data use getData() and to get the starting point of the
     * current data use getStart().
     */
    public void preProcess(double data[]) {
        // take a copy.
        givenDataSize = data.length;
        givenData = data;

        int newSize = backwardMemory.length + data.length + forwardMemory.length;
        if (currentSize != newSize) {
            currentData = new double[newSize];
            currentSize = newSize;
        }

        // copy backward memory

        if (backwardMemory.length > 0) {
            System.arraycopy(backwardMemory, 0, currentData, 0, backwardMemory.length);
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

        startPos = backwardMemory.length;

        System.arraycopy(data, 0, currentData, startPos, data.length);
    }

    /**
     * This function copies the current data to the forward memory for use next time and inserts the processed values
     * back into the given array.  The given array can be set to null and then the data is copied back into the ORGINAL
     * array supplied. Otherwise, you can provide a non null array and the data is copied into this. The given array
     * must be the same size as the one supplied to preProcess.
     */
    public void postProcess(double data[]) throws IncompatibleSizeException {
        if (data == null) {
            data = givenData;
        } else if (data.length != givenDataSize) {
            throw new IncompatibleSizeException(
                    "postProcess : Given data array incompatible with array input to preProcess");
        }

        System.arraycopy(currentData, startPos, data, 0, data.length);
    }
}
