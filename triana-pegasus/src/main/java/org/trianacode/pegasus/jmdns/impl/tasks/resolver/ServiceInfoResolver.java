// Copyright 2003-2005 Arthur van Hoff, Rick Blair
// Licensed under Apache License version 2.0
// Original license LGPL

package org.trianacode.pegasus.jmdns.impl.tasks.resolver;

import java.io.IOException;

import org.trianacode.pegasus.jmdns.impl.DNSOutgoing;
import org.trianacode.pegasus.jmdns.impl.DNSRecord;
import org.trianacode.pegasus.jmdns.impl.ServiceInfoImpl;
import org.trianacode.pegasus.jmdns.impl.constants.DNSRecordClass;
import org.trianacode.pegasus.jmdns.impl.constants.DNSRecordType;

/**
 * The ServiceInfoResolver queries up to three times consecutively for a service info, and then removes itself from the timer.
 * <p/>
 * The ServiceInfoResolver will run only if JmDNS is in state ANNOUNCED. REMIND: Prevent having multiple service resolvers for the same info in the timer queue.
 */
public class ServiceInfoResolver extends DNSResolverTask {

    private final ServiceInfoImpl _info;

    public ServiceInfoResolver(org.trianacode.pegasus.jmdns.impl.JmDNSImpl jmDNSImpl, org.trianacode.pegasus.jmdns.impl.ServiceInfoImpl info) {
        super(jmDNSImpl);
        this._info = info;
        info.setDns(this.getDns());
        this.getDns().addListener(info, org.trianacode.pegasus.jmdns.impl.DNSQuestion.newQuestion(info.getQualifiedName(), org.trianacode.pegasus.jmdns.impl.constants.DNSRecordType.TYPE_ANY, DNSRecordClass.CLASS_IN, DNSRecordClass.NOT_UNIQUE));
    }

    /*
     * (non-Javadoc)
     * @see org.trianacode.pegasus.jmdns.impl.tasks.DNSTask#getName()
     */
    @Override
    public String getName() {
        return "ServiceInfoResolver(" + (this.getDns() != null ? this.getDns().getName() : "") + ")";
    }

    /*
     * (non-Javadoc)
     * @see java.util.TimerTask#cancel()
     */
    @Override
    public boolean cancel() {
        // We should not forget to remove the listener
        boolean result = super.cancel();
        if (!_info.isPersistent()) {
            this.getDns().removeListener(_info);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * @see javax.jmdns.impl.tasks.Resolver#addAnswers(org.trianacode.pegasus.jmdns.impl.DNSOutgoing)
     */
    @Override
    protected DNSOutgoing addAnswers(DNSOutgoing out) throws IOException {
        DNSOutgoing newOut = out;
        if (!_info.hasData()) {
            long now = System.currentTimeMillis();
            newOut = this.addAnswer(newOut, (DNSRecord) this.getDns().getCache().getDNSEntry(_info.getQualifiedName(), DNSRecordType.TYPE_SRV, DNSRecordClass.CLASS_IN), now);
            newOut = this.addAnswer(newOut, (DNSRecord) this.getDns().getCache().getDNSEntry(_info.getQualifiedName(), org.trianacode.pegasus.jmdns.impl.constants.DNSRecordType.TYPE_TXT, DNSRecordClass.CLASS_IN), now);
            if (_info.getServer().length() > 0) {
                newOut = this.addAnswer(newOut, (DNSRecord) this.getDns().getCache().getDNSEntry(_info.getServer(), DNSRecordType.TYPE_A, org.trianacode.pegasus.jmdns.impl.constants.DNSRecordClass.CLASS_IN), now);
                newOut = this.addAnswer(newOut, (org.trianacode.pegasus.jmdns.impl.DNSRecord) this.getDns().getCache().getDNSEntry(_info.getServer(), DNSRecordType.TYPE_AAAA, DNSRecordClass.CLASS_IN), now);
            }
        }
        return newOut;
    }

    /*
     * (non-Javadoc)
     * @see javax.jmdns.impl.tasks.Resolver#addQuestions(org.trianacode.pegasus.jmdns.impl.DNSOutgoing)
     */
    @Override
    protected DNSOutgoing addQuestions(DNSOutgoing out) throws IOException {
        DNSOutgoing newOut = out;
        if (!_info.hasData()) {
            newOut = this.addQuestion(newOut, org.trianacode.pegasus.jmdns.impl.DNSQuestion.newQuestion(_info.getQualifiedName(), DNSRecordType.TYPE_SRV, DNSRecordClass.CLASS_IN, DNSRecordClass.NOT_UNIQUE));
            newOut = this.addQuestion(newOut, org.trianacode.pegasus.jmdns.impl.DNSQuestion.newQuestion(_info.getQualifiedName(), DNSRecordType.TYPE_TXT, DNSRecordClass.CLASS_IN, DNSRecordClass.NOT_UNIQUE));
            if (_info.getServer().length() > 0) {
                newOut = this.addQuestion(newOut, org.trianacode.pegasus.jmdns.impl.DNSQuestion.newQuestion(_info.getServer(), org.trianacode.pegasus.jmdns.impl.constants.DNSRecordType.TYPE_A, DNSRecordClass.CLASS_IN, org.trianacode.pegasus.jmdns.impl.constants.DNSRecordClass.NOT_UNIQUE));
                newOut = this.addQuestion(newOut, org.trianacode.pegasus.jmdns.impl.DNSQuestion.newQuestion(_info.getServer(), org.trianacode.pegasus.jmdns.impl.constants.DNSRecordType.TYPE_AAAA, org.trianacode.pegasus.jmdns.impl.constants.DNSRecordClass.CLASS_IN, DNSRecordClass.NOT_UNIQUE));
            }
        }
        return newOut;
    }

    /*
     * (non-Javadoc)
     * @see javax.jmdns.impl.tasks.Resolver#description()
     */
    @Override
    protected String description() {
        return "querying service info: " + (_info != null ? _info.getQualifiedName() : "null");
    }

}