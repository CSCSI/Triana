// Copyright 2003-2005 Arthur van Hoff, Rick Blair
// Licensed under Apache License version 2.0
// Original license LGPL

package org.trianacode.pegasus.jmdns.impl.tasks.state;

import java.io.IOException;
import java.util.Timer;
import java.util.logging.Logger;

import org.trianacode.pegasus.jmdns.impl.DNSOutgoing;

import org.trianacode.pegasus.jmdns.impl.constants.DNSConstants;
import org.trianacode.pegasus.jmdns.impl.constants.DNSRecordClass;
import org.trianacode.pegasus.jmdns.impl.constants.DNSState;

/**
 * The Prober sends three consecutive probes for all service infos that needs probing as well as for the host name. The state of each service info of the host name is advanced, when a probe has been sent for it. When the prober has run three times,
 * it launches an Announcer.
 * <p/>
 * If a conflict during probes occurs, the affected service infos (and affected host name) are taken away from the prober. This eventually causes the prober to cancel itself.
 */
public class Prober extends DNSStateTask {
    static Logger logger = Logger.getLogger(Prober.class.getName());

    public Prober(org.trianacode.pegasus.jmdns.impl.JmDNSImpl jmDNSImpl) {
        super(jmDNSImpl, defaultTTL());

        this.setTaskState(DNSState.PROBING_1);
        this.associate(DNSState.PROBING_1);
    }

    /*
     * (non-Javadoc)
     * @see org.trianacode.pegasus.jmdns.impl.tasks.DNSTask#getName()
     */
    @Override
    public String getName() {
        return "Prober(" + (this.getDns() != null ? this.getDns().getName() : "") + ")";
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
        long now = System.currentTimeMillis();
        if (now - this.getDns().getLastThrottleIncrement() < org.trianacode.pegasus.jmdns.impl.constants.DNSConstants.PROBE_THROTTLE_COUNT_INTERVAL) {
            this.getDns().setThrottle(this.getDns().getThrottle() + 1);
        } else {
            this.getDns().setThrottle(1);
        }
        this.getDns().setLastThrottleIncrement(now);

        if (this.getDns().isAnnounced() && this.getDns().getThrottle() < org.trianacode.pegasus.jmdns.impl.constants.DNSConstants.PROBE_THROTTLE_COUNT) {
            timer.schedule(this, org.trianacode.pegasus.jmdns.impl.JmDNSImpl.getRandom().nextInt(1 + org.trianacode.pegasus.jmdns.impl.constants.DNSConstants.PROBE_WAIT_INTERVAL), org.trianacode.pegasus.jmdns.impl.constants.DNSConstants.PROBE_WAIT_INTERVAL);
        } else if (!this.getDns().isCanceling() && !this.getDns().isCanceled()) {
            timer.schedule(this, org.trianacode.pegasus.jmdns.impl.constants.DNSConstants.PROBE_CONFLICT_INTERVAL, org.trianacode.pegasus.jmdns.impl.constants.DNSConstants.PROBE_CONFLICT_INTERVAL);
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
        return "probing";
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
    protected org.trianacode.pegasus.jmdns.impl.DNSOutgoing createOugoing() {
        return new DNSOutgoing(DNSConstants.FLAGS_QR_QUERY);
    }

    /*
     * (non-Javadoc)
     * @see javax.jmdns.impl.tasks.state.DNSStateTask#buildOutgoingForDNS(org.trianacode.pegasus.jmdns.impl.DNSOutgoing)
     */
    @Override
    protected org.trianacode.pegasus.jmdns.impl.DNSOutgoing buildOutgoingForDNS(DNSOutgoing out) throws IOException {
        org.trianacode.pegasus.jmdns.impl.DNSOutgoing newOut = out;
        newOut.addQuestion(org.trianacode.pegasus.jmdns.impl.DNSQuestion.newQuestion(this.getDns().getLocalHost().getName(), org.trianacode.pegasus.jmdns.impl.constants.DNSRecordType.TYPE_ANY, org.trianacode.pegasus.jmdns.impl.constants.DNSRecordClass.CLASS_IN, org.trianacode.pegasus.jmdns.impl.constants.DNSRecordClass.NOT_UNIQUE));
        for (org.trianacode.pegasus.jmdns.impl.DNSRecord answer : this.getDns().getLocalHost().answers(DNSRecordClass.NOT_UNIQUE, this.getTTL())) {
            newOut = this.addAuthoritativeAnswer(newOut, answer);
        }
        return newOut;
    }

    /*
     * (non-Javadoc)
     * @see javax.jmdns.impl.tasks.state.DNSStateTask#buildOutgoingForInfo(org.trianacode.pegasus.jmdns.impl.ServiceInfoImpl, org.trianacode.pegasus.jmdns.impl.DNSOutgoing)
     */
    @Override
    protected org.trianacode.pegasus.jmdns.impl.DNSOutgoing buildOutgoingForInfo(org.trianacode.pegasus.jmdns.impl.ServiceInfoImpl info, org.trianacode.pegasus.jmdns.impl.DNSOutgoing out) throws IOException {
        org.trianacode.pegasus.jmdns.impl.DNSOutgoing newOut = out;
        newOut = this.addQuestion(newOut, org.trianacode.pegasus.jmdns.impl.DNSQuestion.newQuestion(info.getQualifiedName(), org.trianacode.pegasus.jmdns.impl.constants.DNSRecordType.TYPE_ANY, DNSRecordClass.CLASS_IN, DNSRecordClass.NOT_UNIQUE));
        // the "unique" flag should be not set here because these answers haven't been proven unique yet this means the record will not exactly match the announcement record
        newOut = this.addAuthoritativeAnswer(newOut, new org.trianacode.pegasus.jmdns.impl.DNSRecord.Service(info.getQualifiedName(), DNSRecordClass.CLASS_IN, org.trianacode.pegasus.jmdns.impl.constants.DNSRecordClass.NOT_UNIQUE, this.getTTL(), info.getPriority(), info.getWeight(), info.getPort(), this.getDns().getLocalHost()
                .getName()));
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
        if (!this.getTaskState().isProbing()) {
            cancel();

            this.getDns().startAnnouncer();
        }
    }

}