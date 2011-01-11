// Copyright 2003-2005 Arthur van Hoff, Rick Blair
// Licensed under Apache License version 2.0
// Original license LGPL

package org.trianacode.pegasus.jmdns.impl.tasks.resolver;

import java.io.IOException;

import org.trianacode.pegasus.jmdns.impl.DNSOutgoing;
import org.trianacode.pegasus.jmdns.impl.DNSQuestion;
import org.trianacode.pegasus.jmdns.impl.DNSRecord;

import org.trianacode.pegasus.jmdns.impl.constants.DNSRecordClass;
import org.trianacode.pegasus.jmdns.impl.constants.DNSRecordType;

/**
 * The ServiceResolver queries three times consecutively for services of a given type, and then removes itself from the timer.
 * <p/>
 * The ServiceResolver will run only if JmDNS is in state ANNOUNCED. REMIND: Prevent having multiple service resolvers for the same type in the timer queue.
 */
public class ServiceResolver extends DNSResolverTask {

    private final String _type;

    public ServiceResolver(org.trianacode.pegasus.jmdns.impl.JmDNSImpl jmDNSImpl, String type) {
        super(jmDNSImpl);
        this._type = type;
    }

    /*
     * (non-Javadoc)
     * @see org.trianacode.pegasus.jmdns.impl.tasks.DNSTask#getName()
     */
    @Override
    public String getName() {
        return "ServiceResolver(" + (this.getDns() != null ? this.getDns().getName() : "") + ")";
    }

    /*
     * (non-Javadoc)
     * @see javax.jmdns.impl.tasks.Resolver#addAnswers(org.trianacode.pegasus.jmdns.impl.DNSOutgoing)
     */
    @Override
    protected DNSOutgoing addAnswers(org.trianacode.pegasus.jmdns.impl.DNSOutgoing out) throws IOException {
        DNSOutgoing newOut = out;
        long now = System.currentTimeMillis();
        for (org.trianacode.pegasus.jmdns.ServiceInfo info : this.getDns().getServices().values()) {
            newOut = this.addAnswer(newOut, new DNSRecord.Pointer(info.getType(), org.trianacode.pegasus.jmdns.impl.constants.DNSRecordClass.CLASS_IN, org.trianacode.pegasus.jmdns.impl.constants.DNSRecordClass.NOT_UNIQUE, org.trianacode.pegasus.jmdns.impl.constants.DNSConstants.DNS_TTL, info.getQualifiedName()), now);
            // newOut = this.addAnswer(newOut, new DNSRecord.Service(info.getQualifiedName(), DNSRecordClass.CLASS_IN, DNSRecordClass.NOT_UNIQUE, DNSConstants.DNS_TTL, info.getPriority(), info.getWeight(), info.getPort(),
            // this.getDns().getLocalHost().getName()), now);
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
        newOut = this.addQuestion(newOut, DNSQuestion.newQuestion(_type, DNSRecordType.TYPE_PTR, org.trianacode.pegasus.jmdns.impl.constants.DNSRecordClass.CLASS_IN, DNSRecordClass.NOT_UNIQUE));
        // newOut = this.addQuestion(newOut, DNSQuestion.newQuestion(_type, DNSRecordType.TYPE_SRV, DNSRecordClass.CLASS_IN, DNSRecordClass.NOT_UNIQUE));
        return newOut;
    }

    /*
     * (non-Javadoc)
     * @see javax.jmdns.impl.tasks.Resolver#description()
     */
    @Override
    protected String description() {
        return "querying service";
    }
}