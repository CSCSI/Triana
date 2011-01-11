// Copyright 2003-2005 Arthur van Hoff, Rick Blair
// Licensed under Apache License version 2.0
// Original license LGPL

package org.trianacode.pegasus.jmdns.impl;

import java.net.InetAddress;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.trianacode.pegasus.jmdns.ServiceInfo;
import org.trianacode.pegasus.jmdns.ServiceInfo.Fields;

import org.trianacode.pegasus.jmdns.impl.constants.DNSConstants;
import org.trianacode.pegasus.jmdns.impl.constants.DNSRecordClass;
import org.trianacode.pegasus.jmdns.impl.constants.DNSRecordType;

/**
 * A DNS question.
 *
 * @author Arthur van Hoff, Pierre Frisch
 */
public class DNSQuestion extends org.trianacode.pegasus.jmdns.impl.DNSEntry {
    private static Logger logger = Logger.getLogger(DNSQuestion.class.getName());

    /**
     * Address question.
     */
    private static class DNS4Address extends DNSQuestion {
        DNS4Address(String name, org.trianacode.pegasus.jmdns.impl.constants.DNSRecordType type, DNSRecordClass recordClass, boolean unique) {
            super(name, type, recordClass, unique);
        }

        @Override
        public void addAnswers(JmDNSImpl jmDNSImpl, Set<DNSRecord> answers) {
            DNSRecord answer = jmDNSImpl.getLocalHost().getDNSAddressRecord(this.getRecordType(), org.trianacode.pegasus.jmdns.impl.constants.DNSRecordClass.UNIQUE, DNSConstants.DNS_TTL);
            if (answer != null) {
                answers.add(answer);
            }
        }

        @Override
        public boolean iAmTheOnlyOne(JmDNSImpl jmDNSImpl) {
            String name = this.getName().toLowerCase();
            return jmDNSImpl.getLocalHost().getName().equals(name) || jmDNSImpl.getServices().keySet().contains(name);
        }

    }

    /**
     * Address question.
     */
    private static class DNS6Address extends DNSQuestion {
        DNS6Address(String name, org.trianacode.pegasus.jmdns.impl.constants.DNSRecordType type, DNSRecordClass recordClass, boolean unique) {
            super(name, type, recordClass, unique);
        }

        @Override
        public void addAnswers(JmDNSImpl jmDNSImpl, Set<DNSRecord> answers) {
            DNSRecord answer = jmDNSImpl.getLocalHost().getDNSAddressRecord(this.getRecordType(), DNSRecordClass.UNIQUE, org.trianacode.pegasus.jmdns.impl.constants.DNSConstants.DNS_TTL);
            if (answer != null) {
                answers.add(answer);
            }
        }

        @Override
        public boolean iAmTheOnlyOne(JmDNSImpl jmDNSImpl) {
            String name = this.getName().toLowerCase();
            return jmDNSImpl.getLocalHost().getName().equals(name) || jmDNSImpl.getServices().keySet().contains(name);
        }

    }

    /**
     * Host Information question.
     */
    private static class HostInformation extends DNSQuestion {
        HostInformation(String name, org.trianacode.pegasus.jmdns.impl.constants.DNSRecordType type, org.trianacode.pegasus.jmdns.impl.constants.DNSRecordClass recordClass, boolean unique) {
            super(name, type, recordClass, unique);
        }
    }

    /**
     * Pointer question.
     */
    private static class Pointer extends DNSQuestion {
        Pointer(String name, org.trianacode.pegasus.jmdns.impl.constants.DNSRecordType type, org.trianacode.pegasus.jmdns.impl.constants.DNSRecordClass recordClass, boolean unique) {
            super(name, type, recordClass, unique);
        }

