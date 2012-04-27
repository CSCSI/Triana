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
package org.trianacode.taskgraph.service;

import org.apache.commons.logging.Log;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.enactment.logging.stampede.StampedeLog;
import org.trianacode.taskgraph.*;
import org.trianacode.taskgraph.clipin.ClipInBucket;
import org.trianacode.taskgraph.clipin.ClipInStore;
import org.trianacode.taskgraph.databus.DataBus;
import org.trianacode.taskgraph.databus.DataBusInterface;
import org.trianacode.taskgraph.databus.DataNotResolvableException;
import org.trianacode.taskgraph.databus.LocalDataBus;
import org.trianacode.taskgraph.databus.packet.WorkflowDataPacket;
import org.trianacode.taskgraph.proxy.java.JavaProxy;
import org.trianacode.taskgraph.ser.TrianaObjectInputStream;
import org.trianacode.taskgraph.tool.Tool;
import org.trianacode.taskgraph.tool.ToolTable;

import java.io.*;
import java.util.*;

/**
 * An extension to Task that allows the underlying OldUnit to be instantiated and run.
 * <p/>
 *
 * @author Ian Wang
 */

public class RunnableTask extends AbstractRunnableTask
        implements RunnableInstance, RunnableInterface, ControlInterface, Runnable {

    private static Log toolLog = Loggers.TOOL_LOGGER;
    //    private static StampedeLoggerInterface stampedeLog = Loggers.STAMPEDE_LOGGER;
    private StampedeLog stampedeLog;


    // A internal string representing null
    private static final String NULL_STR = "#<---NULL->#";
    // valid thread states
    private static final Integer NO_THREAD = new Integer(0);
    private static final Integer IN_PROCESS = new Integer(1);
    private static final Integer UNSTABLE = new Integer(2);

    // object to lock on - an instane variable
    private final Integer THREAD_LOCK = new Integer(-1);


    private Unit unit;

    /**
     * the thead the task is currently running in (or null if not running)
     */
    private Thread thread = null;

    /**
     * if runAgain is > 0 then the unit is run again and runAgain is Ndecremented
     */
    private int runAgain = 0;

    /**
     * The count for the number of data packets still not output
     */
    public int packetsLeft;

    /**
     * An array of the essential nodes that have called but not activated the unit due to waiting for data from other
     * essential nodes. When the unit is finally activated (data received from all essential nodes), this array is
     * cleared.
     */
    private ArrayList wakeups = new ArrayList();

    /**
     * A hashtable of parameter updates that have not been passed to the unit; this is used when parmeter updates are
     * only commited to the unit at the start of its proceess method.
     */
    private Hashtable paramupdates = new Hashtable();

    /**
     * A bucket containing all the clip-ins currently attached to this task
     */
    private ClipInBucket clipins = new ClipInBucket(this);

    /**
     * A hashtable containing the clip-ins for each data item input during the current process cycle.
     */
    private Hashtable clipinbuffer = new Hashtable();

    /**
     * A hashtable of all the queued clip-in objects keyed by name
     */
    private Hashtable clipinqueue = new Hashtable();

    /**
     * a flag indicating whether the unit has been initialised
     */
    private boolean init = false;
    /**
     * a flag indicating whether the thread doesn't exist, is processing or is unstable.
     */
    private Integer threadstate = NO_THREAD;

    protected RunnableTask(Tool task, TaskFactory factory, boolean preserveinst) throws TaskException {

        super(task, factory, preserveinst);
        if (getProxy() instanceof JavaProxy) {
            unit = ((JavaProxy) getProxy()).getUnit();
        }
        if (unit == null) {
            throw new TaskException("Could not load unit for " + this.getQualifiedToolName());
        }
        setDataInputTypes(unit.getInputTypes());
        setDataOutputTypes(unit.getOutputTypes());
        String[][] inTypes = unit.getNodeInputTypes();
        if (inTypes != null) {
            for (int i = 0; i < inTypes.length; i++) {
                String[] inType = inTypes[i];
                if (inType != null && inType.length > 0) {
                    setDataInputTypes(i, inType);
                }
            }
        }
        String[][] outTypes = unit.getNodeOutputTypes();
        if (outTypes != null) {
            for (int i = 0; i < outTypes.length; i++) {
                String[] inType = outTypes[i];
                if (inType != null && inType.length > 0) {
                    setDataOutputTypes(i, inType);
                }
            }
        }


    }

    /**
     * Initializes the unit associated with this runnable task.
     */
    public void initUnit() throws TaskException {
        if (unit != null) {
            unit.setRunnableInterface(this);
            unit.init();
            init = true;

            String[] paramnames = getParameterNames();
            for (int count = 0; count < paramnames.length; count++) {
                if (!getParameterType(paramnames[count]).equals(Tool.LATE_INITIALIZE)) {
                    unit.parameterUpdated(paramnames[count], getParameter(paramnames[count]));
                }
            }
            executionReset();
        }
    }


    /**
     * @return the task interface for this runnable task.
     */
    public Task getTask() {
        return this;
    }

    /**
     * @return the control interface to the scheduler (returns null if the task is not a control task)
     */
    public ControlInterface getControlInterface() {
        return this;
    }


    /**
     * @return the current tool table
     */
    public ToolTable getToolTable() {
        return TaskGraphManager.getToolTable();
    }


    /**
     * @return the clip-in attached to this task with the specified name (null if not present). The flag indicates
     *         whether the clip
     */
    public Object getClipIn(String name) {
        return clipins.getClipIn(name);
    }

    /**
     * @return the clip-in with the specified name that came attached to the the specified data item (null if not
     *         present). Only clip-ins for data that has been input in the current process can be retrieved.
     */
    public Object getClipIn(Object data, String name) {
        if (clipinbuffer.containsKey(data)) {
            return ((ClipInBucket) clipinbuffer.get(data)).getClipIn(name);
        } else {
            return null;
        }
    }

    /**
     * @return true if a clip-in with the specified name exists in this task's clip-in bucket
     */
    public boolean isClipInName(String name) {
        return clipins.isClipInName(name);
    }

    /**
     * Put the specified clip-in into this task's clip-in bucket
     */
    public void putClipIn(String name, Object clipin) {
        clipins.putClipIn(name, clipin);
    }

    /**
     * Queue the specified clip-in to be inserted into the clip-in bucket when the task is next run
     */
    public void queueClipIn(String name, Object clipin) {
        clipinqueue.put(name, clipin);
    }

    /**
     * Remove the clip-in with the specified name from this task.
     *
     * @return the removed clip-in (or null if unknown)
     */
    public Object removeClipIn(String name) {
        return clipins.removeClipIn(name);
    }


    /**
     * @return a copy of the current clip-in bucket
     */
    public ClipInStore extractClipInState() {
        return clipins.extractClipInStore();
    }

    /**
     * Restores a previously stored clip-in bucket state
     */
    public void restoreClipInState(ClipInStore store) {
        clipins.restoreClipInStore(store);
    }


    /**
     * Tells the Unit that an output node has finished sending the data. Here, we simply decrement the <b>packetsLeft
     * </b> variabe which is set to be equal to the number of non-blocking nodes wich are sending the data.
     */
    public final void finished() {
        --packetsLeft;
    }


    /**
     * Tells a task to wake-up if data has been received on all of its essential nodes.
     */
    public final synchronized void wakeUp() {
        boolean wakeup = true;

        Node[] nodes = getRequiredNodes();
        // Check if wake-ups have been received from all essential nodes.
        for (int count = 0; (count < nodes.length) && (wakeup); count++) {
            toolLog.debug(Arrays.toString(nodes) + " are required. Awoken nodes are : " + wakeups);
            if (!wakeups.contains(nodes[count])) {
                toolLog.debug("Awoken nodes are missing node " + nodes[count].getName());
                wakeup = false;
            }
        }

        if (wakeup) {
            startThread();
        }
    }

    /**
     * Tells the Task to wake up if data has been received on all of its essential nodes. The parameter indicates the
     * node that received data to cause this wake-up.
     */
    public final synchronized void wakeUp(Node node) {
        if (isRequired(node)) {
            //          wakeups.add(node);
            Node scopeNode = node.getTopLevelNode();
            wakeups.add(scopeNode);
            while (scopeNode.getChildNode() != null) {
                scopeNode = scopeNode.getChildNode();
                wakeups.add(scopeNode);
            }
            wakeUp();
        } else if (node.isParameterNode()) {
            receiveParameterValue(node);
        } else {
            wakeUp();
        }
    }

    /**
     * @return true if data is required from the specified node
     */
    private boolean isRequired(Node node) {
        Node top = node.getTopLevelNode();
        return top.isEssential() || (top.isEssentialIfConnected() && top.isConnected());
    }

    /**
     * @return an array of all the required nodes for the task
     */
    private Node[] getRequiredNodes() {
        Node[] nodes = getInputNodes();
        ArrayList required = new ArrayList();

        for (int count = 0; count < nodes.length; count++) {
            if (isRequired(nodes[count])) {
                required.add(nodes[count]);
            }
        }

        return (Node[]) required.toArray(new Node[required.size()]);
    }


    /**
     * Receives the parameter value from the specified node
     */
    private void receiveParameterValue(Node node) {
        if (node instanceof RunnableParameterNode) {
            Object parameter = getInput((RunnableParameterNode) node);
            String paramname = ((RunnableParameterNode) node).getParameterName();

            if (paramname.equals(ParameterNode.TRIGGER_PARAM)) {
                return;
            }

            if (parameter instanceof Number) {
                setParameter(paramname, parameter.toString());
            } else {
                setParameter(paramname, parameter);
            }
        }
    }

    /**
     * creates the thread to run the unit, or set a flag to re-run the existing thread
     */
    public final synchronized void startThread() {
        boolean handled = false;
        wakeups.clear();

        do {
            synchronized (THREAD_LOCK) {
                if (threadstate != UNSTABLE) {
                    executionRequested();

                    if (threadstate == IN_PROCESS) {
                        runAgain++;
                    } else {
                        runAgain = 0;
                        threadstate = UNSTABLE;

                        getProperties().getEngine().execute(this);
                    }
                    handled = true;
                } else {
                    Thread.yield();
                }
            }
        }
        while (!handled);
    }


    /**
     * @return true if there is data waiting on the specified node
     */
    public boolean isInput(int nodeNumber) {
        if (nodeNumber >= getDataInputNodeCount()) {
            throw new OutOfRangeException("Node " + nodeNumber + " is out of range on Unit " + getToolName());
        }

        Node node = getDataInputNode(nodeNumber);
        if ((node != null) && (node.isConnected())) {
            if (node.getCable() instanceof InputCable) {
                return ((InputCable) node.getCable()).isDataReady();
            }
        }
        return false;
    }

    /**
     * Returns the data at input node <i>nodeNumber</i>. If data is not ready, NOT_READY triana type is returned. If
     * there is no cable connected to the input node the NOT_CONNECTED triana type is returned.
     *
     * @param nodeNumber the node you want to get the data from.
     */
    public Object getInput(int nodeNumber)
            throws OutOfRangeException, EmptyingException, NotCompatibleException {

        if (nodeNumber >= getDataInputNodeCount()) {
            throw new OutOfRangeException("Node " + nodeNumber + " is out of range on Unit " + getToolName());
        }


        RunnableNode node = (RunnableNode) getDataInputNode(nodeNumber);
        if (!node.isConnected()) {
            toolLog.info("Node is not connected at input:" + nodeNumber);
            return null;
        }
        Object data = getInput(node);

        String[] types = getDataInputTypes(nodeNumber);

        if (types == null) {
            types = getDataInputTypes();
        }

        Class[] typecls = TypeChecking.classForTrianaType(types);

        if (!TypeChecking.isCompatible(data, typecls)) {
            // try and convert parameters or strings to consts
            /*try {
                if (data instanceof String)
                    data = new Const((String) data);
            }
            catch (NumberFormatException except) {
            }*/

            if (!TypeChecking.isCompatible(data, typecls)) {
                String errormsg = "Data type received at node " + node.getNodeIndex() + " on unit " + this.getToolName()
                        + " is incompatible with unit specification.\n";
                errormsg += "Received input types : " + data.getClass().getName() + "\n";
                errormsg += "Allowed input types :";

                for (int count = 0; count < types.length; count++) {
                    errormsg += " " + types[count];
                }

                throw new NotCompatibleException(errormsg);
            }
        }

        /* if (unit instanceof ProcessDoublesInterface) {
            // Convert data types if necessary : condition 1 :
            if ((Env.getConvertToDouble()) && (data instanceof GraphType) &&
                    (((ProcessDoublesInterface) unit).getCanProcessDoubleArrays()))
                ((GraphType) data).convertDependentDataArraysToDoubles();
            // condition 2 :
            if ((data instanceof GraphType) && (((ProcessDoublesInterface) unit).getRequireDoubleInputs()))
                ((GraphType) data).convertDependentDataArraysToDoubles();
        }*/
        return data;
    }

    /**
     * Returns the data at the specified input node. If data is not ready, NOT_READY triana type is returned. If there
     * is no cable connected to the input node the NOT_CONNECTED triana type is returned.
     */
    protected Object getInput(RunnableNodeInterface node) {
        InputCable cable = (InputCable) node.getCable();

        if (!node.isConnected()) {
            return ConnectionStatus.NOT_CONNECTED;
        }

        if (node.isOptional() && (!cable.isDataReady())) {
            return ConnectionStatus.NOT_READY;
        }

        Object mess = cable.recv();
        Object data;

        if (mess instanceof DataMessage) {
            data = ((DataMessage) mess).getData();

            //Now this is always a URL so we can cast....

            WorkflowDataPacket packet = (WorkflowDataPacket) data;

            // IAN T - gets the data by resolving the URL rather than just accepting the data as is.
            try {
                DataBusInterface db = DataBus.getDataBus(packet.getProtocol());
                data = db.get(packet);
            } catch (DataNotResolvableException e) {
                e.printStackTrace();
            }

            // insert the clip-ins attached to the data into the clip-in bucket
            if (((DataMessage) mess).hasClipIns()) {
                ClipInBucket messclips = ((DataMessage) mess).getClipIns();

                clipins.insert(messclips, node);
                clipinbuffer.put(messclips, clipins);
            }
        } else {
            data = mess;
        }
//        if (data != null) {
//            if (LoggingUtils.loggingInputs(this.getProperties())) {
////                logToSchedulerLogger(new StampedeEvent(LogDetail.UNIT_INPUT)
////                        .add(LogDetail.TASK, getQualifiedToolName())
////                        .add("NODE", node.getName())
////                        .add("DATA", data.toString())
////                        .add(LogDetail.WF, getUltimateParent().getQualifiedToolName())
////                );
//            } else {
////                logToSchedulerLogger(new StampedeEvent(LogDetail.UNIT_INPUT)
////                        .add(LogDetail.TASK, getQualifiedToolName())
////                        .add("NODE", node.getName())
////                        .add("DATA_LENGTH", "" + data.toString().length())
////                        .add(LogDetail.WF, getUltimateParent().getQualifiedToolName())
////                );
//            }
//        }
        return data;
    }


    /**
     * b Outputs the data from the unit. This passses the given data set to the first output node and then makes copies
     * for any other output nodes.
     *
     * @param data the data to be sent
     */
    public void output(Object data) {
        try {
            Object toOutput;
            RunnableNode node;

            packetsLeft = 0; // number of packets to output

            for (int i = 0; i < this.getDataOutputNodeCount(); ++i) {
                if (this.getDataOutputNode(i).isConnected()) {
                    ++packetsLeft;
                }
            }

            boolean clonemultiple = ((data instanceof Serializable) && !isParameterName(OUTPUT_POLICY)) || (getParameter(OUTPUT_POLICY).equals(
                    CLONE_MULTIPLE_OUTPUT));
            boolean cloneall = ((data instanceof Serializable) && isParameterName(OUTPUT_POLICY)) && (getParameter(OUTPUT_POLICY).equals(
                    CLONE_ALL_OUTPUT));

            for (int i = 0; i < this.getDataOutputNodeCount(); ++i) {
                node = (RunnableNode) this.getDataOutputNode(i);
                if (node.isConnected()) {
                    // check output policy
                    if (clonemultiple) {
                        if (i != this.getDataOutputNodeCount() - 1) {
                            toOutput = copyData(data);
                        } else {
                            toOutput = data;
                        }
                    } else if (cloneall) {
                        toOutput = copyData(data);
                    } else {
                        toOutput = data;
                    }
                    output(node, toOutput, true);
                }
            }
        } catch (NotSerializableException except) {
            System.out.println("Object " + data.getClass().getCanonicalName() + " is not serializable.");
            // TODO
            except.printStackTrace();
            //throw(new RuntimeException(Env.getString("serializeError") + ": " + except.getMessage()));
        } catch (IOException except) {
            // TODO
            except.printStackTrace();
            //throw(new RuntimeException(Env.getString("outputError") + ": " + except.getMessage()));
        } catch (ClassNotFoundException except) {
            // TODO
            except.printStackTrace();
            //throw(new RuntimeException(Env.getString("outputError") + ": " + except.getMessage()));
        }
    }

    /**
     * Makes a copy of the specified data. If the data is of TrianaType the copyMe method is used, otherwise the data is
     * serialized then deserialized.
     * <p/>
     * Note: this relies on all data types being serialisable Note2: this method uses ByteArrayInputStream and
     * OutputStream which are not optimised, they are synchronized (not needed here) and the buffer size and growth
     * factor could be tweeked
     */
    private Object copyData(Object data) throws IOException, ClassNotFoundException {
        /*
        // TODO
        if (data instanceof TrianaType)
            return ((TrianaType) data).copyMe();
        else {*/
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream objout = new ObjectOutputStream(bos);
        objout.writeObject(data);
        objout.flush();
        objout.close();
        bos.close();

        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream objin = new TrianaObjectInputStream(bis);
        Object ret = null;
        try {
            ret = objin.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        objin.close();
        return ret;
        //}
    }

    /**
     * Outputs the data to the given node <i>outputNode</i>. If specified this method blocks until the data is
     * successfully sent, otherwise, if non-blocking, isOutputSent() can be used to poll whether the data has been
     * successfully sent. This method is used to set the data at each particular output node if this is necessary,
     * otherwise use output(Object data) to copy the data across all nodes.
     *
     * @param outputNode the output node you wish to set
     * @param data       the data to be sent
     */
    public void output(int outputNode, Object data, boolean blocking) {
        output((RunnableNodeInterface) getDataOutputNode(outputNode), data, blocking);
    }

    /**
     * Output the specified object on the specified node
     */
    void output(RunnableNodeInterface node, Object data, boolean blocking) {


        toolLog.debug("RunnableTask.output ENTER with data:" + data);
        if (!node.isParameterNode()) {
            waitPause();

            if (!getExecutionState().equals(ExecutionState.RUNNING)) {
                throw (new EmptyingException("Output Error: " + getToolName() + " is not running"));
            }
        }

        if (node.isConnected()) {
            ClipInBucket extract = clipins.extract(node);

            // IAN T - Need to map GUI choice of data sending to databus here:

            WorkflowDataPacket packet = DataBus.getDataBus(LocalDataBus.LOCAL_PROTOCOL)
                    .addObject(data, true);

            //HTTPServices.getWorkflowServer().addDataResource(packet.getDataLocation().getPath(), (Serializable) data);

            toolLog.debug("RunnableTask.output ENTER URL = " + packet.getDataLocation());
            DataMessage mess = new DataMessage(packet, extract);


//            if (LoggingUtils.loggingInputs(this.getProperties())) {
//                logToSchedulerLogger(new StampedeEvent(LogDetail.UNIT_OUTPUT)
//                        .add(LogDetail.TASK, getQualifiedToolName())
//                        .add("NODE", node.getName())
//                        .add("DATA", data.toString())
//                        .add(LogDetail.WF, getUltimateParent().getQualifiedToolName())
//                );
//            } else {
//                logToSchedulerLogger(new StampedeEvent(LogDetail.UNIT_OUTPUT)
//                        .add(LogDetail.TASK, getQualifiedToolName())
//                        .add("NODE", node.getName())
//                        .add("DATA_LENGTH", "" + data.toString().length())
//                        .add(LogDetail.WF, getUltimateParent().getQualifiedToolName())
//                );
//            }
            if (blocking) {
                ((OutputCable) node.getCable()).send(mess);
            } else {
                ((OutputCable) node.getCable()).sendNonBlocking(mess);
            }
        }
    }

    private Scheduler getScheduler(TaskGraph taskGraph) {
        TrianaServer trianaServer = TaskGraphManager.getTrianaServer(taskGraph);
        SchedulerInterface scheduler = trianaServer.getSchedulerInterface();
        if (scheduler instanceof Scheduler) {
            return (Scheduler) scheduler;
        }
        return null;
    }

//    private void logToSchedulerLogger(StampedeEvent stampedeEvent) {
//        getScheduler(this.getParent()).stampedeLog.logStampedeEvent(stampedeEvent);
//    }


    /**
     * @return true if the data sent with the non-blocking send call has reached its destination
     */
    public boolean isOutputSent(int outputNode) {
        if (outputNode >= getDataOutputNodeCount()) {
            return false;
        }

        RunnableNode gn = (RunnableNode) getDataOutputNode(outputNode);
        OutputCable cable = (OutputCable) gn.getCable();

        if (cable == null) {
            return false;
        } else {
            return cable.isDataSent();
        }
    }


    /**
     * This over-rides the RunnableUnit's run method so that we can display the data flow indicators on the icons when
     * we are processing data.
     */
    public void run() {
        thread = Thread.currentThread();

        do {
            if ((isRunContinuously()) && (runAgain <= 0)) {
                runAgain = 1;
                executionRequested();

            }

            if (getExecutionState() != ExecutionState.RESETTING) {
                beforeProcess();

                synchronized (THREAD_LOCK) {
                    threadstate = IN_PROCESS;
                }
                process();

                synchronized (THREAD_LOCK) {
                    threadstate = UNSTABLE;
                }

                afterProcess();
            }
        }
        while ((runAgain-- > 0) && (getExecutionState() != ExecutionState.RESETTING));

        finishProcess();

        thread = null;
        threadstate = NO_THREAD;
    }

    private void finishProcess() {
        if (getExecutionState().equals(ExecutionState.RESETTING)) {
            unit.reset();
            wakeups.clear();
            runAgain = 0;

            executionReset();
        }
    }


    /**
     * Order a unit to stop, resets the unit to its pre-run state
     */
    public void reset() {
        super.reset();

        getProperties().getEngine().execute(new Runnable() {
            public void run() {

                boolean handled = false;

                do {
                    synchronized (THREAD_LOCK) {
                        if (threadstate != UNSTABLE) {
                            if (threadstate == IN_PROCESS) {
                                unit.stopping();
                                thread.interrupt();
                            } else {
                                unit.reset();
                                wakeups.clear();

                                executionReset();
                            }

                            handled = true;
                        } else {
                            Thread.yield();
                        }
                    }
                }
                while (!handled);
            }
        });
    }


    /**
     * Runs the specified task.
     * <p/>
     * If execution is paused then this method waits until execution is resumed, or throws a SchedulerException if
     * execution is reset.
     */
    public void runTask(Task task) throws SchedulerException {
        waitPause();
        TaskGraphManager.getTrianaServer(getParent()).runTask(task);
    }

    /**
     * Runs all the tasks within the group that this task belongs to.
     * <p/>
     * If execution is paused then this method waits until execution is resumed, or throws a SchedulerException if
     * execution is reset.
     */
    public void runGroup() throws SchedulerException {
        if (getParent() != null) {
            waitPause();

            Task[] tasks = getParent().getTasks(false);

            for (int count = 0; count < tasks.length; count++) {
                TaskGraphManager.getTrianaServer(getParent()).runTask(tasks[count]);
            }
        }
    }

    /**
     * Tries running the process() method.
     * <p/>
     * TODO: Better error handling
     */
    public void process() {
        try {
            stampedeLog = getScheduler(this.getParent()).stampedeLog;
            toolLog.info("RUNNING " + getQualifiedToolName());

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("\"");
            for (String param : this.getParameterNames()) {
                stringBuilder.append(param)
                        .append(":")
                        .append(this.getParameter(param))
                        .append(",");
            }
            stringBuilder.append("\"");

//            String hostname;
//            try {
//                hostname = Inet4Address.getLocalHost().getHostName();
//            } catch (UnknownHostException e) {
//                hostname = "localhost";
//            }

            Scheduler scheduler = this.getScheduler(this.getParent());
            if (scheduler != null) {

//                logToSchedulerLogger(scheduler.addSchedJobInstDetails(new StampedeEvent(LogDetail.JOB_START)
//                        .add(LogDetail.STD_OUT_FILE, scheduler.runtimeFileLog.getLogFilePath())
//                        .add(LogDetail.STD_ERR_FILE, scheduler.runtimeFileLog.getLogFilePath()),
//                        this));

                stampedeLog.logInvocationStart(this);

//                logToSchedulerLogger(scheduler.stampedeLog.addBaseEventDetails(new StampedeEvent(LogDetail.INVOCATION_START)
//                        .add(LogDetail.UNIT_INST_ID, String.valueOf(scheduler.stampedeLog.getTaskNumber(this)))
//                        .add(LogDetail.UNIT_ID, this.getQualifiedToolName())
//                        .add(LogDetail.INVOCATION_ID, "1")));
            }

            long startTime = new Date().getTime() / 1000;

            waitPause();
            try {

                unit.process();
            } catch (Exception except) {
                notifyError(except);
                toolLog.warn("Exception thrown invoking process() on Unit:", except);
            }


            if (scheduler != null) {

                stampedeLog.logJobTerminate(this);

//                logToSchedulerLogger(scheduler.stampedeLog.addSchedJobInstDetails(new StampedeEvent(LogDetail.JOB_TERM)
//                        .add(LogDetail.STATUS, "0"),
//                        this)
//                );
                long duration = (new Date().getTime() / 1000) - startTime;
                if (duration == 0) {
                    duration = 1;
                }

                stampedeLog.logInvocationEnd(
                        this, stringBuilder.toString().replaceAll("[\n\r]", ""), startTime, duration);

//                StampedeEvent invEnd = new StampedeEvent(LogDetail.INVOCATION_END);
//                scheduler.stampedeLog.addBaseEventDetails(invEnd)
//                        .add(LogDetail.UNIT_INST_ID, String.valueOf(scheduler.stampedeLog.getTaskNumber(this)))
//                        .add(LogDetail.INVOCATION_ID, "1")
//                        .add(LogDetail.UNIT_ID, "unit:" + this.getQualifiedToolName())
//                        .add(LogDetail.START_TIME, String.valueOf(startTime))
//                        .add(LogDetail.DURATION, String.valueOf(duration))
//                        .add(LogDetail.TRANSFORMATION, this.getQualifiedTaskName())
//                        .add(LogDetail.EXECUTABLE, "Triana")
//                        .add(LogDetail.ARGS, stringBuilder.toString().replaceAll("[\n\r]", ""))
//                        .add(LogDetail.TASK_ID, this.getQualifiedTaskName()
//                        );

                stampedeLog.logHost(this);
//                logToSchedulerLogger(scheduler.stampedeLog.addBaseJobInstDetails(new StampedeEvent(LogDetail.HOST), this)
//                        .add(LogDetail.SITE, "localhost")
//                        .add(LogDetail.HOSTNAME, hostname)
//                        .add(LogDetail.IP_ADDRESS, "127.0.0.1")
//                );

//                StampedeEvent endJob = new StampedeEvent(LogDetail.JOB_END);
//                scheduler.addSchedJobInstDetails(endJob, this)
//                        .add(LogDetail.STD_OUT_FILE, scheduler.runtimeFileLog.getLogFilePath())
//                        .add(LogDetail.STD_ERR_FILE, scheduler.runtimeFileLog.getLogFilePath())
//                        .add(LogDetail.SITE, hostname)
//                        .add(LogDetail.MULTIPLIER, "1");

                if (!getExecutionState().equals(ExecutionState.ERROR)) {
                    toolLog.info("FINISHED RUNNING " + getQualifiedToolName());
////                    endJob.add(LogDetail.STATUS, "-1");
////                    endJob.add(LogDetail.EXIT_CODE, "1");
//                    invEnd.add(LogDetail.EXIT_CODE, "0");
//
                } else {
////                    endJob.add(LogDetail.STATUS, "0");
////                    endJob.add(LogDetail.EXIT_CODE, "0");
//                    invEnd.add(LogDetail.EXIT_CODE, "1");
                }
//
//                logToSchedulerLogger(invEnd);
//                logToSchedulerLogger(endJob);
            }

        } catch (OutOfRangeException ore) {
            notifyError(ore);
        } catch (EmptyingException ee) {
        } catch (NotCompatibleException nce) {
            notifyError(nce);
        } catch (OutOfMemoryError ep) {
            notifyError(ep);
            System.runFinalization();
            System.gc();
        } catch (Throwable e) {
            notifyError(e);
        }
    }

    private void notifyError(Throwable e) {
        StringWriter s = new StringWriter();
        s.write("Error in:" + getQualifiedTaskName() + "\n");
        PrintWriter p = new PrintWriter(s);
        e.printStackTrace(p);
        p.flush();
        notifyError(s.toString());

    }

    private void receiveTriggerParameters() {
        ParameterNode[] params = getParameterInputNodes();
        for (int count = 0; count < params.length; count++) {
            if (params[count].isTriggerNode()) {
                receiveParameterValue(params[count]);
            }
        }
    }

    /**
     * This function is called immediately before the unit's process() function is called
     */
    public void beforeProcess() {
        // empty the clip-in bucket and buffer
        clipins.empty();
        clipinbuffer.clear();

        Enumeration enumeration = clipinqueue.keys();
        String key;

        while (enumeration.hasMoreElements()) {
            key = (String) enumeration.nextElement();
            clipins.putClipIn(key, clipinqueue.get(key));
        }

        clipinqueue.clear();

        receiveTriggerParameters();
        executionStarting();
    }

    public void afterProcess() {
        // notify unit of parameter updates if param update policy is start of
        // process (also default behaviour if no param update policy).
        if ((!isParameterName(PARAM_UPDATE_POLICY)) || (getParameter(PARAM_UPDATE_POLICY).equals(PROCESS_UPDATE))) {
            synchronized (paramupdates) {
                Enumeration enumeration = paramupdates.keys();
                String paramname;

                while (enumeration.hasMoreElements()) {
                    paramname = (String) enumeration.nextElement();

                    if (paramupdates.get(paramname).equals(NULL_STR)) {
                        unit.parameterUpdated(paramname, null);
                    } else {
                        unit.parameterUpdated(paramname, paramupdates.get(paramname));
                    }
                }

                paramupdates.clear();
            }
        }

        executionFinished();
    }


    protected void notifyParameterSet(String name, String type, Object oldval, Object newval) {
        if ((unit != null) && (init)) {
            if ((threadstate != IN_PROCESS) || ((isParameterName(PARAM_UPDATE_POLICY)) && (getParameter(
                    PARAM_UPDATE_POLICY).equals(IMMEDIATE_UPDATE)))) {
                unit.parameterUpdated(name, getParameter(name));
            } else if ((!isParameterName(PARAM_UPDATE_POLICY)) || (getParameter(PARAM_UPDATE_POLICY).equals(
                    PROCESS_UPDATE))) {
                if (getParameter(name) != null) {
                    paramupdates.put(name, getParameter(name));
                } else {
                    paramupdates.put(name, NULL_STR);
                }
            }
        }

        super.notifyParameterSet(name, type, oldval, newval);
    }

    /**
     * disposes of the running unit
     */
    public void dispose() {
        if (unit != null) {
            unit.dispose();
        }
        super.dispose();
    }

}
