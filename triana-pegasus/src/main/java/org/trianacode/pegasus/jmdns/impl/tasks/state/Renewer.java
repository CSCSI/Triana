// Copyright 2003-2005 Arthur van Hoff, Rick Blair
// Licensed under Apache License version 2.0
// Original license LGPL

package org.trianacode.pegasus.jmdns.impl.tasks.state;

import java.io.IOException;
import java.util.Timer;
import java.util.logging.Logger;

import org.trianacode.pegasus.jmdns.impl.DNSOutgoing;
import org.trianacode.pegasus.jmdns.impl.DNSRecord;

import org.trianacode.pegasus.jmdns.impl.constants.DNSRecordClass;
import org.trianacode.pegasus.jmdns.impl.constants.DNSState;

/**
 * The Renewer is there to send renewal announcement when the record expire for ours infos.
 */
public class Renewer extends DNSStateTask {
    static Logger logger = Logger.getLogger(Renewer.class.getName());

    public Renewer(org.trianacode.pegasus.jmdns.impl.JmDNSImpl jmDNSImpl) {
        super(jmDNSImpl, defaultTTL());

        this.setTaskState(DNSState.ANNOUNCED);
        this.associate(org.trianacode.pegasus.jmdns.impl.constants.DNSState.ANNOUNCED);
    }

    /*
     * (non-Javadoc)
     * @see org.trianacode.pegasus.jmdns.impl.tasks.DNSTask#getName()
     */
    @Override
    public String getName() {
        return "Renewer(" + (this.getDns() != null ? this.getDns().getName() : "") + ")";
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
        if (!this.getDns().isCanceling() && !this.getDns().isCanceled()) {
            timer.schedule(this, org.trianacode.pegasus.jmdns.impl.constants.DNSConstants.ANNOUNCED_RENEWAL_TTL_INTERVAL, org.trianacode.pegasus.jmdns.impl.constants.DNSConstants.ANNOUNCED_RENEWAL_TTL_INTERVAL);
        }
    }

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
        return "renewing";
    }

    /*
     * (non-Javadoc)
     * @see javax.jmdns.impl.tasks.state.DNSStateTask#checkRunCondition()
     */
    @Override
    protected boolean checkRunCondition() {
        return !this.getDns().isCanceling() && !this.getDns().isCanceled();
    }

    /*
     * (non-Javadoc)
     * @see javax.jmdns.impl.tasks.state.DNSStateTask#createOugoing()
     */
    @Override
    protected DNSOutgoing createOugoing() {
        return new DNSOutgoing(org.trianacode.pegasus.jmdns.impl.constants.DNSConstants.FLAGS_QR_RESPONSE | org.trianacode.pegasus.jmdns.impl.constants.DNSConstants.FLAGS_AA);
    }

    /*
     * (non-Javadoc)
     * @see javax.jmdns.impl.tasks.state.DNSStateTask#buildOutgoingForDNS(org.trianacode.pegasus.jmdns.impl.DNSOutgoing)
     */
    @Override
    protected DNSOutgoing buildOutgoingForDNS(DNSOutgoing out) throws IOException {
        DNSOutgoing newOut = out;
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
    protected DNSOutgoing buildOutgoingForInfo(org.trianacode.pegasus.jmdns.impl.ServiceInfoImpl info, DNSOutgoing out) throws IOException {
        org.trianacode.pegasus.jmdns.impl.DNSOutgoing newOut = out;
        for (org.trianacode.pegasus.jmdns.impl.DNSRecord answer : info.answers(org.trianacode.pegasus.jmdns.impl.constants.DNSRecordClass.UNIQUE, this.getTTL(), this.getDns().getLocalHost())) {
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
        if (!this.getTaskState().isAnnounced()) {
            cancel();
        }
    }
}