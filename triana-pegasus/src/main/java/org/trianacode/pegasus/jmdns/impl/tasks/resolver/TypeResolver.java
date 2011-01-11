// Copyright 2003-2005 Arthur van Hoff, Rick Blair
// Licensed under Apache License version 2.0
// Original license LGPL

package org.trianacode.pegasus.jmdns.impl.tasks.resolver;

import java.io.IOException;

import org.trianacode.pegasus.jmdns.impl.DNSOutgoing;
import org.trianacode.pegasus.jmdns.impl.DNSQuestion;
import org.trianacode.pegasus.jmdns.impl.JmDNSImpl;
import org.trianacode.pegasus.jmdns.impl.JmDNSImpl.ServiceTypeEntry;
import org.trianacode.pegasus.jmdns.impl.constants.DNSRecordClass;

/**
 * Helper class to resolve service types.
 * <p/>
 * The TypeResolver queries three times consecutively for service types, and then removes itself from the timer.
 * <p/>
 * The TypeResolver will run only if JmDNS is in state ANNOUNCED.
 */
public class TypeResolver extends DNSResolverTask {

    /**
     * @param jmDNSImpl
     */
    public TypeResolver(JmDNSImpl jmDNSImpl) {
        super(jmDNSImpl);
    }

    /*
     * (non-Javadoc)
     * @see org.trianacode.pegasus.jmdns.impl.tasks.DNSTask#getName()
     */
    @Override
    public String getName() {
        return "TypeResolver(" + (this.getDns() != null ? this.getDns().getName() : "") + ")";
    }

    /*
     * (non-Javadoc)
     * @see javax.jmdns.impl.tasks.Resolver#addAnswers(org.trianacode.pegasus.jmdns.impl.DNSOutgoing)
     */
    @Override
    protected org.trianacode.pegasus.jmdns.impl.DNSOutgoing addAnswers(org.trianacode.pegasus.jmdns.impl.DNSOutgoing out) throws IOException {
        org.trianacode.pegasus.jmdns.impl.DNSOutgoing newOut = out;
        long now = System.currentTimeMillis();
        for (String type : this.getDns().getServiceTypes().keySet()) {
            ServiceTypeEntry typeEntry = this.getDns().getServiceTypes().get(type);
            newOut = this.addAnswer(newOut, new org.trianacode.pegasus.jmdns.impl.DNSRecord.Pointer("_services._dns-sd._udp.local.", DNSRecordClass.CLASS_IN, DNSRecordClass.NOT_UNIQUE, org.trianacode.pegasus.jmdns.impl.constants.DNSConstants.DNS_TTL, typeEntry.getType()), now);
        }
        return newOut;
    }

    /*
     * (non-Javadoc)
     * @see javax.jmdns.impl.tasks.Resolver#addQuestions(org.trianacode.pegasus.jmdns.impl.DNSOutgoing)
     */
    @Override
    protected org.trianacode.pegasus.jmdns.impl.DNSOutgoing addQuestions(DNSOutgoing out) throws IOException {
        return this.addQuestion(out, DNSQuestion.newQuestion("_services._dns-sd._udp.local.", org.trianacode.pegasus.jmdns.impl.constants.DNSRecordType.TYPE_PTR, org.trianacode.pegasus.jmdns.impl.constants.DNSRecordClass.CLASS_IN, org.trianacode.pegasus.jmdns.impl.constants.DNSRecordClass.NOT_UNIQUE));
    }

    /*
     * (non-Javadoc)
     * @see javax.jmdns.impl.tasks.Resolver#description()
     */
    @Override
    protected String description() {
        return "querying type";
    }
}