/*
 * The University of Wales, Cardiff Triana Project Software License (Based
 * on the Apache Software License Version 1.1)
 *
 * Copyright (c) 2003 University of Wales, Cardiff. All rights reserved.
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
 */
package common.input;

import java.util.Iterator;
import java.util.Vector;

import org.trianacode.taskgraph.Node;
import org.trianacode.taskgraph.NodeException;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.Unit;
import org.trianacode.taskgraph.event.ParameterUpdateEvent;
import org.trianacode.taskgraph.event.TaskDisposedEvent;
import org.trianacode.taskgraph.event.TaskListener;
import org.trianacode.taskgraph.event.TaskNodeEvent;
import org.trianacode.taskgraph.event.TaskPropertyEvent;
import triana.types.TrianaType;


/**
 * An input buffer that stores a sequence of TrianaTypes, it has a control GUI with play/fwd/rev/stop buttons and it
 * outputs the stored datatypes in order.
 *
 * @author Matthew Shields
 * @version $Revision: 2921 $
 */
public class SequenceBuffer extends Unit implements TaskListener {

    // parameter data type definitions
    private String executionState;
    private int sequenceCurrent = 0;

    private Vector dataChannels = null;
    private boolean pleaseStop = true;
    private Thread outputThread = null;

    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {
        for (int i = 0; i < getInputNodeCount(); i++) {
            TrianaType input = (TrianaType) getInputAtNode(i);
            add(i, input);
            outputAtNode(i, input);
        }

        int frameNum = getDataElement(0, getChannel(0).size() - 1).getSequenceNumber();
        if (frameNum == -1) {
            frameNum = getChannel(0).size();
        }
        setParameter(SequenceBufferPanel.CURRENT, Integer.toString(frameNum));
        sequenceCurrent = frameNum;
    }

    private Vector getChannel(int channel) {
        return (Vector) dataChannels.get(channel);
    }

    /**
     * Sends the frame from all channels to the corresponding output node
     *
     * @param frameIndex the index into the data channels
     */
    private void outputFramesAt(int frameIndex) {
        for (int i = 0; i < getOutputNodeCount(); i++) {
            TrianaType frame = (TrianaType) getChannel(i).get(frameIndex);
            outputAtNode(i, frame);
        }
    }