        @Override
        public void addAnswers(JmDNSImpl jmDNSImpl, Set<DNSRecord> answers) {
            // find matching services
            for (ServiceInfo serviceInfo : jmDNSImpl.getServices().values()) {
                this.addAnswersForServiceInfo(jmDNSImpl, answers, (ServiceInfoImpl) serviceInfo);
            }
            if (this.isServicesDiscoveryMetaQuery()) {
                for (String serviceType : jmDNSImpl.getServiceTypes().keySet()) {
                    JmDNSImpl.ServiceTypeEntry typeEntry = jmDNSImpl.getServiceTypes().get(serviceType);
                    answers.add(new DNSRecord.Pointer("_services._dns-sd._udp.local.", org.trianacode.pegasus.jmdns.impl.constants.DNSRecordClass.CLASS_IN, DNSRecordClass.NOT_UNIQUE, org.trianacode.pegasus.jmdns.impl.constants.DNSConstants.DNS_TTL, typeEntry.getType()));
                }
            } else if (this.isReverseLookup()) {
                String ipValue = this.getQualifiedNameMap().get(Fields.Instance);
                if ((ipValue != null) && (ipValue.length() > 0)) {
                    InetAddress address = jmDNSImpl.getLocalHost().getInetAddress();
                    String hostIPAddress = (address != null ? address.getHostAddress() : "");
                    if (ipValue.equalsIgnoreCase(hostIPAddress)) {
                        if (this.isV4ReverseLookup()) {
                            answers.add(jmDNSImpl.getLocalHost().getDNSReverseAddressRecord(org.trianacode.pegasus.jmdns.impl.constants.DNSRecordType.TYPE_A, DNSRecordClass.NOT_UNIQUE, org.trianacode.pegasus.jmdns.impl.constants.DNSConstants.DNS_TTL));
                        }
                        if (this.isV6ReverseLookup()) {
                            answers.add(jmDNSImpl.getLocalHost().getDNSReverseAddressRecord(org.trianacode.pegasus.jmdns.impl.constants.DNSRecordType.TYPE_AAAA, org.trianacode.pegasus.jmdns.impl.constants.DNSRecordClass.NOT_UNIQUE, org.trianacode.pegasus.jmdns.impl.constants.DNSConstants.DNS_TTL));
                        }
                    }
                }
            } else if (this.isDomainDiscoveryQuery()) {
                // FIXME [PJYF Nov 16 2010] We do not currently support domain discovery
            }
        }

    }

    /**
     * Service question.
     */
    private static class Service extends DNSQuestion {
        Service(String name, org.trianacode.pegasus.jmdns.impl.constants.DNSRecordType type, DNSRecordClass recordClass, boolean unique) {
            super(name, type, recordClass, unique);
        }

        @Override
        public void addAnswers(JmDNSImpl jmDNSImpl, Set<DNSRecord> answers) {
            String loname = this.getName().toLowerCase();
            if (jmDNSImpl.getLocalHost().getName().equalsIgnoreCase(loname)) {
                // type = DNSConstants.TYPE_A;
                answers.addAll(jmDNSImpl.getLocalHost().answers(this.isUnique(), org.trianacode.pegasus.jmdns.impl.constants.DNSConstants.DNS_TTL));
                return;
            }
            // Service type request
            if (jmDNSImpl.getServiceTypes().containsKey(loname)) {
                DNSQuestion question = new Pointer(this.getName(), DNSRecordType.TYPE_PTR, this.getRecordClass(), this.isUnique());
                question.addAnswers(jmDNSImpl, answers);
                return;
            }

            this.addAnswersForServiceInfo(jmDNSImpl, answers, (ServiceInfoImpl) jmDNSImpl.getServices().get(loname));
        }

        @Override
        public boolean iAmTheOnlyOne(JmDNSImpl jmDNSImpl) {
            String name = this.getName().toLowerCase();
            return jmDNSImpl.getLocalHost().getName().equals(name) || jmDNSImpl.getServices().keySet().contains(name);
        }

    }

    /**
     * Text question.
     */
    private static class Text extends DNSQuestion {
        Text(String name, org.trianacode.pegasus.jmdns.impl.constants.DNSRecordType type, org.trianacode.pegasus.jmdns.impl.constants.DNSRecordClass recordClass, boolean unique) {
            super(name, type, recordClass, unique);
        }

        @Override
        public void addAnswers(JmDNSImpl jmDNSImpl, Set<DNSRecord> answers) {
            this.addAnswersForServiceInfo(jmDNSImpl, answers, (ServiceInfoImpl) jmDNSImpl.getServices().get(this.getName().toLowerCase()));
        }

        @Override
        public boolean iAmTheOnlyOne(JmDNSImpl jmDNSImpl) {
            String name = this.getName().toLowerCase();
            return jmDNSImpl.getLocalHost().getName().equals(name) || jmDNSImpl.getServices().keySet().contains(name);
        }

    }

    /**
     * AllRecords question.
     */
    private static class AllRecords extends DNSQuestion {
        AllRecords(String name, DNSRecordType type, DNSRecordClass recordClass, boolean unique) {
            super(name, type, recordClass, unique);
        }

        @Override
        public boolean isSameType(org.trianacode.pegasus.jmdns.impl.DNSEntry entry) {
            // We match all non null entry
            return (entry != null);
        }

