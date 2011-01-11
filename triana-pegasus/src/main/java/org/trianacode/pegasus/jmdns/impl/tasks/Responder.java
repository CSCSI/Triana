// Copyright 2003-2005 Arthur van Hoff, Rick Blair
// Licensed under Apache License version 2.0
// Original license LGPL

package org.trianacode.pegasus.jmdns.impl.tasks;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.trianacode.pegasus.jmdns.impl.DNSOutgoing;
import org.trianacode.pegasus.jmdns.impl.DNSQuestion;
import org.trianacode.pegasus.jmdns.impl.DNSRecord;
import org.trianacode.pegasus.jmdns.impl.JmDNSImpl;
import org.trianacode.pegasus.jmdns.impl.constants.DNSConstants;

/**
 * The Responder sends a single answer for the specified service infos and for the host name.
 */
public class Responder extends DNSTask {
    static Logger             logger = Logger.getLogger(Responder.class.getName());

    /**
     *
     */
    private final org.trianacode.pegasus.jmdns.impl.DNSIncoming _in;

    /**
     *
     */
    private final boolean     _unicast;

    public Responder(JmDNSImpl jmDNSImpl, org.trianacode.pegasus.jmdns.impl.DNSIncoming in, int port) {
        super(jmDNSImpl);
        this._in = in;
        this._unicast = (port != org.trianacode.pegasus.jmdns.impl.constants.DNSConstants.MDNS_PORT);
    }

    /*
     * (non-Javadoc)
     * @see org.trianacode.pegasus.jmdns.impl.tasks.DNSTask#getName()
     */
    @Override
    public String getName() {
        return "Responder(" + (this.getDns() != null ? this.getDns().getName() : "") + ")";
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return super.toString() + " incomming: " + _in;
    }

    /*
     * (non-Javadoc)
     * @see org.trianacode.pegasus.jmdns.impl.tasks.DNSTask#start(java.util.Timer)
     */
    @Override
    public void start(Timer timer) {
        // According to draft-cheshire-dnsext-multicastdns.txt chapter "7 Responding":
        // We respond immediately if we know for sure, that we are the only one who can respond to the query.
        // In all other cases, we respond within 20-120 ms.
        //
        // According to draft-cheshire-dnsext-multicastdns.txt chapter "6.2 Multi-Packet Known Answer Suppression":
        // We respond after 20-120 ms if the query is truncated.

        boolean iAmTheOnlyOne = true;
        for (org.trianacode.pegasus.jmdns.impl.DNSQuestion question : _in.getQuestions()) {
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest(this.getName() + "start() question=" + question);
            }
            iAmTheOnlyOne = question.iAmTheOnlyOne(this.getDns());
            if (!iAmTheOnlyOne) {
                break;
            }
        }
        int delay = (iAmTheOnlyOne && !_in.isTruncated()) ? 0 : org.trianacode.pegasus.jmdns.impl.constants.DNSConstants.RESPONSE_MIN_WAIT_INTERVAL + JmDNSImpl.getRandom().nextInt(org.trianacode.pegasus.jmdns.impl.constants.DNSConstants.RESPONSE_MAX_WAIT_INTERVAL - org.trianacode.pegasus.jmdns.impl.constants.DNSConstants.RESPONSE_MIN_WAIT_INTERVAL + 1) - _in.elapseSinceArrival();
        if (delay < 0) {
            delay = 0;
        }
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest(this.getName() + "start() Responder chosen delay=" + delay);
        }
        if (!this.getDns().isCanceling() && !this.getDns().isCanceled()) {
            timer.schedule(this, delay);
        }
    }

    @Override
    public void run() {
        this.getDns().respondToQuery(_in);

        // We use these sets to prevent duplicate records
        Set<DNSQuestion> questions = new HashSet<org.trianacode.pegasus.jmdns.impl.DNSQuestion>();
        Set<org.trianacode.pegasus.jmdns.impl.DNSRecord> answers = new HashSet<DNSRecord>();

        if (this.getDns().isAnnounced()) {
            try {
                // Answer questions
                for (org.trianacode.pegasus.jmdns.impl.DNSQuestion question : _in.getQuestions()) {
                    if (logger.isLoggable(Level.FINER)) {
                        logger.finer(this.getName() + "run() JmDNS responding to: " + question);
                    }
                    // for unicast responses the question must be included
                    if (_unicast) {
                        // out.addQuestion(q);
                        questions.add(question);
                    }

                    question.addAnswers(this.getDns(), answers);
                }

                // remove known answers, if the ttl is at least half of the correct value. (See Draft Cheshire chapter 7.1.).
                long now = System.currentTimeMillis();
                for (DNSRecord knownAnswer : _in.getAnswers()) {
                    if (knownAnswer.isStale(now)) {
                        answers.remove(knownAnswer);
                        if (logger.isLoggable(Level.FINER)) {
                            logger.finer(this.getName() + "JmDNS Responder Known Answer Removed");
                        }
                    }
                }

                // respond if we have answers
                if (!answers.isEmpty()) {
                    if (logger.isLoggable(Level.FINER)) {
                        logger.finer(this.getName() + "run() JmDNS responding");
                    }
                    DNSOutgoing out = new org.trianacode.pegasus.jmdns.impl.DNSOutgoing(DNSConstants.FLAGS_QR_RESPONSE | org.trianacode.pegasus.jmdns.impl.constants.DNSConstants.FLAGS_AA, !_unicast, _in.getSenderUDPPayload());
                    out.setId(_in.getId());
                    for (org.trianacode.pegasus.jmdns.impl.DNSQuestion question : questions) {
                        if (question != null) {
                            out = this.addQuestion(out, question);
                        }
                    }
                    for (org.trianacode.pegasus.jmdns.impl.DNSRecord answer : answers) {
                        if (answer != null) {
                            out = this.addAnswer(out, _in, answer);

                        }
                    }
                    if (!out.isEmpty()) this.getDns().send(out);
                }
                // this.cancel();
            } catch (Throwable e) {
                logger.log(Level.WARNING, this.getName() + "run() exception ", e);
                this.getDns().close();
            }
        }
    }
}