    /**
     * Output the frames from all channels with an amended sequence number
     *
     * @param frameIndex the index of frames in the channels
     * @param newSeqNum  the amended sequence number
     */
    private void outPutFramesAtAmendSeq(int frameIndex, int newSeqNum) {
        if ((frameIndex >= 0) && (frameIndex < getChannel(0).size())) {
            try {
                for (int i = 0; i < getOutputNodeCount(); i++) {
                    Vector channel = getChannel(i);
                    if (frameIndex < channel.size()) {
                        TrianaType temp = ((TrianaType) channel.get(frameIndex)).copyMe();
                        temp.setSequenceNumber(newSeqNum);
                        outputAtNode(i, temp);
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            }
        }
    }

    /**
     * Add a data frame to a numbered data channel
     *
     * @param dataChannel the channel to add to
     * @param frame       the TrianaType data item
     */
    private void add(int dataChannel, TrianaType frame) {
        Vector buffer = getChannel(dataChannel);
        if ((!buffer.isEmpty()) && (frame.getSequenceNumber() < ((TrianaType) buffer.lastElement())
                .getSequenceNumber())) {
            emptyAllChannels();
        }
        buffer.add(frame);
    }

    /**
     * returns a particular frame from a channel
     *
     * @param dataChannel The required channel
     * @param index       th frame index
     * @return TrianaType requested
     */
    private TrianaType getDataElement(int dataChannel, int index) {
        try {
            return (TrianaType) getChannel(dataChannel).get(index);
        }
        catch (Exception e) {
            return null;
        }
    }

    private void emptyAllChannels() {
        for (Iterator iter = dataChannels.iterator(); iter.hasNext();) {
            Vector buffer = (Vector) iter.next();
            if (!buffer.isEmpty()) {
                buffer.clear();
            }
        }
        System.gc();
    }

    /**
     * Called when the unit is created. Initialises the unit's properties and parameters.
     */
    public void init() {
        super.init();

        // Initialise node properties
        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(1);

        setParameterPanelClass("common.input.SequenceBufferPanel");
        setHelpFileLocation("SequenceBuffer.html");
        setPopUpDescription("An input buffer that stores a sequence of TrianaTypes");

        setParameterUpdatePolicy(Task.IMMEDIATE_UPDATE);

        defineParameter(SequenceBufferPanel.STATE, SequenceBufferPanel.STOP, TRANSIENT);
        defineParameter(SequenceBufferPanel.CURRENT, "0", TRANSIENT);

        // Set up the default buffer
        dataChannels = new Vector();
        for (int i = 0; i < getInputNodeCount(); i++) {
            dataChannels.add(new Vector());
        }

    }

    /**
     * Called when the unit is reset.
     */
    public void reset() {
        // Set unit parameters to the values specified by the task definition
        setParameter(SequenceBufferPanel.STATE, SequenceBufferPanel.RESET);
        emptyAllChannels();
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up SequenceBuffer (e.g. close open files)
    }


    /**
     * @return an array of the input types for SequenceBuffer
     */
    public String[] getInputTypes() {
        return new String[]{"TrianaType"};
    }

    /**
     * @return an array of the output types for SequenceBuffer
     */
    public String[] getOutputTypes() {
        return new String[]{"TrianaType"};
    }

    /**
     * Called when the value of a parameter is changed, including when a parameter is removed.
     */
    public void parameterUpdate(String paramname, Object value) {
        // Update the unit parameters immediately the task parameters are updated

        if (paramname.equals(SequenceBufferPanel.STATE)) {
            executionState = (String) getParameter(SequenceBufferPanel.STATE);
            executeCommand(executionState);
        }

        if (paramname.equals(SequenceBufferPanel.CURRENT)) {
            int recdCurrent = new Integer((String) getParameter(SequenceBufferPanel.CURRENT)).intValue();
            if (recdCurrent != sequenceCurrent) {
                sequenceCurrent = recdCurrent;
                if (pleaseStop) {
                    int frameNum = getFrameNumForSequence(sequenceCurrent);
                    outPutFramesAtAmendSeq(frameNum, 0);
                }
            }
        }
    }

    /**
     * Called when the core properties of the task change i.e. its name
     */
    public void taskPropertyUpdate(TaskPropertyEvent event) {
    }

    /**
     * Called when the value of a parameter is changed, including when a parameter is removed.
     */
    public void parameterUpdated(ParameterUpdateEvent event) {
    }

    /**
     * Called when a data input node is added to the task.
     */
    public void nodeAdded(TaskNodeEvent event) {
        try {
            Task task = event.getTask();

            while (task.getDataOutputNodeCount() < task.getDataInputNodeCount()) {
                task.addDataOutputNode();
                setMaximumOutputNodes(task.getDataOutputNodeCount());
                setMinimumOutputNodes(task.getDataOutputNodeCount());

                addDataChannel();
            }
        } catch (NodeException except) {
            notifyError(except.getMessage());
        }
    }

    /**
     * Called before a data input node is removed from the task.
     */
    public void nodeRemoved(TaskNodeEvent event) {
        Task task = event.getTask();
        Node node = event.getNode();

        if (node.isInputNode()) {
            while (task.getDataOutputNodeCount() > task.getDataInputNodeCount() - 1) {
                task.removeDataOutputNode(task.getDataOutputNode(node.getNodeIndex()));
                setMaximumOutputNodes(task.getDataOutputNodeCount());
                setMinimumOutputNodes(task.getDataOutputNodeCount());

                removeDataChannel();
            }
        }
    }


    /**
     * Called before the task is disposed
     */
    public void taskDisposed(TaskDisposedEvent event) {
    }


    /**
     * Play, Stop and Pause are handled by the individual SequenceBuffer, Forward and Reverse commands are handled by
     * increment/decrement the current frame parameter. This gets passed through to the SequenceBuffers by the parameter
     * updated method.
     */
    private void executeCommand(String command) {
        if (command.equals(SequenceBufferPanel.PLAY)) {
            playSequence();
        }
        if (command.equals(SequenceBufferPanel.STOP)) {
            stopSequence();
        }
        if (command.equals(SequenceBufferPanel.PAUSE)) {
            pauseSequence();
        }
    }

    private void playSequence() {
        outputThread = new Thread(new Player(), "SequenceBuffer");
        outputThread.setPriority(Thread.NORM_PRIORITY);
        outputThread.start();
    }

    /**
     * Add a data channel to the buffer
     */
    private void addDataChannel() {
        dataChannels.add(new Vector());
    }

    private void removeDataChannel() {
        if (dataChannels.size() > 1) {
            Vector buffer = (Vector) dataChannels.remove(dataChannels.size() - 1);
            buffer.removeAllElements();
            buffer = null;
        }
    }

    private void stopSequence() {
        pleaseStop = true;
    }

    private void pauseSequence() {
        pleaseStop = true;
    }

    /**
     * Attempts to map the sequence number for the data to the frame number in the data channels. Assumes that all data
     * channels have the same sequence numbers...
     *
     * @param seqNum the sequence number for the data frame we are lloking for
     * @return the index into the data channels
     */
    private int getFrameNumForSequence(int seqNum) {
        int frameNum = 0;
        for (Iterator it = getChannel(0).iterator(); it.hasNext();) {
            TrianaType result = (TrianaType) it.next();
            if (result.getSequenceNumber() == -1) {
                return seqNum;
            } else if (result.getSequenceNumber() == seqNum) {
                return frameNum;
            }
            frameNum++;
        }
        return -1;
    }

    private class Player implements Runnable {
        public void run() {
            pleaseStop = false;
            int startFrame = sequenceCurrent;
            int frameNum = 0;
            Iterator it = getChannel(0).iterator();
            while (!pleaseStop && it.hasNext()) {
                TrianaType output = (TrianaType) it.next();
                int seqNum = output.getSequenceNumber() == -1 ? frameNum : output.getSequenceNumber();
                if (seqNum > startFrame) {
                    setParameter(SequenceBufferPanel.CURRENT, Integer.toString(seqNum));

                    // if we are not playing from the start, renumber a copy
                    // so that output starts at zero
                    if (startFrame > 0) {
                        outPutFramesAtAmendSeq(frameNum, seqNum - startFrame);
                    } else {
                        outputFramesAt(frameNum);
                    }
                } else if (output.getSequenceNumber() == -1) {
                    outputFramesAt(frameNum);
                }
                Thread.yield();
                frameNum++;
            }
            pleaseStop = true;
        }
    }
}



