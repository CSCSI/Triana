// Copyright 2003-2005 Arthur van Hoff, Rick Blair
// Licensed under Apache License version 2.0
// Original license LGPL

package org.trianacode.pegasus.jmdns.impl.tasks.state;

import org.trianacode.pegasus.jmdns.impl.DNSRecord;
import org.trianacode.pegasus.jmdns.impl.ServiceInfoImpl;
import org.trianacode.pegasus.jmdns.impl.constants.DNSConstants;
import org.trianacode.pegasus.jmdns.impl.constants.DNSRecordClass;
import org.trianacode.pegasus.jmdns.impl.constants.DNSState;

import java.io.IOException;
import java.util.Timer;
import java.util.logging.Logger;

/**
 * The Canceler sends two announces with TTL=0 for the specified services.
 */
public class Canceler extends DNSStateTask {
    static Logger logger = Logger.getLogger(Canceler.class.getName());

    public Canceler(org.trianacode.pegasus.jmdns.impl.JmDNSImpl jmDNSImpl) {
        super(jmDNSImpl, 0);

        this.setTaskState(org.trianacode.pegasus.jmdns.impl.constants.DNSState.CANCELING_1);
        this.associate(DNSState.CANCELING_1);
    }

    /*
     * (non-Javadoc)
     * @see org.trianacode.pegasus.jmdns.impl.tasks.DNSTask#getName()
     */
    @Override
    public String getName() {
        return "Canceler(" + (this.getDns() != null ? this.getDns().getName() : "") + ")";
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return super.toString() + " state: " + this.getTaskState();
    }

    /*
     * (non-Javadoc)
     * @see org.trianacode.pegasus.jmdns.impl.tasks.DNSTask#start(java.util.Timer)
     */
    @Override
    public void start(Timer timer) {
        timer.schedule(this, 0, DNSConstants.ANNOUNCE_WAIT_INTERVAL);
    }

    /*
     * (non-Javadoc)
     * @see java.util.TimerTask#cancel()
     */
    @Override
    public boolean cancel() {
        this.removeAssociation();

        return super.cancel();
    }

    /*
     * (non-Javadoc)
     * @see javax.jmdns.impl.tasks.state.DNSStateTask#getTaskDescription()
     */
    @Override
    public String getTaskDescription() {
        return "canceling";
    }

    /*
     * (non-Javadoc)
     * @see javax.jmdns.impl.tasks.state.DNSStateTask#checkRunCondition()
     */
    @Override
    protected boolean checkRunCondition() {
        return true;
    }

    /*
     * (non-Javadoc)
     * @see javax.jmdns.impl.tasks.state.DNSStateTask#createOugoing()
     */
    @Override
    protected org.trianacode.pegasus.jmdns.impl.DNSOutgoing createOugoing() {
        return new org.trianacode.pegasus.jmdns.impl.DNSOutgoing(DNSConstants.FLAGS_QR_RESPONSE | DNSConstants.FLAGS_AA);
    }

    /*
     * (non-Javadoc)
     * @see javax.jmdns.impl.tasks.state.DNSStateTask#buildOutgoingForDNS(org.trianacode.pegasus.jmdns.impl.DNSOutgoing)
     */
    @Override
    protected org.trianacode.pegasus.jmdns.impl.DNSOutgoing buildOutgoingForDNS(org.trianacode.pegasus.jmdns.impl.DNSOutgoing out) throws IOException {
        org.trianacode.pegasus.jmdns.impl.DNSOutgoing newOut = out;
        for (DNSRecord answer : this.getDns().getLocalHost().answers(DNSRecordClass.UNIQUE, this.getTTL())) {
            newOut = this.addAnswer(newOut, null, answer);
        }
        return newOut;
    }

    /*
     * (non-Javadoc)
     * @see javax.jmdns.impl.tasks.state.DNSStateTask#buildOutgoingForInfo(org.trianacode.pegasus.jmdns.impl.ServiceInfoImpl, org.trianacode.pegasus.jmdns.impl.DNSOutgoing)
     */
    @Override
    protected org.trianacode.pegasus.jmdns.impl.DNSOutgoing buildOutgoingForInfo(ServiceInfoImpl info, org.trianacode.pegasus.jmdns.impl.DNSOutgoing out) throws IOException {
        org.trianacode.pegasus.jmdns.impl.DNSOutgoing newOut = out;
        for (DNSRecord answer : info.answers(DNSRecordClass.UNIQUE, this.getTTL(), this.getDns().getLocalHost())) {
            newOut = this.addAnswer(newOut, null, answer);
        }
        return newOut;
    }

    /*
     * (non-Javadoc)
     * @see javax.jmdns.impl.tasks.state.DNSStateTask#recoverTask(java.lang.Throwable)
     */
    @Override
    protected void recoverTask(Throwable e) {
        this.getDns().recover();
    }

    /*
     * (non-Javadoc)
     * @see javax.jmdns.impl.tasks.state.DNSStateTask#advanceTask()
     */
    @Override
    protected void advanceTask() {
        this.setTaskState(this.getTaskState().advance());
        if (!this.getTaskState().isCanceling()) {
            cancel();
        }
    }
}