        @Override
        public void addAnswers(JmDNSImpl jmDNSImpl, Set<DNSRecord> answers) {
            String loname = this.getName().toLowerCase();
            if (jmDNSImpl.getLocalHost().getName().equalsIgnoreCase(loname)) {
                // type = DNSConstants.TYPE_A;
                answers.addAll(jmDNSImpl.getLocalHost().answers(this.isUnique(), DNSConstants.DNS_TTL));
                return;
            }
            // Service type request
            if (jmDNSImpl.getServiceTypes().containsKey(loname)) {
                DNSQuestion question = new Pointer(this.getName(), org.trianacode.pegasus.jmdns.impl.constants.DNSRecordType.TYPE_PTR, this.getRecordClass(), this.isUnique());
                question.addAnswers(jmDNSImpl, answers);
                return;
            }

            this.addAnswersForServiceInfo(jmDNSImpl, answers, (ServiceInfoImpl) jmDNSImpl.getServices().get(loname));
        }

        @Override
        public boolean iAmTheOnlyOne(JmDNSImpl jmDNSImpl) {
            String name = this.getName().toLowerCase();
            return jmDNSImpl.getLocalHost().getName().equals(name) || jmDNSImpl.getServices().keySet().contains(name);
        }

    }

    DNSQuestion(String name, org.trianacode.pegasus.jmdns.impl.constants.DNSRecordType type, DNSRecordClass recordClass, boolean unique) {
        super(name, type, recordClass, unique);
    }

    /**
     * Create a question.
     *
     * @param name
     * @param type
     * @param recordClass
     * @param unique
     * @return new question
     */
    public static DNSQuestion newQuestion(String name, org.trianacode.pegasus.jmdns.impl.constants.DNSRecordType type, org.trianacode.pegasus.jmdns.impl.constants.DNSRecordClass recordClass, boolean unique) {
        switch (type) {
            case TYPE_A:
                return new DNS4Address(name, type, recordClass, unique);
            case TYPE_A6:
                return new DNS6Address(name, type, recordClass, unique);
            case TYPE_AAAA:
                return new DNS6Address(name, type, recordClass, unique);
            case TYPE_ANY:
                return new AllRecords(name, type, recordClass, unique);
            case TYPE_HINFO:
                return new HostInformation(name, type, recordClass, unique);
            case TYPE_PTR:
                return new Pointer(name, type, recordClass, unique);
            case TYPE_SRV:
                return new Service(name, type, recordClass, unique);
            case TYPE_TXT:
                return new Text(name, type, recordClass, unique);
            default:
                return new DNSQuestion(name, type, recordClass, unique);
        }
    }

    /**
     * Check if this question is answered by a given DNS record.
     */
    boolean answeredBy(org.trianacode.pegasus.jmdns.impl.DNSEntry rec) {
        return this.isSameRecordClass(rec) && this.isSameType(rec) && this.getName().equals(rec.getName());
    }

    /**
     * Adds answers to the list for our question.
     *
     * @param jmDNSImpl
     *            DNS holding the records
     * @param answers
     *            List of previous answer to append.
     */
    public void addAnswers(JmDNSImpl jmDNSImpl, Set<DNSRecord> answers) {
        // By default we do nothing
    }

    protected void addAnswersForServiceInfo(JmDNSImpl jmDNSImpl, Set<DNSRecord> answers, ServiceInfoImpl info) {
        if ((info != null) && info.isAnnounced()) {
            if (this.getName().equalsIgnoreCase(info.getQualifiedName()) || this.getName().equalsIgnoreCase(info.getType())) {
                answers.addAll(jmDNSImpl.getLocalHost().answers(DNSRecordClass.UNIQUE, org.trianacode.pegasus.jmdns.impl.constants.DNSConstants.DNS_TTL));
                answers.addAll(info.answers(org.trianacode.pegasus.jmdns.impl.constants.DNSRecordClass.UNIQUE, org.trianacode.pegasus.jmdns.impl.constants.DNSConstants.DNS_TTL, jmDNSImpl.getLocalHost()));
            }
            if (logger.isLoggable(Level.FINER)) {
                logger.finer(jmDNSImpl.getName() + " DNSQuestion(" + this.getName() + ").addAnswersForServiceInfo(): info: " + info + "\n" + answers);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see javax.jmdns.impl.DNSEntry#isStale(long)
     */
    @Override
    public boolean isStale(long now) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see javax.jmdns.impl.DNSEntry#isExpired(long)
     */
    @Override
    public boolean isExpired(long now) {
        return false;
    }

    /**
     * Checks if we are the only to be able to answer that question.
     *
     * @param jmDNSImpl
     *            DNS holding the records
     * @return <code>true</code> if we are the only one with the answer to the question, <code>false</code> otherwise.
     */
    public boolean iAmTheOnlyOne(JmDNSImpl jmDNSImpl) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see javax.jmdns.impl.DNSEntry#toString(java.lang.StringBuilder)
     */
    @Override
    public void toString(StringBuilder aLog) {
        // do nothing
    }

}