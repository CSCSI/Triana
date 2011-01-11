// Licensed under Apache License version 2.0
package org.trianacode.pegasus.jmdns.impl.tasks;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.trianacode.pegasus.jmdns.impl.DNSIncoming;
import org.trianacode.pegasus.jmdns.impl.DNSOutgoing;
import org.trianacode.pegasus.jmdns.impl.DNSQuestion;
import org.trianacode.pegasus.jmdns.impl.DNSRecord;
import org.trianacode.pegasus.jmdns.impl.JmDNSImpl;
import org.trianacode.pegasus.jmdns.impl.constants.DNSConstants;

/**
 * This is the root class for all task scheduled by the timer in JmDNS.
 *
 * @author Pierre Frisch
 */
public abstract class DNSTask extends TimerTask {

    /**
     *
     */
    private final org.trianacode.pegasus.jmdns.impl.JmDNSImpl _jmDNSImpl;

    /**
     * @param jmDNSImpl
     */
    protected DNSTask(JmDNSImpl jmDNSImpl) {
        super();
        this._jmDNSImpl = jmDNSImpl;
    }

    /**
     * Return the DNS associated with this task.
     *
     * @return associated DNS
     */
    public org.trianacode.pegasus.jmdns.impl.JmDNSImpl getDns() {
        return _jmDNSImpl;
    }

    /**
     * Start this task.
     *
     * @param timer
     *            task timer.
     */
    public abstract void start(Timer timer);

    /**
     * Return this task name.
     *
     * @return task name
     */
    public abstract String getName();

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.getName();
    }

    /**
     * Add a question to the message.
     *
     * @param out
     *            outgoing message
     * @param rec
     *            DNS question
     * @return outgoing message for the next question
     * @throws IOException
     */
    public org.trianacode.pegasus.jmdns.impl.DNSOutgoing addQuestion(DNSOutgoing out, DNSQuestion rec) throws IOException {
        org.trianacode.pegasus.jmdns.impl.DNSOutgoing newOut = out;
        try {
            newOut.addQuestion(rec);
        } catch (final IOException e) {
            int flags = newOut.getFlags();
            boolean multicast = newOut.isMulticast();
            int maxUDPPayload = newOut.getMaxUDPPayload();
            int id = newOut.getId();

            newOut.setFlags(flags | DNSConstants.FLAGS_TC);
            newOut.setId(id);
            this._jmDNSImpl.send(newOut);

            newOut = new DNSOutgoing(flags, multicast, maxUDPPayload);
            newOut.addQuestion(rec);
        }
        return newOut;
    }

    /**
     * Add an answer if it is not suppressed.
     *
     * @param out
     *            outgoing message
     * @param in
     *            incoming request
     * @param rec
     *            DNS record answer
     * @return outgoing message for the next answer
     * @throws IOException
     */
    public DNSOutgoing addAnswer(DNSOutgoing out, org.trianacode.pegasus.jmdns.impl.DNSIncoming in, org.trianacode.pegasus.jmdns.impl.DNSRecord rec) throws IOException {
        DNSOutgoing newOut = out;
        try {
            newOut.addAnswer(in, rec);
        } catch (final IOException e) {
            int flags = newOut.getFlags();
            boolean multicast = newOut.isMulticast();
            int maxUDPPayload = newOut.getMaxUDPPayload();
            int id = newOut.getId();

            newOut.setFlags(flags | DNSConstants.FLAGS_TC);
            newOut.setId(id);
            this._jmDNSImpl.send(newOut);

            newOut = new DNSOutgoing(flags, multicast, maxUDPPayload);
            newOut.addAnswer(in, rec);
        }
        return newOut;
    }

    /**
     * Add an answer to the message.
     *
     * @param out
     *            outgoing message
     * @param rec
     *            DNS record answer
     * @param now
     * @return outgoing message for the next answer
     * @throws IOException
     */
    public DNSOutgoing addAnswer(DNSOutgoing out, DNSRecord rec, long now) throws IOException {
        org.trianacode.pegasus.jmdns.impl.DNSOutgoing newOut = out;
        try {
            newOut.addAnswer(rec, now);
        } catch (final IOException e) {
            int flags = newOut.getFlags();
            boolean multicast = newOut.isMulticast();
            int maxUDPPayload = newOut.getMaxUDPPayload();
            int id = newOut.getId();

            newOut.setFlags(flags | DNSConstants.FLAGS_TC);
            newOut.setId(id);
            this._jmDNSImpl.send(newOut);

            newOut = new org.trianacode.pegasus.jmdns.impl.DNSOutgoing(flags, multicast, maxUDPPayload);
            newOut.addAnswer(rec, now);
        }
        return newOut;
    }

    /**
     * Add an authoritative answer to the message.
     *
     * @param out
     *            outgoing message
     * @param rec
     *            DNS record answer
     * @return outgoing message for the next answer
     * @throws IOException
     */
    public DNSOutgoing addAuthoritativeAnswer(org.trianacode.pegasus.jmdns.impl.DNSOutgoing out, org.trianacode.pegasus.jmdns.impl.DNSRecord rec) throws IOException {
        DNSOutgoing newOut = out;
        try {
            newOut.addAuthorativeAnswer(rec);
        } catch (final IOException e) {
            int flags = newOut.getFlags();
            boolean multicast = newOut.isMulticast();
            int maxUDPPayload = newOut.getMaxUDPPayload();
            int id = newOut.getId();

            newOut.setFlags(flags | DNSConstants.FLAGS_TC);
            newOut.setId(id);
            this._jmDNSImpl.send(newOut);

            newOut = new DNSOutgoing(flags, multicast, maxUDPPayload);
            newOut.addAuthorativeAnswer(rec);
        }
        return newOut;
    }

    /**
     * Add an additional answer to the record. Omit if there is no room.
     *
     * @param out
     *            outgoing message
     * @param in
     *            incoming request
     * @param rec
     *            DNS record answer
     * @return outgoing message for the next answer
     * @throws IOException
     */
    public DNSOutgoing addAdditionalAnswer(DNSOutgoing out, DNSIncoming in, org.trianacode.pegasus.jmdns.impl.DNSRecord rec) throws IOException {
        DNSOutgoing newOut = out;
        try {
            newOut.addAdditionalAnswer(in, rec);
        } catch (final IOException e) {
            int flags = newOut.getFlags();
            boolean multicast = newOut.isMulticast();
            int maxUDPPayload = newOut.getMaxUDPPayload();
            int id = newOut.getId();

            newOut.setFlags(flags | DNSConstants.FLAGS_TC);
            newOut.setId(id);
            this._jmDNSImpl.send(newOut);

            newOut = new DNSOutgoing(flags, multicast, maxUDPPayload);
            newOut.addAdditionalAnswer(in, rec);
        }
        return newOut;
    }

}
